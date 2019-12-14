package model.resource

import model.CloneMetrics
import model.Unit


class AnalysisResponse(
    val cloneClasses: List<Set<Unit>>,
    val metrics: CloneMetrics
)
