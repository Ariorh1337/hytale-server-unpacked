/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class CMSORIforKEMOtherInfo
extends ASN1Object {
    private final AlgorithmIdentifier wrap;
    private final int kekLength;
    private final byte[] ukm;

    public CMSORIforKEMOtherInfo(AlgorithmIdentifier algorithmIdentifier, int n) {
        this(algorithmIdentifier, n, null);
    }

    public CMSORIforKEMOtherInfo(AlgorithmIdentifier algorithmIdentifier, int n, byte[] byArray) {
        if (n > 65535) {
            throw new IllegalArgumentException("kekLength must be <= 65535");
        }
        this.wrap = algorithmIdentifier;
        this.kekLength = n;
        this.ukm = byArray;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(this.wrap);
        aSN1EncodableVector.add(new ASN1Integer(this.kekLength));
        if (this.ukm != null) {
            aSN1EncodableVector.add(new DERTaggedObject(true, 0, (ASN1Encodable)new DEROctetString(this.ukm)));
        }
        return new DERSequence(aSN1EncodableVector);
    }
}

