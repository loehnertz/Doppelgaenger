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
import model.Unit
import java.io.File


fun Route.analysis(controller: AnalysisController) {
    route("/analysis") {
        get("/") {
            val analysisRequest = AnalysisRequest(
                projectRoot = File(call.parameters["projectRoot"]!!.toString()),
                massThreshold = call.parameters["massThreshold"]?.toInt()
            )
            val clones: List<Set<Unit>> = controller.analyze(analysisRequest)
            call.respond(clones.map { clone -> clone.map { Pair(it.node.toString(), it.location.toString()) } })
        }
    }

    @Suppress("UNUSED_VARIABLE")
    val mapper = jacksonObjectMapper().apply { setSerializationInclusion(JsonInclude.Include.NON_NULL) }
}
