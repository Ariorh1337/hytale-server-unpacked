/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.xwing;

import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters;
import org.bouncycastle.pqc.crypto.xwing.XWingKeyPairGenerator;
import org.bouncycastle.pqc.crypto.xwing.XWingKeyParameters;
import org.bouncycastle.util.Arrays;

public class XWingPrivateKeyParameters
extends XWingKeyParameters {
    private final transient byte[] seed;
    private final transient MLKEMPrivateKeyParameters kyberPrivateKey;
    private final transient X25519PrivateKeyParameters xdhPrivateKey;
    private final transient MLKEMPublicKeyParameters kyberPublicKey;
    private final transient X25519PublicKeyParameters xdhPublicKey;

    public XWingPrivateKeyParameters(byte[] byArray, MLKEMPrivateKeyParameters mLKEMPrivateKeyParameters, X25519PrivateKeyParameters x25519PrivateKeyParameters, MLKEMPublicKeyParameters mLKEMPublicKeyParameters, X25519PublicKeyParameters x25519PublicKeyParameters) {
        super(true);
        this.seed = Arrays.clone(byArray);
        this.kyberPrivateKey = mLKEMPrivateKeyParameters;
        this.xdhPrivateKey = x25519PrivateKeyParameters;
        this.kyberPublicKey = mLKEMPublicKeyParameters;
        this.xdhPublicKey = x25519PublicKeyParameters;
    }

    public XWingPrivateKeyParameters(byte[] byArray) {
        super(true);
        XWingPrivateKeyParameters xWingPrivateKeyParameters = (XWingPrivateKeyParameters)XWingKeyPairGenerator.genKeyPair(byArray).getPrivate();
        this.seed = xWingPrivateKeyParameters.seed;
        this.kyberPrivateKey = xWingPrivateKeyParameters.kyberPrivateKey;
        this.xdhPrivateKey = xWingPrivateKeyParameters.xdhPrivateKey;
        this.kyberPublicKey = xWingPrivateKeyParameters.kyberPublicKey;
        this.xdhPublicKey = xWingPrivateKeyParameters.xdhPublicKey;
    }

    public byte[] getSeed() {
        return Arrays.clone(this.seed);
    }

    MLKEMPrivateKeyParameters getKyberPrivateKey() {
        return this.kyberPrivateKey;
    }

    MLKEMPublicKeyParameters getKyberPublicKey() {
        return this.kyberPublicKey;
    }

    X25519PrivateKeyParameters getXDHPrivateKey() {
        return this.xdhPrivateKey;
    }

    X25519PublicKeyParameters getXDHPublicKey() {
        return this.xdhPublicKey;
    }

    public byte[] getEncoded() {
        return Arrays.clone(this.seed);
    }
}

