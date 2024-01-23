import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 * 
 * @author Chris Bailey-Kellogg, Winter 2014 (based on a very different structure from Fall 2012)
 * @author Travis W. Peters, Dartmouth CS 10, Updated Winter 2015
 * @author CBK, Spring 2015, updated for CamPaint
 */
public class RegionFinder {
	private static final int maxColorDiff = 20;				// how similar a pixel color must be to the target color, to belong to a region
	private static final int minRegion = 50; 				// how many points in a region to be worth considering

	private BufferedImage image;                            // the image in which to find regions
	private BufferedImage recoloredImage;                   // the image with identified regions recolored

	private ArrayList<ArrayList<Point>> regions;			// a region is a list of points
															// so the identified regions are in a list of lists of points

	public RegionFinder() {
		this.image = null;
	}

	public RegionFinder(BufferedImage image) {
		this.image = image;		
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}

	/**
	 * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
	 */
	public void findRegions(Color targetColor) {
		regions  = new ArrayList<ArrayList<Point>>();				//initialize regions
		BufferedImage visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);	//pixels that have been visited in image are set to RGB = 1
		ArrayList<Point> currentRegion;								//the current region being grown
		ArrayList<Point> colorMatch;								//points which match the target color whose neighbors need to be visited
		for(int y = 0; y < image.getHeight(); y++)					//iterate through all pixels in image
		{
			for(int x = 0; x < image.getWidth(); x++)
			{
				if(visited.getRGB(x, y) == 0)
				{
					visited.setRGB(x, y, 1);
					if (colorMatch(new Color(image.getRGB(x, y)), targetColor))
					{
						currentRegion = new ArrayList<>();
						colorMatch = new ArrayList<>();
						currentRegion.add(new Point(x, y));
						colorMatch.add(new Point(x, y));
						while(colorMatch.size() > 0)				//while there are points that match the target color whose neighbors have not been checked
						{
							Point currPoint = colorMatch.get(colorMatch.size() - 1);
							colorMatch.remove(colorMatch.size() - 1);
							for(int adjY = Math.max(currPoint.y - 1, 0); adjY <= Math.min(currPoint.y + 1, image.getHeight() - 1); adjY++)		//check all neighbors
							{
								for(int adjX = Math.max(currPoint.x - 1, 0); adjX <= Math.min(currPoint.x + 1, image.getWidth() - 1); adjX++)
								{
									if(visited.getRGB(adjX, adjY) == 0)
									{
										visited.setRGB(adjX, adjY, 1);
										if(colorMatch(new Color(image.getRGB(adjX, adjY)), targetColor))
										{
											colorMatch.add(new Point(adjX, adjY));
											currentRegion.add(new Point(adjX, adjY));
										}
									}
								}
							}
						}
						if(currentRegion.size() >= minRegion)			//if currentRegion meets minimum pixel threshold
						{
							regions.add(currentRegion);
						}
					}
				}
			}
		}
	}

	/**
	 * Tests whether the two colors are "similar enough" (your definition, subject to the maxColorDiff threshold, which you can vary).
	 */
	private static boolean colorMatch(Color c1, Color c2) {
		if(Math.abs(c1.getRed() - c2.getRed()) < maxColorDiff && Math.abs(c1.getGreen() - c2.getGreen()) < maxColorDiff && Math.abs(c1.getBlue() - c2.getBlue()) < maxColorDiff)
		{
			return true;
		}
		return false;
	}

	/**
	 * Returns the largest region detected (if any region has been detected)
	 */
	public ArrayList<Point> largestRegion() {
		if(regions.size() == 0)
		{
			return null;
		}
		ArrayList<Point> largest = new ArrayList<>();
		for(int i = 0; i < regions.size(); i++)			//go through regions and set largest to any region larger than itself
		{
			if(regions.get(i).size() > largest.size())
			{
				largest = regions.get(i);
			}
		}
		return largest;
	}

	/**
	 * Sets recoloredImage to be a copy of image, 
	 * but with each region a uniform random color, 
	 * so we can see where they are
	 */
	public void recolorImage() {
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
		// Now recolor the regions in it
		for(ArrayList<Point> region : regions)
		{
			int ran = (int) (Math.random() * 16777216);
			for(Point point : region)
			{
				recoloredImage.setRGB(point.x, point.y, ran);
			}
		}
	}
}
