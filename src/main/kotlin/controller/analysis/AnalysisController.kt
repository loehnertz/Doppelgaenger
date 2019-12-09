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
        val units: List<Unit> = Parser(analysisRequest.basePackageIdentifier, analysisRequest.projectRoot, analysisRequest.cloneType).parse()

        val cloneDetector = CloneDetector(analysisRequest.basePackageIdentifier, units, analysisRequest.massThreshold, (100).toDouble(), analysisRequest.cloneType) // TODO: add similarity Threshold as a request parameter
        val (clones: List<Clone>, cloneClasses: List<Set<Unit>>) = cloneDetector.detectClones()

        val sequenceCloneClasses: List<Set<List<Unit>>> = cloneDetector.findSequenceCloneClasses(clones).toList()
        val cloneClassesFiltered: List<Set<Unit>> = cloneDetector.filterOutClonesIncludedInSequenceClasses(cloneClasses, sequenceCloneClasses)

        val metrics: CloneMetrics = CloneMetricsCalculator(clones, units).calculateMetrics() // TODO: Calculate metrics using sequenceCloneClasses and cloneClassesFiltered
        return constructAnalysisResponse(cloneClassesFiltered, sequenceCloneClasses, metrics)
            .also { println("The analysis of project '${analysisRequest.projectRoot}' took ${(System.currentTimeMillis() - startTime) / 1000} seconds.") }
    }

    private fun constructAnalysisResponse(cloneClasses: List<Set<Unit>>, sequenceCloneClasses: List<Set<List<Unit>>>, metrics: CloneMetrics): AnalysisResponse {
        return AnalysisResponse(cloneClasses = cloneClasses, sequenceCloneClasses = sequenceCloneClasses, metrics = metrics)
    }

    fun writeResponseToFile(analysisRequest: AnalysisRequest, response: AnalysisResponse) {
        File("${analysisRequest.projectRoot.absolutePath}/report.json").also { it.delete() }.also { it.createNewFile() }.writeText(response.toJson())
    }
}
