package resource

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import controller.analysis.AnalysisController
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import model.CloneType
import model.CloneType.Companion.getCloneTypeByName
import model.ProjectType
import model.resource.AnalysisRequest
import model.resource.AnalysisResponse
import java.io.File


fun Route.analysis(controller: AnalysisController) {
    route("/analysis") {
        get("/") {
            try {
                val projectDirectory: File = when (ProjectType.getProjectTypeByName(call.parameters["projectType"]!!.toString())) {
                    ProjectType.LOCAL -> File(call.parameters["projectRoot"]!!.toString().removeSuffix("/"), AnalysisController.normalizeBasePath(call.parameters["basePath"]!!.toString()))
                    ProjectType.GIT   -> File(AnalysisController.cloneRepository(call.parameters["projectRoot"]!!.toString()), AnalysisController.normalizeBasePath(call.parameters["basePath"]!!.toString()))
                }

                val request: AnalysisRequest = AnalysisRequest(
                    basePath = AnalysisController.normalizeBasePath(call.parameters["basePath"]!!.toString()),
                    projectRoot = projectDirectory,
                    cloneType = getCloneTypeByName(call.parameters["cloneType"] ?: CloneType.ONE.toString()),
                    similarityThreshold = call.parameters["similarityThreshold"]!!.toDouble() / 100,
                    massThreshold = call.parameters["massThreshold"]?.toInt()
                ).also { it.verify() }

                val response: AnalysisResponse = controller.analyze(request)

                call.respond(response).also { AnalysisController.writeResponseToFile(request, response) }
            } catch (e: IllegalStateException) {
                call.respond(HttpStatusCode.BadRequest, e.localizedMessage)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e.localizedMessage)
            }
        }
    }

    @Suppress("UNUSED_VARIABLE")
    val mapper = jacksonObjectMapper().apply { setSerializationInclusion(JsonInclude.Include.NON_NULL) }
}
