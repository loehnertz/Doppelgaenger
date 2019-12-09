package controller.analysis.detection

import com.github.javaparser.ast.Node
import model.CloneType
import model.Unit
import utility.*


class CloneDetector(private val units: List<Unit>, massThreshold: Int?, private val similarityThreshold: Double) : CloneHandler {
    private val massThreshold = massThreshold ?: calculateNodeMassAverage()

    fun detectClones(): List<Clone> {
        return units
            .filter { it.mass >= massThreshold }
            .groupBy { it.hash }
            .map { it.value }
            .filter { it.size > 1 }
            .flatMap { it.cartesianProduct() } // TODO: Add filter by similarity
            .let { filterOutSubClonesFromCloneCollection(it) }
    }

    // This is still a draft
    fun findSiblingClones(clones: List<Clone>): ArrayList<List<List<Node>>> {
        val sequences: List<List<Node>> = clones.flatMap { it.toList() }.toSet().map { it.node.getAllLineSiblings() }.distinct().filter { it.size > 1 }
        val cloneSequencesGroups: ArrayList<List<List<Node>>> = arrayListOf()

        val minimumSequenceLengthThreshold = 2
        val maximumSequenceLength: Int = sequences.maxBy { it.size }!!.size

        for (k: Int in (minimumSequenceLengthThreshold..maximumSequenceLength).reversed()) {
            val subsequencesOfLengthK = sequences.flatMap { it.windowed(k) }.filter { subsequence -> subsequence.any { it.calculateMass() > massThreshold } }
            val buckets = subsequencesOfLengthK.groupBy { subsequence -> subsequence.map { it.leniantHashCode(CloneType.TWO) }.hashCode() }.map { it.value }.filter { it.size > 1 }
            buckets.forEach { filterBucket(it, cloneSequencesGroups) }
        }

        return cloneSequencesGroups
    }

    private fun filterBucket(bucket: List<List<Node>>, cloneSequencesGroups: ArrayList<List<List<Node>>>) {
        val cloneGroup: List<List<Node>> = bucket.cartesianProduct() // Get clone pairs
                .filter { listOf(
                        sequenceIncludedInList(it.first, cloneSequencesGroups),
                        sequenceIncludedInList(it.second, cloneSequencesGroups)
                ).any{ isIncluded -> !isIncluded } } // filter out subsequences of already found subsequences clones
                .filter { calculateSequenceSimilarity(it.first, it.second, massThreshold) > similarityThreshold } // Similarity check
                .flatMap { listOf(it.first, it.second) } // flatten to obtain the clone group

        if (cloneGroup.isNotEmpty()) cloneSequencesGroups.add(cloneGroup)
    }

    private fun sequenceIncludedInList(sequence: List<Node>, cloneSequencesGroups: ArrayList<List<List<Node>>>): Boolean {
        return cloneSequencesGroups.flatten().filter { it.size == sequence.size + 1 }.any { it.containsAll(sequence) }
    }

    fun filterOutClonesIncludedInSequenceClasses(cloneClasses: List<Set<Unit>>, sequenceCloneClasses: List<Set<List<Unit>>>): List<Set<Unit>> {
        return cloneClasses.filter { cloneClass -> sequenceCloneClasses.any{ sequenceCloneClass -> cloneClassIncludedInSequenceCloneClass(cloneClass, sequenceCloneClass) } }
    }

    private fun cloneClassIncludedInSequenceCloneClass(cloneClass: Set<Unit>, sequenceCloneClass: Set<List<Unit>>): Boolean {
        return cloneClass.all { clone -> sequenceCloneClass.any { sequenceClone -> sequenceClone.contains(clone) } }
    }

    private fun calculateNodeMassAverage(): Int = units.map { it.mass }.average().toInt()
}
