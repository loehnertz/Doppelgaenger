package model

import com.github.javaparser.ast.Node
import java.nio.file.Path


data class Unit(
    val node: Node,
    val type: Class<Node>,
    val hash: Int,
    val mass: Int,
    val location: Path
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Unit

        if (node != other.node) return false
        if (location != other.location) return false

        return true
    }

    override fun hashCode(): Int {
        var result: Int = node.hashCode()
        result = 31 * result + (location.hashCode() ?: 0)
        return result
    }
}
