package controller.analysis.detection

import model.Unit


class CloneDetector(private val units: List<Unit>, private val massThreshold: Int) {
    fun detectClones(): List<Set<Unit>> {
        return units
            .filter { it.mass >= massThreshold }
            .groupBy(keySelector = { Pair(it.hash, it.type) }, valueTransform = { it })
            .map { it.value.toSet() }
            .filter { it.size > 1 }
    }
}
