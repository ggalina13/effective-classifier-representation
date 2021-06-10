package constructionBuilding;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResultTablesMaker {
    private static final int RULE_LENGTH = 104;

    public static void createTables(String groupFolder, String originalClassifiersFolder,
                                    String resultPath, String tableFolder) {
        Map<String, List<Double>> results72 = new HashMap<>();
        Map<String, List<Double>> results144 = new HashMap<>();
        List<Path> classifiersPaths = new ArrayList<>();
        try {
            classifiersPaths = Files.list(Path.of(groupFolder)).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Path classifierPath : classifiersPaths) {
            int totalRules = getTotalRules(classifierPath);
            int rules_left = getRulesLeft(classifierPath);

            String startPath = originalClassifiersFolder + File.separator + classifierPath.getFileName() + ".txt";
            int startRules = getStartRules(startPath);

            List<Double> curResults72 = new ArrayList<>();
            List<Double> curResults144 = new ArrayList<>();

            //standart representation
            curResults72.add(getExpansionCoeff(totalRules, 72, rules_left, startRules));
            curResults144.add(getExpansionCoeff(totalRules, 144, rules_left, startRules));

            try (BufferedReader reader = Files.newBufferedReader(
                    Path.of(resultPath + File.separator + classifierPath.getFileName() + ".txt"), StandardCharsets.UTF_8)) {
                String line = reader.readLine();
                double stripe72 = 0, stripe144 = 0, staircase72 = 0, staircase144 = 0;
                while (line != null) {
                    String[] split = line.split("\t");
                    String algo = split[0];
                    int width = Integer.parseInt(split[1]);
                    int height = Integer.parseInt(split[2]);
                    line = reader.readLine();
                    if (algo.equals("stripe")) {
                        if (width == 72) {
                            stripe72 = getExpansionCoeff(height, 72, rules_left, startRules);
                        }
                        if (width == 144) {
                            stripe144 = getExpansionCoeff(height, 144, rules_left, startRules);
                        }
                    }
                    if (algo.equals("staircase")) {
                        if (width == 72) {
                            staircase72 = getExpansionCoeff(height, 72, rules_left, startRules);
                        }
                        if (width == 144) {
                            staircase144 = getExpansionCoeff(height, 144, rules_left, startRules);
                        }
                    }
                }
                curResults72.add(staircase72);
                curResults72.add(stripe72);

                curResults144.add(staircase144);
                curResults144.add(stripe144);

                results72.put(classifierPath.getFileName().toString(), curResults72);
                results144.put(classifierPath.getFileName().toString(), curResults144);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Files.createDirectories(Path.of(tableFolder));
        } catch (IOException e) {
            e.printStackTrace();
        }

        createTable(tableFolder, "72.txt", results72);
        createTable(tableFolder, "144.txt", results144);
    }

    private static double getExpansionCoeff(int height, int width, int rules_left, int startRules) {
        return (height + rules_left * (int) Math.ceil(RULE_LENGTH / (double) width)) / (double) startRules;
    }

    private static void createTable(String tablesFolder, String tableFileName, Map<String, List<Double>> results) {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(tablesFolder + File.separator + tableFileName), StandardCharsets.UTF_8)) {
            String header = "  & Standart & Staircase & Stripe \\\\\\hline\n";
            writer.write(header);
            for (Map.Entry<String, List<Double>> entry : results.entrySet()) {
                String classifierName = entry.getKey();
                String line = classifierName + " & " +
                        String.format("%.3f", entry.getValue().get(0)) + " & " +
                        String.format("%.3f", entry.getValue().get(1)) + " & " +
                        String.format("%.3f", entry.getValue().get(2)) + "\\\\\\hline\n";
                writer.write(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int getStartRules(String startPath) {
        int count = 0;
        try (BufferedReader reader = Files.newBufferedReader(Path.of(startPath), StandardCharsets.UTF_8)) {
            String line = reader.readLine();
            while (line != null) {
                count++;
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    private static Integer getRulesLeft(Path classifier) {
        return GroupParser.parse(classifier.toString()).getrulesLeftCartesianProductSize();
    }

    private static Integer getTotalRules(Path classifier) {
        return GroupParser.parse(classifier.toString()).getRulesInGroupsCount();
    }
}
