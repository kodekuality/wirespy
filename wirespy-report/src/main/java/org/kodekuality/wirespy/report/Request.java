package org.kodekuality.wirespy.report;

import org.kodekuality.wirespy.messages.Message;

import java.util.List;

public class Request {
    private final String title;
    private final List<Message> messages;

    public Request(String title, List<Message> messages) {
        this.title = title;
        this.messages = messages;
    }

    public String getTitle() {
        return title;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
