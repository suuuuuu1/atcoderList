package abc.abc343

fun main() {
    val n = readln().toLong()
    var i = 1L
    var ans = 0L
    while(i * i * i <= n){
        val t = (i * i * i).toString()
        if(t == t.reversed()){
            ans =  t.toLong()
        }
        i++
    }
    println(ans)
}