import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * calculates how accurate the POSTagger is using known sentences
 */
public class FileTagTester {
    public static void main(String[] args) throws IOException {
        String sentenceFileName = "PS-5/simple-test-sentences.txt";
        String tagFileName = "PS-5/simple-test-tags.txt";

        POSTagger p = new POSTagger();
        Scanner inLines = new Scanner(new File("PS-5/simple-train-sentences.txt"));
        Scanner inTags = new Scanner(new File("PS-5/simple-train-tags.txt"));
        BufferedWriter outFile = new BufferedWriter(new FileWriter("PS-5/simple-tagged-sentences.txt"));

        //write words and tags separated by a slash in a new file
        while(inLines.hasNextLine())
        {
            String line = inLines.nextLine().toLowerCase().replaceAll("\\p{Punct}", "");
            String tagLine = inTags.nextLine().toUpperCase().replaceAll("\\p{Punct}", "");
            Scanner inLine = new Scanner(line);
            Scanner inTagLine = new Scanner(tagLine);
            while(inLine.hasNext() && inTagLine.hasNext())
            {
                outFile.write(inLine.next() + "/" + inTagLine.next() + " ");
            }
            outFile.newLine();
        }
        outFile.close();
        inTags.close();
        inLines.close();

        //train p on the file we filled
        p.train(new File("PS-5/simple-tagged-sentences.txt"));

        Integer numCorrectTags = 0;
        Integer totalTags = 0;
        Scanner in = new Scanner(new File(sentenceFileName));

        //fill correctTags with tags from tag file
        ArrayList<ArrayList<String>> correctTags = new ArrayList<>();
        inTags = new Scanner(new File(tagFileName));
        while(inTags.hasNextLine())
        {
            String tagLine = inTags.nextLine().toUpperCase().replaceAll("\\p{Punct}", "");
            Scanner inLine = new Scanner(tagLine);
            ArrayList lineTags = new ArrayList();
            while(inLine.hasNext())
            {
                lineTags.add(inLine.next());
            }
            correctTags.add(lineTags);
        }

        //fill tags using viterbiDecode method on each line
        ArrayList<ArrayList<String>> tags = new ArrayList<>();
        while(in.hasNextLine())
        {
            String nextLine = in.nextLine().toLowerCase().replaceAll("\\p{Punct}", "");
            ArrayList<String> line = new ArrayList<>();
            while(nextLine.length() > 0)
            {
                if(nextLine.contains(" "))
                {
                    line.add(nextLine.substring(0, nextLine.indexOf(" ")));
                    nextLine = nextLine.substring(nextLine.indexOf(" ") + 1);
                }
                else
                {
                    line.add(nextLine);
                    nextLine = "";
                }
            }
            ArrayList<String> lineTags = (ArrayList<String>) p.viterbiDecode(line);
            tags.add(lineTags);
        }

        //calculate number of total and correct tags
        for(int i = 0; i < Math.min(tags.size(), correctTags.size()); i++)
        {
            for(int j = 0; j < Math.min(tags.get(i).size(), correctTags.get(i).size()); j++)
            {
                totalTags++;
                if(tags.get(i).get(j).equals(correctTags.get(i).get(j)))
                {
                    numCorrectTags++;
                }
            }
        }
        System.out.println(numCorrectTags + "/" + totalTags + " tags were correct");
    }
}
