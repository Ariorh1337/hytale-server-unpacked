package com.hypixel.hytale.protocol.packets.assets;

import com.hypixel.hytale.protocol.ItemBase;
import com.hypixel.hytale.protocol.NetworkChannel;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.ToClientPacket;
import com.hypixel.hytale.protocol.UpdateType;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UpdateItems implements Packet, ToClientPacket {
   public static final int PACKET_ID = 54;
   public static final boolean IS_COMPRESSED = true;
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 4;
   public static final int VARIABLE_FIELD_COUNT = 2;
   public static final int VARIABLE_BLOCK_START = 12;
   public static final int MAX_SIZE = 1677721600;
   @Nonnull
   public UpdateType type = UpdateType.Init;
   @Nullable
   public Map<String, ItemBase> items;
   @Nullable
   public String[] removedItems;
   public boolean updateModels;
   public boolean updateIcons;

   @Override
   public int getId() {
      return 54;
   }

   @Override
   public NetworkChannel getChannel() {
      return NetworkChannel.Default;
   }

   public UpdateItems() {
   }

   public UpdateItems(
      @Nonnull UpdateType type, @Nullable Map<String, ItemBase> items, @Nullable String[] removedItems, boolean updateModels, boolean updateIcons
   ) {
      this.type = type;
      this.items = items;
      this.removedItems = removedItems;
      this.updateModels = updateModels;
      this.updateIcons = updateIcons;
   }

   public UpdateItems(@Nonnull UpdateItems other) {
      this.type = other.type;
      this.items = other.items;
      this.removedItems = other.removedItems;
      this.updateModels = other.updateModels;
      this.updateIcons = other.updateIcons;
   }

   @Nonnull
   public static UpdateItems deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 12) {
         throw ProtocolException.bufferTooSmall("UpdateItems", 12, buf.readableBytes() - offset);
      }

      UpdateItems obj = new UpdateItems();
      byte nullBits = buf.getByte(offset);
      obj.type = UpdateType.fromValue(buf.getByte(offset + 1));
      obj.updateModels = buf.getByte(offset + 2) != 0;
      obj.updateIcons = buf.getByte(offset + 3) != 0;
      if ((nullBits & 1) != 0) {
         int varPosBase0 = buf.getIntLE(offset + 4);
         if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 12) {
            throw ProtocolException.invalidOffset("Items", varPosBase0, buf.readableBytes());
         }

         int varPos0 = offset + 12 + varPosBase0;
         int itemsCount = VarInt.peek(buf, varPos0);
         if (itemsCount < 0) {
            throw ProtocolException.invalidVarInt("Items");
         }

         int varIntLen = VarInt.size(itemsCount);
         if (itemsCount > 4096000) {
            throw ProtocolException.dictionaryTooLarge("Items", itemsCount, 4096000);
         }

         obj.items = new HashMap<>(itemsCount);
         int dictPos = varPos0 + varIntLen;

         for (int i = 0; i < itemsCount; i++) {
            int keyLen = VarInt.peek(buf, dictPos);
            if (keyLen < 0) {
               throw ProtocolException.invalidVarInt("key");
            }

            int keyVarLen = VarInt.size(keyLen);
            if (keyLen > 4096000) {
               throw ProtocolException.stringTooLong("key", keyLen, 4096000);
            }

            if (dictPos + keyVarLen + keyLen > buf.readableBytes()) {
               throw ProtocolException.bufferTooSmall("key", dictPos + keyVarLen + keyLen, buf.readableBytes());
            }

            String key = PacketIO.readVarString(buf, dictPos);
            dictPos += keyVarLen + keyLen;
            ItemBase val = ItemBase.deserialize(buf, dictPos);
            dictPos += ItemBase.computeBytesConsumed(buf, dictPos);
            if (obj.items.put(key, val) != null) {
               throw ProtocolException.duplicateKey("items", key);
            }
         }
      }

      if ((nullBits & 2) != 0) {
         int varPosBase1 = buf.getIntLE(offset + 8);
         if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 12) {
            throw ProtocolException.invalidOffset("RemovedItems", varPosBase1, buf.readableBytes());
         }

         int varPos1 = offset + 12 + varPosBase1;
         int removedItemsCount = VarInt.peek(buf, varPos1);
         if (removedItemsCount < 0) {
            throw ProtocolException.invalidVarInt("RemovedItems");
         }

         int varIntLen = VarInt.size(removedItemsCount);
         if (removedItemsCount > 4096000) {
            throw ProtocolException.arrayTooLong("RemovedItems", removedItemsCount, 4096000);
         }

         if (varPos1 + varIntLen + removedItemsCount * 1L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("RemovedItems", varPos1 + varIntLen + removedItemsCount * 1, buf.readableBytes());
         }

         obj.removedItems = new String[removedItemsCount];
         int elemPos = varPos1 + varIntLen;

         for (int i = 0; i < removedItemsCount; i++) {
            int strLen = VarInt.peek(buf, elemPos);
            if (strLen < 0) {
               throw ProtocolException.invalidVarInt("removedItems[" + i + "]");
            }

            int strVarLen = VarInt.size(strLen);
            if (strLen > 4096000) {
               throw ProtocolException.stringTooLong("removedItems[" + i + "]", strLen, 4096000);
            }

            if (elemPos + strVarLen + strLen > buf.readableBytes()) {
               throw ProtocolException.bufferTooSmall("removedItems[" + i + "]", elemPos + strVarLen + strLen, buf.readableBytes());
            }

            obj.removedItems[i] = PacketIO.readVarString(buf, elemPos);
            elemPos += strVarLen + strLen;
         }
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 12;
      if ((nullBits & 1) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 4);
         if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 12) {
            throw ProtocolException.invalidOffset("Items", fieldOffset0, maxEnd);
         }

         int pos0 = offset + 12 + fieldOffset0;
         int dictLen = VarInt.peek(buf, pos0);
         pos0 += VarInt.size(dictLen);

         for (int i = 0; i < dictLen; i++) {
            int sl = VarInt.peek(buf, pos0);
            pos0 += VarInt.size(sl) + sl;
            pos0 += ItemBase.computeBytesConsumed(buf, pos0);
         }

         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits & 2) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 8);
         if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 12) {
            throw ProtocolException.invalidOffset("RemovedItems", fieldOffset1, maxEnd);
         }

         int pos1 = offset + 12 + fieldOffset1;
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

   @Override
   public void serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      byte nullBits = 0;
      if (this.items != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.removedItems != null) {
         nullBits = (byte)(nullBits | 2);
      }

      buf.writeByte(nullBits);
      buf.writeByte(this.type.getValue());
      buf.writeByte(this.updateModels ? 1 : 0);
      buf.writeByte(this.updateIcons ? 1 : 0);
      int itemsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int removedItemsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.items != null) {
         buf.setIntLE(itemsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.items.size() > 4096000) {
            throw ProtocolException.dictionaryTooLarge("Items", this.items.size(), 4096000);
         }

         VarInt.write(buf, this.items.size());

         for (Entry<String, ItemBase> e : this.items.entrySet()) {
            PacketIO.writeVarString(buf, e.getKey(), 4096000);
            e.getValue().serialize(buf);
         }
      } else {
         buf.setIntLE(itemsOffsetSlot, -1);
      }

      if (this.removedItems != null) {
         buf.setIntLE(removedItemsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.removedItems.length > 4096000) {
            throw ProtocolException.arrayTooLong("RemovedItems", this.removedItems.length, 4096000);
         }

         VarInt.write(buf, this.removedItems.length);

         for (String item : this.removedItems) {
            PacketIO.writeVarString(buf, item, 4096000);
         }
      } else {
         buf.setIntLE(removedItemsOffsetSlot, -1);
      }
   }

   @Override
   public int computeSize() {
      int size = 12;
      if (this.items != null) {
         int itemsSize = 0;

         for (Entry<String, ItemBase> kvp : this.items.entrySet()) {
            itemsSize += PacketIO.stringSize(kvp.getKey()) + kvp.getValue().computeSize();
         }

         size += VarInt.size(this.items.size()) + itemsSize;
      }

      if (this.removedItems != null) {
         int removedItemsSize = 0;

         for (String elem : this.removedItems) {
            removedItemsSize += PacketIO.stringSize(elem);
         }

         size += VarInt.size(this.removedItems.length) + removedItemsSize;
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 12) {
         return ValidationResult.error("Buffer too small: expected at least 12 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      int v = buffer.getByte(offset + 1) & 255;
      if (v >= 3) {
         return ValidationResult.error("Invalid UpdateType value for Type");
      }

      if ((nullBits & 1) != 0) {
         v = buffer.getIntLE(offset + 4);
         if (v < 0 || v > buffer.writerIndex() - offset - 12) {
            return ValidationResult.error("Invalid offset for Items");
         }

         int pos = offset + 12 + v;
         int itemsCount = VarInt.peek(buffer, pos);
         if (itemsCount < 0) {
            return ValidationResult.error("Invalid dictionary count for Items");
         }

         if (itemsCount > 4096000) {
            return ValidationResult.error("Items exceeds max length 4096000");
         }

         pos += VarInt.size(itemsCount);

         for (int i = 0; i < itemsCount; i++) {
            int keyLen = VarInt.peek(buffer, pos);
            if (keyLen < 0) {
               return ValidationResult.error("Invalid string length for key");
            }

            if (keyLen > 4096000) {
               return ValidationResult.error("key exceeds max length 4096000");
            }

            pos += VarInt.size(keyLen);
            pos += keyLen;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading key");
            }

            pos += ItemBase.computeBytesConsumed(buffer, pos);
         }
      }

      if ((nullBits & 2) != 0) {
         v = buffer.getIntLE(offset + 8);
         if (v < 0 || v > buffer.writerIndex() - offset - 12) {
            return ValidationResult.error("Invalid offset for RemovedItems");
         }

         int pos = offset + 12 + v;
         int removedItemsCount = VarInt.peek(buffer, pos);
         if (removedItemsCount < 0) {
            return ValidationResult.error("Invalid array count for RemovedItems");
         }

         if (removedItemsCount > 4096000) {
            return ValidationResult.error("RemovedItems exceeds max length 4096000");
         }

         pos += VarInt.size(removedItemsCount);

         for (int i = 0; i < removedItemsCount; i++) {
            int strLen = VarInt.peek(buffer, pos);
            if (strLen < 0) {
               return ValidationResult.error("Invalid string length in RemovedItems");
            }

            pos += VarInt.size(strLen);
            pos += strLen;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading string in RemovedItems");
            }
         }
      }

      return ValidationResult.OK;
   }

   public UpdateItems clone() {
      UpdateItems copy = new UpdateItems();
      copy.type = this.type;
      if (this.items != null) {
         Map<String, ItemBase> m = new HashMap<>();

         for (Entry<String, ItemBase> e : this.items.entrySet()) {
            m.put(e.getKey(), e.getValue().clone());
         }

         copy.items = m;
      }

      copy.removedItems = this.removedItems != null ? Arrays.copyOf(this.removedItems, this.removedItems.length) : null;
      copy.updateModels = this.updateModels;
      copy.updateIcons = this.updateIcons;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof UpdateItems other)
            ? false
            : Objects.equals(this.type, other.type)
               && Objects.equals(this.items, other.items)
               && Arrays.equals(this.removedItems, other.removedItems)
               && this.updateModels == other.updateModels
               && this.updateIcons == other.updateIcons;
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      result = 31 * result + Objects.hashCode(this.type);
      result = 31 * result + Objects.hashCode(this.items);
      result = 31 * result + Arrays.hashCode(this.removedItems);
      result = 31 * result + Boolean.hashCode(this.updateModels);
      return 31 * result + Boolean.hashCode(this.updateIcons);
   }
}
