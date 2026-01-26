/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.hybrid.internal;

import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;

@Immutable
public interface HpkeKdf {
    public byte[] labeledExtract(byte[] var1, byte[] var2, String var3, byte[] var4) throws GeneralSecurityException;

    public byte[] labeledExpand(byte[] var1, byte[] var2, String var3, byte[] var4, int var5) throws GeneralSecurityException;

    public byte[] extractAndExpand(byte[] var1, byte[] var2, String var3, byte[] var4, String var5, byte[] var6, int var7) throws GeneralSecurityException;

    public byte[] getKdfId() throws GeneralSecurityException;
}

