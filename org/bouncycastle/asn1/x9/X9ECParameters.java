/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x9;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x9.X9Curve;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.asn1.x9.X9FieldID;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.field.FiniteField;
import org.bouncycastle.math.field.PolynomialExtensionField;

public class X9ECParameters
extends ASN1Object
implements X9ObjectIdentifiers {
    private static final BigInteger ONE = BigInteger.valueOf(1L);
    private X9FieldID fieldID;
    private X9Curve curve;
    private X9ECPoint g;
    private BigInteger n;
    private BigInteger h;

    private X9ECParameters(ASN1Sequence aSN1Sequence) {
        if (!(aSN1Sequence.getObjectAt(0) instanceof ASN1Integer) || !((ASN1Integer)aSN1Sequence.getObjectAt(0)).hasValue(1)) {
            throw new IllegalArgumentException("bad version in X9ECParameters");
        }
        this.n = ((ASN1Integer)aSN1Sequence.getObjectAt(4)).getValue();
        if (aSN1Sequence.size() == 6) {
            this.h = ((ASN1Integer)aSN1Sequence.getObjectAt(5)).getValue();
        }
        this.fieldID = X9FieldID.getInstance(aSN1Sequence.getObjectAt(1));
        this.curve = new X9Curve(this.fieldID, this.n, this.h, ASN1Sequence.getInstance(aSN1Sequence.getObjectAt(2)));
        ASN1Encodable aSN1Encodable = aSN1Sequence.getObjectAt(3);
        this.g = aSN1Encodable instanceof X9ECPoint ? (X9ECPoint)aSN1Encodable : new X9ECPoint(this.curve.getCurve(), (ASN1OctetString)aSN1Encodable);
    }

    public static X9ECParameters getInstance(Object object) {
        if (object instanceof X9ECParameters) {
            return (X9ECParameters)object;
        }
        if (object != null) {
            return new X9ECParameters(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public X9ECParameters(ECCurve eCCurve, X9ECPoint x9ECPoint, BigInteger bigInteger) {
        this(eCCurve, x9ECPoint, bigInteger, null, null);
    }

    public X9ECParameters(ECCurve eCCurve, X9ECPoint x9ECPoint, BigInteger bigInteger, BigInteger bigInteger2) {
        this(eCCurve, x9ECPoint, bigInteger, bigInteger2, null);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public X9ECParameters(ECCurve eCCurve, X9ECPoint x9ECPoint, BigInteger bigInteger, BigInteger bigInteger2, byte[] byArray) {
        this.curve = new X9Curve(eCCurve, byArray);
        this.g = x9ECPoint;
        this.n = bigInteger;
        this.h = bigInteger2;
        FiniteField finiteField = eCCurve.getField();
        if (ECAlgorithms.isFpField(finiteField)) {
            this.fieldID = new X9FieldID(finiteField.getCharacteristic());
            return;
        } else {
            if (!ECAlgorithms.isF2mField(finiteField)) throw new IllegalArgumentException("'curve' is of an unsupported type");
            PolynomialExtensionField polynomialExtensionField = (PolynomialExtensionField)finiteField;
            int[] nArray = polynomialExtensionField.getMinimalPolynomial().getExponentsPresent();
            if (nArray.length == 3) {
                this.fieldID = new X9FieldID(nArray[2], nArray[1]);
                return;
            } else {
                if (nArray.length != 5) throw new IllegalArgumentException("Only trinomial and pentomial curves are supported");
                this.fieldID = new X9FieldID(nArray[4], nArray[1], nArray[2], nArray[3]);
            }
        }
    }

    public ECCurve getCurve() {
        return this.curve.getCurve();
    }

    public ECPoint getG() {
        return this.g.getPoint();
    }

    public BigInteger getN() {
        return this.n;
    }

    public BigInteger getH() {
        return this.h;
    }

    public byte[] getSeed() {
        return this.curve.getSeed();
    }

    public boolean hasSeed() {
        return this.curve.hasSeed();
    }

    public X9Curve getCurveEntry() {
        return this.curve;
    }

    public X9FieldID getFieldIDEntry() {
        return this.fieldID;
    }

    public X9ECPoint getBaseEntry() {
        return this.g;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(6);
        aSN1EncodableVector.add(new ASN1Integer(ONE));
        aSN1EncodableVector.add(this.fieldID);
        aSN1EncodableVector.add(this.curve);
        aSN1EncodableVector.add(this.g);
        aSN1EncodableVector.add(new ASN1Integer(this.n));
        if (this.h != null) {
            aSN1EncodableVector.add(new ASN1Integer(this.h));
        }
        return new DERSequence(aSN1EncodableVector);
    }
}

