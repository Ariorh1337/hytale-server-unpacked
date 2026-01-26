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

public final class AesCtr
extends GeneratedFile {
    static final Descriptors.Descriptor internal_static_google_crypto_tink_AesCtrParams_descriptor;
    static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_AesCtrParams_fieldAccessorTable;
    static final Descriptors.Descriptor internal_static_google_crypto_tink_AesCtrKeyFormat_descriptor;
    static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_AesCtrKeyFormat_fieldAccessorTable;
    static final Descriptors.Descriptor internal_static_google_crypto_tink_AesCtrKey_descriptor;
    static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_AesCtrKey_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;

    private AesCtr() {
    }

    public static void registerAllExtensions(ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(ExtensionRegistry registry) {
        AesCtr.registerAllExtensions((ExtensionRegistryLite)registry);
    }

    public static Descriptors.FileDescriptor getDescriptor() {
        return descriptor;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", AesCtr.class.getName());
        String[] descriptorData = new String[]{"\n\u0013proto/aes_ctr.proto\u0012\u0012google.crypto.tink\"\u001f\n\fAesCtrParams\u0012\u000f\n\u0007iv_size\u0018\u0001 \u0001(\r\"U\n\u000fAesCtrKeyFormat\u00120\n\u0006params\u0018\u0001 \u0001(\u000b2 .google.crypto.tink.AesCtrParams\u0012\u0010\n\bkey_size\u0018\u0002 \u0001(\r\"a\n\tAesCtrKey\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u00120\n\u0006params\u0018\u0002 \u0001(\u000b2 .google.crypto.tink.AesCtrParams\u0012\u0011\n\tkey_value\u0018\u0003 \u0001(\fBZ\n\u001ccom.google.crypto.tink.protoP\u0001Z8github.com/tink-crypto/tink-go/v2/proto/aes_ctr_go_protob\u0006proto3"};
        descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
        internal_static_google_crypto_tink_AesCtrParams_descriptor = AesCtr.getDescriptor().getMessageTypes().get(0);
        internal_static_google_crypto_tink_AesCtrParams_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(internal_static_google_crypto_tink_AesCtrParams_descriptor, new String[]{"IvSize"});
        internal_static_google_crypto_tink_AesCtrKeyFormat_descriptor = AesCtr.getDescriptor().getMessageTypes().get(1);
        internal_static_google_crypto_tink_AesCtrKeyFormat_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(internal_static_google_crypto_tink_AesCtrKeyFormat_descriptor, new String[]{"Params", "KeySize"});
        internal_static_google_crypto_tink_AesCtrKey_descriptor = AesCtr.getDescriptor().getMessageTypes().get(2);
        internal_static_google_crypto_tink_AesCtrKey_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(internal_static_google_crypto_tink_AesCtrKey_descriptor, new String[]{"Version", "Params", "KeyValue"});
        descriptor.resolveAllFeaturesImmutable();
    }
}

