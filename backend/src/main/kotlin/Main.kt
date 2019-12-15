package main

import com.fasterxml.jackson.databind.SerializationFeature
import controller.analysis.AnalysisController
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.jackson.jackson
import io.ktor.routing.Routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.KtorExperimentalAPI
import resource.analysis


@KtorExperimentalAPI
fun Application.module() {
    install(DefaultHeaders)

    install(CallLogging)

    install(CORS) {
        method(HttpMethod.Get)
        header(HttpHeaders.AccessControlAllowOrigin)
        anyHost()  // TODO: Secure this properly
    }

    install(ContentNegotiation) {
        jackson {
            configure(SerializationFeature.INDENT_OUTPUT, true)
        }
    }

    install(Routing) {
        analysis(AnalysisController())
    }
}

@KtorExperimentalAPI
fun main() {
    embeddedServer(factory = Netty, port = 8888, module = Application::module).start()
}
