/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.bson.BsonBinaryReader;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonInvalidOperationException;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.ByteBuf;
import org.bson.ByteBufNIO;
import org.bson.RawBsonValueHelper;
import org.bson.assertions.Assertions;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.Codec;
import org.bson.codecs.Decoder;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.RawBsonDocumentCodec;
import org.bson.io.BasicOutputBuffer;
import org.bson.io.ByteBufferBsonInput;
import org.bson.json.JsonMode;
import org.bson.json.JsonReader;
import org.bson.json.JsonWriter;
import org.bson.json.JsonWriterSettings;

public final class RawBsonDocument
extends BsonDocument {
    private static final long serialVersionUID = 1L;
    private static final int MIN_BSON_DOCUMENT_SIZE = 5;
    private final byte[] bytes;
    private final int offset;
    private final int length;

    public static RawBsonDocument parse(String json) {
        Assertions.notNull("json", json);
        return new RawBsonDocumentCodec().decode(new JsonReader(json), DecoderContext.builder().build());
    }

    public RawBsonDocument(byte[] bytes) {
        this(Assertions.notNull("bytes", bytes), 0, bytes.length);
    }

    public RawBsonDocument(byte[] bytes, int offset, int length) {
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
    public <T> RawBsonDocument(T document, Codec<T> codec) {
        Assertions.notNull("document", document);
        Assertions.notNull("codec", codec);
        BasicOutputBuffer buffer = new BasicOutputBuffer();
        try (BsonBinaryWriter writer = new BsonBinaryWriter(buffer);){
            codec.encode(writer, document, EncoderContext.builder().build());
            this.bytes = buffer.getInternalBuffer();
            this.offset = 0;
            this.length = buffer.getPosition();
        }
    }

    public ByteBuf getByteBuffer() {
        ByteBuffer buffer = ByteBuffer.wrap(this.bytes, this.offset, this.length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return new ByteBufNIO(buffer);
    }

    public <T> T decode(Codec<T> codec) {
        return this.decode((Decoder<T>)codec);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> T decode(Decoder<T> decoder) {
        try (BsonBinaryReader reader = this.createReader();){
            T t = decoder.decode(reader, DecoderContext.builder().build());
            return t;
        }
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("RawBsonDocument instances are immutable");
    }

    @Override
    public BsonValue put(String key, BsonValue value) {
        throw new UnsupportedOperationException("RawBsonDocument instances are immutable");
    }

    @Override
    public BsonDocument append(String key, BsonValue value) {
        throw new UnsupportedOperationException("RawBsonDocument instances are immutable");
    }

    @Override
    public void putAll(Map<? extends String, ? extends BsonValue> m) {
        throw new UnsupportedOperationException("RawBsonDocument instances are immutable");
    }

    @Override
    public BsonValue remove(Object key) {
        throw new UnsupportedOperationException("RawBsonDocument instances are immutable");
    }

    @Override
    public boolean isEmpty() {
        try (BsonBinaryReader bsonReader = this.createReader();){
            bsonReader.readStartDocument();
            if (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                boolean bl = false;
                return bl;
            }
            bsonReader.readEndDocument();
        }
        return true;
    }

    @Override
    public int size() {
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
        return size;
    }

    @Override
    public Set<Map.Entry<String, BsonValue>> entrySet() {
        return this.toBaseBsonDocument().entrySet();
    }

    @Override
    public Collection<BsonValue> values() {
        return this.toBaseBsonDocument().values();
    }

    @Override
    public Set<String> keySet() {
        return this.toBaseBsonDocument().keySet();
    }

    @Override
    public String getFirstKey() {
        try (BsonBinaryReader bsonReader = this.createReader();){
            bsonReader.readStartDocument();
            try {
                String string = bsonReader.readName();
                return string;
            }
            catch (BsonInvalidOperationException e) {
                throw new NoSuchElementException();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("key can not be null");
        }
        try (BsonBinaryReader bsonReader = this.createReader();){
            bsonReader.readStartDocument();
            while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                if (bsonReader.readName().equals(key)) {
                    boolean bl = true;
                    return bl;
                }
                bsonReader.skipValue();
            }
            bsonReader.readEndDocument();
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean containsValue(Object value) {
        try (BsonBinaryReader bsonReader = this.createReader();){
            bsonReader.readStartDocument();
            while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                bsonReader.skipName();
                if (!RawBsonValueHelper.decode(this.bytes, bsonReader).equals(value)) continue;
                boolean bl = true;
                return bl;
            }
            bsonReader.readEndDocument();
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BsonValue get(Object key) {
        Assertions.notNull("key", key);
        try (BsonBinaryReader bsonReader = this.createReader();){
            bsonReader.readStartDocument();
            while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                if (bsonReader.readName().equals(key)) {
                    BsonValue bsonValue = RawBsonValueHelper.decode(this.bytes, bsonReader);
                    return bsonValue;
                }
                bsonReader.skipValue();
            }
            bsonReader.readEndDocument();
        }
        return null;
    }

    @Override
    public String toJson() {
        return this.toJson(JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build());
    }

    @Override
    public String toJson(JsonWriterSettings settings) {
        StringWriter writer = new StringWriter();
        new RawBsonDocumentCodec().encode((BsonWriter)new JsonWriter(writer, settings), this, EncoderContext.builder().build());
        return writer.toString();
    }

    @Override
    public boolean equals(Object o) {
        return this.toBaseBsonDocument().equals(o);
    }

    @Override
    public int hashCode() {
        return this.toBaseBsonDocument().hashCode();
    }

    @Override
    public BsonDocument clone() {
        return new RawBsonDocument((byte[])this.bytes.clone(), this.offset, this.length);
    }

    private BsonBinaryReader createReader() {
        return new BsonBinaryReader(new ByteBufferBsonInput(this.getByteBuffer()));
    }

    private BsonDocument toBaseBsonDocument() {
        try (BsonBinaryReader bsonReader = this.createReader();){
            BsonDocument bsonDocument = new BsonDocumentCodec().decode(bsonReader, DecoderContext.builder().build());
            return bsonDocument;
        }
    }

    private Object writeReplace() {
        return new SerializationProxy(this.bytes, this.offset, this.length);
    }

    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required");
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
            return new RawBsonDocument(this.bytes);
        }
    }
}

