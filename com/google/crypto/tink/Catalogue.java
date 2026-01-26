/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink;

import com.google.crypto.tink.KeyManager;
import com.google.crypto.tink.internal.PrimitiveWrapper;
import java.security.GeneralSecurityException;

@Deprecated
public interface Catalogue<P> {
    public KeyManager<P> getKeyManager(String var1, String var2, int var3) throws GeneralSecurityException;

    public PrimitiveWrapper<?, P> getPrimitiveWrapper() throws GeneralSecurityException;
}

