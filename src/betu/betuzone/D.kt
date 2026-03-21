package betu.betuzone

import java.util.ArrayDeque

fun main() {
    val s = readln().trim()
    val deque = ArrayDeque<Char>()
    var reversed = false

    for (ch in s) {
        if (ch == 'R') {
            reversed = !reversed
            continue
        }

        if (!reversed) {
            if (deque.isNotEmpty() && deque.peekLast() == ch) {
                deque.pollLast()
            } else {
                deque.addLast(ch)
            }
        } else {
            if (deque.isNotEmpty() && deque.peekFirst() == ch) {
                deque.pollFirst()
            } else {
                deque.addFirst(ch)
            }
        }
    }

    val sb = StringBuilder()



    if (!reversed) {
        while (deque.isNotEmpty()) sb.append(deque.pollFirst())
    } else {
        while (deque.isNotEmpty()) sb.append(deque.pollLast())
    }
    println(sb.toString())

}