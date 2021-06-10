package constructionBuilding;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        String resultFolder = "./results";
        String timeFolder = "./times";
        String groupsFolder = "./groups";
        String paintingsFolder = "./paintings";
        String originalClassifiersFolder = "./classifiers";
        String tablesFolder = "./tables";

        List<Path> classifiers = new ArrayList<>();
        try {
            classifiers = Files.list(Path.of(groupsFolder)).sorted().collect(Collectors.toList());
            classifiers.add(0, classifiers.get(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ExperiementsStarter.startExperiements(classifiers, resultFolder, timeFolder, paintingsFolder, true, Algorithm.STRIPE);
        ExperiementsStarter.startExperiements(classifiers, resultFolder, timeFolder, paintingsFolder, true, Algorithm.STAIRCASE);
        ResultTablesMaker.createTables(groupsFolder, originalClassifiersFolder, resultFolder, tablesFolder);
        System.out.println("Finished");
    }
}
