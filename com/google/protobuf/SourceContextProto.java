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

public final class SourceContextProto
extends GeneratedFile {
    static final Descriptors.Descriptor internal_static_google_protobuf_SourceContext_descriptor;
    static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_SourceContext_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;

    private SourceContextProto() {
    }

    public static void registerAllExtensions(ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(ExtensionRegistry registry) {
        SourceContextProto.registerAllExtensions((ExtensionRegistryLite)registry);
    }

    public static Descriptors.FileDescriptor getDescriptor() {
        return descriptor;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "SourceContextProto");
        String[] descriptorData = new String[]{"\n$google/protobuf/source_context.proto\u0012\u000fgoogle.protobuf\",\n\rSourceContext\u0012\u001b\n\tfile_name\u0018\u0001 \u0001(\tR\bfileNameB\u008a\u0001\n\u0013com.google.protobufB\u0012SourceContextProtoP\u0001Z6google.golang.org/protobuf/types/known/sourcecontextpb\u00a2\u0002\u0003GPB\u00aa\u0002\u001eGoogle.Protobuf.WellKnownTypesb\u0006proto3"};
        descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
        internal_static_google_protobuf_SourceContext_descriptor = SourceContextProto.getDescriptor().getMessageType(0);
        internal_static_google_protobuf_SourceContext_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(internal_static_google_protobuf_SourceContext_descriptor, new String[]{"FileName"});
        descriptor.resolveAllFeaturesImmutable();
    }
}

