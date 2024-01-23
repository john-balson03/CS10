import java.util.Comparator;

/**
 * Implements the Comparator interface for type CharFrequency. A CharFrequency is considered larger if integer is greater.
 */
public class TreeComparator implements Comparator {

    public TreeComparator()
    {

    }

    @Override
    public int compare(Object o1, Object o2) {
        BinaryTree<CharFrequency> bt1 = (BinaryTree<CharFrequency>) o1;
        BinaryTree<CharFrequency> bt2 = (BinaryTree<CharFrequency>) o2;
        if(bt1.getData().getI() < bt2.getData().getI())
        {
            return -1;
        }

        if(bt1.getData().getI() > bt2.getData().getI())
        {
            return 1;
        }
        return 0;
    }
}
