---
title: 幾何の基本 (外積・内積・線分交差)
category: 数学
acl: -
genres: 幾何, 数学
order: 205
summary: 外積・内積で 3 点の向きや線分の交差を整数のまま判定する
level: 緑
keywords: 幾何, geometry, 外積, cross product, 内積, dot product, ccw, 線分交差, 多角形の面積, 点と線分の距離
---

## 何ができる？

座標を **整数 (Long) のまま** 扱い、誤差なしで次の判定・計算をします。

| 関数 | 内容 | 計算量 |
| --- | --- | --- |
| `dot(a, b)` | 内積 a・b | O(1) |
| `cross(a, b)` | 外積 a×b (z 成分) | O(1) |
| `ccw(a, b, c)` | a→b→c が左回りなら 1、右回りなら -1、一直線なら 0 | O(1) |
| `onSegment(a, b, p)` | 点 p が線分 ab 上 (端点含む) にあるか | O(1) |
| `segmentsIntersect(a, b, c, d)` | 線分 ab と cd が共有点を持つか (接する・重なるも含む) | O(1) |
| `dist2(a, b)` / `dist(a, b)` | 距離の 2 乗 (Long) / 距離 (Double) | O(1) |
| `distPointSegment(p, a, b)` | 点 p と線分 ab の距離 (Double) | O(1) |
| `area2(poly)` | 多角形の面積の **2 倍** (Long) | O(N) |

## いつ使う？

- 「3 点が一直線上にあるか」「左に曲がるか右に曲がるか」
- 「2 本の線分が交わるか」「線分で図形を切ると何個に分かれるか」
- 「多角形が凸か」→ 隣り合う 3 頂点の `ccw` がすべて同じ向きか
- 「多角形の面積」→ 外積の和 (座標が整数なら面積の 2 倍は整数)
- 「距離が R 以内か」→ `dist2(a, b) <= R * R` のように **2 乗のまま比較** する

## しくみ

ベクトル a = (a.x, a.y), b = (b.x, b.y) について

- **内積** a・b = a.x·b.x + a.y·b.y … 正なら同じ向き寄り (なす角 < 90°)、0 なら直角、負なら逆向き寄り
- **外積** a×b = a.x·b.y − a.y·b.x … 正なら b は a の **左** (反時計回り)、負なら **右**、0 なら平行

```
        c (左)
        ↑
  a ────────→ b       cross(b - a, c - a) > 0 → ccw = 1  (左に曲がる)
        ↓
        c (右)        cross(b - a, c - a) < 0 → ccw = -1 (右に曲がる)
```

外積の絶対値は、a と b が作る **平行四辺形の面積** でもあります (三角形ならその半分)。

### 線分の交差判定

線分 ab と cd が交わる ⇔ 「c と d が直線 ab の **反対側** にある」かつ「a と b が直線 cd の **反対側** にある」。

```
        c                    c
        |                    |
  a ----+---- b              |   a ------ b
        |                    |
        d                    d

      交わる            交わらない
                  (a と b が直線 cd の同じ側)
```

ただし 4 点のどれかが一直線上にある場合 (端点で接する、同じ直線上で重なる) は、
「その端点がもう一方の線分の上にあるか」を `onSegment` で別に調べます。

### なぜ整数で計算するのか

`Double` だと、ぴったり一直線上の点の外積が `1e-16` のようにわずかにずれ、判定が逆になることがあります。
入力が整数なら、外積・内積・距離の 2 乗はすべて整数で計算できるので **誤差ゼロ** です。
平方根や割り算が必要な最後の段階だけ Double にします。

## 実装 (Kotlin)

```kotlin
// 座標は整数 (Long) で持つ。|座標| <= 10^9 なら外積・内積は Long に収まる
data class Point(val x: Long, val y: Long) {
    operator fun plus(o: Point) = Point(x + o.x, y + o.y)
    operator fun minus(o: Point) = Point(x - o.x, y - o.y)
}

// 内積 a・b
fun dot(a: Point, b: Point): Long = a.x * b.x + a.y * b.y

// 外積 a×b (z 成分)。正なら b は a の左側 (反時計回り)、負なら右側、0 なら平行
fun cross(a: Point, b: Point): Long = a.x * b.y - a.y * b.x

// a → b → c の向き。1: 左に曲がる (反時計回り)、-1: 右に曲がる (時計回り)、0: 一直線上
fun ccw(a: Point, b: Point, c: Point): Int = java.lang.Long.signum(cross(b - a, c - a))

// 点 p が線分 ab 上 (端点を含む) にあるか
fun onSegment(a: Point, b: Point, p: Point): Boolean =
    cross(b - a, p - a) == 0L && dot(a - p, b - p) <= 0L

// 線分 ab と線分 cd が共有点を持つか (端点で接する・重なる場合も true)
fun segmentsIntersect(a: Point, b: Point, c: Point, d: Point): Boolean {
    val d1 = ccw(a, b, c)
    val d2 = ccw(a, b, d)
    val d3 = ccw(c, d, a)
    val d4 = ccw(c, d, b)
    // 一直線上に並ぶ特別な場合: 端点がもう一方の線分の上にあるか
    if (d1 == 0 && onSegment(a, b, c)) return true
    if (d2 == 0 && onSegment(a, b, d)) return true
    if (d3 == 0 && onSegment(c, d, a)) return true
    if (d4 == 0 && onSegment(c, d, b)) return true
    // ふつうの交差: c と d が直線 ab の反対側、かつ a と b が直線 cd の反対側
    return d1 * d2 < 0 && d3 * d4 < 0
}

// 距離の 2 乗 (整数のまま比較したいときはこちら)
fun dist2(a: Point, b: Point): Long = dot(a - b, a - b)

// 距離 (Double)
fun dist(a: Point, b: Point): Double = Math.sqrt(dist2(a, b).toDouble())

// 点 p と線分 ab の距離
fun distPointSegment(p: Point, a: Point, b: Point): Double {
    if (dot(b - a, p - a) <= 0L) return dist(p, a)   // a の外側
    if (dot(a - b, p - b) <= 0L) return dist(p, b)   // b の外側
    return Math.abs(cross(b - a, p - a)).toDouble() / dist(a, b)  // 垂線の長さ
}

// 多角形の面積の 2 倍 (頂点を順番に並べたもの。2 倍なので必ず整数)
fun area2(poly: List<Point>): Long {
    var s = 0L
    for (i in poly.indices) s += cross(poly[i], poly[(i + 1) % poly.size])
    return Math.abs(s)
}
```

## 使い方

[ABC 016 D - 一刀両断](https://atcoder.jp/contests/abc016/tasks/abc016_4) を解くコードです。
多角形を線分 AB で切ったときにいくつに分かれるかを答えます。
AB と交わる辺の数を数えると、2 本につき 1 個ずつ増えます。

```kotlin
fun main() {
    val br = System.`in`.bufferedReader()
    var st = java.util.StringTokenizer("")
    fun next(): String {
        while (!st.hasMoreTokens()) st = java.util.StringTokenizer(br.readLine())
        return st.nextToken()
    }

    // ABC 016 D: 線分 AB で多角形を切ると何個に分かれるか
    val a = Point(next().toLong(), next().toLong())
    val b = Point(next().toLong(), next().toLong())
    val n = next().toInt()
    val poly = List(n) { Point(next().toLong(), next().toLong()) }

    // 多角形の辺のうち、線分 AB と交わるものを数える
    var cnt = 0
    for (i in 0 until n) {
        if (segmentsIntersect(a, b, poly[i], poly[(i + 1) % n])) cnt++
    }
    // 2 本交わるごとに 1 個増える
    println(cnt / 2 + 1)
}
```

入力例

```
-2 0 2 0
4
1 1
-1 1
-1 -1
1 -1
```

出力例

```
2
```

## 例題

- [ABC 016 D - 一刀両断](https://atcoder.jp/contests/abc016/tasks/abc016_4) — 上の「使い方」そのもの。線分交差の数
- [ABC 266 C - Convex Quadrilateral](https://atcoder.jp/contests/abc266/tasks/abc266_c) — 四角形が凸か。隣り合う 3 頂点の `ccw` がすべて 1 なら凸

## 注意点・よくあるミス

- **Double で判定しない**。`cross(...) == 0.0` のような比較は誤差で失敗します。座標が整数なら Long で計算する。
- オーバーフローに注意。座標の絶対値が 10^9 までなら、差は 2×10^9、外積は最大 8×10^18 で **Long にギリギリ収まります**。
  それより大きい座標なら BigInteger などが必要です。
- 距離の比較は **2 乗のまま** (`dist2(a, b) <= r * r`)。`sqrt` すると誤差が入ります。
- `area2` は面積の **2 倍**。面積が「.5」になりうるので、出力するときは `area2 / 2` と `area2 % 2` で整数部と .5 を分けるか、Double にする。
- 「交わる」に端点での接触を含むかどうかは問題によって違います。含まないなら `d1 * d2 < 0 && d3 * d4 < 0` だけを使う。
- 多角形の頂点が時計回りか反時計回りかで、外積の和の符号が変わります (上の `area2` は絶対値を取っています)。
