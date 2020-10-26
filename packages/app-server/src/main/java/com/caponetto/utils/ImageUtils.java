package com.caponetto.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import javax.imageio.ImageIO;

import ai.djl.util.RandomUtils;
import com.caponetto.model.image.BoundingBox;
import com.caponetto.model.image.ImageItem;

public class ImageUtils {

    private static final String OUTPUT_EXTENSION = "png";
    private static final int DRAW_STROKE = 2;
    private static final int DRAW_PADDING = 4;
    private static final int MAX_WIDTH = 800;
    private static final int MAX_HEIGHT = 600;

    public static final String TEMP_PREFIX = "out";
    public static final String TEMP_SUFFIX = "." + OUTPUT_EXTENSION;

    private ImageUtils() {
        // Empty
    }

    public static Path drawBoundingBoxes(final Path originalPath,
                                         final List<ImageItem> items) throws IOException {
        final Path outputPath = File.createTempFile(TEMP_PREFIX, TEMP_SUFFIX).toPath();
        final BufferedImage originalImage = ImageIO.read(originalPath.toFile());

        double scale;
        BufferedImage outputImage;
        if (ImageUtils.shouldResize(originalImage.getWidth(), originalImage.getHeight())) {
            scale = ImageUtils.calcResizeScale(originalImage.getWidth(), originalImage.getHeight());
            outputImage = new BufferedImage((int) (originalImage.getWidth() * scale),
                                            (int) (originalImage.getHeight() * scale),
                                            BufferedImage.TYPE_INT_ARGB);
        } else {
            scale = 1;
            outputImage = originalImage;
        }

        final Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, outputImage.getWidth(), outputImage.getHeight(), null);
        g2d.setStroke(new BasicStroke(DRAW_STROKE));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        items.forEach(item -> {
            final BoundingBox boundingBox = item.getBoundingBox();

            g2d.setPaint(new Color(RandomUtils.nextInt(255)).darker());

            g2d.drawRect((int) (boundingBox.getX() * scale),
                         (int) (boundingBox.getY() * scale),
                         (int) (boundingBox.getWidth() * scale),
                         (int) (boundingBox.getHeight() * scale));

            drawText(g2d,
                     String.format("%s (%s%%)", item.getClassName(), item.getProbability()),
                     (int) (boundingBox.getX() * scale),
                     (int) (boundingBox.getY() * scale));
        });

        g2d.dispose();

        ImageIO.write(outputImage, OUTPUT_EXTENSION, outputPath.toFile());

        return outputPath;
    }

    private static void drawText(final Graphics2D g, final String text, int x, int y) {
        x += DRAW_STROKE / 2;
        y += DRAW_STROKE / 2;

        final FontMetrics metrics = g.getFontMetrics();
        int width = metrics.stringWidth(text) + DRAW_PADDING * 2 - DRAW_STROKE / 2;
        int height = metrics.getHeight() + metrics.getDescent();

        g.fill(new Rectangle(x, y, width, height));
        g.setPaint(Color.WHITE);
        g.drawString(text, x + DRAW_PADDING, y + metrics.getAscent());
    }

    private static boolean shouldResize(int width, int height) {
        return (width >= height && width > MAX_WIDTH) || (height >= width && height > MAX_HEIGHT);
    }

    private static double calcResizeScale(int width, int height) {
        double scaleW = MAX_WIDTH / (double) width;
        double scaleH = MAX_HEIGHT / (double) height;
        return Math.min(scaleW, scaleH);
    }
}
