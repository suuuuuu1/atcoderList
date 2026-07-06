package abc.abc459

fun main() {
    val n = readln().toInt()
    val s = readln().split(" ")
    val sb = StringBuilder()
    for (i in s) {
        val t = i.take(1)
        if (t == "a" || t == "b" || t == "c") {
            sb.append(2)
        }
        if (t == "d" || t == "e" || t == "f") {
            sb.append(3)
        }
        if (t == "g" || t == "h" || t == "i") {
            sb.append(4)
        }
        if (t == "j" || t == "k" || t == "l") {
            sb.append(5)
        }
        if (t == "m" || t == "n" || t == "o") {
            sb.append(6)
        }
        if (t == "p" || t == "q" || t == "r" || t == "s") {
            sb.append(7)
        }
        if (t == "t" || t == "u" || t == "v") {
            sb.append(8)
        }
        if (t == "w" || t == "x" || t == "y" || t == "z") {
            sb.append(9)
        }
    }
    println(sb)
}