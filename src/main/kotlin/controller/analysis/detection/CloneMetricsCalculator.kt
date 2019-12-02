package controller.analysis.detection

import com.github.javaparser.ast.CompilationUnit
import model.CloneMetrics
import model.Unit
import utility.Clone
import utility.JsonClone
import utility.map


class CloneMetricsCalculator(private val clones: List<Clone>, private val units: List<Unit>) : CloneHandler {
    fun calculateMetrics(): CloneMetrics {
        val clonedUnits: List<Unit> = retrieveClonedUnits()
        val cloneClasses: Map<Int, List<Unit>> = retrieveCloneClasses(clonedUnits)

        return CloneMetrics(
            numberOfClones = clonedUnits.count(),
            numberOfCloneClasses = cloneClasses.count(),
            percentageOfDuplicatedLines = calculatePercentageOfDuplicatedLines(clonedUnits),
            largestClone = findLargestClone(),
            exampleClones = selectRandomExampleClones()
        )
    }

    private fun retrieveClonedUnits(): List<Unit> {
        return clones.flatMap { it.toList() }.toSet().let { filterOutSubClonesFromCloneUnitCollection(it) }
    }

    private fun retrieveCloneClasses(clonedUnits: List<Unit>): Map<Int, List<Unit>> {
        return clonedUnits.groupBy { it.hash }
    }

    private fun calculatePercentageOfDuplicatedLines(clonedUnits: List<Unit>): Int {
        val totalLinesOfCode: Double = countLinesOfCode(units).toDouble()
        val clonedLinesOfCode: Double = clonedUnits.sumBy { countSourceLinesOfCode(it.content) }.toDouble()
        return ((clonedLinesOfCode / totalLinesOfCode) * 100).toInt()
    }

    private fun findLargestClone(): Pair<JsonClone, Int> {
        val largestClone: Pair<Unit, Unit> = clones.maxBy { listOf(it.first.range.lineCount, it.second.range.lineCount).max()!! }!!
        return Pair(largestClone.map { it.convertToJsonUnit() }, listOf(largestClone.first.range.lineCount, largestClone.second.range.lineCount).max()!!)
    }

    private fun selectRandomExampleClones(): List<JsonClone> {
        return clones.shuffled().take(EXAMPLE_CLONE_AMOUNT).map { Pair(it.first.convertToJsonUnit(), it.second.convertToJsonUnit()) }
    }

    private fun countLinesOfCode(units: List<Unit>): Int {
        val files = units.filter { it.node is CompilationUnit }.map { it.node as CompilationUnit }.map { it.storage.get().path.toFile() }.toSet()
        val fileLinesWithoutBlockComments = files.map { it.readText() }.map { it.replace(MultilineCommentRegex, "") }.map { it.split("\n") }
        val fileLinesWithoutBlockCommentsAndLineComments = fileLinesWithoutBlockComments.map { fileContents -> fileContents.filter { !isBlankLine(it) && !isCommentLine(it) } }
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
        private val MultilineCommentRegex = Regex("/\\*[\\s\\S]*?\\*/", RegexOption.MULTILINE)
    }
}
