package abc.abc485
 
//https://suuuuuu1.github.io/atcoderList/#
fun main() {
    val n = readln().toInt()
    val s = readln()

    val lastSet = mutableSetOf<Char>()
    var last = n

    for (i in n - 1 downTo 0) {
        lastSet.add(s[i])

        if (lastSet.size == 3) {
            last = i
            break
        }
    }

    val firstSet = mutableSetOf<Char>()
    var first = 0

    while (first < last && firstSet.size < 3) {
        firstSet.add(s[first])
        first++
    }

    if (firstSet.size < 3) {
        println(0)
        return
    }

    val count = mutableMapOf('A' to 0, 'B' to 0, 'C' to 0)
    var second = first
    var ans = 0L

    while (first < last) {
        while (second < last && count.values.any { it == 0 }) {
            val c = s[second]
            count[c] = count.getValue(c) + 1
            second++
        }

        if (count.values.any { it == 0 }) break

        ans += (last - second + 1).toLong()

        val c = s[first]
        count[c] = count.getValue(c) - 1
        first++
    }

    println(ans)
}