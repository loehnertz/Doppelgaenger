package controller.analysis.detection

import com.github.javaparser.ast.Node
import model.Unit
import utility.Clone
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
}