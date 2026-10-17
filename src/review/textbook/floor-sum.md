---
title: floor_sum
category: 数学
acl: floor_sum
genres: 数学, 整数論
order: 240
summary: Σ floor((a·i + b) / m) を O(log m) で計算する
level: 青
keywords: 床関数, floor, 格子点, 切り捨ての和
---

## 何ができる？

| 関数 | 内容 | 計算量 |
| --- | --- | --- |
| `floorSum(n, m, a, b)` | Σ_{i=0}^{n-1} floor((a·i + b) / m) | O(log m) |

条件 (ACL と同じ): 0 ≤ n < 2^32, 1 ≤ m < 2^32。**a, b は負でもよい** (floor は「マイナス方向への切り捨て」)。答えが Long に収まる範囲を想定しています (ACL も long long で返します)。

## いつ使う？

- **直線 y = (a·x + b) / m の下にある格子点の数** を数えたい
- 「i = 0..n-1 について (a·i + b) / m の切り捨ての和」がそのまま出てくる
- 「(a·i + b) mod m の和」を求めたい → `(a·i+b) mod m = a·i + b - m·floor((a·i+b)/m)` で floor_sum に帰着
- 「x mod m がある範囲に入る x の個数」や、ビットごとに数えて floor の和になる問題
- n が 10^9 のように大きく、1 個ずつ足すと間に合わない

## しくみ

### 図で見ると「直線の下の格子点の数」

floor((a·i + b) / m) は、x = i の縦線上で 直線 y = (a·x + b)/m 以下 (y ≥ 1) にある格子点の数です。

```
 y
 3 |                 ・
 2 |         ・  ・  ・      ← 各列の点の数 = floor((a*i+b)/m)
 1 |  ・  ・  ・  ・  ・
   +---------------------- x
      0   1   2   3   4
```

### 2 つの工夫を交互に繰り返す

1. **a ≥ m や b ≥ m なら、はみ出し分をまとめて計算**
   `a = q·m + a'` なら floor((a·i + b)/m) = q·i + floor((a'·i + b)/m)。
   q·i の和は `q × n(n-1)/2` で一発。b も同様に `(b/m) × n`。
   これで a, b < m になる。
2. **縦と横を入れ替える**
   a, b < m のとき、直線の下の格子点を「横向き」に数え直すと、
   「(m, a) が入れ替わった」同じ形の和になります。

1 → 2 → 1 → 2 … で (m, a) が (a, m mod a) のように小さくなっていくので、
**ユークリッドの互除法と同じく O(log m) 回** で終わります。

### 負の a, b

a < 0 のときは a を [0, m) に直した a' = a + k·m を使い、増やしすぎた分 `k × n(n-1)/2` を先に引きます。b も同様です (ACL と同じ方法)。

## 実装 (Kotlin)

途中の値 (a·n + b など) が 2^63 を超えることがあるので、ACL と同じく符号なし整数 (`ULong`) で計算しています。

```kotlin
// a, b, m >= 0 の場合の本体 (途中の値が 2^63 を超えうるので ULong で計算する)
fun floorSumUnsigned(n0: Long, m0: Long, a0: Long, b0: Long): ULong {
    var n = n0.toULong(); var m = m0.toULong(); var a = a0.toULong(); var b = b0.toULong()
    var ans = 0uL
    while (true) {
        if (a >= m) {  // a = (a/m)*m + a%m に分けると、(a/m)*i の分はまとめて足せる
            ans += n * (n - 1uL) / 2uL * (a / m)
            a %= m
        }
        if (b >= m) {
            ans += n * (b / m)
            b %= m
        }
        val yMax = a * n + b
        if (yMax < m) break
        // 縦横を入れ替えた問題に帰着 (ユークリッドの互除法と同じ形で小さくなる)
        n = yMax / m
        b = yMax % m
        val tmp = m; m = a; a = tmp
    }
    return ans
}

// sum_{i=0}^{n-1} floor((a*i + b) / m)  (ACL の floor_sum, O(log m))
// 0 <= n < 2^32, 1 <= m < 2^32。a, b は負でもよい
fun floorSum(n: Long, m: Long, a: Long, b: Long): Long {
    require(n in 0 until (1L shl 32) && m in 1 until (1L shl 32))
    var ans = 0uL
    var a2 = a; var b2 = b
    if (a2 < 0) {  // a を [0, m) に直し、ずらした分を先に引いておく
        val r = Math.floorMod(a2, m)
        ans -= n.toULong() * (n - 1).toULong() / 2uL * ((r - a2) / m).toULong()
        a2 = r
    }
    if (b2 < 0) {
        val r = Math.floorMod(b2, m)
        ans -= n.toULong() * ((r - b2) / m).toULong()
        b2 = r
    }
    return (ans + floorSumUnsigned(n, m, a2, b2)).toLong()
}
```

## 使い方

AtCoder Library Practice Contest C - Floor Sum の解答例です (T ≤ 10^5 なので入力はまとめて読みます)。

```kotlin
import java.util.StringTokenizer

// AtCoder Library Practice Contest C - Floor Sum
fun main() {
    val st = StringTokenizer(System.`in`.bufferedReader().readText())
    val t = st.nextToken().toInt()
    val sb = StringBuilder()
    repeat(t) {
        val n = st.nextToken().toLong()
        val m = st.nextToken().toLong()
        val a = st.nextToken().toLong()
        val b = st.nextToken().toLong()
        sb.appendLine(floorSum(n, m, a, b))
    }
    print(sb)
}
```

入力例:

```
5
4 10 6 3
6 5 4 3
1 1 0 0
31415 92653 58979 32384
1000000000 1000000000 999999999 999999999
```

出力:

```
3
13
0
314095480
499999999500000000
```

## 例題

- [AtCoder Library Practice Contest C - Floor Sum](https://atcoder.jp/contests/practice2/tasks/practice2_c) — そのまま。
- [ABC 283 Ex - Popcount Sum](https://atcoder.jp/contests/abc283/tasks/abc283_h) — 各ビットが立っている個数を floor の和で表して floor_sum で求める。
- [ABC 372 G - Ax + By < C](https://atcoder.jp/contests/abc372/tasks/abc372_g) — 直線の下の格子点の数え上げ。区間ごとに floor_sum を使う。

## 注意点・よくあるミス

- **Kotlin の `/` は 0 方向への切り捨て**: `-7 / 2 = -3` (floor なら -4)。負の値で愚直に検算するときは `Math.floorDiv` を使う。
- **和は i = 0 から n-1 まで**: i = 1..n にしたいなら b に a を足す (`floorSum(n, m, a, a + b)`)。
- **m は 2^32 未満、n も 2^32 未満**: それより大きいと途中で溢れる。
- **(a·i + b) mod m の和** は `a·n(n-1)/2 + b·n - m·floorSum(n, m, a, b)`。この式自体が Long を超えないか注意。
