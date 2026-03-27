package com.hypixel.hytale.protocol.packets.interface_;

import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class UIUShortDataValue extends UIDataValue {
   public static final int NULLABLE_BIT_FIELD_SIZE = 0;
   public static final int FIXED_BLOCK_SIZE = 2;
   public static final int VARIABLE_FIELD_COUNT = 0;
   public static final int VARIABLE_BLOCK_START = 2;
   public static final int MAX_SIZE = 2;
   public short value;

   public UIUShortDataValue() {
   }

   public UIUShortDataValue(short value) {
      this.value = value;
   }

   public UIUShortDataValue(@Nonnull UIUShortDataValue other) {
      this.value = other.value;
   }

   @Nonnull
   public static UIUShortDataValue deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 2) {
         throw ProtocolException.bufferTooSmall("UIUShortDataValue", 2, buf.readableBytes() - offset);
      }

      UIUShortDataValue obj = new UIUShortDataValue();
      obj.value = buf.getShortLE(offset + 0);
      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      return 2;
   }

   @Override
   public int serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      buf.writeShortLE(this.value);
      return buf.writerIndex() - startPos;
   }

   @Override
   public int computeSize() {
      return 2;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      return buffer.readableBytes() - offset < 2 ? ValidationResult.error("Buffer too small: expected at least 2 bytes") : ValidationResult.OK;
   }

   public UIUShortDataValue clone() {
      UIUShortDataValue copy = new UIUShortDataValue();
      copy.value = this.value;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return obj instanceof UIUShortDataValue other ? this.value == other.value : false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.value);
   }
}
