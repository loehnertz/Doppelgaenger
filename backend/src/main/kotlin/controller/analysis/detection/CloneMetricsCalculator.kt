package controller.analysis.detection

import com.github.javaparser.ast.CompilationUnit
import model.CloneMetrics
import model.Unit
import utility.CloneClass
import utility.countJavaSloc
import java.io.File


class CloneMetricsCalculator(private val cloneClasses: List<CloneClass>, private val units: List<Unit>) : CloneHandler {
    private val clonedUnits: List<Unit> = retrieveClonedUnitsFromCloneClasses(cloneClasses)

    fun calculateMetrics(): CloneMetrics {
        return CloneMetrics(
            numberOfClones = clonedUnits.count(),
            numberOfCloneClasses = cloneClasses.count(),
            percentageOfDuplicatedLines = calculatePercentageOfDuplicatedLines(),
            largestClone = findLargestClone(),
            largestCloneClass = findLargestCloneClass(),
            exampleClones = selectRandomExampleClones()
        )
    }

    private fun calculatePercentageOfDuplicatedLines(): Int {
        val totalLinesOfCode: Double = countLinesOfCode(units).toDouble()
        val clonedLinesOfCode: Double = countClonedLinesOfCode()
        return ((clonedLinesOfCode / totalLinesOfCode) * 100).toInt()
    }

    private fun countClonedLinesOfCode(): Double {
        return clonedUnits.groupBy { it.identifier }.values.sumByDouble { countLinesOfFile(it) }
    }

    private fun countLinesOfFile(group: List<Unit>): Double {
        val setOfLines: Set<Pair<Int, String>> = group.flatMap { unit -> processUnitContent(unit) }.toSet()
        val fileContent: String = setOfLines.joinToString("\n") { it.second }
        return countSourceLinesOfCode(fileContent).toDouble()
    }

    private fun processUnitContent(unit: Unit): List<Pair<Int, String>> {
        val beginLine: Int = unit.range.begin.line
        val linesList: List<Int> = (beginLine..unit.range.end.line).toList()
        val content: List<String> = unit.contentRaw.split("\n")

        return linesList.mapIndexed { index, it -> it to content[index] }
    }

    private fun findLargestClone(): Pair<Set<Unit>, Int> {
        val largestClone: Set<Unit> = cloneClasses.maxBy { cloneClass -> cloneClass.map { it.range.end.line - it.range.begin.line }.max()!! }!!
        return Pair(largestClone, largestClone.map { it.range.end.line - it.range.begin.line }.max()!!)
    }

    private fun findLargestCloneClass(): CloneClass {
        return cloneClasses.maxBy { it.size }!!
    }

    private fun selectRandomExampleClones(): List<Set<Unit>> {
        return cloneClasses.shuffled().take(EXAMPLE_CLONE_AMOUNT)
    }

    private fun countLinesOfCode(units: List<Unit>): Int {
        val files: Set<File> = units.filter { it.node is CompilationUnit }.map { it.node as CompilationUnit }.map { it.storage.get().path.toFile() }.toSet()
        return files.map { it.readText() }.map { it.countJavaSloc() }.sum()
    }

    private fun countSourceLinesOfCode(content: String): Int {
        return content.countJavaSloc()
    }

    companion object Constants {
        private const val EXAMPLE_CLONE_AMOUNT = 10
    }
}
