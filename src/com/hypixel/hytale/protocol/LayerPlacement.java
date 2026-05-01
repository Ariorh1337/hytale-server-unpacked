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

   public static boolean isBufferTooSmall(MemorySegment mem) {
      return mem.byteSize() < 17L;
   }

   public static int getContainerIndex(MemorySegment mem) {
      return getContainerIndex(mem, 0);
   }

   public static int getContainerIndex(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 1);
   }

   @Nullable
   public static String getName(MemorySegment mem) {
      return getName(mem, 0);
   }

   @Nullable
   public static String getName(MemorySegment mem, int offset) {
      return hasName(mem, offset) ? PacketIO.readVarString("Name", mem, offset + 17, 4096000, PacketIO.UTF8) : null;
   }

   @Nullable
   public static BarBeatDuration getClipStart(MemorySegment mem) {
      return getClipStart(mem, 0);
   }

   @Nullable
   public static BarBeatDuration getClipStart(MemorySegment mem, int offset) {
      return hasClipStart(mem, offset) ? BarBeatDuration.toObject(mem, offset + 5) : null;
   }

   public static boolean hasClipStart(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 1) != 0;
   }

   public static boolean hasName(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 2) != 0;
   }

   public static LayerPlacement toObject(MemorySegment mem) {
      return toObject(mem, 0);
   }

   public static LayerPlacement toObject(MemorySegment mem, int offset) {
      if (offset + 17 > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("LayerPlacement", offset + 17, (int)mem.byteSize());
      } else {
         return new LayerPlacement(
            mem.get(PacketIO.PROTO_INT, offset + 1),
            hasName(mem, offset) ? PacketIO.readVarString("Name", mem, offset + 17, 4096000, PacketIO.UTF8) : null,
            hasClipStart(mem, offset) ? BarBeatDuration.toObject(mem, offset + 5) : null
         );
      }
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

   public int serialize(@Nonnull MemorySegment mem, int offset) {
      byte nullBits = 0;
      if (this.clipStart != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.name != null) {
         nullBits = (byte)(nullBits | 2);
      }

      mem.set(PacketIO.PROTO_BYTE, offset + 0, nullBits);
      mem.set(PacketIO.PROTO_INT, offset + 1, this.containerIndex);
      if (this.clipStart != null) {
         this.clipStart.serialize(mem, offset + 5);
      } else {
         mem.asSlice(offset + 5, 12L).fill((byte)0);
      }

      int varOffset = offset + 17;
      if (this.name != null) {
         varOffset += PacketIO.writeVarString(mem, varOffset, this.name, 4096000);
      }

      return varOffset - offset;
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
