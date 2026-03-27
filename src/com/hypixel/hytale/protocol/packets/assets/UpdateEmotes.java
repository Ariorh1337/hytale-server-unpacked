package com.hypixel.hytale.protocol.packets.assets;

import com.hypixel.hytale.protocol.NetworkChannel;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.ProtocolEmote;
import com.hypixel.hytale.protocol.ToClientPacket;
import com.hypixel.hytale.protocol.UpdateType;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UpdateEmotes implements Packet, ToClientPacket {
   public static final int PACKET_ID = 86;
   public static final boolean IS_COMPRESSED = true;
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 6;
   public static final int VARIABLE_FIELD_COUNT = 1;
   public static final int VARIABLE_BLOCK_START = 6;
   public static final int MAX_SIZE = 1677721600;
   @Nonnull
   public UpdateType type = UpdateType.Init;
   public int maxId;
   @Nullable
   public Map<Integer, ProtocolEmote> emotes;

   @Override
   public int getId() {
      return 86;
   }

   @Override
   public NetworkChannel getChannel() {
      return NetworkChannel.Default;
   }

   public UpdateEmotes() {
   }

   public UpdateEmotes(@Nonnull UpdateType type, int maxId, @Nullable Map<Integer, ProtocolEmote> emotes) {
      this.type = type;
      this.maxId = maxId;
      this.emotes = emotes;
   }

   public UpdateEmotes(@Nonnull UpdateEmotes other) {
      this.type = other.type;
      this.maxId = other.maxId;
      this.emotes = other.emotes;
   }

   @Nonnull
   public static UpdateEmotes deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 6) {
         throw ProtocolException.bufferTooSmall("UpdateEmotes", 6, buf.readableBytes() - offset);
      }

      UpdateEmotes obj = new UpdateEmotes();
      byte nullBits = buf.getByte(offset);
      obj.type = UpdateType.fromValue(buf.getByte(offset + 1));
      obj.maxId = buf.getIntLE(offset + 2);
      int pos = offset + 6;
      if ((nullBits & 1) != 0) {
         int emotesCount = VarInt.peek(buf, pos);
         if (emotesCount < 0) {
            throw ProtocolException.invalidVarInt("Emotes");
         }

         int emotesVarLen = VarInt.size(emotesCount);
         if (emotesCount > 4096000) {
            throw ProtocolException.dictionaryTooLarge("Emotes", emotesCount, 4096000);
         }

         pos += emotesVarLen;
         obj.emotes = new HashMap<>(emotesCount);

         for (int i = 0; i < emotesCount; i++) {
            int key = buf.getIntLE(pos);
            pos += 4;
            ProtocolEmote val = ProtocolEmote.deserialize(buf, pos);
            pos += ProtocolEmote.computeBytesConsumed(buf, pos);
            if (obj.emotes.put(key, val) != null) {
               throw ProtocolException.duplicateKey("emotes", key);
            }
         }
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int pos = offset + 6;
      if ((nullBits & 1) != 0) {
         int dictLen = VarInt.peek(buf, pos);
         pos += VarInt.size(dictLen);

         for (int i = 0; i < dictLen; i++) {
            pos += 4;
            pos += ProtocolEmote.computeBytesConsumed(buf, pos);
         }
      }

      return pos - offset;
   }

   @Override
   public void serialize(@Nonnull ByteBuf buf) {
      byte nullBits = 0;
      if (this.emotes != null) {
         nullBits = (byte)(nullBits | 1);
      }

      buf.writeByte(nullBits);
      buf.writeByte(this.type.getValue());
      buf.writeIntLE(this.maxId);
      if (this.emotes != null) {
         if (this.emotes.size() > 4096000) {
            throw ProtocolException.dictionaryTooLarge("Emotes", this.emotes.size(), 4096000);
         }

         VarInt.write(buf, this.emotes.size());

         for (Entry<Integer, ProtocolEmote> e : this.emotes.entrySet()) {
            buf.writeIntLE(e.getKey());
            e.getValue().serialize(buf);
         }
      }
   }

   @Override
   public int computeSize() {
      int size = 6;
      if (this.emotes != null) {
         int emotesSize = 0;

         for (Entry<Integer, ProtocolEmote> kvp : this.emotes.entrySet()) {
            emotesSize += 4 + kvp.getValue().computeSize();
         }

         size += VarInt.size(this.emotes.size()) + emotesSize;
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 6) {
         return ValidationResult.error("Buffer too small: expected at least 6 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      int v = buffer.getByte(offset + 1) & 255;
      if (v >= 3) {
         return ValidationResult.error("Invalid UpdateType value for Type");
      }

      v = offset + 6;
      if ((nullBits & 1) != 0) {
         int emotesCount = VarInt.peek(buffer, v);
         if (emotesCount < 0) {
            return ValidationResult.error("Invalid dictionary count for Emotes");
         }

         if (emotesCount > 4096000) {
            return ValidationResult.error("Emotes exceeds max length 4096000");
         }

         v += VarInt.size(emotesCount);

         for (int i = 0; i < emotesCount; i++) {
            v += 4;
            if (v > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading key");
            }

            v += ProtocolEmote.computeBytesConsumed(buffer, v);
         }
      }

      return ValidationResult.OK;
   }

   public UpdateEmotes clone() {
      UpdateEmotes copy = new UpdateEmotes();
      copy.type = this.type;
      copy.maxId = this.maxId;
      if (this.emotes != null) {
         Map<Integer, ProtocolEmote> m = new HashMap<>();

         for (Entry<Integer, ProtocolEmote> e : this.emotes.entrySet()) {
            m.put(e.getKey(), e.getValue().clone());
         }

         copy.emotes = m;
      }

      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof UpdateEmotes other)
            ? false
            : Objects.equals(this.type, other.type) && this.maxId == other.maxId && Objects.equals(this.emotes, other.emotes);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.type, this.maxId, this.emotes);
   }
}
