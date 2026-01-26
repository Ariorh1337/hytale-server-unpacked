/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.keyderivation.internal;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.KeyStatus;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.internal.KeysetHandleInterface;
import com.google.crypto.tink.internal.MonitoringAnnotations;
import com.google.crypto.tink.internal.MutablePrimitiveRegistry;
import com.google.crypto.tink.internal.PrimitiveRegistry;
import com.google.crypto.tink.internal.PrimitiveWrapper;
import com.google.crypto.tink.keyderivation.KeysetDeriver;
import com.google.crypto.tink.keyderivation.internal.KeyDeriver;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public final class KeysetDeriverWrapper
implements PrimitiveWrapper<KeyDeriver, KeysetDeriver> {
    private static final KeysetDeriverWrapper WRAPPER = new KeysetDeriverWrapper();

    private static void validate(KeysetHandleInterface keysetHandle) throws GeneralSecurityException {
        if (keysetHandle.getPrimary() == null) {
            throw new GeneralSecurityException("Primitive set has no primary.");
        }
    }

    KeysetDeriverWrapper() {
    }

    @Override
    public KeysetDeriver wrap(KeysetHandleInterface keysetHandle, MonitoringAnnotations annotations, PrimitiveWrapper.PrimitiveFactory<KeyDeriver> factory) throws GeneralSecurityException {
        KeysetDeriverWrapper.validate(keysetHandle);
        ArrayList<DeriverWithId> derivers = new ArrayList<DeriverWithId>(keysetHandle.size());
        for (int i = 0; i < keysetHandle.size(); ++i) {
            KeysetHandleInterface.Entry entry = keysetHandle.getAt(i);
            if (!entry.getStatus().equals(KeyStatus.ENABLED)) continue;
            derivers.add(new DeriverWithId(factory.create(entry), entry.getId(), entry.isPrimary()));
        }
        return new WrappedKeysetDeriver(derivers);
    }

    @Override
    public Class<KeysetDeriver> getPrimitiveClass() {
        return KeysetDeriver.class;
    }

    @Override
    public Class<KeyDeriver> getInputPrimitiveClass() {
        return KeyDeriver.class;
    }

    public static void register() throws GeneralSecurityException {
        MutablePrimitiveRegistry.globalInstance().registerPrimitiveWrapper(WRAPPER);
    }

    public static void registerToInternalPrimitiveRegistry(PrimitiveRegistry.Builder primitiveRegistryBuilder) throws GeneralSecurityException {
        primitiveRegistryBuilder.registerPrimitiveWrapper(WRAPPER);
    }

    private static class DeriverWithId {
        final KeyDeriver deriver;
        final int id;
        final boolean isPrimary;

        DeriverWithId(KeyDeriver deriver, int id, boolean isPrimary) {
            this.deriver = deriver;
            this.id = id;
            this.isPrimary = isPrimary;
        }
    }

    @Immutable
    private static class WrappedKeysetDeriver
    implements KeysetDeriver {
        private final List<DeriverWithId> derivers;

        private WrappedKeysetDeriver(List<DeriverWithId> derivers) {
            this.derivers = derivers;
        }

        private static KeysetHandle.Builder.Entry deriveAndGetEntry(byte[] salt, DeriverWithId deriverWithId) throws GeneralSecurityException {
            if (deriverWithId.deriver == null) {
                throw new GeneralSecurityException("Primitive set has non-full primitives -- this is probably a bug");
            }
            Key key = deriverWithId.deriver.deriveKey(salt);
            KeysetHandle.Builder.Entry result = KeysetHandle.importKey(key);
            result.withFixedId(deriverWithId.id);
            if (deriverWithId.isPrimary) {
                result.makePrimary();
            }
            return result;
        }

        @Override
        public KeysetHandle deriveKeyset(byte[] salt) throws GeneralSecurityException {
            KeysetHandle.Builder builder = KeysetHandle.newBuilder();
            for (DeriverWithId deriverWithId : this.derivers) {
                builder.addEntry(WrappedKeysetDeriver.deriveAndGetEntry(salt, deriverWithId));
            }
            return builder.build();
        }
    }
}

