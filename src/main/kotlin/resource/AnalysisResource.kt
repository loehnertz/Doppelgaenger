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
import java.io.File


fun Route.analysis(controller: AnalysisController) {
    route("/analysis") {
        get("/") {
            val analysisRequest = AnalysisRequest(
                projectRoot = File(call.parameters["projectRoot"]!!.toString()),
                massThreshold = call.parameters["massThreshold"]?.toInt()
            )
            val response: AnalysisResponse = controller.analyze(analysisRequest)
            call.respond(response)
        }
    }

    @Suppress("UNUSED_VARIABLE")
    val mapper = jacksonObjectMapper().apply { setSerializationInclusion(JsonInclude.Include.NON_NULL) }
}
