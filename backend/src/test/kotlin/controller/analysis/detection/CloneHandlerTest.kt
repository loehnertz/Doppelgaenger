package controller.analysis.detection

import model.CloneType
import model.Unit
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.TestInstance
import utility.Clone
import utility.cartesianProduct


@TestInstance(TestInstance.Lifecycle.PER_METHOD)
internal class CloneHandlerTest : AbstractCloneDetectorTest() {

    @Test
    fun `filterOutSubClonesFromCloneCollection returns original list`() {
        val (clonePairs, _) = cloneDetector.detectClones()

        val subNodes: List<Clone> = clonePairs.map { Unit.fromNode(it.first.node!!.childNodes.first(), "") to Unit.fromNode(it.second.node!!.childNodes.first(), "") }
        val filteredOut = cloneDetector.filterOutSubClonesFromCloneCollection(clonePairs + subNodes)

        assertThat(filteredOut).isEqualTo(clonePairs)
    }

    @Test
    fun `Similarity of Node Sequences with Type ONE should be 100%`() {
        val (clonePairs, _) = cloneDetector.detectClones()
        val cloneSequenceClasses = cloneDetector.findSequenceCloneClasses(clonePairs)

        val cloneSequencePairs = cloneSequenceClasses
            .map { seqClass -> seqClass.map { seq -> seq.nodeSequence!!.map { node -> Unit.fromNode(node, "", CloneType.ONE) } } }
            .flatMap { it.cartesianProduct() }

        val similarityOfCloneSequences = cloneSequencePairs.map { cloneDetector.calculateSequenceSimilarity(it.first, it.second) }

        assertThat(similarityOfCloneSequences).allMatch { it.equals(1.0) }
    }

    @Test
    fun `Similarity of Nodes with Type ONE should be 100% `() {
        val (clonePairs, _) = cloneDetector.detectClones()
        val similarityOfClones = clonePairs.map { cloneDetector.calculateNodeSimilarity(it.first.node!!, it.second.node!!) }

        assertThat(similarityOfClones).allMatch { it.equals(1.0) }
    }

    @Test
    fun `retrieveClonedUnits returns all Units in clonePairs`() {
        val (clonePairs, _) = cloneDetector.detectClones()
        val clonedUnits = cloneDetector.retrieveClonedUnits(clonePairs)

        assertThat(clonedUnits).hasSize(12)
        assertThat(clonedUnits).allMatch { unit -> clonePairs.any { pair -> pair.first == unit || pair.second == unit } }
    }

    @Test
    fun `retrieveClonedUnitsFromCloneClasses returns all Units in cloneClasses`() {
        val (_, cloneClasses) = cloneDetector.detectClones()

        val clonedUnits = cloneDetector.retrieveClonedUnitsFromCloneClasses(cloneClasses)

        assertThat(clonedUnits).hasSize(12)
        assertThat(clonedUnits).allMatch { unit -> cloneClasses.any { cloneClass -> cloneClass.any { it == unit } } }
    }

    @Test
    fun `retrieveCloneClasses each Unit in the classes exists in clonePairs`() {
        val (clonePairs, _) = cloneDetector.detectClones()
        val cloneClasses = cloneDetector.retrieveCloneClasses(clonePairs)

        assertThat(cloneClasses).hasSize(6)
        assertThat(cloneClasses).allMatch { cloneClass -> cloneClass.all { clone -> clonePairs.any { pair -> pair.first == clone || pair.second == clone } } }
    }
}
