package abc.abc458

fun main() {
    val (h,w) = readln().split(" ").map{it.toInt()}
    val dx = listOf(-1,0,1,0)
    val dy = listOf(0,1,0,-1)
    val ans = Array(h){IntArray(w)}
    for(i in 0 until h){
        for(j in 0 until w){
            var count = 0
            for(k in 0 until 4){
                if((i + dy[k]) in 0 until h && (j + dx[k]) in 0 until w){
                    count++
                }
            }
            ans[i][j] = count
        }
    }
    for (an in ans) {
        for (i in an) {
            print(i)
            print(" ")
        }
        println()
    }
}