/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.codecs.map.EnumMapCodec;
import com.hypixel.hytale.codec.exception.CodecException;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.util.RawJsonReader;
import com.hypixel.hytale.protocol.PrioritySlot;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.InteractionPriority;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import javax.annotation.Nonnull;
import org.bson.BsonInt32;
import org.bson.BsonValue;

public class InteractionPriorityCodec
implements Codec<InteractionPriority> {
    private static final EnumMapCodec<PrioritySlot, Integer> MAP_CODEC = new EnumMapCodec<PrioritySlot, Integer>(PrioritySlot.class, Codec.INTEGER, () -> new EnumMap(PrioritySlot.class), false);

    @Override
    @Nonnull
    public InteractionPriority decode(@Nonnull BsonValue bsonValue, ExtraInfo extraInfo) {
        if (bsonValue.isInt32()) {
            return new InteractionPriority(bsonValue.asInt32().getValue());
        }
        if (bsonValue.isDocument()) {
            return new InteractionPriority((Map<PrioritySlot, Integer>)MAP_CODEC.decode(bsonValue, extraInfo));
        }
        throw new CodecException("Expected integer or object for InteractionPriority, got: " + String.valueOf((Object)bsonValue.getBsonType()));
    }

    @Override
    @Nonnull
    public BsonValue encode(@Nonnull InteractionPriority priority, ExtraInfo extraInfo) {
        Map<PrioritySlot, Integer> values = priority.values();
        if (values == null || values.isEmpty()) {
            return new BsonInt32(0);
        }
        if (values.size() == 1 && values.containsKey((Object)PrioritySlot.Default)) {
            return new BsonInt32(values.get((Object)PrioritySlot.Default));
        }
        return MAP_CODEC.encode(values, extraInfo);
    }

    @Override
    @Nonnull
    public InteractionPriority decodeJson(@Nonnull RawJsonReader reader, ExtraInfo extraInfo) throws IOException {
        reader.consumeWhiteSpace();
        int peek = reader.peek();
        if (peek == 123) {
            return new InteractionPriority((Map<PrioritySlot, Integer>)MAP_CODEC.decodeJson(reader, extraInfo));
        }
        if (peek == 45 || Character.isDigit(peek)) {
            return new InteractionPriority(reader.readIntValue());
        }
        throw new CodecException("Expected integer or object for InteractionPriority, got: " + (char)peek, reader, extraInfo, null);
    }

    @Override
    @Nonnull
    public Schema toSchema(@Nonnull SchemaContext context) {
        Schema schema = MAP_CODEC.toSchema(context);
        schema.setTitle("InteractionPriority");
        schema.setDescription("Either an integer (default for all types) or an object with named priorities (e.g., 'MainHand', 'OffHand', 'Default').");
        return schema;
    }
}

