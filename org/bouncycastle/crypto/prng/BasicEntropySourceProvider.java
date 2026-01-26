/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.prng;

import java.security.SecureRandom;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.EntropySourceProvider;
import org.bouncycastle.crypto.prng.SP800SecureRandom;
import org.bouncycastle.crypto.prng.X931SecureRandom;

public class BasicEntropySourceProvider
implements EntropySourceProvider {
    private final SecureRandom _sr;
    private final boolean _predictionResistant;

    public BasicEntropySourceProvider(SecureRandom secureRandom, boolean bl) {
        this._sr = secureRandom;
        this._predictionResistant = bl;
    }

    @Override
    public EntropySource get(final int n) {
        return new EntropySource(){
            final /* synthetic */ BasicEntropySourceProvider this$0;
            {
                this.this$0 = basicEntropySourceProvider;
            }

            @Override
            public boolean isPredictionResistant() {
                return this.this$0._predictionResistant;
            }

            @Override
            public byte[] getEntropy() {
                if (this.this$0._sr instanceof SP800SecureRandom || this.this$0._sr instanceof X931SecureRandom) {
                    byte[] byArray = new byte[(n + 7) / 8];
                    this.this$0._sr.nextBytes(byArray);
                    return byArray;
                }
                return this.this$0._sr.generateSeed((n + 7) / 8);
            }

            @Override
            public int entropySize() {
                return n;
            }
        };
    }
}

