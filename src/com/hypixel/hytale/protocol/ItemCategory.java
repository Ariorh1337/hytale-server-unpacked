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

public class ItemCategory {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 6;
   public static final int VARIABLE_FIELD_COUNT = 5;
   public static final int VARIABLE_BLOCK_START = 26;
   public static final int MAX_SIZE = 1677721600;
   @Nullable
   public String id;
   @Nullable
   public String name;
   @Nullable
   public String icon;
   public int order;
   @Nonnull
   public ItemGridInfoDisplayMode infoDisplayMode = ItemGridInfoDisplayMode.Tooltip;
   @Nullable
   public ItemCategory[] children;
   @Nullable
   public SubCategoryDefinition[] subCategories;

   public ItemCategory() {
   }

   public ItemCategory(
      @Nullable String id,
      @Nullable String name,
      @Nullable String icon,
      int order,
      @Nonnull ItemGridInfoDisplayMode infoDisplayMode,
      @Nullable ItemCategory[] children,
      @Nullable SubCategoryDefinition[] subCategories
   ) {
      this.id = id;
      this.name = name;
      this.icon = icon;
      this.order = order;
      this.infoDisplayMode = infoDisplayMode;
      this.children = children;
      this.subCategories = subCategories;
   }

   public ItemCategory(@Nonnull ItemCategory other) {
      this.id = other.id;
      this.name = other.name;
      this.icon = other.icon;
      this.order = other.order;
      this.infoDisplayMode = other.infoDisplayMode;
      this.children = other.children;
      this.subCategories = other.subCategories;
   }

   @Nonnull
   public static ItemCategory deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 26) {
         throw ProtocolException.bufferTooSmall("ItemCategory", 26, buf.readableBytes() - offset);
      }

      ItemCategory obj = new ItemCategory();
      byte nullBits = buf.getByte(offset);
      obj.order = buf.getIntLE(offset + 1);
      obj.infoDisplayMode = ItemGridInfoDisplayMode.fromValue(buf.getByte(offset + 5));
      if ((nullBits & 1) != 0) {
         int varPosBase0 = buf.getIntLE(offset + 6);
         if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 26) {
            throw ProtocolException.invalidOffset("Id", varPosBase0, buf.readableBytes());
         }

         int varPos0 = offset + 26 + varPosBase0;
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
         int varPosBase1 = buf.getIntLE(offset + 10);
         if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 26) {
            throw ProtocolException.invalidOffset("Name", varPosBase1, buf.readableBytes());
         }

         int varPos1 = offset + 26 + varPosBase1;
         int nameLen = VarInt.peek(buf, varPos1);
         if (nameLen < 0) {
            throw ProtocolException.invalidVarInt("Name");
         }

         int nameVarIntLen = VarInt.size(nameLen);
         if (nameLen > 4096000) {
            throw ProtocolException.stringTooLong("Name", nameLen, 4096000);
         }

         if (varPos1 + nameVarIntLen + nameLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Name", varPos1 + nameVarIntLen + nameLen, buf.readableBytes());
         }

         obj.name = PacketIO.readVarString(buf, varPos1, PacketIO.UTF8);
      }

      if ((nullBits & 4) != 0) {
         int varPosBase2 = buf.getIntLE(offset + 14);
         if (varPosBase2 < 0 || varPosBase2 > buf.writerIndex() - offset - 26) {
            throw ProtocolException.invalidOffset("Icon", varPosBase2, buf.readableBytes());
         }

         int varPos2 = offset + 26 + varPosBase2;
         int iconLen = VarInt.peek(buf, varPos2);
         if (iconLen < 0) {
            throw ProtocolException.invalidVarInt("Icon");
         }

         int iconVarIntLen = VarInt.size(iconLen);
         if (iconLen > 4096000) {
            throw ProtocolException.stringTooLong("Icon", iconLen, 4096000);
         }

         if (varPos2 + iconVarIntLen + iconLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Icon", varPos2 + iconVarIntLen + iconLen, buf.readableBytes());
         }

         obj.icon = PacketIO.readVarString(buf, varPos2, PacketIO.UTF8);
      }

      if ((nullBits & 8) != 0) {
         int varPosBase3 = buf.getIntLE(offset + 18);
         if (varPosBase3 < 0 || varPosBase3 > buf.writerIndex() - offset - 26) {
            throw ProtocolException.invalidOffset("Children", varPosBase3, buf.readableBytes());
         }

         int varPos3 = offset + 26 + varPosBase3;
         int childrenCount = VarInt.peek(buf, varPos3);
         if (childrenCount < 0) {
            throw ProtocolException.invalidVarInt("Children");
         }

         int varIntLen = VarInt.size(childrenCount);
         if (childrenCount > 4096000) {
            throw ProtocolException.arrayTooLong("Children", childrenCount, 4096000);
         }

         if (varPos3 + varIntLen + childrenCount * 6L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Children", varPos3 + varIntLen + childrenCount * 6, buf.readableBytes());
         }

         obj.children = new ItemCategory[childrenCount];
         int elemPos = varPos3 + varIntLen;

         for (int i = 0; i < childrenCount; i++) {
            obj.children[i] = deserialize(buf, elemPos);
            elemPos += computeBytesConsumed(buf, elemPos);
         }
      }

      if ((nullBits & 16) != 0) {
         int varPosBase4 = buf.getIntLE(offset + 22);
         if (varPosBase4 < 0 || varPosBase4 > buf.writerIndex() - offset - 26) {
            throw ProtocolException.invalidOffset("SubCategories", varPosBase4, buf.readableBytes());
         }

         int varPos4 = offset + 26 + varPosBase4;
         int subCategoriesCount = VarInt.peek(buf, varPos4);
         if (subCategoriesCount < 0) {
            throw ProtocolException.invalidVarInt("SubCategories");
         }

         int varIntLen = VarInt.size(subCategoriesCount);
         if (subCategoriesCount > 4096000) {
            throw ProtocolException.arrayTooLong("SubCategories", subCategoriesCount, 4096000);
         }

         if (varPos4 + varIntLen + subCategoriesCount * 5L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("SubCategories", varPos4 + varIntLen + subCategoriesCount * 5, buf.readableBytes());
         }

         obj.subCategories = new SubCategoryDefinition[subCategoriesCount];
         int elemPos = varPos4 + varIntLen;

         for (int i = 0; i < subCategoriesCount; i++) {
            obj.subCategories[i] = SubCategoryDefinition.deserialize(buf, elemPos);
            elemPos += SubCategoryDefinition.computeBytesConsumed(buf, elemPos);
         }
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 26;
      if ((nullBits & 1) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 6);
         if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 26) {
            throw ProtocolException.invalidOffset("Id", fieldOffset0, maxEnd);
         }

         int pos0 = offset + 26 + fieldOffset0;
         int sl = VarInt.peek(buf, pos0);
         pos0 += VarInt.size(sl) + sl;
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits & 2) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 10);
         if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 26) {
            throw ProtocolException.invalidOffset("Name", fieldOffset1, maxEnd);
         }

         int pos1 = offset + 26 + fieldOffset1;
         int sl = VarInt.peek(buf, pos1);
         pos1 += VarInt.size(sl) + sl;
         if (pos1 - offset > maxEnd) {
            maxEnd = pos1 - offset;
         }
      }

      if ((nullBits & 4) != 0) {
         int fieldOffset2 = buf.getIntLE(offset + 14);
         if (fieldOffset2 < 0 || fieldOffset2 > buf.writerIndex() - offset - 26) {
            throw ProtocolException.invalidOffset("Icon", fieldOffset2, maxEnd);
         }

         int pos2 = offset + 26 + fieldOffset2;
         int sl = VarInt.peek(buf, pos2);
         pos2 += VarInt.size(sl) + sl;
         if (pos2 - offset > maxEnd) {
            maxEnd = pos2 - offset;
         }
      }

      if ((nullBits & 8) != 0) {
         int fieldOffset3 = buf.getIntLE(offset + 18);
         if (fieldOffset3 < 0 || fieldOffset3 > buf.writerIndex() - offset - 26) {
            throw ProtocolException.invalidOffset("Children", fieldOffset3, maxEnd);
         }

         int pos3 = offset + 26 + fieldOffset3;
         int arrLen = VarInt.peek(buf, pos3);
         pos3 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            pos3 += computeBytesConsumed(buf, pos3);
         }

         if (pos3 - offset > maxEnd) {
            maxEnd = pos3 - offset;
         }
      }

      if ((nullBits & 16) != 0) {
         int fieldOffset4 = buf.getIntLE(offset + 22);
         if (fieldOffset4 < 0 || fieldOffset4 > buf.writerIndex() - offset - 26) {
            throw ProtocolException.invalidOffset("SubCategories", fieldOffset4, maxEnd);
         }

         int pos4 = offset + 26 + fieldOffset4;
         int arrLen = VarInt.peek(buf, pos4);
         pos4 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            pos4 += SubCategoryDefinition.computeBytesConsumed(buf, pos4);
         }

         if (pos4 - offset > maxEnd) {
            maxEnd = pos4 - offset;
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

      if (this.name != null) {
         nullBits = (byte)(nullBits | 2);
      }

      if (this.icon != null) {
         nullBits = (byte)(nullBits | 4);
      }

      if (this.children != null) {
         nullBits = (byte)(nullBits | 8);
      }

      if (this.subCategories != null) {
         nullBits = (byte)(nullBits | 16);
      }

      buf.writeByte(nullBits);
      buf.writeIntLE(this.order);
      buf.writeByte(this.infoDisplayMode.getValue());
      int idOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int nameOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int iconOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int childrenOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int subCategoriesOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.id != null) {
         buf.setIntLE(idOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.id, 4096000);
      } else {
         buf.setIntLE(idOffsetSlot, -1);
      }

      if (this.name != null) {
         buf.setIntLE(nameOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.name, 4096000);
      } else {
         buf.setIntLE(nameOffsetSlot, -1);
      }

      if (this.icon != null) {
         buf.setIntLE(iconOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.icon, 4096000);
      } else {
         buf.setIntLE(iconOffsetSlot, -1);
      }

      if (this.children != null) {
         buf.setIntLE(childrenOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.children.length > 4096000) {
            throw ProtocolException.arrayTooLong("Children", this.children.length, 4096000);
         }

         VarInt.write(buf, this.children.length);

         for (ItemCategory item : this.children) {
            item.serialize(buf);
         }
      } else {
         buf.setIntLE(childrenOffsetSlot, -1);
      }

      if (this.subCategories != null) {
         buf.setIntLE(subCategoriesOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.subCategories.length > 4096000) {
            throw ProtocolException.arrayTooLong("SubCategories", this.subCategories.length, 4096000);
         }

         VarInt.write(buf, this.subCategories.length);

         for (SubCategoryDefinition item : this.subCategories) {
            item.serialize(buf);
         }
      } else {
         buf.setIntLE(subCategoriesOffsetSlot, -1);
      }
   }

   public int computeSize() {
      int size = 26;
      if (this.id != null) {
         size += PacketIO.stringSize(this.id);
      }

      if (this.name != null) {
         size += PacketIO.stringSize(this.name);
      }

      if (this.icon != null) {
         size += PacketIO.stringSize(this.icon);
      }

      if (this.children != null) {
         int childrenSize = 0;

         for (ItemCategory elem : this.children) {
            childrenSize += elem.computeSize();
         }

         size += VarInt.size(this.children.length) + childrenSize;
      }

      if (this.subCategories != null) {
         int subCategoriesSize = 0;

         for (SubCategoryDefinition elem : this.subCategories) {
            subCategoriesSize += elem.computeSize();
         }

         size += VarInt.size(this.subCategories.length) + subCategoriesSize;
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 26) {
         return ValidationResult.error("Buffer too small: expected at least 26 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      int v = buffer.getByte(offset + 5) & 255;
      if (v >= 3) {
         return ValidationResult.error("Invalid ItemGridInfoDisplayMode value for InfoDisplayMode");
      }

      if ((nullBits & 1) != 0) {
         v = buffer.getIntLE(offset + 6);
         if (v < 0 || v > buffer.writerIndex() - offset - 26) {
            return ValidationResult.error("Invalid offset for Id");
         }

         int pos = offset + 26 + v;
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
         v = buffer.getIntLE(offset + 10);
         if (v < 0 || v > buffer.writerIndex() - offset - 26) {
            return ValidationResult.error("Invalid offset for Name");
         }

         int pos = offset + 26 + v;
         int nameLen = VarInt.peek(buffer, pos);
         if (nameLen < 0) {
            return ValidationResult.error("Invalid string length for Name");
         }

         if (nameLen > 4096000) {
            return ValidationResult.error("Name exceeds max length 4096000");
         }

         pos += VarInt.size(nameLen);
         pos += nameLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Name");
         }
      }

      if ((nullBits & 4) != 0) {
         v = buffer.getIntLE(offset + 14);
         if (v < 0 || v > buffer.writerIndex() - offset - 26) {
            return ValidationResult.error("Invalid offset for Icon");
         }

         int pos = offset + 26 + v;
         int iconLen = VarInt.peek(buffer, pos);
         if (iconLen < 0) {
            return ValidationResult.error("Invalid string length for Icon");
         }

         if (iconLen > 4096000) {
            return ValidationResult.error("Icon exceeds max length 4096000");
         }

         pos += VarInt.size(iconLen);
         pos += iconLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Icon");
         }
      }

      if ((nullBits & 8) != 0) {
         v = buffer.getIntLE(offset + 18);
         if (v < 0 || v > buffer.writerIndex() - offset - 26) {
            return ValidationResult.error("Invalid offset for Children");
         }

         int pos = offset + 26 + v;
         int childrenCount = VarInt.peek(buffer, pos);
         if (childrenCount < 0) {
            return ValidationResult.error("Invalid array count for Children");
         }

         if (childrenCount > 4096000) {
            return ValidationResult.error("Children exceeds max length 4096000");
         }

         pos += VarInt.size(childrenCount);

         for (int i = 0; i < childrenCount; i++) {
            ValidationResult structResult = validateStructure(buffer, pos);
            if (!structResult.isValid()) {
               return ValidationResult.error("Invalid ItemCategory in Children[" + i + "]: " + structResult.error());
            }

            pos += computeBytesConsumed(buffer, pos);
         }
      }

      if ((nullBits & 16) != 0) {
         v = buffer.getIntLE(offset + 22);
         if (v < 0 || v > buffer.writerIndex() - offset - 26) {
            return ValidationResult.error("Invalid offset for SubCategories");
         }

         int pos = offset + 26 + v;
         int subCategoriesCount = VarInt.peek(buffer, pos);
         if (subCategoriesCount < 0) {
            return ValidationResult.error("Invalid array count for SubCategories");
         }

         if (subCategoriesCount > 4096000) {
            return ValidationResult.error("SubCategories exceeds max length 4096000");
         }

         pos += VarInt.size(subCategoriesCount);

         for (int i = 0; i < subCategoriesCount; i++) {
            ValidationResult structResult = SubCategoryDefinition.validateStructure(buffer, pos);
            if (!structResult.isValid()) {
               return ValidationResult.error("Invalid SubCategoryDefinition in SubCategories[" + i + "]: " + structResult.error());
            }

            pos += SubCategoryDefinition.computeBytesConsumed(buffer, pos);
         }
      }

      return ValidationResult.OK;
   }

   public ItemCategory clone() {
      ItemCategory copy = new ItemCategory();
      copy.id = this.id;
      copy.name = this.name;
      copy.icon = this.icon;
      copy.order = this.order;
      copy.infoDisplayMode = this.infoDisplayMode;
      copy.children = this.children != null ? Arrays.stream(this.children).map(e -> e.clone()).toArray(ItemCategory[]::new) : null;
      copy.subCategories = this.subCategories != null ? Arrays.stream(this.subCategories).map(e -> e.clone()).toArray(SubCategoryDefinition[]::new) : null;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof ItemCategory other)
            ? false
            : Objects.equals(this.id, other.id)
               && Objects.equals(this.name, other.name)
               && Objects.equals(this.icon, other.icon)
               && this.order == other.order
               && Objects.equals(this.infoDisplayMode, other.infoDisplayMode)
               && Arrays.equals(this.children, other.children)
               && Arrays.equals(this.subCategories, other.subCategories);
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      result = 31 * result + Objects.hashCode(this.id);
      result = 31 * result + Objects.hashCode(this.name);
      result = 31 * result + Objects.hashCode(this.icon);
      result = 31 * result + Integer.hashCode(this.order);
      result = 31 * result + Objects.hashCode(this.infoDisplayMode);
      result = 31 * result + Arrays.hashCode(this.children);
      return 31 * result + Arrays.hashCode(this.subCategories);
   }
}
