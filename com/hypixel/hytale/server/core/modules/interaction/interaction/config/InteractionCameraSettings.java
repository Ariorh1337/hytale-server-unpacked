/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.LegacyValidator;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.protocol.Direction;
import com.hypixel.hytale.protocol.Vector3f;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class InteractionCameraSettings
implements NetworkSerializable<com.hypixel.hytale.protocol.InteractionCameraSettings> {
    public static final BuilderCodec<InteractionCameraSettings> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(InteractionCameraSettings.class, InteractionCameraSettings::new).appendInherited(new KeyedCodec<T[]>("FirstPerson", new ArrayCodec<InteractionCamera>(InteractionCamera.CODEC, InteractionCamera[]::new)), (o, i) -> {
        o.firstPerson = i;
    }, o -> o.firstPerson, (o, p) -> {
        o.firstPerson = p.firstPerson;
    }).addValidator(InteractionCameraSettings.getInteractionCameraValidator()).add()).appendInherited(new KeyedCodec<T[]>("ThirdPerson", new ArrayCodec<InteractionCamera>(InteractionCamera.CODEC, InteractionCamera[]::new)), (o, i) -> {
        o.thirdPerson = i;
    }, o -> o.thirdPerson, (o, p) -> {
        o.thirdPerson = p.thirdPerson;
    }).addValidator(InteractionCameraSettings.getInteractionCameraValidator()).add()).build();
    private InteractionCamera[] firstPerson;
    private InteractionCamera[] thirdPerson;

    @Nonnull
    private static LegacyValidator<InteractionCamera[]> getInteractionCameraValidator() {
        return (interactionCameras, results) -> {
            if (interactionCameras == null) {
                return;
            }
            float lastTime = -1.0f;
            for (InteractionCamera entry : interactionCameras) {
                if (entry.time <= lastTime) {
                    results.fail("Camera entry with time: " + entry.time + " conflicts with another entry");
                }
                lastTime = entry.time;
            }
        };
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.InteractionCameraSettings toPacket() {
        int i;
        com.hypixel.hytale.protocol.InteractionCameraSettings packet = new com.hypixel.hytale.protocol.InteractionCameraSettings();
        if (this.firstPerson != null) {
            packet.firstPerson = new com.hypixel.hytale.protocol.InteractionCamera[this.firstPerson.length];
            for (i = 0; i < this.firstPerson.length; ++i) {
                packet.firstPerson[i] = this.firstPerson[i].toPacket();
            }
        }
        if (this.thirdPerson != null) {
            packet.thirdPerson = new com.hypixel.hytale.protocol.InteractionCamera[this.thirdPerson.length];
            for (i = 0; i < this.thirdPerson.length; ++i) {
                packet.thirdPerson[i] = this.thirdPerson[i].toPacket();
            }
        }
        return packet;
    }

    @Nonnull
    public String toString() {
        return "InteractionCameraSettings{firstPerson=" + Arrays.toString(this.firstPerson) + ", thirdPerson=" + Arrays.toString(this.thirdPerson) + "}";
    }

    public static class InteractionCamera
    implements NetworkSerializable<com.hypixel.hytale.protocol.InteractionCamera> {
        public static final BuilderCodec<InteractionCamera> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(InteractionCamera.class, InteractionCamera::new).appendInherited(new KeyedCodec<Float>("Time", Codec.FLOAT), (o, i) -> {
            o.time = i.floatValue();
        }, o -> Float.valueOf(o.time), (o, p) -> {
            o.time = p.time;
        }).addValidator(Validators.greaterThan(Float.valueOf(0.0f))).add()).appendInherited(new KeyedCodec<Vector3f>("Position", ProtocolCodecs.VECTOR3F), (o, i) -> {
            o.position = i;
        }, o -> o.position, (o, p) -> {
            o.position = p.position;
        }).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<Direction>("Rotation", ProtocolCodecs.DIRECTION), (o, i) -> {
            o.rotation = i;
            o.rotation.yaw *= (float)Math.PI / 180;
            o.rotation.pitch *= (float)Math.PI / 180;
            o.rotation.roll *= (float)Math.PI / 180;
        }, o -> new Direction(o.rotation.yaw * 57.295776f, o.rotation.pitch * 57.295776f, o.rotation.roll * 57.295776f), (o, p) -> {
            o.rotation = p.rotation;
        }).addValidator(Validators.nonNull()).add()).build();
        private float time = 0.1f;
        private Vector3f position = new Vector3f(0.0f, 0.0f, 0.0f);
        private Direction rotation = new Direction(0.0f, 0.0f, 0.0f);

        @Override
        @Nonnull
        public com.hypixel.hytale.protocol.InteractionCamera toPacket() {
            com.hypixel.hytale.protocol.InteractionCamera packet = new com.hypixel.hytale.protocol.InteractionCamera();
            packet.time = this.time;
            packet.position = this.position;
            packet.rotation = this.rotation;
            return packet;
        }

        @Nonnull
        public String toString() {
            return "InteractionCamera{time=" + this.time + ", position=" + String.valueOf(this.position) + ", rotation=" + String.valueOf(this.rotation) + "}";
        }
    }
}

