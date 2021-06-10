package constructionBuilding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Visited {
    private int best_height = Integer.MAX_VALUE;
    ArrayList<Visit> visited = new ArrayList<>();

    public void addVisit(int group_mask, List<Point> corners) {
        visited.add(new Visit(group_mask, corners));
    }

    public ArrayList<Visit> getVisited() {
        return visited;
    }

    public void setVisited(ArrayList<Visit> visited) {
        this.visited = visited;
    }

    public int getBest_height() {
        return best_height;
    }

    public void maybeUpdateBestHeight(int best_height) {
        if (this.best_height > best_height) {
            //System.out.println("new best: " + best_height);
            this.best_height = best_height;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Visited visited1 = (Visited) o;
        return Objects.equals(visited, visited1.visited);
    }
}
