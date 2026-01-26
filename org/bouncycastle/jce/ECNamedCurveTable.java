/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;

public class ECNamedCurveTable {
    public static ECNamedCurveParameterSpec getParameterSpec(String string) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier;
        try {
            aSN1ObjectIdentifier = ECNamedCurveTable.possibleOID(string) ? new ASN1ObjectIdentifier(string) : null;
        }
        catch (IllegalArgumentException illegalArgumentException) {
            aSN1ObjectIdentifier = null;
        }
        X9ECParameters x9ECParameters = aSN1ObjectIdentifier != null ? CustomNamedCurves.getByOID(aSN1ObjectIdentifier) : CustomNamedCurves.getByName(string);
        if (x9ECParameters == null) {
            x9ECParameters = aSN1ObjectIdentifier != null ? org.bouncycastle.asn1.x9.ECNamedCurveTable.getByOID(aSN1ObjectIdentifier) : org.bouncycastle.asn1.x9.ECNamedCurveTable.getByName(string);
        }
        if (x9ECParameters == null) {
            return null;
        }
        return new ECNamedCurveParameterSpec(string, x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN(), x9ECParameters.getH(), x9ECParameters.getSeed());
    }

    public static Enumeration getNames() {
        return org.bouncycastle.asn1.x9.ECNamedCurveTable.getNames();
    }

    private static boolean possibleOID(String string) {
        if (string.length() < 3 || string.charAt(1) != '.') {
            return false;
        }
        char c = string.charAt(0);
        return c >= '0' && c <= '2';
    }
}

