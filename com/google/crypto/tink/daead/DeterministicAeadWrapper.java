/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.daead;

import com.google.crypto.tink.DeterministicAead;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.KeyStatus;
import com.google.crypto.tink.daead.DeterministicAeadKey;
import com.google.crypto.tink.daead.internal.LegacyFullDeterministicAead;
import com.google.crypto.tink.internal.KeysetHandleInterface;
import com.google.crypto.tink.internal.LegacyProtoKey;
import com.google.crypto.tink.internal.MonitoringAnnotations;
import com.google.crypto.tink.internal.MonitoringClient;
import com.google.crypto.tink.internal.MonitoringUtil;
import com.google.crypto.tink.internal.MutableMonitoringRegistry;
import com.google.crypto.tink.internal.MutablePrimitiveRegistry;
import com.google.crypto.tink.internal.PrefixMap;
import com.google.crypto.tink.internal.PrimitiveConstructor;
import com.google.crypto.tink.internal.PrimitiveRegistry;
import com.google.crypto.tink.internal.PrimitiveWrapper;
import com.google.crypto.tink.util.Bytes;
import java.security.GeneralSecurityException;

public class DeterministicAeadWrapper
implements PrimitiveWrapper<DeterministicAead, DeterministicAead> {
    private static final DeterministicAeadWrapper WRAPPER = new DeterministicAeadWrapper();
    private static final PrimitiveConstructor<LegacyProtoKey, DeterministicAead> LEGACY_FULL_DAEAD_PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(LegacyFullDeterministicAead::create, LegacyProtoKey.class, DeterministicAead.class);

    private static Bytes getOutputPrefix(Key key) throws GeneralSecurityException {
        if (key instanceof DeterministicAeadKey) {
            return ((DeterministicAeadKey)key).getOutputPrefix();
        }
        if (key instanceof LegacyProtoKey) {
            return ((LegacyProtoKey)key).getOutputPrefix();
        }
        throw new GeneralSecurityException("Cannot get output prefix for key of class " + key.getClass().getName() + " with parameters " + key.getParameters());
    }

    DeterministicAeadWrapper() {
    }

    @Override
    public DeterministicAead wrap(KeysetHandleInterface handle, MonitoringAnnotations annotations, PrimitiveWrapper.PrimitiveFactory<DeterministicAead> factory) throws GeneralSecurityException {
        MonitoringClient.Logger decLogger;
        MonitoringClient.Logger encLogger;
        PrefixMap.Builder<DeterministicAeadWithId> builder = new PrefixMap.Builder<DeterministicAeadWithId>();
        for (int i = 0; i < handle.size(); ++i) {
            KeysetHandleInterface.Entry entry = handle.getAt(i);
            if (!entry.getStatus().equals(KeyStatus.ENABLED)) continue;
            DeterministicAead deterministicAead = factory.create(entry);
            builder.put(DeterministicAeadWrapper.getOutputPrefix(entry.getKey()), new DeterministicAeadWithId(deterministicAead, entry.getId()));
        }
        if (!annotations.isEmpty()) {
            MonitoringClient client = MutableMonitoringRegistry.globalInstance().getMonitoringClient();
            encLogger = client.createLogger(handle, annotations, "daead", "encrypt");
            decLogger = client.createLogger(handle, annotations, "daead", "decrypt");
        } else {
            encLogger = MonitoringUtil.DO_NOTHING_LOGGER;
            decLogger = MonitoringUtil.DO_NOTHING_LOGGER;
        }
        return new WrappedDeterministicAead(new DeterministicAeadWithId(factory.create(handle.getPrimary()), handle.getPrimary().getId()), builder.build(), encLogger, decLogger);
    }

    @Override
    public Class<DeterministicAead> getPrimitiveClass() {
        return DeterministicAead.class;
    }

    @Override
    public Class<DeterministicAead> getInputPrimitiveClass() {
        return DeterministicAead.class;
    }

    public static void register() throws GeneralSecurityException {
        MutablePrimitiveRegistry.globalInstance().registerPrimitiveWrapper(WRAPPER);
        MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(LEGACY_FULL_DAEAD_PRIMITIVE_CONSTRUCTOR);
    }

    public static void registerToInternalPrimitiveRegistry(PrimitiveRegistry.Builder primitiveRegistryBuilder) throws GeneralSecurityException {
        primitiveRegistryBuilder.registerPrimitiveWrapper(WRAPPER);
    }

    private static class DeterministicAeadWithId {
        public final DeterministicAead daead;
        public final int id;

        public DeterministicAeadWithId(DeterministicAead daead, int id) {
            this.daead = daead;
            this.id = id;
        }
    }

    private static class WrappedDeterministicAead
    implements DeterministicAead {
        private final DeterministicAeadWithId primary;
        private final PrefixMap<DeterministicAeadWithId> allDaeads;
        private final MonitoringClient.Logger encLogger;
        private final MonitoringClient.Logger decLogger;

        public WrappedDeterministicAead(DeterministicAeadWithId primary, PrefixMap<DeterministicAeadWithId> allDaeads, MonitoringClient.Logger encLogger, MonitoringClient.Logger decLogger) {
            this.primary = primary;
            this.allDaeads = allDaeads;
            this.encLogger = encLogger;
            this.decLogger = decLogger;
        }

        @Override
        public byte[] encryptDeterministically(byte[] plaintext, byte[] associatedData) throws GeneralSecurityException {
            try {
                byte[] result = this.primary.daead.encryptDeterministically(plaintext, associatedData);
                this.encLogger.log(this.primary.id, plaintext.length);
                return result;
            }
            catch (GeneralSecurityException e) {
                this.encLogger.logFailure();
                throw e;
            }
        }

        @Override
        public byte[] decryptDeterministically(byte[] ciphertext, byte[] associatedData) throws GeneralSecurityException {
            for (DeterministicAeadWithId aeadWithId : this.allDaeads.getAllWithMatchingPrefix(ciphertext)) {
                try {
                    byte[] result = aeadWithId.daead.decryptDeterministically(ciphertext, associatedData);
                    this.decLogger.log(aeadWithId.id, ciphertext.length);
                    return result;
                }
                catch (GeneralSecurityException generalSecurityException) {
                }
            }
            this.decLogger.logFailure();
            throw new GeneralSecurityException("decryption failed");
        }
    }
}

