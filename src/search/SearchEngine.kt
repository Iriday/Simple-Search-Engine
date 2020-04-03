package search

import java.io.File
import search.MenuOption.*
import search.SearchStrategy.*

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
            if (numOfLines < 1) {
                output("Error, the number of lines should be >= 1.")
                return
            }
            output("Enter all lines with data:")
            data = Array(numOfLines) { input() }
        }

        mapWordsInvertedIndex = fillMapWithWordsInvertedIndex(HashMap(), data)

        mainMenu@ while (true) {
            when (mainMenu()) {
                SEARCH_EXTENDED_SLOW -> {
                    val strategy = searchStrategyMenu() ?: continue@mainMenu
                    output("\nEnter data:")
                    output(searchInfo(input(), data, strategy))
                }
                SEARCH_WORD_FAST -> {
                    val strategy = searchStrategyMenu() ?: continue@mainMenu
                    output("\nEnter data:")
                    output(searchInfo(input(), data, strategy, mapWordsInvertedIndex))
                }
                OUTPUT_DATA -> outputAllData(data)
                EXIT -> {
                    output("\nBye!")
                    return
                }
            }
        }
    }

    private fun mainMenu(): MenuOption {
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
                if (input in MenuOption.values().indices) {
                    return MenuOption.values()[input]
                } else {
                    output("\nIncorrect option! Try again.")
                }
            } catch (e: Exception) {
                output("\nIncorrect input! Try again.")
            }
        }
    }

    private fun searchStrategyMenu(): SearchStrategy? {
        while (true) {
            output("""|
                |=== Search strategy ===
                |1. ALL
                |2. ANY
                |3. NONE
                |0. Return
            """.trimMargin())
            try {
                var input = input().toInt()
                if (input == 0) return null
                input--
                if (input in SearchStrategy.values().indices) {
                    return SearchStrategy.values()[input]
                } else {
                    output("\nIncorrect option! Try again.")
                }
            } catch (e: Exception) {
                output("\nIncorrect input! Try again.")
            }
        }
    }

    private fun outputAllData(lines: Array<String>) {
        output("\n=== Data ===")
        lines.forEach { line -> output(line) }
    }

    private fun input(): String = readLine()!!.trim()

    private fun output(vararg data: String) = data.forEach { println(it) }
}

private val regexStr = " \t\n\r.,!?" // \\x0B\\f  // check
private val regex = Regex("[$regexStr]+") // "[\\s\\p{Punct}]+"

private fun convertQuery(query: String): List<String> {
    return query.toLowerCase().trim { char -> char in regexStr }.split(regex)
}

// contains
private fun searchInfo(query: String, data: Array<String>, strategy: SearchStrategy): String {
    val queries = convertQuery(query)
    val builder = StringBuilder()

    return when (strategy) {
        ALL -> {
            val searchResults = ArrayList<String>()
            line@ for (line in data) {
                for (q in queries) {
                    if (!line.contains(q, true)) continue@line
                }
                searchResults.add(line)
            }
            if (searchResults.isNotEmpty()) {
                builder.append("${searchResults.size} results.\n")
                searchResults.forEach { line ->
                    builder.append(line)
                    builder.append('\n')
                }
                return builder.toString().trimEnd()
            } else return ("No data found.")
        }
        ANY -> "This search strategy is not implemented yet, use ALL"
        NONE -> "This search strategy is not implemented yet, use ALL"
    }
}

// inverted index
private fun searchInfo(query: String, data: Array<String>, strategy: SearchStrategy, wordsInvIndex: MutableMap<String, MutableList<Int>>): String {
    val queries = convertQuery(query)
    val builder = StringBuilder()

    return when (strategy) {
        ALL -> {
            val lineIndexes: MutableList<Int> = wordsInvIndex[queries[0]] ?: return "No data found."
            for (wordIndex: Int in 1..queries.lastIndex) {
                val tempLineIndexes = wordsInvIndex[queries[wordIndex]] ?: return "No data found."
                lineIndexes.retainAll(tempLineIndexes)
                if (lineIndexes.isEmpty()) return "No data found."
            }
            builder.append("${lineIndexes.size} results.\n")
            lineIndexes.forEach { lineIndex ->
                builder.append(data[lineIndex])
                builder.append('\n')
            }
            return builder.toString().trimEnd()
        }
        ANY -> "This search strategy is not implemented yet, use ALL"
        NONE -> "This search strategy is not implemented yet, use ALL"
    }
}

fun fillMapWithWordsInvertedIndex(map: MutableMap<String, MutableList<Int>>, data: Array<String>): MutableMap<String, MutableList<Int>> {
    for ((lineIndex, line) in data.withIndex()) {
        val words = convertQuery(line)

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
