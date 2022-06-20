package com.caponetto.service.image;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import ai.djl.Application;
import ai.djl.MalformedModelException;
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
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.util.ClassLoaderUtils;
import ai.djl.util.JsonUtils;
import com.caponetto.model.image.BoundingBox;
import com.caponetto.model.image.ImageDescriptor;
import com.caponetto.model.image.ImageItem;
import com.caponetto.utils.ImageUtils;

@ApplicationScoped
public class ImageServiceImpl implements ImageService {

    private Map<String, Integer> classMap;

    private static final int DEFAULT_TOP_K = 10;
    private static final int DEFAULT_THRESHOLD = 50;
    private static final int RESIZE_DIM = 224;
    private static final float[] MEAN = {103.939f, 116.779f, 123.68f};
    private static final float[] STD = {1f, 1f, 1f};
    private static final String TENSORFLOW_ENGINE = "TensorFlow";
    private static final String RESNET_MODEL_NAME = "resnet";
    private static final String RESNET_50_MODEL_NAME = "resnet50";
    private static final String BACKBONE_KEY = "backbone";
    private static final String SIZE_KEY = "size";
    private static final String BIG_GAN_SIZE = String.valueOf(256);
    private static final String TRUNCATION_KEY = "truncation";
    private static final float BIG_GAN_TRUNCATION = 0.4f;
    private static final String CLASSES_JSON_PATH = "imagenet/classes.json";

    @PostConstruct
    public void postConstruct() {
        this.classMap = loadClassMap();
    }

    @Override
    public List<String> getModels() {
        final List<String> loadedModels = new ArrayList<>();
        try (ZooModel<Image, Classifications> model = ModelZoo.loadModel(buildCriteriaForClassifier())) {
            loadedModels.add(model.getModelPath().toString());
        } catch (MalformedModelException | ModelNotFoundException | IOException e) {
            e.printStackTrace();
        }
        try (ZooModel<Image, DetectedObjects> model = ModelZoo.loadModel(buildCriteriaForDetector())) {
            loadedModels.add(model.getName());
        } catch (MalformedModelException | ModelNotFoundException | IOException e) {
            e.printStackTrace();
        }
        try (ZooModel<int[], Image[]> model = ModelZoo.loadModel(buildCriteriaForBigGan())) {
            loadedModels.add((model.getName()));
        } catch (MalformedModelException | ModelNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return loadedModels;
    }

    @Override
    public ImageDescriptor classify(final String path, int topK, int threshold)
            throws TranslateException, IOException, ModelException {
        final Path originalPath = Paths.get(path);
        final Image image = ImageFactory.getInstance().fromFile(originalPath);
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
        final Path originalPath = Paths.get(path);
        final Image image = ImageFactory.getInstance().fromFile(originalPath);
        int resolvedTopK = resolveTopK(topK);
        float resolvedThreshold = resolveThreshold(threshold);

        try (ZooModel<Image, DetectedObjects> model = ModelZoo.loadModel(buildCriteriaForDetector());
             Predictor<Image, DetectedObjects> predictor = model.newPredictor()) {
            final DetectedObjects detections = predictor.predict(image);
            final List<ImageItem> items = detections
                    .topK(resolvedTopK)
                    .stream()
                    .filter(item -> item.getProbability() >= resolvedThreshold)
                    .map(item -> resolveItem(image, item))
                    .sorted(Comparator.comparing(ImageItem::getProbability).reversed())
                    .collect(Collectors.toList());

            final Path outputPath = ImageUtils.drawBoundingBoxes(originalPath, items);
            return new ImageDescriptor(outputPath.toString(), items);
        }
    }

    @Override
    public List<String> autoCrop(final String path, int topK, int threshold)
            throws TranslateException, IOException, ModelException {
        final ImageDescriptor imageDescriptor = detect(path, topK, threshold);
        return ImageUtils.cropBoundingBoxes(Paths.get(path), imageDescriptor.getItems())
                .stream()
                .map(Path::toString)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> generateRandomImages(final String path, int topK, int threshold) throws TranslateException, IOException, ModelException {
        final ImageDescriptor imageDescriptor = classify(path, topK, threshold);
        var numbers = imageDescriptor.getItems().stream().map(item -> classMap.get(item.getClassName())).mapToInt(i -> i).toArray();
        try (ZooModel<int[], Image[]> model = ModelZoo.loadModel(buildCriteriaForBigGan());
             Predictor<int[], Image[]> generator = model.newPredictor()) {
            final Image[] images = generator.predict(numbers);
            return ImageUtils.saveGeneratedCvImages(imageDescriptor, images)
                    .stream()
                    .map(Path::toString)
                    .collect(Collectors.toList());
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
                .optEngine(Engine.getDefaultEngineName())
                .optProgress(new ProgressBar())
                .build();
    }

    private Criteria<int[], Image[]> buildCriteriaForBigGan() {
        return Criteria.builder()
                .optApplication(Application.CV.IMAGE_GENERATION)
                .setTypes(int[].class, Image[].class)
                .optFilter(SIZE_KEY, BIG_GAN_SIZE)
                .optArgument(TRUNCATION_KEY, BIG_GAN_TRUNCATION)
                .optEngine(Engine.getDefaultEngineName())
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

    private Map<String, Integer> loadClassMap() {
        try (InputStream classStream = ClassLoaderUtils.getContextClassLoader().getResourceAsStream(CLASSES_JSON_PATH)) {
            if (classStream == null) {
                throw new AssertionError("Missing imagenet/classes.json in jar resource");
            }
            Reader reader = new InputStreamReader(classStream, StandardCharsets.UTF_8);
            String[][] classes = JsonUtils.GSON.fromJson(reader, String[][].class);
            Map<String, Integer> map = new HashMap<>();
            for (var i = 0; i < classes.length; i++) {
                map.put(classes[i][2], i);
            }
            return map;
        } catch (IOException e) {
            throw new AssertionError("Failed to read imagenet/classes.json file.", e);
        }
    }
}
