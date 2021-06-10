package constructionBuilding;

import java.util.List;

public class ClassifierGroupsInfo {
    private final List<Integer> groupProportions;
    private final int rulesInGroupsCount;
    private final int rulesLeftCartesianProductSize;

    public ClassifierGroupsInfo(List<Integer> groupProportions, int rulesInGroupsCount, int rulesLeftCartesianProductSize) {
        this.groupProportions = groupProportions;
        this.rulesInGroupsCount = rulesInGroupsCount;
        this.rulesLeftCartesianProductSize = rulesLeftCartesianProductSize;
    }

    public List<Integer> getGroupProportions() {
        return groupProportions;
    }

    public int getRulesInGroupsCount() {
        return rulesInGroupsCount;
    }

    public int getrulesLeftCartesianProductSize() {
        return rulesLeftCartesianProductSize;
    }
}
