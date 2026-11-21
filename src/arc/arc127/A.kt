package arc.arc127
 
//https://suuuuuu1.github.io/atcoderList/#
fun main() {
    val n = readln().toLong()

    var ans = 0L
    var ones = 1L

    while (ones <= n) {
        var start = ones
        var power = 1L

        while (start <= n) {
            val end = start + power - 1

            ans += maxOf(0L, minOf(n, end) - start + 1)

            start *= 10
            power *= 10
        }

        ones = ones * 10 + 1
    }

    println(ans)
}