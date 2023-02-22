
// implementation of a graph data structure,
//which is a collection of nodes (also called vertices) and edges that connect those nodes
package pathFinding.main

import java.util.HashMap

// Define a Graph class that takes the maximum number of nodes as input
class Graph(val maxNodes: Int) {

    // Create an empty adjacency map using a HashMap with Int keys and MutableMap<Int, Int> values
    private val adjacencyMap = HashMap<Int, MutableMap<Int, Int>>()

    // Add an edge to the graph by calling the second addEdge() function with the values from the Edge object
    fun addEdge(edge: Edge) {
        addEdge(edge.src, edge.dst, edge.len)
    }
 // Add an edge to the graph by specifying the source node, destination node, and edge weight
    fun addEdge(srcNode: Int, dstNode: Int, weight: Int) {
    // Check that the weight is greater than or equal to zero    
        if (weight < 0) {
            throw IllegalArgumentException("Weight must be greater than 0")
        }
        // Check that the source node id and destination node id are within the valid range
        if (srcNode < 1 || srcNode > maxNodes || dstNode < 1 || dstNode > maxNodes) {
            throw IllegalArgumentException("Node Id numbers must be between 1 and 256")
        }
// Add both one-way edges to the adjacency map using the addOneWayEdge() function
        addOneWayEdge(srcNode, dstNode, weight)
        addOneWayEdge(dstNode, srcNode, weight)
    }
// Add a one-way edge to the adjacency map
    private fun addOneWayEdge(src: Int, dst: Int, weight: Int) {
// If the source node is already in the adjacency map, add the destination node and edge weight to its map 
        if (adjacencyMap.containsKey(src)) {
            adjacencyMap[src]?.put(dst, weight)
        } 
// If the source node is not in the adjacency map, create a new map and add the destination node and edge weight to it   
        else {
            val map = HashMap<Int, Int>()
            map[dst] = weight
            adjacencyMap[src] = map
        }
    }
// Get the edge weight between two nodes
    fun edgeWeight(srcNode: Int, dstNode: Int): Int? {
        return adjacencyMap[srcNode]?.get(dstNode)
    }
// Get the neighbours of a node
    fun nodeNeighbours(node: Int): Set<Edge>? {
        return adjacencyMap[node]?.entries?.mapTo(mutableSetOf()) {
            it -> Edge(node, it.key, it.value)
        }
 // If the node is not in the adjacency map, return null
    }
  // Get all the nodes in the graph
    fun nodes(): Set<Int> {
        return adjacencyMap.keys.toSet()
    }
 // Check if a node is in the graph
    fun hasNode(node: Int): Boolean {
        return adjacencyMap[node] != null
    }

}