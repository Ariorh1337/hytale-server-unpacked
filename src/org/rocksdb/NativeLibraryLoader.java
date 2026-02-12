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

   public synchronized void loadLibrary(String var1) throws IOException {
      try {
         System.loadLibrary(sharedLibraryName);
      } catch (UnsatisfiedLinkError var5) {
         if (DEBUG_LOADING) {
            System.out.println("Unable to load shared dynamic library: " + sharedLibraryName);
         }

         try {
            System.loadLibrary(jniLibraryName);
         } catch (UnsatisfiedLinkError var4) {
            if (DEBUG_LOADING) {
               System.out.println("Unable to load shared static library: " + jniLibraryName);
            }

            if (fallbackJniLibraryName != null) {
               try {
                  System.loadLibrary(fallbackJniLibraryName);
                  return;
               } catch (UnsatisfiedLinkError var3) {
                  if (DEBUG_LOADING) {
                     System.out.println("Unable to load shared static fallback library: " + fallbackJniLibraryName);
                  }
               }
            }

            this.loadLibraryFromJar(var1);
         }
      }
   }

   void loadLibraryFromJar(String var1) throws IOException {
      if (!initialized) {
         System.load(this.loadLibraryFromJarToTemp(var1).getAbsolutePath());
         initialized = true;
      }
   }

   private File createTemp(String var1, String var2) throws IOException {
      File var3;
      if (var1 != null && !var1.isEmpty()) {
         File var4 = new File(var1);
         if (!var4.exists()) {
            throw new RuntimeException("Directory: " + var4.getAbsolutePath() + " does not exist!");
         }

         var3 = new File(var4, var2);
         if (var3.exists() && !var3.delete()) {
            throw new RuntimeException("File: " + var3.getAbsolutePath() + " already exists and cannot be removed.");
         }

         if (!var3.createNewFile()) {
            throw new RuntimeException("File: " + var3.getAbsolutePath() + " could not be created.");
         }
      } else {
         var3 = File.createTempFile("librocksdbjni", tempFileSuffix);
      }

      if (var3.exists()) {
         var3.deleteOnExit();
         return var3;
      } else {
         throw new RuntimeException("File " + var3.getAbsolutePath() + " does not exist.");
      }
   }

   File loadLibraryFromJarToTemp(String var1) throws IOException {
      try (InputStream var2 = this.getClass().getClassLoader().getResourceAsStream(jniLibraryFileName)) {
         if (var2 != null) {
            File var37 = this.createTemp(var1, jniLibraryFileName);
            Files.copy(var2, var37.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return var37;
         }

         if (DEBUG_LOADING) {
            System.out.println("Unable to find: " + jniLibraryFileName + " on the classpath");
         }
      }

      if (fallbackJniLibraryFileName == null) {
         throw new RuntimeException(jniLibraryFileName + " was not found inside JAR, and there is no fallback.");
      }

      try (InputStream var35 = this.getClass().getClassLoader().getResourceAsStream(fallbackJniLibraryFileName)) {
         if (var35 != null) {
            File var4 = this.createTemp(var1, fallbackJniLibraryFileName);
            Files.copy(var35, var4.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return var4;
         }

         if (DEBUG_LOADING) {
            System.out.println("Unable to find fallback: " + fallbackJniLibraryFileName + " on the classpath");
         }
      }

      throw new RuntimeException(
         "Neither " + jniLibraryFileName + " or " + fallbackJniLibraryFileName + " were found inside the JAR, and there is no fallback."
      );
   }

   private NativeLibraryLoader() {
   }
}
