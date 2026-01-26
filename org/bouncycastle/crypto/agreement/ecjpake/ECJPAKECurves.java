/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.agreement.ecjpake;

import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.agreement.ecjpake.ECJPAKECurve;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.math.ec.ECCurve;

public class ECJPAKECurves {
    public static final ECJPAKECurve NIST_P256 = ECJPAKECurves.getCurve("P-256");
    public static final ECJPAKECurve NIST_P384 = ECJPAKECurves.getCurve("P-384");
    public static final ECJPAKECurve NIST_P521 = ECJPAKECurves.getCurve("P-521");

    private static ECJPAKECurve getCurve(String string) {
        X9ECParameters x9ECParameters = CustomNamedCurves.getByName(string);
        return new ECJPAKECurve((ECCurve.AbstractFp)x9ECParameters.getCurve(), x9ECParameters.getG());
    }
}

