package constructionBuilding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConstructionBuilder {
    public static int build(List<Integer> args, String paintFilePath) {
        if ((args.size() < 2) || (args.size() % 2 == 0))
            throw new IllegalArgumentException("Format is \"width group_count hi wi ...\"");
        int width = args.get(0);
        ArrayList<Group> groups = Group.groupsFromList(args.subList(1, args.size()));
        Construction best = findConstructionHorizontal(width, groups);
        if (best == null) {
            System.out.println("Construction is not found");
            return -1;
        }
        paintConstruction(best, paintFilePath);
        return best.getHeight();
    }

    static Construction findConstructionHorizontal(int width, ArrayList<Group> groups) {
        Construction best = null;
        for (int stripeCount = 1; stripeCount <= groups.size(); stripeCount++) {
            int max_from = (int) Math.ceil((double) groups.size() / stripeCount);
            int max_to = groups.size() - stripeCount + 1;
            for (int maxInStripe = max_from; maxInStripe <= max_to; maxInStripe++) {
                int setColumnCount = (int) Math.ceil(Math.log(stripeCount + 1) / Math.log(2));
                Construction construction = dp(width - setColumnCount * maxInStripe, stripeCount, maxInStripe, groups);
                if (construction == null)
                    continue;
                if ((best == null) || (construction.getHeight() < best.getHeight())) {
                    best = construction;
                    best.setWidth_given(width - setColumnCount * maxInStripe);
                    best.setIdColumnSizes(getIdColumnSizes(maxInStripe, setColumnCount));
                }
            }
        }
        return best;
    }

    private static List<Integer> getIdColumnSizes(int maxInStripe, int setColumnCount) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < maxInStripe; i++) {
            result.add(setColumnCount);
        }
        return result;
    }

    private static Construction dp(int w, int stripeCount, int maxInStripe, ArrayList<Group> groups) {
        List<List<Pair<Integer, Integer>>> height = new ArrayList<>();
        List<List<Integer>> precalculation = new ArrayList<>();
        int maskMax = (int) (Math.pow(2, groups.size()) - 1);
        for (int i = 0; i <= stripeCount; i++) {
            List<Pair<Integer, Integer>> list =
                    new ArrayList<Pair<Integer, Integer>>(
                            Collections.nCopies(maskMax + 1, new Pair<Integer, Integer>(Integer.MAX_VALUE, 0)));
            height.add(list);
        }

        for (int i = 0; i <= maskMax; i++) {
            List<Integer> prelist = new ArrayList<>(Collections.nCopies(maskMax + 1, Integer.MAX_VALUE));
            precalculation.add(prelist);
        }

        for (int mask = 0; mask <= maskMax; mask++) {
            for (int subMask = mask; true; subMask = (subMask - 1) & mask) {
                int stripeHeight = makeStep(subMask, mask, maxInStripe, groups, w);
                precalculation.get(mask).set(subMask, stripeHeight);
                if (subMask == 0)
                    break;
            }
        }

        //height 0 for no stripes ahead and no groups used
        height.get(stripeCount).set(maskMax, new Pair<>(0, 0));
        for (int i = stripeCount; i >= 1; i--) {
            for (int mask = 0; mask <= maskMax; mask++) {
                for (int subMask = mask; true; subMask = (subMask - 1) & mask) {
                    int prevHeight = height.get(i).get(mask).getFirst();
                    if (prevHeight != Integer.MAX_VALUE) {
                        int stripeHeight = precalculation.get(mask).get(subMask);
                        if (stripeHeight != Integer.MAX_VALUE) {
                            if (prevHeight + stripeHeight < height.get(i - 1).get(subMask).getFirst())
                                height.get(i - 1).set(subMask, new Pair<>(prevHeight + stripeHeight, mask));
                        }
                    }
                    if (subMask == 0)
                        break;
                }
            }
        }
        if (height.get(0).get(0).getFirst() == Integer.MAX_VALUE) {
            return null;
        }
        ArrayList<Integer> masks = new ArrayList<>();
        int cur_stripe = 0;
        int cur_mask = 0;
        while (cur_stripe < stripeCount) {
            int new_mask = height.get(cur_stripe).get(cur_mask).getSecond();
            masks.add(new_mask ^ cur_mask);
            cur_mask = new_mask;
            cur_stripe++;
        }
        return new Construction(masks, height.get(0).get(0).getFirst(), w, groups);
    }

    private static int makeStep(int subMask, int mask, int maxInStripe, ArrayList<Group> groups, int width) {
        if (subMask == mask)
            return Integer.MAX_VALUE;
        String maskBinary = String.format("%" + groups.size() + "s", Integer.toBinaryString(mask)).replace(' ', '0');
        String subMaskBinary = String.format("%" + groups.size() + "s", Integer.toBinaryString(subMask)).replace(' ', '0');
        ArrayList<Integer> groupsInStripe = new ArrayList<>();
        int stripeHeight = 0;
        int stripeWidth = 0;
        for (int i = 0; i < groups.size(); i++) {
            //not submask
            if (subMaskBinary.charAt(i) == '1' && maskBinary.charAt(i) == '0')
                return Integer.MAX_VALUE;
            if (subMaskBinary.charAt(i) == '0' && maskBinary.charAt(i) == '1') {
                groupsInStripe.add(i);
                stripeHeight = Math.max(stripeHeight, groups.get(i).getHeight());
                stripeWidth += groups.get(i).getWidth();
            }
        }
        if ((groupsInStripe.size() > maxInStripe) || (stripeWidth > width))
            return Integer.MAX_VALUE;
        return stripeHeight;
    }

    static void paintConstruction(Construction construction, String paintFilePath) {
        Drawing drawing = Utils.setupDrawing(paintFilePath);
        int width_given = construction.getWidth_given();

        drawing.addLine(100, 100, 100 + width_given * 10, 100);
        drawing.addLine(100, 100, 100, 2000);
        drawing.addLine(100 + width_given * 10, 100, 100 + width_given * 10, 2000);

        List<Integer> masks = construction.getMasks();
        ArrayList<Group> groups = construction.getGroups();
        int y = 100;

        //paint groups
        for (Integer mask : masks) {
            int x = 100;
            int max_height = 0;
            for (int i = 0; i < groups.size(); i++) {
                if ((mask & (1 << (groups.size() - i - 1))) != 0) {
                    Group cur_group = groups.get(i);
                    int hi = cur_group.getHeight();
                    int wi = cur_group.getWidth();
                    drawing.addRectangle(x, y, wi * 10, (int) (hi * 0.01));
                    max_height = Math.max(max_height, hi);
                    x += wi * 10;
                }
            }
            y += max_height * 0.01;
        }
        int cur_x = 100 + width_given * 10;

        //paint id columns
        for (int idColumnSize : construction.getIdColumnSizes()) {
            cur_x += idColumnSize * 10;
            drawing.addLine(cur_x, 100, cur_x, 2000);
        }
        drawing.addLine(100, 100, cur_x, 100);
    }
}
