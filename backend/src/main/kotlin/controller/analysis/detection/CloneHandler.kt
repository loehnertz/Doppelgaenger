package controller.analysis.detection

import com.github.javaparser.ast.Node
import controller.analysis.parsing.Visitor
import model.Unit
import utility.Clone
import utility.getAllParentNodes


interface CloneHandler {
    fun filterOutSubClonesFromCloneCollection(clones: Collection<Clone>): List<Clone> {
        val cloneNodes: List<Node> = retrieveAllCloneNodes(clones)
        return clones.filter { clone ->
            listOf(
                isNotSubClone(clone.first.node!!, cloneNodes),
                isNotSubClone(clone.second.node!!, cloneNodes)
            ).any { it }
        }
    }

    fun filterOutSubClonesFromCloneUnitCollection(cloneUnits: Collection<Unit>): List<Unit> {
        val cloneNodes: List<Node> = retrieveAllCloneUnitNodes(cloneUnits)
        return cloneUnits.filter { isNotSubClone(it.node!!, cloneNodes) }.toList()
    }

    private fun retrieveAllCloneNodes(clones: Collection<Clone>): List<Node> {
        return clones.flatMap { clone -> clone.toList().map { it.node!! } }.toList()
    }

    private fun retrieveAllCloneUnitNodes(clonesUnits: Collection<Unit>): List<Node> {
        return clonesUnits.map { it.node!! }.toList()
    }

    private fun isNotSubClone(node: Node, cloneNodes: List<Node>): Boolean {
        return !node.getAllParentNodes().any { parent -> cloneNodes.contains(parent) }
    }

    fun calculateSequenceSimilarity(firstSequence: List<Unit>, secondSequence: List<Unit>): Double {
        val sharedAndUniqueNodes: List<Triple<Int, Int, Int>> = firstSequence.mapIndexed { index, unit -> retrieveSharedAndUnsharedNodes(unit.node!!, secondSequence[index].node!!) }

        val nodesShared: Int = sharedAndUniqueNodes.sumBy { it.first }
        val nodesOnlyInFirst: Int = sharedAndUniqueNodes.sumBy { it.second }
        val nodesOnlyInSecond: Int = sharedAndUniqueNodes.sumBy { it.third }

        return computeSimilarity(nodesShared, nodesOnlyInFirst, nodesOnlyInSecond)
    }

    fun calculateNodeSimilarity(firstNode: Node, secondNode: Node): Double {
        val (nodesShared: Int, nodesOnlyInFirst: Int, nodesOnlyInSecond: Int) = retrieveSharedAndUnsharedNodes(firstNode, secondNode)

        val similarity = computeSimilarity(nodesShared, nodesOnlyInFirst, nodesOnlyInSecond)
        return similarity
    }

    private fun retrieveSharedAndUnsharedNodes(firstNode: Node, secondNode: Node): Triple<Int, Int, Int> {
        val firstCloneSubnodes: List<Node> = Visitor.visit(firstNode).filter { it.childNodes.size != 0 }
        val secondCloneSubnodes: List<Node> = Visitor.visit(secondNode).filter { it.childNodes.size != 0 }
        val sharedNodes: Set<Node> = firstCloneSubnodes.intersect(secondCloneSubnodes)

        return Triple(sharedNodes.size, firstCloneSubnodes.filter { !sharedNodes.contains(it) }.size, secondCloneSubnodes.filter { !sharedNodes.contains(it) }.size)
    }

    fun computeSimilarity(sharedNodesCount: Int, onlyInFirstCount: Int, onlyInSecondCount: Int): Double {
        return 2 * sharedNodesCount.toDouble() / (2 * sharedNodesCount + onlyInFirstCount + onlyInSecondCount).toDouble()
    }

    fun retrieveClonedUnits(clones: List<Clone>): List<Unit> {
        return clones.flatMap { it.toList() }.toSet().let { filterOutSubClonesFromCloneUnitCollection(it) }
    }

    fun retrieveClonedUnitsFromCloneClasses(cloneClasses: List<Set<Unit>>): List<Unit> {
        return cloneClasses.flatten().filter { clone -> !cloneClasses.flatten().any { it.contains(clone) } }
    }

    fun retrieveCloneClasses(clones: List<Clone>): List<Set<Unit>> {
        return retrieveClonedUnits(clones).groupBy { it.hash }.map { it.value.toSet() }.filter { it.size > 1 }
    }
}
