package resource

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import controller.analysis.AnalysisController
import io.ktor.application.call
import io.ktor.features.MissingRequestParameterException
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.getOrFail
import model.CloneType.Companion.getCloneTypeByName
import model.resource.AnalysisRequest
import model.resource.AnalysisResponse


@KtorExperimentalAPI
fun Route.analysis(controller: AnalysisController) {
    route("/analysis") {
        get("/") {
            try {
                val request: AnalysisRequest = AnalysisRequest(
                    basePath = AnalysisController.normalizeBasePath(call.parameters.getOrFail("basePath")),
                    projectRoot = AnalysisController.retrieveProjectDirectory(call.parameters),
                    cloneType = getCloneTypeByName(call.parameters.getOrFail("cloneType")),
                    similarityThreshold = call.parameters.getOrFail("similarityThreshold").toDouble() / 100,
                    massThreshold = call.parameters.getOrFail("massThreshold").toInt()
                ).also { it.verify() }

                val response: AnalysisResponse = controller.analyze(request)

                call.respond(response).also { AnalysisController.writeResponseToFile(request, response) }
            } catch (e: MissingRequestParameterException) {
                call.respond(HttpStatusCode.BadRequest, e.localizedMessage)
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
