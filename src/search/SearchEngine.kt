package search

import java.io.File

fun main(args: Array<String>) {
    SearchEngine().run(args)
}

class SearchEngine {
    fun run(args: Array<String>) {
        val data: Array<String> // lines

        if (args.isNotEmpty()) { // init data from a file
            if (args.size != 2) {
                output("Error, incorrect args length")
                return
            }
            if (args[0] == "--data") {
                try {
                    data = File(args[1]).readLines().toTypedArray()
                    if(data.isEmpty()){
                        output("Error, file isEmpty")
                        return
                    }
                } catch (e: Exception) {
                    output("Error, ${e.message}")
                    return
                }
            } else {
                output("Error, unknown arg \"${args[0]}\"")
                return
            }
        } else { // init data from console
            output("Enter the number of lines with data:")
            val numOfLines: Int
            try {
                numOfLines = input().toInt()
            } catch (e: NumberFormatException) {
                output("Error, incorrect input.")
                return
            }
            output("Enter all lines with data:")
            data = Array(numOfLines) { input() }
        }

        while (true) {
            when (menu()) { // option
                1 -> {
                    output("\nEnter data:")
                    output(searchInfo(input(), data))
                }
                2 -> outputAllData(data)
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
