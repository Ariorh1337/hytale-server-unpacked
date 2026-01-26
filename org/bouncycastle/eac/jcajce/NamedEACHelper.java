/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.eac.jcajce;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import org.bouncycastle.eac.jcajce.EACHelper;

class NamedEACHelper
implements EACHelper {
    private final String providerName;

    NamedEACHelper(String string) {
        this.providerName = string;
    }

    @Override
    public KeyFactory createKeyFactory(String string) throws NoSuchProviderException, NoSuchAlgorithmException {
        return KeyFactory.getInstance(string, this.providerName);
    }
}

