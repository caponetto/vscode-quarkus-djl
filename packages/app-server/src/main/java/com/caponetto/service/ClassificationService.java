package com.caponetto.service;

import java.io.IOException;

import ai.djl.ModelException;
import ai.djl.modality.Classifications;
import ai.djl.translate.TranslateException;
import org.apache.commons.imaging.ImageReadException;

public interface ClassificationService {

    Classifications.Classification classify(String path)
            throws TranslateException, IOException, ModelException, ImageReadException;
}
