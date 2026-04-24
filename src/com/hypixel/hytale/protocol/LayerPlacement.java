package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LayerPlacement {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 17;
   public static final int VARIABLE_FIELD_COUNT = 1;
   public static final int VARIABLE_BLOCK_START = 17;
   public static final int MAX_SIZE = 16384022;
   public int containerIndex;
   @Nullable
   public String name;
   @Nullable
   public BarBeatDuration clipStart;

   public LayerPlacement() {
   }

   public LayerPlacement(int containerIndex, @Nullable String name, @Nullable BarBeatDuration clipStart) {
      this.containerIndex = containerIndex;
      this.name = name;
      this.clipStart = clipStart;
   }

   public LayerPlacement(@Nonnull LayerPlacement other) {
      this.containerIndex = other.containerIndex;
      this.name = other.name;
      this.clipStart = other.clipStart;
   }

   @Nonnull
   public static LayerPlacement deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 17) {
         throw ProtocolException.bufferTooSmall("LayerPlacement", 17, buf.readableBytes() - offset);
      }

      LayerPlacement obj = new LayerPlacement();
      byte nullBits = buf.getByte(offset);
      obj.containerIndex = buf.getIntLE(offset + 1);
      if ((nullBits & 1) != 0) {
         obj.clipStart = BarBeatDuration.deserialize(buf, offset + 5);
      }

      int pos = offset + 17;
      if ((nullBits & 2) != 0) {
         int nameLen = VarInt.peek(buf, pos);
         if (nameLen < 0) {
            throw ProtocolException.invalidVarInt("Name");
         }

         int nameVarLen = VarInt.size(nameLen);
         if (nameLen > 4096000) {
            throw ProtocolException.stringTooLong("Name", nameLen, 4096000);
         }

         if (pos + nameVarLen + nameLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Name", pos + nameVarLen + nameLen, buf.readableBytes());
         }

         obj.name = PacketIO.readVarString(buf, pos, PacketIO.UTF8);
         pos += nameVarLen + nameLen;
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int pos = offset + 17;
      if ((nullBits & 2) != 0) {
         int sl = VarInt.peek(buf, pos);
         pos += VarInt.size(sl) + sl;
      }

      return pos - offset;
   }

   public void serialize(@Nonnull ByteBuf buf) {
      byte nullBits = 0;
      if (this.clipStart != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.name != null) {
         nullBits = (byte)(nullBits | 2);
      }

      buf.writeByte(nullBits);
      buf.writeIntLE(this.containerIndex);
      if (this.clipStart != null) {
         this.clipStart.serialize(buf);
      } else {
         buf.writeZero(12);
      }

      if (this.name != null) {
         PacketIO.writeVarString(buf, this.name, 4096000);
      }
   }

   public int computeSize() {
      int size = 17;
      if (this.name != null) {
         size += PacketIO.stringSize(this.name);
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 17) {
         return ValidationResult.error("Buffer too small: expected at least 17 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      int pos = offset + 17;
      if ((nullBits & 2) != 0) {
         int nameLen = VarInt.peek(buffer, pos);
         if (nameLen < 0) {
            return ValidationResult.error("Invalid string length for Name");
         }

         if (nameLen > 4096000) {
            return ValidationResult.error("Name exceeds max length 4096000");
         }

         pos += VarInt.size(nameLen);
         pos += nameLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Name");
         }
      }

      return ValidationResult.OK;
   }

   public LayerPlacement clone() {
      LayerPlacement copy = new LayerPlacement();
      copy.containerIndex = this.containerIndex;
      copy.name = this.name;
      copy.clipStart = this.clipStart != null ? this.clipStart.clone() : null;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof LayerPlacement other)
            ? false
            : this.containerIndex == other.containerIndex && Objects.equals(this.name, other.name) && Objects.equals(this.clipStart, other.clipStart);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.containerIndex, this.name, this.clipStart);
   }
}
