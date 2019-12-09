package model

class AnalysisResponse(
    val cloneClasses: List<Set<Unit>>,
    val sequenceCloneClasses: List<Set<List<Unit>>>,
    val metrics: CloneMetrics
)
