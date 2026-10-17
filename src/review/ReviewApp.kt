package review

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.awt.Desktop
import java.io.File
import java.net.InetSocketAddress
import java.net.URI
import java.net.URLDecoder

/*
 * 解いたコードをジャンル別に振り返るローカルアプリ。
 * IntelliJ でこのファイルの main を実行 → ブラウザで http://localhost:8765 が開く。
 *
 * ジャンル情報の保存先
 *   - genres.tsv (プロジェクト直下) : アプリ画面から編集した内容
 *   - 各ソースの "// genre: 貪欲, 二分探索" 行 : コードを書きながら付けたタグ
 *   画面には両方を合わせたものが表示される。
 */

private const val PORT = 8765
private val CONTEST_DIRS = listOf("abc", "arc", "atc", "betu")

val GENRES = listOf(
    "全探索", "bit全探索", "順列全探索", "貪欲", "二分探索", "累積和", "いもす法", "尺取り法", "ソート",
    "DP", "bitDP", "桁DP", "区間DP",
    "BFS", "DFS", "ダイクストラ", "ワーシャルフロイド", "Union-Find", "最小全域木", "トポロジカルソート", "木", "グラフ",
    "数学", "整数論", "組み合わせ", "幾何", "行列累乗",
    "文字列", "シミュレーション", "実装", "二次元グリッド", "再帰",
    "スタック・キュー", "優先度付きキュー", "セグメント木・BIT", "座標圧縮", "半分全列挙",
)

private val root: File = listOf(File("."), File(".."))
    .map { it.canonicalFile }
    .first { File(it, "src/review").isDirectory }
private val srcDir = File(root, "src")
private val tsvFile = File(root, "genres.tsv")
private val genreLine = Regex("""^\s*//\s*genre\s*:(.*)$""", RegexOption.IGNORE_CASE)

data class Entry(val genres: List<String>, val review: Boolean, val memo: String)

fun main() {
    val server = HttpServer.create(InetSocketAddress("127.0.0.1", PORT), 0)
    server.createContext("/") { ex -> handle(ex) }
    server.start()
    val url = "http://localhost:$PORT"
    println("振り返りアプリ起動: $url  (停止は IntelliJ の ■ ボタン)")
    if (System.getProperty("noBrowser") == null) runCatching { Desktop.getDesktop().browse(URI(url)) }
}

private fun handle(ex: HttpExchange) {
    try {
        val path = ex.requestURI.path
        when {
            path == "/" -> send(ex, 200, "text/html", File(srcDir, "review/index.html").readText())
            path == "/api/list" -> send(ex, 200, "application/json", listJson())
            path == "/api/code" -> {
                val file = resolve(query(ex)["path"].orEmpty())
                if (file == null) send(ex, 404, "text/plain", "not found")
                else send(ex, 200, "text/plain", file.readText())
            }
            path == "/api/save" && ex.requestMethod == "POST" -> {
                val form = parseForm(ex.requestBody.readBytes().toString(Charsets.UTF_8))
                val p = form["path"].orEmpty()
                if (resolve(p) == null) return send(ex, 400, "text/plain", "bad path")
                val entries = loadTsv()
                entries[p] = Entry(
                    genres = form["genres"].orEmpty().split(',').map { it.trim() }.filter { it.isNotEmpty() },
                    review = form["review"] == "1",
                    memo = form["memo"].orEmpty(),
                )
                saveTsv(entries)
                send(ex, 200, "application/json", """{"ok":true}""")
            }
            else -> send(ex, 404, "text/plain", "not found")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        send(ex, 500, "text/plain", e.toString())
    }
}

private fun listJson(): String {
    val entries = loadTsv()
    val items = CONTEST_DIRS.flatMap { type ->
        File(srcDir, type).walkTopDown().filter { it.isFile && it.extension == "kt" }.mapNotNull { file ->
            val rel = file.relativeTo(srcDir).invariantSeparatorsPath
            val text = file.readText()
            val fileGenres = text.lineSequence().mapNotNull { genreLine.find(it)?.groupValues?.get(1) }
                .flatMap { it.split(',', '、') }.map { it.trim() }.filter { it.isNotEmpty() }.toList()
            val entry = entries[rel]
            if (!hasCode(text) && fileGenres.isEmpty() && entry == null) return@mapNotNull null
            val parts = rel.split('/')
            buildString {
                append('{')
                append("\"path\":").append(js(rel)).append(',')
                append("\"type\":").append(js(type)).append(',')
                append("\"contest\":").append(js(parts.getOrElse(1) { "" })).append(',')
                append("\"problem\":").append(js(file.nameWithoutExtension.uppercase())).append(',')
                append("\"genres\":").append(jsArr(entry?.genres.orEmpty())).append(',')
                append("\"fileGenres\":").append(jsArr(fileGenres)).append(',')
                append("\"review\":").append(entry?.review == true).append(',')
                append("\"memo\":").append(js(entry?.memo.orEmpty())).append(',')
                append("\"lines\":").append(text.lines().count { it.isNotBlank() }).append(',')
                append("\"mtime\":").append(file.lastModified())
                append('}')
            }
        }.toList()
    }
    return """{"genres":${jsArr(GENRES)},"items":[${items.joinToString(",")}]}"""
}

/** main の中身が空 (= まだ解いていないテンプレート) でないか */
private fun hasCode(text: String): Boolean {
    val start = Regex("""fun\s+main\s*\([^)]*\)\s*\{""").find(text) ?: return false
    var depth = 1
    var i = start.range.last + 1
    while (i < text.length && depth > 0) {
        when (text[i]) { '{' -> depth++; '}' -> depth-- }
        i++
    }
    return text.substring(start.range.last + 1, i - 1).lines()
        .any { it.isNotBlank() && !it.trim().startsWith("//") }
}

private fun resolve(rel: String): File? {
    if (rel.isEmpty() || rel.contains("..") || CONTEST_DIRS.none { rel.startsWith("$it/") }) return null
    return File(srcDir, rel).takeIf { it.isFile && it.extension == "kt" }
}

// ---- genres.tsv : path \t genres(カンマ区切り) \t 要復習(1/0) \t メモ ----

private fun loadTsv(): MutableMap<String, Entry> {
    val map = sortedMapOf<String, Entry>()
    if (!tsvFile.exists()) return map
    tsvFile.readLines().filter { it.isNotBlank() && !it.startsWith("#") }.forEach { line ->
        val c = line.split('\t')
        map[c[0]] = Entry(
            genres = c.getOrElse(1) { "" }.split(',').map { it.trim() }.filter { it.isNotEmpty() },
            review = c.getOrElse(2) { "0" } == "1",
            memo = unescape(c.getOrElse(3) { "" }),
        )
    }
    return map
}

private fun saveTsv(entries: Map<String, Entry>) {
    val body = entries.toSortedMap().entries.joinToString("\n") { (p, e) ->
        listOf(p, e.genres.joinToString(","), if (e.review) "1" else "0", escape(e.memo)).joinToString("\t")
    }
    tsvFile.writeText("# path\tgenres\treview\tmemo\n$body\n")
}

private fun escape(s: String) = s.replace("\\", "\\\\").replace("\t", "\\t").replace("\r", "").replace("\n", "\\n")
private fun unescape(s: String) = buildString {
    var i = 0
    while (i < s.length) {
        if (s[i] == '\\' && i + 1 < s.length) {
            append(when (s[i + 1]) { 'n' -> '\n'; 't' -> '\t'; else -> s[i + 1] })
            i += 2
        } else append(s[i++])
    }
}

// ---- HTTP / JSON helpers ----

private fun send(ex: HttpExchange, code: Int, type: String, body: String) {
    val bytes = body.toByteArray(Charsets.UTF_8)
    ex.responseHeaders.add("Content-Type", "$type; charset=utf-8")
    ex.sendResponseHeaders(code, bytes.size.toLong())
    ex.responseBody.use { it.write(bytes) }
}

private fun query(ex: HttpExchange) = parseForm(ex.requestURI.rawQuery.orEmpty())
private fun parseForm(s: String): Map<String, String> = s.split('&').filter { it.contains('=') }.associate {
    val (k, v) = it.split('=', limit = 2)
    URLDecoder.decode(k, Charsets.UTF_8) to URLDecoder.decode(v, Charsets.UTF_8)
}

private fun js(s: String) = buildString {
    append('"')
    s.forEach { c ->
        when {
            c == '"' -> append("\\\"")
            c == '\\' -> append("\\\\")
            c == '\n' -> append("\\n")
            c == '\t' -> append("\\t")
            c < ' ' -> append("\\u%04x".format(c.code))
            else -> append(c)
        }
    }
    append('"')
}
private fun jsArr(list: List<String>) = list.joinToString(",", "[", "]") { js(it) }
