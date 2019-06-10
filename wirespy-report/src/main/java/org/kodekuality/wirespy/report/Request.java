package org.kodekuality.wirespy.report;


import org.kodekuality.wirespy.watcher.message.WireSpyMessage;

import java.util.List;

public class Request {
    private final String title;
    private final List<WireSpyMessage> messages;

    public Request(String title, List<WireSpyMessage> messages) {
        this.title = title;
        this.messages = messages;
    }

    public String getTitle() {
        return title;
    }

    public List<WireSpyMessage> getMessages() {
        return messages;
    }
}
