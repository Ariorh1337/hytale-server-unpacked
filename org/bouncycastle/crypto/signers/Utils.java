/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.signers;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.DSAKeyParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.GOST3410KeyParameters;

class Utils {
    Utils() {
    }

    static CryptoServiceProperties getDefaultProperties(String string, DSAKeyParameters dSAKeyParameters, boolean bl) {
        return new DefaultServiceProperties(string, ConstraintUtils.bitsOfSecurityFor(dSAKeyParameters.getParameters().getP()), dSAKeyParameters, Utils.getPurpose(bl));
    }

    static CryptoServiceProperties getDefaultProperties(String string, GOST3410KeyParameters gOST3410KeyParameters, boolean bl) {
        return new DefaultServiceProperties(string, ConstraintUtils.bitsOfSecurityFor(gOST3410KeyParameters.getParameters().getP()), gOST3410KeyParameters, Utils.getPurpose(bl));
    }

    static CryptoServiceProperties getDefaultProperties(String string, ECKeyParameters eCKeyParameters, boolean bl) {
        return new DefaultServiceProperties(string, ConstraintUtils.bitsOfSecurityFor(eCKeyParameters.getParameters().getCurve()), eCKeyParameters, Utils.getPurpose(bl));
    }

    static CryptoServiceProperties getDefaultProperties(String string, int n, CipherParameters cipherParameters, boolean bl) {
        return new DefaultServiceProperties(string, n, cipherParameters, Utils.getPurpose(bl));
    }

    static CryptoServicePurpose getPurpose(boolean bl) {
        return bl ? CryptoServicePurpose.SIGNING : CryptoServicePurpose.VERIFYING;
    }
}

