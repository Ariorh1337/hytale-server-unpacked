/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.LazyStringList;
import com.google.protobuf.MessageLite;
import com.google.protobuf.PrimitiveNonBoxingCollection;
import com.google.protobuf.ProtobufArrayList;
import com.google.protobuf.Schema;
import com.google.protobuf.UninitializedMessageException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

public abstract class AbstractMessageLite<MessageType extends AbstractMessageLite<MessageType, BuilderType>, BuilderType extends Builder<MessageType, BuilderType>>
implements MessageLite {
    protected int memoizedHashCode = 0;

    @Override
    public ByteString toByteString() {
        try {
            ByteString.CodedBuilder out = ByteString.newCodedBuilder(this.getSerializedSize());
            this.writeTo(out.getCodedOutput());
            return out.build();
        }
        catch (IOException e) {
            throw new RuntimeException(this.getSerializingExceptionMessage("ByteString"), e);
        }
    }

    @Override
    public byte[] toByteArray() {
        try {
            byte[] result = new byte[this.getSerializedSize()];
            CodedOutputStream output = CodedOutputStream.newInstance(result);
            this.writeTo(output);
            output.checkNoSpaceLeft();
            return result;
        }
        catch (IOException e) {
            throw new RuntimeException(this.getSerializingExceptionMessage("byte array"), e);
        }
    }

    @Override
    public void writeTo(OutputStream output) throws IOException {
        int bufferSize = CodedOutputStream.computePreferredBufferSize(this.getSerializedSize());
        CodedOutputStream codedOutput = CodedOutputStream.newInstance(output, bufferSize);
        this.writeTo(codedOutput);
        codedOutput.flush();
    }

    @Override
    public void writeDelimitedTo(OutputStream output) throws IOException {
        int serialized = this.getSerializedSize();
        int bufferSize = CodedOutputStream.computePreferredBufferSize(CodedOutputStream.computeUInt32SizeNoTag(serialized) + serialized);
        CodedOutputStream codedOutput = CodedOutputStream.newInstance(output, bufferSize);
        codedOutput.writeUInt32NoTag(serialized);
        this.writeTo(codedOutput);
        codedOutput.flush();
    }

    int getMemoizedSerializedSize() {
        throw new UnsupportedOperationException();
    }

    void setMemoizedSerializedSize(int size) {
        throw new UnsupportedOperationException();
    }

    int getSerializedSize(Schema schema) {
        int memoizedSerializedSize = this.getMemoizedSerializedSize();
        if (memoizedSerializedSize == -1) {
            memoizedSerializedSize = schema.getSerializedSize(this);
            this.setMemoizedSerializedSize(memoizedSerializedSize);
        }
        return memoizedSerializedSize;
    }

    UninitializedMessageException newUninitializedMessageException() {
        return new UninitializedMessageException(this);
    }

    private String getSerializingExceptionMessage(String target) {
        return "Serializing " + this.getClass().getName() + " to a " + target + " threw an IOException (should never happen).";
    }

    protected static void checkByteStringIsUtf8(ByteString byteString) throws IllegalArgumentException {
        if (!byteString.isValidUtf8()) {
            throw new IllegalArgumentException("Byte string is not UTF-8.");
        }
    }

    protected static <T> void addAll(Iterable<T> values, List<? super T> list) {
        Builder.addAll(values, list);
    }

    public static abstract class Builder<MessageType extends AbstractMessageLite<MessageType, BuilderType>, BuilderType extends Builder<MessageType, BuilderType>>
    implements MessageLite.Builder {
        public abstract BuilderType clone();

        public BuilderType mergeFrom(CodedInputStream input) throws IOException {
            return (BuilderType)this.mergeFrom(input, ExtensionRegistryLite.getEmptyRegistry());
        }

        public abstract BuilderType mergeFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws IOException;

        public BuilderType mergeFrom(ByteString data) throws InvalidProtocolBufferException {
            try {
                CodedInputStream input = data.newCodedInput();
                this.mergeFrom(input);
                input.checkLastTagWas(0);
                return (BuilderType)this;
            }
            catch (InvalidProtocolBufferException e) {
                throw e;
            }
            catch (IOException e) {
                throw new RuntimeException(this.getReadingExceptionMessage("ByteString"), e);
            }
        }

        public BuilderType mergeFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            try {
                CodedInputStream input = data.newCodedInput();
                this.mergeFrom(input, extensionRegistry);
                input.checkLastTagWas(0);
                return (BuilderType)this;
            }
            catch (InvalidProtocolBufferException e) {
                throw e;
            }
            catch (IOException e) {
                throw new RuntimeException(this.getReadingExceptionMessage("ByteString"), e);
            }
        }

        public BuilderType mergeFrom(byte[] data) throws InvalidProtocolBufferException {
            return (BuilderType)this.mergeFrom(data, 0, data.length);
        }

        public BuilderType mergeFrom(byte[] data, int off, int len) throws InvalidProtocolBufferException {
            try {
                CodedInputStream input = CodedInputStream.newInstance(data, off, len);
                this.mergeFrom(input);
                input.checkLastTagWas(0);
                return (BuilderType)this;
            }
            catch (InvalidProtocolBufferException e) {
                throw e;
            }
            catch (IOException e) {
                throw new RuntimeException(this.getReadingExceptionMessage("byte array"), e);
            }
        }

        public BuilderType mergeFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (BuilderType)this.mergeFrom(data, 0, data.length, extensionRegistry);
        }

        public BuilderType mergeFrom(byte[] data, int off, int len, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            try {
                CodedInputStream input = CodedInputStream.newInstance(data, off, len);
                this.mergeFrom(input, extensionRegistry);
                input.checkLastTagWas(0);
                return (BuilderType)this;
            }
            catch (InvalidProtocolBufferException e) {
                throw e;
            }
            catch (IOException e) {
                throw new RuntimeException(this.getReadingExceptionMessage("byte array"), e);
            }
        }

        public BuilderType mergeFrom(InputStream input) throws IOException {
            CodedInputStream codedInput = CodedInputStream.newInstance(input);
            this.mergeFrom(codedInput);
            codedInput.checkLastTagWas(0);
            return (BuilderType)this;
        }

        public BuilderType mergeFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            CodedInputStream codedInput = CodedInputStream.newInstance(input);
            this.mergeFrom(codedInput, extensionRegistry);
            codedInput.checkLastTagWas(0);
            return (BuilderType)this;
        }

        @Override
        public boolean mergeDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            int firstByte = input.read();
            if (firstByte == -1) {
                return false;
            }
            int size = CodedInputStream.readRawVarint32(firstByte, input);
            LimitedInputStream limitedInput = new LimitedInputStream(input, size);
            this.mergeFrom(limitedInput, extensionRegistry);
            return true;
        }

        @Override
        public boolean mergeDelimitedFrom(InputStream input) throws IOException {
            return this.mergeDelimitedFrom(input, ExtensionRegistryLite.getEmptyRegistry());
        }

        public BuilderType mergeFrom(MessageLite other) {
            if (!this.getDefaultInstanceForType().getClass().isInstance(other)) {
                throw new IllegalArgumentException("mergeFrom(MessageLite) can only merge messages of the same type.");
            }
            return this.internalMergeFrom((AbstractMessageLite)other);
        }

        protected abstract BuilderType internalMergeFrom(MessageType var1);

        private String getReadingExceptionMessage(String target) {
            return "Reading " + this.getClass().getName() + " from a " + target + " threw an IOException (should never happen).";
        }

        private static <T> void addAllCheckingNulls(Iterable<T> values, List<? super T> list) {
            if (values instanceof Collection) {
                int growth = ((Collection)values).size();
                if (list instanceof ArrayList) {
                    ((ArrayList)list).ensureCapacity(list.size() + growth);
                } else if (list instanceof ProtobufArrayList) {
                    ((ProtobufArrayList)list).ensureCapacity(list.size() + growth);
                }
            }
            int begin = list.size();
            if (values instanceof List && values instanceof RandomAccess) {
                List valuesList = (List)values;
                int n = valuesList.size();
                for (int i = 0; i < n; ++i) {
                    Object value = valuesList.get(i);
                    if (value == null) {
                        Builder.resetListAndThrow(list, begin);
                    }
                    list.add(value);
                }
            } else {
                for (T value : values) {
                    if (value == null) {
                        Builder.resetListAndThrow(list, begin);
                    }
                    list.add(value);
                }
            }
        }

        private static void resetListAndThrow(List<?> list, int begin) {
            String message = "Element at index " + (list.size() - begin) + " is null.";
            for (int i = list.size() - 1; i >= begin; --i) {
                list.remove(i);
            }
            throw new NullPointerException(message);
        }

        protected static UninitializedMessageException newUninitializedMessageException(MessageLite message) {
            return new UninitializedMessageException(message);
        }

        @Deprecated
        protected static <T> void addAll(Iterable<T> values, Collection<? super T> list) {
            Builder.addAll(values, (List)list);
        }

        protected static <T> void addAll(Iterable<T> values, List<? super T> list) {
            Internal.checkNotNull(values);
            if (values instanceof LazyStringList) {
                List<?> lazyValues = ((LazyStringList)values).getUnderlyingElements();
                LazyStringList lazyList = (LazyStringList)list;
                int begin = list.size();
                for (Object value : lazyValues) {
                    if (value == null) {
                        String message = "Element at index " + (lazyList.size() - begin) + " is null.";
                        for (int i = lazyList.size() - 1; i >= begin; --i) {
                            lazyList.remove(i);
                        }
                        throw new NullPointerException(message);
                    }
                    if (value instanceof ByteString) {
                        lazyList.add((ByteString)value);
                        continue;
                    }
                    if (value instanceof byte[]) {
                        lazyList.add(ByteString.copyFrom((byte[])value));
                        continue;
                    }
                    lazyList.add((String)value);
                }
            } else if (values instanceof PrimitiveNonBoxingCollection) {
                list.addAll((Collection)values);
            } else {
                Builder.addAllCheckingNulls(values, list);
            }
        }

        static final class LimitedInputStream
        extends FilterInputStream {
            private int limit;

            LimitedInputStream(InputStream in, int limit) {
                super(in);
                this.limit = limit;
            }

            @Override
            public int available() throws IOException {
                return Math.min(super.available(), this.limit);
            }

            @Override
            public int read() throws IOException {
                if (this.limit <= 0) {
                    return -1;
                }
                int result = super.read();
                if (result >= 0) {
                    --this.limit;
                }
                return result;
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                if (this.limit <= 0) {
                    return -1;
                }
                int result = super.read(b, off, len = Math.min(len, this.limit));
                if (result >= 0) {
                    this.limit -= result;
                }
                return result;
            }

            @Override
            public long skip(long n) throws IOException {
                int result = (int)super.skip(Math.min(n, (long)this.limit));
                if (result >= 0) {
                    this.limit -= result;
                }
                return result;
            }
        }
    }

    protected static interface InternalOneOfEnum {
        public int getNumber();
    }
}

