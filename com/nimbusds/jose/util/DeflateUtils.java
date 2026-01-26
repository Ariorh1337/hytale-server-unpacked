/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class DeflateUtils {
    private static final boolean NOWRAP = true;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] compress(byte[] bytes) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Deflater deflater = null;
        DeflaterOutputStream def = null;
        try {
            deflater = new Deflater(8, true);
            def = new DeflaterOutputStream((OutputStream)out, deflater);
            def.write(bytes);
        }
        finally {
            if (def != null) {
                def.close();
            }
            if (deflater != null) {
                deflater.end();
            }
        }
        return out.toByteArray();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] decompress(byte[] bytes) throws IOException {
        Inflater inflater = null;
        InflaterInputStream inf = null;
        try {
            int len;
            inflater = new Inflater(true);
            inf = new InflaterInputStream(new ByteArrayInputStream(bytes), inflater);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            while ((len = inf.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            byte[] byArray = out.toByteArray();
            return byArray;
        }
        finally {
            if (inf != null) {
                inf.close();
            }
            if (inflater != null) {
                inflater.end();
            }
        }
    }

    private DeflateUtils() {
    }
}

