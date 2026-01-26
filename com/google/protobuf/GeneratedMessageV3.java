/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.BooleanArrayList;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DoubleArrayList;
import com.google.protobuf.FloatArrayList;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.IntArrayList;
import com.google.protobuf.Internal;
import com.google.protobuf.LongArrayList;
import com.google.protobuf.Message;
import com.google.protobuf.UnknownFieldSet;
import java.util.List;

@Deprecated
public abstract class GeneratedMessageV3
extends GeneratedMessage.ExtendableMessage<GeneratedMessageV3> {
    private static final long serialVersionUID = 1L;

    @Deprecated
    protected GeneratedMessageV3() {
    }

    @Deprecated
    protected GeneratedMessageV3(Builder<?> builder) {
        super(builder);
    }

    protected static Internal.IntList newIntList() {
        return new IntArrayList();
    }

    protected static Internal.LongList newLongList() {
        return new LongArrayList();
    }

    protected static Internal.FloatList newFloatList() {
        return new FloatArrayList();
    }

    protected static Internal.DoubleList newDoubleList() {
        return new DoubleArrayList();
    }

    protected static Internal.BooleanList newBooleanList() {
        return new BooleanArrayList();
    }

    @Deprecated
    protected static Internal.IntList mutableCopy(Internal.IntList list) {
        return GeneratedMessageV3.makeMutableCopy(list);
    }

    @Deprecated
    protected static Internal.LongList mutableCopy(Internal.LongList list) {
        return GeneratedMessageV3.makeMutableCopy(list);
    }

    @Deprecated
    protected static Internal.FloatList mutableCopy(Internal.FloatList list) {
        return GeneratedMessageV3.makeMutableCopy(list);
    }

    @Deprecated
    protected static Internal.DoubleList mutableCopy(Internal.DoubleList list) {
        return GeneratedMessageV3.makeMutableCopy(list);
    }

    @Deprecated
    protected static Internal.BooleanList mutableCopy(Internal.BooleanList list) {
        return GeneratedMessageV3.makeMutableCopy(list);
    }

    @Override
    @Deprecated
    protected FieldAccessorTable internalGetFieldAccessorTable() {
        throw new UnsupportedOperationException("Should be overridden in gencode.");
    }

    @Deprecated
    protected Object newInstance(UnusedPrivateParameter unused) {
        throw new UnsupportedOperationException("This method must be overridden by the subclass.");
    }

    @Deprecated
    protected abstract Message.Builder newBuilderForType(BuilderParent var1);

    @Override
    @Deprecated
    protected Message.Builder newBuilderForType(final AbstractMessage.BuilderParent parent) {
        return this.newBuilderForType(new BuilderParent(){

            @Override
            public void markDirty() {
                parent.markDirty();
            }
        });
    }

    @Deprecated
    protected static interface BuilderParent
    extends AbstractMessage.BuilderParent {
    }

    @Deprecated
    public static final class FieldAccessorTable
    extends GeneratedMessage.FieldAccessorTable {
        @Deprecated
        public FieldAccessorTable(Descriptors.Descriptor descriptor, String[] camelCaseNames, Class<? extends GeneratedMessageV3> messageClass, Class<? extends Builder<?>> builderClass) {
            super(descriptor, camelCaseNames, messageClass, builderClass);
        }

        @Deprecated
        public FieldAccessorTable(Descriptors.Descriptor descriptor, String[] camelCaseNames) {
            super(descriptor, camelCaseNames);
        }

        @Override
        @Deprecated
        public FieldAccessorTable ensureFieldAccessorsInitialized(Class<? extends GeneratedMessage> messageClass, Class<? extends GeneratedMessage.Builder<?>> builderClass) {
            return (FieldAccessorTable)super.ensureFieldAccessorsInitialized(messageClass, builderClass);
        }
    }

    @Deprecated
    public static abstract class ExtendableBuilder<MessageT extends ExtendableMessage<MessageT>, BuilderT extends ExtendableBuilder<MessageT, BuilderT>>
    extends Builder<BuilderT>
    implements ExtendableMessageOrBuilder<MessageT> {
        @Deprecated
        protected ExtendableBuilder() {
        }

        @Deprecated
        protected ExtendableBuilder(BuilderParent parent) {
            super(parent);
        }

        @Override
        @Deprecated
        public <T> BuilderT setExtension(GeneratedMessage.GeneratedExtension<MessageT, T> extension, T value) {
            return (BuilderT)((ExtendableBuilder)((GeneratedMessage.ExtendableBuilder)this).setExtension(extension, value));
        }

        @Override
        @Deprecated
        public <T> BuilderT setExtension(GeneratedMessage.GeneratedExtension<MessageT, List<T>> extension, int index, T value) {
            return (BuilderT)((ExtendableBuilder)((GeneratedMessage.ExtendableBuilder)this).setExtension(extension, index, value));
        }

        @Override
        @Deprecated
        public <T> BuilderT addExtension(GeneratedMessage.GeneratedExtension<MessageT, List<T>> extension, T value) {
            return (BuilderT)((ExtendableBuilder)((GeneratedMessage.ExtendableBuilder)this).addExtension(extension, value));
        }

        @Override
        @Deprecated
        public <T> BuilderT clearExtension(GeneratedMessage.GeneratedExtension<MessageT, T> extension) {
            return (BuilderT)((ExtendableBuilder)((GeneratedMessage.ExtendableBuilder)this).clearExtension(extension));
        }

        @Override
        @Deprecated
        public BuilderT setField(Descriptors.FieldDescriptor field, Object value) {
            return (BuilderT)((ExtendableBuilder)super.setField(field, value));
        }

        @Override
        @Deprecated
        public BuilderT clearField(Descriptors.FieldDescriptor field) {
            return (BuilderT)((ExtendableBuilder)super.clearField(field));
        }

        @Override
        @Deprecated
        public BuilderT clearOneof(Descriptors.OneofDescriptor oneof) {
            return (BuilderT)((ExtendableBuilder)super.clearOneof(oneof));
        }

        @Override
        @Deprecated
        public BuilderT setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (BuilderT)((ExtendableBuilder)super.setRepeatedField(field, index, value));
        }

        @Override
        @Deprecated
        public BuilderT addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (BuilderT)((ExtendableBuilder)super.addRepeatedField(field, value));
        }

        @Override
        @Deprecated
        protected final void mergeExtensionFields(ExtendableMessage<?> other) {
            super.mergeExtensionFields(other);
        }
    }

    @Deprecated
    public static abstract class ExtendableMessage<MessageT extends ExtendableMessage<MessageT>>
    extends GeneratedMessageV3
    implements ExtendableMessageOrBuilder<MessageT> {
        @Deprecated
        protected ExtendableMessage() {
        }

        @Deprecated
        protected ExtendableMessage(ExtendableBuilder<MessageT, ?> builder) {
            super((Builder<?>)builder);
        }

        @Override
        @Deprecated
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            throw new UnsupportedOperationException("Should be overridden in gencode.");
        }

        @Override
        @Deprecated
        protected ExtensionWriter newExtensionWriter() {
            return new ExtensionWriter(false);
        }

        @Override
        @Deprecated
        protected ExtensionWriter newMessageSetExtensionWriter() {
            return new ExtensionWriter(true);
        }

        @Deprecated
        protected class ExtensionWriter
        extends GeneratedMessage.ExtendableMessage.ExtensionWriter {
            private ExtensionWriter(boolean messageSetWireFormat) {
                super(messageSetWireFormat);
            }
        }
    }

    @Deprecated
    public static interface ExtendableMessageOrBuilder<MessageT extends ExtendableMessage<MessageT>>
    extends GeneratedMessage.ExtendableMessageOrBuilder<GeneratedMessageV3> {
    }

    @Deprecated
    public static abstract class Builder<BuilderT extends Builder<BuilderT>>
    extends GeneratedMessage.ExtendableBuilder<GeneratedMessageV3, BuilderT> {
        private BuilderParentImpl meAsParent;

        @Deprecated
        protected Builder() {
            super(null);
        }

        @Deprecated
        protected Builder(BuilderParent builderParent) {
            super(builderParent);
        }

        @Override
        @Deprecated
        public BuilderT clone() {
            return (BuilderT)((Builder)super.clone());
        }

        @Override
        @Deprecated
        public BuilderT clear() {
            return (BuilderT)((Builder)super.clear());
        }

        @Override
        @Deprecated
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            throw new UnsupportedOperationException("Should be overridden in gencode.");
        }

        @Override
        @Deprecated
        public BuilderT setField(Descriptors.FieldDescriptor field, Object value) {
            return (BuilderT)((Builder)super.setField(field, value));
        }

        @Override
        @Deprecated
        public BuilderT clearField(Descriptors.FieldDescriptor field) {
            return (BuilderT)((Builder)super.clearField(field));
        }

        @Override
        @Deprecated
        public BuilderT clearOneof(Descriptors.OneofDescriptor oneof) {
            return (BuilderT)((Builder)super.clearOneof(oneof));
        }

        @Override
        @Deprecated
        public BuilderT setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (BuilderT)((Builder)super.setRepeatedField(field, index, value));
        }

        @Override
        @Deprecated
        public BuilderT addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (BuilderT)((Builder)super.addRepeatedField(field, value));
        }

        @Override
        @Deprecated
        public BuilderT setUnknownFields(UnknownFieldSet unknownFields) {
            return (BuilderT)((Builder)super.setUnknownFields(unknownFields));
        }

        @Override
        @Deprecated
        public BuilderT mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (BuilderT)((Builder)super.mergeUnknownFields(unknownFields));
        }

        @Override
        @Deprecated
        protected BuilderParent getParentForChildren() {
            if (this.meAsParent == null) {
                this.meAsParent = new BuilderParentImpl();
            }
            return this.meAsParent;
        }

        @Deprecated
        private class BuilderParentImpl
        implements BuilderParent {
            private BuilderParentImpl() {
            }

            @Override
            public void markDirty() {
                Builder.this.onChanged();
            }
        }
    }

    @Deprecated
    protected static final class UnusedPrivateParameter {
        static final UnusedPrivateParameter INSTANCE = new UnusedPrivateParameter();

        private UnusedPrivateParameter() {
        }
    }
}

