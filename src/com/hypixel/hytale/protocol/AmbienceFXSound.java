package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.lang.foreign.MemorySegment;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AmbienceFXSound {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 33;
   public static final int VARIABLE_FIELD_COUNT = 0;
   public static final int VARIABLE_BLOCK_START = 33;
   public static final int MAX_SIZE = 33;
   public int soundEventIndex;
   @Nonnull
   public AmbienceFXSoundPlay3D play3D = AmbienceFXSoundPlay3D.Random;
   public int blockSoundSetIndex;
   @Nonnull
   public AmbienceFXAltitude altitude = AmbienceFXAltitude.Normal;
   @Nullable
   public Rangef frequency;
   @Nullable
   public Range radius;
   public int maxBodiesPerEmitter;
   @Nullable
   public Rangeb sunlightRange;

   public AmbienceFXSound() {
   }

   public AmbienceFXSound(
      int soundEventIndex,
      @Nonnull AmbienceFXSoundPlay3D play3D,
      int blockSoundSetIndex,
      @Nonnull AmbienceFXAltitude altitude,
      @Nullable Rangef frequency,
      @Nullable Range radius,
      int maxBodiesPerEmitter,
      @Nullable Rangeb sunlightRange
   ) {
      this.soundEventIndex = soundEventIndex;
      this.play3D = play3D;
      this.blockSoundSetIndex = blockSoundSetIndex;
      this.altitude = altitude;
      this.frequency = frequency;
      this.radius = radius;
      this.maxBodiesPerEmitter = maxBodiesPerEmitter;
      this.sunlightRange = sunlightRange;
   }

   public AmbienceFXSound(@Nonnull AmbienceFXSound other) {
      this.soundEventIndex = other.soundEventIndex;
      this.play3D = other.play3D;
      this.blockSoundSetIndex = other.blockSoundSetIndex;
      this.altitude = other.altitude;
      this.frequency = other.frequency;
      this.radius = other.radius;
      this.maxBodiesPerEmitter = other.maxBodiesPerEmitter;
      this.sunlightRange = other.sunlightRange;
   }

   @Nonnull
   public static AmbienceFXSound deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 33) {
         throw ProtocolException.bufferTooSmall("AmbienceFXSound", 33, buf.readableBytes() - offset);
      }

      AmbienceFXSound obj = new AmbienceFXSound();
      byte nullBits = buf.getByte(offset);
      obj.soundEventIndex = buf.getIntLE(offset + 1);
      obj.play3D = AmbienceFXSoundPlay3D.fromValue(buf.getByte(offset + 5));
      obj.blockSoundSetIndex = buf.getIntLE(offset + 6);
      obj.altitude = AmbienceFXAltitude.fromValue(buf.getByte(offset + 10));
      if ((nullBits & 1) != 0) {
         obj.frequency = Rangef.deserialize(buf, offset + 11);
      }

      if ((nullBits & 2) != 0) {
         obj.radius = Range.deserialize(buf, offset + 19);
      }

      obj.maxBodiesPerEmitter = buf.getIntLE(offset + 27);
      if ((nullBits & 4) != 0) {
         obj.sunlightRange = Rangeb.deserialize(buf, offset + 31);
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      return 33;
   }

   public static boolean isBufferTooSmall(MemorySegment mem) {
      return mem.byteSize() < 33L;
   }

   public static int getSoundEventIndex(MemorySegment mem) {
      return getSoundEventIndex(mem, 0);
   }

   public static int getSoundEventIndex(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 1);
   }

   public static AmbienceFXSoundPlay3D getPlay3D(MemorySegment mem) {
      return getPlay3D(mem, 0);
   }

   public static AmbienceFXSoundPlay3D getPlay3D(MemorySegment mem, int offset) {
      return AmbienceFXSoundPlay3D.fromValue(mem.get(PacketIO.PROTO_BYTE, offset + 5));
   }

   public static int getBlockSoundSetIndex(MemorySegment mem) {
      return getBlockSoundSetIndex(mem, 0);
   }

   public static int getBlockSoundSetIndex(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 6);
   }

   public static AmbienceFXAltitude getAltitude(MemorySegment mem) {
      return getAltitude(mem, 0);
   }

   public static AmbienceFXAltitude getAltitude(MemorySegment mem, int offset) {
      return AmbienceFXAltitude.fromValue(mem.get(PacketIO.PROTO_BYTE, offset + 10));
   }

   @Nullable
   public static Rangef getFrequency(MemorySegment mem) {
      return getFrequency(mem, 0);
   }

   @Nullable
   public static Rangef getFrequency(MemorySegment mem, int offset) {
      return hasFrequency(mem, offset) ? Rangef.toObject(mem, offset + 11) : null;
   }

   @Nullable
   public static Range getRadius(MemorySegment mem) {
      return getRadius(mem, 0);
   }

   @Nullable
   public static Range getRadius(MemorySegment mem, int offset) {
      return hasRadius(mem, offset) ? Range.toObject(mem, offset + 19) : null;
   }

   public static int getMaxBodiesPerEmitter(MemorySegment mem) {
      return getMaxBodiesPerEmitter(mem, 0);
   }

   public static int getMaxBodiesPerEmitter(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 27);
   }

   @Nullable
   public static Rangeb getSunlightRange(MemorySegment mem) {
      return getSunlightRange(mem, 0);
   }

   @Nullable
   public static Rangeb getSunlightRange(MemorySegment mem, int offset) {
      return hasSunlightRange(mem, offset) ? Rangeb.toObject(mem, offset + 31) : null;
   }

   public static boolean hasFrequency(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 1) != 0;
   }

   public static boolean hasRadius(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 2) != 0;
   }

   public static boolean hasSunlightRange(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 4) != 0;
   }

   public static AmbienceFXSound toObject(MemorySegment mem) {
      return toObject(mem, 0);
   }

   public static AmbienceFXSound toObject(MemorySegment mem, int offset) {
      if (offset + 33 > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("AmbienceFXSound", offset + 33, (int)mem.byteSize());
      } else {
         return new AmbienceFXSound(
            mem.get(PacketIO.PROTO_INT, offset + 1),
            AmbienceFXSoundPlay3D.fromValue(mem.get(PacketIO.PROTO_BYTE, offset + 5)),
            mem.get(PacketIO.PROTO_INT, offset + 6),
            AmbienceFXAltitude.fromValue(mem.get(PacketIO.PROTO_BYTE, offset + 10)),
            hasFrequency(mem, offset) ? Rangef.toObject(mem, offset + 11) : null,
            hasRadius(mem, offset) ? Range.toObject(mem, offset + 19) : null,
            mem.get(PacketIO.PROTO_INT, offset + 27),
            hasSunlightRange(mem, offset) ? Rangeb.toObject(mem, offset + 31) : null
         );
      }
   }

   public void serialize(@Nonnull ByteBuf buf) {
      byte nullBits = 0;
      if (this.frequency != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.radius != null) {
         nullBits = (byte)(nullBits | 2);
      }

      if (this.sunlightRange != null) {
         nullBits = (byte)(nullBits | 4);
      }

      buf.writeByte(nullBits);
      buf.writeIntLE(this.soundEventIndex);
      buf.writeByte(this.play3D.getValue());
      buf.writeIntLE(this.blockSoundSetIndex);
      buf.writeByte(this.altitude.getValue());
      if (this.frequency != null) {
         this.frequency.serialize(buf);
      } else {
         buf.writeZero(8);
      }

      if (this.radius != null) {
         this.radius.serialize(buf);
      } else {
         buf.writeZero(8);
      }

      buf.writeIntLE(this.maxBodiesPerEmitter);
      if (this.sunlightRange != null) {
         this.sunlightRange.serialize(buf);
      } else {
         buf.writeZero(2);
      }
   }

   public int serialize(@Nonnull MemorySegment mem, int offset) {
      byte nullBits = 0;
      if (this.frequency != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.radius != null) {
         nullBits = (byte)(nullBits | 2);
      }

      if (this.sunlightRange != null) {
         nullBits = (byte)(nullBits | 4);
      }

      mem.set(PacketIO.PROTO_BYTE, offset + 0, nullBits);
      mem.set(PacketIO.PROTO_INT, offset + 1, this.soundEventIndex);
      mem.set(PacketIO.PROTO_BYTE, offset + 5, (byte)this.play3D.getValue());
      mem.set(PacketIO.PROTO_INT, offset + 6, this.blockSoundSetIndex);
      mem.set(PacketIO.PROTO_BYTE, offset + 10, (byte)this.altitude.getValue());
      if (this.frequency != null) {
         this.frequency.serialize(mem, offset + 11);
      } else {
         mem.asSlice(offset + 11, 8L).fill((byte)0);
      }

      if (this.radius != null) {
         this.radius.serialize(mem, offset + 19);
      } else {
         mem.asSlice(offset + 19, 8L).fill((byte)0);
      }

      mem.set(PacketIO.PROTO_INT, offset + 27, this.maxBodiesPerEmitter);
      if (this.sunlightRange != null) {
         this.sunlightRange.serialize(mem, offset + 31);
      } else {
         mem.asSlice(offset + 31, 2L).fill((byte)0);
      }

      return 33;
   }

   public int computeSize() {
      return 33;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 33) {
         return ValidationResult.error("Buffer too small: expected at least 33 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      int v = buffer.getByte(offset + 5) & 255;
      if (v >= 4) {
         return ValidationResult.error("Invalid AmbienceFXSoundPlay3D value for Play3D");
      }

      v = buffer.getByte(offset + 10) & 255;
      return v >= 4 ? ValidationResult.error("Invalid AmbienceFXAltitude value for Altitude") : ValidationResult.OK;
   }

   public AmbienceFXSound clone() {
      AmbienceFXSound copy = new AmbienceFXSound();
      copy.soundEventIndex = this.soundEventIndex;
      copy.play3D = this.play3D;
      copy.blockSoundSetIndex = this.blockSoundSetIndex;
      copy.altitude = this.altitude;
      copy.frequency = this.frequency != null ? this.frequency.clone() : null;
      copy.radius = this.radius != null ? this.radius.clone() : null;
      copy.maxBodiesPerEmitter = this.maxBodiesPerEmitter;
      copy.sunlightRange = this.sunlightRange != null ? this.sunlightRange.clone() : null;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof AmbienceFXSound other)
            ? false
            : this.soundEventIndex == other.soundEventIndex
               && Objects.equals(this.play3D, other.play3D)
               && this.blockSoundSetIndex == other.blockSoundSetIndex
               && Objects.equals(this.altitude, other.altitude)
               && Objects.equals(this.frequency, other.frequency)
               && Objects.equals(this.radius, other.radius)
               && this.maxBodiesPerEmitter == other.maxBodiesPerEmitter
               && Objects.equals(this.sunlightRange, other.sunlightRange);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(
         this.soundEventIndex, this.play3D, this.blockSoundSetIndex, this.altitude, this.frequency, this.radius, this.maxBodiesPerEmitter, this.sunlightRange
      );
   }
}
