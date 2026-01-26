/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.internal;

import io.sentry.ISentryLifecycleToken;
import io.sentry.SentryIntegrationPackageStorage;
import io.sentry.util.AutoClosableReentrantLock;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class ManifestVersionReader {
    @Nullable
    private static volatile ManifestVersionReader INSTANCE;
    @NotNull
    private static final AutoClosableReentrantLock staticLock;
    private volatile boolean hasManifestBeenRead = false;
    @NotNull
    private final VersionInfoHolder versionInfo = new VersionInfoHolder();
    @NotNull
    private AutoClosableReentrantLock lock = new AutoClosableReentrantLock();

    @NotNull
    public static ManifestVersionReader getInstance() {
        if (INSTANCE == null) {
            try (@NotNull ISentryLifecycleToken ignored = staticLock.acquire();){
                if (INSTANCE == null) {
                    INSTANCE = new ManifestVersionReader();
                }
            }
        }
        return INSTANCE;
    }

    private ManifestVersionReader() {
    }

    @Nullable
    public VersionInfoHolder readOpenTelemetryVersion() {
        this.readManifestFiles();
        if (this.versionInfo.sdkVersion == null) {
            return null;
        }
        return this.versionInfo;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void readManifestFiles() {
        if (this.hasManifestBeenRead) {
            return;
        }
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            if (this.hasManifestBeenRead) {
                return;
            }
            @NotNull Enumeration<URL> resources = ClassLoader.getSystemClassLoader().getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                try {
                    @NotNull Manifest manifest = new Manifest(resources.nextElement().openStream());
                    @Nullable Attributes mainAttributes = manifest.getMainAttributes();
                    if (mainAttributes == null) continue;
                    @Nullable String name = mainAttributes.getValue("Sentry-Opentelemetry-SDK-Name");
                    @Nullable String version = mainAttributes.getValue("Implementation-Version");
                    @Nullable String sdkName = mainAttributes.getValue("Sentry-SDK-Name");
                    @Nullable String packageName = mainAttributes.getValue("Sentry-SDK-Package-Name");
                    if (name != null && version != null) {
                        String otelJavaagentVersion;
                        this.versionInfo.sdkName = name;
                        this.versionInfo.sdkVersion = version;
                        @Nullable String otelVersion = mainAttributes.getValue("Sentry-Opentelemetry-Version-Name");
                        if (otelVersion != null) {
                            SentryIntegrationPackageStorage.getInstance().addPackage("maven:io.opentelemetry:opentelemetry-sdk", otelVersion);
                            SentryIntegrationPackageStorage.getInstance().addIntegration("OpenTelemetry");
                        }
                        if ((otelJavaagentVersion = mainAttributes.getValue("Sentry-Opentelemetry-Javaagent-Version-Name")) != null) {
                            SentryIntegrationPackageStorage.getInstance().addPackage("maven:io.opentelemetry.javaagent:opentelemetry-javaagent", otelJavaagentVersion);
                            SentryIntegrationPackageStorage.getInstance().addIntegration("OpenTelemetry-Agent");
                        }
                        if (name.equals("sentry.java.opentelemetry.agentless")) {
                            SentryIntegrationPackageStorage.getInstance().addIntegration("OpenTelemetry-Agentless");
                        }
                        if (name.equals("sentry.java.opentelemetry.agentless-spring")) {
                            SentryIntegrationPackageStorage.getInstance().addIntegration("OpenTelemetry-Agentless-Spring");
                        }
                    }
                    if (sdkName == null || version == null || packageName == null || !sdkName.startsWith("sentry.java")) continue;
                    SentryIntegrationPackageStorage.getInstance().addPackage(packageName, version);
                }
                catch (Exception exception) {}
            }
        }
        catch (IOException iOException) {
        }
        finally {
            this.hasManifestBeenRead = true;
        }
    }

    static {
        staticLock = new AutoClosableReentrantLock();
    }

    public static final class VersionInfoHolder {
        @Nullable
        private volatile String sdkName;
        @Nullable
        private volatile String sdkVersion;

        @Nullable
        public String getSdkName() {
            return this.sdkName;
        }

        @Nullable
        public String getSdkVersion() {
            return this.sdkVersion;
        }
    }
}

