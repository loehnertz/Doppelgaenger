package controller.analysis.detection

import model.CloneMetrics
import utility.Clone
import utility.JsonClone


class CloneMetricsCalculator(private val clones: List<Clone>) {
    fun calculateMetrics(): CloneMetrics {
        return CloneMetrics(
            exampleClones = selectRandomExampleClones()
        )
    }

    private fun selectRandomExampleClones(): List<JsonClone> {
        return clones.shuffled().take(EXAMPLE_CLONE_AMOUNT).map { Pair(it.first.convertToJsonUnit(), it.second.convertToJsonUnit()) }
    }

    companion object Constants {
        private const val EXAMPLE_CLONE_AMOUNT = 10
    }
}
