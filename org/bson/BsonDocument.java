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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bson.BSONException;
import org.bson.BsonArray;
import org.bson.BsonBinary;
import org.bson.BsonBinaryReader;
import org.bson.BsonBinaryWriter;
import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDecimal128;
import org.bson.BsonDocumentReader;
import org.bson.BsonDouble;
import org.bson.BsonElement;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonInvalidOperationException;
import org.bson.BsonJavaScriptWithScope;
import org.bson.BsonNumber;
import org.bson.BsonObjectId;
import org.bson.BsonReader;
import org.bson.BsonRegularExpression;
import org.bson.BsonString;
import org.bson.BsonTimestamp;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.ByteBuf;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.bson.io.BasicOutputBuffer;
import org.bson.json.JsonMode;
import org.bson.json.JsonReader;
import org.bson.json.JsonWriter;
import org.bson.json.JsonWriterSettings;

public class BsonDocument
extends BsonValue
implements Map<String, BsonValue>,
Cloneable,
Bson,
Serializable {
    private static final long serialVersionUID = 1L;
    private final Map<String, BsonValue> map = new LinkedHashMap<String, BsonValue>();

    public static BsonDocument parse(String json) {
        return new BsonDocumentCodec().decode(new JsonReader(json), DecoderContext.builder().build());
    }

    public BsonDocument(List<BsonElement> bsonElements) {
        for (BsonElement cur : bsonElements) {
            this.put(cur.getName(), cur.getValue());
        }
    }

    public BsonDocument(String key, BsonValue value) {
        this.put(key, value);
    }

    public BsonDocument() {
    }

    public <C> BsonDocument toBsonDocument(Class<C> documentClass, CodecRegistry codecRegistry) {
        return this;
    }

    @Override
    public BsonType getBsonType() {
        return BsonType.DOCUMENT;
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }

    @Override
    public BsonValue get(Object key) {
        return this.map.get(key);
    }

    public BsonDocument getDocument(Object key) {
        this.throwIfKeyAbsent(key);
        return this.get(key).asDocument();
    }

    public BsonArray getArray(Object key) {
        this.throwIfKeyAbsent(key);
        return this.get(key).asArray();
    }

    public BsonNumber getNumber(Object key) {
        this.throwIfKeyAbsent(key);
        return this.get(key).asNumber();
    }

    public BsonInt32 getInt32(Object key) {
        this.throwIfKeyAbsent(key);
        return this.get(key).asInt32();
    }

    public BsonInt64 getInt64(Object key) {
        this.throwIfKeyAbsent(key);
        return this.get(key).asInt64();
    }

    public BsonDecimal128 getDecimal128(Object key) {
        this.throwIfKeyAbsent(key);
        return this.get(key).asDecimal128();
    }

    public BsonDouble getDouble(Object key) {
        this.throwIfKeyAbsent(key);
        return this.get(key).asDouble();
    }

    public BsonBoolean getBoolean(Object key) {
        this.throwIfKeyAbsent(key);
        return this.get(key).asBoolean();
    }

    public BsonString getString(Object key) {
        this.throwIfKeyAbsent(key);
        return this.get(key).asString();
    }

    public BsonDateTime getDateTime(Object key) {
        this.throwIfKeyAbsent(key);
        return this.get(key).asDateTime();
    }

    public BsonTimestamp getTimestamp(Object key) {
        this.throwIfKeyAbsent(key);
        return this.get(key).asTimestamp();
    }

    public BsonObjectId getObjectId(Object key) {
        this.throwIfKeyAbsent(key);
        return this.get(key).asObjectId();
    }

    public BsonRegularExpression getRegularExpression(Object key) {
        this.throwIfKeyAbsent(key);
        return this.get(key).asRegularExpression();
    }

    public BsonBinary getBinary(Object key) {
        this.throwIfKeyAbsent(key);
        return this.get(key).asBinary();
    }

    public boolean isNull(Object key) {
        if (!this.containsKey(key)) {
            return false;
        }
        return this.get(key).isNull();
    }

    public boolean isDocument(Object key) {
        if (!this.containsKey(key)) {
            return false;
        }
        return this.get(key).isDocument();
    }

    public boolean isArray(Object key) {
        if (!this.containsKey(key)) {
            return false;
        }
        return this.get(key).isArray();
    }

    public boolean isNumber(Object key) {
        if (!this.containsKey(key)) {
            return false;
        }
        return this.get(key).isNumber();
    }

    public boolean isInt32(Object key) {
        if (!this.containsKey(key)) {
            return false;
        }
        return this.get(key).isInt32();
    }

    public boolean isInt64(Object key) {
        if (!this.containsKey(key)) {
            return false;
        }
        return this.get(key).isInt64();
    }

    public boolean isDecimal128(Object key) {
        if (!this.containsKey(key)) {
            return false;
        }
        return this.get(key).isDecimal128();
    }

    public boolean isDouble(Object key) {
        if (!this.containsKey(key)) {
            return false;
        }
        return this.get(key).isDouble();
    }

    public boolean isBoolean(Object key) {
        if (!this.containsKey(key)) {
            return false;
        }
        return this.get(key).isBoolean();
    }

    public boolean isString(Object key) {
        if (!this.containsKey(key)) {
            return false;
        }
        return this.get(key).isString();
    }

    public boolean isDateTime(Object key) {
        if (!this.containsKey(key)) {
            return false;
        }
        return this.get(key).isDateTime();
    }

    public boolean isTimestamp(Object key) {
        if (!this.containsKey(key)) {
            return false;
        }
        return this.get(key).isTimestamp();
    }

    public boolean isObjectId(Object key) {
        if (!this.containsKey(key)) {
            return false;
        }
        return this.get(key).isObjectId();
    }

    public boolean isBinary(Object key) {
        if (!this.containsKey(key)) {
            return false;
        }
        return this.get(key).isBinary();
    }

    public BsonValue get(Object key, BsonValue defaultValue) {
        BsonValue value = this.get(key);
        return value != null ? value : defaultValue;
    }

    public BsonDocument getDocument(Object key, BsonDocument defaultValue) {
        if (!this.containsKey(key)) {
            return defaultValue;
        }
        return this.get(key).asDocument();
    }

    public BsonArray getArray(Object key, BsonArray defaultValue) {
        if (!this.containsKey(key)) {
            return defaultValue;
        }
        return this.get(key).asArray();
    }

    public BsonNumber getNumber(Object key, BsonNumber defaultValue) {
        if (!this.containsKey(key)) {
            return defaultValue;
        }
        return this.get(key).asNumber();
    }

    public BsonInt32 getInt32(Object key, BsonInt32 defaultValue) {
        if (!this.containsKey(key)) {
            return defaultValue;
        }
        return this.get(key).asInt32();
    }

    public BsonInt64 getInt64(Object key, BsonInt64 defaultValue) {
        if (!this.containsKey(key)) {
            return defaultValue;
        }
        return this.get(key).asInt64();
    }

    public BsonDecimal128 getDecimal128(Object key, BsonDecimal128 defaultValue) {
        if (!this.containsKey(key)) {
            return defaultValue;
        }
        return this.get(key).asDecimal128();
    }

    public BsonDouble getDouble(Object key, BsonDouble defaultValue) {
        if (!this.containsKey(key)) {
            return defaultValue;
        }
        return this.get(key).asDouble();
    }

    public BsonBoolean getBoolean(Object key, BsonBoolean defaultValue) {
        if (!this.containsKey(key)) {
            return defaultValue;
        }
        return this.get(key).asBoolean();
    }

    public BsonString getString(Object key, BsonString defaultValue) {
        if (!this.containsKey(key)) {
            return defaultValue;
        }
        return this.get(key).asString();
    }

    public BsonDateTime getDateTime(Object key, BsonDateTime defaultValue) {
        if (!this.containsKey(key)) {
            return defaultValue;
        }
        return this.get(key).asDateTime();
    }

    public BsonTimestamp getTimestamp(Object key, BsonTimestamp defaultValue) {
        if (!this.containsKey(key)) {
            return defaultValue;
        }
        return this.get(key).asTimestamp();
    }

    public BsonObjectId getObjectId(Object key, BsonObjectId defaultValue) {
        if (!this.containsKey(key)) {
            return defaultValue;
        }
        return this.get(key).asObjectId();
    }

    public BsonBinary getBinary(Object key, BsonBinary defaultValue) {
        if (!this.containsKey(key)) {
            return defaultValue;
        }
        return this.get(key).asBinary();
    }

    public BsonRegularExpression getRegularExpression(Object key, BsonRegularExpression defaultValue) {
        if (!this.containsKey(key)) {
            return defaultValue;
        }
        return this.get(key).asRegularExpression();
    }

    @Override
    public BsonValue put(String key, BsonValue value) {
        if (value == null) {
            throw new IllegalArgumentException(String.format("The value for key %s can not be null", key));
        }
        if (key.contains("\u0000")) {
            throw new BSONException(String.format("BSON cstring '%s' is not valid because it contains a null character at index %d", key, key.indexOf(0)));
        }
        return this.map.put(key, value);
    }

    @Override
    public BsonValue remove(Object key) {
        return this.map.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends BsonValue> m) {
        for (Map.Entry<? extends String, ? extends BsonValue> cur : m.entrySet()) {
            this.put(cur.getKey(), cur.getValue());
        }
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public Set<String> keySet() {
        return this.map.keySet();
    }

    @Override
    public Collection<BsonValue> values() {
        return this.map.values();
    }

    @Override
    public Set<Map.Entry<String, BsonValue>> entrySet() {
        return this.map.entrySet();
    }

    public BsonDocument append(String key, BsonValue value) {
        this.put(key, value);
        return this;
    }

    public String getFirstKey() {
        return this.keySet().iterator().next();
    }

    public BsonReader asBsonReader() {
        return new BsonDocumentReader(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BsonDocument)) {
            return false;
        }
        BsonDocument that = (BsonDocument)o;
        return this.entrySet().equals(that.entrySet());
    }

    @Override
    public int hashCode() {
        return this.entrySet().hashCode();
    }

    public String toJson() {
        return this.toJson(JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build());
    }

    public String toJson(JsonWriterSettings settings) {
        StringWriter writer = new StringWriter();
        new BsonDocumentCodec().encode((BsonWriter)new JsonWriter(writer, settings), this, EncoderContext.builder().build());
        return writer.toString();
    }

    public String toString() {
        return this.toJson();
    }

    public BsonDocument clone() {
        BsonDocument to = new BsonDocument();
        block6: for (Map.Entry<String, BsonValue> cur : this.entrySet()) {
            switch (cur.getValue().getBsonType()) {
                case DOCUMENT: {
                    to.put(cur.getKey(), cur.getValue().asDocument().clone());
                    continue block6;
                }
                case ARRAY: {
                    to.put(cur.getKey(), cur.getValue().asArray().clone());
                    continue block6;
                }
                case BINARY: {
                    to.put(cur.getKey(), BsonBinary.clone(cur.getValue().asBinary()));
                    continue block6;
                }
                case JAVASCRIPT_WITH_SCOPE: {
                    to.put(cur.getKey(), BsonJavaScriptWithScope.clone(cur.getValue().asJavaScriptWithScope()));
                    continue block6;
                }
            }
            to.put(cur.getKey(), cur.getValue());
        }
        return to;
    }

    private void throwIfKeyAbsent(Object key) {
        if (!this.containsKey(key)) {
            throw new BsonInvalidOperationException("Document does not contain key " + key);
        }
    }

    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required");
    }

    private static class SerializationProxy
    implements Serializable {
        private static final long serialVersionUID = 1L;
        private final byte[] bytes;

        SerializationProxy(BsonDocument document) {
            BasicOutputBuffer buffer = new BasicOutputBuffer();
            new BsonDocumentCodec().encode((BsonWriter)new BsonBinaryWriter(buffer), document, EncoderContext.builder().build());
            this.bytes = new byte[buffer.size()];
            int curPos = 0;
            for (ByteBuf cur : buffer.getByteBuffers()) {
                System.arraycopy(cur.array(), cur.position(), this.bytes, curPos, cur.limit());
                curPos += cur.position();
            }
        }

        private Object readResolve() {
            return new BsonDocumentCodec().decode(new BsonBinaryReader(ByteBuffer.wrap(this.bytes).order(ByteOrder.LITTLE_ENDIAN)), DecoderContext.builder().build());
        }
    }
}

