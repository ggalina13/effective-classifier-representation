package groupMaking;

import java.util.*;

public class Group {
    private final List<String> laws = new ArrayList<>();

    public List<String> getLaws() {
        return laws;
    }

    public void addLaw(String law) {
        laws.add(law);
    }

    public List<String> optimize(double percentOfIndependency) {
        if (laws.isEmpty()) {
            throw new IllegalArgumentException("laws are empty");
        }

        List<String> redundant = new ArrayList<>();

        boolean[][] isIntersect = new boolean[laws.size()][laws.size()];
        for (int i = 0; i < laws.size(); i++) {
            for (int j = i + 1; j < laws.size(); j++) {
                isIntersect[i][j] = true;
                isIntersect[j][i] = true;
            }
        }


        boolean[] isTaken = new boolean[laws.get(0).length() - 16];
        int[] cnt = new int[isTaken.length];
        for (int k = 0; k < isTaken.length; k++) {
            int zeroes = 0, ones = 0;
            for (String law : laws) {
                if (law.charAt(k) == '0') {
                    zeroes++;
                }
                if (law.charAt(k) == '1') {
                    ones++;
                }
            }
            cnt[k] += zeroes * ones;
        }

        HashSet<Integer> taken_laws;

        while (true) {

            int maxPos = 0;
            for (int i = 0; i < cnt.length; i++) {
                if (cnt[i] > cnt[maxPos]) {
                    maxPos = i;
                }
            }

            for (int i = 0; i < laws.size(); i++) {
                if (laws.get(i).charAt(maxPos) != '0') {
                    continue;
                }
                for (int j = 0; j < laws.size(); j++) {
                    if (isIntersect[i][j] && laws.get(j).charAt(maxPos) == '1') {
                        isIntersect[i][j] = false;
                        isIntersect[j][i] = false;
                        for (int k = 0; k < isTaken.length; k++) {
                            char c1 = laws.get(i).charAt(k);
                            char c2 = laws.get(j).charAt(k);
                            if ((c1 != c2) && (c1 != '*') && (c2 != '*')) {
                                cnt[k]--;
                            }
                        }
                    }
                }
            }

            int[] dependentCount = new int[isIntersect.length];

            for (int i = 0; i < isIntersect.length; i++) {
                for (int j = i + 1; j < isIntersect.length; j++) {
                    if (!isIntersect[i][j]) {
                        dependentCount[i]++;
                        dependentCount[j]++;
                    }
                }
            }

            ArrayList<Integer> list = new ArrayList<>();
            for (int i = 0; i < dependentCount.length; i++) {
                list.add(i);
            }
            list.sort(Comparator.comparingInt(o -> dependentCount[o]));
            ArrayList<Integer> taken = new ArrayList<>();

            for (int i : list) {
                boolean is_take = true;
                for (int j : taken) {
                    if (isIntersect[i][j]) {
                        is_take = false;
                        break;
                    }
                }
                if (is_take) {
                    taken.add(i);
                }
            }
            isTaken[maxPos] = true;
            cnt[maxPos] = -1;
            if (percentOfIndependency * laws.size() <= taken.size()) {
                taken_laws = new HashSet<>(taken);
                break;
            }
        }


        for (int i = isIntersect.length - 1; i >= 0; i--) {
            if (!taken_laws.contains((Integer) i)) {
                redundant.add(laws.get(i));
                laws.remove(i);
            } else {
                for (int j = isTaken.length - 1; j >= 0; j--) {
                    if (!isTaken[j]) {
                        laws.set(i, new StringBuilder(laws.get(i)).deleteCharAt(j).toString());
                    }
                }
            }
        }

        Collections.reverse(redundant);
        return redundant;
    }

}
