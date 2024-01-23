import java.io.*;
import java.util.*;

/**
 * static method which takes an input and output file as parameters and compresses the input file and prints the compressed version in the output file
 */
public class Compression {
    public static BinaryTree<CharFrequency> compress(String inputFilePath, String outputFilePath)
    {
        //create and fill frequency table
        HashMap<Character, Integer> charFrequencyMap = findFrequencies(inputFilePath);

        //put initial trees in priority queue
        PriorityQueue<BinaryTree<CharFrequency>> charFrequencyTreeQueue = new PriorityQueue<BinaryTree<CharFrequency>>(1, new TreeComparator());
        CharFrequency tempCF;
        Character tempChar;
        Integer tempInt;
        Iterator<Character> keyIterator = charFrequencyMap.keySet().iterator();
        Iterator<Integer> frequencyIterator = charFrequencyMap.values().iterator();
        for(int i = 0; i < charFrequencyMap.size(); i++)
        {
            tempChar = keyIterator.next();
            tempInt = frequencyIterator.next();
            tempCF = new CharFrequency(tempChar, tempInt);
            charFrequencyTreeQueue.add(new BinaryTree<>(tempCF));
        }

        //tree creation
        BinaryTree<CharFrequency> allCFs = createTree(charFrequencyTreeQueue);

        //code retrieval
        HashMap<Character, String> charCodes = new HashMap<>();
        fillCharCodeHashMap(allCFs, charCodes, "1");

        //compression
        compressFile(inputFilePath, outputFilePath, charCodes);

        return allCFs;
    }

    /**
     * decompresses a given input file and prints the decompressed version in an output file
     * @param inputFile the file to be decompressed
     * @param outputFile the file to write then decompressed version into
     * @param tree the tree used to compress the file
     * @throws IOException
     */
    public static void decompress(String inputFile, String outputFile, BinaryTree<CharFrequency> tree) throws IOException
    {
        BufferedBitReader bitInput;
        try {
            bitInput = new BufferedBitReader(inputFile);
        }
        catch (IOException e)
        {
            System.out.println("filepath: " + inputFile + " does not exist");
            return;
        }
        try {
            BufferedWriter output = new BufferedWriter(new FileWriter(outputFile));
            BinaryTree<CharFrequency> tempTree;
            if(tree != null) {
                //while there are more bits to be processed
                while (bitInput.hasNext()) {
                    //first bit of each code is always a 1, discard this
                    bitInput.readBit();
                    tempTree = tree;
                    //work down the tree until we get to a leaf
                    while (tempTree.isInner()) {
                        if (bitInput.readBit() == true) {
                            tempTree = tempTree.getRight();
                        } else {
                            tempTree = tempTree.getLeft();
                        }
                    }
                    //write character to output file
                    output.write(tempTree.getData().getC());
                }
            }
            bitInput.close();
            output.close();
        }
        catch(IOException e)
        {
            System.out.println("filepath: " + outputFile + " does not exist");
        }
        finally {
                bitInput.close();
        }
    }

    /**
     * compress helper method, finding and creating a map of all characters in a file onto their frequencies
     * @param inputFile
     * @return
     * @throws IOException
     */
    private static HashMap<Character, Integer> findFrequencies(String inputFile)
    {
        HashMap<Character, Integer> charFrequencies = new HashMap<>();
        try
        {
            BufferedReader input = new BufferedReader(new FileReader(inputFile));
            Character currChar;
            //while there are more characters in the file
            while (input.ready()) {
                currChar = (char) input.read();
                //if the character has already been found, increment the count
                if (charFrequencies.containsKey(currChar)) {
                    charFrequencies.replace(currChar, charFrequencies.get(currChar) + 1);
                } else {
                    charFrequencies.put(currChar, 1);
                }
            }
            input.close();
        }
        catch (IOException e)
        {
            System.out.println();
        }
        return charFrequencies;
    }

    /**
     * Compress helper method, creating and returning a tree containing all CharFrequencies
     * @param charFrequencyTreeQueue the queue to holding CharFrequency Binary Trees
     */
    private static BinaryTree<CharFrequency> createTree(PriorityQueue<BinaryTree<CharFrequency>> charFrequencyTreeQueue)
    {
        BinaryTree tempBT1;
        BinaryTree tempBT2;

        //repeat until 1 tree remains
        while(charFrequencyTreeQueue.size() > 1)
        {
            //Remove smallest 2 elements of charFrequencyQueue and store them temporarily
            tempBT1 = charFrequencyTreeQueue.poll();
            tempBT2 = charFrequencyTreeQueue.poll();
            //Create a new entry in charFrequencyTreeQueue with Integer value of the sum of tempBT1 and tempBT2 Integers and childern tempBT1 and tempBT2
            charFrequencyTreeQueue.add(new BinaryTree<CharFrequency>(new CharFrequency('a', ((CharFrequency) tempBT1.getData()).getI() + ((CharFrequency) tempBT2.getData()).getI()), tempBT1, tempBT2));
        }
        return charFrequencyTreeQueue.poll();
    }

    /**
     * recursive compress helper method, filling map charCodes with leaves from tree
     * @param cfTree
     * @param charCodes
     * @param code
     */
    private static void fillCharCodeHashMap(BinaryTree<CharFrequency> cfTree, HashMap<Character, String> charCodes, String code)
    {
        if(cfTree == null) return;
        //if cfTree is a leaf add it to the map
        if(cfTree.isLeaf())
        {
            charCodes.put(cfTree.getData().getC(), code);
        }
        //else move on to its children
        else
        {
            if(cfTree.hasLeft())
            {
                fillCharCodeHashMap(cfTree.getLeft(), charCodes, code + "0");
            }
            if(cfTree.hasRight())
            {
                fillCharCodeHashMap(cfTree.getRight(), charCodes, code + "1");
            }
        }
    }

    /**
     * compress helper method, compressing the input file and writeing the compressed version to the output file
     * @param inputFile
     * @param outputFile
     * @param charCodes
     */
    private static void compressFile(String inputFile, String outputFile, HashMap<Character, String> charCodes)
    {
        BufferedReader input;
        try
        {
           input = new BufferedReader(new FileReader(inputFile));
        }
        catch(IOException e)
        {
            System.out.println("filepath: " + inputFile + " does not exist");
            return;
        }
        try
        {
            BufferedBitWriter bitOutput = new BufferedBitWriter(outputFile);

            String code;
            while(input.ready())
            {
                code = charCodes.get((char) input.read());
                while(code.length() > 0)
                {
                    if(code.charAt(0) == '0')
                    {
                        bitOutput.writeBit(false);
                    }
                    else if(code.charAt(0) == '1')
                    {
                        bitOutput.writeBit(true);
                    }
                    code = code.substring(1);
                }
            }
            input.close();
            bitOutput.close();
        }
        catch(IOException e)
        {
            System.out.println("filepath: " + outputFile + " does not exist");
        }
    }
}
