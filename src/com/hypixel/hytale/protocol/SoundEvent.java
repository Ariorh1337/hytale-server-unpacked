package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.lang.foreign.MemorySegment;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SoundEvent {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 31;
   public static final int VARIABLE_FIELD_COUNT = 4;
   public static final int VARIABLE_BLOCK_START = 47;
   public static final int MAX_SIZE = 1677721600;
   @Nullable
   public String id;
   public float volume;
   public float pitch;
   public int maxInstance;
   public boolean preventSoundInterruption;
   public float startAttenuationDistance;
   public float maxDistance;
   public float spatialBlend;
   @Nullable
   public SoundEventLayer[] layers;
   public int audioCategory;
   @Nullable
   public StateBinding[] stateBindings;
   @Nullable
   public AudioCategoryDuckingRule[] duckingRules;
   public boolean bypassDucking;

   public SoundEvent() {
   }

   public SoundEvent(
      @Nullable String id,
      float volume,
      float pitch,
      int maxInstance,
      boolean preventSoundInterruption,
      float startAttenuationDistance,
      float maxDistance,
      float spatialBlend,
      @Nullable SoundEventLayer[] layers,
      int audioCategory,
      @Nullable StateBinding[] stateBindings,
      @Nullable AudioCategoryDuckingRule[] duckingRules,
      boolean bypassDucking
   ) {
      this.id = id;
      this.volume = volume;
      this.pitch = pitch;
      this.maxInstance = maxInstance;
      this.preventSoundInterruption = preventSoundInterruption;
      this.startAttenuationDistance = startAttenuationDistance;
      this.maxDistance = maxDistance;
      this.spatialBlend = spatialBlend;
      this.layers = layers;
      this.audioCategory = audioCategory;
      this.stateBindings = stateBindings;
      this.duckingRules = duckingRules;
      this.bypassDucking = bypassDucking;
   }

   public SoundEvent(@Nonnull SoundEvent other) {
      this.id = other.id;
      this.volume = other.volume;
      this.pitch = other.pitch;
      this.maxInstance = other.maxInstance;
      this.preventSoundInterruption = other.preventSoundInterruption;
      this.startAttenuationDistance = other.startAttenuationDistance;
      this.maxDistance = other.maxDistance;
      this.spatialBlend = other.spatialBlend;
      this.layers = other.layers;
      this.audioCategory = other.audioCategory;
      this.stateBindings = other.stateBindings;
      this.duckingRules = other.duckingRules;
      this.bypassDucking = other.bypassDucking;
   }

   @Nonnull
   public static SoundEvent deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 47) {
         throw ProtocolException.bufferTooSmall("SoundEvent", 47, buf.readableBytes() - offset);
      }

      SoundEvent obj = new SoundEvent();
      byte nullBits = buf.getByte(offset);
      obj.volume = buf.getFloatLE(offset + 1);
      obj.pitch = buf.getFloatLE(offset + 5);
      obj.maxInstance = buf.getIntLE(offset + 9);
      obj.preventSoundInterruption = buf.getByte(offset + 13) != 0;
      obj.startAttenuationDistance = buf.getFloatLE(offset + 14);
      obj.maxDistance = buf.getFloatLE(offset + 18);
      obj.spatialBlend = buf.getFloatLE(offset + 22);
      obj.audioCategory = buf.getIntLE(offset + 26);
      obj.bypassDucking = buf.getByte(offset + 30) != 0;
      if ((nullBits & 1) != 0) {
         int varPosBase0 = buf.getIntLE(offset + 31);
         if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 47) {
            throw ProtocolException.invalidOffset("Id", varPosBase0, buf.readableBytes());
         }

         int varPos0 = offset + 47 + varPosBase0;
         int idLen = VarInt.peek(buf, varPos0);
         if (idLen < 0) {
            throw ProtocolException.invalidVarInt("Id");
         }

         int idVarIntLen = VarInt.size(idLen);
         if (idLen > 4096000) {
            throw ProtocolException.stringTooLong("Id", idLen, 4096000);
         }

         if (varPos0 + idVarIntLen + idLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Id", varPos0 + idVarIntLen + idLen, buf.readableBytes());
         }

         obj.id = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
      }

      if ((nullBits & 2) != 0) {
         int varPosBase1 = buf.getIntLE(offset + 35);
         if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 47) {
            throw ProtocolException.invalidOffset("Layers", varPosBase1, buf.readableBytes());
         }

         int varPos1 = offset + 47 + varPosBase1;
         int layersCount = VarInt.peek(buf, varPos1);
         if (layersCount < 0) {
            throw ProtocolException.invalidVarInt("Layers");
         }

         int varIntLen = VarInt.size(layersCount);
         if (layersCount > 4096000) {
            throw ProtocolException.arrayTooLong("Layers", layersCount, 4096000);
         }

         if (varPos1 + varIntLen + layersCount * 42L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Layers", varPos1 + varIntLen + layersCount * 42, buf.readableBytes());
         }

         obj.layers = new SoundEventLayer[layersCount];
         int elemPos = varPos1 + varIntLen;

         for (int i = 0; i < layersCount; i++) {
            obj.layers[i] = SoundEventLayer.deserialize(buf, elemPos);
            elemPos += SoundEventLayer.computeBytesConsumed(buf, elemPos);
         }
      }

      if ((nullBits & 4) != 0) {
         int varPosBase2 = buf.getIntLE(offset + 39);
         if (varPosBase2 < 0 || varPosBase2 > buf.writerIndex() - offset - 47) {
            throw ProtocolException.invalidOffset("StateBindings", varPosBase2, buf.readableBytes());
         }

         int varPos2 = offset + 47 + varPosBase2;
         int stateBindingsCount = VarInt.peek(buf, varPos2);
         if (stateBindingsCount < 0) {
            throw ProtocolException.invalidVarInt("StateBindings");
         }

         int varIntLen = VarInt.size(stateBindingsCount);
         if (stateBindingsCount > 4096000) {
            throw ProtocolException.arrayTooLong("StateBindings", stateBindingsCount, 4096000);
         }

         if (varPos2 + varIntLen + stateBindingsCount * 5L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("StateBindings", varPos2 + varIntLen + stateBindingsCount * 5, buf.readableBytes());
         }

         obj.stateBindings = new StateBinding[stateBindingsCount];
         int elemPos = varPos2 + varIntLen;

         for (int i = 0; i < stateBindingsCount; i++) {
            obj.stateBindings[i] = StateBinding.deserialize(buf, elemPos);
            elemPos += StateBinding.computeBytesConsumed(buf, elemPos);
         }
      }

      if ((nullBits & 8) != 0) {
         int varPosBase3 = buf.getIntLE(offset + 43);
         if (varPosBase3 < 0 || varPosBase3 > buf.writerIndex() - offset - 47) {
            throw ProtocolException.invalidOffset("DuckingRules", varPosBase3, buf.readableBytes());
         }

         int varPos3 = offset + 47 + varPosBase3;
         int duckingRulesCount = VarInt.peek(buf, varPos3);
         if (duckingRulesCount < 0) {
            throw ProtocolException.invalidVarInt("DuckingRules");
         }

         int varIntLen = VarInt.size(duckingRulesCount);
         if (duckingRulesCount > 4096000) {
            throw ProtocolException.arrayTooLong("DuckingRules", duckingRulesCount, 4096000);
         }

         if (varPos3 + varIntLen + duckingRulesCount * 24L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("DuckingRules", varPos3 + varIntLen + duckingRulesCount * 24, buf.readableBytes());
         }

         obj.duckingRules = new AudioCategoryDuckingRule[duckingRulesCount];
         int elemPos = varPos3 + varIntLen;

         for (int i = 0; i < duckingRulesCount; i++) {
            obj.duckingRules[i] = AudioCategoryDuckingRule.deserialize(buf, elemPos);
            elemPos += AudioCategoryDuckingRule.computeBytesConsumed(buf, elemPos);
         }
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 47;
      if ((nullBits & 1) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 31);
         if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 47) {
            throw ProtocolException.invalidOffset("Id", fieldOffset0, maxEnd);
         }

         int pos0 = offset + 47 + fieldOffset0;
         int sl = VarInt.peek(buf, pos0);
         pos0 += VarInt.size(sl) + sl;
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits & 2) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 35);
         if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 47) {
            throw ProtocolException.invalidOffset("Layers", fieldOffset1, maxEnd);
         }

         int pos1 = offset + 47 + fieldOffset1;
         int arrLen = VarInt.peek(buf, pos1);
         pos1 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            pos1 += SoundEventLayer.computeBytesConsumed(buf, pos1);
         }

         if (pos1 - offset > maxEnd) {
            maxEnd = pos1 - offset;
         }
      }

      if ((nullBits & 4) != 0) {
         int fieldOffset2 = buf.getIntLE(offset + 39);
         if (fieldOffset2 < 0 || fieldOffset2 > buf.writerIndex() - offset - 47) {
            throw ProtocolException.invalidOffset("StateBindings", fieldOffset2, maxEnd);
         }

         int pos2 = offset + 47 + fieldOffset2;
         int arrLen = VarInt.peek(buf, pos2);
         pos2 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            pos2 += StateBinding.computeBytesConsumed(buf, pos2);
         }

         if (pos2 - offset > maxEnd) {
            maxEnd = pos2 - offset;
         }
      }

      if ((nullBits & 8) != 0) {
         int fieldOffset3 = buf.getIntLE(offset + 43);
         if (fieldOffset3 < 0 || fieldOffset3 > buf.writerIndex() - offset - 47) {
            throw ProtocolException.invalidOffset("DuckingRules", fieldOffset3, maxEnd);
         }

         int pos3 = offset + 47 + fieldOffset3;
         int arrLen = VarInt.peek(buf, pos3);
         pos3 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            pos3 += AudioCategoryDuckingRule.computeBytesConsumed(buf, pos3);
         }

         if (pos3 - offset > maxEnd) {
            maxEnd = pos3 - offset;
         }
      }

      return maxEnd;
   }

   public static boolean isBufferTooSmall(MemorySegment mem) {
      return mem.byteSize() < 47L;
   }

   @Nullable
   public static String getId(MemorySegment mem) {
      return getId(mem, 0);
   }

   @Nullable
   public static String getId(MemorySegment mem, int offset) {
      return hasId(mem, offset) ? PacketIO.readVarString("Id", mem, offset + getValidatedOffset(mem, offset, 31, 47, "Id"), 4096000, PacketIO.UTF8) : null;
   }

   public static float getVolume(MemorySegment mem) {
      return getVolume(mem, 0);
   }

   public static float getVolume(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_FLOAT, offset + 1);
   }

   public static float getPitch(MemorySegment mem) {
      return getPitch(mem, 0);
   }

   public static float getPitch(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_FLOAT, offset + 5);
   }

   public static int getMaxInstance(MemorySegment mem) {
      return getMaxInstance(mem, 0);
   }

   public static int getMaxInstance(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 9);
   }

   public static boolean getPreventSoundInterruption(MemorySegment mem) {
      return getPreventSoundInterruption(mem, 0);
   }

   public static boolean getPreventSoundInterruption(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_BOOL, offset + 13);
   }

   public static float getStartAttenuationDistance(MemorySegment mem) {
      return getStartAttenuationDistance(mem, 0);
   }

   public static float getStartAttenuationDistance(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_FLOAT, offset + 14);
   }

   public static float getMaxDistance(MemorySegment mem) {
      return getMaxDistance(mem, 0);
   }

   public static float getMaxDistance(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_FLOAT, offset + 18);
   }

   public static float getSpatialBlend(MemorySegment mem) {
      return getSpatialBlend(mem, 0);
   }

   public static float getSpatialBlend(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_FLOAT, offset + 22);
   }

   @Nullable
   public static SoundEventLayer[] getLayers(MemorySegment mem) {
      return getLayers(mem, 0);
   }

   @Nullable
   public static SoundEventLayer[] getLayers(MemorySegment mem, int offset) {
      if (!hasLayers(mem, offset)) {
         return null;
      }

      int off = offset + getValidatedOffset(mem, offset, 35, 47, "Layers");
      long packed = VarInt.getWithLength(mem, off);
      int len = (int)packed;
      if (len < 0) {
         throw ProtocolException.negativeLength("Layers", len);
      }

      if (len > 4096000) {
         throw ProtocolException.arrayTooLong("Layers", len, 4096000);
      }

      int lenOffset = (int)(packed >>> 32);
      if (off + lenOffset + len > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("Layers", off + lenOffset + len, (int)mem.byteSize());
      }

      off += lenOffset;
      SoundEventLayer[] data = new SoundEventLayer[len];

      for (int i = 0; i < len; i++) {
         data[i] = SoundEventLayer.toObject(mem, off);
         off += data[i].computeSize();
      }

      return data;
   }

   public static int getAudioCategory(MemorySegment mem) {
      return getAudioCategory(mem, 0);
   }

   public static int getAudioCategory(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 26);
   }

   @Nullable
   public static StateBinding[] getStateBindings(MemorySegment mem) {
      return getStateBindings(mem, 0);
   }

   @Nullable
   public static StateBinding[] getStateBindings(MemorySegment mem, int offset) {
      if (!hasStateBindings(mem, offset)) {
         return null;
      }

      int off = offset + getValidatedOffset(mem, offset, 39, 47, "StateBindings");
      long packed = VarInt.getWithLength(mem, off);
      int len = (int)packed;
      if (len < 0) {
         throw ProtocolException.negativeLength("StateBindings", len);
      }

      if (len > 4096000) {
         throw ProtocolException.arrayTooLong("StateBindings", len, 4096000);
      }

      int lenOffset = (int)(packed >>> 32);
      if (off + lenOffset + len > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("StateBindings", off + lenOffset + len, (int)mem.byteSize());
      }

      off += lenOffset;
      StateBinding[] data = new StateBinding[len];

      for (int i = 0; i < len; i++) {
         data[i] = StateBinding.toObject(mem, off);
         off += data[i].computeSize();
      }

      return data;
   }

   @Nullable
   public static AudioCategoryDuckingRule[] getDuckingRules(MemorySegment mem) {
      return getDuckingRules(mem, 0);
   }

   @Nullable
   public static AudioCategoryDuckingRule[] getDuckingRules(MemorySegment mem, int offset) {
      if (!hasDuckingRules(mem, offset)) {
         return null;
      }

      int off = offset + getValidatedOffset(mem, offset, 43, 47, "DuckingRules");
      long packed = VarInt.getWithLength(mem, off);
      int len = (int)packed;
      if (len < 0) {
         throw ProtocolException.negativeLength("DuckingRules", len);
      }

      if (len > 4096000) {
         throw ProtocolException.arrayTooLong("DuckingRules", len, 4096000);
      }

      int lenOffset = (int)(packed >>> 32);
      if (off + lenOffset + len * 24L > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("DuckingRules", off + lenOffset + len * 24, (int)mem.byteSize());
      }

      off += lenOffset;
      AudioCategoryDuckingRule[] data = new AudioCategoryDuckingRule[len];

      for (int i = 0; i < len; i++) {
         data[i] = AudioCategoryDuckingRule.toObject(mem, off + i * 24);
      }

      return data;
   }

   public static boolean getBypassDucking(MemorySegment mem) {
      return getBypassDucking(mem, 0);
   }

   public static boolean getBypassDucking(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_BOOL, offset + 30);
   }

   public static boolean hasId(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 1) != 0;
   }

   public static boolean hasLayers(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 2) != 0;
   }

   public static boolean hasStateBindings(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 4) != 0;
   }

   public static boolean hasDuckingRules(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 8) != 0;
   }

   private static int getValidatedOffset(MemorySegment buffer, int base, int slotPosition, int varBlockStart, String fieldName) {
      int offset = buffer.get(PacketIO.PROTO_INT, base + slotPosition);
      if (offset >= 0 && offset <= buffer.byteSize() - base - varBlockStart) {
         return varBlockStart + offset;
      } else {
         throw ProtocolException.invalidOffset(fieldName, offset, (int)buffer.byteSize());
      }
   }

   public static SoundEvent toObject(MemorySegment mem) {
      return toObject(mem, 0);
   }

   public static SoundEvent toObject(MemorySegment mem, int offset) {
      if (offset + 47 > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("SoundEvent", offset + 47, (int)mem.byteSize());
      }

      SoundEventLayer[] layers = null;
      if (hasLayers(mem, offset)) {
         int off = offset + getValidatedOffset(mem, offset, 35, 47, "Layers");
         long packed = VarInt.getWithLength(mem, off);
         int len = (int)packed;
         if (len < 0) {
            throw ProtocolException.negativeLength("Layers", len);
         }

         if (len > 4096000) {
            throw ProtocolException.arrayTooLong("Layers", len, 4096000);
         }

         int lenOffset = (int)(packed >>> 32);
         if (off + lenOffset + len > mem.byteSize()) {
            throw ProtocolException.bufferTooSmall("Layers", off + lenOffset + len, (int)mem.byteSize());
         }

         off += lenOffset;
         layers = new SoundEventLayer[len];

         for (int i = 0; i < len; i++) {
            layers[i] = SoundEventLayer.toObject(mem, off);
            off += layers[i].computeSize();
         }
      }

      StateBinding[] stateBindings = null;
      if (hasStateBindings(mem, offset)) {
         int off = offset + getValidatedOffset(mem, offset, 39, 47, "StateBindings");
         long packed = VarInt.getWithLength(mem, off);
         int len = (int)packed;
         if (len < 0) {
            throw ProtocolException.negativeLength("StateBindings", len);
         }

         if (len > 4096000) {
            throw ProtocolException.arrayTooLong("StateBindings", len, 4096000);
         }

         int lenOffset = (int)(packed >>> 32);
         if (off + lenOffset + len > mem.byteSize()) {
            throw ProtocolException.bufferTooSmall("StateBindings", off + lenOffset + len, (int)mem.byteSize());
         }

         off += lenOffset;
         stateBindings = new StateBinding[len];

         for (int i = 0; i < len; i++) {
            stateBindings[i] = StateBinding.toObject(mem, off);
            off += stateBindings[i].computeSize();
         }
      }

      AudioCategoryDuckingRule[] duckingRules = null;
      if (hasDuckingRules(mem, offset)) {
         int off = offset + getValidatedOffset(mem, offset, 43, 47, "DuckingRules");
         long packed = VarInt.getWithLength(mem, off);
         int len = (int)packed;
         if (len < 0) {
            throw ProtocolException.negativeLength("DuckingRules", len);
         }

         if (len > 4096000) {
            throw ProtocolException.arrayTooLong("DuckingRules", len, 4096000);
         }

         int lenOffset = (int)(packed >>> 32);
         if (off + lenOffset + len * 24L > mem.byteSize()) {
            throw ProtocolException.bufferTooSmall("DuckingRules", off + lenOffset + len * 24, (int)mem.byteSize());
         }

         off += lenOffset;
         duckingRules = new AudioCategoryDuckingRule[len];

         for (int i = 0; i < len; i++) {
            duckingRules[i] = AudioCategoryDuckingRule.toObject(mem, off + i * 24);
         }
      }

      return new SoundEvent(
         hasId(mem, offset) ? PacketIO.readVarString("Id", mem, offset + getValidatedOffset(mem, offset, 31, 47, "Id"), 4096000, PacketIO.UTF8) : null,
         mem.get(PacketIO.PROTO_FLOAT, offset + 1),
         mem.get(PacketIO.PROTO_FLOAT, offset + 5),
         mem.get(PacketIO.PROTO_INT, offset + 9),
         mem.get(PacketIO.PROTO_BOOL, offset + 13),
         mem.get(PacketIO.PROTO_FLOAT, offset + 14),
         mem.get(PacketIO.PROTO_FLOAT, offset + 18),
         mem.get(PacketIO.PROTO_FLOAT, offset + 22),
         layers,
         mem.get(PacketIO.PROTO_INT, offset + 26),
         stateBindings,
         duckingRules,
         mem.get(PacketIO.PROTO_BOOL, offset + 30)
      );
   }

   public void serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      byte nullBits = 0;
      if (this.id != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.layers != null) {
         nullBits = (byte)(nullBits | 2);
      }

      if (this.stateBindings != null) {
         nullBits = (byte)(nullBits | 4);
      }

      if (this.duckingRules != null) {
         nullBits = (byte)(nullBits | 8);
      }

      buf.writeByte(nullBits);
      buf.writeFloatLE(this.volume);
      buf.writeFloatLE(this.pitch);
      buf.writeIntLE(this.maxInstance);
      buf.writeByte(this.preventSoundInterruption ? 1 : 0);
      buf.writeFloatLE(this.startAttenuationDistance);
      buf.writeFloatLE(this.maxDistance);
      buf.writeFloatLE(this.spatialBlend);
      buf.writeIntLE(this.audioCategory);
      buf.writeByte(this.bypassDucking ? 1 : 0);
      int idOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int layersOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int stateBindingsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int duckingRulesOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.id != null) {
         buf.setIntLE(idOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.id, 4096000);
      } else {
         buf.setIntLE(idOffsetSlot, -1);
      }

      if (this.layers != null) {
         buf.setIntLE(layersOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.layers.length > 4096000) {
            throw ProtocolException.arrayTooLong("Layers", this.layers.length, 4096000);
         }

         VarInt.write(buf, this.layers.length);

         for (SoundEventLayer item : this.layers) {
            item.serialize(buf);
         }
      } else {
         buf.setIntLE(layersOffsetSlot, -1);
      }

      if (this.stateBindings != null) {
         buf.setIntLE(stateBindingsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.stateBindings.length > 4096000) {
            throw ProtocolException.arrayTooLong("StateBindings", this.stateBindings.length, 4096000);
         }

         VarInt.write(buf, this.stateBindings.length);

         for (StateBinding item : this.stateBindings) {
            item.serialize(buf);
         }
      } else {
         buf.setIntLE(stateBindingsOffsetSlot, -1);
      }

      if (this.duckingRules != null) {
         buf.setIntLE(duckingRulesOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.duckingRules.length > 4096000) {
            throw ProtocolException.arrayTooLong("DuckingRules", this.duckingRules.length, 4096000);
         }

         VarInt.write(buf, this.duckingRules.length);

         for (AudioCategoryDuckingRule item : this.duckingRules) {
            item.serialize(buf);
         }
      } else {
         buf.setIntLE(duckingRulesOffsetSlot, -1);
      }
   }

   public int serialize(@Nonnull MemorySegment mem, int offset) {
      byte nullBits = 0;
      if (this.id != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.layers != null) {
         nullBits = (byte)(nullBits | 2);
      }

      if (this.stateBindings != null) {
         nullBits = (byte)(nullBits | 4);
      }

      if (this.duckingRules != null) {
         nullBits = (byte)(nullBits | 8);
      }

      mem.set(PacketIO.PROTO_BYTE, offset + 0, nullBits);
      mem.set(PacketIO.PROTO_FLOAT, offset + 1, this.volume);
      mem.set(PacketIO.PROTO_FLOAT, offset + 5, this.pitch);
      mem.set(PacketIO.PROTO_INT, offset + 9, this.maxInstance);
      mem.set(PacketIO.PROTO_BOOL, offset + 13, this.preventSoundInterruption);
      mem.set(PacketIO.PROTO_FLOAT, offset + 14, this.startAttenuationDistance);
      mem.set(PacketIO.PROTO_FLOAT, offset + 18, this.maxDistance);
      mem.set(PacketIO.PROTO_FLOAT, offset + 22, this.spatialBlend);
      mem.set(PacketIO.PROTO_INT, offset + 26, this.audioCategory);
      mem.set(PacketIO.PROTO_BOOL, offset + 30, this.bypassDucking);
      int varOffset = offset + 47;
      if (this.id != null) {
         mem.set(PacketIO.PROTO_INT, offset + 31, varOffset - offset - 47);
         varOffset += PacketIO.writeVarString(mem, varOffset, this.id, 4096000);
      } else {
         mem.set(PacketIO.PROTO_INT, offset + 31, -1);
      }

      if (this.layers != null) {
         mem.set(PacketIO.PROTO_INT, offset + 35, varOffset - offset - 47);
         if (this.layers.length > 4096000) {
            throw ProtocolException.arrayTooLong("Layers", this.layers.length, 4096000);
         }

         varOffset += VarInt.set(mem, varOffset, this.layers.length);
         int layersValueOffset = 0;

         for (int i = 0; i < this.layers.length; i++) {
            layersValueOffset += this.layers[i].serialize(mem, varOffset + layersValueOffset);
         }

         varOffset += layersValueOffset;
      } else {
         mem.set(PacketIO.PROTO_INT, offset + 35, -1);
      }

      if (this.stateBindings != null) {
         mem.set(PacketIO.PROTO_INT, offset + 39, varOffset - offset - 47);
         if (this.stateBindings.length > 4096000) {
            throw ProtocolException.arrayTooLong("StateBindings", this.stateBindings.length, 4096000);
         }

         varOffset += VarInt.set(mem, varOffset, this.stateBindings.length);
         int stateBindingsValueOffset = 0;

         for (int i = 0; i < this.stateBindings.length; i++) {
            stateBindingsValueOffset += this.stateBindings[i].serialize(mem, varOffset + stateBindingsValueOffset);
         }

         varOffset += stateBindingsValueOffset;
      } else {
         mem.set(PacketIO.PROTO_INT, offset + 39, -1);
      }

      if (this.duckingRules != null) {
         mem.set(PacketIO.PROTO_INT, offset + 43, varOffset - offset - 47);
         if (this.duckingRules.length > 4096000) {
            throw ProtocolException.arrayTooLong("DuckingRules", this.duckingRules.length, 4096000);
         }

         varOffset += VarInt.set(mem, varOffset, this.duckingRules.length);
         int duckingRulesValueOffset = 0;

         for (int i = 0; i < this.duckingRules.length; i++) {
            duckingRulesValueOffset += this.duckingRules[i].serialize(mem, varOffset + duckingRulesValueOffset);
         }

         varOffset += duckingRulesValueOffset;
      } else {
         mem.set(PacketIO.PROTO_INT, offset + 43, -1);
      }

      return varOffset - offset;
   }

   public int computeSize() {
      int size = 47;
      if (this.id != null) {
         size += PacketIO.stringSize(this.id);
      }

      if (this.layers != null) {
         int layersSize = 0;

         for (SoundEventLayer elem : this.layers) {
            layersSize += elem.computeSize();
         }

         size += VarInt.size(this.layers.length) + layersSize;
      }

      if (this.stateBindings != null) {
         int stateBindingsSize = 0;

         for (StateBinding elem : this.stateBindings) {
            stateBindingsSize += elem.computeSize();
         }

         size += VarInt.size(this.stateBindings.length) + stateBindingsSize;
      }

      if (this.duckingRules != null) {
         size += VarInt.size(this.duckingRules.length) + this.duckingRules.length * 24;
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 47) {
         return ValidationResult.error("Buffer too small: expected at least 47 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      if ((nullBits & 1) != 0) {
         int idOffset = buffer.getIntLE(offset + 31);
         if (idOffset < 0 || idOffset > buffer.writerIndex() - offset - 47) {
            return ValidationResult.error("Invalid offset for Id");
         }

         int pos = offset + 47 + idOffset;
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

      if ((nullBits & 2) != 0) {
         int layersOffset = buffer.getIntLE(offset + 35);
         if (layersOffset < 0 || layersOffset > buffer.writerIndex() - offset - 47) {
            return ValidationResult.error("Invalid offset for Layers");
         }

         int pos = offset + 47 + layersOffset;
         int layersCount = VarInt.peek(buffer, pos);
         if (layersCount < 0) {
            return ValidationResult.error("Invalid array count for Layers");
         }

         if (layersCount > 4096000) {
            return ValidationResult.error("Layers exceeds max length 4096000");
         }

         pos += VarInt.size(layersCount);

         for (int i = 0; i < layersCount; i++) {
            ValidationResult structResult = SoundEventLayer.validateStructure(buffer, pos);
            if (!structResult.isValid()) {
               return ValidationResult.error("Invalid SoundEventLayer in Layers[" + i + "]: " + structResult.error());
            }

            pos += SoundEventLayer.computeBytesConsumed(buffer, pos);
         }
      }

      if ((nullBits & 4) != 0) {
         int stateBindingsOffset = buffer.getIntLE(offset + 39);
         if (stateBindingsOffset < 0 || stateBindingsOffset > buffer.writerIndex() - offset - 47) {
            return ValidationResult.error("Invalid offset for StateBindings");
         }

         int pos = offset + 47 + stateBindingsOffset;
         int stateBindingsCount = VarInt.peek(buffer, pos);
         if (stateBindingsCount < 0) {
            return ValidationResult.error("Invalid array count for StateBindings");
         }

         if (stateBindingsCount > 4096000) {
            return ValidationResult.error("StateBindings exceeds max length 4096000");
         }

         pos += VarInt.size(stateBindingsCount);

         for (int i = 0; i < stateBindingsCount; i++) {
            ValidationResult structResult = StateBinding.validateStructure(buffer, pos);
            if (!structResult.isValid()) {
               return ValidationResult.error("Invalid StateBinding in StateBindings[" + i + "]: " + structResult.error());
            }

            pos += StateBinding.computeBytesConsumed(buffer, pos);
         }
      }

      if ((nullBits & 8) != 0) {
         int duckingRulesOffset = buffer.getIntLE(offset + 43);
         if (duckingRulesOffset < 0 || duckingRulesOffset > buffer.writerIndex() - offset - 47) {
            return ValidationResult.error("Invalid offset for DuckingRules");
         }

         int pos = offset + 47 + duckingRulesOffset;
         int duckingRulesCount = VarInt.peek(buffer, pos);
         if (duckingRulesCount < 0) {
            return ValidationResult.error("Invalid array count for DuckingRules");
         }

         if (duckingRulesCount > 4096000) {
            return ValidationResult.error("DuckingRules exceeds max length 4096000");
         }

         pos += VarInt.size(duckingRulesCount);
         pos += duckingRulesCount * 24;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading DuckingRules");
         }
      }

      return ValidationResult.OK;
   }

   public SoundEvent clone() {
      SoundEvent copy = new SoundEvent();
      copy.id = this.id;
      copy.volume = this.volume;
      copy.pitch = this.pitch;
      copy.maxInstance = this.maxInstance;
      copy.preventSoundInterruption = this.preventSoundInterruption;
      copy.startAttenuationDistance = this.startAttenuationDistance;
      copy.maxDistance = this.maxDistance;
      copy.spatialBlend = this.spatialBlend;
      copy.layers = this.layers != null ? Arrays.stream(this.layers).map(e -> e.clone()).toArray(SoundEventLayer[]::new) : null;
      copy.audioCategory = this.audioCategory;
      copy.stateBindings = this.stateBindings != null ? Arrays.stream(this.stateBindings).map(e -> e.clone()).toArray(StateBinding[]::new) : null;
      copy.duckingRules = this.duckingRules != null ? Arrays.stream(this.duckingRules).map(e -> e.clone()).toArray(AudioCategoryDuckingRule[]::new) : null;
      copy.bypassDucking = this.bypassDucking;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof SoundEvent other)
            ? false
            : Objects.equals(this.id, other.id)
               && this.volume == other.volume
               && this.pitch == other.pitch
               && this.maxInstance == other.maxInstance
               && this.preventSoundInterruption == other.preventSoundInterruption
               && this.startAttenuationDistance == other.startAttenuationDistance
               && this.maxDistance == other.maxDistance
               && this.spatialBlend == other.spatialBlend
               && Arrays.equals(this.layers, other.layers)
               && this.audioCategory == other.audioCategory
               && Arrays.equals(this.stateBindings, other.stateBindings)
               && Arrays.equals(this.duckingRules, other.duckingRules)
               && this.bypassDucking == other.bypassDucking;
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      result = 31 * result + Objects.hashCode(this.id);
      result = 31 * result + Float.hashCode(this.volume);
      result = 31 * result + Float.hashCode(this.pitch);
      result = 31 * result + Integer.hashCode(this.maxInstance);
      result = 31 * result + Boolean.hashCode(this.preventSoundInterruption);
      result = 31 * result + Float.hashCode(this.startAttenuationDistance);
      result = 31 * result + Float.hashCode(this.maxDistance);
      result = 31 * result + Float.hashCode(this.spatialBlend);
      result = 31 * result + Arrays.hashCode(this.layers);
      result = 31 * result + Integer.hashCode(this.audioCategory);
      result = 31 * result + Arrays.hashCode(this.stateBindings);
      result = 31 * result + Arrays.hashCode(this.duckingRules);
      return 31 * result + Boolean.hashCode(this.bypassDucking);
   }
}
