package com.caponetto.model.text;

import java.util.Objects;

public class TextRequest {

    private String text;

    public TextRequest() {
        // Empty
    }

    /**
     * @param text Text to be analyzed.
     */
    public TextRequest(final String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TextRequest that = (TextRequest) o;
        return text.equals(that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }
}
