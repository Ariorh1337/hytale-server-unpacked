/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.IssuerAndSerialNumber;
import org.bouncycastle.asn1.x509.Certificate;

public class PrivateKeyPossessionStatement
extends ASN1Object {
    private final IssuerAndSerialNumber signer;
    private final Certificate cert;

    public static PrivateKeyPossessionStatement getInstance(Object object) {
        if (object instanceof PrivateKeyPossessionStatement) {
            return (PrivateKeyPossessionStatement)object;
        }
        if (object != null) {
            return new PrivateKeyPossessionStatement(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    private PrivateKeyPossessionStatement(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() == 1) {
            this.signer = IssuerAndSerialNumber.getInstance(aSN1Sequence.getObjectAt(0));
            this.cert = null;
        } else if (aSN1Sequence.size() == 2) {
            this.signer = IssuerAndSerialNumber.getInstance(aSN1Sequence.getObjectAt(0));
            this.cert = Certificate.getInstance(aSN1Sequence.getObjectAt(1));
        } else {
            throw new IllegalArgumentException("unknown sequence in PrivateKeyStatement");
        }
    }

    public PrivateKeyPossessionStatement(IssuerAndSerialNumber issuerAndSerialNumber) {
        this.signer = issuerAndSerialNumber;
        this.cert = null;
    }

    public PrivateKeyPossessionStatement(Certificate certificate) {
        this.signer = new IssuerAndSerialNumber(certificate.getIssuer(), certificate.getSerialNumber().getValue());
        this.cert = certificate;
    }

    public IssuerAndSerialNumber getSigner() {
        return this.signer;
    }

    public Certificate getCert() {
        return this.cert;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(2);
        aSN1EncodableVector.add(this.signer);
        if (this.cert != null) {
            aSN1EncodableVector.add(this.cert);
        }
        return new DERSequence(aSN1EncodableVector);
    }
}

