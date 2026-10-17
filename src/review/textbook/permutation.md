---
title: 順列全探索
category: 全探索・探索
acl: -
genres: 順列全探索, 全探索
order: 1030
summary: N 個の並べ方 N! 通りを辞書順に全部試す (nextPermutation)
level: 茶
keywords: 順列, permutation, next_permutation, 並べ方, N!, 階乗, 巡回, 訪問順, 辞書順
---

## 何ができる？

N 個のものの **並べ方 (順列) N! 通りをすべて** 辞書順 (小さい順) に試します。
C++ の `next_permutation` にあたる関数は Kotlin の標準ライブラリに無いので、自分で書きます。

| 操作 | 内容 | 計算量 |
| --- | --- | --- |
| `nextPermutation(a)` | 配列 a を辞書順で「次の並び」に書き換える。次が無ければ `false` | 償却 O(1)、最悪 O(N) |
| 全列挙 | 昇順ソートした配列から `do { ... } while (nextPermutation(a))` | O(N! × 1 回の処理) |

N! の目安: 8! = 40320, 9! = 362880, 10! = 3628800, 11! ≒ 4×10^7。
**N ≦ 8〜10 くらいまで** が順列全探索の範囲です。

## いつ使う？

- **N ≦ 8〜10 と非常に小さい**
- 「訪れる順番」「並べる順番」「割り当て方」を全部試したい (例: 全ての町を回る最短経路)
- 「この順列は辞書順で何番目？」を求めたい
- 2 つのグラフが同じ形か (頂点の対応付け) を調べたい
- N が 15〜20 くらいなら順列全探索は無理 → bit DP を検討

## しくみ

「次の順列」は次の手順で作れます。例: `1 3 5 4 2` の次。

```
1. 右から見て、初めて a[i] < a[i+1] となる i を探す
     1 [3] 5 4 2      i = 1 (a[i] = 3)。i より右 (5 4 2) は降順になっている
2. 右から見て、初めて a[j] > a[i] となる j を探す
     1 [3] 5 [4] 2    j = 3 (a[j] = 4)
3. a[i] と a[j] を入れ替える
     1  4  5  3  2
4. i より右を反転する (降順 → 昇順)
     1  4  2  3  5    ← これが次の順列
```

手順 1 で i が見つからない (全体が降順) なら、それが最後の順列です。

- **同じ値が含まれていても OK**。例えば `1 1 2` からは `1 1 2`, `1 2 1`, `2 1 1` の 3 通り (重複なし) が出てきます。
- 必ず **昇順にソートした状態から始める** と、全通りを 1 回ずつ列挙できます。

## 実装 (Kotlin)

```kotlin
// a を辞書順で次の順列に書き換えて true を返す。
// a が最後の順列 (降順) なら何もせず false を返す。
fun nextPermutation(a: IntArray): Boolean {
    val n = a.size
    var i = n - 2
    while (i >= 0 && a[i] >= a[i + 1]) i--
    if (i < 0) return false
    var j = n - 1
    while (a[j] <= a[i]) j--
    a[i] = a[j].also { a[j] = a[i] }   // a[i] と a[j] を交換
    // i より右を反転 (降順 → 昇順)
    var l = i + 1
    var r = n - 1
    while (l < r) {
        a[l] = a[r].also { a[r] = a[l] }
        l++
        r--
    }
    return true
}
```

## 使い方

[ABC 150 C - Count Order](https://atcoder.jp/contests/abc150/tasks/abc150_c) を解くコードです。
(1, 2, ..., N) の順列 P と Q が、辞書順でそれぞれ何番目かを求めて差の絶対値を出力します。

```kotlin
fun main() {
    val n = readln().toInt()
    val p = readln().split(" ").map { it.toInt() }.toIntArray()
    val q = readln().split(" ").map { it.toInt() }.toIntArray()

    val perm = IntArray(n) { it + 1 }   // 1, 2, ..., n (一番小さい順列から始める)
    var idx = 0
    var a = 0
    var b = 0
    do {
        if (perm.contentEquals(p)) a = idx   // 配列の中身の比較は contentEquals
        if (perm.contentEquals(q)) b = idx
        idx++
    } while (nextPermutation(perm))
    println(kotlin.math.abs(a - b))
}
```

入力例

```
3
1 3 2
3 1 2
```

出力例

```
3
```

## 例題

- [ABC 150 C - Count Order](https://atcoder.jp/contests/abc150/tasks/abc150_c) — 上の使い方の問題
- [ABC 145 C - Average Length](https://atcoder.jp/contests/abc145/tasks/abc145_c) — N 個の町を回る順番 N! 通りの経路長の平均
- [ABC 232 C - Graph Isomorphism](https://atcoder.jp/contests/abc232/tasks/abc232_c) — 頂点の対応 (順列) を全部試して、2 つのグラフが同じ形か調べる

## 注意点・よくあるミス

- **最初にソートする**。途中の順列から始めると、それより辞書順で前の並びが列挙されません。
- ループは `do { } while (nextPermutation(a))`。`while (nextPermutation(a)) { }` にすると最初の並びを飛ばしてしまう。
- この実装は最後の順列で `false` を返すとき **配列を変更しません** (C++ の `next_permutation` は昇順に戻す点が違う)。
- 配列の比較は `==` ではなく `contentEquals`。`IntArray` 同士の `==` は「同じオブジェクトか」の比較です。
- 添字の順列 (0, 1, ..., N-1) を回して `order[k]` 番目の要素を使う形にすると、文字列や座標など何にでも使えます。
- N! は急激に大きくなる。N = 11 で約 4×10^7 通り、1 通りあたり O(N) の処理なら TLE しがち。
