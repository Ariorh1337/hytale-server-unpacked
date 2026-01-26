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

public final class FieldMaskProto
extends GeneratedFile {
    static final Descriptors.Descriptor internal_static_google_protobuf_FieldMask_descriptor;
    static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_FieldMask_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;

    private FieldMaskProto() {
    }

    public static void registerAllExtensions(ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(ExtensionRegistry registry) {
        FieldMaskProto.registerAllExtensions((ExtensionRegistryLite)registry);
    }

    public static Descriptors.FileDescriptor getDescriptor() {
        return descriptor;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "FieldMaskProto");
        String[] descriptorData = new String[]{"\n google/protobuf/field_mask.proto\u0012\u000fgoogle.protobuf\"!\n\tFieldMask\u0012\u0014\n\u0005paths\u0018\u0001 \u0003(\tR\u0005pathsB\u0085\u0001\n\u0013com.google.protobufB\u000eFieldMaskProtoP\u0001Z2google.golang.org/protobuf/types/known/fieldmaskpb\u00f8\u0001\u0001\u00a2\u0002\u0003GPB\u00aa\u0002\u001eGoogle.Protobuf.WellKnownTypesb\u0006proto3"};
        descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
        internal_static_google_protobuf_FieldMask_descriptor = FieldMaskProto.getDescriptor().getMessageType(0);
        internal_static_google_protobuf_FieldMask_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(internal_static_google_protobuf_FieldMask_descriptor, new String[]{"Paths"});
        descriptor.resolveAllFeaturesImmutable();
    }
}

