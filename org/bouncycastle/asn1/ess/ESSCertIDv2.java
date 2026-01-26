/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.ess;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ess.ESSCertID;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.IssuerSerial;
import org.bouncycastle.util.Arrays;

public class ESSCertIDv2
extends ASN1Object {
    private static final AlgorithmIdentifier DEFAULT_HASH_ALGORITHM = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
    private AlgorithmIdentifier hashAlgorithm;
    private ASN1OctetString certHash;
    private IssuerSerial issuerSerial;

    public static ESSCertIDv2 from(ESSCertID eSSCertID) {
        AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1);
        return new ESSCertIDv2(algorithmIdentifier, eSSCertID.getCertHashObject(), eSSCertID.getIssuerSerial());
    }

    public static ESSCertIDv2 getInstance(Object object) {
        if (object instanceof ESSCertIDv2) {
            return (ESSCertIDv2)object;
        }
        if (object != null) {
            return new ESSCertIDv2(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    private ESSCertIDv2(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() > 3) {
            throw new IllegalArgumentException("Bad sequence size: " + aSN1Sequence.size());
        }
        int n = 0;
        this.hashAlgorithm = aSN1Sequence.getObjectAt(0) instanceof ASN1OctetString ? DEFAULT_HASH_ALGORITHM : AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(n++));
        this.certHash = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(n++));
        if (aSN1Sequence.size() > n) {
            this.issuerSerial = IssuerSerial.getInstance(aSN1Sequence.getObjectAt(n));
        }
    }

    public ESSCertIDv2(byte[] byArray) {
        this(null, byArray, null);
    }

    public ESSCertIDv2(AlgorithmIdentifier algorithmIdentifier, byte[] byArray) {
        this(algorithmIdentifier, byArray, null);
    }

    public ESSCertIDv2(byte[] byArray, IssuerSerial issuerSerial) {
        this(null, byArray, issuerSerial);
    }

    public ESSCertIDv2(AlgorithmIdentifier algorithmIdentifier, byte[] byArray, IssuerSerial issuerSerial) {
        if (algorithmIdentifier == null) {
            algorithmIdentifier = DEFAULT_HASH_ALGORITHM;
        }
        this.hashAlgorithm = algorithmIdentifier;
        this.certHash = new DEROctetString(Arrays.clone(byArray));
        this.issuerSerial = issuerSerial;
    }

    public ESSCertIDv2(AlgorithmIdentifier algorithmIdentifier, ASN1OctetString aSN1OctetString, IssuerSerial issuerSerial) {
        if (algorithmIdentifier == null) {
            algorithmIdentifier = DEFAULT_HASH_ALGORITHM;
        }
        if (aSN1OctetString == null) {
            throw new NullPointerException("'certHash' cannot be null");
        }
        this.hashAlgorithm = algorithmIdentifier;
        this.certHash = aSN1OctetString;
        this.issuerSerial = issuerSerial;
    }

    public AlgorithmIdentifier getHashAlgorithm() {
        return this.hashAlgorithm;
    }

    public ASN1OctetString getCertHashObject() {
        return this.certHash;
    }

    public byte[] getCertHash() {
        return Arrays.clone(this.certHash.getOctets());
    }

    public IssuerSerial getIssuerSerial() {
        return this.issuerSerial;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(3);
        if (!DEFAULT_HASH_ALGORITHM.equals(this.hashAlgorithm)) {
            aSN1EncodableVector.add(this.hashAlgorithm);
        }
        aSN1EncodableVector.add(this.certHash);
        if (this.issuerSerial != null) {
            aSN1EncodableVector.add(this.issuerSerial);
        }
        return new DERSequence(aSN1EncodableVector);
    }
}

