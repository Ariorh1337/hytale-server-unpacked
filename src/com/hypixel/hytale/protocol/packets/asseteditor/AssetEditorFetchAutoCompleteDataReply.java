package com.hypixel.hytale.protocol.packets.asseteditor;

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

public class AssetEditorFetchAutoCompleteDataReply implements Packet, ToClientPacket {
   public static final int PACKET_ID = 332;
   public static final boolean IS_COMPRESSED = false;
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 5;
   public static final int VARIABLE_FIELD_COUNT = 1;
   public static final int VARIABLE_BLOCK_START = 5;
   public static final int MAX_SIZE = 1677721600;
   public int token;
   @Nullable
   public String[] results;

   @Override
   public int getId() {
      return 332;
   }

   @Override
   public NetworkChannel getChannel() {
      return NetworkChannel.Default;
   }

   public AssetEditorFetchAutoCompleteDataReply() {
   }

   public AssetEditorFetchAutoCompleteDataReply(int token, @Nullable String[] results) {
      this.token = token;
      this.results = results;
   }

   public AssetEditorFetchAutoCompleteDataReply(@Nonnull AssetEditorFetchAutoCompleteDataReply other) {
      this.token = other.token;
      this.results = other.results;
   }

   @Nonnull
   public static AssetEditorFetchAutoCompleteDataReply deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 5) {
         throw ProtocolException.bufferTooSmall("AssetEditorFetchAutoCompleteDataReply", 5, buf.readableBytes() - offset);
      }

      AssetEditorFetchAutoCompleteDataReply obj = new AssetEditorFetchAutoCompleteDataReply();
      byte nullBits = buf.getByte(offset);
      obj.token = buf.getIntLE(offset + 1);
      int pos = offset + 5;
      if ((nullBits & 1) != 0) {
         int resultsCount = VarInt.peek(buf, pos);
         if (resultsCount < 0) {
            throw ProtocolException.invalidVarInt("Results");
         }

         int resultsVarLen = VarInt.size(resultsCount);
         if (resultsCount > 4096000) {
            throw ProtocolException.arrayTooLong("Results", resultsCount, 4096000);
         }

         if (pos + resultsVarLen + resultsCount * 1L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Results", pos + resultsVarLen + resultsCount * 1, buf.readableBytes());
         }

         pos += resultsVarLen;
         obj.results = new String[resultsCount];

         for (int i = 0; i < resultsCount; i++) {
            int strLen = VarInt.peek(buf, pos);
            if (strLen < 0) {
               throw ProtocolException.invalidVarInt("results[" + i + "]");
            }

            int strVarLen = VarInt.size(strLen);
            if (strLen > 4096000) {
               throw ProtocolException.stringTooLong("results[" + i + "]", strLen, 4096000);
            }

            if (pos + strVarLen + strLen > buf.readableBytes()) {
               throw ProtocolException.bufferTooSmall("results[" + i + "]", pos + strVarLen + strLen, buf.readableBytes());
            }

            obj.results[i] = PacketIO.readVarString(buf, pos);
            pos += strVarLen + strLen;
         }
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int pos = offset + 5;
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
      if (this.results != null) {
         nullBits = (byte)(nullBits | 1);
      }

      buf.writeByte(nullBits);
      buf.writeIntLE(this.token);
      if (this.results != null) {
         if (this.results.length > 4096000) {
            throw ProtocolException.arrayTooLong("Results", this.results.length, 4096000);
         }

         VarInt.write(buf, this.results.length);

         for (String item : this.results) {
            PacketIO.writeVarString(buf, item, 4096000);
         }
      }
   }

   @Override
   public int computeSize() {
      int size = 5;
      if (this.results != null) {
         int resultsSize = 0;

         for (String elem : this.results) {
            resultsSize += PacketIO.stringSize(elem);
         }

         size += VarInt.size(this.results.length) + resultsSize;
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 5) {
         return ValidationResult.error("Buffer too small: expected at least 5 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      int pos = offset + 5;
      if ((nullBits & 1) != 0) {
         int resultsCount = VarInt.peek(buffer, pos);
         if (resultsCount < 0) {
            return ValidationResult.error("Invalid array count for Results");
         }

         if (resultsCount > 4096000) {
            return ValidationResult.error("Results exceeds max length 4096000");
         }

         pos += VarInt.size(resultsCount);

         for (int i = 0; i < resultsCount; i++) {
            int strLen = VarInt.peek(buffer, pos);
            if (strLen < 0) {
               return ValidationResult.error("Invalid string length in Results");
            }

            pos += VarInt.size(strLen);
            pos += strLen;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading string in Results");
            }
         }
      }

      return ValidationResult.OK;
   }

   public AssetEditorFetchAutoCompleteDataReply clone() {
      AssetEditorFetchAutoCompleteDataReply copy = new AssetEditorFetchAutoCompleteDataReply();
      copy.token = this.token;
      copy.results = this.results != null ? Arrays.copyOf(this.results, this.results.length) : null;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof AssetEditorFetchAutoCompleteDataReply other) ? false : this.token == other.token && Arrays.equals(this.results, other.results);
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      result = 31 * result + Integer.hashCode(this.token);
      return 31 * result + Arrays.hashCode(this.results);
   }
}
