package com.hypixel.hytale.protocol.packets.assets;

import com.hypixel.hytale.protocol.BlockBreakingDecal;
import com.hypixel.hytale.protocol.NetworkChannel;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.ToClientPacket;
import com.hypixel.hytale.protocol.UpdateType;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.lang.foreign.MemorySegment;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import javax.annotation.Nonnull;

public class UpdateBlockBreakingDecals implements Packet, ToClientPacket {
   public static final int PACKET_ID = 45;
   public static final boolean IS_COMPRESSED = true;
   public static final int NULLABLE_BIT_FIELD_SIZE = 0;
   public static final int FIXED_BLOCK_SIZE = 1;
   public static final int VARIABLE_FIELD_COUNT = 1;
   public static final int VARIABLE_BLOCK_START = 1;
   public static final int MAX_SIZE = 1677721600;
   @Nonnull
   public UpdateType type = UpdateType.Init;
   @Nonnull
   public Map<String, BlockBreakingDecal> blockBreakingDecals = new HashMap<>();

   @Override
   public int getId() {
      return 45;
   }

   @Override
   public NetworkChannel getChannel() {
      return NetworkChannel.Default;
   }

   public UpdateBlockBreakingDecals() {
   }

   public UpdateBlockBreakingDecals(@Nonnull UpdateType type, @Nonnull Map<String, BlockBreakingDecal> blockBreakingDecals) {
      this.type = type;
      this.blockBreakingDecals = blockBreakingDecals;
   }

   public UpdateBlockBreakingDecals(@Nonnull UpdateBlockBreakingDecals other) {
      this.type = other.type;
      this.blockBreakingDecals = other.blockBreakingDecals;
   }

   @Nonnull
   public static UpdateBlockBreakingDecals deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 1) {
         throw ProtocolException.bufferTooSmall("UpdateBlockBreakingDecals", 1, buf.readableBytes() - offset);
      }

      UpdateBlockBreakingDecals obj = new UpdateBlockBreakingDecals();
      obj.type = UpdateType.fromValue(buf.getByte(offset + 0));
      int pos = offset + 1;
      int blockBreakingDecalsCount = VarInt.peek(buf, pos);
      if (blockBreakingDecalsCount < 0) {
         throw ProtocolException.invalidVarInt("BlockBreakingDecals");
      }

      int blockBreakingDecalsVarLen = VarInt.size(blockBreakingDecalsCount);
      if (blockBreakingDecalsCount > 4096000) {
         throw ProtocolException.dictionaryTooLarge("BlockBreakingDecals", blockBreakingDecalsCount, 4096000);
      }

      pos += blockBreakingDecalsVarLen;
      obj.blockBreakingDecals = new HashMap<>(blockBreakingDecalsCount);

      for (int i = 0; i < blockBreakingDecalsCount; i++) {
         int keyLen = VarInt.peek(buf, pos);
         if (keyLen < 0) {
            throw ProtocolException.invalidVarInt("key");
         }

         int keyVarLen = VarInt.size(keyLen);
         if (keyLen > 4096000) {
            throw ProtocolException.stringTooLong("key", keyLen, 4096000);
         }

         if (pos + keyVarLen + keyLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("key", pos + keyVarLen + keyLen, buf.readableBytes());
         }

         String key = PacketIO.readVarString(buf, pos);
         pos += keyVarLen + keyLen;
         BlockBreakingDecal val = BlockBreakingDecal.deserialize(buf, pos);
         pos += BlockBreakingDecal.computeBytesConsumed(buf, pos);
         if (obj.blockBreakingDecals.put(key, val) != null) {
            throw ProtocolException.duplicateKey("blockBreakingDecals", key);
         }
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      int pos = offset + 1;
      int dictLen = VarInt.peek(buf, pos);
      pos += VarInt.size(dictLen);

      for (int i = 0; i < dictLen; i++) {
         int sl = VarInt.peek(buf, pos);
         pos += VarInt.size(sl) + sl;
         pos += BlockBreakingDecal.computeBytesConsumed(buf, pos);
      }

      return pos - offset;
   }

   public static boolean isBufferTooSmall(MemorySegment mem) {
      return mem.byteSize() < 1L;
   }

   public static UpdateType getType(MemorySegment mem) {
      return getType(mem, 0);
   }

   public static UpdateType getType(MemorySegment mem, int offset) {
      return UpdateType.fromValue(mem.get(PacketIO.PROTO_BYTE, offset + 0));
   }

   public static Map<String, BlockBreakingDecal> getBlockBreakingDecals(MemorySegment mem) {
      return getBlockBreakingDecals(mem, 0);
   }

   public static Map<String, BlockBreakingDecal> getBlockBreakingDecals(MemorySegment mem, int offset) {
      int off = offset + 1;
      long packed = VarInt.getWithLength(mem, off);
      int len = (int)packed;
      if (len < 0) {
         throw ProtocolException.negativeLength("BlockBreakingDecals", len);
      }

      if (len > 4096000) {
         throw ProtocolException.dictionaryTooLarge("BlockBreakingDecals", len, 4096000);
      }

      Map<String, BlockBreakingDecal> data = new HashMap<>(len);
      off += (int)(packed >>> 32);

      for (int i = 0; i < len; i++) {
         long keyPacked = VarInt.getWithLength(mem, off);
         int nkey = (int)keyPacked + (int)(keyPacked >>> 32);
         String key = PacketIO.readVarString("key", mem, off, 16384000, PacketIO.UTF8);
         off += nkey;
         BlockBreakingDecal value = BlockBreakingDecal.toObject(mem, off);
         off += value.computeSize();
         if (data.put(key, value) != null) {
            throw ProtocolException.duplicateKey("BlockBreakingDecals", key);
         }
      }

      return data;
   }

   public static UpdateBlockBreakingDecals toObject(MemorySegment mem) {
      return toObject(mem, 0);
   }

   public static UpdateBlockBreakingDecals toObject(MemorySegment mem, int offset) {
      if (offset + 1 > mem.byteSize()) {
         throw ProtocolException.bufferTooSmall("UpdateBlockBreakingDecals", offset + 1, (int)mem.byteSize());
      }

      int off = offset + 1;
      long packed = VarInt.getWithLength(mem, off);
      int len = (int)packed;
      if (len < 0) {
         throw ProtocolException.negativeLength("BlockBreakingDecals", len);
      }

      if (len > 4096000) {
         throw ProtocolException.dictionaryTooLarge("BlockBreakingDecals", len, 4096000);
      }

      Map<String, BlockBreakingDecal> blockBreakingDecals = new HashMap<>(len);
      off += (int)(packed >>> 32);

      for (int i = 0; i < len; i++) {
         long keyPacked = VarInt.getWithLength(mem, off);
         int nkey = (int)keyPacked + (int)(keyPacked >>> 32);
         String key = PacketIO.readVarString("key", mem, off, 16384000, PacketIO.UTF8);
         off += nkey;
         BlockBreakingDecal value = BlockBreakingDecal.toObject(mem, off);
         off += value.computeSize();
         if (blockBreakingDecals.put(key, value) != null) {
            throw ProtocolException.duplicateKey("BlockBreakingDecals", key);
         }
      }

      return new UpdateBlockBreakingDecals(UpdateType.fromValue(mem.get(PacketIO.PROTO_BYTE, offset + 0)), blockBreakingDecals);
   }

   @Override
   public void serialize(@Nonnull ByteBuf buf) {
      buf.writeByte(this.type.getValue());
      if (this.blockBreakingDecals.size() > 4096000) {
         throw ProtocolException.dictionaryTooLarge("BlockBreakingDecals", this.blockBreakingDecals.size(), 4096000);
      }

      VarInt.write(buf, this.blockBreakingDecals.size());

      for (Entry<String, BlockBreakingDecal> e : this.blockBreakingDecals.entrySet()) {
         PacketIO.writeVarString(buf, e.getKey(), 4096000);
         e.getValue().serialize(buf);
      }
   }

   @Override
   public int serialize(@Nonnull MemorySegment mem, int offset) {
      mem.set(PacketIO.PROTO_BYTE, offset + 0, (byte)this.type.getValue());
      int varOffset = offset + 1;
      if (this.blockBreakingDecals.size() > 4096000) {
         throw ProtocolException.dictionaryTooLarge("BlockBreakingDecals", this.blockBreakingDecals.size(), 4096000);
      }

      varOffset += VarInt.set(mem, varOffset, this.blockBreakingDecals.size());

      for (Entry<String, BlockBreakingDecal> e : this.blockBreakingDecals.entrySet()) {
         varOffset += PacketIO.writeVarString(mem, varOffset, e.getKey(), 16384000);
         varOffset += e.getValue().serialize(mem, varOffset);
      }

      return varOffset - offset;
   }

   @Override
   public int computeSize() {
      int size = 1;
      int blockBreakingDecalsSize = 0;

      for (Entry<String, BlockBreakingDecal> kvp : this.blockBreakingDecals.entrySet()) {
         blockBreakingDecalsSize += PacketIO.stringSize(kvp.getKey()) + kvp.getValue().computeSize();
      }

      return size + VarInt.size(this.blockBreakingDecals.size()) + blockBreakingDecalsSize;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 1) {
         return ValidationResult.error("Buffer too small: expected at least 1 bytes");
      }

      int v = buffer.getByte(offset + 0) & 255;
      if (v >= 3) {
         return ValidationResult.error("Invalid UpdateType value for Type");
      }

      v = offset + 1;
      int blockBreakingDecalsCount = VarInt.peek(buffer, v);
      if (blockBreakingDecalsCount < 0) {
         return ValidationResult.error("Invalid dictionary count for BlockBreakingDecals");
      }

      if (blockBreakingDecalsCount > 4096000) {
         return ValidationResult.error("BlockBreakingDecals exceeds max length 4096000");
      }

      v += VarInt.size(blockBreakingDecalsCount);

      for (int i = 0; i < blockBreakingDecalsCount; i++) {
         int keyLen = VarInt.peek(buffer, v);
         if (keyLen < 0) {
            return ValidationResult.error("Invalid string length for key");
         }

         if (keyLen > 4096000) {
            return ValidationResult.error("key exceeds max length 4096000");
         }

         v += VarInt.size(keyLen);
         v += keyLen;
         if (v > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading key");
         }

         v += BlockBreakingDecal.computeBytesConsumed(buffer, v);
      }

      return ValidationResult.OK;
   }

   public UpdateBlockBreakingDecals clone() {
      UpdateBlockBreakingDecals copy = new UpdateBlockBreakingDecals();
      copy.type = this.type;
      Map<String, BlockBreakingDecal> m = new HashMap<>();

      for (Entry<String, BlockBreakingDecal> e : this.blockBreakingDecals.entrySet()) {
         m.put(e.getKey(), e.getValue().clone());
      }

      copy.blockBreakingDecals = m;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof UpdateBlockBreakingDecals other)
            ? false
            : Objects.equals(this.type, other.type) && Objects.equals(this.blockBreakingDecals, other.blockBreakingDecals);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.type, this.blockBreakingDecals);
   }
}
