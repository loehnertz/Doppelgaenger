package model

import java.io.File


data class AnalysisRequest(
    val projectRoot: File,
    val massThreshold: Int
)
