package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AmbienceFX {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 22;
   public static final int VARIABLE_FIELD_COUNT = 5;
   public static final int VARIABLE_BLOCK_START = 42;
   public static final int MAX_SIZE = 405504250;
   @Nullable
   public String id;
   @Nullable
   public AmbienceFXConditions conditions;
   @Nullable
   public AmbienceFXSound[] sounds;
   public int musicContainerIndex;
   @Nullable
   public AmbienceFXAmbientBed ambientBed;
   @Nullable
   public AmbienceFXSoundEffect soundEffect;
   public int priority;
   @Nullable
   public int[] blockedAmbienceFxIndices;
   public int audioCategoryIndex;

   public AmbienceFX() {
   }

   public AmbienceFX(
      @Nullable String id,
      @Nullable AmbienceFXConditions conditions,
      @Nullable AmbienceFXSound[] sounds,
      int musicContainerIndex,
      @Nullable AmbienceFXAmbientBed ambientBed,
      @Nullable AmbienceFXSoundEffect soundEffect,
      int priority,
      @Nullable int[] blockedAmbienceFxIndices,
      int audioCategoryIndex
   ) {
      this.id = id;
      this.conditions = conditions;
      this.sounds = sounds;
      this.musicContainerIndex = musicContainerIndex;
      this.ambientBed = ambientBed;
      this.soundEffect = soundEffect;
      this.priority = priority;
      this.blockedAmbienceFxIndices = blockedAmbienceFxIndices;
      this.audioCategoryIndex = audioCategoryIndex;
   }

   public AmbienceFX(@Nonnull AmbienceFX other) {
      this.id = other.id;
      this.conditions = other.conditions;
      this.sounds = other.sounds;
      this.musicContainerIndex = other.musicContainerIndex;
      this.ambientBed = other.ambientBed;
      this.soundEffect = other.soundEffect;
      this.priority = other.priority;
      this.blockedAmbienceFxIndices = other.blockedAmbienceFxIndices;
      this.audioCategoryIndex = other.audioCategoryIndex;
   }

   @Nonnull
   public static AmbienceFX deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 42) {
         throw ProtocolException.bufferTooSmall("AmbienceFX", 42, buf.readableBytes() - offset);
      }

      AmbienceFX obj = new AmbienceFX();
      byte nullBits = buf.getByte(offset);
      obj.musicContainerIndex = buf.getIntLE(offset + 1);
      if ((nullBits & 1) != 0) {
         obj.soundEffect = AmbienceFXSoundEffect.deserialize(buf, offset + 5);
      }

      obj.priority = buf.getIntLE(offset + 14);
      obj.audioCategoryIndex = buf.getIntLE(offset + 18);
      if ((nullBits & 2) != 0) {
         int varPosBase0 = buf.getIntLE(offset + 22);
         if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 42) {
            throw ProtocolException.invalidOffset("Id", varPosBase0, buf.readableBytes());
         }

         int varPos0 = offset + 42 + varPosBase0;
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

      if ((nullBits & 4) != 0) {
         int varPosBase1 = buf.getIntLE(offset + 26);
         if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 42) {
            throw ProtocolException.invalidOffset("Conditions", varPosBase1, buf.readableBytes());
         }

         int varPos1 = offset + 42 + varPosBase1;
         obj.conditions = AmbienceFXConditions.deserialize(buf, varPos1);
      }

      if ((nullBits & 8) != 0) {
         int varPosBase2 = buf.getIntLE(offset + 30);
         if (varPosBase2 < 0 || varPosBase2 > buf.writerIndex() - offset - 42) {
            throw ProtocolException.invalidOffset("Sounds", varPosBase2, buf.readableBytes());
         }

         int varPos2 = offset + 42 + varPosBase2;
         int soundsCount = VarInt.peek(buf, varPos2);
         if (soundsCount < 0) {
            throw ProtocolException.invalidVarInt("Sounds");
         }

         int varIntLen = VarInt.size(soundsCount);
         if (soundsCount > 4096000) {
            throw ProtocolException.arrayTooLong("Sounds", soundsCount, 4096000);
         }

         if (varPos2 + varIntLen + soundsCount * 33L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Sounds", varPos2 + varIntLen + soundsCount * 33, buf.readableBytes());
         }

         obj.sounds = new AmbienceFXSound[soundsCount];
         int elemPos = varPos2 + varIntLen;

         for (int i = 0; i < soundsCount; i++) {
            obj.sounds[i] = AmbienceFXSound.deserialize(buf, elemPos);
            elemPos += AmbienceFXSound.computeBytesConsumed(buf, elemPos);
         }
      }

      if ((nullBits & 16) != 0) {
         int varPosBase3 = buf.getIntLE(offset + 34);
         if (varPosBase3 < 0 || varPosBase3 > buf.writerIndex() - offset - 42) {
            throw ProtocolException.invalidOffset("AmbientBed", varPosBase3, buf.readableBytes());
         }

         int varPos3 = offset + 42 + varPosBase3;
         obj.ambientBed = AmbienceFXAmbientBed.deserialize(buf, varPos3);
      }

      if ((nullBits & 32) != 0) {
         int varPosBase4 = buf.getIntLE(offset + 38);
         if (varPosBase4 < 0 || varPosBase4 > buf.writerIndex() - offset - 42) {
            throw ProtocolException.invalidOffset("BlockedAmbienceFxIndices", varPosBase4, buf.readableBytes());
         }

         int varPos4 = offset + 42 + varPosBase4;
         int blockedAmbienceFxIndicesCount = VarInt.peek(buf, varPos4);
         if (blockedAmbienceFxIndicesCount < 0) {
            throw ProtocolException.invalidVarInt("BlockedAmbienceFxIndices");
         }

         int varIntLen = VarInt.size(blockedAmbienceFxIndicesCount);
         if (blockedAmbienceFxIndicesCount > 4096000) {
            throw ProtocolException.arrayTooLong("BlockedAmbienceFxIndices", blockedAmbienceFxIndicesCount, 4096000);
         }

         if (varPos4 + varIntLen + blockedAmbienceFxIndicesCount * 4L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("BlockedAmbienceFxIndices", varPos4 + varIntLen + blockedAmbienceFxIndicesCount * 4, buf.readableBytes());
         }

         obj.blockedAmbienceFxIndices = new int[blockedAmbienceFxIndicesCount];

         for (int i = 0; i < blockedAmbienceFxIndicesCount; i++) {
            obj.blockedAmbienceFxIndices[i] = buf.getIntLE(varPos4 + varIntLen + i * 4);
         }
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 42;
      if ((nullBits & 2) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 22);
         if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 42) {
            throw ProtocolException.invalidOffset("Id", fieldOffset0, maxEnd);
         }

         int pos0 = offset + 42 + fieldOffset0;
         int sl = VarInt.peek(buf, pos0);
         pos0 += VarInt.size(sl) + sl;
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits & 4) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 26);
         if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 42) {
            throw ProtocolException.invalidOffset("Conditions", fieldOffset1, maxEnd);
         }

         int pos1 = offset + 42 + fieldOffset1;
         pos1 += AmbienceFXConditions.computeBytesConsumed(buf, pos1);
         if (pos1 - offset > maxEnd) {
            maxEnd = pos1 - offset;
         }
      }

      if ((nullBits & 8) != 0) {
         int fieldOffset2 = buf.getIntLE(offset + 30);
         if (fieldOffset2 < 0 || fieldOffset2 > buf.writerIndex() - offset - 42) {
            throw ProtocolException.invalidOffset("Sounds", fieldOffset2, maxEnd);
         }

         int pos2 = offset + 42 + fieldOffset2;
         int arrLen = VarInt.peek(buf, pos2);
         pos2 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            pos2 += AmbienceFXSound.computeBytesConsumed(buf, pos2);
         }

         if (pos2 - offset > maxEnd) {
            maxEnd = pos2 - offset;
         }
      }

      if ((nullBits & 16) != 0) {
         int fieldOffset3 = buf.getIntLE(offset + 34);
         if (fieldOffset3 < 0 || fieldOffset3 > buf.writerIndex() - offset - 42) {
            throw ProtocolException.invalidOffset("AmbientBed", fieldOffset3, maxEnd);
         }

         int pos3 = offset + 42 + fieldOffset3;
         pos3 += AmbienceFXAmbientBed.computeBytesConsumed(buf, pos3);
         if (pos3 - offset > maxEnd) {
            maxEnd = pos3 - offset;
         }
      }

      if ((nullBits & 32) != 0) {
         int fieldOffset4 = buf.getIntLE(offset + 38);
         if (fieldOffset4 < 0 || fieldOffset4 > buf.writerIndex() - offset - 42) {
            throw ProtocolException.invalidOffset("BlockedAmbienceFxIndices", fieldOffset4, maxEnd);
         }

         int pos4 = offset + 42 + fieldOffset4;
         int arrLen = VarInt.peek(buf, pos4);
         pos4 += VarInt.size(arrLen) + arrLen * 4;
         if (pos4 - offset > maxEnd) {
            maxEnd = pos4 - offset;
         }
      }

      return maxEnd;
   }

   public void serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      byte nullBits = 0;
      if (this.soundEffect != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.id != null) {
         nullBits = (byte)(nullBits | 2);
      }

      if (this.conditions != null) {
         nullBits = (byte)(nullBits | 4);
      }

      if (this.sounds != null) {
         nullBits = (byte)(nullBits | 8);
      }

      if (this.ambientBed != null) {
         nullBits = (byte)(nullBits | 16);
      }

      if (this.blockedAmbienceFxIndices != null) {
         nullBits = (byte)(nullBits | 32);
      }

      buf.writeByte(nullBits);
      buf.writeIntLE(this.musicContainerIndex);
      if (this.soundEffect != null) {
         this.soundEffect.serialize(buf);
      } else {
         buf.writeZero(9);
      }

      buf.writeIntLE(this.priority);
      buf.writeIntLE(this.audioCategoryIndex);
      int idOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int conditionsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int soundsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int ambientBedOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int blockedAmbienceFxIndicesOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.id != null) {
         buf.setIntLE(idOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.id, 4096000);
      } else {
         buf.setIntLE(idOffsetSlot, -1);
      }

      if (this.conditions != null) {
         buf.setIntLE(conditionsOffsetSlot, buf.writerIndex() - varBlockStart);
         this.conditions.serialize(buf);
      } else {
         buf.setIntLE(conditionsOffsetSlot, -1);
      }

      if (this.sounds != null) {
         buf.setIntLE(soundsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.sounds.length > 4096000) {
            throw ProtocolException.arrayTooLong("Sounds", this.sounds.length, 4096000);
         }

         VarInt.write(buf, this.sounds.length);

         for (AmbienceFXSound item : this.sounds) {
            item.serialize(buf);
         }
      } else {
         buf.setIntLE(soundsOffsetSlot, -1);
      }

      if (this.ambientBed != null) {
         buf.setIntLE(ambientBedOffsetSlot, buf.writerIndex() - varBlockStart);
         this.ambientBed.serialize(buf);
      } else {
         buf.setIntLE(ambientBedOffsetSlot, -1);
      }

      if (this.blockedAmbienceFxIndices != null) {
         buf.setIntLE(blockedAmbienceFxIndicesOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.blockedAmbienceFxIndices.length > 4096000) {
            throw ProtocolException.arrayTooLong("BlockedAmbienceFxIndices", this.blockedAmbienceFxIndices.length, 4096000);
         }

         VarInt.write(buf, this.blockedAmbienceFxIndices.length);

         for (int item : this.blockedAmbienceFxIndices) {
            buf.writeIntLE(item);
         }
      } else {
         buf.setIntLE(blockedAmbienceFxIndicesOffsetSlot, -1);
      }
   }

   public int computeSize() {
      int size = 42;
      if (this.id != null) {
         size += PacketIO.stringSize(this.id);
      }

      if (this.conditions != null) {
         size += this.conditions.computeSize();
      }

      if (this.sounds != null) {
         size += VarInt.size(this.sounds.length) + this.sounds.length * 33;
      }

      if (this.ambientBed != null) {
         size += this.ambientBed.computeSize();
      }

      if (this.blockedAmbienceFxIndices != null) {
         size += VarInt.size(this.blockedAmbienceFxIndices.length) + this.blockedAmbienceFxIndices.length * 4;
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 42) {
         return ValidationResult.error("Buffer too small: expected at least 42 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      if ((nullBits & 2) != 0) {
         int idOffset = buffer.getIntLE(offset + 22);
         if (idOffset < 0 || idOffset > buffer.writerIndex() - offset - 42) {
            return ValidationResult.error("Invalid offset for Id");
         }

         int pos = offset + 42 + idOffset;
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

      if ((nullBits & 4) != 0) {
         int conditionsOffset = buffer.getIntLE(offset + 26);
         if (conditionsOffset < 0 || conditionsOffset > buffer.writerIndex() - offset - 42) {
            return ValidationResult.error("Invalid offset for Conditions");
         }

         int pos = offset + 42 + conditionsOffset;
         ValidationResult conditionsResult = AmbienceFXConditions.validateStructure(buffer, pos);
         if (!conditionsResult.isValid()) {
            return ValidationResult.error("Invalid Conditions: " + conditionsResult.error());
         }

         pos += AmbienceFXConditions.computeBytesConsumed(buffer, pos);
      }

      if ((nullBits & 8) != 0) {
         int soundsOffset = buffer.getIntLE(offset + 30);
         if (soundsOffset < 0 || soundsOffset > buffer.writerIndex() - offset - 42) {
            return ValidationResult.error("Invalid offset for Sounds");
         }

         int pos = offset + 42 + soundsOffset;
         int soundsCount = VarInt.peek(buffer, pos);
         if (soundsCount < 0) {
            return ValidationResult.error("Invalid array count for Sounds");
         }

         if (soundsCount > 4096000) {
            return ValidationResult.error("Sounds exceeds max length 4096000");
         }

         pos += VarInt.size(soundsCount);
         pos += soundsCount * 33;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Sounds");
         }
      }

      if ((nullBits & 16) != 0) {
         int ambientBedOffset = buffer.getIntLE(offset + 34);
         if (ambientBedOffset < 0 || ambientBedOffset > buffer.writerIndex() - offset - 42) {
            return ValidationResult.error("Invalid offset for AmbientBed");
         }

         int pos = offset + 42 + ambientBedOffset;
         ValidationResult ambientBedResult = AmbienceFXAmbientBed.validateStructure(buffer, pos);
         if (!ambientBedResult.isValid()) {
            return ValidationResult.error("Invalid AmbientBed: " + ambientBedResult.error());
         }

         pos += AmbienceFXAmbientBed.computeBytesConsumed(buffer, pos);
      }

      if ((nullBits & 32) != 0) {
         int blockedAmbienceFxIndicesOffset = buffer.getIntLE(offset + 38);
         if (blockedAmbienceFxIndicesOffset < 0 || blockedAmbienceFxIndicesOffset > buffer.writerIndex() - offset - 42) {
            return ValidationResult.error("Invalid offset for BlockedAmbienceFxIndices");
         }

         int pos = offset + 42 + blockedAmbienceFxIndicesOffset;
         int blockedAmbienceFxIndicesCount = VarInt.peek(buffer, pos);
         if (blockedAmbienceFxIndicesCount < 0) {
            return ValidationResult.error("Invalid array count for BlockedAmbienceFxIndices");
         }

         if (blockedAmbienceFxIndicesCount > 4096000) {
            return ValidationResult.error("BlockedAmbienceFxIndices exceeds max length 4096000");
         }

         pos += VarInt.size(blockedAmbienceFxIndicesCount);
         pos += blockedAmbienceFxIndicesCount * 4;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading BlockedAmbienceFxIndices");
         }
      }

      return ValidationResult.OK;
   }

   public AmbienceFX clone() {
      AmbienceFX copy = new AmbienceFX();
      copy.id = this.id;
      copy.conditions = this.conditions != null ? this.conditions.clone() : null;
      copy.sounds = this.sounds != null ? Arrays.stream(this.sounds).map(e -> e.clone()).toArray(AmbienceFXSound[]::new) : null;
      copy.musicContainerIndex = this.musicContainerIndex;
      copy.ambientBed = this.ambientBed != null ? this.ambientBed.clone() : null;
      copy.soundEffect = this.soundEffect != null ? this.soundEffect.clone() : null;
      copy.priority = this.priority;
      copy.blockedAmbienceFxIndices = this.blockedAmbienceFxIndices != null
         ? Arrays.copyOf(this.blockedAmbienceFxIndices, this.blockedAmbienceFxIndices.length)
         : null;
      copy.audioCategoryIndex = this.audioCategoryIndex;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof AmbienceFX other)
            ? false
            : Objects.equals(this.id, other.id)
               && Objects.equals(this.conditions, other.conditions)
               && Arrays.equals(this.sounds, other.sounds)
               && this.musicContainerIndex == other.musicContainerIndex
               && Objects.equals(this.ambientBed, other.ambientBed)
               && Objects.equals(this.soundEffect, other.soundEffect)
               && this.priority == other.priority
               && Arrays.equals(this.blockedAmbienceFxIndices, other.blockedAmbienceFxIndices)
               && this.audioCategoryIndex == other.audioCategoryIndex;
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      result = 31 * result + Objects.hashCode(this.id);
      result = 31 * result + Objects.hashCode(this.conditions);
      result = 31 * result + Arrays.hashCode(this.sounds);
      result = 31 * result + Integer.hashCode(this.musicContainerIndex);
      result = 31 * result + Objects.hashCode(this.ambientBed);
      result = 31 * result + Objects.hashCode(this.soundEffect);
      result = 31 * result + Integer.hashCode(this.priority);
      result = 31 * result + Arrays.hashCode(this.blockedAmbienceFxIndices);
      return 31 * result + Integer.hashCode(this.audioCategoryIndex);
   }
}
