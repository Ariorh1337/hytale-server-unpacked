/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.eac.operator.jcajce;

import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import org.bouncycastle.eac.operator.jcajce.EACHelper;

class DefaultEACHelper
extends EACHelper {
    DefaultEACHelper() {
    }

    @Override
    protected Signature createSignature(String string) throws NoSuchAlgorithmException {
        return Signature.getInstance(string);
    }
}

