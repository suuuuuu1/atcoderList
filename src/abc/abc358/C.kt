package abc.abc358

fun main() {
    val (n,m) = readln().split(" ").map{it.toInt()}
    val s = Array(n){ readln()}

    val t = 1 shl n
    var minAns = Int.MAX_VALUE
    for(i in 0 until t){
        var ans = 0
        val isEaten = BooleanArray(m){false}
        for(j in 0 until n){
            if(i and (1 shl j) == 0){
                for(k in 0 until m){
                   if(s[j][k] == 'o')isEaten[k] = true
                }
                ans ++
            }


            var ok = true
            for (b in isEaten) {
                if(!b)ok = false
            }
            if(ok){
                minAns = minOf(minAns,ans)
            }
        }
    }
    println(minAns)
}