package controller.analysis.detection

import com.github.javaparser.ast.Node
import model.Unit
import utility.Clone
import utility.Utility


class CloneDetector(private val units: List<Unit>, private val massThreshold: Int?) {
    fun detectClones(): List<Clone> {
        val clones: List<Clone> = units
            .filter { it.mass >= massThreshold ?: calculateNodeMassAverage() }
            .groupBy { it.hash }
            .map { it.value }
            .filter { it.size > 1 }
            .flatMap { Utility.cartesianProduct(it) }

        val cloneNodes: List<Node> = clones
            .flatMap { clone ->
                clone.toList().map { it.node }
            }

        return clones
            .filter { clone ->
                listOf(
                    isNotSubClone(clone.first.node, cloneNodes),
                    isNotSubClone(clone.second.node, cloneNodes)
                ).any { it }
            }
    }

    private fun isNotSubClone(node: Node, cloneNodes: List<Node>): Boolean {
        return !getAllParentNodes(node).any { parent -> cloneNodes.contains(parent) }
    }

    private fun getAllParentNodes(node: Node): Set<Node> {
        return if (node.parentNode.isEmpty) {
            setOf()
        } else {
            getAllParentNodes(node.parentNode.get()).plus(node.parentNode.get())
        }
    }

    private fun calculateNodeMassAverage(): Int = units.map { it.mass }.average().toInt()
}
