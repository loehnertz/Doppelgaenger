package utility


const val JavaFileExtension = "java"

const val JavaTestDirectory = "/test/"

const val SinglelineCommentToken = "//"

val MultilineCommentRegex = Regex("/\\*[\\s\\S]*?\\*/", RegexOption.MULTILINE)
