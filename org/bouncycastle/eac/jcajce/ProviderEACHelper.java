/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.eac.jcajce;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import org.bouncycastle.eac.jcajce.EACHelper;

class ProviderEACHelper
implements EACHelper {
    private final Provider provider;

    ProviderEACHelper(Provider provider) {
        this.provider = provider;
    }

    @Override
    public KeyFactory createKeyFactory(String string) throws NoSuchAlgorithmException {
        return KeyFactory.getInstance(string, this.provider);
    }
}

