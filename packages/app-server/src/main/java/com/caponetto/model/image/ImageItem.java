package com.caponetto.model.image;

import java.util.Objects;

public class ImageItem {

    private String className;
    private int probability;
    private BoundingBox boundingBox;

    public ImageItem() {
        // Empty
    }

    /**
     * @param className   Identified class for this image item.
     * @param probability Probability associated with the class, 0 <= probability <= 100.
     */
    public ImageItem(final String className, final int probability) {
        this.className = className;
        this.probability = probability;
    }

    /**
     * @param className   Identified class for this image item.
     * @param probability Probability associated with the class, 0 <= probability <= 100.
     * @param boundingBox Bounding box associated with the class - only available for object detection.
     */
    public ImageItem(final String className, final int probability, final BoundingBox boundingBox) {
        this(className, probability);
        this.boundingBox = boundingBox;
    }

    public String getClassName() {
        return className;
    }

    public int getProbability() {
        return probability;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ImageItem imageItem = (ImageItem) o;
        return probability == imageItem.probability &&
                className.equals(imageItem.className) &&
                boundingBox.equals(imageItem.boundingBox);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, probability, boundingBox);
    }
}
