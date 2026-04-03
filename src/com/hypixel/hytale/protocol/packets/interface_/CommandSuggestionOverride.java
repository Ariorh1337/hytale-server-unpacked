package com.hypixel.hytale.protocol.packets.interface_;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CommandSuggestionOverride {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 9;
   public static final int VARIABLE_FIELD_COUNT = 1;
   public static final int VARIABLE_BLOCK_START = 9;
   public static final int MAX_SIZE = 16384014;
   public int argStart;
   public int argCount;
   @Nullable
   public String argTypeId;

   public CommandSuggestionOverride() {
   }

   public CommandSuggestionOverride(int argStart, int argCount, @Nullable String argTypeId) {
      this.argStart = argStart;
      this.argCount = argCount;
      this.argTypeId = argTypeId;
   }

   public CommandSuggestionOverride(@Nonnull CommandSuggestionOverride other) {
      this.argStart = other.argStart;
      this.argCount = other.argCount;
      this.argTypeId = other.argTypeId;
   }

   @Nonnull
   public static CommandSuggestionOverride deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 9) {
         throw ProtocolException.bufferTooSmall("CommandSuggestionOverride", 9, buf.readableBytes() - offset);
      }

      CommandSuggestionOverride obj = new CommandSuggestionOverride();
      byte nullBits = buf.getByte(offset);
      obj.argStart = buf.getIntLE(offset + 1);
      obj.argCount = buf.getIntLE(offset + 5);
      int pos = offset + 9;
      if ((nullBits & 1) != 0) {
         int argTypeIdLen = VarInt.peek(buf, pos);
         if (argTypeIdLen < 0) {
            throw ProtocolException.invalidVarInt("ArgTypeId");
         }

         int argTypeIdVarLen = VarInt.size(argTypeIdLen);
         if (argTypeIdLen > 4096000) {
            throw ProtocolException.stringTooLong("ArgTypeId", argTypeIdLen, 4096000);
         }

         if (pos + argTypeIdVarLen + argTypeIdLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("ArgTypeId", pos + argTypeIdVarLen + argTypeIdLen, buf.readableBytes());
         }

         obj.argTypeId = PacketIO.readVarString(buf, pos, PacketIO.UTF8);
         pos += argTypeIdVarLen + argTypeIdLen;
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int pos = offset + 9;
      if ((nullBits & 1) != 0) {
         int sl = VarInt.peek(buf, pos);
         pos += VarInt.size(sl) + sl;
      }

      return pos - offset;
   }

   public void serialize(@Nonnull ByteBuf buf) {
      byte nullBits = 0;
      if (this.argTypeId != null) {
         nullBits = (byte)(nullBits | 1);
      }

      buf.writeByte(nullBits);
      buf.writeIntLE(this.argStart);
      buf.writeIntLE(this.argCount);
      if (this.argTypeId != null) {
         PacketIO.writeVarString(buf, this.argTypeId, 4096000);
      }
   }

   public int computeSize() {
      int size = 9;
      if (this.argTypeId != null) {
         size += PacketIO.stringSize(this.argTypeId);
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 9) {
         return ValidationResult.error("Buffer too small: expected at least 9 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      int pos = offset + 9;
      if ((nullBits & 1) != 0) {
         int argTypeIdLen = VarInt.peek(buffer, pos);
         if (argTypeIdLen < 0) {
            return ValidationResult.error("Invalid string length for ArgTypeId");
         }

         if (argTypeIdLen > 4096000) {
            return ValidationResult.error("ArgTypeId exceeds max length 4096000");
         }

         pos += VarInt.size(argTypeIdLen);
         pos += argTypeIdLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading ArgTypeId");
         }
      }

      return ValidationResult.OK;
   }

   public CommandSuggestionOverride clone() {
      CommandSuggestionOverride copy = new CommandSuggestionOverride();
      copy.argStart = this.argStart;
      copy.argCount = this.argCount;
      copy.argTypeId = this.argTypeId;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof CommandSuggestionOverride other)
            ? false
            : this.argStart == other.argStart && this.argCount == other.argCount && Objects.equals(this.argTypeId, other.argTypeId);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.argStart, this.argCount, this.argTypeId);
   }
}
