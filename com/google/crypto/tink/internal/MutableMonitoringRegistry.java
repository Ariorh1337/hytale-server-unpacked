/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.internal;

import com.google.crypto.tink.internal.KeysetHandleInterface;
import com.google.crypto.tink.internal.MonitoringAnnotations;
import com.google.crypto.tink.internal.MonitoringClient;
import com.google.crypto.tink.internal.MonitoringUtil;
import java.util.concurrent.atomic.AtomicReference;

public final class MutableMonitoringRegistry {
    private static final MutableMonitoringRegistry GLOBAL_INSTANCE = new MutableMonitoringRegistry();
    private static final DoNothingClient DO_NOTHING_CLIENT = new DoNothingClient();
    private final AtomicReference<MonitoringClient> monitoringClient = new AtomicReference();

    public static MutableMonitoringRegistry globalInstance() {
        return GLOBAL_INSTANCE;
    }

    public synchronized void clear() {
        this.monitoringClient.set(null);
    }

    public synchronized void registerMonitoringClient(MonitoringClient client) {
        if (this.monitoringClient.get() != null) {
            throw new IllegalStateException("a monitoring client has already been registered");
        }
        this.monitoringClient.set(client);
    }

    public MonitoringClient getMonitoringClient() {
        MonitoringClient client = this.monitoringClient.get();
        if (client == null) {
            return DO_NOTHING_CLIENT;
        }
        return client;
    }

    private static class DoNothingClient
    implements MonitoringClient {
        private DoNothingClient() {
        }

        @Override
        public MonitoringClient.Logger createLogger(KeysetHandleInterface keysetInfo, MonitoringAnnotations annotations, String primitive, String api) {
            return MonitoringUtil.DO_NOTHING_LOGGER;
        }
    }
}

