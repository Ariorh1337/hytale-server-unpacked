/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import javax.crypto.Cipher;

class JceAADStream
extends OutputStream {
    private final byte[] SINGLE_BYTE = new byte[1];
    private Cipher cipher;

    JceAADStream(Cipher cipher) {
        this.cipher = cipher;
    }

    @Override
    public void write(byte[] byArray, int n, int n2) throws IOException {
        this.cipher.updateAAD(byArray, n, n2);
    }

    @Override
    public void write(int n) throws IOException {
        this.SINGLE_BYTE[0] = (byte)n;
        this.cipher.updateAAD(this.SINGLE_BYTE, 0, 1);
    }
}

