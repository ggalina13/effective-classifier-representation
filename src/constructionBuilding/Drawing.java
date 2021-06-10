package constructionBuilding;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Drawing extends Frame {
    ArrayList<Shape> shapes = new ArrayList<>();
    String filePath;

    @Override
    public void paint(Graphics g) {
        BufferedImage bufferedImage = new BufferedImage(2100, 2100, BufferedImage.TYPE_INT_RGB);
        Graphics2D ga = bufferedImage.createGraphics();
        ga.setColor(Color.WHITE);
        ga.setStroke(new BasicStroke(2.0f));
        ga.fillRect(0, 0, 2100, 2100);
        ga.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        for (Shape shape : shapes) {
            ga.setPaint(Color.BLACK);
            if (shape instanceof Line2D) {
                ga.draw(shape);
                continue;
            }
            ga.draw(shape);
            ga.setPaint(new Color(0f, 0f, 1f, .1f));
            ga.fill(shape);
        }
        File file = new File(filePath);
        try {
            ImageIO.write(bufferedImage, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addRectangle(int x, int y, int width, int height) {
        Rectangle2D rect = new Rectangle2D.Float();
        rect.setFrame(x, y, width, height);
        shapes.add(rect);
    }

    public void addLine(int x1, int y1, int x2, int y2) {
        Line2D line = new Line2D.Float();
        line.setLine(x1, y1, x2, y2);
        shapes.add(line);
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}