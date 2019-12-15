package controller.analysis.detection

import controller.analysis.parsing.Parser
import model.CloneType
import model.Unit
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
internal class CloneMetricsCalculatorTest {

    private val exampleFile = File(this::class.java.getResource("/java/").path)
    private val cloneClasses: List<Set<Unit>>
    private val cloneMetricsCalculator: CloneMetricsCalculator

    init {
        val basePath = ""
        val cloneType = CloneType.ONE
        val units = Parser(basePath, exampleFile, cloneType).parse()

        val cloneDetector = CloneDetector(
            basePath = basePath,
            units = units,
            massThreshold = 5,
            similarityThreshold = 0.0,
            cloneType = cloneType
        )

        val (clonePairs, simpleCloneClasses) = cloneDetector.detectClones()
        val cloneSequenceClasses = cloneDetector.findSequenceCloneClasses(clonePairs)
        val filteredCloneClasses = cloneDetector.filterOutClonesIncludedInSequenceClasses(simpleCloneClasses, cloneSequenceClasses)
        cloneClasses = cloneSequenceClasses + filteredCloneClasses
        cloneMetricsCalculator = CloneMetricsCalculator(cloneClasses, units)
    }

    @Test
    fun `One clone class with two clones`() {
        val metrics = cloneMetricsCalculator.calculateMetrics()

        assertThat(metrics.numberOfCloneClasses).isEqualTo(cloneClasses.size)
        assertThat(metrics.numberOfClones).isEqualTo(2)
    }

    @Test
    fun `Largest clone equals the clone class and has 6 SLOC`() {
        val metrics = cloneMetricsCalculator.calculateMetrics()

        assertThat(metrics.largestClone.first).isEqualTo(cloneClasses.first())
        assertThat(metrics.largestClone.second).isEqualTo(6)
    }

    @Test
    fun `Largest clone class equals the clone class`() {
        val metrics = cloneMetricsCalculator.calculateMetrics()

        assertThat(metrics.largestCloneClass).isEqualTo(cloneClasses.first())
        assertThat(metrics.exampleClones).hasSize(1)
        assertThat(metrics.exampleClones.first()).isEqualTo(cloneClasses.first())
        assertThat(metrics.percentageOfDuplicatedLines).isEqualTo(((12.0 / 28.0) * 100).toInt())
    }

    @Test
    fun `Only one example clone that equals the clone class`() {
        val metrics = cloneMetricsCalculator.calculateMetrics()

        assertThat(metrics.exampleClones).hasSize(1)
        assertThat(metrics.exampleClones.first()).isEqualTo(cloneClasses.first())
    }

    @Test
    fun `Percentage of duplicated lines of code is 42`() {
        val metrics = cloneMetricsCalculator.calculateMetrics()

        assertThat(metrics.percentageOfDuplicatedLines).isEqualTo(((12.0 / 28.0) * 100).toInt())
    }
}