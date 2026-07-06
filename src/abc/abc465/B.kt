package abc.abc465

fun main() {
    val tt = readln().split(" ").map{it.toInt()}
    val x = tt[0]
    val y = tt[1]
    val l = tt[2]
    val r = tt[3]
    val a = tt[4]
    val b = tt[5]
    var sum = 0
    for(i in a until b){
        if(i in l until r){
            sum += x
        }else{
            sum += y
        }
    }
    println(sum)
}