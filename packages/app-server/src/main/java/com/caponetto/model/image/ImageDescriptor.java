package com.caponetto.model.image;

import java.util.List;
import java.util.Objects;

public class ImageDescriptor {

    private String path;
    private List<ImageItem> items;

    public ImageDescriptor() {
        // Empty
    }

    /**
     * @param path  Absolute path of the image.
     * @param items List of items, which describes each element found in the image.
     */
    public ImageDescriptor(final String path, final List<ImageItem> items) {
        this.path = path;
        this.items = items;
    }

    public String getPath() {
        return path;
    }

    public List<ImageItem> getItems() {
        return items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ImageDescriptor that = (ImageDescriptor) o;
        return path.equals(that.path) &&
                items.equals(that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, items);
    }
}
