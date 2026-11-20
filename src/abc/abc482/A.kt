package abc.abc482
 
//https://suuuuuu1.github.io/atcoderList/#
fun main() {
    val s = readln().toCharArray()
    println(s.count{it == 'o'}.let{if (it % 2 == 0) "even" else "odd"})
}