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
            .flatMap { it.cartesianProduct() }
            .let { filterOutSubClonesFromCloneCollection(it) }
    }

    // This is still a draft
    private fun findSiblingClones(clones: List<Clone>) {
        val sequences: List<List<Node>> = clones.flatMap { it.toList() }.toSet().map { it.node.getAllLineSiblings() }.distinct().filter { it.size > 1 }
        val cloneSequences: List<List<Node>> = listOf()

        val minimumSequenceLengthThreshold = 2
        val maximumSequenceLength: Int = sequences.maxBy { it.size }!!.size

        for (k: Int in (minimumSequenceLengthThreshold..maximumSequenceLength).reversed()) {
            val subsequencesOfLengthK = sequences.flatMap { it.windowed(k) }.filter { subsequence -> subsequence.any { it.calculateMass() > massThreshold } }
            val buckets = subsequencesOfLengthK.groupBy { subsequence -> subsequence.map { it.leniantHashCode(CloneType.TWO) }.hashCode() }.map { it.value }.filter { it.size > 1 }
        }
    }

    private fun calculateNodeMassAverage(): Int = units.map { it.mass }.average().toInt()
}
