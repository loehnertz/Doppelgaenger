package model

import utility.Clone


data class CloneMetrics(
    val numberOfClones: Int,
    val numberOfCloneClasses: Int,
    val percentageOfDuplicatedLines: Int,
    val largestClone: Pair<Clone, Int>,
    val largestCloneClass: Set<Unit>,
    val exampleClones: List<Clone>
)
