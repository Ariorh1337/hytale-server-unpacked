package com.hypixel.hytale.protocol.packets.interface_;

import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;

public abstract class ServersideUICommand {
   public static final int MAX_SIZE = 1677721605;

   @Nonnull
   public static ServersideUICommand deserialize(@Nonnull ByteBuf buf, int offset) {
      int typeId = VarInt.peek(buf, offset);
      int typeIdLen = VarInt.size(typeId);

      return switch (typeId) {
         case 0 -> InitServersideUICommand.deserialize(buf, offset + typeIdLen);
         case 1 -> SetElementPropertyServersideUICommand.deserialize(buf, offset + typeIdLen);
         case 2 -> SetDataContextPropertyServersideUIProperty.deserialize(buf, offset + typeIdLen);
         case 3 -> InsertDataContextCollectionItemServersideUIProperty.deserialize(buf, offset + typeIdLen);
         case 4 -> RemoveDataContextCollectionItemServersideUIProperty.deserialize(buf, offset + typeIdLen);
         default -> throw ProtocolException.unknownPolymorphicType("ServersideUICommand", typeId);
      };
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      int typeId = VarInt.peek(buf, offset);
      int typeIdLen = VarInt.size(typeId);

      return typeIdLen + switch (typeId) {
         case 0 -> InitServersideUICommand.computeBytesConsumed(buf, offset + typeIdLen);
         case 1 -> SetElementPropertyServersideUICommand.computeBytesConsumed(buf, offset + typeIdLen);
         case 2 -> SetDataContextPropertyServersideUIProperty.computeBytesConsumed(buf, offset + typeIdLen);
         case 3 -> InsertDataContextCollectionItemServersideUIProperty.computeBytesConsumed(buf, offset + typeIdLen);
         case 4 -> RemoveDataContextCollectionItemServersideUIProperty.computeBytesConsumed(buf, offset + typeIdLen);
         default -> throw ProtocolException.unknownPolymorphicType("ServersideUICommand", typeId);
      };
   }

   public int getTypeId() {
      if (this instanceof InitServersideUICommand sub) {
         return 0;
      } else if (this instanceof SetElementPropertyServersideUICommand sub) {
         return 1;
      } else if (this instanceof SetDataContextPropertyServersideUIProperty sub) {
         return 2;
      } else if (this instanceof InsertDataContextCollectionItemServersideUIProperty sub) {
         return 3;
      } else if (this instanceof RemoveDataContextCollectionItemServersideUIProperty sub) {
         return 4;
      } else {
         throw new IllegalStateException("Unknown subtype: " + this.getClass().getName());
      }
   }

   public abstract int serialize(@Nonnull ByteBuf var1);

   public abstract int computeSize();

   public int serializeWithTypeId(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      VarInt.write(buf, this.getTypeId());
      this.serialize(buf);
      return buf.writerIndex() - startPos;
   }

   public int computeSizeWithTypeId() {
      return VarInt.size(this.getTypeId()) + this.computeSize();
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      int typeId = VarInt.peek(buffer, offset);
      int typeIdLen = VarInt.size(typeId);

      return switch (typeId) {
         case 0 -> InitServersideUICommand.validateStructure(buffer, offset + typeIdLen);
         case 1 -> SetElementPropertyServersideUICommand.validateStructure(buffer, offset + typeIdLen);
         case 2 -> SetDataContextPropertyServersideUIProperty.validateStructure(buffer, offset + typeIdLen);
         case 3 -> InsertDataContextCollectionItemServersideUIProperty.validateStructure(buffer, offset + typeIdLen);
         case 4 -> RemoveDataContextCollectionItemServersideUIProperty.validateStructure(buffer, offset + typeIdLen);
         default -> ValidationResult.error("Unknown polymorphic type ID " + typeId + " for ServersideUICommand");
      };
   }
}
