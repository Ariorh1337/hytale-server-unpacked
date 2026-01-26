/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.snova;

import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.snova.SnovaParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaSigner;
import org.bouncycastle.pqc.jcajce.provider.snova.BCSnovaPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.snova.BCSnovaPublicKey;
import org.bouncycastle.util.Strings;

public class SignatureSpi
extends Signature {
    private final ByteArrayOutputStream bOut;
    private final SnovaSigner signer;
    private SecureRandom random;
    private final SnovaParameters parameters;

    protected SignatureSpi(SnovaSigner snovaSigner) {
        super("Snova");
        this.bOut = new ByteArrayOutputStream();
        this.signer = snovaSigner;
        this.parameters = null;
    }

    protected SignatureSpi(SnovaSigner snovaSigner, SnovaParameters snovaParameters) {
        super(Strings.toUpperCase(snovaParameters.getName()));
        this.parameters = snovaParameters;
        this.bOut = new ByteArrayOutputStream();
        this.signer = snovaSigner;
    }

    @Override
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        String string;
        if (!(publicKey instanceof BCSnovaPublicKey)) {
            try {
                publicKey = new BCSnovaPublicKey(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));
            }
            catch (Exception exception) {
                throw new InvalidKeyException("unknown public key passed to Snova: " + exception.getMessage());
            }
        }
        BCSnovaPublicKey bCSnovaPublicKey = (BCSnovaPublicKey)publicKey;
        if (this.parameters != null && !(string = Strings.toUpperCase(this.parameters.getName())).equals(bCSnovaPublicKey.getAlgorithm())) {
            throw new InvalidKeyException("signature configured for " + string);
        }
        this.signer.init(false, bCSnovaPublicKey.getKeyParams());
    }

    @Override
    protected void engineInitSign(PrivateKey privateKey, SecureRandom secureRandom) throws InvalidKeyException {
        this.random = secureRandom;
        this.engineInitSign(privateKey);
    }

    @Override
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        if (privateKey instanceof BCSnovaPrivateKey) {
            String string;
            BCSnovaPrivateKey bCSnovaPrivateKey = (BCSnovaPrivateKey)privateKey;
            SnovaPrivateKeyParameters snovaPrivateKeyParameters = bCSnovaPrivateKey.getKeyParams();
            if (this.parameters != null && !(string = Strings.toUpperCase(this.parameters.getName())).equals(bCSnovaPrivateKey.getAlgorithm())) {
                throw new InvalidKeyException("signature configured for " + string);
            }
            if (this.random != null) {
                this.signer.init(true, new ParametersWithRandom(snovaPrivateKeyParameters, this.random));
            } else {
                this.signer.init(true, snovaPrivateKeyParameters);
            }
        } else {
            throw new InvalidKeyException("unknown private key passed to Snova");
        }
    }

    @Override
    protected void engineUpdate(byte by) throws SignatureException {
        this.bOut.write(by);
    }

    @Override
    protected void engineUpdate(byte[] byArray, int n, int n2) throws SignatureException {
        this.bOut.write(byArray, n, n2);
    }

    @Override
    protected byte[] engineSign() throws SignatureException {
        try {
            byte[] byArray = this.bOut.toByteArray();
            this.bOut.reset();
            return this.signer.generateSignature(byArray);
        }
        catch (Exception exception) {
            throw new SignatureException(exception.toString());
        }
    }

    @Override
    protected boolean engineVerify(byte[] byArray) throws SignatureException {
        byte[] byArray2 = this.bOut.toByteArray();
        this.bOut.reset();
        return this.signer.verifySignature(byArray2, byArray);
    }

    @Override
    protected void engineSetParameter(AlgorithmParameterSpec algorithmParameterSpec) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    @Override
    protected void engineSetParameter(String string, Object object) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    @Override
    protected Object engineGetParameter(String string) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    public static class Base
    extends SignatureSpi {
        public Base() {
            super(new SnovaSigner());
        }
    }

    public static class SNOVA_24_5_4_ESK
    extends SignatureSpi {
        public SNOVA_24_5_4_ESK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_24_5_4_ESK);
        }
    }

    public static class SNOVA_24_5_4_SHAKE_ESK
    extends SignatureSpi {
        public SNOVA_24_5_4_SHAKE_ESK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_24_5_4_SHAKE_ESK);
        }
    }

    public static class SNOVA_24_5_4_SHAKE_SSK
    extends SignatureSpi {
        public SNOVA_24_5_4_SHAKE_SSK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_24_5_4_SHAKE_SSK);
        }
    }

    public static class SNOVA_24_5_4_SSK
    extends SignatureSpi {
        public SNOVA_24_5_4_SSK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_24_5_4_SSK);
        }
    }

    public static class SNOVA_24_5_5_ESK
    extends SignatureSpi {
        public SNOVA_24_5_5_ESK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_24_5_5_ESK);
        }
    }

    public static class SNOVA_24_5_5_SHAKE_ESK
    extends SignatureSpi {
        public SNOVA_24_5_5_SHAKE_ESK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_24_5_5_SHAKE_ESK);
        }
    }

    public static class SNOVA_24_5_5_SHAKE_SSK
    extends SignatureSpi {
        public SNOVA_24_5_5_SHAKE_SSK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_24_5_5_SHAKE_SSK);
        }
    }

    public static class SNOVA_24_5_5_SSK
    extends SignatureSpi {
        public SNOVA_24_5_5_SSK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_24_5_5_SSK);
        }
    }

    public static class SNOVA_25_8_3_ESK
    extends SignatureSpi {
        public SNOVA_25_8_3_ESK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_25_8_3_ESK);
        }
    }

    public static class SNOVA_25_8_3_SHAKE_ESK
    extends SignatureSpi {
        public SNOVA_25_8_3_SHAKE_ESK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_25_8_3_SHAKE_ESK);
        }
    }

    public static class SNOVA_25_8_3_SHAKE_SSK
    extends SignatureSpi {
        public SNOVA_25_8_3_SHAKE_SSK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_25_8_3_SHAKE_SSK);
        }
    }

    public static class SNOVA_25_8_3_SSK
    extends SignatureSpi {
        public SNOVA_25_8_3_SSK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_25_8_3_SSK);
        }
    }

    public static class SNOVA_29_6_5_ESK
    extends SignatureSpi {
        public SNOVA_29_6_5_ESK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_29_6_5_ESK);
        }
    }

    public static class SNOVA_29_6_5_SHAKE_ESK
    extends SignatureSpi {
        public SNOVA_29_6_5_SHAKE_ESK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_29_6_5_SHAKE_ESK);
        }
    }

    public static class SNOVA_29_6_5_SHAKE_SSK
    extends SignatureSpi {
        public SNOVA_29_6_5_SHAKE_SSK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_29_6_5_SHAKE_SSK);
        }
    }

    public static class SNOVA_29_6_5_SSK
    extends SignatureSpi {
        public SNOVA_29_6_5_SSK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_29_6_5_SSK);
        }
    }

    public static class SNOVA_37_17_2_ESK
    extends SignatureSpi {
        public SNOVA_37_17_2_ESK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_37_17_2_ESK);
        }
    }

    public static class SNOVA_37_17_2_SHAKE_ESK
    extends SignatureSpi {
        public SNOVA_37_17_2_SHAKE_ESK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_37_17_2_SHAKE_ESK);
        }
    }

    public static class SNOVA_37_17_2_SHAKE_SSK
    extends SignatureSpi {
        public SNOVA_37_17_2_SHAKE_SSK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_37_17_2_SHAKE_SSK);
        }
    }

    public static class SNOVA_37_17_2_SSK
    extends SignatureSpi {
        public SNOVA_37_17_2_SSK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_37_17_2_SSK);
        }
    }

    public static class SNOVA_37_8_4_ESK
    extends SignatureSpi {
        public SNOVA_37_8_4_ESK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_37_8_4_ESK);
        }
    }

    public static class SNOVA_37_8_4_SHAKE_ESK
    extends SignatureSpi {
        public SNOVA_37_8_4_SHAKE_ESK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_37_8_4_SHAKE_ESK);
        }
    }

    public static class SNOVA_37_8_4_SHAKE_SSK
    extends SignatureSpi {
        public SNOVA_37_8_4_SHAKE_SSK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_37_8_4_SHAKE_SSK);
        }
    }

    public static class SNOVA_37_8_4_SSK
    extends SignatureSpi {
        public SNOVA_37_8_4_SSK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_37_8_4_SSK);
        }
    }

    public static class SNOVA_49_11_3_ESK
    extends SignatureSpi {
        public SNOVA_49_11_3_ESK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_49_11_3_ESK);
        }
    }

    public static class SNOVA_49_11_3_SHAKE_ESK
    extends SignatureSpi {
        public SNOVA_49_11_3_SHAKE_ESK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_49_11_3_SHAKE_ESK);
        }
    }

    public static class SNOVA_49_11_3_SHAKE_SSK
    extends SignatureSpi {
        public SNOVA_49_11_3_SHAKE_SSK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_49_11_3_SHAKE_SSK);
        }
    }

    public static class SNOVA_49_11_3_SSK
    extends SignatureSpi {
        public SNOVA_49_11_3_SSK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_49_11_3_SSK);
        }
    }

    public static class SNOVA_56_25_2_ESK
    extends SignatureSpi {
        public SNOVA_56_25_2_ESK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_56_25_2_ESK);
        }
    }

    public static class SNOVA_56_25_2_SHAKE_ESK
    extends SignatureSpi {
        public SNOVA_56_25_2_SHAKE_ESK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_56_25_2_SHAKE_ESK);
        }
    }

    public static class SNOVA_56_25_2_SHAKE_SSK
    extends SignatureSpi {
        public SNOVA_56_25_2_SHAKE_SSK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_56_25_2_SHAKE_SSK);
        }
    }

    public static class SNOVA_56_25_2_SSK
    extends SignatureSpi {
        public SNOVA_56_25_2_SSK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_56_25_2_SSK);
        }
    }

    public static class SNOVA_60_10_4_ESK
    extends SignatureSpi {
        public SNOVA_60_10_4_ESK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_60_10_4_ESK);
        }
    }

    public static class SNOVA_60_10_4_SHAKE_ESK
    extends SignatureSpi {
        public SNOVA_60_10_4_SHAKE_ESK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_60_10_4_SHAKE_ESK);
        }
    }

    public static class SNOVA_60_10_4_SHAKE_SSK
    extends SignatureSpi {
        public SNOVA_60_10_4_SHAKE_SSK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_60_10_4_SHAKE_SSK);
        }
    }

    public static class SNOVA_60_10_4_SSK
    extends SignatureSpi {
        public SNOVA_60_10_4_SSK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_60_10_4_SSK);
        }
    }

    public static class SNOVA_66_15_3_ESK
    extends SignatureSpi {
        public SNOVA_66_15_3_ESK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_66_15_3_ESK);
        }
    }

    public static class SNOVA_66_15_3_SHAKE_ESK
    extends SignatureSpi {
        public SNOVA_66_15_3_SHAKE_ESK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_66_15_3_SHAKE_ESK);
        }
    }

    public static class SNOVA_66_15_3_SHAKE_SSK
    extends SignatureSpi {
        public SNOVA_66_15_3_SHAKE_SSK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_66_15_3_SHAKE_SSK);
        }
    }

    public static class SNOVA_66_15_3_SSK
    extends SignatureSpi {
        public SNOVA_66_15_3_SSK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_66_15_3_SSK);
        }
    }

    public static class SNOVA_75_33_2_ESK
    extends SignatureSpi {
        public SNOVA_75_33_2_ESK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_75_33_2_ESK);
        }
    }

    public static class SNOVA_75_33_2_SHAKE_ESK
    extends SignatureSpi {
        public SNOVA_75_33_2_SHAKE_ESK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_75_33_2_SHAKE_ESK);
        }
    }

    public static class SNOVA_75_33_2_SHAKE_SSK
    extends SignatureSpi {
        public SNOVA_75_33_2_SHAKE_SSK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_75_33_2_SHAKE_SSK);
        }
    }

    public static class SNOVA_75_33_2_SSK
    extends SignatureSpi {
        public SNOVA_75_33_2_SSK() {
            super(new SnovaSigner(), SnovaParameters.SNOVA_75_33_2_SSK);
        }
    }
}

