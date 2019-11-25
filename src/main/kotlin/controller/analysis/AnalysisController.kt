package controller.analysis

import controller.analysis.detection.CloneDetector
import controller.analysis.parsing.Parser
import model.AnalysisRequest
import model.Unit


class AnalysisController {
    fun analyze(analysisRequest: AnalysisRequest): List<Set<Unit>> {
        val startTime: Long = System.currentTimeMillis()
        val units: List<Unit> = Parser(analysisRequest.projectRoot).parse()
        val clones: List<Set<Unit>> = CloneDetector(units, analysisRequest.massThreshold).detectClones()
        return clones.also { println("The analysis of project '${analysisRequest.projectRoot}' took ${(System.currentTimeMillis() - startTime) / 1000} seconds.") }
    }
}
