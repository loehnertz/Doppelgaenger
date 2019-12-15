package controller.analysis.detection

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@Suppress("ClassName")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
internal class CloneDetectorTest : AbstractCloneDetectorTest() {

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
