package com.hypixel.hytale.protocol.packets.interface_;

import com.hypixel.hytale.protocol.HostAddress;
import com.hypixel.hytale.protocol.NetworkChannel;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.ToClientPacket;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ServerInfo implements Packet, ToClientPacket {
   public static final int PACKET_ID = 223;
   public static final boolean IS_COMPRESSED = false;
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 5;
   public static final int VARIABLE_FIELD_COUNT = 3;
   public static final int VARIABLE_BLOCK_START = 17;
   public static final int MAX_SIZE = 32769058;
   @Nullable
   public String serverName;
   @Nullable
   public String motd;
   public int maxPlayers;
   @Nullable
   public HostAddress fallbackServer;

   @Override
   public int getId() {
      return 223;
   }

   @Override
   public NetworkChannel getChannel() {
      return NetworkChannel.Default;
   }

   public ServerInfo() {
   }

   public ServerInfo(@Nullable String serverName, @Nullable String motd, int maxPlayers, @Nullable HostAddress fallbackServer) {
      this.serverName = serverName;
      this.motd = motd;
      this.maxPlayers = maxPlayers;
      this.fallbackServer = fallbackServer;
   }

   public ServerInfo(@Nonnull ServerInfo other) {
      this.serverName = other.serverName;
      this.motd = other.motd;
      this.maxPlayers = other.maxPlayers;
      this.fallbackServer = other.fallbackServer;
   }

   @Nonnull
   public static ServerInfo deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 17) {
         throw ProtocolException.bufferTooSmall("ServerInfo", 17, buf.readableBytes() - offset);
      }

      ServerInfo obj = new ServerInfo();
      byte nullBits = buf.getByte(offset);
      obj.maxPlayers = buf.getIntLE(offset + 1);
      if ((nullBits & 1) != 0) {
         int varPosBase0 = buf.getIntLE(offset + 5);
         if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 17) {
            throw ProtocolException.invalidOffset("ServerName", varPosBase0, buf.readableBytes());
         }

         int varPos0 = offset + 17 + varPosBase0;
         int serverNameLen = VarInt.peek(buf, varPos0);
         if (serverNameLen < 0) {
            throw ProtocolException.invalidVarInt("ServerName");
         }

         int serverNameVarIntLen = VarInt.size(serverNameLen);
         if (serverNameLen > 4096000) {
            throw ProtocolException.stringTooLong("ServerName", serverNameLen, 4096000);
         }

         if (varPos0 + serverNameVarIntLen + serverNameLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("ServerName", varPos0 + serverNameVarIntLen + serverNameLen, buf.readableBytes());
         }

         obj.serverName = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
      }

      if ((nullBits & 2) != 0) {
         int varPosBase1 = buf.getIntLE(offset + 9);
         if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 17) {
            throw ProtocolException.invalidOffset("Motd", varPosBase1, buf.readableBytes());
         }

         int varPos1 = offset + 17 + varPosBase1;
         int motdLen = VarInt.peek(buf, varPos1);
         if (motdLen < 0) {
            throw ProtocolException.invalidVarInt("Motd");
         }

         int motdVarIntLen = VarInt.size(motdLen);
         if (motdLen > 4096000) {
            throw ProtocolException.stringTooLong("Motd", motdLen, 4096000);
         }

         if (varPos1 + motdVarIntLen + motdLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Motd", varPos1 + motdVarIntLen + motdLen, buf.readableBytes());
         }

         obj.motd = PacketIO.readVarString(buf, varPos1, PacketIO.UTF8);
      }

      if ((nullBits & 4) != 0) {
         int varPosBase2 = buf.getIntLE(offset + 13);
         if (varPosBase2 < 0 || varPosBase2 > buf.writerIndex() - offset - 17) {
            throw ProtocolException.invalidOffset("FallbackServer", varPosBase2, buf.readableBytes());
         }

         int varPos2 = offset + 17 + varPosBase2;
         obj.fallbackServer = HostAddress.deserialize(buf, varPos2);
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 17;
      if ((nullBits & 1) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 5);
         if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 17) {
            throw ProtocolException.invalidOffset("ServerName", fieldOffset0, maxEnd);
         }

         int pos0 = offset + 17 + fieldOffset0;
         int sl = VarInt.peek(buf, pos0);
         pos0 += VarInt.size(sl) + sl;
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits & 2) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 9);
         if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 17) {
            throw ProtocolException.invalidOffset("Motd", fieldOffset1, maxEnd);
         }

         int pos1 = offset + 17 + fieldOffset1;
         int sl = VarInt.peek(buf, pos1);
         pos1 += VarInt.size(sl) + sl;
         if (pos1 - offset > maxEnd) {
            maxEnd = pos1 - offset;
         }
      }

      if ((nullBits & 4) != 0) {
         int fieldOffset2 = buf.getIntLE(offset + 13);
         if (fieldOffset2 < 0 || fieldOffset2 > buf.writerIndex() - offset - 17) {
            throw ProtocolException.invalidOffset("FallbackServer", fieldOffset2, maxEnd);
         }

         int pos2 = offset + 17 + fieldOffset2;
         pos2 += HostAddress.computeBytesConsumed(buf, pos2);
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
      if (this.serverName != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.motd != null) {
         nullBits = (byte)(nullBits | 2);
      }

      if (this.fallbackServer != null) {
         nullBits = (byte)(nullBits | 4);
      }

      buf.writeByte(nullBits);
      buf.writeIntLE(this.maxPlayers);
      int serverNameOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int motdOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int fallbackServerOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.serverName != null) {
         buf.setIntLE(serverNameOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.serverName, 4096000);
      } else {
         buf.setIntLE(serverNameOffsetSlot, -1);
      }

      if (this.motd != null) {
         buf.setIntLE(motdOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.motd, 4096000);
      } else {
         buf.setIntLE(motdOffsetSlot, -1);
      }

      if (this.fallbackServer != null) {
         buf.setIntLE(fallbackServerOffsetSlot, buf.writerIndex() - varBlockStart);
         this.fallbackServer.serialize(buf);
      } else {
         buf.setIntLE(fallbackServerOffsetSlot, -1);
      }
   }

   @Override
   public int computeSize() {
      int size = 17;
      if (this.serverName != null) {
         size += PacketIO.stringSize(this.serverName);
      }

      if (this.motd != null) {
         size += PacketIO.stringSize(this.motd);
      }

      if (this.fallbackServer != null) {
         size += this.fallbackServer.computeSize();
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 17) {
         return ValidationResult.error("Buffer too small: expected at least 17 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      if ((nullBits & 1) != 0) {
         int serverNameOffset = buffer.getIntLE(offset + 5);
         if (serverNameOffset < 0 || serverNameOffset > buffer.writerIndex() - offset - 17) {
            return ValidationResult.error("Invalid offset for ServerName");
         }

         int pos = offset + 17 + serverNameOffset;
         int serverNameLen = VarInt.peek(buffer, pos);
         if (serverNameLen < 0) {
            return ValidationResult.error("Invalid string length for ServerName");
         }

         if (serverNameLen > 4096000) {
            return ValidationResult.error("ServerName exceeds max length 4096000");
         }

         pos += VarInt.size(serverNameLen);
         pos += serverNameLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading ServerName");
         }
      }

      if ((nullBits & 2) != 0) {
         int motdOffset = buffer.getIntLE(offset + 9);
         if (motdOffset < 0 || motdOffset > buffer.writerIndex() - offset - 17) {
            return ValidationResult.error("Invalid offset for Motd");
         }

         int pos = offset + 17 + motdOffset;
         int motdLen = VarInt.peek(buffer, pos);
         if (motdLen < 0) {
            return ValidationResult.error("Invalid string length for Motd");
         }

         if (motdLen > 4096000) {
            return ValidationResult.error("Motd exceeds max length 4096000");
         }

         pos += VarInt.size(motdLen);
         pos += motdLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Motd");
         }
      }

      if ((nullBits & 4) != 0) {
         int fallbackServerOffset = buffer.getIntLE(offset + 13);
         if (fallbackServerOffset < 0 || fallbackServerOffset > buffer.writerIndex() - offset - 17) {
            return ValidationResult.error("Invalid offset for FallbackServer");
         }

         int pos = offset + 17 + fallbackServerOffset;
         ValidationResult fallbackServerResult = HostAddress.validateStructure(buffer, pos);
         if (!fallbackServerResult.isValid()) {
            return ValidationResult.error("Invalid FallbackServer: " + fallbackServerResult.error());
         }

         pos += HostAddress.computeBytesConsumed(buffer, pos);
      }

      return ValidationResult.OK;
   }

   public ServerInfo clone() {
      ServerInfo copy = new ServerInfo();
      copy.serverName = this.serverName;
      copy.motd = this.motd;
      copy.maxPlayers = this.maxPlayers;
      copy.fallbackServer = this.fallbackServer != null ? this.fallbackServer.clone() : null;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof ServerInfo other)
            ? false
            : Objects.equals(this.serverName, other.serverName)
               && Objects.equals(this.motd, other.motd)
               && this.maxPlayers == other.maxPlayers
               && Objects.equals(this.fallbackServer, other.fallbackServer);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.serverName, this.motd, this.maxPlayers, this.fallbackServer);
   }
}
