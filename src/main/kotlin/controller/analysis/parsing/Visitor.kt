package controller.analysis.parsing

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.visitor.TreeVisitor
import model.Unit
import utility.leniantHashCode
import java.nio.file.Path


class Visitor {
    private val nodes = ArrayList<Node>()

    private val visitor: TreeVisitor = object : TreeVisitor() {
        override fun process(node: Node) {
            nodes.add(node)
        }
    }

    fun visit(node: Node): List<Unit> {
        visitor.visitPreOrder(node)
        return nodes.map { convertNodeToUnit(it) }.also { nodes.clear() }
    }

    private fun convertNodeToUnit(node: Node): Unit {
        return Unit(
            node = node,
            type = node::javaClass.get(),
            hash = node.leniantHashCode(),
            mass = calculateNodeMass(node),
            location = retrieveLocation(node)
        )
    }

    private fun retrieveLocation(node: Node): Path {
        return if (node.parentNode.isEmpty || node.parentNode == null) {
            (node as CompilationUnit).storage.get().path
        } else {
            retrieveLocation(node.parentNode.get())
        }
    }

    private fun calculateNodeMass(node: Node): Int = 1 + node.childNodes.sumBy { calculateNodeMass(it) }
}
