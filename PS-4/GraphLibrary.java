import java.util.*;

/**
 * Class which provides static methods bfs, getPath, missingVertices, and averageSeparation
 */
public class GraphLibrary {

    /**
     * Breadth first search on a graph g given a starting point source
     * @param g graph
     * @param source starting point for search
     * @param <V>
     * @param <E>
     * @return a tree of shortest paths from vertices in g to source
     * @throws Exception
     */
    public static <V,E> Graph<V,E> bfs(Graph<V,E> g, V source) throws Exception {
        SLLQueue<V> queue = new SLLQueue<>();
        ArrayList<V> visited = new ArrayList<>();
        Graph<V,E> pathTree = new AdjacencyMapGraph<>();
        V u;

        queue.enqueue(source);
        visited.add(source);
        //add source to the path tree
        pathTree.insertVertex(source);
        //enqueue out neighbors of source and recurse on them
        while(!queue.isEmpty())
        {
            u = queue.dequeue();
            for(V v : g.outNeighbors(u))
            {
                if(!visited.contains(v))
                {
                    visited.add(v);
                    queue.enqueue(v);
                    pathTree.insertVertex(v);
                    pathTree.insertDirected(v, u, g.getLabel(u, v));
                }

            }

        }

        return pathTree;
    }

    /**
     * gets the path from a vertex to the root of the tree
     * @param tree the tree
     * @param v the vertex to start at
     * @param <V>
     * @param <E>
     * @return a list of vertexes visited while going to root
     */
    public static <V,E> List<V> getPath(Graph<V,E> tree, V v)
    {
        List<V> path = new ArrayList<>();
        path.add(v);
        while(tree.outDegree(v) > 0)
        {
            path.add(tree.outNeighbors(v).iterator().next());
            v = tree.outNeighbors(v).iterator().next();
        }
        return path;
    }

    /**
     * computes the vertexes in graph that do not appear in subgraph
     * @param graph the larger graph
     * @param subgraph a graph containing some vertices from graph
     * @param <V>
     * @param <E>
     * @return the set of vertices in graph that do not exist in subgraph
     */
	public static <V,E> Set<V> missingVertices(Graph<V,E> graph, Graph<V,E> subgraph)
    {
        Set<V> set  = new HashSet<>();
        Iterator i = graph.vertices().iterator();
        while(i.hasNext())
        {
            V vertex = (V) i.next();
            if(!subgraph.hasVertex(vertex))
            {
                set.add(vertex);
            }
        }
        return set;
    }

    /**
     * computes the average separation between the root and all vertices in a tree
     * @param tree the tree
     * @param root the root of the tree
     * @param <V>
     * @param <E>
     * @return the average degrees of separation
     */
	public static <V,E> double averageSeparation(Graph<V,E> tree, V root)
    {
        ODouble totDeg = new ODouble(0);
        ODouble numVert = new ODouble(0);
        averageSeparationHelper(tree, root, 1.0, totDeg, numVert);
        return totDeg.get() / numVert.get();
    }

    /**
     * averageSeparation helper method, recursing on all in neighbors of root
     * @param tree
     * @param root
     * @param degree
     * @param totDeg
     * @param numVert
     * @param <V>
     * @param <E>
     */
    private static <V, E> void averageSeparationHelper(Graph<V,E> tree, V root, Double degree, ODouble totDeg, ODouble numVert)
    {
        numVert.add(1);

        Iterator i = tree.inNeighbors(root).iterator();
        while(i.hasNext())
        {
            totDeg.add(degree);
            V v = (V) i.next();
            averageSeparationHelper(tree, v, degree + 1, totDeg, numVert);
        }
    }
}
