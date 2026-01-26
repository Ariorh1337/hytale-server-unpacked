/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.prf;

import com.google.crypto.tink.prf.PrfParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.util.Objects;

public final class AesCmacPrfParameters
extends PrfParameters {
    private final int keySizeBytes;

    public static AesCmacPrfParameters create(int keySizeBytes) throws GeneralSecurityException {
        if (keySizeBytes != 16 && keySizeBytes != 32) {
            throw new InvalidAlgorithmParameterException(String.format("Invalid key size %d; only 128-bit and 256-bit are supported", keySizeBytes * 8));
        }
        return new AesCmacPrfParameters(keySizeBytes);
    }

    private AesCmacPrfParameters(int keySizeBytes) {
        this.keySizeBytes = keySizeBytes;
    }

    public int getKeySizeBytes() {
        return this.keySizeBytes;
    }

    public boolean equals(Object o) {
        if (!(o instanceof AesCmacPrfParameters)) {
            return false;
        }
        AesCmacPrfParameters that = (AesCmacPrfParameters)o;
        return that.getKeySizeBytes() == this.getKeySizeBytes();
    }

    public int hashCode() {
        return Objects.hash(AesCmacPrfParameters.class, this.keySizeBytes);
    }

    @Override
    public boolean hasIdRequirement() {
        return false;
    }

    public String toString() {
        return "AesCmac PRF Parameters (" + this.keySizeBytes + "-byte key)";
    }
}

