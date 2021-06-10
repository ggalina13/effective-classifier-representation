package groupMaking;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String inputFolder = "./test";
        String outputFolder = "./restest";
        String groupFolder = "./groups";
        try {
            ClassifierParser.parseDir(inputFolder, outputFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Path> classifiers = null;
        try {
            classifiers = FileManager.getFilesInput("./restest");
            classifiers.add(0, classifiers.get(0));
            for (Path classifier : classifiers) {
                GroupMaker groupMaker = new GroupMaker();
                groupMaker.makeGroups(classifier, false, groupFolder);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
