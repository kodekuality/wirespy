package org.kodekuality.wirespy.report;

import com.github.jknack.handlebars.Template;
import org.kodekuality.wirespy.protocol.Frame;
import org.kodekuality.wirespy.watcher.message.WireSpyMessage;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GenerateReportService {
    private final Callable<Template> templateSupplier;
    private final Function<Frame, Object> frameViewFactory;

    public GenerateReportService(Callable<Template> templateSupplier, Function<Frame, Object> frameViewFactory) {
        this.templateSupplier = templateSupplier;
        this.frameViewFactory = frameViewFactory;
    }

    public void generate(File file, Request request) throws Exception {
        Template template = templateSupplier.call();

        try (Writer writer = new FileWriter(file)) {
            template.apply(new HashMap<String, Object>() {{
                put("title", request.getTitle());
                put("entities", request.getMessages().stream().flatMap(x -> Stream.of(x.getFrom(), x.getTo())).distinct().collect(Collectors.toList()));
                put("messages", toMessagesView(request.getMessages()));
            }}, writer);
        }
    }

    private List<MessageView> toMessagesView (List<WireSpyMessage> messages) {
        ArrayList<MessageView> result = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            result.add(toMessageView(i+1, messages.get(i)));
        }
        return result;
    }

    private MessageView toMessageView(int index, WireSpyMessage message) {
        return new MessageView(
                index,
                message.getFrom(),
                message.getTo(),
                message.getFrame().getClass().getSimpleName(),
                frameViewFactory.apply(message.getFrame())
        );
    }

    private static class MessageView {
        private final int index;
        private final String from;
        private final String to;
        private final String type;
        private final Object frame;

        public MessageView(int index, String from, String to, String type, Object frame) {
            this.index = index;
            this.from = from;
            this.to = to;
            this.type = type;
            this.frame = frame;
        }

        public int getIndex() {
            return index;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }

        public String getType() {
            return type;
        }

        public Object getFrame() {
            return frame;
        }
    }
}
