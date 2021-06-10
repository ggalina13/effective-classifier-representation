package constructionBuilding;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class StaircasePlacement {
    private final List<Point> corners;
    private final List<Rectangle> rectangles;
    private LinkedList<Integer> bestIndependentsSets = null;
    private int width_given;

    public StaircasePlacement(List<Point> corners, List<Rectangle> rectangles, int width_given) {
        this.corners = corners;
        this.rectangles = rectangles;
        this.width_given = width_given;
    }

    public LinkedList<Integer> getBestIndependentsSets() {
        return bestIndependentsSets;
    }

    public void setBestIndependentsSets(LinkedList<Integer> bestIndependentsSets) {
        this.bestIndependentsSets = bestIndependentsSets;
    }

    public int getWidth_given() {
        return width_given;
    }

    public void setWidth_given(int width_given) {
        this.width_given = width_given;
    }

    public int getHeight() {
        Optional<Point> highest_corner = corners.stream().max(Comparator.comparingInt(Point::getY));
        return highest_corner.map(Point::getY).orElse(0);
    }

    public List<Point> getCorners() {
        return corners;
    }

    public List<Rectangle> getRectangles() {
        return rectangles;
    }
}
