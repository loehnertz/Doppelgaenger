package controller.analysis

import controller.analysis.detection.CloneDetector
import controller.analysis.detection.CloneMetricsCalculator
import controller.analysis.parsing.Parser
import model.AnalysisRequest
import model.AnalysisResponse
import model.CloneMetrics
import model.Unit
import utility.Clone
import utility.toJson
import java.io.File


class AnalysisController {
    fun analyze(analysisRequest: AnalysisRequest): AnalysisResponse {
        val startTime: Long = System.currentTimeMillis()
        val units: List<Unit> = Parser(analysisRequest.projectRoot, analysisRequest.cloneType).parse()
        val clones: List<Clone> = CloneDetector(units, analysisRequest.massThreshold, (100).toDouble()).detectClones()
        val metrics: CloneMetrics = CloneMetricsCalculator(clones, units).calculateMetrics()
        return constructAnalysisResponse(clones, metrics)
            .also { println("The analysis of project '${analysisRequest.projectRoot}' took ${(System.currentTimeMillis() - startTime) / 1000} seconds.") }
    }

    private fun constructAnalysisResponse(clones: List<Clone>, metrics: CloneMetrics): AnalysisResponse {
        return AnalysisResponse(clones = clones.map { Pair(it.first.convertToJsonUnit(), it.second.convertToJsonUnit()) }, metrics = metrics)
    }

    fun writeResponseToFile(analysisRequest: AnalysisRequest, response: AnalysisResponse) {
        File("${analysisRequest.projectRoot.absolutePath}/report.json").also { it.delete() }.also { it.createNewFile() }.writeText(response.toJson())
    }
}
