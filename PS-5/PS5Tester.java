import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class PS5Tester {
    public static void main(String[] args) throws FileNotFoundException {
        HashMap<String, HashMap<String, Double>> vertices = new HashMap<>();
        HashMap<String, HashMap<String, Double>> edges = new HashMap<>();

        vertices.put("#", new HashMap<>());

        HashMap<String, Double> map = new HashMap<>();
        map.put("jobs", -0.7);
        map.put("will", -0.7);
        vertices.put("NP", map);

        map = new HashMap<>();
        map.put("can", -0.7);
        map.put("will", -0.7);
        vertices.put("MOD", map);

        map = new HashMap<>();
        map.put("the", -1.0);
        vertices.put("DET", map);

        map = new HashMap<>();
        map.put("i", -1.9);
        vertices.put("PRO", map);

        vertices.put("VD", new HashMap<>());

        map = new HashMap<>();
        map.put("fish", -1.0);
        vertices.put("N", map);

        map = new HashMap<>();
        map.put("eats", -2.1);
        map.put("fish", -2.1);
        vertices.put("V", map);

        map = new HashMap<>();
        map.put("NP", -1.6);
        map.put("MOD", -2.3);
        map.put("PRO", -1.2);
        map.put("DET", -0.9);
        edges.put("#", map);

        map = new HashMap<>();
        map.put("VD", -0.7);
        map.put("V", -0.7);
        edges.put("NP", map);

        map = new HashMap<>();
        map.put("PRO", -0.7);
        map.put("V", -0.7);
        edges.put("MOD", map);

        map = new HashMap<>();
        map.put("N", 0.0);
        edges.put("DET", map);

        map = new HashMap<>();
        map.put("VD", -1.6);
        map.put("MOD", -1.6);
        map.put("V", -0.5);
        edges.put("PRO", map);

        map = new HashMap<>();
        map.put("PRO", -0.4);
        map.put("DET", -1.1);
        edges.put("VD", map);

        map = new HashMap<>();
        map.put("VD", -1.4);
        map.put("V", -0.3);
        edges.put("N", map);

        map = new HashMap<>();
        map.put("PRO", -1.9);
        map.put("DET", -0.2);
        edges.put("V", map);

        ArrayList<String> line = new ArrayList<>();
        line.add("will");
        line.add("eats");
        line.add("the");
        line.add("fish");

        POSTagger p = new POSTagger();
        p.edges = edges;
        p.vertices = vertices;
        System.out.println(p.viterbiDecode(line));

        System.out.println();

        p = new POSTagger();
        p.train(new File("PS-5/example-tagged-sentences"));
        Scanner s = new Scanner(new File("PS-5/example-sentences.txt"));
        while(s.hasNextLine())
        {
            String stringLine = s.nextLine();
            Scanner inLine = new Scanner(stringLine);
            line = new ArrayList<>();
            while(inLine.hasNext())
            {
                String word = inLine.next();
                line.add(word);
            }
        }
        System.out.println(p.vertices);
        System.out.println(p.edges);
    }
}
