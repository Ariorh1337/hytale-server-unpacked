package com.hypixel.hytale.protocol.packets.interface_;

import com.hypixel.hytale.protocol.NetworkChannel;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.ToClientPacket;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ArgCacheInvalidation implements Packet, ToClientPacket {
   public static final int PACKET_ID = 248;
   public static final boolean IS_COMPRESSED = false;
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 1;
   public static final int VARIABLE_FIELD_COUNT = 1;
   public static final int VARIABLE_BLOCK_START = 1;
   public static final int MAX_SIZE = 1677721600;
   @Nullable
   public String[] argTypeIds;

   @Override
   public int getId() {
      return 248;
   }

   @Override
   public NetworkChannel getChannel() {
      return NetworkChannel.Default;
   }

   public ArgCacheInvalidation() {
   }

   public ArgCacheInvalidation(@Nullable String[] argTypeIds) {
      this.argTypeIds = argTypeIds;
   }

   public ArgCacheInvalidation(@Nonnull ArgCacheInvalidation other) {
      this.argTypeIds = other.argTypeIds;
   }

   @Nonnull
   public static ArgCacheInvalidation deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 1) {
         throw ProtocolException.bufferTooSmall("ArgCacheInvalidation", 1, buf.readableBytes() - offset);
      }

      ArgCacheInvalidation obj = new ArgCacheInvalidation();
      byte nullBits = buf.getByte(offset);
      int pos = offset + 1;
      if ((nullBits & 1) != 0) {
         int argTypeIdsCount = VarInt.peek(buf, pos);
         if (argTypeIdsCount < 0) {
            throw ProtocolException.invalidVarInt("ArgTypeIds");
         }

         int argTypeIdsVarLen = VarInt.size(argTypeIdsCount);
         if (argTypeIdsCount > 4096000) {
            throw ProtocolException.arrayTooLong("ArgTypeIds", argTypeIdsCount, 4096000);
         }

         if (pos + argTypeIdsVarLen + argTypeIdsCount * 1L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("ArgTypeIds", pos + argTypeIdsVarLen + argTypeIdsCount * 1, buf.readableBytes());
         }

         pos += argTypeIdsVarLen;
         obj.argTypeIds = new String[argTypeIdsCount];

         for (int i = 0; i < argTypeIdsCount; i++) {
            int strLen = VarInt.peek(buf, pos);
            if (strLen < 0) {
               throw ProtocolException.invalidVarInt("argTypeIds[" + i + "]");
            }

            int strVarLen = VarInt.size(strLen);
            if (strLen > 4096000) {
               throw ProtocolException.stringTooLong("argTypeIds[" + i + "]", strLen, 4096000);
            }

            if (pos + strVarLen + strLen > buf.readableBytes()) {
               throw ProtocolException.bufferTooSmall("argTypeIds[" + i + "]", pos + strVarLen + strLen, buf.readableBytes());
            }

            obj.argTypeIds[i] = PacketIO.readVarString(buf, pos);
            pos += strVarLen + strLen;
         }
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int pos = offset + 1;
      if ((nullBits & 1) != 0) {
         int arrLen = VarInt.peek(buf, pos);
         pos += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            int sl = VarInt.peek(buf, pos);
            pos += VarInt.size(sl) + sl;
         }
      }

      return pos - offset;
   }

   @Override
   public void serialize(@Nonnull ByteBuf buf) {
      byte nullBits = 0;
      if (this.argTypeIds != null) {
         nullBits = (byte)(nullBits | 1);
      }

      buf.writeByte(nullBits);
      if (this.argTypeIds != null) {
         if (this.argTypeIds.length > 4096000) {
            throw ProtocolException.arrayTooLong("ArgTypeIds", this.argTypeIds.length, 4096000);
         }

         VarInt.write(buf, this.argTypeIds.length);

         for (String item : this.argTypeIds) {
            PacketIO.writeVarString(buf, item, 4096000);
         }
      }
   }

   @Override
   public int computeSize() {
      int size = 1;
      if (this.argTypeIds != null) {
         int argTypeIdsSize = 0;

         for (String elem : this.argTypeIds) {
            argTypeIdsSize += PacketIO.stringSize(elem);
         }

         size += VarInt.size(this.argTypeIds.length) + argTypeIdsSize;
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 1) {
         return ValidationResult.error("Buffer too small: expected at least 1 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      int pos = offset + 1;
      if ((nullBits & 1) != 0) {
         int argTypeIdsCount = VarInt.peek(buffer, pos);
         if (argTypeIdsCount < 0) {
            return ValidationResult.error("Invalid array count for ArgTypeIds");
         }

         if (argTypeIdsCount > 4096000) {
            return ValidationResult.error("ArgTypeIds exceeds max length 4096000");
         }

         pos += VarInt.size(argTypeIdsCount);

         for (int i = 0; i < argTypeIdsCount; i++) {
            int strLen = VarInt.peek(buffer, pos);
            if (strLen < 0) {
               return ValidationResult.error("Invalid string length in ArgTypeIds");
            }

            pos += VarInt.size(strLen);
            pos += strLen;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading string in ArgTypeIds");
            }
         }
      }

      return ValidationResult.OK;
   }

   public ArgCacheInvalidation clone() {
      ArgCacheInvalidation copy = new ArgCacheInvalidation();
      copy.argTypeIds = this.argTypeIds != null ? Arrays.copyOf(this.argTypeIds, this.argTypeIds.length) : null;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return obj instanceof ArgCacheInvalidation other ? Arrays.equals(this.argTypeIds, other.argTypeIds) : false;
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      return 31 * result + Arrays.hashCode(this.argTypeIds);
   }
}
