package model


enum class ProjectType {
    LOCAL,
    GIT;

    companion object {
        fun getProjectTypeByName(name: String): ProjectType = valueOf(name.toUpperCase())
    }
}
