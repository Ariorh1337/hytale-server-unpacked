/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry.config;

import io.sentry.SystemOutLogger;
import io.sentry.config.ClasspathPropertiesLoader;
import io.sentry.config.CompositePropertiesProvider;
import io.sentry.config.EnvironmentVariablePropertiesProvider;
import io.sentry.config.FilesystemPropertiesLoader;
import io.sentry.config.PropertiesProvider;
import io.sentry.config.SimplePropertiesProvider;
import io.sentry.config.SystemPropertyPropertiesProvider;
import java.util.ArrayList;
import java.util.Properties;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class PropertiesProviderFactory {
    @NotNull
    public static PropertiesProvider create() {
        Properties runDirectoryProperties;
        Properties properties;
        String environmentVariablesLocation;
        Properties properties2;
        SystemOutLogger logger = new SystemOutLogger();
        ArrayList<PropertiesProvider> providers = new ArrayList<PropertiesProvider>();
        providers.add(new SystemPropertyPropertiesProvider());
        providers.add(new EnvironmentVariablePropertiesProvider());
        String systemPropertyLocation = System.getProperty("sentry.properties.file");
        if (systemPropertyLocation != null && (properties2 = new FilesystemPropertiesLoader(systemPropertyLocation, logger).load()) != null) {
            providers.add(new SimplePropertiesProvider(properties2));
        }
        if ((environmentVariablesLocation = System.getenv("SENTRY_PROPERTIES_FILE")) != null && (properties = new FilesystemPropertiesLoader(environmentVariablesLocation, logger).load()) != null) {
            providers.add(new SimplePropertiesProvider(properties));
        }
        if ((properties = new ClasspathPropertiesLoader(logger).load()) != null) {
            providers.add(new SimplePropertiesProvider(properties));
        }
        if ((runDirectoryProperties = new FilesystemPropertiesLoader("sentry.properties", logger, false).load()) != null) {
            providers.add(new SimplePropertiesProvider(runDirectoryProperties));
        }
        return new CompositePropertiesProvider(providers);
    }
}

