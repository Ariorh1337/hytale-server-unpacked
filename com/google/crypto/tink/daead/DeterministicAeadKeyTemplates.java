/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.daead;

import com.google.crypto.tink.daead.AesSivKeyManager;
import com.google.crypto.tink.proto.AesSivKeyFormat;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.OutputPrefixType;

@Deprecated
public final class DeterministicAeadKeyTemplates {
    public static final KeyTemplate AES256_SIV = DeterministicAeadKeyTemplates.createAesSivKeyTemplate(64);

    public static KeyTemplate createAesSivKeyTemplate(int keySize) {
        AesSivKeyFormat format = AesSivKeyFormat.newBuilder().setKeySize(keySize).build();
        return KeyTemplate.newBuilder().setValue(format.toByteString()).setTypeUrl(AesSivKeyManager.getKeyType()).setOutputPrefixType(OutputPrefixType.TINK).build();
    }

    private DeterministicAeadKeyTemplates() {
    }
}

