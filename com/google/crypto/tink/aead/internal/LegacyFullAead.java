/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.aead.internal;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.KeyManager;
import com.google.crypto.tink.internal.KeyManagerRegistry;
import com.google.crypto.tink.internal.LegacyProtoKey;
import com.google.crypto.tink.internal.OutputPrefixUtil;
import com.google.crypto.tink.internal.ProtoKeySerialization;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.subtle.Bytes;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class LegacyFullAead
implements Aead {
    private final Aead rawAead;
    private final byte[] identifier;

    public static Aead create(LegacyProtoKey key) throws GeneralSecurityException {
        byte[] identifier;
        ProtoKeySerialization protoKeySerialization = key.getSerialization(InsecureSecretKeyAccess.get());
        KeyManager<Aead> manager = KeyManagerRegistry.globalInstance().getKeyManager(protoKeySerialization.getTypeUrl(), Aead.class);
        Aead rawPrimitive = manager.getPrimitive(protoKeySerialization.getValue());
        OutputPrefixType outputPrefixType = protoKeySerialization.getOutputPrefixType();
        switch (outputPrefixType) {
            case RAW: {
                identifier = OutputPrefixUtil.EMPTY_PREFIX.toByteArray();
                break;
            }
            case LEGACY: 
            case CRUNCHY: {
                identifier = OutputPrefixUtil.getLegacyOutputPrefix(key.getIdRequirementOrNull()).toByteArray();
                break;
            }
            case TINK: {
                identifier = OutputPrefixUtil.getTinkOutputPrefix(key.getIdRequirementOrNull()).toByteArray();
                break;
            }
            default: {
                throw new GeneralSecurityException("unknown output prefix type " + outputPrefixType);
            }
        }
        return new LegacyFullAead(rawPrimitive, identifier);
    }

    public static Aead create(Aead rawAead, com.google.crypto.tink.util.Bytes outputPrefix) {
        return new LegacyFullAead(rawAead, outputPrefix.toByteArray());
    }

    private LegacyFullAead(Aead rawAead, byte[] identifier) {
        this.rawAead = rawAead;
        if (identifier.length != 0 && identifier.length != 5) {
            throw new IllegalArgumentException("identifier has an invalid length");
        }
        this.identifier = identifier;
    }

    @Override
    public byte[] encrypt(byte[] plaintext, byte[] associatedData) throws GeneralSecurityException {
        if (this.identifier.length == 0) {
            return this.rawAead.encrypt(plaintext, associatedData);
        }
        return Bytes.concat(this.identifier, this.rawAead.encrypt(plaintext, associatedData));
    }

    @Override
    public byte[] decrypt(byte[] ciphertext, byte[] associatedData) throws GeneralSecurityException {
        if (this.identifier.length == 0) {
            return this.rawAead.decrypt(ciphertext, associatedData);
        }
        if (!Util.isPrefix(this.identifier, ciphertext)) {
            throw new GeneralSecurityException("wrong prefix");
        }
        return this.rawAead.decrypt(Arrays.copyOfRange(ciphertext, 5, ciphertext.length), associatedData);
    }
}

