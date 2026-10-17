---
title: 周期検出
category: 基本テクニック
acl: -
genres: シミュレーション
order: 2090
summary: 操作を繰り返すといつかループすることを使い、K 回後を O(N) で求める
level: 緑
keywords: cycle detection, ループ, 周期, ρ (ロー), 繰り返し, K 回後, 10^18 回, 鳩の巣原理, visited, functional graph
---

## 何ができる？

状態の種類が N 個しかなく、次の状態が今の状態だけで決まる操作は、**N 回以内に必ず同じ状態に戻ってループ** します。
これを使って、K 回 (10^18 回でも) 操作した後の状態や、それまでの合計を求めます。

| 操作 | 内容 | 計算量 |
| --- | --- | --- |
| `Cycle(n, start, f)` | start から f を繰り返し適用し、ループに入るまでの手数 `mu` と周期 `lam` を求める | O(N) |
| `kth(k)` | k 回操作した後の状態 | O(1) |
| `sumFirst(k, value)` | 0 回目〜k-1 回目の状態 (k 個) の value の合計 | O(N) |

## いつ使う？

- 「K 回操作した後は？」で **K が 10^18 などとても大きい** が、**状態の種類は 10^5〜10^6 程度** しかない
- 次の状態が今の状態だけで決まる (テレポーター、`x → x^2 mod M`、`x → f(x)` など)
- 「数列の第 N 項」「N 項目までの和」を求める漸化式 (`A_{n+1} = A_n^2 mod M` など)
- 開始点が 1 つだけ。いろいろな開始点から聞かれるなら [ダブリング](doubling.md) を考える

## しくみ

状態を順にたどっていくと、ρ (ロー) の字の形になります。

```
start
  a0 → a1 → a2 → a3 → a4 → a5
                 ↑              ↓
                 a8 ← a7 ← a6 ←┘   (a9 = a3 に戻ってきた)

mu  = 3  (ループに入るまでの手数。ループの最初の状態は a3)
lam = 6  (ループの長さ: a3, a4, a5, a6, a7, a8)
```

`visited[v]` = 「状態 v に何回目に来たか」(未訪問なら -1) を記録しながら進み、
**すでに訪問済みの状態に来たら** そこでストップします。

- その状態の `visited` の値が `mu` (ループの始まり)
- 今の手数 − `mu` が `lam` (周期)

K 回後は
- K < mu なら そのまま `order[K]`
- K ≥ mu なら ループ内の位置 `(K - mu) % lam` を使って `order[mu + (K - mu) % lam]`

合計も同じで、「ループに入るまで」+「ループ 1 周の和 × 周回数」+「余り」に分けて計算します。

## 実装 (Kotlin)

```kotlin
// start から f を繰り返し適用したときのループ (ρ の形) を調べる。状態は 0 until n の整数
class Cycle(n: Int, start: Int, f: (Int) -> Int) {
    val order = ArrayList<Int>() // 訪れた順の状態 (ループ 1 周目の終わりまで)。長さ mu + lam
    val mu: Int  // ループに入るまでの手数 (ループの最初の状態は order[mu])
    val lam: Int // ループの長さ (周期)

    init {
        val visited = IntArray(n) { -1 } // visited[v] = 何回目に v に来たか
        var cur = start
        while (visited[cur] == -1) {
            visited[cur] = order.size
            order.add(cur)
            cur = f(cur)
        }
        mu = visited[cur]
        lam = order.size - mu
    }

    // k 回操作した後の状態 (k = 0 なら start)
    fun kth(k: Long): Int {
        require(k >= 0)
        if (k < mu) return order[k.toInt()]
        return order[mu + ((k - mu) % lam).toInt()]
    }

    // 0 回目〜(k-1) 回目の状態 (k 個) について value(状態) の合計
    fun sumFirst(k: Long, value: (Int) -> Long): Long {
        require(k >= 0)
        val pre = LongArray(order.size + 1) // pre[i] = order[0..i-1] の value の合計
        for (i in order.indices) pre[i + 1] = pre[i] + value(order[i])
        if (k <= order.size) return pre[k.toInt()]
        val loopSum = pre[order.size] - pre[mu] // ループ 1 周分の合計
        val rest = k - mu
        val full = rest / lam             // ループを何周するか
        val rem = (rest % lam).toInt()    // 余り
        return pre[mu] + full * loopSum + (pre[mu + rem] - pre[mu])
    }
}
```

## 使い方

[ABC 167 D - Teleporter](https://atcoder.jp/contests/abc167/tasks/abc167_d): 町 1 から K 回 (K ≤ 10^18) テレポートした後の町を出力します。

```kotlin
fun main() {
    val br = System.`in`.bufferedReader()
    var st = java.util.StringTokenizer("")
    fun next(): String {
        while (!st.hasMoreTokens()) st = java.util.StringTokenizer(br.readLine())
        return st.nextToken()
    }

    val n = next().toInt()
    val k = next().toLong()
    val a = IntArray(n) { next().toInt() - 1 } // 0-indexed に
    val cyc = Cycle(n, 0) { a[it] }
    println(cyc.kth(k) + 1)
}
```

入力例

```
4 5
3 2 4 1
```

出力例

```
4
```

合計: [ABC 179 E - Sequence Sum](https://atcoder.jp/contests/abc179/tasks/abc179_e)。
A_1 = X、A_{i+1} = A_i^2 mod M のとき、A_1 + ... + A_N (N ≤ 10^10, M ≤ 10^5) を出力します。

```kotlin
fun main() {
    val (nStr, xStr, mStr) = readLine()!!.trim().split(" ")
    val n = nStr.toLong()
    val x = xStr.toInt()
    val m = mStr.toInt()
    val cyc = Cycle(m, x) { v -> (v.toLong() * v % m).toInt() }
    println(cyc.sumFirst(n) { it.toLong() })
}
```

入力例

```
6 2 1001
```

出力例

```
1369
```

## 例題

- [ABC 167 D - Teleporter](https://atcoder.jp/contests/abc167/tasks/abc167_d) — K ≤ 10^18 回のテレポート (上の使い方)
- [ABC 179 E - Sequence Sum](https://atcoder.jp/contests/abc179/tasks/abc179_e) — ループ + 合計 (上の使い方)
- [典型90 058 - Original Calculator（★4）](https://atcoder.jp/contests/typical90/tasks/typical90_bf) — 状態は 10^5 通りなので必ずループする

## 注意点・よくあるミス

- **ループは最初の状態に戻るとは限らない** (ρ の形)。`mu` (ループに入るまでの部分) を忘れると答えがずれます。
- K < mu (ループに入る前に終わる) の場合を分けて考える。
- 「K 回後」と「K 項目」で 1 ずれやすい。上の `kth(0)` は start、`sumFirst(k)` は start を含めて k 個の和です。
- 状態が数値でない (文字列やペア) ときは、`HashMap<状態, 何回目>` で visited を管理する。
- 状態の種類が多すぎる (10^7 を超える) と visited 配列が作れません。その場合は問題の性質を見直す。
- 合計の計算 `full * loopSum` は大きくなるので Long。mod を取る問題では掛け算の前に mod を取る。
