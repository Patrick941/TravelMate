package pathFinding.main


//Package declaration for a Kotlin class called Dijkstra. It imports the Stack class from the java.util package.
import Java.util.stack
// class declaration for Dijkstra which takes in a Graph object in the constructor.
class Dijkstra(private val graph: Graph) {
// This is a function named distance that takes two Int parameters: srcNode and dstNode. The function returns an Int.
    fun distance(srcNode: Int, dstNode: Int): Int {
        // declare and initialize three arrays, dist, prev, and Q. dist and prev are arrays of size graph.maxNodes, and are initialized to -1. 
        //Q is initialized as a mutable set of graph.nodes(). dist[srcNode] is set to 0.
       
        val dist = Array(graph.maxNodes) {-1}
        val prev = Array(graph.maxNodes) {-1}
        val Q = graph.nodes().toMutableSet()
        dist[srcNode] = 0
        //while loop that continues as long as Q has elements.
        while(Q.size > 0) {
    //declares a val named u and sets it to the minimum value of dist in the set Q.
            val u = min(dist, Q)
            
            //if statement checks if the current node u is the destination node dstNode. If it is, the loop is broken.
            if (u == dstNode) {
                break
            }
            //removes u from the set Q
            Q.remove(u)
//iterates over the neighbors of the current node u. It calculates a new distance alt from srcNode to the neighbor node through u, and updates dist and prev if this distance is smaller than the previous distance. 
//If nodeNeighbors(u) returns null, it throws an exception indicating that no path was found.
            
            graph.nodeNeighbours(u)?.forEach { edge ->
                val alt = dist[u] + edge.len
                if (dist[edge.dst] == -1 || alt < dist[edge.dst]) {
                    dist[edge.dst] = alt
                    prev[edge.dst] = u
                }
            } ?: throw Exception("No path found")
        //ends while loop
        }
// creates a new Stack object called S, and uses prev to build 
//the path from srcNode to dstNode, in reverse order.
        val S = Stack<Int>()
        var u = dstNode
        while (prev[u] > -1) {
            S.push(u)
            u = prev[u]
        }

        S.push(u)
// calculates the total distance of the path by iterating over the elements in `S
        var s = S.pop()
        var pathDist = 0
        for(j in 0 .. (S.size - 1)) {
            val d = S.pop()
            pathDist += graph.edgeWeight(s, d) ?: 0
            s = d
        }
        return pathDist
    }

}

//In the context of Dijkstra's algorithm, this function is used to find the next node to explore in the graph. The set Q represents the unexplored nodes, and dist represents the distance from the starting node to each node in the graph. 
//The function finds the unexplored node with the smallest distance from the starting node, 
//which is the node that is closest to the starting node among the unexplored nodes. This is an important step in Dijkstra's algorithm to ensure that the shortest path is always explored first.
//function initializes two variables pos and last. pos is the index of the minimum value found so far, and last is the minimum value itself.
private fun min(dist: Array<Int>, Q: Set<Int>): Int {
    var pos = -1
    var last = Int.MAX_VALUE
    //iterates over each element in the dist array using the forEachIndexed function, which provides both the index idx and the element value node at that index. It checks whether the current element value node is smaller than the current minimum last, and whether the current index idx is contained in the set Q. 
    //If both conditions are true, it updates the minimum value last and the corresponding index pos.
    dist.forEachIndexed {idx, node ->
        if(node < last && node > -1 && Q.contains(idx)) {
            pos = idx
            last = node
        }
    }
//the function returns the index pos of the node with the minimum distance in dist that is also contained in the set Q.git 
    return pos
}