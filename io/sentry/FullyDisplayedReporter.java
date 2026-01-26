/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class FullyDisplayedReporter {
    @NotNull
    private static final FullyDisplayedReporter instance = new FullyDisplayedReporter();
    @NotNull
    private final List<FullyDisplayedReporterListener> listeners = new CopyOnWriteArrayList<FullyDisplayedReporterListener>();

    private FullyDisplayedReporter() {
    }

    @NotNull
    public static FullyDisplayedReporter getInstance() {
        return instance;
    }

    public void registerFullyDrawnListener(@NotNull FullyDisplayedReporterListener listener) {
        this.listeners.add(listener);
    }

    public void reportFullyDrawn() {
        @NotNull Iterator<FullyDisplayedReporterListener> listenerIterator = this.listeners.iterator();
        this.listeners.clear();
        while (listenerIterator.hasNext()) {
            listenerIterator.next().onFullyDrawn();
        }
    }

    @ApiStatus.Internal
    public static interface FullyDisplayedReporterListener {
        public void onFullyDrawn();
    }
}

