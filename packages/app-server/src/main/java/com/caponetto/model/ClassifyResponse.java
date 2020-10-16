package com.caponetto.model;

import java.util.Objects;

public class ClassifyResponse {

    private String className;
    private int probability;

    public ClassifyResponse() {
        // Empty
    }

    public ClassifyResponse(final String className, final int probability) {
        this.className = className;
        this.probability = probability;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getProbability() {
        return probability;
    }

    public void setProbability(int probability) {
        this.probability = probability;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClassifyResponse that = (ClassifyResponse) o;
        return Double.compare(that.probability, probability) == 0 && className.equals(that.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, probability);
    }
}
