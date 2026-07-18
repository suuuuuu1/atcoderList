package abc.abc467

fun main() {
    val n = readln().toInt()
    var keepMoney = 0
    repeat(n){
        val (a,b,s) = readln().split(" ")
        val a2 = a.toInt()
        val b2 = b.toInt()
        if(s == "keep") {
            keepMoney += b2 - a2
        }
    }
    println( keepMoney)
}