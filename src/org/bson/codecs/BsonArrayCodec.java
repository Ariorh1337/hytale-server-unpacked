package org.bson.codecs;

import org.bson.BsonArray;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.assertions.Assertions;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

public class BsonArrayCodec implements Codec<BsonArray> {
   private static final CodecRegistry DEFAULT_REGISTRY = CodecRegistries.fromProviders(new BsonValueCodecProvider());
   private static final BsonTypeCodecMap DEFAULT_BSON_TYPE_CODEC_MAP = new BsonTypeCodecMap(BsonValueCodecProvider.getBsonTypeClassMap(), DEFAULT_REGISTRY);
   private final BsonTypeCodecMap bsonTypeCodecMap;

   public BsonArrayCodec() {
      this(DEFAULT_BSON_TYPE_CODEC_MAP);
   }

   public BsonArrayCodec(CodecRegistry codecRegistry) {
      this(new BsonTypeCodecMap(BsonValueCodecProvider.getBsonTypeClassMap(), codecRegistry));
   }

   private BsonArrayCodec(BsonTypeCodecMap bsonTypeCodecMap) {
      this.bsonTypeCodecMap = Assertions.notNull("bsonTypeCodecMap", bsonTypeCodecMap);
   }

   public BsonArray decode(BsonReader reader, DecoderContext decoderContext) {
      BsonArray bsonArray = new BsonArray();
      reader.readStartArray();

      while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
         bsonArray.add(this.readValue(reader, decoderContext));
      }

      reader.readEndArray();
      return bsonArray;
   }

   public void encode(BsonWriter writer, BsonArray array, EncoderContext encoderContext) {
      writer.writeStartArray();

      for (BsonValue value : array) {
         Codec codec = this.bsonTypeCodecMap.get(value.getBsonType());
         encoderContext.encodeWithChildContext(codec, writer, value);
      }

      writer.writeEndArray();
   }

   @Override
   public Class<BsonArray> getEncoderClass() {
      return BsonArray.class;
   }

   protected BsonValue readValue(BsonReader reader, DecoderContext decoderContext) {
      BsonType currentBsonType = reader.getCurrentBsonType();
      return (BsonValue)this.bsonTypeCodecMap.get(currentBsonType).decode(reader, decoderContext);
   }
}
