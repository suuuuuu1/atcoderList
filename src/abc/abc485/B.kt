package abc.abc485
 
//https://suuuuuu1.github.io/atcoderList/#
fun main() {
    val (n,m) = readln().split(" ").map{it.toInt()}
    val s = Array(n){readln().toCharArray()}
    val s2 = Array(m){CharArray(n)}
    for(i in 0 until n){
        for(j in 0 until m ){
            s2[j][i] = s[i][j]
        }
    }
    for(i in 0 until n){
        if(s[i].all{it == 'x'}){
            println("Faucet ${i+1}")
            return
        }
    }
    for(i in 0 until m){
        if(s2[i].all{it == 'x'}){
            println("Drain ${i+1}")
            return
        }
    }
}