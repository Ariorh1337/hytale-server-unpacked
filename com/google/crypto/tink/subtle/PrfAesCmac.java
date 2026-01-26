/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.subtle;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.prf.AesCmacPrfKey;
import com.google.crypto.tink.prf.AesCmacPrfParameters;
import com.google.crypto.tink.prf.Prf;
import com.google.crypto.tink.prf.internal.PrfAesCmacConscrypt;
import com.google.crypto.tink.util.SecretBytes;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;

@Immutable
@AccessesPartialKey
public final class PrfAesCmac
implements Prf {
    private final Prf prf;

    @AccessesPartialKey
    private static AesCmacPrfKey createAesCmacPrfKey(byte[] key) throws GeneralSecurityException {
        return AesCmacPrfKey.create(AesCmacPrfParameters.create(key.length), SecretBytes.copyFrom(key, InsecureSecretKeyAccess.get()));
    }

    private PrfAesCmac(AesCmacPrfKey key) throws GeneralSecurityException {
        this.prf = PrfAesCmac.create(key);
    }

    public PrfAesCmac(byte[] key) throws GeneralSecurityException {
        this(PrfAesCmac.createAesCmacPrfKey(key));
    }

    public static Prf create(AesCmacPrfKey key) throws GeneralSecurityException {
        Prf prf = com.google.crypto.tink.prf.internal.PrfAesCmac.create(key);
        try {
            Prf conscryptPrf = PrfAesCmacConscrypt.create(key);
            return new PrfImplementation(prf, conscryptPrf);
        }
        catch (GeneralSecurityException e) {
            return prf;
        }
    }

    @Override
    public byte[] compute(byte[] data, int outputLength) throws GeneralSecurityException {
        return this.prf.compute(data, outputLength);
    }

    @Immutable
    private static class PrfImplementation
    implements Prf {
        final Prf small;
        final Prf large;
        private static final int SMALL_DATA_SIZE = 64;

        @Override
        public byte[] compute(byte[] data, int outputLength) throws GeneralSecurityException {
            if (data.length <= 64) {
                return this.small.compute(data, outputLength);
            }
            return this.large.compute(data, outputLength);
        }

        private PrfImplementation(Prf small, Prf large) {
            this.small = small;
            this.large = large;
        }
    }
}

