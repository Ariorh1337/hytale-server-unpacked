package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntityStatType {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 15;
   public static final int VARIABLE_FIELD_COUNT = 3;
   public static final int VARIABLE_BLOCK_START = 27;
   public static final int MAX_SIZE = 1677721600;
   @Nullable
   public String id;
   public float value;
   public float min;
   public float max;
   @Nullable
   public EntityStatEffects minValueEffects;
   @Nullable
   public EntityStatEffects maxValueEffects;
   @Nonnull
   public EntityStatResetBehavior resetBehavior = EntityStatResetBehavior.InitialValue;
   public boolean hideFromTooltip;

   public EntityStatType() {
   }

   public EntityStatType(
      @Nullable String id,
      float value,
      float min,
      float max,
      @Nullable EntityStatEffects minValueEffects,
      @Nullable EntityStatEffects maxValueEffects,
      @Nonnull EntityStatResetBehavior resetBehavior,
      boolean hideFromTooltip
   ) {
      this.id = id;
      this.value = value;
      this.min = min;
      this.max = max;
      this.minValueEffects = minValueEffects;
      this.maxValueEffects = maxValueEffects;
      this.resetBehavior = resetBehavior;
      this.hideFromTooltip = hideFromTooltip;
   }

   public EntityStatType(@Nonnull EntityStatType other) {
      this.id = other.id;
      this.value = other.value;
      this.min = other.min;
      this.max = other.max;
      this.minValueEffects = other.minValueEffects;
      this.maxValueEffects = other.maxValueEffects;
      this.resetBehavior = other.resetBehavior;
      this.hideFromTooltip = other.hideFromTooltip;
   }

   @Nonnull
   public static EntityStatType deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 27) {
         throw ProtocolException.bufferTooSmall("EntityStatType", 27, buf.readableBytes() - offset);
      }

      EntityStatType obj = new EntityStatType();
      byte nullBits = buf.getByte(offset);
      obj.value = buf.getFloatLE(offset + 1);
      obj.min = buf.getFloatLE(offset + 5);
      obj.max = buf.getFloatLE(offset + 9);
      obj.resetBehavior = EntityStatResetBehavior.fromValue(buf.getByte(offset + 13));
      obj.hideFromTooltip = buf.getByte(offset + 14) != 0;
      if ((nullBits & 1) != 0) {
         int varPosBase0 = buf.getIntLE(offset + 15);
         if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 27) {
            throw ProtocolException.invalidOffset("Id", varPosBase0, buf.readableBytes());
         }

         int varPos0 = offset + 27 + varPosBase0;
         int idLen = VarInt.peek(buf, varPos0);
         if (idLen < 0) {
            throw ProtocolException.invalidVarInt("Id");
         }

         int idVarIntLen = VarInt.size(idLen);
         if (idLen > 4096000) {
            throw ProtocolException.stringTooLong("Id", idLen, 4096000);
         }

         if (varPos0 + idVarIntLen + idLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Id", varPos0 + idVarIntLen + idLen, buf.readableBytes());
         }

         obj.id = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
      }

      if ((nullBits & 2) != 0) {
         int varPosBase1 = buf.getIntLE(offset + 19);
         if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 27) {
            throw ProtocolException.invalidOffset("MinValueEffects", varPosBase1, buf.readableBytes());
         }

         int varPos1 = offset + 27 + varPosBase1;
         obj.minValueEffects = EntityStatEffects.deserialize(buf, varPos1);
      }

      if ((nullBits & 4) != 0) {
         int varPosBase2 = buf.getIntLE(offset + 23);
         if (varPosBase2 < 0 || varPosBase2 > buf.writerIndex() - offset - 27) {
            throw ProtocolException.invalidOffset("MaxValueEffects", varPosBase2, buf.readableBytes());
         }

         int varPos2 = offset + 27 + varPosBase2;
         obj.maxValueEffects = EntityStatEffects.deserialize(buf, varPos2);
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 27;
      if ((nullBits & 1) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 15);
         if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 27) {
            throw ProtocolException.invalidOffset("Id", fieldOffset0, maxEnd);
         }

         int pos0 = offset + 27 + fieldOffset0;
         int sl = VarInt.peek(buf, pos0);
         pos0 += VarInt.size(sl) + sl;
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits & 2) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 19);
         if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 27) {
            throw ProtocolException.invalidOffset("MinValueEffects", fieldOffset1, maxEnd);
         }

         int pos1 = offset + 27 + fieldOffset1;
         pos1 += EntityStatEffects.computeBytesConsumed(buf, pos1);
         if (pos1 - offset > maxEnd) {
            maxEnd = pos1 - offset;
         }
      }

      if ((nullBits & 4) != 0) {
         int fieldOffset2 = buf.getIntLE(offset + 23);
         if (fieldOffset2 < 0 || fieldOffset2 > buf.writerIndex() - offset - 27) {
            throw ProtocolException.invalidOffset("MaxValueEffects", fieldOffset2, maxEnd);
         }

         int pos2 = offset + 27 + fieldOffset2;
         pos2 += EntityStatEffects.computeBytesConsumed(buf, pos2);
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

      if (this.minValueEffects != null) {
         nullBits = (byte)(nullBits | 2);
      }

      if (this.maxValueEffects != null) {
         nullBits = (byte)(nullBits | 4);
      }

      buf.writeByte(nullBits);
      buf.writeFloatLE(this.value);
      buf.writeFloatLE(this.min);
      buf.writeFloatLE(this.max);
      buf.writeByte(this.resetBehavior.getValue());
      buf.writeByte(this.hideFromTooltip ? 1 : 0);
      int idOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int minValueEffectsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int maxValueEffectsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.id != null) {
         buf.setIntLE(idOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.id, 4096000);
      } else {
         buf.setIntLE(idOffsetSlot, -1);
      }

      if (this.minValueEffects != null) {
         buf.setIntLE(minValueEffectsOffsetSlot, buf.writerIndex() - varBlockStart);
         this.minValueEffects.serialize(buf);
      } else {
         buf.setIntLE(minValueEffectsOffsetSlot, -1);
      }

      if (this.maxValueEffects != null) {
         buf.setIntLE(maxValueEffectsOffsetSlot, buf.writerIndex() - varBlockStart);
         this.maxValueEffects.serialize(buf);
      } else {
         buf.setIntLE(maxValueEffectsOffsetSlot, -1);
      }
   }

   public int computeSize() {
      int size = 27;
      if (this.id != null) {
         size += PacketIO.stringSize(this.id);
      }

      if (this.minValueEffects != null) {
         size += this.minValueEffects.computeSize();
      }

      if (this.maxValueEffects != null) {
         size += this.maxValueEffects.computeSize();
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 27) {
         return ValidationResult.error("Buffer too small: expected at least 27 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      int v = buffer.getByte(offset + 13) & 255;
      if (v >= 2) {
         return ValidationResult.error("Invalid EntityStatResetBehavior value for ResetBehavior");
      }

      if ((nullBits & 1) != 0) {
         v = buffer.getIntLE(offset + 15);
         if (v < 0 || v > buffer.writerIndex() - offset - 27) {
            return ValidationResult.error("Invalid offset for Id");
         }

         int pos = offset + 27 + v;
         int idLen = VarInt.peek(buffer, pos);
         if (idLen < 0) {
            return ValidationResult.error("Invalid string length for Id");
         }

         if (idLen > 4096000) {
            return ValidationResult.error("Id exceeds max length 4096000");
         }

         pos += VarInt.size(idLen);
         pos += idLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Id");
         }
      }

      if ((nullBits & 2) != 0) {
         v = buffer.getIntLE(offset + 19);
         if (v < 0 || v > buffer.writerIndex() - offset - 27) {
            return ValidationResult.error("Invalid offset for MinValueEffects");
         }

         int pos = offset + 27 + v;
         ValidationResult minValueEffectsResult = EntityStatEffects.validateStructure(buffer, pos);
         if (!minValueEffectsResult.isValid()) {
            return ValidationResult.error("Invalid MinValueEffects: " + minValueEffectsResult.error());
         }

         pos += EntityStatEffects.computeBytesConsumed(buffer, pos);
      }

      if ((nullBits & 4) != 0) {
         v = buffer.getIntLE(offset + 23);
         if (v < 0 || v > buffer.writerIndex() - offset - 27) {
            return ValidationResult.error("Invalid offset for MaxValueEffects");
         }

         int pos = offset + 27 + v;
         ValidationResult maxValueEffectsResult = EntityStatEffects.validateStructure(buffer, pos);
         if (!maxValueEffectsResult.isValid()) {
            return ValidationResult.error("Invalid MaxValueEffects: " + maxValueEffectsResult.error());
         }

         pos += EntityStatEffects.computeBytesConsumed(buffer, pos);
      }

      return ValidationResult.OK;
   }

   public EntityStatType clone() {
      EntityStatType copy = new EntityStatType();
      copy.id = this.id;
      copy.value = this.value;
      copy.min = this.min;
      copy.max = this.max;
      copy.minValueEffects = this.minValueEffects != null ? this.minValueEffects.clone() : null;
      copy.maxValueEffects = this.maxValueEffects != null ? this.maxValueEffects.clone() : null;
      copy.resetBehavior = this.resetBehavior;
      copy.hideFromTooltip = this.hideFromTooltip;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof EntityStatType other)
            ? false
            : Objects.equals(this.id, other.id)
               && this.value == other.value
               && this.min == other.min
               && this.max == other.max
               && Objects.equals(this.minValueEffects, other.minValueEffects)
               && Objects.equals(this.maxValueEffects, other.maxValueEffects)
               && Objects.equals(this.resetBehavior, other.resetBehavior)
               && this.hideFromTooltip == other.hideFromTooltip;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.id, this.value, this.min, this.max, this.minValueEffects, this.maxValueEffects, this.resetBehavior, this.hideFromTooltip);
   }
}
