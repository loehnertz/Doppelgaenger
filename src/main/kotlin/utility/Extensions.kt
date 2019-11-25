package utility

import com.github.javaparser.ast.Node
import java.util.*


fun <T : Any> Optional<T>.toNullable(): T? = this.orElse(null)

fun Node.leniantHashCode(): Int {
    return LeniantHashCodeVisitor.hashCode(this)
}
