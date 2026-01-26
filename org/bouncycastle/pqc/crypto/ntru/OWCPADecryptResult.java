/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.ntru;

class OWCPADecryptResult {
    final byte[] rm;
    final int fail;

    public OWCPADecryptResult(byte[] byArray, int n) {
        this.rm = byArray;
        this.fail = n;
    }
}

