package com.hypixel.hytale.protocol.packets.window;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class UpdateCategoryAction extends WindowAction {
   public static final int NULLABLE_BIT_FIELD_SIZE = 0;
   public static final int FIXED_BLOCK_SIZE = 0;
   public static final int VARIABLE_FIELD_COUNT = 2;
   public static final int VARIABLE_BLOCK_START = 8;
   public static final int MAX_SIZE = 32768018;
   @Nonnull
   public String category = "";
   @Nonnull
   public String itemCategory = "";

   public UpdateCategoryAction() {
   }

   public UpdateCategoryAction(@Nonnull String category, @Nonnull String itemCategory) {
      this.category = category;
      this.itemCategory = itemCategory;
   }

   public UpdateCategoryAction(@Nonnull UpdateCategoryAction other) {
      this.category = other.category;
      this.itemCategory = other.itemCategory;
   }

   @Nonnull
   public static UpdateCategoryAction deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 8) {
         throw ProtocolException.bufferTooSmall("UpdateCategoryAction", 8, buf.readableBytes() - offset);
      }

      UpdateCategoryAction obj = new UpdateCategoryAction();
      int varPosBase0 = buf.getIntLE(offset + 0);
      if (varPosBase0 >= 0 && varPosBase0 <= buf.writerIndex() - offset - 8) {
         int varPos0 = offset + 8 + varPosBase0;
         int categoryLen = VarInt.peek(buf, varPos0);
         if (categoryLen < 0) {
            throw ProtocolException.invalidVarInt("Category");
         }

         int categoryVarIntLen = VarInt.size(categoryLen);
         if (categoryLen > 4096000) {
            throw ProtocolException.stringTooLong("Category", categoryLen, 4096000);
         }

         if (varPos0 + categoryVarIntLen + categoryLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Category", varPos0 + categoryVarIntLen + categoryLen, buf.readableBytes());
         }

         obj.category = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
         varPosBase0 = buf.getIntLE(offset + 4);
         if (varPosBase0 >= 0 && varPosBase0 <= buf.writerIndex() - offset - 8) {
            varPos0 = offset + 8 + varPosBase0;
            categoryLen = VarInt.peek(buf, varPos0);
            if (categoryLen < 0) {
               throw ProtocolException.invalidVarInt("ItemCategory");
            }

            categoryVarIntLen = VarInt.size(categoryLen);
            if (categoryLen > 4096000) {
               throw ProtocolException.stringTooLong("ItemCategory", categoryLen, 4096000);
            }

            if (varPos0 + categoryVarIntLen + categoryLen > buf.readableBytes()) {
               throw ProtocolException.bufferTooSmall("ItemCategory", varPos0 + categoryVarIntLen + categoryLen, buf.readableBytes());
            }

            obj.itemCategory = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
            return obj;
         } else {
            throw ProtocolException.invalidOffset("ItemCategory", varPosBase0, buf.readableBytes());
         }
      } else {
         throw ProtocolException.invalidOffset("Category", varPosBase0, buf.readableBytes());
      }
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      int maxEnd = 8;
      int fieldOffset0 = buf.getIntLE(offset + 0);
      if (fieldOffset0 >= 0 && fieldOffset0 <= buf.writerIndex() - offset - 8) {
         int pos0 = offset + 8 + fieldOffset0;
         int sl = VarInt.peek(buf, pos0);
         pos0 += VarInt.size(sl) + sl;
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }

         fieldOffset0 = buf.getIntLE(offset + 4);
         if (fieldOffset0 >= 0 && fieldOffset0 <= buf.writerIndex() - offset - 8) {
            pos0 = offset + 8 + fieldOffset0;
            sl = VarInt.peek(buf, pos0);
            pos0 += VarInt.size(sl) + sl;
            if (pos0 - offset > maxEnd) {
               maxEnd = pos0 - offset;
            }

            return maxEnd;
         } else {
            throw ProtocolException.invalidOffset("ItemCategory", fieldOffset0, maxEnd);
         }
      } else {
         throw ProtocolException.invalidOffset("Category", fieldOffset0, maxEnd);
      }
   }

   @Override
   public int serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      int categoryOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int itemCategoryOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      buf.setIntLE(categoryOffsetSlot, buf.writerIndex() - varBlockStart);
      PacketIO.writeVarString(buf, this.category, 4096000);
      buf.setIntLE(itemCategoryOffsetSlot, buf.writerIndex() - varBlockStart);
      PacketIO.writeVarString(buf, this.itemCategory, 4096000);
      return buf.writerIndex() - startPos;
   }

   @Override
   public int computeSize() {
      int size = 8;
      size += PacketIO.stringSize(this.category);
      return size + PacketIO.stringSize(this.itemCategory);
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 8) {
         return ValidationResult.error("Buffer too small: expected at least 8 bytes");
      }

      int categoryOffset = buffer.getIntLE(offset + 0);
      if (categoryOffset >= 0 && categoryOffset <= buffer.writerIndex() - offset - 8) {
         int pos = offset + 8 + categoryOffset;
         int categoryLen = VarInt.peek(buffer, pos);
         if (categoryLen < 0) {
            return ValidationResult.error("Invalid string length for Category");
         }

         if (categoryLen > 4096000) {
            return ValidationResult.error("Category exceeds max length 4096000");
         }

         pos += VarInt.size(categoryLen);
         pos += categoryLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Category");
         }

         categoryOffset = buffer.getIntLE(offset + 4);
         if (categoryOffset >= 0 && categoryOffset <= buffer.writerIndex() - offset - 8) {
            pos = offset + 8 + categoryOffset;
            categoryLen = VarInt.peek(buffer, pos);
            if (categoryLen < 0) {
               return ValidationResult.error("Invalid string length for ItemCategory");
            }

            if (categoryLen > 4096000) {
               return ValidationResult.error("ItemCategory exceeds max length 4096000");
            }

            pos += VarInt.size(categoryLen);
            pos += categoryLen;
            return pos > buffer.writerIndex() ? ValidationResult.error("Buffer overflow reading ItemCategory") : ValidationResult.OK;
         } else {
            return ValidationResult.error("Invalid offset for ItemCategory");
         }
      } else {
         return ValidationResult.error("Invalid offset for Category");
      }
   }

   public UpdateCategoryAction clone() {
      UpdateCategoryAction copy = new UpdateCategoryAction();
      copy.category = this.category;
      copy.itemCategory = this.itemCategory;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof UpdateCategoryAction other)
            ? false
            : Objects.equals(this.category, other.category) && Objects.equals(this.itemCategory, other.itemCategory);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.category, this.itemCategory);
   }
}
