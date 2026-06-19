package com.hypixel.hytale.protocol.packets.buildertools;

import com.hypixel.hytale.protocol.NetworkChannel;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.ToClientPacket;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.lang.foreign.MemorySegment;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderToolsEnabledTools implements Packet, ToClientPacket {
   public static final int PACKET_ID = 432;
   public static final boolean IS_COMPRESSED = false;
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 1;
   public static final int VARIABLE_FIELD_COUNT = 1;
   public static final int VARIABLE_BLOCK_START = 1;
   public static final int MAX_SIZE = 1677721600;
   @Nullable
   public String[] toolIds;

   @Override
   public int getId() {
      return 432;
   }

   @Override
   public NetworkChannel getChannel() {
      return NetworkChannel.Default;
   }

   public BuilderToolsEnabledTools() {
   }

   public BuilderToolsEnabledTools(@Nullable String[] toolIds) {
      this.toolIds = toolIds;
   }

   public BuilderToolsEnabledTools(@Nonnull BuilderToolsEnabledTools other) {
      this.toolIds = other.toolIds;
   }

   @Nonnull
   public static BuilderToolsEnabledTools deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 1) {
         throw ProtocolException.bufferTooSmall("BuilderToolsEnabledTools", 1, buf.readableBytes() - offset);
      }

      BuilderToolsEnabledTools obj = new BuilderToolsEnabledTools();
      byte nullBits = buf.getByte(offset);
      int pos = offset + 1;
      if ((nullBits & 1) != 0) {
         int toolIdsCount = VarInt.peek(buf, pos);
         if (toolIdsCount < 0) {
            throw ProtocolException.invalidVarInt("ToolIds");
         }

         int toolIdsVarLen = VarInt.size(toolIdsCount);
         if (toolIdsCount > 4096000) {
            throw ProtocolException.arrayTooLong("ToolIds", toolIdsCount, 4096000);
         }

         if (pos + toolIdsVarLen + toolIdsCount * 1L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("ToolIds", pos + toolIdsVarLen + toolIdsCount * 1, buf.readableBytes());
         }

         pos += toolIdsVarLen;
         obj.toolIds = new String[toolIdsCount];

         for (int i = 0; i < toolIdsCount; i++) {
            int strLen = VarInt.peek(buf, pos);
            if (strLen < 0) {
               throw ProtocolException.invalidVarInt("toolIds[" + i + "]");
            }

            int strVarLen = VarInt.size(strLen);
            if (strLen > 4096000) {
               throw ProtocolException.stringTooLong("toolIds[" + i + "]", strLen, 4096000);
            }

            if (pos + strVarLen + strLen > buf.readableBytes()) {
               throw ProtocolException.bufferTooSmall("toolIds[" + i + "]", pos + strVarLen + strLen, buf.readableBytes());
            }

            obj.toolIds[i] = PacketIO.readVarString(buf, pos);
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

   public static boolean isBufferTooSmall(MemorySegment mem) {
      return mem.byteSize() < 1L;
   }

   @Nullable
   public static String[] getToolIds(MemorySegment mem) {
      return getToolIds(mem, 0);
   }

   @Nullable
   public static String[] getToolIds(MemorySegment mem, int offset) {
      if (!hasToolIds(mem, offset)) {
         return null;
      }

      int off = offset + 1;
      long packed = VarInt.getWithLength(mem, off);
      int len = (int)packed;
      if (len < 0) {
         throw ProtocolException.negativeLength("ToolIds", len);
      }

      if (len > 4096000) {
         throw ProtocolException.arrayTooLong("ToolIds", len, 4096000);
      }

      int lenOffset = (int)(packed >>> 32);
      if (off + lenOffset + len > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("ToolIds", off + lenOffset + len, (int)mem.byteSize());
      }

      off += lenOffset;
      String[] data = new String[len];

      for (int i = 0; i < len; i++) {
         long sp = VarInt.getWithLength(mem, off);
         int n = (int)sp + (int)(sp >>> 32);
         data[i] = PacketIO.readVarString("ToolIds", mem, off, 16384000, PacketIO.UTF8);
         off += n;
      }

      return data;
   }

   public static boolean hasToolIds(MemorySegment mem, int offset) {
      byte b = mem.get(PacketIO.PROTO_BYTE, offset + 0);
      return (b & 1) != 0;
   }

   public static BuilderToolsEnabledTools toObject(MemorySegment mem) {
      return toObject(mem, 0);
   }

   public static BuilderToolsEnabledTools toObject(MemorySegment mem, int offset) {
      if (offset + 1 > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("BuilderToolsEnabledTools", offset + 1, (int)mem.byteSize());
      }

      String[] toolIds = null;
      if (hasToolIds(mem, offset)) {
         int off = offset + 1;
         long packed = VarInt.getWithLength(mem, off);
         int len = (int)packed;
         if (len < 0) {
            throw ProtocolException.negativeLength("ToolIds", len);
         }

         if (len > 4096000) {
            throw ProtocolException.arrayTooLong("ToolIds", len, 4096000);
         }

         int lenOffset = (int)(packed >>> 32);
         if (off + lenOffset + len > mem.byteSize()) {
            throw ProtocolException.bufferTooSmall("ToolIds", off + lenOffset + len, (int)mem.byteSize());
         }

         off += lenOffset;
         toolIds = new String[len];

         for (int i = 0; i < len; i++) {
            long sp = VarInt.getWithLength(mem, off);
            int n = (int)sp + (int)(sp >>> 32);
            toolIds[i] = PacketIO.readVarString("ToolIds", mem, off, 16384000, PacketIO.UTF8);
            off += n;
         }
      }

      return new BuilderToolsEnabledTools(toolIds);
   }

   @Override
   public void serialize(@Nonnull ByteBuf buf) {
      byte nullBits = 0;
      if (this.toolIds != null) {
         nullBits = (byte)(nullBits | 1);
      }

      buf.writeByte(nullBits);
      if (this.toolIds != null) {
         if (this.toolIds.length > 4096000) {
            throw ProtocolException.arrayTooLong("ToolIds", this.toolIds.length, 4096000);
         }

         VarInt.write(buf, this.toolIds.length);

         for (String item : this.toolIds) {
            PacketIO.writeVarString(buf, item, 4096000);
         }
      }
   }

   @Override
   public int serialize(@Nonnull MemorySegment mem, int offset) {
      byte nullBits = 0;
      if (this.toolIds != null) {
         nullBits = (byte)(nullBits | 1);
      }

      mem.set(PacketIO.PROTO_BYTE, offset + 0, nullBits);
      int varOffset = offset + 1;
      if (this.toolIds != null) {
         if (this.toolIds.length > 4096000) {
            throw ProtocolException.arrayTooLong("ToolIds", this.toolIds.length, 4096000);
         }

         varOffset += VarInt.set(mem, varOffset, this.toolIds.length);
         int toolIdsValueOffset = 0;

         for (int i = 0; i < this.toolIds.length; i++) {
            toolIdsValueOffset += PacketIO.writeVarString(mem, varOffset + toolIdsValueOffset, this.toolIds[i], 16384000);
         }

         varOffset += toolIdsValueOffset;
      }

      return varOffset - offset;
   }

   @Override
   public int computeSize() {
      int size = 1;
      if (this.toolIds != null) {
         int toolIdsSize = 0;

         for (String elem : this.toolIds) {
            toolIdsSize += PacketIO.stringSize(elem);
         }

         size += VarInt.size(this.toolIds.length) + toolIdsSize;
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
         int toolIdsCount = VarInt.peek(buffer, pos);
         if (toolIdsCount < 0) {
            return ValidationResult.error("Invalid array count for ToolIds");
         }

         if (toolIdsCount > 4096000) {
            return ValidationResult.error("ToolIds exceeds max length 4096000");
         }

         pos += VarInt.size(toolIdsCount);

         for (int i = 0; i < toolIdsCount; i++) {
            int strLen = VarInt.peek(buffer, pos);
            if (strLen < 0) {
               return ValidationResult.error("Invalid string length in ToolIds");
            }

            pos += VarInt.size(strLen);
            pos += strLen;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading string in ToolIds");
            }
         }
      }

      return ValidationResult.OK;
   }

   public BuilderToolsEnabledTools clone() {
      BuilderToolsEnabledTools copy = new BuilderToolsEnabledTools();
      copy.toolIds = this.toolIds != null ? Arrays.copyOf(this.toolIds, this.toolIds.length) : null;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return obj instanceof BuilderToolsEnabledTools other ? Arrays.equals(this.toolIds, other.toolIds) : false;
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      return 31 * result + Arrays.hashCode(this.toolIds);
   }
}
