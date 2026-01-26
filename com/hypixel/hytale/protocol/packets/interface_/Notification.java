/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.interface_;

import com.hypixel.hytale.protocol.FormattedMessage;
import com.hypixel.hytale.protocol.ItemWithAllMetadata;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import com.hypixel.hytale.protocol.packets.interface_.NotificationStyle;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Notification
implements Packet {
    public static final int PACKET_ID = 212;
    public static final boolean IS_COMPRESSED = false;
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 2;
    public static final int VARIABLE_FIELD_COUNT = 4;
    public static final int VARIABLE_BLOCK_START = 18;
    public static final int MAX_SIZE = 0x64000000;
    @Nullable
    public FormattedMessage message;
    @Nullable
    public FormattedMessage secondaryMessage;
    @Nullable
    public String icon;
    @Nullable
    public ItemWithAllMetadata item;
    @Nonnull
    public NotificationStyle style = NotificationStyle.Default;

    @Override
    public int getId() {
        return 212;
    }

    public Notification() {
    }

    public Notification(@Nullable FormattedMessage message, @Nullable FormattedMessage secondaryMessage, @Nullable String icon, @Nullable ItemWithAllMetadata item, @Nonnull NotificationStyle style) {
        this.message = message;
        this.secondaryMessage = secondaryMessage;
        this.icon = icon;
        this.item = item;
        this.style = style;
    }

    public Notification(@Nonnull Notification other) {
        this.message = other.message;
        this.secondaryMessage = other.secondaryMessage;
        this.icon = other.icon;
        this.item = other.item;
        this.style = other.style;
    }

    @Nonnull
    public static Notification deserialize(@Nonnull ByteBuf buf, int offset) {
        Notification obj = new Notification();
        byte nullBits = buf.getByte(offset);
        obj.style = NotificationStyle.fromValue(buf.getByte(offset + 1));
        if ((nullBits & 1) != 0) {
            int varPos0 = offset + 18 + buf.getIntLE(offset + 2);
            obj.message = FormattedMessage.deserialize(buf, varPos0);
        }
        if ((nullBits & 2) != 0) {
            int varPos1 = offset + 18 + buf.getIntLE(offset + 6);
            obj.secondaryMessage = FormattedMessage.deserialize(buf, varPos1);
        }
        if ((nullBits & 4) != 0) {
            int varPos2 = offset + 18 + buf.getIntLE(offset + 10);
            int iconLen = VarInt.peek(buf, varPos2);
            if (iconLen < 0) {
                throw ProtocolException.negativeLength("Icon", iconLen);
            }
            if (iconLen > 4096000) {
                throw ProtocolException.stringTooLong("Icon", iconLen, 4096000);
            }
            obj.icon = PacketIO.readVarString(buf, varPos2, PacketIO.UTF8);
        }
        if ((nullBits & 8) != 0) {
            int varPos3 = offset + 18 + buf.getIntLE(offset + 14);
            obj.item = ItemWithAllMetadata.deserialize(buf, varPos3);
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        byte nullBits = buf.getByte(offset);
        int maxEnd = 18;
        if ((nullBits & 1) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 2);
            int pos0 = offset + 18 + fieldOffset0;
            if ((pos0 += FormattedMessage.computeBytesConsumed(buf, pos0)) - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 2) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 6);
            int pos1 = offset + 18 + fieldOffset1;
            if ((pos1 += FormattedMessage.computeBytesConsumed(buf, pos1)) - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        if ((nullBits & 4) != 0) {
            int fieldOffset2 = buf.getIntLE(offset + 10);
            int pos2 = offset + 18 + fieldOffset2;
            int sl = VarInt.peek(buf, pos2);
            if ((pos2 += VarInt.length(buf, pos2) + sl) - offset > maxEnd) {
                maxEnd = pos2 - offset;
            }
        }
        if ((nullBits & 8) != 0) {
            int fieldOffset3 = buf.getIntLE(offset + 14);
            int pos3 = offset + 18 + fieldOffset3;
            if ((pos3 += ItemWithAllMetadata.computeBytesConsumed(buf, pos3)) - offset > maxEnd) {
                maxEnd = pos3 - offset;
            }
        }
        return maxEnd;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        byte nullBits = 0;
        if (this.message != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.secondaryMessage != null) {
            nullBits = (byte)(nullBits | 2);
        }
        if (this.icon != null) {
            nullBits = (byte)(nullBits | 4);
        }
        if (this.item != null) {
            nullBits = (byte)(nullBits | 8);
        }
        buf.writeByte(nullBits);
        buf.writeByte(this.style.getValue());
        int messageOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int secondaryMessageOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int iconOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int itemOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.message != null) {
            buf.setIntLE(messageOffsetSlot, buf.writerIndex() - varBlockStart);
            this.message.serialize(buf);
        } else {
            buf.setIntLE(messageOffsetSlot, -1);
        }
        if (this.secondaryMessage != null) {
            buf.setIntLE(secondaryMessageOffsetSlot, buf.writerIndex() - varBlockStart);
            this.secondaryMessage.serialize(buf);
        } else {
            buf.setIntLE(secondaryMessageOffsetSlot, -1);
        }
        if (this.icon != null) {
            buf.setIntLE(iconOffsetSlot, buf.writerIndex() - varBlockStart);
            PacketIO.writeVarString(buf, this.icon, 4096000);
        } else {
            buf.setIntLE(iconOffsetSlot, -1);
        }
        if (this.item != null) {
            buf.setIntLE(itemOffsetSlot, buf.writerIndex() - varBlockStart);
            this.item.serialize(buf);
        } else {
            buf.setIntLE(itemOffsetSlot, -1);
        }
    }

    @Override
    public int computeSize() {
        int size = 18;
        if (this.message != null) {
            size += this.message.computeSize();
        }
        if (this.secondaryMessage != null) {
            size += this.secondaryMessage.computeSize();
        }
        if (this.icon != null) {
            size += PacketIO.stringSize(this.icon);
        }
        if (this.item != null) {
            size += this.item.computeSize();
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int pos;
        if (buffer.readableBytes() - offset < 18) {
            return ValidationResult.error("Buffer too small: expected at least 18 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 1) != 0) {
            int messageOffset = buffer.getIntLE(offset + 2);
            if (messageOffset < 0) {
                return ValidationResult.error("Invalid offset for Message");
            }
            pos = offset + 18 + messageOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Message");
            }
            ValidationResult messageResult = FormattedMessage.validateStructure(buffer, pos);
            if (!messageResult.isValid()) {
                return ValidationResult.error("Invalid Message: " + messageResult.error());
            }
            pos += FormattedMessage.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits & 2) != 0) {
            int secondaryMessageOffset = buffer.getIntLE(offset + 6);
            if (secondaryMessageOffset < 0) {
                return ValidationResult.error("Invalid offset for SecondaryMessage");
            }
            pos = offset + 18 + secondaryMessageOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for SecondaryMessage");
            }
            ValidationResult secondaryMessageResult = FormattedMessage.validateStructure(buffer, pos);
            if (!secondaryMessageResult.isValid()) {
                return ValidationResult.error("Invalid SecondaryMessage: " + secondaryMessageResult.error());
            }
            pos += FormattedMessage.computeBytesConsumed(buffer, pos);
        }
        if ((nullBits & 4) != 0) {
            int iconOffset = buffer.getIntLE(offset + 10);
            if (iconOffset < 0) {
                return ValidationResult.error("Invalid offset for Icon");
            }
            pos = offset + 18 + iconOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Icon");
            }
            int iconLen = VarInt.peek(buffer, pos);
            if (iconLen < 0) {
                return ValidationResult.error("Invalid string length for Icon");
            }
            if (iconLen > 4096000) {
                return ValidationResult.error("Icon exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            if ((pos += iconLen) > buffer.writerIndex()) {
                return ValidationResult.error("Buffer overflow reading Icon");
            }
        }
        if ((nullBits & 8) != 0) {
            int itemOffset = buffer.getIntLE(offset + 14);
            if (itemOffset < 0) {
                return ValidationResult.error("Invalid offset for Item");
            }
            pos = offset + 18 + itemOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Item");
            }
            ValidationResult itemResult = ItemWithAllMetadata.validateStructure(buffer, pos);
            if (!itemResult.isValid()) {
                return ValidationResult.error("Invalid Item: " + itemResult.error());
            }
            pos += ItemWithAllMetadata.computeBytesConsumed(buffer, pos);
        }
        return ValidationResult.OK;
    }

    public Notification clone() {
        Notification copy = new Notification();
        copy.message = this.message != null ? this.message.clone() : null;
        copy.secondaryMessage = this.secondaryMessage != null ? this.secondaryMessage.clone() : null;
        copy.icon = this.icon;
        copy.item = this.item != null ? this.item.clone() : null;
        copy.style = this.style;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Notification)) {
            return false;
        }
        Notification other = (Notification)obj;
        return Objects.equals(this.message, other.message) && Objects.equals(this.secondaryMessage, other.secondaryMessage) && Objects.equals(this.icon, other.icon) && Objects.equals(this.item, other.item) && Objects.equals((Object)this.style, (Object)other.style);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.message, this.secondaryMessage, this.icon, this.item, this.style});
    }
}

