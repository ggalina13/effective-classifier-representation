package constructionBuilding;

import java.util.ArrayList;
import java.util.List;

public class Group {
    public Group(int height, int width) {
        this.height = height;
        this.width = width;
    }

    private int height;
    private int width;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public static ArrayList<Group> groupsFromList(List<Integer> params) {
        ArrayList<Group> groups = new ArrayList<>();
        for (int i = 0; i + 1 < params.size(); i += 2) {
            groups.add(new Group(params.get(i), params.get(i + 1)));
        }
        return groups;
    }
}
