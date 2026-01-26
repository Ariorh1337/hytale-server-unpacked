/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.CollectionPropertyCodecProvider;
import org.bson.codecs.pojo.EnumPropertyCodecProvider;
import org.bson.codecs.pojo.FallbackPropertyCodecProvider;
import org.bson.codecs.pojo.MapPropertyCodecProvider;
import org.bson.codecs.pojo.PojoCodec;
import org.bson.codecs.pojo.PropertyCodecProvider;
import org.bson.codecs.pojo.PropertyCodecRegistry;
import org.bson.codecs.pojo.TypeWithTypeParameters;

class PropertyCodecRegistryImpl
implements PropertyCodecRegistry {
    private final List<PropertyCodecProvider> propertyCodecProviders;
    private final ConcurrentHashMap<TypeWithTypeParameters<?>, Codec<?>> propertyCodecCache;

    PropertyCodecRegistryImpl(PojoCodec<?> pojoCodec, CodecRegistry codecRegistry, List<PropertyCodecProvider> propertyCodecProviders) {
        ArrayList<PropertyCodecProvider> augmentedProviders = new ArrayList<PropertyCodecProvider>();
        if (propertyCodecProviders != null) {
            augmentedProviders.addAll(propertyCodecProviders);
        }
        augmentedProviders.add(new CollectionPropertyCodecProvider());
        augmentedProviders.add(new MapPropertyCodecProvider());
        augmentedProviders.add(new EnumPropertyCodecProvider(codecRegistry));
        augmentedProviders.add(new FallbackPropertyCodecProvider(pojoCodec, codecRegistry));
        this.propertyCodecProviders = augmentedProviders;
        this.propertyCodecCache = new ConcurrentHashMap();
    }

    public <S> Codec<S> get(TypeWithTypeParameters<S> typeWithTypeParameters) {
        if (this.propertyCodecCache.containsKey(typeWithTypeParameters)) {
            return this.propertyCodecCache.get(typeWithTypeParameters);
        }
        for (PropertyCodecProvider propertyCodecProvider : this.propertyCodecProviders) {
            Codec<S> codec = propertyCodecProvider.get(typeWithTypeParameters, this);
            if (codec == null) continue;
            this.propertyCodecCache.put(typeWithTypeParameters, codec);
            return codec;
        }
        return null;
    }
}

