/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccCurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Point256;
import org.bouncycastle.util.Arrays;

public class EccP256CurvePoint
extends EccCurvePoint
implements ASN1Choice {
    public static final int xonly = 0;
    public static final int fill = 1;
    public static final int compressedY0 = 2;
    public static final int compressedY1 = 3;
    public static final int uncompressedP256 = 4;
    private final int choice;
    private final ASN1Encodable eccp256CurvePoint;

    public EccP256CurvePoint(int n, ASN1Encodable aSN1Encodable) {
        this.choice = n;
        this.eccp256CurvePoint = aSN1Encodable;
    }

    private EccP256CurvePoint(ASN1TaggedObject aSN1TaggedObject) {
        this.choice = aSN1TaggedObject.getTagNo();
        switch (aSN1TaggedObject.getTagNo()) {
            case 1: {
                this.eccp256CurvePoint = ASN1Null.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                break;
            }
            case 0: 
            case 2: 
            case 3: {
                this.eccp256CurvePoint = ASN1OctetString.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                break;
            }
            case 4: {
                this.eccp256CurvePoint = Point256.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + aSN1TaggedObject.getTagNo());
            }
        }
    }

    public static EccP256CurvePoint xOnly(ASN1OctetString aSN1OctetString) {
        return new EccP256CurvePoint(0, aSN1OctetString);
    }

    public static EccP256CurvePoint xOnly(byte[] byArray) {
        return new EccP256CurvePoint(0, new DEROctetString(Arrays.clone(byArray)));
    }

    public static EccP256CurvePoint fill() {
        return new EccP256CurvePoint(1, DERNull.INSTANCE);
    }

    public static EccP256CurvePoint compressedY0(ASN1OctetString aSN1OctetString) {
        return new EccP256CurvePoint(2, aSN1OctetString);
    }

    public static EccP256CurvePoint compressedY1(ASN1OctetString aSN1OctetString) {
        return new EccP256CurvePoint(3, aSN1OctetString);
    }

    public static EccP256CurvePoint compressedY0(byte[] byArray) {
        return new EccP256CurvePoint(2, new DEROctetString(Arrays.clone(byArray)));
    }

    public static EccP256CurvePoint compressedY1(byte[] byArray) {
        return new EccP256CurvePoint(3, new DEROctetString(Arrays.clone(byArray)));
    }

    public static EccP256CurvePoint uncompressedP256(Point256 point256) {
        return new EccP256CurvePoint(4, point256);
    }

    public static EccP256CurvePoint uncompressedP256(BigInteger bigInteger, BigInteger bigInteger2) {
        return new EccP256CurvePoint(4, Point256.builder().setX(bigInteger).setY(bigInteger2).createPoint256());
    }

    public static EccP256CurvePoint createEncodedPoint(byte[] byArray) {
        if (byArray[0] == 2) {
            byte[] byArray2 = new byte[byArray.length - 1];
            System.arraycopy(byArray, 1, byArray2, 0, byArray2.length);
            return new EccP256CurvePoint(2, new DEROctetString(byArray2));
        }
        if (byArray[0] == 3) {
            byte[] byArray3 = new byte[byArray.length - 1];
            System.arraycopy(byArray, 1, byArray3, 0, byArray3.length);
            return new EccP256CurvePoint(3, new DEROctetString(byArray3));
        }
        if (byArray[0] == 4) {
            return new EccP256CurvePoint(4, new Point256(new DEROctetString(Arrays.copyOfRange(byArray, 1, 34)), new DEROctetString(Arrays.copyOfRange(byArray, 34, 66))));
        }
        throw new IllegalArgumentException("unrecognised encoding " + byArray[0]);
    }

    public EccP256CurvePoint createCompressed(ECPoint eCPoint) {
        int n = 0;
        byte[] byArray = eCPoint.getEncoded(true);
        if (byArray[0] == 2) {
            n = 2;
        } else if (byArray[0] == 3) {
            n = 3;
        }
        byte[] byArray2 = new byte[byArray.length - 1];
        System.arraycopy(byArray, 1, byArray2, 0, byArray2.length);
        return new EccP256CurvePoint(n, new DEROctetString(byArray2));
    }

    public static EccP256CurvePoint getInstance(Object object) {
        if (object instanceof EccP256CurvePoint) {
            return (EccP256CurvePoint)object;
        }
        if (object != null) {
            return new EccP256CurvePoint(ASN1TaggedObject.getInstance(object, 128));
        }
        return null;
    }

    public ASN1Encodable getEccp256CurvePoint() {
        return this.eccp256CurvePoint;
    }

    public int getChoice() {
        return this.choice;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.eccp256CurvePoint);
    }

    @Override
    public byte[] getEncodedPoint() {
        byte[] byArray;
        switch (this.choice) {
            case 2: {
                byte[] byArray2 = DEROctetString.getInstance(this.eccp256CurvePoint).getOctets();
                byArray = new byte[byArray2.length + 1];
                byArray[0] = 2;
                System.arraycopy(byArray2, 0, byArray, 1, byArray2.length);
                break;
            }
            case 3: {
                byte[] byArray3 = DEROctetString.getInstance(this.eccp256CurvePoint).getOctets();
                byArray = new byte[byArray3.length + 1];
                byArray[0] = 3;
                System.arraycopy(byArray3, 0, byArray, 1, byArray3.length);
                break;
            }
            case 4: {
                Point256 point256 = Point256.getInstance(this.eccp256CurvePoint);
                byte[] byArray4 = point256.getX().getOctets();
                byte[] byArray5 = point256.getY().getOctets();
                byArray = Arrays.concatenate(new byte[]{4}, byArray4, byArray5);
                break;
            }
            case 0: {
                throw new IllegalStateException("x Only not implemented");
            }
            default: {
                throw new IllegalStateException("unknown point choice");
            }
        }
        return byArray;
    }
}

