package unh.edu.cs

import java.io.File

val incomingLinks: HashMap<String, ArrayList<String>> = HashMap()           // Nodes that link to node (key)
val scores: HashMap<String, Double> = HashMap()                             // Current scores of the nodes
val nodeList: ArrayList<String> = ArrayList()                               // List of all nodes
const val tChance: Double = 0.85
val outDegree: HashMap<String, Int> = HashMap()
val argOptions: HashMap<String, String> = HashMap()

// Reads node adjacency list
fun parsePages(filename: String) {
    File(filename)
            .bufferedReader()
            .forEachLine {
                val elements = it.split("\t")
                val originNode = elements[0]
                val outGoingNodes = elements.drop(1).toList()

                nodeList += originNode
                scores[originNode] = 0.0
                outDegree[originNode] = outGoingNodes.size
                outGoingNodes.forEach { incomingLinks.getOrPut(it, {ArrayList()}) += originNode}
            }
}

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
    argOptions.forEach { t, u -> println("$t: $u")  }
//    val graphLocation = "graph.txt"
//    parsePages(graphLocation)
//    doPageRank()
//    printScore()
//    val out = File(graphLocation).bufferedReader()
//    out.forEachLine { println(it) }
}