/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale;

import com.hypixel.hytale.LateMain;
import com.hypixel.hytale.plugin.early.EarlyPluginLoader;
import com.hypixel.hytale.plugin.early.TransformingClassLoader;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Locale;
import javax.annotation.Nonnull;

public final class Main {
    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        System.setProperty("java.awt.headless", "true");
        System.setProperty("file.encoding", "UTF-8");
        EarlyPluginLoader.loadEarlyPlugins(args);
        if (EarlyPluginLoader.hasTransformers()) {
            Main.launchWithTransformingClassLoader(args);
        } else {
            LateMain.lateMain(args);
        }
    }

    private static void launchWithTransformingClassLoader(@Nonnull String[] args) {
        try {
            URL[] urls = Main.getClasspathUrls();
            ClassLoader appClassLoader = Main.class.getClassLoader();
            TransformingClassLoader transformingClassLoader = new TransformingClassLoader(urls, EarlyPluginLoader.getTransformers(), appClassLoader.getParent(), appClassLoader);
            Thread.currentThread().setContextClassLoader(transformingClassLoader);
            Class<?> lateMainClass = transformingClassLoader.loadClass("com.hypixel.hytale.LateMain");
            Method mainMethod = lateMainClass.getMethod("lateMain", String[].class);
            mainMethod.invoke(null, new Object[]{args});
        }
        catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException("Failed to launch with transforming classloader", e);
        }
        catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                RuntimeException re = (RuntimeException)cause;
                throw re;
            }
            if (cause instanceof Error) {
                Error err = (Error)cause;
                throw err;
            }
            throw new RuntimeException("LateMain.lateMain() threw an exception", cause);
        }
    }

    private static URL[] getClasspathUrls() {
        ClassLoader classLoader = Main.class.getClassLoader();
        if (classLoader instanceof URLClassLoader) {
            URLClassLoader urlClassLoader = (URLClassLoader)classLoader;
            return urlClassLoader.getURLs();
        }
        ObjectArrayList<URL> urls = new ObjectArrayList<URL>();
        String classpath = System.getProperty("java.class.path");
        if (classpath != null && !classpath.isEmpty()) {
            for (String pathStr : classpath.split(System.getProperty("path.separator"))) {
                try {
                    Path path = Path.of(pathStr, new String[0]);
                    if (!Files.exists(path, new LinkOption[0])) continue;
                    urls.add(path.toUri().toURL());
                }
                catch (Exception e) {
                    System.err.println("[EarlyPlugin] Failed to parse classpath entry: " + pathStr);
                }
            }
        }
        return (URL[])urls.toArray(URL[]::new);
    }
}

