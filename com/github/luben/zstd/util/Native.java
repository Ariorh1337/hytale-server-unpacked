/*
 * Decompiled with CFR 0.152.
 */
package com.github.luben.zstd.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Native
extends Enum<Native> {
    private static final String nativePathOverride = "ZstdNativePath";
    private static final String tempFolderOverride = "ZstdTempFolder";
    private static final String libnameShort = "zstd-jni-1.5.7-6";
    private static final String libname = "libzstd-jni-1.5.7-6";
    private static final String errorMsg;
    private static AtomicBoolean loaded;
    private static final /* synthetic */ Native[] $VALUES;

    public static Native[] values() {
        return (Native[])$VALUES.clone();
    }

    public static Native valueOf(String string) {
        return Enum.valueOf(Native.class, string);
    }

    private static String osName() {
        String string = System.getProperty("os.name").toLowerCase().replace(' ', '_');
        if (string.startsWith("win")) {
            return "win";
        }
        if (string.startsWith("mac")) {
            return "darwin";
        }
        return string;
    }

    private static String libExtension() {
        if (Native.osName().contains("os_x") || Native.osName().contains("darwin")) {
            return "dylib";
        }
        if (Native.osName().contains("win")) {
            return "dll";
        }
        return "so";
    }

    private static String resourceName() {
        String string = Native.osName();
        String string2 = System.getProperty("os.arch");
        if (string.equals("darwin") && string2.equals("amd64")) {
            string2 = "x86_64";
        }
        return "/" + string + "/" + string2 + "/" + libname + "." + Native.libExtension();
    }

    public static synchronized void assumeLoaded() {
        loaded.set(true);
    }

    public static synchronized boolean isLoaded() {
        return loaded.get();
    }

    private static void loadLibrary(final String string) {
        AccessController.doPrivileged(new PrivilegedAction<Void>(){

            @Override
            public Void run() {
                System.loadLibrary(string);
                return null;
            }
        });
    }

    private static void loadLibraryFile(final String string) {
        AccessController.doPrivileged(new PrivilegedAction<Void>(){

            @Override
            public Void run() {
                System.load(string);
                return null;
            }
        });
    }

    public static synchronized void load() {
        String string = System.getProperty(tempFolderOverride);
        if (string == null) {
            Native.load(null);
        } else {
            Native.load(new File(string));
        }
    }

    public static synchronized void load(File file) {
        if (loaded.get()) {
            return;
        }
        String string = Native.resourceName();
        String string2 = System.getProperty(nativePathOverride);
        if (string2 != null) {
            Native.loadLibraryFile(string2);
            loaded.set(true);
            return;
        }
        try {
            Class.forName("org.osgi.framework.BundleEvent");
            Native.loadLibrary(libname);
            loaded.set(true);
            return;
        }
        catch (Throwable throwable) {
            InputStream inputStream = Native.class.getResourceAsStream(string);
            if (inputStream == null) {
                try {
                    Native.loadLibrary(libnameShort);
                    loaded.set(true);
                    return;
                }
                catch (UnsatisfiedLinkError unsatisfiedLinkError) {
                    UnsatisfiedLinkError unsatisfiedLinkError2 = new UnsatisfiedLinkError(unsatisfiedLinkError.getMessage() + "\n" + errorMsg);
                    unsatisfiedLinkError2.setStackTrace(unsatisfiedLinkError.getStackTrace());
                    throw unsatisfiedLinkError2;
                }
            }
            File file2 = null;
            FileOutputStream fileOutputStream = null;
            try {
                int n;
                file2 = File.createTempFile(libname, "." + Native.libExtension(), file);
                file2.deleteOnExit();
                fileOutputStream = new FileOutputStream(file2);
                byte[] byArray = new byte[4096];
                while ((n = inputStream.read(byArray)) != -1) {
                    fileOutputStream.write(byArray, 0, n);
                }
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    fileOutputStream = null;
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                try {
                    Native.loadLibraryFile(file2.getAbsolutePath());
                }
                catch (UnsatisfiedLinkError unsatisfiedLinkError) {
                    try {
                        Native.loadLibrary(libnameShort);
                    }
                    catch (UnsatisfiedLinkError unsatisfiedLinkError3) {
                        UnsatisfiedLinkError unsatisfiedLinkError4 = new UnsatisfiedLinkError(unsatisfiedLinkError.getMessage() + "\n" + unsatisfiedLinkError3.getMessage() + "\n" + errorMsg);
                        unsatisfiedLinkError4.setStackTrace(unsatisfiedLinkError3.getStackTrace());
                        throw unsatisfiedLinkError4;
                    }
                }
                loaded.set(true);
            }
            catch (IOException iOException) {
                ExceptionInInitializerError exceptionInInitializerError = new ExceptionInInitializerError("Cannot unpack libzstd-jni-1.5.7-6: " + iOException.getMessage());
                exceptionInInitializerError.setStackTrace(iOException.getStackTrace());
                throw exceptionInInitializerError;
            }
            finally {
                try {
                    inputStream.close();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    if (file2 != null && file2.exists()) {
                        file2.delete();
                    }
                }
                catch (IOException iOException) {}
            }
            return;
        }
    }

    private static /* synthetic */ Native[] $values() {
        return new Native[0];
    }

    static {
        $VALUES = Native.$values();
        errorMsg = "Unsupported OS/arch, cannot find " + Native.resourceName() + " or load " + libnameShort + " from system libraries. Please try building from source the jar or providing " + libname + " in your system.";
        loaded = new AtomicBoolean(false);
    }
}

