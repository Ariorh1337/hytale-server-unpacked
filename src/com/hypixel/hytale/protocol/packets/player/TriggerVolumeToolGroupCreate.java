package com.hypixel.hytale.protocol.packets.player;

import com.hypixel.hytale.protocol.NetworkChannel;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.ToServerPacket;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class TriggerVolumeToolGroupCreate implements Packet, ToServerPacket {
   public static final int PACKET_ID = 486;
   public static final boolean IS_COMPRESSED = false;
   public static final int NULLABLE_BIT_FIELD_SIZE = 0;
   public static final int FIXED_BLOCK_SIZE = 0;
   public static final int VARIABLE_FIELD_COUNT = 1;
   public static final int VARIABLE_BLOCK_START = 0;
   public static final int MAX_SIZE = 1677721600;
   @Nonnull
   public String[] volumeIds = new String[0];

   @Override
   public int getId() {
      return 486;
   }

   @Override
   public NetworkChannel getChannel() {
      return NetworkChannel.Default;
   }

   public TriggerVolumeToolGroupCreate() {
   }

   public TriggerVolumeToolGroupCreate(@Nonnull String[] volumeIds) {
      this.volumeIds = volumeIds;
   }

   public TriggerVolumeToolGroupCreate(@Nonnull TriggerVolumeToolGroupCreate other) {
      this.volumeIds = other.volumeIds;
   }

   @Nonnull
   public static TriggerVolumeToolGroupCreate deserialize(@Nonnull ByteBuf buf, int offset) {
      TriggerVolumeToolGroupCreate obj = new TriggerVolumeToolGroupCreate();
      int pos = offset + 0;
      int volumeIdsCount = VarInt.peek(buf, pos);
      if (volumeIdsCount < 0) {
         throw ProtocolException.invalidVarInt("VolumeIds");
      }

      int volumeIdsVarLen = VarInt.size(volumeIdsCount);
      if (volumeIdsCount > 4096000) {
         throw ProtocolException.arrayTooLong("VolumeIds", volumeIdsCount, 4096000);
      }

      if (pos + volumeIdsVarLen + volumeIdsCount * 1L > buf.readableBytes()) {
         throw ProtocolException.bufferTooSmall("VolumeIds", pos + volumeIdsVarLen + volumeIdsCount * 1, buf.readableBytes());
      }

      pos += volumeIdsVarLen;
      obj.volumeIds = new String[volumeIdsCount];

      for (int i = 0; i < volumeIdsCount; i++) {
         int strLen = VarInt.peek(buf, pos);
         if (strLen < 0) {
            throw ProtocolException.invalidVarInt("volumeIds[" + i + "]");
         }

         int strVarLen = VarInt.size(strLen);
         if (strLen > 4096000) {
            throw ProtocolException.stringTooLong("volumeIds[" + i + "]", strLen, 4096000);
         }

         if (pos + strVarLen + strLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("volumeIds[" + i + "]", pos + strVarLen + strLen, buf.readableBytes());
         }

         obj.volumeIds[i] = PacketIO.readVarString(buf, pos);
         pos += strVarLen + strLen;
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      int pos = offset + 0;
      int arrLen = VarInt.peek(buf, pos);
      pos += VarInt.size(arrLen);

      for (int i = 0; i < arrLen; i++) {
         int sl = VarInt.peek(buf, pos);
         pos += VarInt.size(sl) + sl;
      }

      return pos - offset;
   }

   @Override
   public void serialize(@Nonnull ByteBuf buf) {
      if (this.volumeIds.length > 4096000) {
         throw ProtocolException.arrayTooLong("VolumeIds", this.volumeIds.length, 4096000);
      }

      VarInt.write(buf, this.volumeIds.length);

      for (String item : this.volumeIds) {
         PacketIO.writeVarString(buf, item, 4096000);
      }
   }

   @Override
   public int computeSize() {
      int size = 0;
      int volumeIdsSize = 0;

      for (String elem : this.volumeIds) {
         volumeIdsSize += PacketIO.stringSize(elem);
      }

      return size + VarInt.size(this.volumeIds.length) + volumeIdsSize;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 0) {
         return ValidationResult.error("Buffer too small: expected at least 0 bytes");
      }

      int pos = offset + 0;
      int volumeIdsCount = VarInt.peek(buffer, pos);
      if (volumeIdsCount < 0) {
         return ValidationResult.error("Invalid array count for VolumeIds");
      }

      if (volumeIdsCount > 4096000) {
         return ValidationResult.error("VolumeIds exceeds max length 4096000");
      }

      pos += VarInt.size(volumeIdsCount);

      for (int i = 0; i < volumeIdsCount; i++) {
         int strLen = VarInt.peek(buffer, pos);
         if (strLen < 0) {
            return ValidationResult.error("Invalid string length in VolumeIds");
         }

         pos += VarInt.size(strLen);
         pos += strLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading string in VolumeIds");
         }
      }

      return ValidationResult.OK;
   }

   public TriggerVolumeToolGroupCreate clone() {
      TriggerVolumeToolGroupCreate copy = new TriggerVolumeToolGroupCreate();
      copy.volumeIds = Arrays.copyOf(this.volumeIds, this.volumeIds.length);
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return obj instanceof TriggerVolumeToolGroupCreate other ? Arrays.equals(this.volumeIds, other.volumeIds) : false;
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      return 31 * result + Arrays.hashCode(this.volumeIds);
   }
}
