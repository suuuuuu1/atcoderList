package abc.abc462

fun main(args: Array<String>) {
    val (n,h,w) = readln().split(" ").map{it.toInt()}
    var (sy,sx) = readln().split(" ").map{it.toInt()-1}
    val s = readln()
    val grid = Array(n){mutableListOf<Int>()}
    for(i in 0 until h){
        val t = readln().split(" ").map{it.toInt()}.toMutableList()
        grid[i] = t
    }
    val sb = StringBuilder()
    for(i in s){
        if(i == 'F')sy--
        if(i == 'B')sy++
        if(i == 'L')sx--
        if(i == 'R')sx++
        sb.append(grid[sy][sx]).append("\n")
    }
    print(sb)
}