/*
 * Decompiled with CFR 0.152.
 */
package org.fusesource.jansi.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;
import org.fusesource.jansi.internal.OSInfo;

public class JansiLoader {
    private static boolean loaded = false;
    private static String nativeLibraryPath;
    private static String nativeLibrarySourceUrl;

    public static synchronized boolean initialize() {
        block3: {
            if (!loaded) {
                Thread cleanup = new Thread(JansiLoader::cleanup, "cleanup");
                cleanup.setPriority(1);
                cleanup.setDaemon(true);
                cleanup.start();
            }
            try {
                JansiLoader.loadJansiNativeLibrary();
            }
            catch (Exception e) {
                if (Boolean.parseBoolean(System.getProperty("jansi.graceful", "true"))) break block3;
                throw new RuntimeException("Unable to load jansi native library. You may want set the `jansi.graceful` system property to true to be able to use Jansi on your platform", e);
            }
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
        return new File(System.getProperty("jansi.tmpdir", System.getProperty("java.io.tmpdir")));
    }

    static void cleanup() {
        String tempFolder = JansiLoader.getTempDir().getAbsolutePath();
        File dir = new File(tempFolder);
        File[] nativeLibFiles = dir.listFiles(new FilenameFilter(){
            private final String searchPattern = "jansi-" + JansiLoader.getVersion();

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
                    System.err.println("Failed to delete old native lib" + e.getMessage());
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
                int numRead1 = JansiLoader.readNBytes(in1, buffer1);
                numRead2 = JansiLoader.readNBytes(in2, buffer2);
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
        String uuid = JansiLoader.randomUUID();
        String extractedLibFileName = String.format("jansi-%s-%s-%s", JansiLoader.getVersion(), uuid, libraryFileName);
        String extractedLckFileName = extractedLibFileName + ".lck";
        File extractedLibFile = new File(targetFolder, extractedLibFileName);
        File extractedLckFile = new File(targetFolder, extractedLckFileName);
        try {
            try (InputStream in = JansiLoader.class.getResourceAsStream(nativeLibraryFilePath);){
                if (!extractedLckFile.exists()) {
                    new FileOutputStream(extractedLckFile).close();
                }
                Files.copy(in, extractedLibFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            finally {
                extractedLibFile.deleteOnExit();
                extractedLckFile.deleteOnExit();
            }
            extractedLibFile.setReadable(true);
            extractedLibFile.setWritable(true);
            extractedLibFile.setExecutable(true);
            try (InputStream nativeIn = JansiLoader.class.getResourceAsStream(nativeLibraryFilePath);
                 FileInputStream extractedLibIn = new FileInputStream(extractedLibFile);){
                String eq = JansiLoader.contentsEquals(nativeIn, extractedLibIn);
                if (eq != null) {
                    throw new RuntimeException(String.format("Failed to write a native library file at %s because %s", extractedLibFile, eq));
                }
            }
            if (JansiLoader.loadNativeLibrary(extractedLibFile)) {
                nativeLibrarySourceUrl = JansiLoader.class.getResource(nativeLibraryFilePath).toExternalForm();
                return true;
            }
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    private static String randomUUID() {
        return Long.toHexString(new Random().nextLong());
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
                if (!libPath.canExecute()) {
                    System.err.printf("Failed to load native library:%s. The native library file at %s is not executable, make sure that the directory is mounted on a partition without the noexec flag, or set the jansi.tmpdir system property to point to a proper location.  osinfo: %s%n", libPath.getName(), libPath, OSInfo.getNativeLibFolderPathForCurrentOS());
                } else {
                    System.err.printf("Failed to load native library:%s. osinfo: %s%n", libPath.getName(), OSInfo.getNativeLibFolderPathForCurrentOS());
                }
                System.err.println(e);
                return false;
            }
        }
        return false;
    }

    private static void loadJansiNativeLibrary() throws Exception {
        String packagePath;
        boolean hasNativeLib;
        if (loaded) {
            return;
        }
        LinkedList<String> triedPaths = new LinkedList<String>();
        String jansiNativeLibraryPath = System.getProperty("library.jansi.path");
        String jansiNativeLibraryName = System.getProperty("library.jansi.name");
        if (jansiNativeLibraryName == null) {
            jansiNativeLibraryName = System.mapLibraryName("jansi");
            assert (jansiNativeLibraryName != null);
            if (jansiNativeLibraryName.endsWith(".dylib")) {
                jansiNativeLibraryName = jansiNativeLibraryName.replace(".dylib", ".jnilib");
            }
        }
        if (jansiNativeLibraryPath != null) {
            String withOs = jansiNativeLibraryPath + "/" + OSInfo.getNativeLibFolderPathForCurrentOS();
            if (JansiLoader.loadNativeLibrary(new File(withOs, jansiNativeLibraryName))) {
                loaded = true;
                return;
            }
            triedPaths.add(withOs);
            if (JansiLoader.loadNativeLibrary(new File(jansiNativeLibraryPath, jansiNativeLibraryName))) {
                loaded = true;
                return;
            }
            triedPaths.add(jansiNativeLibraryPath);
        }
        if (hasNativeLib = JansiLoader.hasResource((jansiNativeLibraryPath = String.format("/%s/native/%s", packagePath = JansiLoader.class.getPackage().getName().replace('.', '/'), OSInfo.getNativeLibFolderPathForCurrentOS())) + "/" + jansiNativeLibraryName)) {
            String tempFolder = JansiLoader.getTempDir().getAbsolutePath();
            if (JansiLoader.extractAndLoadLibraryFile(jansiNativeLibraryPath, jansiNativeLibraryName, tempFolder)) {
                loaded = true;
                return;
            }
            triedPaths.add(jansiNativeLibraryPath);
        }
        String javaLibraryPath = System.getProperty("java.library.path", "");
        for (String ldPath : javaLibraryPath.split(File.pathSeparator)) {
            if (ldPath.isEmpty()) continue;
            if (JansiLoader.loadNativeLibrary(new File(ldPath, jansiNativeLibraryName))) {
                loaded = true;
                return;
            }
            triedPaths.add(ldPath);
        }
        throw new Exception(String.format("No native library found for os.name=%s, os.arch=%s, paths=[%s]", OSInfo.getOSName(), OSInfo.getArchName(), String.join((CharSequence)File.pathSeparator, triedPaths)));
    }

    private static boolean hasResource(String path) {
        return JansiLoader.class.getResource(path) != null;
    }

    public static int getMajorVersion() {
        String[] c = JansiLoader.getVersion().split("\\.");
        return c.length > 0 ? Integer.parseInt(c[0]) : 1;
    }

    public static int getMinorVersion() {
        String[] c = JansiLoader.getVersion().split("\\.");
        return c.length > 1 ? Integer.parseInt(c[1]) : 0;
    }

    public static String getVersion() {
        URL versionFile = JansiLoader.class.getResource("/org/fusesource/jansi/jansi.properties");
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
            System.err.println(e);
        }
        return version;
    }
}

