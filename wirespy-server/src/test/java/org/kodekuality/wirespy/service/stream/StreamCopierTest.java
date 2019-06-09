package org.kodekuality.wirespy.service.stream;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.io.*;
import java.util.concurrent.Semaphore;

import static org.junit.Assert.assertThat;

public class StreamCopierTest {
    @Test
    public void readingTerminatesWhenInputStreamFinishes() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("hi".getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        StreamCopier underTest = new StreamCopier("name", inputStream, outputStream);

        Boolean result = underTest.call();

        assertThat(result, CoreMatchers.is(true));
        assertThat(outputStream.toString(), CoreMatchers.is("hi"));
    }

    @Test
    public void readingBlocksUntilDataArrives() throws IOException, InterruptedException {
        PipedOutputStream output = new PipedOutputStream();
        PipedInputStream input  = new PipedInputStream(output);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        StreamCopier underTest = new StreamCopier("name", input, outputStream);

        Thread copyThread = new Thread(underTest::call);
        copyThread.start();

        output.write("asd".getBytes());

        Semaphore inputFinish = new Semaphore(0);
        Thread finalise = new Thread(() -> {
            try {
                output.close();
                inputFinish.release();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        finalise.start();
        inputFinish.acquire();
        copyThread.join();

        assertThat(outputStream.toString(), CoreMatchers.is("asd"));
    }
}