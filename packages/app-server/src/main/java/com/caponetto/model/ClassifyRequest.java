package com.caponetto.model;

import java.util.Objects;

public class ClassifyRequest {

    private String path;

    public ClassifyRequest() {
        // Empty
    }

    public ClassifyRequest(final String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClassifyRequest that = (ClassifyRequest) o;
        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
