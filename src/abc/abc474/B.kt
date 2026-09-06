package abc.abc474



fun main() {
    val n = readln().toInt()
    val p = readln().split(" ").map{it.toInt()}
    val set = HashSet<Int>()

    var count = 0
    for(i in 0 until  n){

        if(count % 10 == 0 && count != 0){

            for(j in count downTo count-9 ){
                set.remove(j)
            }

            if(set.size != 0){
                println("No")
                return
            }
        }
        set.add(p[i])
        count++
    }
    println("Yes")
}