package betu
import java.util.LinkedList
import java.util.Queue
import java.util.PriorityQueue

fun main() {
    // 追加する適当な数字のリスト
    val numbers = listOf(5, 1, 3, 6, 2, 5, 12, 5)

    // 1. 普通のQueue (FIFO: 先に入れたものが先に出る)
    val standardQueue: Queue<Int> = LinkedList()

    // 2. PriorityQueue (デフォルトは昇順: 小さいものが先に出る)
    val minPriorityQueue = PriorityQueue<Int>()

    // 3. PriorityQueue (降順: 大きいものが先に出る)
    val maxPriorityQueue = PriorityQueue<Int>(compareByDescending { it })

    // 3つのキューに、全く同じ順番で数字を追加していく
    for (num in numbers) {
        standardQueue.add(num)
        minPriorityQueue.add(num)
        maxPriorityQueue.add(num)
    }

    println("■ 追加した順番: $numbers\n")

    // それぞれのキューから要素がなくなるまで取り出し（poll）続ける

    print("1. 普通のQueue         : ")
    while (standardQueue.isNotEmpty()) {
        print("${standardQueue.poll()} ")
    }
    println()

    print("2. 昇順 PriorityQueue : ")
    while (minPriorityQueue.isNotEmpty()) {
        print("${minPriorityQueue.poll()} ")
    }
    println()

    print("3. 降順 PriorityQueue : ")
    while (maxPriorityQueue.isNotEmpty()) {
        print("${maxPriorityQueue.poll()} ")
    }
    println()
}
