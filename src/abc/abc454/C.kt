package abc.abc454


fun main() {
    val (n,m) = readln().split(" ").map{it.toInt()}

    val ary = Array(n){ mutableListOf<Int>()}
    repeat(m) {
        val (a, b) = readln().split(" ").map { it.toInt() - 1 }
        ary[a].add(b)

    }
    val stack = ArrayDeque<Int>()
    stack.add(0)
    var ans = 0
    val Bool = BooleanArray(n){false}
    Bool[0] = true
    while (stack.isNotEmpty()){
        val t = stack.removeLastOrNull()
        for (i in ary[t!!]) {

            if(Bool[i])continue
            Bool[i] = true
            stack.add(i)
            ans++
        }
    }
    println(ans+1)
}