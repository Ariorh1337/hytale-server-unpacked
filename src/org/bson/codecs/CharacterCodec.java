package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.assertions.Assertions;
import org.bson.internal.StringCodecHelper;

public class CharacterCodec implements Codec<Character> {
   public void encode(BsonWriter writer, Character value, EncoderContext encoderContext) {
      Assertions.notNull("value", value);
      writer.writeString(value.toString());
   }

   public Character decode(BsonReader reader, DecoderContext decoderContext) {
      return StringCodecHelper.decodeChar(reader);
   }

   @Override
   public Class<Character> getEncoderClass() {
      return Character.class;
   }
}
