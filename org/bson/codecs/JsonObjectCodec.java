/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import java.io.StringWriter;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.json.JsonObject;
import org.bson.json.JsonReader;
import org.bson.json.JsonWriter;
import org.bson.json.JsonWriterSettings;

public class JsonObjectCodec
implements Codec<JsonObject> {
    private final JsonWriterSettings writerSettings;

    public JsonObjectCodec() {
        this(JsonWriterSettings.builder().build());
    }

    public JsonObjectCodec(JsonWriterSettings writerSettings) {
        this.writerSettings = writerSettings;
    }

    @Override
    public void encode(BsonWriter writer, JsonObject value, EncoderContext encoderContext) {
        writer.pipe(new JsonReader(value.getJson()));
    }

    @Override
    public JsonObject decode(BsonReader reader, DecoderContext decoderContext) {
        StringWriter stringWriter = new StringWriter();
        new JsonWriter(stringWriter, this.writerSettings).pipe(reader);
        return new JsonObject(stringWriter.toString());
    }

    @Override
    public Class<JsonObject> getEncoderClass() {
        return JsonObject.class;
    }
}

