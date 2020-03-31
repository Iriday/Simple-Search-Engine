package search

fun main() {
    SearchEngine().run()
}

class SearchEngine {
    fun run() {
        val input = input()
        val words = input[0].split(Regex("\\s+"))
        val word = input[1]

        output(getWordNumber(word, words)?.toString() ?: "Not found")
    }

    private fun input(): Array<String> {
        return arrayOf(readLine()!!.trim(), readLine()!!.trim())
    }

    private fun output(data: String) = println(data)
}

fun getWordNumber(word: String, words: List<String>): Int? {
    for ((index, w) in words.withIndex()) {
        if (w == word) return index + 1 // numbers start from 1
    }
    return null
}
