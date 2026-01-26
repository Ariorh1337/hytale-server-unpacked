/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.its.bc;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.DSADigestSigner;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.bc.BcITSPublicVerificationKey;
import org.bouncycastle.its.operator.ITSContentVerifierProvider;
import org.bouncycastle.oer.OEREncoder;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.VerificationKeyIndicator;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicVerificationKey;
import org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDefaultDigestProvider;
import org.bouncycastle.util.Arrays;

public class BcITSContentVerifierProvider
implements ITSContentVerifierProvider {
    private final ITSCertificate issuer;
    private final byte[] parentData;
    private final AlgorithmIdentifier digestAlgo;
    private final ECPublicKeyParameters pubParams;
    private final int sigChoice;

    public BcITSContentVerifierProvider(ITSCertificate iTSCertificate) throws IOException {
        PublicVerificationKey publicVerificationKey;
        this.issuer = iTSCertificate;
        this.parentData = iTSCertificate.getEncoded();
        ToBeSignedCertificate toBeSignedCertificate = iTSCertificate.toASN1Structure().getToBeSigned();
        VerificationKeyIndicator verificationKeyIndicator = toBeSignedCertificate.getVerifyKeyIndicator();
        if (verificationKeyIndicator.getVerificationKeyIndicator() instanceof PublicVerificationKey) {
            publicVerificationKey = PublicVerificationKey.getInstance(verificationKeyIndicator.getVerificationKeyIndicator());
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
                    throw new IllegalStateException("unknown key type");
                }
            }
        } else {
            throw new IllegalStateException("not public verification key");
        }
        this.pubParams = (ECPublicKeyParameters)new BcITSPublicVerificationKey(publicVerificationKey).getKey();
    }

    @Override
    public ITSCertificate getAssociatedCertificate() {
        return this.issuer;
    }

    @Override
    public boolean hasAssociatedCertificate() {
        return this.issuer != null;
    }

    @Override
    public ContentVerifier get(int n) throws OperatorCreationException {
        Object object;
        byte[] byArray;
        if (this.sigChoice != n) {
            throw new OperatorCreationException("wrong verifier for algorithm: " + n);
        }
        final ExtendedDigest extendedDigest = BcDefaultDigestProvider.INSTANCE.get(this.digestAlgo);
        byte[] byArray2 = new byte[extendedDigest.getDigestSize()];
        extendedDigest.update(this.parentData, 0, this.parentData.length);
        extendedDigest.doFinal(byArray2, 0);
        byte[] byArray3 = byArray = this.issuer.getIssuer().isSelf() ? new byte[extendedDigest.getDigestSize()] : null;
        if (byArray != null) {
            object = OEREncoder.toByteArray(this.issuer.toASN1Structure().getToBeSigned(), IEEE1609dot2.ToBeSignedCertificate.build());
            extendedDigest.update((byte[])object, 0, ((Object)object).length);
            extendedDigest.doFinal(byArray, 0);
        }
        object = new OutputStream(this){
            final /* synthetic */ BcITSContentVerifierProvider this$0;
            {
                this.this$0 = bcITSContentVerifierProvider;
            }

            @Override
            public void write(int n) throws IOException {
                extendedDigest.update((byte)n);
            }

            @Override
            public void write(byte[] byArray) throws IOException {
                extendedDigest.update(byArray, 0, byArray.length);
            }

            @Override
            public void write(byte[] byArray, int n, int n2) throws IOException {
                extendedDigest.update(byArray, n, n2);
            }
        };
        return new ContentVerifier(){
            final DSADigestSigner signer;
            final /* synthetic */ OutputStream val$os;
            final /* synthetic */ Digest val$digest;
            final /* synthetic */ byte[] val$parentTBSDigest;
            final /* synthetic */ byte[] val$parentDigest;
            final /* synthetic */ BcITSContentVerifierProvider this$0;
            {
                this.val$os = outputStream;
                this.val$digest = digest;
                this.val$parentTBSDigest = byArray;
                this.val$parentDigest = byArray2;
                this.this$0 = bcITSContentVerifierProvider;
                this.signer = new DSADigestSigner(new ECDSASigner(), BcDefaultDigestProvider.INSTANCE.get(this.this$0.digestAlgo));
            }

            @Override
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return null;
            }

            @Override
            public OutputStream getOutputStream() {
                return this.val$os;
            }

            @Override
            public boolean verify(byte[] byArray) {
                byte[] byArray2 = new byte[this.val$digest.getDigestSize()];
                this.val$digest.doFinal(byArray2, 0);
                this.signer.init(false, this.this$0.pubParams);
                this.signer.update(byArray2, 0, byArray2.length);
                if (this.val$parentTBSDigest != null && Arrays.areEqual(byArray2, this.val$parentTBSDigest)) {
                    byte[] byArray3 = new byte[this.val$digest.getDigestSize()];
                    this.val$digest.doFinal(byArray3, 0);
                    this.signer.update(byArray3, 0, byArray3.length);
                } else {
                    this.signer.update(this.val$parentDigest, 0, this.val$parentDigest.length);
                }
                return this.signer.verifySignature(byArray);
            }
        };
    }
}

