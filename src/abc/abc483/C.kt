package abc.abc483
 
//https://suuuuuu1.github.io/atcoderList/#
fun main() {
    val (n,m) = readln().split(" ").map{it.toInt()}
    val hash =  Array(n){HashMap<Int,Int>()}
    val first = IntArray(n)
    repeat(m){
        val (a,b)  = readln().split(" ").map{it.toInt()}
        first[a-1]++
        hash[a - 1][b - 1] = (hash[a - 1][b - 1] ?: 0) + 1
    }
    val rank = (0 until n).sortedWith(
        compareByDescending<Int>{first[it]}.thenBy{it}
    )
    val sb = StringBuilder()

    for(k in 0 until n){
        var winner = -1
        var maxVotes = -1

        for((b,count) in hash[k]){
            val votes = first[b] + count
            if(votes > maxVotes || (votes == maxVotes && b < winner)){
                maxVotes = votes
                winner = b
            }
        }
        for (b in rank) {
            if (b != k) {
                if (first[b] > maxVotes || (first[b] == maxVotes && b < winner)) {
                    maxVotes = first[b]
                    winner = b
                }
                break
            }
        }
        sb.append(winner + 1).append('\n')
    }
    println(sb)
}