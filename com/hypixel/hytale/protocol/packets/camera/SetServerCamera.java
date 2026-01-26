/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.camera;

import com.hypixel.hytale.protocol.ClientCameraView;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.ServerCameraSettings;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SetServerCamera
implements Packet {
    public static final int PACKET_ID = 280;
    public static final boolean IS_COMPRESSED = false;
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 157;
    public static final int VARIABLE_FIELD_COUNT = 0;
    public static final int VARIABLE_BLOCK_START = 157;
    public static final int MAX_SIZE = 157;
    @Nonnull
    public ClientCameraView clientCameraView = ClientCameraView.FirstPerson;
    public boolean isLocked;
    @Nullable
    public ServerCameraSettings cameraSettings;

    @Override
    public int getId() {
        return 280;
    }

    public SetServerCamera() {
    }

    public SetServerCamera(@Nonnull ClientCameraView clientCameraView, boolean isLocked, @Nullable ServerCameraSettings cameraSettings) {
        this.clientCameraView = clientCameraView;
        this.isLocked = isLocked;
        this.cameraSettings = cameraSettings;
    }

    public SetServerCamera(@Nonnull SetServerCamera other) {
        this.clientCameraView = other.clientCameraView;
        this.isLocked = other.isLocked;
        this.cameraSettings = other.cameraSettings;
    }

    @Nonnull
    public static SetServerCamera deserialize(@Nonnull ByteBuf buf, int offset) {
        SetServerCamera obj = new SetServerCamera();
        byte nullBits = buf.getByte(offset);
        obj.clientCameraView = ClientCameraView.fromValue(buf.getByte(offset + 1));
        boolean bl = obj.isLocked = buf.getByte(offset + 2) != 0;
        if ((nullBits & 1) != 0) {
            obj.cameraSettings = ServerCameraSettings.deserialize(buf, offset + 3);
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        return 157;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        int nullBits = 0;
        if (this.cameraSettings != null) {
            nullBits = (byte)(nullBits | 1);
        }
        buf.writeByte(nullBits);
        buf.writeByte(this.clientCameraView.getValue());
        buf.writeByte(this.isLocked ? 1 : 0);
        if (this.cameraSettings != null) {
            this.cameraSettings.serialize(buf);
        } else {
            buf.writeZero(154);
        }
    }

    @Override
    public int computeSize() {
        return 157;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        if (buffer.readableBytes() - offset < 157) {
            return ValidationResult.error("Buffer too small: expected at least 157 bytes");
        }
        return ValidationResult.OK;
    }

    public SetServerCamera clone() {
        SetServerCamera copy = new SetServerCamera();
        copy.clientCameraView = this.clientCameraView;
        copy.isLocked = this.isLocked;
        copy.cameraSettings = this.cameraSettings != null ? this.cameraSettings.clone() : null;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SetServerCamera)) {
            return false;
        }
        SetServerCamera other = (SetServerCamera)obj;
        return Objects.equals((Object)this.clientCameraView, (Object)other.clientCameraView) && this.isLocked == other.isLocked && Objects.equals(this.cameraSettings, other.cameraSettings);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.clientCameraView, this.isLocked, this.cameraSettings});
    }
}

