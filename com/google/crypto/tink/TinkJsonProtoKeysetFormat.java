/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.JsonKeysetWriter;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.SecretKeyAccess;
import com.google.crypto.tink.internal.Util;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

public final class TinkJsonProtoKeysetFormat {
    public static KeysetHandle parseKeyset(String serializedKeyset, SecretKeyAccess access) throws GeneralSecurityException {
        if (access == null) {
            throw new NullPointerException("SecretKeyAccess cannot be null");
        }
        try {
            return CleartextKeysetHandle.read(JsonKeysetReader.withString(serializedKeyset));
        }
        catch (IOException e) {
            throw new GeneralSecurityException("Parse keyset failed");
        }
    }

    public static String serializeKeyset(KeysetHandle keysetHandle, SecretKeyAccess access) throws GeneralSecurityException {
        if (access == null) {
            throw new NullPointerException("SecretKeyAccess cannot be null");
        }
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CleartextKeysetHandle.write(keysetHandle, JsonKeysetWriter.withOutputStream(outputStream));
            return new String(outputStream.toByteArray(), Util.UTF_8);
        }
        catch (IOException e) {
            throw new GeneralSecurityException("Serialize keyset failed");
        }
    }

    public static KeysetHandle parseKeysetWithoutSecret(String serializedKeyset) throws GeneralSecurityException {
        try {
            return KeysetHandle.readNoSecret(JsonKeysetReader.withString(serializedKeyset));
        }
        catch (IOException e) {
            throw new GeneralSecurityException("Parse keyset failed");
        }
    }

    public static String serializeKeysetWithoutSecret(KeysetHandle keysetHandle) throws GeneralSecurityException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            keysetHandle.writeNoSecret(JsonKeysetWriter.withOutputStream(outputStream));
            return new String(outputStream.toByteArray(), Util.UTF_8);
        }
        catch (IOException e) {
            throw new GeneralSecurityException("Serialize keyset failed");
        }
    }

    public static KeysetHandle parseEncryptedKeyset(String serializedEncryptedKeyset, Aead keysetEncryptionAead, byte[] associatedData) throws GeneralSecurityException {
        try {
            return KeysetHandle.readWithAssociatedData(JsonKeysetReader.withString(serializedEncryptedKeyset), keysetEncryptionAead, associatedData);
        }
        catch (IOException e) {
            throw new GeneralSecurityException("Parse keyset failed");
        }
    }

    public static String serializeEncryptedKeyset(KeysetHandle keysetHandle, Aead keysetEncryptionAead, byte[] associatedData) throws GeneralSecurityException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            keysetHandle.writeWithAssociatedData(JsonKeysetWriter.withOutputStream(outputStream), keysetEncryptionAead, associatedData);
            return new String(outputStream.toByteArray(), Util.UTF_8);
        }
        catch (IOException e) {
            throw new GeneralSecurityException("Serialize keyset failed");
        }
    }

    private TinkJsonProtoKeysetFormat() {
    }
}

