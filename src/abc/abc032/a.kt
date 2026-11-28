package abc.abc032

fun main(args: Array<String>){
    println(calc(2,3,::add))
    println(calc(2,3,::mul))
}
fun add (a : Int , b : Int) = a + b
fun mul (a: Int, b: Int) = a * b
fun calc (a : Int , b : Int , c :(Int,Int)->Int) = c(a,b)

