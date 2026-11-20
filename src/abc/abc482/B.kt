package abc.abc482
 
//https://suuuuuu1.github.io/atcoderList/#
fun main() {
    val grid = Array(5){readln().toCharArray()}
    var min = Int.MAX_VALUE
    for(i in 0 until 5){
        var count = 0
        for(j in 0 until 5){
            if(grid[i][j] == '.')count++
        }
        min = minOf(min, count)
    }

    for(i in 0 until 5){
        var count = 0
        for(j in 0 until 5){
            if(grid[j][i] == '.')count++
        }
        min = minOf(min, count)
    }

    var count = 0
    for(i in 0 until 5){
        if(grid[i][i] == '.')count++
    }
    min = minOf(min, count)

    count = 0
    for(i in 0 until 5){
        if(grid[4-i][i] == '.')count++
    }
    min = minOf(min, count)
    println(min)
}