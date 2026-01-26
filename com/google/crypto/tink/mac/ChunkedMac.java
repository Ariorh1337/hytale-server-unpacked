/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.mac;

import com.google.crypto.tink.mac.ChunkedMacComputation;
import com.google.crypto.tink.mac.ChunkedMacVerification;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;

@Immutable
public interface ChunkedMac {
    public ChunkedMacComputation createComputation() throws GeneralSecurityException;

    public ChunkedMacVerification createVerification(byte[] var1) throws GeneralSecurityException;
}

