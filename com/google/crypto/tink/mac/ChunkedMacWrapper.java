/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.mac;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.KeyStatus;
import com.google.crypto.tink.internal.KeysetHandleInterface;
import com.google.crypto.tink.internal.LegacyProtoKey;
import com.google.crypto.tink.internal.MonitoringAnnotations;
import com.google.crypto.tink.internal.MutablePrimitiveRegistry;
import com.google.crypto.tink.internal.PrefixMap;
import com.google.crypto.tink.internal.PrimitiveRegistry;
import com.google.crypto.tink.internal.PrimitiveWrapper;
import com.google.crypto.tink.mac.ChunkedMac;
import com.google.crypto.tink.mac.ChunkedMacComputation;
import com.google.crypto.tink.mac.ChunkedMacVerification;
import com.google.crypto.tink.mac.MacKey;
import com.google.crypto.tink.util.Bytes;
import com.google.errorprone.annotations.Immutable;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class ChunkedMacWrapper
implements PrimitiveWrapper<ChunkedMac, ChunkedMac> {
    private static final ChunkedMacWrapper WRAPPER = new ChunkedMacWrapper();

    private static Bytes getOutputPrefix(Key key) throws GeneralSecurityException {
        if (key instanceof MacKey) {
            return ((MacKey)key).getOutputPrefix();
        }
        if (key instanceof LegacyProtoKey) {
            return ((LegacyProtoKey)key).getOutputPrefix();
        }
        throw new GeneralSecurityException("Cannot get output prefix for key of class " + key.getClass().getName() + " with parameters " + key.getParameters());
    }

    private ChunkedMacWrapper() {
    }

    @Override
    public ChunkedMac wrap(KeysetHandleInterface keysetHandle, MonitoringAnnotations annotations, PrimitiveWrapper.PrimitiveFactory<ChunkedMac> factory) throws GeneralSecurityException {
        KeysetHandleInterface.Entry primaryEntry = keysetHandle.getPrimary();
        if (primaryEntry == null) {
            throw new GeneralSecurityException("no primary in primitive set");
        }
        PrefixMap.Builder<ChunkedMac> allChunkedMacsBuilder = new PrefixMap.Builder<ChunkedMac>();
        for (int i = 0; i < keysetHandle.size(); ++i) {
            KeysetHandleInterface.Entry entry = keysetHandle.getAt(i);
            if (!entry.getStatus().equals(KeyStatus.ENABLED)) continue;
            ChunkedMac chunkedMac = factory.create(entry);
            allChunkedMacsBuilder.put(ChunkedMacWrapper.getOutputPrefix(entry.getKey()), chunkedMac);
        }
        ChunkedMac primaryChunkedMac = factory.create(primaryEntry);
        return new WrappedChunkedMac(allChunkedMacsBuilder.build(), primaryChunkedMac);
    }

    @Override
    public Class<ChunkedMac> getPrimitiveClass() {
        return ChunkedMac.class;
    }

    @Override
    public Class<ChunkedMac> getInputPrimitiveClass() {
        return ChunkedMac.class;
    }

    static void register() throws GeneralSecurityException {
        MutablePrimitiveRegistry.globalInstance().registerPrimitiveWrapper(WRAPPER);
    }

    public static void registerToInternalPrimitiveRegistry(PrimitiveRegistry.Builder primitiveRegistryBuilder) throws GeneralSecurityException {
        primitiveRegistryBuilder.registerPrimitiveWrapper(WRAPPER);
    }

    @Immutable
    private static class WrappedChunkedMac
    implements ChunkedMac {
        private final PrefixMap<ChunkedMac> allChunkedMacs;
        private final ChunkedMac primaryChunkedMac;

        private WrappedChunkedMac(PrefixMap<ChunkedMac> allChunkedMacs, ChunkedMac primaryChunkedMac) {
            this.allChunkedMacs = allChunkedMacs;
            this.primaryChunkedMac = primaryChunkedMac;
        }

        @Override
        public ChunkedMacComputation createComputation() throws GeneralSecurityException {
            return this.primaryChunkedMac.createComputation();
        }

        @Override
        public ChunkedMacVerification createVerification(byte[] tag) throws GeneralSecurityException {
            ArrayList<ChunkedMacVerification> allVerifications = new ArrayList<ChunkedMacVerification>();
            for (ChunkedMac mac : this.allChunkedMacs.getAllWithMatchingPrefix(tag)) {
                allVerifications.add(mac.createVerification(tag));
            }
            return new WrappedChunkedMacVerification(allVerifications);
        }
    }

    private static class WrappedChunkedMacVerification
    implements ChunkedMacVerification {
        private final List<ChunkedMacVerification> verifications;

        private WrappedChunkedMacVerification(List<ChunkedMacVerification> verificationEntries) {
            this.verifications = verificationEntries;
        }

        @Override
        public void update(ByteBuffer data) throws GeneralSecurityException {
            ByteBuffer clonedData = data.duplicate();
            clonedData.mark();
            for (ChunkedMacVerification entry : this.verifications) {
                clonedData.reset();
                entry.update(clonedData);
            }
            data.position(data.limit());
        }

        @Override
        public void verifyMac() throws GeneralSecurityException {
            GeneralSecurityException errorSink = new GeneralSecurityException("MAC verification failed for all suitable keys in keyset");
            for (ChunkedMacVerification entry : this.verifications) {
                try {
                    entry.verifyMac();
                    return;
                }
                catch (GeneralSecurityException e) {
                    errorSink.addSuppressed(e);
                }
            }
            throw errorSink;
        }
    }
}

