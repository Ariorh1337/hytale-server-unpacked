package com.hypixel.hytale.protocol.packets.world;

import com.hypixel.hytale.protocol.NetworkChannel;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.ToClientPacket;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.lang.foreign.MemorySegment;
import java.util.Objects;
import javax.annotation.Nonnull;

public class UpdateMusicState implements Packet, ToClientPacket {
   public static final int PACKET_ID = 168;
   public static final boolean IS_COMPRESSED = false;
   public static final int NULLABLE_BIT_FIELD_SIZE = 0;
   public static final int FIXED_BLOCK_SIZE = 12;
   public static final int VARIABLE_FIELD_COUNT = 0;
   public static final int VARIABLE_BLOCK_START = 12;
   public static final int MAX_SIZE = 12;
   public int containerIndex;
   public int stateIndex;
   public float fadeDuration;

   @Override
   public int getId() {
      return 168;
   }

   @Override
   public NetworkChannel getChannel() {
      return NetworkChannel.Default;
   }

   public UpdateMusicState() {
   }

   public UpdateMusicState(int containerIndex, int stateIndex, float fadeDuration) {
      this.containerIndex = containerIndex;
      this.stateIndex = stateIndex;
      this.fadeDuration = fadeDuration;
   }

   public UpdateMusicState(@Nonnull UpdateMusicState other) {
      this.containerIndex = other.containerIndex;
      this.stateIndex = other.stateIndex;
      this.fadeDuration = other.fadeDuration;
   }

   @Nonnull
   public static UpdateMusicState deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 12) {
         throw ProtocolException.bufferTooSmall("UpdateMusicState", 12, buf.readableBytes() - offset);
      }

      UpdateMusicState obj = new UpdateMusicState();
      obj.containerIndex = buf.getIntLE(offset + 0);
      obj.stateIndex = buf.getIntLE(offset + 4);
      obj.fadeDuration = buf.getFloatLE(offset + 8);
      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      return 12;
   }

   public static boolean isBufferTooSmall(MemorySegment mem) {
      return mem.byteSize() < 12L;
   }

   public static int getContainerIndex(MemorySegment mem) {
      return getContainerIndex(mem, 0);
   }

   public static int getContainerIndex(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 0);
   }

   public static int getStateIndex(MemorySegment mem) {
      return getStateIndex(mem, 0);
   }

   public static int getStateIndex(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 4);
   }

   public static float getFadeDuration(MemorySegment mem) {
      return getFadeDuration(mem, 0);
   }

   public static float getFadeDuration(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_FLOAT, offset + 8);
   }

   public static UpdateMusicState toObject(MemorySegment mem) {
      return toObject(mem, 0);
   }

   public static UpdateMusicState toObject(MemorySegment mem, int offset) {
      if (offset + 12 > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("UpdateMusicState", offset + 12, (int)mem.byteSize());
      } else {
         return new UpdateMusicState(
            mem.get(PacketIO.PROTO_INT, offset + 0), mem.get(PacketIO.PROTO_INT, offset + 4), mem.get(PacketIO.PROTO_FLOAT, offset + 8)
         );
      }
   }

   @Override
   public void serialize(@Nonnull ByteBuf buf) {
      buf.writeIntLE(this.containerIndex);
      buf.writeIntLE(this.stateIndex);
      buf.writeFloatLE(this.fadeDuration);
   }

   @Override
   public int serialize(@Nonnull MemorySegment mem, int offset) {
      mem.set(PacketIO.PROTO_INT, offset + 0, this.containerIndex);
      mem.set(PacketIO.PROTO_INT, offset + 4, this.stateIndex);
      mem.set(PacketIO.PROTO_FLOAT, offset + 8, this.fadeDuration);
      return 12;
   }

   @Override
   public int computeSize() {
      return 12;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      return buffer.readableBytes() - offset < 12 ? ValidationResult.error("Buffer too small: expected at least 12 bytes") : ValidationResult.OK;
   }

   public UpdateMusicState clone() {
      UpdateMusicState copy = new UpdateMusicState();
      copy.containerIndex = this.containerIndex;
      copy.stateIndex = this.stateIndex;
      copy.fadeDuration = this.fadeDuration;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof UpdateMusicState other)
            ? false
            : this.containerIndex == other.containerIndex && this.stateIndex == other.stateIndex && this.fadeDuration == other.fadeDuration;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.containerIndex, this.stateIndex, this.fadeDuration);
   }
}
