package org.kodekuality.wirespy.report;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.helper.ConditionalHelpers;
import com.github.jknack.handlebars.helper.StringHelpers;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import org.kodekuality.wirespy.protocol.Frame;
import org.kodekuality.wirespy.watcher.message.WireSpyMessage;
import org.kodekuality.wirespy.watcher.message.WireSpyMessageReceiver;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WirespyReport implements WireSpyMessageReceiver {
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

    private final List<WireSpyMessage> messages = new CopyOnWriteArrayList<>();
    private final File baseDir;
    private final CopyStaticResourcesService copyStaticResourcesService;
    private final GenerateReportService generateReportService;
    private final AtomicBoolean staticGenerated = new AtomicBoolean(false);

    public WirespyReport(File baseDir, CopyStaticResourcesService copyStaticResourcesService, GenerateReportService generateReportService) {
        this.baseDir = baseDir;
        this.copyStaticResourcesService = copyStaticResourcesService;
        this.generateReportService = generateReportService;
    }

    public WirespyReport generate(String file, String title) throws IOException {
        if (!staticGenerated.getAndSet(true)) {
            copyStaticResourcesService.copyTo(baseDir);
        }
        try {
            generateReportService.generate(new File(baseDir, file), new Request(
                    title,
                    Collections.unmodifiableList(messages).stream()
                        .sorted(Comparator.comparingLong(WireSpyMessage::getNanoTimeRecorded))
                        .collect(Collectors.toList())
            ));
        } catch (IOException | RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
        return this;
    }


    @Override
    public void handle(WireSpyMessage message) {
        messages.add(message);
    }

    public void reset () {
        messages.clear();
    }
}
