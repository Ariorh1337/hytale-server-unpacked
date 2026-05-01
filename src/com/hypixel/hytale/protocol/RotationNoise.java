package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.lang.foreign.MemorySegment;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RotationNoise {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 1;
   public static final int VARIABLE_FIELD_COUNT = 3;
   public static final int VARIABLE_BLOCK_START = 13;
   public static final int MAX_SIZE = 282624028;
   @Nullable
   public NoiseConfig[] pitch;
   @Nullable
   public NoiseConfig[] yaw;
   @Nullable
   public NoiseConfig[] roll;

   public RotationNoise() {
   }

   public RotationNoise(@Nullable NoiseConfig[] pitch, @Nullable NoiseConfig[] yaw, @Nullable NoiseConfig[] roll) {
      this.pitch = pitch;
      this.yaw = yaw;
      this.roll = roll;
   }

   public RotationNoise(@Nonnull RotationNoise other) {
      this.pitch = other.pitch;
      this.yaw = other.yaw;
      this.roll = other.roll;
   }

   @Nonnull
   public static RotationNoise deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 13) {
         throw ProtocolException.bufferTooSmall("RotationNoise", 13, buf.readableBytes() - offset);
      }

      RotationNoise obj = new RotationNoise();
      byte nullBits = buf.getByte(offset);
      if ((nullBits & 1) != 0) {
         int varPosBase0 = buf.getIntLE(offset + 1);
         if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 13) {
            throw ProtocolException.invalidOffset("Pitch", varPosBase0, buf.readableBytes());
         }

         int varPos0 = offset + 13 + varPosBase0;
         int pitchCount = VarInt.peek(buf, varPos0);
         if (pitchCount < 0) {
            throw ProtocolException.invalidVarInt("Pitch");
         }

         int varIntLen = VarInt.size(pitchCount);
         if (pitchCount > 4096000) {
            throw ProtocolException.arrayTooLong("Pitch", pitchCount, 4096000);
         }

         if (varPos0 + varIntLen + pitchCount * 23L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Pitch", varPos0 + varIntLen + pitchCount * 23, buf.readableBytes());
         }

         obj.pitch = new NoiseConfig[pitchCount];
         int elemPos = varPos0 + varIntLen;

         for (int i = 0; i < pitchCount; i++) {
            obj.pitch[i] = NoiseConfig.deserialize(buf, elemPos);
            elemPos += NoiseConfig.computeBytesConsumed(buf, elemPos);
         }
      }

      if ((nullBits & 2) != 0) {
         int varPosBase1 = buf.getIntLE(offset + 5);
         if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 13) {
            throw ProtocolException.invalidOffset("Yaw", varPosBase1, buf.readableBytes());
         }

         int varPos1 = offset + 13 + varPosBase1;
         int yawCount = VarInt.peek(buf, varPos1);
         if (yawCount < 0) {
            throw ProtocolException.invalidVarInt("Yaw");
         }

         int varIntLen = VarInt.size(yawCount);
         if (yawCount > 4096000) {
            throw ProtocolException.arrayTooLong("Yaw", yawCount, 4096000);
         }

         if (varPos1 + varIntLen + yawCount * 23L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Yaw", varPos1 + varIntLen + yawCount * 23, buf.readableBytes());
         }

         obj.yaw = new NoiseConfig[yawCount];
         int elemPos = varPos1 + varIntLen;

         for (int i = 0; i < yawCount; i++) {
            obj.yaw[i] = NoiseConfig.deserialize(buf, elemPos);
            elemPos += NoiseConfig.computeBytesConsumed(buf, elemPos);
         }
      }

      if ((nullBits & 4) != 0) {
         int varPosBase2 = buf.getIntLE(offset + 9);
         if (varPosBase2 < 0 || varPosBase2 > buf.writerIndex() - offset - 13) {
            throw ProtocolException.invalidOffset("Roll", varPosBase2, buf.readableBytes());
         }

         int varPos2 = offset + 13 + varPosBase2;
         int rollCount = VarInt.peek(buf, varPos2);
         if (rollCount < 0) {
            throw ProtocolException.invalidVarInt("Roll");
         }

         int varIntLen = VarInt.size(rollCount);
         if (rollCount > 4096000) {
            throw ProtocolException.arrayTooLong("Roll", rollCount, 4096000);
         }

         if (varPos2 + varIntLen + rollCount * 23L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Roll", varPos2 + varIntLen + rollCount * 23, buf.readableBytes());
         }

         obj.roll = new NoiseConfig[rollCount];
         int elemPos = varPos2 + varIntLen;

         for (int i = 0; i < rollCount; i++) {
            obj.roll[i] = NoiseConfig.deserialize(buf, elemPos);
            elemPos += NoiseConfig.computeBytesConsumed(buf, elemPos);
         }
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 13;
      if ((nullBits & 1) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 1);
         if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 13) {
            throw ProtocolException.invalidOffset("Pitch", fieldOffset0, maxEnd);
         }

         int pos0 = offset + 13 + fieldOffset0;
         int arrLen = VarInt.peek(buf, pos0);
         pos0 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            pos0 += NoiseConfig.computeBytesConsumed(buf, pos0);
         }

         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits & 2) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 5);
         if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 13) {
            throw ProtocolException.invalidOffset("Yaw", fieldOffset1, maxEnd);
         }

         int pos1 = offset + 13 + fieldOffset1;
         int arrLen = VarInt.peek(buf, pos1);
         pos1 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            pos1 += NoiseConfig.computeBytesConsumed(buf, pos1);
         }

         if (pos1 - offset > maxEnd) {
            maxEnd = pos1 - offset;
         }
      }

      if ((nullBits & 4) != 0) {
         int fieldOffset2 = buf.getIntLE(offset + 9);
         if (fieldOffset2 < 0 || fieldOffset2 > buf.writerIndex() - offset - 13) {
            throw ProtocolException.invalidOffset("Roll", fieldOffset2, maxEnd);
         }

         int pos2 = offset + 13 + fieldOffset2;
         int arrLen = VarInt.peek(buf, pos2);
         pos2 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            pos2 += NoiseConfig.computeBytesConsumed(buf, pos2);
         }

         if (pos2 - offset > maxEnd) {
            maxEnd = pos2 - offset;
         }
      }

      return maxEnd;
   }

   public static boolean isBufferTooSmall(MemorySegment mem) {
      return mem.byteSize() < 13L;
   }

   @Nullable
   public static NoiseConfig[] getPitch(MemorySegment mem) {
      return getPitch(mem, 0);
   }

   @Nullable
   public static NoiseConfig[] getPitch(MemorySegment mem, int offset) {
      if (!hasPitch(mem, offset)) {
         return null;
      }

      int off = offset + getValidatedOffset(mem, offset, 1, 13, "Pitch");
      long packed = VarInt.getWithLength(mem, off);
      int len = (int)packed;
      if (len < 0) {
         throw ProtocolException.negativeLength("Pitch", len);
      }

      if (len > 4096000) {
         throw ProtocolException.arrayTooLong("Pitch", len, 4096000);
      }

      int lenOffset = (int)(packed >>> 32);
      if (off + lenOffset + len * 23L > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("Pitch", off + lenOffset + len * 23, (int)mem.byteSize());
      }

      off += lenOffset;
      NoiseConfig[] data = new NoiseConfig[len];

      for (int i = 0; i < len; i++) {
         data[i] = NoiseConfig.toObject(mem, off + i * 23);
      }

      return data;
   }

   @Nullable
   public static NoiseConfig[] getYaw(MemorySegment mem) {
      return getYaw(mem, 0);
   }

   @Nullable
   public static NoiseConfig[] getYaw(MemorySegment mem, int offset) {
      if (!hasYaw(mem, offset)) {
         return null;
      }

      int off = offset + getValidatedOffset(mem, offset, 5, 13, "Yaw");
      long packed = VarInt.getWithLength(mem, off);
      int len = (int)packed;
      if (len < 0) {
         throw ProtocolException.negativeLength("Yaw", len);
      }

      if (len > 4096000) {
         throw ProtocolException.arrayTooLong("Yaw", len, 4096000);
      }

      int lenOffset = (int)(packed >>> 32);
      if (off + lenOffset + len * 23L > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("Yaw", off + lenOffset + len * 23, (int)mem.byteSize());
      }

      off += lenOffset;
      NoiseConfig[] data = new NoiseConfig[len];

      for (int i = 0; i < len; i++) {
         data[i] = NoiseConfig.toObject(mem, off + i * 23);
      }

      return data;
   }

   @Nullable
   public static NoiseConfig[] getRoll(MemorySegment mem) {
      return getRoll(mem, 0);
   }

   @Nullable
   public static NoiseConfig[] getRoll(MemorySegment mem, int offset) {
      if (!hasRoll(mem, offset)) {
         return null;
      }

      int off = offset + getValidatedOffset(mem, offset, 9, 13, "Roll");
      long packed = VarInt.getWithLength(mem, off);
      int len = (int)packed;
      if (len < 0) {
         throw ProtocolException.negativeLength("Roll", len);
      }

      if (len > 4096000) {
         throw ProtocolException.arrayTooLong("Roll", len, 4096000);
      }

      int lenOffset = (int)(packed >>> 32);
      if (off + lenOffset + len * 23L > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("Roll", off + lenOffset + len * 23, (int)mem.byteSize());
      }

      off += lenOffset;
      NoiseConfig[] data = new NoiseConfig[len];

      for (int i = 0; i < len; i++) {
         data[i] = NoiseConfig.toObject(mem, off + i * 23);
      }

      return data;
   }

   public static boolean hasPitch(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 1) != 0;
   }

   public static boolean hasYaw(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 2) != 0;
   }

   public static boolean hasRoll(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 4) != 0;
   }

   private static int getValidatedOffset(MemorySegment buffer, int base, int slotPosition, int varBlockStart, String fieldName) {
      int offset = buffer.get(PacketIO.PROTO_INT, base + slotPosition);
      if (offset >= 0 && offset <= buffer.byteSize() - base - varBlockStart) {
         return varBlockStart + offset;
      } else {
         throw ProtocolException.invalidOffset(fieldName, offset, (int)buffer.byteSize());
      }
   }

   public static RotationNoise toObject(MemorySegment mem) {
      return toObject(mem, 0);
   }

   public static RotationNoise toObject(MemorySegment mem, int offset) {
      if (offset + 13 > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("RotationNoise", offset + 13, (int)mem.byteSize());
      }

      NoiseConfig[] pitch = null;
      if (hasPitch(mem, offset)) {
         int off = offset + getValidatedOffset(mem, offset, 1, 13, "Pitch");
         long packed = VarInt.getWithLength(mem, off);
         int len = (int)packed;
         if (len < 0) {
            throw ProtocolException.negativeLength("Pitch", len);
         }

         if (len > 4096000) {
            throw ProtocolException.arrayTooLong("Pitch", len, 4096000);
         }

         int lenOffset = (int)(packed >>> 32);
         if (off + lenOffset + len * 23L > mem.byteSize()) {
            throw ProtocolException.bufferTooSmall("Pitch", off + lenOffset + len * 23, (int)mem.byteSize());
         }

         off += lenOffset;
         pitch = new NoiseConfig[len];

         for (int i = 0; i < len; i++) {
            pitch[i] = NoiseConfig.toObject(mem, off + i * 23);
         }
      }

      NoiseConfig[] yaw = null;
      if (hasYaw(mem, offset)) {
         int off = offset + getValidatedOffset(mem, offset, 5, 13, "Yaw");
         long packed = VarInt.getWithLength(mem, off);
         int len = (int)packed;
         if (len < 0) {
            throw ProtocolException.negativeLength("Yaw", len);
         }

         if (len > 4096000) {
            throw ProtocolException.arrayTooLong("Yaw", len, 4096000);
         }

         int lenOffset = (int)(packed >>> 32);
         if (off + lenOffset + len * 23L > mem.byteSize()) {
            throw ProtocolException.bufferTooSmall("Yaw", off + lenOffset + len * 23, (int)mem.byteSize());
         }

         off += lenOffset;
         yaw = new NoiseConfig[len];

         for (int i = 0; i < len; i++) {
            yaw[i] = NoiseConfig.toObject(mem, off + i * 23);
         }
      }

      NoiseConfig[] roll = null;
      if (hasRoll(mem, offset)) {
         int off = offset + getValidatedOffset(mem, offset, 9, 13, "Roll");
         long packed = VarInt.getWithLength(mem, off);
         int len = (int)packed;
         if (len < 0) {
            throw ProtocolException.negativeLength("Roll", len);
         }

         if (len > 4096000) {
            throw ProtocolException.arrayTooLong("Roll", len, 4096000);
         }

         int lenOffset = (int)(packed >>> 32);
         if (off + lenOffset + len * 23L > mem.byteSize()) {
            throw ProtocolException.bufferTooSmall("Roll", off + lenOffset + len * 23, (int)mem.byteSize());
         }

         off += lenOffset;
         roll = new NoiseConfig[len];

         for (int i = 0; i < len; i++) {
            roll[i] = NoiseConfig.toObject(mem, off + i * 23);
         }
      }

      return new RotationNoise(pitch, yaw, roll);
   }

   public void serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      byte nullBits = 0;
      if (this.pitch != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.yaw != null) {
         nullBits = (byte)(nullBits | 2);
      }

      if (this.roll != null) {
         nullBits = (byte)(nullBits | 4);
      }

      buf.writeByte(nullBits);
      int pitchOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int yawOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int rollOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.pitch != null) {
         buf.setIntLE(pitchOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.pitch.length > 4096000) {
            throw ProtocolException.arrayTooLong("Pitch", this.pitch.length, 4096000);
         }

         VarInt.write(buf, this.pitch.length);

         for (NoiseConfig item : this.pitch) {
            item.serialize(buf);
         }
      } else {
         buf.setIntLE(pitchOffsetSlot, -1);
      }

      if (this.yaw != null) {
         buf.setIntLE(yawOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.yaw.length > 4096000) {
            throw ProtocolException.arrayTooLong("Yaw", this.yaw.length, 4096000);
         }

         VarInt.write(buf, this.yaw.length);

         for (NoiseConfig item : this.yaw) {
            item.serialize(buf);
         }
      } else {
         buf.setIntLE(yawOffsetSlot, -1);
      }

      if (this.roll != null) {
         buf.setIntLE(rollOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.roll.length > 4096000) {
            throw ProtocolException.arrayTooLong("Roll", this.roll.length, 4096000);
         }

         VarInt.write(buf, this.roll.length);

         for (NoiseConfig item : this.roll) {
            item.serialize(buf);
         }
      } else {
         buf.setIntLE(rollOffsetSlot, -1);
      }
   }

   public int serialize(@Nonnull MemorySegment mem, int offset) {
      byte nullBits = 0;
      if (this.pitch != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.yaw != null) {
         nullBits = (byte)(nullBits | 2);
      }

      if (this.roll != null) {
         nullBits = (byte)(nullBits | 4);
      }

      mem.set(PacketIO.PROTO_BYTE, offset + 0, nullBits);
      int varOffset = offset + 13;
      if (this.pitch != null) {
         mem.set(PacketIO.PROTO_INT, offset + 1, varOffset - offset - 13);
         if (this.pitch.length > 4096000) {
            throw ProtocolException.arrayTooLong("Pitch", this.pitch.length, 4096000);
         }

         varOffset += VarInt.set(mem, varOffset, this.pitch.length);
         int pitchValueOffset = 0;

         for (int i = 0; i < this.pitch.length; i++) {
            pitchValueOffset += this.pitch[i].serialize(mem, varOffset + pitchValueOffset);
         }

         varOffset += pitchValueOffset;
      } else {
         mem.set(PacketIO.PROTO_INT, offset + 1, -1);
      }

      if (this.yaw != null) {
         mem.set(PacketIO.PROTO_INT, offset + 5, varOffset - offset - 13);
         if (this.yaw.length > 4096000) {
            throw ProtocolException.arrayTooLong("Yaw", this.yaw.length, 4096000);
         }

         varOffset += VarInt.set(mem, varOffset, this.yaw.length);
         int yawValueOffset = 0;

         for (int i = 0; i < this.yaw.length; i++) {
            yawValueOffset += this.yaw[i].serialize(mem, varOffset + yawValueOffset);
         }

         varOffset += yawValueOffset;
      } else {
         mem.set(PacketIO.PROTO_INT, offset + 5, -1);
      }

      if (this.roll != null) {
         mem.set(PacketIO.PROTO_INT, offset + 9, varOffset - offset - 13);
         if (this.roll.length > 4096000) {
            throw ProtocolException.arrayTooLong("Roll", this.roll.length, 4096000);
         }

         varOffset += VarInt.set(mem, varOffset, this.roll.length);
         int rollValueOffset = 0;

         for (int i = 0; i < this.roll.length; i++) {
            rollValueOffset += this.roll[i].serialize(mem, varOffset + rollValueOffset);
         }

         varOffset += rollValueOffset;
      } else {
         mem.set(PacketIO.PROTO_INT, offset + 9, -1);
      }

      return varOffset - offset;
   }

   public int computeSize() {
      int size = 13;
      if (this.pitch != null) {
         size += VarInt.size(this.pitch.length) + this.pitch.length * 23;
      }

      if (this.yaw != null) {
         size += VarInt.size(this.yaw.length) + this.yaw.length * 23;
      }

      if (this.roll != null) {
         size += VarInt.size(this.roll.length) + this.roll.length * 23;
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 13) {
         return ValidationResult.error("Buffer too small: expected at least 13 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      if ((nullBits & 1) != 0) {
         int pitchOffset = buffer.getIntLE(offset + 1);
         if (pitchOffset < 0 || pitchOffset > buffer.writerIndex() - offset - 13) {
            return ValidationResult.error("Invalid offset for Pitch");
         }

         int pos = offset + 13 + pitchOffset;
         int pitchCount = VarInt.peek(buffer, pos);
         if (pitchCount < 0) {
            return ValidationResult.error("Invalid array count for Pitch");
         }

         if (pitchCount > 4096000) {
            return ValidationResult.error("Pitch exceeds max length 4096000");
         }

         pos += VarInt.size(pitchCount);
         pos += pitchCount * 23;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Pitch");
         }
      }

      if ((nullBits & 2) != 0) {
         int yawOffset = buffer.getIntLE(offset + 5);
         if (yawOffset < 0 || yawOffset > buffer.writerIndex() - offset - 13) {
            return ValidationResult.error("Invalid offset for Yaw");
         }

         int pos = offset + 13 + yawOffset;
         int yawCount = VarInt.peek(buffer, pos);
         if (yawCount < 0) {
            return ValidationResult.error("Invalid array count for Yaw");
         }

         if (yawCount > 4096000) {
            return ValidationResult.error("Yaw exceeds max length 4096000");
         }

         pos += VarInt.size(yawCount);
         pos += yawCount * 23;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Yaw");
         }
      }

      if ((nullBits & 4) != 0) {
         int rollOffset = buffer.getIntLE(offset + 9);
         if (rollOffset < 0 || rollOffset > buffer.writerIndex() - offset - 13) {
            return ValidationResult.error("Invalid offset for Roll");
         }

         int pos = offset + 13 + rollOffset;
         int rollCount = VarInt.peek(buffer, pos);
         if (rollCount < 0) {
            return ValidationResult.error("Invalid array count for Roll");
         }

         if (rollCount > 4096000) {
            return ValidationResult.error("Roll exceeds max length 4096000");
         }

         pos += VarInt.size(rollCount);
         pos += rollCount * 23;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Roll");
         }
      }

      return ValidationResult.OK;
   }

   public RotationNoise clone() {
      RotationNoise copy = new RotationNoise();
      copy.pitch = this.pitch != null ? Arrays.stream(this.pitch).map(e -> e.clone()).toArray(NoiseConfig[]::new) : null;
      copy.yaw = this.yaw != null ? Arrays.stream(this.yaw).map(e -> e.clone()).toArray(NoiseConfig[]::new) : null;
      copy.roll = this.roll != null ? Arrays.stream(this.roll).map(e -> e.clone()).toArray(NoiseConfig[]::new) : null;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof RotationNoise other)
            ? false
            : Arrays.equals(this.pitch, other.pitch) && Arrays.equals(this.yaw, other.yaw) && Arrays.equals(this.roll, other.roll);
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      result = 31 * result + Arrays.hashCode(this.pitch);
      result = 31 * result + Arrays.hashCode(this.yaw);
      return 31 * result + Arrays.hashCode(this.roll);
   }
}
