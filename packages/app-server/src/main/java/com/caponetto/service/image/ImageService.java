package com.caponetto.service.image;

import java.io.IOException;
import java.util.List;

import ai.djl.ModelException;
import ai.djl.translate.TranslateException;
import com.caponetto.model.image.ImageDescriptor;
import com.caponetto.service.EditorService;
import org.apache.commons.imaging.ImageReadException;

public interface ImageService extends EditorService {

    /**
     * Classify an image in the given path and return a descriptor.
     *
     * @param path      Absolute path of the image.
     * @param topK      Consider only k classes with the highest probability, k >= 1.
     * @param threshold Consider only classes with probability >= threshold, 0 <= threshold <= 100.
     * @return A descriptor of the image.
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
     * @return A descriptor of the image.
     * @throws TranslateException
     * @throws IOException
     * @throws ModelException
     */
    ImageDescriptor detect(String path, int topK, int threshold)
            throws TranslateException, IOException, ModelException;

    /**
     * Detect objects on an image in the given path and crop them into new files.
     *
     * @param path      Absolute path of the image.
     * @param topK      Consider only k classes with the highest probability, k >= 1.
     * @param threshold Consider only classes with probability >= threshold, 0 <= threshold <= 100.
     * @return The list of paths of the output files.
     * @throws TranslateException
     * @throws IOException
     * @throws ModelException
     */
    List<String> autoCrop(String path, int topK, int threshold)
            throws TranslateException, IOException, ModelException;

    /**
     * Generate random images related to given image using BigGAN.
     *
     * @param path      Absolute path of the image that should be related to the output images.
     * @param topK      Consider only k classes with the highest probability, k >= 1.
     * @param threshold Consider only classes with probability >= threshold, 0 <= threshold <= 100.
     * @return The list of paths of the output files.
     * @throws TranslateException
     * @throws IOException
     * @throws ModelException
     */
    List<String> generateRandomImages(String path, int topK, int threshold)
            throws TranslateException, IOException, ModelException;
}
