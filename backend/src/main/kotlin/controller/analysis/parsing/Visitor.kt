package controller.analysis.parsing

import com.github.javaparser.ast.Node
import com.github.javaparser.ast.visitor.TreeVisitor
import model.Unit


object Visitor {
    fun visit(node: Node, nodeConversionFunction: (Node) -> Unit): List<Unit> {
        val nodes: ArrayList<Node> = arrayListOf()
        RecursiveVisitor(nodes).also { it.visitPreOrder(node) }
        return nodes.map { nodeConversionFunction(it) }.also { nodes.clear() }
    }

    fun visit(node: Node): List<Node> {
        val nodes: ArrayList<Node> = arrayListOf()
        RecursiveVisitor(nodes).also { it.visitPreOrder(node) }
        return nodes
    }
}

class RecursiveVisitor(private val nodes: ArrayList<Node>) : TreeVisitor() {
    override fun process(node: Node) {
        nodes.add(node)
    }
}
