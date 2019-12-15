package controller.analysis.parsing

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Node
import model.CloneType
import model.Unit
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File
import java.lang.reflect.Method


@Suppress("ClassName")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
internal class ParserTest {

    private val testFile = File(this::class.java.getResource("/java/").path)
    private val parser = Parser(
        basePath = "java",
        projectRoot = testFile,
        cloneType = CloneType.ONE
    )
    private val units = parser.parse()

    @Nested
    inner class parse {

        @Test
        fun `A total of 124 units were parsed`() {
            assertThat(units).hasSize(124)
        }

        @Test
        fun `First parsed unit is a CompilationUnit`() {
            assertThat(units.first().node).isOfAnyClassIn(CompilationUnit::class.java)
        }

        @Test
        fun `A total of 28 SLOC were detected`() {
            assertThat(units.first().sloc).isEqualTo(28)
        }
    }

    @Suppress("UNCHECKED_CAST")
    @Nested
    inner class constructNodeConversionFunction {

        private val compilationUnit = units.first().node!!
        private val constructNodeConversionFunctionReference: Method = Parser::class.java.getDeclaredMethod("constructNodeConversionFunction").also { it.trySetAccessible() }
        private val constructNodeConversionFunction = constructNodeConversionFunctionReference.invoke(parser) as (Node) -> Unit

        @Test
        fun `The conversion function yields a Unit`() {
            assertThat(constructNodeConversionFunction(compilationUnit)).isOfAnyClassIn(Unit::class.java)
        }

        @Test
        fun `The node of the Unit is the same as the CompilationUnit`() {
            assertThat(constructNodeConversionFunction(compilationUnit).node).isEqualTo(compilationUnit)
        }

        @Test
        fun `The LOC count of the Unit is correct`() {
            assertThat(constructNodeConversionFunction(compilationUnit).range.lineCount).isEqualTo(41)
        }

        @Test
        fun `The SLOC count of the Unit is correct`() {
            assertThat(constructNodeConversionFunction(compilationUnit).sloc).isEqualTo(28)
        }

        @Test
        fun `The mass of the Unit is correct`() {
            assertThat(constructNodeConversionFunction(compilationUnit).mass).isEqualTo(124)
        }

        @Test
        fun `The content of the Unit has the same amount of SLOC as the calculated SLOC`() {
            val unit = constructNodeConversionFunction(compilationUnit)
            assertThat(unit.content.split("\n").size).isEqualTo(unit.sloc)
        }

        @Test
        fun `The Unit out of the conversion function is the same as the Unit out of the parsing`() {
            assertThat(constructNodeConversionFunction(compilationUnit)).isEqualTo(units.first())
        }
    }
}
