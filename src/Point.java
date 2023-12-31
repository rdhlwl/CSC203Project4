/**
 * A simple class representing a location in 2D space.
 */
public final class Point {
    public final int x;
    public final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean adjacent(Point p2) {
        return (x == p2.x && Math.abs(y - p2.y) == 1) || (y == p2.y && Math.abs(x - p2.x) == 1);
    }

    public boolean targetProximityEuclidean(Point targetPos){
        double dist = Math.sqrt((targetPos.y - y) * (targetPos.y - y) + (targetPos.x - x) * (targetPos.x - x));
        return (dist < 5.0);
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public boolean equals(Object other) {
        return other instanceof Point && ((Point) other).x == this.x && ((Point) other).y == this.y;
    }

    public int hashCode() {
        int result = 17;
        result = result * 31 + x;
        result = result * 31 + y;
        return result;
    }
    public double distance(Point end) {
        int xd = this.x - end.x;
        int yd = this.y - end.y;
        return Math.sqrt(xd*xd + yd*yd);
    }

    public double manhattanDistance(Point end) {
        int xd = Math.abs(this.x - end.x);
        int yd = Math.abs(this.y - end.y);
        return xd + yd;
    }
}
