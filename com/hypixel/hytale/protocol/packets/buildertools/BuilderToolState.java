/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.buildertools;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolArg;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolBrushData;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderToolState {
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 2;
    public static final int VARIABLE_FIELD_COUNT = 3;
    public static final int VARIABLE_BLOCK_START = 14;
    public static final int MAX_SIZE = 0x64000000;
    @Nullable
    public String id;
    public boolean isBrush;
    @Nullable
    public BuilderToolBrushData brushData;
    @Nullable
    public Map<String, BuilderToolArg> args;

    public BuilderToolState() {
    }

    public BuilderToolState(@Nullable String id, boolean isBrush, @Nullable BuilderToolBrushData brushData, @Nullable Map<String, BuilderToolArg> args) {
        this.id = id;
        this.isBrush = isBrush;
        this.brushData = brushData;
        this.args = args;
    }

    public BuilderToolState(@Nonnull BuilderToolState other) {
        this.id = other.id;
        this.isBrush = other.isBrush;
        this.brushData = other.brushData;
        this.args = other.args;
    }

    @Nonnull
    public static BuilderToolState deserialize(@Nonnull ByteBuf buf, int offset) {
        BuilderToolState obj = new BuilderToolState();
        byte nullBits = buf.getByte(offset);
        boolean bl = obj.isBrush = buf.getByte(offset + 1) != 0;
        if ((nullBits & 1) != 0) {
            int varPos0 = offset + 14 + buf.getIntLE(offset + 2);
            int idLen = VarInt.peek(buf, varPos0);
            if (idLen < 0) {
                throw ProtocolException.negativeLength("Id", idLen);
            }
            if (idLen > 4096000) {
                throw ProtocolException.stringTooLong("Id", idLen, 4096000);
            }
            obj.id = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
        }
        if ((nullBits & 2) != 0) {
            int varPos1 = offset + 14 + buf.getIntLE(offset + 6);
            obj.brushData = BuilderToolBrushData.deserialize(buf, varPos1);
        }
        if ((nullBits & 4) != 0) {
            int varPos2 = offset + 14 + buf.getIntLE(offset + 10);
            int argsCount = VarInt.peek(buf, varPos2);
            if (argsCount < 0) {
                throw ProtocolException.negativeLength("Args", argsCount);
            }
            if (argsCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Args", argsCount, 4096000);
            }
            int varIntLen = VarInt.length(buf, varPos2);
            obj.args = new HashMap<String, BuilderToolArg>(argsCount);
            int dictPos = varPos2 + varIntLen;
            for (int i = 0; i < argsCount; ++i) {
                int keyLen = VarInt.peek(buf, dictPos);
                if (keyLen < 0) {
                    throw ProtocolException.negativeLength("key", keyLen);
                }
                if (keyLen > 4096000) {
                    throw ProtocolException.stringTooLong("key", keyLen, 4096000);
                }
                int keyVarLen = VarInt.length(buf, dictPos);
                String key = PacketIO.readVarString(buf, dictPos);
                BuilderToolArg val = BuilderToolArg.deserialize(buf, dictPos += keyVarLen + keyLen);
                dictPos += BuilderToolArg.computeBytesConsumed(buf, dictPos);
                if (obj.args.put(key, val) == null) continue;
                throw ProtocolException.duplicateKey("args", key);
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        byte nullBits = buf.getByte(offset);
        int maxEnd = 14;
        if ((nullBits & 1) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 2);
            int pos0 = offset + 14 + fieldOffset0;
            int sl = VarInt.peek(buf, pos0);
            if ((pos0 += VarInt.length(buf, pos0) + sl) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 2) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 6);
            int pos1 = offset + 14 + fieldOffset1;
            if ((pos1 += BuilderToolBrushData.computeBytesConsumed(buf, pos1)) - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        if ((nullBits & 4) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 10);
            int pos2 = offset + 14 + fieldOffset2;
            int dictLen = VarInt.peek(buf, pos2);
            pos2 += VarInt.length(buf, pos2);
            for (int i = 0; i < dictLen; ++i) {
                int sl = VarInt.peek(buf, pos2);
                pos2 += VarInt.length(buf, pos2) + sl;
                pos2 += BuilderToolArg.computeBytesConsumed(buf, pos2);
            }
            if (pos2 - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        return maxEnd;
    }

    public void serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        byte nullBits = 0;
        if (this.id != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.brushData != null) {
            nullBits = (byte)(nullBits | 2);
        }
        if (this.args != null) {
            nullBits = (byte)(nullBits | 4);
        }
        buf.writeByte(nullBits);
        buf.writeByte(this.isBrush ? 1 : 0);
        int idOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int brushDataOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int argsOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.id != null) {
            buf.setIntLE(idOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.id, 4096000);
        } else {
            buf.setIntLE(idOffsetSlot, -1);
        }
        if (this.brushData != null) {
            buf.setIntLE(brushDataOffsetSlot, buf.writerIndex() - varBlockStart);
            this.brushData.serialize(buf);
        } else {
            buf.setIntLE(brushDataOffsetSlot, -1);
        }
        if (this.args != null) {
            buf.setIntLE(argsOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.args.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Args", this.args.size(), 4096000);
            }
            VarInt.write(buf, this.args.size());
            for (Map.Entry<String, BuilderToolArg> e : this.args.entrySet()) {
                PacketIO.writeVarString(buf, e.getKey(), 4096000);
                e.getValue().serialize(buf);
            }
        } else {
            buf.setIntLE(argsOffsetSlot, -1);
        }
    }

    public int computeSize() {
        int size = 14;
        if (this.id != null) {
            size += PacketIO.stringSize(this.id);
        }
        if (this.brushData != null) {
            size += this.brushData.computeSize();
        }
        if (this.args != null) {
            int argsSize = 0;
            for (Map.Entry<String, BuilderToolArg> kvp : this.args.entrySet()) {
                argsSize += PacketIO.stringSize(kvp.getKey()) + kvp.getValue().computeSize();
            }
            size += VarInt.size(this.args.size()) + argsSize;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int pos;
        if (buffer.readableBytes() - offset < 14) {
            return ValidationResult.error("Buffer too small: expected at least 14 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 1) != 0) {
            int idOffset = buffer.getIntLE(offset + 2);
            if (idOffset < 0) {
                return ValidationResult.error("Invalid offset for Id");
            }
            pos = offset + 14 + idOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Id");
            }
            int idLen = VarInt.peek(buffer, pos);
            if (idLen < 0) {
                return ValidationResult.error("Invalid string length for Id");
            }
            if (idLen > 4096000) {
                return ValidationResult.error("Id exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += idLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading Id");
            }
        }
        if ((nullBits & 2) != 0) {
            int brushDataOffset = buffer.getIntLE(offset + 6);
            if (brushDataOffset < 0) {
                return ValidationResult.error("Invalid offset for BrushData");
            }
            pos = offset + 14 + brushDataOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for BrushData");
            }
            ValidationResult brushDataResult = BuilderToolBrushData.validateStructure(buffer, pos);
            if (!brushDataResult.isValid()) {
                return ValidationResult.error("Invalid BrushData: " + brushDataResult.error());
            }
            pos += BuilderToolBrushData.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits & 4) != 0) {
            int argsOffset = buffer.getIntLE(offset + 10);
            if (argsOffset < 0) {
                return ValidationResult.error("Invalid offset for Args");
            }
            pos = offset + 14 + argsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Args");
            }
            int argsCount = VarInt.peek(buffer, pos);
            if (argsCount < 0) {
                return ValidationResult.error("Invalid dictionary count for Args");
            }
            if (argsCount > 4096000) {
                return ValidationResult.error("Args exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (int i = 0; i < argsCount; ++i) {
                int keyLen = VarInt.peek(buffer, pos);
                if (keyLen < 0) {
                    return ValidationResult.error("Invalid string length for key");
                }
                if (keyLen > 4096000) {
                    return ValidationResult.error("key exceeds max length 4096000");
                }
                pos += VarInt.length(buffer, pos);
                if ((pos += keyLen) > buffer.writerIndex()) {
                    return ValidationResult.error("Buffer overflow reading key");
                }
                pos += BuilderToolArg.computeBytesConsumed(buffer, pos);
            }
        }
        return ValidationResult.OK;
    }

    public BuilderToolState clone() {
        BuilderToolState copy = new BuilderToolState();
        copy.id = this.id;
        copy.isBrush = this.isBrush;
        BuilderToolBrushData builderToolBrushData = copy.brushData = this.brushData != null ? this.brushData.clone() : null;
        if (this.args != null) {
            HashMap<String, BuilderToolArg> m = new HashMap<String, BuilderToolArg>();
            for (Map.Entry<String, BuilderToolArg> e : this.args.entrySet()) {
                m.put(e.getKey(), e.getValue().clone());
            }
            copy.args = m;
        }
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BuilderToolState)) {
            return false;
        }
        BuilderToolState other = (BuilderToolState)obj;
        return Objects.equals(this.id, other.id) && this.isBrush == other.isBrush && Objects.equals(this.brushData, other.brushData) && Objects.equals(this.args, other.args);
    }

    public int hashCode() {
        return Objects.hash(this.id, this.isBrush, this.brushData, this.args);
    }
}

