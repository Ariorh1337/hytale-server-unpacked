/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.hybrid.internal;

import com.google.crypto.tink.hybrid.internal.HpkeKemEncapOutput;
import com.google.crypto.tink.hybrid.internal.HpkeKemPrivateKey;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;

@Immutable
public interface HpkeKem {
    public HpkeKemEncapOutput encapsulate(byte[] var1) throws GeneralSecurityException;

    public byte[] decapsulate(byte[] var1, HpkeKemPrivateKey var2) throws GeneralSecurityException;

    public HpkeKemEncapOutput authEncapsulate(byte[] var1, HpkeKemPrivateKey var2) throws GeneralSecurityException;

    public byte[] authDecapsulate(byte[] var1, HpkeKemPrivateKey var2, byte[] var3) throws GeneralSecurityException;

    public byte[] getKemId() throws GeneralSecurityException;
}

