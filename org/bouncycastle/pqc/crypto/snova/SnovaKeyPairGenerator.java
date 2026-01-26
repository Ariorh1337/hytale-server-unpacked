/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.snova;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.snova.GF16Utils;
import org.bouncycastle.pqc.crypto.snova.SnovaEngine;
import org.bouncycastle.pqc.crypto.snova.SnovaKeyElements;
import org.bouncycastle.pqc.crypto.snova.SnovaKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaPublicKeyParameters;
import org.bouncycastle.util.Arrays;

public class SnovaKeyPairGenerator
implements AsymmetricCipherKeyPairGenerator {
    private SnovaEngine engine;
    private static final int seedLength = 48;
    static final int publicSeedLength = 16;
    static final int privateSeedLength = 32;
    private SnovaParameters params;
    private SecureRandom random;
    private boolean initialized;

    @Override
    public void init(KeyGenerationParameters keyGenerationParameters) {
        SnovaKeyGenerationParameters snovaKeyGenerationParameters = (SnovaKeyGenerationParameters)keyGenerationParameters;
        this.params = snovaKeyGenerationParameters.getParameters();
        this.random = snovaKeyGenerationParameters.getRandom();
        this.initialized = true;
        this.engine = new SnovaEngine(this.params);
    }

    @Override
    public AsymmetricCipherKeyPair generateKeyPair() {
        if (!this.initialized) {
            throw new IllegalStateException("SNOVA key pair generator not initialized");
        }
        byte[] byArray = new byte[48];
        this.random.nextBytes(byArray);
        byte[] byArray2 = new byte[this.params.getPublicKeyLength()];
        byte[] byArray3 = new byte[this.params.getPrivateKeyLength()];
        byte[] byArray4 = Arrays.copyOfRange(byArray, 0, 16);
        byte[] byArray5 = Arrays.copyOfRange(byArray, 16, byArray.length);
        SnovaKeyElements snovaKeyElements = new SnovaKeyElements(this.params);
        System.arraycopy(byArray4, 0, byArray2, 0, byArray4.length);
        this.engine.genMap1T12Map2(snovaKeyElements, byArray4, byArray5);
        this.engine.genP22(byArray2, byArray4.length, snovaKeyElements.T12, snovaKeyElements.map1.p21, snovaKeyElements.map2.f12);
        System.arraycopy(byArray4, 0, byArray2, 0, byArray4.length);
        if (this.params.isSkIsSeed()) {
            byArray3 = byArray;
        } else {
            int n = this.params.getO();
            int n2 = this.params.getLsq();
            int n3 = this.params.getV();
            int n4 = n * this.params.getAlpha() * n2 * 4 + n3 * n * n2 + (n * n3 * n3 + n * n3 * n + n * n * n3) * n2;
            byte[] byArray6 = new byte[n4];
            int n5 = 0;
            n5 = SnovaKeyElements.copy3d(snovaKeyElements.map1.aAlpha, byArray6, n5);
            n5 = SnovaKeyElements.copy3d(snovaKeyElements.map1.bAlpha, byArray6, n5);
            n5 = SnovaKeyElements.copy3d(snovaKeyElements.map1.qAlpha1, byArray6, n5);
            n5 = SnovaKeyElements.copy3d(snovaKeyElements.map1.qAlpha2, byArray6, n5);
            n5 = SnovaKeyElements.copy3d(snovaKeyElements.T12, byArray6, n5);
            n5 = SnovaKeyElements.copy4d(snovaKeyElements.map2.f11, byArray6, n5);
            n5 = SnovaKeyElements.copy4d(snovaKeyElements.map2.f12, byArray6, n5);
            SnovaKeyElements.copy4d(snovaKeyElements.map2.f21, byArray6, n5);
            GF16Utils.encodeMergeInHalf(byArray6, n4, byArray3);
            System.arraycopy(byArray, 0, byArray3, byArray3.length - 48, 48);
        }
        return new AsymmetricCipherKeyPair(new SnovaPublicKeyParameters(this.params, byArray2), new SnovaPrivateKeyParameters(this.params, byArray3));
    }
}

