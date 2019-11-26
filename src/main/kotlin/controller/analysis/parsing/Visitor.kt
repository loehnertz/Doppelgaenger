package controller.analysis.parsing

import com.github.javaparser.ast.Node
import com.github.javaparser.ast.visitor.TreeVisitor
import model.Unit


class Visitor {
    private val nodes = ArrayList<Node>()

    private val visitor: TreeVisitor = object : TreeVisitor() {
        override fun process(node: Node) {
            nodes.add(node)
        }
    }

    fun visit(node: Node, nodeConversionFunction: (Node) -> Unit): List<Unit> {
        visitor.visitPreOrder(node)
        return nodes.map { nodeConversionFunction(it) }.also { nodes.clear() }
    }
}
