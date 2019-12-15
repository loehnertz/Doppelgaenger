package controller.analysis

import controller.analysis.detection.CloneDetector
import controller.analysis.detection.CloneMetricsCalculator
import controller.analysis.parsing.Parser
import io.ktor.http.Parameters
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.getOrFail
import model.CloneMetrics
import model.ProjectType
import model.Unit
import model.resource.AnalysisRequest
import model.resource.AnalysisResponse
import utility.*
import java.io.File


class AnalysisController {
    fun analyze(analysisRequest: AnalysisRequest): AnalysisResponse {
        val startTime: Long = System.currentTimeMillis()
        val units: List<Unit> = Parser(analysisRequest.basePath, analysisRequest.projectRoot, analysisRequest.cloneType).parse()

        val cloneDetector = CloneDetector(analysisRequest.basePath, units, analysisRequest.massThreshold, analysisRequest.similarityThreshold, analysisRequest.cloneType)
        val (clones: List<Clone>, cloneClasses: List<CloneClass>) = cloneDetector.detectClones()

        val sequenceCloneClasses: List<CloneClass> = cloneDetector.findSequenceCloneClasses(clones).toList()
        val filteredCloneClasses: List<CloneClass> = cloneDetector.filterOutClonesIncludedInSequenceClasses(cloneClasses, sequenceCloneClasses)

        val allClonesClasses: List<CloneClass> = (filteredCloneClasses + sequenceCloneClasses).sortedByDescending { it.size }

        val metrics: CloneMetrics = CloneMetricsCalculator(allClonesClasses, units).calculateMetrics()
        return constructAnalysisResponse(allClonesClasses, metrics)
            .also { println("The analysis of project '${analysisRequest.basePath}' took ${(System.currentTimeMillis() - startTime) / 1000} seconds.") }
    }

    private fun constructAnalysisResponse(cloneClasses: List<CloneClass>, metrics: CloneMetrics): AnalysisResponse {
        return AnalysisResponse(cloneClasses = cloneClasses, metrics = metrics)
    }

    companion object {
        @KtorExperimentalAPI
        fun retrieveProjectDirectory(parameters: Parameters): File {
            return ProjectRoot + when (ProjectType.getProjectTypeByName(parameters.getOrFail("projectType"))) {
                ProjectType.LOCAL -> File(parameters.getOrFail("projectRoot").removeSuffix("/") + normalizeBasePath(parameters.getOrFail("basePath")))
                ProjectType.GIT   -> File(cloneRepository(parameters.getOrFail("projectRoot")), normalizeBasePath(parameters.getOrFail("basePath")))
            }
        }

        fun normalizeBasePath(basePath: String): String {
            return if (basePath.startsWith('/')) {
                basePath
            } else {
                "/$basePath"
            }
        }

        fun writeResponseToFile(analysisRequest: AnalysisRequest, response: AnalysisResponse) {
            File("${analysisRequest.projectRoot.absolutePath}/report.json").also { it.delete() }.also { it.createNewFile() }.writeText(response.toJson())
        }

        private fun cloneRepository(gitUri: String): File {
            val projectDirectory = File("/tmp/${gitUri.substringAfterLast('/')}")
            if (projectDirectory.exists()) return projectDirectory
            ProcessBuilder("git", "clone", gitUri, projectDirectory.absolutePath).start().also { it.waitFor() }
            return projectDirectory
        }
    }
}
