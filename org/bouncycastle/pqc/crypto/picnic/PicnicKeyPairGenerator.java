/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.picnic;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicEngine;
import org.bouncycastle.pqc.crypto.picnic.PicnicKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicPublicKeyParameters;

public class PicnicKeyPairGenerator
implements AsymmetricCipherKeyPairGenerator {
    private SecureRandom random;
    private PicnicParameters parameters;

    @Override
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.random = keyGenerationParameters.getRandom();
        this.parameters = ((PicnicKeyGenerationParameters)keyGenerationParameters).getParameters();
    }

    @Override
    public AsymmetricCipherKeyPair generateKeyPair() {
        PicnicEngine picnicEngine = this.parameters.getEngine();
        byte[] byArray = new byte[picnicEngine.getSecretKeySize()];
        byte[] byArray2 = new byte[picnicEngine.getPublicKeySize()];
        picnicEngine.crypto_sign_keypair(byArray2, byArray, this.random);
        PicnicPublicKeyParameters picnicPublicKeyParameters = new PicnicPublicKeyParameters(this.parameters, byArray2);
        PicnicPrivateKeyParameters picnicPrivateKeyParameters = new PicnicPrivateKeyParameters(this.parameters, byArray);
        return new AsymmetricCipherKeyPair(picnicPublicKeyParameters, picnicPrivateKeyParameters);
    }
}

