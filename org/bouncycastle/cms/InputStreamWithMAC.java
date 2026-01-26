/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.cms.MACProvider;
import org.bouncycastle.util.Arrays;

public final class InputStreamWithMAC
extends InputStream {
    private final InputStream base;
    private MACProvider macProvider;
    private byte[] mac;
    private boolean baseFinished;
    private int index;

    InputStreamWithMAC(InputStream inputStream, MACProvider mACProvider) {
        this.base = inputStream;
        this.macProvider = mACProvider;
        this.baseFinished = false;
        this.index = 0;
    }

    public InputStreamWithMAC(InputStream inputStream, byte[] byArray) {
        this.base = inputStream;
        this.mac = byArray;
        this.baseFinished = false;
        this.index = 0;
    }

    @Override
    public int read() throws IOException {
        int n;
        if (!this.baseFinished) {
            n = this.base.read();
            if (n < 0) {
                this.baseFinished = true;
                if (this.macProvider != null) {
                    this.macProvider.init();
                    this.mac = this.macProvider.getMAC();
                }
                return this.mac[this.index++] & 0xFF;
            }
        } else {
            if (this.index >= this.mac.length) {
                return -1;
            }
            return this.mac[this.index++] & 0xFF;
        }
        return n;
    }

    public byte[] getMAC() {
        if (!this.baseFinished) {
            throw new IllegalStateException("input stream not fully processed");
        }
        return Arrays.clone(this.mac);
    }

    @Override
    public int read(byte[] byArray, int n, int n2) throws IOException {
        if (byArray == null) {
            throw new NullPointerException("input array is null");
        }
        if (n < 0 || byArray.length < n + n2) {
            throw new IndexOutOfBoundsException("invalid off(" + n + ") and len(" + n2 + ")");
        }
        if (!this.baseFinished) {
            int n3 = this.base.read(byArray, n, n2);
            if (n3 < 0) {
                this.baseFinished = true;
                if (this.macProvider != null) {
                    this.macProvider.init();
                    this.mac = this.macProvider.getMAC();
                }
                if (n2 >= this.mac.length) {
                    System.arraycopy(this.mac, 0, byArray, n, this.mac.length);
                    this.index = this.mac.length;
                    return this.mac.length;
                }
                System.arraycopy(this.mac, 0, byArray, n, n2);
                this.index += n2;
                return n2;
            }
            return n3;
        }
        if (this.index < this.mac.length) {
            if (n2 >= this.mac.length - this.index) {
                System.arraycopy(this.mac, this.index, byArray, n, this.mac.length - this.index);
                int n4 = this.mac.length - this.index;
                this.index = this.mac.length;
                return n4;
            }
            System.arraycopy(this.mac, this.index, byArray, n, n2);
            this.index += n2;
            return n2;
        }
        return -1;
    }
}

