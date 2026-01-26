/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink;

import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.Registry;
import com.google.crypto.tink.internal.KeyStatusTypeProtoConverter;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyStatusType;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.Keyset;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.tinkkey.KeyAccess;
import com.google.crypto.tink.tinkkey.KeyHandle;
import com.google.crypto.tink.tinkkey.SecretKeyAccess;
import com.google.crypto.tink.tinkkey.internal.ProtoKey;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.InlineMe;
import java.security.GeneralSecurityException;
import javax.annotation.concurrent.GuardedBy;

public final class KeysetManager {
    @GuardedBy(value="this")
    private final Keyset.Builder keysetBuilder;

    private KeysetManager(Keyset.Builder val) {
        this.keysetBuilder = val;
    }

    public static KeysetManager withKeysetHandle(KeysetHandle val) {
        return new KeysetManager(val.getKeyset().toBuilder());
    }

    public static KeysetManager withEmptyKeyset() {
        return new KeysetManager(Keyset.newBuilder());
    }

    public synchronized KeysetHandle getKeysetHandle() throws GeneralSecurityException {
        return KeysetHandle.fromKeyset(this.keysetBuilder.build());
    }

    @CanIgnoreReturnValue
    public synchronized KeysetManager rotate(KeyTemplate keyTemplate) throws GeneralSecurityException {
        this.addNewKey(keyTemplate, true);
        return this;
    }

    @CanIgnoreReturnValue
    public synchronized KeysetManager add(KeyTemplate keyTemplate) throws GeneralSecurityException {
        this.addNewKey(keyTemplate, false);
        return this;
    }

    @CanIgnoreReturnValue
    public synchronized KeysetManager add(com.google.crypto.tink.KeyTemplate keyTemplate) throws GeneralSecurityException {
        this.addNewKey(keyTemplate.getProtoMaybeThrow(), false);
        return this;
    }

    @Deprecated
    @CanIgnoreReturnValue
    public synchronized KeysetManager add(KeyHandle keyHandle) throws GeneralSecurityException {
        ProtoKey pkey;
        try {
            pkey = (ProtoKey)keyHandle.getKey(SecretKeyAccess.insecureSecretAccess());
        }
        catch (ClassCastException e) {
            throw new UnsupportedOperationException("KeyHandles which contain TinkKeys that are not ProtoKeys are not yet supported.", e);
        }
        if (this.keyIdExists(keyHandle.getId())) {
            throw new GeneralSecurityException("Trying to add a key with an ID already contained in the keyset.");
        }
        this.keysetBuilder.addKey(Keyset.Key.newBuilder().setKeyData(pkey.getProtoKey()).setKeyId(keyHandle.getId()).setStatus(KeyStatusTypeProtoConverter.toProto(keyHandle.getStatus())).setOutputPrefixType(com.google.crypto.tink.KeyTemplate.toProto(pkey.getOutputPrefixType())).build());
        return this;
    }

    @Deprecated
    @CanIgnoreReturnValue
    public synchronized KeysetManager add(KeyHandle keyHandle, KeyAccess access) throws GeneralSecurityException {
        return this.add(keyHandle);
    }

    @CanIgnoreReturnValue
    public synchronized int addNewKey(KeyTemplate keyTemplate, boolean asPrimary) throws GeneralSecurityException {
        Keyset.Key key = this.newKey(keyTemplate);
        this.keysetBuilder.addKey(key);
        if (asPrimary) {
            this.keysetBuilder.setPrimaryKeyId(key.getKeyId());
        }
        return key.getKeyId();
    }

    @CanIgnoreReturnValue
    public synchronized KeysetManager setPrimary(int keyId) throws GeneralSecurityException {
        for (int i = 0; i < this.keysetBuilder.getKeyCount(); ++i) {
            Keyset.Key key = this.keysetBuilder.getKey(i);
            if (key.getKeyId() != keyId) continue;
            if (!key.getStatus().equals(KeyStatusType.ENABLED)) {
                throw new GeneralSecurityException("cannot set key as primary because it's not enabled: " + keyId);
            }
            this.keysetBuilder.setPrimaryKeyId(keyId);
            return this;
        }
        throw new GeneralSecurityException("key not found: " + keyId);
    }

    @InlineMe(replacement="this.setPrimary(keyId)")
    @CanIgnoreReturnValue
    public synchronized KeysetManager promote(int keyId) throws GeneralSecurityException {
        return this.setPrimary(keyId);
    }

    @CanIgnoreReturnValue
    public synchronized KeysetManager enable(int keyId) throws GeneralSecurityException {
        for (int i = 0; i < this.keysetBuilder.getKeyCount(); ++i) {
            Keyset.Key key = this.keysetBuilder.getKey(i);
            if (key.getKeyId() != keyId) continue;
            if (key.getStatus() != KeyStatusType.ENABLED && key.getStatus() != KeyStatusType.DISABLED) {
                throw new GeneralSecurityException("cannot enable key with id " + keyId);
            }
            this.keysetBuilder.setKey(i, key.toBuilder().setStatus(KeyStatusType.ENABLED).build());
            return this;
        }
        throw new GeneralSecurityException("key not found: " + keyId);
    }

    @CanIgnoreReturnValue
    public synchronized KeysetManager disable(int keyId) throws GeneralSecurityException {
        if (keyId == this.keysetBuilder.getPrimaryKeyId()) {
            throw new GeneralSecurityException("cannot disable the primary key");
        }
        for (int i = 0; i < this.keysetBuilder.getKeyCount(); ++i) {
            Keyset.Key key = this.keysetBuilder.getKey(i);
            if (key.getKeyId() != keyId) continue;
            if (key.getStatus() != KeyStatusType.ENABLED && key.getStatus() != KeyStatusType.DISABLED) {
                throw new GeneralSecurityException("cannot disable key with id " + keyId);
            }
            this.keysetBuilder.setKey(i, key.toBuilder().setStatus(KeyStatusType.DISABLED).build());
            return this;
        }
        throw new GeneralSecurityException("key not found: " + keyId);
    }

    @CanIgnoreReturnValue
    public synchronized KeysetManager delete(int keyId) throws GeneralSecurityException {
        if (keyId == this.keysetBuilder.getPrimaryKeyId()) {
            throw new GeneralSecurityException("cannot delete the primary key");
        }
        for (int i = 0; i < this.keysetBuilder.getKeyCount(); ++i) {
            Keyset.Key key = this.keysetBuilder.getKey(i);
            if (key.getKeyId() != keyId) continue;
            this.keysetBuilder.removeKey(i);
            return this;
        }
        throw new GeneralSecurityException("key not found: " + keyId);
    }

    @CanIgnoreReturnValue
    public synchronized KeysetManager destroy(int keyId) throws GeneralSecurityException {
        if (keyId == this.keysetBuilder.getPrimaryKeyId()) {
            throw new GeneralSecurityException("cannot destroy the primary key");
        }
        for (int i = 0; i < this.keysetBuilder.getKeyCount(); ++i) {
            Keyset.Key key = this.keysetBuilder.getKey(i);
            if (key.getKeyId() != keyId) continue;
            if (key.getStatus() != KeyStatusType.ENABLED && key.getStatus() != KeyStatusType.DISABLED && key.getStatus() != KeyStatusType.DESTROYED) {
                throw new GeneralSecurityException("cannot destroy key with id " + keyId);
            }
            this.keysetBuilder.setKey(i, key.toBuilder().setStatus(KeyStatusType.DESTROYED).clearKeyData().build());
            return this;
        }
        throw new GeneralSecurityException("key not found: " + keyId);
    }

    private synchronized Keyset.Key newKey(KeyTemplate keyTemplate) throws GeneralSecurityException {
        return this.createKeysetKey(Registry.newKeyData(keyTemplate), keyTemplate.getOutputPrefixType());
    }

    private synchronized Keyset.Key createKeysetKey(KeyData keyData, OutputPrefixType outputPrefixType) throws GeneralSecurityException {
        int keyId = this.newKeyId();
        if (outputPrefixType == OutputPrefixType.UNKNOWN_PREFIX) {
            throw new GeneralSecurityException("unknown output prefix type");
        }
        return Keyset.Key.newBuilder().setKeyData(keyData).setKeyId(keyId).setStatus(KeyStatusType.ENABLED).setOutputPrefixType(outputPrefixType).build();
    }

    private synchronized boolean keyIdExists(int keyId) {
        for (Keyset.Key key : this.keysetBuilder.getKeyList()) {
            if (key.getKeyId() != keyId) continue;
            return true;
        }
        return false;
    }

    private synchronized int newKeyId() {
        int keyId = Util.randKeyId();
        while (this.keyIdExists(keyId)) {
            keyId = Util.randKeyId();
        }
        return keyId;
    }
}

