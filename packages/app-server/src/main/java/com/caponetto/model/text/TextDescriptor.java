package com.caponetto.model.text;

import java.util.Objects;

public class TextDescriptor {

    private boolean positive;
    private int sentimentProbability;

    public TextDescriptor() {
        // Empty
    }

    /**
     * @param positive             Whether the sentiment extracted from the text is positive or not.
     * @param sentimentProbability Probability associated with the extracted sentiment.
     */
    public TextDescriptor(boolean positive, int sentimentProbability) {
        this.positive = positive;
        this.sentimentProbability = sentimentProbability;
    }

    public boolean isPositive() {
        return positive;
    }

    public int getSentimentProbability() {
        return sentimentProbability;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TextDescriptor that = (TextDescriptor) o;
        return positive == that.positive &&
                sentimentProbability == that.sentimentProbability;
    }

    @Override
    public int hashCode() {
        return Objects.hash(positive, sentimentProbability);
    }
}
