/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.aead.subtle;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.aead.AesGcmSivKey;
import com.google.crypto.tink.aead.AesGcmSivParameters;
import com.google.crypto.tink.annotations.Alpha;
import com.google.crypto.tink.subtle.EngineFactory;
import com.google.crypto.tink.util.SecretBytes;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;
import javax.crypto.Cipher;

@Alpha
public final class AesGcmSiv
implements Aead {
    private static final ThreadLocal<Cipher> localAesGcmSivCipher = new ThreadLocal<Cipher>(){

        @Override
        @Nullable
        protected Cipher initialValue() {
            try {
                Cipher cipher = EngineFactory.CIPHER.getInstance("AES/GCM-SIV/NoPadding");
                if (!com.google.crypto.tink.aead.internal.AesGcmSiv.isAesGcmSivCipher(cipher)) {
                    return null;
                }
                return cipher;
            }
            catch (GeneralSecurityException ex) {
                throw new IllegalStateException(ex);
            }
        }
    };
    private final Aead aead;

    private static Cipher cipherSupplier() throws GeneralSecurityException {
        try {
            Cipher cipher = localAesGcmSivCipher.get();
            if (cipher == null) {
                throw new GeneralSecurityException("AES GCM SIV cipher is invalid.");
            }
            return cipher;
        }
        catch (IllegalStateException ex) {
            throw new GeneralSecurityException("AES GCM SIV cipher is not available or is invalid.", ex);
        }
    }

    @AccessesPartialKey
    public static Aead create(AesGcmSivKey key) throws GeneralSecurityException {
        return com.google.crypto.tink.aead.internal.AesGcmSiv.create(key, AesGcmSiv::cipherSupplier);
    }

    @AccessesPartialKey
    private static Aead createFromRawKey(byte[] key) throws GeneralSecurityException {
        return com.google.crypto.tink.aead.internal.AesGcmSiv.create(AesGcmSivKey.builder().setKeyBytes(SecretBytes.copyFrom(key, InsecureSecretKeyAccess.get())).setParameters(AesGcmSivParameters.builder().setKeySizeBytes(key.length).setVariant(AesGcmSivParameters.Variant.NO_PREFIX).build()).build(), AesGcmSiv::cipherSupplier);
    }

    private AesGcmSiv(Aead aead) {
        this.aead = aead;
    }

    public AesGcmSiv(byte[] key) throws GeneralSecurityException {
        this(AesGcmSiv.createFromRawKey(key));
    }

    @Override
    public byte[] encrypt(byte[] plaintext, byte[] associatedData) throws GeneralSecurityException {
        return this.aead.encrypt(plaintext, associatedData);
    }

    @Override
    public byte[] decrypt(byte[] ciphertext, byte[] associatedData) throws GeneralSecurityException {
        return this.aead.decrypt(ciphertext, associatedData);
    }
}

