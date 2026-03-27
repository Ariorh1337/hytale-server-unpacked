package com.hypixel.hytale.protocol.packets.interface_;

import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class UIByteDataValue extends UIDataValue {
   public static final int NULLABLE_BIT_FIELD_SIZE = 0;
   public static final int FIXED_BLOCK_SIZE = 1;
   public static final int VARIABLE_FIELD_COUNT = 0;
   public static final int VARIABLE_BLOCK_START = 1;
   public static final int MAX_SIZE = 1;
   public byte value;

   public UIByteDataValue() {
   }

   public UIByteDataValue(byte value) {
      this.value = value;
   }

   public UIByteDataValue(@Nonnull UIByteDataValue other) {
      this.value = other.value;
   }

   @Nonnull
   public static UIByteDataValue deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 1) {
         throw ProtocolException.bufferTooSmall("UIByteDataValue", 1, buf.readableBytes() - offset);
      }

      UIByteDataValue obj = new UIByteDataValue();
      obj.value = buf.getByte(offset + 0);
      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      return 1;
   }

   @Override
   public int serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      buf.writeByte(this.value);
      return buf.writerIndex() - startPos;
   }

   @Override
   public int computeSize() {
      return 1;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      return buffer.readableBytes() - offset < 1 ? ValidationResult.error("Buffer too small: expected at least 1 bytes") : ValidationResult.OK;
   }

   public UIByteDataValue clone() {
      UIByteDataValue copy = new UIByteDataValue();
      copy.value = this.value;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return obj instanceof UIByteDataValue other ? this.value == other.value : false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.value);
   }
}
