/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.none;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.protocol.CameraActionType;
import com.hypixel.hytale.protocol.CameraPerspectiveType;
import com.hypixel.hytale.protocol.Interaction;
import com.hypixel.hytale.protocol.InteractionSyncData;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
import javax.annotation.Nonnull;

public class CameraInteraction
extends SimpleInteraction {
    @Nonnull
    public static final BuilderCodec<CameraInteraction> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(CameraInteraction.class, CameraInteraction::new, SimpleInteraction.CODEC).documentation("Adjusts the camera of the user.")).appendInherited(new KeyedCodec<Boolean>("PersistCameraState", Codec.BOOLEAN), (i, s) -> {
        i.persistCameraState = s;
    }, i -> i.persistCameraState, (i, parent) -> {
        i.persistCameraState = parent.persistCameraState;
    }).documentation("Should the camera state from this interaction persist to the next camera interaction. If the next interaction is null or not a camera interaction then this field does nothing.").add()).appendInherited(new KeyedCodec<CameraActionType>("Action", new EnumCodec<CameraActionType>(CameraActionType.class)), (i, s) -> {
        i.action = s;
    }, i -> i.action, (i, parent) -> {
        i.action = parent.action;
    }).documentation("What kind of camera action should we take").add()).appendInherited(new KeyedCodec<CameraPerspectiveType>("Perspective", new EnumCodec<CameraPerspectiveType>(CameraPerspectiveType.class)), (i, s) -> {
        i.perspective = s;
    }, i -> i.perspective, (i, parent) -> {
        i.perspective = parent.perspective;
    }).documentation("What camera perspective we want this interaction to take place in").add()).appendInherited(new KeyedCodec<Float>("CameraInteractionTime", Codec.FLOAT), (i, s) -> {
        i.cameraInteractionTime = s.floatValue();
    }, i -> Float.valueOf(i.cameraInteractionTime), (i, parent) -> {
        i.cameraInteractionTime = parent.cameraInteractionTime;
    }).documentation("How long this camera action lasts for").add()).build();
    protected CameraActionType action;
    protected CameraPerspectiveType perspective;
    protected boolean persistCameraState;
    protected float cameraInteractionTime;

    @Override
    @Nonnull
    protected Interaction generatePacket() {
        return new com.hypixel.hytale.protocol.CameraInteraction();
    }

    @Override
    protected void configurePacket(Interaction packet) {
        super.configurePacket(packet);
        com.hypixel.hytale.protocol.CameraInteraction p = (com.hypixel.hytale.protocol.CameraInteraction)packet;
        p.cameraAction = this.action;
        p.cameraPerspective = this.perspective;
        p.cameraPersist = this.persistCameraState;
        p.cameraInteractionTime = this.cameraInteractionTime;
    }

    @Override
    @Nonnull
    public String toString() {
        return "CameraInteraction{action=" + String.valueOf((Object)this.action) + ", perspective='" + String.valueOf((Object)this.perspective) + "', persistCameraState='" + this.persistCameraState + "', cameraInteractionTime='" + this.cameraInteractionTime + "'} " + super.toString();
    }

    @Override
    protected void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        super.tick0(firstRun, time, type, context, cooldownHandler);
        InteractionSyncData clientState = context.getClientState();
        assert (clientState != null);
        context.getState().state = clientState.state;
    }

    @Override
    @Nonnull
    public WaitForDataFrom getWaitForDataFrom() {
        return WaitForDataFrom.Client;
    }

    @Override
    public boolean needsRemoteSync() {
        return true;
    }
}

