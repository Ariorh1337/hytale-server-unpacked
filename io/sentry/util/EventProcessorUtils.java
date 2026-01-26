/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.util;

import io.sentry.EventProcessor;
import io.sentry.internal.eventprocessor.EventProcessorAndOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.Nullable;

public final class EventProcessorUtils {
    public static List<EventProcessor> unwrap(@Nullable List<EventProcessorAndOrder> orderedEventProcessor) {
        ArrayList<EventProcessor> eventProcessors = new ArrayList<EventProcessor>();
        if (orderedEventProcessor != null) {
            for (EventProcessorAndOrder eventProcessorAndOrder : orderedEventProcessor) {
                eventProcessors.add(eventProcessorAndOrder.getEventProcessor());
            }
        }
        return new CopyOnWriteArrayList<EventProcessor>(eventProcessors);
    }
}

