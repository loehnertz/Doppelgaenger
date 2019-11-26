package model


class AnalysisResponse(
    val clones: List<Pair<JsonUnit, JsonUnit>>,
    val metrics: CloneMetrics
)
