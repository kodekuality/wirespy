package org.kodekuality.wirespy.report;

import org.apache.commons.io.FileUtils;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.regex.Pattern;

public class CopyStaticResourcesService {
    private static final String RESOURCES = "sequence-diagram-resources";

    public void copyTo (File baseDir) throws IOException {
        Reflections reflections = new Reflections(RESOURCES, new ResourcesScanner());
        Set<String> resources = reflections.getResources(Pattern.compile(".*"));

        for (String resource : resources) {
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resource)) {
                FileUtils.copyInputStreamToFile(inputStream, new File(baseDir, resource));
            }
        }
    }
}
