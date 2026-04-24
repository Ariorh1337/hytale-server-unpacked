package org.bson.json;

import java.util.Base64;
import org.bson.BsonBinary;

class ShellBinaryConverter implements Converter<BsonBinary> {
   public void convert(BsonBinary value, StrictJsonWriter writer) {
      writer.writeRaw(String.format("new BinData(%s, \"%s\")", value.getType() & 255, Base64.getEncoder().encodeToString(value.getData())));
   }
}
