package abc.abc466

fun main() {
    val n = readln().toInt()
    val x = readln().split(" ").map { it.toInt() }
    for (x1 in x) {
        if(x1 >= 0){
            println("No")
            return
        }
    }
    println("Yes")
}