/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.machinima;

import com.hypixel.hytale.protocol.Model;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SetMachinimaActorModel
implements Packet {
    public static final int PACKET_ID = 261;
    public static final boolean IS_COMPRESSED = false;
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 1;
    public static final int VARIABLE_FIELD_COUNT = 3;
    public static final int VARIABLE_BLOCK_START = 13;
    public static final int MAX_SIZE = 0x64000000;
    @Nullable
    public Model model;
    @Nullable
    public String sceneName;
    @Nullable
    public String actorName;

    @Override
    public int getId() {
        return 261;
    }

    public SetMachinimaActorModel() {
    }

    public SetMachinimaActorModel(@Nullable Model model, @Nullable String sceneName, @Nullable String actorName) {
        this.model = model;
        this.sceneName = sceneName;
        this.actorName = actorName;
    }

    public SetMachinimaActorModel(@Nonnull SetMachinimaActorModel other) {
        this.model = other.model;
        this.sceneName = other.sceneName;
        this.actorName = other.actorName;
    }

    @Nonnull
    public static SetMachinimaActorModel deserialize(@Nonnull ByteBuf buf, int offset) {
        SetMachinimaActorModel obj = new SetMachinimaActorModel();
        byte nullBits = buf.getByte(offset);
        if ((nullBits & 1) != 0) {
            int varPos0 = offset + 13 + buf.getIntLE(offset + 1);
            obj.model = Model.deserialize(buf, varPos0);
        }
        if ((nullBits & 2) != 0) {
            int varPos1 = offset + 13 + buf.getIntLE(offset + 5);
            int sceneNameLen = VarInt.peek(buf, varPos1);
            if (sceneNameLen < 0) {
                throw ProtocolException.negativeLength("SceneName", sceneNameLen);
            }
            if (sceneNameLen > 4096000) {
                throw ProtocolException.stringTooLong("SceneName", sceneNameLen, 4096000);
            }
            obj.sceneName = PacketIO.readVarString(buf, varPos1, PacketIO.UTF8);
        }
        if ((nullBits & 4) != 0) {
            int varPos2 = offset + 13 + buf.getIntLE(offset + 9);
            int actorNameLen = VarInt.peek(buf, varPos2);
            if (actorNameLen < 0) {
                throw ProtocolException.negativeLength("ActorName", actorNameLen);
            }
            if (actorNameLen > 4096000) {
                throw ProtocolException.stringTooLong("ActorName", actorNameLen, 4096000);
            }
            obj.actorName = PacketIO.readVarString(buf, varPos2, PacketIO.UTF8);
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int sl;
        byte nullBits = buf.getByte(offset);
        int maxEnd = 13;
        if ((nullBits & 1) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 1);
            int pos0 = offset + 13 + fieldOffset0;
            if ((pos0 += Model.computeBytesConsumed(buf, pos0)) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 2) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 5);
            int pos1 = offset + 13 + fieldOffset1;
            sl = VarInt.peek(buf, pos1);
            if ((pos1 += VarInt.length(buf, pos1) + sl) - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        if ((nullBits & 4) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 9);
            int pos2 = offset + 13 + fieldOffset2;
            sl = VarInt.peek(buf, pos2);
            if ((pos2 += VarInt.length(buf, pos2) + sl) - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        return maxEnd;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        byte nullBits = 0;
        if (this.model != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.sceneName != null) {
            nullBits = (byte)(nullBits | 2);
        }
        if (this.actorName != null) {
            nullBits = (byte)(nullBits | 4);
        }
        buf.writeByte(nullBits);
        int modelOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int sceneNameOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int actorNameOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.model != null) {
            buf.setIntLE(modelOffsetSlot, buf.writerIndex() - varBlockStart);
            this.model.serialize(buf);
        } else {
            buf.setIntLE(modelOffsetSlot, -1);
        }
        if (this.sceneName != null) {
            buf.setIntLE(sceneNameOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.sceneName, 4096000);
        } else {
            buf.setIntLE(sceneNameOffsetSlot, -1);
        }
        if (this.actorName != null) {
            buf.setIntLE(actorNameOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.actorName, 4096000);
        } else {
            buf.setIntLE(actorNameOffsetSlot, -1);
        }
    }

    @Override
    public int computeSize() {
        int size = 13;
        if (this.model != null) {
            size += this.model.computeSize();
        }
        if (this.sceneName != null) {
            size += PacketIO.stringSize(this.sceneName);
        }
        if (this.actorName != null) {
            size += PacketIO.stringSize(this.actorName);
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int pos;
        if (buffer.readableBytes() - offset < 13) {
            return ValidationResult.error("Buffer too small: expected at least 13 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 1) != 0) {
            int modelOffset = buffer.getIntLE(offset + 1);
            if (modelOffset < 0) {
                return ValidationResult.error("Invalid offset for Model");
            }
            pos = offset + 13 + modelOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Model");
            }
            ValidationResult modelResult = Model.validateStructure(buffer, pos);
            if (!modelResult.isValid()) {
                return ValidationResult.error("Invalid Model: " + modelResult.error());
            }
            pos += Model.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits & 2) != 0) {
            int sceneNameOffset = buffer.getIntLE(offset + 5);
            if (sceneNameOffset < 0) {
                return ValidationResult.error("Invalid offset for SceneName");
            }
            pos = offset + 13 + sceneNameOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for SceneName");
            }
            int sceneNameLen = VarInt.peek(buffer, pos);
            if (sceneNameLen < 0) {
                return ValidationResult.error("Invalid string length for SceneName");
            }
            if (sceneNameLen > 4096000) {
                return ValidationResult.error("SceneName exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += sceneNameLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading SceneName");
            }
        }
        if ((nullBits & 4) != 0) {
            int actorNameOffset = buffer.getIntLE(offset + 9);
            if (actorNameOffset < 0) {
                return ValidationResult.error("Invalid offset for ActorName");
            }
            pos = offset + 13 + actorNameOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for ActorName");
            }
            int actorNameLen = VarInt.peek(buffer, pos);
            if (actorNameLen < 0) {
                return ValidationResult.error("Invalid string length for ActorName");
            }
            if (actorNameLen > 4096000) {
                return ValidationResult.error("ActorName exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += actorNameLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading ActorName");
            }
        }
        return ValidationResult.OK;
    }

    public SetMachinimaActorModel clone() {
        SetMachinimaActorModel copy = new SetMachinimaActorModel();
        copy.model = this.model != null ? this.model.clone() : null;
        copy.sceneName = this.sceneName;
        copy.actorName = this.actorName;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SetMachinimaActorModel)) {
            return false;
        }
        SetMachinimaActorModel other = (SetMachinimaActorModel)obj;
        return Objects.equals(this.model, other.model) && Objects.equals(this.sceneName, other.sceneName) && Objects.equals(this.actorName, other.actorName);
    }

    public int hashCode() {
        return Objects.hash(this.model, this.sceneName, this.actorName);
    }
}

