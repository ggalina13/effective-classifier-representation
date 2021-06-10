package constructionBuilding;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Construction {
    public Construction(List<Integer> masks, int height, int width_given, ArrayList<Group> groups) {
        this.masks = masks;
        this.height = height;
        this.width_given = width_given;
        this.groups = groups;
    }

    private List<Integer> masks;
    private int width;
    private int height;
    private int width_given;
    private ArrayList<Group> groups;

    public void setIdColumnSizes(List<Integer> idColumnSizes) {
        this.idColumnSizes = idColumnSizes;
    }

    public List<Integer> getIdColumnSizes() {
        return idColumnSizes;
    }

    private List<Integer> idColumnSizes;

    public List<Integer> getMasks() {
        return masks;
    }

    public void setMasks(LinkedList<Integer> masks) {
        this.masks = masks;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }

    public int getWidth_given() {
        return width_given;
    }

    public void setWidth_given(int width_given) {
        this.width_given = width_given;
    }
}
