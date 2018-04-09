package org.myproject.ecommerce.hadoop;

import java.io.File;
import java.io.FileFilter;

public class HadoopVersionFilter implements FileFilter {
    private static final String FORMAT = String.format("-%s.jar", BaseHadoopTest.PROJECT_VERSION);

    public HadoopVersionFilter() {
    }

    @Override
    public boolean accept(final File pathname) {
        return pathname.getName().endsWith(FORMAT);
    }
}