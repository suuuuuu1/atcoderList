package abc.abc485

//https://suuuuuu1.github.io/atcoderList/#
fun main() {

    val n = readln().toInt()
    val s = readln().toMutableList()
    val one = mutableListOf<Char>()
    val two = mutableListOf<Char>()
    val three = mutableListOf<Char>()
    var a = true
    var b = true
    var c = true
    var i = 0

    while (a || b || c) {
        val tmp = s[i]
        when (tmp) {
            'A' -> {
                one.add(tmp)
                a = false
            }

            'B' -> {
                one.add(tmp)
                b = false
            }

            'C' -> {
                one.add(tmp)
                c = false
            }
        }
        i++
        if (i >= n) {
            println(0)
            return
        }
    }
    a = true
    b = true
    c = true
    while (a || b || c) {
        val tmp = s[i]
        when (tmp) {
            'A' -> {
                two.add(tmp)
                a = false
            }

            'B' -> {
                two.add(tmp)
                b = false
            }

            'C' -> {
                two.add(tmp)
                c = false
            }
        }
        i++

        if (i >= n) {
            println(0)
            return
        }
    }
    three.addAll(i,s)

}