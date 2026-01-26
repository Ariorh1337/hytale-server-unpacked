/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.Common;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedFile;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.RuntimeVersion;

public final class Ecdsa
extends GeneratedFile {
    static final Descriptors.Descriptor internal_static_google_crypto_tink_EcdsaParams_descriptor;
    static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_EcdsaParams_fieldAccessorTable;
    static final Descriptors.Descriptor internal_static_google_crypto_tink_EcdsaPublicKey_descriptor;
    static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_EcdsaPublicKey_fieldAccessorTable;
    static final Descriptors.Descriptor internal_static_google_crypto_tink_EcdsaPrivateKey_descriptor;
    static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_EcdsaPrivateKey_fieldAccessorTable;
    static final Descriptors.Descriptor internal_static_google_crypto_tink_EcdsaKeyFormat_descriptor;
    static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_EcdsaKeyFormat_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;

    private Ecdsa() {
    }

    public static void registerAllExtensions(ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(ExtensionRegistry registry) {
        Ecdsa.registerAllExtensions((ExtensionRegistryLite)registry);
    }

    public static Descriptors.FileDescriptor getDescriptor() {
        return descriptor;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", Ecdsa.class.getName());
        String[] descriptorData = new String[]{"\n\u0011proto/ecdsa.proto\u0012\u0012google.crypto.tink\u001a\u0012proto/common.proto\"\u00b2\u0001\n\u000bEcdsaParams\u0012/\n\thash_type\u0018\u0001 \u0001(\u000e2\u001c.google.crypto.tink.HashType\u00124\n\u0005curve\u0018\u0002 \u0001(\u000e2%.google.crypto.tink.EllipticCurveType\u0012<\n\bencoding\u0018\u0003 \u0001(\u000e2*.google.crypto.tink.EcdsaSignatureEncoding\"h\n\u000eEcdsaPublicKey\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u0012/\n\u0006params\u0018\u0002 \u0001(\u000b2\u001f.google.crypto.tink.EcdsaParams\u0012\t\n\u0001x\u0018\u0003 \u0001(\f\u0012\t\n\u0001y\u0018\u0004 \u0001(\f\"m\n\u000fEcdsaPrivateKey\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u00126\n\npublic_key\u0018\u0002 \u0001(\u000b2\".google.crypto.tink.EcdsaPublicKey\u0012\u0011\n\tkey_value\u0018\u0003 \u0001(\f\"R\n\u000eEcdsaKeyFormat\u0012/\n\u0006params\u0018\u0002 \u0001(\u000b2\u001f.google.crypto.tink.EcdsaParams\u0012\u000f\n\u0007version\u0018\u0003 \u0001(\r*G\n\u0016EcdsaSignatureEncoding\u0012\u0014\n\u0010UNKNOWN_ENCODING\u0010\u0000\u0012\u000e\n\nIEEE_P1363\u0010\u0001\u0012\u0007\n\u0003DER\u0010\u0002BX\n\u001ccom.google.crypto.tink.protoP\u0001Z6github.com/tink-crypto/tink-go/v2/proto/ecdsa_go_protob\u0006proto3"};
        descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[]{Common.getDescriptor()});
        internal_static_google_crypto_tink_EcdsaParams_descriptor = Ecdsa.getDescriptor().getMessageTypes().get(0);
        internal_static_google_crypto_tink_EcdsaParams_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(internal_static_google_crypto_tink_EcdsaParams_descriptor, new String[]{"HashType", "Curve", "Encoding"});
        internal_static_google_crypto_tink_EcdsaPublicKey_descriptor = Ecdsa.getDescriptor().getMessageTypes().get(1);
        internal_static_google_crypto_tink_EcdsaPublicKey_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(internal_static_google_crypto_tink_EcdsaPublicKey_descriptor, new String[]{"Version", "Params", "X", "Y"});
        internal_static_google_crypto_tink_EcdsaPrivateKey_descriptor = Ecdsa.getDescriptor().getMessageTypes().get(2);
        internal_static_google_crypto_tink_EcdsaPrivateKey_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(internal_static_google_crypto_tink_EcdsaPrivateKey_descriptor, new String[]{"Version", "PublicKey", "KeyValue"});
        internal_static_google_crypto_tink_EcdsaKeyFormat_descriptor = Ecdsa.getDescriptor().getMessageTypes().get(3);
        internal_static_google_crypto_tink_EcdsaKeyFormat_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(internal_static_google_crypto_tink_EcdsaKeyFormat_descriptor, new String[]{"Params", "Version"});
        descriptor.resolveAllFeaturesImmutable();
        Common.getDescriptor();
    }
}

