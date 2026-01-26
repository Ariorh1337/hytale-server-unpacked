/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.crystals.dilithium;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumEngine;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPublicKeyParameters;

public class DilithiumKeyPairGenerator
implements AsymmetricCipherKeyPairGenerator {
    private DilithiumParameters dilithiumParams;
    private SecureRandom random;

    private void initialize(KeyGenerationParameters keyGenerationParameters) {
        this.dilithiumParams = ((DilithiumKeyGenerationParameters)keyGenerationParameters).getParameters();
        this.random = keyGenerationParameters.getRandom();
    }

    private AsymmetricCipherKeyPair genKeyPair() {
        DilithiumEngine dilithiumEngine = this.dilithiumParams.getEngine(this.random);
        byte[][] byArray = dilithiumEngine.generateKeyPair();
        DilithiumPublicKeyParameters dilithiumPublicKeyParameters = new DilithiumPublicKeyParameters(this.dilithiumParams, byArray[0], byArray[6]);
        DilithiumPrivateKeyParameters dilithiumPrivateKeyParameters = new DilithiumPrivateKeyParameters(this.dilithiumParams, byArray[0], byArray[1], byArray[2], byArray[3], byArray[4], byArray[5], byArray[6]);
        return new AsymmetricCipherKeyPair(dilithiumPublicKeyParameters, dilithiumPrivateKeyParameters);
    }

    @Override
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.initialize(keyGenerationParameters);
    }

    @Override
    public AsymmetricCipherKeyPair generateKeyPair() {
        return this.genKeyPair();
    }

    public AsymmetricCipherKeyPair internalGenerateKeyPair(byte[] byArray) {
        DilithiumEngine dilithiumEngine = this.dilithiumParams.getEngine(this.random);
        byte[][] byArray2 = dilithiumEngine.generateKeyPairInternal(byArray);
        DilithiumPublicKeyParameters dilithiumPublicKeyParameters = new DilithiumPublicKeyParameters(this.dilithiumParams, byArray2[0], byArray2[6]);
        DilithiumPrivateKeyParameters dilithiumPrivateKeyParameters = new DilithiumPrivateKeyParameters(this.dilithiumParams, byArray2[0], byArray2[1], byArray2[2], byArray2[3], byArray2[4], byArray2[5], byArray2[6]);
        return new AsymmetricCipherKeyPair(dilithiumPublicKeyParameters, dilithiumPrivateKeyParameters);
    }
}

