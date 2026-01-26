/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.common.util.java;

import com.hypixel.hytale.common.semver.Semver;
import com.hypixel.hytale.function.supplier.CachedSupplier;
import com.hypixel.hytale.function.supplier.SupplierUtil;
import com.hypixel.hytale.logger.HytaleLogger;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Level;
import javax.annotation.Nullable;

public class ManifestUtil {
    public static final String VENDOR_ID_PROPERTY = "Implementation-Vendor-Id";
    public static final String VERSION_PROPERTY = "Implementation-Version";
    public static final String REVISION_ID_PROPERTY = "Implementation-Revision-Id";
    public static final String PATCHLINE_PROPERTY = "Implementation-Patchline";
    private static final CachedSupplier<Manifest> MANIFEST = SupplierUtil.cache(() -> {
        try {
            ClassLoader cl = ManifestUtil.class.getClassLoader();
            Enumeration<URL> enumeration = cl.getResources("META-INF/MANIFEST.MF");
            Manifest theManifest = null;
            while (enumeration.hasMoreElements()) {
                Manifest possible;
                URL url = enumeration.nextElement();
                try (InputStream is = url.openStream();){
                    possible = new Manifest(is);
                }
                Attributes mainAttributes = possible.getMainAttributes();
                String vendorId = mainAttributes.getValue(VENDOR_ID_PROPERTY);
                if (vendorId == null || !vendorId.equals("com.hypixel.hytale")) continue;
                theManifest = possible;
                break;
            }
            return theManifest;
        }
        catch (Throwable t) {
            HytaleLogger.getLogger().at(Level.WARNING).log("Exception was thrown getting manifest!", t);
            return null;
        }
    });
    private static final CachedSupplier<String> IMPLEMENTATION_VERSION = SupplierUtil.cache(() -> {
        try {
            Manifest localManifest = MANIFEST.get();
            if (localManifest == null) {
                return "NoJar";
            }
            return Objects.requireNonNull(localManifest.getMainAttributes().getValue(VERSION_PROPERTY), "Null implementation version!");
        }
        catch (Throwable t) {
            HytaleLogger.getLogger().at(Level.WARNING).log("Exception was thrown getting implementation version!", t);
            return "UNKNOWN";
        }
    });
    private static final CachedSupplier<String> IMPLEMENTATION_REVISION_ID = SupplierUtil.cache(() -> {
        try {
            Manifest localManifest = MANIFEST.get();
            if (localManifest == null) {
                return "NoJar";
            }
            return Objects.requireNonNull(localManifest.getMainAttributes().getValue(REVISION_ID_PROPERTY), "Null implementation revision id!");
        }
        catch (Throwable t) {
            HytaleLogger.getLogger().at(Level.WARNING).log("Exception was thrown getting implementation revision id!", t);
            return "UNKNOWN";
        }
    });
    private static final CachedSupplier<String> IMPLEMENTATION_PATCHLINE = SupplierUtil.cache(() -> {
        try {
            Manifest localManifest = MANIFEST.get();
            if (localManifest == null) {
                return "dev";
            }
            String value = localManifest.getMainAttributes().getValue(PATCHLINE_PROPERTY);
            return value != null && !value.isEmpty() ? value : "dev";
        }
        catch (Throwable t) {
            HytaleLogger.getLogger().at(Level.WARNING).log("Exception was thrown getting implementation patchline!", t);
            return "dev";
        }
    });
    private static final CachedSupplier<Semver> VERSION = SupplierUtil.cache(() -> {
        String version = IMPLEMENTATION_VERSION.get();
        if ("NoJar".equals(version)) {
            return null;
        }
        return Semver.fromString(version);
    });

    public static boolean isJar() {
        return MANIFEST.get() != null;
    }

    @Nullable
    public static Manifest getManifest() {
        return MANIFEST.get();
    }

    @Nullable
    public static String getImplementationVersion() {
        return IMPLEMENTATION_VERSION.get();
    }

    @Nullable
    public static Semver getVersion() {
        return VERSION.get();
    }

    @Nullable
    public static String getImplementationRevisionId() {
        return IMPLEMENTATION_REVISION_ID.get();
    }

    @Nullable
    public static String getPatchline() {
        return IMPLEMENTATION_PATCHLINE.get();
    }
}

