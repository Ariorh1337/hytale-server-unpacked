/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.BinaryKeysetReader;
import com.google.crypto.tink.BinaryKeysetWriter;
import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.SecretKeyAccess;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

public final class TinkProtoKeysetFormat {
    public static KeysetHandle parseKeyset(byte[] serializedKeyset, SecretKeyAccess access) throws GeneralSecurityException {
        if (access == null) {
            throw new NullPointerException("SecretKeyAccess cannot be null");
        }
        try {
            return CleartextKeysetHandle.read(BinaryKeysetReader.withBytes(serializedKeyset));
        }
        catch (IOException e) {
            throw new GeneralSecurityException("Parse keyset failed");
        }
    }

    public static byte[] serializeKeyset(KeysetHandle keysetHandle, SecretKeyAccess access) throws GeneralSecurityException {
        if (access == null) {
            throw new NullPointerException("SecretKeyAccess cannot be null");
        }
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CleartextKeysetHandle.write(keysetHandle, BinaryKeysetWriter.withOutputStream(outputStream));
            return outputStream.toByteArray();
        }
        catch (IOException e) {
            throw new GeneralSecurityException("Serialize keyset failed");
        }
    }

    public static KeysetHandle parseKeysetWithoutSecret(byte[] serializedKeyset) throws GeneralSecurityException {
        return KeysetHandle.readNoSecret(serializedKeyset);
    }

    public static byte[] serializeKeysetWithoutSecret(KeysetHandle keysetHandle) throws GeneralSecurityException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            keysetHandle.writeNoSecret(BinaryKeysetWriter.withOutputStream(outputStream));
            return outputStream.toByteArray();
        }
        catch (IOException e) {
            throw new GeneralSecurityException("Serialize keyset failed");
        }
    }

    public static KeysetHandle parseEncryptedKeyset(byte[] serializedEncryptedKeyset, Aead keysetEncryptionAead, byte[] associatedData) throws GeneralSecurityException {
        try {
            return KeysetHandle.readWithAssociatedData(BinaryKeysetReader.withBytes(serializedEncryptedKeyset), keysetEncryptionAead, associatedData);
        }
        catch (IOException e) {
            throw new GeneralSecurityException("Parse keyset failed");
        }
    }

    public static byte[] serializeEncryptedKeyset(KeysetHandle keysetHandle, Aead keysetEncryptionAead, byte[] associatedData) throws GeneralSecurityException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            keysetHandle.writeWithAssociatedData(BinaryKeysetWriter.withOutputStream(outputStream), keysetEncryptionAead, associatedData);
            return outputStream.toByteArray();
        }
        catch (IOException e) {
            throw new GeneralSecurityException("Serialize keyset failed");
        }
    }

    private TinkProtoKeysetFormat() {
    }
}

