/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class GenericHybridParameters
extends ASN1Object {
    private final AlgorithmIdentifier kem;
    private final AlgorithmIdentifier dem;

    private GenericHybridParameters(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("ASN.1 SEQUENCE should be of length 2");
        }
        this.kem = AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(0));
        this.dem = AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public static GenericHybridParameters getInstance(Object object) {
        if (object instanceof GenericHybridParameters) {
            return (GenericHybridParameters)object;
        }
        if (object != null) {
            return new GenericHybridParameters(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public GenericHybridParameters(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2) {
        this.kem = algorithmIdentifier;
        this.dem = algorithmIdentifier2;
    }

    public AlgorithmIdentifier getDem() {
        return this.dem;
    }

    public AlgorithmIdentifier getKem() {
        return this.kem;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.kem, this.dem);
    }
}

