package abc.abc071

// genre
fun main() = readln().let{s -> println(('a'..'z').firstOrNull{it !in s} ?: "None")}
