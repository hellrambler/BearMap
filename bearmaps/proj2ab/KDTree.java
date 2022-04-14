package bearmaps.proj2ab;

import java.util.List;

public class KDTree implements PointSet {

    private KDNode root;
    private Point target;
    private int ops;

    private class KDNode {
        private KDNode left;
        private KDNode right;
        private double x, y;

        public KDNode (double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    public KDTree(List<Point> points) {
        this.target = null;
        Point firstNode = points.remove(0);
        this.root = new KDNode(firstNode.getX(), firstNode.getY());

        for (Point p: points) {
            this.root = add(0, this.root, p);
        }

        points.add(firstNode);
    }

    private KDNode add(int lvl, KDNode curr, Point p) {
        if (curr == null) return new KDNode(p.getX(), p.getY());

        if (curr.x == p.getX() && curr.y == p.getY()) return curr;
        else if (lvl % 2 == 0 && p.getX() <= curr.x) {
            curr.left = add(lvl+1, curr.left, p);
        } else if (lvl % 2 == 0 && p.getX() > curr.x) {
            curr.right = add(lvl+1, curr.right, p);
        } else if (lvl % 2 != 0 && p.getY() <= curr.y) {
            curr.left = add(lvl+1, curr.left, p);
        } else if (lvl % 2 != 0 && p.getY() > curr.y) {
            curr.right = add(lvl+1, curr.right, p);
        }
        return curr;
    }

    public int getOps() {
        return this.ops;
    }

    @Override
    public Point nearest(double x, double y) {
        target = new Point(x, y);
        ops = 0;

        //xmin, ymin, xmax, ymax initialization
        double[] Bound = new double[] {Double.NEGATIVE_INFINITY,
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY};
        KDNode res = KDNearest(this.root, null, 0, Bound);
        return new Point(res.x, res.y);
    }

    private KDNode KDNearest(KDNode curr, KDNode currRes, int lvl, double[] Bound) {
        ops += 1;
        double currDist = currRes != null? distTarget(currRes): Double.POSITIVE_INFINITY;
        if (curr == null || Bound[0] - target.getX() > Math.sqrt(currDist) || Bound[1] - target.getY() > Math.sqrt(currDist) ||
                target.getX() - Bound[2] > Math.sqrt(currDist) || target.getY() - Bound[3] > Math.sqrt(currDist)) {
            return currRes;
        }

        if (distTarget(curr) < currDist) {
            currRes = curr;
        }

        if (lvl % 2 == 0) {
            if (target.getX() <= curr.x) {
                currRes = KDNearest(curr.left, currRes, lvl+1,
                        new double[]{Bound[0], Bound[1], curr.x, Bound[3]});
                currRes = KDNearest(curr.right, currRes, lvl+1,
                        new double[]{curr.x, Bound[1], Bound[2], Bound[3]});
            } else {
                currRes = KDNearest(curr.right, currRes, lvl+1,
                        new double[]{curr.x, Bound[1], Bound[2], Bound[3]});
                currRes = KDNearest(curr.left, currRes, lvl+1,
                        new double[]{Bound[0], Bound[1], curr.x, Bound[3]});
            }
        } else {
            if (target.getY() <= curr.y) {
                currRes = KDNearest(curr.left, currRes, lvl+1,
                    new double[]{Bound[0], Bound[1], Bound[2], curr.y});
                currRes = KDNearest(curr.right, currRes, lvl+1,
                        new double[]{Bound[0], curr.y, Bound[2], Bound[3]});
            } else {
                currRes = KDNearest(curr.right, currRes, lvl+1,
                    new double[]{Bound[0], curr.y, Bound[2], Bound[3]});
                currRes = KDNearest(curr.left, currRes, lvl+1,
                        new double[]{Bound[0], Bound[1], Bound[2], curr.y});
            }
        }

        return currRes;
    }

    private double distTarget(KDNode curr) {
        return Point.distance(target, new Point(curr.x, curr.y));
    }


    private KDNode traverseNearest(Point target, KDNode curr, KDNode currRes, double currDist) {
        if (Point.distance(target, new Point(curr.x, curr.y)) < currDist) {
            currDist = Point.distance(target, new Point(curr.x, curr.y));
            currRes = curr;
        }

        if (curr.left != null) {
            currRes = traverseNearest(target, curr.left, currRes, currDist);
            currDist = Point.distance(target, new Point(currRes.x, currRes.y));
        }

        if (curr.right != null) {
            currRes = traverseNearest(target, curr.right, currRes, currDist);
            currDist = Point.distance(target, new Point(currRes.x, currRes.y));
        }

        return currRes;
    }
}
