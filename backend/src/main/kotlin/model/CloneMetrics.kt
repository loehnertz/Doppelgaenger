package model


data class CloneMetrics(
    val numberOfClones: Int,
    val numberOfCloneClasses: Int,
    val percentageOfDuplicatedLines: Int,
    val largestClone: Pair<Set<Unit>, Int>,
    val largestCloneClass: Set<Unit>,
    val exampleClones: List<Set<Unit>>
)
