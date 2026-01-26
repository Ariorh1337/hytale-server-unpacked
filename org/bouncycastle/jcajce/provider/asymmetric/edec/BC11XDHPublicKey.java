/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.jcajce.provider.asymmetric.edec.BC11XDHPublicKey
 */
package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.security.interfaces.XECPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.NamedParameterSpec;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCXDHPublicKey;

class BC11XDHPublicKey
extends BCXDHPublicKey
implements XECPublicKey {
    BC11XDHPublicKey(AsymmetricKeyParameter asymmetricKeyParameter) {
        super(asymmetricKeyParameter);
    }

    BC11XDHPublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        super(subjectPublicKeyInfo);
    }

    BC11XDHPublicKey(byte[] byArray, byte[] byArray2) throws InvalidKeySpecException {
        super(byArray, byArray2);
    }

    @Override
    public AlgorithmParameterSpec getParams() {
        if (this.xdhPublicKey instanceof X448PublicKeyParameters) {
            return NamedParameterSpec.X448;
        }
        return NamedParameterSpec.X25519;
    }
}

