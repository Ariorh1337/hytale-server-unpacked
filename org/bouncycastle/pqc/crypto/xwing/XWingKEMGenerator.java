/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.xwing;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.agreement.X25519Agreement;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.generators.X25519KeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.X25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMGenerator;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.SecretWithEncapsulationImpl;
import org.bouncycastle.pqc.crypto.xwing.XWingPublicKeyParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class XWingKEMGenerator
implements EncapsulatedSecretGenerator {
    private final SecureRandom random;
    private static final byte[] XWING_LABEL = Strings.toByteArray("\\.//^\\");

    public XWingKEMGenerator(SecureRandom secureRandom) {
        this.random = secureRandom;
    }

    @Override
    public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter asymmetricKeyParameter) {
        XWingPublicKeyParameters xWingPublicKeyParameters = (XWingPublicKeyParameters)asymmetricKeyParameter;
        MLKEMPublicKeyParameters mLKEMPublicKeyParameters = xWingPublicKeyParameters.getKyberPublicKey();
        X25519PublicKeyParameters x25519PublicKeyParameters = xWingPublicKeyParameters.getXDHPublicKey();
        byte[] byArray = x25519PublicKeyParameters.getEncoded();
        MLKEMGenerator mLKEMGenerator = new MLKEMGenerator(this.random);
        SecretWithEncapsulation secretWithEncapsulation = mLKEMGenerator.generateEncapsulated(mLKEMPublicKeyParameters);
        byte[] byArray2 = secretWithEncapsulation.getEncapsulation();
        X25519KeyPairGenerator x25519KeyPairGenerator = new X25519KeyPairGenerator();
        x25519KeyPairGenerator.init(new X25519KeyGenerationParameters(this.random));
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = x25519KeyPairGenerator.generateKeyPair();
        byte[] byArray3 = ((X25519PublicKeyParameters)asymmetricCipherKeyPair.getPublic()).getEncoded();
        byte[] byArray4 = XWingKEMGenerator.computeSSX(x25519PublicKeyParameters, (X25519PrivateKeyParameters)asymmetricCipherKeyPair.getPrivate());
        byte[] byArray5 = XWingKEMGenerator.computeSharedSecret(byArray, secretWithEncapsulation.getSecret(), byArray3, byArray4);
        Arrays.clear(byArray4);
        return new SecretWithEncapsulationImpl(byArray5, Arrays.concatenate(byArray2, byArray3));
    }

    static byte[] computeSSX(X25519PublicKeyParameters x25519PublicKeyParameters, X25519PrivateKeyParameters x25519PrivateKeyParameters) {
        X25519Agreement x25519Agreement = new X25519Agreement();
        x25519Agreement.init(x25519PrivateKeyParameters);
        byte[] byArray = new byte[x25519Agreement.getAgreementSize()];
        x25519Agreement.calculateAgreement(x25519PublicKeyParameters, byArray, 0);
        return byArray;
    }

    static byte[] computeSharedSecret(byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4) {
        SHA3Digest sHA3Digest = new SHA3Digest(256);
        sHA3Digest.update(byArray2, 0, byArray2.length);
        sHA3Digest.update(byArray4, 0, byArray4.length);
        sHA3Digest.update(byArray3, 0, byArray3.length);
        sHA3Digest.update(byArray, 0, byArray.length);
        sHA3Digest.update(XWING_LABEL, 0, XWING_LABEL.length);
        byte[] byArray5 = new byte[32];
        sHA3Digest.doFinal(byArray5, 0);
        return byArray5;
    }
}

