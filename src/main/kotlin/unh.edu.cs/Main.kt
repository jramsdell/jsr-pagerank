package unh.edu.cs

import java.io.File
import java.lang.System.exit

val incomingLinks: HashMap<String, ArrayList<String>> = HashMap()           // Nodes that link to node (key)
val outDegree: HashMap<String, Int> = HashMap()                             // Out-degree of a node
val scores: HashMap<String, Double> = HashMap()                             // Current scores of the nodes
val argOptions: HashMap<String, String> = HashMap()                         // Stores command-line options

val seedSet: HashSet<String> = HashSet()                                    // Used for personalized PageRank
val nodeList: ArrayList<String> = ArrayList()                               // List of all nodes

const val tChance: Double = 0.85                                            // Dampening factor

val usage =
"""Usage: -graph graphFile [-seed_set seedFile]
Where:
    graphFile: adjacency list of nodes (see graph.txt for example)
    seedFile: (optional) file containing seed node IDs for personalized PageRank, one per line."""


fun printUsage() {
    println(usage)
    exit(1)
}

// Reads node adjacency list
fun parsePages(filename: String) =
    File(filename)
            .bufferedReader()
            .forEachLine { line ->
                val elements = line.split("\t")
                val originNode = elements[0]
                val outGoingNodes = elements.drop(1).toList()

                scores[originNode] = 0.0
                nodeList += originNode
                outDegree[originNode] = outGoingNodes.size

                outGoingNodes.forEach { node -> incomingLinks.getOrPut(node, { ArrayList() }) += originNode }
            }

// Parse personalized pages
fun parseSeedSet(filename: String) = File(filename).readLines().toCollection(seedSet)

// Backup a single node with respect to the PageRank formula
fun doBackup(node: String) {
    val total = incomingLinks[node]?.sumByDouble { scores[it]!! / outDegree[it]!! } ?: 0.0
    val initialValue = when {
        seedSet.isEmpty() -> (1 - tChance) / nodeList.size          // Normal PageRank
        seedSet.contains(node) -> (1 - tChance) / seedSet.size      // Personalized PageRank
        else -> 0.0                                                 // Personalized PageRank
    }

    scores[node] = initialValue + tChance * total
}

// I didn't know if there was a set numer of iterations we should do, or if we should be using residuals (a la MDPs).
// So I just settled on a constant number of iterations...
fun doPageRank() {
    (0..5000).forEach {
        nodeList.forEach { doBackup(it) }
    }
}

// Prints the top 10 pages according to PageRank scores
fun printScore() {
    // renormalize values
    scores.entries.sumByDouble { it.value }
            .let { total -> scores.replaceAll { _, value -> value / total } }

    // print top 10
    scores.entries.sortedByDescending { it.value }
            .take(10)
            .forEach { (k,v) -> println("$v \t $k")  }
}

fun parseCommands(args: Array<String>) {
    var cur = ""
    args.forEach { if (it.startsWith("-")) cur = it else argOptions[cur] = it }
}

fun main(args: Array<String>) {
    parseCommands(args)
    argOptions["-seed_set"]?.let { parseSeedSet(it) }
    argOptions["-graph"]?.let { parsePages(it) } ?: printUsage()
    doPageRank()
    printScore()
}