/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.TestOnly
 */
package io.sentry;

import io.sentry.ISentryLifecycleToken;
import io.sentry.util.AutoClosableReentrantLock;
import java.io.File;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

@ApiStatus.Internal
public final class SentryCrashLastRunState {
    private static final SentryCrashLastRunState INSTANCE = new SentryCrashLastRunState();
    private boolean readCrashedLastRun;
    @Nullable
    private Boolean crashedLastRun;
    @NotNull
    private final AutoClosableReentrantLock crashedLastRunLock = new AutoClosableReentrantLock();

    private SentryCrashLastRunState() {
    }

    public static SentryCrashLastRunState getInstance() {
        return INSTANCE;
    }

    @Nullable
    public Boolean isCrashedLastRun(@Nullable String cacheDirPath, boolean deleteFile) {
        try (@NotNull ISentryLifecycleToken ignored = this.crashedLastRunLock.acquire();){
            if (this.readCrashedLastRun) {
                Boolean bl = this.crashedLastRun;
                return bl;
            }
            if (cacheDirPath == null) {
                Boolean bl = null;
                return bl;
            }
            this.readCrashedLastRun = true;
            File javaMarker = new File(cacheDirPath, "last_crash");
            File nativeMarker = new File(cacheDirPath, ".sentry-native/last_crash");
            boolean exists = false;
            try {
                if (javaMarker.exists()) {
                    exists = true;
                    javaMarker.delete();
                } else if (nativeMarker.exists()) {
                    exists = true;
                    if (deleteFile) {
                        nativeMarker.delete();
                    }
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            this.crashedLastRun = exists;
        }
        return this.crashedLastRun;
    }

    public void setCrashedLastRun(boolean crashedLastRun) {
        try (@NotNull ISentryLifecycleToken ignored = this.crashedLastRunLock.acquire();){
            if (!this.readCrashedLastRun) {
                this.crashedLastRun = crashedLastRun;
                this.readCrashedLastRun = true;
            }
        }
    }

    @TestOnly
    public void reset() {
        try (@NotNull ISentryLifecycleToken ignored = this.crashedLastRunLock.acquire();){
            this.readCrashedLastRun = false;
            this.crashedLastRun = null;
        }
    }
}

