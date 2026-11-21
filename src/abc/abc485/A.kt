package abc.abc485
 
//https://suuuuuu1.github.io/atcoderList/#
fun main() {
    val (a,b) = readln().split(" ").map{it.toInt()}
    if(a >= 0 && b >= 0){
        println("Yes")
    }else if(a <= 0 && b <= 0){
        println("Yes")
    }else{
        println("No")
    }
}