/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.its;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.ITSCertificateBuilder;
import org.bouncycastle.its.ITSPublicEncryptionKey;
import org.bouncycastle.its.ITSPublicVerificationKey;
import org.bouncycastle.its.operator.ECDSAEncoder;
import org.bouncycastle.its.operator.ITSContentSigner;
import org.bouncycastle.oer.OEREncoder;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateBase;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateId;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateType;
import org.bouncycastle.oer.its.ieee1609dot2.IssuerIdentifier;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.VerificationKeyIndicator;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashAlgorithm;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature;
import org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2;
import org.bouncycastle.util.Arrays;

public class ITSExplicitCertificateBuilder
extends ITSCertificateBuilder {
    private final ITSContentSigner signer;

    public ITSExplicitCertificateBuilder(ITSContentSigner iTSContentSigner, ToBeSignedCertificate.Builder builder) {
        super(builder);
        this.signer = iTSContentSigner;
    }

    public ITSCertificate build(CertificateId certificateId, ITSPublicVerificationKey iTSPublicVerificationKey) {
        return this.build(certificateId, iTSPublicVerificationKey, null);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public ITSCertificate build(CertificateId certificateId, ITSPublicVerificationKey iTSPublicVerificationKey, ITSPublicEncryptionKey iTSPublicEncryptionKey) {
        IssuerIdentifier issuerIdentifier;
        VerificationKeyIndicator verificationKeyIndicator;
        ToBeSignedCertificate.Builder builder = new ToBeSignedCertificate.Builder(this.tbsCertificateBuilder);
        builder.setId(certificateId);
        if (iTSPublicEncryptionKey != null) {
            builder.setEncryptionKey(iTSPublicEncryptionKey.toASN1Structure());
        }
        builder.setVerifyKeyIndicator(VerificationKeyIndicator.verificationKey(iTSPublicVerificationKey.toASN1Structure()));
        ToBeSignedCertificate toBeSignedCertificate = builder.createToBeSignedCertificate();
        ToBeSignedCertificate toBeSignedCertificate2 = null;
        if (this.signer.isForSelfSigning()) {
            verificationKeyIndicator = toBeSignedCertificate.getVerifyKeyIndicator();
        } else {
            toBeSignedCertificate2 = this.signer.getAssociatedCertificate().toASN1Structure().getToBeSigned();
            verificationKeyIndicator = toBeSignedCertificate2.getVerifyKeyIndicator();
        }
        OutputStream outputStream = this.signer.getOutputStream();
        try {
            outputStream.write(OEREncoder.toByteArray(toBeSignedCertificate, IEEE1609dot2.ToBeSignedCertificate.build()));
            outputStream.close();
        }
        catch (IOException iOException) {
            throw new IllegalArgumentException("cannot produce certificate signature");
        }
        Signature signature = null;
        switch (verificationKeyIndicator.getChoice()) {
            case 0: {
                signature = ECDSAEncoder.toITS(SECObjectIdentifiers.secp256r1, this.signer.getSignature());
                break;
            }
            case 1: {
                signature = ECDSAEncoder.toITS(TeleTrusTObjectIdentifiers.brainpoolP256r1, this.signer.getSignature());
                break;
            }
            case 2: {
                signature = ECDSAEncoder.toITS(TeleTrusTObjectIdentifiers.brainpoolP384r1, this.signer.getSignature());
                break;
            }
            default: {
                throw new IllegalStateException("unknown key type");
            }
        }
        CertificateBase.Builder builder2 = new CertificateBase.Builder();
        ASN1ObjectIdentifier aSN1ObjectIdentifier = this.signer.getDigestAlgorithm().getAlgorithm();
        if (this.signer.isForSelfSigning()) {
            if (aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_sha256)) {
                issuerIdentifier = IssuerIdentifier.self(HashAlgorithm.sha256);
            } else {
                if (!aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_sha384)) throw new IllegalStateException("unknown digest");
                issuerIdentifier = IssuerIdentifier.self(HashAlgorithm.sha384);
            }
        } else {
            byte[] byArray = this.signer.getAssociatedCertificateDigest();
            HashedId8 hashedId8 = new HashedId8(Arrays.copyOfRange(byArray, byArray.length - 8, byArray.length));
            if (aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_sha256)) {
                issuerIdentifier = IssuerIdentifier.sha256AndDigest(hashedId8);
            } else {
                if (!aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_sha384)) throw new IllegalStateException("unknown digest");
                issuerIdentifier = IssuerIdentifier.sha384AndDigest(hashedId8);
            }
        }
        builder2.setVersion(this.version);
        builder2.setType(CertificateType.explicit);
        builder2.setIssuer(issuerIdentifier);
        builder2.setToBeSigned(toBeSignedCertificate);
        builder2.setSignature(signature);
        return new ITSCertificate(builder2.createCertificateBase());
    }
}

