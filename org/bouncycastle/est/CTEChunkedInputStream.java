/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.est;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CTEChunkedInputStream
extends InputStream {
    private InputStream src;
    int chunkLen = 0;

    public CTEChunkedInputStream(InputStream inputStream) {
        this.src = inputStream;
    }

    private String readEOL() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int n = 0;
        do {
            if ((n = this.src.read()) == -1) {
                if (byteArrayOutputStream.size() == 0) {
                    return null;
                }
                return byteArrayOutputStream.toString().trim();
            }
            byteArrayOutputStream.write(n & 0xFF);
        } while (n != 10);
        return byteArrayOutputStream.toString().trim();
    }

    @Override
    public int read() throws IOException {
        if (this.chunkLen == Integer.MIN_VALUE) {
            return -1;
        }
        if (this.chunkLen == 0) {
            String string = null;
            while ((string = this.readEOL()) != null && string.length() == 0) {
            }
            if (string == null) {
                return -1;
            }
            this.chunkLen = Integer.parseInt(string.trim(), 16);
            if (this.chunkLen == 0) {
                this.readEOL();
                this.chunkLen = Integer.MIN_VALUE;
                return -1;
            }
        }
        int n = this.src.read();
        --this.chunkLen;
        return n;
    }
}

