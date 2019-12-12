package model

import java.io.File


data class AnalysisRequest(
    val basePath: String,
    val projectRoot: File,
    val cloneType: CloneType = CloneType.ONE,
    val massThreshold: Int?,
    val similarityThreshold: Double
)
