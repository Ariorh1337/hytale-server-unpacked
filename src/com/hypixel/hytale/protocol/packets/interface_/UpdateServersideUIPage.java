package com.hypixel.hytale.protocol.packets.interface_;

import com.hypixel.hytale.protocol.NetworkChannel;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.ToClientPacket;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class UpdateServersideUIPage implements Packet, ToClientPacket {
   public static final int PACKET_ID = 1200;
   public static final boolean IS_COMPRESSED = false;
   public static final int NULLABLE_BIT_FIELD_SIZE = 0;
   public static final int FIXED_BLOCK_SIZE = 0;
   public static final int VARIABLE_FIELD_COUNT = 1;
   public static final int VARIABLE_BLOCK_START = 0;
   public static final int MAX_SIZE = 1677721600;
   @Nonnull
   public ServersideUICommand[] commands = new ServersideUICommand[0];

   @Override
   public int getId() {
      return 1200;
   }

   @Override
   public NetworkChannel getChannel() {
      return NetworkChannel.Default;
   }

   public UpdateServersideUIPage() {
   }

   public UpdateServersideUIPage(@Nonnull ServersideUICommand[] commands) {
      this.commands = commands;
   }

   public UpdateServersideUIPage(@Nonnull UpdateServersideUIPage other) {
      this.commands = other.commands;
   }

   @Nonnull
   public static UpdateServersideUIPage deserialize(@Nonnull ByteBuf buf, int offset) {
      UpdateServersideUIPage obj = new UpdateServersideUIPage();
      int pos = offset + 0;
      int commandsCount = VarInt.peek(buf, pos);
      if (commandsCount < 0) {
         throw ProtocolException.invalidVarInt("Commands");
      }

      int commandsVarLen = VarInt.size(commandsCount);
      if (commandsCount > 4096000) {
         throw ProtocolException.arrayTooLong("Commands", commandsCount, 4096000);
      }

      if (pos + commandsVarLen + commandsCount * 1L > buf.readableBytes()) {
         throw ProtocolException.bufferTooSmall("Commands", pos + commandsVarLen + commandsCount * 1, buf.readableBytes());
      }

      pos += commandsVarLen;
      obj.commands = new ServersideUICommand[commandsCount];

      for (int i = 0; i < commandsCount; i++) {
         obj.commands[i] = ServersideUICommand.deserialize(buf, pos);
         pos += ServersideUICommand.computeBytesConsumed(buf, pos);
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      int pos = offset + 0;
      int arrLen = VarInt.peek(buf, pos);
      pos += VarInt.size(arrLen);

      for (int i = 0; i < arrLen; i++) {
         pos += ServersideUICommand.computeBytesConsumed(buf, pos);
      }

      return pos - offset;
   }

   @Override
   public void serialize(@Nonnull ByteBuf buf) {
      if (this.commands.length > 4096000) {
         throw ProtocolException.arrayTooLong("Commands", this.commands.length, 4096000);
      }

      VarInt.write(buf, this.commands.length);

      for (ServersideUICommand item : this.commands) {
         item.serializeWithTypeId(buf);
      }
   }

   @Override
   public int computeSize() {
      int size = 0;
      int commandsSize = 0;

      for (ServersideUICommand elem : this.commands) {
         commandsSize += elem.computeSizeWithTypeId();
      }

      return size + VarInt.size(this.commands.length) + commandsSize;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 0) {
         return ValidationResult.error("Buffer too small: expected at least 0 bytes");
      }

      int pos = offset + 0;
      int commandsCount = VarInt.peek(buffer, pos);
      if (commandsCount < 0) {
         return ValidationResult.error("Invalid array count for Commands");
      }

      if (commandsCount > 4096000) {
         return ValidationResult.error("Commands exceeds max length 4096000");
      }

      pos += VarInt.size(commandsCount);

      for (int i = 0; i < commandsCount; i++) {
         ValidationResult structResult = ServersideUICommand.validateStructure(buffer, pos);
         if (!structResult.isValid()) {
            return ValidationResult.error("Invalid ServersideUICommand in Commands[" + i + "]: " + structResult.error());
         }

         pos += ServersideUICommand.computeBytesConsumed(buffer, pos);
      }

      return ValidationResult.OK;
   }

   public UpdateServersideUIPage clone() {
      UpdateServersideUIPage copy = new UpdateServersideUIPage();
      copy.commands = Arrays.copyOf(this.commands, this.commands.length);
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return obj instanceof UpdateServersideUIPage other ? Arrays.equals(this.commands, other.commands) : false;
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      return 31 * result + Arrays.hashCode(this.commands);
   }
}
