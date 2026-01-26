/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.its.jcajce;

import java.math.BigInteger;
import java.security.spec.ECField;
import java.security.spec.ECFieldF2m;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.field.FiniteField;
import org.bouncycastle.math.field.Polynomial;
import org.bouncycastle.math.field.PolynomialExtensionField;
import org.bouncycastle.util.Arrays;

class ECUtil {
    ECUtil() {
    }

    static ECPoint convertPoint(org.bouncycastle.math.ec.ECPoint eCPoint) {
        eCPoint = eCPoint.normalize();
        return new ECPoint(eCPoint.getAffineXCoord().toBigInteger(), eCPoint.getAffineYCoord().toBigInteger());
    }

    public static EllipticCurve convertCurve(ECCurve eCCurve, byte[] byArray) {
        ECField eCField = ECUtil.convertField(eCCurve.getField());
        BigInteger bigInteger = eCCurve.getA().toBigInteger();
        BigInteger bigInteger2 = eCCurve.getB().toBigInteger();
        return new EllipticCurve(eCField, bigInteger, bigInteger2, null);
    }

    public static ECParameterSpec convertToSpec(X9ECParameters x9ECParameters) {
        return new ECParameterSpec(ECUtil.convertCurve(x9ECParameters.getCurve(), null), ECUtil.convertPoint(x9ECParameters.getG()), x9ECParameters.getN(), x9ECParameters.getH().intValue());
    }

    public static ECField convertField(FiniteField finiteField) {
        if (ECAlgorithms.isFpField(finiteField)) {
            return new ECFieldFp(finiteField.getCharacteristic());
        }
        Polynomial polynomial = ((PolynomialExtensionField)finiteField).getMinimalPolynomial();
        int[] nArray = polynomial.getExponentsPresent();
        int[] nArray2 = Arrays.reverseInPlace(Arrays.copyOfRange(nArray, 1, nArray.length - 1));
        return new ECFieldF2m(polynomial.getDegree(), nArray2);
    }
}

