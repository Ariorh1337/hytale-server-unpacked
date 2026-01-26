/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.crmf.CertId;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class OOBCertHash
extends ASN1Object {
    private final AlgorithmIdentifier hashAlg;
    private final CertId certId;
    private final ASN1BitString hashVal;

    private OOBCertHash(ASN1Sequence aSN1Sequence) {
        int n = aSN1Sequence.size() - 1;
        this.hashVal = ASN1BitString.getInstance(aSN1Sequence.getObjectAt(n--));
        AlgorithmIdentifier algorithmIdentifier = null;
        CertId certId = null;
        for (int i = n; i >= 0; --i) {
            ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)aSN1Sequence.getObjectAt(i);
            if (aSN1TaggedObject.hasContextTag(0)) {
                algorithmIdentifier = AlgorithmIdentifier.getInstance(aSN1TaggedObject, true);
                continue;
            }
            if (aSN1TaggedObject.hasContextTag(1)) {
                certId = CertId.getInstance(aSN1TaggedObject, true);
                continue;
            }
            throw new IllegalArgumentException("unknown tag " + ASN1Util.getTagText(aSN1TaggedObject));
        }
        this.hashAlg = algorithmIdentifier;
        this.certId = certId;
    }

    public OOBCertHash(AlgorithmIdentifier algorithmIdentifier, CertId certId, byte[] byArray) {
        this(algorithmIdentifier, certId, new DERBitString(byArray));
    }

    public OOBCertHash(AlgorithmIdentifier algorithmIdentifier, CertId certId, DERBitString dERBitString) {
        this.hashAlg = algorithmIdentifier;
        this.certId = certId;
        this.hashVal = dERBitString;
    }

    public static OOBCertHash getInstance(Object object) {
        if (object instanceof OOBCertHash) {
            return (OOBCertHash)object;
        }
        if (object != null) {
            return new OOBCertHash(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public AlgorithmIdentifier getHashAlg() {
        return this.hashAlg;
    }

    public CertId getCertId() {
        return this.certId;
    }

    public ASN1BitString getHashVal() {
        return this.hashVal;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(3);
        this.addOptional(aSN1EncodableVector, 0, this.hashAlg);
        this.addOptional(aSN1EncodableVector, 1, this.certId);
        aSN1EncodableVector.add(this.hashVal);
        return new DERSequence(aSN1EncodableVector);
    }

    private void addOptional(ASN1EncodableVector aSN1EncodableVector, int n, ASN1Encodable aSN1Encodable) {
        if (aSN1Encodable != null) {
            aSN1EncodableVector.add(new DERTaggedObject(true, n, aSN1Encodable));
        }
    }
}

