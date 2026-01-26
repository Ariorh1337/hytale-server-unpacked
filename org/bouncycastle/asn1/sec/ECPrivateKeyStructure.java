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

public class ECPrivateKeyStructure
extends ASN1Object {
    private ASN1Sequence seq;

    public ECPrivateKeyStructure(ASN1Sequence aSN1Sequence) {
        this.seq = aSN1Sequence;
    }

    public ECPrivateKeyStructure(BigInteger bigInteger) {
        byte[] byArray = BigIntegers.asUnsignedByteArray(bigInteger);
        this.seq = new DERSequence(new ASN1Integer(1L), new DEROctetString(byArray));
    }

    public ECPrivateKeyStructure(BigInteger bigInteger, ASN1Encodable aSN1Encodable) {
        this(bigInteger, null, aSN1Encodable);
    }

    public ECPrivateKeyStructure(BigInteger bigInteger, ASN1BitString aSN1BitString, ASN1Encodable aSN1Encodable) {
        byte[] byArray = BigIntegers.asUnsignedByteArray(bigInteger);
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
        return (ASN1BitString)this.getObjectInTag(1);
    }

    public ASN1Primitive getParameters() {
        return this.getObjectInTag(0);
    }

    private ASN1Primitive getObjectInTag(int n) {
        Enumeration enumeration = this.seq.getObjects();
        while (enumeration.hasMoreElements()) {
            ASN1TaggedObject aSN1TaggedObject;
            ASN1Encodable aSN1Encodable = (ASN1Encodable)enumeration.nextElement();
            if (!(aSN1Encodable instanceof ASN1TaggedObject) || (aSN1TaggedObject = (ASN1TaggedObject)aSN1Encodable).getTagNo() != n) continue;
            return aSN1TaggedObject.getExplicitBaseObject().toASN1Primitive();
        }
        return null;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.seq;
    }
}

