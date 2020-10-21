package com.caponetto.service;

import java.io.IOException;

import ai.djl.ModelException;
import ai.djl.translate.TranslateException;
import com.caponetto.model.ImageDescriptor;
import org.apache.commons.imaging.ImageReadException;

public interface ImageService {

    /**
     * Classify an image in the given path and return a descriptor.
     *
     * @param path      Absolute path of the image.
     * @param topK      Consider only k classes with the highest probability, k >= 1.
     * @param threshold Consider only classes with probability >= threshold, 0 <= threshold <= 100.
     * @return a descriptor of the image.
     * @throws TranslateException
     * @throws IOException
     * @throws ModelException
     * @throws ImageReadException
     */
    ImageDescriptor classify(String path, int topK, int threshold)
            throws TranslateException, IOException, ModelException, ImageReadException;

    /**
     * Detect objects on an image in the given path and return a descriptor.
     *
     * @param path      Absolute path of the image.
     * @param topK      Consider only k classes with the highest probability, k >= 1.
     * @param threshold Consider only classes with probability >= threshold, 0 <= threshold <= 100.
     * @return a descriptor of the image.
     * @throws TranslateException
     * @throws IOException
     * @throws ModelException
     */
    ImageDescriptor detect(String path, int topK, int threshold)
            throws TranslateException, IOException, ModelException;
}
