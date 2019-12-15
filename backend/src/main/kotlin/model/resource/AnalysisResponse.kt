package model.resource

import model.CloneMetrics
import utility.CloneClass


class AnalysisResponse(
    val cloneClasses: List<CloneClass>,
    val metrics: CloneMetrics
)
