package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.lang.foreign.MemorySegment;
import java.util.Objects;
import javax.annotation.Nonnull;

public class StateDelta {
   public static final int NULLABLE_BIT_FIELD_SIZE = 0;
   public static final int FIXED_BLOCK_SIZE = 9;
   public static final int VARIABLE_FIELD_COUNT = 0;
   public static final int VARIABLE_BLOCK_START = 9;
   public static final int MAX_SIZE = 9;
   public int valueIndex;
   public float volumeDb;
   public boolean mute;

   public StateDelta() {
   }

   public StateDelta(int valueIndex, float volumeDb, boolean mute) {
      this.valueIndex = valueIndex;
      this.volumeDb = volumeDb;
      this.mute = mute;
   }

   public StateDelta(@Nonnull StateDelta other) {
      this.valueIndex = other.valueIndex;
      this.volumeDb = other.volumeDb;
      this.mute = other.mute;
   }

   @Nonnull
   public static StateDelta deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 9) {
         throw ProtocolException.bufferTooSmall("StateDelta", 9, buf.readableBytes() - offset);
      }

      StateDelta obj = new StateDelta();
      obj.valueIndex = buf.getIntLE(offset + 0);
      obj.volumeDb = buf.getFloatLE(offset + 4);
      obj.mute = buf.getByte(offset + 8) != 0;
      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      return 9;
   }

   public static boolean isBufferTooSmall(MemorySegment mem) {
      return mem.byteSize() < 9L;
   }

   public static int getValueIndex(MemorySegment mem) {
      return getValueIndex(mem, 0);
   }

   public static int getValueIndex(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 0);
   }

   public static float getVolumeDb(MemorySegment mem) {
      return getVolumeDb(mem, 0);
   }

   public static float getVolumeDb(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_FLOAT, offset + 4);
   }

   public static boolean getMute(MemorySegment mem) {
      return getMute(mem, 0);
   }

   public static boolean getMute(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_BOOL, offset + 8);
   }

   public static StateDelta toObject(MemorySegment mem) {
      return toObject(mem, 0);
   }

   public static StateDelta toObject(MemorySegment mem, int offset) {
      if (offset + 9 > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("StateDelta", offset + 9, (int)mem.byteSize());
      } else {
         return new StateDelta(mem.get(PacketIO.PROTO_INT, offset + 0), mem.get(PacketIO.PROTO_FLOAT, offset + 4), mem.get(PacketIO.PROTO_BOOL, offset + 8));
      }
   }

   public void serialize(@Nonnull ByteBuf buf) {
      buf.writeIntLE(this.valueIndex);
      buf.writeFloatLE(this.volumeDb);
      buf.writeByte(this.mute ? 1 : 0);
   }

   public int serialize(@Nonnull MemorySegment mem, int offset) {
      mem.set(PacketIO.PROTO_INT, offset + 0, this.valueIndex);
      mem.set(PacketIO.PROTO_FLOAT, offset + 4, this.volumeDb);
      mem.set(PacketIO.PROTO_BOOL, offset + 8, this.mute);
      return 9;
   }

   public int computeSize() {
      return 9;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      return buffer.readableBytes() - offset < 9 ? ValidationResult.error("Buffer too small: expected at least 9 bytes") : ValidationResult.OK;
   }

   public StateDelta clone() {
      StateDelta copy = new StateDelta();
      copy.valueIndex = this.valueIndex;
      copy.volumeDb = this.volumeDb;
      copy.mute = this.mute;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof StateDelta other) ? false : this.valueIndex == other.valueIndex && this.volumeDb == other.volumeDb && this.mute == other.mute;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.valueIndex, this.volumeDb, this.mute);
   }
}
