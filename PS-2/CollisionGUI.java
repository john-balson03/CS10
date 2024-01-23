import java.awt.*;

import javax.swing.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Using a quadtree for collision detection
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, updated for blobs
 * @author CBK, Fall 2016, using generic PointQuadtree
 */
public class CollisionGUI extends DrawingGUI {
	private static final int width=800, height=600;		// size of the universe

	private List<Blob> blobs;						// all the blobs
	private List<Blob> colliders;					// the blobs who collided at this step
	private char blobType = 'b';						// what type of blob to create
	private char collisionHandler = 'c';				// when there's a collision, 'c'olor them, or 'd'estroy them
	private int delay = 100;							// timer control

	public CollisionGUI() {
		super("super-collider", width, height);

		blobs = new ArrayList<Blob>();
		// Timer drives the animation.
		startTimer();
	}

	/**
	 * Adds an blob of the current blobType at the location
	 */
	private void add(int x, int y) {
		if (blobType=='b') {
			blobs.add(new Bouncer(x,y,width,height));
		}
		else if (blobType=='w') {
			blobs.add(new Wanderer(x,y));
		}
		else {
			System.err.println("Unknown blob type "+blobType);
		}
	}

	/**
	 * DrawingGUI method, here creating a new blob
	 */
	public void handleMousePress(int x, int y) {
		add(x,y);
		repaint();
	}

	/**
	 * tests edges of collisions between blobs
	 */
	private void test0()
	{
		colliders = new ArrayList<>();
		blobs = new ArrayList<>();
		blobs.add(new Blob(10, 10, 10));
		blobs.add(new Blob(10, 30, 10));
		blobs.add(new Blob(50, 10, 10));
		blobs.add(new Blob(50, 31, 10));
	}


	/**
	 * DrawingGUI method
	 */
	public void handleKeyPress(char k) {
		if (k == 'f') { // faster
			if (delay>1) delay /= 2;
			setTimerDelay(delay);
			System.out.println("delay:"+delay);
		}
		else if (k == 's') { // slower
			delay *= 2;
			setTimerDelay(delay);
			System.out.println("delay:"+delay);
		}
		else if (k == 'r') { // add some new blobs at random positions
			for (int i=0; i<10; i++) {
				add((int)(width*Math.random()), (int)(height*Math.random()));
				repaint();
			}			
		}
		else if (k == 'c' || k == 'd') { // control how collisions are handled
			collisionHandler = k;
			System.out.println("collision:"+k);
		}
		else if(k == '0') test0();
		else { // set the type for new blobs
			blobType = k;			
		}
	}

	/**
	 * DrawingGUI method, here drawing all the blobs and then re-drawing the colliders in red
	 */
	public void draw(Graphics g) {
		// Ask all the blobs to draw themselves.
		for(Blob blob : blobs)
		{
			g.fillOval((int)(blob.getX() - blob.getR()), (int)(blob.getY() - blob.getR()), (int)blob.getR() * 2, (int)blob.getR() * 2);
		}
		// Ask the colliders to draw themselves in red.
		if(colliders != null)
		{
			g.setColor(Color.red);
			for (Blob collider : colliders) {
				g.fillOval((int) (collider.getX() - collider.getR()), (int) (collider.getY() - collider.getR()), (int) collider.getR() * 2, (int) collider.getR() * 2);
			}
		}
	}

	/**
	 * Sets colliders to include all blobs in contact with another blob
	 */
	private void findColliders() {
		if(colliders == null) colliders = new ArrayList<>();
		// Create the tree
		PointQuadtree<Blob> blobsQuadtree = new PointQuadtree<>(blobs.get(0), 0, 0, 800, 600);
		for(Blob blob : blobs)
		{
			blobsQuadtree.insert(blob);
		}
		// For each blob, see if anybody else collided with it
		for(Blob blob : blobsQuadtree.allPoints())
		{
			ArrayList<Blob> blobsInCircle = (ArrayList<Blob>) blobsQuadtree.findInCircle(blob.getX(), blob.getY(), blob.getR() * 2);
			for(Blob blobInCircle : blobsInCircle)
			{
				if(blobInCircle != blob) colliders.add(blobInCircle);
			}
		}
	}

	/**
	 * DrawingGUI method, here moving all the blobs and checking for collisions
	 */
	public void handleTimer() {
		// Ask all the blobs to move themselves.
		for (Blob blob : blobs) {
			blob.step();
		}
		// Check for collisions
		if (blobs.size() > 0) {
			findColliders();
			if (collisionHandler=='d') {
				blobs.removeAll(colliders);
				colliders = null;
			}
		}
		// Now update the drawing
		repaint();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CollisionGUI();
			}
		});
	}
}
