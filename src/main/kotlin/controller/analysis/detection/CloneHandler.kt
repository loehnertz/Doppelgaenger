package controller.analysis.detection

import com.github.javaparser.ast.Node
import controller.analysis.parsing.Visitor
import model.Unit
import utility.Clone
import utility.calculateMass
import utility.getAllParentNodes


interface CloneHandler {
    fun filterOutSubClonesFromCloneCollection(clones: Collection<Clone>): List<Clone> {
        val cloneNodes: List<Node> = retrieveAllCloneNodes(clones)
        return clones.filter { clone ->
            listOf(
                isNotSubClone(clone.first.node, cloneNodes),
                isNotSubClone(clone.second.node, cloneNodes)
            ).any { it }
        }
    }

    fun filterOutSubClonesFromCloneUnitCollection(cloneUnits: Collection<Unit>): List<Unit> {
        val cloneNodes: List<Node> = retrieveAllCloneUnitNodes(cloneUnits)
        return cloneUnits.filter { isNotSubClone(it.node, cloneNodes) }.toList()
    }

    private fun retrieveAllCloneNodes(clones: Collection<Clone>): List<Node> {
        return clones.flatMap { clone -> clone.toList().map { it.node } }.toList()
    }

    private fun retrieveAllCloneUnitNodes(clonesUnits: Collection<Unit>): List<Node> {
        return clonesUnits.map { it.node }.toList()
    }

    private fun isNotSubClone(node: Node, cloneNodes: List<Node>): Boolean {
        return !node.getAllParentNodes().any { parent -> cloneNodes.contains(parent) }
    }

    fun calculateSimilarity(clone: Clone, massThreshold: Int): Double {
        val firstCloneSubnodes: List<Node> = Visitor.visit(clone.first.node).filter { it.calculateMass() >= massThreshold }
        val secondCloneSubnodes: List<Node> = Visitor.visit(clone.second.node).filter { it.calculateMass() >= massThreshold }
        val sharedNodes = firstCloneSubnodes.intersect(secondCloneSubnodes)

        return computeSimilarity(sharedNodes.size, firstCloneSubnodes.filter { !sharedNodes.contains(it) }.size, secondCloneSubnodes.filter { !sharedNodes.contains(it) }.size)
    }

    fun computeSimilarity(sharedNodesCount: Int, onlyInFirstCount: Int, onlyInSecondCount: Int): Double {
        return 2 * sharedNodesCount.toDouble() / (2 * sharedNodesCount + onlyInFirstCount + onlyInSecondCount).toDouble()
    }

    fun retrieveClonedUnits(clones: List<Clone>): List<Unit> {
        return clones.flatMap { it.toList() }.toSet().let { filterOutSubClonesFromCloneUnitCollection(it) }
    }

    fun retrieveCloneClasses(clonedUnits: List<Unit>): Map<Int, List<Unit>> {
        return clonedUnits.groupBy { it.hash }
    }
}
