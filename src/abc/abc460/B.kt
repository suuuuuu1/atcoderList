package abc.abc460

fun main() {
    val t = readln().toInt()
    repeat(t) {
        val r = readln().split(" ").map { it.toDouble() }
        var x1 = r[0]
        var y1 = r[1];
        val r1 = Math.sqrt(r[2].toDouble())
        var x2 = r[3];
        var y2 = r[4];
        val r2 = Math.sqrt(r[5].toDouble())
        val susumux1 = r1 * Math.cos(Math.toRadians(45.0))
        val susumuy1 = r1 * Math.sin(Math.toRadians(45.0))
        val susumux2 = r2 * Math.cos(Math.toRadians(45.0))
        val susumuy2 = r2 * Math.sin(Math.toRadians(45.0))
        var a = false
        if (x1 <= x2) {
            if (y1 <= y2) {
                if ((x1 + susumux1) > (x2 - susumux2) ||(y1 + susumuy1) > (y2 - susumuy2)) a = true
            } else {
                if ((x1 + susumux1) > (x2 - susumux2) || (y1 - susumuy1) < (y2 + susumuy2)) a = true
            }

        } else {
            if (y1 <= y2) {
                if ((x1 - susumux1) < (x2 + susumux2) || (y1 + susumuy1) > (y2 - susumuy2)) a = true
            } else {
                if ((x1 - susumux1) < (x2 + susumux2) || (y1 - susumuy1) < (y2 + susumuy2)) a = true
            }
        }

        if (a) {
            println("Yes")
        } else {
            println("No")
        }
    }
}