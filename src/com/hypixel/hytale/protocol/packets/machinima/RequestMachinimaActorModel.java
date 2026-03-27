package com.hypixel.hytale.protocol.packets.machinima;

import com.hypixel.hytale.protocol.NetworkChannel;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.ToServerPacket;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RequestMachinimaActorModel implements Packet, ToServerPacket {
   public static final int PACKET_ID = 260;
   public static final boolean IS_COMPRESSED = false;
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 1;
   public static final int VARIABLE_FIELD_COUNT = 3;
   public static final int VARIABLE_BLOCK_START = 13;
   public static final int MAX_SIZE = 49152028;
   @Nullable
   public String modelId;
   @Nullable
   public String sceneName;
   @Nullable
   public String actorName;

   @Override
   public int getId() {
      return 260;
   }

   @Override
   public NetworkChannel getChannel() {
      return NetworkChannel.Default;
   }

   public RequestMachinimaActorModel() {
   }

   public RequestMachinimaActorModel(@Nullable String modelId, @Nullable String sceneName, @Nullable String actorName) {
      this.modelId = modelId;
      this.sceneName = sceneName;
      this.actorName = actorName;
   }

   public RequestMachinimaActorModel(@Nonnull RequestMachinimaActorModel other) {
      this.modelId = other.modelId;
      this.sceneName = other.sceneName;
      this.actorName = other.actorName;
   }

   @Nonnull
   public static RequestMachinimaActorModel deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 13) {
         throw ProtocolException.bufferTooSmall("RequestMachinimaActorModel", 13, buf.readableBytes() - offset);
      }

      RequestMachinimaActorModel obj = new RequestMachinimaActorModel();
      byte nullBits = buf.getByte(offset);
      if ((nullBits & 1) != 0) {
         int varPosBase0 = buf.getIntLE(offset + 1);
         if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 13) {
            throw ProtocolException.invalidOffset("ModelId", varPosBase0, buf.readableBytes());
         }

         int varPos0 = offset + 13 + varPosBase0;
         int modelIdLen = VarInt.peek(buf, varPos0);
         if (modelIdLen < 0) {
            throw ProtocolException.invalidVarInt("ModelId");
         }

         int modelIdVarIntLen = VarInt.size(modelIdLen);
         if (modelIdLen > 4096000) {
            throw ProtocolException.stringTooLong("ModelId", modelIdLen, 4096000);
         }

         if (varPos0 + modelIdVarIntLen + modelIdLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("ModelId", varPos0 + modelIdVarIntLen + modelIdLen, buf.readableBytes());
         }

         obj.modelId = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
      }

      if ((nullBits & 2) != 0) {
         int varPosBase1 = buf.getIntLE(offset + 5);
         if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 13) {
            throw ProtocolException.invalidOffset("SceneName", varPosBase1, buf.readableBytes());
         }

         int varPos1 = offset + 13 + varPosBase1;
         int sceneNameLen = VarInt.peek(buf, varPos1);
         if (sceneNameLen < 0) {
            throw ProtocolException.invalidVarInt("SceneName");
         }

         int sceneNameVarIntLen = VarInt.size(sceneNameLen);
         if (sceneNameLen > 4096000) {
            throw ProtocolException.stringTooLong("SceneName", sceneNameLen, 4096000);
         }

         if (varPos1 + sceneNameVarIntLen + sceneNameLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("SceneName", varPos1 + sceneNameVarIntLen + sceneNameLen, buf.readableBytes());
         }

         obj.sceneName = PacketIO.readVarString(buf, varPos1, PacketIO.UTF8);
      }

      if ((nullBits & 4) != 0) {
         int varPosBase2 = buf.getIntLE(offset + 9);
         if (varPosBase2 < 0 || varPosBase2 > buf.writerIndex() - offset - 13) {
            throw ProtocolException.invalidOffset("ActorName", varPosBase2, buf.readableBytes());
         }

         int varPos2 = offset + 13 + varPosBase2;
         int actorNameLen = VarInt.peek(buf, varPos2);
         if (actorNameLen < 0) {
            throw ProtocolException.invalidVarInt("ActorName");
         }

         int actorNameVarIntLen = VarInt.size(actorNameLen);
         if (actorNameLen > 4096000) {
            throw ProtocolException.stringTooLong("ActorName", actorNameLen, 4096000);
         }

         if (varPos2 + actorNameVarIntLen + actorNameLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("ActorName", varPos2 + actorNameVarIntLen + actorNameLen, buf.readableBytes());
         }

         obj.actorName = PacketIO.readVarString(buf, varPos2, PacketIO.UTF8);
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 13;
      if ((nullBits & 1) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 1);
         if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 13) {
            throw ProtocolException.invalidOffset("ModelId", fieldOffset0, maxEnd);
         }

         int pos0 = offset + 13 + fieldOffset0;
         int sl = VarInt.peek(buf, pos0);
         pos0 += VarInt.size(sl) + sl;
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits & 2) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 5);
         if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 13) {
            throw ProtocolException.invalidOffset("SceneName", fieldOffset1, maxEnd);
         }

         int pos1 = offset + 13 + fieldOffset1;
         int sl = VarInt.peek(buf, pos1);
         pos1 += VarInt.size(sl) + sl;
         if (pos1 - offset > maxEnd) {
            maxEnd = pos1 - offset;
         }
      }

      if ((nullBits & 4) != 0) {
         int fieldOffset2 = buf.getIntLE(offset + 9);
         if (fieldOffset2 < 0 || fieldOffset2 > buf.writerIndex() - offset - 13) {
            throw ProtocolException.invalidOffset("ActorName", fieldOffset2, maxEnd);
         }

         int pos2 = offset + 13 + fieldOffset2;
         int sl = VarInt.peek(buf, pos2);
         pos2 += VarInt.size(sl) + sl;
         if (pos2 - offset > maxEnd) {
            maxEnd = pos2 - offset;
         }
      }

      return maxEnd;
   }

   @Override
   public void serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      byte nullBits = 0;
      if (this.modelId != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.sceneName != null) {
         nullBits = (byte)(nullBits | 2);
      }

      if (this.actorName != null) {
         nullBits = (byte)(nullBits | 4);
      }

      buf.writeByte(nullBits);
      int modelIdOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int sceneNameOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int actorNameOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.modelId != null) {
         buf.setIntLE(modelIdOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.modelId, 4096000);
      } else {
         buf.setIntLE(modelIdOffsetSlot, -1);
      }

      if (this.sceneName != null) {
         buf.setIntLE(sceneNameOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.sceneName, 4096000);
      } else {
         buf.setIntLE(sceneNameOffsetSlot, -1);
      }

      if (this.actorName != null) {
         buf.setIntLE(actorNameOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.actorName, 4096000);
      } else {
         buf.setIntLE(actorNameOffsetSlot, -1);
      }
   }

   @Override
   public int computeSize() {
      int size = 13;
      if (this.modelId != null) {
         size += PacketIO.stringSize(this.modelId);
      }

      if (this.sceneName != null) {
         size += PacketIO.stringSize(this.sceneName);
      }

      if (this.actorName != null) {
         size += PacketIO.stringSize(this.actorName);
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 13) {
         return ValidationResult.error("Buffer too small: expected at least 13 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      if ((nullBits & 1) != 0) {
         int modelIdOffset = buffer.getIntLE(offset + 1);
         if (modelIdOffset < 0 || modelIdOffset > buffer.writerIndex() - offset - 13) {
            return ValidationResult.error("Invalid offset for ModelId");
         }

         int pos = offset + 13 + modelIdOffset;
         int modelIdLen = VarInt.peek(buffer, pos);
         if (modelIdLen < 0) {
            return ValidationResult.error("Invalid string length for ModelId");
         }

         if (modelIdLen > 4096000) {
            return ValidationResult.error("ModelId exceeds max length 4096000");
         }

         pos += VarInt.size(modelIdLen);
         pos += modelIdLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading ModelId");
         }
      }

      if ((nullBits & 2) != 0) {
         int sceneNameOffset = buffer.getIntLE(offset + 5);
         if (sceneNameOffset < 0 || sceneNameOffset > buffer.writerIndex() - offset - 13) {
            return ValidationResult.error("Invalid offset for SceneName");
         }

         int pos = offset + 13 + sceneNameOffset;
         int sceneNameLen = VarInt.peek(buffer, pos);
         if (sceneNameLen < 0) {
            return ValidationResult.error("Invalid string length for SceneName");
         }

         if (sceneNameLen > 4096000) {
            return ValidationResult.error("SceneName exceeds max length 4096000");
         }

         pos += VarInt.size(sceneNameLen);
         pos += sceneNameLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading SceneName");
         }
      }

      if ((nullBits & 4) != 0) {
         int actorNameOffset = buffer.getIntLE(offset + 9);
         if (actorNameOffset < 0 || actorNameOffset > buffer.writerIndex() - offset - 13) {
            return ValidationResult.error("Invalid offset for ActorName");
         }

         int pos = offset + 13 + actorNameOffset;
         int actorNameLen = VarInt.peek(buffer, pos);
         if (actorNameLen < 0) {
            return ValidationResult.error("Invalid string length for ActorName");
         }

         if (actorNameLen > 4096000) {
            return ValidationResult.error("ActorName exceeds max length 4096000");
         }

         pos += VarInt.size(actorNameLen);
         pos += actorNameLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading ActorName");
         }
      }

      return ValidationResult.OK;
   }

   public RequestMachinimaActorModel clone() {
      RequestMachinimaActorModel copy = new RequestMachinimaActorModel();
      copy.modelId = this.modelId;
      copy.sceneName = this.sceneName;
      copy.actorName = this.actorName;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof RequestMachinimaActorModel other)
            ? false
            : Objects.equals(this.modelId, other.modelId) && Objects.equals(this.sceneName, other.sceneName) && Objects.equals(this.actorName, other.actorName);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.modelId, this.sceneName, this.actorName);
   }
}
