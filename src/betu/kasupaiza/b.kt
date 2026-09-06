package betu.kasupaiza

fun main(args: Array<String>) {
    val h = readln().toInt()
    var s = Array(h) { readln().toCharArray() }
    var s2 = Array(h) { CharArray(h) }
    for (i in 0 until h) {
        s2[i] = s[i].clone()
    }

    val seeX = intArrayOf(1, 0, -1, 0)
    val seeY = intArrayOf(0, -1, 0, 1)
    var count  = 1
    while ( count != 1000) {
        count ++
        for (i in 0 until h) {
            for (j in 0 until 5) {
                var check = true
                for (k in 0 until 4) {
                    if (!(i + seeY[k] in 0 until h) || !(j + seeX[k] in 0 until 5))continue
                    if(s[i][j] != s[i+seeY[k]][j + seeX[k]]){
                        check = false
                        break
                    }
                }
                if(check){
                    for (k in 0 until 4) {
                        s2[i][j] = '.'
                        if (!(i + seeY[k] in 0 until h) || !(j + seeX[k] in 0 until 5))continue
                        s2[i + seeY[k]][j + seeX[k]] = '.'
                    }
                }
            }

        }



        for (i in 0 until 5) {
            repeat(h - 1) {
                for (k in h - 2 downTo 0) {
                    if (s2[k + 1][i] == '.' && s2[k][i] != '.') {
                        val temp = s2[k][i]
                        s2[k][i] = '.'
                        s2[k + 1][i] = temp
                    }
                }
            }
        }

        for (i in 0 until h) {
            s[i] = s2[i].clone()
        }
    }
    for (chars in s2) {
        for (ch in chars) {
            print(ch)
        }
        println()
    }


}