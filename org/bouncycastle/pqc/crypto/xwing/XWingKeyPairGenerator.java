/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.xwing;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.generators.X25519KeyPairGenerator;
import org.bouncycastle.crypto.params.X25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.prng.FixedSecureRandom;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMKeyPairGenerator;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters;
import org.bouncycastle.pqc.crypto.xwing.XWingPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xwing.XWingPublicKeyParameters;
import org.bouncycastle.util.Arrays;

public class XWingKeyPairGenerator
implements AsymmetricCipherKeyPairGenerator {
    private SecureRandom random;

    private void initialize(KeyGenerationParameters keyGenerationParameters) {
        this.random = keyGenerationParameters.getRandom();
    }

    static AsymmetricCipherKeyPair genKeyPair(byte[] byArray) {
        SHAKEDigest sHAKEDigest = new SHAKEDigest(256);
        sHAKEDigest.update(byArray, 0, byArray.length);
        byte[] byArray2 = new byte[96];
        sHAKEDigest.doOutput(byArray2, 0, byArray2.length);
        byte[] byArray3 = Arrays.copyOfRange(byArray2, 0, 64);
        byte[] byArray4 = Arrays.copyOfRange(byArray2, 64, 96);
        FixedSecureRandom fixedSecureRandom = new FixedSecureRandom(byArray3);
        MLKEMKeyPairGenerator mLKEMKeyPairGenerator = new MLKEMKeyPairGenerator();
        mLKEMKeyPairGenerator.init(new MLKEMKeyGenerationParameters((SecureRandom)fixedSecureRandom, MLKEMParameters.ml_kem_768));
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = mLKEMKeyPairGenerator.generateKeyPair();
        MLKEMPublicKeyParameters mLKEMPublicKeyParameters = (MLKEMPublicKeyParameters)asymmetricCipherKeyPair.getPublic();
        MLKEMPrivateKeyParameters mLKEMPrivateKeyParameters = (MLKEMPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate();
        FixedSecureRandom fixedSecureRandom2 = new FixedSecureRandom(byArray4);
        X25519KeyPairGenerator x25519KeyPairGenerator = new X25519KeyPairGenerator();
        x25519KeyPairGenerator.init(new X25519KeyGenerationParameters(fixedSecureRandom2));
        AsymmetricCipherKeyPair asymmetricCipherKeyPair2 = x25519KeyPairGenerator.generateKeyPair();
        X25519PublicKeyParameters x25519PublicKeyParameters = (X25519PublicKeyParameters)asymmetricCipherKeyPair2.getPublic();
        X25519PrivateKeyParameters x25519PrivateKeyParameters = (X25519PrivateKeyParameters)asymmetricCipherKeyPair2.getPrivate();
        return new AsymmetricCipherKeyPair(new XWingPublicKeyParameters(mLKEMPublicKeyParameters, x25519PublicKeyParameters), new XWingPrivateKeyParameters(byArray, mLKEMPrivateKeyParameters, x25519PrivateKeyParameters, mLKEMPublicKeyParameters, x25519PublicKeyParameters));
    }

    @Override
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.initialize(keyGenerationParameters);
    }

    @Override
    public AsymmetricCipherKeyPair generateKeyPair() {
        byte[] byArray = new byte[32];
        this.random.nextBytes(byArray);
        return XWingKeyPairGenerator.genKeyPair(byArray);
    }
}

