/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class KemCiphertextInfo
extends ASN1Object {
    private final AlgorithmIdentifier kem;
    private final ASN1OctetString ct;

    private KemCiphertextInfo(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("sequence size should 2");
        }
        this.kem = AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(0));
        this.ct = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public KemCiphertextInfo(AlgorithmIdentifier algorithmIdentifier, ASN1OctetString aSN1OctetString) {
        this.kem = algorithmIdentifier;
        this.ct = aSN1OctetString;
    }

    public static KemCiphertextInfo getInstance(Object object) {
        if (object instanceof KemCiphertextInfo) {
            return (KemCiphertextInfo)object;
        }
        if (object != null) {
            return new KemCiphertextInfo(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public AlgorithmIdentifier getKem() {
        return this.kem;
    }

    public ASN1OctetString getCt() {
        return this.ct;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.kem, this.ct);
    }
}

