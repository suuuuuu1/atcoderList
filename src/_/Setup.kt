package _

import java.io.File

fun main() {
    print("contestName -> ")
    val contestName = readln().trim()
    require(contestName.isNotEmpty()) { "コンテスト名が空です" }

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
    val template = buildTemplate(contestType, contestName)

    val dir = File("src/$contestType/$contestName").also { it.mkdirs() }
    problems.forEach { problem ->
        val file = File(dir, "$problem.kt")
        if (file.exists()) return@forEach
        file.writeText(template)
        println("${file.name} を生成しました")
    }
    println("==== $contestName の準備が完了しました ====")
}

private const val DEFAULT_PROBLEMS = "A B C D E F G"
private const val EXTENDED_PROBLEMS = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z"

private fun buildTemplate(contestType: String, contestName: String): String = """
    package $contestType.$contestName

    fun main() {
        
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
