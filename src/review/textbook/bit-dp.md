---
title: bit DP
category: DP
acl: -
genres: bitDP, DP
order: 3050
summary: 「どれを使ったか」の集合を整数で表して DP する (N ≦ 20 程度)
level: 水
keywords: bitDP, bit DP, 集合DP, 巡回セールスマン, TSP, traveling salesman, 部分集合列挙, マッチング, 順列, 訪問済み
---

## 何ができる？

「N 個のうち、どれをもう使ったか」という **集合** を、N 桁の 2 進数 (整数) で表して DP の添字にします。
N! 通りの順番を全部試す代わりに、2^N × N 程度で解けます。

| 関数 | 内容 | 計算量 |
| --- | --- | --- |
| `tsp(dist)` | 巡回セールスマン: 頂点 0 から全頂点を 1 回ずつ回って 0 に戻る最小コスト | O(2^N N^2) |
| `matching(ok)` | N 人と N 人の完全マッチングの数 (mod 10^9+7) | O(2^N N) |
| `grouping(a)` | N 匹をグループ分けして、同じグループのペアの得点の和を最大化 (**部分集合列挙**) | O(3^N) |

目安: N ≦ 16〜20 程度。

| N | 2^N | 2^N × N^2 | 3^N |
| --- | --- | --- | --- |
| 10 | 約 1000 | 10^5 | 6×10^4 |
| 16 | 約 6.5×10^4 | 1.7×10^7 | 4.3×10^7 |
| 20 | 約 10^6 | 4×10^8 (きつい) | 3.5×10^9 (無理) |

## いつ使う？

- **N が 20 以下くらいと極端に小さい** (これが一番のサイン)
- 「すべての都市を 1 回ずつ訪れる」「順番を決める」「全員を割り当てる」
- 順列 N! 通りの全探索 (N ≦ 8 程度まで) では間に合わないが、「どれを使ったか」だけ分かれば先が決まる
- 「グループに分ける」「集合を分割する」→ 部分集合列挙で O(3^N)

## しくみ

### 集合を整数で表す

N = 4 のとき、集合 {0, 2, 3} は 2 進数で `1101` (= 13)。i ビット目が 1 なら「i を含む」です。

| やりたいこと | Kotlin |
| --- | --- |
| i を含むか | `(s shr i and 1) == 1` |
| i を追加 | `s or (1 shl i)` |
| i を削除 | `s and (1 shl i).inv()` |
| 全体集合 | `(1 shl n) - 1` |
| 要素数 | `Integer.bitCount(s)` |

### 巡回セールスマン (TSP)

`dp[S][v]` = 訪問済みの集合が S で、今 v にいるときの最小コスト。

```
dp[{0}][0] = 0
dp[S ∪ {u}][u] = min( dp[S][v] + dist[v][u] )    (u は S に含まれない)
答え = min_v ( dp[全体][v] + dist[v][0] )
```

S ∪ {u} は S より必ず大きい整数なので、**S を 0, 1, 2, ... と小さい順に回すだけ** で正しい順番になります。

### 部分集合列挙

集合 S の空でない部分集合を全部回すテンプレートです。

```kotlin
var sub = s
while (sub > 0) {
    // sub は s の部分集合
    sub = (sub - 1) and s
}
```

全ての S について部分集合を回しても、合計 3^N 回で済みます (各要素が「S に無い / S にあって sub に無い / sub にある」の 3 通り)。

## 実装 (Kotlin)

```kotlin
// 巡回セールスマン: dist[i][j] = i から j へのコスト。0 から出発し全頂点を 1 回ずつ訪れて 0 に戻る最小コスト
fun tsp(dist: Array<LongArray>): Long {
    val n = dist.size
    val INF = Long.MAX_VALUE / 4
    // dp[s][v] = 訪問済み集合が s で、今 v にいるときの最小コスト
    val dp = Array(1 shl n) { LongArray(n) { INF } }
    dp[1][0] = 0
    for (s in 1 until (1 shl n)) {
        for (v in 0 until n) {
            val cur = dp[s][v]
            if (cur == INF) continue
            for (u in 0 until n) {
                if ((s shr u and 1) == 1) continue  // u は訪問済み
                val ns = s or (1 shl u)
                dp[ns][u] = minOf(dp[ns][u], cur + dist[v][u])
            }
        }
    }
    val full = (1 shl n) - 1
    var ans = INF
    for (v in 0 until n) if (dp[full][v] != INF) ans = minOf(ans, dp[full][v] + dist[v][0])
    return ans
}

// 完全マッチングの数 (EDPC O): ok[i][j] = 男性 i と女性 j が相性が良いか。mod 1e9+7
fun matching(ok: Array<BooleanArray>): Long {
    val MOD = 1_000_000_007L
    val n = ok.size
    // dp[s] = 男性 0..(|s|-1) を、女性の集合 s とペアにする方法の数
    val dp = LongArray(1 shl n)
    dp[0] = 1
    for (s in 0 until (1 shl n)) {
        if (dp[s] == 0L) continue
        val i = Integer.bitCount(s)  // 次にペアを決める男性
        if (i == n) continue
        for (j in 0 until n) {
            if ((s shr j and 1) == 0 && ok[i][j]) {
                val ns = s or (1 shl j)
                dp[ns] = (dp[ns] + dp[s]) % MOD
            }
        }
    }
    return dp[(1 shl n) - 1]
}

// グループ分け (EDPC U): 同じグループの i < j について a[i][j] を足した合計の最大値。部分集合列挙で O(3^N)
fun grouping(a: Array<LongArray>): Long {
    val n = a.size
    val full = 1 shl n
    // score[s] = s を 1 つのグループにしたときの得点
    val score = LongArray(full)
    for (s in 1 until full) {
        val i = Integer.numberOfTrailingZeros(s)  // s の中で一番小さい要素
        val rest = s and (s - 1)                  // s から i を除いた集合
        var add = 0L
        for (j in i + 1 until n) if ((rest shr j and 1) == 1) add += a[i][j]
        score[s] = score[rest] + add
    }
    // dp[s] = 集合 s をいくつかのグループに分けたときの得点の最大値
    val dp = LongArray(full)
    for (s in 1 until full) {
        var best = Long.MIN_VALUE
        var sub = s
        while (sub > 0) {  // s の空でない部分集合 sub を 1 つのグループにする
            best = maxOf(best, score[sub] + dp[s xor sub])
            sub = (sub - 1) and s
        }
        dp[s] = best
    }
    return dp[full - 1]
}
```

## 使い方

[ABC 180 E - Traveling Salesman among Aerial Cities](https://atcoder.jp/contests/abc180/tasks/abc180_e) を解くコードです。
都市 (a, b, c) から (p, q, r) へのコストは |p − a| + |q − b| + max(0, r − c)。都市 1 から全都市を回って都市 1 に戻ります。

```kotlin
fun main() {
    val br = System.`in`.bufferedReader()
    var st = java.util.StringTokenizer("")
    fun next(): String {
        while (!st.hasMoreTokens()) st = java.util.StringTokenizer(br.readLine())
        return st.nextToken()
    }

    val n = next().toInt()
    val x = LongArray(n)
    val y = LongArray(n)
    val z = LongArray(n)
    for (i in 0 until n) {
        x[i] = next().toLong()
        y[i] = next().toLong()
        z[i] = next().toLong()
    }
    val dist = Array(n) { i ->
        LongArray(n) { j -> Math.abs(x[j] - x[i]) + Math.abs(y[j] - y[i]) + maxOf(0L, z[j] - z[i]) }
    }
    println(tsp(dist))
}
```

入力例

```
2
0 0 0
1 2 3
```

出力例

```
9
```

入力例2

```
3
0 0 0
1 1 1
-1 -1 -1
```

出力例2

```
10
```

## 例題

- [EDPC O - Matching](https://atcoder.jp/contests/dp/tasks/dp_o) — 「男性を順番に決める」ので、何人目かは集合の要素数から分かる。`matching` そのもの
- [EDPC U - Grouping](https://atcoder.jp/contests/dp/tasks/dp_u) — 部分集合列挙 O(3^N)。`grouping` そのもの
- [ABC 180 E - Traveling Salesman among Aerial Cities](https://atcoder.jp/contests/abc180/tasks/abc180_e) — 巡回セールスマンそのもの (上の「使い方」)

## 注意点・よくあるミス

- **Kotlin の演算子の優先順位**: `s and 1 == 0` は `s and (1 == 0)` と解釈されてコンパイルエラーになります。
  `(s and 1) == 0`、`(s shr i and 1) == 1` のように **必ずかっこ** を付ける。
  (`shr` `and` などの中置関数どうしは左から順なので、`s shr i and 1` は `(s shr i) and 1` です)
- メモリ: `dp[1 shl n][n]` の Long は N = 20 で約 168MB。N が大きいときは Int にする、`Array<LongArray>` より 1 次元 `LongArray((1 shl n) * n)` にするなどで節約。
- TSP で「スタート地点に戻らない」問題なら、最後に `+ dist[v][0]` をしない。スタートが自由なら全頂点を `dp[1 shl v][v] = 0` にする。
- 到達できない状態 (INF) に足し算しない (`if (cur == INF) continue`)。
- 部分集合列挙の `while (sub > 0)` は **空集合を回りません**。空集合も必要なら do-while にして `sub == 0` の処理後に抜ける。
- グラフの TSP で辺が無い組は、先に **ワーシャルフロイド** で全点間最短距離にしておくと楽です。
