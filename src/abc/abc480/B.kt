package abc.abc480

// genre:
fun main() {
    val (n,m) = readln().split(" ").map{it.toInt()}
    val s = Array(n){readln()}
    val t = Array(m){readln()}
    val ans = IntArray(n){0}
    for(tIndex in 0 until m){
        val mut = mutableListOf<Int>()
        var ok = true
        for(sIndex in 0 until n){
            var complete = 0
            if(t[tIndex].equals(s[sIndex])){
                ans[sIndex]++
                ok = false
                break
            }
            if((t[tIndex].length) == (s[sIndex].length)){
                for(stIndex in 0 until t[tIndex].length){
                    if(s[sIndex][stIndex] == t[tIndex][stIndex]){
                        complete++
                    }
                }
            }
            if((t[tIndex].length-complete == 1) || (t[tIndex].length - complete == 0)){
                mut.add(sIndex)
            }
        }

        if(mut.size == 1 && ok == true){
            ans[mut[0]]++
        }

    }
    println(ans.joinToString("\n"))
}