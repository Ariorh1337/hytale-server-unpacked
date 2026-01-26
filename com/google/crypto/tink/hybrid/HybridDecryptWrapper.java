/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.hybrid;

import com.google.crypto.tink.HybridDecrypt;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.KeyStatus;
import com.google.crypto.tink.hybrid.HybridPrivateKey;
import com.google.crypto.tink.hybrid.internal.LegacyFullHybridDecrypt;
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

public class HybridDecryptWrapper
implements PrimitiveWrapper<HybridDecrypt, HybridDecrypt> {
    private static final HybridDecryptWrapper WRAPPER = new HybridDecryptWrapper();
    private static final PrimitiveConstructor<LegacyProtoKey, HybridDecrypt> LEGACY_PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(LegacyFullHybridDecrypt::create, LegacyProtoKey.class, HybridDecrypt.class);

    private static Bytes getOutputPrefix(Key key) throws GeneralSecurityException {
        if (key instanceof HybridPrivateKey) {
            return ((HybridPrivateKey)key).getOutputPrefix();
        }
        if (key instanceof LegacyProtoKey) {
            return ((LegacyProtoKey)key).getOutputPrefix();
        }
        throw new GeneralSecurityException("Cannot get output prefix for key of class " + key.getClass().getName() + " with parameters " + key.getParameters());
    }

    HybridDecryptWrapper() {
    }

    @Override
    public HybridDecrypt wrap(KeysetHandleInterface keysetHandle, MonitoringAnnotations annotations, PrimitiveWrapper.PrimitiveFactory<HybridDecrypt> factory) throws GeneralSecurityException {
        MonitoringClient.Logger decLogger;
        PrefixMap.Builder<HybridDecryptWithId> builder = new PrefixMap.Builder<HybridDecryptWithId>();
        for (int i = 0; i < keysetHandle.size(); ++i) {
            KeysetHandleInterface.Entry entry = keysetHandle.getAt(i);
            if (!entry.getStatus().equals(KeyStatus.ENABLED)) continue;
            HybridDecrypt hybridDecrypt = factory.create(entry);
            builder.put(HybridDecryptWrapper.getOutputPrefix(entry.getKey()), new HybridDecryptWithId(hybridDecrypt, entry.getId()));
        }
        if (!annotations.isEmpty()) {
            MonitoringClient client = MutableMonitoringRegistry.globalInstance().getMonitoringClient();
            decLogger = client.createLogger(keysetHandle, annotations, "hybrid_decrypt", "decrypt");
        } else {
            decLogger = MonitoringUtil.DO_NOTHING_LOGGER;
        }
        return new WrappedHybridDecrypt(builder.build(), decLogger);
    }

    @Override
    public Class<HybridDecrypt> getPrimitiveClass() {
        return HybridDecrypt.class;
    }

    @Override
    public Class<HybridDecrypt> getInputPrimitiveClass() {
        return HybridDecrypt.class;
    }

    public static void register() throws GeneralSecurityException {
        MutablePrimitiveRegistry.globalInstance().registerPrimitiveWrapper(WRAPPER);
        MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(LEGACY_PRIMITIVE_CONSTRUCTOR);
    }

    public static void registerToInternalPrimitiveRegistry(PrimitiveRegistry.Builder primitiveRegistryBuilder) throws GeneralSecurityException {
        primitiveRegistryBuilder.registerPrimitiveWrapper(WRAPPER);
    }

    private static class HybridDecryptWithId {
        public final HybridDecrypt hybridDecrypt;
        public final int id;

        public HybridDecryptWithId(HybridDecrypt hybridDecrypt, int id) {
            this.hybridDecrypt = hybridDecrypt;
            this.id = id;
        }
    }

    private static class WrappedHybridDecrypt
    implements HybridDecrypt {
        private final PrefixMap<HybridDecryptWithId> allHybridDecrypts;
        private final MonitoringClient.Logger decLogger;

        public WrappedHybridDecrypt(PrefixMap<HybridDecryptWithId> allHybridDecrypts, MonitoringClient.Logger decLogger) {
            this.allHybridDecrypts = allHybridDecrypts;
            this.decLogger = decLogger;
        }

        @Override
        public byte[] decrypt(byte[] ciphertext, byte[] contextInfo) throws GeneralSecurityException {
            for (HybridDecryptWithId hybridDecryptWithId : this.allHybridDecrypts.getAllWithMatchingPrefix(ciphertext)) {
                try {
                    byte[] result = hybridDecryptWithId.hybridDecrypt.decrypt(ciphertext, contextInfo);
                    this.decLogger.log(hybridDecryptWithId.id, ciphertext.length);
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

