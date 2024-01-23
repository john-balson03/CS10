/**
 * A CharFrequency stores both a Character and an Integer for use in class Compression.
 */
public class CharFrequency {
    private Character c;
    private Integer i;

    public CharFrequency(Character c, Integer i)
    {
        this.c = c;
        this.i = i;
    }

    public Character getC()
    {
        return c;
    }

    public Integer getI()
    {
        return i;
    }

    @Override
    public String toString() {
        return "CharFrequency{" +
                "c=" + c +
                ", i=" + i +
                '}';
    }
}
