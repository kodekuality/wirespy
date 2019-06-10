package org.kodekuality.wirespy.service.concurrency;

import org.kodekuality.wirespy.service.tcp.socket.ListenerSocketFactory;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;

public class ConcurrencyUtils {
    private ConcurrencyUtils () {}

    public static void waitSilently (Future<?> future) {
        try {
            future.get();
        } catch (InterruptedException | ExecutionException ignore) {

        }
    }

    public static void runSilently(Callable<?> callable) {
        try {
            callable.call();
        } catch (Exception ignored) {

        }
    }

    public static void sleepSilently(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T getOrRethrow(Future<T> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ListenerSocketFactory.NoLongerAcceptingException(e);
        }
    }
}
