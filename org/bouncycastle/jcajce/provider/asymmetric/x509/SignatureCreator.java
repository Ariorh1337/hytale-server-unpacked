/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;

interface SignatureCreator {
    public Signature createSignature(String var1) throws NoSuchAlgorithmException, NoSuchProviderException;
}

