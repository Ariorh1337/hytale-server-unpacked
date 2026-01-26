/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.cmp;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class CertStatus
extends ASN1Object {
    private final ASN1OctetString certHash;
    private final ASN1Integer certReqId;
    private final PKIStatusInfo statusInfo;
    private final AlgorithmIdentifier hashAlg;

    private CertStatus(ASN1Sequence aSN1Sequence) {
        this.certHash = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(0));
        this.certReqId = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(1));
        PKIStatusInfo pKIStatusInfo = null;
        AlgorithmIdentifier algorithmIdentifier = null;
        if (aSN1Sequence.size() > 2) {
            for (int i = 2; i < aSN1Sequence.size(); ++i) {
                ASN1Primitive aSN1Primitive = aSN1Sequence.getObjectAt(i).toASN1Primitive();
                if (aSN1Primitive instanceof ASN1Sequence) {
                    pKIStatusInfo = PKIStatusInfo.getInstance(aSN1Primitive);
                }
                if (!(aSN1Primitive instanceof ASN1TaggedObject)) continue;
                ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)aSN1Primitive;
                if (!aSN1TaggedObject.hasContextTag(0)) {
                    throw new IllegalArgumentException("unknown tag " + ASN1Util.getTagText(aSN1TaggedObject));
                }
                algorithmIdentifier = AlgorithmIdentifier.getInstance(aSN1TaggedObject, true);
            }
        }
        this.statusInfo = pKIStatusInfo;
        this.hashAlg = algorithmIdentifier;
    }

    public CertStatus(byte[] byArray, BigInteger bigInteger) {
        this(byArray, new ASN1Integer(bigInteger));
    }

    public CertStatus(byte[] byArray, ASN1Integer aSN1Integer) {
        this.certHash = new DEROctetString(byArray);
        this.certReqId = aSN1Integer;
        this.statusInfo = null;
        this.hashAlg = null;
    }

    public CertStatus(byte[] byArray, BigInteger bigInteger, PKIStatusInfo pKIStatusInfo) {
        this.certHash = new DEROctetString(byArray);
        this.certReqId = new ASN1Integer(bigInteger);
        this.statusInfo = pKIStatusInfo;
        this.hashAlg = null;
    }

    public CertStatus(byte[] byArray, BigInteger bigInteger, PKIStatusInfo pKIStatusInfo, AlgorithmIdentifier algorithmIdentifier) {
        this.certHash = new DEROctetString(byArray);
        this.certReqId = new ASN1Integer(bigInteger);
        this.statusInfo = pKIStatusInfo;
        this.hashAlg = algorithmIdentifier;
    }

    public static CertStatus getInstance(Object object) {
        if (object instanceof CertStatus) {
            return (CertStatus)object;
        }
        if (object != null) {
            return new CertStatus(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public ASN1OctetString getCertHash() {
        return this.certHash;
    }

    public ASN1Integer getCertReqId() {
        return this.certReqId;
    }

    public PKIStatusInfo getStatusInfo() {
        return this.statusInfo;
    }

    public AlgorithmIdentifier getHashAlg() {
        return this.hashAlg;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(4);
        aSN1EncodableVector.add(this.certHash);
        aSN1EncodableVector.add(this.certReqId);
        if (this.statusInfo != null) {
            aSN1EncodableVector.add(this.statusInfo);
        }
        if (this.hashAlg != null) {
            aSN1EncodableVector.add(new DERTaggedObject(true, 0, (ASN1Encodable)this.hashAlg));
        }
        return new DERSequence(aSN1EncodableVector);
    }
}

