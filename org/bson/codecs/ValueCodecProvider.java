/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import java.util.HashMap;
import java.util.Map;
import org.bson.codecs.AtomicBooleanCodec;
import org.bson.codecs.AtomicIntegerCodec;
import org.bson.codecs.AtomicLongCodec;
import org.bson.codecs.BigDecimalCodec;
import org.bson.codecs.BinaryCodec;
import org.bson.codecs.BooleanCodec;
import org.bson.codecs.ByteArrayCodec;
import org.bson.codecs.ByteCodec;
import org.bson.codecs.CharacterCodec;
import org.bson.codecs.CodeCodec;
import org.bson.codecs.Codec;
import org.bson.codecs.DateCodec;
import org.bson.codecs.Decimal128Codec;
import org.bson.codecs.DoubleCodec;
import org.bson.codecs.FloatCodec;
import org.bson.codecs.IntegerCodec;
import org.bson.codecs.LongCodec;
import org.bson.codecs.MaxKeyCodec;
import org.bson.codecs.MinKeyCodec;
import org.bson.codecs.ObjectIdCodec;
import org.bson.codecs.OverridableUuidRepresentationUuidCodec;
import org.bson.codecs.PatternCodec;
import org.bson.codecs.ShortCodec;
import org.bson.codecs.StringCodec;
import org.bson.codecs.SymbolCodec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class ValueCodecProvider
implements CodecProvider {
    private final Map<Class<?>, Codec<?>> codecs = new HashMap();

    public ValueCodecProvider() {
        this.addCodecs();
    }

    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        return this.codecs.get(clazz);
    }

    private void addCodecs() {
        this.addCodec(new BinaryCodec());
        this.addCodec(new BooleanCodec());
        this.addCodec(new DateCodec());
        this.addCodec(new DoubleCodec());
        this.addCodec(new IntegerCodec());
        this.addCodec(new LongCodec());
        this.addCodec(new MinKeyCodec());
        this.addCodec(new MaxKeyCodec());
        this.addCodec(new CodeCodec());
        this.addCodec(new Decimal128Codec());
        this.addCodec(new BigDecimalCodec());
        this.addCodec(new ObjectIdCodec());
        this.addCodec(new CharacterCodec());
        this.addCodec(new StringCodec());
        this.addCodec(new SymbolCodec());
        this.addCodec(new OverridableUuidRepresentationUuidCodec());
        this.addCodec(new ByteCodec());
        this.addCodec(new PatternCodec());
        this.addCodec(new ShortCodec());
        this.addCodec(new ByteArrayCodec());
        this.addCodec(new FloatCodec());
        this.addCodec(new AtomicBooleanCodec());
        this.addCodec(new AtomicIntegerCodec());
        this.addCodec(new AtomicLongCodec());
    }

    private <T> void addCodec(Codec<T> codec) {
        this.codecs.put(codec.getEncoderClass(), codec);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o != null && this.getClass() == o.getClass();
    }

    public int hashCode() {
        return 0;
    }
}

