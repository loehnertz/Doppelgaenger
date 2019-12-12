package model.resource

import model.CloneType
import java.io.File


data class AnalysisRequest(
    val basePath: String,
    val projectRoot: File,
    val cloneType: CloneType = CloneType.ONE,
    val similarityThreshold: Double,
    val massThreshold: Int?
) {
    fun verify() {
        check(projectRoot.exists()) { "Project root does not exist" }
        check(similarityThreshold in 0.0..1.0) { "Similarity threshold has to be a percentage between 0 and 100" }
        if (massThreshold != null) check(massThreshold >= 0) { "Mass threshold has to be a positive integer" }
    }
}
