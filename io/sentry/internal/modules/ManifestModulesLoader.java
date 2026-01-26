/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Experimental
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.internal.modules;

import io.sentry.ILogger;
import io.sentry.SentryLevel;
import io.sentry.internal.modules.ModulesLoader;
import io.sentry.util.ClassLoaderUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Experimental
@ApiStatus.Internal
public final class ManifestModulesLoader
extends ModulesLoader {
    private final Pattern URL_LIB_PATTERN = Pattern.compile(".*/(.+)!/META-INF/MANIFEST.MF");
    private final Pattern NAME_AND_VERSION = Pattern.compile("(.*?)-(\\d+\\.\\d+.*).jar");
    private final ClassLoader classLoader;

    public ManifestModulesLoader(@NotNull ILogger logger) {
        this(ManifestModulesLoader.class.getClassLoader(), logger);
    }

    ManifestModulesLoader(@Nullable ClassLoader classLoader, @NotNull ILogger logger) {
        super(logger);
        this.classLoader = ClassLoaderUtils.classLoaderOrDefault(classLoader);
    }

    @Override
    protected Map<String, String> loadModules() {
        @NotNull HashMap<String, String> modules = new HashMap<String, String>();
        List<Module> detectedModules = this.detectModulesViaManifestFiles();
        for (Module module : detectedModules) {
            modules.put(module.name, module.version);
        }
        return modules;
    }

    @NotNull
    private List<Module> detectModulesViaManifestFiles() {
        @NotNull ArrayList<Module> modules = new ArrayList<Module>();
        try {
            @NotNull Enumeration<URL> manifestUrls = this.classLoader.getResources("META-INF/MANIFEST.MF");
            while (manifestUrls.hasMoreElements()) {
                @NotNull URL manifestUrl = manifestUrls.nextElement();
                @Nullable String originalName = this.extractDependencyNameFromUrl(manifestUrl);
                @Nullable Module module = this.convertOriginalNameToModule(originalName);
                if (module == null) continue;
                modules.add(module);
            }
        }
        catch (Throwable e) {
            this.logger.log(SentryLevel.ERROR, "Unable to detect modules via manifest files.", e);
        }
        return modules;
    }

    @Nullable
    private Module convertOriginalNameToModule(@Nullable String originalName) {
        if (originalName == null) {
            return null;
        }
        @NotNull Matcher matcher = this.NAME_AND_VERSION.matcher(originalName);
        if (matcher.matches() && matcher.groupCount() == 2) {
            @NotNull String moduleName = matcher.group(1);
            @NotNull String moduleVersion = matcher.group(2);
            return new Module(moduleName, moduleVersion);
        }
        return null;
    }

    @Nullable
    private String extractDependencyNameFromUrl(@NotNull URL url) {
        @NotNull String urlString = url.toString();
        @NotNull Matcher matcher = this.URL_LIB_PATTERN.matcher(urlString);
        if (matcher.matches() && matcher.groupCount() == 1) {
            return matcher.group(1);
        }
        return null;
    }

    private static final class Module {
        @NotNull
        private final String name;
        @NotNull
        private final String version;

        public Module(@NotNull String name, @NotNull String version) {
            this.name = name;
            this.version = version;
        }
    }
}

