package org.kodekuality.wirespy.watcher.session;

public interface WireSpyWatcherSession {
    WireSpyWatcherSession startSession ();
    WireSpyWatcherSession stopSession ();
    WireSpyWatcherSession awaitTermination ();
}
