package controller.analysis

import controller.analysis.detection.CloneDetector
import controller.analysis.detection.CloneMetricsCalculator
import controller.analysis.parsing.Parser
import model.*
import model.Unit
import utility.Clone


class AnalysisController {
    fun analyze(analysisRequest: AnalysisRequest): AnalysisResponse {
        val startTime: Long = System.currentTimeMillis()
        val units: List<Unit> = Parser(analysisRequest.projectRoot, analysisRequest.cloneType).parse()
        val clones: List<Clone> = CloneDetector(units, analysisRequest.massThreshold).detectClones()
        val metrics: CloneMetrics = CloneMetricsCalculator(clones).calculateMetrics()
        return constructAnalysisResponse(clones, metrics)
            .also { println("The analysis of project '${analysisRequest.projectRoot}' took ${(System.currentTimeMillis() - startTime) / 1000} seconds.") }
    }

    private fun constructAnalysisResponse(clones: List<Clone>, metrics: CloneMetrics): AnalysisResponse {
        val jsonFriendlyClones: List<Pair<JsonUnit, JsonUnit>> = clones.map { Pair(it.first.convertToJsonUnit(), it.second.convertToJsonUnit()) }
        return AnalysisResponse(clones = jsonFriendlyClones, metrics = metrics)
    }
}
