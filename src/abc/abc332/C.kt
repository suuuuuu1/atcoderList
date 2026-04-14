package abc.abc332

fun main() {
    val (n,m) = readln().split(" ").map{it.toInt()}
    val s = readln()
    var noLogoT = m
    var logoT = 0
    var logoCount = 0
    var noLogoCount = m
    var buyCount = 0
    for (c in s) {
        when(c.digitToInt()){
            0 -> {
                noLogoT = noLogoCount
                logoT = logoCount
            }
            1 ->{
                if(noLogoT == 0 &&  logoT == 0){
                    buyCount++
                    logoCount++
                }else if(noLogoT > 0)noLogoT --
                else logoT--
            }
            else ->{
                if(logoT > 0)logoT --
                else {
                    logoCount++
                    buyCount++
                }
            }

        }



    }
    println(buyCount)
}

@Suppress("unused")
private fun chmin(a: IntArray, idx: Int, v: Int): Boolean {
    if (v < a[idx]) {
        a[idx] = v
        return true
    }
    return false
}

@Suppress("unused")
private fun chmax(a: IntArray, idx: Int, v: Int): Boolean {
    if (v > a[idx]) {
        a[idx] = v
        return true
    }
    return false
}

@Suppress("unused")
private fun chmin(a: LongArray, idx: Int, v: Long): Boolean {
    if (v < a[idx]) {
        a[idx] = v
        return true
    }
    return false
}

@Suppress("unused")
private fun chmax(a: LongArray, idx: Int, v: Long): Boolean {
    if (v > a[idx]) {
        a[idx] = v
        return true
    }
    return false
}