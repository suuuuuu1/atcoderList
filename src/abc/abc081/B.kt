package abc.abc081

// genre:
fun main() {
    val n = readln().toInt()
    var a = readln().split(" ").map{it.toInt()}
    var count = 0
    while(a.all{it %2 == 0}){
        a = a.map { it / 2 }
        count ++
    }
    println(count)
}