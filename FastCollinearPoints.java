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

public class FastCollinearPoints {

    private final LineSegment[] segments;
    private final int paralel = 1;
    private final int sameLine = 2;
    private final int other = 3;
    private Point[] myPoints;

    public FastCollinearPoints(Point[] points) {

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

        List<List<LineSegmentClass>> segmentsSlopeOrigins = new ArrayList<>();

        for (int i = 0; i < myPoints.length; i++) {
            Point[] remainingPoints = removeTheElement(myPoints, i);
            segmentsSlopeOrigins.add(findLines(remainingPoints, myPoints[i]));
        }

        List<LineSegmentClass> lineSegmentClassArrayList = new ArrayList<>();

        for (List<LineSegmentClass> lineSegmentClasses : segmentsSlopeOrigins) {
            for (LineSegmentClass segmentAndSlope : lineSegmentClasses) {

                if (lineSegmentClassArrayList.isEmpty()) {
                    lineSegmentClassArrayList.add(segmentAndSlope);
                    continue;
                }

                ValidSegmentSlopeOrigin validSegmentSlopeOrigin =
                        getElement(lineSegmentClassArrayList, segmentAndSlope);

                switch (validSegmentSlopeOrigin.type) {
                    case paralel:
                        lineSegmentClassArrayList.add(segmentAndSlope);
                        break;
                    case sameLine:
                        if (validSegmentSlopeOrigin.lineSegmentClass.numPoints
                                < segmentAndSlope.numPoints) {
                            boolean deleted = lineSegmentClassArrayList
                                    .remove(validSegmentSlopeOrigin.lineSegmentClass);
                            lineSegmentClassArrayList.add(segmentAndSlope);
                        }
                        break;
                    case other:
                        lineSegmentClassArrayList.add(segmentAndSlope);
                        break;
                }
            }
        }

        List<LineSegment> finalSegments = new ArrayList<>();

        for (LineSegmentClass key : lineSegmentClassArrayList) {
            finalSegments.add(key.segment);
        }

        this.segments = finalSegments.toArray(LineSegment[]::new);
    }

    private List<LineSegmentClass> findLines(Point[] points, Point origin) {

        List<SlopeOriginPoints> slopeOriginPoints = new ArrayList<>();

        for (int i = 0; i < points.length; i++) {
            double slope = origin.slopeTo(points[i]);
            SlopeOrigin slopeOrigin = new SlopeOrigin(slope, origin);
            SlopeOriginPoints slopeOriginPoints1 = findSlopeOriginInList(slopeOriginPoints,
                                                                         slopeOrigin);
            if (slopeOriginPoints1 == null) {
                List<Point> pointList = new ArrayList<>();
                pointList.add(origin);
                pointList.add(points[i]);
                SlopeOriginPoints slopeOriginPoints2 = new SlopeOriginPoints(slopeOrigin,
                                                                             pointList);
                slopeOriginPoints.add(slopeOriginPoints2);
            }
            else {
                slopeOriginPoints1.points.add(points[i]);
            }
        }

        List<LineSegmentClass> lineSegmentList = new ArrayList<>();

        for (SlopeOriginPoints element : slopeOriginPoints) {

            if (element.points.size() >= 4) {
                List<Point> pointList = element.points;
                LineSegment segment = new LineSegment(element.points.get(0),
                                                      element.points.get(pointList.size() - 1));
                lineSegmentList
                        .add(new LineSegmentClass(segment, element.slopeOrigin, pointList.size()));
            }
        }

        return lineSegmentList;
    }

    private boolean isParallel(LineSegmentClass e1, LineSegmentClass e2) {

        if (Double.compare(e1.slopeOrigin.slope, e2.slopeOrigin.slope) == 0) {
            double slope = e1.slopeOrigin.origin.slopeTo(e2.slopeOrigin.origin);
            if (Double.compare(slope, e1.slopeOrigin.slope) != 0)
                return true;
        }

        return false;
    }

    private boolean isSameSegment(LineSegmentClass e1, LineSegmentClass e2) {

        if (Double.compare(e1.slopeOrigin.slope, e2.slopeOrigin.slope) == 0) {
            double slope = e1.slopeOrigin.origin.slopeTo(e2.slopeOrigin.origin);
            if (Double.compare(slope, e2.slopeOrigin.slope) == 0) {
                return true;
            }
        }

        return false;
    }

    private ValidSegmentSlopeOrigin getElement(List<LineSegmentClass> list,
                                               LineSegmentClass lineSegmentClass) {

        ParallelSameClass parallelSameClass = new ParallelSameClass();

        for (LineSegmentClass element : list) {

            if (isSameSegment(element, lineSegmentClass)) {
                parallelSameClass.isSame = true;
                break;
            }
            if (isParallel(element, lineSegmentClass)) {
                parallelSameClass.isParallel = true;
            }
        }

        if (parallelSameClass.isSame) {
            return new ValidSegmentSlopeOrigin(lineSegmentClass, sameLine);
        }
        else if (parallelSameClass.isParallel) {
            return new ValidSegmentSlopeOrigin(lineSegmentClass, paralel);
        }

        return new ValidSegmentSlopeOrigin(lineSegmentClass, other);
    }

    private static SlopeOriginPoints findSlopeOriginInList(List<SlopeOriginPoints> list,
                                                           SlopeOrigin slopeOrigin) {
        return list.stream()
                   .filter(item -> Double.compare(item.slopeOrigin.slope, slopeOrigin.slope) == 0
                           && item.slopeOrigin.origin.compareTo(slopeOrigin.origin) == 0)
                   .findFirst()
                   .orElse(null);
    }

    private static Point[] removeTheElement(Point[] arr, int index) {
        if (arr == null || index < 0 || index >= arr.length) {
            return arr;
        }

        Point[] anotherArray = new Point[arr.length - index - 1];

        int k = 0;
        for (int i = 0; i < arr.length; i++) {
            if (i <= index) {
                continue;
            }
            anotherArray[k++] = arr[i];
        }

        return anotherArray;
    }

    public int numberOfSegments() {
        return segments.length;
    }

    public LineSegment[] segments() {
        return segments.clone();
    }

    private class ParallelSameClass {
        boolean isParallel;
        boolean isSame;
        boolean isOther = true;
    }

    private class ValidSegmentSlopeOrigin {

        LineSegmentClass lineSegmentClass;
        int type;

        public ValidSegmentSlopeOrigin(LineSegmentClass lineSegmentClass, int type) {
            this.lineSegmentClass = lineSegmentClass;
            this.type = type;
        }
    }

    public static void main(String[] args) {
        In in = new In("input50.txt");
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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }

    private class SlopeOriginPoints {
        public SlopeOrigin slopeOrigin;
        public List<Point> points = new ArrayList<>();

        public SlopeOriginPoints(SlopeOrigin slopeOrigin, List<Point> points) {
            this.slopeOrigin = slopeOrigin;
            this.points = points;
        }
    }

    private class LineSegmentClass {

        public LineSegment segment;
        public SlopeOrigin slopeOrigin;
        public int numPoints;

        public LineSegmentClass(LineSegment segment, SlopeOrigin slopeOrigin, int numPoints) {
            this.segment = segment;
            this.slopeOrigin = slopeOrigin;
            this.numPoints = numPoints;
        }
    }

    private class SlopeOrigin {

        public Point origin;
        public double slope;

        public SlopeOrigin(Double slope, Point origin) {
            this.slope = slope;
            this.origin = origin;
        }
    }
}
