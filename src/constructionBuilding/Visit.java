package constructionBuilding;

import java.util.List;
import java.util.Objects;

public class Visit {
    private int group_mask;
    private List<Point> corners;

    public Visit(int group_mask, List<Point> corners) {
        this.group_mask = group_mask;
        this.corners = corners;
    }

    public int getGroup_mask() {
        return group_mask;
    }

    public void setGroup_mask(int group_mask) {
        this.group_mask = group_mask;
    }

    public List<Point> getCorners() {
        return corners;
    }

    public void setCorners(List<Point> corners) {
        this.corners = corners;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Visit visit = (Visit) o;
        return group_mask == visit.group_mask && Objects.equals(corners, visit.corners);
    }
}
