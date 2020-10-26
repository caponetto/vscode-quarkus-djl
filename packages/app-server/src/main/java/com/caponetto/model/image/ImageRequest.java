package com.caponetto.model.image;

import java.util.Objects;

public class ImageRequest {

    private String path;
    private int topK;
    private int threshold;

    public ImageRequest() {
        // Empty
    }

    /**
     * @param path      Absolute path of the image.
     * @param topK      Consider only k classes with the highest probability, k >= 1.
     * @param threshold Consider only classes with probability >= threshold, 0 <= threshold <= 100.
     */
    public ImageRequest(final String path, int topK, int threshold) {
        this.path = path;
        this.topK = topK;
        this.threshold = threshold;
    }

    public String getPath() {
        return path;
    }

    public int getTopK() {
        return topK;
    }

    public int getThreshold() {
        return threshold;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ImageRequest that = (ImageRequest) o;
        return topK == that.topK &&
                threshold == that.threshold &&
                path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, topK, threshold);
    }
}
