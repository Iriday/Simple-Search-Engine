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

        while (true) {
            when (menu()) { // option
                1 -> {
                    output("\nEnter data:")
                    output(searchInfo(input(), lines))
                }
                2 -> outputAllData(lines)
                0 -> {
                    output("\nBye!")
                    return
                }
            }
        }
    }

    private fun menu(): Int {
        while (true) {
            output("""|
                |=== Menu ===
                |1. Search information
                |2. Print all data
                |0. Exit
            """.trimMargin())
            try {
                val input = input().toInt()
                if (input in 0..2) return input
                else output("\nIncorrect option! Try again.")
            } catch (e: Exception) {
                output("\nIncorrect input! Try again.")
            }
        }
    }

    private fun searchInfo(data: String, lines: Array<String>): String {
        val searchResults = search(data, lines)
        if (searchResults.isNotEmpty()) {
            val builder = StringBuilder()
            searchResults.forEach { line ->
                builder.append(line)
                builder.append('\n')
            }
            return builder.toString().trimEnd()

        } else return ("No data found.")
    }

    private fun outputAllData(lines: Array<String>) {
        output("\n=== Data ===")
        lines.forEach { line -> output(line) }
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
