/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson.internal.bind;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Map;

class JsonElementTypeAdapter
extends TypeAdapter<JsonElement> {
    static final JsonElementTypeAdapter ADAPTER = new JsonElementTypeAdapter();

    private JsonElementTypeAdapter() {
    }

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
                return new JsonPrimitive(in.nextString());
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
        if (in instanceof JsonTreeReader) {
            return ((JsonTreeReader)in).nextJsonElement();
        }
        JsonToken peeked = in.peek();
        JsonElement current = this.tryBeginNesting(in, peeked);
        if (current == null) {
            return this.readTerminal(in, peeked);
        }
        ArrayDeque<JsonElement> stack = new ArrayDeque<JsonElement>();
        while (true) {
            if (in.hasNext()) {
                JsonElement value;
                boolean isNesting;
                String name = null;
                if (current instanceof JsonObject) {
                    name = in.nextName();
                }
                boolean bl = isNesting = (value = this.tryBeginNesting(in, peeked = in.peek())) != null;
                if (value == null) {
                    value = this.readTerminal(in, peeked);
                }
                if (current instanceof JsonArray) {
                    ((JsonArray)current).add(value);
                } else {
                    ((JsonObject)current).add(name, value);
                }
                if (!isNesting) continue;
                stack.addLast(current);
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
    public void write(JsonWriter out, JsonElement value) throws IOException {
        if (value == null || value.isJsonNull()) {
            out.nullValue();
        } else if (value.isJsonPrimitive()) {
            JsonPrimitive primitive = value.getAsJsonPrimitive();
            if (primitive.isNumber()) {
                out.value(primitive.getAsNumber());
            } else if (primitive.isBoolean()) {
                out.value(primitive.getAsBoolean());
            } else {
                out.value(primitive.getAsString());
            }
        } else if (value.isJsonArray()) {
            out.beginArray();
            for (JsonElement e : value.getAsJsonArray()) {
                this.write(out, e);
            }
            out.endArray();
        } else if (value.isJsonObject()) {
            out.beginObject();
            for (Map.Entry<String, JsonElement> e : value.getAsJsonObject().entrySet()) {
                out.name(e.getKey());
                this.write(out, e.getValue());
            }
            out.endObject();
        } else {
            throw new IllegalArgumentException("Couldn't write " + value.getClass());
        }
    }
}

