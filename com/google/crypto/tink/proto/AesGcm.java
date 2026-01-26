/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedFile;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.RuntimeVersion;

public final class AesGcm
extends GeneratedFile {
    static final Descriptors.Descriptor internal_static_google_crypto_tink_AesGcmKeyFormat_descriptor;
    static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_AesGcmKeyFormat_fieldAccessorTable;
    static final Descriptors.Descriptor internal_static_google_crypto_tink_AesGcmKey_descriptor;
    static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_AesGcmKey_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;

    private AesGcm() {
    }

    public static void registerAllExtensions(ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(ExtensionRegistry registry) {
        AesGcm.registerAllExtensions((ExtensionRegistryLite)registry);
    }

    public static Descriptors.FileDescriptor getDescriptor() {
        return descriptor;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", AesGcm.class.getName());
        String[] descriptorData = new String[]{"\n\u0013proto/aes_gcm.proto\u0012\u0012google.crypto.tink\"4\n\u000fAesGcmKeyFormat\u0012\u0010\n\bkey_size\u0018\u0002 \u0001(\r\u0012\u000f\n\u0007version\u0018\u0003 \u0001(\r\"/\n\tAesGcmKey\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u0012\u0011\n\tkey_value\u0018\u0003 \u0001(\fBc\n\u001ccom.google.crypto.tink.protoP\u0001Z8github.com/tink-crypto/tink-go/v2/proto/aes_gcm_go_proto\u00a2\u0002\u0006TINKPBb\u0006proto3"};
        descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
        internal_static_google_crypto_tink_AesGcmKeyFormat_descriptor = AesGcm.getDescriptor().getMessageTypes().get(0);
        internal_static_google_crypto_tink_AesGcmKeyFormat_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(internal_static_google_crypto_tink_AesGcmKeyFormat_descriptor, new String[]{"KeySize", "Version"});
        internal_static_google_crypto_tink_AesGcmKey_descriptor = AesGcm.getDescriptor().getMessageTypes().get(1);
        internal_static_google_crypto_tink_AesGcmKey_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(internal_static_google_crypto_tink_AesGcmKey_descriptor, new String[]{"Version", "KeyValue"});
        descriptor.resolveAllFeaturesImmutable();
    }
}

