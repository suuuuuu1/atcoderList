package abc.abc474

fun main() {
    val x = readln().toInt()
    for(i in 1 until 4){
        if(x != i) {
            print(i)
            return
        }
    }
}