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
        TODO("Implement")
    }
}
