package abc.abc344

import java.util.HashMap

fun main() {
    val n = readln().toInt()
    val a = readln().split(" ").map{it.toInt()}
    val m = readln().toInt()
    val b = readln().split(" ").map{it.toInt()}
    val l = readln().toInt()
    val c = readln().split(" ").map{it.toInt()}
    val q = readln().toInt()
    val x = readln().split(" ").map{it.toInt()}
    val hash = HashMap<Int,Int>()
    for (i in a) {
        for (i1 in b) {
            for (i2 in c) {
                val t = i+i1+i2
                hash.put(t,hash.getOrDefault(t,0)+1)
            }
        }
    }
    val sb = StringBuilder()
    for (x1 in x) {
        if(hash.getOrDefault(x1,0) == 0){
            sb.append("No").append("\n")
        } else{
            sb.append("Yes").append("\n")
        }
    }
    println(sb)
}