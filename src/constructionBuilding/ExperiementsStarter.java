package constructionBuilding;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ExperiementsStarter {
    public static void startExperiements(List<Path> classifiers, String resultsFolder, String timeFolder,
                                         String paintingsFolder, boolean rewriteIfExists, Algorithm algorithm) {
        for (Path classifier : classifiers) {
            String resultsPath = resultsFolder + File.separator + classifier.getFileName() + ".txt";
            String timePath = timeFolder + File.separator + classifier.getFileName() + ".txt";

            try {
                Files.createDirectories(Path.of(resultsPath).getParent());
                Files.createDirectories(Path.of(timePath).getParent());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (BufferedWriter resultWriter = new BufferedWriter(new FileWriter(resultsPath, true));
                 BufferedWriter timeWriter = new BufferedWriter(new FileWriter(timePath, true))) {
                for (int width : new int[]{72, 144}) {
                    String paintingFile = paintingsFolder +
                            File.separator +
                            classifier.getFileName() +
                            File.separator +
                            width + "_" + algorithm.name() + ".png";
                    if (rewriteIfExists || !Files.exists(Paths.get(paintingFile))) {
                        int h;
                        System.out.println(paintingFile);
                        long startTime = System.currentTimeMillis();
                        if (algorithm == Algorithm.STRIPE) {
                            h = ConstructionBuilder.build(GroupParser.createArgs(width, classifier.toString()),
                                    paintingFile);
                        } else if (algorithm == Algorithm.STAIRCASE) {
                            h = StaircasePlacementBuilder.build(GroupParser.createArgs(width, classifier.toString()),
                                    paintingFile);
                        } else throw new IllegalArgumentException("Wrong algorithm: " + algorithm.toString());
                        long finishTime = System.currentTimeMillis();
                        resultWriter.write(algorithm.name().toLowerCase() + "\t" + width + "\t" + h + '\n');
                        timeWriter.write(algorithm.toString() + ", width = " + width +
                                " : " + (finishTime - startTime) + " ms\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
