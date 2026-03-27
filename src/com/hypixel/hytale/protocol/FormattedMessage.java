package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FormattedMessage {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 6;
   public static final int VARIABLE_FIELD_COUNT = 8;
   public static final int VARIABLE_BLOCK_START = 38;
   public static final int MAX_SIZE = 1677721600;
   @Nullable
   public String rawText;
   @Nullable
   public String messageId;
   @Nullable
   public FormattedMessage[] children;
   @Nullable
   public Map<String, ParamValue> params;
   @Nullable
   public Map<String, FormattedMessage> messageParams;
   @Nullable
   public String color;
   @Nonnull
   public MaybeBool bold = MaybeBool.Null;
   @Nonnull
   public MaybeBool italic = MaybeBool.Null;
   @Nonnull
   public MaybeBool monospace = MaybeBool.Null;
   @Nonnull
   public MaybeBool underlined = MaybeBool.Null;
   @Nullable
   public String link;
   public boolean markupEnabled;
   @Nullable
   public FormattedMessageImage image;

   public FormattedMessage() {
   }

   public FormattedMessage(
      @Nullable String rawText,
      @Nullable String messageId,
      @Nullable FormattedMessage[] children,
      @Nullable Map<String, ParamValue> params,
      @Nullable Map<String, FormattedMessage> messageParams,
      @Nullable String color,
      @Nonnull MaybeBool bold,
      @Nonnull MaybeBool italic,
      @Nonnull MaybeBool monospace,
      @Nonnull MaybeBool underlined,
      @Nullable String link,
      boolean markupEnabled,
      @Nullable FormattedMessageImage image
   ) {
      this.rawText = rawText;
      this.messageId = messageId;
      this.children = children;
      this.params = params;
      this.messageParams = messageParams;
      this.color = color;
      this.bold = bold;
      this.italic = italic;
      this.monospace = monospace;
      this.underlined = underlined;
      this.link = link;
      this.markupEnabled = markupEnabled;
      this.image = image;
   }

   public FormattedMessage(@Nonnull FormattedMessage other) {
      this.rawText = other.rawText;
      this.messageId = other.messageId;
      this.children = other.children;
      this.params = other.params;
      this.messageParams = other.messageParams;
      this.color = other.color;
      this.bold = other.bold;
      this.italic = other.italic;
      this.monospace = other.monospace;
      this.underlined = other.underlined;
      this.link = other.link;
      this.markupEnabled = other.markupEnabled;
      this.image = other.image;
   }

   @Nonnull
   public static FormattedMessage deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 38) {
         throw ProtocolException.bufferTooSmall("FormattedMessage", 38, buf.readableBytes() - offset);
      }

      FormattedMessage obj = new FormattedMessage();
      byte nullBits = buf.getByte(offset);
      obj.bold = MaybeBool.fromValue(buf.getByte(offset + 1));
      obj.italic = MaybeBool.fromValue(buf.getByte(offset + 2));
      obj.monospace = MaybeBool.fromValue(buf.getByte(offset + 3));
      obj.underlined = MaybeBool.fromValue(buf.getByte(offset + 4));
      obj.markupEnabled = buf.getByte(offset + 5) != 0;
      if ((nullBits & 1) != 0) {
         int varPosBase0 = buf.getIntLE(offset + 6);
         if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 38) {
            throw ProtocolException.invalidOffset("RawText", varPosBase0, buf.readableBytes());
         }

         int varPos0 = offset + 38 + varPosBase0;
         int rawTextLen = VarInt.peek(buf, varPos0);
         if (rawTextLen < 0) {
            throw ProtocolException.invalidVarInt("RawText");
         }

         int rawTextVarIntLen = VarInt.size(rawTextLen);
         if (rawTextLen > 4096000) {
            throw ProtocolException.stringTooLong("RawText", rawTextLen, 4096000);
         }

         if (varPos0 + rawTextVarIntLen + rawTextLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("RawText", varPos0 + rawTextVarIntLen + rawTextLen, buf.readableBytes());
         }

         obj.rawText = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
      }

      if ((nullBits & 2) != 0) {
         int varPosBase1 = buf.getIntLE(offset + 10);
         if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 38) {
            throw ProtocolException.invalidOffset("MessageId", varPosBase1, buf.readableBytes());
         }

         int varPos1 = offset + 38 + varPosBase1;
         int messageIdLen = VarInt.peek(buf, varPos1);
         if (messageIdLen < 0) {
            throw ProtocolException.invalidVarInt("MessageId");
         }

         int messageIdVarIntLen = VarInt.size(messageIdLen);
         if (messageIdLen > 4096000) {
            throw ProtocolException.stringTooLong("MessageId", messageIdLen, 4096000);
         }

         if (varPos1 + messageIdVarIntLen + messageIdLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("MessageId", varPos1 + messageIdVarIntLen + messageIdLen, buf.readableBytes());
         }

         obj.messageId = PacketIO.readVarString(buf, varPos1, PacketIO.UTF8);
      }

      if ((nullBits & 4) != 0) {
         int varPosBase2 = buf.getIntLE(offset + 14);
         if (varPosBase2 < 0 || varPosBase2 > buf.writerIndex() - offset - 38) {
            throw ProtocolException.invalidOffset("Children", varPosBase2, buf.readableBytes());
         }

         int varPos2 = offset + 38 + varPosBase2;
         int childrenCount = VarInt.peek(buf, varPos2);
         if (childrenCount < 0) {
            throw ProtocolException.invalidVarInt("Children");
         }

         int varIntLen = VarInt.size(childrenCount);
         if (childrenCount > 4096000) {
            throw ProtocolException.arrayTooLong("Children", childrenCount, 4096000);
         }

         if (varPos2 + varIntLen + childrenCount * 6L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Children", varPos2 + varIntLen + childrenCount * 6, buf.readableBytes());
         }

         obj.children = new FormattedMessage[childrenCount];
         int elemPos = varPos2 + varIntLen;

         for (int i = 0; i < childrenCount; i++) {
            obj.children[i] = deserialize(buf, elemPos);
            elemPos += computeBytesConsumed(buf, elemPos);
         }
      }

      if ((nullBits & 8) != 0) {
         int varPosBase3 = buf.getIntLE(offset + 18);
         if (varPosBase3 < 0 || varPosBase3 > buf.writerIndex() - offset - 38) {
            throw ProtocolException.invalidOffset("Params", varPosBase3, buf.readableBytes());
         }

         int varPos3 = offset + 38 + varPosBase3;
         int paramsCount = VarInt.peek(buf, varPos3);
         if (paramsCount < 0) {
            throw ProtocolException.invalidVarInt("Params");
         }

         int varIntLen = VarInt.size(paramsCount);
         if (paramsCount > 4096000) {
            throw ProtocolException.dictionaryTooLarge("Params", paramsCount, 4096000);
         }

         obj.params = new HashMap<>(paramsCount);
         int dictPos = varPos3 + varIntLen;

         for (int i = 0; i < paramsCount; i++) {
            int keyLen = VarInt.peek(buf, dictPos);
            if (keyLen < 0) {
               throw ProtocolException.invalidVarInt("key");
            }

            int keyVarLen = VarInt.size(keyLen);
            if (keyLen > 4096000) {
               throw ProtocolException.stringTooLong("key", keyLen, 4096000);
            }

            if (dictPos + keyVarLen + keyLen > buf.readableBytes()) {
               throw ProtocolException.bufferTooSmall("key", dictPos + keyVarLen + keyLen, buf.readableBytes());
            }

            String key = PacketIO.readVarString(buf, dictPos);
            dictPos += keyVarLen + keyLen;
            ParamValue val = ParamValue.deserialize(buf, dictPos);
            dictPos += ParamValue.computeBytesConsumed(buf, dictPos);
            if (obj.params.put(key, val) != null) {
               throw ProtocolException.duplicateKey("params", key);
            }
         }
      }

      if ((nullBits & 16) != 0) {
         int varPosBase4 = buf.getIntLE(offset + 22);
         if (varPosBase4 < 0 || varPosBase4 > buf.writerIndex() - offset - 38) {
            throw ProtocolException.invalidOffset("MessageParams", varPosBase4, buf.readableBytes());
         }

         int varPos4 = offset + 38 + varPosBase4;
         int messageParamsCount = VarInt.peek(buf, varPos4);
         if (messageParamsCount < 0) {
            throw ProtocolException.invalidVarInt("MessageParams");
         }

         int varIntLen = VarInt.size(messageParamsCount);
         if (messageParamsCount > 4096000) {
            throw ProtocolException.dictionaryTooLarge("MessageParams", messageParamsCount, 4096000);
         }

         obj.messageParams = new HashMap<>(messageParamsCount);
         int dictPos = varPos4 + varIntLen;

         for (int i = 0; i < messageParamsCount; i++) {
            int keyLen = VarInt.peek(buf, dictPos);
            if (keyLen < 0) {
               throw ProtocolException.invalidVarInt("key");
            }

            int keyVarLen = VarInt.size(keyLen);
            if (keyLen > 4096000) {
               throw ProtocolException.stringTooLong("key", keyLen, 4096000);
            }

            if (dictPos + keyVarLen + keyLen > buf.readableBytes()) {
               throw ProtocolException.bufferTooSmall("key", dictPos + keyVarLen + keyLen, buf.readableBytes());
            }

            String key = PacketIO.readVarString(buf, dictPos);
            dictPos += keyVarLen + keyLen;
            FormattedMessage val = deserialize(buf, dictPos);
            dictPos += computeBytesConsumed(buf, dictPos);
            if (obj.messageParams.put(key, val) != null) {
               throw ProtocolException.duplicateKey("messageParams", key);
            }
         }
      }

      if ((nullBits & 32) != 0) {
         int varPosBase5 = buf.getIntLE(offset + 26);
         if (varPosBase5 < 0 || varPosBase5 > buf.writerIndex() - offset - 38) {
            throw ProtocolException.invalidOffset("Color", varPosBase5, buf.readableBytes());
         }

         int varPos5 = offset + 38 + varPosBase5;
         int colorLen = VarInt.peek(buf, varPos5);
         if (colorLen < 0) {
            throw ProtocolException.invalidVarInt("Color");
         }

         int colorVarIntLen = VarInt.size(colorLen);
         if (colorLen > 4096000) {
            throw ProtocolException.stringTooLong("Color", colorLen, 4096000);
         }

         if (varPos5 + colorVarIntLen + colorLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Color", varPos5 + colorVarIntLen + colorLen, buf.readableBytes());
         }

         obj.color = PacketIO.readVarString(buf, varPos5, PacketIO.UTF8);
      }

      if ((nullBits & 64) != 0) {
         int varPosBase6 = buf.getIntLE(offset + 30);
         if (varPosBase6 < 0 || varPosBase6 > buf.writerIndex() - offset - 38) {
            throw ProtocolException.invalidOffset("Link", varPosBase6, buf.readableBytes());
         }

         int varPos6 = offset + 38 + varPosBase6;
         int linkLen = VarInt.peek(buf, varPos6);
         if (linkLen < 0) {
            throw ProtocolException.invalidVarInt("Link");
         }

         int linkVarIntLen = VarInt.size(linkLen);
         if (linkLen > 4096000) {
            throw ProtocolException.stringTooLong("Link", linkLen, 4096000);
         }

         if (varPos6 + linkVarIntLen + linkLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Link", varPos6 + linkVarIntLen + linkLen, buf.readableBytes());
         }

         obj.link = PacketIO.readVarString(buf, varPos6, PacketIO.UTF8);
      }

      if ((nullBits & 128) != 0) {
         int varPosBase7 = buf.getIntLE(offset + 34);
         if (varPosBase7 < 0 || varPosBase7 > buf.writerIndex() - offset - 38) {
            throw ProtocolException.invalidOffset("Image", varPosBase7, buf.readableBytes());
         }

         int varPos7 = offset + 38 + varPosBase7;
         obj.image = FormattedMessageImage.deserialize(buf, varPos7);
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 38;
      if ((nullBits & 1) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 6);
         if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 38) {
            throw ProtocolException.invalidOffset("RawText", fieldOffset0, maxEnd);
         }

         int pos0 = offset + 38 + fieldOffset0;
         int sl = VarInt.peek(buf, pos0);
         pos0 += VarInt.size(sl) + sl;
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits & 2) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 10);
         if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 38) {
            throw ProtocolException.invalidOffset("MessageId", fieldOffset1, maxEnd);
         }

         int pos1 = offset + 38 + fieldOffset1;
         int sl = VarInt.peek(buf, pos1);
         pos1 += VarInt.size(sl) + sl;
         if (pos1 - offset > maxEnd) {
            maxEnd = pos1 - offset;
         }
      }

      if ((nullBits & 4) != 0) {
         int fieldOffset2 = buf.getIntLE(offset + 14);
         if (fieldOffset2 < 0 || fieldOffset2 > buf.writerIndex() - offset - 38) {
            throw ProtocolException.invalidOffset("Children", fieldOffset2, maxEnd);
         }

         int pos2 = offset + 38 + fieldOffset2;
         int arrLen = VarInt.peek(buf, pos2);
         pos2 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            pos2 += computeBytesConsumed(buf, pos2);
         }

         if (pos2 - offset > maxEnd) {
            maxEnd = pos2 - offset;
         }
      }

      if ((nullBits & 8) != 0) {
         int fieldOffset3 = buf.getIntLE(offset + 18);
         if (fieldOffset3 < 0 || fieldOffset3 > buf.writerIndex() - offset - 38) {
            throw ProtocolException.invalidOffset("Params", fieldOffset3, maxEnd);
         }

         int pos3 = offset + 38 + fieldOffset3;
         int dictLen = VarInt.peek(buf, pos3);
         pos3 += VarInt.size(dictLen);

         for (int i = 0; i < dictLen; i++) {
            int sl = VarInt.peek(buf, pos3);
            pos3 += VarInt.size(sl) + sl;
            pos3 += ParamValue.computeBytesConsumed(buf, pos3);
         }

         if (pos3 - offset > maxEnd) {
            maxEnd = pos3 - offset;
         }
      }

      if ((nullBits & 16) != 0) {
         int fieldOffset4 = buf.getIntLE(offset + 22);
         if (fieldOffset4 < 0 || fieldOffset4 > buf.writerIndex() - offset - 38) {
            throw ProtocolException.invalidOffset("MessageParams", fieldOffset4, maxEnd);
         }

         int pos4 = offset + 38 + fieldOffset4;
         int dictLen = VarInt.peek(buf, pos4);
         pos4 += VarInt.size(dictLen);

         for (int i = 0; i < dictLen; i++) {
            int sl = VarInt.peek(buf, pos4);
            pos4 += VarInt.size(sl) + sl;
            pos4 += computeBytesConsumed(buf, pos4);
         }

         if (pos4 - offset > maxEnd) {
            maxEnd = pos4 - offset;
         }
      }

      if ((nullBits & 32) != 0) {
         int fieldOffset5 = buf.getIntLE(offset + 26);
         if (fieldOffset5 < 0 || fieldOffset5 > buf.writerIndex() - offset - 38) {
            throw ProtocolException.invalidOffset("Color", fieldOffset5, maxEnd);
         }

         int pos5 = offset + 38 + fieldOffset5;
         int sl = VarInt.peek(buf, pos5);
         pos5 += VarInt.size(sl) + sl;
         if (pos5 - offset > maxEnd) {
            maxEnd = pos5 - offset;
         }
      }

      if ((nullBits & 64) != 0) {
         int fieldOffset6 = buf.getIntLE(offset + 30);
         if (fieldOffset6 < 0 || fieldOffset6 > buf.writerIndex() - offset - 38) {
            throw ProtocolException.invalidOffset("Link", fieldOffset6, maxEnd);
         }

         int pos6 = offset + 38 + fieldOffset6;
         int sl = VarInt.peek(buf, pos6);
         pos6 += VarInt.size(sl) + sl;
         if (pos6 - offset > maxEnd) {
            maxEnd = pos6 - offset;
         }
      }

      if ((nullBits & 128) != 0) {
         int fieldOffset7 = buf.getIntLE(offset + 34);
         if (fieldOffset7 < 0 || fieldOffset7 > buf.writerIndex() - offset - 38) {
            throw ProtocolException.invalidOffset("Image", fieldOffset7, maxEnd);
         }

         int pos7 = offset + 38 + fieldOffset7;
         pos7 += FormattedMessageImage.computeBytesConsumed(buf, pos7);
         if (pos7 - offset > maxEnd) {
            maxEnd = pos7 - offset;
         }
      }

      return maxEnd;
   }

   public void serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      byte nullBits = 0;
      if (this.rawText != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.messageId != null) {
         nullBits = (byte)(nullBits | 2);
      }

      if (this.children != null) {
         nullBits = (byte)(nullBits | 4);
      }

      if (this.params != null) {
         nullBits = (byte)(nullBits | 8);
      }

      if (this.messageParams != null) {
         nullBits = (byte)(nullBits | 16);
      }

      if (this.color != null) {
         nullBits = (byte)(nullBits | 32);
      }

      if (this.link != null) {
         nullBits = (byte)(nullBits | 64);
      }

      if (this.image != null) {
         nullBits = (byte)(nullBits | 128);
      }

      buf.writeByte(nullBits);
      buf.writeByte(this.bold.getValue());
      buf.writeByte(this.italic.getValue());
      buf.writeByte(this.monospace.getValue());
      buf.writeByte(this.underlined.getValue());
      buf.writeByte(this.markupEnabled ? 1 : 0);
      int rawTextOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int messageIdOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int childrenOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int paramsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int messageParamsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int colorOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int linkOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int imageOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.rawText != null) {
         buf.setIntLE(rawTextOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.rawText, 4096000);
      } else {
         buf.setIntLE(rawTextOffsetSlot, -1);
      }

      if (this.messageId != null) {
         buf.setIntLE(messageIdOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.messageId, 4096000);
      } else {
         buf.setIntLE(messageIdOffsetSlot, -1);
      }

      if (this.children != null) {
         buf.setIntLE(childrenOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.children.length > 4096000) {
            throw ProtocolException.arrayTooLong("Children", this.children.length, 4096000);
         }

         VarInt.write(buf, this.children.length);

         for (FormattedMessage item : this.children) {
            item.serialize(buf);
         }
      } else {
         buf.setIntLE(childrenOffsetSlot, -1);
      }

      if (this.params != null) {
         buf.setIntLE(paramsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.params.size() > 4096000) {
            throw ProtocolException.dictionaryTooLarge("Params", this.params.size(), 4096000);
         }

         VarInt.write(buf, this.params.size());

         for (Entry<String, ParamValue> e : this.params.entrySet()) {
            PacketIO.writeVarString(buf, e.getKey(), 4096000);
            e.getValue().serializeWithTypeId(buf);
         }
      } else {
         buf.setIntLE(paramsOffsetSlot, -1);
      }

      if (this.messageParams != null) {
         buf.setIntLE(messageParamsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.messageParams.size() > 4096000) {
            throw ProtocolException.dictionaryTooLarge("MessageParams", this.messageParams.size(), 4096000);
         }

         VarInt.write(buf, this.messageParams.size());

         for (Entry<String, FormattedMessage> e : this.messageParams.entrySet()) {
            PacketIO.writeVarString(buf, e.getKey(), 4096000);
            e.getValue().serialize(buf);
         }
      } else {
         buf.setIntLE(messageParamsOffsetSlot, -1);
      }

      if (this.color != null) {
         buf.setIntLE(colorOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.color, 4096000);
      } else {
         buf.setIntLE(colorOffsetSlot, -1);
      }

      if (this.link != null) {
         buf.setIntLE(linkOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.link, 4096000);
      } else {
         buf.setIntLE(linkOffsetSlot, -1);
      }

      if (this.image != null) {
         buf.setIntLE(imageOffsetSlot, buf.writerIndex() - varBlockStart);
         this.image.serialize(buf);
      } else {
         buf.setIntLE(imageOffsetSlot, -1);
      }
   }

   public int computeSize() {
      int size = 38;
      if (this.rawText != null) {
         size += PacketIO.stringSize(this.rawText);
      }

      if (this.messageId != null) {
         size += PacketIO.stringSize(this.messageId);
      }

      if (this.children != null) {
         int childrenSize = 0;

         for (FormattedMessage elem : this.children) {
            childrenSize += elem.computeSize();
         }

         size += VarInt.size(this.children.length) + childrenSize;
      }

      if (this.params != null) {
         int paramsSize = 0;

         for (Entry<String, ParamValue> kvp : this.params.entrySet()) {
            paramsSize += PacketIO.stringSize(kvp.getKey()) + kvp.getValue().computeSizeWithTypeId();
         }

         size += VarInt.size(this.params.size()) + paramsSize;
      }

      if (this.messageParams != null) {
         int messageParamsSize = 0;

         for (Entry<String, FormattedMessage> kvp : this.messageParams.entrySet()) {
            messageParamsSize += PacketIO.stringSize(kvp.getKey()) + kvp.getValue().computeSize();
         }

         size += VarInt.size(this.messageParams.size()) + messageParamsSize;
      }

      if (this.color != null) {
         size += PacketIO.stringSize(this.color);
      }

      if (this.link != null) {
         size += PacketIO.stringSize(this.link);
      }

      if (this.image != null) {
         size += this.image.computeSize();
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 38) {
         return ValidationResult.error("Buffer too small: expected at least 38 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      int v = buffer.getByte(offset + 1) & 255;
      if (v >= 3) {
         return ValidationResult.error("Invalid MaybeBool value for Bold");
      }

      v = buffer.getByte(offset + 2) & 255;
      if (v >= 3) {
         return ValidationResult.error("Invalid MaybeBool value for Italic");
      }

      v = buffer.getByte(offset + 3) & 255;
      if (v >= 3) {
         return ValidationResult.error("Invalid MaybeBool value for Monospace");
      }

      v = buffer.getByte(offset + 4) & 255;
      if (v >= 3) {
         return ValidationResult.error("Invalid MaybeBool value for Underlined");
      }

      if ((nullBits & 1) != 0) {
         v = buffer.getIntLE(offset + 6);
         if (v < 0 || v > buffer.writerIndex() - offset - 38) {
            return ValidationResult.error("Invalid offset for RawText");
         }

         int pos = offset + 38 + v;
         int rawTextLen = VarInt.peek(buffer, pos);
         if (rawTextLen < 0) {
            return ValidationResult.error("Invalid string length for RawText");
         }

         if (rawTextLen > 4096000) {
            return ValidationResult.error("RawText exceeds max length 4096000");
         }

         pos += VarInt.size(rawTextLen);
         pos += rawTextLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading RawText");
         }
      }

      if ((nullBits & 2) != 0) {
         v = buffer.getIntLE(offset + 10);
         if (v < 0 || v > buffer.writerIndex() - offset - 38) {
            return ValidationResult.error("Invalid offset for MessageId");
         }

         int pos = offset + 38 + v;
         int messageIdLen = VarInt.peek(buffer, pos);
         if (messageIdLen < 0) {
            return ValidationResult.error("Invalid string length for MessageId");
         }

         if (messageIdLen > 4096000) {
            return ValidationResult.error("MessageId exceeds max length 4096000");
         }

         pos += VarInt.size(messageIdLen);
         pos += messageIdLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading MessageId");
         }
      }

      if ((nullBits & 4) != 0) {
         v = buffer.getIntLE(offset + 14);
         if (v < 0 || v > buffer.writerIndex() - offset - 38) {
            return ValidationResult.error("Invalid offset for Children");
         }

         int pos = offset + 38 + v;
         int childrenCount = VarInt.peek(buffer, pos);
         if (childrenCount < 0) {
            return ValidationResult.error("Invalid array count for Children");
         }

         if (childrenCount > 4096000) {
            return ValidationResult.error("Children exceeds max length 4096000");
         }

         pos += VarInt.size(childrenCount);

         for (int i = 0; i < childrenCount; i++) {
            ValidationResult structResult = validateStructure(buffer, pos);
            if (!structResult.isValid()) {
               return ValidationResult.error("Invalid FormattedMessage in Children[" + i + "]: " + structResult.error());
            }

            pos += computeBytesConsumed(buffer, pos);
         }
      }

      if ((nullBits & 8) != 0) {
         v = buffer.getIntLE(offset + 18);
         if (v < 0 || v > buffer.writerIndex() - offset - 38) {
            return ValidationResult.error("Invalid offset for Params");
         }

         int pos = offset + 38 + v;
         int paramsCount = VarInt.peek(buffer, pos);
         if (paramsCount < 0) {
            return ValidationResult.error("Invalid dictionary count for Params");
         }

         if (paramsCount > 4096000) {
            return ValidationResult.error("Params exceeds max length 4096000");
         }

         pos += VarInt.size(paramsCount);

         for (int i = 0; i < paramsCount; i++) {
            int keyLen = VarInt.peek(buffer, pos);
            if (keyLen < 0) {
               return ValidationResult.error("Invalid string length for key");
            }

            if (keyLen > 4096000) {
               return ValidationResult.error("key exceeds max length 4096000");
            }

            pos += VarInt.size(keyLen);
            pos += keyLen;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading key");
            }

            pos += ParamValue.computeBytesConsumed(buffer, pos);
         }
      }

      if ((nullBits & 16) != 0) {
         v = buffer.getIntLE(offset + 22);
         if (v < 0 || v > buffer.writerIndex() - offset - 38) {
            return ValidationResult.error("Invalid offset for MessageParams");
         }

         int pos = offset + 38 + v;
         int messageParamsCount = VarInt.peek(buffer, pos);
         if (messageParamsCount < 0) {
            return ValidationResult.error("Invalid dictionary count for MessageParams");
         }

         if (messageParamsCount > 4096000) {
            return ValidationResult.error("MessageParams exceeds max length 4096000");
         }

         pos += VarInt.size(messageParamsCount);

         for (int i = 0; i < messageParamsCount; i++) {
            int keyLen = VarInt.peek(buffer, pos);
            if (keyLen < 0) {
               return ValidationResult.error("Invalid string length for key");
            }

            if (keyLen > 4096000) {
               return ValidationResult.error("key exceeds max length 4096000");
            }

            pos += VarInt.size(keyLen);
            pos += keyLen;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading key");
            }

            pos += computeBytesConsumed(buffer, pos);
         }
      }

      if ((nullBits & 32) != 0) {
         v = buffer.getIntLE(offset + 26);
         if (v < 0 || v > buffer.writerIndex() - offset - 38) {
            return ValidationResult.error("Invalid offset for Color");
         }

         int pos = offset + 38 + v;
         int colorLen = VarInt.peek(buffer, pos);
         if (colorLen < 0) {
            return ValidationResult.error("Invalid string length for Color");
         }

         if (colorLen > 4096000) {
            return ValidationResult.error("Color exceeds max length 4096000");
         }

         pos += VarInt.size(colorLen);
         pos += colorLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Color");
         }
      }

      if ((nullBits & 64) != 0) {
         v = buffer.getIntLE(offset + 30);
         if (v < 0 || v > buffer.writerIndex() - offset - 38) {
            return ValidationResult.error("Invalid offset for Link");
         }

         int pos = offset + 38 + v;
         int linkLen = VarInt.peek(buffer, pos);
         if (linkLen < 0) {
            return ValidationResult.error("Invalid string length for Link");
         }

         if (linkLen > 4096000) {
            return ValidationResult.error("Link exceeds max length 4096000");
         }

         pos += VarInt.size(linkLen);
         pos += linkLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Link");
         }
      }

      if ((nullBits & 128) != 0) {
         v = buffer.getIntLE(offset + 34);
         if (v < 0 || v > buffer.writerIndex() - offset - 38) {
            return ValidationResult.error("Invalid offset for Image");
         }

         int pos = offset + 38 + v;
         ValidationResult imageResult = FormattedMessageImage.validateStructure(buffer, pos);
         if (!imageResult.isValid()) {
            return ValidationResult.error("Invalid Image: " + imageResult.error());
         }

         pos += FormattedMessageImage.computeBytesConsumed(buffer, pos);
      }

      return ValidationResult.OK;
   }

   public FormattedMessage clone() {
      FormattedMessage copy = new FormattedMessage();
      copy.rawText = this.rawText;
      copy.messageId = this.messageId;
      copy.children = this.children != null ? Arrays.stream(this.children).map(ex -> ex.clone()).toArray(FormattedMessage[]::new) : null;
      copy.params = this.params != null ? new HashMap<>(this.params) : null;
      if (this.messageParams != null) {
         Map<String, FormattedMessage> m = new HashMap<>();

         for (Entry<String, FormattedMessage> e : this.messageParams.entrySet()) {
            m.put(e.getKey(), e.getValue().clone());
         }

         copy.messageParams = m;
      }

      copy.color = this.color;
      copy.bold = this.bold;
      copy.italic = this.italic;
      copy.monospace = this.monospace;
      copy.underlined = this.underlined;
      copy.link = this.link;
      copy.markupEnabled = this.markupEnabled;
      copy.image = this.image != null ? this.image.clone() : null;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof FormattedMessage other)
            ? false
            : Objects.equals(this.rawText, other.rawText)
               && Objects.equals(this.messageId, other.messageId)
               && Arrays.equals(this.children, other.children)
               && Objects.equals(this.params, other.params)
               && Objects.equals(this.messageParams, other.messageParams)
               && Objects.equals(this.color, other.color)
               && Objects.equals(this.bold, other.bold)
               && Objects.equals(this.italic, other.italic)
               && Objects.equals(this.monospace, other.monospace)
               && Objects.equals(this.underlined, other.underlined)
               && Objects.equals(this.link, other.link)
               && this.markupEnabled == other.markupEnabled
               && Objects.equals(this.image, other.image);
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      result = 31 * result + Objects.hashCode(this.rawText);
      result = 31 * result + Objects.hashCode(this.messageId);
      result = 31 * result + Arrays.hashCode(this.children);
      result = 31 * result + Objects.hashCode(this.params);
      result = 31 * result + Objects.hashCode(this.messageParams);
      result = 31 * result + Objects.hashCode(this.color);
      result = 31 * result + Objects.hashCode(this.bold);
      result = 31 * result + Objects.hashCode(this.italic);
      result = 31 * result + Objects.hashCode(this.monospace);
      result = 31 * result + Objects.hashCode(this.underlined);
      result = 31 * result + Objects.hashCode(this.link);
      result = 31 * result + Boolean.hashCode(this.markupEnabled);
      return 31 * result + Objects.hashCode(this.image);
   }
}
