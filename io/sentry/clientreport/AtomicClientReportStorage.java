/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.clientreport;

import io.sentry.DataCategory;
import io.sentry.clientreport.ClientReportKey;
import io.sentry.clientreport.DiscardReason;
import io.sentry.clientreport.DiscardedEvent;
import io.sentry.clientreport.IClientReportStorage;
import io.sentry.util.LazyEvaluator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
final class AtomicClientReportStorage
implements IClientReportStorage {
    @NotNull
    private final LazyEvaluator<Map<ClientReportKey, AtomicLong>> lostEventCounts = new LazyEvaluator<Map>(() -> {
        ConcurrentHashMap<ClientReportKey, AtomicLong> modifyableEventCountsForInit = new ConcurrentHashMap<ClientReportKey, AtomicLong>();
        for (DiscardReason discardReason : DiscardReason.values()) {
            for (DataCategory category : DataCategory.values()) {
                modifyableEventCountsForInit.put(new ClientReportKey(discardReason.getReason(), category.getCategory()), new AtomicLong(0L));
            }
        }
        return Collections.unmodifiableMap(modifyableEventCountsForInit);
    });

    @Override
    public void addCount(ClientReportKey key, Long count) {
        @Nullable AtomicLong quantity = this.lostEventCounts.getValue().get(key);
        if (quantity != null) {
            quantity.addAndGet(count);
        }
    }

    @Override
    public List<DiscardedEvent> resetCountsAndGet() {
        ArrayList<DiscardedEvent> discardedEvents = new ArrayList<DiscardedEvent>();
        Set<Map.Entry<ClientReportKey, AtomicLong>> entrySet = this.lostEventCounts.getValue().entrySet();
        for (Map.Entry<ClientReportKey, AtomicLong> entry : entrySet) {
            Long quantity = entry.getValue().getAndSet(0L);
            if (quantity <= 0L) continue;
            discardedEvents.add(new DiscardedEvent(entry.getKey().getReason(), entry.getKey().getCategory(), quantity));
        }
        return discardedEvents;
    }
}

