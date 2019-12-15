package controller.analysis.detection

import controller.analysis.parsing.Parser
import model.CloneType
import java.io.File

abstract class AbstractCloneDetectorTest {

    private val exampleFile = File(this::class.java.getResource("/java/").path)
    val cloneDetector: CloneDetector

    init {
        val basePath = ""
        val cloneType = CloneType.ONE
        cloneDetector = CloneDetector(
            basePath = basePath,
            units = Parser(basePath, exampleFile, cloneType).parse(),
            massThreshold = 5,
            similarityThreshold = 0.0,
            cloneType = cloneType
        )
    }
}
