/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.util;

import com.nimbusds.jose.util.StandardCharset;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

public class IOUtils {
    public static String readInputStreamToString(InputStream stream) throws IOException {
        return IOUtils.readInputStreamToString(stream, StandardCharset.UTF_8);
    }

    public static String readInputStreamToString(InputStream stream, Charset charset) throws IOException {
        int bufferSize = 1024;
        char[] buffer = new char[1024];
        StringBuilder out = new StringBuilder();
        try (InputStreamReader in = new InputStreamReader(stream, charset);){
            int rsz;
            while ((rsz = ((Reader)in).read(buffer, 0, buffer.length)) >= 0) {
                out.append(buffer, 0, rsz);
            }
            String string = out.toString();
            return string;
        }
    }

    public static String readFileToString(File file) throws IOException {
        return IOUtils.readInputStreamToString(new FileInputStream(file));
    }

    public static String readFileToString(File file, Charset charset) throws IOException {
        return IOUtils.readInputStreamToString(new FileInputStream(file), charset);
    }

    public static void closeSilently(Closeable closeable) {
        try {
            closeable.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private IOUtils() {
    }
}

