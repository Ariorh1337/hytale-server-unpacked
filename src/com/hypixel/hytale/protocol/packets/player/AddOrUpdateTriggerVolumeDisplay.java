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

public class AddOrUpdateTriggerVolumeDisplay implements Packet, ToClientPacket {
   public static final int PACKET_ID = 471;
   public static final boolean IS_COMPRESSED = false;
   public static final int NULLABLE_BIT_FIELD_SIZE = 0;
   public static final int FIXED_BLOCK_SIZE = 0;
   public static final int VARIABLE_FIELD_COUNT = 2;
   public static final int VARIABLE_BLOCK_START = 8;
   public static final int MAX_SIZE = 65536097;
   @Nonnull
   public String volumeId = "";
   @Nonnull
   public TriggerVolumeDisplayEntry entry = new TriggerVolumeDisplayEntry();

   @Override
   public int getId() {
      return 471;
   }

   @Override
   public NetworkChannel getChannel() {
      return NetworkChannel.Default;
   }

   public AddOrUpdateTriggerVolumeDisplay() {
   }

   public AddOrUpdateTriggerVolumeDisplay(@Nonnull String volumeId, @Nonnull TriggerVolumeDisplayEntry entry) {
      this.volumeId = volumeId;
      this.entry = entry;
   }

   public AddOrUpdateTriggerVolumeDisplay(@Nonnull AddOrUpdateTriggerVolumeDisplay other) {
      this.volumeId = other.volumeId;
      this.entry = other.entry;
   }

   @Nonnull
   public static AddOrUpdateTriggerVolumeDisplay deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 8) {
         throw ProtocolException.bufferTooSmall("AddOrUpdateTriggerVolumeDisplay", 8, buf.readableBytes() - offset);
      }

      AddOrUpdateTriggerVolumeDisplay obj = new AddOrUpdateTriggerVolumeDisplay();
      int varPosBase0 = buf.getIntLE(offset + 0);
      if (varPosBase0 >= 0 && varPosBase0 <= buf.writerIndex() - offset - 8) {
         int varPos0 = offset + 8 + varPosBase0;
         int volumeIdLen = VarInt.peek(buf, varPos0);
         if (volumeIdLen < 0) {
            throw ProtocolException.invalidVarInt("VolumeId");
         } else {
            int volumeIdVarIntLen = VarInt.size(volumeIdLen);
            if (volumeIdLen > 4096000) {
               throw ProtocolException.stringTooLong("VolumeId", volumeIdLen, 4096000);
            } else if (varPos0 + volumeIdVarIntLen + volumeIdLen > buf.readableBytes()) {
               throw ProtocolException.bufferTooSmall("VolumeId", varPos0 + volumeIdVarIntLen + volumeIdLen, buf.readableBytes());
            } else {
               obj.volumeId = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
               varPosBase0 = buf.getIntLE(offset + 4);
               if (varPosBase0 >= 0 && varPosBase0 <= buf.writerIndex() - offset - 8) {
                  varPos0 = offset + 8 + varPosBase0;
                  obj.entry = TriggerVolumeDisplayEntry.deserialize(buf, varPos0);
                  return obj;
               } else {
                  throw ProtocolException.invalidOffset("Entry", varPosBase0, buf.readableBytes());
               }
            }
         }
      } else {
         throw ProtocolException.invalidOffset("VolumeId", varPosBase0, buf.readableBytes());
      }
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      int maxEnd = 8;
      int fieldOffset0 = buf.getIntLE(offset + 0);
      if (fieldOffset0 >= 0 && fieldOffset0 <= buf.writerIndex() - offset - 8) {
         int pos0 = offset + 8 + fieldOffset0;
         int sl = VarInt.peek(buf, pos0);
         pos0 += VarInt.size(sl) + sl;
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }

         fieldOffset0 = buf.getIntLE(offset + 4);
         if (fieldOffset0 >= 0 && fieldOffset0 <= buf.writerIndex() - offset - 8) {
            pos0 = offset + 8 + fieldOffset0;
            pos0 += TriggerVolumeDisplayEntry.computeBytesConsumed(buf, pos0);
            if (pos0 - offset > maxEnd) {
               maxEnd = pos0 - offset;
            }

            return maxEnd;
         } else {
            throw ProtocolException.invalidOffset("Entry", fieldOffset0, maxEnd);
         }
      } else {
         throw ProtocolException.invalidOffset("VolumeId", fieldOffset0, maxEnd);
      }
   }

   @Override
   public void serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      int volumeIdOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int entryOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      buf.setIntLE(volumeIdOffsetSlot, buf.writerIndex() - varBlockStart);
      PacketIO.writeVarString(buf, this.volumeId, 4096000);
      buf.setIntLE(entryOffsetSlot, buf.writerIndex() - varBlockStart);
      this.entry.serialize(buf);
   }

   @Override
   public int computeSize() {
      int size = 8;
      size += PacketIO.stringSize(this.volumeId);
      return size + this.entry.computeSize();
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 8) {
         return ValidationResult.error("Buffer too small: expected at least 8 bytes");
      }

      int volumeIdOffset = buffer.getIntLE(offset + 0);
      if (volumeIdOffset >= 0 && volumeIdOffset <= buffer.writerIndex() - offset - 8) {
         int pos = offset + 8 + volumeIdOffset;
         int volumeIdLen = VarInt.peek(buffer, pos);
         if (volumeIdLen < 0) {
            return ValidationResult.error("Invalid string length for VolumeId");
         }

         if (volumeIdLen > 4096000) {
            return ValidationResult.error("VolumeId exceeds max length 4096000");
         }

         pos += VarInt.size(volumeIdLen);
         pos += volumeIdLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading VolumeId");
         }

         volumeIdOffset = buffer.getIntLE(offset + 4);
         if (volumeIdOffset >= 0 && volumeIdOffset <= buffer.writerIndex() - offset - 8) {
            pos = offset + 8 + volumeIdOffset;
            ValidationResult entryResult = TriggerVolumeDisplayEntry.validateStructure(buffer, pos);
            if (!entryResult.isValid()) {
               return ValidationResult.error("Invalid Entry: " + entryResult.error());
            }

            pos += TriggerVolumeDisplayEntry.computeBytesConsumed(buffer, pos);
            return ValidationResult.OK;
         } else {
            return ValidationResult.error("Invalid offset for Entry");
         }
      } else {
         return ValidationResult.error("Invalid offset for VolumeId");
      }
   }

   public AddOrUpdateTriggerVolumeDisplay clone() {
      AddOrUpdateTriggerVolumeDisplay copy = new AddOrUpdateTriggerVolumeDisplay();
      copy.volumeId = this.volumeId;
      copy.entry = this.entry.clone();
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof AddOrUpdateTriggerVolumeDisplay other)
            ? false
            : Objects.equals(this.volumeId, other.volumeId) && Objects.equals(this.entry, other.entry);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.volumeId, this.entry);
   }
}
