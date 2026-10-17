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
 *
 * 一覧の組み立ては index.html 側で行う。このサーバーはファイルを渡すのと genres.tsv の保存だけ。
 * スマホ用の GitHub Pages 版は、同じ形式の bundle.json を GitHub Actions が作る (bundle.py)。
 */

private const val PORT = 8765
private val CONTEST_DIRS = listOf("abc", "arc", "atc", "betu")

private val root: File = listOf(File("."), File(".."))
    .map { it.canonicalFile }
    .first { File(it, "src/review").isDirectory }
private val srcDir = File(root, "src")
private val tsvFile = File(root, "genres.tsv")
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
            path.matches(Regex("""/[\w-]+\.js""")) && File(srcDir, "review$path").isFile ->
                send(ex, 200, "text/javascript", File(srcDir, "review$path").readText())
            path == "/api/bundle" -> send(ex, 200, "application/json", bundleJson())
            path == "/api/save" && ex.requestMethod == "POST" -> {
                val form = parseForm(ex.requestBody.readBytes().toString(Charsets.UTF_8))
                val p = form["path"].orEmpty()
                if (resolve(p) == null) return send(ex, 400, "text/plain", "bad path")
                val entries = loadTsv()
                entries[p] = listOf(
                    p,
                    form["genres"].orEmpty().split(',').map { it.trim() }.filter { it.isNotEmpty() }.joinToString(","),
                    if (form["review"] == "1") "1" else "0",
                    escape(form["memo"].orEmpty()),
                ).joinToString("\t")
                tsvFile.writeText("# path\tgenres\treview\tmemo\n" + entries.values.joinToString("\n") + "\n")
                send(ex, 200, "application/json", """{"ok":true}""")
            }
            else -> send(ex, 404, "text/plain", "not found")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        send(ex, 500, "text/plain", e.toString())
    }
}

/** {"editable":true,"tsv":"...","files":{"abc/abc001/A.kt":"...", ...},"textbook":{"dsu.md":"...", ...}} */
private fun bundleJson(): String {
    val files = CONTEST_DIRS.flatMap { type ->
        File(srcDir, type).walkTopDown().filter { it.isFile && it.extension == "kt" }
            .map { js(it.relativeTo(srcDir).invariantSeparatorsPath) + ":" + js(it.readText()) }.toList()
    }
    val textbook = File(srcDir, "review/textbook").listFiles { f -> f.extension == "md" }.orEmpty()
        .sortedBy { it.name }.map { js(it.name) + ":" + js(it.readText()) }
    val tsv = if (tsvFile.exists()) tsvFile.readText() else ""
    return """{"editable":true,"tsv":${js(tsv)},"files":{${files.joinToString(",")}},""" +
        """"textbook":{${textbook.joinToString(",")}}}"""
}

private fun resolve(rel: String): File? {
    if (rel.isEmpty() || rel.contains("..") || CONTEST_DIRS.none { rel.startsWith("$it/") }) return null
    return File(srcDir, rel).takeIf { it.isFile && it.extension == "kt" }
}

// genres.tsv : path \t genres(カンマ区切り) \t 要復習(1/0) \t メモ  → path ごとの行 (並び順は path 順)
private fun loadTsv() = sortedMapOf<String, String>().apply {
    if (tsvFile.exists()) tsvFile.readLines().filter { it.isNotBlank() && !it.startsWith("#") }
        .forEach { put(it.substringBefore('\t'), it) }
}

private fun escape(s: String) = s.replace("\\", "\\\\").replace("\t", "\\t").replace("\r", "").replace("\n", "\\n")

// ---- HTTP / JSON helpers ----

private fun send(ex: HttpExchange, code: Int, type: String, body: String) {
    val bytes = body.toByteArray(Charsets.UTF_8)
    ex.responseHeaders.add("Content-Type", "$type; charset=utf-8")
    ex.sendResponseHeaders(code, bytes.size.toLong())
    ex.responseBody.use { it.write(bytes) }
}

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
            c == '\r' -> append("\\r")
            c == '\t' -> append("\\t")
            c < ' ' -> append("\\u%04x".format(c.code))
            else -> append(c)
        }
    }
    append('"')
}
