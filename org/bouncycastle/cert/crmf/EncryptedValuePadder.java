/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.crmf;

public interface EncryptedValuePadder {
    public byte[] getPaddedData(byte[] var1);

    public byte[] getUnpaddedData(byte[] var1);
}

