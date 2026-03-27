package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntityUpdate {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 5;
   public static final int VARIABLE_FIELD_COUNT = 2;
   public static final int VARIABLE_BLOCK_START = 13;
   public static final int MAX_SIZE = 1677721600;
   public int networkId;
   @Nullable
   public ComponentUpdateType[] removed;
   @Nullable
   public ComponentUpdate[] updates;

   public EntityUpdate() {
   }

   public EntityUpdate(int networkId, @Nullable ComponentUpdateType[] removed, @Nullable ComponentUpdate[] updates) {
      this.networkId = networkId;
      this.removed = removed;
      this.updates = updates;
   }

   public EntityUpdate(@Nonnull EntityUpdate other) {
      this.networkId = other.networkId;
      this.removed = other.removed;
      this.updates = other.updates;
   }

   @Nonnull
   public static EntityUpdate deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 13) {
         throw ProtocolException.bufferTooSmall("EntityUpdate", 13, buf.readableBytes() - offset);
      }

      EntityUpdate obj = new EntityUpdate();
      byte nullBits = buf.getByte(offset);
      obj.networkId = buf.getIntLE(offset + 1);
      if ((nullBits & 1) != 0) {
         int varPosBase0 = buf.getIntLE(offset + 5);
         if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 13) {
            throw ProtocolException.invalidOffset("Removed", varPosBase0, buf.readableBytes());
         }

         int varPos0 = offset + 13 + varPosBase0;
         int removedCount = VarInt.peek(buf, varPos0);
         if (removedCount < 0) {
            throw ProtocolException.invalidVarInt("Removed");
         }

         int varIntLen = VarInt.size(removedCount);
         if (removedCount > 4096000) {
            throw ProtocolException.arrayTooLong("Removed", removedCount, 4096000);
         }

         if (varPos0 + varIntLen + removedCount * 1L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Removed", varPos0 + varIntLen + removedCount * 1, buf.readableBytes());
         }

         obj.removed = new ComponentUpdateType[removedCount];
         int elemPos = varPos0 + varIntLen;

         for (int i = 0; i < removedCount; i++) {
            obj.removed[i] = ComponentUpdateType.fromValue(buf.getByte(elemPos));
            elemPos++;
         }
      }

      if ((nullBits & 2) != 0) {
         int varPosBase1 = buf.getIntLE(offset + 9);
         if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 13) {
            throw ProtocolException.invalidOffset("Updates", varPosBase1, buf.readableBytes());
         }

         int varPos1 = offset + 13 + varPosBase1;
         int updatesCount = VarInt.peek(buf, varPos1);
         if (updatesCount < 0) {
            throw ProtocolException.invalidVarInt("Updates");
         }

         int varIntLen = VarInt.size(updatesCount);
         if (updatesCount > 4096000) {
            throw ProtocolException.arrayTooLong("Updates", updatesCount, 4096000);
         }

         if (varPos1 + varIntLen + updatesCount * 1L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Updates", varPos1 + varIntLen + updatesCount * 1, buf.readableBytes());
         }

         obj.updates = new ComponentUpdate[updatesCount];
         int elemPos = varPos1 + varIntLen;

         for (int i = 0; i < updatesCount; i++) {
            obj.updates[i] = ComponentUpdate.deserialize(buf, elemPos);
            elemPos += ComponentUpdate.computeBytesConsumed(buf, elemPos);
         }
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 13;
      if ((nullBits & 1) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 5);
         if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 13) {
            throw ProtocolException.invalidOffset("Removed", fieldOffset0, maxEnd);
         }

         int pos0 = offset + 13 + fieldOffset0;
         int arrLen = VarInt.peek(buf, pos0);
         pos0 += VarInt.size(arrLen) + arrLen * 1;
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits & 2) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 9);
         if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 13) {
            throw ProtocolException.invalidOffset("Updates", fieldOffset1, maxEnd);
         }

         int pos1 = offset + 13 + fieldOffset1;
         int arrLen = VarInt.peek(buf, pos1);
         pos1 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            pos1 += ComponentUpdate.computeBytesConsumed(buf, pos1);
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
      if (this.removed != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.updates != null) {
         nullBits = (byte)(nullBits | 2);
      }

      buf.writeByte(nullBits);
      buf.writeIntLE(this.networkId);
      int removedOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int updatesOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.removed != null) {
         buf.setIntLE(removedOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.removed.length > 4096000) {
            throw ProtocolException.arrayTooLong("Removed", this.removed.length, 4096000);
         }

         VarInt.write(buf, this.removed.length);

         for (ComponentUpdateType item : this.removed) {
            buf.writeByte(item.getValue());
         }
      } else {
         buf.setIntLE(removedOffsetSlot, -1);
      }

      if (this.updates != null) {
         buf.setIntLE(updatesOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.updates.length > 4096000) {
            throw ProtocolException.arrayTooLong("Updates", this.updates.length, 4096000);
         }

         VarInt.write(buf, this.updates.length);

         for (ComponentUpdate item : this.updates) {
            item.serializeWithTypeId(buf);
         }
      } else {
         buf.setIntLE(updatesOffsetSlot, -1);
      }
   }

   public int computeSize() {
      int size = 13;
      if (this.removed != null) {
         size += VarInt.size(this.removed.length) + this.removed.length * 1;
      }

      if (this.updates != null) {
         int updatesSize = 0;

         for (ComponentUpdate elem : this.updates) {
            updatesSize += elem.computeSizeWithTypeId();
         }

         size += VarInt.size(this.updates.length) + updatesSize;
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 13) {
         return ValidationResult.error("Buffer too small: expected at least 13 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      if ((nullBits & 1) != 0) {
         int removedOffset = buffer.getIntLE(offset + 5);
         if (removedOffset < 0 || removedOffset > buffer.writerIndex() - offset - 13) {
            return ValidationResult.error("Invalid offset for Removed");
         }

         int pos = offset + 13 + removedOffset;
         int removedCount = VarInt.peek(buffer, pos);
         if (removedCount < 0) {
            return ValidationResult.error("Invalid array count for Removed");
         }

         if (removedCount > 4096000) {
            return ValidationResult.error("Removed exceeds max length 4096000");
         }

         pos += VarInt.size(removedCount);
         if (pos + removedCount * 1L > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Removed");
         }

         for (int i = 0; i < removedCount; i++) {
            int v = buffer.getByte(pos) & 255;
            if (v >= 26) {
               return ValidationResult.error("Invalid ComponentUpdateType value for Removed[i]");
            }

            pos++;
         }
      }

      if ((nullBits & 2) != 0) {
         int updatesOffset = buffer.getIntLE(offset + 9);
         if (updatesOffset < 0 || updatesOffset > buffer.writerIndex() - offset - 13) {
            return ValidationResult.error("Invalid offset for Updates");
         }

         int pos = offset + 13 + updatesOffset;
         int updatesCount = VarInt.peek(buffer, pos);
         if (updatesCount < 0) {
            return ValidationResult.error("Invalid array count for Updates");
         }

         if (updatesCount > 4096000) {
            return ValidationResult.error("Updates exceeds max length 4096000");
         }

         pos += VarInt.size(updatesCount);

         for (int i = 0; i < updatesCount; i++) {
            ValidationResult structResult = ComponentUpdate.validateStructure(buffer, pos);
            if (!structResult.isValid()) {
               return ValidationResult.error("Invalid ComponentUpdate in Updates[" + i + "]: " + structResult.error());
            }

            pos += ComponentUpdate.computeBytesConsumed(buffer, pos);
         }
      }

      return ValidationResult.OK;
   }

   public EntityUpdate clone() {
      EntityUpdate copy = new EntityUpdate();
      copy.networkId = this.networkId;
      copy.removed = this.removed != null ? Arrays.copyOf(this.removed, this.removed.length) : null;
      copy.updates = this.updates != null ? Arrays.copyOf(this.updates, this.updates.length) : null;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof EntityUpdate other)
            ? false
            : this.networkId == other.networkId && Arrays.equals(this.removed, other.removed) && Arrays.equals(this.updates, other.updates);
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      result = 31 * result + Integer.hashCode(this.networkId);
      result = 31 * result + Arrays.hashCode(this.removed);
      return 31 * result + Arrays.hashCode(this.updates);
   }
}
