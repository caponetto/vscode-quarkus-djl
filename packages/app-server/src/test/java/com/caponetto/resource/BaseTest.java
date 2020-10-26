package com.caponetto.resource;

import java.io.File;

public abstract class BaseTest {

    protected String getPathFromTestFile(final String filename) {
        return new File(getClass()
                                .getClassLoader()
                                .getResource(filename)
                                .getFile())
                .getAbsolutePath();
    }
}
