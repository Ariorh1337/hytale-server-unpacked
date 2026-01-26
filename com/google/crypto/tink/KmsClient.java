/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink;

import com.google.crypto.tink.Aead;
import java.security.GeneralSecurityException;

public interface KmsClient {
    public boolean doesSupport(String var1);

    public KmsClient withCredentials(String var1) throws GeneralSecurityException;

    public KmsClient withDefaultCredentials() throws GeneralSecurityException;

    public Aead getAead(String var1) throws GeneralSecurityException;
}

