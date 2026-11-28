package abc.abc486
 
//https://suuuuuu1.github.io/atcoderList/#
fun main() {
    val (n,k) = readln().split(" ").map{it.toInt()}
    val s = readln().toCharArray()
    val hot = BooleanArray(n) {false}
    val list = mutableListOf<Char>()
    for(i in 0 until n){
        list.add('.')
    }
    var ans = 0
    for(i in 0 until n){
        if(s[i] == '.')continue
        if(hot[i])continue
        var j = 0
        while(i+j in 0 until n && !hot[i+j]){
            if(s[i+j] == '.')break
            list[i+j] = 'o'
            hot[i+j] = true
            j += k
        }
        ans ++

    }
    println(ans)
}