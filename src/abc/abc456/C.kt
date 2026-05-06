package abc.abc456

fun main() {
    val s = readln()
    var l = 0
    var r = 0
    val mod = 998244353
    var ans = 0L
    var length = 1L
    for (i in 0 until s.length - 1) {
        if (s[i] != s[i + 1]) {
            length++
        } else {
            val count = ((length * (length + 1)) / 2) % mod
            ans = (ans + count) % mod
            length = 1
        }
    }
    val count = ((length * (length + 1)) / 2) % mod
    ans = (ans + count) % mod
    println(ans)
}