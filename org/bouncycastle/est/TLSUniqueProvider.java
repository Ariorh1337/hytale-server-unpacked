/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.est;

public interface TLSUniqueProvider {
    public boolean isTLSUniqueAvailable();

    public byte[] getTLSUnique();
}

