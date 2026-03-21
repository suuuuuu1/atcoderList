
package atc.atc001

class UnionFind(n: Int) {
    private val parent = IntArray(n) { it }
    private val size = IntArray(n) { 1 }

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
}

fun main() {
    val (n, q) = readln().split(" ").map { it.toInt() }
    val uf = UnionFind(n)

    val out = StringBuilder()

    repeat(q) {
        val (p, a, b) = readln().split(" ").map { it.toInt() }
        if (p == 0) {
            uf.unite(a, b)
        } else {
            if (uf.same(a, b)) {
                out.append("Yes\n")
            } else {
                out.append("No\n")
            }
        }
    }
    print(out)
}