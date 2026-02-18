/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.rocksdb.util.Environment;

public class NativeLibraryLoader {
    private static final NativeLibraryLoader instance = new NativeLibraryLoader();
    private static boolean initialized = false;
    private static final String ROCKSDB_LIBRARY_NAME = "rocksdb";
    private static final String sharedLibraryName = Environment.getSharedLibraryName("rocksdb");
    private static final String jniLibraryName = Environment.getJniLibraryName("rocksdb");
    private static final String fallbackJniLibraryName = Environment.getFallbackJniLibraryName("rocksdb");
    private static final String jniLibraryFileName = Environment.getJniLibraryFileName("rocksdb");
    private static final String fallbackJniLibraryFileName = Environment.getFallbackJniLibraryFileName("rocksdb");
    private static final String tempFilePrefix = "librocksdbjni";
    private static final String tempFileSuffix = Environment.getJniLibraryExtension();
    private static boolean DEBUG_LOADING = "true".equals(System.getProperty("ROCKS_JAVA_DEBUG_NLL", "false"));

    public static NativeLibraryLoader getInstance() {
        return instance;
    }

    public synchronized void loadLibrary(String string) throws IOException {
        try {
            System.loadLibrary(sharedLibraryName);
            return;
        }
        catch (UnsatisfiedLinkError unsatisfiedLinkError) {
            if (DEBUG_LOADING) {
                System.out.println("Unable to load shared dynamic library: " + sharedLibraryName);
            }
            try {
                System.loadLibrary(jniLibraryName);
                return;
            }
            catch (UnsatisfiedLinkError unsatisfiedLinkError2) {
                block9: {
                    if (DEBUG_LOADING) {
                        System.out.println("Unable to load shared static library: " + jniLibraryName);
                    }
                    if (fallbackJniLibraryName != null) {
                        try {
                            System.loadLibrary(fallbackJniLibraryName);
                            return;
                        }
                        catch (UnsatisfiedLinkError unsatisfiedLinkError3) {
                            if (!DEBUG_LOADING) break block9;
                            System.out.println("Unable to load shared static fallback library: " + fallbackJniLibraryName);
                        }
                    }
                }
                this.loadLibraryFromJar(string);
                return;
            }
        }
    }

    void loadLibraryFromJar(String string) throws IOException {
        if (!initialized) {
            System.load(this.loadLibraryFromJarToTemp(string).getAbsolutePath());
            initialized = true;
        }
    }

    private File createTemp(String string, String string2) throws IOException {
        File file;
        if (string == null || string.isEmpty()) {
            file = File.createTempFile(tempFilePrefix, tempFileSuffix);
        } else {
            File file2 = new File(string);
            if (!file2.exists()) {
                throw new RuntimeException("Directory: " + file2.getAbsolutePath() + " does not exist!");
            }
            file = new File(file2, string2);
            if (file.exists() && !file.delete()) {
                throw new RuntimeException("File: " + file.getAbsolutePath() + " already exists and cannot be removed.");
            }
            if (!file.createNewFile()) {
                throw new RuntimeException("File: " + file.getAbsolutePath() + " could not be created.");
            }
        }
        if (file.exists()) {
            file.deleteOnExit();
            return file;
        }
        throw new RuntimeException("File " + file.getAbsolutePath() + " does not exist.");
    }

    File loadLibraryFromJarToTemp(String string) throws IOException {
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(jniLibraryFileName);){
            if (inputStream != null) {
                File file = this.createTemp(string, jniLibraryFileName);
                Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                File file2 = file;
                return file2;
            }
            if (DEBUG_LOADING) {
                System.out.println("Unable to find: " + jniLibraryFileName + " on the classpath");
            }
        }
        if (fallbackJniLibraryFileName == null) {
            throw new RuntimeException(jniLibraryFileName + " was not found inside JAR, and there is no fallback.");
        }
        inputStream = this.getClass().getClassLoader().getResourceAsStream(fallbackJniLibraryFileName);
        var3_3 = null;
        try {
            if (inputStream != null) {
                File file = this.createTemp(string, fallbackJniLibraryFileName);
                Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                File file3 = file;
                return file3;
            }
            if (DEBUG_LOADING) {
                System.out.println("Unable to find fallback: " + fallbackJniLibraryFileName + " on the classpath");
            }
        }
        catch (Throwable throwable) {
            var3_3 = throwable;
            throw throwable;
        }
        finally {
            if (inputStream != null) {
                if (var3_3 != null) {
                    try {
                        inputStream.close();
                    }
                    catch (Throwable throwable) {
                        var3_3.addSuppressed(throwable);
                    }
                } else {
                    inputStream.close();
                }
            }
        }
        throw new RuntimeException("Neither " + jniLibraryFileName + " or " + fallbackJniLibraryFileName + " were found inside the JAR, and there is no fallback.");
    }

    private NativeLibraryLoader() {
    }
}

