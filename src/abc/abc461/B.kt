package abc.abc461

fun main() {
    val n = readln().toInt()
    val a = readln().split(" ").map{it.toInt()}
    val b = readln().split(" ").map{it.toInt()}
    for(i in 0 until n ){
        if(i+1 != a[b[i]-1]){
            println("No")
            return
        }

    }
    println("Yes")
}