---
title: GCD / LCM (最大公約数・最小公倍数)
category: 数学
acl: -
genres: 整数論, 数学
order: 203
summary: ユークリッドの互除法で GCD を求め、オーバーフローせずに LCM を計算する
level: 茶
keywords: 最大公約数, 最小公倍数, gcd, lcm, ユークリッドの互除法, euclid, 互いに素, 公約数, 公倍数
---

## 何ができる？

| 関数 | 内容 | 計算量 |
| --- | --- | --- |
| `gcd(a, b)` | 最大公約数。`gcd(a, 0) = a` | O(log min(a, b)) |
| `lcm(a, b)` | 最小公倍数 (a, b ≥ 1。結果が Long に収まるとき) | O(log min(a, b)) |
| `lcmOrNull(a, b, limit)` | 最小公倍数。`limit` を超えるなら `null` | O(log min(a, b)) |

たくさんの数の GCD / LCM は、左から順に 2 つずつまとめていけば求まります。

## いつ使う？

- 「全部を割り切る最大の数」「何個ずつに等分できるか」→ GCD
- 「a と b は互いに素か」→ `gcd(a, b) == 1`
- 「x から ±D ずつ動いて全部の点に行ける最大の D」→ 差の GCD
- 「何秒後にまた全員そろう？」「両方で割り切れる最小の数」→ LCM
- 分数 a/b を約分したい → 両方を `gcd(a, b)` で割る

## しくみ

### ユークリッドの互除法

**gcd(a, b) = gcd(b, a % b)** を、b が 0 になるまで繰り返します。最後に残った a が答えです。

```
gcd(84, 36)
= gcd(36, 84 % 36 = 12)
= gcd(12, 36 % 12 = 0)
= 12
```

a と b の公約数は、a − b (や a % b) も割り切るので、小さい数に置き換えても答えが変わらない、という仕組みです。
数は 2 回で半分以下になるので、10^18 でも 100 回もかかりません。

### LCM

**lcm(a, b) = a × b / gcd(a, b)** ですが、そのまま `a * b` を計算するとオーバーフローしやすいので、
**先に割ってから掛ける**: `a / gcd(a, b) * b` と書きます。

それでも答えそのものが大きいと Long を超えます。たくさんの数の LCM は一気に大きくなるので、
「上限 (例: 10^18) を超えたら打ち切る」のが安全です。`x * b > limit` は掛け算せずに
`x > limit / b` で判定できます (x, b ≥ 1 のとき同じ意味です)。

## 実装 (Kotlin)

```kotlin
// 最大公約数 (ユークリッドの互除法)。O(log min(a, b))。負の数は絶対値で考える
fun gcd(a: Long, b: Long): Long {
    var x = Math.abs(a)
    var y = Math.abs(b)
    while (y != 0L) {
        val t = x % y
        x = y
        y = t
    }
    return x
}

// 最小公倍数 (a, b >= 1)。先に割ってから掛けるのがポイント
fun lcm(a: Long, b: Long): Long = a / gcd(a, b) * b

// 最小公倍数が limit を超えるなら null を返す (a, b >= 1)。オーバーフローしない
fun lcmOrNull(a: Long, b: Long, limit: Long): Long? {
    val x = a / gcd(a, b)
    if (x > limit / b) return null   // x * b > limit と同じ判定を、掛け算せずに行う
    return x * b
}
```

## 使い方

N 個の整数を読み、全体の GCD と、全体の LCM (10^18 を超えるなら -1) を出力する例です。
1 行目の GCD は [ABC 118 C - Monsters Battle Royale](https://atcoder.jp/contests/abc118/tasks/abc118_c) の答えになります。

```kotlin
fun main() {
    val br = System.`in`.bufferedReader()
    var st = java.util.StringTokenizer("")
    fun next(): String {
        while (!st.hasMoreTokens()) st = java.util.StringTokenizer(br.readLine())
        return st.nextToken()
    }

    val n = next().toInt()
    val a = LongArray(n) { next().toLong() }

    // 全体の GCD: gcd(gcd(gcd(a0, a1), a2), ...) と順にまとめる。gcd(0, x) = x なので 0 から始めてよい
    var g = 0L
    for (x in a) g = gcd(g, x)
    println(g)

    // 全体の LCM: 1 から始めて順にまとめる。10^18 を超えたら -1 を出力
    var l = 1L
    var over = false
    for (x in a) {
        val next = lcmOrNull(l, x, 1_000_000_000_000_000_000L)
        if (next == null) {
            over = true
            break
        }
        l = next
    }
    println(if (over) -1 else l)
}
```

入力例

```
4
2 10 8 40
```

出力例

```
2
40
```

## 例題

- [ABC 118 C - Monsters Battle Royale](https://atcoder.jp/contests/abc118/tasks/abc118_c) — 答えは全体の GCD
- [ABC 109 C - Skip](https://atcoder.jp/contests/abc109/tasks/abc109_c) — 各都市とスタート地点の **差の GCD**
- [ABC 148 C - Snack](https://atcoder.jp/contests/abc148/tasks/abc148_c) — 2 数の LCM。そのまま `lcm(a, b)`
- [典型90 022 - Cubic Cake](https://atcoder.jp/contests/typical90/tasks/typical90_v) — 3 辺の GCD で立方体に切る

## 注意点・よくあるミス

- LCM は **`a / gcd(a, b) * b`** の順。`a * b / gcd(a, b)` は途中でオーバーフローします。
- たくさんの数の LCM は **すぐに 10^18 を超えます**。上限があるなら `lcmOrNull` で打ち切る。
  答えを mod で求める問題なら、素因数分解して「各素数の指数の最大値」から計算します。
- GCD のまとめは `0` から始めて OK (`gcd(0, x) = x`)。LCM のまとめは `1` から始めます。
- `lcm` に 0 を渡すと 0 で割ってしまいます (a, b ≥ 1 で使う)。
- Java/Kotlin には Long 用の gcd 関数が標準にありません (`BigInteger.gcd` はあるが遅い)。自分で書きましょう。
- 再帰で `fun gcd(a, b) = if (b == 0L) a else gcd(b, a % b)` と書いても OK です (深さは高々 100 程度)。
