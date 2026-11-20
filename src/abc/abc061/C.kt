package abc.abc061
 
//https://suuuuuu1.github.io/atcoderList/#
fun main() {
    val (n,k) = readln().split(" ").map { it.toLong() }
    val count = LongArray(100001)

    repeat(n.toInt()) {
        val (a, b) = readln().split(" ").map { it.toInt() }
        count[a] += b.toLong()
    }

    var remaining = k

    for (i in 1..100000) {
        remaining -= count[i]

        if (remaining <= 0) {
            println(i)
            break
        }
    }
}