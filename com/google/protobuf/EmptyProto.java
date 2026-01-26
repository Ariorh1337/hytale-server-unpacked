/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedFile;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.RuntimeVersion;

public final class EmptyProto
extends GeneratedFile {
    static final Descriptors.Descriptor internal_static_google_protobuf_Empty_descriptor;
    static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_Empty_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;

    private EmptyProto() {
    }

    public static void registerAllExtensions(ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(ExtensionRegistry registry) {
        EmptyProto.registerAllExtensions((ExtensionRegistryLite)registry);
    }

    public static Descriptors.FileDescriptor getDescriptor() {
        return descriptor;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "EmptyProto");
        String[] descriptorData = new String[]{"\n\u001bgoogle/protobuf/empty.proto\u0012\u000fgoogle.protobuf\"\u0007\n\u0005EmptyB}\n\u0013com.google.protobufB\nEmptyProtoP\u0001Z.google.golang.org/protobuf/types/known/emptypb\u00f8\u0001\u0001\u00a2\u0002\u0003GPB\u00aa\u0002\u001eGoogle.Protobuf.WellKnownTypesb\u0006proto3"};
        descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
        internal_static_google_protobuf_Empty_descriptor = EmptyProto.getDescriptor().getMessageType(0);
        internal_static_google_protobuf_Empty_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(internal_static_google_protobuf_Empty_descriptor, new String[0]);
        descriptor.resolveAllFeaturesImmutable();
    }
}

