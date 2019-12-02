package utility

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Node
import model.CloneType
import java.nio.file.Path
import java.util.*


fun <T : Any> Optional<T>.toNullable(): T? = this.orElse(null)

inline fun <T, R> Pair<T, T>.map(transform: (T) -> R): Pair<R, R> {
    val pairList: List<R> = this.toList().map { transform(it) }
    return Pair(pairList.first(), pairList.last())
}

fun <T> Collection<T>.cartesianProduct(): List<Pair<T, T>> {
    val pairs: ArrayList<Pair<T, T>> = arrayListOf()
    for (item1 in this) {
        for (item2 in this) {
            if (item1 == item2) continue
            if (pairs.contains(Pair(item1, item2)) || pairs.contains(Pair(item2, item1))) continue
            pairs.add(Pair(item1, item2))
        }
    }
    return pairs.toList()
}

fun Node.retrieveLocation(): Path {
    return if (this.parentNode.isEmpty) {
        (this as CompilationUnit).storage.get().path
    } else {
        this.parentNode.get().retrieveLocation()
    }
}

fun Node.getAllParentNodes(): Set<Node> {
    return if (this.parentNode.isEmpty) {
        setOf()
    } else {
        this.parentNode.get().getAllParentNodes().plus(this.parentNode.get())
    }
}

fun Node.leniantHashCode(cloneType: CloneType = CloneType.ONE): Int {
    LeniantHashCodeVisitor.setCloneType(cloneType)
    return LeniantHashCodeVisitor.hashCode(this)
}

fun Node.calculateMass(): Int = 1 + this.childNodes.sumBy { it.calculateMass() }
