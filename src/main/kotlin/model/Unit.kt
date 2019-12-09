package model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.javaparser.Range
import com.github.javaparser.ast.Node
import utility.*
import kotlin.reflect.KClass


data class Unit(
    @JsonIgnore val node: Node,
    val content: String,
    val range: Range,
    val identifier: String,
    @JsonIgnore val type: KClass<out Node>,
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
        if (identifier != other.identifier) return false

        return true
    }

    override fun hashCode(): Int {
        var result: Int = content.hashCode()
        result = 31 * result + range.hashCode()
        result = 31 * result + identifier.hashCode()
        return result
    }

    override fun toString(): String {
        return "Unit(id='$id', content='$content', range=$range, identifier=$identifier)"
    }

    companion object {
        private val DEFAULT_CLONETYPE = CloneType.ONE

        fun fromNode(node: Node, basePackageIdentifier: String, cloneType: CloneType = DEFAULT_CLONETYPE): Unit {
            return Unit(
                node = node,
                content = node.tokenRange.get().toString().filterOutComments(),
                range = node.range.get(),
                identifier = node.retrieveLocation().convertToPackageIdentifier(basePackageIdentifier),
                type = node::class,
                hash = node.leniantHashCode(cloneType),
                mass = node.calculateMass()
            )
        }
    }
}
