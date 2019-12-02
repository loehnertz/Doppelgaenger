package controller.analysis.detection

import model.Unit
import utility.Clone
import utility.Utilities


class CloneDetector(private val units: List<Unit>, private val massThreshold: Int?) {
    fun detectClones(): List<Clone> {
        return units
            .filter { it.mass >= massThreshold ?: calculateNodeMassAverage() }
            .groupBy { it.hash }
            .map { it.value }
            .filter { it.size > 1 }
            .flatMap { Utilities.cartesianProduct(it) }
            .let { CloneUtilities.filterOutSubClonesFromCloneCollection(it) }
    }

    private fun calculateNodeMassAverage(): Int = units.map { it.mass }.average().toInt()
}
