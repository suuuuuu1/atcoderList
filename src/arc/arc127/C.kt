package arc.arc127
 
//https://suuuuuu1.github.io/atcoderList/#
fun main() {
    val n = readln().toInt()
    val x = readln()

    val bits = IntArray(n)
    var ones = 0

    for (i in x.indices) {
        val bit = x[x.lastIndex - i].digitToInt()
        bits[i] = bit
        ones += bit
    }

    fun subtractOne() {
        var i = 0

        while (bits[i] == 0) {
            bits[i] = 1
            ones++
            i++
        }

        bits[i] = 0
        ones--
    }

    subtractOne()

    val ans = StringBuilder("1")
    for (i in n - 1 downTo 1) {
        if (bits[i] == 1) {
            ans.append('1')
            bits[i] = 0
            ones--
        } else if (ones > 0) {
            ans.append('0')
            subtractOne()
        } else {
            break
        }
    }
    println(ans)
}