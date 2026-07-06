package abc.abc458
fun main() {
    val s = readln()
    val cLengs = LongArray(s.length)
    for(i in 0 until s.length){
        if(s[i] == 'C')cLengs[i] = i.toLong()
    }
    var count = 0L

    for(i in s.length-1 downTo 0 ){
        if(s[i] == 'C')cLengs[i] = minOf(cLengs[i],count)
        count ++
    }
    for(i in 0 until s.length){
        if(s[i] == 'C')cLengs[i]++
    }
    println(cLengs.sum())

}
