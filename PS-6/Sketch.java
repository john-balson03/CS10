import java.awt.*;
import java.util.TreeMap;

/**
 * Stores a map of ids and shapes representing a sketch, and provides an addShape method for parsing strings and adding shapes to the list
 * @author johnbalson
 */
public class Sketch {
    private TreeMap<Integer, Shape> shapeIDs;
    private Integer nextID;

    public Sketch()
    {
        shapeIDs = new TreeMap<>();
        nextID = 0;
    }

    /**
     * Parses the input string and adds a shape, changes position of a shape, recolors a shape, or deletes a shape if specified
     * @param input
     */
    public synchronized void addShape(String input)
    {
        if(input == null)
        {
            return;
        }

        Integer id = -1;
        String[] s = input.split(" ");
        if(s[0] == null)
        {
            return;
        }

        if(s[0].equals("-1"))
        {
            if(!s[1].equals("delete") && !s[1].equals("recolor"))
            {
                id = nextID;
                nextID++;
            }
        }
        else
        {
            id = Integer.parseInt(s[0]);
        }

        if(s[1].equals("ellipse"))
        {
            shapeIDs.put(id, new Ellipse(Integer.parseInt(s[2]), Integer.parseInt(s[3]), Integer.parseInt(s[4]), Integer.parseInt(s[5]), new Color(Integer.parseInt(s[6]))));
        }
        else if(s[1].equals("rectangle"))
        {
            shapeIDs.put(id, new Rectangle(Integer.parseInt(s[2]), Integer.parseInt(s[3]), Integer.parseInt(s[4]), Integer.parseInt(s[5]), new Color(Integer.parseInt(s[6]))));
        }
        else if(s[1].equals("polyline"))
        {
            shapeIDs.put(id, new Polyline(Integer.parseInt(s[3]), Integer.parseInt(s[4]), new Color(Integer.parseInt(s[2]))));
            for(int i = 5; i < s.length - 1; i = i + 2)
            {
                ((Polyline)shapeIDs.get(id)).addSegment(Integer.parseInt(s[i]), Integer.parseInt(s[i+1]));
            }
        }
        else if(s[1].equals("segment"))
        {
            shapeIDs.put(id, new Segment(Integer.parseInt(s[2]), Integer.parseInt(s[3]), Integer.parseInt(s[4]), Integer.parseInt(s[5]), new Color(Integer.parseInt(s[6]))));
        }
        else if(s[1].equals("delete"))
        {
            if(id != -1)
            {
                shapeIDs.remove(id);
            }
        }
        else if(s[1].equals("recolor"))
        {
            if(id != -1)
            {
                shapeIDs.get(id).setColor(new Color(Integer.parseInt(s[2])));
            }
        }
    }

    public Integer contains(Integer x, Integer y)
    {
        for(Integer id : shapeIDs.descendingKeySet())
        {
            if(shapeIDs.get(id).contains(x, y))
            {
                return id;
            }
        }
        return -1;
    }

    public TreeMap<Integer, Shape> getShapeIDs()
    {
        return shapeIDs;
    }

}
