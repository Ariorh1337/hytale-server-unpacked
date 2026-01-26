/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.entityui.asset;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.protocol.CombatTextEntityUIAnimationEventType;
import com.hypixel.hytale.protocol.CombatTextEntityUIComponentAnimationEvent;
import com.hypixel.hytale.protocol.Vector2f;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import com.hypixel.hytale.server.core.modules.entityui.asset.CombatTextUIComponentAnimationEvent;
import javax.annotation.Nonnull;

public class CombatTextUIComponentPositionAnimationEvent
extends CombatTextUIComponentAnimationEvent {
    public static final BuilderCodec<CombatTextUIComponentPositionAnimationEvent> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(CombatTextUIComponentPositionAnimationEvent.class, CombatTextUIComponentPositionAnimationEvent::new, CombatTextUIComponentAnimationEvent.ABSTRACT_CODEC).appendInherited(new KeyedCodec<Vector2f>("PositionOffset", ProtocolCodecs.VECTOR2F), (event, f) -> {
        event.positionOffset = f;
    }, event -> event.positionOffset, (parent, event) -> {
        event.positionOffset = parent.positionOffset;
    }).documentation("The offset from the starting position that the text instance should animate to.").addValidator(Validators.nonNull()).add()).build();
    private Vector2f positionOffset;

    @Override
    @Nonnull
    public CombatTextEntityUIComponentAnimationEvent generatePacket() {
        CombatTextEntityUIComponentAnimationEvent packet = super.generatePacket();
        packet.type = CombatTextEntityUIAnimationEventType.Position;
        packet.positionOffset = this.positionOffset;
        return packet;
    }

    @Override
    @Nonnull
    public String toString() {
        return "CombatTextUIComponentPositionAnimationEvent{positionOffset=" + String.valueOf(this.positionOffset) + "} " + super.toString();
    }
}

