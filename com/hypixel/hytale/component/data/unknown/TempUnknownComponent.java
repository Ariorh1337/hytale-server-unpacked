/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component.data.unknown;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.function.FunctionCodec;
import com.hypixel.hytale.component.Component;
import javax.annotation.Nonnull;
import org.bson.BsonDocument;

public class TempUnknownComponent<ECS_TYPE>
implements Component<ECS_TYPE> {
    @Nonnull
    public static final Codec<Component> COMPONENT_CODEC = new FunctionCodec<BsonDocument, Component>(Codec.BSON_DOCUMENT, TempUnknownComponent::new, component -> ((TempUnknownComponent)component).document);
    @Nonnull
    private final BsonDocument document;

    public TempUnknownComponent(@Nonnull BsonDocument document) {
        this.document = document;
    }

    @Nonnull
    public BsonDocument getDocument() {
        return this.document;
    }

    @Override
    @Nonnull
    public Component<ECS_TYPE> clone() {
        return new TempUnknownComponent<ECS_TYPE>(this.document.clone());
    }
}

