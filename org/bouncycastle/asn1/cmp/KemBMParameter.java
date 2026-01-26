/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class KemBMParameter
extends ASN1Object {
    private final AlgorithmIdentifier kdf;
    private final ASN1Integer len;
    private final AlgorithmIdentifier mac;

    private KemBMParameter(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 3) {
            throw new IllegalArgumentException("sequence size should 3");
        }
        this.kdf = AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(0));
        this.len = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(1));
        this.mac = AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(2));
    }

    public KemBMParameter(AlgorithmIdentifier algorithmIdentifier, ASN1Integer aSN1Integer, AlgorithmIdentifier algorithmIdentifier2) {
        this.kdf = algorithmIdentifier;
        this.len = aSN1Integer;
        this.mac = algorithmIdentifier2;
    }

    public KemBMParameter(AlgorithmIdentifier algorithmIdentifier, long l, AlgorithmIdentifier algorithmIdentifier2) {
        this(algorithmIdentifier, new ASN1Integer(l), algorithmIdentifier2);
    }

    public static KemBMParameter getInstance(Object object) {
        if (object instanceof KemBMParameter) {
            return (KemBMParameter)object;
        }
        if (object != null) {
            return new KemBMParameter(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public AlgorithmIdentifier getKdf() {
        return this.kdf;
    }

    public ASN1Integer getLen() {
        return this.len;
    }

    public AlgorithmIdentifier getMac() {
        return this.mac;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(3);
        aSN1EncodableVector.add(this.kdf);
        aSN1EncodableVector.add(this.len);
        aSN1EncodableVector.add(this.mac);
        return new DERSequence(aSN1EncodableVector);
    }
}

