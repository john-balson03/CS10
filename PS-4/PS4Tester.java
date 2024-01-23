import java.io.File;
import java.util.*;

public class PS4Tester {
    public static void main(String[] args) throws Exception {
        Graph<String, String> g1 = new AdjacencyMapGraph<>();
        g1.insertVertex("A");
        g1.insertVertex("B");
        g1.insertVertex("C");
        g1.insertVertex("D");
        g1.insertVertex("E");
        g1.insertDirected("A", "B", "");
        g1.insertDirected("A", "C", "");
        g1.insertDirected("A", "D", "");
        g1.insertDirected("A", "E", "");
        g1.insertDirected("B", "A", "");
        g1.insertDirected("B", "C", "");
        g1.insertDirected("C", "A", "");
        g1.insertDirected("C", "B", "");
        g1.insertDirected("C", "D", "");
        g1.insertDirected("E", "B", "");
        g1.insertDirected("E", "C", "");

        AdjacencyMapGraph spt1 = (AdjacencyMapGraph) (GraphLibrary.bfs(g1, "E"));
        System.out.println("Tree->");
        System.out.println(spt1);
        System.out.println("Path: " + GraphLibrary.getPath(spt1, "D"));
        System.out.println("Missing vertices: " + GraphLibrary.missingVertices(g1, spt1));
        System.out.println("Average separation: " + GraphLibrary.averageSeparation(spt1, "E"));
        System.out.println();

        Graph<String, Set<String>> g2 = new AdjacencyMapGraph<>();
        HashMap<String, String> actors = new HashMap<>();
        HashMap<String, String> movies = new HashMap<>();

        //build map from actorIDs to actor names
        Scanner in = new Scanner(new File("PS-4/actorsTest.txt"));
        String[] line;
        while(in.hasNextLine())
        {
            line = in.nextLine().split("\\|");
            actors.put(line[0], line[1]);
        }

        //build map from movieIDs to movie names
        in = new Scanner(new File("PS-4/moviesTest.txt"));
        while(in.hasNextLine())
        {
            line = in.nextLine().split("\\|");
            movies.put(line[0], line[1]);
        }

        //fill graph with actors as vertices and movies as edges
        in = new Scanner(new File("PS-4/movie-actorsTest.txt"));
        line = new String[2];
        if(in.hasNextLine())
        {
            line = in.nextLine().split("\\|");
        }
        while(in.hasNextLine())
        {
            //list starting with a movie ID followed by all actor IDs in movie
            ArrayList<String> movieActorsIDs = new ArrayList<>();
            movieActorsIDs.add(line[0]);
            movieActorsIDs.add(line[1]);

            if(in.hasNextLine())
            {
                String[] nextLine = in.nextLine().split("\\|");
                while(nextLine[0].equals(line[0]))
                {
                    movieActorsIDs.add(nextLine[1]);
                    if(in.hasNextLine())
                    {
                        nextLine = in.nextLine().split("\\|");
                    }
                    else break;
                }
                line = nextLine;
            }
            for(int i = 1; i < movieActorsIDs.size(); i++)
            {
                for(int j = 1; j < movieActorsIDs.size(); j++)
                {
                    if (!g2.hasVertex(actors.get(movieActorsIDs.get(i))))
                    {
                        g2.insertVertex(actors.get(movieActorsIDs.get(i)));
                    }
                    if (!g2.hasVertex(actors.get(movieActorsIDs.get(j))))
                    {
                        g2.insertVertex(actors.get(movieActorsIDs.get(j)));
                    }
                    if(!g2.hasEdge(actors.get(movieActorsIDs.get(i)), actors.get(movieActorsIDs.get(j))))
                    {
                        g2.insertDirected(actors.get(movieActorsIDs.get(i)), actors.get(movieActorsIDs.get(j)), new HashSet<String>());
                    }
                    g2.getLabel(actors.get(movieActorsIDs.get(i)), actors.get(movieActorsIDs.get(j))).add(movies.get(movieActorsIDs.get(0)));
                }
            }
        }
        System.out.println("Tree->");
        AdjacencyMapGraph spt2 = (AdjacencyMapGraph) (GraphLibrary.bfs(g2, "Kevin Bacon"));
        System.out.println(spt1);
        System.out.println("Path: " + GraphLibrary.getPath(spt2, "Dartmouth (Earl thereof)"));
        System.out.println("Missing vertices: " + GraphLibrary.missingVertices(g2, spt2));
        System.out.println("Average separation: " + GraphLibrary.averageSeparation(spt2, "Kevin Bacon"));

    }
}
