package model

import utility.CloneClass


data class CloneMetrics(
    val numberOfClones: Int,
    val numberOfCloneClasses: Int,
    val percentageOfDuplicatedLines: Int,
    val largestClone: Pair<Set<Unit>, Int>,
    val largestCloneClass: CloneClass,
    val exampleClones: List<Set<Unit>>
)
