package betu.kasupaiza

fun main(args: Array<String>) {
    val (day,MaxHP,heal) = readln().split(" ").map{it.toInt()}
    val train = Array(day){readln().split(" ").map{it.toInt()}}
    val dp = Array(day){IntArray(MaxHP+1){-1} }
    if(MaxHP-train[0][0] >= 0) dp[0][train[0][0]] = train[0][1]
    dp[0][minOf(MaxHP,MaxHP+heal)] = maxOf(dp[0][minOf(MaxHP,MaxHP+heal)],0)
    for(i in 1 until day){
        val TrainValue = train[i][0]
        val NeedHP = train[i][1]
        for(hp in 0 .. MaxHP){
            if(dp[i-1][hp] == -1)continue
            if(hp - NeedHP >= 0){
                dp[i][hp - NeedHP] = maxOf(dp[i][hp - NeedHP], dp[i-1][hp] + TrainValue)
            }
            dp[i][minOf(MaxHP , hp + heal)] = maxOf(dp[i][minOf(MaxHP , hp + heal)],dp[i-1][hp])
        }
    }
    var ans = 0
    for(hp in 0 .. MaxHP){
        ans = maxOf(ans,dp[day-1][hp])
    }
    println(ans)
}