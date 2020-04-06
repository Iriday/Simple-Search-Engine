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
        val dataInvertedIndex: MutableMap<String, MutableList<Int>>
        val queryConverter = ::trimSplitToLowerCaseQuery

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

        dataInvertedIndex = invertedIndex(HashMap(), data, queryConverter)

        mainMenu@ while (true) {
            when (val menuOption = mainMenu()) {
                SEARCH_EXTENDED_SLOW, SEARCH_FAST -> {
                    val strategy = searchStrategyMenu() ?: continue@mainMenu

                    output("\nEnter data:")
                    var query: String
                    do {
                        query = input()
                    } while (query.isEmpty())
                    val convertedQuery = queryConverter(query)

                    val searchResults = when (menuOption) {
                        SEARCH_EXTENDED_SLOW -> search(convertedQuery, data, strategy)
                        SEARCH_FAST -> search(convertedQuery, data, strategy, dataInvertedIndex)
                        else -> emptyList() /*ignore this line*/
                    }
                    output(searchResultsToString(searchResults))
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

    private val trimQuery = " \t\n\r.,!?" // \\x0B\\f  // check
    private val splitQuery = Regex("[$trimQuery]+") // "[\\s\\p{Punct}]+"

    private fun trimSplitToLowerCaseQuery(query: String): List<String> {
        return query.toLowerCase().trim { char -> char in trimQuery }.split(splitQuery)
    }

    private fun input(): String = readLine()!!.trim()

    private fun output(vararg data: String) = data.forEach { println(it) }
}

// contains
private fun search(convertedQuery: List<String>, data: Array<String>, strategy: SearchStrategy): List<String> {
    val searchResults = when (strategy) {
        ALL -> {
            val searchResults = ArrayList<String>()
            line@ for (line in data) {
                for (q in convertedQuery) {
                    if (!line.contains(q, true)) continue@line
                }
                searchResults.add(line)
            }
            searchResults
        }
        ANY -> listOf("This search strategy is not implemented yet, use ALL or NONE")
        NONE -> {
            val searchResults = data.filter { line ->
                for (q in convertedQuery) {
                    if (line.contains(q, true)) return@filter false
                }
                true
            }
            searchResults
        }
    }
    return searchResults
}

// inverted index
private fun search(convertedQuery: List<String>, data: Array<String>, strategy: SearchStrategy, dataInvIndex: MutableMap<String, MutableList<Int>>): List<String> {
    if (strategy == ANY) return listOf("This search strategy is not implemented yet, use ALL or NONE")

    val lineIndexes = when (strategy) {
        ALL -> {
            val lineIndexes = ArrayList<Int>()
            lineIndexes.addAll(dataInvIndex[convertedQuery[0]] ?: return emptyList())
            for (queryIndex in 1..convertedQuery.lastIndex) {
                val tempLineIndexes = dataInvIndex[convertedQuery[queryIndex]] ?: return emptyList()
                lineIndexes.retainAll(tempLineIndexes)
                if (lineIndexes.isEmpty()) return emptyList()
            }
            lineIndexes
        }
        ANY -> listOf(0)
        NONE -> {
            var supplier = 0
            val lineIndexes = MutableList(data.size) { supplier++ }
            for (q in convertedQuery) {
                val temp = dataInvIndex[q] ?: continue
                lineIndexes.removeAll(temp)
            }
            lineIndexes
        }
    }
    return lineIndexes.map { data[it] }
}

fun invertedIndex(map: MutableMap<String, MutableList<Int>>, data: Array<String>, lineConverter: (String) -> List<String>): MutableMap<String, MutableList<Int>> {
    for ((lineIndex, line) in data.withIndex()) {
        val words = lineConverter(line) // = convertQuery(line)

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

fun searchResultsToString(data: List<String>): String {
    if (data.isEmpty()) return "No data found."

    val builder = StringBuilder("${data.size} results.\n")
    data.forEach { d ->
        builder.append(d)
        builder.append('\n')
    }
    return builder.toString().trimEnd()
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
