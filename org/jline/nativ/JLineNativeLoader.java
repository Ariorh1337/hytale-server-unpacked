/*
 * Decompiled with CFR 0.152.
 */
package org.jline.nativ;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jline.nativ.OSInfo;

public class JLineNativeLoader {
    private static final Logger logger = Logger.getLogger("org.jline");
    private static boolean loaded = false;
    private static String nativeLibraryPath;
    private static String nativeLibrarySourceUrl;

    public static synchronized boolean initialize() {
        if (!loaded) {
            Thread cleanup = new Thread(JLineNativeLoader::cleanup, "cleanup");
            cleanup.setPriority(1);
            cleanup.setDaemon(true);
            cleanup.start();
        }
        try {
            JLineNativeLoader.loadJLineNativeLibrary();
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to load jline native library: " + e.getMessage(), e);
        }
        return loaded;
    }

    public static String getNativeLibraryPath() {
        return nativeLibraryPath;
    }

    public static String getNativeLibrarySourceUrl() {
        return nativeLibrarySourceUrl;
    }

    private static File getTempDir() {
        return new File(System.getProperty("jline.tmpdir", System.getProperty("java.io.tmpdir")));
    }

    static void cleanup() {
        String tempFolder = JLineNativeLoader.getTempDir().getAbsolutePath();
        File dir = new File(tempFolder);
        File[] nativeLibFiles = dir.listFiles(new FilenameFilter(){
            private final String searchPattern = "jlinenative-" + JLineNativeLoader.getVersion();

            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(this.searchPattern) && !name.endsWith(".lck");
            }
        });
        if (nativeLibFiles != null) {
            for (File nativeLibFile : nativeLibFiles) {
                File lckFile = new File(nativeLibFile.getAbsolutePath() + ".lck");
                if (lckFile.exists()) continue;
                try {
                    nativeLibFile.delete();
                }
                catch (SecurityException e) {
                    logger.log(Level.INFO, "Failed to delete old native lib" + e.getMessage(), e);
                }
            }
        }
    }

    private static int readNBytes(InputStream in, byte[] b) throws IOException {
        int n;
        int count;
        int len = b.length;
        for (n = 0; n < len && (count = in.read(b, n, len - n)) > 0; n += count) {
        }
        return n;
    }

    private static String contentsEquals(InputStream in1, InputStream in2) throws IOException {
        int numRead2;
        block3: {
            byte[] buffer1 = new byte[8192];
            byte[] buffer2 = new byte[8192];
            do {
                int numRead1 = JLineNativeLoader.readNBytes(in1, buffer1);
                numRead2 = JLineNativeLoader.readNBytes(in2, buffer2);
                if (numRead1 <= 0) break block3;
                if (numRead2 <= 0) {
                    return "EOF on second stream but not first";
                }
                if (numRead2 == numRead1) continue;
                return "Read size different (" + numRead1 + " vs " + numRead2 + ")";
            } while (Arrays.equals(buffer1, buffer2));
            return "Content differs";
        }
        if (numRead2 > 0) {
            return "EOF on first stream but not second";
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static boolean extractAndLoadLibraryFile(String libFolderForCurrentOS, String libraryFileName, String targetFolder) {
        String nativeLibraryFilePath = libFolderForCurrentOS + "/" + libraryFileName;
        String uuid = JLineNativeLoader.randomUUID();
        String extractedLibFileName = String.format("jlinenative-%s-%s-%s", JLineNativeLoader.getVersion(), uuid, libraryFileName);
        String extractedLckFileName = extractedLibFileName + ".lck";
        File extractedLibFile = new File(targetFolder, extractedLibFileName);
        File extractedLckFile = new File(targetFolder, extractedLckFileName);
        try {
            try (InputStream in = JLineNativeLoader.class.getResourceAsStream(nativeLibraryFilePath);){
                if (!extractedLckFile.exists()) {
                    new FileOutputStream(extractedLckFile).close();
                }
                try (FileOutputStream out = new FileOutputStream(extractedLibFile);){
                    JLineNativeLoader.copy(in, out);
                }
            }
            finally {
                extractedLibFile.deleteOnExit();
                extractedLckFile.deleteOnExit();
            }
            extractedLibFile.setReadable(true);
            extractedLibFile.setWritable(true);
            extractedLibFile.setExecutable(true);
            try (InputStream nativeIn = JLineNativeLoader.class.getResourceAsStream(nativeLibraryFilePath);
                 FileInputStream extractedLibIn = new FileInputStream(extractedLibFile);){
                String eq = JLineNativeLoader.contentsEquals(nativeIn, extractedLibIn);
                if (eq != null) {
                    throw new RuntimeException(String.format("Failed to write a native library file at %s because %s", extractedLibFile, eq));
                }
            }
            if (JLineNativeLoader.loadNativeLibrary(extractedLibFile)) {
                nativeLibrarySourceUrl = JLineNativeLoader.class.getResource(nativeLibraryFilePath).toExternalForm();
                return true;
            }
        }
        catch (IOException e) {
            JLineNativeLoader.log(Level.WARNING, "Unable to load JLine's native library", e);
        }
        return false;
    }

    private static String randomUUID() {
        return Long.toHexString(new Random().nextLong());
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        int n;
        byte[] buf = new byte[8192];
        while ((n = in.read(buf)) > 0) {
            out.write(buf, 0, n);
        }
    }

    private static boolean loadNativeLibrary(File libPath) {
        if (libPath.exists()) {
            try {
                String path = libPath.getAbsolutePath();
                System.load(path);
                nativeLibraryPath = path;
                return true;
            }
            catch (UnsatisfiedLinkError e) {
                JLineNativeLoader.log(Level.WARNING, "Failed to load native library:" + libPath.getName() + ". osinfo: " + OSInfo.getNativeLibFolderPathForCurrentOS(), e);
                return false;
            }
        }
        return false;
    }

    private static void loadJLineNativeLibrary() throws Exception {
        String packagePath;
        boolean hasNativeLib;
        if (loaded) {
            return;
        }
        ArrayList<String> triedPaths = new ArrayList<String>();
        String jlineNativeLibraryPath = System.getProperty("library.jline.path");
        String jlineNativeLibraryName = System.getProperty("library.jline.name");
        if (jlineNativeLibraryName == null) {
            jlineNativeLibraryName = System.mapLibraryName("jlinenative");
            assert (jlineNativeLibraryName != null);
            if (jlineNativeLibraryName.endsWith(".dylib")) {
                jlineNativeLibraryName = jlineNativeLibraryName.replace(".dylib", ".jnilib");
            }
        }
        if (jlineNativeLibraryPath != null) {
            String withOs = jlineNativeLibraryPath + "/" + OSInfo.getNativeLibFolderPathForCurrentOS();
            if (JLineNativeLoader.loadNativeLibrary(new File(withOs, jlineNativeLibraryName))) {
                loaded = true;
                return;
            }
            triedPaths.add(withOs);
            if (JLineNativeLoader.loadNativeLibrary(new File(jlineNativeLibraryPath, jlineNativeLibraryName))) {
                loaded = true;
                return;
            }
            triedPaths.add(jlineNativeLibraryPath);
        }
        if (hasNativeLib = JLineNativeLoader.hasResource((jlineNativeLibraryPath = String.format("/%s/%s", packagePath = JLineNativeLoader.class.getPackage().getName().replace('.', '/'), OSInfo.getNativeLibFolderPathForCurrentOS())) + "/" + jlineNativeLibraryName)) {
            String tempFolder = JLineNativeLoader.getTempDir().getAbsolutePath();
            if (JLineNativeLoader.extractAndLoadLibraryFile(jlineNativeLibraryPath, jlineNativeLibraryName, tempFolder)) {
                loaded = true;
                return;
            }
            triedPaths.add(jlineNativeLibraryPath);
        }
        String javaLibraryPath = System.getProperty("java.library.path", "");
        for (String ldPath : javaLibraryPath.split(File.pathSeparator)) {
            if (ldPath.isEmpty()) continue;
            if (JLineNativeLoader.loadNativeLibrary(new File(ldPath, jlineNativeLibraryName))) {
                loaded = true;
                return;
            }
            triedPaths.add(ldPath);
        }
        throw new Exception(String.format("No native library found for os.name=%s, os.arch=%s, paths=[%s]", OSInfo.getOSName(), OSInfo.getArchName(), JLineNativeLoader.join(triedPaths, File.pathSeparator)));
    }

    private static boolean hasResource(String path) {
        return JLineNativeLoader.class.getResource(path) != null;
    }

    public static int getMajorVersion() {
        String[] c = JLineNativeLoader.getVersion().split("\\.");
        return c.length > 0 ? Integer.parseInt(c[0]) : 1;
    }

    public static int getMinorVersion() {
        String[] c = JLineNativeLoader.getVersion().split("\\.");
        return c.length > 1 ? Integer.parseInt(c[1]) : 0;
    }

    public static String getVersion() {
        URL versionFile = JLineNativeLoader.class.getResource("/META-INF/maven/org.jline/jline-native/pom.properties");
        String version = "unknown";
        try {
            if (versionFile != null) {
                Properties versionData = new Properties();
                versionData.load(versionFile.openStream());
                version = versionData.getProperty("version", version);
                version = version.trim().replaceAll("[^0-9.]", "");
            }
        }
        catch (IOException e) {
            JLineNativeLoader.log(Level.WARNING, "Unable to load jline-native version", e);
        }
        return version;
    }

    private static String join(List<String> list, String separator) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String item : list) {
            if (first) {
                first = false;
            } else {
                sb.append(separator);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    private static void log(Level level, String message, Throwable t) {
        if (logger.isLoggable(level)) {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(level, message, t);
            } else {
                logger.log(level, message + " (caused by: " + t + ", enable debug logging for stacktrace)");
            }
        }
    }
}

