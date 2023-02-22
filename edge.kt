//defines a class Edge with three properties: 
//src (source vertex), dst (destination vertex), and len (length or weight of the edge).
// It also overrides three methods: equals, hashCode, and toString.

package pathFinding.main


class Edge(val src: Int, val dst: Int, val len: Int) {
//checks if two edges are equal
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (this.javaClass != other?.javaClass) return false

        other as Edge

        if (src != other.src) {
            return (src == other.dst) && (dst == other.src)
        }
        if (dst != other.dst) return false
        if (len != other.len) return false

        return true
    }
//Computes a hash code for an edge. The hash code is computed based on the 
//source and destination vertices and the length of the edge, using the formula
// produces a unique hash code for each edge, taking into account the fact that an edge
// from vertex A to vertex B is the same as an edge from vertex B to vertex A.
    override fun hashCode(): Int {
        var result = (src + dst) * Math.abs(src - dst) * 31
        result = 31 * result + len
        return result
    }
//returns a string representation of an edge, in the format 
    override fun toString(): String {
        return "{$src <-($len)-> $dst}"
    }
}
