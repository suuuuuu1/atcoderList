package abc.abc483
 
//https://suuuuuu1.github.io/atcoderList/#
fun main() {
    val (n,q) = readln().split(" ").map{it.toInt()}
    val k = readln().split(" ").map{it.toInt()}
    var deckIndex = mutableListOf<Int>()
    for(i in 0 until n){
        deckIndex.add(i+1)
    }
    val bool = BooleanArray(deckIndex.size){true}
    for (i in k) {
        val a = mutableListOf<Int>()
        for(j in 0 until i){
            a.add(-deckIndex[j])
        }
        a.reverse()
        val b = mutableListOf<Int>()
        for(j in i until n){
            b.add(deckIndex[j])
        }
        val c = a+b

        for(j in 0 until deckIndex.size){
            deckIndex.removeFirst()
        }
        deckIndex = c.toMutableList()
    }
    println(deckIndex.joinToString("\n"))
}