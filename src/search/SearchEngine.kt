package search

fun main() {
    SearchEngine().run()
}

class SearchEngine {
    fun run() {
        output("Enter the number of lines with data:")
        val numOfLines: Int
        try {
            numOfLines = input().toInt()
        } catch (e: NumberFormatException) {
            output("Error, incorrect input")
            return
        }
        output("Enter all lines with data:")
        val lines = Array(numOfLines) { input() }
        output("\nEnter the word:")
        val word = input()

        val searchResults = ArrayList<String>()

        for (line in lines) {
            val searchResult = getWordNumber(word, line.split(Regex("\\s+")))
            if (searchResult != -1) searchResults.add(line)
        }

        if (searchResults.isNotEmpty()) {
            output("\nfound data:")
            searchResults.forEach { line -> output(line) }
        } else output("No data found.")
    }

    private fun input(): String = readLine()!!.trim()

    private fun output(data: String) = println(data)
}

fun getWordNumber(word: String, words: List<String>): Int { // linear search
    for ((index, w) in words.withIndex()) {
        if (w.equals(word, true)) return index + 1 // numbers start from 1
    }
    return -1
}
