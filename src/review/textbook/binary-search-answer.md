---
title: 答えで二分探索
category: 全探索・探索
acl: -
genres: 二分探索
order: 1050
summary: 「答えは X 以下にできる？」の判定問題に直して答えを二分探索する
level: 緑
keywords: 答えで二分探索, 二分探索, めぐる式, 判定問題, 最大値の最小化, 最小値の最大化, binary search on answer, ok ng, 単調性
---

## 何ができる？

「答えの最小値 (最大値) を求めよ」という問題を、
**「答えを X にできるか？」(Yes/No の判定問題)** に言い換えて、X を二分探索で求めます。

| 操作 | 内容 | 計算量 |
| --- | --- | --- |
| `binarySearch(ok, ng) { x -> 判定 }` | 判定が true になる範囲と false になる範囲の境界 (ok 側) を返す | O(log(\|ok - ng\|) × 判定 1 回) |

範囲が 10^18 でも判定は約 60 回しか呼ばれないので、判定が O(N) なら全体 O(N log(範囲)) です。

## いつ使う？

- 「**最大値の最小値**」「**最小値の最大値**」を求める問題 (ほぼこれ)
- 答えを決め打ちすると、判定が貪欲などで簡単にできる
- 「X 以下にできるなら、X + 1 以下にもできる」のような **単調性** がある
- 答えの範囲がとても大きい (10^9, 10^18) のに、制約上全部は試せない
- 「K 番目に小さい値」→「X 以下の値は K 個以上あるか？」で二分探索

## しくみ

判定 `isOk(x)` が、ある境界を境に **true と false がきれいに分かれる** ことがポイントです。

```
x      :  0  1  2  3  4  5  6  7  8  9  10
isOk(x):  T  T  T  T  T  T  F  F  F  F  F
                         ↑  ↑
                        ok  ng     → 答えは 5 (true になる最大の x)
```

「めぐる式」二分探索では、次の 2 つの変数を持ちます。

- `ok`: 判定が **必ず true** になる値 (はじめは答えの範囲の外側でもよい)
- `ng`: 判定が **必ず false** になる値

`ok` と `ng` の差が 1 になるまで、真ん中 `mid` を調べて `ok` か `ng` を `mid` に置き換えます。
最後に `ok` が答えです。

- **true になる最大値** を求めたい → `ok` を小さい側、`ng` を大きい側に置く
- **true になる最小値** を求めたい → `ok` を大きい側、`ng` を小さい側に置く

どちら向きでも同じ関数で書けるのがめぐる式の良いところです。

## 実装 (Kotlin)

```kotlin
// めぐる式二分探索。
// isOk(ok) == true、isOk(ng) == false で、ok と ng の間で判定が 1 回だけ切り替わるとき、
// 判定が true になる値のうち最も ng 寄りのものを返す。
// (ok < ng なら true になる最大値、ok > ng なら true になる最小値)
// ※ isOk(ok) と isOk(ng) 自体は呼ばれないので、範囲外の値を置いてよい
fun binarySearch(ok: Long, ng: Long, isOk: (Long) -> Boolean): Long {
    var lo = ok
    var hi = ng
    while (kotlin.math.abs(hi - lo) > 1) {
        val mid = lo + (hi - lo) / 2      // (lo + hi) / 2 だと大きい値でオーバーフローしうる
        if (isOk(mid)) lo = mid else hi = mid
    }
    return lo
}
```

## 使い方

[ABC 146 C - Buy an Integer](https://atcoder.jp/contests/abc146/tasks/abc146_c) を解くコードです。
整数 N の値段は A × N + B × (N の桁数) 円。X 円で買える最大の整数 (1 〜 10^9) を出力し、
1 つも買えなければ 0 を出力します。

「N が買えるなら、N より小さい数も買える」ので、true になる最大値を二分探索します。

```kotlin
fun main() {
    val (a, b, x) = readln().split(" ").map { it.toLong() }
    // ok = 0 (買えないときの答え), ng = 10^9 + 1 (範囲外なので必ず買えない扱い)
    val ans = binarySearch(0L, 1_000_000_001L) { n ->
        val digits = n.toString().length
        a * n + b * digits <= x           // N を買えるか？ (a * n は最大 10^18 なので Long)
    }
    println(ans)
}
```

入力例

```
10 7 100
```

出力例

```
9
```

### 「最小値」を求める向きの例

「x^2 >= 2000 となる最小の正整数 x」なら、ok を大きい側に置きます。

```kotlin
fun main() {
    val ans = binarySearch(2000L, 0L) { x -> x * x >= 2000 }
    println(ans)   // 45 (44^2 = 1936, 45^2 = 2025)
}
```

### 実数で二分探索するとき

答えが実数なら、差が 1 になるまでではなく **決まった回数 (100 回など)** 繰り返します。

```kotlin
fun main() {
    // x^2 = 2 となる x (√2) を求める
    var ok = 2.0     // ok*ok >= 2
    var ng = 0.0     // ng*ng < 2
    repeat(100) {
        val mid = (ok + ng) / 2
        if (mid * mid >= 2) ok = mid else ng = mid
    }
    println("%.10f".format(ok))   // 1.4142135624
}
```

## 例題

- [ABC 146 C - Buy an Integer](https://atcoder.jp/contests/abc146/tasks/abc146_c) — 上の使い方の問題
- [典型90 001 - Yokan Party (★4)](https://atcoder.jp/contests/typical90/tasks/typical90_a) — 「一番短いピースを X 以上にできるか？」を貪欲で判定。最小値の最大化の代表例
- [ABC 023 D - 射撃王](https://atcoder.jp/contests/abc023/tasks/abc023_d) — 「ペナルティの最大値を X 以下にできるか？」を判定。最大値の最小化の代表例

## 注意点・よくあるミス

- **単調性があるか確認する**。判定が T, F, T と行ったり来たりするなら二分探索は使えません。
- `ok` と `ng` の初期値: `ok` は「必ず true」、`ng` は「必ず false」と言える値に。
  迷ったら答えの範囲より 1 つ外側 (例: 答えが 1〜10^9 なら ng = 10^9 + 1、ok = 0 など) に置く。
  答えが存在しない場合があるなら、最後に返ってきた値がその「外側の値」かどうかで判定できます。
- 範囲が大きいときは **Long** を使う。判定の中の掛け算 (a * n など) もオーバーフローしやすい。
  10^18 × 10 のように Long も超えそうなら、割り算で比べる (`a > x / n`) などの工夫を。
- 判定関数の中で答えを直接求めようとしない。判定は「X でできるか？」の Yes/No だけを返す。
- 実数の二分探索で `while (ng - ok > 1e-9)` のように誤差で止めると、値が大きいとき無限ループすることがある。回数で止めるのが安全。
