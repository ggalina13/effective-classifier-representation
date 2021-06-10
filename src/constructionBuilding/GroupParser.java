package constructionBuilding;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroupParser {
    public static List<Integer> createArgs(Integer width, String groupFolder) {
        List<Integer> args = parse(groupFolder).getGroupProportions();
        args.add(0, width);
        return args;
    }

    public static ClassifierGroupsInfo parse(String groupFolder) {
        List<Integer> args = new ArrayList<>();
        Path path = Paths.get(groupFolder);
        int rulesInGroupsCount = 0;
        int rulesLeftCartesianProductSize = 0;
        try {
            List<Path> groups = Files.list(path).collect(Collectors.toList());
            groups = groups.stream().filter(group -> Integer
                    .parseInt(group
                            .getFileName()
                            .toString()
                            .substring(5, group
                                    .getFileName()
                                    .toString().indexOf('.'))) <= 10).collect(Collectors.toList());
            for (Path group : groups) {
                BufferedReader reader = Files.newBufferedReader(group, StandardCharsets.UTF_8);
                String line = reader.readLine();
                int w = line.length() - 16;
                int h = 0;
                while (line != null) {
                    h += 1;
                    if (group.getFileName().toString().equals("group10.txt")) {
                        rulesLeftCartesianProductSize += Integer.parseInt(line.substring(line.length() - 16), 2);
                    }
                    line = reader.readLine();
                }
                if (group.getFileName().toString().equals("group10.txt")) {
                    continue;
                }
                args.add(h);
                rulesInGroupsCount += h;
                args.add(w);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ClassifierGroupsInfo(args, rulesInGroupsCount, rulesLeftCartesianProductSize);
    }
}
