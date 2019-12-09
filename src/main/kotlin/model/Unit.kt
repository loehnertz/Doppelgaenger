package model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.javaparser.Range
import com.github.javaparser.ast.Node
import utility.calculateMass
import utility.leniantHashCode
import utility.retrieveLocation
import java.nio.file.Path
import kotlin.reflect.KClass


data class Unit(
    @JsonIgnore val node: Node,
    val content: String,
    val range: Range,
    val location: Path,
    @JsonIgnore val type: KClass<out Node>,
    val hash: Int,
    val mass: Int
) {
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

    companion object {
        private val DEFAULT_CLONETYPE = CloneType.ONE

        fun fromNode(node: Node, cloneType: CloneType = DEFAULT_CLONETYPE): Unit {
            return Unit(
                node = node,
                content = node.tokenRange.get().toString(),
                range = node.range.get(),
                location = node.retrieveLocation(),
                type = node::class,
                hash = node.leniantHashCode(cloneType),
                mass = node.calculateMass()
            )
        }
    }
}
