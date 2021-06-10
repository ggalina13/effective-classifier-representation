package constructionBuilding;

import java.util.*;
import java.util.stream.Collectors;

public class StaircasePlacementBuilder {
    public static int build(List<Integer> args, String paintFilePath) {
        System.out.println(paintFilePath);
        if ((args.size() < 2) || (args.size() % 2 == 0))
            throw new IllegalArgumentException("Format is \"width group_count hi wi ...\"");
        int width = args.get(0);
        ArrayList<Group> groups = Group.groupsFromList(args.subList(1, args.size()));
        int groupCount = groups.size();

        //create exact staircase placement
        StaircasePlacement staircasePlacement = findPlacementWithBestColumnCount(groupCount, groups, width);

        if (staircasePlacement == null) {
            System.out.println("Construction is not found");
            return -1;
        }

        paintPlacement(staircasePlacement, paintFilePath);
        return staircasePlacement.getHeight();
    }

    static StaircasePlacement findPlacementWithBestColumnCount(int groupCount, List<Group> groups, int width) {
        StaircasePlacement best = null;
        for (int x = 0; x <= groupCount; x++) {
            List<Point> corners = new ArrayList<>();
            corners.add(new Point(0, 0));
            StaircasePlacement staircasePlacement = findPlacement(groups, corners, 0, width - groupCount + x, new ArrayList<>(), new Visited(), 0);
            ArrayList<Set<Integer>> independent = new ArrayList<>();
            if (staircasePlacement == null)
                continue;
            ArrayList<Rectangle> rectangles = (ArrayList<Rectangle>) staircasePlacement.getRectangles();
            for (int i = 0; i < rectangles.size(); i++) {
                independent.add(new HashSet<>());
            }
            for (int i = 0; i < (rectangles.size() + 1) / 2; i++) {
                for (int j = i + 1; j < rectangles.size(); j++) {
                    if (independent(rectangles.get(i), rectangles.get(j))) {
                        independent.get(i).add(j);
                        independent.get(j).add(i);
                    }
                }
            }
            Pair<LinkedList<Integer>, Integer> independentSets = hasIndependentSets(groupCount - x,
                    groupCount, independent);
            if (independentSets != null && (best == null || (staircasePlacement.getHeight() <= best.getHeight()))) {
                best = staircasePlacement;
                best.setBestIndependentsSets(independentSets.getFirst());
                best.setWidth_given(best.getWidth_given() + independentSets.getSecond());
            }
        }
        return best;
    }

    private static Pair<LinkedList<Integer>, Integer> hasIndependentSets(int columnCount, int groupCount,
                                                                         ArrayList<Set<Integer>> independent) {
        return tryMakeSets(0, groupCount, 0, new LinkedList<>(List.of(0)),
                0, columnCount, independent);
    }

    private static Pair<LinkedList<Integer>, Integer> tryMakeSets(int curInd, int groupCount, int takenMask,
                                                                  LinkedList<Integer> curMasks, int curColumnCount,
                                                                  int maxColumnCount, ArrayList<Set<Integer>> independent) {
        if (curInd == groupCount) {
            int curMask = curMasks.get(curMasks.size() - 1);
            takenMask |= curMask;
            if (curMask == 0) {
                return null;
            }
            if (takenMask == Math.pow(2, groupCount) - 1) {
                //all groups are placed, return construction
                return new Pair<>((LinkedList<Integer>) curMasks.clone(), maxColumnCount - curColumnCount);
            }
            //not all groups are placed, start new set
            curMasks.add(0);
            Pair<LinkedList<Integer>, Integer> sets = tryMakeSets(0, groupCount, takenMask,
                    curMasks, curColumnCount, maxColumnCount, independent);
            curMasks.removeLast();
            return sets;
        }
        //don't take group number i in stripe
        Pair<LinkedList<Integer>, Integer> sets1 = tryMakeSets(curInd + 1, groupCount,
                takenMask, curMasks, curColumnCount, maxColumnCount, independent);
        if (sets1 != null)
            return sets1;
        //group is not taken
        Pair<LinkedList<Integer>, Integer> sets2 = null;
        if (((takenMask & (1 << curInd)) == 0)) {
            //group is independent with others
            int curMask = curMasks.get(curMasks.size() - 1);
            int curSize = 0;
            for (int i = 0; i < groupCount; i++) {
                if ((curMask & (1 << i)) != 0) {
                    curSize += 1;
                    //i is in this set, check if i is independent with curInd
                    if (!independent.get(i).contains(curInd)) {
                        return sets1;
                    }
                }
            }
            //curInd independent with cur set, add it
            int newColumnCount = curColumnCount;
            if (isPowerOf2(curSize + 1)) {
                newColumnCount += 1;
            }
            if (newColumnCount > maxColumnCount) {
                return null;
            }
            curMasks.set(curMasks.size() - 1, curMasks.get(curMasks.size() - 1) | (1 << curInd));
            sets2 = tryMakeSets(curInd + 1, groupCount, takenMask,
                    curMasks, newColumnCount, maxColumnCount, independent);
            curMasks.set(curMasks.size() - 1, curMasks.get(curMasks.size() - 1) ^ (1 << curInd));
        }
        return sets2;
    }

    private static boolean isPowerOf2(int a) {
        while ((a > 0) && (a % 2 == 0)) {
            a /= 2;
        }
        return a == 1;
    }

    private static boolean independent(Rectangle rectangle1, Rectangle rectangle2) {
        int y1_1 = rectangle1.getY();
        int y2_1 = rectangle1.getY() + rectangle1.getHeight();

        int y1_2 = rectangle2.getY();
        int y2_2 = rectangle2.getY() + rectangle2.getHeight();

        return ((y2_1 <= y1_2) || (y1_1 >= y2_2));
    }

    public static void paintPlacement(StaircasePlacement staircasePlacement, String paintFilePath) {
        LinkedList<Integer> bestIndependentSets = staircasePlacement.getBestIndependentsSets();
        Drawing drawing = Utils.setupDrawing(paintFilePath);
        int width_given = staircasePlacement.getWidth_given();

        drawing.addLine(100, 100, 100, 2000);
        drawing.addLine(100 + width_given * 10, 100, 100 + width_given * 10, 2000);

        List<Rectangle> rectangles = staircasePlacement.getRectangles();

        //paint rectangles
        for (Rectangle rectangle : rectangles) {
            drawing.addRectangle(
                    rectangle.getX() * 10 + 100, (int) (rectangle.getY() * 0.01 + 100),
                    rectangle.getWidth() * 10, (int) (rectangle.getHeight() * 0.01));
        }
        int cur_x = 100 + width_given * 10;

        //paint id columns
        if (bestIndependentSets != null) {
            for (int bestIndependentSet : bestIndependentSets) {
                cur_x += getColumnCount(bestIndependentSet, staircasePlacement.getRectangles().size()) * 10;
                drawing.addLine(cur_x, 100, cur_x, 2000);
            }
        }
        drawing.addLine(100, 100, cur_x, 100);
    }

    private static int getColumnCount(int setMask, int groupCount) {
        int setSize = 0;
        for (int i = 0; i < groupCount; i++) {
            if ((setMask & (1 << i)) != 0) {
                //group is in set
                setSize += 1;
            }
        }
        int result = 1;
        setSize += 1;
        while (setSize > Math.pow(2, result)) {
            result += 1;
        }

        return result;
    }

    public static StaircasePlacement findPlacement(
            List<Group> groups, List<Point> corners, int groups_mask, int width, List<Rectangle> rectangles,
            Visited visited, int cur_height) {
        corners.sort(Comparator.comparingInt(Point::getX));
        if (visited.getVisited().contains(new Visit(groups_mask, corners)) ||
                visited.getBest_height() < cur_height) {
            return null;
        }
        if (groups_mask == Math.pow(2, groups.size()) - 1) {
            List<Rectangle> cur_rectangles = new ArrayList<Rectangle>(rectangles);
            visited.maybeUpdateBestHeight(cur_height);
            return new StaircasePlacement(corners, cur_rectangles, width);
        }
        int min_height = Integer.MAX_VALUE;
        StaircasePlacement best_Staircase_placement = null;
        for (Point corner : corners) {
            int x1 = corner.getX();
            int y1 = corner.getY();
            for (int g_i = 0; g_i < groups.size(); g_i++) {
                //group is already placed
                if ((groups_mask & (1 << g_i)) != 0) {
                    continue;
                }
                int x2 = x1 + groups.get(g_i).getWidth();
                int y2 = y1 + groups.get(g_i).getHeight();
                if (x2 > width) {
                    continue;
                }
                Point new_corner1 = new Point(0, y2);
                Point new_corner2 = new Point(x2, 0);
                for (int c_i = 0; c_i < corners.size() - 1; c_i++) {
                    int x_i = corners.get(c_i).getX();
                    int y_i = corners.get(c_i).getY();

                    int x_ip1 = corners.get(c_i + 1).getX();
                    int y_ip1 = corners.get(c_i + 1).getY();
                    if ((y2 < y_i) && (y2 > y_ip1)) {
                        new_corner1 = new Point(x_ip1, y2);
                    }
                    if (y2 == y_i && x_i != 0) {
                        new_corner1 = null;
                    }
                    if ((x2 > x_i) && (x2 < x_ip1)) {
                        new_corner2 = new Point(x2, y_i);
                    }
                    if (x2 == x_ip1 && y_ip1 != 0) {
                        new_corner2 = null;
                    }
                }
                //delete expired corners
                List<Point> new_corners = corners
                        .stream()
                        .filter(p -> (p.getX() < x1 || p.getX() > x2) &&
                                (p.getY() < y1 || p.getY() > y2))
                        .collect(Collectors.toCollection(ArrayList::new));
                if (new_corner1 != null)
                    new_corners.add(new_corner1);
                if (new_corner2 != null)
                    new_corners.add(new_corner2);
                rectangles.add(new Rectangle(x1, y1, groups.get(g_i).getWidth(), groups.get(g_i).getHeight()));
                StaircasePlacement staircasePlacement = findPlacement(
                        groups,
                        new_corners,
                        groups_mask | (1 << g_i),
                        width,
                        rectangles,
                        visited,
                        Math.max(cur_height, y2));
                rectangles.remove(rectangles.size() - 1);
                if ((staircasePlacement != null) && (staircasePlacement.getHeight() < min_height)) {
                    best_Staircase_placement = staircasePlacement;
                    min_height = staircasePlacement.getHeight();
                }
            }
        }
        return best_Staircase_placement;
    }
}
