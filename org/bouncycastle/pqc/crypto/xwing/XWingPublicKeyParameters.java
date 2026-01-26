/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.xwing;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters;
import org.bouncycastle.pqc.crypto.xwing.XWingKeyParameters;
import org.bouncycastle.util.Arrays;

public class XWingPublicKeyParameters
extends XWingKeyParameters {
    private final MLKEMPublicKeyParameters kybPub;
    private final X25519PublicKeyParameters xdhPub;

    XWingPublicKeyParameters(AsymmetricKeyParameter asymmetricKeyParameter, AsymmetricKeyParameter asymmetricKeyParameter2) {
        super(false);
        this.kybPub = (MLKEMPublicKeyParameters)asymmetricKeyParameter;
        this.xdhPub = (X25519PublicKeyParameters)asymmetricKeyParameter2;
    }

    public XWingPublicKeyParameters(byte[] byArray) {
        super(false);
        this.kybPub = new MLKEMPublicKeyParameters(MLKEMParameters.ml_kem_768, Arrays.copyOfRange(byArray, 0, byArray.length - 32));
        this.xdhPub = new X25519PublicKeyParameters(byArray, byArray.length - 32);
    }

    MLKEMPublicKeyParameters getKyberPublicKey() {
        return this.kybPub;
    }

    X25519PublicKeyParameters getXDHPublicKey() {
        return this.xdhPub;
    }

    public byte[] getEncoded() {
        return Arrays.concatenate(this.kybPub.getEncoded(), this.xdhPub.getEncoded());
    }
}

