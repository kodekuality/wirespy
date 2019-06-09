package org.kodekuality.wirespy.report;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.helper.ConditionalHelpers;
import com.github.jknack.handlebars.helper.StringHelpers;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import org.kodekuality.wirespy.protocol.Frame;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Stream;

public class WirespyReport {
    public static WirespyReport wirespyReport(File baseDir) {
        final Handlebars handlebars = new Handlebars(new ClassPathTemplateLoader("", ""));
        StringHelpers.register(handlebars);
        Stream.of(ConditionalHelpers.values()).forEach(x -> handlebars.registerHelper(x.name(), x));
        handlebars.registerHelper("optionalPresentString", new Helper<Object>() {
            @Override
            public Object apply(Object context, Options options) throws IOException {
                if (context instanceof Optional) {
                    if (((Optional) context).isPresent()) {
                        return options.fn(new String((byte[]) ((Optional) context).get()));
                    }
                }
                return "";
            }
        });

        return new WirespyReport(
                baseDir,
                new CopyStaticResourcesService(),
                new GenerateReportService(() -> handlebars.compile("template/report.template.html"), x -> x)
        );
    }

    public static WirespyReport wirespyReport(File baseDir, Callable<Template> handlebarsTemplate) {
        return new WirespyReport(
                baseDir,
                new CopyStaticResourcesService(),
                new GenerateReportService(handlebarsTemplate, x -> x)
        );
    }

    public static WirespyReport wirespyReport(File baseDir, Callable<Template> handlebarsTemplate, Function<Frame, Object> frameViewFactory) {
        return new WirespyReport(
                baseDir,
                new CopyStaticResourcesService(),
                new GenerateReportService(handlebarsTemplate, frameViewFactory)
        );
    }

    private final File baseDir;
    private final CopyStaticResourcesService copyStaticResourcesService;
    private final GenerateReportService generateReportService;
    private final AtomicBoolean staticGenerated = new AtomicBoolean(false);

    public WirespyReport(File baseDir, CopyStaticResourcesService copyStaticResourcesService, GenerateReportService generateReportService) {
        this.baseDir = baseDir;
        this.copyStaticResourcesService = copyStaticResourcesService;
        this.generateReportService = generateReportService;
    }

    public WirespyReport generate(String file, Request request) throws IOException {
        if (!staticGenerated.getAndSet(true)) {
            copyStaticResourcesService.copyTo(baseDir);
        }
        try {
            generateReportService.generate(new File(baseDir, file), request);
        } catch (IOException | RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
        return this;
    }


}
