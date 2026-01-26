/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.featureflags;

import io.sentry.ISentryLifecycleToken;
import io.sentry.ScopeType;
import io.sentry.SentryOptions;
import io.sentry.featureflags.IFeatureFlagBuffer;
import io.sentry.featureflags.NoOpFeatureFlagBuffer;
import io.sentry.protocol.FeatureFlag;
import io.sentry.protocol.FeatureFlags;
import io.sentry.util.AutoClosableReentrantLock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class FeatureFlagBuffer
implements IFeatureFlagBuffer {
    @NotNull
    private volatile CopyOnWriteArrayList<FeatureFlagEntry> flags;
    @NotNull
    private final AutoClosableReentrantLock lock = new AutoClosableReentrantLock();
    private int maxSize;

    private FeatureFlagBuffer(int maxSize) {
        this.maxSize = maxSize;
        this.flags = new CopyOnWriteArrayList();
    }

    private FeatureFlagBuffer(int maxSize, @NotNull CopyOnWriteArrayList<FeatureFlagEntry> flags) {
        this.maxSize = maxSize;
        this.flags = flags;
    }

    private FeatureFlagBuffer(@NotNull FeatureFlagBuffer other) {
        this.maxSize = other.maxSize;
        this.flags = new CopyOnWriteArrayList<FeatureFlagEntry>(other.flags);
    }

    @Override
    public void add(@Nullable String flag, @Nullable Boolean result) {
        if (flag == null || result == null) {
            return;
        }
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            int size = this.flags.size();
            for (int i = 0; i < size; ++i) {
                @NotNull FeatureFlagEntry entry = this.flags.get(i);
                if (!entry.flag.equals(flag)) continue;
                this.flags.remove(i);
                break;
            }
            this.flags.add(new FeatureFlagEntry(flag, result, System.nanoTime()));
            if (this.flags.size() > this.maxSize) {
                this.flags.remove(0);
            }
        }
    }

    @Override
    @Nullable
    public FeatureFlags getFeatureFlags() {
        ArrayList<FeatureFlag> featureFlags = new ArrayList<FeatureFlag>();
        for (FeatureFlagEntry entry : this.flags) {
            featureFlags.add(entry.toFeatureFlag());
        }
        return new FeatureFlags(featureFlags);
    }

    @Override
    @NotNull
    public IFeatureFlagBuffer clone() {
        return new FeatureFlagBuffer(this);
    }

    @NotNull
    public static IFeatureFlagBuffer create(@NotNull SentryOptions options) {
        int maxFeatureFlags = options.getMaxFeatureFlags();
        if (maxFeatureFlags > 0) {
            return new FeatureFlagBuffer(maxFeatureFlags);
        }
        return NoOpFeatureFlagBuffer.getInstance();
    }

    @NotNull
    public static IFeatureFlagBuffer merged(@NotNull SentryOptions options, @Nullable IFeatureFlagBuffer globalBuffer, @Nullable IFeatureFlagBuffer isolationBuffer, @Nullable IFeatureFlagBuffer currentBuffer) {
        int maxSize = options.getMaxFeatureFlags();
        if (maxSize <= 0) {
            return NoOpFeatureFlagBuffer.getInstance();
        }
        return FeatureFlagBuffer.merged(maxSize, globalBuffer instanceof FeatureFlagBuffer ? (FeatureFlagBuffer)globalBuffer : null, isolationBuffer instanceof FeatureFlagBuffer ? (FeatureFlagBuffer)isolationBuffer : null, currentBuffer instanceof FeatureFlagBuffer ? (FeatureFlagBuffer)currentBuffer : null);
    }

    @NotNull
    private static IFeatureFlagBuffer merged(int maxSize, @Nullable FeatureFlagBuffer globalBuffer, @Nullable FeatureFlagBuffer isolationBuffer, @Nullable FeatureFlagBuffer currentBuffer) {
        int currentSize;
        @Nullable CopyOnWriteArrayList<FeatureFlagEntry> globalFlags = globalBuffer == null ? null : globalBuffer.flags;
        @Nullable CopyOnWriteArrayList<FeatureFlagEntry> isolationFlags = isolationBuffer == null ? null : isolationBuffer.flags;
        @Nullable CopyOnWriteArrayList<FeatureFlagEntry> currentFlags = currentBuffer == null ? null : currentBuffer.flags;
        int globalSize = globalFlags == null ? 0 : globalFlags.size();
        int isolationSize = isolationFlags == null ? 0 : isolationFlags.size();
        int n = currentSize = currentFlags == null ? 0 : currentFlags.size();
        if (globalSize == 0 && isolationSize == 0 && currentSize == 0) {
            return NoOpFeatureFlagBuffer.getInstance();
        }
        int globalIndex = globalSize - 1;
        int isolationIndex = isolationSize - 1;
        int currentIndex = currentSize - 1;
        @Nullable FeatureFlagEntry globalEntry = globalFlags == null || globalIndex < 0 ? null : globalFlags.get(globalIndex);
        @Nullable FeatureFlagEntry isolationEntry = isolationFlags == null || isolationIndex < 0 ? null : isolationFlags.get(isolationIndex);
        @Nullable FeatureFlagEntry currentEntry = currentFlags == null || currentIndex < 0 ? null : currentFlags.get(currentIndex);
        @NotNull LinkedHashMap<String, FeatureFlagEntry> uniqueFlags = new LinkedHashMap<String, FeatureFlagEntry>(maxSize);
        while (uniqueFlags.size() < maxSize && (globalEntry != null || isolationEntry != null || currentEntry != null)) {
            FeatureFlagEntry entryToAdd = null;
            ScopeType selectedBuffer = null;
            if (globalEntry != null && (entryToAdd == null || globalEntry.nanos > entryToAdd.nanos)) {
                entryToAdd = globalEntry;
                selectedBuffer = ScopeType.GLOBAL;
            }
            if (isolationEntry != null && (entryToAdd == null || isolationEntry.nanos > entryToAdd.nanos)) {
                entryToAdd = isolationEntry;
                selectedBuffer = ScopeType.ISOLATION;
            }
            if (currentEntry != null && (entryToAdd == null || currentEntry.nanos > entryToAdd.nanos)) {
                entryToAdd = currentEntry;
                selectedBuffer = ScopeType.CURRENT;
            }
            if (entryToAdd == null) break;
            if (!uniqueFlags.containsKey(entryToAdd.flag)) {
                uniqueFlags.put(entryToAdd.flag, entryToAdd);
            }
            if (ScopeType.CURRENT.equals((Object)selectedBuffer)) {
                currentEntry = currentFlags != null && currentIndex >= 0 ? currentFlags.get(--currentIndex) : null;
                continue;
            }
            if (ScopeType.ISOLATION.equals((Object)selectedBuffer)) {
                isolationEntry = isolationFlags != null && isolationIndex >= 0 ? isolationFlags.get(--isolationIndex) : null;
                continue;
            }
            if (!ScopeType.GLOBAL.equals((Object)selectedBuffer)) continue;
            globalEntry = globalFlags != null && globalIndex >= 0 ? globalFlags.get(--globalIndex) : null;
        }
        @NotNull ArrayList<V> resultList = new ArrayList(uniqueFlags.values());
        Collections.reverse(resultList);
        return new FeatureFlagBuffer(maxSize, new CopyOnWriteArrayList<FeatureFlagEntry>(resultList));
    }

    private static class FeatureFlagEntry {
        @NotNull
        private final String flag;
        private final boolean result;
        @NotNull
        private final Long nanos;

        public FeatureFlagEntry(@NotNull String flag, boolean result, @NotNull Long nanos) {
            this.flag = flag;
            this.result = result;
            this.nanos = nanos;
        }

        @NotNull
        public FeatureFlag toFeatureFlag() {
            return new FeatureFlag(this.flag, this.result);
        }
    }
}

