package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.lang.foreign.MemorySegment;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemMovementSettings {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 5;
   public static final int VARIABLE_FIELD_COUNT = 1;
   public static final int VARIABLE_BLOCK_START = 5;
   public static final int MAX_SIZE = 16384010;
   public int extraJumpCount;
   @Nullable
   public String extraJumpParticleSystem;

   public ItemMovementSettings() {
   }

   public ItemMovementSettings(int extraJumpCount, @Nullable String extraJumpParticleSystem) {
      this.extraJumpCount = extraJumpCount;
      this.extraJumpParticleSystem = extraJumpParticleSystem;
   }

   public ItemMovementSettings(@Nonnull ItemMovementSettings other) {
      this.extraJumpCount = other.extraJumpCount;
      this.extraJumpParticleSystem = other.extraJumpParticleSystem;
   }

   @Nonnull
   public static ItemMovementSettings deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 5) {
         throw ProtocolException.bufferTooSmall("ItemMovementSettings", 5, buf.readableBytes() - offset);
      }

      ItemMovementSettings obj = new ItemMovementSettings();
      byte nullBits = buf.getByte(offset);
      obj.extraJumpCount = buf.getIntLE(offset + 1);
      int pos = offset + 5;
      if ((nullBits & 1) != 0) {
         int extraJumpParticleSystemLen = VarInt.peek(buf, pos);
         if (extraJumpParticleSystemLen < 0) {
            throw ProtocolException.invalidVarInt("ExtraJumpParticleSystem");
         }

         int extraJumpParticleSystemVarLen = VarInt.size(extraJumpParticleSystemLen);
         if (extraJumpParticleSystemLen > 4096000) {
            throw ProtocolException.stringTooLong("ExtraJumpParticleSystem", extraJumpParticleSystemLen, 4096000);
         }

         if (pos + extraJumpParticleSystemVarLen + extraJumpParticleSystemLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall(
               "ExtraJumpParticleSystem", pos + extraJumpParticleSystemVarLen + extraJumpParticleSystemLen, buf.readableBytes()
            );
         }

         obj.extraJumpParticleSystem = PacketIO.readVarString(buf, pos, PacketIO.UTF8);
         pos += extraJumpParticleSystemVarLen + extraJumpParticleSystemLen;
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int pos = offset + 5;
      if ((nullBits & 1) != 0) {
         int sl = VarInt.peek(buf, pos);
         pos += VarInt.size(sl) + sl;
      }

      return pos - offset;
   }

   public static boolean isBufferTooSmall(MemorySegment mem) {
      return mem.byteSize() < 5L;
   }

   public static int getExtraJumpCount(MemorySegment mem) {
      return getExtraJumpCount(mem, 0);
   }

   public static int getExtraJumpCount(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 1);
   }

   @Nullable
   public static String getExtraJumpParticleSystem(MemorySegment mem) {
      return getExtraJumpParticleSystem(mem, 0);
   }

   @Nullable
   public static String getExtraJumpParticleSystem(MemorySegment mem, int offset) {
      return hasExtraJumpParticleSystem(mem, offset) ? PacketIO.readVarString("ExtraJumpParticleSystem", mem, offset + 5, 4096000, PacketIO.UTF8) : null;
   }

   public static boolean hasExtraJumpParticleSystem(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 1) != 0;
   }

   public static ItemMovementSettings toObject(MemorySegment mem) {
      return toObject(mem, 0);
   }

   public static ItemMovementSettings toObject(MemorySegment mem, int offset) {
      if (offset + 5 > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("ItemMovementSettings", offset + 5, (int)mem.byteSize());
      } else {
         return new ItemMovementSettings(
            mem.get(PacketIO.PROTO_INT, offset + 1),
            hasExtraJumpParticleSystem(mem, offset) ? PacketIO.readVarString("ExtraJumpParticleSystem", mem, offset + 5, 4096000, PacketIO.UTF8) : null
         );
      }
   }

   public void serialize(@Nonnull ByteBuf buf) {
      byte nullBits = 0;
      if (this.extraJumpParticleSystem != null) {
         nullBits = (byte)(nullBits | 1);
      }

      buf.writeByte(nullBits);
      buf.writeIntLE(this.extraJumpCount);
      if (this.extraJumpParticleSystem != null) {
         PacketIO.writeVarString(buf, this.extraJumpParticleSystem, 4096000);
      }
   }

   public int serialize(@Nonnull MemorySegment mem, int offset) {
      byte nullBits = 0;
      if (this.extraJumpParticleSystem != null) {
         nullBits = (byte)(nullBits | 1);
      }

      mem.set(PacketIO.PROTO_BYTE, offset + 0, nullBits);
      mem.set(PacketIO.PROTO_INT, offset + 1, this.extraJumpCount);
      int varOffset = offset + 5;
      if (this.extraJumpParticleSystem != null) {
         varOffset += PacketIO.writeVarString(mem, varOffset, this.extraJumpParticleSystem, 4096000);
      }

      return varOffset - offset;
   }

   public int computeSize() {
      int size = 5;
      if (this.extraJumpParticleSystem != null) {
         size += PacketIO.stringSize(this.extraJumpParticleSystem);
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 5) {
         return ValidationResult.error("Buffer too small: expected at least 5 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      int pos = offset + 5;
      if ((nullBits & 1) != 0) {
         int extraJumpParticleSystemLen = VarInt.peek(buffer, pos);
         if (extraJumpParticleSystemLen < 0) {
            return ValidationResult.error("Invalid string length for ExtraJumpParticleSystem");
         }

         if (extraJumpParticleSystemLen > 4096000) {
            return ValidationResult.error("ExtraJumpParticleSystem exceeds max length 4096000");
         }

         pos += VarInt.size(extraJumpParticleSystemLen);
         pos += extraJumpParticleSystemLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading ExtraJumpParticleSystem");
         }
      }

      return ValidationResult.OK;
   }

   public ItemMovementSettings clone() {
      ItemMovementSettings copy = new ItemMovementSettings();
      copy.extraJumpCount = this.extraJumpCount;
      copy.extraJumpParticleSystem = this.extraJumpParticleSystem;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof ItemMovementSettings other)
            ? false
            : this.extraJumpCount == other.extraJumpCount && Objects.equals(this.extraJumpParticleSystem, other.extraJumpParticleSystem);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.extraJumpCount, this.extraJumpParticleSystem);
   }
}
