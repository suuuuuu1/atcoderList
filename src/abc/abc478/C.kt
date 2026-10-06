package abc.abc478

fun main() {
    val (n, k) = readln().split(" ").map { it.toInt() }
    val a = readln().split(" ").map { it.toInt() }
    val asorted = a.sorted()
    val lst = mutableListOf<Int>()
    for(i in 0 until n){
        if(a[i] != asorted[i]) {
            lst.add(i)
            break
        }
    }
    if(lst.isEmpty()){
        println("Yes")
        return
    }
    for(i in n-1 downTo 0 ){
        if(a[i] != asorted[i]) {
            lst.add(i)
            break
        }
    }
    if(lst.last() - lst.first() +1 <=  k){
        println("Yes")
    }else{
        println("No")
    }

}