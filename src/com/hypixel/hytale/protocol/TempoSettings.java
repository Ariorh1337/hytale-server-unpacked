package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class TempoSettings {
   public static final int NULLABLE_BIT_FIELD_SIZE = 0;
   public static final int FIXED_BLOCK_SIZE = 12;
   public static final int VARIABLE_FIELD_COUNT = 0;
   public static final int VARIABLE_BLOCK_START = 12;
   public static final int MAX_SIZE = 12;
   public float bpm;
   public int beatsPerBar;
   public int beatValue;

   public TempoSettings() {
   }

   public TempoSettings(float bpm, int beatsPerBar, int beatValue) {
      this.bpm = bpm;
      this.beatsPerBar = beatsPerBar;
      this.beatValue = beatValue;
   }

   public TempoSettings(@Nonnull TempoSettings other) {
      this.bpm = other.bpm;
      this.beatsPerBar = other.beatsPerBar;
      this.beatValue = other.beatValue;
   }

   @Nonnull
   public static TempoSettings deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 12) {
         throw ProtocolException.bufferTooSmall("TempoSettings", 12, buf.readableBytes() - offset);
      }

      TempoSettings obj = new TempoSettings();
      obj.bpm = buf.getFloatLE(offset + 0);
      obj.beatsPerBar = buf.getIntLE(offset + 4);
      obj.beatValue = buf.getIntLE(offset + 8);
      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      return 12;
   }

   public void serialize(@Nonnull ByteBuf buf) {
      buf.writeFloatLE(this.bpm);
      buf.writeIntLE(this.beatsPerBar);
      buf.writeIntLE(this.beatValue);
   }

   public int computeSize() {
      return 12;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      return buffer.readableBytes() - offset < 12 ? ValidationResult.error("Buffer too small: expected at least 12 bytes") : ValidationResult.OK;
   }

   public TempoSettings clone() {
      TempoSettings copy = new TempoSettings();
      copy.bpm = this.bpm;
      copy.beatsPerBar = this.beatsPerBar;
      copy.beatValue = this.beatValue;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof TempoSettings other)
            ? false
            : this.bpm == other.bpm && this.beatsPerBar == other.beatsPerBar && this.beatValue == other.beatValue;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.bpm, this.beatsPerBar, this.beatValue);
   }
}
