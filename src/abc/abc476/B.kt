package abc.abc476

fun main() {
    val n = readln().toInt()
    val s = readln()
    val t = readln()

    for (i in 0 until n) {
        if (t[i] != '*') {
            if(s[i] != t[i]){
                println("No")
                return
            }
        }
    }
    println("Yes")


}