package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AmbienceFXConditions {
   public static final int NULLABLE_BIT_FIELD_SIZE = 2;
   public static final int FIXED_BLOCK_SIZE = 41;
   public static final int VARIABLE_FIELD_COUNT = 4;
   public static final int VARIABLE_BLOCK_START = 57;
   public static final int MAX_SIZE = 102400077;
   public boolean never;
   @Nullable
   public int[] environmentIndices;
   @Nullable
   public int[] weatherIndices;
   @Nullable
   public int[] fluidFXIndices;
   public int environmentTagPatternIndex;
   public int weatherTagPatternIndex;
   @Nullable
   public AmbienceFXBlockSoundSet[] surroundingBlockSoundSets;
   @Nullable
   public Range altitude;
   @Nullable
   public Rangeb walls;
   public boolean roof;
   public int roofMaterialTagPatternIndex;
   public boolean floor;
   @Nullable
   public Rangeb sunLightLevel;
   @Nullable
   public Rangeb torchLightLevel;
   @Nullable
   public Rangeb globalLightLevel;
   @Nullable
   public Rangef dayTime;

   public AmbienceFXConditions() {
   }

   public AmbienceFXConditions(
      boolean never,
      @Nullable int[] environmentIndices,
      @Nullable int[] weatherIndices,
      @Nullable int[] fluidFXIndices,
      int environmentTagPatternIndex,
      int weatherTagPatternIndex,
      @Nullable AmbienceFXBlockSoundSet[] surroundingBlockSoundSets,
      @Nullable Range altitude,
      @Nullable Rangeb walls,
      boolean roof,
      int roofMaterialTagPatternIndex,
      boolean floor,
      @Nullable Rangeb sunLightLevel,
      @Nullable Rangeb torchLightLevel,
      @Nullable Rangeb globalLightLevel,
      @Nullable Rangef dayTime
   ) {
      this.never = never;
      this.environmentIndices = environmentIndices;
      this.weatherIndices = weatherIndices;
      this.fluidFXIndices = fluidFXIndices;
      this.environmentTagPatternIndex = environmentTagPatternIndex;
      this.weatherTagPatternIndex = weatherTagPatternIndex;
      this.surroundingBlockSoundSets = surroundingBlockSoundSets;
      this.altitude = altitude;
      this.walls = walls;
      this.roof = roof;
      this.roofMaterialTagPatternIndex = roofMaterialTagPatternIndex;
      this.floor = floor;
      this.sunLightLevel = sunLightLevel;
      this.torchLightLevel = torchLightLevel;
      this.globalLightLevel = globalLightLevel;
      this.dayTime = dayTime;
   }

   public AmbienceFXConditions(@Nonnull AmbienceFXConditions other) {
      this.never = other.never;
      this.environmentIndices = other.environmentIndices;
      this.weatherIndices = other.weatherIndices;
      this.fluidFXIndices = other.fluidFXIndices;
      this.environmentTagPatternIndex = other.environmentTagPatternIndex;
      this.weatherTagPatternIndex = other.weatherTagPatternIndex;
      this.surroundingBlockSoundSets = other.surroundingBlockSoundSets;
      this.altitude = other.altitude;
      this.walls = other.walls;
      this.roof = other.roof;
      this.roofMaterialTagPatternIndex = other.roofMaterialTagPatternIndex;
      this.floor = other.floor;
      this.sunLightLevel = other.sunLightLevel;
      this.torchLightLevel = other.torchLightLevel;
      this.globalLightLevel = other.globalLightLevel;
      this.dayTime = other.dayTime;
   }

   @Nonnull
   public static AmbienceFXConditions deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 57) {
         throw ProtocolException.bufferTooSmall("AmbienceFXConditions", 57, buf.readableBytes() - offset);
      }

      AmbienceFXConditions obj = new AmbienceFXConditions();
      byte[] nullBits = PacketIO.readBytes(buf, offset, 2);
      obj.never = buf.getByte(offset + 2) != 0;
      obj.environmentTagPatternIndex = buf.getIntLE(offset + 3);
      obj.weatherTagPatternIndex = buf.getIntLE(offset + 7);
      if ((nullBits[0] & 1) != 0) {
         obj.altitude = Range.deserialize(buf, offset + 11);
      }

      if ((nullBits[0] & 2) != 0) {
         obj.walls = Rangeb.deserialize(buf, offset + 19);
      }

      obj.roof = buf.getByte(offset + 21) != 0;
      obj.roofMaterialTagPatternIndex = buf.getIntLE(offset + 22);
      obj.floor = buf.getByte(offset + 26) != 0;
      if ((nullBits[0] & 4) != 0) {
         obj.sunLightLevel = Rangeb.deserialize(buf, offset + 27);
      }

      if ((nullBits[0] & 8) != 0) {
         obj.torchLightLevel = Rangeb.deserialize(buf, offset + 29);
      }

      if ((nullBits[0] & 16) != 0) {
         obj.globalLightLevel = Rangeb.deserialize(buf, offset + 31);
      }

      if ((nullBits[0] & 32) != 0) {
         obj.dayTime = Rangef.deserialize(buf, offset + 33);
      }

      if ((nullBits[0] & 64) != 0) {
         int varPosBase0 = buf.getIntLE(offset + 41);
         if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 57) {
            throw ProtocolException.invalidOffset("EnvironmentIndices", varPosBase0, buf.readableBytes());
         }

         int varPos0 = offset + 57 + varPosBase0;
         int environmentIndicesCount = VarInt.peek(buf, varPos0);
         if (environmentIndicesCount < 0) {
            throw ProtocolException.invalidVarInt("EnvironmentIndices");
         }

         int varIntLen = VarInt.size(environmentIndicesCount);
         if (environmentIndicesCount > 4096000) {
            throw ProtocolException.arrayTooLong("EnvironmentIndices", environmentIndicesCount, 4096000);
         }

         if (varPos0 + varIntLen + environmentIndicesCount * 4L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("EnvironmentIndices", varPos0 + varIntLen + environmentIndicesCount * 4, buf.readableBytes());
         }

         obj.environmentIndices = new int[environmentIndicesCount];

         for (int i = 0; i < environmentIndicesCount; i++) {
            obj.environmentIndices[i] = buf.getIntLE(varPos0 + varIntLen + i * 4);
         }
      }

      if ((nullBits[0] & 128) != 0) {
         int varPosBase1 = buf.getIntLE(offset + 45);
         if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 57) {
            throw ProtocolException.invalidOffset("WeatherIndices", varPosBase1, buf.readableBytes());
         }

         int varPos1 = offset + 57 + varPosBase1;
         int weatherIndicesCount = VarInt.peek(buf, varPos1);
         if (weatherIndicesCount < 0) {
            throw ProtocolException.invalidVarInt("WeatherIndices");
         }

         int varIntLen = VarInt.size(weatherIndicesCount);
         if (weatherIndicesCount > 4096000) {
            throw ProtocolException.arrayTooLong("WeatherIndices", weatherIndicesCount, 4096000);
         }

         if (varPos1 + varIntLen + weatherIndicesCount * 4L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("WeatherIndices", varPos1 + varIntLen + weatherIndicesCount * 4, buf.readableBytes());
         }

         obj.weatherIndices = new int[weatherIndicesCount];

         for (int i = 0; i < weatherIndicesCount; i++) {
            obj.weatherIndices[i] = buf.getIntLE(varPos1 + varIntLen + i * 4);
         }
      }

      if ((nullBits[1] & 1) != 0) {
         int varPosBase2 = buf.getIntLE(offset + 49);
         if (varPosBase2 < 0 || varPosBase2 > buf.writerIndex() - offset - 57) {
            throw ProtocolException.invalidOffset("FluidFXIndices", varPosBase2, buf.readableBytes());
         }

         int varPos2 = offset + 57 + varPosBase2;
         int fluidFXIndicesCount = VarInt.peek(buf, varPos2);
         if (fluidFXIndicesCount < 0) {
            throw ProtocolException.invalidVarInt("FluidFXIndices");
         }

         int varIntLen = VarInt.size(fluidFXIndicesCount);
         if (fluidFXIndicesCount > 4096000) {
            throw ProtocolException.arrayTooLong("FluidFXIndices", fluidFXIndicesCount, 4096000);
         }

         if (varPos2 + varIntLen + fluidFXIndicesCount * 4L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("FluidFXIndices", varPos2 + varIntLen + fluidFXIndicesCount * 4, buf.readableBytes());
         }

         obj.fluidFXIndices = new int[fluidFXIndicesCount];

         for (int i = 0; i < fluidFXIndicesCount; i++) {
            obj.fluidFXIndices[i] = buf.getIntLE(varPos2 + varIntLen + i * 4);
         }
      }

      if ((nullBits[1] & 2) != 0) {
         int varPosBase3 = buf.getIntLE(offset + 53);
         if (varPosBase3 < 0 || varPosBase3 > buf.writerIndex() - offset - 57) {
            throw ProtocolException.invalidOffset("SurroundingBlockSoundSets", varPosBase3, buf.readableBytes());
         }

         int varPos3 = offset + 57 + varPosBase3;
         int surroundingBlockSoundSetsCount = VarInt.peek(buf, varPos3);
         if (surroundingBlockSoundSetsCount < 0) {
            throw ProtocolException.invalidVarInt("SurroundingBlockSoundSets");
         }

         int varIntLen = VarInt.size(surroundingBlockSoundSetsCount);
         if (surroundingBlockSoundSetsCount > 4096000) {
            throw ProtocolException.arrayTooLong("SurroundingBlockSoundSets", surroundingBlockSoundSetsCount, 4096000);
         }

         if (varPos3 + varIntLen + surroundingBlockSoundSetsCount * 13L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("SurroundingBlockSoundSets", varPos3 + varIntLen + surroundingBlockSoundSetsCount * 13, buf.readableBytes());
         }

         obj.surroundingBlockSoundSets = new AmbienceFXBlockSoundSet[surroundingBlockSoundSetsCount];
         int elemPos = varPos3 + varIntLen;

         for (int i = 0; i < surroundingBlockSoundSetsCount; i++) {
            obj.surroundingBlockSoundSets[i] = AmbienceFXBlockSoundSet.deserialize(buf, elemPos);
            elemPos += AmbienceFXBlockSoundSet.computeBytesConsumed(buf, elemPos);
         }
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte[] nullBits = PacketIO.readBytes(buf, offset, 2);
      int maxEnd = 57;
      if ((nullBits[0] & 64) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 41);
         if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 57) {
            throw ProtocolException.invalidOffset("EnvironmentIndices", fieldOffset0, maxEnd);
         }

         int pos0 = offset + 57 + fieldOffset0;
         int arrLen = VarInt.peek(buf, pos0);
         pos0 += VarInt.size(arrLen) + arrLen * 4;
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits[0] & 128) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 45);
         if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 57) {
            throw ProtocolException.invalidOffset("WeatherIndices", fieldOffset1, maxEnd);
         }

         int pos1 = offset + 57 + fieldOffset1;
         int arrLen = VarInt.peek(buf, pos1);
         pos1 += VarInt.size(arrLen) + arrLen * 4;
         if (pos1 - offset > maxEnd) {
            maxEnd = pos1 - offset;
         }
      }

      if ((nullBits[1] & 1) != 0) {
         int fieldOffset2 = buf.getIntLE(offset + 49);
         if (fieldOffset2 < 0 || fieldOffset2 > buf.writerIndex() - offset - 57) {
            throw ProtocolException.invalidOffset("FluidFXIndices", fieldOffset2, maxEnd);
         }

         int pos2 = offset + 57 + fieldOffset2;
         int arrLen = VarInt.peek(buf, pos2);
         pos2 += VarInt.size(arrLen) + arrLen * 4;
         if (pos2 - offset > maxEnd) {
            maxEnd = pos2 - offset;
         }
      }

      if ((nullBits[1] & 2) != 0) {
         int fieldOffset3 = buf.getIntLE(offset + 53);
         if (fieldOffset3 < 0 || fieldOffset3 > buf.writerIndex() - offset - 57) {
            throw ProtocolException.invalidOffset("SurroundingBlockSoundSets", fieldOffset3, maxEnd);
         }

         int pos3 = offset + 57 + fieldOffset3;
         int arrLen = VarInt.peek(buf, pos3);
         pos3 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            pos3 += AmbienceFXBlockSoundSet.computeBytesConsumed(buf, pos3);
         }

         if (pos3 - offset > maxEnd) {
            maxEnd = pos3 - offset;
         }
      }

      return maxEnd;
   }

   public void serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      byte[] nullBits = new byte[2];
      if (this.altitude != null) {
         nullBits[0] = (byte)(nullBits[0] | 1);
      }

      if (this.walls != null) {
         nullBits[0] = (byte)(nullBits[0] | 2);
      }

      if (this.sunLightLevel != null) {
         nullBits[0] = (byte)(nullBits[0] | 4);
      }

      if (this.torchLightLevel != null) {
         nullBits[0] = (byte)(nullBits[0] | 8);
      }

      if (this.globalLightLevel != null) {
         nullBits[0] = (byte)(nullBits[0] | 16);
      }

      if (this.dayTime != null) {
         nullBits[0] = (byte)(nullBits[0] | 32);
      }

      if (this.environmentIndices != null) {
         nullBits[0] = (byte)(nullBits[0] | 64);
      }

      if (this.weatherIndices != null) {
         nullBits[0] = (byte)(nullBits[0] | 128);
      }

      if (this.fluidFXIndices != null) {
         nullBits[1] = (byte)(nullBits[1] | 1);
      }

      if (this.surroundingBlockSoundSets != null) {
         nullBits[1] = (byte)(nullBits[1] | 2);
      }

      buf.writeBytes(nullBits);
      buf.writeByte(this.never ? 1 : 0);
      buf.writeIntLE(this.environmentTagPatternIndex);
      buf.writeIntLE(this.weatherTagPatternIndex);
      if (this.altitude != null) {
         this.altitude.serialize(buf);
      } else {
         buf.writeZero(8);
      }

      if (this.walls != null) {
         this.walls.serialize(buf);
      } else {
         buf.writeZero(2);
      }

      buf.writeByte(this.roof ? 1 : 0);
      buf.writeIntLE(this.roofMaterialTagPatternIndex);
      buf.writeByte(this.floor ? 1 : 0);
      if (this.sunLightLevel != null) {
         this.sunLightLevel.serialize(buf);
      } else {
         buf.writeZero(2);
      }

      if (this.torchLightLevel != null) {
         this.torchLightLevel.serialize(buf);
      } else {
         buf.writeZero(2);
      }

      if (this.globalLightLevel != null) {
         this.globalLightLevel.serialize(buf);
      } else {
         buf.writeZero(2);
      }

      if (this.dayTime != null) {
         this.dayTime.serialize(buf);
      } else {
         buf.writeZero(8);
      }

      int environmentIndicesOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int weatherIndicesOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int fluidFXIndicesOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int surroundingBlockSoundSetsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.environmentIndices != null) {
         buf.setIntLE(environmentIndicesOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.environmentIndices.length > 4096000) {
            throw ProtocolException.arrayTooLong("EnvironmentIndices", this.environmentIndices.length, 4096000);
         }

         VarInt.write(buf, this.environmentIndices.length);

         for (int item : this.environmentIndices) {
            buf.writeIntLE(item);
         }
      } else {
         buf.setIntLE(environmentIndicesOffsetSlot, -1);
      }

      if (this.weatherIndices != null) {
         buf.setIntLE(weatherIndicesOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.weatherIndices.length > 4096000) {
            throw ProtocolException.arrayTooLong("WeatherIndices", this.weatherIndices.length, 4096000);
         }

         VarInt.write(buf, this.weatherIndices.length);

         for (int item : this.weatherIndices) {
            buf.writeIntLE(item);
         }
      } else {
         buf.setIntLE(weatherIndicesOffsetSlot, -1);
      }

      if (this.fluidFXIndices != null) {
         buf.setIntLE(fluidFXIndicesOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.fluidFXIndices.length > 4096000) {
            throw ProtocolException.arrayTooLong("FluidFXIndices", this.fluidFXIndices.length, 4096000);
         }

         VarInt.write(buf, this.fluidFXIndices.length);

         for (int item : this.fluidFXIndices) {
            buf.writeIntLE(item);
         }
      } else {
         buf.setIntLE(fluidFXIndicesOffsetSlot, -1);
      }

      if (this.surroundingBlockSoundSets != null) {
         buf.setIntLE(surroundingBlockSoundSetsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.surroundingBlockSoundSets.length > 4096000) {
            throw ProtocolException.arrayTooLong("SurroundingBlockSoundSets", this.surroundingBlockSoundSets.length, 4096000);
         }

         VarInt.write(buf, this.surroundingBlockSoundSets.length);

         for (AmbienceFXBlockSoundSet item : this.surroundingBlockSoundSets) {
            item.serialize(buf);
         }
      } else {
         buf.setIntLE(surroundingBlockSoundSetsOffsetSlot, -1);
      }
   }

   public int computeSize() {
      int size = 57;
      if (this.environmentIndices != null) {
         size += VarInt.size(this.environmentIndices.length) + this.environmentIndices.length * 4;
      }

      if (this.weatherIndices != null) {
         size += VarInt.size(this.weatherIndices.length) + this.weatherIndices.length * 4;
      }

      if (this.fluidFXIndices != null) {
         size += VarInt.size(this.fluidFXIndices.length) + this.fluidFXIndices.length * 4;
      }

      if (this.surroundingBlockSoundSets != null) {
         size += VarInt.size(this.surroundingBlockSoundSets.length) + this.surroundingBlockSoundSets.length * 13;
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 57) {
         return ValidationResult.error("Buffer too small: expected at least 57 bytes");
      }

      byte[] nullBits = PacketIO.readBytes(buffer, offset, 2);
      if ((nullBits[0] & 64) != 0) {
         int environmentIndicesOffset = buffer.getIntLE(offset + 41);
         if (environmentIndicesOffset < 0 || environmentIndicesOffset > buffer.writerIndex() - offset - 57) {
            return ValidationResult.error("Invalid offset for EnvironmentIndices");
         }

         int pos = offset + 57 + environmentIndicesOffset;
         int environmentIndicesCount = VarInt.peek(buffer, pos);
         if (environmentIndicesCount < 0) {
            return ValidationResult.error("Invalid array count for EnvironmentIndices");
         }

         if (environmentIndicesCount > 4096000) {
            return ValidationResult.error("EnvironmentIndices exceeds max length 4096000");
         }

         pos += VarInt.size(environmentIndicesCount);
         pos += environmentIndicesCount * 4;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading EnvironmentIndices");
         }
      }

      if ((nullBits[0] & 128) != 0) {
         int weatherIndicesOffset = buffer.getIntLE(offset + 45);
         if (weatherIndicesOffset < 0 || weatherIndicesOffset > buffer.writerIndex() - offset - 57) {
            return ValidationResult.error("Invalid offset for WeatherIndices");
         }

         int pos = offset + 57 + weatherIndicesOffset;
         int weatherIndicesCount = VarInt.peek(buffer, pos);
         if (weatherIndicesCount < 0) {
            return ValidationResult.error("Invalid array count for WeatherIndices");
         }

         if (weatherIndicesCount > 4096000) {
            return ValidationResult.error("WeatherIndices exceeds max length 4096000");
         }

         pos += VarInt.size(weatherIndicesCount);
         pos += weatherIndicesCount * 4;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading WeatherIndices");
         }
      }

      if ((nullBits[1] & 1) != 0) {
         int fluidFXIndicesOffset = buffer.getIntLE(offset + 49);
         if (fluidFXIndicesOffset < 0 || fluidFXIndicesOffset > buffer.writerIndex() - offset - 57) {
            return ValidationResult.error("Invalid offset for FluidFXIndices");
         }

         int pos = offset + 57 + fluidFXIndicesOffset;
         int fluidFXIndicesCount = VarInt.peek(buffer, pos);
         if (fluidFXIndicesCount < 0) {
            return ValidationResult.error("Invalid array count for FluidFXIndices");
         }

         if (fluidFXIndicesCount > 4096000) {
            return ValidationResult.error("FluidFXIndices exceeds max length 4096000");
         }

         pos += VarInt.size(fluidFXIndicesCount);
         pos += fluidFXIndicesCount * 4;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading FluidFXIndices");
         }
      }

      if ((nullBits[1] & 2) != 0) {
         int surroundingBlockSoundSetsOffset = buffer.getIntLE(offset + 53);
         if (surroundingBlockSoundSetsOffset < 0 || surroundingBlockSoundSetsOffset > buffer.writerIndex() - offset - 57) {
            return ValidationResult.error("Invalid offset for SurroundingBlockSoundSets");
         }

         int pos = offset + 57 + surroundingBlockSoundSetsOffset;
         int surroundingBlockSoundSetsCount = VarInt.peek(buffer, pos);
         if (surroundingBlockSoundSetsCount < 0) {
            return ValidationResult.error("Invalid array count for SurroundingBlockSoundSets");
         }

         if (surroundingBlockSoundSetsCount > 4096000) {
            return ValidationResult.error("SurroundingBlockSoundSets exceeds max length 4096000");
         }

         pos += VarInt.size(surroundingBlockSoundSetsCount);
         pos += surroundingBlockSoundSetsCount * 13;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading SurroundingBlockSoundSets");
         }
      }

      return ValidationResult.OK;
   }

   public AmbienceFXConditions clone() {
      AmbienceFXConditions copy = new AmbienceFXConditions();
      copy.never = this.never;
      copy.environmentIndices = this.environmentIndices != null ? Arrays.copyOf(this.environmentIndices, this.environmentIndices.length) : null;
      copy.weatherIndices = this.weatherIndices != null ? Arrays.copyOf(this.weatherIndices, this.weatherIndices.length) : null;
      copy.fluidFXIndices = this.fluidFXIndices != null ? Arrays.copyOf(this.fluidFXIndices, this.fluidFXIndices.length) : null;
      copy.environmentTagPatternIndex = this.environmentTagPatternIndex;
      copy.weatherTagPatternIndex = this.weatherTagPatternIndex;
      copy.surroundingBlockSoundSets = this.surroundingBlockSoundSets != null
         ? Arrays.stream(this.surroundingBlockSoundSets).map(e -> e.clone()).toArray(AmbienceFXBlockSoundSet[]::new)
         : null;
      copy.altitude = this.altitude != null ? this.altitude.clone() : null;
      copy.walls = this.walls != null ? this.walls.clone() : null;
      copy.roof = this.roof;
      copy.roofMaterialTagPatternIndex = this.roofMaterialTagPatternIndex;
      copy.floor = this.floor;
      copy.sunLightLevel = this.sunLightLevel != null ? this.sunLightLevel.clone() : null;
      copy.torchLightLevel = this.torchLightLevel != null ? this.torchLightLevel.clone() : null;
      copy.globalLightLevel = this.globalLightLevel != null ? this.globalLightLevel.clone() : null;
      copy.dayTime = this.dayTime != null ? this.dayTime.clone() : null;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof AmbienceFXConditions other)
            ? false
            : this.never == other.never
               && Arrays.equals(this.environmentIndices, other.environmentIndices)
               && Arrays.equals(this.weatherIndices, other.weatherIndices)
               && Arrays.equals(this.fluidFXIndices, other.fluidFXIndices)
               && this.environmentTagPatternIndex == other.environmentTagPatternIndex
               && this.weatherTagPatternIndex == other.weatherTagPatternIndex
               && Arrays.equals(this.surroundingBlockSoundSets, other.surroundingBlockSoundSets)
               && Objects.equals(this.altitude, other.altitude)
               && Objects.equals(this.walls, other.walls)
               && this.roof == other.roof
               && this.roofMaterialTagPatternIndex == other.roofMaterialTagPatternIndex
               && this.floor == other.floor
               && Objects.equals(this.sunLightLevel, other.sunLightLevel)
               && Objects.equals(this.torchLightLevel, other.torchLightLevel)
               && Objects.equals(this.globalLightLevel, other.globalLightLevel)
               && Objects.equals(this.dayTime, other.dayTime);
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      result = 31 * result + Boolean.hashCode(this.never);
      result = 31 * result + Arrays.hashCode(this.environmentIndices);
      result = 31 * result + Arrays.hashCode(this.weatherIndices);
      result = 31 * result + Arrays.hashCode(this.fluidFXIndices);
      result = 31 * result + Integer.hashCode(this.environmentTagPatternIndex);
      result = 31 * result + Integer.hashCode(this.weatherTagPatternIndex);
      result = 31 * result + Arrays.hashCode(this.surroundingBlockSoundSets);
      result = 31 * result + Objects.hashCode(this.altitude);
      result = 31 * result + Objects.hashCode(this.walls);
      result = 31 * result + Boolean.hashCode(this.roof);
      result = 31 * result + Integer.hashCode(this.roofMaterialTagPatternIndex);
      result = 31 * result + Boolean.hashCode(this.floor);
      result = 31 * result + Objects.hashCode(this.sunLightLevel);
      result = 31 * result + Objects.hashCode(this.torchLightLevel);
      result = 31 * result + Objects.hashCode(this.globalLightLevel);
      return 31 * result + Objects.hashCode(this.dayTime);
   }
}
