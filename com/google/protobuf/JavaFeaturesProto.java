/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.AbstractMessage;
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
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Parser;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.RuntimeVersion;
import com.google.protobuf.UninitializedMessageException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class JavaFeaturesProto
extends GeneratedFile {
    public static final int JAVA_FIELD_NUMBER = 1001;
    public static final GeneratedMessage.GeneratedExtension<DescriptorProtos.FeatureSet, JavaFeatures> java_;
    private static final Descriptors.Descriptor internal_static_pb_JavaFeatures_descriptor;
    private static final GeneratedMessage.FieldAccessorTable internal_static_pb_JavaFeatures_fieldAccessorTable;
    private static final Descriptors.Descriptor internal_static_pb_JavaFeatures_NestInFileClassFeature_descriptor;
    private static final GeneratedMessage.FieldAccessorTable internal_static_pb_JavaFeatures_NestInFileClassFeature_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;

    private JavaFeaturesProto() {
    }

    public static void registerAllExtensions(ExtensionRegistryLite registry) {
        registry.add(java_);
    }

    public static void registerAllExtensions(ExtensionRegistry registry) {
        JavaFeaturesProto.registerAllExtensions((ExtensionRegistryLite)registry);
    }

    public static Descriptors.FileDescriptor getDescriptor() {
        return descriptor;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "JavaFeaturesProto");
        java_ = GeneratedMessage.newFileScopedGeneratedExtension(JavaFeatures.class, JavaFeatures.getDefaultInstance());
        String[] descriptorData = new String[]{"\n#google/protobuf/java_features.proto\u0012\u0002pb\u001a google/protobuf/descriptor.proto\"\u00df\b\n\fJavaFeatures\u0012\u0090\u0002\n\u0012legacy_closed_enum\u0018\u0001 \u0001(\bB\u00e1\u0001\u0088\u0001\u0001\u0098\u0001\u0004\u0098\u0001\u0001\u00a2\u0001\t\u0012\u0004true\u0018\u0084\u0007\u00a2\u0001\n\u0012\u0005false\u0018\u00e7\u0007\u00b2\u0001\u00bb\u0001\b\u00e8\u0007\u0010\u00e8\u0007\u001a\u00b2\u0001The legacy closed enum behavior in Java is deprecated and is scheduled to be removed in edition 2025.  See http://protobuf.dev/programming-guides/enum/#java for more information.R\u0010legacyClosedEnum\u0012\u00af\u0002\n\u000futf8_validation\u0018\u0002 \u0001(\u000e2\u001f.pb.JavaFeatures.Utf8ValidationB\u00e4\u0001\u0088\u0001\u0001\u0098\u0001\u0004\u0098\u0001\u0001\u00a2\u0001\f\u0012\u0007DEFAULT\u0018\u0084\u0007\u00b2\u0001\u00c8\u0001\b\u00e8\u0007\u0010\u00e9\u0007\u001a\u00bf\u0001The Java-specific utf8 validation feature is deprecated and is scheduled to be removed in edition 2025.  Utf8 validation behavior should use the global cross-language utf8_validation feature.R\u000eutf8Validation\u0012;\n\nlarge_enum\u0018\u0003 \u0001(\bB\u001c\u0088\u0001\u0001\u0098\u0001\u0006\u0098\u0001\u0001\u00a2\u0001\n\u0012\u0005false\u0018\u0084\u0007\u00b2\u0001\u0003\b\u00e9\u0007R\tlargeEnum\u0012n\n\u001fuse_old_outer_classname_default\u0018\u0004 \u0001(\bB(\u0088\u0001\u0001\u0098\u0001\u0001\u00a2\u0001\t\u0012\u0004true\u0018\u0084\u0007\u00a2\u0001\n\u0012\u0005false\u0018\u00e9\u0007\u00b2\u0001\u0006\b\u00e9\u0007 \u00e9\u0007R\u001buseOldOuterClassnameDefault\u0012\u0090\u0001\n\u0012nest_in_file_class\u0018\u0005 \u0001(\u000e27.pb.JavaFeatures.NestInFileClassFeature.NestInFileClassB*\u0088\u0001\u0001\u0098\u0001\u0003\u0098\u0001\u0006\u0098\u0001\b\u00a2\u0001\u000b\u0012\u0006LEGACY\u0018\u0084\u0007\u00a2\u0001\u0007\u0012\u0002NO\u0018\u00e9\u0007\u00b2\u0001\u0003\b\u00e9\u0007R\u000fnestInFileClass\u001a|\n\u0016NestInFileClassFeature\"X\n\u000fNestInFileClass\u0012\u001e\n\u001aNEST_IN_FILE_CLASS_UNKNOWN\u0010\u0000\u0012\u0006\n\u0002NO\u0010\u0001\u0012\u0007\n\u0003YES\u0010\u0002\u0012\u0014\n\u0006LEGACY\u0010\u0003\u001a\b\"\u0006\b\u00e9\u0007 \u00e9\u0007J\b\b\u0001\u0010\u0080\u0080\u0080\u0080\u0002\"F\n\u000eUtf8Validation\u0012\u001b\n\u0017UTF8_VALIDATION_UNKNOWN\u0010\u0000\u0012\u000b\n\u0007DEFAULT\u0010\u0001\u0012\n\n\u0006VERIFY\u0010\u0002J\u0004\b\u0006\u0010\u0007:B\n\u0004java\u0012\u001b.google.protobuf.FeatureSet\u0018\u00e9\u0007 \u0001(\u000b2\u0010.pb.JavaFeaturesR\u0004javaB(\n\u0013com.google.protobufB\u0011JavaFeaturesProto"};
        descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[]{DescriptorProtos.getDescriptor()});
        internal_static_pb_JavaFeatures_descriptor = JavaFeaturesProto.getDescriptor().getMessageType(0);
        internal_static_pb_JavaFeatures_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(internal_static_pb_JavaFeatures_descriptor, new String[]{"LegacyClosedEnum", "Utf8Validation", "LargeEnum", "UseOldOuterClassnameDefault", "NestInFileClass"});
        internal_static_pb_JavaFeatures_NestInFileClassFeature_descriptor = internal_static_pb_JavaFeatures_descriptor.getNestedType(0);
        internal_static_pb_JavaFeatures_NestInFileClassFeature_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(internal_static_pb_JavaFeatures_NestInFileClassFeature_descriptor, new String[0]);
        java_.internalInit(descriptor.getExtension(0));
        descriptor.resolveAllFeaturesImmutable();
        DescriptorProtos.getDescriptor();
    }

    public static final class JavaFeatures
    extends GeneratedMessage
    implements JavaFeaturesOrBuilder {
        private static final long serialVersionUID = 0L;
        private int bitField0_;
        public static final int LEGACY_CLOSED_ENUM_FIELD_NUMBER = 1;
        private boolean legacyClosedEnum_ = false;
        public static final int UTF8_VALIDATION_FIELD_NUMBER = 2;
        private int utf8Validation_ = 0;
        public static final int LARGE_ENUM_FIELD_NUMBER = 3;
        private boolean largeEnum_ = false;
        public static final int USE_OLD_OUTER_CLASSNAME_DEFAULT_FIELD_NUMBER = 4;
        private boolean useOldOuterClassnameDefault_ = false;
        public static final int NEST_IN_FILE_CLASS_FIELD_NUMBER = 5;
        private int nestInFileClass_ = 0;
        private byte memoizedIsInitialized = (byte)-1;
        private static final JavaFeatures DEFAULT_INSTANCE;
        private static final Parser<JavaFeatures> PARSER;

        private JavaFeatures(GeneratedMessage.Builder<?> builder) {
            super(builder);
        }

        private JavaFeatures() {
            this.utf8Validation_ = 0;
            this.nestInFileClass_ = 0;
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return internal_static_pb_JavaFeatures_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return internal_static_pb_JavaFeatures_fieldAccessorTable.ensureFieldAccessorsInitialized(JavaFeatures.class, Builder.class);
        }

        @Override
        public boolean hasLegacyClosedEnum() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override
        public boolean getLegacyClosedEnum() {
            return this.legacyClosedEnum_;
        }

        @Override
        public boolean hasUtf8Validation() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override
        public Utf8Validation getUtf8Validation() {
            Utf8Validation result = Utf8Validation.forNumber(this.utf8Validation_);
            return result == null ? Utf8Validation.UTF8_VALIDATION_UNKNOWN : result;
        }

        @Override
        public boolean hasLargeEnum() {
            return (this.bitField0_ & 4) != 0;
        }

        @Override
        public boolean getLargeEnum() {
            return this.largeEnum_;
        }

        @Override
        public boolean hasUseOldOuterClassnameDefault() {
            return (this.bitField0_ & 8) != 0;
        }

        @Override
        public boolean getUseOldOuterClassnameDefault() {
            return this.useOldOuterClassnameDefault_;
        }

        @Override
        public boolean hasNestInFileClass() {
            return (this.bitField0_ & 0x10) != 0;
        }

        @Override
        public NestInFileClassFeature.NestInFileClass getNestInFileClass() {
            NestInFileClassFeature.NestInFileClass result = NestInFileClassFeature.NestInFileClass.forNumber(this.nestInFileClass_);
            return result == null ? NestInFileClassFeature.NestInFileClass.NEST_IN_FILE_CLASS_UNKNOWN : result;
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
                output.writeBool(1, this.legacyClosedEnum_);
            }
            if ((this.bitField0_ & 2) != 0) {
                output.writeEnum(2, this.utf8Validation_);
            }
            if ((this.bitField0_ & 4) != 0) {
                output.writeBool(3, this.largeEnum_);
            }
            if ((this.bitField0_ & 8) != 0) {
                output.writeBool(4, this.useOldOuterClassnameDefault_);
            }
            if ((this.bitField0_ & 0x10) != 0) {
                output.writeEnum(5, this.nestInFileClass_);
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
                size += CodedOutputStream.computeBoolSize(1, this.legacyClosedEnum_);
            }
            if ((this.bitField0_ & 2) != 0) {
                size += CodedOutputStream.computeEnumSize(2, this.utf8Validation_);
            }
            if ((this.bitField0_ & 4) != 0) {
                size += CodedOutputStream.computeBoolSize(3, this.largeEnum_);
            }
            if ((this.bitField0_ & 8) != 0) {
                size += CodedOutputStream.computeBoolSize(4, this.useOldOuterClassnameDefault_);
            }
            if ((this.bitField0_ & 0x10) != 0) {
                size += CodedOutputStream.computeEnumSize(5, this.nestInFileClass_);
            }
            this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
            return size;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof JavaFeatures)) {
                return super.equals(obj);
            }
            JavaFeatures other = (JavaFeatures)obj;
            if (this.hasLegacyClosedEnum() != other.hasLegacyClosedEnum()) {
                return false;
            }
            if (this.hasLegacyClosedEnum() && this.getLegacyClosedEnum() != other.getLegacyClosedEnum()) {
                return false;
            }
            if (this.hasUtf8Validation() != other.hasUtf8Validation()) {
                return false;
            }
            if (this.hasUtf8Validation() && this.utf8Validation_ != other.utf8Validation_) {
                return false;
            }
            if (this.hasLargeEnum() != other.hasLargeEnum()) {
                return false;
            }
            if (this.hasLargeEnum() && this.getLargeEnum() != other.getLargeEnum()) {
                return false;
            }
            if (this.hasUseOldOuterClassnameDefault() != other.hasUseOldOuterClassnameDefault()) {
                return false;
            }
            if (this.hasUseOldOuterClassnameDefault() && this.getUseOldOuterClassnameDefault() != other.getUseOldOuterClassnameDefault()) {
                return false;
            }
            if (this.hasNestInFileClass() != other.hasNestInFileClass()) {
                return false;
            }
            if (this.hasNestInFileClass() && this.nestInFileClass_ != other.nestInFileClass_) {
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
            hash = 19 * hash + JavaFeatures.getDescriptor().hashCode();
            if (this.hasLegacyClosedEnum()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + Internal.hashBoolean(this.getLegacyClosedEnum());
            }
            if (this.hasUtf8Validation()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.utf8Validation_;
            }
            if (this.hasLargeEnum()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + Internal.hashBoolean(this.getLargeEnum());
            }
            if (this.hasUseOldOuterClassnameDefault()) {
                hash = 37 * hash + 4;
                hash = 53 * hash + Internal.hashBoolean(this.getUseOldOuterClassnameDefault());
            }
            if (this.hasNestInFileClass()) {
                hash = 37 * hash + 5;
                hash = 53 * hash + this.nestInFileClass_;
            }
            this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
            return hash;
        }

        public static JavaFeatures parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static JavaFeatures parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static JavaFeatures parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static JavaFeatures parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static JavaFeatures parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static JavaFeatures parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static JavaFeatures parseFrom(InputStream input) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input);
        }

        public static JavaFeatures parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static JavaFeatures parseDelimitedFrom(InputStream input) throws IOException {
            return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
        }

        public static JavaFeatures parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static JavaFeatures parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input);
        }

        public static JavaFeatures parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
        }

        @Override
        public Builder newBuilderForType() {
            return JavaFeatures.newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(JavaFeatures prototype) {
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

        public static JavaFeatures getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<JavaFeatures> parser() {
            return PARSER;
        }

        public Parser<JavaFeatures> getParserForType() {
            return PARSER;
        }

        @Override
        public JavaFeatures getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

        static {
            RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "JavaFeatures");
            DEFAULT_INSTANCE = new JavaFeatures();
            PARSER = new AbstractParser<JavaFeatures>(){

                @Override
                public JavaFeatures parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    Builder builder = JavaFeatures.newBuilder();
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
        implements JavaFeaturesOrBuilder {
            private int bitField0_;
            private boolean legacyClosedEnum_;
            private int utf8Validation_ = 0;
            private boolean largeEnum_;
            private boolean useOldOuterClassnameDefault_;
            private int nestInFileClass_ = 0;

            public static final Descriptors.Descriptor getDescriptor() {
                return internal_static_pb_JavaFeatures_descriptor;
            }

            @Override
            protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return internal_static_pb_JavaFeatures_fieldAccessorTable.ensureFieldAccessorsInitialized(JavaFeatures.class, Builder.class);
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
                this.legacyClosedEnum_ = false;
                this.utf8Validation_ = 0;
                this.largeEnum_ = false;
                this.useOldOuterClassnameDefault_ = false;
                this.nestInFileClass_ = 0;
                return this;
            }

            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return internal_static_pb_JavaFeatures_descriptor;
            }

            @Override
            public JavaFeatures getDefaultInstanceForType() {
                return JavaFeatures.getDefaultInstance();
            }

            @Override
            public JavaFeatures build() {
                JavaFeatures result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw Builder.newUninitializedMessageException(result);
                }
                return result;
            }

            @Override
            public JavaFeatures buildPartial() {
                JavaFeatures result = new JavaFeatures(this);
                if (this.bitField0_ != 0) {
                    this.buildPartial0(result);
                }
                this.onBuilt();
                return result;
            }

            private void buildPartial0(JavaFeatures result) {
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) != 0) {
                    result.legacyClosedEnum_ = this.legacyClosedEnum_;
                    to_bitField0_ |= 1;
                }
                if ((from_bitField0_ & 2) != 0) {
                    result.utf8Validation_ = this.utf8Validation_;
                    to_bitField0_ |= 2;
                }
                if ((from_bitField0_ & 4) != 0) {
                    result.largeEnum_ = this.largeEnum_;
                    to_bitField0_ |= 4;
                }
                if ((from_bitField0_ & 8) != 0) {
                    result.useOldOuterClassnameDefault_ = this.useOldOuterClassnameDefault_;
                    to_bitField0_ |= 8;
                }
                if ((from_bitField0_ & 0x10) != 0) {
                    result.nestInFileClass_ = this.nestInFileClass_;
                    to_bitField0_ |= 0x10;
                }
                result.bitField0_ |= to_bitField0_;
            }

            @Override
            public Builder mergeFrom(Message other) {
                if (other instanceof JavaFeatures) {
                    return this.mergeFrom((JavaFeatures)other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(JavaFeatures other) {
                if (other == JavaFeatures.getDefaultInstance()) {
                    return this;
                }
                if (other.hasLegacyClosedEnum()) {
                    this.setLegacyClosedEnum(other.getLegacyClosedEnum());
                }
                if (other.hasUtf8Validation()) {
                    this.setUtf8Validation(other.getUtf8Validation());
                }
                if (other.hasLargeEnum()) {
                    this.setLargeEnum(other.getLargeEnum());
                }
                if (other.hasUseOldOuterClassnameDefault()) {
                    this.setUseOldOuterClassnameDefault(other.getUseOldOuterClassnameDefault());
                }
                if (other.hasNestInFileClass()) {
                    this.setNestInFileClass(other.getNestInFileClass());
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
                            case 8: {
                                this.legacyClosedEnum_ = input.readBool();
                                this.bitField0_ |= 1;
                                continue block13;
                            }
                            case 16: {
                                int tmpRaw = input.readEnum();
                                Enum tmpValue = Utf8Validation.forNumber(tmpRaw);
                                if (tmpValue == null) {
                                    this.mergeUnknownVarintField(2, tmpRaw);
                                    continue block13;
                                }
                                this.utf8Validation_ = tmpRaw;
                                this.bitField0_ |= 2;
                                continue block13;
                            }
                            case 24: {
                                this.largeEnum_ = input.readBool();
                                this.bitField0_ |= 4;
                                continue block13;
                            }
                            case 32: {
                                this.useOldOuterClassnameDefault_ = input.readBool();
                                this.bitField0_ |= 8;
                                continue block13;
                            }
                            case 40: {
                                int tmpRaw = input.readEnum();
                                Enum tmpValue = NestInFileClassFeature.NestInFileClass.forNumber(tmpRaw);
                                if (tmpValue == null) {
                                    this.mergeUnknownVarintField(5, tmpRaw);
                                    continue block13;
                                }
                                this.nestInFileClass_ = tmpRaw;
                                this.bitField0_ |= 0x10;
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
            public boolean hasLegacyClosedEnum() {
                return (this.bitField0_ & 1) != 0;
            }

            @Override
            public boolean getLegacyClosedEnum() {
                return this.legacyClosedEnum_;
            }

            public Builder setLegacyClosedEnum(boolean value) {
                this.legacyClosedEnum_ = value;
                this.bitField0_ |= 1;
                this.onChanged();
                return this;
            }

            public Builder clearLegacyClosedEnum() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.legacyClosedEnum_ = false;
                this.onChanged();
                return this;
            }

            @Override
            public boolean hasUtf8Validation() {
                return (this.bitField0_ & 2) != 0;
            }

            @Override
            public Utf8Validation getUtf8Validation() {
                Utf8Validation result = Utf8Validation.forNumber(this.utf8Validation_);
                return result == null ? Utf8Validation.UTF8_VALIDATION_UNKNOWN : result;
            }

            public Builder setUtf8Validation(Utf8Validation value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 2;
                this.utf8Validation_ = value.getNumber();
                this.onChanged();
                return this;
            }

            public Builder clearUtf8Validation() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.utf8Validation_ = 0;
                this.onChanged();
                return this;
            }

            @Override
            public boolean hasLargeEnum() {
                return (this.bitField0_ & 4) != 0;
            }

            @Override
            public boolean getLargeEnum() {
                return this.largeEnum_;
            }

            public Builder setLargeEnum(boolean value) {
                this.largeEnum_ = value;
                this.bitField0_ |= 4;
                this.onChanged();
                return this;
            }

            public Builder clearLargeEnum() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.largeEnum_ = false;
                this.onChanged();
                return this;
            }

            @Override
            public boolean hasUseOldOuterClassnameDefault() {
                return (this.bitField0_ & 8) != 0;
            }

            @Override
            public boolean getUseOldOuterClassnameDefault() {
                return this.useOldOuterClassnameDefault_;
            }

            public Builder setUseOldOuterClassnameDefault(boolean value) {
                this.useOldOuterClassnameDefault_ = value;
                this.bitField0_ |= 8;
                this.onChanged();
                return this;
            }

            public Builder clearUseOldOuterClassnameDefault() {
                this.bitField0_ &= 0xFFFFFFF7;
                this.useOldOuterClassnameDefault_ = false;
                this.onChanged();
                return this;
            }

            @Override
            public boolean hasNestInFileClass() {
                return (this.bitField0_ & 0x10) != 0;
            }

            @Override
            public NestInFileClassFeature.NestInFileClass getNestInFileClass() {
                NestInFileClassFeature.NestInFileClass result = NestInFileClassFeature.NestInFileClass.forNumber(this.nestInFileClass_);
                return result == null ? NestInFileClassFeature.NestInFileClass.NEST_IN_FILE_CLASS_UNKNOWN : result;
            }

            public Builder setNestInFileClass(NestInFileClassFeature.NestInFileClass value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x10;
                this.nestInFileClass_ = value.getNumber();
                this.onChanged();
                return this;
            }

            public Builder clearNestInFileClass() {
                this.bitField0_ &= 0xFFFFFFEF;
                this.nestInFileClass_ = 0;
                this.onChanged();
                return this;
            }
        }

        public static enum Utf8Validation implements ProtocolMessageEnum
        {
            UTF8_VALIDATION_UNKNOWN(0),
            DEFAULT(1),
            VERIFY(2);

            public static final int UTF8_VALIDATION_UNKNOWN_VALUE = 0;
            public static final int DEFAULT_VALUE = 1;
            public static final int VERIFY_VALUE = 2;
            private static final Internal.EnumLiteMap<Utf8Validation> internalValueMap;
            private static final Utf8Validation[] VALUES;
            private final int value;

            @Override
            public final int getNumber() {
                return this.value;
            }

            @Deprecated
            public static Utf8Validation valueOf(int value) {
                return Utf8Validation.forNumber(value);
            }

            public static Utf8Validation forNumber(int value) {
                switch (value) {
                    case 0: {
                        return UTF8_VALIDATION_UNKNOWN;
                    }
                    case 1: {
                        return DEFAULT;
                    }
                    case 2: {
                        return VERIFY;
                    }
                }
                return null;
            }

            public static Internal.EnumLiteMap<Utf8Validation> internalGetValueMap() {
                return internalValueMap;
            }

            @Override
            public final Descriptors.EnumValueDescriptor getValueDescriptor() {
                return Utf8Validation.getDescriptor().getValues().get(this.ordinal());
            }

            @Override
            public final Descriptors.EnumDescriptor getDescriptorForType() {
                return Utf8Validation.getDescriptor();
            }

            public static Descriptors.EnumDescriptor getDescriptor() {
                return JavaFeatures.getDescriptor().getEnumTypes().get(0);
            }

            public static Utf8Validation valueOf(Descriptors.EnumValueDescriptor desc) {
                if (desc.getType() != Utf8Validation.getDescriptor()) {
                    throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
                }
                return VALUES[desc.getIndex()];
            }

            private Utf8Validation(int value) {
                this.value = value;
            }

            static {
                RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "Utf8Validation");
                internalValueMap = new Internal.EnumLiteMap<Utf8Validation>(){

                    @Override
                    public Utf8Validation findValueByNumber(int number) {
                        return Utf8Validation.forNumber(number);
                    }
                };
                VALUES = Utf8Validation.values();
            }
        }

        public static final class NestInFileClassFeature
        extends GeneratedMessage
        implements NestInFileClassFeatureOrBuilder {
            private static final long serialVersionUID = 0L;
            private byte memoizedIsInitialized = (byte)-1;
            private static final NestInFileClassFeature DEFAULT_INSTANCE;
            private static final Parser<NestInFileClassFeature> PARSER;

            private NestInFileClassFeature(GeneratedMessage.Builder<?> builder) {
                super(builder);
            }

            private NestInFileClassFeature() {
            }

            public static final Descriptors.Descriptor getDescriptor() {
                return internal_static_pb_JavaFeatures_NestInFileClassFeature_descriptor;
            }

            @Override
            protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return internal_static_pb_JavaFeatures_NestInFileClassFeature_fieldAccessorTable.ensureFieldAccessorsInitialized(NestInFileClassFeature.class, Builder.class);
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
                this.getUnknownFields().writeTo(output);
            }

            @Override
            public int getSerializedSize() {
                int size = this.memoizedSize;
                if (size != -1) {
                    return size;
                }
                size = 0;
                this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
                return size;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == this) {
                    return true;
                }
                if (!(obj instanceof NestInFileClassFeature)) {
                    return super.equals(obj);
                }
                NestInFileClassFeature other = (NestInFileClassFeature)obj;
                return this.getUnknownFields().equals(other.getUnknownFields());
            }

            @Override
            public int hashCode() {
                if (this.memoizedHashCode != 0) {
                    return this.memoizedHashCode;
                }
                int hash = 41;
                hash = 19 * hash + NestInFileClassFeature.getDescriptor().hashCode();
                this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
                return hash;
            }

            public static NestInFileClassFeature parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
                return PARSER.parseFrom(data);
            }

            public static NestInFileClassFeature parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return PARSER.parseFrom(data, extensionRegistry);
            }

            public static NestInFileClassFeature parseFrom(ByteString data) throws InvalidProtocolBufferException {
                return PARSER.parseFrom(data);
            }

            public static NestInFileClassFeature parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return PARSER.parseFrom(data, extensionRegistry);
            }

            public static NestInFileClassFeature parseFrom(byte[] data) throws InvalidProtocolBufferException {
                return PARSER.parseFrom(data);
            }

            public static NestInFileClassFeature parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return PARSER.parseFrom(data, extensionRegistry);
            }

            public static NestInFileClassFeature parseFrom(InputStream input) throws IOException {
                return GeneratedMessage.parseWithIOException(PARSER, input);
            }

            public static NestInFileClassFeature parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
            }

            public static NestInFileClassFeature parseDelimitedFrom(InputStream input) throws IOException {
                return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
            }

            public static NestInFileClassFeature parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
            }

            public static NestInFileClassFeature parseFrom(CodedInputStream input) throws IOException {
                return GeneratedMessage.parseWithIOException(PARSER, input);
            }

            public static NestInFileClassFeature parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
            }

            @Override
            public Builder newBuilderForType() {
                return NestInFileClassFeature.newBuilder();
            }

            public static Builder newBuilder() {
                return DEFAULT_INSTANCE.toBuilder();
            }

            public static Builder newBuilder(NestInFileClassFeature prototype) {
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

            public static NestInFileClassFeature getDefaultInstance() {
                return DEFAULT_INSTANCE;
            }

            public static Parser<NestInFileClassFeature> parser() {
                return PARSER;
            }

            public Parser<NestInFileClassFeature> getParserForType() {
                return PARSER;
            }

            @Override
            public NestInFileClassFeature getDefaultInstanceForType() {
                return DEFAULT_INSTANCE;
            }

            static {
                RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "NestInFileClassFeature");
                DEFAULT_INSTANCE = new NestInFileClassFeature();
                PARSER = new AbstractParser<NestInFileClassFeature>(){

                    @Override
                    public NestInFileClassFeature parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                        Builder builder = NestInFileClassFeature.newBuilder();
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
            implements NestInFileClassFeatureOrBuilder {
                public static final Descriptors.Descriptor getDescriptor() {
                    return internal_static_pb_JavaFeatures_NestInFileClassFeature_descriptor;
                }

                @Override
                protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                    return internal_static_pb_JavaFeatures_NestInFileClassFeature_fieldAccessorTable.ensureFieldAccessorsInitialized(NestInFileClassFeature.class, Builder.class);
                }

                private Builder() {
                }

                private Builder(AbstractMessage.BuilderParent parent) {
                    super(parent);
                }

                @Override
                public Builder clear() {
                    super.clear();
                    return this;
                }

                @Override
                public Descriptors.Descriptor getDescriptorForType() {
                    return internal_static_pb_JavaFeatures_NestInFileClassFeature_descriptor;
                }

                @Override
                public NestInFileClassFeature getDefaultInstanceForType() {
                    return NestInFileClassFeature.getDefaultInstance();
                }

                @Override
                public NestInFileClassFeature build() {
                    NestInFileClassFeature result = this.buildPartial();
                    if (!result.isInitialized()) {
                        throw Builder.newUninitializedMessageException(result);
                    }
                    return result;
                }

                @Override
                public NestInFileClassFeature buildPartial() {
                    NestInFileClassFeature result = new NestInFileClassFeature(this);
                    this.onBuilt();
                    return result;
                }

                @Override
                public Builder mergeFrom(Message other) {
                    if (other instanceof NestInFileClassFeature) {
                        return this.mergeFrom((NestInFileClassFeature)other);
                    }
                    super.mergeFrom(other);
                    return this;
                }

                public Builder mergeFrom(NestInFileClassFeature other) {
                    if (other == NestInFileClassFeature.getDefaultInstance()) {
                        return this;
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
                        block8: while (!done) {
                            int tag = input.readTag();
                            switch (tag) {
                                case 0: {
                                    done = true;
                                    continue block8;
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
            }

            public static enum NestInFileClass implements ProtocolMessageEnum
            {
                NEST_IN_FILE_CLASS_UNKNOWN(0),
                NO(1),
                YES(2),
                LEGACY(3);

                public static final int NEST_IN_FILE_CLASS_UNKNOWN_VALUE = 0;
                public static final int NO_VALUE = 1;
                public static final int YES_VALUE = 2;
                public static final int LEGACY_VALUE = 3;
                private static final Internal.EnumLiteMap<NestInFileClass> internalValueMap;
                private static final NestInFileClass[] VALUES;
                private final int value;

                @Override
                public final int getNumber() {
                    return this.value;
                }

                @Deprecated
                public static NestInFileClass valueOf(int value) {
                    return NestInFileClass.forNumber(value);
                }

                public static NestInFileClass forNumber(int value) {
                    switch (value) {
                        case 0: {
                            return NEST_IN_FILE_CLASS_UNKNOWN;
                        }
                        case 1: {
                            return NO;
                        }
                        case 2: {
                            return YES;
                        }
                        case 3: {
                            return LEGACY;
                        }
                    }
                    return null;
                }

                public static Internal.EnumLiteMap<NestInFileClass> internalGetValueMap() {
                    return internalValueMap;
                }

                @Override
                public final Descriptors.EnumValueDescriptor getValueDescriptor() {
                    return NestInFileClass.getDescriptor().getValues().get(this.ordinal());
                }

                @Override
                public final Descriptors.EnumDescriptor getDescriptorForType() {
                    return NestInFileClass.getDescriptor();
                }

                public static Descriptors.EnumDescriptor getDescriptor() {
                    return NestInFileClassFeature.getDescriptor().getEnumTypes().get(0);
                }

                public static NestInFileClass valueOf(Descriptors.EnumValueDescriptor desc) {
                    if (desc.getType() != NestInFileClass.getDescriptor()) {
                        throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
                    }
                    return VALUES[desc.getIndex()];
                }

                private NestInFileClass(int value) {
                    this.value = value;
                }

                static {
                    RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "NestInFileClass");
                    internalValueMap = new Internal.EnumLiteMap<NestInFileClass>(){

                        @Override
                        public NestInFileClass findValueByNumber(int number) {
                            return NestInFileClass.forNumber(number);
                        }
                    };
                    VALUES = NestInFileClass.values();
                }
            }
        }

        public static interface NestInFileClassFeatureOrBuilder
        extends MessageOrBuilder {
        }
    }

    public static interface JavaFeaturesOrBuilder
    extends MessageOrBuilder {
        public boolean hasLegacyClosedEnum();

        public boolean getLegacyClosedEnum();

        public boolean hasUtf8Validation();

        public JavaFeatures.Utf8Validation getUtf8Validation();

        public boolean hasLargeEnum();

        public boolean getLargeEnum();

        public boolean hasUseOldOuterClassnameDefault();

        public boolean getUseOldOuterClassnameDefault();

        public boolean hasNestInFileClass();

        public JavaFeatures.NestInFileClassFeature.NestInFileClass getNestInFileClass();
    }
}

