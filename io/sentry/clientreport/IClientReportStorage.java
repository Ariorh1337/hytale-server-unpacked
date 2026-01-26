/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package io.sentry.clientreport;

import io.sentry.clientreport.ClientReportKey;
import io.sentry.clientreport.DiscardedEvent;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface IClientReportStorage {
    public void addCount(ClientReportKey var1, Long var2);

    public List<DiscardedEvent> resetCountsAndGet();
}

