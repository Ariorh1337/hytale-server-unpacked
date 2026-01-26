/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.math.ec.AbstractECLookupTable;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECLookupTable;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.custom.sec.SecP192K1Field;
import org.bouncycastle.math.ec.custom.sec.SecP192K1FieldElement;
import org.bouncycastle.math.ec.custom.sec.SecP192K1Point;
import org.bouncycastle.math.raw.Nat192;
import org.bouncycastle.util.encoders.Hex;

public class SecP192K1Curve
extends ECCurve.AbstractFp {
    public static final BigInteger q = SecP192K1FieldElement.Q;
    private static final int SECP192K1_DEFAULT_COORDS = 2;
    private static final ECFieldElement[] SECP192K1_AFFINE_ZS = new ECFieldElement[]{new SecP192K1FieldElement(ECConstants.ONE)};
    protected SecP192K1Point infinity = new SecP192K1Point(this, null, null);

    public SecP192K1Curve() {
        super(q);
        this.a = this.fromBigInteger(ECConstants.ZERO);
        this.b = this.fromBigInteger(BigInteger.valueOf(3L));
        this.order = new BigInteger(1, Hex.decodeStrict("FFFFFFFFFFFFFFFFFFFFFFFE26F2FC170F69466A74DEFD8D"));
        this.cofactor = BigInteger.valueOf(1L);
        this.coord = 2;
    }

    @Override
    protected ECCurve cloneCurve() {
        return new SecP192K1Curve();
    }

    @Override
    public boolean supportsCoordinateSystem(int n) {
        switch (n) {
            case 2: {
                return true;
            }
        }
        return false;
    }

    @Override
    public BigInteger getQ() {
        return q;
    }

    @Override
    public int getFieldSize() {
        return q.bitLength();
    }

    @Override
    public ECFieldElement fromBigInteger(BigInteger bigInteger) {
        return new SecP192K1FieldElement(bigInteger);
    }

    @Override
    protected ECPoint createRawPoint(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
        return new SecP192K1Point(this, eCFieldElement, eCFieldElement2);
    }

    @Override
    protected ECPoint createRawPoint(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement[] eCFieldElementArray) {
        return new SecP192K1Point(this, eCFieldElement, eCFieldElement2, eCFieldElementArray);
    }

    @Override
    public ECPoint getInfinity() {
        return this.infinity;
    }

    @Override
    public ECLookupTable createCacheSafeLookupTable(ECPoint[] eCPointArray, int n, int n2) {
        int[] nArray = new int[n2 * 6 * 2];
        int n3 = 0;
        for (int i = 0; i < n2; ++i) {
            ECPoint eCPoint = eCPointArray[n + i];
            Nat192.copy(((SecP192K1FieldElement)eCPoint.getRawXCoord()).x, 0, nArray, n3);
            Nat192.copy(((SecP192K1FieldElement)eCPoint.getRawYCoord()).x, 0, nArray, n3 += 6);
            n3 += 6;
        }
        return new AbstractECLookupTable(this){
            final /* synthetic */ SecP192K1Curve this$0;
            {
                this.this$0 = secP192K1Curve;
            }

            @Override
            public int getSize() {
                return n2;
            }

            @Override
            public ECPoint lookup(int n) {
                int[] nArray3 = Nat192.create();
                int[] nArray2 = Nat192.create();
                int n22 = 0;
                for (int i = 0; i < n2; ++i) {
                    int n3 = (i ^ n) - 1 >> 31;
                    for (int j = 0; j < 6; ++j) {
                        int n4 = j;
                        nArray3[n4] = nArray3[n4] ^ nArray[n22 + j] & n3;
                        int n5 = j;
                        nArray2[n5] = nArray2[n5] ^ nArray[n22 + 6 + j] & n3;
                    }
                    n22 += 12;
                }
                return this.createPoint(nArray3, nArray2);
            }

            @Override
            public ECPoint lookupVar(int n) {
                int[] nArray3 = Nat192.create();
                int[] nArray2 = Nat192.create();
                int n22 = n * 6 * 2;
                for (int i = 0; i < 6; ++i) {
                    nArray3[i] = nArray[n22 + i];
                    nArray2[i] = nArray[n22 + 6 + i];
                }
                return this.createPoint(nArray3, nArray2);
            }

            private ECPoint createPoint(int[] nArray3, int[] nArray2) {
                return this.this$0.createRawPoint(new SecP192K1FieldElement(nArray3), new SecP192K1FieldElement(nArray2), SECP192K1_AFFINE_ZS);
            }
        };
    }

    @Override
    public ECFieldElement randomFieldElement(SecureRandom secureRandom) {
        int[] nArray = Nat192.create();
        SecP192K1Field.random(secureRandom, nArray);
        return new SecP192K1FieldElement(nArray);
    }

    @Override
    public ECFieldElement randomFieldElementMult(SecureRandom secureRandom) {
        int[] nArray = Nat192.create();
        SecP192K1Field.randomMult(secureRandom, nArray);
        return new SecP192K1FieldElement(nArray);
    }
}

