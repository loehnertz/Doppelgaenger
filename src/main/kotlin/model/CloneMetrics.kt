package model

import utility.JsonClone


data class CloneMetrics(
    val numberOfClones: Int,
    val numberOfCloneClasses: Int,
    val percentageOfDuplicatedLines: Int,
    val largestClone: Pair<JsonClone, Int>,
    val largestCloneClass: List<JsonUnit>,
    val exampleClones: List<JsonClone>
)
