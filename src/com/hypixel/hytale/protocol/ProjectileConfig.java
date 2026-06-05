package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.lang.foreign.MemorySegment;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3fc;

public class ProjectileConfig {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 169;
   public static final int VARIABLE_FIELD_COUNT = 2;
   public static final int VARIABLE_BLOCK_START = 177;
   public static final int MAX_SIZE = 1677721600;
   @Nullable
   public PhysicsConfig physicsConfig;
   @Nullable
   public Model model;
   public double launchForce;
   @Nullable
   public Vector3fc spawnOffset;
   @Nullable
   public Direction rotationOffset;
   public boolean rotateSpawnOffsetByPitch;
   public boolean rotateSpawnOffsetByYaw;
   @Nullable
   public Map<InteractionType, Integer> interactions;
   public int launchLocalSoundEventIndex;
   public int launchWorldSoundEventIndex;
   public int projectileSoundEventIndex;

   public ProjectileConfig() {
   }

   public ProjectileConfig(
      @Nullable PhysicsConfig physicsConfig,
      @Nullable Model model,
      double launchForce,
      @Nullable Vector3fc spawnOffset,
      @Nullable Direction rotationOffset,
      boolean rotateSpawnOffsetByPitch,
      boolean rotateSpawnOffsetByYaw,
      @Nullable Map<InteractionType, Integer> interactions,
      int launchLocalSoundEventIndex,
      int launchWorldSoundEventIndex,
      int projectileSoundEventIndex
   ) {
      this.physicsConfig = physicsConfig;
      this.model = model;
      this.launchForce = launchForce;
      this.spawnOffset = spawnOffset;
      this.rotationOffset = rotationOffset;
      this.rotateSpawnOffsetByPitch = rotateSpawnOffsetByPitch;
      this.rotateSpawnOffsetByYaw = rotateSpawnOffsetByYaw;
      this.interactions = interactions;
      this.launchLocalSoundEventIndex = launchLocalSoundEventIndex;
      this.launchWorldSoundEventIndex = launchWorldSoundEventIndex;
      this.projectileSoundEventIndex = projectileSoundEventIndex;
   }

   public ProjectileConfig(@Nonnull ProjectileConfig other) {
      this.physicsConfig = other.physicsConfig;
      this.model = other.model;
      this.launchForce = other.launchForce;
      this.spawnOffset = other.spawnOffset;
      this.rotationOffset = other.rotationOffset;
      this.rotateSpawnOffsetByPitch = other.rotateSpawnOffsetByPitch;
      this.rotateSpawnOffsetByYaw = other.rotateSpawnOffsetByYaw;
      this.interactions = other.interactions;
      this.launchLocalSoundEventIndex = other.launchLocalSoundEventIndex;
      this.launchWorldSoundEventIndex = other.launchWorldSoundEventIndex;
      this.projectileSoundEventIndex = other.projectileSoundEventIndex;
   }

   @Nonnull
   public static ProjectileConfig deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 177) {
         throw ProtocolException.bufferTooSmall("ProjectileConfig", 177, buf.readableBytes() - offset);
      }

      ProjectileConfig obj = new ProjectileConfig();
      byte nullBits = buf.getByte(offset);
      if ((nullBits & 1) != 0) {
         obj.physicsConfig = PhysicsConfig.deserialize(buf, offset + 1);
      }

      obj.launchForce = buf.getDoubleLE(offset + 123);
      if ((nullBits & 2) != 0) {
         obj.spawnOffset = PacketIO.readVector3f(buf, offset + 131);
      }

      if ((nullBits & 4) != 0) {
         obj.rotationOffset = Direction.deserialize(buf, offset + 143);
      }

      obj.rotateSpawnOffsetByPitch = buf.getByte(offset + 155) != 0;
      obj.rotateSpawnOffsetByYaw = buf.getByte(offset + 156) != 0;
      obj.launchLocalSoundEventIndex = buf.getIntLE(offset + 157);
      obj.launchWorldSoundEventIndex = buf.getIntLE(offset + 161);
      obj.projectileSoundEventIndex = buf.getIntLE(offset + 165);
      if ((nullBits & 8) != 0) {
         int varPosBase0 = buf.getIntLE(offset + 169);
         if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 177) {
            throw ProtocolException.invalidOffset("Model", varPosBase0, buf.readableBytes());
         }

         int varPos0 = offset + 177 + varPosBase0;
         obj.model = Model.deserialize(buf, varPos0);
      }

      if ((nullBits & 16) != 0) {
         int varPosBase1 = buf.getIntLE(offset + 173);
         if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 177) {
            throw ProtocolException.invalidOffset("Interactions", varPosBase1, buf.readableBytes());
         }

         int varPos1 = offset + 177 + varPosBase1;
         int interactionsCount = VarInt.peek(buf, varPos1);
         if (interactionsCount < 0) {
            throw ProtocolException.invalidVarInt("Interactions");
         }

         int varIntLen = VarInt.size(interactionsCount);
         if (interactionsCount > 4096000) {
            throw ProtocolException.dictionaryTooLarge("Interactions", interactionsCount, 4096000);
         }

         obj.interactions = new HashMap<>(interactionsCount);
         int dictPos = varPos1 + varIntLen;

         for (int i = 0; i < interactionsCount; i++) {
            InteractionType key = InteractionType.fromValue(buf.getByte(dictPos));
            int val = buf.getIntLE(++dictPos);
            dictPos += 4;
            if (obj.interactions.put(key, val) != null) {
               throw ProtocolException.duplicateKey("interactions", key);
            }
         }
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 177;
      if ((nullBits & 8) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 169);
         if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 177) {
            throw ProtocolException.invalidOffset("Model", fieldOffset0, maxEnd);
         }

         int pos0 = offset + 177 + fieldOffset0;
         pos0 += Model.computeBytesConsumed(buf, pos0);
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits & 16) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 173);
         if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 177) {
            throw ProtocolException.invalidOffset("Interactions", fieldOffset1, maxEnd);
         }

         int pos1 = offset + 177 + fieldOffset1;
         int dictLen = VarInt.peek(buf, pos1);
         pos1 += VarInt.size(dictLen);

         for (int i = 0; i < dictLen; i++) {
            pos1 = ++pos1 + 4;
         }

         if (pos1 - offset > maxEnd) {
            maxEnd = pos1 - offset;
         }
      }

      return maxEnd;
   }

   public static boolean isBufferTooSmall(MemorySegment mem) {
      return mem.byteSize() < 177L;
   }

   @Nullable
   public static PhysicsConfig getPhysicsConfig(MemorySegment mem) {
      return getPhysicsConfig(mem, 0);
   }

   @Nullable
   public static PhysicsConfig getPhysicsConfig(MemorySegment mem, int offset) {
      return hasPhysicsConfig(mem, offset) ? PhysicsConfig.toObject(mem, offset + 1) : null;
   }

   @Nullable
   public static Model getModel(MemorySegment mem) {
      return getModel(mem, 0);
   }

   @Nullable
   public static Model getModel(MemorySegment mem, int offset) {
      return hasModel(mem, offset) ? Model.toObject(mem, offset + getValidatedOffset(mem, offset, 169, 177, "Model")) : null;
   }

   public static double getLaunchForce(MemorySegment mem) {
      return getLaunchForce(mem, 0);
   }

   public static double getLaunchForce(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_DOUBLE, offset + 123);
   }

   @Nullable
   public static Vector3fc getSpawnOffset(MemorySegment mem) {
      return getSpawnOffset(mem, 0);
   }

   @Nullable
   public static Vector3fc getSpawnOffset(MemorySegment mem, int offset) {
      return hasSpawnOffset(mem, offset) ? PacketIO.readVector3f(mem, offset + 131) : null;
   }

   @Nullable
   public static Direction getRotationOffset(MemorySegment mem) {
      return getRotationOffset(mem, 0);
   }

   @Nullable
   public static Direction getRotationOffset(MemorySegment mem, int offset) {
      return hasRotationOffset(mem, offset) ? Direction.toObject(mem, offset + 143) : null;
   }

   public static boolean getRotateSpawnOffsetByPitch(MemorySegment mem) {
      return getRotateSpawnOffsetByPitch(mem, 0);
   }

   public static boolean getRotateSpawnOffsetByPitch(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_BOOL, offset + 155);
   }

   public static boolean getRotateSpawnOffsetByYaw(MemorySegment mem) {
      return getRotateSpawnOffsetByYaw(mem, 0);
   }

   public static boolean getRotateSpawnOffsetByYaw(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_BOOL, offset + 156);
   }

   @Nullable
   public static Map<InteractionType, Integer> getInteractions(MemorySegment mem) {
      return getInteractions(mem, 0);
   }

   @Nullable
   public static Map<InteractionType, Integer> getInteractions(MemorySegment mem, int offset) {
      if (!hasInteractions(mem, offset)) {
         return null;
      }

      int off = offset + getValidatedOffset(mem, offset, 173, 177, "Interactions");
      long packed = VarInt.getWithLength(mem, off);
      int len = (int)packed;
      if (len < 0) {
         throw ProtocolException.negativeLength("Interactions", len);
      }

      if (len > 4096000) {
         throw ProtocolException.dictionaryTooLarge("Interactions", len, 4096000);
      }

      Map<InteractionType, Integer> data = new HashMap<>(len);
      off += (int)(packed >>> 32);

      for (int i = 0; i < len; i++) {
         InteractionType key = InteractionType.fromValue(mem.get(PacketIO.PROTO_BYTE, off));
         int value = mem.get(PacketIO.PROTO_INT, ++off);
         off += 4;
         if (data.put(key, value) != null) {
            throw ProtocolException.duplicateKey("Interactions", key);
         }
      }

      return data;
   }

   public static int getLaunchLocalSoundEventIndex(MemorySegment mem) {
      return getLaunchLocalSoundEventIndex(mem, 0);
   }

   public static int getLaunchLocalSoundEventIndex(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 157);
   }

   public static int getLaunchWorldSoundEventIndex(MemorySegment mem) {
      return getLaunchWorldSoundEventIndex(mem, 0);
   }

   public static int getLaunchWorldSoundEventIndex(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 161);
   }

   public static int getProjectileSoundEventIndex(MemorySegment mem) {
      return getProjectileSoundEventIndex(mem, 0);
   }

   public static int getProjectileSoundEventIndex(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 165);
   }

   public static boolean hasPhysicsConfig(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 1) != 0;
   }

   public static boolean hasSpawnOffset(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 2) != 0;
   }

   public static boolean hasRotationOffset(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 4) != 0;
   }

   public static boolean hasModel(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 8) != 0;
   }

   public static boolean hasInteractions(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 16) != 0;
   }

   private static int getValidatedOffset(MemorySegment buffer, int base, int slotPosition, int varBlockStart, String fieldName) {
      int offset = buffer.get(PacketIO.PROTO_INT, base + slotPosition);
      if (offset >= 0 && offset <= buffer.byteSize() - base - varBlockStart) {
         return varBlockStart + offset;
      } else {
         throw ProtocolException.invalidOffset(fieldName, offset, (int)buffer.byteSize());
      }
   }

   public static ProjectileConfig toObject(MemorySegment mem) {
      return toObject(mem, 0);
   }

   public static ProjectileConfig toObject(MemorySegment mem, int offset) {
      if (offset + 177 > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("ProjectileConfig", offset + 177, (int)mem.byteSize());
      }

      Map<InteractionType, Integer> interactions = null;
      if (hasInteractions(mem, offset)) {
         int off = offset + getValidatedOffset(mem, offset, 173, 177, "Interactions");
         long packed = VarInt.getWithLength(mem, off);
         int len = (int)packed;
         if (len < 0) {
            throw ProtocolException.negativeLength("Interactions", len);
         }

         if (len > 4096000) {
            throw ProtocolException.dictionaryTooLarge("Interactions", len, 4096000);
         }

         interactions = new HashMap<>(len);
         off += (int)(packed >>> 32);

         for (int i = 0; i < len; i++) {
            InteractionType key = InteractionType.fromValue(mem.get(PacketIO.PROTO_BYTE, off));
            int value = mem.get(PacketIO.PROTO_INT, ++off);
            off += 4;
            if (interactions.put(key, value) != null) {
               throw ProtocolException.duplicateKey("Interactions", key);
            }
         }
      }

      return new ProjectileConfig(
         hasPhysicsConfig(mem, offset) ? PhysicsConfig.toObject(mem, offset + 1) : null,
         hasModel(mem, offset) ? Model.toObject(mem, offset + getValidatedOffset(mem, offset, 169, 177, "Model")) : null,
         mem.get(PacketIO.PROTO_DOUBLE, offset + 123),
         hasSpawnOffset(mem, offset) ? PacketIO.readVector3f(mem, offset + 131) : null,
         hasRotationOffset(mem, offset) ? Direction.toObject(mem, offset + 143) : null,
         mem.get(PacketIO.PROTO_BOOL, offset + 155),
         mem.get(PacketIO.PROTO_BOOL, offset + 156),
         interactions,
         mem.get(PacketIO.PROTO_INT, offset + 157),
         mem.get(PacketIO.PROTO_INT, offset + 161),
         mem.get(PacketIO.PROTO_INT, offset + 165)
      );
   }

   public void serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      byte nullBits = 0;
      if (this.physicsConfig != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.spawnOffset != null) {
         nullBits = (byte)(nullBits | 2);
      }

      if (this.rotationOffset != null) {
         nullBits = (byte)(nullBits | 4);
      }

      if (this.model != null) {
         nullBits = (byte)(nullBits | 8);
      }

      if (this.interactions != null) {
         nullBits = (byte)(nullBits | 16);
      }

      buf.writeByte(nullBits);
      if (this.physicsConfig != null) {
         this.physicsConfig.serialize(buf);
      } else {
         buf.writeZero(122);
      }

      buf.writeDoubleLE(this.launchForce);
      if (this.spawnOffset != null) {
         PacketIO.writeVector3f(buf, this.spawnOffset);
      } else {
         buf.writeZero(12);
      }

      if (this.rotationOffset != null) {
         this.rotationOffset.serialize(buf);
      } else {
         buf.writeZero(12);
      }

      buf.writeByte(this.rotateSpawnOffsetByPitch ? 1 : 0);
      buf.writeByte(this.rotateSpawnOffsetByYaw ? 1 : 0);
      buf.writeIntLE(this.launchLocalSoundEventIndex);
      buf.writeIntLE(this.launchWorldSoundEventIndex);
      buf.writeIntLE(this.projectileSoundEventIndex);
      int modelOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int interactionsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.model != null) {
         buf.setIntLE(modelOffsetSlot, buf.writerIndex() - varBlockStart);
         this.model.serialize(buf);
      } else {
         buf.setIntLE(modelOffsetSlot, -1);
      }

      if (this.interactions != null) {
         buf.setIntLE(interactionsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.interactions.size() > 4096000) {
            throw ProtocolException.dictionaryTooLarge("Interactions", this.interactions.size(), 4096000);
         }

         VarInt.write(buf, this.interactions.size());

         for (Entry<InteractionType, Integer> e : this.interactions.entrySet()) {
            buf.writeByte(e.getKey().getValue());
            buf.writeIntLE(e.getValue());
         }
      } else {
         buf.setIntLE(interactionsOffsetSlot, -1);
      }
   }

   public int serialize(@Nonnull MemorySegment mem, int offset) {
      byte nullBits = 0;
      if (this.physicsConfig != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.spawnOffset != null) {
         nullBits = (byte)(nullBits | 2);
      }

      if (this.rotationOffset != null) {
         nullBits = (byte)(nullBits | 4);
      }

      if (this.model != null) {
         nullBits = (byte)(nullBits | 8);
      }

      if (this.interactions != null) {
         nullBits = (byte)(nullBits | 16);
      }

      mem.set(PacketIO.PROTO_BYTE, offset + 0, nullBits);
      if (this.physicsConfig != null) {
         this.physicsConfig.serialize(mem, offset + 1);
      } else {
         mem.asSlice(offset + 1, 122L).fill((byte)0);
      }

      mem.set(PacketIO.PROTO_DOUBLE, offset + 123, this.launchForce);
      if (this.spawnOffset != null) {
         PacketIO.writeVector3f(mem, offset + 131, this.spawnOffset);
      } else {
         mem.asSlice(offset + 131, 12L).fill((byte)0);
      }

      if (this.rotationOffset != null) {
         this.rotationOffset.serialize(mem, offset + 143);
      } else {
         mem.asSlice(offset + 143, 12L).fill((byte)0);
      }

      mem.set(PacketIO.PROTO_BOOL, offset + 155, this.rotateSpawnOffsetByPitch);
      mem.set(PacketIO.PROTO_BOOL, offset + 156, this.rotateSpawnOffsetByYaw);
      mem.set(PacketIO.PROTO_INT, offset + 157, this.launchLocalSoundEventIndex);
      mem.set(PacketIO.PROTO_INT, offset + 161, this.launchWorldSoundEventIndex);
      mem.set(PacketIO.PROTO_INT, offset + 165, this.projectileSoundEventIndex);
      int varOffset = offset + 177;
      if (this.model != null) {
         mem.set(PacketIO.PROTO_INT, offset + 169, varOffset - offset - 177);
         varOffset += this.model.serialize(mem, varOffset);
      } else {
         mem.set(PacketIO.PROTO_INT, offset + 169, -1);
      }

      if (this.interactions != null) {
         mem.set(PacketIO.PROTO_INT, offset + 173, varOffset - offset - 177);
         if (this.interactions.size() > 4096000) {
            throw ProtocolException.dictionaryTooLarge("Interactions", this.interactions.size(), 4096000);
         }

         varOffset += VarInt.set(mem, varOffset, this.interactions.size());

         for (Entry<InteractionType, Integer> e : this.interactions.entrySet()) {
            mem.set(PacketIO.PROTO_BYTE, varOffset, (byte)e.getKey().getValue());
            mem.set(PacketIO.PROTO_INT, ++varOffset, e.getValue());
            varOffset += 4;
         }
      } else {
         mem.set(PacketIO.PROTO_INT, offset + 173, -1);
      }

      return varOffset - offset;
   }

   public int computeSize() {
      int size = 177;
      if (this.model != null) {
         size += this.model.computeSize();
      }

      if (this.interactions != null) {
         size += VarInt.size(this.interactions.size()) + this.interactions.size() * 5;
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 177) {
         return ValidationResult.error("Buffer too small: expected at least 177 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      if ((nullBits & 8) != 0) {
         int modelOffset = buffer.getIntLE(offset + 169);
         if (modelOffset < 0 || modelOffset > buffer.writerIndex() - offset - 177) {
            return ValidationResult.error("Invalid offset for Model");
         }

         int pos = offset + 177 + modelOffset;
         ValidationResult modelResult = Model.validateStructure(buffer, pos);
         if (!modelResult.isValid()) {
            return ValidationResult.error("Invalid Model: " + modelResult.error());
         }

         pos += Model.computeBytesConsumed(buffer, pos);
      }

      if ((nullBits & 16) != 0) {
         int interactionsOffset = buffer.getIntLE(offset + 173);
         if (interactionsOffset < 0 || interactionsOffset > buffer.writerIndex() - offset - 177) {
            return ValidationResult.error("Invalid offset for Interactions");
         }

         int pos = offset + 177 + interactionsOffset;
         int interactionsCount = VarInt.peek(buffer, pos);
         if (interactionsCount < 0) {
            return ValidationResult.error("Invalid dictionary count for Interactions");
         }

         if (interactionsCount > 4096000) {
            return ValidationResult.error("Interactions exceeds max length 4096000");
         }

         pos += VarInt.size(interactionsCount);

         for (int i = 0; i < interactionsCount; i++) {
            int v = buffer.getByte(pos) & 255;
            if (v >= 25) {
               return ValidationResult.error("Invalid InteractionType value for key");
            }

            pos = ++pos + 4;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading value");
            }
         }
      }

      return ValidationResult.OK;
   }

   public ProjectileConfig clone() {
      ProjectileConfig copy = new ProjectileConfig();
      copy.physicsConfig = this.physicsConfig != null ? this.physicsConfig.clone() : null;
      copy.model = this.model != null ? this.model.clone() : null;
      copy.launchForce = this.launchForce;
      copy.spawnOffset = this.spawnOffset;
      copy.rotationOffset = this.rotationOffset != null ? this.rotationOffset.clone() : null;
      copy.rotateSpawnOffsetByPitch = this.rotateSpawnOffsetByPitch;
      copy.rotateSpawnOffsetByYaw = this.rotateSpawnOffsetByYaw;
      copy.interactions = this.interactions != null ? new HashMap<>(this.interactions) : null;
      copy.launchLocalSoundEventIndex = this.launchLocalSoundEventIndex;
      copy.launchWorldSoundEventIndex = this.launchWorldSoundEventIndex;
      copy.projectileSoundEventIndex = this.projectileSoundEventIndex;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof ProjectileConfig other)
            ? false
            : Objects.equals(this.physicsConfig, other.physicsConfig)
               && Objects.equals(this.model, other.model)
               && this.launchForce == other.launchForce
               && Objects.equals(this.spawnOffset, other.spawnOffset)
               && Objects.equals(this.rotationOffset, other.rotationOffset)
               && this.rotateSpawnOffsetByPitch == other.rotateSpawnOffsetByPitch
               && this.rotateSpawnOffsetByYaw == other.rotateSpawnOffsetByYaw
               && Objects.equals(this.interactions, other.interactions)
               && this.launchLocalSoundEventIndex == other.launchLocalSoundEventIndex
               && this.launchWorldSoundEventIndex == other.launchWorldSoundEventIndex
               && this.projectileSoundEventIndex == other.projectileSoundEventIndex;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(
         this.physicsConfig,
         this.model,
         this.launchForce,
         this.spawnOffset,
         this.rotationOffset,
         this.rotateSpawnOffsetByPitch,
         this.rotateSpawnOffsetByYaw,
         this.interactions,
         this.launchLocalSoundEventIndex,
         this.launchWorldSoundEventIndex,
         this.projectileSoundEventIndex
      );
   }
}
