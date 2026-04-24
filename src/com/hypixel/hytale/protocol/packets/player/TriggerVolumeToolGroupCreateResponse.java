package com.hypixel.hytale.protocol.packets.player;

import com.hypixel.hytale.protocol.NetworkChannel;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.ToClientPacket;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class TriggerVolumeToolGroupCreateResponse implements Packet, ToClientPacket {
   public static final int PACKET_ID = 487;
   public static final boolean IS_COMPRESSED = false;
   public static final int NULLABLE_BIT_FIELD_SIZE = 0;
   public static final int FIXED_BLOCK_SIZE = 9;
   public static final int VARIABLE_FIELD_COUNT = 1;
   public static final int VARIABLE_BLOCK_START = 9;
   public static final int MAX_SIZE = 16384014;
   @Nonnull
   public String groupId = "";
   public int color;
   public boolean success;
   public int skippedCount;

   @Override
   public int getId() {
      return 487;
   }

   @Override
   public NetworkChannel getChannel() {
      return NetworkChannel.Default;
   }

   public TriggerVolumeToolGroupCreateResponse() {
   }

   public TriggerVolumeToolGroupCreateResponse(@Nonnull String groupId, int color, boolean success, int skippedCount) {
      this.groupId = groupId;
      this.color = color;
      this.success = success;
      this.skippedCount = skippedCount;
   }

   public TriggerVolumeToolGroupCreateResponse(@Nonnull TriggerVolumeToolGroupCreateResponse other) {
      this.groupId = other.groupId;
      this.color = other.color;
      this.success = other.success;
      this.skippedCount = other.skippedCount;
   }

   @Nonnull
   public static TriggerVolumeToolGroupCreateResponse deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 9) {
         throw ProtocolException.bufferTooSmall("TriggerVolumeToolGroupCreateResponse", 9, buf.readableBytes() - offset);
      }

      TriggerVolumeToolGroupCreateResponse obj = new TriggerVolumeToolGroupCreateResponse();
      obj.color = buf.getIntLE(offset + 0);
      obj.success = buf.getByte(offset + 4) != 0;
      obj.skippedCount = buf.getIntLE(offset + 5);
      int pos = offset + 9;
      int groupIdLen = VarInt.peek(buf, pos);
      if (groupIdLen < 0) {
         throw ProtocolException.invalidVarInt("GroupId");
      }

      int groupIdVarLen = VarInt.size(groupIdLen);
      if (groupIdLen > 4096000) {
         throw ProtocolException.stringTooLong("GroupId", groupIdLen, 4096000);
      }

      if (pos + groupIdVarLen + groupIdLen > buf.readableBytes()) {
         throw ProtocolException.bufferTooSmall("GroupId", pos + groupIdVarLen + groupIdLen, buf.readableBytes());
      }

      obj.groupId = PacketIO.readVarString(buf, pos, PacketIO.UTF8);
      pos += groupIdVarLen + groupIdLen;
      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      int pos = offset + 9;
      int sl = VarInt.peek(buf, pos);
      pos += VarInt.size(sl) + sl;
      return pos - offset;
   }

   @Override
   public void serialize(@Nonnull ByteBuf buf) {
      buf.writeIntLE(this.color);
      buf.writeByte(this.success ? 1 : 0);
      buf.writeIntLE(this.skippedCount);
      PacketIO.writeVarString(buf, this.groupId, 4096000);
   }

   @Override
   public int computeSize() {
      int size = 9;
      return size + PacketIO.stringSize(this.groupId);
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 9) {
         return ValidationResult.error("Buffer too small: expected at least 9 bytes");
      }

      int pos = offset + 9;
      int groupIdLen = VarInt.peek(buffer, pos);
      if (groupIdLen < 0) {
         return ValidationResult.error("Invalid string length for GroupId");
      }

      if (groupIdLen > 4096000) {
         return ValidationResult.error("GroupId exceeds max length 4096000");
      }

      pos += VarInt.size(groupIdLen);
      pos += groupIdLen;
      return pos > buffer.writerIndex() ? ValidationResult.error("Buffer overflow reading GroupId") : ValidationResult.OK;
   }

   public TriggerVolumeToolGroupCreateResponse clone() {
      TriggerVolumeToolGroupCreateResponse copy = new TriggerVolumeToolGroupCreateResponse();
      copy.groupId = this.groupId;
      copy.color = this.color;
      copy.success = this.success;
      copy.skippedCount = this.skippedCount;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof TriggerVolumeToolGroupCreateResponse other)
            ? false
            : Objects.equals(this.groupId, other.groupId)
               && this.color == other.color
               && this.success == other.success
               && this.skippedCount == other.skippedCount;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.groupId, this.color, this.success, this.skippedCount);
   }
}
