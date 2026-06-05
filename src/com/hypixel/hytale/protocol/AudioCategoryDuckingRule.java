package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.lang.foreign.MemorySegment;
import java.util.Objects;
import javax.annotation.Nonnull;

public class AudioCategoryDuckingRule {
   public static final int NULLABLE_BIT_FIELD_SIZE = 0;
   public static final int FIXED_BLOCK_SIZE = 24;
   public static final int VARIABLE_FIELD_COUNT = 0;
   public static final int VARIABLE_BLOCK_START = 24;
   public static final int MAX_SIZE = 24;
   public int targetAudioCategoryIndex;
   public float duckingVolumeDb;
   public float attackMs;
   public float holdMs;
   public float releaseMs;
   @Nonnull
   public FadeCurve curve = FadeCurve.Linear;
   @Nonnull
   public FadeCurve releaseCurve = FadeCurve.Linear;
   public short priority;

   public AudioCategoryDuckingRule() {
   }

   public AudioCategoryDuckingRule(
      int targetAudioCategoryIndex,
      float duckingVolumeDb,
      float attackMs,
      float holdMs,
      float releaseMs,
      @Nonnull FadeCurve curve,
      @Nonnull FadeCurve releaseCurve,
      short priority
   ) {
      this.targetAudioCategoryIndex = targetAudioCategoryIndex;
      this.duckingVolumeDb = duckingVolumeDb;
      this.attackMs = attackMs;
      this.holdMs = holdMs;
      this.releaseMs = releaseMs;
      this.curve = curve;
      this.releaseCurve = releaseCurve;
      this.priority = priority;
   }

   public AudioCategoryDuckingRule(@Nonnull AudioCategoryDuckingRule other) {
      this.targetAudioCategoryIndex = other.targetAudioCategoryIndex;
      this.duckingVolumeDb = other.duckingVolumeDb;
      this.attackMs = other.attackMs;
      this.holdMs = other.holdMs;
      this.releaseMs = other.releaseMs;
      this.curve = other.curve;
      this.releaseCurve = other.releaseCurve;
      this.priority = other.priority;
   }

   @Nonnull
   public static AudioCategoryDuckingRule deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 24) {
         throw ProtocolException.bufferTooSmall("AudioCategoryDuckingRule", 24, buf.readableBytes() - offset);
      }

      AudioCategoryDuckingRule obj = new AudioCategoryDuckingRule();
      obj.targetAudioCategoryIndex = buf.getIntLE(offset + 0);
      obj.duckingVolumeDb = buf.getFloatLE(offset + 4);
      obj.attackMs = buf.getFloatLE(offset + 8);
      obj.holdMs = buf.getFloatLE(offset + 12);
      obj.releaseMs = buf.getFloatLE(offset + 16);
      obj.curve = FadeCurve.fromValue(buf.getByte(offset + 20));
      obj.releaseCurve = FadeCurve.fromValue(buf.getByte(offset + 21));
      obj.priority = buf.getShortLE(offset + 22);
      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      return 24;
   }

   public static boolean isBufferTooSmall(MemorySegment mem) {
      return mem.byteSize() < 24L;
   }

   public static int getTargetAudioCategoryIndex(MemorySegment mem) {
      return getTargetAudioCategoryIndex(mem, 0);
   }

   public static int getTargetAudioCategoryIndex(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 0);
   }

   public static float getDuckingVolumeDb(MemorySegment mem) {
      return getDuckingVolumeDb(mem, 0);
   }

   public static float getDuckingVolumeDb(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_FLOAT, offset + 4);
   }

   public static float getAttackMs(MemorySegment mem) {
      return getAttackMs(mem, 0);
   }

   public static float getAttackMs(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_FLOAT, offset + 8);
   }

   public static float getHoldMs(MemorySegment mem) {
      return getHoldMs(mem, 0);
   }

   public static float getHoldMs(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_FLOAT, offset + 12);
   }

   public static float getReleaseMs(MemorySegment mem) {
      return getReleaseMs(mem, 0);
   }

   public static float getReleaseMs(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_FLOAT, offset + 16);
   }

   public static FadeCurve getCurve(MemorySegment mem) {
      return getCurve(mem, 0);
   }

   public static FadeCurve getCurve(MemorySegment mem, int offset) {
      return FadeCurve.fromValue(mem.get(PacketIO.PROTO_BYTE, offset + 20));
   }

   public static FadeCurve getReleaseCurve(MemorySegment mem) {
      return getReleaseCurve(mem, 0);
   }

   public static FadeCurve getReleaseCurve(MemorySegment mem, int offset) {
      return FadeCurve.fromValue(mem.get(PacketIO.PROTO_BYTE, offset + 21));
   }

   public static short getPriority(MemorySegment mem) {
      return getPriority(mem, 0);
   }

   public static short getPriority(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_SHORT, offset + 22);
   }

   public static AudioCategoryDuckingRule toObject(MemorySegment mem) {
      return toObject(mem, 0);
   }

   public static AudioCategoryDuckingRule toObject(MemorySegment mem, int offset) {
      if (offset + 24 > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("AudioCategoryDuckingRule", offset + 24, (int)mem.byteSize());
      } else {
         return new AudioCategoryDuckingRule(
            mem.get(PacketIO.PROTO_INT, offset + 0),
            mem.get(PacketIO.PROTO_FLOAT, offset + 4),
            mem.get(PacketIO.PROTO_FLOAT, offset + 8),
            mem.get(PacketIO.PROTO_FLOAT, offset + 12),
            mem.get(PacketIO.PROTO_FLOAT, offset + 16),
            FadeCurve.fromValue(mem.get(PacketIO.PROTO_BYTE, offset + 20)),
            FadeCurve.fromValue(mem.get(PacketIO.PROTO_BYTE, offset + 21)),
            mem.get(PacketIO.PROTO_SHORT, offset + 22)
         );
      }
   }

   public void serialize(@Nonnull ByteBuf buf) {
      buf.writeIntLE(this.targetAudioCategoryIndex);
      buf.writeFloatLE(this.duckingVolumeDb);
      buf.writeFloatLE(this.attackMs);
      buf.writeFloatLE(this.holdMs);
      buf.writeFloatLE(this.releaseMs);
      buf.writeByte(this.curve.getValue());
      buf.writeByte(this.releaseCurve.getValue());
      buf.writeShortLE(this.priority);
   }

   public int serialize(@Nonnull MemorySegment mem, int offset) {
      mem.set(PacketIO.PROTO_INT, offset + 0, this.targetAudioCategoryIndex);
      mem.set(PacketIO.PROTO_FLOAT, offset + 4, this.duckingVolumeDb);
      mem.set(PacketIO.PROTO_FLOAT, offset + 8, this.attackMs);
      mem.set(PacketIO.PROTO_FLOAT, offset + 12, this.holdMs);
      mem.set(PacketIO.PROTO_FLOAT, offset + 16, this.releaseMs);
      mem.set(PacketIO.PROTO_BYTE, offset + 20, (byte)this.curve.getValue());
      mem.set(PacketIO.PROTO_BYTE, offset + 21, (byte)this.releaseCurve.getValue());
      mem.set(PacketIO.PROTO_SHORT, offset + 22, this.priority);
      return 24;
   }

   public int computeSize() {
      return 24;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 24) {
         return ValidationResult.error("Buffer too small: expected at least 24 bytes");
      }

      int v = buffer.getByte(offset + 20) & 255;
      if (v >= 5) {
         return ValidationResult.error("Invalid FadeCurve value for Curve");
      }

      v = buffer.getByte(offset + 21) & 255;
      return v >= 5 ? ValidationResult.error("Invalid FadeCurve value for ReleaseCurve") : ValidationResult.OK;
   }

   public AudioCategoryDuckingRule clone() {
      AudioCategoryDuckingRule copy = new AudioCategoryDuckingRule();
      copy.targetAudioCategoryIndex = this.targetAudioCategoryIndex;
      copy.duckingVolumeDb = this.duckingVolumeDb;
      copy.attackMs = this.attackMs;
      copy.holdMs = this.holdMs;
      copy.releaseMs = this.releaseMs;
      copy.curve = this.curve;
      copy.releaseCurve = this.releaseCurve;
      copy.priority = this.priority;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof AudioCategoryDuckingRule other)
            ? false
            : this.targetAudioCategoryIndex == other.targetAudioCategoryIndex
               && this.duckingVolumeDb == other.duckingVolumeDb
               && this.attackMs == other.attackMs
               && this.holdMs == other.holdMs
               && this.releaseMs == other.releaseMs
               && Objects.equals(this.curve, other.curve)
               && Objects.equals(this.releaseCurve, other.releaseCurve)
               && this.priority == other.priority;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(
         this.targetAudioCategoryIndex, this.duckingVolumeDb, this.attackMs, this.holdMs, this.releaseMs, this.curve, this.releaseCurve, this.priority
      );
   }
}
