package model

import com.github.javaparser.Range
import com.github.javaparser.ast.Node
import java.nio.file.Path


data class Unit(
    var node: Node?,
    val content: String,
    val range: Range,
    val location: Path,
    val type: Class<Node>,
    val hash: Int,
    val mass: Int
) {
    fun removeNode(): Unit {
        this.node = null
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Unit

        if (content != other.content) return false
        if (range != other.range) return false
        if (location != other.location) return false

        return true
    }

    override fun hashCode(): Int {
        var result: Int = content.hashCode()
        result = 31 * result + range.hashCode()
        result = 31 * result + location.hashCode()
        return result
    }

    override fun toString(): String {
        return "Unit(content='$content', range=$range, location=$location)"
    }
}
