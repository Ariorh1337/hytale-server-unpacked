package com.hypixel.hytale.protocol.packets.interface_;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class SetDataContextPropertyServersideUIProperty extends ServersideUICommand {
   public static final int NULLABLE_BIT_FIELD_SIZE = 0;
   public static final int FIXED_BLOCK_SIZE = 0;
   public static final int VARIABLE_FIELD_COUNT = 2;
   public static final int VARIABLE_BLOCK_START = 8;
   public static final int MAX_SIZE = 16385037;
   @Nonnull
   public String property = "";
   @Nonnull
   public UIDataValue value;

   public SetDataContextPropertyServersideUIProperty() {
   }

   public SetDataContextPropertyServersideUIProperty(@Nonnull String property, @Nonnull UIDataValue value) {
      this.property = property;
      this.value = value;
   }

   public SetDataContextPropertyServersideUIProperty(@Nonnull SetDataContextPropertyServersideUIProperty other) {
      this.property = other.property;
      this.value = other.value;
   }

   @Nonnull
   public static SetDataContextPropertyServersideUIProperty deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 8) {
         throw ProtocolException.bufferTooSmall("SetDataContextPropertyServersideUIProperty", 8, buf.readableBytes() - offset);
      }

      SetDataContextPropertyServersideUIProperty obj = new SetDataContextPropertyServersideUIProperty();
      int varPosBase0 = buf.getIntLE(offset + 0);
      if (varPosBase0 >= 0 && varPosBase0 <= buf.writerIndex() - offset - 8) {
         int varPos0 = offset + 8 + varPosBase0;
         int propertyLen = VarInt.peek(buf, varPos0);
         if (propertyLen < 0) {
            throw ProtocolException.invalidVarInt("Property");
         } else {
            int propertyVarIntLen = VarInt.size(propertyLen);
            if (propertyLen > 4096000) {
               throw ProtocolException.stringTooLong("Property", propertyLen, 4096000);
            } else if (varPos0 + propertyVarIntLen + propertyLen > buf.readableBytes()) {
               throw ProtocolException.bufferTooSmall("Property", varPos0 + propertyVarIntLen + propertyLen, buf.readableBytes());
            } else {
               obj.property = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
               varPosBase0 = buf.getIntLE(offset + 4);
               if (varPosBase0 >= 0 && varPosBase0 <= buf.writerIndex() - offset - 8) {
                  varPos0 = offset + 8 + varPosBase0;
                  obj.value = UIDataValue.deserialize(buf, varPos0);
                  return obj;
               } else {
                  throw ProtocolException.invalidOffset("Value", varPosBase0, buf.readableBytes());
               }
            }
         }
      } else {
         throw ProtocolException.invalidOffset("Property", varPosBase0, buf.readableBytes());
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
            pos0 += UIDataValue.computeBytesConsumed(buf, pos0);
            if (pos0 - offset > maxEnd) {
               maxEnd = pos0 - offset;
            }

            return maxEnd;
         } else {
            throw ProtocolException.invalidOffset("Value", fieldOffset0, maxEnd);
         }
      } else {
         throw ProtocolException.invalidOffset("Property", fieldOffset0, maxEnd);
      }
   }

   @Override
   public int serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      int propertyOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int valueOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      buf.setIntLE(propertyOffsetSlot, buf.writerIndex() - varBlockStart);
      PacketIO.writeVarString(buf, this.property, 4096000);
      buf.setIntLE(valueOffsetSlot, buf.writerIndex() - varBlockStart);
      this.value.serializeWithTypeId(buf);
      return buf.writerIndex() - startPos;
   }

   @Override
   public int computeSize() {
      int size = 8;
      size += PacketIO.stringSize(this.property);
      return size + this.value.computeSizeWithTypeId();
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 8) {
         return ValidationResult.error("Buffer too small: expected at least 8 bytes");
      }

      int propertyOffset = buffer.getIntLE(offset + 0);
      if (propertyOffset >= 0 && propertyOffset <= buffer.writerIndex() - offset - 8) {
         int pos = offset + 8 + propertyOffset;
         int propertyLen = VarInt.peek(buffer, pos);
         if (propertyLen < 0) {
            return ValidationResult.error("Invalid string length for Property");
         }

         if (propertyLen > 4096000) {
            return ValidationResult.error("Property exceeds max length 4096000");
         }

         pos += VarInt.size(propertyLen);
         pos += propertyLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Property");
         }

         propertyOffset = buffer.getIntLE(offset + 4);
         if (propertyOffset >= 0 && propertyOffset <= buffer.writerIndex() - offset - 8) {
            pos = offset + 8 + propertyOffset;
            ValidationResult valueResult = UIDataValue.validateStructure(buffer, pos);
            if (!valueResult.isValid()) {
               return ValidationResult.error("Invalid Value: " + valueResult.error());
            }

            pos += UIDataValue.computeBytesConsumed(buffer, pos);
            return ValidationResult.OK;
         } else {
            return ValidationResult.error("Invalid offset for Value");
         }
      } else {
         return ValidationResult.error("Invalid offset for Property");
      }
   }

   public SetDataContextPropertyServersideUIProperty clone() {
      SetDataContextPropertyServersideUIProperty copy = new SetDataContextPropertyServersideUIProperty();
      copy.property = this.property;
      copy.value = this.value;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof SetDataContextPropertyServersideUIProperty other)
            ? false
            : Objects.equals(this.property, other.property) && Objects.equals(this.value, other.value);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.property, this.value);
   }
}
