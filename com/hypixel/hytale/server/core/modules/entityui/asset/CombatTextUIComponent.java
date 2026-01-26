/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.entityui.asset;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.protocol.CombatTextEntityUIComponentAnimationEvent;
import com.hypixel.hytale.protocol.EntityUIType;
import com.hypixel.hytale.protocol.RangeVector2f;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import com.hypixel.hytale.server.core.modules.entityui.asset.CombatTextUIComponentAnimationEvent;
import com.hypixel.hytale.server.core.modules.entityui.asset.EntityUIComponent;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class CombatTextUIComponent
extends EntityUIComponent {
    private static final float DEFAULT_FONT_SIZE = 68.0f;
    private static final Color DEFAULT_TEXT_COLOR = new Color(-1, -1, -1);
    public static final BuilderCodec<CombatTextUIComponent> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(CombatTextUIComponent.class, CombatTextUIComponent::new, EntityUIComponent.ABSTRACT_CODEC).appendInherited(new KeyedCodec<RangeVector2f>("RandomPositionOffsetRange", ProtocolCodecs.RANGE_VECTOR2F), (component, v) -> {
        component.randomPositionOffsetRange = v;
    }, component -> component.randomPositionOffsetRange, (component, parent) -> {
        component.randomPositionOffsetRange = parent.randomPositionOffsetRange;
    }).addValidator(Validators.nonNull()).documentation("The maximum range for randomly offsetting text instances from their starting position. Values are treated as absolute and apply in both directions of the axis.").add()).appendInherited(new KeyedCodec<Float>("ViewportMargin", Codec.FLOAT), (component, f) -> {
        component.viewportMargin = f.floatValue();
    }, component -> Float.valueOf(component.viewportMargin), (component, parent) -> {
        component.viewportMargin = parent.viewportMargin;
    }).addValidator(Validators.range(Float.valueOf(0.0f), Float.valueOf(200.0f))).documentation("The minimum distance (in px) from the edges of the viewport that text instances should clamp to.").add()).appendInherited(new KeyedCodec<Float>("Duration", Codec.FLOAT), (component, f) -> {
        component.duration = f.floatValue();
    }, component -> Float.valueOf(component.duration), (component, parent) -> {
        component.duration = parent.duration;
    }).addValidator(Validators.nonNull()).addValidator(Validators.range(Float.valueOf(0.1f), Float.valueOf(10.0f))).documentation("The duration for which text instances in this component should be visible.").add()).appendInherited(new KeyedCodec<Float>("HitAngleModifierStrength", Codec.FLOAT), (component, f) -> {
        component.hitAngleModifierStrength = f.floatValue();
    }, component -> Float.valueOf(component.hitAngleModifierStrength), (component, parent) -> {
        component.hitAngleModifierStrength = parent.hitAngleModifierStrength;
    }).addValidator(Validators.range(Float.valueOf(0.0f), Float.valueOf(10.0f))).documentation("Strength of the modifier to apply to the X axis of a position animation (if set) based on the angle of a melee attack.").add()).appendInherited(new KeyedCodec<Float>("FontSize", Codec.FLOAT), (component, f) -> {
        component.fontSize = f.floatValue();
    }, component -> Float.valueOf(component.fontSize), (component, parent) -> {
        component.fontSize = parent.fontSize;
    }).documentation("The font size to apply to text instances.").add()).appendInherited(new KeyedCodec<Color>("TextColor", ProtocolCodecs.COLOR), (component, c) -> {
        component.textColor = c;
    }, component -> component.textColor, (component, parent) -> {
        component.textColor = parent.textColor;
    }).documentation("The text color to apply to text instances.").add()).appendInherited(new KeyedCodec<T[]>("AnimationEvents", new ArrayCodec(CombatTextUIComponentAnimationEvent.CODEC, CombatTextUIComponentAnimationEvent[]::new)), (component, o) -> {
        component.animationEvents = o;
    }, component -> component.animationEvents, (component, parent) -> {
        component.animationEvents = parent.animationEvents;
    }).addValidator(Validators.nonNull()).documentation("Animation events for controlling animation of scale, position, and opacity.").add()).build();
    private RangeVector2f randomPositionOffsetRange;
    private float viewportMargin;
    private float duration;
    private float hitAngleModifierStrength = 1.0f;
    private float fontSize = 68.0f;
    private Color textColor = DEFAULT_TEXT_COLOR;
    private CombatTextUIComponentAnimationEvent[] animationEvents;

    @Override
    @Nonnull
    protected com.hypixel.hytale.protocol.EntityUIComponent generatePacket() {
        com.hypixel.hytale.protocol.EntityUIComponent packet = super.generatePacket();
        packet.type = EntityUIType.CombatText;
        packet.combatTextRandomPositionOffsetRange = this.randomPositionOffsetRange;
        packet.combatTextViewportMargin = this.viewportMargin;
        packet.combatTextDuration = this.duration;
        packet.combatTextHitAngleModifierStrength = this.hitAngleModifierStrength;
        packet.combatTextFontSize = this.fontSize;
        packet.combatTextColor = this.textColor;
        packet.combatTextAnimationEvents = new CombatTextEntityUIComponentAnimationEvent[this.animationEvents.length];
        for (int i = 0; i < this.animationEvents.length; ++i) {
            packet.combatTextAnimationEvents[i] = this.animationEvents[i].generatePacket();
        }
        return packet;
    }

    @Override
    @Nonnull
    public String toString() {
        return "CombatTextUIComponent{randomPositionOffsetRange=" + String.valueOf(this.randomPositionOffsetRange) + ", viewportMargin" + this.viewportMargin + ", duration=" + this.duration + ", hitAngleModifierStrength=" + this.hitAngleModifierStrength + ", fontSize=" + this.fontSize + ", textColor=" + String.valueOf(this.textColor) + ", animationEvents=" + Arrays.toString(this.animationEvents) + "} " + super.toString();
    }
}

