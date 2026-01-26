/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.newhope;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.ShortBufferException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseAgreementSpi;
import org.bouncycastle.pqc.crypto.ExchangePair;
import org.bouncycastle.pqc.crypto.newhope.NHAgreement;
import org.bouncycastle.pqc.crypto.newhope.NHExchangePairGenerator;
import org.bouncycastle.pqc.crypto.newhope.NHPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.newhope.BCNHPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.newhope.BCNHPublicKey;
import org.bouncycastle.util.Arrays;

public class KeyAgreementSpi
extends BaseAgreementSpi {
    private NHAgreement agreement;
    private BCNHPublicKey otherPartyKey;
    private NHExchangePairGenerator exchangePairGenerator;
    private byte[] shared;

    public KeyAgreementSpi() {
        super("NH", null);
    }

    @Override
    protected void engineInit(Key key, SecureRandom secureRandom) throws InvalidKeyException {
        if (key != null) {
            this.agreement = new NHAgreement();
            this.agreement.init(((BCNHPrivateKey)key).getKeyParams());
        } else {
            this.exchangePairGenerator = new NHExchangePairGenerator(secureRandom);
        }
    }

    @Override
    protected void doInitFromKey(Key key, AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        throw new InvalidAlgorithmParameterException("NewHope does not require parameters");
    }

    @Override
    protected Key engineDoPhase(Key key, boolean bl) throws InvalidKeyException, IllegalStateException {
        if (!bl) {
            throw new IllegalStateException("NewHope can only be between two parties.");
        }
        this.otherPartyKey = (BCNHPublicKey)key;
        if (this.exchangePairGenerator != null) {
            ExchangePair exchangePair = this.exchangePairGenerator.generateExchange((AsymmetricKeyParameter)this.otherPartyKey.getKeyParams());
            this.shared = exchangePair.getSharedValue();
            return new BCNHPublicKey((NHPublicKeyParameters)exchangePair.getPublicKey());
        }
        this.shared = this.agreement.calculateAgreement(this.otherPartyKey.getKeyParams());
        return null;
    }

    @Override
    protected byte[] engineGenerateSecret() throws IllegalStateException {
        byte[] byArray = Arrays.clone(this.shared);
        Arrays.fill(this.shared, (byte)0);
        return byArray;
    }

    @Override
    protected int engineGenerateSecret(byte[] byArray, int n) throws IllegalStateException, ShortBufferException {
        System.arraycopy(this.shared, 0, byArray, n, this.shared.length);
        Arrays.fill(this.shared, (byte)0);
        return this.shared.length;
    }

    @Override
    protected byte[] doCalcSecret() {
        return this.engineGenerateSecret();
    }
}

