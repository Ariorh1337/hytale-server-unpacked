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
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AssetEditorRequestChildrenListReply implements Packet, ToClientPacket {
   public static final int PACKET_ID = 322;
   public static final boolean IS_COMPRESSED = false;
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 1;
   public static final int VARIABLE_FIELD_COUNT = 2;
   public static final int VARIABLE_BLOCK_START = 9;
   public static final int MAX_SIZE = 1677721600;
   @Nullable
   public AssetPath path;
   @Nullable
   public String[] childrenIds;

   @Override
   public int getId() {
      return 322;
   }

   @Override
   public NetworkChannel getChannel() {
      return NetworkChannel.Default;
   }

   public AssetEditorRequestChildrenListReply() {
   }

   public AssetEditorRequestChildrenListReply(@Nullable AssetPath path, @Nullable String[] childrenIds) {
      this.path = path;
      this.childrenIds = childrenIds;
   }

   public AssetEditorRequestChildrenListReply(@Nonnull AssetEditorRequestChildrenListReply other) {
      this.path = other.path;
      this.childrenIds = other.childrenIds;
   }

   @Nonnull
   public static AssetEditorRequestChildrenListReply deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 9) {
         throw ProtocolException.bufferTooSmall("AssetEditorRequestChildrenListReply", 9, buf.readableBytes() - offset);
      }

      AssetEditorRequestChildrenListReply obj = new AssetEditorRequestChildrenListReply();
      byte nullBits = buf.getByte(offset);
      if ((nullBits & 1) != 0) {
         int varPosBase0 = buf.getIntLE(offset + 1);
         if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 9) {
            throw ProtocolException.invalidOffset("Path", varPosBase0, buf.readableBytes());
         }

         int varPos0 = offset + 9 + varPosBase0;
         obj.path = AssetPath.deserialize(buf, varPos0);
      }

      if ((nullBits & 2) != 0) {
         int varPosBase1 = buf.getIntLE(offset + 5);
         if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 9) {
            throw ProtocolException.invalidOffset("ChildrenIds", varPosBase1, buf.readableBytes());
         }

         int varPos1 = offset + 9 + varPosBase1;
         int childrenIdsCount = VarInt.peek(buf, varPos1);
         if (childrenIdsCount < 0) {
            throw ProtocolException.invalidVarInt("ChildrenIds");
         }

         int varIntLen = VarInt.size(childrenIdsCount);
         if (childrenIdsCount > 4096000) {
            throw ProtocolException.arrayTooLong("ChildrenIds", childrenIdsCount, 4096000);
         }

         if (varPos1 + varIntLen + childrenIdsCount * 1L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("ChildrenIds", varPos1 + varIntLen + childrenIdsCount * 1, buf.readableBytes());
         }

         obj.childrenIds = new String[childrenIdsCount];
         int elemPos = varPos1 + varIntLen;

         for (int i = 0; i < childrenIdsCount; i++) {
            int strLen = VarInt.peek(buf, elemPos);
            if (strLen < 0) {
               throw ProtocolException.invalidVarInt("childrenIds[" + i + "]");
            }

            int strVarLen = VarInt.size(strLen);
            if (strLen > 4096000) {
               throw ProtocolException.stringTooLong("childrenIds[" + i + "]", strLen, 4096000);
            }

            if (elemPos + strVarLen + strLen > buf.readableBytes()) {
               throw ProtocolException.bufferTooSmall("childrenIds[" + i + "]", elemPos + strVarLen + strLen, buf.readableBytes());
            }

            obj.childrenIds[i] = PacketIO.readVarString(buf, elemPos);
            elemPos += strVarLen + strLen;
         }
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 9;
      if ((nullBits & 1) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 1);
         if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 9) {
            throw ProtocolException.invalidOffset("Path", fieldOffset0, maxEnd);
         }

         int pos0 = offset + 9 + fieldOffset0;
         pos0 += AssetPath.computeBytesConsumed(buf, pos0);
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits & 2) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 5);
         if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 9) {
            throw ProtocolException.invalidOffset("ChildrenIds", fieldOffset1, maxEnd);
         }

         int pos1 = offset + 9 + fieldOffset1;
         int arrLen = VarInt.peek(buf, pos1);
         pos1 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            int sl = VarInt.peek(buf, pos1);
            pos1 += VarInt.size(sl) + sl;
         }

         if (pos1 - offset > maxEnd) {
            maxEnd = pos1 - offset;
         }
      }

      return maxEnd;
   }

   @Override
   public void serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      byte nullBits = 0;
      if (this.path != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.childrenIds != null) {
         nullBits = (byte)(nullBits | 2);
      }

      buf.writeByte(nullBits);
      int pathOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int childrenIdsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.path != null) {
         buf.setIntLE(pathOffsetSlot, buf.writerIndex() - varBlockStart);
         this.path.serialize(buf);
      } else {
         buf.setIntLE(pathOffsetSlot, -1);
      }

      if (this.childrenIds != null) {
         buf.setIntLE(childrenIdsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.childrenIds.length > 4096000) {
            throw ProtocolException.arrayTooLong("ChildrenIds", this.childrenIds.length, 4096000);
         }

         VarInt.write(buf, this.childrenIds.length);

         for (String item : this.childrenIds) {
            PacketIO.writeVarString(buf, item, 4096000);
         }
      } else {
         buf.setIntLE(childrenIdsOffsetSlot, -1);
      }
   }

   @Override
   public int computeSize() {
      int size = 9;
      if (this.path != null) {
         size += this.path.computeSize();
      }

      if (this.childrenIds != null) {
         int childrenIdsSize = 0;

         for (String elem : this.childrenIds) {
            childrenIdsSize += PacketIO.stringSize(elem);
         }

         size += VarInt.size(this.childrenIds.length) + childrenIdsSize;
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 9) {
         return ValidationResult.error("Buffer too small: expected at least 9 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      if ((nullBits & 1) != 0) {
         int pathOffset = buffer.getIntLE(offset + 1);
         if (pathOffset < 0 || pathOffset > buffer.writerIndex() - offset - 9) {
            return ValidationResult.error("Invalid offset for Path");
         }

         int pos = offset + 9 + pathOffset;
         ValidationResult pathResult = AssetPath.validateStructure(buffer, pos);
         if (!pathResult.isValid()) {
            return ValidationResult.error("Invalid Path: " + pathResult.error());
         }

         pos += AssetPath.computeBytesConsumed(buffer, pos);
      }

      if ((nullBits & 2) != 0) {
         int childrenIdsOffset = buffer.getIntLE(offset + 5);
         if (childrenIdsOffset < 0 || childrenIdsOffset > buffer.writerIndex() - offset - 9) {
            return ValidationResult.error("Invalid offset for ChildrenIds");
         }

         int pos = offset + 9 + childrenIdsOffset;
         int childrenIdsCount = VarInt.peek(buffer, pos);
         if (childrenIdsCount < 0) {
            return ValidationResult.error("Invalid array count for ChildrenIds");
         }

         if (childrenIdsCount > 4096000) {
            return ValidationResult.error("ChildrenIds exceeds max length 4096000");
         }

         pos += VarInt.size(childrenIdsCount);

         for (int i = 0; i < childrenIdsCount; i++) {
            int strLen = VarInt.peek(buffer, pos);
            if (strLen < 0) {
               return ValidationResult.error("Invalid string length in ChildrenIds");
            }

            pos += VarInt.size(strLen);
            pos += strLen;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading string in ChildrenIds");
            }
         }
      }

      return ValidationResult.OK;
   }

   public AssetEditorRequestChildrenListReply clone() {
      AssetEditorRequestChildrenListReply copy = new AssetEditorRequestChildrenListReply();
      copy.path = this.path != null ? this.path.clone() : null;
      copy.childrenIds = this.childrenIds != null ? Arrays.copyOf(this.childrenIds, this.childrenIds.length) : null;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof AssetEditorRequestChildrenListReply other)
            ? false
            : Objects.equals(this.path, other.path) && Arrays.equals(this.childrenIds, other.childrenIds);
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      result = 31 * result + Objects.hashCode(this.path);
      return 31 * result + Arrays.hashCode(this.childrenIds);
   }
}
