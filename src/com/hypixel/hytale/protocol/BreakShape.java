package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.lang.foreign.MemorySegment;
import javax.annotation.Nonnull;

public abstract class BreakShape {
   public static final int MAX_SIZE = 32;

   @Nonnull
   public static BreakShape deserialize(@Nonnull ByteBuf buf, int offset) {
      int typeId = VarInt.peek(buf, offset);
      int typeIdLen = VarInt.size(typeId);

      return switch (typeId) {
         case 0 -> BoxBreakShape.deserialize(buf, offset + typeIdLen);
         case 1 -> CylinderBreakShape.deserialize(buf, offset + typeIdLen);
         default -> throw ProtocolException.unknownPolymorphicType("BreakShape", typeId);
      };
   }

   public static BreakShape toObject(MemorySegment mem) {
      return toObject(mem, 0);
   }

   public static BreakShape toObject(MemorySegment mem, int offset) {
      int typeId = VarInt.get(mem, offset);
      int typeIdLen = VarInt.size(typeId);

      return switch (typeId) {
         case 0 -> BoxBreakShape.toObject(mem, offset + typeIdLen);
         case 1 -> CylinderBreakShape.toObject(mem, offset + typeIdLen);
         default -> throw ProtocolException.unknownPolymorphicType("BreakShape", typeId);
      };
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      int typeId = VarInt.peek(buf, offset);
      int typeIdLen = VarInt.size(typeId);

      return typeIdLen + switch (typeId) {
         case 0 -> BoxBreakShape.computeBytesConsumed(buf, offset + typeIdLen);
         case 1 -> CylinderBreakShape.computeBytesConsumed(buf, offset + typeIdLen);
         default -> throw ProtocolException.unknownPolymorphicType("BreakShape", typeId);
      };
   }

   public int getTypeId() {
      if (this instanceof BoxBreakShape sub) {
         return 0;
      } else if (this instanceof CylinderBreakShape sub) {
         return 1;
      } else {
         throw new IllegalStateException("Unknown subtype: " + this.getClass().getName());
      }
   }

   public abstract int serialize(@Nonnull ByteBuf var1);

   public abstract int serialize(@Nonnull MemorySegment var1, int var2);

   public abstract int computeSize();

   public int serializeWithTypeId(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      VarInt.write(buf, this.getTypeId());
      this.serialize(buf);
      return buf.writerIndex() - startPos;
   }

   public int serializeWithTypeId(@Nonnull MemorySegment mem, int offset) {
      int len = VarInt.set(mem, offset, this.getTypeId());
      return len + this.serialize(mem, offset + len);
   }

   public int computeSizeWithTypeId() {
      return VarInt.size(this.getTypeId()) + this.computeSize();
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      int typeId = VarInt.peek(buffer, offset);
      int typeIdLen = VarInt.size(typeId);

      return switch (typeId) {
         case 0 -> BoxBreakShape.validateStructure(buffer, offset + typeIdLen);
         case 1 -> CylinderBreakShape.validateStructure(buffer, offset + typeIdLen);
         default -> ValidationResult.error("Unknown polymorphic type ID " + typeId + " for BreakShape");
      };
   }
}
