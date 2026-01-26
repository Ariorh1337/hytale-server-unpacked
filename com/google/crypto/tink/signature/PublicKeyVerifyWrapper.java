/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.signature;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.KeyStatus;
import com.google.crypto.tink.PublicKeyVerify;
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
import com.google.crypto.tink.signature.SignaturePublicKey;
import com.google.crypto.tink.signature.internal.LegacyFullVerify;
import com.google.crypto.tink.util.Bytes;
import java.security.GeneralSecurityException;

public class PublicKeyVerifyWrapper
implements PrimitiveWrapper<PublicKeyVerify, PublicKeyVerify> {
    private static final PublicKeyVerifyWrapper WRAPPER = new PublicKeyVerifyWrapper();
    private static final PrimitiveConstructor<LegacyProtoKey, PublicKeyVerify> LEGACY_PRIMITIVE_CONSTRUCTOR = PrimitiveConstructor.create(LegacyFullVerify::create, LegacyProtoKey.class, PublicKeyVerify.class);

    private static Bytes getOutputPrefix(Key key) throws GeneralSecurityException {
        if (key instanceof SignaturePublicKey) {
            return ((SignaturePublicKey)key).getOutputPrefix();
        }
        if (key instanceof LegacyProtoKey) {
            return ((LegacyProtoKey)key).getOutputPrefix();
        }
        throw new GeneralSecurityException("Cannot get output prefix for key of class " + key.getClass().getName() + " with parameters " + key.getParameters());
    }

    @Override
    public PublicKeyVerify wrap(KeysetHandleInterface keysetHandle, MonitoringAnnotations annotations, PrimitiveWrapper.PrimitiveFactory<PublicKeyVerify> factory) throws GeneralSecurityException {
        MonitoringClient.Logger logger;
        PrefixMap.Builder<PublicKeyVerifyWithId> builder = new PrefixMap.Builder<PublicKeyVerifyWithId>();
        for (int i = 0; i < keysetHandle.size(); ++i) {
            KeysetHandleInterface.Entry entry = keysetHandle.getAt(i);
            if (!entry.getStatus().equals(KeyStatus.ENABLED)) continue;
            PublicKeyVerify publicKeyVerify = factory.create(entry);
            builder.put(PublicKeyVerifyWrapper.getOutputPrefix(entry.getKey()), new PublicKeyVerifyWithId(publicKeyVerify, entry.getId()));
        }
        if (!annotations.isEmpty()) {
            MonitoringClient client = MutableMonitoringRegistry.globalInstance().getMonitoringClient();
            logger = client.createLogger(keysetHandle, annotations, "public_key_verify", "verify");
        } else {
            logger = MonitoringUtil.DO_NOTHING_LOGGER;
        }
        return new WrappedPublicKeyVerify(builder.build(), logger);
    }

    @Override
    public Class<PublicKeyVerify> getPrimitiveClass() {
        return PublicKeyVerify.class;
    }

    @Override
    public Class<PublicKeyVerify> getInputPrimitiveClass() {
        return PublicKeyVerify.class;
    }

    static void register() throws GeneralSecurityException {
        MutablePrimitiveRegistry.globalInstance().registerPrimitiveWrapper(WRAPPER);
        MutablePrimitiveRegistry.globalInstance().registerPrimitiveConstructor(LEGACY_PRIMITIVE_CONSTRUCTOR);
    }

    public static void registerToInternalPrimitiveRegistry(PrimitiveRegistry.Builder primitiveRegistryBuilder) throws GeneralSecurityException {
        primitiveRegistryBuilder.registerPrimitiveWrapper(WRAPPER);
    }

    private static class PublicKeyVerifyWithId {
        public final PublicKeyVerify publicKeyVerify;
        public final int id;

        public PublicKeyVerifyWithId(PublicKeyVerify publicKeyVerify, int id) {
            this.publicKeyVerify = publicKeyVerify;
            this.id = id;
        }
    }

    private static class WrappedPublicKeyVerify
    implements PublicKeyVerify {
        private final PrefixMap<PublicKeyVerifyWithId> allPublicKeyVerifys;
        private final MonitoringClient.Logger monitoringLogger;

        public WrappedPublicKeyVerify(PrefixMap<PublicKeyVerifyWithId> allPublicKeyVerifys, MonitoringClient.Logger monitoringLogger) {
            this.allPublicKeyVerifys = allPublicKeyVerifys;
            this.monitoringLogger = monitoringLogger;
        }

        @Override
        public void verify(byte[] signature, byte[] data) throws GeneralSecurityException {
            for (PublicKeyVerifyWithId publicKeyVerifyWithId : this.allPublicKeyVerifys.getAllWithMatchingPrefix(signature)) {
                try {
                    publicKeyVerifyWithId.publicKeyVerify.verify(signature, data);
                    this.monitoringLogger.log(publicKeyVerifyWithId.id, data.length);
                    return;
                }
                catch (GeneralSecurityException generalSecurityException) {
                }
            }
            this.monitoringLogger.logFailure();
            throw new GeneralSecurityException("invalid signature");
        }
    }
}

