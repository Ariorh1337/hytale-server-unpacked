/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.spec;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import org.bouncycastle.jce.interfaces.IESKey;

public class IEKeySpec
implements KeySpec,
IESKey {
    private PublicKey pubKey;
    private PrivateKey privKey;

    public IEKeySpec(PrivateKey privateKey, PublicKey publicKey) {
        this.privKey = privateKey;
        this.pubKey = publicKey;
    }

    @Override
    public PublicKey getPublic() {
        return this.pubKey;
    }

    @Override
    public PrivateKey getPrivate() {
        return this.privKey;
    }

    @Override
    public String getAlgorithm() {
        return "IES";
    }

    @Override
    public String getFormat() {
        return null;
    }

    @Override
    public byte[] getEncoded() {
        return null;
    }
}

