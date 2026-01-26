/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.bson.BsonArray;
import org.bson.BsonBinaryReader;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.bson.ByteBuf;
import org.bson.ByteBufNIO;
import org.bson.RawBsonValueHelper;
import org.bson.assertions.Assertions;
import org.bson.io.ByteBufferBsonInput;

public class RawBsonArray
extends BsonArray
implements Serializable {
    private static final long serialVersionUID = 2L;
    private static final String IMMUTABLE_MSG = "RawBsonArray instances are immutable";
    private final transient RawBsonArrayList delegate;

    public RawBsonArray(byte[] bytes) {
        this(Assertions.notNull("bytes", bytes), 0, bytes.length);
    }

    public RawBsonArray(byte[] bytes, int offset, int length) {
        this(new RawBsonArrayList(bytes, offset, length));
    }

    private RawBsonArray(RawBsonArrayList values) {
        super(values, false);
        this.delegate = values;
    }

    ByteBuf getByteBuffer() {
        return this.delegate.getByteBuffer();
    }

    @Override
    public boolean add(BsonValue bsonValue) {
        throw new UnsupportedOperationException(IMMUTABLE_MSG);
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException(IMMUTABLE_MSG);
    }

    @Override
    public boolean addAll(Collection<? extends BsonValue> c) {
        throw new UnsupportedOperationException(IMMUTABLE_MSG);
    }

    @Override
    public boolean addAll(int index, Collection<? extends BsonValue> c) {
        throw new UnsupportedOperationException(IMMUTABLE_MSG);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException(IMMUTABLE_MSG);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException(IMMUTABLE_MSG);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException(IMMUTABLE_MSG);
    }

    @Override
    public BsonValue set(int index, BsonValue element) {
        throw new UnsupportedOperationException(IMMUTABLE_MSG);
    }

    @Override
    public void add(int index, BsonValue element) {
        throw new UnsupportedOperationException(IMMUTABLE_MSG);
    }

    @Override
    public BsonValue remove(int index) {
        throw new UnsupportedOperationException(IMMUTABLE_MSG);
    }

    @Override
    public BsonArray clone() {
        return new RawBsonArray((byte[])this.delegate.bytes.clone(), this.delegate.offset, this.delegate.length);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    private Object writeReplace() {
        return new SerializationProxy(this.delegate.bytes, this.delegate.offset, this.delegate.length);
    }

    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required");
    }

    static class RawBsonArrayList
    extends AbstractList<BsonValue> {
        private static final int MIN_BSON_ARRAY_SIZE = 5;
        private Integer cachedSize;
        private final byte[] bytes;
        private final int offset;
        private final int length;

        RawBsonArrayList(byte[] bytes, int offset, int length) {
            Assertions.notNull("bytes", bytes);
            Assertions.isTrueArgument("offset >= 0", offset >= 0);
            Assertions.isTrueArgument("offset < bytes.length", offset < bytes.length);
            Assertions.isTrueArgument("length <= bytes.length - offset", length <= bytes.length - offset);
            Assertions.isTrueArgument("length >= 5", length >= 5);
            this.bytes = bytes;
            this.offset = offset;
            this.length = length;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public BsonValue get(int index) {
            if (index < 0) {
                throw new IndexOutOfBoundsException();
            }
            int curIndex = 0;
            try (BsonBinaryReader bsonReader = this.createReader();){
                bsonReader.readStartDocument();
                while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                    bsonReader.skipName();
                    if (curIndex == index) {
                        BsonValue bsonValue = RawBsonValueHelper.decode(this.bytes, bsonReader);
                        return bsonValue;
                    }
                    bsonReader.skipValue();
                    ++curIndex;
                }
                bsonReader.readEndDocument();
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public int size() {
            if (this.cachedSize != null) {
                return this.cachedSize;
            }
            int size = 0;
            try (BsonBinaryReader bsonReader = this.createReader();){
                bsonReader.readStartDocument();
                while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                    ++size;
                    bsonReader.readName();
                    bsonReader.skipValue();
                }
                bsonReader.readEndDocument();
            }
            this.cachedSize = size;
            return this.cachedSize;
        }

        @Override
        public Iterator<BsonValue> iterator() {
            return new Itr();
        }

        @Override
        public ListIterator<BsonValue> listIterator() {
            return new ListItr(0);
        }

        @Override
        public ListIterator<BsonValue> listIterator(int index) {
            return new ListItr(index);
        }

        private BsonBinaryReader createReader() {
            return new BsonBinaryReader(new ByteBufferBsonInput(this.getByteBuffer()));
        }

        ByteBuf getByteBuffer() {
            ByteBuffer buffer = ByteBuffer.wrap(this.bytes, this.offset, this.length);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            return new ByteBufNIO(buffer);
        }

        private class ListItr
        extends Itr
        implements ListIterator<BsonValue> {
            ListItr(int index) {
                super(index);
            }

            @Override
            public boolean hasPrevious() {
                return this.getCursor() != 0;
            }

            @Override
            public BsonValue previous() {
                try {
                    BsonValue previous = RawBsonArrayList.this.get(this.previousIndex());
                    this.setIterator(this.previousIndex());
                    return previous;
                }
                catch (IndexOutOfBoundsException e) {
                    throw new NoSuchElementException();
                }
            }

            @Override
            public int nextIndex() {
                return this.getCursor();
            }

            @Override
            public int previousIndex() {
                return this.getCursor() - 1;
            }

            @Override
            public void set(BsonValue bsonValue) {
                throw new UnsupportedOperationException(RawBsonArray.IMMUTABLE_MSG);
            }

            @Override
            public void add(BsonValue bsonValue) {
                throw new UnsupportedOperationException(RawBsonArray.IMMUTABLE_MSG);
            }
        }

        private class Itr
        implements Iterator<BsonValue> {
            private int cursor = 0;
            private BsonBinaryReader bsonReader;
            private int currentPosition = 0;

            Itr() {
                this(0);
            }

            Itr(int cursorPosition) {
                this.setIterator(cursorPosition);
            }

            @Override
            public boolean hasNext() {
                boolean hasNext;
                boolean bl = hasNext = this.cursor != RawBsonArrayList.this.size();
                if (!hasNext) {
                    this.bsonReader.close();
                }
                return hasNext;
            }

            @Override
            public BsonValue next() {
                while (this.cursor > this.currentPosition && this.bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                    this.bsonReader.skipName();
                    this.bsonReader.skipValue();
                    ++this.currentPosition;
                }
                if (this.bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                    this.bsonReader.skipName();
                    ++this.cursor;
                    this.currentPosition = this.cursor;
                    return RawBsonValueHelper.decode(RawBsonArrayList.this.bytes, this.bsonReader);
                }
                this.bsonReader.close();
                throw new NoSuchElementException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException(RawBsonArray.IMMUTABLE_MSG);
            }

            public int getCursor() {
                return this.cursor;
            }

            public void setCursor(int cursor) {
                this.cursor = cursor;
            }

            void setIterator(int cursorPosition) {
                this.cursor = cursorPosition;
                this.currentPosition = 0;
                if (this.bsonReader != null) {
                    this.bsonReader.close();
                }
                this.bsonReader = RawBsonArrayList.this.createReader();
                this.bsonReader.readStartDocument();
            }
        }
    }

    private static class SerializationProxy
    implements Serializable {
        private static final long serialVersionUID = 1L;
        private final byte[] bytes;

        SerializationProxy(byte[] bytes, int offset, int length) {
            if (bytes.length == length) {
                this.bytes = bytes;
            } else {
                this.bytes = new byte[length];
                System.arraycopy(bytes, offset, this.bytes, 0, length);
            }
        }

        private Object readResolve() {
            return new RawBsonArray(this.bytes);
        }
    }
}

