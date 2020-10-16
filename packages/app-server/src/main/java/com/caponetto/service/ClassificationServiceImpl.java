package com.caponetto.service;

import java.io.IOException;
import java.nio.file.Path;

import javax.enterprise.context.ApplicationScoped;

import ai.djl.ModelException;
import ai.djl.engine.Engine;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.transform.Normalize;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;

@ApplicationScoped
public class ClassificationServiceImpl implements ClassificationService {

    private static final float[] MEAN = { 103.939f, 116.779f, 123.68f };
    private static final float[] STD = { 1f, 1f, 1f };
    private static final String TENSORFLOW_ENGINE = "TensorFlow";
    private static final String MODEL = "resnet";

    @Override
    public Classifications.Classification classify(final String path)
            throws TranslateException, IOException, ModelException {
        final Image image = ImageFactory.getInstance().fromFile(Path.of(path));

        final Criteria<Image, Classifications> criteria = resolveCriteria();

        try (ZooModel<Image, Classifications> model = ModelZoo.loadModel(criteria);
                Predictor<Image, Classifications> predictor = model.newPredictor()) {
            final Classifications result = predictor.predict(image);
            return result.best();
        }
    }

    private Criteria<Image, Classifications> resolveCriteria() {
        if (TENSORFLOW_ENGINE.equals(Engine.getInstance().getEngineName())) {
            final Translator<Image, Classifications> translator = ImageClassificationTranslator.builder()
                    .addTransform(new Resize(224)).addTransform(new Normalize(MEAN, STD)).build();

            return Criteria.builder().setTypes(Image.class, Classifications.class).optArtifactId(MODEL)
                    .optTranslator(translator).optProgress(new ProgressBar()).build();
        }

        return Criteria.builder().setTypes(Image.class, Classifications.class).optArtifactId(MODEL)
                .optProgress(new ProgressBar()).build();
    }
}
