package abc.abc486
 
//https://suuuuuu1.github.io/atcoderList/#
fun main() {
    val n = readln().toInt()
    val s = readln()
    val sb = StringBuilder()
    for (ch in s) {
        when (ch) {
            'A' -> sb.append("Alpha").append(" ")
            'B' -> sb.append("Bravo").append(" ")
            'C' -> sb.append("Charlie").append(" ")
        }
    }
    println(sb)
}