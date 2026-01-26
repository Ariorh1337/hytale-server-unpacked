/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.SignerIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSAttributeTableGenerator;
import org.bouncycastle.cms.CMSSignatureEncryptionAlgorithmFinder;
import org.bouncycastle.cms.DefaultCMSSignatureEncryptionAlgorithmFinder;
import org.bouncycastle.cms.DefaultSignedAttributeTableGenerator;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.ExtendedContentSigner;
import org.bouncycastle.operator.OperatorCreationException;

public class SignerInfoGeneratorBuilder {
    private final DigestAlgorithmIdentifierFinder digAlgFinder = new DefaultDigestAlgorithmIdentifierFinder();
    private DigestCalculatorProvider digestProvider;
    private boolean directSignature;
    private CMSAttributeTableGenerator signedGen;
    private CMSAttributeTableGenerator unsignedGen;
    private CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder;
    private AlgorithmIdentifier contentDigest;

    public SignerInfoGeneratorBuilder(DigestCalculatorProvider digestCalculatorProvider) {
        this(digestCalculatorProvider, new DefaultCMSSignatureEncryptionAlgorithmFinder());
    }

    public SignerInfoGeneratorBuilder(DigestCalculatorProvider digestCalculatorProvider, CMSSignatureEncryptionAlgorithmFinder cMSSignatureEncryptionAlgorithmFinder) {
        this.digestProvider = digestCalculatorProvider;
        this.sigEncAlgFinder = cMSSignatureEncryptionAlgorithmFinder;
    }

    public SignerInfoGeneratorBuilder setDirectSignature(boolean bl) {
        this.directSignature = bl;
        return this;
    }

    public SignerInfoGeneratorBuilder setContentDigest(AlgorithmIdentifier algorithmIdentifier) {
        this.contentDigest = algorithmIdentifier;
        return this;
    }

    public SignerInfoGeneratorBuilder setSignedAttributeGenerator(CMSAttributeTableGenerator cMSAttributeTableGenerator) {
        this.signedGen = cMSAttributeTableGenerator;
        return this;
    }

    public SignerInfoGeneratorBuilder setUnsignedAttributeGenerator(CMSAttributeTableGenerator cMSAttributeTableGenerator) {
        this.unsignedGen = cMSAttributeTableGenerator;
        return this;
    }

    public SignerInfoGenerator build(ContentSigner contentSigner, X509CertificateHolder x509CertificateHolder) throws OperatorCreationException {
        SignerIdentifier signerIdentifier = new SignerIdentifier(new IssuerAndSerialNumber(x509CertificateHolder.toASN1Structure()));
        SignerInfoGenerator signerInfoGenerator = this.createGenerator(contentSigner, signerIdentifier);
        signerInfoGenerator.setAssociatedCertificate(x509CertificateHolder);
        return signerInfoGenerator;
    }

    public SignerInfoGenerator build(ContentSigner contentSigner, byte[] byArray) throws OperatorCreationException {
        SignerIdentifier signerIdentifier = new SignerIdentifier(new DEROctetString(byArray));
        return this.createGenerator(contentSigner, signerIdentifier);
    }

    private SignerInfoGenerator createGenerator(ContentSigner contentSigner, SignerIdentifier signerIdentifier) throws OperatorCreationException {
        DigestCalculator digestCalculator;
        if (this.contentDigest != null) {
            digestCalculator = this.digestProvider.get(this.contentDigest);
        } else {
            AlgorithmIdentifier algorithmIdentifier = null;
            if (contentSigner instanceof ExtendedContentSigner) {
                algorithmIdentifier = ((ExtendedContentSigner)contentSigner).getDigestAlgorithmIdentifier();
            }
            if (algorithmIdentifier == null) {
                algorithmIdentifier = this.digAlgFinder.find(contentSigner.getAlgorithmIdentifier());
            }
            if (algorithmIdentifier != null) {
                digestCalculator = this.digestProvider.get(algorithmIdentifier);
            } else {
                throw new OperatorCreationException("no digest algorithm specified for signature algorithm");
            }
        }
        if (this.directSignature) {
            return new SignerInfoGenerator(signerIdentifier, contentSigner, digestCalculator.getAlgorithmIdentifier(), this.sigEncAlgFinder);
        }
        if (this.signedGen != null || this.unsignedGen != null) {
            if (this.signedGen == null) {
                this.signedGen = new DefaultSignedAttributeTableGenerator();
            }
            return new SignerInfoGenerator(signerIdentifier, contentSigner, digestCalculator, this.sigEncAlgFinder, this.signedGen, this.unsignedGen);
        }
        return new SignerInfoGenerator(signerIdentifier, contentSigner, digestCalculator, this.sigEncAlgFinder, new DefaultSignedAttributeTableGenerator(), null);
    }
}

