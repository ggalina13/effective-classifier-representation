package constructionBuilding;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Utils {
    public static Drawing setupDrawing(String filePath) {
        Drawing frame = new Drawing();
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        frame.setExtendedState(Frame.NORMAL);
        //frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        try {
            if (Files.exists(Paths.get(filePath))) {
                Files.delete(Paths.get(filePath));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Files.createDirectories(Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        frame.setFilePath(filePath);
        return frame;
    }
}
