/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BruteCollinearPoints {

    private final LineSegment[] segments;
    private Point[] myPoints;

    public BruteCollinearPoints(Point[] points) {
        if (points == null) throw new IllegalArgumentException("Array points is null");
        myPoints = new Point[points.length];
        for (int i = 0; i < points.length; i++) myPoints[i] = points[i];
        for (int i = 0; i < myPoints.length; i++) {
            if (myPoints[i] == null)
                throw new IllegalArgumentException("Point is null");
        }

        Arrays.sort(myPoints);

        for (int i = 0; i < myPoints.length - 1; i++) {
            if (myPoints[i].compareTo(myPoints[i + 1]) == 0)
                throw new IllegalArgumentException("Repeated points");
        }

        List<LineSegment> lineSegments = new ArrayList<>();


        for (int i = 0; i < myPoints.length; i++) {
            for (int j = i + 1; j < myPoints.length; j++) {
                for (int k = j + 1; k < myPoints.length; k++) {
                    for (int p = k + 1; p < myPoints.length; p++) {
                        double slope1 = myPoints[i].slopeTo(myPoints[j]);
                        double slope2 = myPoints[j].slopeTo(myPoints[k]);
                        double slope3 = myPoints[k].slopeTo(myPoints[p]);
                        if (Double.compare(slope1, slope2) == 0
                                && Double.compare(slope2, slope3) == 0) {
                            LineSegment line = new LineSegment(myPoints[i], myPoints[p]);
                            lineSegments.add(line);
                        }
                    }
                }
            }
        }

        this.segments = lineSegments.toArray(LineSegment[]::new);
    }

    public static void main(String[] args) {
        // read the n points from a file
        In in = new In("input8.txt");
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }

    public int numberOfSegments() {
        return segments.length;
    }

    public LineSegment[] segments() {
        return segments.clone();
    }
}
