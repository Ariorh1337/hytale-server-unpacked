/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.plugin;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PluginClassLoader
extends URLClassLoader {
    public static final String THIRD_PARTY_LOADER_NAME = "ThirdPartyPlugin";
    @Nonnull
    private final PluginManager pluginManager;
    private final boolean inServerClassPath;
    @Nullable
    private JavaPlugin plugin;

    public PluginClassLoader(@Nonnull PluginManager pluginManager, boolean inServerClassPath, URL ... urls) {
        super(inServerClassPath ? "BuiltinPlugin" : THIRD_PARTY_LOADER_NAME, urls, null);
        this.inServerClassPath = inServerClassPath;
        this.pluginManager = pluginManager;
    }

    public boolean isInServerClassPath() {
        return this.inServerClassPath;
    }

    void setPlugin(@Nonnull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    @Nonnull
    protected Class<?> loadClass(@Nonnull String name, boolean resolve) throws ClassNotFoundException {
        return this.loadClass0(name, true);
    }

    @Nonnull
    private Class<?> loadClass0(@Nonnull String name, boolean useBridge) throws ClassNotFoundException {
        Class<?> loadClass2;
        try {
            loadClass2 = PluginManager.class.getClassLoader().loadClass(name);
            if (loadClass2 != null) {
                return loadClass2;
            }
        }
        catch (ClassNotFoundException loadClass2) {
            // empty catch block
        }
        try {
            loadClass2 = super.loadClass(name, false);
            if (loadClass2 != null) {
                return loadClass2;
            }
        }
        catch (ClassNotFoundException loadClass3) {
            // empty catch block
        }
        if (useBridge) {
            if (this.plugin != null) {
                try {
                    loadClass2 = this.pluginManager.getBridgeClassLoader().loadClass0(name, this, this.plugin.getManifest());
                    if (loadClass2 != null) {
                        return loadClass2;
                    }
                }
                catch (ClassNotFoundException loadClass4) {}
            } else {
                try {
                    loadClass2 = this.pluginManager.getBridgeClassLoader().loadClass0(name, this);
                    if (loadClass2 != null) {
                        return loadClass2;
                    }
                }
                catch (ClassNotFoundException classNotFoundException) {
                    // empty catch block
                }
            }
        }
        throw new ClassNotFoundException(name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    public Class<?> loadLocalClass(@Nonnull String name) throws ClassNotFoundException {
        Object object = this.getClassLoadingLock(name);
        synchronized (object) {
            Class<?> loadedClass = this.findLoadedClass(name);
            if (loadedClass == null) {
                try {
                    ClassLoader parent = this.getParent();
                    if (parent != null) {
                        loadedClass = parent.loadClass(name);
                    }
                }
                catch (ClassNotFoundException classNotFoundException) {
                    // empty catch block
                }
                if (loadedClass == null) {
                    loadedClass = this.loadClass0(name, false);
                }
            }
            return loadedClass;
        }
    }

    @Override
    @Nullable
    public URL getResource(@Nonnull String name) {
        URL resource = PluginManager.class.getClassLoader().getResource(name);
        if (resource != null) {
            return resource;
        }
        resource = super.getResource(name);
        if (resource != null) {
            return resource;
        }
        PluginManager.PluginBridgeClassLoader bridge = this.pluginManager.getBridgeClassLoader();
        resource = this.plugin != null ? bridge.getResource0(name, this, this.plugin.getManifest()) : bridge.getResource0(name, this);
        return resource;
    }

    @Override
    @Nonnull
    public Enumeration<URL> getResources(@Nonnull String name) throws IOException {
        ObjectArrayList<URL> results = new ObjectArrayList<URL>();
        Enumeration<URL> serverResources = PluginManager.class.getClassLoader().getResources(name);
        while (serverResources.hasMoreElements()) {
            results.add(serverResources.nextElement());
        }
        Enumeration<URL> pluginResources = super.getResources(name);
        while (pluginResources.hasMoreElements()) {
            results.add(pluginResources.nextElement());
        }
        PluginManager.PluginBridgeClassLoader bridge = this.pluginManager.getBridgeClassLoader();
        Enumeration<URL> bridgeResources = this.plugin != null ? bridge.getResources0(name, this, this.plugin.getManifest()) : bridge.getResources0(name, this);
        while (bridgeResources.hasMoreElements()) {
            results.add(bridgeResources.nextElement());
        }
        return Collections.enumeration(results);
    }

    public static boolean isFromThirdPartyPlugin(@Nullable Throwable throwable) {
        while (throwable != null) {
            for (StackTraceElement element : throwable.getStackTrace()) {
                if (!THIRD_PARTY_LOADER_NAME.equals(element.getClassLoaderName())) continue;
                return true;
            }
            if (throwable.getCause() == throwable) break;
            throwable = throwable.getCause();
        }
        return false;
    }

    static {
        PluginClassLoader.registerAsParallelCapable();
    }
}

