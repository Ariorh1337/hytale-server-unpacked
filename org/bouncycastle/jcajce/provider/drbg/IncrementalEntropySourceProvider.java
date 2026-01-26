/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.drbg;

import java.security.SecureRandom;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.EntropySourceProvider;
import org.bouncycastle.jcajce.provider.drbg.IncrementalEntropySource;

class IncrementalEntropySourceProvider
implements EntropySourceProvider {
    private final SecureRandom random;
    private final boolean predictionResistant;

    public IncrementalEntropySourceProvider(SecureRandom secureRandom, boolean bl) {
        this.random = secureRandom;
        this.predictionResistant = bl;
    }

    @Override
    public EntropySource get(final int n) {
        return new IncrementalEntropySource(){
            final int numBytes;
            final /* synthetic */ IncrementalEntropySourceProvider this$0;
            {
                this.this$0 = incrementalEntropySourceProvider;
                this.numBytes = (n + 7) / 8;
            }

            @Override
            public boolean isPredictionResistant() {
                return this.this$0.predictionResistant;
            }

            @Override
            public byte[] getEntropy() {
                try {
                    return this.getEntropy(0L);
                }
                catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("initial entropy fetch interrupted");
                }
            }

            @Override
            public byte[] getEntropy(long l) throws InterruptedException {
                byte[] byArray;
                int n2;
                byte[] byArray2 = new byte[this.numBytes];
                for (n2 = 0; n2 < this.numBytes / 8; ++n2) {
                    IncrementalEntropySourceProvider.sleep(l);
                    byArray = this.this$0.random.generateSeed(8);
                    System.arraycopy(byArray, 0, byArray2, n2 * 8, byArray.length);
                }
                n2 = this.numBytes - this.numBytes / 8 * 8;
                if (n2 != 0) {
                    IncrementalEntropySourceProvider.sleep(l);
                    byArray = this.this$0.random.generateSeed(n2);
                    System.arraycopy(byArray, 0, byArray2, byArray2.length - byArray.length, byArray.length);
                }
                return byArray2;
            }

            @Override
            public int entropySize() {
                return n;
            }
        };
    }

    private static void sleep(long l) throws InterruptedException {
        if (l != 0L) {
            Thread.sleep(l);
        }
    }
}

