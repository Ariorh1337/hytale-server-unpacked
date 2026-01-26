/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.MaybeBool;
import com.hypixel.hytale.protocol.ParamValue;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FormattedMessage {
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 6;
    public static final int VARIABLE_FIELD_COUNT = 7;
    public static final int VARIABLE_BLOCK_START = 34;
    public static final int MAX_SIZE = 0x64000000;
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

    public FormattedMessage() {
    }

    public FormattedMessage(@Nullable String rawText, @Nullable String messageId, @Nullable FormattedMessage[] children, @Nullable Map<String, ParamValue> params, @Nullable Map<String, FormattedMessage> messageParams, @Nullable String color, @Nonnull MaybeBool bold, @Nonnull MaybeBool italic, @Nonnull MaybeBool monospace, @Nonnull MaybeBool underlined, @Nullable String link, boolean markupEnabled) {
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
    }

    @Nonnull
    public static FormattedMessage deserialize(@Nonnull ByteBuf buf, int offset) {
        Object val;
        String key;
        int keyVarLen;
        int keyLen;
        int dictPos;
        int i;
        int varIntLen;
        FormattedMessage obj = new FormattedMessage();
        byte nullBits = buf.getByte(offset);
        obj.bold = MaybeBool.fromValue(buf.getByte(offset + 1));
        obj.italic = MaybeBool.fromValue(buf.getByte(offset + 2));
        obj.monospace = MaybeBool.fromValue(buf.getByte(offset + 3));
        obj.underlined = MaybeBool.fromValue(buf.getByte(offset + 4));
        boolean bl = obj.markupEnabled = buf.getByte(offset + 5) != 0;
        if ((nullBits & 1) != 0) {
            int varPos0 = offset + 34 + buf.getIntLE(offset + 6);
            int rawTextLen = VarInt.peek(buf, varPos0);
            if (rawTextLen < 0) {
                throw ProtocolException.negativeLength("RawText", rawTextLen);
            }
            if (rawTextLen > 4096000) {
                throw ProtocolException.stringTooLong("RawText", rawTextLen, 4096000);
            }
            obj.rawText = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
        }
        if ((nullBits & 2) != 0) {
            int varPos1 = offset + 34 + buf.getIntLE(offset + 10);
            int messageIdLen = VarInt.peek(buf, varPos1);
            if (messageIdLen < 0) {
                throw ProtocolException.negativeLength("MessageId", messageIdLen);
            }
            if (messageIdLen > 4096000) {
                throw ProtocolException.stringTooLong("MessageId", messageIdLen, 4096000);
            }
            obj.messageId = PacketIO.readVarString(buf, varPos1, PacketIO.UTF8);
        }
        if ((nullBits & 4) != 0) {
            int varPos2 = offset + 34 + buf.getIntLE(offset + 14);
            int childrenCount = VarInt.peek(buf, varPos2);
            if (childrenCount < 0) {
                throw ProtocolException.negativeLength("Children", childrenCount);
            }
            if (childrenCount > 4096000) {
                throw ProtocolException.arrayTooLong("Children", childrenCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos2);
            if ((long)(varPos2 + varIntLen) + (long)childrenCount * 6L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("Children", varPos2 + varIntLen + childrenCount * 6, buf.readableBytes());
            }
            obj.children = new FormattedMessage[childrenCount];
            int elemPos = varPos2 + varIntLen;
            for (i = 0; i < childrenCount; ++i) {
                obj.children[i] = FormattedMessage.deserialize(buf, elemPos);
                elemPos += FormattedMessage.computeBytesConsumed(buf, elemPos);
            }
        }
        if ((nullBits & 8) != 0) {
            int varPos3 = offset + 34 + buf.getIntLE(offset + 18);
            int paramsCount = VarInt.peek(buf, varPos3);
            if (paramsCount < 0) {
                throw ProtocolException.negativeLength("Params", paramsCount);
            }
            if (paramsCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Params", paramsCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos3);
            obj.params = new HashMap<String, ParamValue>(paramsCount);
            dictPos = varPos3 + varIntLen;
            for (i = 0; i < paramsCount; ++i) {
                keyLen = VarInt.peek(buf, dictPos);
                if (keyLen < 0) {
                    throw ProtocolException.negativeLength("key", keyLen);
                }
                if (keyLen > 4096000) {
                    throw ProtocolException.stringTooLong("key", keyLen, 4096000);
                }
                keyVarLen = VarInt.length(buf, dictPos);
                key = PacketIO.readVarString(buf, dictPos);
                val = ParamValue.deserialize(buf, dictPos += keyVarLen + keyLen);
                dictPos += ParamValue.computeBytesConsumed(buf, dictPos);
                if (obj.params.put(key, (ParamValue)val) == null) continue;
                throw ProtocolException.duplicateKey("params", key);
            }
        }
        if ((nullBits & 0x10) != 0) {
            int varPos4 = offset + 34 + buf.getIntLE(offset + 22);
            int messageParamsCount = VarInt.peek(buf, varPos4);
            if (messageParamsCount < 0) {
                throw ProtocolException.negativeLength("MessageParams", messageParamsCount);
            }
            if (messageParamsCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("MessageParams", messageParamsCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos4);
            obj.messageParams = new HashMap<String, FormattedMessage>(messageParamsCount);
            dictPos = varPos4 + varIntLen;
            for (i = 0; i < messageParamsCount; ++i) {
                keyLen = VarInt.peek(buf, dictPos);
                if (keyLen < 0) {
                    throw ProtocolException.negativeLength("key", keyLen);
                }
                if (keyLen > 4096000) {
                    throw ProtocolException.stringTooLong("key", keyLen, 4096000);
                }
                keyVarLen = VarInt.length(buf, dictPos);
                key = PacketIO.readVarString(buf, dictPos);
                val = FormattedMessage.deserialize(buf, dictPos += keyVarLen + keyLen);
                dictPos += FormattedMessage.computeBytesConsumed(buf, dictPos);
                if (obj.messageParams.put(key, (FormattedMessage)val) == null) continue;
                throw ProtocolException.duplicateKey("messageParams", key);
            }
        }
        if ((nullBits & 0x20) != 0) {
            int varPos5 = offset + 34 + buf.getIntLE(offset + 26);
            int colorLen = VarInt.peek(buf, varPos5);
            if (colorLen < 0) {
                throw ProtocolException.negativeLength("Color", colorLen);
            }
            if (colorLen > 4096000) {
                throw ProtocolException.stringTooLong("Color", colorLen, 4096000);
            }
            obj.color = PacketIO.readVarString(buf, varPos5, PacketIO.UTF8);
        }
        if ((nullBits & 0x40) != 0) {
            int varPos6 = offset + 34 + buf.getIntLE(offset + 30);
            int linkLen = VarInt.peek(buf, varPos6);
            if (linkLen < 0) {
                throw ProtocolException.negativeLength("Link", linkLen);
            }
            if (linkLen > 4096000) {
                throw ProtocolException.stringTooLong("Link", linkLen, 4096000);
            }
            obj.link = PacketIO.readVarString(buf, varPos6, PacketIO.UTF8);
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int sl;
        int dictLen;
        int i;
        int sl2;
        byte nullBits = buf.getByte(offset);
        int maxEnd = 34;
        if ((nullBits & 1) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 6);
            int pos0 = offset + 34 + fieldOffset0;
            sl2 = VarInt.peek(buf, pos0);
            if ((pos0 += VarInt.length(buf, pos0) + sl2) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 2) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 10);
            int pos1 = offset + 34 + fieldOffset1;
            sl2 = VarInt.peek(buf, pos1);
            if ((pos1 += VarInt.length(buf, pos1) + sl2) - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        if ((nullBits & 4) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 14);
            int pos2 = offset + 34 + fieldOffset2;
            int arrLen = VarInt.peek(buf, pos2);
            pos2 += VarInt.length(buf, pos2);
            for (i = 0; i < arrLen; ++i) {
                pos2 += FormattedMessage.computeBytesConsumed(buf, pos2);
            }
            if (pos2 - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        if ((nullBits & 8) != 0) {
            int fieldOffset3 = buf.getIntLE(offset + 18);
            int pos3 = offset + 34 + fieldOffset3;
            dictLen = VarInt.peek(buf, pos3);
            pos3 += VarInt.length(buf, pos3);
            for (i = 0; i < dictLen; ++i) {
                sl = VarInt.peek(buf, pos3);
                pos3 += VarInt.length(buf, pos3) + sl;
                pos3 += ParamValue.computeBytesConsumed(buf, pos3);
            }
            if (pos3 - offset > maxEnd) {
                maxEnd = pos3 - offset;
            }
        }
        if ((nullBits & 0x10) != 0) {
            int fieldOffset4 = buf.getIntLE(offset + 22);
            int pos4 = offset + 34 + fieldOffset4;
            dictLen = VarInt.peek(buf, pos4);
            pos4 += VarInt.length(buf, pos4);
            for (i = 0; i < dictLen; ++i) {
                sl = VarInt.peek(buf, pos4);
                pos4 += VarInt.length(buf, pos4) + sl;
                pos4 += FormattedMessage.computeBytesConsumed(buf, pos4);
            }
            if (pos4 - offset > maxEnd) {
                maxEnd = pos4 - offset;
            }
        }
        if ((nullBits & 0x20) != 0) {
            int fieldOffset5 = buf.getIntLE(offset + 26);
            int pos5 = offset + 34 + fieldOffset5;
            sl2 = VarInt.peek(buf, pos5);
            if ((pos5 += VarInt.length(buf, pos5) + sl2) - offset > maxEnd) {
                maxEnd = pos5 - offset;
            }
        }
        if ((nullBits & 0x40) != 0) {
            int fieldOffset6 = buf.getIntLE(offset + 30);
            int pos6 = offset + 34 + fieldOffset6;
            sl2 = VarInt.peek(buf, pos6);
            if ((pos6 += VarInt.length(buf, pos6) + sl2) - offset > maxEnd) {
                maxEnd = pos6 - offset;
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
            nullBits = (byte)(nullBits | 0x10);
        }
        if (this.color != null) {
            nullBits = (byte)(nullBits | 0x20);
        }
        if (this.link != null) {
            nullBits = (byte)(nullBits | 0x40);
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
            for (Map.Entry entry : this.params.entrySet()) {
                PacketIO.writeVarString(buf, (String)entry.getKey(), 4096000);
                ((ParamValue)entry.getValue()).serializeWithTypeId(buf);
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
            for (Map.Entry entry : this.messageParams.entrySet()) {
                PacketIO.writeVarString(buf, (String)entry.getKey(), 4096000);
                ((FormattedMessage)entry.getValue()).serialize(buf);
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
    }

    public int computeSize() {
        int size = 34;
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
            for (Map.Entry entry : this.params.entrySet()) {
                paramsSize += PacketIO.stringSize((String)entry.getKey()) + ((ParamValue)entry.getValue()).computeSizeWithTypeId();
            }
            size += VarInt.size(this.params.size()) + paramsSize;
        }
        if (this.messageParams != null) {
            int messageParamsSize = 0;
            for (Map.Entry entry : this.messageParams.entrySet()) {
                messageParamsSize += PacketIO.stringSize((String)entry.getKey()) + ((FormattedMessage)entry.getValue()).computeSize();
            }
            size += VarInt.size(this.messageParams.size()) + messageParamsSize;
        }
        if (this.color != null) {
            size += PacketIO.stringSize(this.color);
        }
        if (this.link != null) {
            size += PacketIO.stringSize(this.link);
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int i;
        int pos;
        if (buffer.readableBytes() - offset < 34) {
            return ValidationResult.error("Buffer too small: expected at least 34 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 1) != 0) {
            int rawTextOffset = buffer.getIntLE(offset + 6);
            if (rawTextOffset < 0) {
                return ValidationResult.error("Invalid offset for RawText");
            }
            pos = offset + 34 + rawTextOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for RawText");
            }
            int rawTextLen = VarInt.peek(buffer, pos);
            if (rawTextLen < 0) {
                return ValidationResult.error("Invalid string length for RawText");
            }
            if (rawTextLen > 4096000) {
                return ValidationResult.error("RawText exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += rawTextLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading RawText");
            }
        }
        if ((nullBits & 2) != 0) {
            int messageIdOffset = buffer.getIntLE(offset + 10);
            if (messageIdOffset < 0) {
                return ValidationResult.error("Invalid offset for MessageId");
            }
            pos = offset + 34 + messageIdOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for MessageId");
            }
            int messageIdLen = VarInt.peek(buffer, pos);
            if (messageIdLen < 0) {
                return ValidationResult.error("Invalid string length for MessageId");
            }
            if (messageIdLen > 4096000) {
                return ValidationResult.error("MessageId exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += messageIdLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading MessageId");
            }
        }
        if ((nullBits & 4) != 0) {
            int childrenOffset = buffer.getIntLE(offset + 14);
            if (childrenOffset < 0) {
                return ValidationResult.error("Invalid offset for Children");
            }
            pos = offset + 34 + childrenOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Children");
            }
            int childrenCount = VarInt.peek(buffer, pos);
            if (childrenCount < 0) {
                return ValidationResult.error("Invalid array count for Children");
            }
            if (childrenCount > 4096000) {
                return ValidationResult.error("Children exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < childrenCount; ++i) {
                ValidationResult structResult = FormattedMessage.validateStructure(buffer, pos);
                if (!structResult.isValid()) {
                    return ValidationResult.error("Invalid FormattedMessage in Children[" + i + "]: " + structResult.error());
                }
                pos += FormattedMessage.computeBytesConsumed(buffer, pos);
            }
        }
        if ((nullBits & 8) != 0) {
            int paramsOffset = buffer.getIntLE(offset + 18);
            if (paramsOffset < 0) {
                return ValidationResult.error("Invalid offset for Params");
            }
            pos = offset + 34 + paramsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Params");
            }
            int paramsCount = VarInt.peek(buffer, pos);
            if (paramsCount < 0) {
                return ValidationResult.error("Invalid dictionary count for Params");
            }
            if (paramsCount > 4096000) {
                return ValidationResult.error("Params exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < paramsCount; ++i) {
                int keyLen = VarInt.peek(buffer, pos);
                if (keyLen < 0) {
                    return ValidationResult.error("Invalid string length for key");
                }
                if (keyLen > 4096000) {
                    return ValidationResult.error("key exceeds max length 4096000");
                }
                pos += VarInt.length(buffer, pos);
                if ((pos += keyLen) > buffer.writerIndex()) {
                    return ValidationResult.error("Buffer overflow reading key");
                }
                pos += ParamValue.computeBytesConsumed(buffer, pos);
            }
        }
        if ((nullBits & 0x10) != 0) {
            int messageParamsOffset = buffer.getIntLE(offset + 22);
            if (messageParamsOffset < 0) {
                return ValidationResult.error("Invalid offset for MessageParams");
            }
            pos = offset + 34 + messageParamsOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for MessageParams");
            }
            int messageParamsCount = VarInt.peek(buffer, pos);
            if (messageParamsCount < 0) {
                return ValidationResult.error("Invalid dictionary count for MessageParams");
            }
            if (messageParamsCount > 4096000) {
                return ValidationResult.error("MessageParams exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < messageParamsCount; ++i) {
                int keyLen = VarInt.peek(buffer, pos);
                if (keyLen < 0) {
                    return ValidationResult.error("Invalid string length for key");
                }
                if (keyLen > 4096000) {
                    return ValidationResult.error("key exceeds max length 4096000");
                }
                pos += VarInt.length(buffer, pos);
                if ((pos += keyLen) > buffer.writerIndex()) {
                    return ValidationResult.error("Buffer overflow reading key");
                }
                pos += FormattedMessage.computeBytesConsumed(buffer, pos);
            }
        }
        if ((nullBits & 0x20) != 0) {
            int colorOffset = buffer.getIntLE(offset + 26);
            if (colorOffset < 0) {
                return ValidationResult.error("Invalid offset for Color");
            }
            pos = offset + 34 + colorOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Color");
            }
            int colorLen = VarInt.peek(buffer, pos);
            if (colorLen < 0) {
                return ValidationResult.error("Invalid string length for Color");
            }
            if (colorLen > 4096000) {
                return ValidationResult.error("Color exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += colorLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading Color");
            }
        }
        if ((nullBits & 0x40) != 0) {
            int linkOffset = buffer.getIntLE(offset + 30);
            if (linkOffset < 0) {
                return ValidationResult.error("Invalid offset for Link");
            }
            pos = offset + 34 + linkOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Link");
            }
            int linkLen = VarInt.peek(buffer, pos);
            if (linkLen < 0) {
                return ValidationResult.error("Invalid string length for Link");
            }
            if (linkLen > 4096000) {
                return ValidationResult.error("Link exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += linkLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading Link");
            }
        }
        return ValidationResult.OK;
    }

    public FormattedMessage clone() {
        FormattedMessage copy = new FormattedMessage();
        copy.rawText = this.rawText;
        copy.messageId = this.messageId;
        copy.children = this.children != null ? (FormattedMessage[])Arrays.stream(this.children).map(e -> e.clone()).toArray(FormattedMessage[]::new) : null;
        HashMap<String, ParamValue> hashMap = copy.params = this.params != null ? new HashMap<String, ParamValue>(this.params) : null;
        if (this.messageParams != null) {
            HashMap<String, FormattedMessage> m = new HashMap<String, FormattedMessage>();
            for (Map.Entry<String, FormattedMessage> e2 : this.messageParams.entrySet()) {
                m.put(e2.getKey(), e2.getValue().clone());
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
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FormattedMessage)) {
            return false;
        }
        FormattedMessage other = (FormattedMessage)obj;
        return Objects.equals(this.rawText, other.rawText) && Objects.equals(this.messageId, other.messageId) && Arrays.equals(this.children, other.children) && Objects.equals(this.params, other.params) && Objects.equals(this.messageParams, other.messageParams) && Objects.equals(this.color, other.color) && Objects.equals((Object)this.bold, (Object)other.bold) && Objects.equals((Object)this.italic, (Object)other.italic) && Objects.equals((Object)this.monospace, (Object)other.monospace) && Objects.equals((Object)this.underlined, (Object)other.underlined) && Objects.equals(this.link, other.link) && this.markupEnabled == other.markupEnabled;
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + Objects.hashCode(this.rawText);
        result = 31 * result + Objects.hashCode(this.messageId);
        result = 31 * result + Arrays.hashCode(this.children);
        result = 31 * result + Objects.hashCode(this.params);
        result = 31 * result + Objects.hashCode(this.messageParams);
        result = 31 * result + Objects.hashCode(this.color);
        result = 31 * result + Objects.hashCode((Object)this.bold);
        result = 31 * result + Objects.hashCode((Object)this.italic);
        result = 31 * result + Objects.hashCode((Object)this.monospace);
        result = 31 * result + Objects.hashCode((Object)this.underlined);
        result = 31 * result + Objects.hashCode(this.link);
        result = 31 * result + Boolean.hashCode(this.markupEnabled);
        return result;
    }
}

