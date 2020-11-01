package com.caponetto.service.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import ai.djl.Application;
import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import com.caponetto.model.text.TextDescriptor;

@ApplicationScoped
public class TextServiceImpl implements TextService {

    @Override
    public List<String> getModels() {
        final List<String> loadedModels = new ArrayList<>();
        try (ZooModel<String, Classifications> model = ModelZoo.loadModel(buildCriteria())) {
            loadedModels.add(model.getName());
        } catch (MalformedModelException | ModelNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return loadedModels;
    }

    @Override
    public TextDescriptor analyzeSentiment(final String text)
            throws MalformedModelException, ModelNotFoundException, IOException, TranslateException {
        try (ZooModel<String, Classifications> model = ModelZoo.loadModel(buildCriteria());
             Predictor<String, Classifications> predictor = model.newPredictor()) {
            final Classifications.Classification bestClass = predictor.predict(text)
                    .items()
                    .stream()
                    .max(Comparator.comparing(Classifications.Classification::getProbability))
                    .get();

            boolean isPositive = bestClass.getClassName().equals("Positive");
            int probability = (int) (bestClass.getProbability() * 100);
            return new TextDescriptor(isPositive, probability);
        }
    }

    private Criteria<String, Classifications> buildCriteria() {
        return Criteria.builder()
                .optApplication(Application.NLP.SENTIMENT_ANALYSIS)
                .setTypes(String.class, Classifications.class)
                .optProgress(new ProgressBar())
                .build();
    }
}
