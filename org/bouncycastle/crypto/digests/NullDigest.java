/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Arrays;

public class NullDigest
implements Digest {
    private OpenByteArrayOutputStream bOut = new OpenByteArrayOutputStream();

    @Override
    public String getAlgorithmName() {
        return "NULL";
    }

    @Override
    public int getDigestSize() {
        return this.bOut.size();
    }

    @Override
    public void update(byte by) {
        this.bOut.write(by);
    }

    @Override
    public void update(byte[] byArray, int n, int n2) {
        this.bOut.write(byArray, n, n2);
    }

    @Override
    public int doFinal(byte[] byArray, int n) {
        int n2 = this.bOut.size();
        this.bOut.copy(byArray, n);
        this.reset();
        return n2;
    }

    @Override
    public void reset() {
        this.bOut.reset();
    }

    private static class OpenByteArrayOutputStream
    extends ByteArrayOutputStream {
        private OpenByteArrayOutputStream() {
        }

        @Override
        public void reset() {
            super.reset();
            Arrays.clear(this.buf);
        }

        void copy(byte[] byArray, int n) {
            System.arraycopy(this.buf, 0, byArray, n, this.size());
        }
    }
}

