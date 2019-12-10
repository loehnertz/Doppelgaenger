package model

class AnalysisResponse(
    val cloneClasses: List<Set<Unit>>,
    val metrics: CloneMetrics
)
