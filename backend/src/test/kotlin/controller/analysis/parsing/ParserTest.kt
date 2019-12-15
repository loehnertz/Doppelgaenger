package controller.analysis.parsing

import com.github.javaparser.ast.CompilationUnit
import model.CloneType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File


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
}
