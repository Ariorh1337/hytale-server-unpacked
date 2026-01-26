/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.entityui.asset;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.protocol.CombatTextEntityUIAnimationEventType;
import com.hypixel.hytale.protocol.CombatTextEntityUIComponentAnimationEvent;
import com.hypixel.hytale.server.core.modules.entityui.asset.CombatTextUIComponentAnimationEvent;
import javax.annotation.Nonnull;

public class CombatTextUIComponentScaleAnimationEvent
extends CombatTextUIComponentAnimationEvent {
    public static final BuilderCodec<CombatTextUIComponentScaleAnimationEvent> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(CombatTextUIComponentScaleAnimationEvent.class, CombatTextUIComponentScaleAnimationEvent::new, CombatTextUIComponentAnimationEvent.ABSTRACT_CODEC).appendInherited(new KeyedCodec<Float>("StartScale", Codec.FLOAT), (event, f) -> {
        event.startScale = f.floatValue();
    }, event -> Float.valueOf(event.startScale), (parent, event) -> {
        event.startScale = parent.startScale;
    }).documentation("The scale that should be applied to text instances before the animation event begins.").addValidator(Validators.nonNull()).addValidator(Validators.range(Float.valueOf(0.0f), Float.valueOf(1.0f))).add()).appendInherited(new KeyedCodec<Float>("EndScale", Codec.FLOAT), (event, f) -> {
        event.endScale = f.floatValue();
    }, event -> Float.valueOf(event.endScale), (parent, event) -> {
        event.endScale = parent.endScale;
    }).documentation("The scale that should be applied to text instances by the end of the animation.").addValidator(Validators.nonNull()).addValidator(Validators.range(Float.valueOf(0.0f), Float.valueOf(1.0f))).add()).build();
    private float startScale;
    private float endScale;

    @Override
    @Nonnull
    public CombatTextEntityUIComponentAnimationEvent generatePacket() {
        CombatTextEntityUIComponentAnimationEvent packet = super.generatePacket();
        packet.type = CombatTextEntityUIAnimationEventType.Scale;
        packet.startScale = this.startScale;
        packet.endScale = this.endScale;
        return packet;
    }

    @Override
    @Nonnull
    public String toString() {
        return "CombatTextUIComponentConfig{startScale=" + this.startScale + ", endScale=" + this.endScale + "} " + super.toString();
    }
}

