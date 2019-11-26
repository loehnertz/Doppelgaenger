package controller.analysis.detection

import com.github.javaparser.ast.Node
import model.Unit
import utility.Clone


class CloneDetector(private val units: List<Unit>, private val massThreshold: Int?) {
    fun detectClones(): List<Clone> {
        val clones: List<Clone> = units
            .filter { it.mass >= massThreshold ?: calculateNodeMassAverage() }
            .groupBy { it.hash }
            .filter { it.value.size > 1 }
            .map { it.value.toSet() }

        val cloneNodes: List<Node> = clones
            .flatMap { clone ->
                clone.map { it.node!! }
            }

        return clones
            .map { clone ->
                clone.filter { unit -> !getAllParentNodes(unit.node!!).any { parent -> cloneNodes.contains(parent) } }
            }
            .filter { it.size > 1 }
            .map { it.toSet() }
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
