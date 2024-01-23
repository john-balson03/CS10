import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * A multi-segment Shape, with straight lines connecting "joint" points -- (x1,y1) to (x2,y2) to (x3,y3) ...
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2016
 * @author CBK, updated Fall 2016
 */
public class Polyline implements Shape {
	// TODO: YOUR CODE HERE
	private ArrayList<Point> points;
	private Color color;

	public Polyline(int startX, int startY, Color color)
	{
		points = new ArrayList<>();
		points.add(new Point(startX, startY));
		this.color = color;
	}

	/**
	 * Adds a point to the list of points, representing a new line segment from the previous entry in the list
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	public void addSegment(int x, int y)
	{
		points.add(new Point(x, y));
	}

	@Override
	public void moveBy(int dx, int dy) {
		for(Point point : points)
		{
			point.setLocation(point.getX() + dx, point.getY() + dy);
		}
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	public ArrayList<Point> getPoints()
	{
		return points;
	}
	
	@Override
	public boolean contains(int x, int y) {
		for(int i = 1; i < points.size(); i++)
		{
			//if the distance between a line segment and the point is 0
			if(Segment.pointToSegmentDistance(x, y, (int) points.get(i).getX(), (int) points.get(i).getY(), (int) points.get(i-1).getX(), (int) points.get(i-1).getY()) == 0)
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public void draw(Graphics g) {
		//draw a line segment for each of the consecutive points in points
		for(int i = 1; i < points.size(); i++)
		{
			g.drawLine(points.get(i).x, points.get(i).y, points.get(i-1).x, points.get(i-1).y);
		}
	}

	@Override
	public String toString() {
		String toReturn = "polyline "+color.getRGB();
		for(int i = 0; i < points.size(); i++)
		{
			toReturn += " " +points.get(i).x+" "+points.get(i).y;
		}
		return toReturn;
	}
}
