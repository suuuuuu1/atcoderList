---
title: 再帰による全列挙・バックトラック
category: 全探索・探索
acl: -
genres: 再帰, DFS, 全探索
order: 1060
summary: 組み合わせ・重複組み合わせ・数列を再帰で全列挙し、枝刈りで速くする
level: 茶
keywords: 再帰, バックトラック, backtracking, DFS, 組み合わせ列挙, 重複組み合わせ, 枝刈り, 広義単調増加, nCk, 全列挙
---

## 何ができる？

「1 つ目を決める → 2 つ目を決める → …」を **再帰関数** で書いて、
ループの数が決まっていない (入力で変わる) 全探索を行います。

| 列挙するもの | 例 (n = 4 や m = 3 のとき) | 個数 |
| --- | --- | --- |
| 組み合わせ: 0..n-1 から k 個 (昇順) | k = 2: (0,1) (0,2) (0,3) (1,2) (1,3) (2,3) | C(n, k) |
| 重複組み合わせ: 0..m-1 から重複ありで k 個 (広義単調増加) | k = 2: (0,0) (0,1) (0,2) (1,1) (1,2) (2,2) | C(m+k-1, k) |
| 長さ k の数列 (各項 0..m-1 自由) | m^k 通り | m^k |
| 順列 | [順列全探索](#textbook/permutation) を参照 | n! |

さらに途中で「この先は絶対ダメ」と分かったら探索を打ち切る (**枝刈り**) ことで速くできます。

## いつ使う？

- 「長さ N の数列 A で、1 ≦ A_1 ≦ A_2 ≦ … ≦ A_N ≦ M を満たすもの全部」のように、**for を N 重にしたいが N が入力で決まる**
- 組み合わせの個数 C(n, k) や m^k が 10^6〜10^7 程度に収まる
- 「〜以下の、ある条件を満たす数を全部作る」(桁を 1 つずつ足していく)
- 条件を満たさなくなった時点で打ち切れる (枝刈りが効く) 探索
- 正しい括弧列、迷路の経路など、「1 手ずつ決めていく」ものを全部試したい

## しくみ

`dfs(pos)` は「pos 番目の値を決める関数」です。
値を 1 つ置いて `dfs(pos + 1)` を呼び、戻ってきたら次の値を試します (= バックトラック)。

```
combinations(4, 2) の探索の様子 (start より大きい値だけ置く)

dfs(0)
 ├ cur[0]=0 → dfs(1) ├ cur[1]=1 → (0,1)
 │                   ├ cur[1]=2 → (0,2)
 │                   └ cur[1]=3 → (0,3)
 ├ cur[0]=1 → dfs(1) ├ cur[1]=2 → (1,2)
 │                   └ cur[1]=3 → (1,3)
 └ cur[0]=2 → dfs(1) └ cur[1]=3 → (2,3)
   (cur[0]=3 は後ろに置ける値が無いので試さない = 枝刈り)
```

- 次の値を「前の値 + 1 から」にすると **組み合わせ** (同じ値なし・昇順)
- 次の値を「前の値から」にすると **重複組み合わせ** (広義単調増加)
- 次の値を「0 から」にすると **全部の数列** (m^k 通り)

### 枝刈りの例

[ABC 114 C - 755](https://atcoder.jp/contests/abc114/tasks/abc114_c) (N 以下の「七五三数」= 3, 5, 7 だけからなり、3 つとも 1 回以上使う数の個数):
数を 1 桁ずつ伸ばし、**N を超えたらそれ以上伸ばさない** のが枝刈りです。

```kotlin
fun main() {
    val n = readln().toLong()
    var count = 0
    fun dfs(x: Long, used: Int) {           // used: 3,5,7 を使ったかを bit で記録
        if (x > n) return                   // 枝刈り: N を超えたら先は見ない
        if (used == 0b111) count++
        dfs(x * 10 + 3, used or 1)
        dfs(x * 10 + 5, used or 2)
        dfs(x * 10 + 7, used or 4)
    }
    dfs(0, 0)
    println(count)
}
```

## 実装 (Kotlin)

```kotlin
// 0..n-1 から k 個選ぶ組み合わせ (昇順) をすべて列挙し、action に渡す。C(n, k) 通り
// ※ action に渡す配列は使い回すので、保存したいときは copyOf() する
fun combinations(n: Int, k: Int, action: (IntArray) -> Unit) {
    val cur = IntArray(k)
    fun dfs(pos: Int, start: Int) {
        if (pos == k) {
            action(cur)
            return
        }
        // 残り k - pos 個を置ける範囲だけ試す (枝刈り)
        for (v in start..n - (k - pos)) {
            cur[pos] = v
            dfs(pos + 1, v + 1)
        }
    }
    dfs(0, 0)
}

// 0..m-1 から重複を許して k 個選ぶ (cur[0] <= cur[1] <= ... <= cur[k-1]) をすべて列挙する。
// C(m + k - 1, k) 通り
fun combinationsWithRepetition(m: Int, k: Int, action: (IntArray) -> Unit) {
    val cur = IntArray(k)
    fun dfs(pos: Int, start: Int) {
        if (pos == k) {
            action(cur)
            return
        }
        for (v in start until m) {
            cur[pos] = v
            dfs(pos + 1, v)          // 次も同じ値から (重複を許す)
        }
    }
    dfs(0, 0)
}

// 各項が 0..m-1 の長さ k の数列をすべて列挙する。m^k 通り
fun allSequences(m: Int, k: Int, action: (IntArray) -> Unit) {
    val cur = IntArray(k)
    fun dfs(pos: Int) {
        if (pos == k) {
            action(cur)
            return
        }
        for (v in 0 until m) {
            cur[pos] = v
            dfs(pos + 1)
        }
    }
    dfs(0)
}
```

## 使い方

[ABC 165 C - Many Requirements](https://atcoder.jp/contests/abc165/tasks/abc165_c) を解くコードです。
1 ≦ A_1 ≦ … ≦ A_N ≦ M の数列 A をすべて試し、条件 (A[b_i] - A[a_i] == c_i なら d_i 点) の得点の最大値を求めます。
この数列は「1..M から重複ありで N 個選ぶ」重複組み合わせなので、`combinationsWithRepetition` がそのまま使えます
(値が 0..M-1 になりますが、差しか見ないので問題ありません)。

```kotlin
fun main() {
    val (n, m, q) = readln().split(" ").map { it.toInt() }
    val a = IntArray(q)
    val b = IntArray(q)
    val c = IntArray(q)
    val d = IntArray(q)
    for (i in 0 until q) {
        val line = readln().split(" ").map { it.toInt() }
        a[i] = line[0] - 1      // 0-indexed に
        b[i] = line[1] - 1
        c[i] = line[2]
        d[i] = line[3]
    }
    var best = 0L
    combinationsWithRepetition(m, n) { seq ->
        var score = 0L
        for (i in 0 until q) {
            if (seq[b[i]] - seq[a[i]] == c[i]) score += d[i]
        }
        best = maxOf(best, score)
    }
    println(best)
}
```

入力例

```
3 4 3
1 3 3 100
1 2 2 10
2 3 2 10
```

出力例

```
110
```

## 例題

- [ABC 165 C - Many Requirements](https://atcoder.jp/contests/abc165/tasks/abc165_c) — 上の使い方の問題。重複組み合わせの全列挙
- [ABC 114 C - 755](https://atcoder.jp/contests/abc114/tasks/abc114_c) — 上の「枝刈りの例」。N を超えたら打ち切る
- [典型90 002 - Encyclopedia of Parentheses (★3)](https://atcoder.jp/contests/typical90/tasks/typical90_b) — 長さ N の正しい括弧列を辞書順に全部出力。「`)` が多すぎたら打ち切る」枝刈り

## 注意点・よくあるミス

- **列挙する個数を先に計算する**。C(n, k) や m^k が 10^7 を大きく超えるなら間に合いません。
  例: ABC 165 C は N = M = 10 で C(19, 10) = 92378 通りなので余裕。
- `action` に渡される配列は **使い回し** です。リストに保存するときは `cur.copyOf()` してから。
  そのまま `add(cur)` すると、全部最後の状態になってしまいます。
- 再帰の終了条件 (`pos == k`) を最初に書く。書き忘れると無限再帰で StackOverflow。
- 状態を配列などで共有して変更するなら、再帰から戻ったときに **元に戻す** (バックトラック) のを忘れずに。
  上の実装は `cur[pos]` を毎回上書きするので戻す必要はありません。
- 再帰が深い (数千段以上) ときは StackOverflow に注意 ([Kotlin 競プロの基本](#textbook/kotlin-basics) のスタック拡張を参照)。
  全列挙の深さは k なので、普通は問題になりません。
- ローカル関数 (`fun` の中の `fun dfs`) は外側の変数を読み書きできて便利です。
