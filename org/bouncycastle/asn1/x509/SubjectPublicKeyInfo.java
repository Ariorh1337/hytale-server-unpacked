/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import java.io.IOException;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class SubjectPublicKeyInfo
extends ASN1Object {
    private AlgorithmIdentifier algId;
    private ASN1BitString keyData;

    public static SubjectPublicKeyInfo getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return SubjectPublicKeyInfo.getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, bl));
    }

    public static SubjectPublicKeyInfo getInstance(Object object) {
        if (object instanceof SubjectPublicKeyInfo) {
            return (SubjectPublicKeyInfo)object;
        }
        if (object != null) {
            return new SubjectPublicKeyInfo(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public SubjectPublicKeyInfo(AlgorithmIdentifier algorithmIdentifier, ASN1BitString aSN1BitString) {
        this.keyData = aSN1BitString;
        this.algId = algorithmIdentifier;
    }

    public SubjectPublicKeyInfo(AlgorithmIdentifier algorithmIdentifier, ASN1Encodable aSN1Encodable) throws IOException {
        this.keyData = new DERBitString(aSN1Encodable);
        this.algId = algorithmIdentifier;
    }

    public SubjectPublicKeyInfo(AlgorithmIdentifier algorithmIdentifier, byte[] byArray) {
        this.keyData = new DERBitString(byArray);
        this.algId = algorithmIdentifier;
    }

    @Deprecated
    public SubjectPublicKeyInfo(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("Bad sequence size: " + aSN1Sequence.size());
        }
        Enumeration enumeration = aSN1Sequence.getObjects();
        this.algId = AlgorithmIdentifier.getInstance(enumeration.nextElement());
        this.keyData = ASN1BitString.getInstance(enumeration.nextElement());
    }

    public AlgorithmIdentifier getAlgorithm() {
        return this.algId;
    }

    @Deprecated
    public AlgorithmIdentifier getAlgorithmId() {
        return this.algId;
    }

    public ASN1Primitive parsePublicKey() throws IOException {
        return ASN1Primitive.fromByteArray(this.keyData.getOctets());
    }

    @Deprecated
    public ASN1Primitive getPublicKey() throws IOException {
        return ASN1Primitive.fromByteArray(this.keyData.getOctets());
    }

    public ASN1BitString getPublicKeyData() {
        return this.keyData;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.algId, this.keyData);
    }
}

