/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.eac.operator.jcajce;

import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Signature;
import org.bouncycastle.eac.operator.jcajce.EACHelper;

class ProviderEACHelper
extends EACHelper {
    private final Provider provider;

    ProviderEACHelper(Provider provider) {
        this.provider = provider;
    }

    @Override
    protected Signature createSignature(String string) throws NoSuchAlgorithmException {
        return Signature.getInstance(string, this.provider);
    }
}

