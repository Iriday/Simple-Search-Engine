package search

import java.io.File

fun main(args: Array<String>) {
    SearchEngine().run(args)
}

class SearchEngine {
    fun run(args: Array<String>) {
        val data: Array<String> // lines
        val mapWordsInvertedIndex: MutableMap<String, MutableList<Int>>

        if (args.isNotEmpty()) { // init data from a file
            if (args.size != 2) {
                output("Error, incorrect args length")
                return
            }
            if (args[0] == "--data") {
                try {
                    data = File(args[1]).readLines().toTypedArray()
                    if (data.isEmpty()) {
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

        mapWordsInvertedIndex = fillMapWithWordsInvertedIndex(HashMap(), data)

        while (true) {
            when (menu()) { // option
                1 -> {
                    output("\nEnter data:")
                    output(searchInfo(input(), data))
                }
                2 -> {
                    output("\nEnter data:")
                    output(searchInfo(input(), data, mapWordsInvertedIndex))
                }
                3 -> outputAllData(data)
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
                |1. Search information (extended, slower search "contains")
                |2. Search information (fast search "inverted index")
                |3. Print all data
                |0. Exit
            """.trimMargin())
            try {
                val input = input().toInt()
                if (input in 0..3) return input
                else output("\nIncorrect option! Try again.")
            } catch (e: Exception) {
                output("\nIncorrect input! Try again.")
            }
        }
    }

    // contains
    private fun searchInfo(chars: String, data: Array<String>): String {
        val searchResults = search(chars, data)
        if (searchResults.isNotEmpty()) {
            val builder = StringBuilder("${searchResults.size} results.\n")
            searchResults.forEach { line ->
                builder.append(line)
                builder.append('\n')
            }
            return builder.toString().trimEnd()

        } else return ("No data found.")
    }

    // inverted index
    private fun searchInfo(word: String, data: Array<String>, wordsInvIndex: MutableMap<String, MutableList<Int>>): String {
        val lineIndexes = wordsInvIndex[word.toLowerCase()] ?: return "No data found."
        val builder = StringBuilder("${lineIndexes.size} results.\n")
        lineIndexes.forEach { lineIndex ->
            builder.append(data[lineIndex])
            builder.append('\n')
        }
        return builder.toString().trimEnd()
    }

    private fun outputAllData(lines: Array<String>) {
        output("\n=== Data ===")
        lines.forEach { line -> output(line) }
    }

    private fun input(): String = readLine()!!.trim()

    private fun output(data: String) = println(data)
}

fun fillMapWithWordsInvertedIndex(map: MutableMap<String, MutableList<Int>>, data: Array<String>): MutableMap<String, MutableList<Int>> {
    val regex = Regex("\\s+")
    for ((lineIndex, line) in data.withIndex()) {
        val words = line.split(regex)

        for (word in words) {
            val w = word.toLowerCase()
            val temp = map[w]

            if (temp == null) {
                map[w] = mutableListOf(lineIndex)
            } else {
                temp.add(lineIndex)
            }
        }
    }
    return map
}

// contains
fun search(chars: String, lines: Array<String>): List<String> {
    val searchResults = ArrayList<String>()

    for (line in lines) {
        if (line.contains(chars, true)) searchResults.add(line)
    }
    return searchResults
}

fun getWordNumber(word: String, words: List<String>): Int { // linear search
    for ((index, w) in words.withIndex()) {
        if (w.equals(word, true)) return index + 1 // numbers start from 1
    }
    return -1
}
