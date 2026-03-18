package edu.luc.etl.cs313.android.shapes.model;

/**
 * A shape visitor for calculating the bounding box, that is, the smallest
 * rectangle containing the shape. The resulting bounding box is returned as a
 * rectangle at a specific location.
 */
public class BoundingBox implements Visitor<Location> {

    // TODO entirely your job (except onCircle)

    @Override
    public Location onCircle(final Circle c) {
        final int radius = c.getRadius();
        return new Location(-radius, -radius, new Rectangle(2 * radius, 2 * radius));
    }

    @Override
    public Location onFill(final Fill f) {
        return f.getShape().accept(this);
    }

    @Override
    public Location onGroup(final Group g) {
        if (g.getShapes().isEmpty()) {
            return new Location(0, 0, new Rectangle(0, 0));
        }

        Location first = g.getShapes().get(0).accept(this);

        int minX = first.getX();
        int minY = first.getY();
        int maxX = minX + ((Rectangle) first.getShape()).getWidth();
        int maxY = minY + ((Rectangle) first.getShape()).getHeight();

        for (int i = 1; i < g.getShapes().size(); i++) {
            Location box = g.getShapes().get(i).accept(this);

            int x1 = box.getX();
            int y1 = box.getY();
            int x2 = x1 + ((Rectangle) box.getShape()).getWidth();
            int y2 = y1 + ((Rectangle) box.getShape()).getHeight();

            minX = Math.min(minX, x1);
            minY = Math.min(minY, y1);
            maxX = Math.max(maxX, x2);
            maxY = Math.max(maxY, y2);
        }

        return new Location(minX, minY,
                new Rectangle(maxX - minX, maxY - minY));
    }

    @Override
    public Location onLocation(final Location l) {
        Location childBox = l.getShape().accept(this);

        int x = l.getX() + childBox.getX();
        int y = l.getY() + childBox.getY();

        return new Location(x, y, childBox.getShape());
    }

    @Override
    public Location onRectangle(final Rectangle r) {
        return new Location(0, 0,
                new Rectangle(r.getWidth(), r.getHeight()));
    }

    @Override
    public Location onStrokeColor(final StrokeColor c) {
        return null;
    }

    @Override
    public Location onOutline(final Outline o) {
        return o.getShape().accept(this);
    }

    @Override
    public Location onPolygon(final Polygon s) {
        return onGroup(s);
    }
}
