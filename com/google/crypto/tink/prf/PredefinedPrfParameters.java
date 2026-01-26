/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.prf;

import com.google.crypto.tink.internal.TinkBugException;
import com.google.crypto.tink.prf.AesCmacPrfParameters;
import com.google.crypto.tink.prf.HkdfPrfParameters;
import com.google.crypto.tink.prf.HmacPrfParameters;

public final class PredefinedPrfParameters {
    public static final HkdfPrfParameters HKDF_SHA256 = TinkBugException.exceptionIsBug(() -> HkdfPrfParameters.builder().setKeySizeBytes(32).setHashType(HkdfPrfParameters.HashType.SHA256).build());
    public static final HmacPrfParameters HMAC_SHA256_PRF = TinkBugException.exceptionIsBug(() -> HmacPrfParameters.builder().setKeySizeBytes(32).setHashType(HmacPrfParameters.HashType.SHA256).build());
    public static final HmacPrfParameters HMAC_SHA512_PRF = TinkBugException.exceptionIsBug(() -> HmacPrfParameters.builder().setKeySizeBytes(64).setHashType(HmacPrfParameters.HashType.SHA512).build());
    public static final AesCmacPrfParameters AES_CMAC_PRF = TinkBugException.exceptionIsBug(() -> AesCmacPrfParameters.create(32));

    private PredefinedPrfParameters() {
    }
}

