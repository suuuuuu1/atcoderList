package abc.abc332

fun main() {
    val (n, m) = readln().split(" ").map { it.toInt() }
    val s = readln()
    var musi = m
    var now = m
    var logo = 0
    var ans = 0
    for (c in s) {
        when (c.digitToInt()) {
            0 -> {
                musi = m
                logo = ans
            }

            1 -> {
                if (musi > 0) {
                    musi--
                    now--
                } else if(logo > 0){
                    logo --
                }else{
                    ans++
                }
            }

            2 -> {
                if(logo == 0){
                    ans++
                }else{
                    logo --
                }
            }
        }
    }
    println(ans)
}