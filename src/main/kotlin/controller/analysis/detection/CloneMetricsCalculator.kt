package controller.analysis.detection

import com.github.javaparser.ast.CompilationUnit
import model.CloneMetrics
import model.Unit
import utility.MultilineCommentRegex
import java.io.File


class CloneMetricsCalculator(private val cloneClasses: List<Set<Unit>>, private val units: List<Unit>) : CloneHandler {
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
        val clonedLinesOfCode: Double = clonedUnits.sumBy { countSourceLinesOfCode(it.content) }.toDouble()
        return ((clonedLinesOfCode / totalLinesOfCode) * 100).toInt()
    }

    private fun findLargestClone(): Pair<Set<Unit>, Int> {
        val largestClone: Set<Unit> = cloneClasses.maxBy { cloneClass -> cloneClass.map { it.range.end.line - it.range.begin.line }.max()!! }!!
        return Pair(largestClone, largestClone.map { it.range.end.line - it.range.begin.line }.max()!!)
    }

    private fun findLargestCloneClass(): Set<Unit> {
        return cloneClasses.maxBy { cloneClass -> cloneClass.maxBy { unit -> unit.content.length }!!.content.length }!!
    }

    private fun selectRandomExampleClones(): List<Set<Unit>> {
        return cloneClasses.shuffled().take(EXAMPLE_CLONE_AMOUNT)
    }

    private fun countLinesOfCode(units: List<Unit>): Int {
        val files: Set<File> = units.filter { it.node is CompilationUnit }.map { it.node as CompilationUnit }.map { it.storage.get().path.toFile() }.toSet()
        val fileLinesWithoutBlockComments: List<List<String>> = files.map { it.readText() }.map { it.replace(MultilineCommentRegex, "") }.map { it.split("\n") }
        val fileLinesWithoutBlockCommentsAndLineComments: List<List<String>> = fileLinesWithoutBlockComments.map { fileContents -> fileContents.filter { !isBlankLine(it) && !isCommentLine(it) } }
        return fileLinesWithoutBlockCommentsAndLineComments.sumBy { it.count() }
    }

    private fun countSourceLinesOfCode(content: String): Int {
        return content.replace(MultilineCommentRegex, "").split("\n").filter { !isBlankLine(it) && !isCommentLine(it) }.count()
    }

    private fun isBlankLine(line: String): Boolean = line.isBlank()

    private fun isCommentLine(line: String): Boolean = line.trim().startsWith(SinglelineCommentToken)

    companion object Constants {
        private const val EXAMPLE_CLONE_AMOUNT = 10
        private const val SinglelineCommentToken = "//"
    }
}
