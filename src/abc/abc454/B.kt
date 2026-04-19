package abc.abc454

fun main() {
    val (n,m) = readln().split(" ").map{it.toInt()}
    val f = readln().split(" ").map{it.toInt()}
    val hukuCount = IntArray(m)
    for(i in 0 until n){
        hukuCount[f[i]-1]++
    }
    var check = true
    for (i in hukuCount) {
        if(i >= 2)check  = false
    }
    if(check) {
        println("Yes")
    }else{
        println("No")
    }
    check = true
    for (i in hukuCount) {
       if(i == 0){
           check = false
           break
       }
    }
    if(check){
        println("Yes")
    }else{
        println("No")
    }

}