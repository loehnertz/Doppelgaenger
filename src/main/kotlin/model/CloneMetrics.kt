package model

import utility.JsonClone


data class CloneMetrics(
    val numberOfClones: Int,
    val numberOfCloneClasses: Int? = null,
    val percentageOfDuplicatedLines: Int? = null,
    val largestClone: Pair<JsonClone, Int>,
    val largestCloneClass: Unit? = null,
    val exampleClones: List<JsonClone>
)
