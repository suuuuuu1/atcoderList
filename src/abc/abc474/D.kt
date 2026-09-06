package abc.abc474

import java.math.BigInteger

fun main() {
    val n = readln().toInt()
    val takahashi = readln().split(" ").map { it.toBigInteger() }
    val aoki = readln().split(" ").map { it.toBigInteger() }
    var takahashiSum = BigInteger("0")
    var aokiSum = BigInteger("0")
    val max = Math.pow(10.0, 18.0).toLong()
    val sb = StringBuilder()
    for (i in 0 until n) {
        if (aoki[i] < takahashi[i]) {
            takahashiSum = takahashiSum + (takahashi[i] * max.toBigInteger())
            aokiSum = aokiSum + (aoki[i] * max.toBigInteger())
            sb.append(max).append(" ")
        } else {
            takahashiSum = takahashiSum + (takahashi[i] * 1.toBigInteger())
            aokiSum = aokiSum + (aoki[i] * 1.toBigInteger())
            sb.append(1).append(" ")
        }
    }

    if (takahashiSum > aokiSum) {
        println("Yes")
        println(sb)
        return
    }
    println("No")
}