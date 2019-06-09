package org.kodekuality.wirespy.protocol.fix;

import org.kodekuality.wirespy.protocol.Frame;

import java.util.List;
import java.util.Objects;

public class FixFrame implements Frame {
    private final List<Field> fields;

    public FixFrame(List<Field> fields) {
        this.fields = fields;
    }

    public List<Field> getFields() {
        return fields;
    }

    public static class Field {
        private final int tag;
        private final String value;

        public Field(int tag, String value) {
            this.tag = tag;
            this.value = value;
        }

        public int getTag() {
            return tag;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "Field{" +
                    "tag=" + tag +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "FixFrame{" +
                "fields=" + fields +
                '}';
    }
}
