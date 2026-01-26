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

import io.sentry.ILogger;
import io.sentry.ISentryLifecycleToken;
import io.sentry.SentryLevel;
import io.sentry.protocol.SentryPackage;
import io.sentry.util.AutoClosableReentrantLock;
import io.sentry.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

@ApiStatus.Internal
public final class SentryIntegrationPackageStorage {
    @Nullable
    private static volatile SentryIntegrationPackageStorage INSTANCE;
    @NotNull
    private static final AutoClosableReentrantLock staticLock;
    @Nullable
    private static volatile Boolean mixedVersionsDetected;
    @NotNull
    private static final AutoClosableReentrantLock mixedVersionsLock;
    private final Set<String> integrations = new CopyOnWriteArraySet<String>();
    private final Set<SentryPackage> packages = new CopyOnWriteArraySet<SentryPackage>();

    @NotNull
    public static SentryIntegrationPackageStorage getInstance() {
        if (INSTANCE == null) {
            try (@NotNull ISentryLifecycleToken ignored = staticLock.acquire();){
                if (INSTANCE == null) {
                    INSTANCE = new SentryIntegrationPackageStorage();
                }
            }
        }
        return INSTANCE;
    }

    private SentryIntegrationPackageStorage() {
    }

    public void addIntegration(@NotNull String integration) {
        Objects.requireNonNull(integration, "integration is required.");
        this.integrations.add(integration);
    }

    @NotNull
    public Set<String> getIntegrations() {
        return this.integrations;
    }

    public void addPackage(@NotNull String name, @NotNull String version) {
        Objects.requireNonNull(name, "name is required.");
        Objects.requireNonNull(version, "version is required.");
        SentryPackage newPackage = new SentryPackage(name, version);
        this.packages.add(newPackage);
        try (@NotNull ISentryLifecycleToken ignored = mixedVersionsLock.acquire();){
            mixedVersionsDetected = null;
        }
    }

    @NotNull
    public Set<SentryPackage> getPackages() {
        return this.packages;
    }

    public boolean checkForMixedVersions(@NotNull ILogger logger) {
        @Nullable Boolean mixedVersionsDetectedBefore = mixedVersionsDetected;
        if (mixedVersionsDetectedBefore != null) {
            return mixedVersionsDetectedBefore;
        }
        try (@NotNull ISentryLifecycleToken ignored = mixedVersionsLock.acquire();){
            @NotNull String sdkVersion = "8.29.0";
            boolean mixedVersionsDetectedThisCheck = false;
            for (SentryPackage pkg : this.packages) {
                if (!pkg.getName().startsWith("maven:io.sentry:") || "8.29.0".equalsIgnoreCase(pkg.getVersion())) continue;
                logger.log(SentryLevel.ERROR, "The Sentry SDK has been configured with mixed versions. Expected %s to match core SDK version %s but was %s", pkg.getName(), "8.29.0", pkg.getVersion());
                mixedVersionsDetectedThisCheck = true;
            }
            if (mixedVersionsDetectedThisCheck) {
                logger.log(SentryLevel.ERROR, "^^^^^^^^^^^^^^^^^^^^^^^^^^^^", new Object[0]);
                logger.log(SentryLevel.ERROR, "^^^^^^^^^^^^^^^^^^^^^^^^^^^^", new Object[0]);
                logger.log(SentryLevel.ERROR, "^^^^^^^^^^^^^^^^^^^^^^^^^^^^", new Object[0]);
                logger.log(SentryLevel.ERROR, "^^^^^^^^^^^^^^^^^^^^^^^^^^^^", new Object[0]);
            }
            mixedVersionsDetected = mixedVersionsDetectedThisCheck;
            boolean bl = mixedVersionsDetectedThisCheck;
            return bl;
        }
    }

    @TestOnly
    public void clearStorage() {
        this.integrations.clear();
        this.packages.clear();
    }

    static {
        staticLock = new AutoClosableReentrantLock();
        mixedVersionsDetected = null;
        mixedVersionsLock = new AutoClosableReentrantLock();
    }
}

