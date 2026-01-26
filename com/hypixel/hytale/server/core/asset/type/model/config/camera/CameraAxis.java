/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.model.config.camera;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.protocol.CameraNode;
import com.hypixel.hytale.protocol.Rangef;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class CameraAxis
implements NetworkSerializable<com.hypixel.hytale.protocol.CameraAxis> {
    public static final BuilderCodec<CameraAxis> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(CameraAxis.class, CameraAxis::new).append(new KeyedCodec<Rangef>("AngleRange", ProtocolCodecs.RANGEF), (cameraAxis, s) -> {
        cameraAxis.angleRange = s;
    }, cameraAxis -> cameraAxis.angleRange).add()).append(new KeyedCodec<T[]>("TargetNodes", new ArrayCodec<CameraNode>(new EnumCodec<CameraNode>(CameraNode.class), CameraNode[]::new)), (cameraAxis, s) -> {
        cameraAxis.targetNodes = s;
    }, cameraAxis -> cameraAxis.targetNodes).addValidator(Validators.nonNull()).add()).build();
    public static final CameraAxis STATIC_HEAD = new CameraAxis(new Rangef(0.0f, 0.0f), new CameraNode[]{CameraNode.Head});
    protected Rangef angleRange;
    protected CameraNode[] targetNodes;

    protected CameraAxis() {
    }

    public CameraAxis(Rangef angleRange, CameraNode[] targetNodes) {
        this.angleRange = angleRange;
        this.targetNodes = targetNodes;
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.CameraAxis toPacket() {
        com.hypixel.hytale.protocol.CameraAxis packet = new com.hypixel.hytale.protocol.CameraAxis();
        packet.angleRange = this.angleRange;
        packet.targetNodes = this.targetNodes;
        return packet;
    }

    public Rangef getAngleRange() {
        return this.angleRange;
    }

    public CameraNode[] getTargetNodes() {
        return this.targetNodes;
    }

    @Nonnull
    public String toString() {
        return "CameraAxis{angleRange=" + String.valueOf(this.angleRange) + ", targetNodes=" + Arrays.toString((Object[])this.targetNodes) + "}";
    }
}

