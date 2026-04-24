package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PhysicalMaterial {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 17;
   public static final int VARIABLE_FIELD_COUNT = 1;
   public static final int VARIABLE_BLOCK_START = 17;
   public static final int MAX_SIZE = 16384022;
   @Nullable
   public String id;
   public float reflectionCoeff;
   public float attenuationPerBlock;
   public float hFAttenuationPerBlock;
   public float shelterOpacity;

   public PhysicalMaterial() {
   }

   public PhysicalMaterial(@Nullable String id, float reflectionCoeff, float attenuationPerBlock, float hFAttenuationPerBlock, float shelterOpacity) {
      this.id = id;
      this.reflectionCoeff = reflectionCoeff;
      this.attenuationPerBlock = attenuationPerBlock;
      this.hFAttenuationPerBlock = hFAttenuationPerBlock;
      this.shelterOpacity = shelterOpacity;
   }

   public PhysicalMaterial(@Nonnull PhysicalMaterial other) {
      this.id = other.id;
      this.reflectionCoeff = other.reflectionCoeff;
      this.attenuationPerBlock = other.attenuationPerBlock;
      this.hFAttenuationPerBlock = other.hFAttenuationPerBlock;
      this.shelterOpacity = other.shelterOpacity;
   }

   @Nonnull
   public static PhysicalMaterial deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 17) {
         throw ProtocolException.bufferTooSmall("PhysicalMaterial", 17, buf.readableBytes() - offset);
      }

      PhysicalMaterial obj = new PhysicalMaterial();
      byte nullBits = buf.getByte(offset);
      obj.reflectionCoeff = buf.getFloatLE(offset + 1);
      obj.attenuationPerBlock = buf.getFloatLE(offset + 5);
      obj.hFAttenuationPerBlock = buf.getFloatLE(offset + 9);
      obj.shelterOpacity = buf.getFloatLE(offset + 13);
      int pos = offset + 17;
      if ((nullBits & 1) != 0) {
         int idLen = VarInt.peek(buf, pos);
         if (idLen < 0) {
            throw ProtocolException.invalidVarInt("Id");
         }

         int idVarLen = VarInt.size(idLen);
         if (idLen > 4096000) {
            throw ProtocolException.stringTooLong("Id", idLen, 4096000);
         }

         if (pos + idVarLen + idLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Id", pos + idVarLen + idLen, buf.readableBytes());
         }

         obj.id = PacketIO.readVarString(buf, pos, PacketIO.UTF8);
         pos += idVarLen + idLen;
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int pos = offset + 17;
      if ((nullBits & 1) != 0) {
         int sl = VarInt.peek(buf, pos);
         pos += VarInt.size(sl) + sl;
      }

      return pos - offset;
   }

   public void serialize(@Nonnull ByteBuf buf) {
      byte nullBits = 0;
      if (this.id != null) {
         nullBits = (byte)(nullBits | 1);
      }

      buf.writeByte(nullBits);
      buf.writeFloatLE(this.reflectionCoeff);
      buf.writeFloatLE(this.attenuationPerBlock);
      buf.writeFloatLE(this.hFAttenuationPerBlock);
      buf.writeFloatLE(this.shelterOpacity);
      if (this.id != null) {
         PacketIO.writeVarString(buf, this.id, 4096000);
      }
   }

   public int computeSize() {
      int size = 17;
      if (this.id != null) {
         size += PacketIO.stringSize(this.id);
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 17) {
         return ValidationResult.error("Buffer too small: expected at least 17 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      int pos = offset + 17;
      if ((nullBits & 1) != 0) {
         int idLen = VarInt.peek(buffer, pos);
         if (idLen < 0) {
            return ValidationResult.error("Invalid string length for Id");
         }

         if (idLen > 4096000) {
            return ValidationResult.error("Id exceeds max length 4096000");
         }

         pos += VarInt.size(idLen);
         pos += idLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Id");
         }
      }

      return ValidationResult.OK;
   }

   public PhysicalMaterial clone() {
      PhysicalMaterial copy = new PhysicalMaterial();
      copy.id = this.id;
      copy.reflectionCoeff = this.reflectionCoeff;
      copy.attenuationPerBlock = this.attenuationPerBlock;
      copy.hFAttenuationPerBlock = this.hFAttenuationPerBlock;
      copy.shelterOpacity = this.shelterOpacity;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof PhysicalMaterial other)
            ? false
            : Objects.equals(this.id, other.id)
               && this.reflectionCoeff == other.reflectionCoeff
               && this.attenuationPerBlock == other.attenuationPerBlock
               && this.hFAttenuationPerBlock == other.hFAttenuationPerBlock
               && this.shelterOpacity == other.shelterOpacity;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.id, this.reflectionCoeff, this.attenuationPerBlock, this.hFAttenuationPerBlock, this.shelterOpacity);
   }
}
