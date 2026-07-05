package abc.abc461

fun main() {
    val (a,d) = readln().split(" ").map{it.toInt()}
    if(a > d){
        println("No")
    }else{
        println("Yes")
    }
}