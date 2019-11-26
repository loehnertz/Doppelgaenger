package model

import utility.Clone


data class CloneMetrics(
    val numberOfClones: Int? = null,
    val numberOfCloneClasses: Int? = null,
    val percentageOfDuplicatedLines: Int? = null,
    val largestClone: Pair<Clone, Int>? = null
)
