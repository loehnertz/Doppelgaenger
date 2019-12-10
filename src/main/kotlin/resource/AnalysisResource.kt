package resource

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import controller.analysis.AnalysisController
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import model.AnalysisRequest
import model.AnalysisResponse
import model.CloneType
import model.CloneType.Companion.getCloneTypeByName
import model.ProjectType
import java.io.File


fun Route.analysis(controller: AnalysisController) {
    route("/analysis") {
        get("/") {
            val projectDirectory: File = when (ProjectType.getProjectTypeByName(call.parameters["projectType"]!!.toString())) {
                ProjectType.LOCAL -> File(call.parameters["projectRoot"]!!.toString())
                ProjectType.GIT   -> File(AnalysisController.cloneRepository("git@github.com:kevinvandervlist/SE.git"), AnalysisController.normalizeBasePath(call.parameters["basePath"]!!.toString()))
            }

            val request = AnalysisRequest(
                basePath = call.parameters["basePath"]!!.toString(),
                projectRoot = projectDirectory,
                cloneType = getCloneTypeByName(call.parameters["cloneType"] ?: CloneType.ONE.toString()),
                massThreshold = call.parameters["massThreshold"]?.toInt()
            )

            val response: AnalysisResponse = controller.analyze(request)

            call.respond(response).also { AnalysisController.writeResponseToFile(request, response) }
        }
    }

    @Suppress("UNUSED_VARIABLE")
    val mapper = jacksonObjectMapper().apply { setSerializationInclusion(JsonInclude.Include.NON_NULL) }
}
