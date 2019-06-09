package org.kodekuality.wirespy.service.concurrency;

import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConcurrencyUtilsTest {
    @Test
    public void noExceptionThrown() throws ExecutionException, InterruptedException {
        Future future = mock(Future.class);

        when(future.get()).thenThrow(ExecutionException.class);

        ConcurrencyUtils.waitSilently(future);
    }
    @Test
    public void runSilently() throws Exception {
        Callable callable = mock(Callable.class);

        when(callable.call()).thenThrow(ExecutionException.class);

        ConcurrencyUtils.runSilently(callable);
    }

    @Test(expected = IOException.class)
    public void getOrRethrow() throws Exception {
        Future future = mock(Future.class);

        when(future.get()).thenThrow(ExecutionException.class);

        ConcurrencyUtils.getOrRethrow(future);
    }

    @Test
    public void sleepSilently() throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ConcurrencyUtils.sleepSilently(10000);
            }
        });
        thread.start();
        Thread.sleep(100);

        thread.interrupt();
    }
}