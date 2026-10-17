package abc.abc480

// genre:
fun main() {
    val a = readln()
    val b= readln()
    if(a[0] == 'A'){
        if(a[1] == 'D'){
            println("Yes")
            return
        }
        if(b[0] == 'D'){
            println("Yes")
            return
        }
    }
    if(a[1] == 'A'){
        if(a[0] == 'D'){
            println("Yes")
            return
        }
        if(b[1] == 'D'){
            println("Yes")
            return
        }
    }
    if(b[0] == 'A'){
        if(a[0] == 'D'){
            println("Yes")
            return
        }
        if(b[1] == 'D'){
            println("Yes")
            return
        }
    }
    if(b[1] == 'A'){
        if(b[0] == 'D'){
            println("Yes")
            return
        }
        if(a[1] == 'D'){
            println("Yes")
            return
        }
    }
    println("No")
}