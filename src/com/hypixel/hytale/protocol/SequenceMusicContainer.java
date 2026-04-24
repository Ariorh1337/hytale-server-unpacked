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

public class SequenceMusicContainer extends MusicContainer {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 59;
   public static final int VARIABLE_FIELD_COUNT = 2;
   public static final int VARIABLE_BLOCK_START = 67;
   public static final int MAX_SIZE = 32768077;
   @Nullable
   public int[] children;

   public SequenceMusicContainer() {
   }

   public SequenceMusicContainer(
      float volume,
      int loopCount,
      float weight,
      @Nullable Rangef silenceAfter,
      @Nullable Rangef exitSilence,
      float fadeInDuration,
      float fadeOutDuration,
      @Nonnull MusicTransitionType transitionType,
      float transitionDuration,
      boolean playToCompletion,
      @Nullable String nameTranslationKey,
      int audioCategoryIndex,
      @Nullable TempoSettings tempo,
      @Nullable int[] children
   ) {
      this.volume = volume;
      this.loopCount = loopCount;
      this.weight = weight;
      this.silenceAfter = silenceAfter;
      this.exitSilence = exitSilence;
      this.fadeInDuration = fadeInDuration;
      this.fadeOutDuration = fadeOutDuration;
      this.transitionType = transitionType;
      this.transitionDuration = transitionDuration;
      this.playToCompletion = playToCompletion;
      this.nameTranslationKey = nameTranslationKey;
      this.audioCategoryIndex = audioCategoryIndex;
      this.tempo = tempo;
      this.children = children;
   }

   public SequenceMusicContainer(@Nonnull SequenceMusicContainer other) {
      this.volume = other.volume;
      this.loopCount = other.loopCount;
      this.weight = other.weight;
      this.silenceAfter = other.silenceAfter;
      this.exitSilence = other.exitSilence;
      this.fadeInDuration = other.fadeInDuration;
      this.fadeOutDuration = other.fadeOutDuration;
      this.transitionType = other.transitionType;
      this.transitionDuration = other.transitionDuration;
      this.playToCompletion = other.playToCompletion;
      this.nameTranslationKey = other.nameTranslationKey;
      this.audioCategoryIndex = other.audioCategoryIndex;
      this.tempo = other.tempo;
      this.children = other.children;
   }

   @Nonnull
   public static SequenceMusicContainer deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 67) {
         throw ProtocolException.bufferTooSmall("SequenceMusicContainer", 67, buf.readableBytes() - offset);
      }

      SequenceMusicContainer obj = new SequenceMusicContainer();
      byte nullBits = buf.getByte(offset);
      obj.volume = buf.getFloatLE(offset + 1);
      obj.loopCount = buf.getIntLE(offset + 5);
      obj.weight = buf.getFloatLE(offset + 9);
      if ((nullBits & 1) != 0) {
         obj.silenceAfter = Rangef.deserialize(buf, offset + 13);
      }

      if ((nullBits & 2) != 0) {
         obj.exitSilence = Rangef.deserialize(buf, offset + 21);
      }

      obj.fadeInDuration = buf.getFloatLE(offset + 29);
      obj.fadeOutDuration = buf.getFloatLE(offset + 33);
      obj.transitionType = MusicTransitionType.fromValue(buf.getByte(offset + 37));
      obj.transitionDuration = buf.getFloatLE(offset + 38);
      obj.playToCompletion = buf.getByte(offset + 42) != 0;
      obj.audioCategoryIndex = buf.getIntLE(offset + 43);
      if ((nullBits & 4) != 0) {
         obj.tempo = TempoSettings.deserialize(buf, offset + 47);
      }

      if ((nullBits & 8) != 0) {
         int varPosBase0 = buf.getIntLE(offset + 59);
         if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 67) {
            throw ProtocolException.invalidOffset("NameTranslationKey", varPosBase0, buf.readableBytes());
         }

         int varPos0 = offset + 67 + varPosBase0;
         int nameTranslationKeyLen = VarInt.peek(buf, varPos0);
         if (nameTranslationKeyLen < 0) {
            throw ProtocolException.invalidVarInt("NameTranslationKey");
         }

         int nameTranslationKeyVarIntLen = VarInt.size(nameTranslationKeyLen);
         if (nameTranslationKeyLen > 4096000) {
            throw ProtocolException.stringTooLong("NameTranslationKey", nameTranslationKeyLen, 4096000);
         }

         if (varPos0 + nameTranslationKeyVarIntLen + nameTranslationKeyLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("NameTranslationKey", varPos0 + nameTranslationKeyVarIntLen + nameTranslationKeyLen, buf.readableBytes());
         }

         obj.nameTranslationKey = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
      }

      if ((nullBits & 16) != 0) {
         int varPosBase1 = buf.getIntLE(offset + 63);
         if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 67) {
            throw ProtocolException.invalidOffset("Children", varPosBase1, buf.readableBytes());
         }

         int varPos1 = offset + 67 + varPosBase1;
         int childrenCount = VarInt.peek(buf, varPos1);
         if (childrenCount < 0) {
            throw ProtocolException.invalidVarInt("Children");
         }

         int varIntLen = VarInt.size(childrenCount);
         if (childrenCount > 4096000) {
            throw ProtocolException.arrayTooLong("Children", childrenCount, 4096000);
         }

         if (varPos1 + varIntLen + childrenCount * 4L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Children", varPos1 + varIntLen + childrenCount * 4, buf.readableBytes());
         }

         obj.children = new int[childrenCount];

         for (int i = 0; i < childrenCount; i++) {
            obj.children[i] = buf.getIntLE(varPos1 + varIntLen + i * 4);
         }
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 67;
      if ((nullBits & 8) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 59);
         if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 67) {
            throw ProtocolException.invalidOffset("NameTranslationKey", fieldOffset0, maxEnd);
         }

         int pos0 = offset + 67 + fieldOffset0;
         int sl = VarInt.peek(buf, pos0);
         pos0 += VarInt.size(sl) + sl;
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits & 16) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 63);
         if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 67) {
            throw ProtocolException.invalidOffset("Children", fieldOffset1, maxEnd);
         }

         int pos1 = offset + 67 + fieldOffset1;
         int arrLen = VarInt.peek(buf, pos1);
         pos1 += VarInt.size(arrLen) + arrLen * 4;
         if (pos1 - offset > maxEnd) {
            maxEnd = pos1 - offset;
         }
      }

      return maxEnd;
   }

   @Override
   public int serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      byte nullBits = 0;
      if (this.silenceAfter != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.exitSilence != null) {
         nullBits = (byte)(nullBits | 2);
      }

      if (this.tempo != null) {
         nullBits = (byte)(nullBits | 4);
      }

      if (this.nameTranslationKey != null) {
         nullBits = (byte)(nullBits | 8);
      }

      if (this.children != null) {
         nullBits = (byte)(nullBits | 16);
      }

      buf.writeByte(nullBits);
      buf.writeFloatLE(this.volume);
      buf.writeIntLE(this.loopCount);
      buf.writeFloatLE(this.weight);
      if (this.silenceAfter != null) {
         this.silenceAfter.serialize(buf);
      } else {
         buf.writeZero(8);
      }

      if (this.exitSilence != null) {
         this.exitSilence.serialize(buf);
      } else {
         buf.writeZero(8);
      }

      buf.writeFloatLE(this.fadeInDuration);
      buf.writeFloatLE(this.fadeOutDuration);
      buf.writeByte(this.transitionType.getValue());
      buf.writeFloatLE(this.transitionDuration);
      buf.writeByte(this.playToCompletion ? 1 : 0);
      buf.writeIntLE(this.audioCategoryIndex);
      if (this.tempo != null) {
         this.tempo.serialize(buf);
      } else {
         buf.writeZero(12);
      }

      int nameTranslationKeyOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int childrenOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.nameTranslationKey != null) {
         buf.setIntLE(nameTranslationKeyOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.nameTranslationKey, 4096000);
      } else {
         buf.setIntLE(nameTranslationKeyOffsetSlot, -1);
      }

      if (this.children != null) {
         buf.setIntLE(childrenOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.children.length > 4096000) {
            throw ProtocolException.arrayTooLong("Children", this.children.length, 4096000);
         }

         VarInt.write(buf, this.children.length);

         for (int item : this.children) {
            buf.writeIntLE(item);
         }
      } else {
         buf.setIntLE(childrenOffsetSlot, -1);
      }

      return buf.writerIndex() - startPos;
   }

   @Override
   public int computeSize() {
      int size = 67;
      if (this.nameTranslationKey != null) {
         size += PacketIO.stringSize(this.nameTranslationKey);
      }

      if (this.children != null) {
         size += VarInt.size(this.children.length) + this.children.length * 4;
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 67) {
         return ValidationResult.error("Buffer too small: expected at least 67 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      int v = buffer.getByte(offset + 37) & 255;
      if (v >= 3) {
         return ValidationResult.error("Invalid MusicTransitionType value for TransitionType");
      }

      if ((nullBits & 8) != 0) {
         v = buffer.getIntLE(offset + 59);
         if (v < 0 || v > buffer.writerIndex() - offset - 67) {
            return ValidationResult.error("Invalid offset for NameTranslationKey");
         }

         int pos = offset + 67 + v;
         int nameTranslationKeyLen = VarInt.peek(buffer, pos);
         if (nameTranslationKeyLen < 0) {
            return ValidationResult.error("Invalid string length for NameTranslationKey");
         }

         if (nameTranslationKeyLen > 4096000) {
            return ValidationResult.error("NameTranslationKey exceeds max length 4096000");
         }

         pos += VarInt.size(nameTranslationKeyLen);
         pos += nameTranslationKeyLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading NameTranslationKey");
         }
      }

      if ((nullBits & 16) != 0) {
         v = buffer.getIntLE(offset + 63);
         if (v < 0 || v > buffer.writerIndex() - offset - 67) {
            return ValidationResult.error("Invalid offset for Children");
         }

         int pos = offset + 67 + v;
         int childrenCount = VarInt.peek(buffer, pos);
         if (childrenCount < 0) {
            return ValidationResult.error("Invalid array count for Children");
         }

         if (childrenCount > 4096000) {
            return ValidationResult.error("Children exceeds max length 4096000");
         }

         pos += VarInt.size(childrenCount);
         pos += childrenCount * 4;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Children");
         }
      }

      return ValidationResult.OK;
   }

   public SequenceMusicContainer clone() {
      SequenceMusicContainer copy = new SequenceMusicContainer();
      copy.volume = this.volume;
      copy.loopCount = this.loopCount;
      copy.weight = this.weight;
      copy.silenceAfter = this.silenceAfter != null ? this.silenceAfter.clone() : null;
      copy.exitSilence = this.exitSilence != null ? this.exitSilence.clone() : null;
      copy.fadeInDuration = this.fadeInDuration;
      copy.fadeOutDuration = this.fadeOutDuration;
      copy.transitionType = this.transitionType;
      copy.transitionDuration = this.transitionDuration;
      copy.playToCompletion = this.playToCompletion;
      copy.nameTranslationKey = this.nameTranslationKey;
      copy.audioCategoryIndex = this.audioCategoryIndex;
      copy.tempo = this.tempo != null ? this.tempo.clone() : null;
      copy.children = this.children != null ? Arrays.copyOf(this.children, this.children.length) : null;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof SequenceMusicContainer other)
            ? false
            : this.volume == other.volume
               && this.loopCount == other.loopCount
               && this.weight == other.weight
               && Objects.equals(this.silenceAfter, other.silenceAfter)
               && Objects.equals(this.exitSilence, other.exitSilence)
               && this.fadeInDuration == other.fadeInDuration
               && this.fadeOutDuration == other.fadeOutDuration
               && Objects.equals(this.transitionType, other.transitionType)
               && this.transitionDuration == other.transitionDuration
               && this.playToCompletion == other.playToCompletion
               && Objects.equals(this.nameTranslationKey, other.nameTranslationKey)
               && this.audioCategoryIndex == other.audioCategoryIndex
               && Objects.equals(this.tempo, other.tempo)
               && Arrays.equals(this.children, other.children);
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      result = 31 * result + Float.hashCode(this.volume);
      result = 31 * result + Integer.hashCode(this.loopCount);
      result = 31 * result + Float.hashCode(this.weight);
      result = 31 * result + Objects.hashCode(this.silenceAfter);
      result = 31 * result + Objects.hashCode(this.exitSilence);
      result = 31 * result + Float.hashCode(this.fadeInDuration);
      result = 31 * result + Float.hashCode(this.fadeOutDuration);
      result = 31 * result + Objects.hashCode(this.transitionType);
      result = 31 * result + Float.hashCode(this.transitionDuration);
      result = 31 * result + Boolean.hashCode(this.playToCompletion);
      result = 31 * result + Objects.hashCode(this.nameTranslationKey);
      result = 31 * result + Integer.hashCode(this.audioCategoryIndex);
      result = 31 * result + Objects.hashCode(this.tempo);
      return 31 * result + Arrays.hashCode(this.children);
   }
}
