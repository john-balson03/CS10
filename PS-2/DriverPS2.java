
public class DriverPS2 {
    public static void main(String[] args) {
        PointQuadtree<Blob> points = new PointQuadtree<Blob>(new Blob(40, 40), 0, 0, 100, 100);
        points.insert(new Blob(20, 20));
        points.insert(new Blob(30, 10));
        points.insert(new Blob(70, 70));
        System.out.println(points.findInCircle(0, 0, 100));
    }
}
