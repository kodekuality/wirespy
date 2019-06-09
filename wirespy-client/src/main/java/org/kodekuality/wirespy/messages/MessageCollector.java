package org.kodekuality.wirespy.messages;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class MessageCollector implements Consumer<Message> {
    private final List<Message> messages = new CopyOnWriteArrayList<>();

    @Override
    public void accept(Message message) {
        messages.add(message);
    }

    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }
}
