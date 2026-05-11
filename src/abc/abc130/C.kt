package abc.abc130

import java.math.BigDecimal

fun main() {
    val (w, h, x, y) = readln().split(" ").map { it.toDouble() }
    val sb = StringBuilder()
    sb.append(1.0 * w * h / 2)
    if ((x * 2) == w && (y * 2) == h) sb.append(" 1") else sb.append(" 0")
    println(sb)
}