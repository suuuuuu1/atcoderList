package abc.abc362
import kotlin.math.*

fun main() {
    val n = readln().toInt()
    val l = LongArray(n)
    val r = LongArray(n)
    repeat(n){
        val (a,b) = readln().split(" ").map { it.toLong() }
        l[it] = a
        r[it] = b
    }
    val sumL = l.sum()
    val sumR = r.sum()
    if(sumL > 0L || sumR < 0L){
        println("No")
        return
    }
    val x = l.copyOf()
    var need = -sumL
    for (i in 0 until n) {
        println(need)
        if(need == 0L)break
        val add = min(need,r[i] - l[i])
        x[i] += add
        need -= add
    }
    println("Yes")
    println(x.joinToString(" ") )


}
