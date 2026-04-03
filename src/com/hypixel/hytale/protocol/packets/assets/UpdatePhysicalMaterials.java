package com.hypixel.hytale.protocol.packets.assets;

import com.hypixel.hytale.protocol.NetworkChannel;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.PhysicalMaterial;
import com.hypixel.hytale.protocol.ToClientPacket;
import com.hypixel.hytale.protocol.UpdateType;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UpdatePhysicalMaterials implements Packet, ToClientPacket {
   public static final int PACKET_ID = 87;
   public static final boolean IS_COMPRESSED = true;
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 6;
   public static final int VARIABLE_FIELD_COUNT = 1;
   public static final int VARIABLE_BLOCK_START = 6;
   public static final int MAX_SIZE = 1677721600;
   @Nonnull
   public UpdateType type = UpdateType.Init;
   public int maxId;
   @Nullable
   public Map<Integer, PhysicalMaterial> physicalMaterials;

   @Override
   public int getId() {
      return 87;
   }

   @Override
   public NetworkChannel getChannel() {
      return NetworkChannel.Default;
   }

   public UpdatePhysicalMaterials() {
   }

   public UpdatePhysicalMaterials(@Nonnull UpdateType type, int maxId, @Nullable Map<Integer, PhysicalMaterial> physicalMaterials) {
      this.type = type;
      this.maxId = maxId;
      this.physicalMaterials = physicalMaterials;
   }

   public UpdatePhysicalMaterials(@Nonnull UpdatePhysicalMaterials other) {
      this.type = other.type;
      this.maxId = other.maxId;
      this.physicalMaterials = other.physicalMaterials;
   }

   @Nonnull
   public static UpdatePhysicalMaterials deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 6) {
         throw ProtocolException.bufferTooSmall("UpdatePhysicalMaterials", 6, buf.readableBytes() - offset);
      }

      UpdatePhysicalMaterials obj = new UpdatePhysicalMaterials();
      byte nullBits = buf.getByte(offset);
      obj.type = UpdateType.fromValue(buf.getByte(offset + 1));
      obj.maxId = buf.getIntLE(offset + 2);
      int pos = offset + 6;
      if ((nullBits & 1) != 0) {
         int physicalMaterialsCount = VarInt.peek(buf, pos);
         if (physicalMaterialsCount < 0) {
            throw ProtocolException.invalidVarInt("PhysicalMaterials");
         }

         int physicalMaterialsVarLen = VarInt.size(physicalMaterialsCount);
         if (physicalMaterialsCount > 4096000) {
            throw ProtocolException.dictionaryTooLarge("PhysicalMaterials", physicalMaterialsCount, 4096000);
         }

         pos += physicalMaterialsVarLen;
         obj.physicalMaterials = new HashMap<>(physicalMaterialsCount);

         for (int i = 0; i < physicalMaterialsCount; i++) {
            int key = buf.getIntLE(pos);
            pos += 4;
            PhysicalMaterial val = PhysicalMaterial.deserialize(buf, pos);
            pos += PhysicalMaterial.computeBytesConsumed(buf, pos);
            if (obj.physicalMaterials.put(key, val) != null) {
               throw ProtocolException.duplicateKey("physicalMaterials", key);
            }
         }
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int pos = offset + 6;
      if ((nullBits & 1) != 0) {
         int dictLen = VarInt.peek(buf, pos);
         pos += VarInt.size(dictLen);

         for (int i = 0; i < dictLen; i++) {
            pos += 4;
            pos += PhysicalMaterial.computeBytesConsumed(buf, pos);
         }
      }

      return pos - offset;
   }

   @Override
   public void serialize(@Nonnull ByteBuf buf) {
      byte nullBits = 0;
      if (this.physicalMaterials != null) {
         nullBits = (byte)(nullBits | 1);
      }

      buf.writeByte(nullBits);
      buf.writeByte(this.type.getValue());
      buf.writeIntLE(this.maxId);
      if (this.physicalMaterials != null) {
         if (this.physicalMaterials.size() > 4096000) {
            throw ProtocolException.dictionaryTooLarge("PhysicalMaterials", this.physicalMaterials.size(), 4096000);
         }

         VarInt.write(buf, this.physicalMaterials.size());

         for (Entry<Integer, PhysicalMaterial> e : this.physicalMaterials.entrySet()) {
            buf.writeIntLE(e.getKey());
            e.getValue().serialize(buf);
         }
      }
   }

   @Override
   public int computeSize() {
      int size = 6;
      if (this.physicalMaterials != null) {
         int physicalMaterialsSize = 0;

         for (Entry<Integer, PhysicalMaterial> kvp : this.physicalMaterials.entrySet()) {
            physicalMaterialsSize += 4 + kvp.getValue().computeSize();
         }

         size += VarInt.size(this.physicalMaterials.size()) + physicalMaterialsSize;
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 6) {
         return ValidationResult.error("Buffer too small: expected at least 6 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      int v = buffer.getByte(offset + 1) & 255;
      if (v >= 3) {
         return ValidationResult.error("Invalid UpdateType value for Type");
      }

      v = offset + 6;
      if ((nullBits & 1) != 0) {
         int physicalMaterialsCount = VarInt.peek(buffer, v);
         if (physicalMaterialsCount < 0) {
            return ValidationResult.error("Invalid dictionary count for PhysicalMaterials");
         }

         if (physicalMaterialsCount > 4096000) {
            return ValidationResult.error("PhysicalMaterials exceeds max length 4096000");
         }

         v += VarInt.size(physicalMaterialsCount);

         for (int i = 0; i < physicalMaterialsCount; i++) {
            v += 4;
            if (v > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading key");
            }

            v += PhysicalMaterial.computeBytesConsumed(buffer, v);
         }
      }

      return ValidationResult.OK;
   }

   public UpdatePhysicalMaterials clone() {
      UpdatePhysicalMaterials copy = new UpdatePhysicalMaterials();
      copy.type = this.type;
      copy.maxId = this.maxId;
      if (this.physicalMaterials != null) {
         Map<Integer, PhysicalMaterial> m = new HashMap<>();

         for (Entry<Integer, PhysicalMaterial> e : this.physicalMaterials.entrySet()) {
            m.put(e.getKey(), e.getValue().clone());
         }

         copy.physicalMaterials = m;
      }

      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof UpdatePhysicalMaterials other)
            ? false
            : Objects.equals(this.type, other.type) && this.maxId == other.maxId && Objects.equals(this.physicalMaterials, other.physicalMaterials);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.type, this.maxId, this.physicalMaterials);
   }
}
