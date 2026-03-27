package com.hypixel.hytale.protocol.packets.buildertools;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderToolOptionArg {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 1;
   public static final int VARIABLE_FIELD_COUNT = 2;
   public static final int VARIABLE_BLOCK_START = 9;
   public static final int MAX_SIZE = 1677721600;
   @Nullable
   public String defaultValue;
   @Nullable
   public String[] options;

   public BuilderToolOptionArg() {
   }

   public BuilderToolOptionArg(@Nullable String defaultValue, @Nullable String[] options) {
      this.defaultValue = defaultValue;
      this.options = options;
   }

   public BuilderToolOptionArg(@Nonnull BuilderToolOptionArg other) {
      this.defaultValue = other.defaultValue;
      this.options = other.options;
   }

   @Nonnull
   public static BuilderToolOptionArg deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 9) {
         throw ProtocolException.bufferTooSmall("BuilderToolOptionArg", 9, buf.readableBytes() - offset);
      }

      BuilderToolOptionArg obj = new BuilderToolOptionArg();
      byte nullBits = buf.getByte(offset);
      if ((nullBits & 1) != 0) {
         int varPosBase0 = buf.getIntLE(offset + 1);
         if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 9) {
            throw ProtocolException.invalidOffset("Default", varPosBase0, buf.readableBytes());
         }

         int varPos0 = offset + 9 + varPosBase0;
         int defaultValueLen = VarInt.peek(buf, varPos0);
         if (defaultValueLen < 0) {
            throw ProtocolException.invalidVarInt("Default");
         }

         int defaultValueVarIntLen = VarInt.size(defaultValueLen);
         if (defaultValueLen > 4096000) {
            throw ProtocolException.stringTooLong("Default", defaultValueLen, 4096000);
         }

         if (varPos0 + defaultValueVarIntLen + defaultValueLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Default", varPos0 + defaultValueVarIntLen + defaultValueLen, buf.readableBytes());
         }

         obj.defaultValue = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
      }

      if ((nullBits & 2) != 0) {
         int varPosBase1 = buf.getIntLE(offset + 5);
         if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 9) {
            throw ProtocolException.invalidOffset("Options", varPosBase1, buf.readableBytes());
         }

         int varPos1 = offset + 9 + varPosBase1;
         int optionsCount = VarInt.peek(buf, varPos1);
         if (optionsCount < 0) {
            throw ProtocolException.invalidVarInt("Options");
         }

         int varIntLen = VarInt.size(optionsCount);
         if (optionsCount > 4096000) {
            throw ProtocolException.arrayTooLong("Options", optionsCount, 4096000);
         }

         if (varPos1 + varIntLen + optionsCount * 1L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Options", varPos1 + varIntLen + optionsCount * 1, buf.readableBytes());
         }

         obj.options = new String[optionsCount];
         int elemPos = varPos1 + varIntLen;

         for (int i = 0; i < optionsCount; i++) {
            int strLen = VarInt.peek(buf, elemPos);
            if (strLen < 0) {
               throw ProtocolException.invalidVarInt("options[" + i + "]");
            }

            int strVarLen = VarInt.size(strLen);
            if (strLen > 4096000) {
               throw ProtocolException.stringTooLong("options[" + i + "]", strLen, 4096000);
            }

            if (elemPos + strVarLen + strLen > buf.readableBytes()) {
               throw ProtocolException.bufferTooSmall("options[" + i + "]", elemPos + strVarLen + strLen, buf.readableBytes());
            }

            obj.options[i] = PacketIO.readVarString(buf, elemPos);
            elemPos += strVarLen + strLen;
         }
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 9;
      if ((nullBits & 1) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 1);
         if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 9) {
            throw ProtocolException.invalidOffset("Default", fieldOffset0, maxEnd);
         }

         int pos0 = offset + 9 + fieldOffset0;
         int sl = VarInt.peek(buf, pos0);
         pos0 += VarInt.size(sl) + sl;
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits & 2) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 5);
         if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 9) {
            throw ProtocolException.invalidOffset("Options", fieldOffset1, maxEnd);
         }

         int pos1 = offset + 9 + fieldOffset1;
         int arrLen = VarInt.peek(buf, pos1);
         pos1 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            int sl = VarInt.peek(buf, pos1);
            pos1 += VarInt.size(sl) + sl;
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
      if (this.defaultValue != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.options != null) {
         nullBits = (byte)(nullBits | 2);
      }

      buf.writeByte(nullBits);
      int defaultValueOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int optionsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.defaultValue != null) {
         buf.setIntLE(defaultValueOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.defaultValue, 4096000);
      } else {
         buf.setIntLE(defaultValueOffsetSlot, -1);
      }

      if (this.options != null) {
         buf.setIntLE(optionsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.options.length > 4096000) {
            throw ProtocolException.arrayTooLong("Options", this.options.length, 4096000);
         }

         VarInt.write(buf, this.options.length);

         for (String item : this.options) {
            PacketIO.writeVarString(buf, item, 4096000);
         }
      } else {
         buf.setIntLE(optionsOffsetSlot, -1);
      }
   }

   public int computeSize() {
      int size = 9;
      if (this.defaultValue != null) {
         size += PacketIO.stringSize(this.defaultValue);
      }

      if (this.options != null) {
         int optionsSize = 0;

         for (String elem : this.options) {
            optionsSize += PacketIO.stringSize(elem);
         }

         size += VarInt.size(this.options.length) + optionsSize;
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 9) {
         return ValidationResult.error("Buffer too small: expected at least 9 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      if ((nullBits & 1) != 0) {
         int defaultOffset = buffer.getIntLE(offset + 1);
         if (defaultOffset < 0 || defaultOffset > buffer.writerIndex() - offset - 9) {
            return ValidationResult.error("Invalid offset for Default");
         }

         int pos = offset + 9 + defaultOffset;
         int defaultLen = VarInt.peek(buffer, pos);
         if (defaultLen < 0) {
            return ValidationResult.error("Invalid string length for Default");
         }

         if (defaultLen > 4096000) {
            return ValidationResult.error("Default exceeds max length 4096000");
         }

         pos += VarInt.size(defaultLen);
         pos += defaultLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Default");
         }
      }

      if ((nullBits & 2) != 0) {
         int optionsOffset = buffer.getIntLE(offset + 5);
         if (optionsOffset < 0 || optionsOffset > buffer.writerIndex() - offset - 9) {
            return ValidationResult.error("Invalid offset for Options");
         }

         int pos = offset + 9 + optionsOffset;
         int optionsCount = VarInt.peek(buffer, pos);
         if (optionsCount < 0) {
            return ValidationResult.error("Invalid array count for Options");
         }

         if (optionsCount > 4096000) {
            return ValidationResult.error("Options exceeds max length 4096000");
         }

         pos += VarInt.size(optionsCount);

         for (int i = 0; i < optionsCount; i++) {
            int strLen = VarInt.peek(buffer, pos);
            if (strLen < 0) {
               return ValidationResult.error("Invalid string length in Options");
            }

            pos += VarInt.size(strLen);
            pos += strLen;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading string in Options");
            }
         }
      }

      return ValidationResult.OK;
   }

   public BuilderToolOptionArg clone() {
      BuilderToolOptionArg copy = new BuilderToolOptionArg();
      copy.defaultValue = this.defaultValue;
      copy.options = this.options != null ? Arrays.copyOf(this.options, this.options.length) : null;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof BuilderToolOptionArg other)
            ? false
            : Objects.equals(this.defaultValue, other.defaultValue) && Arrays.equals(this.options, other.options);
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      result = 31 * result + Objects.hashCode(this.defaultValue);
      return 31 * result + Arrays.hashCode(this.options);
   }
}
