package com.hypixel.hytale.protocol.packets.worldmap;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.lang.foreign.MemorySegment;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BiomeData {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 9;
   public static final int VARIABLE_FIELD_COUNT = 2;
   public static final int VARIABLE_BLOCK_START = 17;
   public static final int MAX_SIZE = 32768027;
   public int zoneId;
   @Nullable
   public String zoneName;
   @Nullable
   public String biomeName;
   public int biomeColor;

   public BiomeData() {
   }

   public BiomeData(int zoneId, @Nullable String zoneName, @Nullable String biomeName, int biomeColor) {
      this.zoneId = zoneId;
      this.zoneName = zoneName;
      this.biomeName = biomeName;
      this.biomeColor = biomeColor;
   }

   public BiomeData(@Nonnull BiomeData other) {
      this.zoneId = other.zoneId;
      this.zoneName = other.zoneName;
      this.biomeName = other.biomeName;
      this.biomeColor = other.biomeColor;
   }

   @Nonnull
   public static BiomeData deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 17) {
         throw ProtocolException.bufferTooSmall("BiomeData", 17, buf.readableBytes() - offset);
      }

      BiomeData obj = new BiomeData();
      byte nullBits = buf.getByte(offset);
      obj.zoneId = buf.getIntLE(offset + 1);
      obj.biomeColor = buf.getIntLE(offset + 5);
      if ((nullBits & 1) != 0) {
         int varPosBase0 = buf.getIntLE(offset + 9);
         if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 17) {
            throw ProtocolException.invalidOffset("ZoneName", varPosBase0, buf.readableBytes());
         }

         int varPos0 = offset + 17 + varPosBase0;
         int zoneNameLen = VarInt.peek(buf, varPos0);
         if (zoneNameLen < 0) {
            throw ProtocolException.invalidVarInt("ZoneName");
         }

         int zoneNameVarIntLen = VarInt.size(zoneNameLen);
         if (zoneNameLen > 4096000) {
            throw ProtocolException.stringTooLong("ZoneName", zoneNameLen, 4096000);
         }

         if (varPos0 + zoneNameVarIntLen + zoneNameLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("ZoneName", varPos0 + zoneNameVarIntLen + zoneNameLen, buf.readableBytes());
         }

         obj.zoneName = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
      }

      if ((nullBits & 2) != 0) {
         int varPosBase1 = buf.getIntLE(offset + 13);
         if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 17) {
            throw ProtocolException.invalidOffset("BiomeName", varPosBase1, buf.readableBytes());
         }

         int varPos1 = offset + 17 + varPosBase1;
         int biomeNameLen = VarInt.peek(buf, varPos1);
         if (biomeNameLen < 0) {
            throw ProtocolException.invalidVarInt("BiomeName");
         }

         int biomeNameVarIntLen = VarInt.size(biomeNameLen);
         if (biomeNameLen > 4096000) {
            throw ProtocolException.stringTooLong("BiomeName", biomeNameLen, 4096000);
         }

         if (varPos1 + biomeNameVarIntLen + biomeNameLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("BiomeName", varPos1 + biomeNameVarIntLen + biomeNameLen, buf.readableBytes());
         }

         obj.biomeName = PacketIO.readVarString(buf, varPos1, PacketIO.UTF8);
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 17;
      if ((nullBits & 1) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 9);
         if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 17) {
            throw ProtocolException.invalidOffset("ZoneName", fieldOffset0, maxEnd);
         }

         int pos0 = offset + 17 + fieldOffset0;
         int sl = VarInt.peek(buf, pos0);
         pos0 += VarInt.size(sl) + sl;
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits & 2) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 13);
         if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 17) {
            throw ProtocolException.invalidOffset("BiomeName", fieldOffset1, maxEnd);
         }

         int pos1 = offset + 17 + fieldOffset1;
         int sl = VarInt.peek(buf, pos1);
         pos1 += VarInt.size(sl) + sl;
         if (pos1 - offset > maxEnd) {
            maxEnd = pos1 - offset;
         }
      }

      return maxEnd;
   }

   public static boolean isBufferTooSmall(MemorySegment mem) {
      return mem.byteSize() < 17L;
   }

   public static int getZoneId(MemorySegment mem) {
      return getZoneId(mem, 0);
   }

   public static int getZoneId(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 1);
   }

   @Nullable
   public static String getZoneName(MemorySegment mem) {
      return getZoneName(mem, 0);
   }

   @Nullable
   public static String getZoneName(MemorySegment mem, int offset) {
      return hasZoneName(mem, offset)
         ? PacketIO.readVarString("ZoneName", mem, offset + getValidatedOffset(mem, offset, 9, 17, "ZoneName"), 4096000, PacketIO.UTF8)
         : null;
   }

   @Nullable
   public static String getBiomeName(MemorySegment mem) {
      return getBiomeName(mem, 0);
   }

   @Nullable
   public static String getBiomeName(MemorySegment mem, int offset) {
      return hasBiomeName(mem, offset)
         ? PacketIO.readVarString("BiomeName", mem, offset + getValidatedOffset(mem, offset, 13, 17, "BiomeName"), 4096000, PacketIO.UTF8)
         : null;
   }

   public static int getBiomeColor(MemorySegment mem) {
      return getBiomeColor(mem, 0);
   }

   public static int getBiomeColor(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 5);
   }

   public static boolean hasZoneName(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 1) != 0;
   }

   public static boolean hasBiomeName(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 2) != 0;
   }

   private static int getValidatedOffset(MemorySegment buffer, int base, int slotPosition, int varBlockStart, String fieldName) {
      int offset = buffer.get(PacketIO.PROTO_INT, base + slotPosition);
      if (offset >= 0 && offset <= buffer.byteSize() - base - varBlockStart) {
         return varBlockStart + offset;
      } else {
         throw ProtocolException.invalidOffset(fieldName, offset, (int)buffer.byteSize());
      }
   }

   public static BiomeData toObject(MemorySegment mem) {
      return toObject(mem, 0);
   }

   public static BiomeData toObject(MemorySegment mem, int offset) {
      if (offset + 17 > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("BiomeData", offset + 17, (int)mem.byteSize());
      } else {
         return new BiomeData(
            mem.get(PacketIO.PROTO_INT, offset + 1),
            hasZoneName(mem, offset)
               ? PacketIO.readVarString("ZoneName", mem, offset + getValidatedOffset(mem, offset, 9, 17, "ZoneName"), 4096000, PacketIO.UTF8)
               : null,
            hasBiomeName(mem, offset)
               ? PacketIO.readVarString("BiomeName", mem, offset + getValidatedOffset(mem, offset, 13, 17, "BiomeName"), 4096000, PacketIO.UTF8)
               : null,
            mem.get(PacketIO.PROTO_INT, offset + 5)
         );
      }
   }

   public void serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      byte nullBits = 0;
      if (this.zoneName != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.biomeName != null) {
         nullBits = (byte)(nullBits | 2);
      }

      buf.writeByte(nullBits);
      buf.writeIntLE(this.zoneId);
      buf.writeIntLE(this.biomeColor);
      int zoneNameOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int biomeNameOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.zoneName != null) {
         buf.setIntLE(zoneNameOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.zoneName, 4096000);
      } else {
         buf.setIntLE(zoneNameOffsetSlot, -1);
      }

      if (this.biomeName != null) {
         buf.setIntLE(biomeNameOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.biomeName, 4096000);
      } else {
         buf.setIntLE(biomeNameOffsetSlot, -1);
      }
   }

   public int serialize(@Nonnull MemorySegment mem, int offset) {
      byte nullBits = 0;
      if (this.zoneName != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.biomeName != null) {
         nullBits = (byte)(nullBits | 2);
      }

      mem.set(PacketIO.PROTO_BYTE, offset + 0, nullBits);
      mem.set(PacketIO.PROTO_INT, offset + 1, this.zoneId);
      mem.set(PacketIO.PROTO_INT, offset + 5, this.biomeColor);
      int varOffset = offset + 17;
      if (this.zoneName != null) {
         mem.set(PacketIO.PROTO_INT, offset + 9, varOffset - offset - 17);
         varOffset += PacketIO.writeVarString(mem, varOffset, this.zoneName, 4096000);
      } else {
         mem.set(PacketIO.PROTO_INT, offset + 9, -1);
      }

      if (this.biomeName != null) {
         mem.set(PacketIO.PROTO_INT, offset + 13, varOffset - offset - 17);
         varOffset += PacketIO.writeVarString(mem, varOffset, this.biomeName, 4096000);
      } else {
         mem.set(PacketIO.PROTO_INT, offset + 13, -1);
      }

      return varOffset - offset;
   }

   public int computeSize() {
      int size = 17;
      if (this.zoneName != null) {
         size += PacketIO.stringSize(this.zoneName);
      }

      if (this.biomeName != null) {
         size += PacketIO.stringSize(this.biomeName);
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 17) {
         return ValidationResult.error("Buffer too small: expected at least 17 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      if ((nullBits & 1) != 0) {
         int zoneNameOffset = buffer.getIntLE(offset + 9);
         if (zoneNameOffset < 0 || zoneNameOffset > buffer.writerIndex() - offset - 17) {
            return ValidationResult.error("Invalid offset for ZoneName");
         }

         int pos = offset + 17 + zoneNameOffset;
         int zoneNameLen = VarInt.peek(buffer, pos);
         if (zoneNameLen < 0) {
            return ValidationResult.error("Invalid string length for ZoneName");
         }

         if (zoneNameLen > 4096000) {
            return ValidationResult.error("ZoneName exceeds max length 4096000");
         }

         pos += VarInt.size(zoneNameLen);
         pos += zoneNameLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading ZoneName");
         }
      }

      if ((nullBits & 2) != 0) {
         int biomeNameOffset = buffer.getIntLE(offset + 13);
         if (biomeNameOffset < 0 || biomeNameOffset > buffer.writerIndex() - offset - 17) {
            return ValidationResult.error("Invalid offset for BiomeName");
         }

         int pos = offset + 17 + biomeNameOffset;
         int biomeNameLen = VarInt.peek(buffer, pos);
         if (biomeNameLen < 0) {
            return ValidationResult.error("Invalid string length for BiomeName");
         }

         if (biomeNameLen > 4096000) {
            return ValidationResult.error("BiomeName exceeds max length 4096000");
         }

         pos += VarInt.size(biomeNameLen);
         pos += biomeNameLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading BiomeName");
         }
      }

      return ValidationResult.OK;
   }

   public BiomeData clone() {
      BiomeData copy = new BiomeData();
      copy.zoneId = this.zoneId;
      copy.zoneName = this.zoneName;
      copy.biomeName = this.biomeName;
      copy.biomeColor = this.biomeColor;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof BiomeData other)
            ? false
            : this.zoneId == other.zoneId
               && Objects.equals(this.zoneName, other.zoneName)
               && Objects.equals(this.biomeName, other.biomeName)
               && this.biomeColor == other.biomeColor;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.zoneId, this.zoneName, this.biomeName, this.biomeColor);
   }
}
