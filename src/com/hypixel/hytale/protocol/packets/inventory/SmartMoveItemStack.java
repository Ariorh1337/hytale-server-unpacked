package com.hypixel.hytale.protocol.packets.inventory;

import com.hypixel.hytale.protocol.NetworkChannel;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.SmartMoveType;
import com.hypixel.hytale.protocol.ToClientPacket;
import com.hypixel.hytale.protocol.ToServerPacket;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import io.netty.buffer.ByteBuf;
import java.lang.foreign.MemorySegment;
import java.util.Objects;
import javax.annotation.Nonnull;

public class SmartMoveItemStack implements Packet, ToServerPacket, ToClientPacket {
   public static final int PACKET_ID = 176;
   public static final boolean IS_COMPRESSED = false;
   public static final int NULLABLE_BIT_FIELD_SIZE = 0;
   public static final int FIXED_BLOCK_SIZE = 13;
   public static final int VARIABLE_FIELD_COUNT = 0;
   public static final int VARIABLE_BLOCK_START = 13;
   public static final int MAX_SIZE = 13;
   public int fromSectionId;
   public int fromSlotId;
   public int quantity;
   @Nonnull
   public SmartMoveType moveType = SmartMoveType.EquipOrMergeStack;

   @Override
   public int getId() {
      return 176;
   }

   @Override
   public NetworkChannel getChannel() {
      return NetworkChannel.Default;
   }

   public SmartMoveItemStack() {
   }

   public SmartMoveItemStack(int fromSectionId, int fromSlotId, int quantity, @Nonnull SmartMoveType moveType) {
      this.fromSectionId = fromSectionId;
      this.fromSlotId = fromSlotId;
      this.quantity = quantity;
      this.moveType = moveType;
   }

   public SmartMoveItemStack(@Nonnull SmartMoveItemStack other) {
      this.fromSectionId = other.fromSectionId;
      this.fromSlotId = other.fromSlotId;
      this.quantity = other.quantity;
      this.moveType = other.moveType;
   }

   @Nonnull
   public static SmartMoveItemStack deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 13) {
         throw ProtocolException.bufferTooSmall("SmartMoveItemStack", 13, buf.readableBytes() - offset);
      }

      SmartMoveItemStack obj = new SmartMoveItemStack();
      obj.fromSectionId = buf.getIntLE(offset + 0);
      obj.fromSlotId = buf.getIntLE(offset + 4);
      obj.quantity = buf.getIntLE(offset + 8);
      if (obj.quantity < 1) {
         throw ProtocolException.valueBelowMinimum("Quantity", obj.quantity, 1.0);
      }

      obj.moveType = SmartMoveType.fromValue(buf.getByte(offset + 12));
      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      return 13;
   }

   public static boolean isBufferTooSmall(MemorySegment mem) {
      return mem.byteSize() < 13L;
   }

   public static int getFromSectionId(MemorySegment mem) {
      return getFromSectionId(mem, 0);
   }

   public static int getFromSectionId(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 0);
   }

   public static int getFromSlotId(MemorySegment mem) {
      return getFromSlotId(mem, 0);
   }

   public static int getFromSlotId(MemorySegment mem, int offset) {
      return mem.get(PacketIO.PROTO_INT, offset + 4);
   }

   public static int getQuantity(MemorySegment mem) {
      return getQuantity(mem, 0);
   }

   public static int getQuantity(MemorySegment mem, int offset) {
      int value = mem.get(PacketIO.PROTO_INT, offset + 8);
      if (value < 1) {
         throw ProtocolException.valueBelowMinimum("Quantity", value, 1.0);
      } else {
         return value;
      }
   }

   public static SmartMoveType getMoveType(MemorySegment mem) {
      return getMoveType(mem, 0);
   }

   public static SmartMoveType getMoveType(MemorySegment mem, int offset) {
      return SmartMoveType.fromValue(mem.get(PacketIO.PROTO_BYTE, offset + 12));
   }

   public static SmartMoveItemStack toObject(MemorySegment mem) {
      return toObject(mem, 0);
   }

   public static SmartMoveItemStack toObject(MemorySegment mem, int offset) {
      if (offset + 13 > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("SmartMoveItemStack", offset + 13, (int)mem.byteSize());
      } else {
         int quantity = mem.get(PacketIO.PROTO_INT, offset + 8);
         if (quantity < 1) {
            throw ProtocolException.valueBelowMinimum("Quantity", quantity, 1.0);
         } else {
            return new SmartMoveItemStack(
               mem.get(PacketIO.PROTO_INT, offset + 0),
               mem.get(PacketIO.PROTO_INT, offset + 4),
               quantity,
               SmartMoveType.fromValue(mem.get(PacketIO.PROTO_BYTE, offset + 12))
            );
         }
      }
   }

   @Override
   public void serialize(@Nonnull ByteBuf buf) {
      buf.writeIntLE(this.fromSectionId);
      buf.writeIntLE(this.fromSlotId);
      if (this.quantity < 1) {
         throw ProtocolException.valueBelowMinimum("Quantity", this.quantity, 1.0);
      }

      buf.writeIntLE(this.quantity);
      buf.writeByte(this.moveType.getValue());
   }

   @Override
   public int serialize(@Nonnull MemorySegment mem, int offset) {
      mem.set(PacketIO.PROTO_INT, offset + 0, this.fromSectionId);
      mem.set(PacketIO.PROTO_INT, offset + 4, this.fromSlotId);
      mem.set(PacketIO.PROTO_INT, offset + 8, this.quantity);
      mem.set(PacketIO.PROTO_BYTE, offset + 12, (byte)this.moveType.getValue());
      return 13;
   }

   @Override
   public int computeSize() {
      return 13;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 13) {
         return ValidationResult.error("Buffer too small: expected at least 13 bytes");
      }

      int quantityVal = buffer.getIntLE(offset + 8);
      if (quantityVal < 1) {
         return ValidationResult.error("Quantity value out of range");
      }

      quantityVal = buffer.getByte(offset + 12) & 255;
      return quantityVal >= 3 ? ValidationResult.error("Invalid SmartMoveType value for MoveType") : ValidationResult.OK;
   }

   public SmartMoveItemStack clone() {
      SmartMoveItemStack copy = new SmartMoveItemStack();
      copy.fromSectionId = this.fromSectionId;
      copy.fromSlotId = this.fromSlotId;
      copy.quantity = this.quantity;
      copy.moveType = this.moveType;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof SmartMoveItemStack other)
            ? false
            : this.fromSectionId == other.fromSectionId
               && this.fromSlotId == other.fromSlotId
               && this.quantity == other.quantity
               && Objects.equals(this.moveType, other.moveType);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.fromSectionId, this.fromSlotId, this.quantity, this.moveType);
   }
}
