# jsr-pagerank

## Installation
To compile a jar file using Maven, run the following command: mvn clean compile assembly:single

The resulting jar file will be located in the target/ directory.

Alternatively, you may run the precompiled jar file located in bin (jsr_pagerank.jar)

## Usage
java -jar jsr_pagerank.jar -graph graphFile [-seed_set seedFile]

Where:
 - graphFile: adjacency list of nodes (see graph.txt for example)
 - seedFile: (optional) file containing seed node IDs for personalized PageRank, one per line (see seed_set.txt)
 
 The top 10 pages will be printed according to PageRank (or Personalized PageRank if a seed set is given).
 
 #### Example for normal PageRank: 
 -jar jsr_pagerank.jar -graph graph.txt
 
 #### Example for personalized PageRank: 
 -jar jsr_pagerank.jar -graph graph.txt -seed_set seed_Set.txt
