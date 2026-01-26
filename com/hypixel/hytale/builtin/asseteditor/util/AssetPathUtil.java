/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.asseteditor.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;

public class AssetPathUtil {
    public static final String UNIX_FILE_SEPARATOR = "/";
    public static final String FILE_EXTENSION_JSON = ".json";
    public static final String DIR_SERVER = "Server";
    public static final String DIR_COMMON = "Common";
    public static final Path PATH_DIR_COMMON = Paths.get("Common", new String[0]);
    public static final Path PATH_DIR_SERVER = Paths.get("Server", new String[0]);
    public static final Path EMPTY_PATH = Path.of("", new String[0]);
    private static final Pattern INVALID_FILENAME_CHAR_REGEX = Pattern.compile("[<>:\"|?*/\\\\]");
    private static final String[] RESERVED_NAMES = new String[]{"CON", "PRN", "AUX", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

    public static boolean isInvalidFileName(@Nonnull Path path) {
        int codePoint;
        String fileName = path.getFileName().toString();
        if (fileName.isEmpty()) {
            return true;
        }
        if (fileName.charAt(fileName.length() - 1) == '.') {
            return true;
        }
        for (int i = 0; i < fileName.length(); i += Character.charCount(codePoint)) {
            codePoint = fileName.codePointAt(i);
            if (codePoint < 31) {
                return true;
            }
            switch (codePoint) {
                case 34: 
                case 42: 
                case 58: 
                case 60: 
                case 62: 
                case 63: 
                case 124: {
                    return true;
                }
            }
        }
        int pos = fileName.indexOf(46);
        if (pos == 0) {
            return false;
        }
        String baseFileName = pos < 0 ? fileName : fileName.substring(0, pos);
        for (String str : RESERVED_NAMES) {
            if (!str.equals(baseFileName)) continue;
            return true;
        }
        return false;
    }

    public static String removeInvalidFileNameChars(String name) {
        return INVALID_FILENAME_CHAR_REGEX.matcher(name).replaceAll("");
    }

    @Nonnull
    private static String getIdFromPath(@Nonnull Path path) {
        String fileName = path.getFileName().toString();
        int extensionIndex = fileName.lastIndexOf(46);
        if (extensionIndex == -1) {
            return fileName;
        }
        return fileName.substring(0, extensionIndex);
    }
}

