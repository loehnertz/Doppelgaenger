package model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.javaparser.Position
import com.github.javaparser.Range
import com.github.javaparser.ast.Node
import utility.*


data class Unit(
    @JsonIgnore val node: Node? = null,
    @JsonIgnore val nodeSequence: List<Node>? = null,
    @JsonIgnore val contentRaw: String,
    val content: String,
    val range: Range,
    val identifier: String,
    val hash: Int,
    val mass: Int,
    val sloc: Int,
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

    fun contains(clone: Unit): Boolean = when {
        this == clone                       -> false
        this.identifier != clone.identifier -> false
        this.range.contains(clone.range)    -> true
        else                                -> false
    }

    companion object {
        private val DEFAULT_CLONETYPE = CloneType.ONE

        fun fromNode(node: Node, basePath: String, cloneType: CloneType = DEFAULT_CLONETYPE): Unit {
            val content: String = node.tokenRange.get().toString()
            return Unit(
                node = node,
                contentRaw = node.tokenRange.get().toString(),
                content = content.filterOutBlankLinesAndJavaComments(),
                range = node.range.get(),
                identifier = node.retrieveLocation().convertToPackageIdentifier(basePath),
                hash = node.leniantHashCode(cloneType),
                mass = node.calculateMass(),
                sloc = content.countJavaSloc()
            )
        }

        fun fromNodeSequence(nodeSequence: List<Node>, basePath: String, cloneType: CloneType = DEFAULT_CLONETYPE): Unit {
            val content: String = calculateNodeSequenceContent(nodeSequence)
            return Unit(
                nodeSequence = nodeSequence,
                contentRaw = calculateNodeSequenceContent(nodeSequence),
                content = content.filterOutBlankLinesAndJavaComments(),
                range = calculateNodeSequenceRange(nodeSequence),
                identifier = nodeSequence.first().retrieveLocation().convertToPackageIdentifier(basePath),
                hash = nodeSequence.map { it.leniantHashCode(cloneType) }.hashCode(),
                mass = nodeSequence.sumBy { it.calculateMass() } + nodeSequence.size,
                sloc = content.countJavaSloc()
            )
        }

        private fun calculateNodeSequenceContent(nodeSequence: List<Node>): String {
            var result: String = nodeSequence[0].tokenRange.get().toString()
            for (i: Int in (1 until nodeSequence.size)) {
                if (nodeSequence[i - 1].range.get().end.line == nodeSequence[i].range.get().begin.line) result += " "
                else {
                    for (k: Int in (nodeSequence[i - 1].range.get().end.line + 1 until nodeSequence[i].range.get().begin.line)) {
                        result += " \n"
                    }
                    result += "\n"
                }
                result += nodeSequence[i].tokenRange.get().toString()
            }

            return result
        }

        private fun calculateNodeSequenceRange(nodeSequence: List<Node>): Range {
            val initialPosition = nodeSequence[0].range.get().begin
            val finalPosition = nodeSequence[nodeSequence.size - 1].range.get().end
            return Range(Position.pos(initialPosition.line, initialPosition.column), Position.pos(finalPosition.line, finalPosition.column))
        }
    }
}
