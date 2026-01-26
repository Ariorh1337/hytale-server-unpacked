/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import org.bson.BsonBinaryReader;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.bson.RawBsonArray;
import org.bson.RawBsonDocument;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.io.BsonInputMark;

final class RawBsonValueHelper {
    private static final CodecRegistry REGISTRY = CodecRegistries.fromProviders(new BsonValueCodecProvider());

    static BsonValue decode(byte[] bytes, BsonBinaryReader bsonReader) {
        if (bsonReader.getCurrentBsonType() == BsonType.DOCUMENT || bsonReader.getCurrentBsonType() == BsonType.ARRAY) {
            int position = bsonReader.getBsonInput().getPosition();
            BsonInputMark mark = bsonReader.getBsonInput().getMark(4);
            int size = bsonReader.getBsonInput().readInt32();
            mark.reset();
            bsonReader.skipValue();
            if (bsonReader.getCurrentBsonType() == BsonType.DOCUMENT) {
                return new RawBsonDocument(bytes, position, size);
            }
            return new RawBsonArray(bytes, position, size);
        }
        return (BsonValue)REGISTRY.get(BsonValueCodecProvider.getClassForBsonType(bsonReader.getCurrentBsonType())).decode(bsonReader, DecoderContext.builder().build());
    }

    private RawBsonValueHelper() {
    }
}

