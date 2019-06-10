package org.kodekuality.wirespy.report;

import org.kodekuality.wirespy.watcher.message.WireSpyMessage;
import org.kodekuality.wirespy.watcher.message.WireSpyMessageReceiver;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

public class WireSpyMessageCollector implements WireSpyMessageReceiver, Supplier<List<WireSpyMessage>> {
    private final List<WireSpyMessage> messages = new CopyOnWriteArrayList<>();

    @Override
    public List<WireSpyMessage> get() {
        return Collections.unmodifiableList(messages);
    }

    @Override
    public void handle(WireSpyMessage message) {
        messages.add(message);
    }

    public void reset () {
        messages.clear();
    }
}
