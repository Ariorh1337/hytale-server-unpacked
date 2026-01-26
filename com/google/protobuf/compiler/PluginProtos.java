/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf.compiler;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedFile;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.LazyStringArrayList;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Parser;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.ProtocolStringList;
import com.google.protobuf.RepeatedFieldBuilder;
import com.google.protobuf.RuntimeVersion;
import com.google.protobuf.SingleFieldBuilder;
import com.google.protobuf.UninitializedMessageException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PluginProtos
extends GeneratedFile {
    private static final Descriptors.Descriptor internal_static_google_protobuf_compiler_Version_descriptor;
    private static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_compiler_Version_fieldAccessorTable;
    private static final Descriptors.Descriptor internal_static_google_protobuf_compiler_CodeGeneratorRequest_descriptor;
    private static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_compiler_CodeGeneratorRequest_fieldAccessorTable;
    private static final Descriptors.Descriptor internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor;
    private static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_compiler_CodeGeneratorResponse_fieldAccessorTable;
    private static final Descriptors.Descriptor internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_descriptor;
    private static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;

    private PluginProtos() {
    }

    public static void registerAllExtensions(ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(ExtensionRegistry registry) {
        PluginProtos.registerAllExtensions((ExtensionRegistryLite)registry);
    }

    public static Descriptors.FileDescriptor getDescriptor() {
        return descriptor;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "PluginProtos");
        String[] descriptorData = new String[]{"\n%google/protobuf/compiler/plugin.proto\u0012\u0018google.protobuf.compiler\u001a google/protobuf/descriptor.proto\"c\n\u0007Version\u0012\u0014\n\u0005major\u0018\u0001 \u0001(\u0005R\u0005major\u0012\u0014\n\u0005minor\u0018\u0002 \u0001(\u0005R\u0005minor\u0012\u0014\n\u0005patch\u0018\u0003 \u0001(\u0005R\u0005patch\u0012\u0016\n\u0006suffix\u0018\u0004 \u0001(\tR\u0006suffix\"\u00cf\u0002\n\u0014CodeGeneratorRequest\u0012(\n\u0010file_to_generate\u0018\u0001 \u0003(\tR\u000efileToGenerate\u0012\u001c\n\tparameter\u0018\u0002 \u0001(\tR\tparameter\u0012C\n\nproto_file\u0018\u000f \u0003(\u000b2$.google.protobuf.FileDescriptorProtoR\tprotoFile\u0012\\\n\u0017source_file_descriptors\u0018\u0011 \u0003(\u000b2$.google.protobuf.FileDescriptorProtoR\u0015sourceFileDescriptors\u0012L\n\u0010compiler_version\u0018\u0003 \u0001(\u000b2!.google.protobuf.compiler.VersionR\u000fcompilerVersion\"\u0085\u0004\n\u0015CodeGeneratorResponse\u0012\u0014\n\u0005error\u0018\u0001 \u0001(\tR\u0005error\u0012-\n\u0012supported_features\u0018\u0002 \u0001(\u0004R\u0011supportedFeatures\u0012'\n\u000fminimum_edition\u0018\u0003 \u0001(\u0005R\u000eminimumEdition\u0012'\n\u000fmaximum_edition\u0018\u0004 \u0001(\u0005R\u000emaximumEdition\u0012H\n\u0004file\u0018\u000f \u0003(\u000b24.google.protobuf.compiler.CodeGeneratorResponse.FileR\u0004file\u001a\u00b1\u0001\n\u0004File\u0012\u0012\n\u0004name\u0018\u0001 \u0001(\tR\u0004name\u0012'\n\u000finsertion_point\u0018\u0002 \u0001(\tR\u000einsertionPoint\u0012\u0018\n\u0007content\u0018\u000f \u0001(\tR\u0007content\u0012R\n\u0013generated_code_info\u0018\u0010 \u0001(\u000b2\".google.protobuf.GeneratedCodeInfoR\u0011generatedCodeInfo\"W\n\u0007Feature\u0012\u0010\n\fFEATURE_NONE\u0010\u0000\u0012\u001b\n\u0017FEATURE_PROTO3_OPTIONAL\u0010\u0001\u0012\u001d\n\u0019FEATURE_SUPPORTS_EDITIONS\u0010\u0002Br\n\u001ccom.google.protobuf.compilerB\fPluginProtosZ)google.golang.org/protobuf/types/pluginpb\u00aa\u0002\u0018Google.Protobuf.Compiler"};
        descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[]{DescriptorProtos.getDescriptor()});
        internal_static_google_protobuf_compiler_Version_descriptor = PluginProtos.getDescriptor().getMessageType(0);
        internal_static_google_protobuf_compiler_Version_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(internal_static_google_protobuf_compiler_Version_descriptor, new String[]{"Major", "Minor", "Patch", "Suffix"});
        internal_static_google_protobuf_compiler_CodeGeneratorRequest_descriptor = PluginProtos.getDescriptor().getMessageType(1);
        internal_static_google_protobuf_compiler_CodeGeneratorRequest_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(internal_static_google_protobuf_compiler_CodeGeneratorRequest_descriptor, new String[]{"FileToGenerate", "Parameter", "ProtoFile", "SourceFileDescriptors", "CompilerVersion"});
        internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor = PluginProtos.getDescriptor().getMessageType(2);
        internal_static_google_protobuf_compiler_CodeGeneratorResponse_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor, new String[]{"Error", "SupportedFeatures", "MinimumEdition", "MaximumEdition", "File"});
        internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_descriptor = internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor.getNestedType(0);
        internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_descriptor, new String[]{"Name", "InsertionPoint", "Content", "GeneratedCodeInfo"});
        descriptor.resolveAllFeaturesImmutable();
        DescriptorProtos.getDescriptor();
    }

    public static final class CodeGeneratorResponse
    extends GeneratedMessage
    implements CodeGeneratorResponseOrBuilder {
        private static final long serialVersionUID = 0L;
        private int bitField0_;
        public static final int ERROR_FIELD_NUMBER = 1;
        private volatile Object error_ = "";
        public static final int SUPPORTED_FEATURES_FIELD_NUMBER = 2;
        private long supportedFeatures_ = 0L;
        public static final int MINIMUM_EDITION_FIELD_NUMBER = 3;
        private int minimumEdition_ = 0;
        public static final int MAXIMUM_EDITION_FIELD_NUMBER = 4;
        private int maximumEdition_ = 0;
        public static final int FILE_FIELD_NUMBER = 15;
        private List<File> file_;
        private byte memoizedIsInitialized = (byte)-1;
        private static final CodeGeneratorResponse DEFAULT_INSTANCE;
        private static final Parser<CodeGeneratorResponse> PARSER;

        private CodeGeneratorResponse(GeneratedMessage.Builder<?> builder) {
            super(builder);
        }

        private CodeGeneratorResponse() {
            this.error_ = "";
            this.file_ = Collections.emptyList();
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return internal_static_google_protobuf_compiler_CodeGeneratorResponse_fieldAccessorTable.ensureFieldAccessorsInitialized(CodeGeneratorResponse.class, Builder.class);
        }

        @Override
        public boolean hasError() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override
        public String getError() {
            Object ref = this.error_;
            if (ref instanceof String) {
                return (String)ref;
            }
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.error_ = s;
            }
            return s;
        }

        @Override
        public ByteString getErrorBytes() {
            Object ref = this.error_;
            if (ref instanceof String) {
                ByteString b = ByteString.copyFromUtf8((String)ref);
                this.error_ = b;
                return b;
            }
            return (ByteString)ref;
        }

        @Override
        public boolean hasSupportedFeatures() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override
        public long getSupportedFeatures() {
            return this.supportedFeatures_;
        }

        @Override
        public boolean hasMinimumEdition() {
            return (this.bitField0_ & 4) != 0;
        }

        @Override
        public int getMinimumEdition() {
            return this.minimumEdition_;
        }

        @Override
        public boolean hasMaximumEdition() {
            return (this.bitField0_ & 8) != 0;
        }

        @Override
        public int getMaximumEdition() {
            return this.maximumEdition_;
        }

        @Override
        public List<File> getFileList() {
            return this.file_;
        }

        @Override
        public List<? extends FileOrBuilder> getFileOrBuilderList() {
            return this.file_;
        }

        @Override
        public int getFileCount() {
            return this.file_.size();
        }

        @Override
        public File getFile(int index) {
            return this.file_.get(index);
        }

        @Override
        public FileOrBuilder getFileOrBuilder(int index) {
            return this.file_.get(index);
        }

        @Override
        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
                return true;
            }
            if (isInitialized == 0) {
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }

        @Override
        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 1) != 0) {
                GeneratedMessage.writeString(output, 1, this.error_);
            }
            if ((this.bitField0_ & 2) != 0) {
                output.writeUInt64(2, this.supportedFeatures_);
            }
            if ((this.bitField0_ & 4) != 0) {
                output.writeInt32(3, this.minimumEdition_);
            }
            if ((this.bitField0_ & 8) != 0) {
                output.writeInt32(4, this.maximumEdition_);
            }
            for (int i = 0; i < this.file_.size(); ++i) {
                output.writeMessage(15, this.file_.get(i));
            }
            this.getUnknownFields().writeTo(output);
        }

        @Override
        public int getSerializedSize() {
            int size = this.memoizedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
                size += GeneratedMessage.computeStringSize(1, this.error_);
            }
            if ((this.bitField0_ & 2) != 0) {
                size += CodedOutputStream.computeUInt64Size(2, this.supportedFeatures_);
            }
            if ((this.bitField0_ & 4) != 0) {
                size += CodedOutputStream.computeInt32Size(3, this.minimumEdition_);
            }
            if ((this.bitField0_ & 8) != 0) {
                size += CodedOutputStream.computeInt32Size(4, this.maximumEdition_);
            }
            for (int i = 0; i < this.file_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(15, this.file_.get(i));
            }
            this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
            return size;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof CodeGeneratorResponse)) {
                return super.equals(obj);
            }
            CodeGeneratorResponse other = (CodeGeneratorResponse)obj;
            if (this.hasError() != other.hasError()) {
                return false;
            }
            if (this.hasError() && !this.getError().equals(other.getError())) {
                return false;
            }
            if (this.hasSupportedFeatures() != other.hasSupportedFeatures()) {
                return false;
            }
            if (this.hasSupportedFeatures() && this.getSupportedFeatures() != other.getSupportedFeatures()) {
                return false;
            }
            if (this.hasMinimumEdition() != other.hasMinimumEdition()) {
                return false;
            }
            if (this.hasMinimumEdition() && this.getMinimumEdition() != other.getMinimumEdition()) {
                return false;
            }
            if (this.hasMaximumEdition() != other.hasMaximumEdition()) {
                return false;
            }
            if (this.hasMaximumEdition() && this.getMaximumEdition() != other.getMaximumEdition()) {
                return false;
            }
            if (!this.getFileList().equals(other.getFileList())) {
                return false;
            }
            return this.getUnknownFields().equals(other.getUnknownFields());
        }

        @Override
        public int hashCode() {
            if (this.memoizedHashCode != 0) {
                return this.memoizedHashCode;
            }
            int hash = 41;
            hash = 19 * hash + CodeGeneratorResponse.getDescriptor().hashCode();
            if (this.hasError()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getError().hashCode();
            }
            if (this.hasSupportedFeatures()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + Internal.hashLong(this.getSupportedFeatures());
            }
            if (this.hasMinimumEdition()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getMinimumEdition();
            }
            if (this.hasMaximumEdition()) {
                hash = 37 * hash + 4;
                hash = 53 * hash + this.getMaximumEdition();
            }
            if (this.getFileCount() > 0) {
                hash = 37 * hash + 15;
                hash = 53 * hash + this.getFileList().hashCode();
            }
            this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
            return hash;
        }

        public static CodeGeneratorResponse parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static CodeGeneratorResponse parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static CodeGeneratorResponse parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static CodeGeneratorResponse parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static CodeGeneratorResponse parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static CodeGeneratorResponse parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static CodeGeneratorResponse parseFrom(InputStream input) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input);
        }

        public static CodeGeneratorResponse parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static CodeGeneratorResponse parseDelimitedFrom(InputStream input) throws IOException {
            return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
        }

        public static CodeGeneratorResponse parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static CodeGeneratorResponse parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input);
        }

        public static CodeGeneratorResponse parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
        }

        @Override
        public Builder newBuilderForType() {
            return CodeGeneratorResponse.newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(CodeGeneratorResponse prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        @Override
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
        }

        @Override
        protected Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        public static CodeGeneratorResponse getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<CodeGeneratorResponse> parser() {
            return PARSER;
        }

        public Parser<CodeGeneratorResponse> getParserForType() {
            return PARSER;
        }

        @Override
        public CodeGeneratorResponse getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

        static {
            RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "CodeGeneratorResponse");
            DEFAULT_INSTANCE = new CodeGeneratorResponse();
            PARSER = new AbstractParser<CodeGeneratorResponse>(){

                @Override
                public CodeGeneratorResponse parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    Builder builder = CodeGeneratorResponse.newBuilder();
                    try {
                        builder.mergeFrom(input, extensionRegistry);
                    }
                    catch (InvalidProtocolBufferException e) {
                        throw e.setUnfinishedMessage(builder.buildPartial());
                    }
                    catch (UninitializedMessageException e) {
                        throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
                    }
                    catch (IOException e) {
                        throw new InvalidProtocolBufferException(e).setUnfinishedMessage(builder.buildPartial());
                    }
                    return builder.buildPartial();
                }
            };
        }

        public static final class Builder
        extends GeneratedMessage.Builder<Builder>
        implements CodeGeneratorResponseOrBuilder {
            private int bitField0_;
            private Object error_ = "";
            private long supportedFeatures_;
            private int minimumEdition_;
            private int maximumEdition_;
            private List<File> file_ = Collections.emptyList();
            private RepeatedFieldBuilder<File, File.Builder, FileOrBuilder> fileBuilder_;

            public static final Descriptors.Descriptor getDescriptor() {
                return internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor;
            }

            @Override
            protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return internal_static_google_protobuf_compiler_CodeGeneratorResponse_fieldAccessorTable.ensureFieldAccessorsInitialized(CodeGeneratorResponse.class, Builder.class);
            }

            private Builder() {
            }

            private Builder(AbstractMessage.BuilderParent parent) {
                super(parent);
            }

            @Override
            public Builder clear() {
                super.clear();
                this.bitField0_ = 0;
                this.error_ = "";
                this.supportedFeatures_ = 0L;
                this.minimumEdition_ = 0;
                this.maximumEdition_ = 0;
                if (this.fileBuilder_ == null) {
                    this.file_ = Collections.emptyList();
                } else {
                    this.file_ = null;
                    this.fileBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFEF;
                return this;
            }

            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor;
            }

            @Override
            public CodeGeneratorResponse getDefaultInstanceForType() {
                return CodeGeneratorResponse.getDefaultInstance();
            }

            @Override
            public CodeGeneratorResponse build() {
                CodeGeneratorResponse result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw Builder.newUninitializedMessageException(result);
                }
                return result;
            }

            @Override
            public CodeGeneratorResponse buildPartial() {
                CodeGeneratorResponse result = new CodeGeneratorResponse(this);
                this.buildPartialRepeatedFields(result);
                if (this.bitField0_ != 0) {
                    this.buildPartial0(result);
                }
                this.onBuilt();
                return result;
            }

            private void buildPartialRepeatedFields(CodeGeneratorResponse result) {
                if (this.fileBuilder_ == null) {
                    if ((this.bitField0_ & 0x10) != 0) {
                        this.file_ = Collections.unmodifiableList(this.file_);
                        this.bitField0_ &= 0xFFFFFFEF;
                    }
                    result.file_ = this.file_;
                } else {
                    result.file_ = this.fileBuilder_.build();
                }
            }

            private void buildPartial0(CodeGeneratorResponse result) {
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) != 0) {
                    result.error_ = this.error_;
                    to_bitField0_ |= 1;
                }
                if ((from_bitField0_ & 2) != 0) {
                    result.supportedFeatures_ = this.supportedFeatures_;
                    to_bitField0_ |= 2;
                }
                if ((from_bitField0_ & 4) != 0) {
                    result.minimumEdition_ = this.minimumEdition_;
                    to_bitField0_ |= 4;
                }
                if ((from_bitField0_ & 8) != 0) {
                    result.maximumEdition_ = this.maximumEdition_;
                    to_bitField0_ |= 8;
                }
                result.bitField0_ |= to_bitField0_;
            }

            @Override
            public Builder mergeFrom(Message other) {
                if (other instanceof CodeGeneratorResponse) {
                    return this.mergeFrom((CodeGeneratorResponse)other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(CodeGeneratorResponse other) {
                if (other == CodeGeneratorResponse.getDefaultInstance()) {
                    return this;
                }
                if (other.hasError()) {
                    this.error_ = other.error_;
                    this.bitField0_ |= 1;
                    this.onChanged();
                }
                if (other.hasSupportedFeatures()) {
                    this.setSupportedFeatures(other.getSupportedFeatures());
                }
                if (other.hasMinimumEdition()) {
                    this.setMinimumEdition(other.getMinimumEdition());
                }
                if (other.hasMaximumEdition()) {
                    this.setMaximumEdition(other.getMaximumEdition());
                }
                if (this.fileBuilder_ == null) {
                    if (!other.file_.isEmpty()) {
                        if (this.file_.isEmpty()) {
                            this.file_ = other.file_;
                            this.bitField0_ &= 0xFFFFFFEF;
                        } else {
                            this.ensureFileIsMutable();
                            this.file_.addAll(other.file_);
                        }
                        this.onChanged();
                    }
                } else if (!other.file_.isEmpty()) {
                    if (this.fileBuilder_.isEmpty()) {
                        this.fileBuilder_.dispose();
                        this.fileBuilder_ = null;
                        this.file_ = other.file_;
                        this.bitField0_ &= 0xFFFFFFEF;
                        this.fileBuilder_ = alwaysUseFieldBuilders ? this.internalGetFileFieldBuilder() : null;
                    } else {
                        this.fileBuilder_.addAllMessages(other.file_);
                    }
                }
                this.mergeUnknownFields(other.getUnknownFields());
                this.onChanged();
                return this;
            }

            @Override
            public final boolean isInitialized() {
                return true;
            }

            @Override
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                if (extensionRegistry == null) {
                    throw new NullPointerException();
                }
                try {
                    boolean done = false;
                    block13: while (!done) {
                        int tag = input.readTag();
                        switch (tag) {
                            case 0: {
                                done = true;
                                continue block13;
                            }
                            case 10: {
                                this.error_ = input.readBytes();
                                this.bitField0_ |= 1;
                                continue block13;
                            }
                            case 16: {
                                this.supportedFeatures_ = input.readUInt64();
                                this.bitField0_ |= 2;
                                continue block13;
                            }
                            case 24: {
                                this.minimumEdition_ = input.readInt32();
                                this.bitField0_ |= 4;
                                continue block13;
                            }
                            case 32: {
                                this.maximumEdition_ = input.readInt32();
                                this.bitField0_ |= 8;
                                continue block13;
                            }
                            case 122: {
                                File m = input.readMessage(File.parser(), extensionRegistry);
                                if (this.fileBuilder_ == null) {
                                    this.ensureFileIsMutable();
                                    this.file_.add(m);
                                    continue block13;
                                }
                                this.fileBuilder_.addMessage(m);
                                continue block13;
                            }
                        }
                        if (super.parseUnknownField(input, extensionRegistry, tag)) continue;
                        done = true;
                    }
                }
                catch (InvalidProtocolBufferException e) {
                    throw e.unwrapIOException();
                }
                finally {
                    this.onChanged();
                }
                return this;
            }

            @Override
            public boolean hasError() {
                return (this.bitField0_ & 1) != 0;
            }

            @Override
            public String getError() {
                Object ref = this.error_;
                if (!(ref instanceof String)) {
                    ByteString bs = (ByteString)ref;
                    String s = bs.toStringUtf8();
                    if (bs.isValidUtf8()) {
                        this.error_ = s;
                    }
                    return s;
                }
                return (String)ref;
            }

            @Override
            public ByteString getErrorBytes() {
                Object ref = this.error_;
                if (ref instanceof String) {
                    ByteString b = ByteString.copyFromUtf8((String)ref);
                    this.error_ = b;
                    return b;
                }
                return (ByteString)ref;
            }

            public Builder setError(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.error_ = value;
                this.bitField0_ |= 1;
                this.onChanged();
                return this;
            }

            public Builder clearError() {
                this.error_ = CodeGeneratorResponse.getDefaultInstance().getError();
                this.bitField0_ &= 0xFFFFFFFE;
                this.onChanged();
                return this;
            }

            public Builder setErrorBytes(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.error_ = value;
                this.bitField0_ |= 1;
                this.onChanged();
                return this;
            }

            @Override
            public boolean hasSupportedFeatures() {
                return (this.bitField0_ & 2) != 0;
            }

            @Override
            public long getSupportedFeatures() {
                return this.supportedFeatures_;
            }

            public Builder setSupportedFeatures(long value) {
                this.supportedFeatures_ = value;
                this.bitField0_ |= 2;
                this.onChanged();
                return this;
            }

            public Builder clearSupportedFeatures() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.supportedFeatures_ = 0L;
                this.onChanged();
                return this;
            }

            @Override
            public boolean hasMinimumEdition() {
                return (this.bitField0_ & 4) != 0;
            }

            @Override
            public int getMinimumEdition() {
                return this.minimumEdition_;
            }

            public Builder setMinimumEdition(int value) {
                this.minimumEdition_ = value;
                this.bitField0_ |= 4;
                this.onChanged();
                return this;
            }

            public Builder clearMinimumEdition() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.minimumEdition_ = 0;
                this.onChanged();
                return this;
            }

            @Override
            public boolean hasMaximumEdition() {
                return (this.bitField0_ & 8) != 0;
            }

            @Override
            public int getMaximumEdition() {
                return this.maximumEdition_;
            }

            public Builder setMaximumEdition(int value) {
                this.maximumEdition_ = value;
                this.bitField0_ |= 8;
                this.onChanged();
                return this;
            }

            public Builder clearMaximumEdition() {
                this.bitField0_ &= 0xFFFFFFF7;
                this.maximumEdition_ = 0;
                this.onChanged();
                return this;
            }

            private void ensureFileIsMutable() {
                if ((this.bitField0_ & 0x10) == 0) {
                    this.file_ = new ArrayList<File>(this.file_);
                    this.bitField0_ |= 0x10;
                }
            }

            @Override
            public List<File> getFileList() {
                if (this.fileBuilder_ == null) {
                    return Collections.unmodifiableList(this.file_);
                }
                return this.fileBuilder_.getMessageList();
            }

            @Override
            public int getFileCount() {
                if (this.fileBuilder_ == null) {
                    return this.file_.size();
                }
                return this.fileBuilder_.getCount();
            }

            @Override
            public File getFile(int index) {
                if (this.fileBuilder_ == null) {
                    return this.file_.get(index);
                }
                return this.fileBuilder_.getMessage(index);
            }

            public Builder setFile(int index, File value) {
                if (this.fileBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureFileIsMutable();
                    this.file_.set(index, value);
                    this.onChanged();
                } else {
                    this.fileBuilder_.setMessage(index, value);
                }
                return this;
            }

            public Builder setFile(int index, File.Builder builderForValue) {
                if (this.fileBuilder_ == null) {
                    this.ensureFileIsMutable();
                    this.file_.set(index, builderForValue.build());
                    this.onChanged();
                } else {
                    this.fileBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addFile(File value) {
                if (this.fileBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureFileIsMutable();
                    this.file_.add(value);
                    this.onChanged();
                } else {
                    this.fileBuilder_.addMessage(value);
                }
                return this;
            }

            public Builder addFile(int index, File value) {
                if (this.fileBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureFileIsMutable();
                    this.file_.add(index, value);
                    this.onChanged();
                } else {
                    this.fileBuilder_.addMessage(index, value);
                }
                return this;
            }

            public Builder addFile(File.Builder builderForValue) {
                if (this.fileBuilder_ == null) {
                    this.ensureFileIsMutable();
                    this.file_.add(builderForValue.build());
                    this.onChanged();
                } else {
                    this.fileBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addFile(int index, File.Builder builderForValue) {
                if (this.fileBuilder_ == null) {
                    this.ensureFileIsMutable();
                    this.file_.add(index, builderForValue.build());
                    this.onChanged();
                } else {
                    this.fileBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllFile(Iterable<? extends File> values) {
                if (this.fileBuilder_ == null) {
                    this.ensureFileIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.file_);
                    this.onChanged();
                } else {
                    this.fileBuilder_.addAllMessages(values);
                }
                return this;
            }

            public Builder clearFile() {
                if (this.fileBuilder_ == null) {
                    this.file_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFEF;
                    this.onChanged();
                } else {
                    this.fileBuilder_.clear();
                }
                return this;
            }

            public Builder removeFile(int index) {
                if (this.fileBuilder_ == null) {
                    this.ensureFileIsMutable();
                    this.file_.remove(index);
                    this.onChanged();
                } else {
                    this.fileBuilder_.remove(index);
                }
                return this;
            }

            public File.Builder getFileBuilder(int index) {
                return this.internalGetFileFieldBuilder().getBuilder(index);
            }

            @Override
            public FileOrBuilder getFileOrBuilder(int index) {
                if (this.fileBuilder_ == null) {
                    return this.file_.get(index);
                }
                return this.fileBuilder_.getMessageOrBuilder(index);
            }

            @Override
            public List<? extends FileOrBuilder> getFileOrBuilderList() {
                if (this.fileBuilder_ != null) {
                    return this.fileBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.file_);
            }

            public File.Builder addFileBuilder() {
                return this.internalGetFileFieldBuilder().addBuilder(File.getDefaultInstance());
            }

            public File.Builder addFileBuilder(int index) {
                return this.internalGetFileFieldBuilder().addBuilder(index, File.getDefaultInstance());
            }

            public List<File.Builder> getFileBuilderList() {
                return this.internalGetFileFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilder<File, File.Builder, FileOrBuilder> internalGetFileFieldBuilder() {
                if (this.fileBuilder_ == null) {
                    this.fileBuilder_ = new RepeatedFieldBuilder(this.file_, (this.bitField0_ & 0x10) != 0, this.getParentForChildren(), this.isClean());
                    this.file_ = null;
                }
                return this.fileBuilder_;
            }
        }

        public static final class File
        extends GeneratedMessage
        implements FileOrBuilder {
            private static final long serialVersionUID = 0L;
            private int bitField0_;
            public static final int NAME_FIELD_NUMBER = 1;
            private volatile Object name_ = "";
            public static final int INSERTION_POINT_FIELD_NUMBER = 2;
            private volatile Object insertionPoint_ = "";
            public static final int CONTENT_FIELD_NUMBER = 15;
            private volatile Object content_ = "";
            public static final int GENERATED_CODE_INFO_FIELD_NUMBER = 16;
            private DescriptorProtos.GeneratedCodeInfo generatedCodeInfo_;
            private byte memoizedIsInitialized = (byte)-1;
            private static final File DEFAULT_INSTANCE;
            private static final Parser<File> PARSER;

            private File(GeneratedMessage.Builder<?> builder) {
                super(builder);
            }

            private File() {
                this.name_ = "";
                this.insertionPoint_ = "";
                this.content_ = "";
            }

            public static final Descriptors.Descriptor getDescriptor() {
                return internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_descriptor;
            }

            @Override
            protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_fieldAccessorTable.ensureFieldAccessorsInitialized(File.class, Builder.class);
            }

            @Override
            public boolean hasName() {
                return (this.bitField0_ & 1) != 0;
            }

            @Override
            public String getName() {
                Object ref = this.name_;
                if (ref instanceof String) {
                    return (String)ref;
                }
                ByteString bs = (ByteString)ref;
                String s = bs.toStringUtf8();
                if (bs.isValidUtf8()) {
                    this.name_ = s;
                }
                return s;
            }

            @Override
            public ByteString getNameBytes() {
                Object ref = this.name_;
                if (ref instanceof String) {
                    ByteString b = ByteString.copyFromUtf8((String)ref);
                    this.name_ = b;
                    return b;
                }
                return (ByteString)ref;
            }

            @Override
            public boolean hasInsertionPoint() {
                return (this.bitField0_ & 2) != 0;
            }

            @Override
            public String getInsertionPoint() {
                Object ref = this.insertionPoint_;
                if (ref instanceof String) {
                    return (String)ref;
                }
                ByteString bs = (ByteString)ref;
                String s = bs.toStringUtf8();
                if (bs.isValidUtf8()) {
                    this.insertionPoint_ = s;
                }
                return s;
            }

            @Override
            public ByteString getInsertionPointBytes() {
                Object ref = this.insertionPoint_;
                if (ref instanceof String) {
                    ByteString b = ByteString.copyFromUtf8((String)ref);
                    this.insertionPoint_ = b;
                    return b;
                }
                return (ByteString)ref;
            }

            @Override
            public boolean hasContent() {
                return (this.bitField0_ & 4) != 0;
            }

            @Override
            public String getContent() {
                Object ref = this.content_;
                if (ref instanceof String) {
                    return (String)ref;
                }
                ByteString bs = (ByteString)ref;
                String s = bs.toStringUtf8();
                if (bs.isValidUtf8()) {
                    this.content_ = s;
                }
                return s;
            }

            @Override
            public ByteString getContentBytes() {
                Object ref = this.content_;
                if (ref instanceof String) {
                    ByteString b = ByteString.copyFromUtf8((String)ref);
                    this.content_ = b;
                    return b;
                }
                return (ByteString)ref;
            }

            @Override
            public boolean hasGeneratedCodeInfo() {
                return (this.bitField0_ & 8) != 0;
            }

            @Override
            public DescriptorProtos.GeneratedCodeInfo getGeneratedCodeInfo() {
                return this.generatedCodeInfo_ == null ? DescriptorProtos.GeneratedCodeInfo.getDefaultInstance() : this.generatedCodeInfo_;
            }

            @Override
            public DescriptorProtos.GeneratedCodeInfoOrBuilder getGeneratedCodeInfoOrBuilder() {
                return this.generatedCodeInfo_ == null ? DescriptorProtos.GeneratedCodeInfo.getDefaultInstance() : this.generatedCodeInfo_;
            }

            @Override
            public final boolean isInitialized() {
                byte isInitialized = this.memoizedIsInitialized;
                if (isInitialized == 1) {
                    return true;
                }
                if (isInitialized == 0) {
                    return false;
                }
                this.memoizedIsInitialized = 1;
                return true;
            }

            @Override
            public void writeTo(CodedOutputStream output) throws IOException {
                if ((this.bitField0_ & 1) != 0) {
                    GeneratedMessage.writeString(output, 1, this.name_);
                }
                if ((this.bitField0_ & 2) != 0) {
                    GeneratedMessage.writeString(output, 2, this.insertionPoint_);
                }
                if ((this.bitField0_ & 4) != 0) {
                    GeneratedMessage.writeString(output, 15, this.content_);
                }
                if ((this.bitField0_ & 8) != 0) {
                    output.writeMessage(16, this.getGeneratedCodeInfo());
                }
                this.getUnknownFields().writeTo(output);
            }

            @Override
            public int getSerializedSize() {
                int size = this.memoizedSize;
                if (size != -1) {
                    return size;
                }
                size = 0;
                if ((this.bitField0_ & 1) != 0) {
                    size += GeneratedMessage.computeStringSize(1, this.name_);
                }
                if ((this.bitField0_ & 2) != 0) {
                    size += GeneratedMessage.computeStringSize(2, this.insertionPoint_);
                }
                if ((this.bitField0_ & 4) != 0) {
                    size += GeneratedMessage.computeStringSize(15, this.content_);
                }
                if ((this.bitField0_ & 8) != 0) {
                    size += CodedOutputStream.computeMessageSize(16, this.getGeneratedCodeInfo());
                }
                this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
                return size;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == this) {
                    return true;
                }
                if (!(obj instanceof File)) {
                    return super.equals(obj);
                }
                File other = (File)obj;
                if (this.hasName() != other.hasName()) {
                    return false;
                }
                if (this.hasName() && !this.getName().equals(other.getName())) {
                    return false;
                }
                if (this.hasInsertionPoint() != other.hasInsertionPoint()) {
                    return false;
                }
                if (this.hasInsertionPoint() && !this.getInsertionPoint().equals(other.getInsertionPoint())) {
                    return false;
                }
                if (this.hasContent() != other.hasContent()) {
                    return false;
                }
                if (this.hasContent() && !this.getContent().equals(other.getContent())) {
                    return false;
                }
                if (this.hasGeneratedCodeInfo() != other.hasGeneratedCodeInfo()) {
                    return false;
                }
                if (this.hasGeneratedCodeInfo() && !this.getGeneratedCodeInfo().equals(other.getGeneratedCodeInfo())) {
                    return false;
                }
                return this.getUnknownFields().equals(other.getUnknownFields());
            }

            @Override
            public int hashCode() {
                if (this.memoizedHashCode != 0) {
                    return this.memoizedHashCode;
                }
                int hash = 41;
                hash = 19 * hash + File.getDescriptor().hashCode();
                if (this.hasName()) {
                    hash = 37 * hash + 1;
                    hash = 53 * hash + this.getName().hashCode();
                }
                if (this.hasInsertionPoint()) {
                    hash = 37 * hash + 2;
                    hash = 53 * hash + this.getInsertionPoint().hashCode();
                }
                if (this.hasContent()) {
                    hash = 37 * hash + 15;
                    hash = 53 * hash + this.getContent().hashCode();
                }
                if (this.hasGeneratedCodeInfo()) {
                    hash = 37 * hash + 16;
                    hash = 53 * hash + this.getGeneratedCodeInfo().hashCode();
                }
                this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
                return hash;
            }

            public static File parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
                return PARSER.parseFrom(data);
            }

            public static File parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return PARSER.parseFrom(data, extensionRegistry);
            }

            public static File parseFrom(ByteString data) throws InvalidProtocolBufferException {
                return PARSER.parseFrom(data);
            }

            public static File parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return PARSER.parseFrom(data, extensionRegistry);
            }

            public static File parseFrom(byte[] data) throws InvalidProtocolBufferException {
                return PARSER.parseFrom(data);
            }

            public static File parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return PARSER.parseFrom(data, extensionRegistry);
            }

            public static File parseFrom(InputStream input) throws IOException {
                return GeneratedMessage.parseWithIOException(PARSER, input);
            }

            public static File parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
            }

            public static File parseDelimitedFrom(InputStream input) throws IOException {
                return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
            }

            public static File parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
            }

            public static File parseFrom(CodedInputStream input) throws IOException {
                return GeneratedMessage.parseWithIOException(PARSER, input);
            }

            public static File parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
            }

            @Override
            public Builder newBuilderForType() {
                return File.newBuilder();
            }

            public static Builder newBuilder() {
                return DEFAULT_INSTANCE.toBuilder();
            }

            public static Builder newBuilder(File prototype) {
                return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
            }

            @Override
            public Builder toBuilder() {
                return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
            }

            @Override
            protected Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
                Builder builder = new Builder(parent);
                return builder;
            }

            public static File getDefaultInstance() {
                return DEFAULT_INSTANCE;
            }

            public static Parser<File> parser() {
                return PARSER;
            }

            public Parser<File> getParserForType() {
                return PARSER;
            }

            @Override
            public File getDefaultInstanceForType() {
                return DEFAULT_INSTANCE;
            }

            static {
                RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "File");
                DEFAULT_INSTANCE = new File();
                PARSER = new AbstractParser<File>(){

                    @Override
                    public File parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                        Builder builder = File.newBuilder();
                        try {
                            builder.mergeFrom(input, extensionRegistry);
                        }
                        catch (InvalidProtocolBufferException e) {
                            throw e.setUnfinishedMessage(builder.buildPartial());
                        }
                        catch (UninitializedMessageException e) {
                            throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
                        }
                        catch (IOException e) {
                            throw new InvalidProtocolBufferException(e).setUnfinishedMessage(builder.buildPartial());
                        }
                        return builder.buildPartial();
                    }
                };
            }

            public static final class Builder
            extends GeneratedMessage.Builder<Builder>
            implements FileOrBuilder {
                private int bitField0_;
                private Object name_ = "";
                private Object insertionPoint_ = "";
                private Object content_ = "";
                private DescriptorProtos.GeneratedCodeInfo generatedCodeInfo_;
                private SingleFieldBuilder<DescriptorProtos.GeneratedCodeInfo, DescriptorProtos.GeneratedCodeInfo.Builder, DescriptorProtos.GeneratedCodeInfoOrBuilder> generatedCodeInfoBuilder_;

                public static final Descriptors.Descriptor getDescriptor() {
                    return internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_descriptor;
                }

                @Override
                protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                    return internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_fieldAccessorTable.ensureFieldAccessorsInitialized(File.class, Builder.class);
                }

                private Builder() {
                    this.maybeForceBuilderInitialization();
                }

                private Builder(AbstractMessage.BuilderParent parent) {
                    super(parent);
                    this.maybeForceBuilderInitialization();
                }

                private void maybeForceBuilderInitialization() {
                    if (alwaysUseFieldBuilders) {
                        this.internalGetGeneratedCodeInfoFieldBuilder();
                    }
                }

                @Override
                public Builder clear() {
                    super.clear();
                    this.bitField0_ = 0;
                    this.name_ = "";
                    this.insertionPoint_ = "";
                    this.content_ = "";
                    this.generatedCodeInfo_ = null;
                    if (this.generatedCodeInfoBuilder_ != null) {
                        this.generatedCodeInfoBuilder_.dispose();
                        this.generatedCodeInfoBuilder_ = null;
                    }
                    return this;
                }

                @Override
                public Descriptors.Descriptor getDescriptorForType() {
                    return internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_descriptor;
                }

                @Override
                public File getDefaultInstanceForType() {
                    return File.getDefaultInstance();
                }

                @Override
                public File build() {
                    File result = this.buildPartial();
                    if (!result.isInitialized()) {
                        throw Builder.newUninitializedMessageException(result);
                    }
                    return result;
                }

                @Override
                public File buildPartial() {
                    File result = new File(this);
                    if (this.bitField0_ != 0) {
                        this.buildPartial0(result);
                    }
                    this.onBuilt();
                    return result;
                }

                private void buildPartial0(File result) {
                    int from_bitField0_ = this.bitField0_;
                    int to_bitField0_ = 0;
                    if ((from_bitField0_ & 1) != 0) {
                        result.name_ = this.name_;
                        to_bitField0_ |= 1;
                    }
                    if ((from_bitField0_ & 2) != 0) {
                        result.insertionPoint_ = this.insertionPoint_;
                        to_bitField0_ |= 2;
                    }
                    if ((from_bitField0_ & 4) != 0) {
                        result.content_ = this.content_;
                        to_bitField0_ |= 4;
                    }
                    if ((from_bitField0_ & 8) != 0) {
                        result.generatedCodeInfo_ = this.generatedCodeInfoBuilder_ == null ? this.generatedCodeInfo_ : this.generatedCodeInfoBuilder_.build();
                        to_bitField0_ |= 8;
                    }
                    result.bitField0_ |= to_bitField0_;
                }

                @Override
                public Builder mergeFrom(Message other) {
                    if (other instanceof File) {
                        return this.mergeFrom((File)other);
                    }
                    super.mergeFrom(other);
                    return this;
                }

                public Builder mergeFrom(File other) {
                    if (other == File.getDefaultInstance()) {
                        return this;
                    }
                    if (other.hasName()) {
                        this.name_ = other.name_;
                        this.bitField0_ |= 1;
                        this.onChanged();
                    }
                    if (other.hasInsertionPoint()) {
                        this.insertionPoint_ = other.insertionPoint_;
                        this.bitField0_ |= 2;
                        this.onChanged();
                    }
                    if (other.hasContent()) {
                        this.content_ = other.content_;
                        this.bitField0_ |= 4;
                        this.onChanged();
                    }
                    if (other.hasGeneratedCodeInfo()) {
                        this.mergeGeneratedCodeInfo(other.getGeneratedCodeInfo());
                    }
                    this.mergeUnknownFields(other.getUnknownFields());
                    this.onChanged();
                    return this;
                }

                @Override
                public final boolean isInitialized() {
                    return true;
                }

                @Override
                public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                    if (extensionRegistry == null) {
                        throw new NullPointerException();
                    }
                    try {
                        boolean done = false;
                        block12: while (!done) {
                            int tag = input.readTag();
                            switch (tag) {
                                case 0: {
                                    done = true;
                                    continue block12;
                                }
                                case 10: {
                                    this.name_ = input.readBytes();
                                    this.bitField0_ |= 1;
                                    continue block12;
                                }
                                case 18: {
                                    this.insertionPoint_ = input.readBytes();
                                    this.bitField0_ |= 2;
                                    continue block12;
                                }
                                case 122: {
                                    this.content_ = input.readBytes();
                                    this.bitField0_ |= 4;
                                    continue block12;
                                }
                                case 130: {
                                    input.readMessage(this.internalGetGeneratedCodeInfoFieldBuilder().getBuilder(), extensionRegistry);
                                    this.bitField0_ |= 8;
                                    continue block12;
                                }
                            }
                            if (super.parseUnknownField(input, extensionRegistry, tag)) continue;
                            done = true;
                        }
                    }
                    catch (InvalidProtocolBufferException e) {
                        throw e.unwrapIOException();
                    }
                    finally {
                        this.onChanged();
                    }
                    return this;
                }

                @Override
                public boolean hasName() {
                    return (this.bitField0_ & 1) != 0;
                }

                @Override
                public String getName() {
                    Object ref = this.name_;
                    if (!(ref instanceof String)) {
                        ByteString bs = (ByteString)ref;
                        String s = bs.toStringUtf8();
                        if (bs.isValidUtf8()) {
                            this.name_ = s;
                        }
                        return s;
                    }
                    return (String)ref;
                }

                @Override
                public ByteString getNameBytes() {
                    Object ref = this.name_;
                    if (ref instanceof String) {
                        ByteString b = ByteString.copyFromUtf8((String)ref);
                        this.name_ = b;
                        return b;
                    }
                    return (ByteString)ref;
                }

                public Builder setName(String value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.name_ = value;
                    this.bitField0_ |= 1;
                    this.onChanged();
                    return this;
                }

                public Builder clearName() {
                    this.name_ = File.getDefaultInstance().getName();
                    this.bitField0_ &= 0xFFFFFFFE;
                    this.onChanged();
                    return this;
                }

                public Builder setNameBytes(ByteString value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.name_ = value;
                    this.bitField0_ |= 1;
                    this.onChanged();
                    return this;
                }

                @Override
                public boolean hasInsertionPoint() {
                    return (this.bitField0_ & 2) != 0;
                }

                @Override
                public String getInsertionPoint() {
                    Object ref = this.insertionPoint_;
                    if (!(ref instanceof String)) {
                        ByteString bs = (ByteString)ref;
                        String s = bs.toStringUtf8();
                        if (bs.isValidUtf8()) {
                            this.insertionPoint_ = s;
                        }
                        return s;
                    }
                    return (String)ref;
                }

                @Override
                public ByteString getInsertionPointBytes() {
                    Object ref = this.insertionPoint_;
                    if (ref instanceof String) {
                        ByteString b = ByteString.copyFromUtf8((String)ref);
                        this.insertionPoint_ = b;
                        return b;
                    }
                    return (ByteString)ref;
                }

                public Builder setInsertionPoint(String value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.insertionPoint_ = value;
                    this.bitField0_ |= 2;
                    this.onChanged();
                    return this;
                }

                public Builder clearInsertionPoint() {
                    this.insertionPoint_ = File.getDefaultInstance().getInsertionPoint();
                    this.bitField0_ &= 0xFFFFFFFD;
                    this.onChanged();
                    return this;
                }

                public Builder setInsertionPointBytes(ByteString value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.insertionPoint_ = value;
                    this.bitField0_ |= 2;
                    this.onChanged();
                    return this;
                }

                @Override
                public boolean hasContent() {
                    return (this.bitField0_ & 4) != 0;
                }

                @Override
                public String getContent() {
                    Object ref = this.content_;
                    if (!(ref instanceof String)) {
                        ByteString bs = (ByteString)ref;
                        String s = bs.toStringUtf8();
                        if (bs.isValidUtf8()) {
                            this.content_ = s;
                        }
                        return s;
                    }
                    return (String)ref;
                }

                @Override
                public ByteString getContentBytes() {
                    Object ref = this.content_;
                    if (ref instanceof String) {
                        ByteString b = ByteString.copyFromUtf8((String)ref);
                        this.content_ = b;
                        return b;
                    }
                    return (ByteString)ref;
                }

                public Builder setContent(String value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.content_ = value;
                    this.bitField0_ |= 4;
                    this.onChanged();
                    return this;
                }

                public Builder clearContent() {
                    this.content_ = File.getDefaultInstance().getContent();
                    this.bitField0_ &= 0xFFFFFFFB;
                    this.onChanged();
                    return this;
                }

                public Builder setContentBytes(ByteString value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.content_ = value;
                    this.bitField0_ |= 4;
                    this.onChanged();
                    return this;
                }

                @Override
                public boolean hasGeneratedCodeInfo() {
                    return (this.bitField0_ & 8) != 0;
                }

                @Override
                public DescriptorProtos.GeneratedCodeInfo getGeneratedCodeInfo() {
                    if (this.generatedCodeInfoBuilder_ == null) {
                        return this.generatedCodeInfo_ == null ? DescriptorProtos.GeneratedCodeInfo.getDefaultInstance() : this.generatedCodeInfo_;
                    }
                    return this.generatedCodeInfoBuilder_.getMessage();
                }

                public Builder setGeneratedCodeInfo(DescriptorProtos.GeneratedCodeInfo value) {
                    if (this.generatedCodeInfoBuilder_ == null) {
                        if (value == null) {
                            throw new NullPointerException();
                        }
                        this.generatedCodeInfo_ = value;
                    } else {
                        this.generatedCodeInfoBuilder_.setMessage(value);
                    }
                    this.bitField0_ |= 8;
                    this.onChanged();
                    return this;
                }

                public Builder setGeneratedCodeInfo(DescriptorProtos.GeneratedCodeInfo.Builder builderForValue) {
                    if (this.generatedCodeInfoBuilder_ == null) {
                        this.generatedCodeInfo_ = builderForValue.build();
                    } else {
                        this.generatedCodeInfoBuilder_.setMessage(builderForValue.build());
                    }
                    this.bitField0_ |= 8;
                    this.onChanged();
                    return this;
                }

                public Builder mergeGeneratedCodeInfo(DescriptorProtos.GeneratedCodeInfo value) {
                    if (this.generatedCodeInfoBuilder_ == null) {
                        if ((this.bitField0_ & 8) != 0 && this.generatedCodeInfo_ != null && this.generatedCodeInfo_ != DescriptorProtos.GeneratedCodeInfo.getDefaultInstance()) {
                            this.getGeneratedCodeInfoBuilder().mergeFrom(value);
                        } else {
                            this.generatedCodeInfo_ = value;
                        }
                    } else {
                        this.generatedCodeInfoBuilder_.mergeFrom(value);
                    }
                    if (this.generatedCodeInfo_ != null) {
                        this.bitField0_ |= 8;
                        this.onChanged();
                    }
                    return this;
                }

                public Builder clearGeneratedCodeInfo() {
                    this.bitField0_ &= 0xFFFFFFF7;
                    this.generatedCodeInfo_ = null;
                    if (this.generatedCodeInfoBuilder_ != null) {
                        this.generatedCodeInfoBuilder_.dispose();
                        this.generatedCodeInfoBuilder_ = null;
                    }
                    this.onChanged();
                    return this;
                }

                public DescriptorProtos.GeneratedCodeInfo.Builder getGeneratedCodeInfoBuilder() {
                    this.bitField0_ |= 8;
                    this.onChanged();
                    return this.internalGetGeneratedCodeInfoFieldBuilder().getBuilder();
                }

                @Override
                public DescriptorProtos.GeneratedCodeInfoOrBuilder getGeneratedCodeInfoOrBuilder() {
                    if (this.generatedCodeInfoBuilder_ != null) {
                        return this.generatedCodeInfoBuilder_.getMessageOrBuilder();
                    }
                    return this.generatedCodeInfo_ == null ? DescriptorProtos.GeneratedCodeInfo.getDefaultInstance() : this.generatedCodeInfo_;
                }

                private SingleFieldBuilder<DescriptorProtos.GeneratedCodeInfo, DescriptorProtos.GeneratedCodeInfo.Builder, DescriptorProtos.GeneratedCodeInfoOrBuilder> internalGetGeneratedCodeInfoFieldBuilder() {
                    if (this.generatedCodeInfoBuilder_ == null) {
                        this.generatedCodeInfoBuilder_ = new SingleFieldBuilder(this.getGeneratedCodeInfo(), this.getParentForChildren(), this.isClean());
                        this.generatedCodeInfo_ = null;
                    }
                    return this.generatedCodeInfoBuilder_;
                }
            }
        }

        public static interface FileOrBuilder
        extends MessageOrBuilder {
            public boolean hasName();

            public String getName();

            public ByteString getNameBytes();

            public boolean hasInsertionPoint();

            public String getInsertionPoint();

            public ByteString getInsertionPointBytes();

            public boolean hasContent();

            public String getContent();

            public ByteString getContentBytes();

            public boolean hasGeneratedCodeInfo();

            public DescriptorProtos.GeneratedCodeInfo getGeneratedCodeInfo();

            public DescriptorProtos.GeneratedCodeInfoOrBuilder getGeneratedCodeInfoOrBuilder();
        }

        public static enum Feature implements ProtocolMessageEnum
        {
            FEATURE_NONE(0),
            FEATURE_PROTO3_OPTIONAL(1),
            FEATURE_SUPPORTS_EDITIONS(2);

            public static final int FEATURE_NONE_VALUE = 0;
            public static final int FEATURE_PROTO3_OPTIONAL_VALUE = 1;
            public static final int FEATURE_SUPPORTS_EDITIONS_VALUE = 2;
            private static final Internal.EnumLiteMap<Feature> internalValueMap;
            private static final Feature[] VALUES;
            private final int value;

            @Override
            public final int getNumber() {
                return this.value;
            }

            @Deprecated
            public static Feature valueOf(int value) {
                return Feature.forNumber(value);
            }

            public static Feature forNumber(int value) {
                switch (value) {
                    case 0: {
                        return FEATURE_NONE;
                    }
                    case 1: {
                        return FEATURE_PROTO3_OPTIONAL;
                    }
                    case 2: {
                        return FEATURE_SUPPORTS_EDITIONS;
                    }
                }
                return null;
            }

            public static Internal.EnumLiteMap<Feature> internalGetValueMap() {
                return internalValueMap;
            }

            @Override
            public final Descriptors.EnumValueDescriptor getValueDescriptor() {
                return Feature.getDescriptor().getValues().get(this.ordinal());
            }

            @Override
            public final Descriptors.EnumDescriptor getDescriptorForType() {
                return Feature.getDescriptor();
            }

            public static Descriptors.EnumDescriptor getDescriptor() {
                return CodeGeneratorResponse.getDescriptor().getEnumTypes().get(0);
            }

            public static Feature valueOf(Descriptors.EnumValueDescriptor desc) {
                if (desc.getType() != Feature.getDescriptor()) {
                    throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
                }
                return VALUES[desc.getIndex()];
            }

            private Feature(int value) {
                this.value = value;
            }

            static {
                RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "Feature");
                internalValueMap = new Internal.EnumLiteMap<Feature>(){

                    @Override
                    public Feature findValueByNumber(int number) {
                        return Feature.forNumber(number);
                    }
                };
                VALUES = Feature.values();
            }
        }
    }

    public static interface CodeGeneratorResponseOrBuilder
    extends MessageOrBuilder {
        public boolean hasError();

        public String getError();

        public ByteString getErrorBytes();

        public boolean hasSupportedFeatures();

        public long getSupportedFeatures();

        public boolean hasMinimumEdition();

        public int getMinimumEdition();

        public boolean hasMaximumEdition();

        public int getMaximumEdition();

        public List<CodeGeneratorResponse.File> getFileList();

        public CodeGeneratorResponse.File getFile(int var1);

        public int getFileCount();

        public List<? extends CodeGeneratorResponse.FileOrBuilder> getFileOrBuilderList();

        public CodeGeneratorResponse.FileOrBuilder getFileOrBuilder(int var1);
    }

    public static final class CodeGeneratorRequest
    extends GeneratedMessage
    implements CodeGeneratorRequestOrBuilder {
        private static final long serialVersionUID = 0L;
        private int bitField0_;
        public static final int FILE_TO_GENERATE_FIELD_NUMBER = 1;
        private LazyStringArrayList fileToGenerate_ = LazyStringArrayList.emptyList();
        public static final int PARAMETER_FIELD_NUMBER = 2;
        private volatile Object parameter_ = "";
        public static final int PROTO_FILE_FIELD_NUMBER = 15;
        private List<DescriptorProtos.FileDescriptorProto> protoFile_;
        public static final int SOURCE_FILE_DESCRIPTORS_FIELD_NUMBER = 17;
        private List<DescriptorProtos.FileDescriptorProto> sourceFileDescriptors_;
        public static final int COMPILER_VERSION_FIELD_NUMBER = 3;
        private Version compilerVersion_;
        private byte memoizedIsInitialized = (byte)-1;
        private static final CodeGeneratorRequest DEFAULT_INSTANCE;
        private static final Parser<CodeGeneratorRequest> PARSER;

        private CodeGeneratorRequest(GeneratedMessage.Builder<?> builder) {
            super(builder);
        }

        private CodeGeneratorRequest() {
            this.fileToGenerate_ = LazyStringArrayList.emptyList();
            this.parameter_ = "";
            this.protoFile_ = Collections.emptyList();
            this.sourceFileDescriptors_ = Collections.emptyList();
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return internal_static_google_protobuf_compiler_CodeGeneratorRequest_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return internal_static_google_protobuf_compiler_CodeGeneratorRequest_fieldAccessorTable.ensureFieldAccessorsInitialized(CodeGeneratorRequest.class, Builder.class);
        }

        public ProtocolStringList getFileToGenerateList() {
            return this.fileToGenerate_;
        }

        @Override
        public int getFileToGenerateCount() {
            return this.fileToGenerate_.size();
        }

        @Override
        public String getFileToGenerate(int index) {
            return this.fileToGenerate_.get(index);
        }

        @Override
        public ByteString getFileToGenerateBytes(int index) {
            return this.fileToGenerate_.getByteString(index);
        }

        @Override
        public boolean hasParameter() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override
        public String getParameter() {
            Object ref = this.parameter_;
            if (ref instanceof String) {
                return (String)ref;
            }
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.parameter_ = s;
            }
            return s;
        }

        @Override
        public ByteString getParameterBytes() {
            Object ref = this.parameter_;
            if (ref instanceof String) {
                ByteString b = ByteString.copyFromUtf8((String)ref);
                this.parameter_ = b;
                return b;
            }
            return (ByteString)ref;
        }

        @Override
        public List<DescriptorProtos.FileDescriptorProto> getProtoFileList() {
            return this.protoFile_;
        }

        @Override
        public List<? extends DescriptorProtos.FileDescriptorProtoOrBuilder> getProtoFileOrBuilderList() {
            return this.protoFile_;
        }

        @Override
        public int getProtoFileCount() {
            return this.protoFile_.size();
        }

        @Override
        public DescriptorProtos.FileDescriptorProto getProtoFile(int index) {
            return this.protoFile_.get(index);
        }

        @Override
        public DescriptorProtos.FileDescriptorProtoOrBuilder getProtoFileOrBuilder(int index) {
            return this.protoFile_.get(index);
        }

        @Override
        public List<DescriptorProtos.FileDescriptorProto> getSourceFileDescriptorsList() {
            return this.sourceFileDescriptors_;
        }

        @Override
        public List<? extends DescriptorProtos.FileDescriptorProtoOrBuilder> getSourceFileDescriptorsOrBuilderList() {
            return this.sourceFileDescriptors_;
        }

        @Override
        public int getSourceFileDescriptorsCount() {
            return this.sourceFileDescriptors_.size();
        }

        @Override
        public DescriptorProtos.FileDescriptorProto getSourceFileDescriptors(int index) {
            return this.sourceFileDescriptors_.get(index);
        }

        @Override
        public DescriptorProtos.FileDescriptorProtoOrBuilder getSourceFileDescriptorsOrBuilder(int index) {
            return this.sourceFileDescriptors_.get(index);
        }

        @Override
        public boolean hasCompilerVersion() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override
        public Version getCompilerVersion() {
            return this.compilerVersion_ == null ? Version.getDefaultInstance() : this.compilerVersion_;
        }

        @Override
        public VersionOrBuilder getCompilerVersionOrBuilder() {
            return this.compilerVersion_ == null ? Version.getDefaultInstance() : this.compilerVersion_;
        }

        @Override
        public final boolean isInitialized() {
            int i;
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
                return true;
            }
            if (isInitialized == 0) {
                return false;
            }
            for (i = 0; i < this.getProtoFileCount(); ++i) {
                if (this.getProtoFile(i).isInitialized()) continue;
                this.memoizedIsInitialized = 0;
                return false;
            }
            for (i = 0; i < this.getSourceFileDescriptorsCount(); ++i) {
                if (this.getSourceFileDescriptors(i).isInitialized()) continue;
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }

        @Override
        public void writeTo(CodedOutputStream output) throws IOException {
            int i;
            for (i = 0; i < this.fileToGenerate_.size(); ++i) {
                GeneratedMessage.writeString(output, 1, this.fileToGenerate_.getRaw(i));
            }
            if ((this.bitField0_ & 1) != 0) {
                GeneratedMessage.writeString(output, 2, this.parameter_);
            }
            if ((this.bitField0_ & 2) != 0) {
                output.writeMessage(3, this.getCompilerVersion());
            }
            for (i = 0; i < this.protoFile_.size(); ++i) {
                output.writeMessage(15, this.protoFile_.get(i));
            }
            for (i = 0; i < this.sourceFileDescriptors_.size(); ++i) {
                output.writeMessage(17, this.sourceFileDescriptors_.get(i));
            }
            this.getUnknownFields().writeTo(output);
        }

        @Override
        public int getSerializedSize() {
            int i;
            int size = this.memoizedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
            int dataSize = 0;
            for (int i2 = 0; i2 < this.fileToGenerate_.size(); ++i2) {
                dataSize += CodeGeneratorRequest.computeStringSizeNoTag(this.fileToGenerate_.getRaw(i2));
            }
            size += dataSize;
            size += 1 * this.getFileToGenerateList().size();
            if ((this.bitField0_ & 1) != 0) {
                size += GeneratedMessage.computeStringSize(2, this.parameter_);
            }
            if ((this.bitField0_ & 2) != 0) {
                size += CodedOutputStream.computeMessageSize(3, this.getCompilerVersion());
            }
            for (i = 0; i < this.protoFile_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(15, this.protoFile_.get(i));
            }
            for (i = 0; i < this.sourceFileDescriptors_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(17, this.sourceFileDescriptors_.get(i));
            }
            this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
            return size;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof CodeGeneratorRequest)) {
                return super.equals(obj);
            }
            CodeGeneratorRequest other = (CodeGeneratorRequest)obj;
            if (!this.getFileToGenerateList().equals(other.getFileToGenerateList())) {
                return false;
            }
            if (this.hasParameter() != other.hasParameter()) {
                return false;
            }
            if (this.hasParameter() && !this.getParameter().equals(other.getParameter())) {
                return false;
            }
            if (!this.getProtoFileList().equals(other.getProtoFileList())) {
                return false;
            }
            if (!this.getSourceFileDescriptorsList().equals(other.getSourceFileDescriptorsList())) {
                return false;
            }
            if (this.hasCompilerVersion() != other.hasCompilerVersion()) {
                return false;
            }
            if (this.hasCompilerVersion() && !this.getCompilerVersion().equals(other.getCompilerVersion())) {
                return false;
            }
            return this.getUnknownFields().equals(other.getUnknownFields());
        }

        @Override
        public int hashCode() {
            if (this.memoizedHashCode != 0) {
                return this.memoizedHashCode;
            }
            int hash = 41;
            hash = 19 * hash + CodeGeneratorRequest.getDescriptor().hashCode();
            if (this.getFileToGenerateCount() > 0) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getFileToGenerateList().hashCode();
            }
            if (this.hasParameter()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getParameter().hashCode();
            }
            if (this.getProtoFileCount() > 0) {
                hash = 37 * hash + 15;
                hash = 53 * hash + this.getProtoFileList().hashCode();
            }
            if (this.getSourceFileDescriptorsCount() > 0) {
                hash = 37 * hash + 17;
                hash = 53 * hash + this.getSourceFileDescriptorsList().hashCode();
            }
            if (this.hasCompilerVersion()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getCompilerVersion().hashCode();
            }
            this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
            return hash;
        }

        public static CodeGeneratorRequest parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static CodeGeneratorRequest parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static CodeGeneratorRequest parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static CodeGeneratorRequest parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static CodeGeneratorRequest parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static CodeGeneratorRequest parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static CodeGeneratorRequest parseFrom(InputStream input) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input);
        }

        public static CodeGeneratorRequest parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static CodeGeneratorRequest parseDelimitedFrom(InputStream input) throws IOException {
            return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
        }

        public static CodeGeneratorRequest parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static CodeGeneratorRequest parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input);
        }

        public static CodeGeneratorRequest parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
        }

        @Override
        public Builder newBuilderForType() {
            return CodeGeneratorRequest.newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(CodeGeneratorRequest prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        @Override
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
        }

        @Override
        protected Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        public static CodeGeneratorRequest getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<CodeGeneratorRequest> parser() {
            return PARSER;
        }

        public Parser<CodeGeneratorRequest> getParserForType() {
            return PARSER;
        }

        @Override
        public CodeGeneratorRequest getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

        static {
            RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "CodeGeneratorRequest");
            DEFAULT_INSTANCE = new CodeGeneratorRequest();
            PARSER = new AbstractParser<CodeGeneratorRequest>(){

                @Override
                public CodeGeneratorRequest parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    Builder builder = CodeGeneratorRequest.newBuilder();
                    try {
                        builder.mergeFrom(input, extensionRegistry);
                    }
                    catch (InvalidProtocolBufferException e) {
                        throw e.setUnfinishedMessage(builder.buildPartial());
                    }
                    catch (UninitializedMessageException e) {
                        throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
                    }
                    catch (IOException e) {
                        throw new InvalidProtocolBufferException(e).setUnfinishedMessage(builder.buildPartial());
                    }
                    return builder.buildPartial();
                }
            };
        }

        public static final class Builder
        extends GeneratedMessage.Builder<Builder>
        implements CodeGeneratorRequestOrBuilder {
            private int bitField0_;
            private LazyStringArrayList fileToGenerate_ = LazyStringArrayList.emptyList();
            private Object parameter_ = "";
            private List<DescriptorProtos.FileDescriptorProto> protoFile_ = Collections.emptyList();
            private RepeatedFieldBuilder<DescriptorProtos.FileDescriptorProto, DescriptorProtos.FileDescriptorProto.Builder, DescriptorProtos.FileDescriptorProtoOrBuilder> protoFileBuilder_;
            private List<DescriptorProtos.FileDescriptorProto> sourceFileDescriptors_ = Collections.emptyList();
            private RepeatedFieldBuilder<DescriptorProtos.FileDescriptorProto, DescriptorProtos.FileDescriptorProto.Builder, DescriptorProtos.FileDescriptorProtoOrBuilder> sourceFileDescriptorsBuilder_;
            private Version compilerVersion_;
            private SingleFieldBuilder<Version, Version.Builder, VersionOrBuilder> compilerVersionBuilder_;

            public static final Descriptors.Descriptor getDescriptor() {
                return internal_static_google_protobuf_compiler_CodeGeneratorRequest_descriptor;
            }

            @Override
            protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return internal_static_google_protobuf_compiler_CodeGeneratorRequest_fieldAccessorTable.ensureFieldAccessorsInitialized(CodeGeneratorRequest.class, Builder.class);
            }

            private Builder() {
                this.maybeForceBuilderInitialization();
            }

            private Builder(AbstractMessage.BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (alwaysUseFieldBuilders) {
                    this.internalGetProtoFileFieldBuilder();
                    this.internalGetSourceFileDescriptorsFieldBuilder();
                    this.internalGetCompilerVersionFieldBuilder();
                }
            }

            @Override
            public Builder clear() {
                super.clear();
                this.bitField0_ = 0;
                this.fileToGenerate_ = LazyStringArrayList.emptyList();
                this.parameter_ = "";
                if (this.protoFileBuilder_ == null) {
                    this.protoFile_ = Collections.emptyList();
                } else {
                    this.protoFile_ = null;
                    this.protoFileBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFB;
                if (this.sourceFileDescriptorsBuilder_ == null) {
                    this.sourceFileDescriptors_ = Collections.emptyList();
                } else {
                    this.sourceFileDescriptors_ = null;
                    this.sourceFileDescriptorsBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFF7;
                this.compilerVersion_ = null;
                if (this.compilerVersionBuilder_ != null) {
                    this.compilerVersionBuilder_.dispose();
                    this.compilerVersionBuilder_ = null;
                }
                return this;
            }

            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return internal_static_google_protobuf_compiler_CodeGeneratorRequest_descriptor;
            }

            @Override
            public CodeGeneratorRequest getDefaultInstanceForType() {
                return CodeGeneratorRequest.getDefaultInstance();
            }

            @Override
            public CodeGeneratorRequest build() {
                CodeGeneratorRequest result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw Builder.newUninitializedMessageException(result);
                }
                return result;
            }

            @Override
            public CodeGeneratorRequest buildPartial() {
                CodeGeneratorRequest result = new CodeGeneratorRequest(this);
                this.buildPartialRepeatedFields(result);
                if (this.bitField0_ != 0) {
                    this.buildPartial0(result);
                }
                this.onBuilt();
                return result;
            }

            private void buildPartialRepeatedFields(CodeGeneratorRequest result) {
                if (this.protoFileBuilder_ == null) {
                    if ((this.bitField0_ & 4) != 0) {
                        this.protoFile_ = Collections.unmodifiableList(this.protoFile_);
                        this.bitField0_ &= 0xFFFFFFFB;
                    }
                    result.protoFile_ = this.protoFile_;
                } else {
                    result.protoFile_ = this.protoFileBuilder_.build();
                }
                if (this.sourceFileDescriptorsBuilder_ == null) {
                    if ((this.bitField0_ & 8) != 0) {
                        this.sourceFileDescriptors_ = Collections.unmodifiableList(this.sourceFileDescriptors_);
                        this.bitField0_ &= 0xFFFFFFF7;
                    }
                    result.sourceFileDescriptors_ = this.sourceFileDescriptors_;
                } else {
                    result.sourceFileDescriptors_ = this.sourceFileDescriptorsBuilder_.build();
                }
            }

            private void buildPartial0(CodeGeneratorRequest result) {
                int from_bitField0_ = this.bitField0_;
                if ((from_bitField0_ & 1) != 0) {
                    this.fileToGenerate_.makeImmutable();
                    result.fileToGenerate_ = this.fileToGenerate_;
                }
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 2) != 0) {
                    result.parameter_ = this.parameter_;
                    to_bitField0_ |= 1;
                }
                if ((from_bitField0_ & 0x10) != 0) {
                    result.compilerVersion_ = this.compilerVersionBuilder_ == null ? this.compilerVersion_ : this.compilerVersionBuilder_.build();
                    to_bitField0_ |= 2;
                }
                result.bitField0_ |= to_bitField0_;
            }

            @Override
            public Builder mergeFrom(Message other) {
                if (other instanceof CodeGeneratorRequest) {
                    return this.mergeFrom((CodeGeneratorRequest)other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(CodeGeneratorRequest other) {
                if (other == CodeGeneratorRequest.getDefaultInstance()) {
                    return this;
                }
                if (!other.fileToGenerate_.isEmpty()) {
                    if (this.fileToGenerate_.isEmpty()) {
                        this.fileToGenerate_ = other.fileToGenerate_;
                        this.bitField0_ |= 1;
                    } else {
                        this.ensureFileToGenerateIsMutable();
                        this.fileToGenerate_.addAll(other.fileToGenerate_);
                    }
                    this.onChanged();
                }
                if (other.hasParameter()) {
                    this.parameter_ = other.parameter_;
                    this.bitField0_ |= 2;
                    this.onChanged();
                }
                if (this.protoFileBuilder_ == null) {
                    if (!other.protoFile_.isEmpty()) {
                        if (this.protoFile_.isEmpty()) {
                            this.protoFile_ = other.protoFile_;
                            this.bitField0_ &= 0xFFFFFFFB;
                        } else {
                            this.ensureProtoFileIsMutable();
                            this.protoFile_.addAll(other.protoFile_);
                        }
                        this.onChanged();
                    }
                } else if (!other.protoFile_.isEmpty()) {
                    if (this.protoFileBuilder_.isEmpty()) {
                        this.protoFileBuilder_.dispose();
                        this.protoFileBuilder_ = null;
                        this.protoFile_ = other.protoFile_;
                        this.bitField0_ &= 0xFFFFFFFB;
                        this.protoFileBuilder_ = alwaysUseFieldBuilders ? this.internalGetProtoFileFieldBuilder() : null;
                    } else {
                        this.protoFileBuilder_.addAllMessages(other.protoFile_);
                    }
                }
                if (this.sourceFileDescriptorsBuilder_ == null) {
                    if (!other.sourceFileDescriptors_.isEmpty()) {
                        if (this.sourceFileDescriptors_.isEmpty()) {
                            this.sourceFileDescriptors_ = other.sourceFileDescriptors_;
                            this.bitField0_ &= 0xFFFFFFF7;
                        } else {
                            this.ensureSourceFileDescriptorsIsMutable();
                            this.sourceFileDescriptors_.addAll(other.sourceFileDescriptors_);
                        }
                        this.onChanged();
                    }
                } else if (!other.sourceFileDescriptors_.isEmpty()) {
                    if (this.sourceFileDescriptorsBuilder_.isEmpty()) {
                        this.sourceFileDescriptorsBuilder_.dispose();
                        this.sourceFileDescriptorsBuilder_ = null;
                        this.sourceFileDescriptors_ = other.sourceFileDescriptors_;
                        this.bitField0_ &= 0xFFFFFFF7;
                        this.sourceFileDescriptorsBuilder_ = alwaysUseFieldBuilders ? this.internalGetSourceFileDescriptorsFieldBuilder() : null;
                    } else {
                        this.sourceFileDescriptorsBuilder_.addAllMessages(other.sourceFileDescriptors_);
                    }
                }
                if (other.hasCompilerVersion()) {
                    this.mergeCompilerVersion(other.getCompilerVersion());
                }
                this.mergeUnknownFields(other.getUnknownFields());
                this.onChanged();
                return this;
            }

            @Override
            public final boolean isInitialized() {
                int i;
                for (i = 0; i < this.getProtoFileCount(); ++i) {
                    if (this.getProtoFile(i).isInitialized()) continue;
                    return false;
                }
                for (i = 0; i < this.getSourceFileDescriptorsCount(); ++i) {
                    if (this.getSourceFileDescriptors(i).isInitialized()) continue;
                    return false;
                }
                return true;
            }

            @Override
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                if (extensionRegistry == null) {
                    throw new NullPointerException();
                }
                try {
                    boolean done = false;
                    block13: while (!done) {
                        int tag = input.readTag();
                        switch (tag) {
                            case 0: {
                                done = true;
                                continue block13;
                            }
                            case 10: {
                                ByteString bs = input.readBytes();
                                this.ensureFileToGenerateIsMutable();
                                this.fileToGenerate_.add(bs);
                                continue block13;
                            }
                            case 18: {
                                this.parameter_ = input.readBytes();
                                this.bitField0_ |= 2;
                                continue block13;
                            }
                            case 26: {
                                input.readMessage(this.internalGetCompilerVersionFieldBuilder().getBuilder(), extensionRegistry);
                                this.bitField0_ |= 0x10;
                                continue block13;
                            }
                            case 122: {
                                DescriptorProtos.FileDescriptorProto m = input.readMessage(DescriptorProtos.FileDescriptorProto.parser(), extensionRegistry);
                                if (this.protoFileBuilder_ == null) {
                                    this.ensureProtoFileIsMutable();
                                    this.protoFile_.add(m);
                                    continue block13;
                                }
                                this.protoFileBuilder_.addMessage(m);
                                continue block13;
                            }
                            case 138: {
                                DescriptorProtos.FileDescriptorProto m = input.readMessage(DescriptorProtos.FileDescriptorProto.parser(), extensionRegistry);
                                if (this.sourceFileDescriptorsBuilder_ == null) {
                                    this.ensureSourceFileDescriptorsIsMutable();
                                    this.sourceFileDescriptors_.add(m);
                                    continue block13;
                                }
                                this.sourceFileDescriptorsBuilder_.addMessage(m);
                                continue block13;
                            }
                        }
                        if (super.parseUnknownField(input, extensionRegistry, tag)) continue;
                        done = true;
                    }
                }
                catch (InvalidProtocolBufferException e) {
                    throw e.unwrapIOException();
                }
                finally {
                    this.onChanged();
                }
                return this;
            }

            private void ensureFileToGenerateIsMutable() {
                if (!this.fileToGenerate_.isModifiable()) {
                    this.fileToGenerate_ = new LazyStringArrayList(this.fileToGenerate_);
                }
                this.bitField0_ |= 1;
            }

            public ProtocolStringList getFileToGenerateList() {
                this.fileToGenerate_.makeImmutable();
                return this.fileToGenerate_;
            }

            @Override
            public int getFileToGenerateCount() {
                return this.fileToGenerate_.size();
            }

            @Override
            public String getFileToGenerate(int index) {
                return this.fileToGenerate_.get(index);
            }

            @Override
            public ByteString getFileToGenerateBytes(int index) {
                return this.fileToGenerate_.getByteString(index);
            }

            public Builder setFileToGenerate(int index, String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureFileToGenerateIsMutable();
                this.fileToGenerate_.set(index, value);
                this.bitField0_ |= 1;
                this.onChanged();
                return this;
            }

            public Builder addFileToGenerate(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureFileToGenerateIsMutable();
                this.fileToGenerate_.add(value);
                this.bitField0_ |= 1;
                this.onChanged();
                return this;
            }

            public Builder addAllFileToGenerate(Iterable<String> values) {
                this.ensureFileToGenerateIsMutable();
                AbstractMessageLite.Builder.addAll(values, this.fileToGenerate_);
                this.bitField0_ |= 1;
                this.onChanged();
                return this;
            }

            public Builder clearFileToGenerate() {
                this.fileToGenerate_ = LazyStringArrayList.emptyList();
                this.bitField0_ &= 0xFFFFFFFE;
                this.onChanged();
                return this;
            }

            public Builder addFileToGenerateBytes(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureFileToGenerateIsMutable();
                this.fileToGenerate_.add(value);
                this.bitField0_ |= 1;
                this.onChanged();
                return this;
            }

            @Override
            public boolean hasParameter() {
                return (this.bitField0_ & 2) != 0;
            }

            @Override
            public String getParameter() {
                Object ref = this.parameter_;
                if (!(ref instanceof String)) {
                    ByteString bs = (ByteString)ref;
                    String s = bs.toStringUtf8();
                    if (bs.isValidUtf8()) {
                        this.parameter_ = s;
                    }
                    return s;
                }
                return (String)ref;
            }

            @Override
            public ByteString getParameterBytes() {
                Object ref = this.parameter_;
                if (ref instanceof String) {
                    ByteString b = ByteString.copyFromUtf8((String)ref);
                    this.parameter_ = b;
                    return b;
                }
                return (ByteString)ref;
            }

            public Builder setParameter(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.parameter_ = value;
                this.bitField0_ |= 2;
                this.onChanged();
                return this;
            }

            public Builder clearParameter() {
                this.parameter_ = CodeGeneratorRequest.getDefaultInstance().getParameter();
                this.bitField0_ &= 0xFFFFFFFD;
                this.onChanged();
                return this;
            }

            public Builder setParameterBytes(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.parameter_ = value;
                this.bitField0_ |= 2;
                this.onChanged();
                return this;
            }

            private void ensureProtoFileIsMutable() {
                if ((this.bitField0_ & 4) == 0) {
                    this.protoFile_ = new ArrayList<DescriptorProtos.FileDescriptorProto>(this.protoFile_);
                    this.bitField0_ |= 4;
                }
            }

            @Override
            public List<DescriptorProtos.FileDescriptorProto> getProtoFileList() {
                if (this.protoFileBuilder_ == null) {
                    return Collections.unmodifiableList(this.protoFile_);
                }
                return this.protoFileBuilder_.getMessageList();
            }

            @Override
            public int getProtoFileCount() {
                if (this.protoFileBuilder_ == null) {
                    return this.protoFile_.size();
                }
                return this.protoFileBuilder_.getCount();
            }

            @Override
            public DescriptorProtos.FileDescriptorProto getProtoFile(int index) {
                if (this.protoFileBuilder_ == null) {
                    return this.protoFile_.get(index);
                }
                return this.protoFileBuilder_.getMessage(index);
            }

            public Builder setProtoFile(int index, DescriptorProtos.FileDescriptorProto value) {
                if (this.protoFileBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureProtoFileIsMutable();
                    this.protoFile_.set(index, value);
                    this.onChanged();
                } else {
                    this.protoFileBuilder_.setMessage(index, value);
                }
                return this;
            }

            public Builder setProtoFile(int index, DescriptorProtos.FileDescriptorProto.Builder builderForValue) {
                if (this.protoFileBuilder_ == null) {
                    this.ensureProtoFileIsMutable();
                    this.protoFile_.set(index, builderForValue.build());
                    this.onChanged();
                } else {
                    this.protoFileBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addProtoFile(DescriptorProtos.FileDescriptorProto value) {
                if (this.protoFileBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureProtoFileIsMutable();
                    this.protoFile_.add(value);
                    this.onChanged();
                } else {
                    this.protoFileBuilder_.addMessage(value);
                }
                return this;
            }

            public Builder addProtoFile(int index, DescriptorProtos.FileDescriptorProto value) {
                if (this.protoFileBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureProtoFileIsMutable();
                    this.protoFile_.add(index, value);
                    this.onChanged();
                } else {
                    this.protoFileBuilder_.addMessage(index, value);
                }
                return this;
            }

            public Builder addProtoFile(DescriptorProtos.FileDescriptorProto.Builder builderForValue) {
                if (this.protoFileBuilder_ == null) {
                    this.ensureProtoFileIsMutable();
                    this.protoFile_.add(builderForValue.build());
                    this.onChanged();
                } else {
                    this.protoFileBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addProtoFile(int index, DescriptorProtos.FileDescriptorProto.Builder builderForValue) {
                if (this.protoFileBuilder_ == null) {
                    this.ensureProtoFileIsMutable();
                    this.protoFile_.add(index, builderForValue.build());
                    this.onChanged();
                } else {
                    this.protoFileBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllProtoFile(Iterable<? extends DescriptorProtos.FileDescriptorProto> values) {
                if (this.protoFileBuilder_ == null) {
                    this.ensureProtoFileIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.protoFile_);
                    this.onChanged();
                } else {
                    this.protoFileBuilder_.addAllMessages(values);
                }
                return this;
            }

            public Builder clearProtoFile() {
                if (this.protoFileBuilder_ == null) {
                    this.protoFile_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFB;
                    this.onChanged();
                } else {
                    this.protoFileBuilder_.clear();
                }
                return this;
            }

            public Builder removeProtoFile(int index) {
                if (this.protoFileBuilder_ == null) {
                    this.ensureProtoFileIsMutable();
                    this.protoFile_.remove(index);
                    this.onChanged();
                } else {
                    this.protoFileBuilder_.remove(index);
                }
                return this;
            }

            public DescriptorProtos.FileDescriptorProto.Builder getProtoFileBuilder(int index) {
                return this.internalGetProtoFileFieldBuilder().getBuilder(index);
            }

            @Override
            public DescriptorProtos.FileDescriptorProtoOrBuilder getProtoFileOrBuilder(int index) {
                if (this.protoFileBuilder_ == null) {
                    return this.protoFile_.get(index);
                }
                return this.protoFileBuilder_.getMessageOrBuilder(index);
            }

            @Override
            public List<? extends DescriptorProtos.FileDescriptorProtoOrBuilder> getProtoFileOrBuilderList() {
                if (this.protoFileBuilder_ != null) {
                    return this.protoFileBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.protoFile_);
            }

            public DescriptorProtos.FileDescriptorProto.Builder addProtoFileBuilder() {
                return this.internalGetProtoFileFieldBuilder().addBuilder(DescriptorProtos.FileDescriptorProto.getDefaultInstance());
            }

            public DescriptorProtos.FileDescriptorProto.Builder addProtoFileBuilder(int index) {
                return this.internalGetProtoFileFieldBuilder().addBuilder(index, DescriptorProtos.FileDescriptorProto.getDefaultInstance());
            }

            public List<DescriptorProtos.FileDescriptorProto.Builder> getProtoFileBuilderList() {
                return this.internalGetProtoFileFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilder<DescriptorProtos.FileDescriptorProto, DescriptorProtos.FileDescriptorProto.Builder, DescriptorProtos.FileDescriptorProtoOrBuilder> internalGetProtoFileFieldBuilder() {
                if (this.protoFileBuilder_ == null) {
                    this.protoFileBuilder_ = new RepeatedFieldBuilder(this.protoFile_, (this.bitField0_ & 4) != 0, this.getParentForChildren(), this.isClean());
                    this.protoFile_ = null;
                }
                return this.protoFileBuilder_;
            }

            private void ensureSourceFileDescriptorsIsMutable() {
                if ((this.bitField0_ & 8) == 0) {
                    this.sourceFileDescriptors_ = new ArrayList<DescriptorProtos.FileDescriptorProto>(this.sourceFileDescriptors_);
                    this.bitField0_ |= 8;
                }
            }

            @Override
            public List<DescriptorProtos.FileDescriptorProto> getSourceFileDescriptorsList() {
                if (this.sourceFileDescriptorsBuilder_ == null) {
                    return Collections.unmodifiableList(this.sourceFileDescriptors_);
                }
                return this.sourceFileDescriptorsBuilder_.getMessageList();
            }

            @Override
            public int getSourceFileDescriptorsCount() {
                if (this.sourceFileDescriptorsBuilder_ == null) {
                    return this.sourceFileDescriptors_.size();
                }
                return this.sourceFileDescriptorsBuilder_.getCount();
            }

            @Override
            public DescriptorProtos.FileDescriptorProto getSourceFileDescriptors(int index) {
                if (this.sourceFileDescriptorsBuilder_ == null) {
                    return this.sourceFileDescriptors_.get(index);
                }
                return this.sourceFileDescriptorsBuilder_.getMessage(index);
            }

            public Builder setSourceFileDescriptors(int index, DescriptorProtos.FileDescriptorProto value) {
                if (this.sourceFileDescriptorsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureSourceFileDescriptorsIsMutable();
                    this.sourceFileDescriptors_.set(index, value);
                    this.onChanged();
                } else {
                    this.sourceFileDescriptorsBuilder_.setMessage(index, value);
                }
                return this;
            }

            public Builder setSourceFileDescriptors(int index, DescriptorProtos.FileDescriptorProto.Builder builderForValue) {
                if (this.sourceFileDescriptorsBuilder_ == null) {
                    this.ensureSourceFileDescriptorsIsMutable();
                    this.sourceFileDescriptors_.set(index, builderForValue.build());
                    this.onChanged();
                } else {
                    this.sourceFileDescriptorsBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addSourceFileDescriptors(DescriptorProtos.FileDescriptorProto value) {
                if (this.sourceFileDescriptorsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureSourceFileDescriptorsIsMutable();
                    this.sourceFileDescriptors_.add(value);
                    this.onChanged();
                } else {
                    this.sourceFileDescriptorsBuilder_.addMessage(value);
                }
                return this;
            }

            public Builder addSourceFileDescriptors(int index, DescriptorProtos.FileDescriptorProto value) {
                if (this.sourceFileDescriptorsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureSourceFileDescriptorsIsMutable();
                    this.sourceFileDescriptors_.add(index, value);
                    this.onChanged();
                } else {
                    this.sourceFileDescriptorsBuilder_.addMessage(index, value);
                }
                return this;
            }

            public Builder addSourceFileDescriptors(DescriptorProtos.FileDescriptorProto.Builder builderForValue) {
                if (this.sourceFileDescriptorsBuilder_ == null) {
                    this.ensureSourceFileDescriptorsIsMutable();
                    this.sourceFileDescriptors_.add(builderForValue.build());
                    this.onChanged();
                } else {
                    this.sourceFileDescriptorsBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addSourceFileDescriptors(int index, DescriptorProtos.FileDescriptorProto.Builder builderForValue) {
                if (this.sourceFileDescriptorsBuilder_ == null) {
                    this.ensureSourceFileDescriptorsIsMutable();
                    this.sourceFileDescriptors_.add(index, builderForValue.build());
                    this.onChanged();
                } else {
                    this.sourceFileDescriptorsBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllSourceFileDescriptors(Iterable<? extends DescriptorProtos.FileDescriptorProto> values) {
                if (this.sourceFileDescriptorsBuilder_ == null) {
                    this.ensureSourceFileDescriptorsIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.sourceFileDescriptors_);
                    this.onChanged();
                } else {
                    this.sourceFileDescriptorsBuilder_.addAllMessages(values);
                }
                return this;
            }

            public Builder clearSourceFileDescriptors() {
                if (this.sourceFileDescriptorsBuilder_ == null) {
                    this.sourceFileDescriptors_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFF7;
                    this.onChanged();
                } else {
                    this.sourceFileDescriptorsBuilder_.clear();
                }
                return this;
            }

            public Builder removeSourceFileDescriptors(int index) {
                if (this.sourceFileDescriptorsBuilder_ == null) {
                    this.ensureSourceFileDescriptorsIsMutable();
                    this.sourceFileDescriptors_.remove(index);
                    this.onChanged();
                } else {
                    this.sourceFileDescriptorsBuilder_.remove(index);
                }
                return this;
            }

            public DescriptorProtos.FileDescriptorProto.Builder getSourceFileDescriptorsBuilder(int index) {
                return this.internalGetSourceFileDescriptorsFieldBuilder().getBuilder(index);
            }

            @Override
            public DescriptorProtos.FileDescriptorProtoOrBuilder getSourceFileDescriptorsOrBuilder(int index) {
                if (this.sourceFileDescriptorsBuilder_ == null) {
                    return this.sourceFileDescriptors_.get(index);
                }
                return this.sourceFileDescriptorsBuilder_.getMessageOrBuilder(index);
            }

            @Override
            public List<? extends DescriptorProtos.FileDescriptorProtoOrBuilder> getSourceFileDescriptorsOrBuilderList() {
                if (this.sourceFileDescriptorsBuilder_ != null) {
                    return this.sourceFileDescriptorsBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.sourceFileDescriptors_);
            }

            public DescriptorProtos.FileDescriptorProto.Builder addSourceFileDescriptorsBuilder() {
                return this.internalGetSourceFileDescriptorsFieldBuilder().addBuilder(DescriptorProtos.FileDescriptorProto.getDefaultInstance());
            }

            public DescriptorProtos.FileDescriptorProto.Builder addSourceFileDescriptorsBuilder(int index) {
                return this.internalGetSourceFileDescriptorsFieldBuilder().addBuilder(index, DescriptorProtos.FileDescriptorProto.getDefaultInstance());
            }

            public List<DescriptorProtos.FileDescriptorProto.Builder> getSourceFileDescriptorsBuilderList() {
                return this.internalGetSourceFileDescriptorsFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilder<DescriptorProtos.FileDescriptorProto, DescriptorProtos.FileDescriptorProto.Builder, DescriptorProtos.FileDescriptorProtoOrBuilder> internalGetSourceFileDescriptorsFieldBuilder() {
                if (this.sourceFileDescriptorsBuilder_ == null) {
                    this.sourceFileDescriptorsBuilder_ = new RepeatedFieldBuilder(this.sourceFileDescriptors_, (this.bitField0_ & 8) != 0, this.getParentForChildren(), this.isClean());
                    this.sourceFileDescriptors_ = null;
                }
                return this.sourceFileDescriptorsBuilder_;
            }

            @Override
            public boolean hasCompilerVersion() {
                return (this.bitField0_ & 0x10) != 0;
            }

            @Override
            public Version getCompilerVersion() {
                if (this.compilerVersionBuilder_ == null) {
                    return this.compilerVersion_ == null ? Version.getDefaultInstance() : this.compilerVersion_;
                }
                return this.compilerVersionBuilder_.getMessage();
            }

            public Builder setCompilerVersion(Version value) {
                if (this.compilerVersionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.compilerVersion_ = value;
                } else {
                    this.compilerVersionBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x10;
                this.onChanged();
                return this;
            }

            public Builder setCompilerVersion(Version.Builder builderForValue) {
                if (this.compilerVersionBuilder_ == null) {
                    this.compilerVersion_ = builderForValue.build();
                } else {
                    this.compilerVersionBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x10;
                this.onChanged();
                return this;
            }

            public Builder mergeCompilerVersion(Version value) {
                if (this.compilerVersionBuilder_ == null) {
                    if ((this.bitField0_ & 0x10) != 0 && this.compilerVersion_ != null && this.compilerVersion_ != Version.getDefaultInstance()) {
                        this.getCompilerVersionBuilder().mergeFrom(value);
                    } else {
                        this.compilerVersion_ = value;
                    }
                } else {
                    this.compilerVersionBuilder_.mergeFrom(value);
                }
                if (this.compilerVersion_ != null) {
                    this.bitField0_ |= 0x10;
                    this.onChanged();
                }
                return this;
            }

            public Builder clearCompilerVersion() {
                this.bitField0_ &= 0xFFFFFFEF;
                this.compilerVersion_ = null;
                if (this.compilerVersionBuilder_ != null) {
                    this.compilerVersionBuilder_.dispose();
                    this.compilerVersionBuilder_ = null;
                }
                this.onChanged();
                return this;
            }

            public Version.Builder getCompilerVersionBuilder() {
                this.bitField0_ |= 0x10;
                this.onChanged();
                return this.internalGetCompilerVersionFieldBuilder().getBuilder();
            }

            @Override
            public VersionOrBuilder getCompilerVersionOrBuilder() {
                if (this.compilerVersionBuilder_ != null) {
                    return this.compilerVersionBuilder_.getMessageOrBuilder();
                }
                return this.compilerVersion_ == null ? Version.getDefaultInstance() : this.compilerVersion_;
            }

            private SingleFieldBuilder<Version, Version.Builder, VersionOrBuilder> internalGetCompilerVersionFieldBuilder() {
                if (this.compilerVersionBuilder_ == null) {
                    this.compilerVersionBuilder_ = new SingleFieldBuilder(this.getCompilerVersion(), this.getParentForChildren(), this.isClean());
                    this.compilerVersion_ = null;
                }
                return this.compilerVersionBuilder_;
            }
        }
    }

    public static interface CodeGeneratorRequestOrBuilder
    extends MessageOrBuilder {
        public List<String> getFileToGenerateList();

        public int getFileToGenerateCount();

        public String getFileToGenerate(int var1);

        public ByteString getFileToGenerateBytes(int var1);

        public boolean hasParameter();

        public String getParameter();

        public ByteString getParameterBytes();

        public List<DescriptorProtos.FileDescriptorProto> getProtoFileList();

        public DescriptorProtos.FileDescriptorProto getProtoFile(int var1);

        public int getProtoFileCount();

        public List<? extends DescriptorProtos.FileDescriptorProtoOrBuilder> getProtoFileOrBuilderList();

        public DescriptorProtos.FileDescriptorProtoOrBuilder getProtoFileOrBuilder(int var1);

        public List<DescriptorProtos.FileDescriptorProto> getSourceFileDescriptorsList();

        public DescriptorProtos.FileDescriptorProto getSourceFileDescriptors(int var1);

        public int getSourceFileDescriptorsCount();

        public List<? extends DescriptorProtos.FileDescriptorProtoOrBuilder> getSourceFileDescriptorsOrBuilderList();

        public DescriptorProtos.FileDescriptorProtoOrBuilder getSourceFileDescriptorsOrBuilder(int var1);

        public boolean hasCompilerVersion();

        public Version getCompilerVersion();

        public VersionOrBuilder getCompilerVersionOrBuilder();
    }

    public static final class Version
    extends GeneratedMessage
    implements VersionOrBuilder {
        private static final long serialVersionUID = 0L;
        private int bitField0_;
        public static final int MAJOR_FIELD_NUMBER = 1;
        private int major_ = 0;
        public static final int MINOR_FIELD_NUMBER = 2;
        private int minor_ = 0;
        public static final int PATCH_FIELD_NUMBER = 3;
        private int patch_ = 0;
        public static final int SUFFIX_FIELD_NUMBER = 4;
        private volatile Object suffix_ = "";
        private byte memoizedIsInitialized = (byte)-1;
        private static final Version DEFAULT_INSTANCE;
        private static final Parser<Version> PARSER;

        private Version(GeneratedMessage.Builder<?> builder) {
            super(builder);
        }

        private Version() {
            this.suffix_ = "";
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return internal_static_google_protobuf_compiler_Version_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return internal_static_google_protobuf_compiler_Version_fieldAccessorTable.ensureFieldAccessorsInitialized(Version.class, Builder.class);
        }

        @Override
        public boolean hasMajor() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override
        public int getMajor() {
            return this.major_;
        }

        @Override
        public boolean hasMinor() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override
        public int getMinor() {
            return this.minor_;
        }

        @Override
        public boolean hasPatch() {
            return (this.bitField0_ & 4) != 0;
        }

        @Override
        public int getPatch() {
            return this.patch_;
        }

        @Override
        public boolean hasSuffix() {
            return (this.bitField0_ & 8) != 0;
        }

        @Override
        public String getSuffix() {
            Object ref = this.suffix_;
            if (ref instanceof String) {
                return (String)ref;
            }
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.suffix_ = s;
            }
            return s;
        }

        @Override
        public ByteString getSuffixBytes() {
            Object ref = this.suffix_;
            if (ref instanceof String) {
                ByteString b = ByteString.copyFromUtf8((String)ref);
                this.suffix_ = b;
                return b;
            }
            return (ByteString)ref;
        }

        @Override
        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
                return true;
            }
            if (isInitialized == 0) {
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }

        @Override
        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 1) != 0) {
                output.writeInt32(1, this.major_);
            }
            if ((this.bitField0_ & 2) != 0) {
                output.writeInt32(2, this.minor_);
            }
            if ((this.bitField0_ & 4) != 0) {
                output.writeInt32(3, this.patch_);
            }
            if ((this.bitField0_ & 8) != 0) {
                GeneratedMessage.writeString(output, 4, this.suffix_);
            }
            this.getUnknownFields().writeTo(output);
        }

        @Override
        public int getSerializedSize() {
            int size = this.memoizedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
                size += CodedOutputStream.computeInt32Size(1, this.major_);
            }
            if ((this.bitField0_ & 2) != 0) {
                size += CodedOutputStream.computeInt32Size(2, this.minor_);
            }
            if ((this.bitField0_ & 4) != 0) {
                size += CodedOutputStream.computeInt32Size(3, this.patch_);
            }
            if ((this.bitField0_ & 8) != 0) {
                size += GeneratedMessage.computeStringSize(4, this.suffix_);
            }
            this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
            return size;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Version)) {
                return super.equals(obj);
            }
            Version other = (Version)obj;
            if (this.hasMajor() != other.hasMajor()) {
                return false;
            }
            if (this.hasMajor() && this.getMajor() != other.getMajor()) {
                return false;
            }
            if (this.hasMinor() != other.hasMinor()) {
                return false;
            }
            if (this.hasMinor() && this.getMinor() != other.getMinor()) {
                return false;
            }
            if (this.hasPatch() != other.hasPatch()) {
                return false;
            }
            if (this.hasPatch() && this.getPatch() != other.getPatch()) {
                return false;
            }
            if (this.hasSuffix() != other.hasSuffix()) {
                return false;
            }
            if (this.hasSuffix() && !this.getSuffix().equals(other.getSuffix())) {
                return false;
            }
            return this.getUnknownFields().equals(other.getUnknownFields());
        }

        @Override
        public int hashCode() {
            if (this.memoizedHashCode != 0) {
                return this.memoizedHashCode;
            }
            int hash = 41;
            hash = 19 * hash + Version.getDescriptor().hashCode();
            if (this.hasMajor()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getMajor();
            }
            if (this.hasMinor()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getMinor();
            }
            if (this.hasPatch()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getPatch();
            }
            if (this.hasSuffix()) {
                hash = 37 * hash + 4;
                hash = 53 * hash + this.getSuffix().hashCode();
            }
            this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
            return hash;
        }

        public static Version parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static Version parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static Version parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static Version parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static Version parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static Version parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static Version parseFrom(InputStream input) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input);
        }

        public static Version parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static Version parseDelimitedFrom(InputStream input) throws IOException {
            return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
        }

        public static Version parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static Version parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input);
        }

        public static Version parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
        }

        @Override
        public Builder newBuilderForType() {
            return Version.newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(Version prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        @Override
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
        }

        @Override
        protected Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        public static Version getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<Version> parser() {
            return PARSER;
        }

        public Parser<Version> getParserForType() {
            return PARSER;
        }

        @Override
        public Version getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

        static {
            RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "Version");
            DEFAULT_INSTANCE = new Version();
            PARSER = new AbstractParser<Version>(){

                @Override
                public Version parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    Builder builder = Version.newBuilder();
                    try {
                        builder.mergeFrom(input, extensionRegistry);
                    }
                    catch (InvalidProtocolBufferException e) {
                        throw e.setUnfinishedMessage(builder.buildPartial());
                    }
                    catch (UninitializedMessageException e) {
                        throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
                    }
                    catch (IOException e) {
                        throw new InvalidProtocolBufferException(e).setUnfinishedMessage(builder.buildPartial());
                    }
                    return builder.buildPartial();
                }
            };
        }

        public static final class Builder
        extends GeneratedMessage.Builder<Builder>
        implements VersionOrBuilder {
            private int bitField0_;
            private int major_;
            private int minor_;
            private int patch_;
            private Object suffix_ = "";

            public static final Descriptors.Descriptor getDescriptor() {
                return internal_static_google_protobuf_compiler_Version_descriptor;
            }

            @Override
            protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return internal_static_google_protobuf_compiler_Version_fieldAccessorTable.ensureFieldAccessorsInitialized(Version.class, Builder.class);
            }

            private Builder() {
            }

            private Builder(AbstractMessage.BuilderParent parent) {
                super(parent);
            }

            @Override
            public Builder clear() {
                super.clear();
                this.bitField0_ = 0;
                this.major_ = 0;
                this.minor_ = 0;
                this.patch_ = 0;
                this.suffix_ = "";
                return this;
            }

            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return internal_static_google_protobuf_compiler_Version_descriptor;
            }

            @Override
            public Version getDefaultInstanceForType() {
                return Version.getDefaultInstance();
            }

            @Override
            public Version build() {
                Version result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw Builder.newUninitializedMessageException(result);
                }
                return result;
            }

            @Override
            public Version buildPartial() {
                Version result = new Version(this);
                if (this.bitField0_ != 0) {
                    this.buildPartial0(result);
                }
                this.onBuilt();
                return result;
            }

            private void buildPartial0(Version result) {
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) != 0) {
                    result.major_ = this.major_;
                    to_bitField0_ |= 1;
                }
                if ((from_bitField0_ & 2) != 0) {
                    result.minor_ = this.minor_;
                    to_bitField0_ |= 2;
                }
                if ((from_bitField0_ & 4) != 0) {
                    result.patch_ = this.patch_;
                    to_bitField0_ |= 4;
                }
                if ((from_bitField0_ & 8) != 0) {
                    result.suffix_ = this.suffix_;
                    to_bitField0_ |= 8;
                }
                result.bitField0_ |= to_bitField0_;
            }

            @Override
            public Builder mergeFrom(Message other) {
                if (other instanceof Version) {
                    return this.mergeFrom((Version)other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(Version other) {
                if (other == Version.getDefaultInstance()) {
                    return this;
                }
                if (other.hasMajor()) {
                    this.setMajor(other.getMajor());
                }
                if (other.hasMinor()) {
                    this.setMinor(other.getMinor());
                }
                if (other.hasPatch()) {
                    this.setPatch(other.getPatch());
                }
                if (other.hasSuffix()) {
                    this.suffix_ = other.suffix_;
                    this.bitField0_ |= 8;
                    this.onChanged();
                }
                this.mergeUnknownFields(other.getUnknownFields());
                this.onChanged();
                return this;
            }

            @Override
            public final boolean isInitialized() {
                return true;
            }

            @Override
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                if (extensionRegistry == null) {
                    throw new NullPointerException();
                }
                try {
                    boolean done = false;
                    block12: while (!done) {
                        int tag = input.readTag();
                        switch (tag) {
                            case 0: {
                                done = true;
                                continue block12;
                            }
                            case 8: {
                                this.major_ = input.readInt32();
                                this.bitField0_ |= 1;
                                continue block12;
                            }
                            case 16: {
                                this.minor_ = input.readInt32();
                                this.bitField0_ |= 2;
                                continue block12;
                            }
                            case 24: {
                                this.patch_ = input.readInt32();
                                this.bitField0_ |= 4;
                                continue block12;
                            }
                            case 34: {
                                this.suffix_ = input.readBytes();
                                this.bitField0_ |= 8;
                                continue block12;
                            }
                        }
                        if (super.parseUnknownField(input, extensionRegistry, tag)) continue;
                        done = true;
                    }
                }
                catch (InvalidProtocolBufferException e) {
                    throw e.unwrapIOException();
                }
                finally {
                    this.onChanged();
                }
                return this;
            }

            @Override
            public boolean hasMajor() {
                return (this.bitField0_ & 1) != 0;
            }

            @Override
            public int getMajor() {
                return this.major_;
            }

            public Builder setMajor(int value) {
                this.major_ = value;
                this.bitField0_ |= 1;
                this.onChanged();
                return this;
            }

            public Builder clearMajor() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.major_ = 0;
                this.onChanged();
                return this;
            }

            @Override
            public boolean hasMinor() {
                return (this.bitField0_ & 2) != 0;
            }

            @Override
            public int getMinor() {
                return this.minor_;
            }

            public Builder setMinor(int value) {
                this.minor_ = value;
                this.bitField0_ |= 2;
                this.onChanged();
                return this;
            }

            public Builder clearMinor() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.minor_ = 0;
                this.onChanged();
                return this;
            }

            @Override
            public boolean hasPatch() {
                return (this.bitField0_ & 4) != 0;
            }

            @Override
            public int getPatch() {
                return this.patch_;
            }

            public Builder setPatch(int value) {
                this.patch_ = value;
                this.bitField0_ |= 4;
                this.onChanged();
                return this;
            }

            public Builder clearPatch() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.patch_ = 0;
                this.onChanged();
                return this;
            }

            @Override
            public boolean hasSuffix() {
                return (this.bitField0_ & 8) != 0;
            }

            @Override
            public String getSuffix() {
                Object ref = this.suffix_;
                if (!(ref instanceof String)) {
                    ByteString bs = (ByteString)ref;
                    String s = bs.toStringUtf8();
                    if (bs.isValidUtf8()) {
                        this.suffix_ = s;
                    }
                    return s;
                }
                return (String)ref;
            }

            @Override
            public ByteString getSuffixBytes() {
                Object ref = this.suffix_;
                if (ref instanceof String) {
                    ByteString b = ByteString.copyFromUtf8((String)ref);
                    this.suffix_ = b;
                    return b;
                }
                return (ByteString)ref;
            }

            public Builder setSuffix(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.suffix_ = value;
                this.bitField0_ |= 8;
                this.onChanged();
                return this;
            }

            public Builder clearSuffix() {
                this.suffix_ = Version.getDefaultInstance().getSuffix();
                this.bitField0_ &= 0xFFFFFFF7;
                this.onChanged();
                return this;
            }

            public Builder setSuffixBytes(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.suffix_ = value;
                this.bitField0_ |= 8;
                this.onChanged();
                return this;
            }
        }
    }

    public static interface VersionOrBuilder
    extends MessageOrBuilder {
        public boolean hasMajor();

        public int getMajor();

        public boolean hasMinor();

        public int getMinor();

        public boolean hasPatch();

        public int getPatch();

        public boolean hasSuffix();

        public String getSuffix();

        public ByteString getSuffixBytes();
    }
}

