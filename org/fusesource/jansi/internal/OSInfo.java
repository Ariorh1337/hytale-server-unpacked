/*
 * Decompiled with CFR 0.152.
 */
package org.fusesource.jansi.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;

public class OSInfo {
    public static final String X86 = "x86";
    public static final String X86_64 = "x86_64";
    public static final String IA64_32 = "ia64_32";
    public static final String IA64 = "ia64";
    public static final String PPC = "ppc";
    public static final String PPC64 = "ppc64";
    public static final String ARM64 = "arm64";
    private static final HashMap<String, String> archMapping = new HashMap();

    public static void main(String[] args) {
        if (args.length >= 1) {
            if ("--os".equals(args[0])) {
                System.out.print(OSInfo.getOSName());
                return;
            }
            if ("--arch".equals(args[0])) {
                System.out.print(OSInfo.getArchName());
                return;
            }
        }
        System.out.print(OSInfo.getNativeLibFolderPathForCurrentOS());
    }

    public static String getNativeLibFolderPathForCurrentOS() {
        return OSInfo.getOSName() + "/" + OSInfo.getArchName();
    }

    public static String getOSName() {
        return OSInfo.translateOSNameToFolderName(System.getProperty("os.name"));
    }

    public static boolean isAndroid() {
        return System.getProperty("java.runtime.name", "").toLowerCase().contains("android");
    }

    public static boolean isAlpine() {
        try {
            for (String line : Files.readAllLines(Paths.get("/etc/os-release", new String[0]))) {
                if (!line.startsWith("ID") || !line.toLowerCase(Locale.ROOT).contains("alpine")) continue;
                return true;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return false;
    }

    static String getHardwareName() {
        String string;
        Process p = Runtime.getRuntime().exec("uname -m");
        p.waitFor();
        InputStream in = p.getInputStream();
        try {
            string = OSInfo.readFully(in);
        }
        catch (Throwable throwable) {
            try {
                in.close();
                throw throwable;
            }
            catch (Throwable e) {
                System.err.println("Error while running uname -m: " + e.getMessage());
                return "unknown";
            }
        }
        in.close();
        return string;
    }

    private static String readFully(InputStream in) throws IOException {
        int readLen = 0;
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        byte[] buf = new byte[32];
        while ((readLen = in.read(buf, 0, buf.length)) >= 0) {
            b.write(buf, 0, readLen);
        }
        return b.toString();
    }

    static String resolveArmArchType() {
        if (System.getProperty("os.name").contains("Linux")) {
            String armType = OSInfo.getHardwareName();
            if (armType.startsWith("armv6")) {
                return "armv6";
            }
            if (armType.startsWith("armv7")) {
                return "armv7";
            }
            if (armType.startsWith("armv5")) {
                return "arm";
            }
            if (armType.equals("aarch64")) {
                return ARM64;
            }
            String abi = System.getProperty("sun.arch.abi");
            if (abi != null && abi.startsWith("gnueabihf")) {
                return "armv7";
            }
        }
        return "arm";
    }

    public static String getArchName() {
        String osArch = System.getProperty("os.arch");
        if (OSInfo.isAndroid()) {
            return "android-arm";
        }
        if (osArch.startsWith("arm")) {
            osArch = OSInfo.resolveArmArchType();
        } else {
            String lc = osArch.toLowerCase(Locale.US);
            if (archMapping.containsKey(lc)) {
                return archMapping.get(lc);
            }
        }
        return OSInfo.translateArchNameToFolderName(osArch);
    }

    static String translateOSNameToFolderName(String osName) {
        if (osName.contains("Windows")) {
            return "Windows";
        }
        if (osName.contains("Mac") || osName.contains("Darwin")) {
            return "Mac";
        }
        if (osName.contains("Linux")) {
            return "Linux";
        }
        if (osName.contains("AIX")) {
            return "AIX";
        }
        return osName.replaceAll("\\W", "");
    }

    static String translateArchNameToFolderName(String archName) {
        return archName.replaceAll("\\W", "");
    }

    static {
        archMapping.put(X86, X86);
        archMapping.put("i386", X86);
        archMapping.put("i486", X86);
        archMapping.put("i586", X86);
        archMapping.put("i686", X86);
        archMapping.put("pentium", X86);
        archMapping.put(X86_64, X86_64);
        archMapping.put("amd64", X86_64);
        archMapping.put("em64t", X86_64);
        archMapping.put("universal", X86_64);
        archMapping.put(IA64, IA64);
        archMapping.put("ia64w", IA64);
        archMapping.put(IA64_32, IA64_32);
        archMapping.put("ia64n", IA64_32);
        archMapping.put(PPC, PPC);
        archMapping.put("power", PPC);
        archMapping.put("powerpc", PPC);
        archMapping.put("power_pc", PPC);
        archMapping.put("power_rs", PPC);
        archMapping.put(PPC64, PPC64);
        archMapping.put("power64", PPC64);
        archMapping.put("powerpc64", PPC64);
        archMapping.put("power_pc64", PPC64);
        archMapping.put("power_rs64", PPC64);
        archMapping.put("aarch64", ARM64);
    }
}

