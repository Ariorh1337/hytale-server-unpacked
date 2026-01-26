/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.daead.subtle;

import com.google.crypto.tink.DeterministicAead;
import java.security.GeneralSecurityException;

public interface DeterministicAeads
extends DeterministicAead {
    public byte[] encryptDeterministicallyWithAssociatedDatas(byte[] var1, byte[] ... var2) throws GeneralSecurityException;

    public byte[] decryptDeterministicallyWithAssociatedDatas(byte[] var1, byte[] ... var2) throws GeneralSecurityException;
}

