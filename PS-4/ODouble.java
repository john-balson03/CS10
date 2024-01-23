/**
 * Simple class with some functionality of a double, used to save data when using recursion in GraphLibrary.java (ObjectDouble)
 */
public class ODouble {
    private double d;

    public ODouble(double d)
    {
        this.d = d;
    }

    public void add(double d)
    {
        this.d = this.d + d;
    }

    public void set(double d)
    {
        this.d = d;
    }

    public double get()
    {
        return d;
    }
}
