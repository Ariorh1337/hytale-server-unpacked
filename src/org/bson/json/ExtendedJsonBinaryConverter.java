package org.bson.json;

import java.util.Base64;
import org.bson.BsonBinary;

class ExtendedJsonBinaryConverter implements Converter<BsonBinary> {
   public void convert(BsonBinary value, StrictJsonWriter writer) {
      writer.writeStartObject();
      writer.writeStartObject("$binary");
      writer.writeString("base64", Base64.getEncoder().encodeToString(value.getData()));
      writer.writeString("subType", String.format("%02X", value.getType()));
      writer.writeEndObject();
      writer.writeEndObject();
   }
}
