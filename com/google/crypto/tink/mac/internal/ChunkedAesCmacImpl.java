/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.mac.internal;

import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.ConscryptUtil;
import com.google.crypto.tink.mac.AesCmacKey;
import com.google.crypto.tink.mac.ChunkedMac;
import com.google.crypto.tink.mac.ChunkedMacComputation;
import com.google.crypto.tink.mac.ChunkedMacVerification;
import com.google.crypto.tink.mac.internal.ChunkedAesCmacComputation;
import com.google.crypto.tink.mac.internal.ChunkedAesCmacConscrypt;
import com.google.crypto.tink.mac.internal.ChunkedMacVerificationFromComputation;
import com.google.crypto.tink.util.Bytes;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.Provider;

@Immutable
public final class ChunkedAesCmacImpl
implements ChunkedMac {
    private static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS;
    private final AesCmacKey key;

    public ChunkedAesCmacImpl(AesCmacKey key) {
        this.key = key;
    }

    @Override
    public ChunkedMacComputation createComputation() throws GeneralSecurityException {
        return new ChunkedAesCmacComputation(this.key);
    }

    @Override
    public ChunkedMacVerification createVerification(byte[] tag) throws GeneralSecurityException {
        if (tag.length < this.key.getOutputPrefix().size()) {
            throw new GeneralSecurityException("Tag too short");
        }
        if (!this.key.getOutputPrefix().equals(Bytes.copyFrom(tag, 0, this.key.getOutputPrefix().size()))) {
            throw new GeneralSecurityException("Wrong tag prefix");
        }
        return ChunkedMacVerificationFromComputation.create(new ChunkedAesCmacComputation(this.key), tag);
    }

    public static ChunkedMac create(AesCmacKey key) throws GeneralSecurityException {
        if (!FIPS.isCompatible()) {
            throw new GeneralSecurityException("Cannot use AES-CMAC in FIPS-mode.");
        }
        Provider conscrypt = ConscryptUtil.providerOrNull();
        if (conscrypt != null) {
            try {
                return ChunkedAesCmacConscrypt.create(key, conscrypt);
            }
            catch (GeneralSecurityException generalSecurityException) {
                // empty catch block
            }
        }
        return new ChunkedAesCmacImpl(key);
    }
}

