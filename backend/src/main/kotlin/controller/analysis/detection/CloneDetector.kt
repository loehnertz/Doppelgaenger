package controller.analysis.detection

import com.github.javaparser.ast.Node
import model.CloneType
import model.Unit
import utility.Clone
import utility.CloneClass
import utility.cartesianProduct
import utility.getAllLineSiblings


class CloneDetector(private val basePath: String, private val units: List<Unit>, massThreshold: Int?, private val similarityThreshold: Double, private val cloneType: CloneType) : CloneHandler {
    private val massThreshold: Int = massThreshold ?: calculateNodeMassAverage()

    fun detectClones(): Pair<List<Clone>, List<CloneClass>> {
        val clones: List<Clone> = units
            .filter { it.mass >= massThreshold }
            .groupBy { it.hash }
            .map { it.value }
            .filter { it.size > 1 }
            .flatMap { it.cartesianProduct() }
            .filter { calculateNodeSimilarity(it.first.node!!, it.second.node!!) >= similarityThreshold }
            .let { filterOutSubClonesFromCloneCollection(it) }
        val cloneClasses: List<CloneClass> = retrieveCloneClasses(clones)
        return clones to cloneClasses
    }

    fun filterOutClonesIncludedInSequenceClasses(cloneClasses: List<CloneClass>, sequenceCloneClasses: List<CloneClass>): List<CloneClass> {
        return cloneClasses.filter { cloneClass -> !sequenceCloneClasses.any { sequenceCloneClass -> cloneClassIncludedInSequenceCloneClass(cloneClass, sequenceCloneClass) } }
    }

    fun findSequenceCloneClasses(clones: List<Clone>): List<CloneClass> {
        val sequences: List<List<Unit>> = clones.flatMap { it.toList() }.asSequence().distinct().map { it.node!!.getAllLineSiblings() }.distinct().filter { it.size > 1 }.map { it.map { node -> Unit.fromNode(node, basePath, cloneType) } }.toList()
        val cloneSequencesClasses: ArrayList<Set<List<Unit>>> = arrayListOf()

        if (sequences.isEmpty()) return listOf()

        val minimumSequenceLengthThreshold = 2
        val maximumSequenceLength: Int = sequences.maxBy { it.size }!!.size

        for (k: Int in (minimumSequenceLengthThreshold..maximumSequenceLength).reversed()) {
            val subsequencesOfLengthK: List<List<Unit>> = sequences.flatMap { it.windowed(k) }.filter { subsequence -> subsequence.sumBy { it.mass } + subsequence.size >= massThreshold }
            val buckets: List<List<List<Unit>>> = subsequencesOfLengthK.groupBy { subsequence -> subsequence.map { it.hash }.hashCode() }.map { it.value }.filter { it.size > 1 }
            buckets.forEach { filterBucket(it, cloneSequencesClasses) }
        }

        val cloneSequenceClassesUnit: List<List<List<Node>>> = cloneSequencesClasses.map { seqClass -> seqClass.map { sequence -> sequence.map { unit -> unit.node!! } } }
        return cloneSequenceClassesUnit.map { seqClass -> seqClass.map { Unit.fromNodeSequence(it, basePath, cloneType) }.toSet() }
    }

    private fun filterBucket(bucket: List<List<Unit>>, cloneSequencesClasses: ArrayList<Set<List<Unit>>>) {
        val cloneGroup: Set<List<Unit>> = bucket.cartesianProduct() // Get clone pairs
            .filter {
                // filter out subsequences of already found subsequences clones
                listOf(
                    sequenceIncludedInList(it.first, cloneSequencesClasses),
                    sequenceIncludedInList(it.second, cloneSequencesClasses)
                ).any { isIncluded -> !isIncluded }
            } // filter out subsequences of already found subsequences clones
            .filter { calculateSequenceSimilarity(it.first, it.second) > similarityThreshold }
            .flatMap { listOf(it.first, it.second) } // flatten to obtain the clone group
            .toSet()

        if (cloneGroup.isNotEmpty()) cloneSequencesClasses.add(cloneGroup)
    }

    private fun sequenceIncludedInList(sequence: List<Unit>, cloneSequencesClasses: ArrayList<Set<List<Unit>>>): Boolean {
        return cloneSequencesClasses.flatten().any { it != sequence && it.containsAll(sequence) }
    }

    private fun cloneClassIncludedInSequenceCloneClass(cloneClass: CloneClass, sequenceCloneClass: CloneClass): Boolean {
        return cloneClass.all { clone -> sequenceCloneClass.any { sequenceClone -> sequenceClone.nodeSequence!!.contains(clone.node!!) } }
    }

    private fun calculateNodeMassAverage(): Int = units.map { it.mass }.average().toInt()
}
