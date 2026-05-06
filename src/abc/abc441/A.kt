package abc.abc441

fun main() {
    val (p,q) = readln().split(" ").map{it.toInt()}
    val (x,y) = readln().split(" ").map{it.toInt()}
    if(x >= p && x < p+100 && y >= q && y < q+100){
        println("Yes")
    }else{
        println("No")
    }
}