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
            .filter { it.value.size > 1 }
            .map { it.value }
            .flatMap { Utility.cartesianProduct(it) }

        val cloneNodes: List<Node> = clones
            .flatMap { clone ->
                clone.toList().map { it.node!! }
            }

        return clones
            .filter { clone ->
                return@filter listOf(
                    !getAllParentNodes(clone.first.node!!).any { parent -> cloneNodes.contains(parent) },
                    !getAllParentNodes(clone.second.node!!).any { parent -> cloneNodes.contains(parent) }
                ).any { it }
            }
    }

    private fun getAllParentNodes(node: Node): Set<Node> {
        return if (node.parentNode.isEmpty || node.parentNode == null) {
            setOf()
        } else {
            getAllParentNodes(node.parentNode.get()).plus(node.parentNode.get())
        }
    }

    private fun calculateNodeMassAverage(): Int = units.map { it.mass }.average().toInt()
}
