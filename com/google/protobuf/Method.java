/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.ApiProto;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MethodOrBuilder;
import com.google.protobuf.Option;
import com.google.protobuf.OptionOrBuilder;
import com.google.protobuf.Parser;
import com.google.protobuf.RepeatedFieldBuilder;
import com.google.protobuf.RuntimeVersion;
import com.google.protobuf.Syntax;
import com.google.protobuf.UninitializedMessageException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Method
extends GeneratedMessage
implements MethodOrBuilder {
    private static final long serialVersionUID = 0L;
    public static final int NAME_FIELD_NUMBER = 1;
    private volatile Object name_ = "";
    public static final int REQUEST_TYPE_URL_FIELD_NUMBER = 2;
    private volatile Object requestTypeUrl_ = "";
    public static final int REQUEST_STREAMING_FIELD_NUMBER = 3;
    private boolean requestStreaming_ = false;
    public static final int RESPONSE_TYPE_URL_FIELD_NUMBER = 4;
    private volatile Object responseTypeUrl_ = "";
    public static final int RESPONSE_STREAMING_FIELD_NUMBER = 5;
    private boolean responseStreaming_ = false;
    public static final int OPTIONS_FIELD_NUMBER = 6;
    private List<Option> options_;
    public static final int SYNTAX_FIELD_NUMBER = 7;
    private int syntax_ = 0;
    public static final int EDITION_FIELD_NUMBER = 8;
    private volatile Object edition_ = "";
    private byte memoizedIsInitialized = (byte)-1;
    private static final Method DEFAULT_INSTANCE;
    private static final Parser<Method> PARSER;

    private Method(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private Method() {
        this.name_ = "";
        this.requestTypeUrl_ = "";
        this.responseTypeUrl_ = "";
        this.options_ = Collections.emptyList();
        this.syntax_ = 0;
        this.edition_ = "";
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return ApiProto.internal_static_google_protobuf_Method_descriptor;
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return ApiProto.internal_static_google_protobuf_Method_fieldAccessorTable.ensureFieldAccessorsInitialized(Method.class, Builder.class);
    }

    @Override
    public String getName() {
        Object ref = this.name_;
        if (ref instanceof String) {
            return (String)ref;
        }
        ByteString bs = (ByteString)ref;
        String s = bs.toStringUtf8();
        this.name_ = s;
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
    public String getRequestTypeUrl() {
        Object ref = this.requestTypeUrl_;
        if (ref instanceof String) {
            return (String)ref;
        }
        ByteString bs = (ByteString)ref;
        String s = bs.toStringUtf8();
        this.requestTypeUrl_ = s;
        return s;
    }

    @Override
    public ByteString getRequestTypeUrlBytes() {
        Object ref = this.requestTypeUrl_;
        if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.requestTypeUrl_ = b;
            return b;
        }
        return (ByteString)ref;
    }

    @Override
    public boolean getRequestStreaming() {
        return this.requestStreaming_;
    }

    @Override
    public String getResponseTypeUrl() {
        Object ref = this.responseTypeUrl_;
        if (ref instanceof String) {
            return (String)ref;
        }
        ByteString bs = (ByteString)ref;
        String s = bs.toStringUtf8();
        this.responseTypeUrl_ = s;
        return s;
    }

    @Override
    public ByteString getResponseTypeUrlBytes() {
        Object ref = this.responseTypeUrl_;
        if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.responseTypeUrl_ = b;
            return b;
        }
        return (ByteString)ref;
    }

    @Override
    public boolean getResponseStreaming() {
        return this.responseStreaming_;
    }

    @Override
    public List<Option> getOptionsList() {
        return this.options_;
    }

    @Override
    public List<? extends OptionOrBuilder> getOptionsOrBuilderList() {
        return this.options_;
    }

    @Override
    public int getOptionsCount() {
        return this.options_.size();
    }

    @Override
    public Option getOptions(int index) {
        return this.options_.get(index);
    }

    @Override
    public OptionOrBuilder getOptionsOrBuilder(int index) {
        return this.options_.get(index);
    }

    @Override
    @Deprecated
    public int getSyntaxValue() {
        return this.syntax_;
    }

    @Override
    @Deprecated
    public Syntax getSyntax() {
        Syntax result = Syntax.forNumber(this.syntax_);
        return result == null ? Syntax.UNRECOGNIZED : result;
    }

    @Override
    @Deprecated
    public String getEdition() {
        Object ref = this.edition_;
        if (ref instanceof String) {
            return (String)ref;
        }
        ByteString bs = (ByteString)ref;
        String s = bs.toStringUtf8();
        this.edition_ = s;
        return s;
    }

    @Override
    @Deprecated
    public ByteString getEditionBytes() {
        Object ref = this.edition_;
        if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.edition_ = b;
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
        if (!GeneratedMessage.isStringEmpty(this.name_)) {
            GeneratedMessage.writeString(output, 1, this.name_);
        }
        if (!GeneratedMessage.isStringEmpty(this.requestTypeUrl_)) {
            GeneratedMessage.writeString(output, 2, this.requestTypeUrl_);
        }
        if (this.requestStreaming_) {
            output.writeBool(3, this.requestStreaming_);
        }
        if (!GeneratedMessage.isStringEmpty(this.responseTypeUrl_)) {
            GeneratedMessage.writeString(output, 4, this.responseTypeUrl_);
        }
        if (this.responseStreaming_) {
            output.writeBool(5, this.responseStreaming_);
        }
        for (int i = 0; i < this.options_.size(); ++i) {
            output.writeMessage(6, this.options_.get(i));
        }
        if (this.syntax_ != Syntax.SYNTAX_PROTO2.getNumber()) {
            output.writeEnum(7, this.syntax_);
        }
        if (!GeneratedMessage.isStringEmpty(this.edition_)) {
            GeneratedMessage.writeString(output, 8, this.edition_);
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
        if (!GeneratedMessage.isStringEmpty(this.name_)) {
            size += GeneratedMessage.computeStringSize(1, this.name_);
        }
        if (!GeneratedMessage.isStringEmpty(this.requestTypeUrl_)) {
            size += GeneratedMessage.computeStringSize(2, this.requestTypeUrl_);
        }
        if (this.requestStreaming_) {
            size += CodedOutputStream.computeBoolSize(3, this.requestStreaming_);
        }
        if (!GeneratedMessage.isStringEmpty(this.responseTypeUrl_)) {
            size += GeneratedMessage.computeStringSize(4, this.responseTypeUrl_);
        }
        if (this.responseStreaming_) {
            size += CodedOutputStream.computeBoolSize(5, this.responseStreaming_);
        }
        for (int i = 0; i < this.options_.size(); ++i) {
            size += CodedOutputStream.computeMessageSize(6, this.options_.get(i));
        }
        if (this.syntax_ != Syntax.SYNTAX_PROTO2.getNumber()) {
            size += CodedOutputStream.computeEnumSize(7, this.syntax_);
        }
        if (!GeneratedMessage.isStringEmpty(this.edition_)) {
            size += GeneratedMessage.computeStringSize(8, this.edition_);
        }
        this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Method)) {
            return super.equals(obj);
        }
        Method other = (Method)obj;
        if (!this.getName().equals(other.getName())) {
            return false;
        }
        if (!this.getRequestTypeUrl().equals(other.getRequestTypeUrl())) {
            return false;
        }
        if (this.getRequestStreaming() != other.getRequestStreaming()) {
            return false;
        }
        if (!this.getResponseTypeUrl().equals(other.getResponseTypeUrl())) {
            return false;
        }
        if (this.getResponseStreaming() != other.getResponseStreaming()) {
            return false;
        }
        if (!this.getOptionsList().equals(other.getOptionsList())) {
            return false;
        }
        if (this.syntax_ != other.syntax_) {
            return false;
        }
        if (!this.getEdition().equals(other.getEdition())) {
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
        hash = 19 * hash + Method.getDescriptor().hashCode();
        hash = 37 * hash + 1;
        hash = 53 * hash + this.getName().hashCode();
        hash = 37 * hash + 2;
        hash = 53 * hash + this.getRequestTypeUrl().hashCode();
        hash = 37 * hash + 3;
        hash = 53 * hash + Internal.hashBoolean(this.getRequestStreaming());
        hash = 37 * hash + 4;
        hash = 53 * hash + this.getResponseTypeUrl().hashCode();
        hash = 37 * hash + 5;
        hash = 53 * hash + Internal.hashBoolean(this.getResponseStreaming());
        if (this.getOptionsCount() > 0) {
            hash = 37 * hash + 6;
            hash = 53 * hash + this.getOptionsList().hashCode();
        }
        hash = 37 * hash + 7;
        hash = 53 * hash + this.syntax_;
        hash = 37 * hash + 8;
        hash = 53 * hash + this.getEdition().hashCode();
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static Method parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static Method parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static Method parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static Method parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static Method parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static Method parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static Method parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static Method parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static Method parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static Method parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static Method parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static Method parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return Method.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(Method prototype) {
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

    public static Method getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<Method> parser() {
        return PARSER;
    }

    public Parser<Method> getParserForType() {
        return PARSER;
    }

    @Override
    public Method getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "Method");
        DEFAULT_INSTANCE = new Method();
        PARSER = new AbstractParser<Method>(){

            @Override
            public Method parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = Method.newBuilder();
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
    implements MethodOrBuilder {
        private int bitField0_;
        private Object name_ = "";
        private Object requestTypeUrl_ = "";
        private boolean requestStreaming_;
        private Object responseTypeUrl_ = "";
        private boolean responseStreaming_;
        private List<Option> options_ = Collections.emptyList();
        private RepeatedFieldBuilder<Option, Option.Builder, OptionOrBuilder> optionsBuilder_;
        private int syntax_ = 0;
        private Object edition_ = "";

        public static final Descriptors.Descriptor getDescriptor() {
            return ApiProto.internal_static_google_protobuf_Method_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return ApiProto.internal_static_google_protobuf_Method_fieldAccessorTable.ensureFieldAccessorsInitialized(Method.class, Builder.class);
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
            this.name_ = "";
            this.requestTypeUrl_ = "";
            this.requestStreaming_ = false;
            this.responseTypeUrl_ = "";
            this.responseStreaming_ = false;
            if (this.optionsBuilder_ == null) {
                this.options_ = Collections.emptyList();
            } else {
                this.options_ = null;
                this.optionsBuilder_.clear();
            }
            this.bitField0_ &= 0xFFFFFFDF;
            this.syntax_ = 0;
            this.edition_ = "";
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return ApiProto.internal_static_google_protobuf_Method_descriptor;
        }

        @Override
        public Method getDefaultInstanceForType() {
            return Method.getDefaultInstance();
        }

        @Override
        public Method build() {
            Method result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public Method buildPartial() {
            Method result = new Method(this);
            this.buildPartialRepeatedFields(result);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartialRepeatedFields(Method result) {
            if (this.optionsBuilder_ == null) {
                if ((this.bitField0_ & 0x20) != 0) {
                    this.options_ = Collections.unmodifiableList(this.options_);
                    this.bitField0_ &= 0xFFFFFFDF;
                }
                result.options_ = this.options_;
            } else {
                result.options_ = this.optionsBuilder_.build();
            }
        }

        private void buildPartial0(Method result) {
            int from_bitField0_ = this.bitField0_;
            if ((from_bitField0_ & 1) != 0) {
                result.name_ = this.name_;
            }
            if ((from_bitField0_ & 2) != 0) {
                result.requestTypeUrl_ = this.requestTypeUrl_;
            }
            if ((from_bitField0_ & 4) != 0) {
                result.requestStreaming_ = this.requestStreaming_;
            }
            if ((from_bitField0_ & 8) != 0) {
                result.responseTypeUrl_ = this.responseTypeUrl_;
            }
            if ((from_bitField0_ & 0x10) != 0) {
                result.responseStreaming_ = this.responseStreaming_;
            }
            if ((from_bitField0_ & 0x40) != 0) {
                result.syntax_ = this.syntax_;
            }
            if ((from_bitField0_ & 0x80) != 0) {
                result.edition_ = this.edition_;
            }
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof Method) {
                return this.mergeFrom((Method)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(Method other) {
            if (other == Method.getDefaultInstance()) {
                return this;
            }
            if (!other.getName().isEmpty()) {
                this.name_ = other.name_;
                this.bitField0_ |= 1;
                this.onChanged();
            }
            if (!other.getRequestTypeUrl().isEmpty()) {
                this.requestTypeUrl_ = other.requestTypeUrl_;
                this.bitField0_ |= 2;
                this.onChanged();
            }
            if (other.getRequestStreaming()) {
                this.setRequestStreaming(other.getRequestStreaming());
            }
            if (!other.getResponseTypeUrl().isEmpty()) {
                this.responseTypeUrl_ = other.responseTypeUrl_;
                this.bitField0_ |= 8;
                this.onChanged();
            }
            if (other.getResponseStreaming()) {
                this.setResponseStreaming(other.getResponseStreaming());
            }
            if (this.optionsBuilder_ == null) {
                if (!other.options_.isEmpty()) {
                    if (this.options_.isEmpty()) {
                        this.options_ = other.options_;
                        this.bitField0_ &= 0xFFFFFFDF;
                    } else {
                        this.ensureOptionsIsMutable();
                        this.options_.addAll(other.options_);
                    }
                    this.onChanged();
                }
            } else if (!other.options_.isEmpty()) {
                if (this.optionsBuilder_.isEmpty()) {
                    this.optionsBuilder_.dispose();
                    this.optionsBuilder_ = null;
                    this.options_ = other.options_;
                    this.bitField0_ &= 0xFFFFFFDF;
                    this.optionsBuilder_ = GeneratedMessage.alwaysUseFieldBuilders ? this.internalGetOptionsFieldBuilder() : null;
                } else {
                    this.optionsBuilder_.addAllMessages(other.options_);
                }
            }
            if (other.syntax_ != 0) {
                this.setSyntaxValue(other.getSyntaxValue());
            }
            if (!other.getEdition().isEmpty()) {
                this.edition_ = other.edition_;
                this.bitField0_ |= 0x80;
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
                block16: while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue block16;
                        }
                        case 10: {
                            this.name_ = input.readStringRequireUtf8();
                            this.bitField0_ |= 1;
                            continue block16;
                        }
                        case 18: {
                            this.requestTypeUrl_ = input.readStringRequireUtf8();
                            this.bitField0_ |= 2;
                            continue block16;
                        }
                        case 24: {
                            this.requestStreaming_ = input.readBool();
                            this.bitField0_ |= 4;
                            continue block16;
                        }
                        case 34: {
                            this.responseTypeUrl_ = input.readStringRequireUtf8();
                            this.bitField0_ |= 8;
                            continue block16;
                        }
                        case 40: {
                            this.responseStreaming_ = input.readBool();
                            this.bitField0_ |= 0x10;
                            continue block16;
                        }
                        case 50: {
                            Option m = input.readMessage(Option.parser(), extensionRegistry);
                            if (this.optionsBuilder_ == null) {
                                this.ensureOptionsIsMutable();
                                this.options_.add(m);
                                continue block16;
                            }
                            this.optionsBuilder_.addMessage(m);
                            continue block16;
                        }
                        case 56: {
                            this.syntax_ = input.readEnum();
                            this.bitField0_ |= 0x40;
                            continue block16;
                        }
                        case 66: {
                            this.edition_ = input.readStringRequireUtf8();
                            this.bitField0_ |= 0x80;
                            continue block16;
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
        public String getName() {
            Object ref = this.name_;
            if (!(ref instanceof String)) {
                ByteString bs = (ByteString)ref;
                String s = bs.toStringUtf8();
                this.name_ = s;
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
            this.name_ = Method.getDefaultInstance().getName();
            this.bitField0_ &= 0xFFFFFFFE;
            this.onChanged();
            return this;
        }

        public Builder setNameBytes(ByteString value) {
            if (value == null) {
                throw new NullPointerException();
            }
            AbstractMessageLite.checkByteStringIsUtf8(value);
            this.name_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
        }

        @Override
        public String getRequestTypeUrl() {
            Object ref = this.requestTypeUrl_;
            if (!(ref instanceof String)) {
                ByteString bs = (ByteString)ref;
                String s = bs.toStringUtf8();
                this.requestTypeUrl_ = s;
                return s;
            }
            return (String)ref;
        }

        @Override
        public ByteString getRequestTypeUrlBytes() {
            Object ref = this.requestTypeUrl_;
            if (ref instanceof String) {
                ByteString b = ByteString.copyFromUtf8((String)ref);
                this.requestTypeUrl_ = b;
                return b;
            }
            return (ByteString)ref;
        }

        public Builder setRequestTypeUrl(String value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.requestTypeUrl_ = value;
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
        }

        public Builder clearRequestTypeUrl() {
            this.requestTypeUrl_ = Method.getDefaultInstance().getRequestTypeUrl();
            this.bitField0_ &= 0xFFFFFFFD;
            this.onChanged();
            return this;
        }

        public Builder setRequestTypeUrlBytes(ByteString value) {
            if (value == null) {
                throw new NullPointerException();
            }
            AbstractMessageLite.checkByteStringIsUtf8(value);
            this.requestTypeUrl_ = value;
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
        }

        @Override
        public boolean getRequestStreaming() {
            return this.requestStreaming_;
        }

        public Builder setRequestStreaming(boolean value) {
            this.requestStreaming_ = value;
            this.bitField0_ |= 4;
            this.onChanged();
            return this;
        }

        public Builder clearRequestStreaming() {
            this.bitField0_ &= 0xFFFFFFFB;
            this.requestStreaming_ = false;
            this.onChanged();
            return this;
        }

        @Override
        public String getResponseTypeUrl() {
            Object ref = this.responseTypeUrl_;
            if (!(ref instanceof String)) {
                ByteString bs = (ByteString)ref;
                String s = bs.toStringUtf8();
                this.responseTypeUrl_ = s;
                return s;
            }
            return (String)ref;
        }

        @Override
        public ByteString getResponseTypeUrlBytes() {
            Object ref = this.responseTypeUrl_;
            if (ref instanceof String) {
                ByteString b = ByteString.copyFromUtf8((String)ref);
                this.responseTypeUrl_ = b;
                return b;
            }
            return (ByteString)ref;
        }

        public Builder setResponseTypeUrl(String value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.responseTypeUrl_ = value;
            this.bitField0_ |= 8;
            this.onChanged();
            return this;
        }

        public Builder clearResponseTypeUrl() {
            this.responseTypeUrl_ = Method.getDefaultInstance().getResponseTypeUrl();
            this.bitField0_ &= 0xFFFFFFF7;
            this.onChanged();
            return this;
        }

        public Builder setResponseTypeUrlBytes(ByteString value) {
            if (value == null) {
                throw new NullPointerException();
            }
            AbstractMessageLite.checkByteStringIsUtf8(value);
            this.responseTypeUrl_ = value;
            this.bitField0_ |= 8;
            this.onChanged();
            return this;
        }

        @Override
        public boolean getResponseStreaming() {
            return this.responseStreaming_;
        }

        public Builder setResponseStreaming(boolean value) {
            this.responseStreaming_ = value;
            this.bitField0_ |= 0x10;
            this.onChanged();
            return this;
        }

        public Builder clearResponseStreaming() {
            this.bitField0_ &= 0xFFFFFFEF;
            this.responseStreaming_ = false;
            this.onChanged();
            return this;
        }

        private void ensureOptionsIsMutable() {
            if ((this.bitField0_ & 0x20) == 0) {
                this.options_ = new ArrayList<Option>(this.options_);
                this.bitField0_ |= 0x20;
            }
        }

        @Override
        public List<Option> getOptionsList() {
            if (this.optionsBuilder_ == null) {
                return Collections.unmodifiableList(this.options_);
            }
            return this.optionsBuilder_.getMessageList();
        }

        @Override
        public int getOptionsCount() {
            if (this.optionsBuilder_ == null) {
                return this.options_.size();
            }
            return this.optionsBuilder_.getCount();
        }

        @Override
        public Option getOptions(int index) {
            if (this.optionsBuilder_ == null) {
                return this.options_.get(index);
            }
            return this.optionsBuilder_.getMessage(index);
        }

        public Builder setOptions(int index, Option value) {
            if (this.optionsBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureOptionsIsMutable();
                this.options_.set(index, value);
                this.onChanged();
            } else {
                this.optionsBuilder_.setMessage(index, value);
            }
            return this;
        }

        public Builder setOptions(int index, Option.Builder builderForValue) {
            if (this.optionsBuilder_ == null) {
                this.ensureOptionsIsMutable();
                this.options_.set(index, builderForValue.build());
                this.onChanged();
            } else {
                this.optionsBuilder_.setMessage(index, builderForValue.build());
            }
            return this;
        }

        public Builder addOptions(Option value) {
            if (this.optionsBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureOptionsIsMutable();
                this.options_.add(value);
                this.onChanged();
            } else {
                this.optionsBuilder_.addMessage(value);
            }
            return this;
        }

        public Builder addOptions(int index, Option value) {
            if (this.optionsBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureOptionsIsMutable();
                this.options_.add(index, value);
                this.onChanged();
            } else {
                this.optionsBuilder_.addMessage(index, value);
            }
            return this;
        }

        public Builder addOptions(Option.Builder builderForValue) {
            if (this.optionsBuilder_ == null) {
                this.ensureOptionsIsMutable();
                this.options_.add(builderForValue.build());
                this.onChanged();
            } else {
                this.optionsBuilder_.addMessage(builderForValue.build());
            }
            return this;
        }

        public Builder addOptions(int index, Option.Builder builderForValue) {
            if (this.optionsBuilder_ == null) {
                this.ensureOptionsIsMutable();
                this.options_.add(index, builderForValue.build());
                this.onChanged();
            } else {
                this.optionsBuilder_.addMessage(index, builderForValue.build());
            }
            return this;
        }

        public Builder addAllOptions(Iterable<? extends Option> values) {
            if (this.optionsBuilder_ == null) {
                this.ensureOptionsIsMutable();
                AbstractMessageLite.Builder.addAll(values, this.options_);
                this.onChanged();
            } else {
                this.optionsBuilder_.addAllMessages(values);
            }
            return this;
        }

        public Builder clearOptions() {
            if (this.optionsBuilder_ == null) {
                this.options_ = Collections.emptyList();
                this.bitField0_ &= 0xFFFFFFDF;
                this.onChanged();
            } else {
                this.optionsBuilder_.clear();
            }
            return this;
        }

        public Builder removeOptions(int index) {
            if (this.optionsBuilder_ == null) {
                this.ensureOptionsIsMutable();
                this.options_.remove(index);
                this.onChanged();
            } else {
                this.optionsBuilder_.remove(index);
            }
            return this;
        }

        public Option.Builder getOptionsBuilder(int index) {
            return this.internalGetOptionsFieldBuilder().getBuilder(index);
        }

        @Override
        public OptionOrBuilder getOptionsOrBuilder(int index) {
            if (this.optionsBuilder_ == null) {
                return this.options_.get(index);
            }
            return this.optionsBuilder_.getMessageOrBuilder(index);
        }

        @Override
        public List<? extends OptionOrBuilder> getOptionsOrBuilderList() {
            if (this.optionsBuilder_ != null) {
                return this.optionsBuilder_.getMessageOrBuilderList();
            }
            return Collections.unmodifiableList(this.options_);
        }

        public Option.Builder addOptionsBuilder() {
            return this.internalGetOptionsFieldBuilder().addBuilder(Option.getDefaultInstance());
        }

        public Option.Builder addOptionsBuilder(int index) {
            return this.internalGetOptionsFieldBuilder().addBuilder(index, Option.getDefaultInstance());
        }

        public List<Option.Builder> getOptionsBuilderList() {
            return this.internalGetOptionsFieldBuilder().getBuilderList();
        }

        private RepeatedFieldBuilder<Option, Option.Builder, OptionOrBuilder> internalGetOptionsFieldBuilder() {
            if (this.optionsBuilder_ == null) {
                this.optionsBuilder_ = new RepeatedFieldBuilder(this.options_, (this.bitField0_ & 0x20) != 0, this.getParentForChildren(), this.isClean());
                this.options_ = null;
            }
            return this.optionsBuilder_;
        }

        @Override
        @Deprecated
        public int getSyntaxValue() {
            return this.syntax_;
        }

        @Deprecated
        public Builder setSyntaxValue(int value) {
            this.syntax_ = value;
            this.bitField0_ |= 0x40;
            this.onChanged();
            return this;
        }

        @Override
        @Deprecated
        public Syntax getSyntax() {
            Syntax result = Syntax.forNumber(this.syntax_);
            return result == null ? Syntax.UNRECOGNIZED : result;
        }

        @Deprecated
        public Builder setSyntax(Syntax value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.bitField0_ |= 0x40;
            this.syntax_ = value.getNumber();
            this.onChanged();
            return this;
        }

        @Deprecated
        public Builder clearSyntax() {
            this.bitField0_ &= 0xFFFFFFBF;
            this.syntax_ = 0;
            this.onChanged();
            return this;
        }

        @Override
        @Deprecated
        public String getEdition() {
            Object ref = this.edition_;
            if (!(ref instanceof String)) {
                ByteString bs = (ByteString)ref;
                String s = bs.toStringUtf8();
                this.edition_ = s;
                return s;
            }
            return (String)ref;
        }

        @Override
        @Deprecated
        public ByteString getEditionBytes() {
            Object ref = this.edition_;
            if (ref instanceof String) {
                ByteString b = ByteString.copyFromUtf8((String)ref);
                this.edition_ = b;
                return b;
            }
            return (ByteString)ref;
        }

        @Deprecated
        public Builder setEdition(String value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.edition_ = value;
            this.bitField0_ |= 0x80;
            this.onChanged();
            return this;
        }

        @Deprecated
        public Builder clearEdition() {
            this.edition_ = Method.getDefaultInstance().getEdition();
            this.bitField0_ &= 0xFFFFFF7F;
            this.onChanged();
            return this;
        }

        @Deprecated
        public Builder setEditionBytes(ByteString value) {
            if (value == null) {
                throw new NullPointerException();
            }
            AbstractMessageLite.checkByteStringIsUtf8(value);
            this.edition_ = value;
            this.bitField0_ |= 0x80;
            this.onChanged();
            return this;
        }
    }
}

