package abc.abc460

fun main() {
    var (n,m) = readln().split(" ").map{it.toInt()}
    var x = 10000
    var ans = 0
    while(x != 0){
        x = n % m
        m = x
        ans ++
    }
    println(ans)
}