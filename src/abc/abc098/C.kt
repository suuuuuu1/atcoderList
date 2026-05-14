package abc.abc098

fun main() {
    val n = readln().toInt()
    val s = readln()
    var module = s.substring(1).count{it == 'E'}
    var ans = module
    for(i in 1 until n){
        if(s[i-1] == 'W') module++
        if(s[i] == 'E') module--
        ans = minOf(ans,module)
    }
    
    println(ans)
}