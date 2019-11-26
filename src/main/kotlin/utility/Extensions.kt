package utility

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Node
import model.CloneType
import java.nio.file.Path
import java.util.*


fun <T : Any> Optional<T>.toNullable(): T? = this.orElse(null)

fun Node.retrieveLocation(): Path {
    return if (this.parentNode.isEmpty || this.parentNode == null) {
        (this as CompilationUnit).storage.get().path
    } else {
        this.parentNode.get().retrieveLocation()
    }
}

fun Node.leniantHashCode(cloneType: CloneType = CloneType.ONE): Int {
    LeniantHashCodeVisitor.setCloneType(cloneType)
    return LeniantHashCodeVisitor.hashCode(this)
}

fun Node.calculateMass(): Int = 1 + this.childNodes.sumBy { it.calculateMass() }
