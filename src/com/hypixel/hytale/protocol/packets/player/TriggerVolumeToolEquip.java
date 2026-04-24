package com.hypixel.hytale.protocol.packets.player;

import com.hypixel.hytale.protocol.NetworkChannel;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.ToServerPacket;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;

public class TriggerVolumeToolEquip implements Packet, ToServerPacket {
   public static final int PACKET_ID = 484;
   public static final boolean IS_COMPRESSED = false;
   public static final int NULLABLE_BIT_FIELD_SIZE = 0;
   public static final int FIXED_BLOCK_SIZE = 1;
   public static final int VARIABLE_FIELD_COUNT = 0;
   public static final int VARIABLE_BLOCK_START = 1;
   public static final int MAX_SIZE = 1;
   public boolean active;

   @Override
   public int getId() {
      return 484;
   }

   @Override
   public NetworkChannel getChannel() {
      return NetworkChannel.Default;
   }

   public TriggerVolumeToolEquip() {
   }

   public TriggerVolumeToolEquip(boolean active) {
      this.active = active;
   }

   public TriggerVolumeToolEquip(@Nonnull TriggerVolumeToolEquip other) {
      this.active = other.active;
   }

   @Nonnull
   public static TriggerVolumeToolEquip deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 1) {
         throw ProtocolException.bufferTooSmall("TriggerVolumeToolEquip", 1, buf.readableBytes() - offset);
      }

      TriggerVolumeToolEquip obj = new TriggerVolumeToolEquip();
      obj.active = buf.getByte(offset + 0) != 0;
      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      return 1;
   }

   @Override
   public void serialize(@Nonnull ByteBuf buf) {
      buf.writeByte(this.active ? 1 : 0);
   }

   @Override
   public int computeSize() {
      return 1;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      return buffer.readableBytes() - offset < 1 ? ValidationResult.error("Buffer too small: expected at least 1 bytes") : ValidationResult.OK;
   }

   public TriggerVolumeToolEquip clone() {
      TriggerVolumeToolEquip copy = new TriggerVolumeToolEquip();
      copy.active = this.active;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return obj instanceof TriggerVolumeToolEquip other ? this.active == other.active : false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.active);
   }
}
