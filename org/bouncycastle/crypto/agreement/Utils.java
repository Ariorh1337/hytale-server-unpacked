/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.agreement;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.DHKeyParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X448PrivateKeyParameters;

class Utils {
    Utils() {
    }

    static CryptoServiceProperties getDefaultProperties(String string, ECKeyParameters eCKeyParameters) {
        return new DefaultServiceProperties(string, ConstraintUtils.bitsOfSecurityFor(eCKeyParameters.getParameters().getCurve()), eCKeyParameters, CryptoServicePurpose.AGREEMENT);
    }

    static CryptoServiceProperties getDefaultProperties(String string, DHKeyParameters dHKeyParameters) {
        return new DefaultServiceProperties(string, ConstraintUtils.bitsOfSecurityFor(dHKeyParameters.getParameters().getP()), dHKeyParameters, CryptoServicePurpose.AGREEMENT);
    }

    static CryptoServiceProperties getDefaultProperties(String string, X448PrivateKeyParameters x448PrivateKeyParameters) {
        return new DefaultServiceProperties(string, 224, x448PrivateKeyParameters, CryptoServicePurpose.AGREEMENT);
    }

    static CryptoServiceProperties getDefaultProperties(String string, X25519PrivateKeyParameters x25519PrivateKeyParameters) {
        return new DefaultServiceProperties(string, 128, x25519PrivateKeyParameters, CryptoServicePurpose.AGREEMENT);
    }
}

