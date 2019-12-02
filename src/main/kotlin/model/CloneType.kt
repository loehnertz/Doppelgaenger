package model


enum class CloneType {
    ONE,
    TWO,
    THREE,
    FOUR;

    companion object {
        fun getCloneTypeByName(name: String): CloneType = valueOf(name.toUpperCase())
    }
}
