package abc.abc477

fun main() {
    val (n, d) = readln().split(" ").map { it.toInt() }
    val xInput = readln().split(" ").map { it.toInt() }
    var ans = 0
    val ans2 = mutableListOf<Int>()
    val hash = HashMap<Int, Int>()
    for (i in xInput) {
        hash.put(i,hash.getOrDefault(i,0)+1)
    }
    val xinputset = xInput.toSet().sorted().toMutableList()

    var c = 1
    for (i in xInput) {

        var t = true
        for(j in 1 until  100){
            if(hash[i] != 1){
                t = false
                break
            }
            if(c+j-1 in 0 until n ){
                if(Math.abs(xInput[c-1] - xInput[c+j-1]) < d){
                    t = false
                    break
                }

            }

            if( c-j-1 in 0 until n){
                if((Math.abs(xInput[c-1] - xInput[c-j-1] ) < d)) {
                    t = false
                    break
                }
            }
        }
        if(t){
            ans ++
            ans2.add(c)
        }
        c++
    }
    println(ans)
    ans2.sort()
    println(ans2.joinToString(" "))

}