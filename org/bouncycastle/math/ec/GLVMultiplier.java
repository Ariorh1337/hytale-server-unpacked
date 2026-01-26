/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.AbstractECMultiplier;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.endo.EndoUtil;
import org.bouncycastle.math.ec.endo.GLVEndomorphism;

public class GLVMultiplier
extends AbstractECMultiplier {
    protected final ECCurve curve;
    protected final GLVEndomorphism glvEndomorphism;

    public GLVMultiplier(ECCurve eCCurve, GLVEndomorphism gLVEndomorphism) {
        if (eCCurve == null || eCCurve.getOrder() == null) {
            throw new IllegalArgumentException("Need curve with known group order");
        }
        this.curve = eCCurve;
        this.glvEndomorphism = gLVEndomorphism;
    }

    @Override
    protected ECPoint multiplyPositive(ECPoint eCPoint, BigInteger bigInteger) {
        if (!this.curve.equals(eCPoint.getCurve())) {
            throw new IllegalStateException();
        }
        BigInteger bigInteger2 = eCPoint.getCurve().getOrder();
        BigInteger[] bigIntegerArray = this.glvEndomorphism.decomposeScalar(bigInteger.mod(bigInteger2));
        BigInteger bigInteger3 = bigIntegerArray[0];
        BigInteger bigInteger4 = bigIntegerArray[1];
        if (this.glvEndomorphism.hasEfficientPointMap()) {
            return ECAlgorithms.implShamirsTrickWNaf(this.glvEndomorphism, eCPoint, bigInteger3, bigInteger4);
        }
        ECPoint eCPoint2 = EndoUtil.mapPoint(this.glvEndomorphism, eCPoint);
        return ECAlgorithms.implShamirsTrickWNaf(eCPoint, bigInteger3, eCPoint2, bigInteger4);
    }
}

