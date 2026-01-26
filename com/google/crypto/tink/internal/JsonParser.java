/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.internal;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayDeque;
import javax.annotation.Nullable;

public final class JsonParser {
    private static final JsonElementTypeAdapter JSON_ELEMENT = new JsonElementTypeAdapter();

    public static boolean isValidString(String s) {
        int length = s.length();
        int i = 0;
        while (i != length) {
            char ch = s.charAt(i);
            ++i;
            if (!Character.isSurrogate(ch)) continue;
            if (Character.isLowSurrogate(ch) || i == length || !Character.isLowSurrogate(s.charAt(i))) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public static JsonElement parse(String json) throws IOException {
        try {
            JsonReader jsonReader = new JsonReader(new StringReader(json));
            jsonReader.setLenient(false);
            return JSON_ELEMENT.read(jsonReader);
        }
        catch (NumberFormatException e) {
            throw new IOException(e);
        }
    }

    public static long getParsedNumberAsLongOrThrow(Number number) {
        if (!(number instanceof LazilyParsedNumber)) {
            throw new IllegalArgumentException("does not contain a parsed number.");
        }
        return Long.parseLong(number.toString());
    }

    private JsonParser() {
    }

    private static final class JsonElementTypeAdapter
    extends TypeAdapter<JsonElement> {
        private static final int RECURSION_LIMIT = 100;

        private JsonElementTypeAdapter() {
        }

        @Nullable
        private JsonElement tryBeginNesting(JsonReader in, JsonToken peeked) throws IOException {
            switch (peeked) {
                case BEGIN_ARRAY: {
                    in.beginArray();
                    return new JsonArray();
                }
                case BEGIN_OBJECT: {
                    in.beginObject();
                    return new JsonObject();
                }
            }
            return null;
        }

        private JsonElement readTerminal(JsonReader in, JsonToken peeked) throws IOException {
            switch (peeked) {
                case STRING: {
                    String value = in.nextString();
                    if (!JsonParser.isValidString(value)) {
                        throw new IOException("illegal characters in string");
                    }
                    return new JsonPrimitive(value);
                }
                case NUMBER: {
                    String number = in.nextString();
                    return new JsonPrimitive(new LazilyParsedNumber(number));
                }
                case BOOLEAN: {
                    return new JsonPrimitive(in.nextBoolean());
                }
                case NULL: {
                    in.nextNull();
                    return JsonNull.INSTANCE;
                }
            }
            throw new IllegalStateException("Unexpected token: " + (Object)((Object)peeked));
        }

        @Override
        public JsonElement read(JsonReader in) throws IOException {
            JsonToken peeked = in.peek();
            JsonElement current = this.tryBeginNesting(in, peeked);
            if (current == null) {
                return this.readTerminal(in, peeked);
            }
            ArrayDeque<JsonElement> stack = new ArrayDeque<JsonElement>();
            while (true) {
                if (in.hasNext()) {
                    boolean isNesting;
                    String name = null;
                    if (current instanceof JsonObject && !JsonParser.isValidString(name = in.nextName())) {
                        throw new IOException("illegal characters in string");
                    }
                    peeked = in.peek();
                    JsonElement value = this.tryBeginNesting(in, peeked);
                    boolean bl = isNesting = value != null;
                    if (value == null) {
                        value = this.readTerminal(in, peeked);
                    }
                    if (current instanceof JsonArray) {
                        ((JsonArray)current).add(value);
                    } else {
                        if (((JsonObject)current).has(name)) {
                            throw new IOException("duplicate key: " + name);
                        }
                        ((JsonObject)current).add(name, value);
                    }
                    if (!isNesting) continue;
                    stack.addLast(current);
                    if (stack.size() > 100) {
                        throw new IOException("too many recursions");
                    }
                    current = value;
                    continue;
                }
                if (current instanceof JsonArray) {
                    in.endArray();
                } else {
                    in.endObject();
                }
                if (stack.isEmpty()) {
                    return current;
                }
                current = (JsonElement)stack.removeLast();
            }
        }

        @Override
        public void write(JsonWriter out, JsonElement value) {
            throw new UnsupportedOperationException("write is not supported");
        }
    }

    private static final class LazilyParsedNumber
    extends Number {
        private final String value;

        public LazilyParsedNumber(String value) {
            this.value = value;
        }

        @Override
        public int intValue() {
            try {
                return Integer.parseInt(this.value);
            }
            catch (NumberFormatException e) {
                try {
                    return (int)Long.parseLong(this.value);
                }
                catch (NumberFormatException nfe) {
                    return new BigDecimal(this.value).intValue();
                }
            }
        }

        @Override
        public long longValue() {
            try {
                return Long.parseLong(this.value);
            }
            catch (NumberFormatException e) {
                return new BigDecimal(this.value).longValue();
            }
        }

        @Override
        public float floatValue() {
            return Float.parseFloat(this.value);
        }

        @Override
        public double doubleValue() {
            return Double.parseDouble(this.value);
        }

        public String toString() {
            return this.value;
        }

        private Object writeReplace() throws NotSerializableException {
            throw new NotSerializableException("serialization is not supported");
        }

        private void readObject(ObjectInputStream in) throws NotSerializableException {
            throw new NotSerializableException("serialization is not supported");
        }

        public int hashCode() {
            return this.value.hashCode();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof LazilyParsedNumber) {
                LazilyParsedNumber other = (LazilyParsedNumber)obj;
                return this.value.equals(other.value);
            }
            return false;
        }
    }
}

