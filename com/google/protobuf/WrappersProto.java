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

public final class WrappersProto
extends GeneratedFile {
    static final Descriptors.Descriptor internal_static_google_protobuf_DoubleValue_descriptor;
    static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_DoubleValue_fieldAccessorTable;
    static final Descriptors.Descriptor internal_static_google_protobuf_FloatValue_descriptor;
    static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_FloatValue_fieldAccessorTable;
    static final Descriptors.Descriptor internal_static_google_protobuf_Int64Value_descriptor;
    static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_Int64Value_fieldAccessorTable;
    static final Descriptors.Descriptor internal_static_google_protobuf_UInt64Value_descriptor;
    static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_UInt64Value_fieldAccessorTable;
    static final Descriptors.Descriptor internal_static_google_protobuf_Int32Value_descriptor;
    static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_Int32Value_fieldAccessorTable;
    static final Descriptors.Descriptor internal_static_google_protobuf_UInt32Value_descriptor;
    static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_UInt32Value_fieldAccessorTable;
    static final Descriptors.Descriptor internal_static_google_protobuf_BoolValue_descriptor;
    static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_BoolValue_fieldAccessorTable;
    static final Descriptors.Descriptor internal_static_google_protobuf_StringValue_descriptor;
    static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_StringValue_fieldAccessorTable;
    static final Descriptors.Descriptor internal_static_google_protobuf_BytesValue_descriptor;
    static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_BytesValue_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;

    private WrappersProto() {
    }

    public static void registerAllExtensions(ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(ExtensionRegistry registry) {
        WrappersProto.registerAllExtensions((ExtensionRegistryLite)registry);
    }

    public static Descriptors.FileDescriptor getDescriptor() {
        return descriptor;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "WrappersProto");
        String[] descriptorData = new String[]{"\n\u001egoogle/protobuf/wrappers.proto\u0012\u000fgoogle.protobuf\"#\n\u000bDoubleValue\u0012\u0014\n\u0005value\u0018\u0001 \u0001(\u0001R\u0005value\"\"\n\nFloatValue\u0012\u0014\n\u0005value\u0018\u0001 \u0001(\u0002R\u0005value\"\"\n\nInt64Value\u0012\u0014\n\u0005value\u0018\u0001 \u0001(\u0003R\u0005value\"#\n\u000bUInt64Value\u0012\u0014\n\u0005value\u0018\u0001 \u0001(\u0004R\u0005value\"\"\n\nInt32Value\u0012\u0014\n\u0005value\u0018\u0001 \u0001(\u0005R\u0005value\"#\n\u000bUInt32Value\u0012\u0014\n\u0005value\u0018\u0001 \u0001(\rR\u0005value\"!\n\tBoolValue\u0012\u0014\n\u0005value\u0018\u0001 \u0001(\bR\u0005value\"#\n\u000bStringValue\u0012\u0014\n\u0005value\u0018\u0001 \u0001(\tR\u0005value\"\"\n\nBytesValue\u0012\u0014\n\u0005value\u0018\u0001 \u0001(\fR\u0005valueB\u0083\u0001\n\u0013com.google.protobufB\rWrappersProtoP\u0001Z1google.golang.org/protobuf/types/known/wrapperspb\u00f8\u0001\u0001\u00a2\u0002\u0003GPB\u00aa\u0002\u001eGoogle.Protobuf.WellKnownTypesb\u0006proto3"};
        descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
        internal_static_google_protobuf_DoubleValue_descriptor = WrappersProto.getDescriptor().getMessageType(0);
        internal_static_google_protobuf_DoubleValue_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(internal_static_google_protobuf_DoubleValue_descriptor, new String[]{"Value"});
        internal_static_google_protobuf_FloatValue_descriptor = WrappersProto.getDescriptor().getMessageType(1);
        internal_static_google_protobuf_FloatValue_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(internal_static_google_protobuf_FloatValue_descriptor, new String[]{"Value"});
        internal_static_google_protobuf_Int64Value_descriptor = WrappersProto.getDescriptor().getMessageType(2);
        internal_static_google_protobuf_Int64Value_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(internal_static_google_protobuf_Int64Value_descriptor, new String[]{"Value"});
        internal_static_google_protobuf_UInt64Value_descriptor = WrappersProto.getDescriptor().getMessageType(3);
        internal_static_google_protobuf_UInt64Value_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(internal_static_google_protobuf_UInt64Value_descriptor, new String[]{"Value"});
        internal_static_google_protobuf_Int32Value_descriptor = WrappersProto.getDescriptor().getMessageType(4);
        internal_static_google_protobuf_Int32Value_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(internal_static_google_protobuf_Int32Value_descriptor, new String[]{"Value"});
        internal_static_google_protobuf_UInt32Value_descriptor = WrappersProto.getDescriptor().getMessageType(5);
        internal_static_google_protobuf_UInt32Value_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(internal_static_google_protobuf_UInt32Value_descriptor, new String[]{"Value"});
        internal_static_google_protobuf_BoolValue_descriptor = WrappersProto.getDescriptor().getMessageType(6);
        internal_static_google_protobuf_BoolValue_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(internal_static_google_protobuf_BoolValue_descriptor, new String[]{"Value"});
        internal_static_google_protobuf_StringValue_descriptor = WrappersProto.getDescriptor().getMessageType(7);
        internal_static_google_protobuf_StringValue_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(internal_static_google_protobuf_StringValue_descriptor, new String[]{"Value"});
        internal_static_google_protobuf_BytesValue_descriptor = WrappersProto.getDescriptor().getMessageType(8);
        internal_static_google_protobuf_BytesValue_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(internal_static_google_protobuf_BytesValue_descriptor, new String[]{"Value"});
        descriptor.resolveAllFeaturesImmutable();
    }
}

