package model

import utility.Clone


class AnalysisResponse(
    val clones: List<Clone>,
    val cloneClasses: List<Set<Unit>>,
    val metrics: CloneMetrics
)
