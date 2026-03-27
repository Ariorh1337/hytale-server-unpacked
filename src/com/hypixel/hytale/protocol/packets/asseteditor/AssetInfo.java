package com.hypixel.hytale.protocol.packets.asseteditor;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AssetInfo {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 11;
   public static final int VARIABLE_FIELD_COUNT = 3;
   public static final int VARIABLE_BLOCK_START = 23;
   public static final int MAX_SIZE = 81920066;
   @Nullable
   public AssetPath path;
   @Nullable
   public AssetPath oldPath;
   public boolean isDeleted;
   public boolean isNew;
   public long lastModificationDate;
   @Nullable
   public String lastModificationUsername;

   public AssetInfo() {
   }

   public AssetInfo(
      @Nullable AssetPath path,
      @Nullable AssetPath oldPath,
      boolean isDeleted,
      boolean isNew,
      long lastModificationDate,
      @Nullable String lastModificationUsername
   ) {
      this.path = path;
      this.oldPath = oldPath;
      this.isDeleted = isDeleted;
      this.isNew = isNew;
      this.lastModificationDate = lastModificationDate;
      this.lastModificationUsername = lastModificationUsername;
   }

   public AssetInfo(@Nonnull AssetInfo other) {
      this.path = other.path;
      this.oldPath = other.oldPath;
      this.isDeleted = other.isDeleted;
      this.isNew = other.isNew;
      this.lastModificationDate = other.lastModificationDate;
      this.lastModificationUsername = other.lastModificationUsername;
   }

   @Nonnull
   public static AssetInfo deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 23) {
         throw ProtocolException.bufferTooSmall("AssetInfo", 23, buf.readableBytes() - offset);
      }

      AssetInfo obj = new AssetInfo();
      byte nullBits = buf.getByte(offset);
      obj.isDeleted = buf.getByte(offset + 1) != 0;
      obj.isNew = buf.getByte(offset + 2) != 0;
      obj.lastModificationDate = buf.getLongLE(offset + 3);
      if ((nullBits & 1) != 0) {
         int varPosBase0 = buf.getIntLE(offset + 11);
         if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 23) {
            throw ProtocolException.invalidOffset("Path", varPosBase0, buf.readableBytes());
         }

         int varPos0 = offset + 23 + varPosBase0;
         obj.path = AssetPath.deserialize(buf, varPos0);
      }

      if ((nullBits & 2) != 0) {
         int varPosBase1 = buf.getIntLE(offset + 15);
         if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 23) {
            throw ProtocolException.invalidOffset("OldPath", varPosBase1, buf.readableBytes());
         }

         int varPos1 = offset + 23 + varPosBase1;
         obj.oldPath = AssetPath.deserialize(buf, varPos1);
      }

      if ((nullBits & 4) != 0) {
         int varPosBase2 = buf.getIntLE(offset + 19);
         if (varPosBase2 < 0 || varPosBase2 > buf.writerIndex() - offset - 23) {
            throw ProtocolException.invalidOffset("LastModificationUsername", varPosBase2, buf.readableBytes());
         }

         int varPos2 = offset + 23 + varPosBase2;
         int lastModificationUsernameLen = VarInt.peek(buf, varPos2);
         if (lastModificationUsernameLen < 0) {
            throw ProtocolException.invalidVarInt("LastModificationUsername");
         }

         int lastModificationUsernameVarIntLen = VarInt.size(lastModificationUsernameLen);
         if (lastModificationUsernameLen > 4096000) {
            throw ProtocolException.stringTooLong("LastModificationUsername", lastModificationUsernameLen, 4096000);
         }

         if (varPos2 + lastModificationUsernameVarIntLen + lastModificationUsernameLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall(
               "LastModificationUsername", varPos2 + lastModificationUsernameVarIntLen + lastModificationUsernameLen, buf.readableBytes()
            );
         }

         obj.lastModificationUsername = PacketIO.readVarString(buf, varPos2, PacketIO.UTF8);
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 23;
      if ((nullBits & 1) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 11);
         if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 23) {
            throw ProtocolException.invalidOffset("Path", fieldOffset0, maxEnd);
         }

         int pos0 = offset + 23 + fieldOffset0;
         pos0 += AssetPath.computeBytesConsumed(buf, pos0);
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits & 2) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 15);
         if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 23) {
            throw ProtocolException.invalidOffset("OldPath", fieldOffset1, maxEnd);
         }

         int pos1 = offset + 23 + fieldOffset1;
         pos1 += AssetPath.computeBytesConsumed(buf, pos1);
         if (pos1 - offset > maxEnd) {
            maxEnd = pos1 - offset;
         }
      }

      if ((nullBits & 4) != 0) {
         int fieldOffset2 = buf.getIntLE(offset + 19);
         if (fieldOffset2 < 0 || fieldOffset2 > buf.writerIndex() - offset - 23) {
            throw ProtocolException.invalidOffset("LastModificationUsername", fieldOffset2, maxEnd);
         }

         int pos2 = offset + 23 + fieldOffset2;
         int sl = VarInt.peek(buf, pos2);
         pos2 += VarInt.size(sl) + sl;
         if (pos2 - offset > maxEnd) {
            maxEnd = pos2 - offset;
         }
      }

      return maxEnd;
   }

   public void serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      byte nullBits = 0;
      if (this.path != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.oldPath != null) {
         nullBits = (byte)(nullBits | 2);
      }

      if (this.lastModificationUsername != null) {
         nullBits = (byte)(nullBits | 4);
      }

      buf.writeByte(nullBits);
      buf.writeByte(this.isDeleted ? 1 : 0);
      buf.writeByte(this.isNew ? 1 : 0);
      buf.writeLongLE(this.lastModificationDate);
      int pathOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int oldPathOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int lastModificationUsernameOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.path != null) {
         buf.setIntLE(pathOffsetSlot, buf.writerIndex() - varBlockStart);
         this.path.serialize(buf);
      } else {
         buf.setIntLE(pathOffsetSlot, -1);
      }

      if (this.oldPath != null) {
         buf.setIntLE(oldPathOffsetSlot, buf.writerIndex() - varBlockStart);
         this.oldPath.serialize(buf);
      } else {
         buf.setIntLE(oldPathOffsetSlot, -1);
      }

      if (this.lastModificationUsername != null) {
         buf.setIntLE(lastModificationUsernameOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.lastModificationUsername, 4096000);
      } else {
         buf.setIntLE(lastModificationUsernameOffsetSlot, -1);
      }
   }

   public int computeSize() {
      int size = 23;
      if (this.path != null) {
         size += this.path.computeSize();
      }

      if (this.oldPath != null) {
         size += this.oldPath.computeSize();
      }

      if (this.lastModificationUsername != null) {
         size += PacketIO.stringSize(this.lastModificationUsername);
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 23) {
         return ValidationResult.error("Buffer too small: expected at least 23 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      if ((nullBits & 1) != 0) {
         int pathOffset = buffer.getIntLE(offset + 11);
         if (pathOffset < 0 || pathOffset > buffer.writerIndex() - offset - 23) {
            return ValidationResult.error("Invalid offset for Path");
         }

         int pos = offset + 23 + pathOffset;
         ValidationResult pathResult = AssetPath.validateStructure(buffer, pos);
         if (!pathResult.isValid()) {
            return ValidationResult.error("Invalid Path: " + pathResult.error());
         }

         pos += AssetPath.computeBytesConsumed(buffer, pos);
      }

      if ((nullBits & 2) != 0) {
         int oldPathOffset = buffer.getIntLE(offset + 15);
         if (oldPathOffset < 0 || oldPathOffset > buffer.writerIndex() - offset - 23) {
            return ValidationResult.error("Invalid offset for OldPath");
         }

         int pos = offset + 23 + oldPathOffset;
         ValidationResult oldPathResult = AssetPath.validateStructure(buffer, pos);
         if (!oldPathResult.isValid()) {
            return ValidationResult.error("Invalid OldPath: " + oldPathResult.error());
         }

         pos += AssetPath.computeBytesConsumed(buffer, pos);
      }

      if ((nullBits & 4) != 0) {
         int lastModificationUsernameOffset = buffer.getIntLE(offset + 19);
         if (lastModificationUsernameOffset < 0 || lastModificationUsernameOffset > buffer.writerIndex() - offset - 23) {
            return ValidationResult.error("Invalid offset for LastModificationUsername");
         }

         int pos = offset + 23 + lastModificationUsernameOffset;
         int lastModificationUsernameLen = VarInt.peek(buffer, pos);
         if (lastModificationUsernameLen < 0) {
            return ValidationResult.error("Invalid string length for LastModificationUsername");
         }

         if (lastModificationUsernameLen > 4096000) {
            return ValidationResult.error("LastModificationUsername exceeds max length 4096000");
         }

         pos += VarInt.size(lastModificationUsernameLen);
         pos += lastModificationUsernameLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading LastModificationUsername");
         }
      }

      return ValidationResult.OK;
   }

   public AssetInfo clone() {
      AssetInfo copy = new AssetInfo();
      copy.path = this.path != null ? this.path.clone() : null;
      copy.oldPath = this.oldPath != null ? this.oldPath.clone() : null;
      copy.isDeleted = this.isDeleted;
      copy.isNew = this.isNew;
      copy.lastModificationDate = this.lastModificationDate;
      copy.lastModificationUsername = this.lastModificationUsername;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof AssetInfo other)
            ? false
            : Objects.equals(this.path, other.path)
               && Objects.equals(this.oldPath, other.oldPath)
               && this.isDeleted == other.isDeleted
               && this.isNew == other.isNew
               && this.lastModificationDate == other.lastModificationDate
               && Objects.equals(this.lastModificationUsername, other.lastModificationUsername);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.path, this.oldPath, this.isDeleted, this.isNew, this.lastModificationDate, this.lastModificationUsername);
   }
}
