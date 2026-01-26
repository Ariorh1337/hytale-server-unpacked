/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.util.io;

import com.hypixel.hytale.sneakythrow.SneakyThrow;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Comparator;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import javax.annotation.Nonnull;

public class FileUtil {
    public static final Set<OpenOption> DEFAULT_WRITE_OPTIONS = Set.of(StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    public static final Set<FileVisitOption> DEFAULT_WALK_TREE_OPTIONS_SET = Set.of();
    public static final FileVisitOption[] DEFAULT_WALK_TREE_OPTIONS_ARRAY = new FileVisitOption[0];
    public static final Pattern INVALID_FILENAME_CHARACTERS = Pattern.compile("[<>:\"|?*/\\\\]");

    public static void unzipFile(@Nonnull Path path, @Nonnull byte[] buffer, @Nonnull ZipInputStream zipStream, @Nonnull ZipEntry zipEntry, @Nonnull String name) throws IOException {
        Path filePath = path.resolve(name);
        if (!filePath.toAbsolutePath().startsWith(path)) {
            throw new ZipException("Entry is outside of the target dir: " + zipEntry.getName());
        }
        if (zipEntry.isDirectory()) {
            Files.createDirectory(filePath, new FileAttribute[0]);
        } else {
            try (OutputStream stream = Files.newOutputStream(filePath, new OpenOption[0]);){
                int len;
                while ((len = zipStream.read(buffer)) > 0) {
                    stream.write(buffer, 0, len);
                }
            }
        }
        zipStream.closeEntry();
    }

    public static void copyDirectory(@Nonnull Path origin, @Nonnull Path destination) throws IOException {
        try (Stream<Path> paths = Files.walk(origin, new FileVisitOption[0]);){
            paths.forEach(originSubPath -> {
                try {
                    Path relative = origin.relativize((Path)originSubPath);
                    Path destinationSubPath = destination.resolve(relative);
                    Files.copy(originSubPath, destinationSubPath, new CopyOption[0]);
                }
                catch (Throwable t) {
                    throw new RuntimeException("Error copying path", t);
                }
            });
        }
    }

    public static void moveDirectoryContents(@Nonnull Path origin, @Nonnull Path destination, CopyOption ... options) throws IOException {
        try (Stream<Path> paths = Files.walk(origin, new FileVisitOption[0]);){
            paths.forEach(originSubPath -> {
                if (originSubPath.equals(origin)) {
                    return;
                }
                try {
                    Path relative = origin.relativize((Path)originSubPath);
                    Path destinationSubPath = destination.resolve(relative);
                    Files.move(originSubPath, destinationSubPath, options);
                }
                catch (Throwable t) {
                    throw new RuntimeException("Error moving path", t);
                }
            });
        }
    }

    public static void deleteDirectory(@Nonnull Path path) throws IOException {
        try (Stream<Path> stream = Files.walk(path, new FileVisitOption[0]);){
            stream.sorted(Comparator.reverseOrder()).forEach(SneakyThrow.sneakyConsumer(Files::delete));
        }
    }

    public static void extractZip(@Nonnull Path zipFile, @Nonnull Path destDir) throws IOException {
        FileUtil.extractZip(Files.newInputStream(zipFile, new OpenOption[0]), destDir);
    }

    public static void extractZip(@Nonnull InputStream inputStream, @Nonnull Path destDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(inputStream);){
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path destPath = destDir.resolve(entry.getName()).normalize();
                if (!destPath.startsWith(destDir)) {
                    throw new ZipException("Zip entry outside target directory: " + entry.getName());
                }
                if (entry.isDirectory()) {
                    Files.createDirectories(destPath, new FileAttribute[0]);
                } else {
                    Files.createDirectories(destPath.getParent(), new FileAttribute[0]);
                    Files.copy(zis, destPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }
    }
}

