---
title: AtCoder Library (ACL) とは
category: はじめに
acl: -
genres:
order: 10
summary: この教科書の読み方と、Kotlin で ACL のアルゴリズムを使う方法
keywords: ACL, AtCoder Library, ライブラリ, 使い方, はじめに
---

## ACL とは？

**AtCoder Library (ACL)** は、AtCoder 公式が配布しているアルゴリズム・データ構造のライブラリです。
「知っていれば解ける、でも毎回自分で書くのは大変」なものがまとめられています。

- 公式: https://github.com/atcoder/ac-library
- ドキュメント (日本語): https://atcoder.github.io/ac-library/production/document_ja/
- 練習用コンテスト: [AtCoder Library Practice Contest](https://atcoder.jp/contests/practice2) — ACL の各機能をそのまま使う問題が A〜L まで並んでいます。

## Kotlin で使うには？

ACL は **C++ 用** なので、Kotlin からは `import` できません。
この教科書の各ページにある「実装 (Kotlin)」を、提出するコードの下にコピーして使います。

```kotlin
fun main() {
    val uf = DSU(5)      // ← 下に貼った class をそのまま使える
    uf.merge(0, 1)
    println(uf.same(0, 1))
}

// ===== ここから下に教科書の実装を貼る =====
class DSU(n: Int) { /* ... */ }
```

自分用のテンプレートファイルに、よく使うもの (DSU, Fenwick Tree, セグメント木, modint) を最初から入れておくのもおすすめです。

## 収録しているもの

| 分類 | 内容 | だいたいの難易度 |
|---|---|---|
| データ構造 | Union-Find / Fenwick Tree / セグメント木 / 遅延セグメント木 | 茶〜緑 (Union-Find) / 緑〜水 |
| 数学 | modint / pow_mod・inv_mod / 中国剰余定理 / floor_sum / 畳み込み | 茶〜緑 (modint) / 水〜青 |
| グラフ | 最大流 / 最小費用流 / 強連結成分分解 / 2-SAT | 水〜青 |
| 文字列 | 接尾辞配列・LCP / Z アルゴリズム | 水〜青 |

まずは **Union-Find**、**modint (nCr)**、**Fenwick Tree**、**セグメント木** の4つを使えるようになると、ABC の D〜E 問題で役立つ場面がぐっと増えます。

## 各ページの読み方

1. **何ができる？** — できる操作と計算量
2. **いつ使う？** — 問題文のどこを見たら「これだ」と気づけるか
3. **しくみ** — なぜ速いのか (最初は読み飛ばしても OK)
4. **実装** — コピペ用の Kotlin コード
5. **使い方** — 実装を呼び出す短い例
6. **例題** — 実際に解いてみる問題
7. **注意点** — よくあるミス

ページ上部の「自分の解答」には、関連ジャンルが付いた自分の解答が出ます。

## 自分でページを追加するには

`src/review/textbook/` に `.md` ファイルを置くと、教科書にページが増えます。
このファイルの先頭のような `---` で囲まれた部分 (title, category, genres, order, summary) を書いて、本文は Markdown で書きます。
