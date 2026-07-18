package abc.abc467

import java.math.BigDecimal

fun main() {
    val (h,w) = readln().split(" ").map{it.toInt()}
    val h2 = BigDecimal(h/100.0)
    val w2 = BigDecimal(w)
    println(if((w2 / h2 / h2) >= (25.0).toBigDecimal()) "Yes" else "No")
}