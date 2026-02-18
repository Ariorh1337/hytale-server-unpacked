/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb.util;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class Environment {
    private static String OS = System.getProperty("os.name").toLowerCase(Locale.getDefault());
    private static String ARCH = System.getProperty("os.arch").toLowerCase(Locale.getDefault());
    private static String MUSL_ENVIRONMENT = System.getenv("ROCKSDB_MUSL_LIBC");
    private static final String LIBC_MUSL_PREFIX = "libc.musl";
    private static final String SPARCV9 = "sparcv9";
    private static Boolean MUSL_LIBC = null;

    public static boolean isAarch64() {
        return ARCH.contains("aarch64");
    }

    public static boolean isPowerPC() {
        return ARCH.contains("ppc");
    }

    public static boolean isS390x() {
        return ARCH.contains("s390x");
    }

    public static boolean isRiscv64() {
        return ARCH.contains("riscv64");
    }

    public static boolean isWindows() {
        return OS.contains("win");
    }

    public static boolean isFreeBSD() {
        return OS.contains("freebsd");
    }

    public static boolean isMac() {
        return OS.contains("mac");
    }

    public static boolean isAix() {
        return OS.contains("aix");
    }

    public static boolean isUnix() {
        return OS.contains("nix") || OS.contains("nux");
    }

    public static boolean isMuslLibc() {
        if (MUSL_LIBC == null) {
            MUSL_LIBC = Environment.initIsMuslLibc();
        }
        return MUSL_LIBC;
    }

    static boolean initIsMuslLibc() {
        Object object;
        if ("true".equalsIgnoreCase(MUSL_ENVIRONMENT)) {
            return true;
        }
        if ("false".equalsIgnoreCase(MUSL_ENVIRONMENT)) {
            return false;
        }
        try {
            object = new ProcessBuilder("/usr/bin/env", "sh", "-c", "ldd /usr/bin/env | grep -q musl").start();
            if (((Process)object).waitFor() == 0) {
                return true;
            }
        }
        catch (IOException | InterruptedException exception) {
            // empty catch block
        }
        object = new File("/lib");
        if (((File)object).exists() && ((File)object).isDirectory() && ((File)object).canRead()) {
            String string = Environment.isPowerPC() ? "libc.musl-ppc64le.so.1" : (Environment.isAarch64() ? "libc.musl-aarch64.so.1" : (Environment.isS390x() ? "libc.musl-s390x.so.1" : "libc.musl-x86_64.so.1"));
            File file = new File((File)object, string);
            if (file.exists() && file.canRead()) {
                return true;
            }
            File[] fileArray = ((File)object).listFiles();
            if (fileArray == null) {
                return false;
            }
            for (File file2 : fileArray) {
                if (!file2.getName().startsWith(LIBC_MUSL_PREFIX)) continue;
                return true;
            }
        }
        return false;
    }

    public static boolean isSolaris() {
        return OS.contains("sunos");
    }

    public static boolean isOpenBSD() {
        return OS.contains("openbsd");
    }

    public static boolean is64Bit() {
        if (ARCH.contains(SPARCV9)) {
            return true;
        }
        return ARCH.indexOf("64") > 0;
    }

    public static String getSharedLibraryName(String string) {
        return string + "jni";
    }

    public static String getSharedLibraryFileName(String string) {
        return Environment.appendLibOsSuffix("lib" + Environment.getSharedLibraryName(string), true);
    }

    public static String getLibcName() {
        if (Environment.isMuslLibc()) {
            return "musl";
        }
        return null;
    }

    private static String getLibcPostfix() {
        String string = Environment.getLibcName();
        if (string == null) {
            return "";
        }
        return "-" + string;
    }

    public static String getJniLibraryName(String string) {
        if (Environment.isUnix()) {
            String string2;
            String string3 = string2 = Environment.is64Bit() ? "64" : "32";
            if (Environment.isPowerPC() || Environment.isAarch64() || Environment.isRiscv64()) {
                return String.format("%sjni-linux-%s%s", string, ARCH, Environment.getLibcPostfix());
            }
            if (Environment.isS390x()) {
                return String.format("%sjni-linux-%s", string, ARCH);
            }
            return String.format("%sjni-linux%s%s", string, string2, Environment.getLibcPostfix());
        }
        if (Environment.isMac()) {
            if (Environment.is64Bit()) {
                String string4 = Environment.isAarch64() ? "arm64" : "x86_64";
                return String.format("%sjni-osx-%s", string, string4);
            }
            return String.format("%sjni-osx", string);
        }
        if (Environment.isFreeBSD()) {
            return String.format("%sjni-freebsd%s", string, Environment.is64Bit() ? "64" : "32");
        }
        if (Environment.isAix() && Environment.is64Bit()) {
            return String.format("%sjni-aix64", string);
        }
        if (Environment.isSolaris()) {
            String string5 = Environment.is64Bit() ? "64" : "32";
            return String.format("%sjni-solaris%s", string, string5);
        }
        if (Environment.isWindows() && Environment.is64Bit()) {
            return String.format("%sjni-win64", string);
        }
        if (Environment.isOpenBSD()) {
            return String.format("%sjni-openbsd%s", string, Environment.is64Bit() ? "64" : "32");
        }
        throw new UnsupportedOperationException(String.format("Cannot determine JNI library name for ARCH='%s' OS='%s' name='%s'", ARCH, OS, string));
    }

    public static String getFallbackJniLibraryName(String string) {
        if (Environment.isMac() && Environment.is64Bit()) {
            return String.format("%sjni-osx", string);
        }
        return null;
    }

    public static String getJniLibraryFileName(String string) {
        return Environment.appendLibOsSuffix("lib" + Environment.getJniLibraryName(string), false);
    }

    public static String getFallbackJniLibraryFileName(String string) {
        String string2 = Environment.getFallbackJniLibraryName(string);
        if (string2 == null) {
            return null;
        }
        return Environment.appendLibOsSuffix("lib" + string2, false);
    }

    private static String appendLibOsSuffix(String string, boolean bl) {
        if (Environment.isUnix() || Environment.isAix() || Environment.isSolaris() || Environment.isFreeBSD() || Environment.isOpenBSD()) {
            return string + ".so";
        }
        if (Environment.isMac()) {
            return string + (bl ? ".dylib" : ".jnilib");
        }
        if (Environment.isWindows()) {
            return string + ".dll";
        }
        throw new UnsupportedOperationException();
    }

    public static String getJniLibraryExtension() {
        if (Environment.isWindows()) {
            return ".dll";
        }
        return Environment.isMac() ? ".jnilib" : ".so";
    }
}

