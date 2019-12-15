package utility

import java.io.File


val ProjectRoot: File = File(System.getenv("PROJECT_ROOT") ?: "/")

const val JavaFileExtension = "java"

const val JavaTestDirectory = "/test/"

const val SinglelineCommentToken = "//"

val MultilineCommentRegex = Regex("/\\*[\\s\\S]*?\\*/", RegexOption.MULTILINE)
