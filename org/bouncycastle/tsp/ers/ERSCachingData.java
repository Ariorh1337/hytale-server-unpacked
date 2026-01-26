/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp.ers;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.tsp.ers.ERSData;
import org.bouncycastle.util.Arrays;

public abstract class ERSCachingData
implements ERSData {
    private Map<CacheIndex, byte[]> preCalcs = new HashMap<CacheIndex, byte[]>();

    @Override
    public byte[] getHash(DigestCalculator digestCalculator, byte[] byArray) {
        CacheIndex cacheIndex = new CacheIndex(digestCalculator.getAlgorithmIdentifier(), byArray);
        if (this.preCalcs.containsKey(cacheIndex)) {
            return this.preCalcs.get(cacheIndex);
        }
        byte[] byArray2 = this.calculateHash(digestCalculator, byArray);
        this.preCalcs.put(cacheIndex, byArray2);
        return byArray2;
    }

    protected abstract byte[] calculateHash(DigestCalculator var1, byte[] var2);

    private static class CacheIndex {
        final AlgorithmIdentifier algId;
        final byte[] chainHash;

        private CacheIndex(AlgorithmIdentifier algorithmIdentifier, byte[] byArray) {
            this.algId = algorithmIdentifier;
            this.chainHash = byArray;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof CacheIndex)) {
                return false;
            }
            CacheIndex cacheIndex = (CacheIndex)object;
            return this.algId.equals(cacheIndex.algId) && Arrays.areEqual(this.chainHash, cacheIndex.chainHash);
        }

        public int hashCode() {
            int n = this.algId.hashCode();
            return 31 * n + Arrays.hashCode(this.chainHash);
        }
    }
}

