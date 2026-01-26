/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.prefab.selection.buffer;

import com.hypixel.hytale.server.core.prefab.selection.buffer.PrefabBufferUtil;
import com.hypixel.hytale.server.core.util.io.FileUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import javax.annotation.Nonnull;

public class PrefabLoader {
    private static final char JSON_FILEPATH_SEPARATOR = '.';
    private final Path rootFolder;

    public PrefabLoader(Path rootFolder) {
        this.rootFolder = rootFolder;
    }

    public Path getRootFolder() {
        return this.rootFolder;
    }

    public void resolvePrefabs(@Nonnull String prefabName, @Nonnull Consumer<Path> pathConsumer) throws IOException {
        PrefabLoader.resolvePrefabs(this.rootFolder, prefabName, pathConsumer);
    }

    public static void resolvePrefabs(@Nonnull Path rootFolder, @Nonnull String prefabName, @Nonnull Consumer<Path> pathConsumer) throws IOException {
        if (prefabName.endsWith(".*")) {
            PrefabLoader.resolvePrefabFolder(rootFolder, prefabName, pathConsumer);
        } else {
            Path prefabPath = rootFolder.resolve(prefabName.replace('.', File.separatorChar) + ".prefab.json");
            if (!Files.exists(prefabPath, new LinkOption[0])) {
                throw new NoSuchFileException(prefabPath.toString());
            }
            pathConsumer.accept(prefabPath);
        }
    }

    public static void resolvePrefabFolder(@Nonnull Path rootFolder, @Nonnull String prefabName, final @Nonnull Consumer<Path> pathConsumer) throws IOException {
        String prefabDirectory = prefabName.substring(0, prefabName.length() - 2);
        Path directoryPath = rootFolder.resolve(prefabDirectory.replace('.', File.separatorChar));
        if (!Files.isDirectory(directoryPath, new LinkOption[0])) {
            throw new NotDirectoryException(directoryPath.toString());
        }
        Files.walkFileTree(directoryPath, FileUtil.DEFAULT_WALK_TREE_OPTIONS_SET, Integer.MAX_VALUE, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

            @Override
            @Nonnull
            public FileVisitResult visitFile(@Nonnull Path file, BasicFileAttributes attrs) {
                String fileName = file.getFileName().toString();
                Matcher matcher = PrefabBufferUtil.FILE_SUFFIX_PATTERN.matcher(fileName);
                if (matcher.find()) {
                    String fileNameNoExtension = matcher.replaceAll("");
                    pathConsumer.accept(file.resolveSibling(fileNameNoExtension));
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Nonnull
    public static String resolveRelativeJsonPath(@Nonnull String prefabName, @Nonnull Path prefabPath, @Nonnull Path rootPrefabDir) {
        if (!prefabName.endsWith(".*")) {
            return prefabName;
        }
        String filepath = rootPrefabDir.relativize(prefabPath).toString();
        int start = prefabName.equals(".*") ? 0 : prefabName.length() - 1;
        int length = PrefabLoader.getFilepathLengthNoExtension(filepath);
        if (length < start) {
            throw new IllegalArgumentException(String.format("Prefab key '%s' is longer than its filepath '%s'", prefabName, filepath));
        }
        char[] chars = new char[length - start];
        filepath.getChars(start, length, chars, 0);
        for (int i = 0; i < chars.length; ++i) {
            if (chars[i] != File.separatorChar) continue;
            chars[i] = 46;
        }
        return new String(chars);
    }

    private static int getFilepathLengthNoExtension(@Nonnull String filepath) {
        int extensionSize = 0;
        if (filepath.endsWith(".prefab.json")) {
            extensionSize = ".prefab.json".length();
        } else if (filepath.endsWith(".prefab.json.lpf")) {
            extensionSize = ".prefab.json.lpf".length();
        }
        return filepath.length() - extensionSize;
    }
}

