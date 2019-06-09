package org.kodekuality.wirespy.protocol.fix;

import org.kodekuality.wirespy.protocol.Frame;
import org.kodekuality.wirespy.protocol.FrameSequencer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FixFrameSequencer implements FrameSequencer {
    @Override
    public boolean sequence(InputStream inputStream, Consumer<Frame> receiver) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

        int read = bufferedInputStream.read();

        List<FixFrame.Field> fields = new ArrayList<>();
        StringBuffer stringBuffer = new StringBuffer();
        while (read != -1) {
            if (read != 1) stringBuffer.append((char) read);
            else {
                String tagAndValue = stringBuffer.toString();
                FixFrame.Field field = toField(tagAndValue);
                fields.add(field);

                if (field.getTag() == 10) { // checksum
                    receiver.accept(new FixFrame(fields));
                    fields = new ArrayList<>();
                }

                stringBuffer = new StringBuffer();
            }
            read = bufferedInputStream.read();
        }

        return false;
    }

    private FixFrame.Field toField(String tagAndValue) {
        String[] split = tagAndValue.split("=");
        return new FixFrame.Field(
                Integer.parseInt(split[0]),
                split.length > 1 ? split[1] : ""
        );
    }
}
