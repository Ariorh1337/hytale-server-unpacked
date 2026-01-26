/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.sec;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.BigIntegers;

public class ECPrivateKey
extends ASN1Object {
    private ASN1Sequence seq;

    private ECPrivateKey(ASN1Sequence aSN1Sequence) {
        this.seq = aSN1Sequence;
    }

    public static ECPrivateKey getInstance(Object object) {
        if (object instanceof ECPrivateKey) {
            return (ECPrivateKey)object;
        }
        if (object != null) {
            return new ECPrivateKey(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public ECPrivateKey(BigInteger bigInteger) {
        this(bigInteger.bitLength(), bigInteger);
    }

    public ECPrivateKey(int n, BigInteger bigInteger) {
        byte[] byArray = BigIntegers.asUnsignedByteArray((n + 7) / 8, bigInteger);
        this.seq = new DERSequence(new ASN1Integer(1L), new DEROctetString(byArray));
    }

    public ECPrivateKey(BigInteger bigInteger, ASN1Encodable aSN1Encodable) {
        this(bigInteger, null, aSN1Encodable);
    }

    public ECPrivateKey(BigInteger bigInteger, ASN1BitString aSN1BitString, ASN1Encodable aSN1Encodable) {
        this(bigInteger.bitLength(), bigInteger, aSN1BitString, aSN1Encodable);
    }

    public ECPrivateKey(int n, BigInteger bigInteger, ASN1Encodable aSN1Encodable) {
        this(n, bigInteger, null, aSN1Encodable);
    }

    public ECPrivateKey(int n, BigInteger bigInteger, ASN1BitString aSN1BitString, ASN1Encodable aSN1Encodable) {
        byte[] byArray = BigIntegers.asUnsignedByteArray((n + 7) / 8, bigInteger);
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(4);
        aSN1EncodableVector.add(new ASN1Integer(1L));
        aSN1EncodableVector.add(new DEROctetString(byArray));
        if (aSN1Encodable != null) {
            aSN1EncodableVector.add(new DERTaggedObject(true, 0, aSN1Encodable));
        }
        if (aSN1BitString != null) {
            aSN1EncodableVector.add(new DERTaggedObject(true, 1, (ASN1Encodable)aSN1BitString));
        }
        this.seq = new DERSequence(aSN1EncodableVector);
    }

    public BigInteger getKey() {
        ASN1OctetString aSN1OctetString = (ASN1OctetString)this.seq.getObjectAt(1);
        return new BigInteger(1, aSN1OctetString.getOctets());
    }

    public ASN1BitString getPublicKey() {
        return (ASN1BitString)this.getObjectInTag(1, 3);
    }

    public ASN1Primitive getParameters() {
        return this.getParametersObject().toASN1Primitive();
    }

    public ASN1Object getParametersObject() {
        return this.getObjectInTag(0, -1);
    }

    private ASN1Object getObjectInTag(int n, int n2) {
        Enumeration enumeration = this.seq.getObjects();
        while (enumeration.hasMoreElements()) {
            ASN1TaggedObject aSN1TaggedObject;
            ASN1Encodable aSN1Encodable = (ASN1Encodable)enumeration.nextElement();
            if (!(aSN1Encodable instanceof ASN1TaggedObject) || !(aSN1TaggedObject = (ASN1TaggedObject)aSN1Encodable).hasContextTag(n)) continue;
            return n2 < 0 ? aSN1TaggedObject.getExplicitBaseObject().toASN1Primitive() : aSN1TaggedObject.getBaseUniversal(true, n2);
        }
        return null;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.seq;
    }
}

