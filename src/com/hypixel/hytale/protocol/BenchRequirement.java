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

public class BenchRequirement {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 6;
   public static final int VARIABLE_FIELD_COUNT = 2;
   public static final int VARIABLE_BLOCK_START = 14;
   public static final int MAX_SIZE = 1677721600;
   @Nonnull
   public BenchType type = BenchType.Crafting;
   @Nonnull
   public String id = "";
   @Nullable
   public String[] categories;
   public int requiredTierLevel;

   public BenchRequirement() {
   }

   public BenchRequirement(@Nonnull BenchType type, @Nonnull String id, @Nullable String[] categories, int requiredTierLevel) {
      this.type = type;
      this.id = id;
      this.categories = categories;
      this.requiredTierLevel = requiredTierLevel;
   }

   public BenchRequirement(@Nonnull BenchRequirement other) {
      this.type = other.type;
      this.id = other.id;
      this.categories = other.categories;
      this.requiredTierLevel = other.requiredTierLevel;
   }

   @Nonnull
   public static BenchRequirement deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 14) {
         throw ProtocolException.bufferTooSmall("BenchRequirement", 14, buf.readableBytes() - offset);
      }

      BenchRequirement obj = new BenchRequirement();
      byte nullBits = buf.getByte(offset);
      obj.type = BenchType.fromValue(buf.getByte(offset + 1));
      obj.requiredTierLevel = buf.getIntLE(offset + 2);
      int varPosBase0 = buf.getIntLE(offset + 6);
      if (varPosBase0 >= 0 && varPosBase0 <= buf.writerIndex() - offset - 14) {
         int varPos0 = offset + 14 + varPosBase0;
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
         if ((nullBits & 1) != 0) {
            varPosBase0 = buf.getIntLE(offset + 10);
            if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 14) {
               throw ProtocolException.invalidOffset("Categories", varPosBase0, buf.readableBytes());
            }

            varPos0 = offset + 14 + varPosBase0;
            idLen = VarInt.peek(buf, varPos0);
            if (idLen < 0) {
               throw ProtocolException.invalidVarInt("Categories");
            }

            idVarIntLen = VarInt.size(idLen);
            if (idLen > 4096000) {
               throw ProtocolException.arrayTooLong("Categories", idLen, 4096000);
            }

            if (varPos0 + idVarIntLen + idLen * 1L > buf.readableBytes()) {
               throw ProtocolException.bufferTooSmall("Categories", varPos0 + idVarIntLen + idLen * 1, buf.readableBytes());
            }

            obj.categories = new String[idLen];
            int elemPos = varPos0 + idVarIntLen;

            for (int i = 0; i < idLen; i++) {
               int strLen = VarInt.peek(buf, elemPos);
               if (strLen < 0) {
                  throw ProtocolException.invalidVarInt("categories[" + i + "]");
               }

               int strVarLen = VarInt.size(strLen);
               if (strLen > 4096000) {
                  throw ProtocolException.stringTooLong("categories[" + i + "]", strLen, 4096000);
               }

               if (elemPos + strVarLen + strLen > buf.readableBytes()) {
                  throw ProtocolException.bufferTooSmall("categories[" + i + "]", elemPos + strVarLen + strLen, buf.readableBytes());
               }

               obj.categories[i] = PacketIO.readVarString(buf, elemPos);
               elemPos += strVarLen + strLen;
            }
         }

         return obj;
      } else {
         throw ProtocolException.invalidOffset("Id", varPosBase0, buf.readableBytes());
      }
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 14;
      int fieldOffset0 = buf.getIntLE(offset + 6);
      if (fieldOffset0 >= 0 && fieldOffset0 <= buf.writerIndex() - offset - 14) {
         int pos0 = offset + 14 + fieldOffset0;
         int sl = VarInt.peek(buf, pos0);
         pos0 += VarInt.size(sl) + sl;
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }

         if ((nullBits & 1) != 0) {
            fieldOffset0 = buf.getIntLE(offset + 10);
            if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 14) {
               throw ProtocolException.invalidOffset("Categories", fieldOffset0, maxEnd);
            }

            pos0 = offset + 14 + fieldOffset0;
            sl = VarInt.peek(buf, pos0);
            pos0 += VarInt.size(sl);

            for (int i = 0; i < sl; i++) {
               int slx = VarInt.peek(buf, pos0);
               pos0 += VarInt.size(slx) + slx;
            }

            if (pos0 - offset > maxEnd) {
               maxEnd = pos0 - offset;
            }
         }

         return maxEnd;
      } else {
         throw ProtocolException.invalidOffset("Id", fieldOffset0, maxEnd);
      }
   }

   public void serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      byte nullBits = 0;
      if (this.categories != null) {
         nullBits = (byte)(nullBits | 1);
      }

      buf.writeByte(nullBits);
      buf.writeByte(this.type.getValue());
      buf.writeIntLE(this.requiredTierLevel);
      int idOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int categoriesOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      buf.setIntLE(idOffsetSlot, buf.writerIndex() - varBlockStart);
      PacketIO.writeVarString(buf, this.id, 4096000);
      if (this.categories != null) {
         buf.setIntLE(categoriesOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.categories.length > 4096000) {
            throw ProtocolException.arrayTooLong("Categories", this.categories.length, 4096000);
         }

         VarInt.write(buf, this.categories.length);

         for (String item : this.categories) {
            PacketIO.writeVarString(buf, item, 4096000);
         }
      } else {
         buf.setIntLE(categoriesOffsetSlot, -1);
      }
   }

   public int computeSize() {
      int size = 14;
      size += PacketIO.stringSize(this.id);
      if (this.categories != null) {
         int categoriesSize = 0;

         for (String elem : this.categories) {
            categoriesSize += PacketIO.stringSize(elem);
         }

         size += VarInt.size(this.categories.length) + categoriesSize;
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 14) {
         return ValidationResult.error("Buffer too small: expected at least 14 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      int v = buffer.getByte(offset + 1) & 255;
      if (v >= 4) {
         return ValidationResult.error("Invalid BenchType value for Type");
      }

      v = buffer.getIntLE(offset + 6);
      if (v >= 0 && v <= buffer.writerIndex() - offset - 14) {
         int pos = offset + 14 + v;
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

         if ((nullBits & 1) != 0) {
            v = buffer.getIntLE(offset + 10);
            if (v < 0 || v > buffer.writerIndex() - offset - 14) {
               return ValidationResult.error("Invalid offset for Categories");
            }

            pos = offset + 14 + v;
            idLen = VarInt.peek(buffer, pos);
            if (idLen < 0) {
               return ValidationResult.error("Invalid array count for Categories");
            }

            if (idLen > 4096000) {
               return ValidationResult.error("Categories exceeds max length 4096000");
            }

            pos += VarInt.size(idLen);

            for (int i = 0; i < idLen; i++) {
               int strLen = VarInt.peek(buffer, pos);
               if (strLen < 0) {
                  return ValidationResult.error("Invalid string length in Categories");
               }

               pos += VarInt.size(strLen);
               pos += strLen;
               if (pos > buffer.writerIndex()) {
                  return ValidationResult.error("Buffer overflow reading string in Categories");
               }
            }
         }

         return ValidationResult.OK;
      } else {
         return ValidationResult.error("Invalid offset for Id");
      }
   }

   public BenchRequirement clone() {
      BenchRequirement copy = new BenchRequirement();
      copy.type = this.type;
      copy.id = this.id;
      copy.categories = this.categories != null ? Arrays.copyOf(this.categories, this.categories.length) : null;
      copy.requiredTierLevel = this.requiredTierLevel;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof BenchRequirement other)
            ? false
            : Objects.equals(this.type, other.type)
               && Objects.equals(this.id, other.id)
               && Arrays.equals(this.categories, other.categories)
               && this.requiredTierLevel == other.requiredTierLevel;
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      result = 31 * result + Objects.hashCode(this.type);
      result = 31 * result + Objects.hashCode(this.id);
      result = 31 * result + Arrays.hashCode(this.categories);
      return 31 * result + Integer.hashCode(this.requiredTierLevel);
   }
}
