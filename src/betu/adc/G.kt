package betu.adc

fun main() {
    val n = readln().toLong()
    val max = 1_000_000
    val isPrime = BooleanArray(max + 1) { true }
    isPrime[0] = false
    isPrime[1] = false

    var p = 2
    while (p * p <= max) {
        if (isPrime[p]) {
            var i = p * p
            while (i <= max) {
                isPrime[i] = false
                i += p
            }
        }
        p++
    }

    val primes = mutableListOf<Long>()
    for (i in 2..max) {
        if (isPrime[i]) {
            primes.add(i.toLong())
        }
    }
    var ans = 0L

    for (qIdx in 1 until primes.size) {
        val q = primes[qIdx]
        val q3 = q * q * q
        if (primes[0] * q3 > n) break

        val maxP = minOf(n / q3, q - 1)
        if (maxP < 2) continue

        val serchResult = primes.binarySearch(maxP, 0, qIdx)
        val count = if (serchResult >= 0) {
            serchResult + 1
        } else {
            -(serchResult - 1)
        }
        ans += count
    }
    println(ans)
}