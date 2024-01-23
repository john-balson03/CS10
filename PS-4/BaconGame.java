import java.io.File;
import java.util.*;

public class BaconGame {
    public static void main(String[] args) throws Exception {
        AdjacencyMapGraph<String, Set<String>> g = new AdjacencyMapGraph<>();
        HashMap<String, String> actors = new HashMap<>();
        HashMap<String, String> movies = new HashMap<>();

        //build map from actorIDs to actor names
        Scanner in = new Scanner(new File("PS-4/actors.txt"));
        String[] line;
        while(in.hasNextLine())
        {
            line = in.nextLine().split("\\|");
            actors.put(line[0], line[1]);
        }

        //build map from movieIDs to movie names
        in = new Scanner(new File("PS-4/movies.txt"));
        while(in.hasNextLine())
        {
            line = in.nextLine().split("\\|");
            movies.put(line[0], line[1]);
        }

        //fill graph with actors as vertices and movies as edges
        in = new Scanner(new File("PS-4/movie-actors.txt"));
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

            //for each movie in file, find which actors starred and connect their vertices with edge labeled with the movie
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
                    if (!g.hasVertex(actors.get(movieActorsIDs.get(i))))
                    {
                        g.insertVertex(actors.get(movieActorsIDs.get(i)));
                    }
                    if (!g.hasVertex(actors.get(movieActorsIDs.get(j))))
                    {
                        g.insertVertex(actors.get(movieActorsIDs.get(j)));
                    }
                    if(!g.hasEdge(actors.get(movieActorsIDs.get(i)), actors.get(movieActorsIDs.get(j))))
                    {
                        g.insertDirected(actors.get(movieActorsIDs.get(i)), actors.get(movieActorsIDs.get(j)), new HashSet<String>());
                    }
                    g.getLabel(actors.get(movieActorsIDs.get(i)), actors.get(movieActorsIDs.get(j))).add(movies.get(movieActorsIDs.get(0)));
                }
            }
        }


        //implementation of the Kevin Bacon game
        Graph pathTree = GraphLibrary.bfs(g, "Kevin Bacon");
        Set<String> disconnected = GraphLibrary.missingVertices(g, pathTree);

        System.out.println("Commands: \nc <#>: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation \nd <low> <high>: list actors sorted by degree, with degree between low and high \ni: list actors with infinite separation from the current center \np <name>: find path from <name> to current center of the universe \ns <low> <high>: list actors sorted by non-infinite separation from the current center, with separation between low and high \nu <name>: make <name> the center of the universe \nq: quit game\n");
        System.out.println("Kevin Bacon is now the center of the acting universe, connected to " + (g.numVertices() - disconnected.size()) + "/9235 actors with average separation " + GraphLibrary.averageSeparation(pathTree, "Kevin Bacon"));

        String command = "";
        String center = "Kevin Bacon";
        in = new Scanner(System.in);

        //continue looking for inputs until command "q" is given
        while(!command.equals("q"))
        {
            System.out.println();
            System.out.println(center + " game >");
            command = in.nextLine();

            //if command is "c <#>", compute the top or bottom centers of the universe by average separation
            if (command.charAt(0) == 'c' && command.length() > 2 && command.charAt(1) == ' ')
            {
                try
                {
                    Integer num = Integer.parseInt(command.substring(2));
                    //create a map from average separation values to sets of actor names
                    HashMap<Double, Set<String>> avgSeparations = new HashMap<>();

                    Iterator i = pathTree.vertices().iterator();
                    //while there are vertexes we haven't processed, figure out their average separation as the center of the universe and add it to the map
                    while(i.hasNext())
                    {
                        String actor = (String) i.next();
                        pathTree = GraphLibrary.bfs(g, actor);
                        Double avgSeperation = GraphLibrary.averageSeparation(pathTree, actor);
                        if(!avgSeparations.containsKey(avgSeperation))
                        {
                            avgSeparations.put(avgSeperation, new HashSet<>());
                        }
                        avgSeparations.get(avgSeperation).add(actor);
                    }
                    //find and list top centers of the universe
                    if(num > 0)
                    {
                        for(int x = 0; x < num; x++)
                        {
                            if(!avgSeparations.isEmpty())
                            {
                                i = avgSeparations.keySet().iterator();
                                Double minKey = (Double) i.next();
                                while (i.hasNext()) {
                                    Double next = (Double) i.next();
                                    if (next < minKey) {
                                        minKey = next;
                                    }
                                }
                                System.out.println(avgSeparations.get(minKey) + " average separation: " + minKey);
                                avgSeparations.remove(minKey);
                            }
                        }
                    }
                    //find and list bottom centers of the universe
                    else if(num < 0)
                    {
                        for(int x = 0; x > num; x--)
                        {
                            if(!avgSeparations.isEmpty())
                            {
                                i = avgSeparations.keySet().iterator();
                                Double maxKey = (Double) i.next();
                                while (i.hasNext()) {
                                    Double next = (Double) i.next();
                                    if (next > maxKey) {
                                        maxKey = next;
                                    }
                                }
                                System.out.println(avgSeparations.get(maxKey) + " average separation: " + maxKey);
                                avgSeparations.remove(maxKey);
                            }
                        }
                    }
                }
                catch(Exception e)
                {
                    System.out.println("Please enter a valid command");
                }
            }
            //if command is "d <#> <#>", list actors sorted by degree from low to high
            else if(command.charAt(0) == 'd' && command.length() > 2 && command.charAt(1) == ' ')
            {
                try
                {
                    String[] nums = command.substring(2).split(" ");
                    Integer low = Integer.parseInt(nums[0]);
                    Integer high = Integer.parseInt(nums[1]);

                    //create a map from degrees to actors
                    HashMap<Integer, Set<String>> degrees = new HashMap<>();
                    Iterator i = g.vertices().iterator();
                    while(i.hasNext())
                    {
                        String actor = (String) i.next();
                        Integer degree = g.inDegree(actor);
                        if(!degrees.containsKey(degree))
                        {
                            degrees.put(degree, new HashSet<String>());
                        }
                        degrees.get(degree).add(actor);
                    }
                    //list actors with degrees in range
                    for (int x = low + 1; x <= high + 1; x++)
                    {
                        if(degrees.containsKey(x))
                        {
                            System.out.println((x - 1) + " degree(s)" + ": " + degrees.get(x));
                        }
                    }
                }
                catch (Exception e)
                {
                    System.out.println("Please enter a valid command");
                }

            }
            //if command is "i", list actors with infinite separation
            else if(command.equals("i"))
            {
                System.out.println("infinite separation: " + disconnected);
            }
            //if command is "p <name>" find and list path from name to root
            else if(command.charAt(0) == 'p' && command.length() > 2 && command.charAt(1) == ' ')
            {
                try
                {
                    String actor = command.substring(2);
                    List<String> path = GraphLibrary.getPath(pathTree, actor);
                    System.out.println(actor + "'s number is " + (path.size() - 1));
                    while(path.size() > 1)
                    {
                        System.out.println(path.get(0) + " appeared in " + ((Set) pathTree.getLabel(path.get(0), path.get(1))).toString() + " with " + path.get(1));
                        path.remove(0);
                    }
                }
                catch(Exception e)
                {
                    System.out.println("Please enter a valid command");
                }
            }
            //if command is "s <#> <#>", list actors by separation from current center from low to high
            else if(command.charAt(0) == 's' && command.length() > 2 && command.charAt(1) == ' ')
            {
                try {
                    String[] nums = command.substring(2).split(" ");
                    Integer low = Integer.parseInt(nums[0]);
                    Integer high = Integer.parseInt(nums[1]);

                    //create and fill map from separation to actors
                    HashMap<Integer, Set<String>> separation = new HashMap<>();
                    Iterator i = pathTree.vertices().iterator();
                    while (i.hasNext()) {
                        String actor = (String) i.next();
                        if (!disconnected.contains(actor))
                        {
                            if(!separation.containsKey(GraphLibrary.getPath(pathTree, actor).size())) {
                                separation.put(GraphLibrary.getPath(pathTree, actor).size(), new HashSet<String>());
                            }
                            separation.get(GraphLibrary.getPath(pathTree, actor).size()).add(actor);
                        }
                    }

                    //print actors whose separation lies within range
                    for (int x = low + 1; x <= high + 1; x++)
                    {
                        if(separation.containsKey(x))
                        {
                            System.out.println("separation " + (x - 1)  + ": " + separation.get(x));
                        }
                    }
                }
                catch (Exception e)
                {
                    System.out.println("Please enter a valid command");
                }
            }
            //if command is "u <name>" set name to be the center of the universe
            else if(command.charAt(0) == 'u' && command.length() > 2 && command.charAt(1) == ' ')
            {
                try
                {
                    center = command.substring(2);
                    pathTree = GraphLibrary.bfs(g, center);
                    disconnected = GraphLibrary.missingVertices(g, pathTree);
                    System.out.println(center + " is now the center of the acting universe, connected to " + (g.numVertices() - disconnected.size()) + "/9235 actors with average separation " + GraphLibrary.averageSeparation(pathTree, center));
                }
                catch(Exception e)
                {
                    System.out.println("Please enter a valid command");
                }

            }
            //if command is "q", quit game
            else if(command.equals("q")){}
            else
            {
                System.out.println("Please enter a valid command");
            }
        }
        in.close();

    }
}
