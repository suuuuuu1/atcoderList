package abc.abc459

fun main() {
    val x = readln().toInt()
    val hello = "HelloWorld"
    for(i in 0 until hello.length){
        if(i == x-1)continue
        print(hello[i])
    }
}