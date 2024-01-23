import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * asks for an input sentence in the console and gives the predicted parts of speech using a POSTagger trained on brown data
 */
public class ConsoleTagger {
    static void main(String[] args) throws IOException
    {
        POSTagger p = new POSTagger();
        Scanner inLines = new Scanner(new File("PS-5/brown-train-sentences.txt"));
        Scanner inTags = new Scanner(new File("PS-5/brown-train-tags.txt"));
        BufferedWriter outFile = new BufferedWriter(new FileWriter("PS-5/brown-tagged-sentences.txt"));

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
        p.train(new File("PS-5/brown-tagged-sentences.txt"));

        //ask for user input from console, parse and return predicted parts of speech using viterbiDecode
        Scanner in = new Scanner(System.in);
        System.out.println("Please type line to be processed: ");
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
        System.out.println(line);
        System.out.println(p.viterbiDecode(line));
        in.close();
    }

}
