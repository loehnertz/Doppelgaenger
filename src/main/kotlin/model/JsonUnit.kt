package model

import com.github.javaparser.Range
import java.nio.file.Path


data class JsonUnit(
    val content: String,
    val range: Range,
    val location: Path,
    val hash: Int,
    val mass: Int
) {
    override fun toString(): String {
        return "Unit(content='$content', range=$range, location=$location)"
    }
}
