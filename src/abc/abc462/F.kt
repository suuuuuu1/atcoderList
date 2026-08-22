package abc.abc462
fun main(args: Array<String>) {
    val n = readln().toInt()
    val grid = Array(n){
        val (h,w) = readln().split(" ").map{it.toInt()}
        Array(h) {readln().toCharArray()}
    }
    var ans = 0
    val variety = mutableListOf(0)
    for(i in 1 until n){
        var t = grid[i]
        val h = grid[0].size-1
        val w =  grid[0][0].size-1
        val t2 = t
        var a = false
        for(num in variety){
            for(j in 0 until 4){
                for(k in 0 until h){
                    for(l in 0 until w){
                        t2[l][h - k] = t[l][k]
                    }
                }
                for(k in 0 until h){
                    for(l in 0 until w){
                        t[k][l] = t2[k][l]
                    }
                }
                for (chars in t) {
                    for (char in chars) {
                        print(char)
                    }
                    println()
                }
                if(t.contentEquals (grid[num])){
                    a = true
                }
            }
        }
        if(!a){
            variety.add(i)
            ans++
            println(i)
        }
    }
    print(ans)
}