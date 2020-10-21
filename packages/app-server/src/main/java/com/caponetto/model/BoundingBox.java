package com.caponetto.model;

import java.util.Objects;

public class BoundingBox {

    private int x;
    private int y;
    private int width;
    private int height;

    public BoundingBox() {
        // Empty
    }

    /**
     * @param x      Top value of the rectangle.
     * @param y      Left value of the rectangle.
     * @param width  Width of the rectangle.
     * @param height Height of the rectangle.
     */
    public BoundingBox(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BoundingBox rect = (BoundingBox) o;
        return x == rect.x &&
                y == rect.y &&
                width == rect.width &&
                height == rect.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, width, height);
    }
}
