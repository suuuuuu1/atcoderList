package abc.abc462

fun main() {
    val n = readln().toInt()
    val ans = Array(n){ mutableListOf<Int>()}
    val k = Array(n){ mutableListOf<Int>()}
    for(i in 0 until n){
        k[i] = readln().split(" ").map{it.toInt()}.drop(1).toMutableList()
    }
    for(i in 0 until n){
        for(j in 0 until k[i].size){
            val t = k[i][j]-1
            ans[t].add(i+1)
        }
    }
    val a = StringBuilder()
    for (an in ans) {
        
    }
    for (an in ans) {

        println(an.size)
        print(" ")
        for (i in an) {
            print("$i ")
        }
        println()
    }
}