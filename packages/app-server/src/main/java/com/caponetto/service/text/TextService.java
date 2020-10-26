package com.caponetto.service.text;

import java.io.IOException;

import ai.djl.MalformedModelException;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.translate.TranslateException;
import com.caponetto.model.text.TextDescriptor;

public interface TextService {

    /**
     * Analyse the sentiment of the given text and report back the descriptor.
     *
     * @param text Text to be analyzed.
     * @return a descriptor of the text.
     * @throws MalformedModelException
     * @throws ModelNotFoundException
     * @throws IOException
     * @throws TranslateException
     */
    TextDescriptor analyzeSentiment(String text)
            throws MalformedModelException, ModelNotFoundException, IOException, TranslateException;
}
