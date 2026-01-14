package com.nimbusds.jose.shaded.gson;

import com.google.errorprone.annotations.InlineMe;
import com.nimbusds.jose.shaded.gson.internal.Streams;
import com.nimbusds.jose.shaded.gson.stream.JsonReader;
import com.nimbusds.jose.shaded.gson.stream.JsonToken;
import com.nimbusds.jose.shaded.gson.stream.MalformedJsonException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public final class JsonParser {
   public static JsonElement parseString(String json) throws JsonSyntaxException {
      return parseReader(new StringReader(json));
   }

   public static JsonElement parseReader(Reader reader) throws JsonIOException, JsonSyntaxException {
      try {
         JsonReader jsonReader = new JsonReader(reader);
         JsonElement element = parseReader(jsonReader);
         if (!element.isJsonNull() && jsonReader.peek() != JsonToken.END_DOCUMENT) {
            throw new JsonSyntaxException("Did not consume the entire document.");
         } else {
            return element;
         }
      } catch (MalformedJsonException | NumberFormatException e) {
         throw new JsonSyntaxException(e);
      } catch (IOException e) {
         throw new JsonIOException(e);
      }
   }

   public static JsonElement parseReader(JsonReader reader) throws JsonIOException, JsonSyntaxException {
      Strictness strictness = reader.getStrictness();
      if (strictness == Strictness.LEGACY_STRICT) {
         reader.setStrictness(Strictness.LENIENT);
      }

      try {
         return Streams.parse(reader);
      } catch (StackOverflowError | OutOfMemoryError e) {
         throw new JsonParseException("Failed parsing JSON source: " + reader + " to Json", e);
      } finally {
         reader.setStrictness(strictness);
      }
   }

   @Deprecated
   @InlineMe(replacement = "JsonParser.parseString(json)", imports = "com.nimbusds.jose.shaded.gson.JsonParser")
   public JsonElement parse(String json) throws JsonSyntaxException {
      return parseString(json);
   }

   @Deprecated
   @InlineMe(replacement = "JsonParser.parseReader(json)", imports = "com.nimbusds.jose.shaded.gson.JsonParser")
   public JsonElement parse(Reader json) throws JsonIOException, JsonSyntaxException {
      return parseReader(json);
   }

   @Deprecated
   @InlineMe(replacement = "JsonParser.parseReader(json)", imports = "com.nimbusds.jose.shaded.gson.JsonParser")
   public JsonElement parse(JsonReader json) throws JsonIOException, JsonSyntaxException {
      return parseReader(json);
   }
}
