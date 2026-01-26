/*
 * Decompiled with CFR 0.152.
 */
package org.jline.builtins;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.jline.reader.LineReader;

public final class InputRC {
    public static void configure(LineReader reader, URL url) throws IOException {
        org.jline.reader.impl.InputRC.configure(reader, url);
    }

    public static void configure(LineReader reader, InputStream is) throws IOException {
        org.jline.reader.impl.InputRC.configure(reader, is);
    }

    public static void configure(LineReader reader, Reader r) throws IOException {
        org.jline.reader.impl.InputRC.configure(reader, r);
    }

    public static void configure(LineReader lineReader, Path path) throws IOException {
        if (Files.exists(path, new LinkOption[0]) && Files.isRegularFile(path, new LinkOption[0]) && Files.isReadable(path)) {
            try (BufferedReader reader = Files.newBufferedReader(path);){
                InputRC.configure(lineReader, reader);
            }
        }
    }

    public static void configure(LineReader lineReader) throws IOException {
        String userHome = System.getProperty("user.home");
        if (userHome != null) {
            InputRC.configure(lineReader, Paths.get(userHome, ".inputrc"));
        }
        InputRC.configure(lineReader, Paths.get("/etc/inputrc", new String[0]));
    }
}

