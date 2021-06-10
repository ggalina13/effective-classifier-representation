package groupMaking;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileManager {
    public static List<Path> getFilesInput(String inputFolder) throws IOException {
        Path inputFolderPath = Paths.get(inputFolder);
        return Files.list(inputFolderPath).collect(Collectors.toList());
    }

    public static List<Path> getFilesOutput(String inputFolder, String outputFolder) throws IOException {
        Path inputFolderPath = Paths.get(inputFolder);
        Path outputFolderPath = Paths.get(outputFolder);
        Files.createDirectories(outputFolderPath);
        List<Path> filesInput = Files.list(inputFolderPath).collect(Collectors.toList());
        List<Path> filesOutput = filesInput
                .stream()
                .map(path -> Paths.get(path.getParent().getParent().toString() +
                        File.separator +
                        outputFolderPath.getFileName() +
                        File.separator +
                        path.getFileName()))
                .collect(Collectors.toList());
        return filesOutput;
    }
}
