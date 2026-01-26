/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.its.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.security.Provider;
import java.security.Signature;
import java.security.interfaces.ECPublicKey;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.ITSPublicVerificationKey;
import org.bouncycastle.its.jcajce.JcaITSPublicVerificationKey;
import org.bouncycastle.its.operator.ITSContentVerifierProvider;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.oer.OEREncoder;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.VerificationKeyIndicator;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicVerificationKey;
import org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Arrays;

public class JcaITSContentVerifierProvider
implements ITSContentVerifierProvider {
    private final ITSCertificate issuer;
    private final byte[] parentData;
    private final JcaJceHelper helper;
    private AlgorithmIdentifier digestAlgo;
    private ECPublicKey pubParams;
    private int sigChoice;

    private JcaITSContentVerifierProvider(ITSCertificate iTSCertificate, JcaJceHelper jcaJceHelper) {
        this.issuer = iTSCertificate;
        this.helper = jcaJceHelper;
        try {
            this.parentData = iTSCertificate.getEncoded();
        }
        catch (IOException iOException) {
            throw new IllegalStateException("unable to extract parent data: " + iOException.getMessage());
        }
        ToBeSignedCertificate toBeSignedCertificate = iTSCertificate.toASN1Structure().getToBeSigned();
        VerificationKeyIndicator verificationKeyIndicator = toBeSignedCertificate.getVerifyKeyIndicator();
        if (!(verificationKeyIndicator.getVerificationKeyIndicator() instanceof PublicVerificationKey)) {
            throw new IllegalArgumentException("not public verification key");
        }
        PublicVerificationKey publicVerificationKey = PublicVerificationKey.getInstance(verificationKeyIndicator.getVerificationKeyIndicator());
        this.initForPvi(publicVerificationKey, jcaJceHelper);
    }

    private JcaITSContentVerifierProvider(ITSPublicVerificationKey iTSPublicVerificationKey, JcaJceHelper jcaJceHelper) {
        this.issuer = null;
        this.parentData = null;
        this.helper = jcaJceHelper;
        this.initForPvi(iTSPublicVerificationKey.toASN1Structure(), jcaJceHelper);
    }

    private void initForPvi(PublicVerificationKey publicVerificationKey, JcaJceHelper jcaJceHelper) {
        this.sigChoice = publicVerificationKey.getChoice();
        switch (publicVerificationKey.getChoice()) {
            case 0: {
                this.digestAlgo = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
                break;
            }
            case 1: {
                this.digestAlgo = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
                break;
            }
            case 2: {
                this.digestAlgo = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384);
                break;
            }
            default: {
                throw new IllegalArgumentException("unknown key type");
            }
        }
        this.pubParams = (ECPublicKey)new JcaITSPublicVerificationKey(publicVerificationKey, jcaJceHelper).getKey();
    }

    @Override
    public boolean hasAssociatedCertificate() {
        return this.issuer != null;
    }

    @Override
    public ITSCertificate getAssociatedCertificate() {
        return this.issuer;
    }

    @Override
    public ContentVerifier get(int n) throws OperatorCreationException {
        DigestCalculatorProvider digestCalculatorProvider;
        Object object;
        if (this.sigChoice != n) {
            throw new OperatorCreationException("wrong verifier for algorithm: " + n);
        }
        try {
            object = new JcaDigestCalculatorProviderBuilder().setHelper(this.helper);
            digestCalculatorProvider = ((JcaDigestCalculatorProviderBuilder)object).build();
        }
        catch (Exception exception) {
            throw new IllegalStateException(exception.getMessage(), exception);
        }
        object = digestCalculatorProvider.get(this.digestAlgo);
        try {
            byte[] byArray;
            Object object2;
            final OutputStream outputStream = object.getOutputStream();
            if (this.parentData != null) {
                outputStream.write(this.parentData, 0, this.parentData.length);
            }
            byte[] byArray2 = object.getDigest();
            if (this.issuer != null && this.issuer.getIssuer().isSelf()) {
                object2 = OEREncoder.toByteArray(this.issuer.toASN1Structure().getToBeSigned(), IEEE1609dot2.ToBeSignedCertificate.build());
                outputStream.write((byte[])object2, 0, ((Object)object2).length);
                byArray = object.getDigest();
            } else {
                byArray = null;
            }
            switch (this.sigChoice) {
                case 0: 
                case 1: {
                    object2 = this.helper.createSignature("SHA256withECDSA");
                    break;
                }
                case 2: {
                    object2 = this.helper.createSignature("SHA384withECDSA");
                    break;
                }
                default: {
                    throw new IllegalArgumentException("choice " + this.sigChoice + " not supported");
                }
            }
            return new ContentVerifier(){
                final /* synthetic */ DigestCalculator val$calculator;
                final /* synthetic */ Signature val$signature;
                final /* synthetic */ byte[] val$parentTBSDigest;
                final /* synthetic */ byte[] val$parentDigest;
                final /* synthetic */ JcaITSContentVerifierProvider this$0;
                {
                    this.val$calculator = digestCalculator;
                    this.val$signature = signature;
                    this.val$parentTBSDigest = byArray;
                    this.val$parentDigest = byArray2;
                    this.this$0 = jcaITSContentVerifierProvider;
                }

                @Override
                public AlgorithmIdentifier getAlgorithmIdentifier() {
                    return null;
                }

                @Override
                public OutputStream getOutputStream() {
                    return outputStream;
                }

                @Override
                public boolean verify(byte[] byArray) {
                    byte[] byArray2 = this.val$calculator.getDigest();
                    try {
                        this.val$signature.initVerify(this.this$0.pubParams);
                        this.val$signature.update(byArray2);
                        if (this.val$parentTBSDigest != null && Arrays.areEqual(byArray2, this.val$parentTBSDigest)) {
                            byte[] byArray3 = this.val$calculator.getDigest();
                            this.val$signature.update(byArray3);
                        } else {
                            this.val$signature.update(this.val$parentDigest);
                        }
                        return this.val$signature.verify(byArray);
                    }
                    catch (Exception exception) {
                        throw new RuntimeException(exception.getMessage(), exception);
                    }
                }
            };
        }
        catch (Exception exception) {
            throw new IllegalStateException(exception.getMessage(), exception);
        }
    }

    public static class Builder {
        private JcaJceHelper helper = new DefaultJcaJceHelper();

        public Builder setProvider(Provider provider) {
            this.helper = new ProviderJcaJceHelper(provider);
            return this;
        }

        public Builder setProvider(String string) {
            this.helper = new NamedJcaJceHelper(string);
            return this;
        }

        public JcaITSContentVerifierProvider build(ITSCertificate iTSCertificate) {
            return new JcaITSContentVerifierProvider(iTSCertificate, this.helper);
        }

        public JcaITSContentVerifierProvider build(ITSPublicVerificationKey iTSPublicVerificationKey) {
            return new JcaITSContentVerifierProvider(iTSPublicVerificationKey, this.helper);
        }
    }
}

