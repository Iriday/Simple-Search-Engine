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
            output("Error, incorrect input.")
            return
        }
        output("Enter all lines with data:")
        val lines = Array(numOfLines) { input() }

        output("\nEnter the number of search queries:")
        val numOfQueries: Int
        try {
            numOfQueries = input().toInt()
        } catch (e: NumberFormatException) {
            output("Error, incorrect input.")
            return
        }

        repeat(numOfQueries) {
            output("\nEnter the data:")
            val data = input()

            val searchResults = search(data, lines)
            if (searchResults.isNotEmpty()) {
                output("\nFound data:")
                searchResults.forEach { line -> output(line) }
            } else output("No data found.")
        }
    }

    private fun input(): String = readLine()!!.trim()

    private fun output(data: String) = println(data)
}

fun search(data: String, lines: Array<String>): List<String> {
    val searchResults = ArrayList<String>()

    for (line in lines) {
        if (line.contains(data, true)) searchResults.add(line)
    }
    return searchResults
}

fun getWordNumber(word: String, words: List<String>): Int { // linear search
    for ((index, w) in words.withIndex()) {
        if (w.equals(word, true)) return index + 1 // numbers start from 1
    }
    return -1
}
