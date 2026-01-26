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

public final class Xchacha20Poly1305
extends GeneratedFile {
    static final Descriptors.Descriptor internal_static_google_crypto_tink_XChaCha20Poly1305KeyFormat_descriptor;
    static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_XChaCha20Poly1305KeyFormat_fieldAccessorTable;
    static final Descriptors.Descriptor internal_static_google_crypto_tink_XChaCha20Poly1305Key_descriptor;
    static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_XChaCha20Poly1305Key_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;

    private Xchacha20Poly1305() {
    }

    public static void registerAllExtensions(ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(ExtensionRegistry registry) {
        Xchacha20Poly1305.registerAllExtensions((ExtensionRegistryLite)registry);
    }

    public static Descriptors.FileDescriptor getDescriptor() {
        return descriptor;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", Xchacha20Poly1305.class.getName());
        String[] descriptorData = new String[]{"\n\u001eproto/xchacha20_poly1305.proto\u0012\u0012google.crypto.tink\"-\n\u001aXChaCha20Poly1305KeyFormat\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\":\n\u0014XChaCha20Poly1305Key\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u0012\u0011\n\tkey_value\u0018\u0003 \u0001(\fBe\n\u001ccom.google.crypto.tink.protoP\u0001ZCgithub.com/tink-crypto/tink-go/v2/proto/xchacha20_poly1305_go_protob\u0006proto3"};
        descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
        internal_static_google_crypto_tink_XChaCha20Poly1305KeyFormat_descriptor = Xchacha20Poly1305.getDescriptor().getMessageTypes().get(0);
        internal_static_google_crypto_tink_XChaCha20Poly1305KeyFormat_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(internal_static_google_crypto_tink_XChaCha20Poly1305KeyFormat_descriptor, new String[]{"Version"});
        internal_static_google_crypto_tink_XChaCha20Poly1305Key_descriptor = Xchacha20Poly1305.getDescriptor().getMessageTypes().get(1);
        internal_static_google_crypto_tink_XChaCha20Poly1305Key_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(internal_static_google_crypto_tink_XChaCha20Poly1305Key_descriptor, new String[]{"Version", "KeyValue"});
        descriptor.resolveAllFeaturesImmutable();
    }
}

