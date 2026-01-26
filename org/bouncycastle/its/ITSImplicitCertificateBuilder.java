/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.its;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.ITSCertificateBuilder;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateBase;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateId;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateType;
import org.bouncycastle.oer.its.ieee1609dot2.IssuerIdentifier;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.VerificationKeyIndicator;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicEncryptionKey;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Arrays;

public class ITSImplicitCertificateBuilder
extends ITSCertificateBuilder {
    private final IssuerIdentifier issuerIdentifier;

    public ITSImplicitCertificateBuilder(ITSCertificate iTSCertificate, DigestCalculatorProvider digestCalculatorProvider, ToBeSignedCertificate.Builder builder) {
        super(iTSCertificate, builder);
        Object object;
        DigestCalculator digestCalculator;
        AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
        ASN1ObjectIdentifier aSN1ObjectIdentifier = algorithmIdentifier.getAlgorithm();
        try {
            digestCalculator = digestCalculatorProvider.get(algorithmIdentifier);
        }
        catch (OperatorCreationException operatorCreationException) {
            throw new IllegalStateException(operatorCreationException.getMessage(), operatorCreationException);
        }
        try {
            object = digestCalculator.getOutputStream();
            ((OutputStream)object).write(iTSCertificate.getEncoded());
            ((OutputStream)object).close();
        }
        catch (IOException iOException) {
            throw new IllegalStateException(iOException.getMessage(), iOException);
        }
        object = digestCalculator.getDigest();
        HashedId8 hashedId8 = new HashedId8(Arrays.copyOfRange((byte[])object, ((Object)object).length - 8, ((Object)object).length));
        if (aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_sha256)) {
            this.issuerIdentifier = IssuerIdentifier.sha256AndDigest(hashedId8);
        } else if (aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_sha384)) {
            this.issuerIdentifier = IssuerIdentifier.sha384AndDigest(hashedId8);
        } else {
            throw new IllegalStateException("unknown digest");
        }
    }

    public ITSCertificate build(CertificateId certificateId, BigInteger bigInteger, BigInteger bigInteger2) {
        return this.build(certificateId, bigInteger, bigInteger2, null);
    }

    public ITSCertificate build(CertificateId certificateId, BigInteger bigInteger, BigInteger bigInteger2, PublicEncryptionKey publicEncryptionKey) {
        EccP256CurvePoint eccP256CurvePoint = EccP256CurvePoint.uncompressedP256(bigInteger, bigInteger2);
        ToBeSignedCertificate.Builder builder = new ToBeSignedCertificate.Builder(this.tbsCertificateBuilder);
        builder.setId(certificateId);
        if (publicEncryptionKey != null) {
            builder.setEncryptionKey(publicEncryptionKey);
        }
        builder.setVerifyKeyIndicator(VerificationKeyIndicator.reconstructionValue(eccP256CurvePoint));
        CertificateBase.Builder builder2 = new CertificateBase.Builder();
        builder2.setVersion(this.version);
        builder2.setType(CertificateType.implicit);
        builder2.setIssuer(this.issuerIdentifier);
        builder2.setToBeSigned(builder.createToBeSignedCertificate());
        return new ITSCertificate(builder2.createCertificateBase());
    }
}

