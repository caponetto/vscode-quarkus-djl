package com.caponetto.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import ai.djl.Application;
import ai.djl.ModelException;
import ai.djl.engine.Engine;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.output.DetectedObjects.DetectedObject;
import ai.djl.modality.cv.output.Rectangle;
import ai.djl.modality.cv.transform.Normalize;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.Criteria.Builder;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import com.caponetto.model.BoundingBox;
import com.caponetto.model.ImageDescriptor;
import com.caponetto.model.ImageItem;

@ApplicationScoped
public class ImageServiceImpl implements ImageService {

    private static final int DEFAULT_TOP_K = 1;
    private static final int DEFAULT_THRESHOLD = 50;
    private static final int RESIZE_DIM = 224;
    private static final float[] MEAN = {103.939f, 116.779f, 123.68f};
    private static final float[] STD = {1f, 1f, 1f};
    private static final String TENSORFLOW_ENGINE = "TensorFlow";
    private static final String RESNET_MODEL_NAME = "resnet";
    private static final String RESNET_50_MODEL_NAME = "resnet50";
    private static final String BACKBONE_KEY = "backbone";

    @Override
    public ImageDescriptor classify(final String path, int topK, int threshold)
            throws TranslateException, IOException, ModelException {
        final Image image = ImageFactory.getInstance().fromFile(Path.of(path));
        int resolvedTopK = resolveTopK(topK);
        float resolvedThreshold = resolveThreshold(threshold);

        try (ZooModel<Image, Classifications> model = ModelZoo.loadModel(buildCriteriaForClassifier());
             Predictor<Image, Classifications> predictor = model.newPredictor()) {
            final List<ImageItem> items = predictor
                    .predict(image)
                    .topK(resolvedTopK)
                    .stream()
                    .filter(item -> item.getProbability() >= resolvedThreshold)
                    .map(item -> resolveItem(image, item))
                    .sorted(Comparator.comparing(ImageItem::getProbability).reversed())
                    .collect(Collectors.toList());
            return new ImageDescriptor(path, items);
        }
    }

    @Override
    public ImageDescriptor detect(final String path, int topK, int threshold)
            throws TranslateException, IOException, ModelException {
        final Image image = ImageFactory.getInstance().fromFile(Path.of(path));
        int resolvedTopK = resolveTopK(topK);
        float resolvedThreshold = resolveThreshold(threshold);

        try (ZooModel<Image, DetectedObjects> model = ModelZoo.loadModel(buildCriteriaForDetector());
             Predictor<Image, DetectedObjects> predictor = model.newPredictor()) {
            final List<ImageItem> items = predictor
                    .predict(image)
                    .topK(resolvedTopK)
                    .stream()
                    .filter(item -> item.getProbability() >= resolvedThreshold)
                    .map(item -> resolveItem(image, item))
                    .sorted(Comparator.comparing(ImageItem::getProbability).reversed())
                    .collect(Collectors.toList());
            return new ImageDescriptor(path, items);
        }
    }

    private Criteria<Image, Classifications> buildCriteriaForClassifier() {
        final Builder<Image, Classifications> criteriaBuilder = Criteria.builder()
                .setTypes(Image.class, Classifications.class)
                .optArtifactId(RESNET_MODEL_NAME)
                .optProgress(new ProgressBar());

        if (TENSORFLOW_ENGINE.equals(Engine.getInstance().getEngineName())) {
            final Translator<Image, Classifications> translator = ImageClassificationTranslator.builder()
                    .addTransform(new Resize(RESIZE_DIM))
                    .addTransform(new Normalize(MEAN, STD))
                    .build();

            criteriaBuilder.optTranslator(translator);
        }

        return criteriaBuilder.build();
    }

    private Criteria<Image, DetectedObjects> buildCriteriaForDetector() {
        return Criteria.builder()
                .optApplication(Application.CV.OBJECT_DETECTION)
                .setTypes(Image.class, DetectedObjects.class)
                .optFilter(BACKBONE_KEY, RESNET_50_MODEL_NAME)
                .optProgress(new ProgressBar())
                .build();
    }

    private ImageItem resolveItem(final Image image, final Classifications.Classification result) {
        final String className = result.getClassName().substring(result.getClassName().indexOf(" ") + 1);
        final int probability = (int) (result.getProbability() * 100);

        if (result instanceof DetectedObject) {
            return new ImageItem(className,
                                 probability,
                                 extractBoundingBox((DetectedObject) result, image));
        }

        return new ImageItem(className, probability);
    }

    private BoundingBox extractBoundingBox(final DetectedObject item, final Image image) {
        final Rectangle bounds = item.getBoundingBox().getBounds();

        return new BoundingBox((int) (bounds.getPoint().getX() * image.getWidth()),
                               (int) (bounds.getPoint().getY() * image.getHeight()),
                               (int) (bounds.getWidth() * image.getWidth()),
                               (int) (bounds.getHeight() * image.getHeight()));
    }

    private int resolveTopK(int topK) {
        return topK > 0 ? topK : DEFAULT_TOP_K;
    }

    private float resolveThreshold(int threshold) {
        return (threshold >= 0 ? threshold : DEFAULT_THRESHOLD) / 100f;
    }
}
