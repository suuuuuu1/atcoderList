package abc.abc351

fun main() {
    val n = readln().toInt()
    val a = readln().split(" ").map { it.toInt() }
    val stack = ArrayDeque<Int>()
    var i = 0
    while (i != n) {
        stack.add(a[i])
        while (stack.size > 1 && stack[stack.size - 1] == stack[stack.size - 2]) {
            stack.removeLast()
            val t = stack.removeLast()
            stack.addLast(t + 1)
        }
        i++
    }
    println(stack.size)
}