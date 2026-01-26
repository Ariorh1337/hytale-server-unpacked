/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.streamingaead;

import com.google.crypto.tink.internal.TinkBugException;
import com.google.crypto.tink.streamingaead.AesCtrHmacStreamingParameters;
import com.google.crypto.tink.streamingaead.AesGcmHkdfStreamingParameters;

public final class PredefinedStreamingAeadParameters {
    public static final AesCtrHmacStreamingParameters AES128_CTR_HMAC_SHA256_4KB = TinkBugException.exceptionIsBug(() -> AesCtrHmacStreamingParameters.builder().setKeySizeBytes(16).setDerivedKeySizeBytes(16).setHkdfHashType(AesCtrHmacStreamingParameters.HashType.SHA256).setHmacHashType(AesCtrHmacStreamingParameters.HashType.SHA256).setHmacTagSizeBytes(32).setCiphertextSegmentSizeBytes(4096).build());
    public static final AesCtrHmacStreamingParameters AES128_CTR_HMAC_SHA256_1MB = TinkBugException.exceptionIsBug(() -> AesCtrHmacStreamingParameters.builder().setKeySizeBytes(16).setDerivedKeySizeBytes(16).setHkdfHashType(AesCtrHmacStreamingParameters.HashType.SHA256).setHmacHashType(AesCtrHmacStreamingParameters.HashType.SHA256).setHmacTagSizeBytes(32).setCiphertextSegmentSizeBytes(0x100000).build());
    public static final AesCtrHmacStreamingParameters AES256_CTR_HMAC_SHA256_4KB = TinkBugException.exceptionIsBug(() -> AesCtrHmacStreamingParameters.builder().setKeySizeBytes(32).setDerivedKeySizeBytes(32).setHkdfHashType(AesCtrHmacStreamingParameters.HashType.SHA256).setHmacHashType(AesCtrHmacStreamingParameters.HashType.SHA256).setHmacTagSizeBytes(32).setCiphertextSegmentSizeBytes(4096).build());
    public static final AesCtrHmacStreamingParameters AES256_CTR_HMAC_SHA256_1MB = TinkBugException.exceptionIsBug(() -> AesCtrHmacStreamingParameters.builder().setKeySizeBytes(32).setDerivedKeySizeBytes(32).setHkdfHashType(AesCtrHmacStreamingParameters.HashType.SHA256).setHmacHashType(AesCtrHmacStreamingParameters.HashType.SHA256).setHmacTagSizeBytes(32).setCiphertextSegmentSizeBytes(0x100000).build());
    public static final AesGcmHkdfStreamingParameters AES128_GCM_HKDF_4KB = TinkBugException.exceptionIsBug(() -> AesGcmHkdfStreamingParameters.builder().setKeySizeBytes(16).setDerivedAesGcmKeySizeBytes(16).setHkdfHashType(AesGcmHkdfStreamingParameters.HashType.SHA256).setCiphertextSegmentSizeBytes(4096).build());
    public static final AesGcmHkdfStreamingParameters AES128_GCM_HKDF_1MB = TinkBugException.exceptionIsBug(() -> AesGcmHkdfStreamingParameters.builder().setKeySizeBytes(16).setDerivedAesGcmKeySizeBytes(16).setHkdfHashType(AesGcmHkdfStreamingParameters.HashType.SHA256).setCiphertextSegmentSizeBytes(0x100000).build());
    public static final AesGcmHkdfStreamingParameters AES256_GCM_HKDF_4KB = TinkBugException.exceptionIsBug(() -> AesGcmHkdfStreamingParameters.builder().setKeySizeBytes(32).setDerivedAesGcmKeySizeBytes(32).setHkdfHashType(AesGcmHkdfStreamingParameters.HashType.SHA256).setCiphertextSegmentSizeBytes(4096).build());
    public static final AesGcmHkdfStreamingParameters AES256_GCM_HKDF_1MB = TinkBugException.exceptionIsBug(() -> AesGcmHkdfStreamingParameters.builder().setKeySizeBytes(32).setDerivedAesGcmKeySizeBytes(32).setHkdfHashType(AesGcmHkdfStreamingParameters.HashType.SHA256).setCiphertextSegmentSizeBytes(0x100000).build());

    private PredefinedStreamingAeadParameters() {
    }
}

