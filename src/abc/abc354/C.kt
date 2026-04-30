package abc.abc355
fun main() {
    val n = readln().toInt()
    val input = Array(n) {
        val parts = readln().split(" ")
        val a = parts[0].toInt()
        val c = parts[1].toInt()
        a to c
    }.withIndex()
    for (indexedValue in input) {
        println(indexedValue)
    }
    println()
    val sortedInput = input.sortedByDescending { it.value.first }
    for (indexedValue in sortedInput) {
        println(indexedValue)
    }
    val ans = mutableListOf<Int>()
    var minCost = Int.MAX_VALUE
    for (card in sortedInput) {
        val cost = card.value.second
        val originalIndex = card.index + 1 // 問題文に合わせるため1-based indexにする
        if (cost <= minCost) {
            ans.add(originalIndex)
            minCost = cost
        }
    }
    ans.sort()
    println(ans.size)
    println(ans.joinToString(" "))
}

