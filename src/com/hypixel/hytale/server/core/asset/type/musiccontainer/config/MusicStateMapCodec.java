package com.hypixel.hytale.server.core.asset.type.musiccontainer.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.exception.CodecException;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.NumberSchema;
import com.hypixel.hytale.codec.schema.config.ObjectSchema;
import com.hypixel.hytale.codec.schema.config.Schema;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonValue;

public class MusicStateMapCodec implements Codec<Map<String, Map<String, Float>>> {
   public static final MusicStateMapCodec INSTANCE = new MusicStateMapCodec();

   @Nullable
   public Map<String, Map<String, Float>> decode(@Nonnull BsonValue value, @Nullable ExtraInfo extraInfo) throws CodecException {
      if (!value.isDocument()) {
         return null;
      }

      BsonDocument doc = value.asDocument();
      LinkedHashMap<String, Map<String, Float>> result = new LinkedHashMap<>();

      for (Entry<String, BsonValue> entry : doc.entrySet()) {
         BsonValue entryValue = entry.getValue();
         if (!entryValue.isDocument()) {
            throw new CodecException("State '" + entry.getKey() + "' must be an object mapping layer name to volume");
         }

         LinkedHashMap<String, Float> volumeMap = new LinkedHashMap<>();

         for (Entry<String, BsonValue> volumeEntry : entryValue.asDocument().entrySet()) {
            BsonValue volumeValue = volumeEntry.getValue();
            if (!volumeValue.isNumber()) {
               throw new CodecException("State '" + entry.getKey() + "' layer '" + volumeEntry.getKey() + "' must be a number");
            }

            float v = (float)volumeValue.asNumber().doubleValue();
            if (!Float.isFinite(v)) {
               throw new CodecException("State '" + entry.getKey() + "' layer '" + volumeEntry.getKey() + "' must be a finite number");
            }

            volumeMap.put(volumeEntry.getKey(), v);
         }

         result.put(entry.getKey(), volumeMap);
      }

      return result;
   }

   @Nonnull
   public BsonValue encode(@Nullable Map<String, Map<String, Float>> value, @Nullable ExtraInfo extraInfo) {
      if (value == null) {
         return new BsonDocument();
      }

      BsonDocument doc = new BsonDocument();

      for (Entry<String, Map<String, Float>> entry : value.entrySet()) {
         BsonDocument inner = new BsonDocument();

         for (Entry<String, Float> volumeEntry : entry.getValue().entrySet()) {
            inner.put(volumeEntry.getKey(), new BsonDouble(volumeEntry.getValue().floatValue()));
         }

         doc.put(entry.getKey(), inner);
      }

      return doc;
   }

   @Nonnull
   @Override
   public Schema toSchema(@Nonnull SchemaContext context) {
      ObjectSchema volumeMap = new ObjectSchema();
      volumeMap.setTitle("LayerVolumes");
      volumeMap.setAdditionalProperties(new NumberSchema());
      ObjectSchema obj = new ObjectSchema();
      obj.setTitle("MusicStates");
      obj.setAdditionalProperties(volumeMap);
      return obj;
   }
}
