package groupMaking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class GroupMaker {
    List<Group> groups = new ArrayList<>();

    public void makeGroups(Path inputFile, boolean isStandart, String folderName) {
        System.out.println(inputFile.getFileName());
        List<String> strings = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8)) {
            String line = reader.readLine();
            while (line != null) {
                strings.add(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        long tm = System.currentTimeMillis();

        int maxId = strings.size();
        int maxInOneGroup = isStandart ? 100000000 : (int) (1.1 * maxId / 10);
        while ((groups.size() < 100) && (!strings.isEmpty())) {
            Group cur_group = new Group();
            if (strings.size() < 10000) {
                HashMap<String, Integer> Imap = new HashMap<>();
                for (String r : strings) {
                    for (String r1 : strings) {
                        if (!GroupMaker.ternaryIndependent(r, r1)) {
                            Imap.put(r, Imap.getOrDefault(r, 0) + 1);
                        }
                    }
                }
                strings.sort(Comparator.comparingInt(Imap::get));
            }

            Iterator<String> iterator = strings.iterator();
            while (cur_group.getLaws().size() <= maxInOneGroup && iterator.hasNext()) {
                String cur_string = iterator.next();
                if (tryAddString(cur_group, cur_string)) {
                    iterator.remove();
                }
            }
            if (!isStandart) {
                List<String> lawsRemoved = cur_group.optimize(1.0);
                strings.addAll(0, lawsRemoved);
            }
            groups.add(cur_group);
        }


        groups.sort(Comparator.comparingInt(this::cover));

        for (int i = groups.size() - 1; i >= 11; i--) {
            groups.get(10).getLaws().addAll(groups.get(i).getLaws());
        }

        for (int i = 0; i < 11; i++) {
            System.out.println((i == 10 ? "Left: " : i) + " " + groups.get(i).getLaws().size() + " " + (groups.get(i).getLaws().get(0).length() - 16) + " " + (-cover(groups.get(i))));
        }

        System.out.println("Time: " + (System.currentTimeMillis() - tm) / 1000.0);

        String fileName = inputFile.getFileName().toString();
        String fileNameNoExt = fileName.substring(0, fileName.indexOf('.'));
        Path outputPath = Path.of(inputFile.getParent().getParent() + File.separator + folderName + File.separator + fileNameNoExt);
        try {
            Files.createDirectories(outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < groups.size(); i++) {
            Path curOutput = Path.of(outputPath + File.separator + "group" + i + ".txt");
            Group curGroup = groups.get(i);
            try {
                if (Files.exists(curOutput)) {
                    Files.delete(curOutput);
                }
                Files.createFile(curOutput);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (BufferedWriter writer = Files.newBufferedWriter(curOutput, StandardCharsets.UTF_8)) {
                for (String law : curGroup.getLaws()) {
                    writer.write(law + '\n');
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int cover(Group o) {
        int cnt = 0;
        for (String s : o.getLaws()) {
            cnt += Integer.parseInt(s.substring(s.length() - 16), 2);
        }
        return -cnt;
    }


    public boolean tryAddString(Group group, String ternary) {
        for (String string : group.getLaws()) {
            if (!ternaryIndependent(ternary, string)) {
                return false;
            }
        }
        group.addLaw(ternary);
        return true;
    }

    public static boolean ternaryIndependent(String string1, String string2) {
        boolean ans = false;
        for (int i = 0; i < string1.length() - 16; i++) {
            char c1 = string1.charAt(i);
            char c2 = string2.charAt(i);
            if ((c1 != c2) && (c1 != '*') && (c2 != '*')) {
                ans = true;
                return ans;
            }
        }
        return ans;
    }
}
