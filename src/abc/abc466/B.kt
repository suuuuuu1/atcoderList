package abc.abc466

import java.util.HashMap

fun main() {
    val (n,m) = readln().split(" ").map{it.toInt()}
    val hash = IntArray(m){-1}
    repeat(n){
        val (c,s) = readln().split(" ").map{it.toInt()}
        if(hash[c-1] < s){
            hash[c-1] = s
        }
    }
    println(hash.joinToString (" "))
}