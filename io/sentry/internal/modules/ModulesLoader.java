/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.internal.modules;

import io.sentry.ILogger;
import io.sentry.ISentryLifecycleToken;
import io.sentry.SentryLevel;
import io.sentry.internal.modules.IModulesLoader;
import io.sentry.util.AutoClosableReentrantLock;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.TreeMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public abstract class ModulesLoader
implements IModulesLoader {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    public static final String EXTERNAL_MODULES_FILENAME = "sentry-external-modules.txt";
    @NotNull
    protected final ILogger logger;
    @NotNull
    private final AutoClosableReentrantLock modulesLock = new AutoClosableReentrantLock();
    @Nullable
    private volatile Map<String, String> cachedModules = null;

    public ModulesLoader(@NotNull ILogger logger) {
        this.logger = logger;
    }

    @Override
    @Nullable
    public Map<String, String> getOrLoadModules() {
        if (this.cachedModules == null) {
            try (@NotNull ISentryLifecycleToken ignored = this.modulesLock.acquire();){
                if (this.cachedModules == null) {
                    this.cachedModules = this.loadModules();
                }
            }
        }
        return this.cachedModules;
    }

    protected abstract Map<String, String> loadModules();

    protected Map<String, String> parseStream(@NotNull InputStream stream) {
        TreeMap<String, String> modules = new TreeMap<String, String>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, UTF_8));){
            String module = reader.readLine();
            while (module != null) {
                int sep = module.lastIndexOf(58);
                String group = module.substring(0, sep);
                String version = module.substring(sep + 1);
                modules.put(group, version);
                module = reader.readLine();
            }
            this.logger.log(SentryLevel.DEBUG, "Extracted %d modules from resources.", modules.size());
        }
        catch (IOException e) {
            this.logger.log(SentryLevel.ERROR, "Error extracting modules.", e);
        }
        catch (RuntimeException e) {
            this.logger.log(SentryLevel.ERROR, e, "%s file is malformed.", EXTERNAL_MODULES_FILENAME);
        }
        return modules;
    }
}

