package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.lang.foreign.MemorySegment;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class BlockBreakingDecal {
   public static final int NULLABLE_BIT_FIELD_SIZE = 0;
   public static final int FIXED_BLOCK_SIZE = 0;
   public static final int VARIABLE_FIELD_COUNT = 1;
   public static final int VARIABLE_BLOCK_START = 0;
   public static final int MAX_SIZE = 1677721600;
   @Nonnull
   public String[] stageTextures = new String[0];

   public BlockBreakingDecal() {
   }

   public BlockBreakingDecal(@Nonnull String[] stageTextures) {
      this.stageTextures = stageTextures;
   }

   public BlockBreakingDecal(@Nonnull BlockBreakingDecal other) {
      this.stageTextures = other.stageTextures;
   }

   @Nonnull
   public static BlockBreakingDecal deserialize(@Nonnull ByteBuf buf, int offset) {
      BlockBreakingDecal obj = new BlockBreakingDecal();
      int pos = offset + 0;
      int stageTexturesCount = VarInt.peek(buf, pos);
      if (stageTexturesCount < 0) {
         throw ProtocolException.invalidVarInt("StageTextures");
      }

      int stageTexturesVarLen = VarInt.size(stageTexturesCount);
      if (stageTexturesCount > 4096000) {
         throw ProtocolException.arrayTooLong("StageTextures", stageTexturesCount, 4096000);
      }

      if (pos + stageTexturesVarLen + stageTexturesCount * 1L > buf.readableBytes()) {
         throw ProtocolException.bufferTooSmall("StageTextures", pos + stageTexturesVarLen + stageTexturesCount * 1, buf.readableBytes());
      }

      pos += stageTexturesVarLen;
      obj.stageTextures = new String[stageTexturesCount];

      for (int i = 0; i < stageTexturesCount; i++) {
         int strLen = VarInt.peek(buf, pos);
         if (strLen < 0) {
            throw ProtocolException.invalidVarInt("stageTextures[" + i + "]");
         }

         int strVarLen = VarInt.size(strLen);
         if (strLen > 4096000) {
            throw ProtocolException.stringTooLong("stageTextures[" + i + "]", strLen, 4096000);
         }

         if (pos + strVarLen + strLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("stageTextures[" + i + "]", pos + strVarLen + strLen, buf.readableBytes());
         }

         obj.stageTextures[i] = PacketIO.readVarString(buf, pos);
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

   public static boolean isBufferTooSmall(MemorySegment mem) {
      return mem.byteSize() < 0L;
   }

   public static String[] getStageTextures(MemorySegment mem) {
      return getStageTextures(mem, 0);
   }

   public static String[] getStageTextures(MemorySegment mem, int offset) {
      int off = offset + 0;
      long packed = VarInt.getWithLength(mem, off);
      int len = (int)packed;
      if (len < 0) {
         throw ProtocolException.negativeLength("StageTextures", len);
      }

      if (len > 4096000) {
         throw ProtocolException.arrayTooLong("StageTextures", len, 4096000);
      }

      int lenOffset = (int)(packed >>> 32);
      if (off + lenOffset + len > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("StageTextures", off + lenOffset + len, (int)mem.byteSize());
      }

      off += lenOffset;
      String[] data = new String[len];

      for (int i = 0; i < len; i++) {
         long sp = VarInt.getWithLength(mem, off);
         int n = (int)sp + (int)(sp >>> 32);
         data[i] = PacketIO.readVarString("StageTextures", mem, off, 16384000, PacketIO.UTF8);
         off += n;
      }

      return data;
   }

   public static BlockBreakingDecal toObject(MemorySegment mem) {
      return toObject(mem, 0);
   }

   public static BlockBreakingDecal toObject(MemorySegment mem, int offset) {
      if (offset + 0 > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("BlockBreakingDecal", offset + 0, (int)mem.byteSize());
      }

      int off = offset + 0;
      long packed = VarInt.getWithLength(mem, off);
      int len = (int)packed;
      if (len < 0) {
         throw ProtocolException.negativeLength("StageTextures", len);
      }

      if (len > 4096000) {
         throw ProtocolException.arrayTooLong("StageTextures", len, 4096000);
      }

      int lenOffset = (int)(packed >>> 32);
      if (off + lenOffset + len > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("StageTextures", off + lenOffset + len, (int)mem.byteSize());
      }

      off += lenOffset;
      String[] stageTextures = new String[len];

      for (int i = 0; i < len; i++) {
         long sp = VarInt.getWithLength(mem, off);
         int n = (int)sp + (int)(sp >>> 32);
         stageTextures[i] = PacketIO.readVarString("StageTextures", mem, off, 16384000, PacketIO.UTF8);
         off += n;
      }

      return new BlockBreakingDecal(stageTextures);
   }

   public void serialize(@Nonnull ByteBuf buf) {
      if (this.stageTextures.length > 4096000) {
         throw ProtocolException.arrayTooLong("StageTextures", this.stageTextures.length, 4096000);
      }

      VarInt.write(buf, this.stageTextures.length);

      for (String item : this.stageTextures) {
         PacketIO.writeVarString(buf, item, 4096000);
      }
   }

   public int serialize(@Nonnull MemorySegment mem, int offset) {
      int varOffset = offset + 0;
      if (this.stageTextures.length > 4096000) {
         throw ProtocolException.arrayTooLong("StageTextures", this.stageTextures.length, 4096000);
      }

      varOffset += VarInt.set(mem, varOffset, this.stageTextures.length);
      int stageTexturesValueOffset = 0;

      for (int i = 0; i < this.stageTextures.length; i++) {
         stageTexturesValueOffset += PacketIO.writeVarString(mem, varOffset + stageTexturesValueOffset, this.stageTextures[i], 16384000);
      }

      varOffset += stageTexturesValueOffset;
      return varOffset - offset;
   }

   public int computeSize() {
      int size = 0;
      int stageTexturesSize = 0;

      for (String elem : this.stageTextures) {
         stageTexturesSize += PacketIO.stringSize(elem);
      }

      return size + VarInt.size(this.stageTextures.length) + stageTexturesSize;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 0) {
         return ValidationResult.error("Buffer too small: expected at least 0 bytes");
      }

      int pos = offset + 0;
      int stageTexturesCount = VarInt.peek(buffer, pos);
      if (stageTexturesCount < 0) {
         return ValidationResult.error("Invalid array count for StageTextures");
      }

      if (stageTexturesCount > 4096000) {
         return ValidationResult.error("StageTextures exceeds max length 4096000");
      }

      pos += VarInt.size(stageTexturesCount);

      for (int i = 0; i < stageTexturesCount; i++) {
         int strLen = VarInt.peek(buffer, pos);
         if (strLen < 0) {
            return ValidationResult.error("Invalid string length in StageTextures");
         }

         pos += VarInt.size(strLen);
         pos += strLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading string in StageTextures");
         }
      }

      return ValidationResult.OK;
   }

   public BlockBreakingDecal clone() {
      BlockBreakingDecal copy = new BlockBreakingDecal();
      copy.stageTextures = Arrays.copyOf(this.stageTextures, this.stageTextures.length);
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return obj instanceof BlockBreakingDecal other ? Arrays.equals(this.stageTextures, other.stageTextures) : false;
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      return 31 * result + Arrays.hashCode(this.stageTextures);
   }
}
