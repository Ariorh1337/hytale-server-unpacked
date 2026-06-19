package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.lang.foreign.MemorySegment;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemTool {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 5;
   public static final int VARIABLE_FIELD_COUNT = 2;
   public static final int VARIABLE_BLOCK_START = 13;
   public static final int MAX_SIZE = 1677721600;
   @Nullable
   public ItemToolSpec[] specs;
   public float speed;
   @Nullable
   public BreakShape breakShape;

   public ItemTool() {
   }

   public ItemTool(@Nullable ItemToolSpec[] specs, float speed, @Nullable BreakShape breakShape) {
      this.specs = specs;
      this.speed = speed;
      this.breakShape = breakShape;
   }

   public ItemTool(@Nonnull ItemTool other) {
      this.specs = other.specs;
      this.speed = other.speed;
      this.breakShape = other.breakShape;
   }

   @Nonnull
   public static ItemTool deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 13) {
         throw ProtocolException.bufferTooSmall("ItemTool", 13, buf.readableBytes() - offset);
      }

      ItemTool obj = new ItemTool();
      byte nullBits = buf.getByte(offset);
      obj.speed = buf.getFloatLE(offset + 1);
      if ((nullBits & 1) != 0) {
         int varPosBase0 = buf.getIntLE(offset + 5);
         if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 13) {
            throw ProtocolException.invalidOffset("Specs", varPosBase0, buf.readableBytes());
         }

         int varPos0 = offset + 13 + varPosBase0;
         int specsCount = VarInt.peek(buf, varPos0);
         if (specsCount < 0) {
            throw ProtocolException.invalidVarInt("Specs");
         }

         int varIntLen = VarInt.size(specsCount);
         if (specsCount > 4096000) {
            throw ProtocolException.arrayTooLong("Specs", specsCount, 4096000);
         }

         if (varPos0 + varIntLen + specsCount * 9L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Specs", varPos0 + varIntLen + specsCount * 9, buf.readableBytes());
         }

         obj.specs = new ItemToolSpec[specsCount];
         int elemPos = varPos0 + varIntLen;

         for (int i = 0; i < specsCount; i++) {
            obj.specs[i] = ItemToolSpec.deserialize(buf, elemPos);
            elemPos += ItemToolSpec.computeBytesConsumed(buf, elemPos);
         }
      }

      if ((nullBits & 2) != 0) {
         int varPosBase1 = buf.getIntLE(offset + 9);
         if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 13) {
            throw ProtocolException.invalidOffset("BreakShape", varPosBase1, buf.readableBytes());
         }

         int varPos1 = offset + 13 + varPosBase1;
         obj.breakShape = BreakShape.deserialize(buf, varPos1);
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 13;
      if ((nullBits & 1) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 5);
         if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 13) {
            throw ProtocolException.invalidOffset("Specs", fieldOffset0, maxEnd);
         }

         int pos0 = offset + 13 + fieldOffset0;
         int arrLen = VarInt.peek(buf, pos0);
         pos0 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            pos0 += ItemToolSpec.computeBytesConsumed(buf, pos0);
         }

         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits & 2) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 9);
         if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 13) {
            throw ProtocolException.invalidOffset("BreakShape", fieldOffset1, maxEnd);
         }

         int pos1 = offset + 13 + fieldOffset1;
         pos1 += BreakShape.computeBytesConsumed(buf, pos1);
         if (pos1 - offset > maxEnd) {
            maxEnd = pos1 - offset;
         }
      }

      return maxEnd;
   }

   public static boolean isBufferTooSmall(MemorySegment mem) {
      return mem.byteSize() < 13L;
   }

   @Nullable
   public static ItemToolSpec[] getSpecs(MemorySegment mem) {
      return getSpecs(mem, 0);
   }

   @Nullable
   public static ItemToolSpec[] getSpecs(MemorySegment mem, int offset) {
      if (!hasSpecs(mem, offset)) {
         return null;
      }

      int off = offset + getValidatedOffset(mem, offset, 5, 13, "Specs");
      long packed = VarInt.getWithLength(mem, off);
      int len = (int)packed;
      if (len < 0) {
         throw ProtocolException.negativeLength("Specs", len);
      }

      if (len > 4096000) {
         throw ProtocolException.arrayTooLong("Specs", len, 4096000);
      }

      int lenOffset = (int)(packed >>> 32);
      if (off + lenOffset + len > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("Specs", off + lenOffset + len, (int)mem.byteSize());
      }

      off += lenOffset;
      ItemToolSpec[] data = new ItemToolSpec[len];

      for (int i = 0; i < len; i++) {
         data[i] = ItemToolSpec.toObject(mem, off);
         off += data[i].computeSize();
      }

      return data;
   }

   public static float getSpeed(MemorySegment mem) {
      return getSpeed(mem, 0);
   }

   public static float getSpeed(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_FLOAT, offset + 1);
   }

   @Nullable
   public static BreakShape getBreakShape(MemorySegment mem) {
      return getBreakShape(mem, 0);
   }

   @Nullable
   public static BreakShape getBreakShape(MemorySegment mem, int offset) {
      return hasBreakShape(mem, offset) ? BreakShape.toObject(mem, offset + getValidatedOffset(mem, offset, 9, 13, "BreakShape")) : null;
   }

   public static boolean hasSpecs(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 1) != 0;
   }

   public static boolean hasBreakShape(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 2) != 0;
   }

   private static int getValidatedOffset(MemorySegment buffer, int base, int slotPosition, int varBlockStart, String fieldName) {
      int offset = buffer.get(PacketIO.PROTO_INT, base + slotPosition);
      if (offset >= 0 && offset <= buffer.byteSize() - base - varBlockStart) {
         return varBlockStart + offset;
      } else {
         throw ProtocolException.invalidOffset(fieldName, offset, (int)buffer.byteSize());
      }
   }

   public static ItemTool toObject(MemorySegment mem) {
      return toObject(mem, 0);
   }

   public static ItemTool toObject(MemorySegment mem, int offset) {
      if (offset + 13 > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("ItemTool", offset + 13, (int)mem.byteSize());
      }

      ItemToolSpec[] specs = null;
      if (hasSpecs(mem, offset)) {
         int off = offset + getValidatedOffset(mem, offset, 5, 13, "Specs");
         long packed = VarInt.getWithLength(mem, off);
         int len = (int)packed;
         if (len < 0) {
            throw ProtocolException.negativeLength("Specs", len);
         }

         if (len > 4096000) {
            throw ProtocolException.arrayTooLong("Specs", len, 4096000);
         }

         int lenOffset = (int)(packed >>> 32);
         if (off + lenOffset + len > mem.byteSize()) {
            throw ProtocolException.bufferTooSmall("Specs", off + lenOffset + len, (int)mem.byteSize());
         }

         off += lenOffset;
         specs = new ItemToolSpec[len];

         for (int i = 0; i < len; i++) {
            specs[i] = ItemToolSpec.toObject(mem, off);
            off += specs[i].computeSize();
         }
      }

      return new ItemTool(
         specs,
         mem.get(PacketIO.PROTO_FLOAT, offset + 1),
         hasBreakShape(mem, offset) ? BreakShape.toObject(mem, offset + getValidatedOffset(mem, offset, 9, 13, "BreakShape")) : null
      );
   }

   public void serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      byte nullBits = 0;
      if (this.specs != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.breakShape != null) {
         nullBits = (byte)(nullBits | 2);
      }

      buf.writeByte(nullBits);
      buf.writeFloatLE(this.speed);
      int specsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int breakShapeOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.specs != null) {
         buf.setIntLE(specsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.specs.length > 4096000) {
            throw ProtocolException.arrayTooLong("Specs", this.specs.length, 4096000);
         }

         VarInt.write(buf, this.specs.length);

         for (ItemToolSpec item : this.specs) {
            item.serialize(buf);
         }
      } else {
         buf.setIntLE(specsOffsetSlot, -1);
      }

      if (this.breakShape != null) {
         buf.setIntLE(breakShapeOffsetSlot, buf.writerIndex() - varBlockStart);
         this.breakShape.serializeWithTypeId(buf);
      } else {
         buf.setIntLE(breakShapeOffsetSlot, -1);
      }
   }

   public int serialize(@Nonnull MemorySegment mem, int offset) {
      byte nullBits = 0;
      if (this.specs != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.breakShape != null) {
         nullBits = (byte)(nullBits | 2);
      }

      mem.set(PacketIO.PROTO_BYTE, offset + 0, nullBits);
      mem.set(PacketIO.PROTO_FLOAT, offset + 1, this.speed);
      int varOffset = offset + 13;
      if (this.specs != null) {
         mem.set(PacketIO.PROTO_INT, offset + 5, varOffset - offset - 13);
         if (this.specs.length > 4096000) {
            throw ProtocolException.arrayTooLong("Specs", this.specs.length, 4096000);
         }

         varOffset += VarInt.set(mem, varOffset, this.specs.length);
         int specsValueOffset = 0;

         for (int i = 0; i < this.specs.length; i++) {
            specsValueOffset += this.specs[i].serialize(mem, varOffset + specsValueOffset);
         }

         varOffset += specsValueOffset;
      } else {
         mem.set(PacketIO.PROTO_INT, offset + 5, -1);
      }

      if (this.breakShape != null) {
         mem.set(PacketIO.PROTO_INT, offset + 9, varOffset - offset - 13);
         varOffset += this.breakShape.serializeWithTypeId(mem, varOffset);
      } else {
         mem.set(PacketIO.PROTO_INT, offset + 9, -1);
      }

      return varOffset - offset;
   }

   public int computeSize() {
      int size = 13;
      if (this.specs != null) {
         int specsSize = 0;

         for (ItemToolSpec elem : this.specs) {
            specsSize += elem.computeSize();
         }

         size += VarInt.size(this.specs.length) + specsSize;
      }

      if (this.breakShape != null) {
         size += this.breakShape.computeSizeWithTypeId();
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 13) {
         return ValidationResult.error("Buffer too small: expected at least 13 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      if ((nullBits & 1) != 0) {
         int specsOffset = buffer.getIntLE(offset + 5);
         if (specsOffset < 0 || specsOffset > buffer.writerIndex() - offset - 13) {
            return ValidationResult.error("Invalid offset for Specs");
         }

         int pos = offset + 13 + specsOffset;
         int specsCount = VarInt.peek(buffer, pos);
         if (specsCount < 0) {
            return ValidationResult.error("Invalid array count for Specs");
         }

         if (specsCount > 4096000) {
            return ValidationResult.error("Specs exceeds max length 4096000");
         }

         pos += VarInt.size(specsCount);

         for (int i = 0; i < specsCount; i++) {
            ValidationResult structResult = ItemToolSpec.validateStructure(buffer, pos);
            if (!structResult.isValid()) {
               return ValidationResult.error("Invalid ItemToolSpec in Specs[" + i + "]: " + structResult.error());
            }

            pos += ItemToolSpec.computeBytesConsumed(buffer, pos);
         }
      }

      if ((nullBits & 2) != 0) {
         int breakShapeOffset = buffer.getIntLE(offset + 9);
         if (breakShapeOffset < 0 || breakShapeOffset > buffer.writerIndex() - offset - 13) {
            return ValidationResult.error("Invalid offset for BreakShape");
         }

         int pos = offset + 13 + breakShapeOffset;
         ValidationResult breakShapeResult = BreakShape.validateStructure(buffer, pos);
         if (!breakShapeResult.isValid()) {
            return ValidationResult.error("Invalid BreakShape: " + breakShapeResult.error());
         }

         pos += BreakShape.computeBytesConsumed(buffer, pos);
      }

      return ValidationResult.OK;
   }

   public ItemTool clone() {
      ItemTool copy = new ItemTool();
      copy.specs = this.specs != null ? Arrays.stream(this.specs).map(e -> e.clone()).toArray(ItemToolSpec[]::new) : null;
      copy.speed = this.speed;
      copy.breakShape = this.breakShape;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof ItemTool other)
            ? false
            : Arrays.equals(this.specs, other.specs) && this.speed == other.speed && Objects.equals(this.breakShape, other.breakShape);
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      result = 31 * result + Arrays.hashCode(this.specs);
      result = 31 * result + Float.hashCode(this.speed);
      return 31 * result + Objects.hashCode(this.breakShape);
   }
}
