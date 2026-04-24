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
import org.joml.Vector3fc;

public class TriggerVolumeToolSetColor implements Packet, ToServerPacket {
   public static final int PACKET_ID = 492;
   public static final boolean IS_COMPRESSED = false;
   public static final int NULLABLE_BIT_FIELD_SIZE = 0;
   public static final int FIXED_BLOCK_SIZE = 12;
   public static final int VARIABLE_FIELD_COUNT = 1;
   public static final int VARIABLE_BLOCK_START = 12;
   public static final int MAX_SIZE = 16384017;
   @Nonnull
   public String volumeId = "";
   @Nonnull
   public Vector3fc color = PacketIO.ZERO_VECTOR3;

   @Override
   public int getId() {
      return 492;
   }

   @Override
   public NetworkChannel getChannel() {
      return NetworkChannel.Default;
   }

   public TriggerVolumeToolSetColor() {
   }

   public TriggerVolumeToolSetColor(@Nonnull String volumeId, @Nonnull Vector3fc color) {
      this.volumeId = volumeId;
      this.color = color;
   }

   public TriggerVolumeToolSetColor(@Nonnull TriggerVolumeToolSetColor other) {
      this.volumeId = other.volumeId;
      this.color = other.color;
   }

   @Nonnull
   public static TriggerVolumeToolSetColor deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 12) {
         throw ProtocolException.bufferTooSmall("TriggerVolumeToolSetColor", 12, buf.readableBytes() - offset);
      }

      TriggerVolumeToolSetColor obj = new TriggerVolumeToolSetColor();
      obj.color = PacketIO.readVector3f(buf, offset + 0);
      int pos = offset + 12;
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
      int pos = offset + 12;
      int sl = VarInt.peek(buf, pos);
      pos += VarInt.size(sl) + sl;
      return pos - offset;
   }

   @Override
   public void serialize(@Nonnull ByteBuf buf) {
      PacketIO.writeVector3f(buf, this.color);
      PacketIO.writeVarString(buf, this.volumeId, 4096000);
   }

   @Override
   public int computeSize() {
      int size = 12;
      return size + PacketIO.stringSize(this.volumeId);
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 12) {
         return ValidationResult.error("Buffer too small: expected at least 12 bytes");
      }

      int pos = offset + 12;
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

   public TriggerVolumeToolSetColor clone() {
      TriggerVolumeToolSetColor copy = new TriggerVolumeToolSetColor();
      copy.volumeId = this.volumeId;
      copy.color = this.color;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof TriggerVolumeToolSetColor other)
            ? false
            : Objects.equals(this.volumeId, other.volumeId) && Objects.equals(this.color, other.color);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.volumeId, this.color);
   }
}
