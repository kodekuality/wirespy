package org.kodekuality.wirespy.report;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;

public class CopyStaticResourcesServiceTest {
    @Test
    public void test() throws IOException {
        File baseDir = new File("build/test");
        CopyStaticResourcesService copyStaticResourcesService = new CopyStaticResourcesService();

        copyStaticResourcesService.copyTo(baseDir);

        assertThat(new File(baseDir, "sequence-diagram-resources/convert.js").exists(), CoreMatchers.is(true));
    }
}