package abc.abc472

fun main() {
    val (n, m, k) = readln().split(" ").map { it.toLong() }
    val a = readln().split(" ").map { it.toLong() }
    val b = BooleanArray(n.toInt()) { false }
    var nowCaloly = 0L
    val sb = StringBuilder()

    for (i in 0 until n.toInt()) {
        if (i - m >= 0) {
            if (b[i - m.toInt()] == true) {

                nowCaloly -= a[i - m.toInt()]
            }
        }
        if (nowCaloly + a[i] <= k) {
            nowCaloly += a[i]
            sb.append("Yes\n")
            b[i] = true
        } else {
            sb.append("No\n")
        }



    }
    println(sb)
}