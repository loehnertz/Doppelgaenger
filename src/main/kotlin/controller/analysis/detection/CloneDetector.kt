package controller.analysis.detection

import com.github.javaparser.ast.Node
import model.CloneType
import model.Unit
import utility.*


class CloneDetector(private val units: List<Unit>, massThreshold: Int?, private val similarityThreshold: Double) : CloneHandler {
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
        return cloneClasses.filter { cloneClass -> sequenceCloneClasses.any{ sequenceCloneClass -> cloneClassIncludedInSequenceCloneClass(cloneClass, sequenceCloneClass) } }
    }

    // This is still a draft
    fun findSequenceCloneClasses(clones: List<Clone>): ArrayList<Set<List<Node>>> {
        val sequences: List<List<Node>> = clones.flatMap { it.toList() }.toSet().map { it.node.getAllLineSiblings() }.distinct().filter { it.size > 1 }
        val cloneSequencesClasses: ArrayList<Set<List<Node>>> = arrayListOf()

        val minimumSequenceLengthThreshold = 2
        val maximumSequenceLength: Int = sequences.maxBy { it.size }!!.size

        for (k: Int in (minimumSequenceLengthThreshold..maximumSequenceLength).reversed()) {
            val subsequencesOfLengthK = sequences.flatMap { it.windowed(k) }.filter { subsequence -> subsequence.any { it.calculateMass() > massThreshold } }
            val buckets = subsequencesOfLengthK.groupBy { subsequence -> subsequence.map { it.leniantHashCode(CloneType.TWO) }.hashCode() }.map { it.value }.filter { it.size > 1 }
            buckets.forEach { filterBucket(it, cloneSequencesClasses) }
        }

        return cloneSequencesClasses
    }

    private fun filterBucket(bucket: List<List<Node>>, cloneSequencesClasses: ArrayList<Set<List<Node>>>) {
        val cloneGroup: Set<List<Node>> = bucket.cartesianProduct() // Get clone pairs
                .filter { listOf(
                        sequenceIncludedInList(it.first, cloneSequencesClasses),
                        sequenceIncludedInList(it.second, cloneSequencesClasses)
                ).any{ isIncluded -> !isIncluded } } // filter out subsequences of already found subsequences clones
                .filter { calculateSequenceSimilarity(it.first, it.second, massThreshold) > similarityThreshold } // Similarity check
                .flatMap { listOf(it.first, it.second) } // flatten to obtain the clone group
                .toSet()

        if (cloneGroup.isNotEmpty()) cloneSequencesClasses.add(cloneGroup)
    }

    private fun sequenceIncludedInList(sequence: List<Node>, cloneSequencesClasses: ArrayList<Set<List<Node>>>): Boolean {
        return cloneSequencesClasses.flatten().filter { it.size == sequence.size + 1 }.any { it.containsAll(sequence) }
    }

    private fun cloneClassIncludedInSequenceCloneClass(cloneClass: Set<Unit>, sequenceCloneClass: Set<List<Unit>>): Boolean {
        return cloneClass.all { clone -> sequenceCloneClass.any { sequenceClone -> sequenceClone.contains(clone) } }
    }

    private fun calculateNodeMassAverage(): Int = units.map { it.mass }.average().toInt()
}
