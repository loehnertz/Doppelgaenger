package controller.analysis.detection

import controller.analysis.parsing.Parser
import model.CloneType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.TestInstance
import java.io.File

@Suppress("ClassName")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
internal class CloneDetectorTest {

    private val exampleFile = File(this::class.java.getResource("/java/").path)
    private val cloneDetector: CloneDetector

    init {
        val basePath = ""
        val cloneType = CloneType.ONE
        cloneDetector = CloneDetector(
            basePath = basePath,
            units = Parser(basePath, exampleFile, cloneType).parse(),
            massThreshold = 5,
            similarityThreshold = 0.0,
            cloneType = cloneType
        )
    }

    @Nested
    inner class detectClones {
        private val detectedClones = cloneDetector.detectClones()

        @Test
        fun `Six clones are detected`() {
            val (clones, cloneClasses) = detectedClones
            assertThat(clones).hasSize(6)
            assertThat(cloneClasses).hasSize(6)
        }

        @Test
        fun `Clones have same content`() {
            val (clones, cloneClasses) = detectedClones
            assertThat(clones).allMatch { it.first.content == it.second.content }
            assertThat(cloneClasses).allMatch { it.all { clone -> clone.content == it.first().content } }
        }

    }

    @Nested
    inner class findSequenceCloneClasses {
        private val simpleClonesDetected = cloneDetector.detectClones()
        private val cloneSequenceClasses = cloneDetector.findSequenceCloneClasses(simpleClonesDetected.first)

        @Test
        fun `One clone sequence found with two repetitions`() {
            assertThat(cloneSequenceClasses).hasSize(1)
            assertThat(cloneSequenceClasses.first()).hasSize(2)
        }

        @Test
        fun `All classes have same size and content`() {
            assertThat(cloneSequenceClasses).allMatch { it.all { clone -> clone.content == it.first().content } }
            assertThat(cloneSequenceClasses).allMatch { it.all { clone -> clone.range.lineCount == 6 } }
        }
    }

    @Nested
    inner class filterOutClonesIncludedInSequenceClasses {
        private val simpleClonesDetected = cloneDetector.detectClones()
        private val cloneSequenceClasses = cloneDetector.findSequenceCloneClasses(simpleClonesDetected.first)

        @Test
        fun `All clone classes filtered out`() {
            val filteredCloneClasses = cloneDetector.filterOutClonesIncludedInSequenceClasses(simpleClonesDetected.second, cloneSequenceClasses)

            assertThat(filteredCloneClasses).isEmpty()
        }
    }
}
