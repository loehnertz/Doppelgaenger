package model

import utility.JsonClone


class AnalysisResponse(
    val clones: List<JsonClone>,
    val metrics: CloneMetrics
)
