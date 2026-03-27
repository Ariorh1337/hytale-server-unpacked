package com.hypixel.hytale.protocol.packets.interface_;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class UIStringDataValue extends UIDataValue {
   public static final int NULLABLE_BIT_FIELD_SIZE = 0;
   public static final int FIXED_BLOCK_SIZE = 0;
   public static final int VARIABLE_FIELD_COUNT = 1;
   public static final int VARIABLE_BLOCK_START = 0;
   public static final int MAX_SIZE = 16384005;
   @Nonnull
   public String value = "";

   public UIStringDataValue() {
   }

   public UIStringDataValue(@Nonnull String value) {
      this.value = value;
   }

   public UIStringDataValue(@Nonnull UIStringDataValue other) {
      this.value = other.value;
   }

   @Nonnull
   public static UIStringDataValue deserialize(@Nonnull ByteBuf buf, int offset) {
      UIStringDataValue obj = new UIStringDataValue();
      int pos = offset + 0;
      int valueLen = VarInt.peek(buf, pos);
      if (valueLen < 0) {
         throw ProtocolException.invalidVarInt("Value");
      }

      int valueVarLen = VarInt.size(valueLen);
      if (valueLen > 4096000) {
         throw ProtocolException.stringTooLong("Value", valueLen, 4096000);
      }

      if (pos + valueVarLen + valueLen > buf.readableBytes()) {
         throw ProtocolException.bufferTooSmall("Value", pos + valueVarLen + valueLen, buf.readableBytes());
      }

      obj.value = PacketIO.readVarString(buf, pos, PacketIO.UTF8);
      pos += valueVarLen + valueLen;
      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      int pos = offset + 0;
      int sl = VarInt.peek(buf, pos);
      pos += VarInt.size(sl) + sl;
      return pos - offset;
   }

   @Override
   public int serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      PacketIO.writeVarString(buf, this.value, 4096000);
      return buf.writerIndex() - startPos;
   }

   @Override
   public int computeSize() {
      int size = 0;
      return size + PacketIO.stringSize(this.value);
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 0) {
         return ValidationResult.error("Buffer too small: expected at least 0 bytes");
      }

      int pos = offset + 0;
      int valueLen = VarInt.peek(buffer, pos);
      if (valueLen < 0) {
         return ValidationResult.error("Invalid string length for Value");
      }

      if (valueLen > 4096000) {
         return ValidationResult.error("Value exceeds max length 4096000");
      }

      pos += VarInt.size(valueLen);
      pos += valueLen;
      return pos > buffer.writerIndex() ? ValidationResult.error("Buffer overflow reading Value") : ValidationResult.OK;
   }

   public UIStringDataValue clone() {
      UIStringDataValue copy = new UIStringDataValue();
      copy.value = this.value;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return obj instanceof UIStringDataValue other ? Objects.equals(this.value, other.value) : false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.value);
   }
}
