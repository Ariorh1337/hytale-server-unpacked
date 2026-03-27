package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockSoundSet {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 9;
   public static final int VARIABLE_FIELD_COUNT = 2;
   public static final int VARIABLE_BLOCK_START = 17;
   public static final int MAX_SIZE = 36864027;
   @Nullable
   public String id;
   @Nullable
   public Map<BlockSoundEvent, Integer> soundEventIndices;
   @Nullable
   public FloatRange moveInRepeatRange;

   public BlockSoundSet() {
   }

   public BlockSoundSet(@Nullable String id, @Nullable Map<BlockSoundEvent, Integer> soundEventIndices, @Nullable FloatRange moveInRepeatRange) {
      this.id = id;
      this.soundEventIndices = soundEventIndices;
      this.moveInRepeatRange = moveInRepeatRange;
   }

   public BlockSoundSet(@Nonnull BlockSoundSet other) {
      this.id = other.id;
      this.soundEventIndices = other.soundEventIndices;
      this.moveInRepeatRange = other.moveInRepeatRange;
   }

   @Nonnull
   public static BlockSoundSet deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 17) {
         throw ProtocolException.bufferTooSmall("BlockSoundSet", 17, buf.readableBytes() - offset);
      }

      BlockSoundSet obj = new BlockSoundSet();
      byte nullBits = buf.getByte(offset);
      if ((nullBits & 1) != 0) {
         obj.moveInRepeatRange = FloatRange.deserialize(buf, offset + 1);
      }

      if ((nullBits & 2) != 0) {
         int varPosBase0 = buf.getIntLE(offset + 9);
         if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 17) {
            throw ProtocolException.invalidOffset("Id", varPosBase0, buf.readableBytes());
         }

         int varPos0 = offset + 17 + varPosBase0;
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
         int varPosBase1 = buf.getIntLE(offset + 13);
         if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 17) {
            throw ProtocolException.invalidOffset("SoundEventIndices", varPosBase1, buf.readableBytes());
         }

         int varPos1 = offset + 17 + varPosBase1;
         int soundEventIndicesCount = VarInt.peek(buf, varPos1);
         if (soundEventIndicesCount < 0) {
            throw ProtocolException.invalidVarInt("SoundEventIndices");
         }

         int varIntLen = VarInt.size(soundEventIndicesCount);
         if (soundEventIndicesCount > 4096000) {
            throw ProtocolException.dictionaryTooLarge("SoundEventIndices", soundEventIndicesCount, 4096000);
         }

         obj.soundEventIndices = new HashMap<>(soundEventIndicesCount);
         int dictPos = varPos1 + varIntLen;

         for (int i = 0; i < soundEventIndicesCount; i++) {
            BlockSoundEvent key = BlockSoundEvent.fromValue(buf.getByte(dictPos));
            int val = buf.getIntLE(++dictPos);
            dictPos += 4;
            if (obj.soundEventIndices.put(key, val) != null) {
               throw ProtocolException.duplicateKey("soundEventIndices", key);
            }
         }
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 17;
      if ((nullBits & 2) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 9);
         if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 17) {
            throw ProtocolException.invalidOffset("Id", fieldOffset0, maxEnd);
         }

         int pos0 = offset + 17 + fieldOffset0;
         int sl = VarInt.peek(buf, pos0);
         pos0 += VarInt.size(sl) + sl;
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits & 4) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 13);
         if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 17) {
            throw ProtocolException.invalidOffset("SoundEventIndices", fieldOffset1, maxEnd);
         }

         int pos1 = offset + 17 + fieldOffset1;
         int dictLen = VarInt.peek(buf, pos1);
         pos1 += VarInt.size(dictLen);

         for (int i = 0; i < dictLen; i++) {
            pos1 = ++pos1 + 4;
         }

         if (pos1 - offset > maxEnd) {
            maxEnd = pos1 - offset;
         }
      }

      return maxEnd;
   }

   public void serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      byte nullBits = 0;
      if (this.moveInRepeatRange != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.id != null) {
         nullBits = (byte)(nullBits | 2);
      }

      if (this.soundEventIndices != null) {
         nullBits = (byte)(nullBits | 4);
      }

      buf.writeByte(nullBits);
      if (this.moveInRepeatRange != null) {
         this.moveInRepeatRange.serialize(buf);
      } else {
         buf.writeZero(8);
      }

      int idOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int soundEventIndicesOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.id != null) {
         buf.setIntLE(idOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.id, 4096000);
      } else {
         buf.setIntLE(idOffsetSlot, -1);
      }

      if (this.soundEventIndices != null) {
         buf.setIntLE(soundEventIndicesOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.soundEventIndices.size() > 4096000) {
            throw ProtocolException.dictionaryTooLarge("SoundEventIndices", this.soundEventIndices.size(), 4096000);
         }

         VarInt.write(buf, this.soundEventIndices.size());

         for (Entry<BlockSoundEvent, Integer> e : this.soundEventIndices.entrySet()) {
            buf.writeByte(e.getKey().getValue());
            buf.writeIntLE(e.getValue());
         }
      } else {
         buf.setIntLE(soundEventIndicesOffsetSlot, -1);
      }
   }

   public int computeSize() {
      int size = 17;
      if (this.id != null) {
         size += PacketIO.stringSize(this.id);
      }

      if (this.soundEventIndices != null) {
         size += VarInt.size(this.soundEventIndices.size()) + this.soundEventIndices.size() * 5;
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 17) {
         return ValidationResult.error("Buffer too small: expected at least 17 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      if ((nullBits & 2) != 0) {
         int idOffset = buffer.getIntLE(offset + 9);
         if (idOffset < 0 || idOffset > buffer.writerIndex() - offset - 17) {
            return ValidationResult.error("Invalid offset for Id");
         }

         int pos = offset + 17 + idOffset;
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
         int soundEventIndicesOffset = buffer.getIntLE(offset + 13);
         if (soundEventIndicesOffset < 0 || soundEventIndicesOffset > buffer.writerIndex() - offset - 17) {
            return ValidationResult.error("Invalid offset for SoundEventIndices");
         }

         int pos = offset + 17 + soundEventIndicesOffset;
         int soundEventIndicesCount = VarInt.peek(buffer, pos);
         if (soundEventIndicesCount < 0) {
            return ValidationResult.error("Invalid dictionary count for SoundEventIndices");
         }

         if (soundEventIndicesCount > 4096000) {
            return ValidationResult.error("SoundEventIndices exceeds max length 4096000");
         }

         pos += VarInt.size(soundEventIndicesCount);

         for (int i = 0; i < soundEventIndicesCount; i++) {
            int v = buffer.getByte(pos) & 255;
            if (v >= 9) {
               return ValidationResult.error("Invalid BlockSoundEvent value for key");
            }

            pos = ++pos + 4;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading value");
            }
         }
      }

      return ValidationResult.OK;
   }

   public BlockSoundSet clone() {
      BlockSoundSet copy = new BlockSoundSet();
      copy.id = this.id;
      copy.soundEventIndices = this.soundEventIndices != null ? new HashMap<>(this.soundEventIndices) : null;
      copy.moveInRepeatRange = this.moveInRepeatRange != null ? this.moveInRepeatRange.clone() : null;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof BlockSoundSet other)
            ? false
            : Objects.equals(this.id, other.id)
               && Objects.equals(this.soundEventIndices, other.soundEventIndices)
               && Objects.equals(this.moveInRepeatRange, other.moveInRepeatRange);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.id, this.soundEventIndices, this.moveInRepeatRange);
   }
}
