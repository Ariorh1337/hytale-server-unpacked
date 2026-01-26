/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import org.bson.BsonDocument;
import org.bson.BsonDocumentWrapper;
import org.bson.assertions.Assertions;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

public class JsonObject
implements Bson {
    private final String json;

    public JsonObject(String json) {
        Assertions.notNull("Json", json);
        boolean foundBrace = false;
        for (int i = 0; i < json.length(); ++i) {
            char c = json.charAt(i);
            if (c == '{') {
                foundBrace = true;
                break;
            }
            Assertions.isTrueArgument("json is a valid JSON object", Character.isWhitespace(c));
        }
        Assertions.isTrueArgument("json is a valid JSON object", foundBrace);
        this.json = json;
    }

    public String getJson() {
        return this.json;
    }

    @Override
    public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry registry) {
        return new BsonDocumentWrapper<JsonObject>(this, registry.get(JsonObject.class));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        JsonObject that = (JsonObject)o;
        return this.json.equals(that.getJson());
    }

    public int hashCode() {
        return this.json.hashCode();
    }

    public String toString() {
        return this.json;
    }
}

