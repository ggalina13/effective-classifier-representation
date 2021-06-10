package groupMaking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClassifierParser {

    public static void parseDir(String inputFolder, String outputFolder) throws IOException {
        List<Path> filesInput = FileManager.getFilesInput(inputFolder);
        List<Path> filesOutput = FileManager.getFilesOutput(inputFolder, outputFolder);
        for (int i = 0; i < filesInput.size(); i++) {
            parseFile(filesInput.get(i), filesOutput.get(i));
        }
    }

    private static void parseFile(Path input, Path output) {
        try {
            if (Files.exists(output)) {
                Files.delete(output);
            }
            Files.createFile(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedReader reader = Files.newBufferedReader(input, StandardCharsets.UTF_8);
             BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
            String line = reader.readLine();
            int stringId = 0;
            while (line != null) {
                writer.write(toTernary(line, stringId) + "\n");
                line = reader.readLine();
                stringId++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String toTernary(String line, int id) {
        StringBuilder begining = new StringBuilder();
        String[] components = line.split("\t");
        String addr1 = components[0].substring(1);
        String addr2 = components[1];
        begining
                .append(addrToTernary(addr1))
                .append(addrToTernary(addr2))
                .append(protocolToTernary(components[2]));
        String[] endings1 = processArrayPart(components[3]);
        String[] endings2 = processArrayPart(components[4]);
        return begining.toString() + formatId(endings1.length * endings2.length);//String.join("\n", stringsResult);
    }

    private static String[] processArrayPart(String component) {
        return Arrays.stream(component
                .substring(1, component.length() - 1)
                .split(", "))
                .map(x -> x.substring(1, x.length() - 1)).toArray(String[]::new);
    }

    private static String processStarsPart(String component) {
        return component.substring(2, component.length() - 2);
    }

    private static String protocolToTernary(String component) {
        String[] parts = component.split("/");
        int num = Integer.decode(parts[0]);
        int mask = Integer.decode(parts[1]);
        if (mask == 255)
            return intToBinaryString(num);
        if (mask == 0)
            return generateStars(8);
        throw new IllegalArgumentException("Wrong mask provided: " + mask);
    }

    private static String addrToTernary(String addr) {
        StringBuilder ternary = new StringBuilder();
        ArrayList<String> parts = Arrays
                .stream(addr.split("\\."))
                .collect(Collectors.toCollection(ArrayList::new));
        String[] last_part = parts.get(parts.size() - 1).split("/");
        parts.set(parts.size() - 1, last_part[0]);
        ArrayList<Integer> bytes = parts.stream()
                .map(Integer::parseInt)
                .collect(Collectors.toCollection(ArrayList::new));
        int prefix_size = Integer.parseInt(last_part[1]);
        for (Integer byte_ : bytes) {
            ternary.append(intToBinaryString(byte_));
        }
        return ternary.substring(0, prefix_size) + generateStars(32 - prefix_size);
    }

    private static String intToBinaryString(Integer byte_) {
        return Integer.toBinaryString((byte_ & 0xFF) + 0x100).substring(1);
    }

    private static String generateStars(int num) {
        char[] stars = new char[num];
        Arrays.fill(stars, '*');
        return new String(stars);
    }

    private static String formatId(int id) {
        return String.format("%16s", Integer.toBinaryString(id)).replace(" ", "0");
    }
}
