package controller.analysis.detection

import com.github.javaparser.ast.Node
import model.CloneType
import model.Unit
import utility.*


class CloneDetector(private val units: List<Unit>, private val massThreshold: Int?) : CloneHandler {
    fun detectClones(): Pair<List<Clone>, List<Set<Unit>>> {
        val clones: List<Clone> = units
            .filter { it.mass >= massThreshold ?: calculateNodeMassAverage() }
            .groupBy { it.hash }
            .map { it.value }
            .filter { it.size > 1 }
            .flatMap { it.cartesianProduct() }
            .let { filterOutSubClonesFromCloneCollection(it) }
        val cloneClasses: List<Set<Unit>> = retrieveCloneClasses(clones)
        return clones to cloneClasses
    }

    // This is still a draft
    private fun findSiblingClones(clones: List<Clone>) {
        val sequences: List<List<Node>> = clones.flatMap { it.toList() }.toSet().map { it.node.getAllLineSiblings() }.distinct().filter { it.size > 1 }
        val cloneSequences: List<List<Node>> = listOf()

        val minimumSequenceLengthThreshold = 2
        val maximumSequenceLength: Int = sequences.maxBy { it.size }!!.size

        for (k: Int in (minimumSequenceLengthThreshold..maximumSequenceLength).reversed()) {
            val subsequencesOfLengthK = sequences.flatMap { it.windowed(k) }.filter { subsequence -> subsequence.any { it.calculateMass() > 25 } }
            val buckets = subsequencesOfLengthK.groupBy { subsequence -> subsequence.map { it.leniantHashCode(CloneType.TWO) }.hashCode() }.map { it.value }.filter { it.size > 1 }
        }
    }

    private fun calculateNodeMassAverage(): Int = units.map { it.mass }.average().toInt()
}
