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

fun printUsage() {
    exit(1)
}

// Reads node adjacency list
fun parsePages(filename: String) =
    File(filename)
            .bufferedReader()
            .forEachLine {
                val elements = it.split("\t")
                val originNode = elements[0]
                val outGoingNodes = elements.drop(1).toList()
                scores[originNode] = 0.0

                // Normal PageRank
                if (!argOptions.containsKey("-seed_set")) {
                    nodeList += originNode
                    outDegree[originNode] = outGoingNodes.size
                    outGoingNodes.forEach { incomingLinks.getOrPut(it, { ArrayList() }) += originNode }
                }
                // Personalized PageRank
                else if (originNode in seedSet || outGoingNodes.any { seedSet.contains(it) }) {
                    nodeList += originNode

                    // If the origin is in the seedSet, all of the outgoing nodes are relevant.
                    // Otherwise, we only consider links to outgoing nodes that are in the seed set.
                    val relevantNodes = if (seedSet.contains(originNode)) outGoingNodes
                                        else outGoingNodes.filter { seedSet.contains(it) }

                    outDegree[originNode] = relevantNodes.size
                    relevantNodes.forEach { incomingLinks.getOrPut(it, { ArrayList() }) += originNode }
                }
            }

// Parse personalized pages
fun parseSeedSet(filename: String) = File(filename).readLines().toCollection(seedSet)

fun doBackup(node: String) {
    val total = incomingLinks[node]?.sumByDouble { scores[it]!! / outDegree[it]!! } ?: 0.0
    scores[node] = (1 - tChance) / nodeList.size + tChance * total
}

fun doPageRank() {
    (0..50000).forEach {
        nodeList.forEach { doBackup(it) }
    }
}

fun printScore() {
    // renormalize values
    scores.entries.sumByDouble { it.value }
            .let { total -> scores.replaceAll { key, value -> value / total } }

    // print top 10
    scores.entries.sortedByDescending { it.value }
            .take(10)
            .forEach { (k,v) -> println("$k: $v")  }
}

fun parseCommands(args: Array<String>) {
    var cur = ""
    args.forEach { if (it.startsWith("-")) cur = it else argOptions[cur] = it }
}

fun main(args: Array<String>) {
    parseCommands(args)
    argOptions["-graph"]?.let { parsePages(it) } ?: printUsage()
    argOptions["-seed_set"]?.let { parseSeedSet(it) }
    doPageRank()
    printScore()
}