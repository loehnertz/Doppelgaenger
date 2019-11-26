package utility


object Utilities {
    fun <T> cartesianProduct(list: List<T>): List<Pair<T, T>> {
        val pairs: ArrayList<Pair<T, T>> = arrayListOf()
        for (item1 in list) {
            for (item2 in list) {
                if (item1 == item2) continue
                if (pairs.contains(Pair(item1, item2)) || pairs.contains(Pair(item2, item1))) continue
                pairs.add(Pair(item1, item2))
            }
        }
        return pairs.toList()
    }
}
