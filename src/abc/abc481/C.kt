package abc.abc481

// genre:
fun main() {
    val q = readln().toInt()
    var c = 1
    val meibo = HashMap<String, Int>()
    val sb = StringBuilder()
    repeat(q) {
        val input = readln()
        if(meibo.contains(input)){
            sb.append(meibo[input]).append("\n")
        }else{
            sb.append(c).append("\n")
            meibo.put(input, c)
            c++
        }
    }
    println(sb.toString())
}