package abc.abc117

fun main() {
    val (n,m) = readln().split(" ").map { it.toInt() }
    val x = readln().split(" ").map { it.toInt() }.sorted()
    if(n >= m) {
        println(0)
        return
    }
    val disances = x.zipWithNext{a,b -> b-a}.sorted()
    val ans = disances.take(m-n).sumOf { it.toLong() }
    println(ans)
}