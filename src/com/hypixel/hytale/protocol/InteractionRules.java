package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InteractionRules {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 17;
   public static final int VARIABLE_FIELD_COUNT = 4;
   public static final int VARIABLE_BLOCK_START = 33;
   public static final int MAX_SIZE = 16384053;
   @Nullable
   public InteractionType[] blockedBy;
   @Nullable
   public InteractionType[] blocking;
   @Nullable
   public InteractionType[] interruptedBy;
   @Nullable
   public InteractionType[] interrupting;
   public int blockedByBypassIndex;
   public int blockingBypassIndex;
   public int interruptedByBypassIndex;
   public int interruptingBypassIndex;

   public InteractionRules() {
   }

   public InteractionRules(
      @Nullable InteractionType[] blockedBy,
      @Nullable InteractionType[] blocking,
      @Nullable InteractionType[] interruptedBy,
      @Nullable InteractionType[] interrupting,
      int blockedByBypassIndex,
      int blockingBypassIndex,
      int interruptedByBypassIndex,
      int interruptingBypassIndex
   ) {
      this.blockedBy = blockedBy;
      this.blocking = blocking;
      this.interruptedBy = interruptedBy;
      this.interrupting = interrupting;
      this.blockedByBypassIndex = blockedByBypassIndex;
      this.blockingBypassIndex = blockingBypassIndex;
      this.interruptedByBypassIndex = interruptedByBypassIndex;
      this.interruptingBypassIndex = interruptingBypassIndex;
   }

   public InteractionRules(@Nonnull InteractionRules other) {
      this.blockedBy = other.blockedBy;
      this.blocking = other.blocking;
      this.interruptedBy = other.interruptedBy;
      this.interrupting = other.interrupting;
      this.blockedByBypassIndex = other.blockedByBypassIndex;
      this.blockingBypassIndex = other.blockingBypassIndex;
      this.interruptedByBypassIndex = other.interruptedByBypassIndex;
      this.interruptingBypassIndex = other.interruptingBypassIndex;
   }

   @Nonnull
   public static InteractionRules deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 33) {
         throw ProtocolException.bufferTooSmall("InteractionRules", 33, buf.readableBytes() - offset);
      }

      InteractionRules obj = new InteractionRules();
      byte nullBits = buf.getByte(offset);
      obj.blockedByBypassIndex = buf.getIntLE(offset + 1);
      obj.blockingBypassIndex = buf.getIntLE(offset + 5);
      obj.interruptedByBypassIndex = buf.getIntLE(offset + 9);
      obj.interruptingBypassIndex = buf.getIntLE(offset + 13);
      if ((nullBits & 1) != 0) {
         int varPosBase0 = buf.getIntLE(offset + 17);
         if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 33) {
            throw ProtocolException.invalidOffset("BlockedBy", varPosBase0, buf.readableBytes());
         }

         int varPos0 = offset + 33 + varPosBase0;
         int blockedByCount = VarInt.peek(buf, varPos0);
         if (blockedByCount < 0) {
            throw ProtocolException.invalidVarInt("BlockedBy");
         }

         int varIntLen = VarInt.size(blockedByCount);
         if (blockedByCount > 4096000) {
            throw ProtocolException.arrayTooLong("BlockedBy", blockedByCount, 4096000);
         }

         if (varPos0 + varIntLen + blockedByCount * 1L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("BlockedBy", varPos0 + varIntLen + blockedByCount * 1, buf.readableBytes());
         }

         obj.blockedBy = new InteractionType[blockedByCount];
         int elemPos = varPos0 + varIntLen;

         for (int i = 0; i < blockedByCount; i++) {
            obj.blockedBy[i] = InteractionType.fromValue(buf.getByte(elemPos));
            elemPos++;
         }
      }

      if ((nullBits & 2) != 0) {
         int varPosBase1 = buf.getIntLE(offset + 21);
         if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 33) {
            throw ProtocolException.invalidOffset("Blocking", varPosBase1, buf.readableBytes());
         }

         int varPos1 = offset + 33 + varPosBase1;
         int blockingCount = VarInt.peek(buf, varPos1);
         if (blockingCount < 0) {
            throw ProtocolException.invalidVarInt("Blocking");
         }

         int varIntLen = VarInt.size(blockingCount);
         if (blockingCount > 4096000) {
            throw ProtocolException.arrayTooLong("Blocking", blockingCount, 4096000);
         }

         if (varPos1 + varIntLen + blockingCount * 1L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Blocking", varPos1 + varIntLen + blockingCount * 1, buf.readableBytes());
         }

         obj.blocking = new InteractionType[blockingCount];
         int elemPos = varPos1 + varIntLen;

         for (int i = 0; i < blockingCount; i++) {
            obj.blocking[i] = InteractionType.fromValue(buf.getByte(elemPos));
            elemPos++;
         }
      }

      if ((nullBits & 4) != 0) {
         int varPosBase2 = buf.getIntLE(offset + 25);
         if (varPosBase2 < 0 || varPosBase2 > buf.writerIndex() - offset - 33) {
            throw ProtocolException.invalidOffset("InterruptedBy", varPosBase2, buf.readableBytes());
         }

         int varPos2 = offset + 33 + varPosBase2;
         int interruptedByCount = VarInt.peek(buf, varPos2);
         if (interruptedByCount < 0) {
            throw ProtocolException.invalidVarInt("InterruptedBy");
         }

         int varIntLen = VarInt.size(interruptedByCount);
         if (interruptedByCount > 4096000) {
            throw ProtocolException.arrayTooLong("InterruptedBy", interruptedByCount, 4096000);
         }

         if (varPos2 + varIntLen + interruptedByCount * 1L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("InterruptedBy", varPos2 + varIntLen + interruptedByCount * 1, buf.readableBytes());
         }

         obj.interruptedBy = new InteractionType[interruptedByCount];
         int elemPos = varPos2 + varIntLen;

         for (int i = 0; i < interruptedByCount; i++) {
            obj.interruptedBy[i] = InteractionType.fromValue(buf.getByte(elemPos));
            elemPos++;
         }
      }

      if ((nullBits & 8) != 0) {
         int varPosBase3 = buf.getIntLE(offset + 29);
         if (varPosBase3 < 0 || varPosBase3 > buf.writerIndex() - offset - 33) {
            throw ProtocolException.invalidOffset("Interrupting", varPosBase3, buf.readableBytes());
         }

         int varPos3 = offset + 33 + varPosBase3;
         int interruptingCount = VarInt.peek(buf, varPos3);
         if (interruptingCount < 0) {
            throw ProtocolException.invalidVarInt("Interrupting");
         }

         int varIntLen = VarInt.size(interruptingCount);
         if (interruptingCount > 4096000) {
            throw ProtocolException.arrayTooLong("Interrupting", interruptingCount, 4096000);
         }

         if (varPos3 + varIntLen + interruptingCount * 1L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Interrupting", varPos3 + varIntLen + interruptingCount * 1, buf.readableBytes());
         }

         obj.interrupting = new InteractionType[interruptingCount];
         int elemPos = varPos3 + varIntLen;

         for (int i = 0; i < interruptingCount; i++) {
            obj.interrupting[i] = InteractionType.fromValue(buf.getByte(elemPos));
            elemPos++;
         }
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 33;
      if ((nullBits & 1) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 17);
         if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 33) {
            throw ProtocolException.invalidOffset("BlockedBy", fieldOffset0, maxEnd);
         }

         int pos0 = offset + 33 + fieldOffset0;
         int arrLen = VarInt.peek(buf, pos0);
         pos0 += VarInt.size(arrLen) + arrLen * 1;
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits & 2) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 21);
         if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 33) {
            throw ProtocolException.invalidOffset("Blocking", fieldOffset1, maxEnd);
         }

         int pos1 = offset + 33 + fieldOffset1;
         int arrLen = VarInt.peek(buf, pos1);
         pos1 += VarInt.size(arrLen) + arrLen * 1;
         if (pos1 - offset > maxEnd) {
            maxEnd = pos1 - offset;
         }
      }

      if ((nullBits & 4) != 0) {
         int fieldOffset2 = buf.getIntLE(offset + 25);
         if (fieldOffset2 < 0 || fieldOffset2 > buf.writerIndex() - offset - 33) {
            throw ProtocolException.invalidOffset("InterruptedBy", fieldOffset2, maxEnd);
         }

         int pos2 = offset + 33 + fieldOffset2;
         int arrLen = VarInt.peek(buf, pos2);
         pos2 += VarInt.size(arrLen) + arrLen * 1;
         if (pos2 - offset > maxEnd) {
            maxEnd = pos2 - offset;
         }
      }

      if ((nullBits & 8) != 0) {
         int fieldOffset3 = buf.getIntLE(offset + 29);
         if (fieldOffset3 < 0 || fieldOffset3 > buf.writerIndex() - offset - 33) {
            throw ProtocolException.invalidOffset("Interrupting", fieldOffset3, maxEnd);
         }

         int pos3 = offset + 33 + fieldOffset3;
         int arrLen = VarInt.peek(buf, pos3);
         pos3 += VarInt.size(arrLen) + arrLen * 1;
         if (pos3 - offset > maxEnd) {
            maxEnd = pos3 - offset;
         }
      }

      return maxEnd;
   }

   public void serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      byte nullBits = 0;
      if (this.blockedBy != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.blocking != null) {
         nullBits = (byte)(nullBits | 2);
      }

      if (this.interruptedBy != null) {
         nullBits = (byte)(nullBits | 4);
      }

      if (this.interrupting != null) {
         nullBits = (byte)(nullBits | 8);
      }

      buf.writeByte(nullBits);
      buf.writeIntLE(this.blockedByBypassIndex);
      buf.writeIntLE(this.blockingBypassIndex);
      buf.writeIntLE(this.interruptedByBypassIndex);
      buf.writeIntLE(this.interruptingBypassIndex);
      int blockedByOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int blockingOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int interruptedByOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int interruptingOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.blockedBy != null) {
         buf.setIntLE(blockedByOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.blockedBy.length > 4096000) {
            throw ProtocolException.arrayTooLong("BlockedBy", this.blockedBy.length, 4096000);
         }

         VarInt.write(buf, this.blockedBy.length);

         for (InteractionType item : this.blockedBy) {
            buf.writeByte(item.getValue());
         }
      } else {
         buf.setIntLE(blockedByOffsetSlot, -1);
      }

      if (this.blocking != null) {
         buf.setIntLE(blockingOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.blocking.length > 4096000) {
            throw ProtocolException.arrayTooLong("Blocking", this.blocking.length, 4096000);
         }

         VarInt.write(buf, this.blocking.length);

         for (InteractionType item : this.blocking) {
            buf.writeByte(item.getValue());
         }
      } else {
         buf.setIntLE(blockingOffsetSlot, -1);
      }

      if (this.interruptedBy != null) {
         buf.setIntLE(interruptedByOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.interruptedBy.length > 4096000) {
            throw ProtocolException.arrayTooLong("InterruptedBy", this.interruptedBy.length, 4096000);
         }

         VarInt.write(buf, this.interruptedBy.length);

         for (InteractionType item : this.interruptedBy) {
            buf.writeByte(item.getValue());
         }
      } else {
         buf.setIntLE(interruptedByOffsetSlot, -1);
      }

      if (this.interrupting != null) {
         buf.setIntLE(interruptingOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.interrupting.length > 4096000) {
            throw ProtocolException.arrayTooLong("Interrupting", this.interrupting.length, 4096000);
         }

         VarInt.write(buf, this.interrupting.length);

         for (InteractionType item : this.interrupting) {
            buf.writeByte(item.getValue());
         }
      } else {
         buf.setIntLE(interruptingOffsetSlot, -1);
      }
   }

   public int computeSize() {
      int size = 33;
      if (this.blockedBy != null) {
         size += VarInt.size(this.blockedBy.length) + this.blockedBy.length * 1;
      }

      if (this.blocking != null) {
         size += VarInt.size(this.blocking.length) + this.blocking.length * 1;
      }

      if (this.interruptedBy != null) {
         size += VarInt.size(this.interruptedBy.length) + this.interruptedBy.length * 1;
      }

      if (this.interrupting != null) {
         size += VarInt.size(this.interrupting.length) + this.interrupting.length * 1;
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 33) {
         return ValidationResult.error("Buffer too small: expected at least 33 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      if ((nullBits & 1) != 0) {
         int blockedByOffset = buffer.getIntLE(offset + 17);
         if (blockedByOffset < 0 || blockedByOffset > buffer.writerIndex() - offset - 33) {
            return ValidationResult.error("Invalid offset for BlockedBy");
         }

         int pos = offset + 33 + blockedByOffset;
         int blockedByCount = VarInt.peek(buffer, pos);
         if (blockedByCount < 0) {
            return ValidationResult.error("Invalid array count for BlockedBy");
         }

         if (blockedByCount > 4096000) {
            return ValidationResult.error("BlockedBy exceeds max length 4096000");
         }

         pos += VarInt.size(blockedByCount);
         if (pos + blockedByCount * 1L > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading BlockedBy");
         }

         for (int i = 0; i < blockedByCount; i++) {
            int v = buffer.getByte(pos) & 255;
            if (v >= 25) {
               return ValidationResult.error("Invalid InteractionType value for BlockedBy[i]");
            }

            pos++;
         }
      }

      if ((nullBits & 2) != 0) {
         int blockingOffset = buffer.getIntLE(offset + 21);
         if (blockingOffset < 0 || blockingOffset > buffer.writerIndex() - offset - 33) {
            return ValidationResult.error("Invalid offset for Blocking");
         }

         int pos = offset + 33 + blockingOffset;
         int blockingCount = VarInt.peek(buffer, pos);
         if (blockingCount < 0) {
            return ValidationResult.error("Invalid array count for Blocking");
         }

         if (blockingCount > 4096000) {
            return ValidationResult.error("Blocking exceeds max length 4096000");
         }

         pos += VarInt.size(blockingCount);
         if (pos + blockingCount * 1L > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Blocking");
         }

         for (int i = 0; i < blockingCount; i++) {
            int v = buffer.getByte(pos) & 255;
            if (v >= 25) {
               return ValidationResult.error("Invalid InteractionType value for Blocking[i]");
            }

            pos++;
         }
      }

      if ((nullBits & 4) != 0) {
         int interruptedByOffset = buffer.getIntLE(offset + 25);
         if (interruptedByOffset < 0 || interruptedByOffset > buffer.writerIndex() - offset - 33) {
            return ValidationResult.error("Invalid offset for InterruptedBy");
         }

         int pos = offset + 33 + interruptedByOffset;
         int interruptedByCount = VarInt.peek(buffer, pos);
         if (interruptedByCount < 0) {
            return ValidationResult.error("Invalid array count for InterruptedBy");
         }

         if (interruptedByCount > 4096000) {
            return ValidationResult.error("InterruptedBy exceeds max length 4096000");
         }

         pos += VarInt.size(interruptedByCount);
         if (pos + interruptedByCount * 1L > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading InterruptedBy");
         }

         for (int i = 0; i < interruptedByCount; i++) {
            int v = buffer.getByte(pos) & 255;
            if (v >= 25) {
               return ValidationResult.error("Invalid InteractionType value for InterruptedBy[i]");
            }

            pos++;
         }
      }

      if ((nullBits & 8) != 0) {
         int interruptingOffset = buffer.getIntLE(offset + 29);
         if (interruptingOffset < 0 || interruptingOffset > buffer.writerIndex() - offset - 33) {
            return ValidationResult.error("Invalid offset for Interrupting");
         }

         int pos = offset + 33 + interruptingOffset;
         int interruptingCount = VarInt.peek(buffer, pos);
         if (interruptingCount < 0) {
            return ValidationResult.error("Invalid array count for Interrupting");
         }

         if (interruptingCount > 4096000) {
            return ValidationResult.error("Interrupting exceeds max length 4096000");
         }

         pos += VarInt.size(interruptingCount);
         if (pos + interruptingCount * 1L > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Interrupting");
         }

         for (int i = 0; i < interruptingCount; i++) {
            int v = buffer.getByte(pos) & 255;
            if (v >= 25) {
               return ValidationResult.error("Invalid InteractionType value for Interrupting[i]");
            }

            pos++;
         }
      }

      return ValidationResult.OK;
   }

   public InteractionRules clone() {
      InteractionRules copy = new InteractionRules();
      copy.blockedBy = this.blockedBy != null ? Arrays.copyOf(this.blockedBy, this.blockedBy.length) : null;
      copy.blocking = this.blocking != null ? Arrays.copyOf(this.blocking, this.blocking.length) : null;
      copy.interruptedBy = this.interruptedBy != null ? Arrays.copyOf(this.interruptedBy, this.interruptedBy.length) : null;
      copy.interrupting = this.interrupting != null ? Arrays.copyOf(this.interrupting, this.interrupting.length) : null;
      copy.blockedByBypassIndex = this.blockedByBypassIndex;
      copy.blockingBypassIndex = this.blockingBypassIndex;
      copy.interruptedByBypassIndex = this.interruptedByBypassIndex;
      copy.interruptingBypassIndex = this.interruptingBypassIndex;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof InteractionRules other)
            ? false
            : Arrays.equals(this.blockedBy, other.blockedBy)
               && Arrays.equals(this.blocking, other.blocking)
               && Arrays.equals(this.interruptedBy, other.interruptedBy)
               && Arrays.equals(this.interrupting, other.interrupting)
               && this.blockedByBypassIndex == other.blockedByBypassIndex
               && this.blockingBypassIndex == other.blockingBypassIndex
               && this.interruptedByBypassIndex == other.interruptedByBypassIndex
               && this.interruptingBypassIndex == other.interruptingBypassIndex;
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      result = 31 * result + Arrays.hashCode(this.blockedBy);
      result = 31 * result + Arrays.hashCode(this.blocking);
      result = 31 * result + Arrays.hashCode(this.interruptedBy);
      result = 31 * result + Arrays.hashCode(this.interrupting);
      result = 31 * result + Integer.hashCode(this.blockedByBypassIndex);
      result = 31 * result + Integer.hashCode(this.blockingBypassIndex);
      result = 31 * result + Integer.hashCode(this.interruptedByBypassIndex);
      return 31 * result + Integer.hashCode(this.interruptingBypassIndex);
   }
}
