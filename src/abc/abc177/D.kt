package abc.abc177

class UnionFind(n: Int) {
    private val parent = IntArray(n + 1) { it }
    private val size = IntArray(n+1) { 1 }

    fun find(x: Int): Int {
        if (parent[x] == x) return x
        parent[x] = find(parent[x])
        return parent[x]
    }

    fun unite(x: Int, y: Int) {
        val rootX = find(x)
        val rootY = find(y)

        if (rootX == rootY) return

        if (size[rootX] < size[rootY]) {
            parent[rootX] = rootY
            size[rootY] += size[rootX]
        } else {
            parent[rootY] = rootX
            size[rootX] += size[rootY]
        }
    }

    fun same(x: Int, y: Int): Boolean {
        return find(x) == find(y)
    }
    fun getSize(x: Int):Int{
        return size[find(x)]
    }
}

fun main() {
    val (n, m) = readln().split(" ").map { it.toInt() }
    val unionFind = UnionFind(n)
    for (i in 0 until m) {
        val (a, b) = readln().split(" ").map { it.toInt() }
        unionFind.unite(a, b)
    }
    var max = 0
    for (i in 1..n) {
        max = maxOf(unionFind.getSize(i), max)
    }
    println(max)
}