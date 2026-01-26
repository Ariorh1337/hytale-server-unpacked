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

public class CombatTextUIComponentOpacityAnimationEvent
extends CombatTextUIComponentAnimationEvent {
    public static final BuilderCodec<CombatTextUIComponentOpacityAnimationEvent> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(CombatTextUIComponentOpacityAnimationEvent.class, CombatTextUIComponentOpacityAnimationEvent::new, CombatTextUIComponentAnimationEvent.ABSTRACT_CODEC).appendInherited(new KeyedCodec<Float>("StartOpacity", Codec.FLOAT), (event, f) -> {
        event.startOpacity = f.floatValue();
    }, event -> Float.valueOf(event.startOpacity), (parent, event) -> {
        event.startOpacity = parent.startOpacity;
    }).documentation("The opacity that should be applied to text instances before the animation event begins.").addValidator(Validators.nonNull()).addValidator(Validators.range(Float.valueOf(0.0f), Float.valueOf(1.0f))).add()).appendInherited(new KeyedCodec<Float>("EndOpacity", Codec.FLOAT), (event, f) -> {
        event.endOpacity = f.floatValue();
    }, event -> Float.valueOf(event.endOpacity), (parent, event) -> {
        event.endOpacity = parent.endOpacity;
    }).documentation("The opacity that should be applied to text instances by the end of the animation.").addValidator(Validators.nonNull()).addValidator(Validators.range(Float.valueOf(0.0f), Float.valueOf(1.0f))).add()).build();
    private float startOpacity;
    private float endOpacity;

    @Override
    @Nonnull
    public CombatTextEntityUIComponentAnimationEvent generatePacket() {
        CombatTextEntityUIComponentAnimationEvent packet = super.generatePacket();
        packet.type = CombatTextEntityUIAnimationEventType.Opacity;
        packet.startOpacity = this.startOpacity;
        packet.endOpacity = this.endOpacity;
        return packet;
    }

    @Override
    @Nonnull
    public String toString() {
        return "CombatTextUIComponentOpacityAnimationEvent{startOpacity=" + this.startOpacity + ", endOpacity=" + this.endOpacity + "} " + super.toString();
    }
}

