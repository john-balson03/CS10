import java.util.ArrayList;
import java.util.List;

/**
 * A point quadtree: stores an element at a 2D position, 
 * with children at the subdivided quadrants.
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015.
 * @author CBK, Spring 2016, explicit rectangle.
 * @author CBK, Fall 2016, generic with Point2D interface.
 * 
 */
public class PointQuadtree<E extends Point2D> {
	private E point;							// the point anchoring this node
	private int x1, y1;							// upper-left corner of the region
	private int x2, y2;							// bottom-right corner of the region
	private PointQuadtree<E> c1, c2, c3, c4;	// children

	/**
	 * Initializes a leaf quadtree, holding the point in the rectangle
	 */
	public PointQuadtree(E point, int x1, int y1, int x2, int y2) {
		this.point = point;
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
	}

	// Getters
	
	public E getPoint() {
		return point;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	/**
	 * Returns the child (if any) at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public PointQuadtree<E> getChild(int quadrant) {
		if (quadrant==1) return c1;
		if (quadrant==2) return c2;
		if (quadrant==3) return c3;
		if (quadrant==4) return c4;
		return null;
	}

	/**
	 * Returns whether or not there is a child at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public boolean hasChild(int quadrant) {
		return (quadrant==1 && c1!=null) || (quadrant==2 && c2!=null) || (quadrant==3 && c3!=null) || (quadrant==4 && c4!=null);
	}

	/**
	 * Inserts the point into the tree
	 */
	public void insert(E p2) {
		if(p2.getX() < point.getX() && p2.getY() < point.getY())		//if p2 is in 1st quadrant
		{
			if(hasChild(1))
			{
				getChild(1).insert(p2);
			}
			else
			{
				c1 = new PointQuadtree<E>(p2, getX1(), getY1(), (int) point.getX(), (int) point.getY());
			}
		}
		else if(p2.getX() >= point.getX() && p2.getY() < point.getY())		//if p2 is in 2nd quadrant
		{
			if(hasChild(2))
			{
				getChild(2).insert(p2);
			}
			else
			{
				c2 = new PointQuadtree<E>(p2, (int)point.getX(), getY1(), getX2(), (int) point.getY());
			}
		}
		else if(p2.getX() < point.getX() && p2.getY() >= point.getY())		//if p2 is in 3rd quadrant
		{
			if(hasChild(3))
			{
				getChild(3).insert(p2);
			}
			else
			{
				c3 = new PointQuadtree<E>(p2, getX1(), (int)point.getY(), (int)point.getX(), getY2());
			}
		}
		else if(p2.getX() >= point.getX() && p2.getY() >= point.getY())		//if p2 is in 4th quadrant
		{
			if(hasChild(4))
			{
				getChild(4).insert(p2);
			}
			else
			{
				c4 = new PointQuadtree<E>(p2, (int)point.getX(), (int)point.getY(), getX2(), getY2());
			}
		}
	}
	
	/**
	 * Finds the number of points in the quadtree (including its descendants)
	 */
	public int size() {
		int size = 1;
		if(hasChild(1))
		{
			size = size + c1.size();
		}
		if(hasChild(2))
		{
			size = size + c2.size();
		}
		if(hasChild(3))
		{
			size = size + c3.size();
		}
		if(hasChild(4))
		{
			size = size + c4.size();
		}
		return size;
	}
	
	/**
	 * Builds a list of all the points in the quadtree (including its descendants)
	 */
	public List<E> allPoints() {
		ArrayList<E> allPoints = new ArrayList<E>();
		allPoints.add(getPoint());
		if(hasChild(1))
		{
			for(E point : c1.allPoints())
			{
				allPoints.add(point);
			}
		}
		if(hasChild(2))
		{
			for(E point : c2.allPoints())
			{
				allPoints.add(point);
			}
		}
		if(hasChild(3))
		{
			for(E point : c3.allPoints())
			{
				allPoints.add(point);
			}
		}
		if(hasChild(4))
		{
			for(E point : c4.allPoints())
			{
				allPoints.add(point);
			}
		}
		return allPoints;
	}	

	/**
	 * Uses the quadtree to find all points within the circle
	 * @param cx	circle center x
	 * @param cy  	circle center y
	 * @param cr  	circle radius
	 * @return    	the points in the circle (and the qt's rectangle)
	 */
	public List<E> findInCircle(double cx, double cy, double cr) {
		ArrayList<E> pointsInCircle = new ArrayList<>();
		findInCircleHelper(cx,cy, cr, pointsInCircle);
		return pointsInCircle;
	}

	/**
	 * Helper method for findInCircle
	 * @param cx circle center x
	 * @param cy circle center x
	 * @param cr circle radius
	 * @param pointsInCircle ArrayList from findInCircle
	 */
	private void findInCircleHelper(double cx, double cy, double cr, ArrayList<E> pointsInCircle)
	{
		if (Geometry.circleIntersectsRectangle(cx, cy, cr, x1, y1, x2, y2))
		{
			if (Geometry.pointInCircle(point.getX(), point.getY(), cx, cy, cr)) pointsInCircle.add(getPoint());
			if (hasChild(1)) getChild(1).findInCircleHelper(cx, cy, cr, pointsInCircle);
			if (hasChild(2)) getChild(2).findInCircleHelper(cx, cy, cr, pointsInCircle);
			if (hasChild(3)) getChild(3).findInCircleHelper(cx, cy, cr, pointsInCircle);
			if (hasChild(4)) getChild(4).findInCircleHelper(cx, cy, cr, pointsInCircle);
		}
	}


}
