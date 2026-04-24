package com.hypixel.hytale.protocol.packets.player;

import com.hypixel.hytale.protocol.NetworkChannel;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.ToServerPacket;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class TriggerVolumeToolSetActivationDelay implements Packet, ToServerPacket {
   public static final int PACKET_ID = 502;
   public static final boolean IS_COMPRESSED = false;
   public static final int NULLABLE_BIT_FIELD_SIZE = 0;
   public static final int FIXED_BLOCK_SIZE = 4;
   public static final int VARIABLE_FIELD_COUNT = 1;
   public static final int VARIABLE_BLOCK_START = 4;
   public static final int MAX_SIZE = 16384009;
   @Nonnull
   public String volumeId = "";
   public float activationDelay;

   @Override
   public int getId() {
      return 502;
   }

   @Override
   public NetworkChannel getChannel() {
      return NetworkChannel.Default;
   }

   public TriggerVolumeToolSetActivationDelay() {
   }

   public TriggerVolumeToolSetActivationDelay(@Nonnull String volumeId, float activationDelay) {
      this.volumeId = volumeId;
      this.activationDelay = activationDelay;
   }

   public TriggerVolumeToolSetActivationDelay(@Nonnull TriggerVolumeToolSetActivationDelay other) {
      this.volumeId = other.volumeId;
      this.activationDelay = other.activationDelay;
   }

   @Nonnull
   public static TriggerVolumeToolSetActivationDelay deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 4) {
         throw ProtocolException.bufferTooSmall("TriggerVolumeToolSetActivationDelay", 4, buf.readableBytes() - offset);
      }

      TriggerVolumeToolSetActivationDelay obj = new TriggerVolumeToolSetActivationDelay();
      obj.activationDelay = buf.getFloatLE(offset + 0);
      int pos = offset + 4;
      int volumeIdLen = VarInt.peek(buf, pos);
      if (volumeIdLen < 0) {
         throw ProtocolException.invalidVarInt("VolumeId");
      }

      int volumeIdVarLen = VarInt.size(volumeIdLen);
      if (volumeIdLen > 4096000) {
         throw ProtocolException.stringTooLong("VolumeId", volumeIdLen, 4096000);
      }

      if (pos + volumeIdVarLen + volumeIdLen > buf.readableBytes()) {
         throw ProtocolException.bufferTooSmall("VolumeId", pos + volumeIdVarLen + volumeIdLen, buf.readableBytes());
      }

      obj.volumeId = PacketIO.readVarString(buf, pos, PacketIO.UTF8);
      pos += volumeIdVarLen + volumeIdLen;
      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      int pos = offset + 4;
      int sl = VarInt.peek(buf, pos);
      pos += VarInt.size(sl) + sl;
      return pos - offset;
   }

   @Override
   public void serialize(@Nonnull ByteBuf buf) {
      buf.writeFloatLE(this.activationDelay);
      PacketIO.writeVarString(buf, this.volumeId, 4096000);
   }

   @Override
   public int computeSize() {
      int size = 4;
      return size + PacketIO.stringSize(this.volumeId);
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 4) {
         return ValidationResult.error("Buffer too small: expected at least 4 bytes");
      }

      int pos = offset + 4;
      int volumeIdLen = VarInt.peek(buffer, pos);
      if (volumeIdLen < 0) {
         return ValidationResult.error("Invalid string length for VolumeId");
      }

      if (volumeIdLen > 4096000) {
         return ValidationResult.error("VolumeId exceeds max length 4096000");
      }

      pos += VarInt.size(volumeIdLen);
      pos += volumeIdLen;
      return pos > buffer.writerIndex() ? ValidationResult.error("Buffer overflow reading VolumeId") : ValidationResult.OK;
   }

   public TriggerVolumeToolSetActivationDelay clone() {
      TriggerVolumeToolSetActivationDelay copy = new TriggerVolumeToolSetActivationDelay();
      copy.volumeId = this.volumeId;
      copy.activationDelay = this.activationDelay;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof TriggerVolumeToolSetActivationDelay other)
            ? false
            : Objects.equals(this.volumeId, other.volumeId) && this.activationDelay == other.activationDelay;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.volumeId, this.activationDelay);
   }
}
