import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * A POSTagger can be trained using an input file to predict the parts of speech in a sentence.
 */
public class POSTagger {
    static Double unobservedValue = -100.0;
    HashMap<String, HashMap<String, Double>> vertices;
    HashMap<String, HashMap<String, Double>> edges;

    public POSTagger()
    {
        vertices = new HashMap<>();
        edges = new HashMap<>();
    }

    /**
     * takes an array list of strings representing a sentence and predicts the part of speech of each word using the training data
     * @param line the line to be parsed
     * @return a list of the predicted parts of speech
     */
    public List<String> viterbiDecode(ArrayList<String> line)
    {
        ArrayList<String> currStates = new ArrayList<>();
        currStates.add("#");
        HashMap<String, Double> currScores = new HashMap<>();
        currScores.put("#", 0.0);
        //ArrayList of maps from next state to previous state
        ArrayList<HashMap<String, String>> prev = new ArrayList<>();

        //for i from 0 to # observations - 1
        for(int i = 0; i < line.size(); i++)
        {
            ArrayList<String> nextStates = new ArrayList<>();
            HashMap<String, Double> nextScores = new HashMap<>();
            //for each currState in currStates
            for(String currState : currStates)
            {
                if(edges.get(currState) != null)
                {
                    //for each transition currState -> nextState
                    for(String nextState : edges.get(currState).keySet())
                    {
                        nextStates.add(nextState);
                        Double nextScore;
                        if (vertices.get(nextState).containsKey(line.get(i)))
                        {
                            nextScore = currScores.get(currState) + edges.get(currState).get(nextState) + vertices.get(nextState).get(line.get(i));
                        }
                        else
                        {
                            nextScore = currScores.get(currState) + edges.get(currState).get(nextState) + unobservedValue;
                        }
                        //if nextState isn't in nextScores or nextScore > nextScores[nextState] replace nextState's score with nextScore
                        if (!nextScores.containsKey(nextState) || nextScore > nextScores.get(nextState)) {
                            nextScores.put(nextState, nextScore);

                            if (i >= prev.size()) {
                                HashMap<String, String> map = new HashMap<>();
                                map.put(nextState, currState);
                                prev.add(map);
                            }
                            else
                            {
                                prev.get(i).put(nextState, currState);
                            }
                        }
                    }
                }
            }
            currStates = nextStates;
            currScores = nextScores;
        }

        //get state with the highest final score
        String bestFinalState = currStates.get(0);
        Double maxValue = Double.NEGATIVE_INFINITY;
        Iterator iter = currScores.keySet().iterator();
        while(iter.hasNext())
        {
            String nextPOS = (String) iter.next();
            Double nextValue = currScores.get(nextPOS);
            if(nextValue > maxValue)
            {
                maxValue = nextValue;
                bestFinalState = nextPOS;
            }
        }

        //backtrack and fill in path
        ArrayList<String> path = new ArrayList<>();
        for(int i = prev.size() - 1; i >= 0; i--)
        {
            path.add(0, bestFinalState);
            bestFinalState = prev.get(i).get(bestFinalState);
        }

        return path;
    }

    /**
     * parses a training file and trains the POSTagger based on the data in the file
     * @param f the file containing the training data
     * @throws FileNotFoundException
     */
    public void train(File f) throws FileNotFoundException {
        Scanner inFile = new Scanner(f);
        HashMap<String, Double> totalTransitions = new HashMap<>();
        HashMap<String, Double> totalObservations = new HashMap<>();
        HashMap<String, HashMap<String, Double>> transitions = new HashMap<>();
        HashMap<String, HashMap<String, Double>> observations = new HashMap<>();

        while(inFile.hasNextLine())
        {
            String line = inFile.nextLine();
            if(line.length() > 0)
            {
                Scanner inLine = new Scanner(line);
                String[] currWordTag = inLine.next().split("/");
                String currWord = currWordTag[0];
                String currTag = currWordTag[1];
                //increment the total number of transitions from "#"
                if (totalTransitions.containsKey("#")) {
                    totalTransitions.put("#", totalTransitions.get("#") + 1);
                } else {
                    totalTransitions.put("#", 1.0);
                }
                //increment the number of transitions from "#" to currTag (process first observation in line)
                if(transitions.containsKey("#"))
                {
                    if(transitions.get("#").containsKey(currTag))
                    {
                        transitions.get("#").put(currTag, transitions.get("#").get(currTag) + 1.0);
                    }
                    else
                    {
                        transitions.get("#").put(currTag, 1.0);
                    }
                }
                else
                {
                    transitions.put("#", new HashMap<>());
                    transitions.get("#").put(currTag, 1.0);
                }
                //increment the total number of observations for currTag
                if(totalObservations.containsKey(currTag))
                {
                    totalObservations.put(currTag, totalObservations.get(currTag) + 1.0);
                }
                else
                {
                    totalObservations.put(currTag, 1.0);
                }

                //increment the number of observations at currTag for currWord
                if(observations.containsKey(currTag))
                {
                    if(observations.get(currTag).containsKey(currWord))
                    {
                        observations.get(currTag).put(currWord, observations.get(currTag).get(currWord) + 1.0);
                    }
                    else
                    {
                        observations.get(currTag).put(currWord, 1.0);
                    }
                }
                else
                {
                    observations.put(currTag, new HashMap<>());
                    observations.get(currTag).put(currWord, 1.0);
                }
                //for each observation in the line, increment totalTransitions, totalObservations, transitions and observations
                while(inLine.hasNext()) {
                    //get and save the next observation
                    String[] nextWordTag = inLine.next().split("/");
                    String nextWord = nextWordTag[0];
                    String nextTag = nextWordTag[1];

                    //increment the total number of transitions from currTag
                    if (totalTransitions.containsKey(currTag)) {
                        totalTransitions.put(currTag, totalTransitions.get(currTag) + 1);
                    } else {
                        totalTransitions.put(currTag, 1.0);
                    }

                    //increment the total number of observations for nextTag
                    if (totalObservations.containsKey(nextTag)) {
                        totalObservations.put(nextTag, totalObservations.get(nextTag) + 1.0);
                    } else {
                        totalObservations.put(nextTag, 1.0);
                    }

                    //increment the number of transitions from currTag to nextTag
                    if (transitions.containsKey(currTag)) {
                        if (transitions.get(currTag).containsKey(nextTag)) {
                            transitions.get(currTag).put(nextTag, transitions.get(currTag).get(nextTag) + 1.0);
                        } else {
                            transitions.get(currTag).put(nextTag, 1.0);
                        }
                    } else {
                        transitions.put(currTag, new HashMap<>());
                        transitions.get(currTag).put(nextTag, 1.0);
                    }

                    //increment the number of observations of nextWord for nextTag
                    if (observations.containsKey(nextTag)) {
                        if (observations.get(nextTag).containsKey(nextWord)) {
                            observations.get(nextTag).put(nextWord, observations.get(nextTag).get(nextWord) + 1.0);
                        } else {
                            observations.get(nextTag).put(nextWord, 1.0);
                        }
                    } else {
                        observations.put(nextTag, new HashMap<>());
                        observations.get(nextTag).put(nextWord, 1.0);
                    }
                    currTag = nextTag;
                }
            }
        }

        //create all vertices
        Iterator iter = observations.keySet().iterator();
        while(iter.hasNext())
        {
            String vertex = (String) iter.next();
            Iterator wordsIter = observations.get(vertex).keySet().iterator();
            HashMap<String, Double> wordsInVertex = new HashMap<>();
            while(wordsIter.hasNext())
            {
                String word = (String) wordsIter.next();
                //fill map with words and ln(observations of word / total observations for vertex)
                wordsInVertex.put(word, Math.log(observations.get(vertex).get(word) / totalObservations.get(vertex)));
            }
            vertices.put(vertex, wordsInVertex);
        }

        //create all edges
        iter = transitions.keySet().iterator();
        while(iter.hasNext())
        {
            String startingVertex = (String) iter.next();
            Iterator connectingVerticesIter = transitions.get(startingVertex).keySet().iterator();
            HashMap<String, Double> edgesFromVertex = new HashMap<>();
            while(connectingVerticesIter.hasNext())
            {
                String connectingVertex = (String) connectingVerticesIter.next();
                //fill map with connecting vertices and ln(transitions to connecting vertex / total transitions from vertex)
                edgesFromVertex.put(connectingVertex, Math.log(transitions.get(startingVertex).get(connectingVertex) / totalTransitions.get(startingVertex)));
            }
            edges.put(startingVertex, edgesFromVertex);
        }
    }
}
