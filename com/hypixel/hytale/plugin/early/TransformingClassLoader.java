/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.plugin.early;

import com.hypixel.hytale.plugin.early.ClassTransformer;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;

public final class TransformingClassLoader
extends URLClassLoader {
    private static final Set<String> SECURE_PACKAGE_PREFIXES = Set.of("java.", "javax.", "jdk.", "sun.", "com.sun.", "org.bouncycastle.", "server.io.netty.", "org.objectweb.asm.", "com.google.gson.", "org.slf4j.", "org.apache.logging.", "ch.qos.logback.", "com.google.flogger.", "server.io.sentry.", "com.hypixel.protoplus.", "com.hypixel.fastutil.", "com.hypixel.hytale.plugin.early.");
    private final List<ClassTransformer> transformers;
    private final ClassLoader appClassLoader;

    public TransformingClassLoader(@Nonnull URL[] urls, @Nonnull List<ClassTransformer> transformers, ClassLoader parent, ClassLoader appClassLoader) {
        super(urls, parent);
        this.transformers = transformers;
        this.appClassLoader = appClassLoader;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Object object = this.getClassLoadingLock(name);
        synchronized (object) {
            Class<?> clazz;
            Class<?> loaded = this.findLoadedClass(name);
            if (loaded != null) {
                if (!resolve) return loaded;
                this.resolveClass(loaded);
                return loaded;
            }
            if (TransformingClassLoader.isPreloadedClass(name)) {
                Class<?> clazz2 = this.appClassLoader.loadClass(name);
                if (!resolve) return clazz2;
                this.resolveClass(clazz2);
                return clazz2;
            }
            String internalName = name.replace('.', '/');
            URL resource = this.findResource(internalName + ".class");
            if (resource == null) return super.loadClass(name, resolve);
            InputStream is = resource.openStream();
            try {
                Class<?> clazz3 = this.transformAndDefine(name, internalName, is.readAllBytes(), resource);
                if (resolve) {
                    this.resolveClass(clazz3);
                }
                clazz = clazz3;
                if (is == null) return clazz;
            }
            catch (Throwable throwable) {
                try {
                    if (is == null) throw throwable;
                    try {
                        is.close();
                        throw throwable;
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                    throw throwable;
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                return super.loadClass(name, resolve);
            }
            is.close();
            return clazz;
        }
    }

    private Class<?> transformAndDefine(String name, String internalName, byte[] classBytes, URL resource) {
        if (!TransformingClassLoader.isSecureClass(name)) {
            for (ClassTransformer transformer : this.transformers) {
                try {
                    byte[] transformed = transformer.transform(name, internalName, classBytes);
                    if (transformed == null) continue;
                    classBytes = transformed;
                }
                catch (Exception e) {
                    System.err.println("[EarlyPlugin] Transformer " + transformer.getClass().getName() + " failed on " + name + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        URL codeSourceUrl = TransformingClassLoader.getCodeSourceUrl(resource, internalName);
        CodeSource codeSource = new CodeSource(codeSourceUrl, (Certificate[])null);
        ProtectionDomain protectionDomain = new ProtectionDomain(codeSource, null, this, null);
        return this.defineClass(name, classBytes, 0, classBytes.length, protectionDomain);
    }

    private static URL getCodeSourceUrl(URL resource, String internalName) {
        String urlStr = resource.toString();
        String classPath = internalName + ".class";
        if (urlStr.startsWith("jar:")) {
            int bangIndex = urlStr.indexOf("!/");
            if (bangIndex > 0) {
                try {
                    return new URL(urlStr.substring(4, bangIndex));
                }
                catch (Exception e) {
                    return resource;
                }
            }
        } else if (urlStr.endsWith(classPath)) {
            try {
                return new URL(urlStr.substring(0, urlStr.length() - classPath.length()));
            }
            catch (Exception e) {
                return resource;
            }
        }
        return resource;
    }

    private static boolean isPreloadedClass(@Nonnull String name) {
        return name.equals("com.hypixel.hytale.Main") || name.startsWith("com.hypixel.hytale.plugin.early.");
    }

    private static boolean isSecureClass(@Nonnull String name) {
        for (String prefix : SECURE_PACKAGE_PREFIXES) {
            if (!name.startsWith(prefix)) continue;
            return true;
        }
        return false;
    }
}

