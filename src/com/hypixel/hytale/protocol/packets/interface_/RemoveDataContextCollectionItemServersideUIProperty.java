package com.hypixel.hytale.protocol.packets.interface_;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class RemoveDataContextCollectionItemServersideUIProperty extends ServersideUICommand {
   public static final int NULLABLE_BIT_FIELD_SIZE = 0;
   public static final int FIXED_BLOCK_SIZE = 4;
   public static final int VARIABLE_FIELD_COUNT = 1;
   public static final int VARIABLE_BLOCK_START = 4;
   public static final int MAX_SIZE = 16384009;
   @Nonnull
   public String property = "";
   public int index;

   public RemoveDataContextCollectionItemServersideUIProperty() {
   }

   public RemoveDataContextCollectionItemServersideUIProperty(@Nonnull String property, int index) {
      this.property = property;
      this.index = index;
   }

   public RemoveDataContextCollectionItemServersideUIProperty(@Nonnull RemoveDataContextCollectionItemServersideUIProperty other) {
      this.property = other.property;
      this.index = other.index;
   }

   @Nonnull
   public static RemoveDataContextCollectionItemServersideUIProperty deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 4) {
         throw ProtocolException.bufferTooSmall("RemoveDataContextCollectionItemServersideUIProperty", 4, buf.readableBytes() - offset);
      }

      RemoveDataContextCollectionItemServersideUIProperty obj = new RemoveDataContextCollectionItemServersideUIProperty();
      obj.index = buf.getIntLE(offset + 0);
      int pos = offset + 4;
      int propertyLen = VarInt.peek(buf, pos);
      if (propertyLen < 0) {
         throw ProtocolException.invalidVarInt("Property");
      }

      int propertyVarLen = VarInt.size(propertyLen);
      if (propertyLen > 4096000) {
         throw ProtocolException.stringTooLong("Property", propertyLen, 4096000);
      }

      if (pos + propertyVarLen + propertyLen > buf.readableBytes()) {
         throw ProtocolException.bufferTooSmall("Property", pos + propertyVarLen + propertyLen, buf.readableBytes());
      }

      obj.property = PacketIO.readVarString(buf, pos, PacketIO.UTF8);
      pos += propertyVarLen + propertyLen;
      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      int pos = offset + 4;
      int sl = VarInt.peek(buf, pos);
      pos += VarInt.size(sl) + sl;
      return pos - offset;
   }

   @Override
   public int serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      buf.writeIntLE(this.index);
      PacketIO.writeVarString(buf, this.property, 4096000);
      return buf.writerIndex() - startPos;
   }

   @Override
   public int computeSize() {
      int size = 4;
      return size + PacketIO.stringSize(this.property);
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 4) {
         return ValidationResult.error("Buffer too small: expected at least 4 bytes");
      }

      int pos = offset + 4;
      int propertyLen = VarInt.peek(buffer, pos);
      if (propertyLen < 0) {
         return ValidationResult.error("Invalid string length for Property");
      }

      if (propertyLen > 4096000) {
         return ValidationResult.error("Property exceeds max length 4096000");
      }

      pos += VarInt.size(propertyLen);
      pos += propertyLen;
      return pos > buffer.writerIndex() ? ValidationResult.error("Buffer overflow reading Property") : ValidationResult.OK;
   }

   public RemoveDataContextCollectionItemServersideUIProperty clone() {
      RemoveDataContextCollectionItemServersideUIProperty copy = new RemoveDataContextCollectionItemServersideUIProperty();
      copy.property = this.property;
      copy.index = this.index;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof RemoveDataContextCollectionItemServersideUIProperty other)
            ? false
            : Objects.equals(this.property, other.property) && this.index == other.index;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.property, this.index);
   }
}
