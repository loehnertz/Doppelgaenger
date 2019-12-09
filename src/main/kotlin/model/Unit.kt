package model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.javaparser.Position
import com.github.javaparser.Range
import com.github.javaparser.ast.Node
import utility.calculateMass
import utility.leniantHashCode
import utility.retrieveLocation
import java.nio.file.Path
import kotlin.reflect.KClass


data class Unit(
    @JsonIgnore val node: Node? = null,
    @JsonIgnore val nodeSequence: List<Node>? = null,
    val content: String,
    val range: Range,
    val location: Path,
    val hash: Int,
    val mass: Int,
    var id: Int? = null
) {
    init {
        id = hashCode()
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
        return "Unit(id='$id', content='$content', range=$range, location=$location)"
    }

    companion object {
        private val DEFAULT_CLONETYPE = CloneType.ONE

        fun fromNode(node: Node, cloneType: CloneType = DEFAULT_CLONETYPE): Unit {
            return Unit(
                node = node,
                content = node.tokenRange.get().toString(),
                range = node.range.get(),
                location = node.retrieveLocation(),
                hash = node.leniantHashCode(cloneType),
                mass = node.calculateMass()
            )
        }

        fun fromNodeSequence(nodeSequence: List<Node>, cloneType: CloneType = DEFAULT_CLONETYPE): Unit {
            return Unit(
                    nodeSequence = nodeSequence,
                    content = nodeSequence.joinToString(separator = "") { it.tokenRange.get().toString() },
                    range = calculateNodeSequenceRange(nodeSequence),
                    location = nodeSequence[0].retrieveLocation(),
                    hash = nodeSequence.map { it.leniantHashCode(cloneType) }.hashCode(),
                    mass = nodeSequence.sumBy { it.calculateMass() } + nodeSequence.size
            )
        }

        private fun calculateNodeSequenceRange(nodeSequence: List<Node>): Range {
            val initialPosition = nodeSequence[0].range.get().begin
            val finalPosition = nodeSequence[nodeSequence.size - 1].range.get().end
            return Range(Position.pos(initialPosition.line, initialPosition.column), Position.pos(finalPosition.line, finalPosition.column))
        }
    }
}
