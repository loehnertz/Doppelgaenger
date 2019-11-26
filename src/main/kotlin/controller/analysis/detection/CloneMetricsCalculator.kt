package controller.analysis.detection

import model.CloneMetrics
import model.Unit
import utility.Clone
import utility.JsonClone
import utility.map


class CloneMetricsCalculator(private val clones: List<Clone>) {
    fun calculateMetrics(): CloneMetrics {
        val clonedUnits: List<Unit> = retrieveClonedUnits()

        return CloneMetrics(
            numberOfClones = clonedUnits.count(),
            largestClone = findLargestClone(),
            exampleClones = selectRandomExampleClones()
        )
    }

    private fun retrieveClonedUnits(): List<Unit> {
        return clones.flatMap { it.toList() }.toSet().let { CloneUtilities.filterOutSubClonesFromCloneUnitCollection(it) }
    }

    private fun findLargestClone(): Pair<JsonClone, Int> {
        val largestClone: Pair<Unit, Unit> = clones.maxBy { listOf(it.first.range.lineCount, it.second.range.lineCount).max()!! }!!
        return Pair(largestClone.map { it.convertToJsonUnit() }, listOf(largestClone.first.range.lineCount, largestClone.second.range.lineCount).max()!!)
    }

    private fun selectRandomExampleClones(): List<JsonClone> {
        return clones.shuffled().take(EXAMPLE_CLONE_AMOUNT).map { Pair(it.first.convertToJsonUnit(), it.second.convertToJsonUnit()) }
    }

    companion object Constants {
        private const val EXAMPLE_CLONE_AMOUNT = 10
    }
}
