package `_`

import java.io.File
import java.net.URI

fun main() {
    print("contestName or URL -> ")
    val rawInput = readLine()!!.trim()
    require(rawInput.isNotEmpty()) { "コンテスト名が空です" }
    val contestName = normalizeContestName(rawInput)

    val contestType = when {
        contestName.startsWith("abc", ignoreCase = true) -> "abc"
        contestName.startsWith("arc", ignoreCase = true) -> "arc"
        contestName.startsWith("atc", ignoreCase = true) -> "atc"
        contestName.startsWith("betu", ignoreCase = true) -> "betu"
        else -> "betu"
    }

    val isRegularContest = contestName.startsWith("abc", ignoreCase = true) ||
        contestName.startsWith("arc", ignoreCase = true) ||
        contestName.startsWith("atc", ignoreCase = true)
    val problems = if (isRegularContest) {
        DEFAULT_PROBLEMS.split(' ')
    } else {
        EXTENDED_PROBLEMS.split(' ')
    }
    val dir = File("src/$contestType/$contestName").also { it.mkdirs() }
    problems.forEach { problem ->
        val file = File(dir, "$problem.kt")
        if (file.exists()) return@forEach
        val template = buildTemplate(contestType, contestName)
        file.writeText(template)
        println("${file.name} を生成しました")
    }
    println("==== $contestName の準備が完了しました ====")
}

private fun normalizeContestName(input: String): String {
    val trimmed = input.trim()
    if (!trimmed.contains("://") && !trimmed.startsWith("atcoder.jp/")) {
        return trimmed.lowercase()
    }

    val withoutQuery = trimmed.substringBefore('?').substringBefore('#').trimEnd('/')
    val normalizedUrl = if (withoutQuery.contains("://")) withoutQuery else "https://$withoutQuery"

    val contest = runCatching {
        val path = URI(normalizedUrl).path ?: ""
        extractContestFromUrlPath(path)
    }.getOrNull()

    return contest?.takeIf { it.isNotEmpty() } ?: trimmed.lowercase()
}

private fun extractContestFromUrlPath(path: String): String? {
    val parts = path.split('/').filter { it.isNotBlank() }

    val contestsIndex = parts.indexOf("contests")
    if (contestsIndex >= 0 && contestsIndex + 1 < parts.size) {
        return parts[contestsIndex + 1].lowercase()
    }

    val tasksIndex = parts.indexOf("tasks")
    if (tasksIndex >= 0 && tasksIndex + 1 < parts.size) {
        val taskId = parts[tasksIndex + 1].lowercase()
        return taskId.substringBefore('_').takeIf { it.isNotEmpty() }
    }

    return null
}

private const val DEFAULT_PROBLEMS = "A B C D E F G"
private const val EXTENDED_PROBLEMS = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z"

private fun buildTemplate(contestType: String, contestName: String): String {
    val packageName = "$contestType.$contestName"
    return """
        package $packageName

        fun main() {

        }

        @Suppress("unused")
        private fun chmin(a: IntArray, idx: Int, v: Int): Boolean {
            if (v < a[idx]) {
                a[idx] = v
                return true
            }
            return false
        }

        @Suppress("unused")
        private fun chmax(a: IntArray, idx: Int, v: Int): Boolean {
            if (v > a[idx]) {
                a[idx] = v
                return true
            }
            return false
        }

        @Suppress("unused")
        private fun chmin(a: LongArray, idx: Int, v: Long): Boolean {
            if (v < a[idx]) {
                a[idx] = v
                return true
            }
            return false
        }

        @Suppress("unused")
        private fun chmax(a: LongArray, idx: Int, v: Long): Boolean {
            if (v > a[idx]) {
                a[idx] = v
                return true
            }
            return false
        }
    """.trimIndent()
}
