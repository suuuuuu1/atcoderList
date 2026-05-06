package abc.abc456

fun main() {
    val x = readln().toInt()
    var a = 0
    for(i in 1 until 7){
        for(j in 1 until 7){
            for(k in 1 until 7){
                if(i+j+k == x){
                    println("Yes")
                    return
                }
            }
        }
    }
    println("No")
}