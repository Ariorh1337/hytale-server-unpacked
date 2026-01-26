/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.streamingaead;

import com.google.crypto.tink.proto.AesCtrHmacStreamingKeyFormat;
import com.google.crypto.tink.proto.AesCtrHmacStreamingParams;
import com.google.crypto.tink.proto.AesGcmHkdfStreamingKeyFormat;
import com.google.crypto.tink.proto.AesGcmHkdfStreamingParams;
import com.google.crypto.tink.proto.HashType;
import com.google.crypto.tink.proto.HmacParams;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.streamingaead.AesCtrHmacStreamingKeyManager;
import com.google.crypto.tink.streamingaead.AesGcmHkdfStreamingKeyManager;

@Deprecated
public final class StreamingAeadKeyTemplates {
    public static final KeyTemplate AES128_CTR_HMAC_SHA256_4KB = StreamingAeadKeyTemplates.createAesCtrHmacStreamingKeyTemplate(16, HashType.SHA256, 16, HashType.SHA256, 32, 4096);
    public static final KeyTemplate AES128_CTR_HMAC_SHA256_1MB = StreamingAeadKeyTemplates.createAesCtrHmacStreamingKeyTemplate(16, HashType.SHA256, 16, HashType.SHA256, 32, 0x100000);
    public static final KeyTemplate AES256_CTR_HMAC_SHA256_4KB = StreamingAeadKeyTemplates.createAesCtrHmacStreamingKeyTemplate(32, HashType.SHA256, 32, HashType.SHA256, 32, 4096);
    public static final KeyTemplate AES256_CTR_HMAC_SHA256_1MB = StreamingAeadKeyTemplates.createAesCtrHmacStreamingKeyTemplate(32, HashType.SHA256, 32, HashType.SHA256, 32, 0x100000);
    public static final KeyTemplate AES128_GCM_HKDF_4KB = StreamingAeadKeyTemplates.createAesGcmHkdfStreamingKeyTemplate(16, HashType.SHA256, 16, 4096);
    public static final KeyTemplate AES128_GCM_HKDF_1MB = StreamingAeadKeyTemplates.createAesGcmHkdfStreamingKeyTemplate(16, HashType.SHA256, 16, 0x100000);
    public static final KeyTemplate AES256_GCM_HKDF_4KB = StreamingAeadKeyTemplates.createAesGcmHkdfStreamingKeyTemplate(32, HashType.SHA256, 32, 4096);
    public static final KeyTemplate AES256_GCM_HKDF_1MB = StreamingAeadKeyTemplates.createAesGcmHkdfStreamingKeyTemplate(32, HashType.SHA256, 32, 0x100000);

    public static KeyTemplate createAesCtrHmacStreamingKeyTemplate(int mainKeySize, HashType hkdfHashType, int derivedKeySize, HashType macHashType, int tagSize, int ciphertextSegmentSize) {
        HmacParams hmacParams = HmacParams.newBuilder().setHash(macHashType).setTagSize(tagSize).build();
        AesCtrHmacStreamingParams params = AesCtrHmacStreamingParams.newBuilder().setCiphertextSegmentSize(ciphertextSegmentSize).setDerivedKeySize(derivedKeySize).setHkdfHashType(hkdfHashType).setHmacParams(hmacParams).build();
        AesCtrHmacStreamingKeyFormat format = AesCtrHmacStreamingKeyFormat.newBuilder().setParams(params).setKeySize(mainKeySize).build();
        return KeyTemplate.newBuilder().setValue(format.toByteString()).setTypeUrl(AesCtrHmacStreamingKeyManager.getKeyType()).setOutputPrefixType(OutputPrefixType.RAW).build();
    }

    public static KeyTemplate createAesGcmHkdfStreamingKeyTemplate(int mainKeySize, HashType hkdfHashType, int derivedKeySize, int ciphertextSegmentSize) {
        AesGcmHkdfStreamingParams keyParams = AesGcmHkdfStreamingParams.newBuilder().setCiphertextSegmentSize(ciphertextSegmentSize).setDerivedKeySize(derivedKeySize).setHkdfHashType(hkdfHashType).build();
        AesGcmHkdfStreamingKeyFormat format = AesGcmHkdfStreamingKeyFormat.newBuilder().setKeySize(mainKeySize).setParams(keyParams).build();
        return KeyTemplate.newBuilder().setValue(format.toByteString()).setTypeUrl(AesGcmHkdfStreamingKeyManager.getKeyType()).setOutputPrefixType(OutputPrefixType.RAW).build();
    }

    private StreamingAeadKeyTemplates() {
    }
}

