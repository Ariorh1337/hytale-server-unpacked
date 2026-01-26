/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.util;

import io.sentry.SentryIntegrationPackageStorage;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import java.util.List;
import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DebugMetaPropertiesApplier {
    @NotNull
    public static String DEBUG_META_PROPERTIES_FILENAME = "sentry-debug-meta.properties";

    public static void apply(@NotNull SentryOptions options, @Nullable List<Properties> debugMetaProperties) {
        if (debugMetaProperties != null) {
            DebugMetaPropertiesApplier.applyToOptions(options, debugMetaProperties);
            DebugMetaPropertiesApplier.applyBuildTool(options, debugMetaProperties);
            DebugMetaPropertiesApplier.applyDistributionOptions(options, debugMetaProperties);
        }
    }

    public static void applyToOptions(@NotNull SentryOptions options, @Nullable List<Properties> debugMetaProperties) {
        if (debugMetaProperties != null) {
            DebugMetaPropertiesApplier.applyBundleIds(options, debugMetaProperties);
            DebugMetaPropertiesApplier.applyProguardUuid(options, debugMetaProperties);
        }
    }

    private static void applyBundleIds(@NotNull SentryOptions options, @NotNull List<Properties> debugMetaProperties) {
        if (options.getBundleIds().isEmpty()) {
            for (Properties properties : debugMetaProperties) {
                String[] bundleIds;
                @Nullable String bundleIdStrings = properties.getProperty("io.sentry.bundle-ids");
                options.getLogger().log(SentryLevel.DEBUG, "Bundle IDs found: %s", bundleIdStrings);
                if (bundleIdStrings == null) continue;
                for (String bundleId : bundleIds = bundleIdStrings.split(",", -1)) {
                    options.addBundleId(bundleId);
                }
            }
        }
    }

    private static void applyProguardUuid(@NotNull SentryOptions options, @NotNull List<Properties> debugMetaProperties) {
        if (options.getProguardUuid() == null) {
            for (Properties properties : debugMetaProperties) {
                @Nullable String proguardUuid = DebugMetaPropertiesApplier.getProguardUuid(properties);
                if (proguardUuid == null) continue;
                options.getLogger().log(SentryLevel.DEBUG, "Proguard UUID found: %s", proguardUuid);
                options.setProguardUuid(proguardUuid);
                break;
            }
        }
    }

    private static void applyBuildTool(@NotNull SentryOptions options, @NotNull List<Properties> debugMetaProperties) {
        for (Properties properties : debugMetaProperties) {
            @Nullable String buildTool = DebugMetaPropertiesApplier.getBuildTool(properties);
            if (buildTool == null) continue;
            @Nullable String buildToolVersion = DebugMetaPropertiesApplier.getBuildToolVersion(properties);
            if (buildToolVersion == null) {
                buildToolVersion = "unknown";
            }
            options.getLogger().log(SentryLevel.DEBUG, "Build tool found: %s, version %s", buildTool, buildToolVersion);
            SentryIntegrationPackageStorage.getInstance().addPackage(buildTool, buildToolVersion);
            break;
        }
    }

    @Nullable
    public static String getProguardUuid(@NotNull Properties debugMetaProperties) {
        return debugMetaProperties.getProperty("io.sentry.ProguardUuids");
    }

    @Nullable
    public static String getBuildTool(@NotNull Properties debugMetaProperties) {
        return debugMetaProperties.getProperty("io.sentry.build-tool");
    }

    @Nullable
    public static String getBuildToolVersion(@NotNull Properties debugMetaProperties) {
        return debugMetaProperties.getProperty("io.sentry.build-tool-version");
    }

    private static void applyDistributionOptions(@NotNull SentryOptions options, @NotNull List<Properties> debugMetaProperties) {
        for (Properties properties : debugMetaProperties) {
            @Nullable String orgSlug = DebugMetaPropertiesApplier.getDistributionOrgSlug(properties);
            @Nullable String projectSlug = DebugMetaPropertiesApplier.getDistributionProjectSlug(properties);
            @Nullable String orgAuthToken = DebugMetaPropertiesApplier.getDistributionAuthToken(properties);
            @Nullable String buildConfiguration = DebugMetaPropertiesApplier.getDistributionBuildConfiguration(properties);
            if (orgSlug == null && projectSlug == null && orgAuthToken == null && buildConfiguration == null) continue;
            @NotNull SentryOptions.DistributionOptions distributionOptions = options.getDistribution();
            if (orgSlug != null && !orgSlug.isEmpty() && distributionOptions.orgSlug.isEmpty()) {
                options.getLogger().log(SentryLevel.DEBUG, "Distribution org slug found: %s", orgSlug);
                distributionOptions.orgSlug = orgSlug;
            }
            if (projectSlug != null && !projectSlug.isEmpty() && distributionOptions.projectSlug.isEmpty()) {
                options.getLogger().log(SentryLevel.DEBUG, "Distribution project slug found: %s", projectSlug);
                distributionOptions.projectSlug = projectSlug;
            }
            if (orgAuthToken != null && !orgAuthToken.isEmpty() && distributionOptions.orgAuthToken.isEmpty()) {
                options.getLogger().log(SentryLevel.DEBUG, "Distribution org auth token found", new Object[0]);
                distributionOptions.orgAuthToken = orgAuthToken;
            }
            if (buildConfiguration == null || buildConfiguration.isEmpty() || distributionOptions.buildConfiguration != null) break;
            options.getLogger().log(SentryLevel.DEBUG, "Distribution build configuration found: %s", buildConfiguration);
            distributionOptions.buildConfiguration = buildConfiguration;
            break;
        }
    }

    @Nullable
    private static String getDistributionOrgSlug(@NotNull Properties debugMetaProperties) {
        return debugMetaProperties.getProperty("io.sentry.distribution.org-slug");
    }

    @Nullable
    private static String getDistributionProjectSlug(@NotNull Properties debugMetaProperties) {
        return debugMetaProperties.getProperty("io.sentry.distribution.project-slug");
    }

    @Nullable
    private static String getDistributionAuthToken(@NotNull Properties debugMetaProperties) {
        return debugMetaProperties.getProperty("io.sentry.distribution.auth-token");
    }

    @Nullable
    private static String getDistributionBuildConfiguration(@NotNull Properties debugMetaProperties) {
        return debugMetaProperties.getProperty("io.sentry.distribution.build-configuration");
    }
}

