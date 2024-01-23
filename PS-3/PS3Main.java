import java.io.IOException;

public class PS3Main {
    public static void main(String[] args) throws IOException {
        BinaryTree<CharFrequency> binTree1 = Compression.compress("inputs/In.txt", "outputs/In_compressed.txt");
        Compression.decompress("outputs/In_compressed.txt", "outputs/In_decompressed.txt", binTree1);

        BinaryTree<CharFrequency> binTree2 = Compression.compress("inputs/USConstitution.txt", "outputs/USConstitution_compressed.txt");
        Compression.decompress("outputs/USConstitution_compressed.txt", "outputs/USConstitution_decompressed.txt", binTree2);
    }
}
