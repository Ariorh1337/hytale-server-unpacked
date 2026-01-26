/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.eac.jcajce;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import org.bouncycastle.eac.jcajce.EACHelper;

class DefaultEACHelper
implements EACHelper {
    DefaultEACHelper() {
    }

    @Override
    public KeyFactory createKeyFactory(String string) throws NoSuchAlgorithmException {
        return KeyFactory.getInstance(string);
    }
}

