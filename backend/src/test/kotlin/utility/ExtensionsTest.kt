package utility

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.Node
import model.CloneType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.File
import java.util.*


@Suppress("ClassName")
internal class ExtensionsTest {

    @Nested
    inner class CollectionTest {

        @Test
        fun cartesianProduct() {
            val value: List<Pair<Int, Int>> = listOf(1, 2, 3).cartesianProduct()
            assertThat(value).contains(Pair(1, 2), Pair(1, 3), Pair(2, 3))
            assertThat(value.flatMap { it.toList() }).contains(1, 2, 3)
        }
    }

    @Nested
    inner class StringTest {

        private val regularLine = "int a = 1;"
        private val sourceCode = "/**\n" +
                                 "     * Returns a string\n" +
                                 "     *\n" +
                                 "     * @return A string\n" +
                                 "     */\n" +
                                 "    public static String hasDocstring() {\n" +
                                 "                                           " +
                                 "        return \"TEST\";\n" +
                                 "    }"

        @Test
        fun isSingleLineJavaCommentLine() {
            val commentLine = "// THIS IS A JAVA COMMENT"

            assertThat(regularLine.isSingleLineJavaCommentLine()).isFalse()
            assertThat(commentLine.isSingleLineJavaCommentLine()).isTrue()
        }

        @Test
        fun isBlankLine() {
            val blankLine = "\t\t"

            assertThat(regularLine.isBlankLine()).isFalse()
            assertThat(blankLine.isBlankLine()).isTrue()
        }

        @Test
        fun filterOutBlankLinesAndJavaComments() {
            assertThat(sourceCode.filterOutBlankLinesAndJavaComments().replace(" ", "")).isEqualTo("publicstaticStringhasDocstring(){\nreturn\"TEST\";\n}")
        }

        @Test
        fun countJavaSloc() {
            assertThat(sourceCode.countJavaSloc()).isEqualTo(3)
        }
    }

    @Nested
    inner class OptionalTest {

        @Test
        fun `toNullable() of empty Optional should result in null`() {
            val emptyOptional: Optional<String> = Optional.empty()
            assertThat(emptyOptional.toNullable()).isNull()
        }

        @Test
        fun `toNullable() of non-empty Optional should result in value`() {
            val value = "TEST"
            val nonEmptyOptional: Optional<String> = Optional.of(value)
            assertThat(nonEmptyOptional.toNullable()).isNotNull().hasSameClassAs(value).isEqualTo(value)
        }
    }

    @Nested
    inner class FileTest {

        @Test
        fun plus() {
            val testDirectory = File(this::class.java.getResource("/java/").path)
            val testFile = File(this::class.java.getResource("/java/Example.java").path)

            assertThat(testDirectory + testFile).isEqualTo(testFile)
        }
    }

    @Nested
    inner class PathTest {

        @Test
        fun convertToPackageIdentifier() {
            val testPath = File(this::class.java.getResource("/java/Example.java").path).toPath()

            assertThat(testPath.convertToPackageIdentifier("java")).isEqualTo("")
        }
    }

    @Nested
    inner class PairTest {

        @Test
        fun map() {
            assertThat(Pair(10, 20).map { it * it }).isEqualTo(Pair(100, 400))
        }
    }

    @Nested
    inner class NodeTest {

        private val exampleCompilationUnit = StaticJavaParser.parse(File(this::class.java.getResource("/java/Example.java").path))

        @Test
        fun retrieveLocation() {
            val arbitraryChild: Node = exampleCompilationUnit.childNodes.last().childNodes.last()
            assertThat(arbitraryChild.retrieveLocation()).isEqualTo(exampleCompilationUnit.storage.get().path)
        }

        @Test
        fun getAllParentNodes() {
            val parents: List<Node> = listOf(exampleCompilationUnit, exampleCompilationUnit.childNodes.last())
            val arbitraryChild: Node = exampleCompilationUnit.childNodes.last().childNodes.last()
            assertThat(arbitraryChild.getAllParentNodes()).containsAll(parents)
        }

        @Nested
        inner class lenientHashCode {
            @Test
            fun `lenientHashCode ignores comments`() {
                val compilationUnitWithComments = StaticJavaParser.parse(File(this::class.java.getResource("/hashTests/TestComment.java").path))
                val compilationUnitWithoutComments = StaticJavaParser.parse(File(this::class.java.getResource("/hashTestsCompare/TestComment.java").path))

                assertThat(compilationUnitWithComments.lenientHashCode()).isEqualTo(compilationUnitWithoutComments.lenientHashCode())
            }

            @Test
            fun `lenientHashCode does not ignore identifiers with Clone Type one`() {
                val compilationUnit1 = StaticJavaParser.parse(File(this::class.java.getResource("/hashTests/TestIdentifiers.java").path))
                val compilationUnit2 = StaticJavaParser.parse(File(this::class.java.getResource("/hashTestsCompare/TestIdentifiers.java").path))

                assertThat(compilationUnit1.lenientHashCode()).isNotEqualTo(compilationUnit2.lenientHashCode())
            }

            @Test
            fun `lenientHashCode ignores identifiers with Clone Type two`() {
                val compilationUnit1 = StaticJavaParser.parse(File(this::class.java.getResource("/hashTests/TestIdentifiers.java").path))
                val compilationUnit2 = StaticJavaParser.parse(File(this::class.java.getResource("/hashTestsCompare/TestIdentifiers.java").path))

                assertThat(compilationUnit1.lenientHashCode(CloneType.TWO)).isEqualTo(compilationUnit2.lenientHashCode(CloneType.TWO))
            }
        }

        @Test
        fun calculateMass() {
            assertThat(exampleCompilationUnit.calculateMass()).isEqualTo(124)
        }
    }
}
