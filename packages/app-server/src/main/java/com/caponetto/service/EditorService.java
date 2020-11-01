package com.caponetto.service;

import java.util.List;

public interface EditorService {

    /**
     * Try to load all available models and return their names.
     * The models will be downloaded in case they are not available yet.
     *
     * @return List of model names that can be loaded.
     */
    List<String> getModels();
}
