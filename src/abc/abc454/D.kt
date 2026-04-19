package abc.abc454

fun main() {
    val t = readln().toInt()
    val sb  = StringBuilder()
    repeat(t){
        val a = readln()
        val b = readln()
        var count = 0
        for (c in a) {
            if(c == 'x')count++
        }
        for (c in b) {
            if(c == 'x')count--
        }
        if(count == 0){
            sb.append("Yes").append("\n")
        }else{
            sb.append("No").append("\n")
        }

    }
    println(sb)
}