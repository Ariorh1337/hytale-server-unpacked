/*
 * Decompiled with CFR 0.152.
 */
package io.netty.bootstrap;

import io.netty.bootstrap.ChannelInitializerExtension;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ServiceLoader;

abstract class ChannelInitializerExtensions {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChannelInitializerExtensions.class);
    private static volatile ChannelInitializerExtensions implementation;

    private ChannelInitializerExtensions() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    static ChannelInitializerExtensions getExtensions() {
        ChannelInitializerExtensions impl = implementation;
        if (impl != null) return impl;
        Class<ChannelInitializerExtensions> clazz = ChannelInitializerExtensions.class;
        synchronized (ChannelInitializerExtensions.class) {
            impl = implementation;
            if (impl != null) {
                // ** MonitorExit[var1_1] (shouldn't be in output)
                return impl;
            }
            String extensionProp = SystemPropertyUtil.get("io.netty.bootstrap.extensions");
            logger.debug("-Dio.netty.bootstrap.extensions: {}", (Object)extensionProp);
            impl = "serviceload".equalsIgnoreCase(extensionProp) ? new ServiceLoadingExtensions(true) : ("log".equalsIgnoreCase(extensionProp) ? new ServiceLoadingExtensions(false) : new EmptyExtensions());
            implementation = impl;
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return impl;
        }
    }

    abstract Collection<ChannelInitializerExtension> extensions(ClassLoader var1);

    private static final class ServiceLoadingExtensions
    extends ChannelInitializerExtensions {
        private final boolean loadAndCache;
        private WeakReference<ClassLoader> classLoader;
        private Collection<ChannelInitializerExtension> extensions;

        ServiceLoadingExtensions(boolean loadAndCache) {
            this.loadAndCache = loadAndCache;
        }

        @Override
        synchronized Collection<ChannelInitializerExtension> extensions(ClassLoader cl) {
            ClassLoader configured;
            ClassLoader classLoader = configured = this.classLoader == null ? null : (ClassLoader)this.classLoader.get();
            if (configured == null || configured != cl) {
                Collection<ChannelInitializerExtension> loaded = ServiceLoadingExtensions.serviceLoadExtensions(this.loadAndCache, cl);
                this.classLoader = new WeakReference<ClassLoader>(cl);
                this.extensions = this.loadAndCache ? loaded : Collections.emptyList();
            }
            return this.extensions;
        }

        private static Collection<ChannelInitializerExtension> serviceLoadExtensions(boolean load, ClassLoader cl) {
            ArrayList<ChannelInitializerExtension> extensions = new ArrayList<ChannelInitializerExtension>();
            ServiceLoader<ChannelInitializerExtension> loader = ServiceLoader.load(ChannelInitializerExtension.class, cl);
            for (ChannelInitializerExtension extension : loader) {
                extensions.add(extension);
            }
            if (!extensions.isEmpty()) {
                Collections.sort(extensions, new Comparator<ChannelInitializerExtension>(){

                    @Override
                    public int compare(ChannelInitializerExtension a, ChannelInitializerExtension b) {
                        return Double.compare(a.priority(), b.priority());
                    }
                });
                logger.info("ServiceLoader {}(s) {}: {}", ChannelInitializerExtension.class.getSimpleName(), load ? "registered" : "detected", extensions);
                return Collections.unmodifiableList(extensions);
            }
            logger.debug("ServiceLoader {}(s) {}: []", (Object)ChannelInitializerExtension.class.getSimpleName(), (Object)(load ? "registered" : "detected"));
            return Collections.emptyList();
        }
    }

    private static final class EmptyExtensions
    extends ChannelInitializerExtensions {
        private EmptyExtensions() {
        }

        @Override
        Collection<ChannelInitializerExtension> extensions(ClassLoader cl) {
            return Collections.emptyList();
        }
    }
}

