package controller.analysis.detection

import model.CloneType
import model.Unit
import utility.*


class CloneDetector(private val units: List<Unit>, massThreshold: Int?, private val similarityThreshold: Double, private val cloneType: CloneType) : CloneHandler {
    private val massThreshold = massThreshold ?: calculateNodeMassAverage()

    fun detectClones(): Pair<List<Clone>, List<Set<Unit>>> {
        val clones: List<Clone> = units
            .filter { it.mass >= massThreshold }
            .groupBy { it.hash }
            .map { it.value }
            .filter { it.size > 1 }
            .flatMap { it.cartesianProduct() } // TODO: Add filter by similarity
            .let { filterOutSubClonesFromCloneCollection(it) }
        val cloneClasses: List<Set<Unit>> = retrieveCloneClasses(clones)
        return clones to cloneClasses
    }

    fun filterOutClonesIncludedInSequenceClasses(cloneClasses: List<Set<Unit>>, sequenceCloneClasses: List<Set<List<Unit>>>): List<Set<Unit>> {
        return cloneClasses.filter { cloneClass -> !sequenceCloneClasses.any{ sequenceCloneClass -> cloneClassIncludedInSequenceCloneClass(cloneClass, sequenceCloneClass) } }
    }

    fun findSequenceCloneClasses(clones: List<Clone>): ArrayList<Set<List<Unit>>> {
        val sequences: List<List<Unit>> = clones.flatMap { it.toList() }.asSequence().distinct().map { it.node!!.getAllLineSiblings() }.distinct().filter { it.size > 1 }.map { it.map { node -> Unit.fromNode(node, cloneType) } }.toList()
        val cloneSequencesClasses: ArrayList<Set<List<Unit>>> = arrayListOf()

        val minimumSequenceLengthThreshold = 2
        val maximumSequenceLength: Int = sequences.maxBy { it.size }!!.size

        for (k: Int in (minimumSequenceLengthThreshold..maximumSequenceLength).reversed()) {
            val subsequencesOfLengthK: List<List<Unit>> = sequences.flatMap { it.windowed(k) }.filter { subsequence -> subsequence.any { it.mass > massThreshold } }
            val buckets: List<List<List<Unit>>> = subsequencesOfLengthK.groupBy { subsequence -> subsequence.map { it.hash }.hashCode() }.map { it.value }.filter { it.size > 1 }
            buckets.forEach { filterBucket(it, cloneSequencesClasses) }
        }

        return cloneSequencesClasses
    }

    private fun filterBucket(bucket: List<List<Unit>>, cloneSequencesClasses: ArrayList<Set<List<Unit>>>) {
        val cloneGroup: Set<List<Unit>> = bucket.cartesianProduct() // Get clone pairs
                .filter { listOf(
                        sequenceIncludedInList(it.first, cloneSequencesClasses),
                        sequenceIncludedInList(it.second, cloneSequencesClasses)
                ).any{ isIncluded -> !isIncluded } } // filter out subsequences of already found subsequences clones
                //.filter { calculateSequenceSimilarity(it.first, it.second, massThreshold) > similarityThreshold } // TODO: Study similarity check
                .flatMap { listOf(it.first, it.second) } // flatten to obtain the clone group
                .toSet()

        if (cloneGroup.isNotEmpty()) cloneSequencesClasses.add(cloneGroup)
    }

    private fun sequenceIncludedInList(sequence: List<Unit>, cloneSequencesClasses: ArrayList<Set<List<Unit>>>): Boolean {
        return cloneSequencesClasses.flatten().any { it.containsAll(sequence) }
    }

    private fun cloneClassIncludedInSequenceCloneClass(cloneClass: Set<Unit>, sequenceCloneClass: Set<List<Unit>>): Boolean {
        return cloneClass.all { clone -> sequenceCloneClass.any { sequenceClone -> sequenceClone.contains(clone) } }
    }

    private fun calculateNodeMassAverage(): Int = units.map { it.mass }.average().toInt()
}
