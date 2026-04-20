package abc.abc328

fun main() {
    val (n,q) = readln().split(" ").map{it.toInt()}
    val s = readln()
    val ruisekiwa = IntArray(n+1){0}
    for(i in 0 until s.length-1){
        ruisekiwa[i+1] = ruisekiwa[i]
        if(s[i] == s[i+1]){
            ruisekiwa[i+1] = ruisekiwa[i]+1
        }
    }
    repeat(q){
        val (l,r) = readln().split(" ").map{it.toInt()}
        println(ruisekiwa[r-1] - ruisekiwa[l-1])
    }
}