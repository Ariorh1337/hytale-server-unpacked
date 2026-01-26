/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECCSIPublicKeyParameters;

public class ECCSIPrivateKeyParameters
extends AsymmetricKeyParameter {
    private final BigInteger ssk;
    private final ECCSIPublicKeyParameters pub;

    public ECCSIPrivateKeyParameters(BigInteger bigInteger, ECCSIPublicKeyParameters eCCSIPublicKeyParameters) {
        super(true);
        this.ssk = bigInteger;
        this.pub = eCCSIPublicKeyParameters;
    }

    public ECCSIPublicKeyParameters getPublicKeyParameters() {
        return this.pub;
    }

    public BigInteger getSSK() {
        return this.ssk;
    }
}

