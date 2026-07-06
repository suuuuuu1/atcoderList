package abc.abc465

fun main() {
    val n = readln().toInt()
    val s = readln()
    val a = IntArray(n)
    val sb = ArrayDeque<Int>()
    for(i in 0 until n){
        a[i] = i+1
    }
    var ismaekara = true
    var marucount = 0
    for(i in 0 until n){
        if(s[i] == 'o'){
            marucount++
            ismaekara = !ismaekara
            if(ismaekara){
                sb.addFirst(a[i])
            }else{
                sb.addLast(a[i])
            }
        }else{
            if(ismaekara){
                sb.addLast(a[i])
            }else{
                sb.addFirst(a[i])
            }
        }
    }
    val ans = StringBuilder()
    if(marucount %2 == 0){
        for (i in sb) {
            ans.append(i).append(" ")
        }
    }else{
        sb.reverse()
        for (i in sb) {
            ans.append(i).append(" ")
        }
    }
    println(ans)
}