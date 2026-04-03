package com.hypixel.hytale.protocol.packets.interface_;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CommandTreeEntry {
   public static final int NULLABLE_BIT_FIELD_SIZE = 2;
   public static final int FIXED_BLOCK_SIZE = 2;
   public static final int VARIABLE_FIELD_COUNT = 10;
   public static final int VARIABLE_BLOCK_START = 42;
   public static final int MAX_SIZE = 1677721600;
   @Nullable
   public String name;
   @Nullable
   public String[] aliases;
   @Nullable
   public String usageText;
   @Nullable
   public String description;
   @Nullable
   public CommandVariantEntry[] variants;
   @Nullable
   public CommandTreeEntry[] subcommands;
   @Nullable
   public String[] subcommandHints;
   @Nullable
   public CommandArgInfo[] requiredArgs;
   @Nullable
   public CommandOptionalArgEntry[] optionalArgs;
   @Nullable
   public CommandSuggestionOverride[] suggestionOverrides;

   public CommandTreeEntry() {
   }

   public CommandTreeEntry(
      @Nullable String name,
      @Nullable String[] aliases,
      @Nullable String usageText,
      @Nullable String description,
      @Nullable CommandVariantEntry[] variants,
      @Nullable CommandTreeEntry[] subcommands,
      @Nullable String[] subcommandHints,
      @Nullable CommandArgInfo[] requiredArgs,
      @Nullable CommandOptionalArgEntry[] optionalArgs,
      @Nullable CommandSuggestionOverride[] suggestionOverrides
   ) {
      this.name = name;
      this.aliases = aliases;
      this.usageText = usageText;
      this.description = description;
      this.variants = variants;
      this.subcommands = subcommands;
      this.subcommandHints = subcommandHints;
      this.requiredArgs = requiredArgs;
      this.optionalArgs = optionalArgs;
      this.suggestionOverrides = suggestionOverrides;
   }

   public CommandTreeEntry(@Nonnull CommandTreeEntry other) {
      this.name = other.name;
      this.aliases = other.aliases;
      this.usageText = other.usageText;
      this.description = other.description;
      this.variants = other.variants;
      this.subcommands = other.subcommands;
      this.subcommandHints = other.subcommandHints;
      this.requiredArgs = other.requiredArgs;
      this.optionalArgs = other.optionalArgs;
      this.suggestionOverrides = other.suggestionOverrides;
   }

   @Nonnull
   public static CommandTreeEntry deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 42) {
         throw ProtocolException.bufferTooSmall("CommandTreeEntry", 42, buf.readableBytes() - offset);
      }

      CommandTreeEntry obj = new CommandTreeEntry();
      byte[] nullBits = PacketIO.readBytes(buf, offset, 2);
      if ((nullBits[0] & 1) != 0) {
         int varPosBase0 = buf.getIntLE(offset + 2);
         if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 42) {
            throw ProtocolException.invalidOffset("Name", varPosBase0, buf.readableBytes());
         }

         int varPos0 = offset + 42 + varPosBase0;
         int nameLen = VarInt.peek(buf, varPos0);
         if (nameLen < 0) {
            throw ProtocolException.invalidVarInt("Name");
         }

         int nameVarIntLen = VarInt.size(nameLen);
         if (nameLen > 4096000) {
            throw ProtocolException.stringTooLong("Name", nameLen, 4096000);
         }

         if (varPos0 + nameVarIntLen + nameLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Name", varPos0 + nameVarIntLen + nameLen, buf.readableBytes());
         }

         obj.name = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
      }

      if ((nullBits[0] & 2) != 0) {
         int varPosBase1 = buf.getIntLE(offset + 6);
         if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 42) {
            throw ProtocolException.invalidOffset("Aliases", varPosBase1, buf.readableBytes());
         }

         int varPos1 = offset + 42 + varPosBase1;
         int aliasesCount = VarInt.peek(buf, varPos1);
         if (aliasesCount < 0) {
            throw ProtocolException.invalidVarInt("Aliases");
         }

         int varIntLen = VarInt.size(aliasesCount);
         if (aliasesCount > 4096000) {
            throw ProtocolException.arrayTooLong("Aliases", aliasesCount, 4096000);
         }

         if (varPos1 + varIntLen + aliasesCount * 1L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Aliases", varPos1 + varIntLen + aliasesCount * 1, buf.readableBytes());
         }

         obj.aliases = new String[aliasesCount];
         int elemPos = varPos1 + varIntLen;

         for (int i = 0; i < aliasesCount; i++) {
            int strLen = VarInt.peek(buf, elemPos);
            if (strLen < 0) {
               throw ProtocolException.invalidVarInt("aliases[" + i + "]");
            }

            int strVarLen = VarInt.size(strLen);
            if (strLen > 4096000) {
               throw ProtocolException.stringTooLong("aliases[" + i + "]", strLen, 4096000);
            }

            if (elemPos + strVarLen + strLen > buf.readableBytes()) {
               throw ProtocolException.bufferTooSmall("aliases[" + i + "]", elemPos + strVarLen + strLen, buf.readableBytes());
            }

            obj.aliases[i] = PacketIO.readVarString(buf, elemPos);
            elemPos += strVarLen + strLen;
         }
      }

      if ((nullBits[0] & 4) != 0) {
         int varPosBase2 = buf.getIntLE(offset + 10);
         if (varPosBase2 < 0 || varPosBase2 > buf.writerIndex() - offset - 42) {
            throw ProtocolException.invalidOffset("UsageText", varPosBase2, buf.readableBytes());
         }

         int varPos2 = offset + 42 + varPosBase2;
         int usageTextLen = VarInt.peek(buf, varPos2);
         if (usageTextLen < 0) {
            throw ProtocolException.invalidVarInt("UsageText");
         }

         int usageTextVarIntLen = VarInt.size(usageTextLen);
         if (usageTextLen > 4096000) {
            throw ProtocolException.stringTooLong("UsageText", usageTextLen, 4096000);
         }

         if (varPos2 + usageTextVarIntLen + usageTextLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("UsageText", varPos2 + usageTextVarIntLen + usageTextLen, buf.readableBytes());
         }

         obj.usageText = PacketIO.readVarString(buf, varPos2, PacketIO.UTF8);
      }

      if ((nullBits[0] & 8) != 0) {
         int varPosBase3 = buf.getIntLE(offset + 14);
         if (varPosBase3 < 0 || varPosBase3 > buf.writerIndex() - offset - 42) {
            throw ProtocolException.invalidOffset("Description", varPosBase3, buf.readableBytes());
         }

         int varPos3 = offset + 42 + varPosBase3;
         int descriptionLen = VarInt.peek(buf, varPos3);
         if (descriptionLen < 0) {
            throw ProtocolException.invalidVarInt("Description");
         }

         int descriptionVarIntLen = VarInt.size(descriptionLen);
         if (descriptionLen > 4096000) {
            throw ProtocolException.stringTooLong("Description", descriptionLen, 4096000);
         }

         if (varPos3 + descriptionVarIntLen + descriptionLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Description", varPos3 + descriptionVarIntLen + descriptionLen, buf.readableBytes());
         }

         obj.description = PacketIO.readVarString(buf, varPos3, PacketIO.UTF8);
      }

      if ((nullBits[0] & 16) != 0) {
         int varPosBase4 = buf.getIntLE(offset + 18);
         if (varPosBase4 < 0 || varPosBase4 > buf.writerIndex() - offset - 42) {
            throw ProtocolException.invalidOffset("Variants", varPosBase4, buf.readableBytes());
         }

         int varPos4 = offset + 42 + varPosBase4;
         int variantsCount = VarInt.peek(buf, varPos4);
         if (variantsCount < 0) {
            throw ProtocolException.invalidVarInt("Variants");
         }

         int varIntLen = VarInt.size(variantsCount);
         if (variantsCount > 4096000) {
            throw ProtocolException.arrayTooLong("Variants", variantsCount, 4096000);
         }

         if (varPos4 + varIntLen + variantsCount * 1L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Variants", varPos4 + varIntLen + variantsCount * 1, buf.readableBytes());
         }

         obj.variants = new CommandVariantEntry[variantsCount];
         int elemPos = varPos4 + varIntLen;

         for (int i = 0; i < variantsCount; i++) {
            obj.variants[i] = CommandVariantEntry.deserialize(buf, elemPos);
            elemPos += CommandVariantEntry.computeBytesConsumed(buf, elemPos);
         }
      }

      if ((nullBits[0] & 32) != 0) {
         int varPosBase5 = buf.getIntLE(offset + 22);
         if (varPosBase5 < 0 || varPosBase5 > buf.writerIndex() - offset - 42) {
            throw ProtocolException.invalidOffset("Subcommands", varPosBase5, buf.readableBytes());
         }

         int varPos5 = offset + 42 + varPosBase5;
         int subcommandsCount = VarInt.peek(buf, varPos5);
         if (subcommandsCount < 0) {
            throw ProtocolException.invalidVarInt("Subcommands");
         }

         int varIntLen = VarInt.size(subcommandsCount);
         if (subcommandsCount > 4096000) {
            throw ProtocolException.arrayTooLong("Subcommands", subcommandsCount, 4096000);
         }

         if (varPos5 + varIntLen + subcommandsCount * 2L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Subcommands", varPos5 + varIntLen + subcommandsCount * 2, buf.readableBytes());
         }

         obj.subcommands = new CommandTreeEntry[subcommandsCount];
         int elemPos = varPos5 + varIntLen;

         for (int i = 0; i < subcommandsCount; i++) {
            obj.subcommands[i] = deserialize(buf, elemPos);
            elemPos += computeBytesConsumed(buf, elemPos);
         }
      }

      if ((nullBits[0] & 64) != 0) {
         int varPosBase6 = buf.getIntLE(offset + 26);
         if (varPosBase6 < 0 || varPosBase6 > buf.writerIndex() - offset - 42) {
            throw ProtocolException.invalidOffset("SubcommandHints", varPosBase6, buf.readableBytes());
         }

         int varPos6 = offset + 42 + varPosBase6;
         int subcommandHintsCount = VarInt.peek(buf, varPos6);
         if (subcommandHintsCount < 0) {
            throw ProtocolException.invalidVarInt("SubcommandHints");
         }

         int varIntLen = VarInt.size(subcommandHintsCount);
         if (subcommandHintsCount > 4096000) {
            throw ProtocolException.arrayTooLong("SubcommandHints", subcommandHintsCount, 4096000);
         }

         if (varPos6 + varIntLen + subcommandHintsCount * 1L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("SubcommandHints", varPos6 + varIntLen + subcommandHintsCount * 1, buf.readableBytes());
         }

         obj.subcommandHints = new String[subcommandHintsCount];
         int elemPos = varPos6 + varIntLen;

         for (int i = 0; i < subcommandHintsCount; i++) {
            int strLen = VarInt.peek(buf, elemPos);
            if (strLen < 0) {
               throw ProtocolException.invalidVarInt("subcommandHints[" + i + "]");
            }

            int strVarLen = VarInt.size(strLen);
            if (strLen > 4096000) {
               throw ProtocolException.stringTooLong("subcommandHints[" + i + "]", strLen, 4096000);
            }

            if (elemPos + strVarLen + strLen > buf.readableBytes()) {
               throw ProtocolException.bufferTooSmall("subcommandHints[" + i + "]", elemPos + strVarLen + strLen, buf.readableBytes());
            }

            obj.subcommandHints[i] = PacketIO.readVarString(buf, elemPos);
            elemPos += strVarLen + strLen;
         }
      }

      if ((nullBits[0] & 128) != 0) {
         int varPosBase7 = buf.getIntLE(offset + 30);
         if (varPosBase7 < 0 || varPosBase7 > buf.writerIndex() - offset - 42) {
            throw ProtocolException.invalidOffset("RequiredArgs", varPosBase7, buf.readableBytes());
         }

         int varPos7 = offset + 42 + varPosBase7;
         int requiredArgsCount = VarInt.peek(buf, varPos7);
         if (requiredArgsCount < 0) {
            throw ProtocolException.invalidVarInt("RequiredArgs");
         }

         int varIntLen = VarInt.size(requiredArgsCount);
         if (requiredArgsCount > 4096000) {
            throw ProtocolException.arrayTooLong("RequiredArgs", requiredArgsCount, 4096000);
         }

         if (varPos7 + varIntLen + requiredArgsCount * 5L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("RequiredArgs", varPos7 + varIntLen + requiredArgsCount * 5, buf.readableBytes());
         }

         obj.requiredArgs = new CommandArgInfo[requiredArgsCount];
         int elemPos = varPos7 + varIntLen;

         for (int i = 0; i < requiredArgsCount; i++) {
            obj.requiredArgs[i] = CommandArgInfo.deserialize(buf, elemPos);
            elemPos += CommandArgInfo.computeBytesConsumed(buf, elemPos);
         }
      }

      if ((nullBits[1] & 1) != 0) {
         int varPosBase8 = buf.getIntLE(offset + 34);
         if (varPosBase8 < 0 || varPosBase8 > buf.writerIndex() - offset - 42) {
            throw ProtocolException.invalidOffset("OptionalArgs", varPosBase8, buf.readableBytes());
         }

         int varPos8 = offset + 42 + varPosBase8;
         int optionalArgsCount = VarInt.peek(buf, varPos8);
         if (optionalArgsCount < 0) {
            throw ProtocolException.invalidVarInt("OptionalArgs");
         }

         int varIntLen = VarInt.size(optionalArgsCount);
         if (optionalArgsCount > 4096000) {
            throw ProtocolException.arrayTooLong("OptionalArgs", optionalArgsCount, 4096000);
         }

         if (varPos8 + varIntLen + optionalArgsCount * 1L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("OptionalArgs", varPos8 + varIntLen + optionalArgsCount * 1, buf.readableBytes());
         }

         obj.optionalArgs = new CommandOptionalArgEntry[optionalArgsCount];
         int elemPos = varPos8 + varIntLen;

         for (int i = 0; i < optionalArgsCount; i++) {
            obj.optionalArgs[i] = CommandOptionalArgEntry.deserialize(buf, elemPos);
            elemPos += CommandOptionalArgEntry.computeBytesConsumed(buf, elemPos);
         }
      }

      if ((nullBits[1] & 2) != 0) {
         int varPosBase9 = buf.getIntLE(offset + 38);
         if (varPosBase9 < 0 || varPosBase9 > buf.writerIndex() - offset - 42) {
            throw ProtocolException.invalidOffset("SuggestionOverrides", varPosBase9, buf.readableBytes());
         }

         int varPos9 = offset + 42 + varPosBase9;
         int suggestionOverridesCount = VarInt.peek(buf, varPos9);
         if (suggestionOverridesCount < 0) {
            throw ProtocolException.invalidVarInt("SuggestionOverrides");
         }

         int varIntLen = VarInt.size(suggestionOverridesCount);
         if (suggestionOverridesCount > 4096000) {
            throw ProtocolException.arrayTooLong("SuggestionOverrides", suggestionOverridesCount, 4096000);
         }

         if (varPos9 + varIntLen + suggestionOverridesCount * 9L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("SuggestionOverrides", varPos9 + varIntLen + suggestionOverridesCount * 9, buf.readableBytes());
         }

         obj.suggestionOverrides = new CommandSuggestionOverride[suggestionOverridesCount];
         int elemPos = varPos9 + varIntLen;

         for (int i = 0; i < suggestionOverridesCount; i++) {
            obj.suggestionOverrides[i] = CommandSuggestionOverride.deserialize(buf, elemPos);
            elemPos += CommandSuggestionOverride.computeBytesConsumed(buf, elemPos);
         }
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte[] nullBits = PacketIO.readBytes(buf, offset, 2);
      int maxEnd = 42;
      if ((nullBits[0] & 1) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 2);
         if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 42) {
            throw ProtocolException.invalidOffset("Name", fieldOffset0, maxEnd);
         }

         int pos0 = offset + 42 + fieldOffset0;
         int sl = VarInt.peek(buf, pos0);
         pos0 += VarInt.size(sl) + sl;
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits[0] & 2) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 6);
         if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 42) {
            throw ProtocolException.invalidOffset("Aliases", fieldOffset1, maxEnd);
         }

         int pos1 = offset + 42 + fieldOffset1;
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

      if ((nullBits[0] & 4) != 0) {
         int fieldOffset2 = buf.getIntLE(offset + 10);
         if (fieldOffset2 < 0 || fieldOffset2 > buf.writerIndex() - offset - 42) {
            throw ProtocolException.invalidOffset("UsageText", fieldOffset2, maxEnd);
         }

         int pos2 = offset + 42 + fieldOffset2;
         int sl = VarInt.peek(buf, pos2);
         pos2 += VarInt.size(sl) + sl;
         if (pos2 - offset > maxEnd) {
            maxEnd = pos2 - offset;
         }
      }

      if ((nullBits[0] & 8) != 0) {
         int fieldOffset3 = buf.getIntLE(offset + 14);
         if (fieldOffset3 < 0 || fieldOffset3 > buf.writerIndex() - offset - 42) {
            throw ProtocolException.invalidOffset("Description", fieldOffset3, maxEnd);
         }

         int pos3 = offset + 42 + fieldOffset3;
         int sl = VarInt.peek(buf, pos3);
         pos3 += VarInt.size(sl) + sl;
         if (pos3 - offset > maxEnd) {
            maxEnd = pos3 - offset;
         }
      }

      if ((nullBits[0] & 16) != 0) {
         int fieldOffset4 = buf.getIntLE(offset + 18);
         if (fieldOffset4 < 0 || fieldOffset4 > buf.writerIndex() - offset - 42) {
            throw ProtocolException.invalidOffset("Variants", fieldOffset4, maxEnd);
         }

         int pos4 = offset + 42 + fieldOffset4;
         int arrLen = VarInt.peek(buf, pos4);
         pos4 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            pos4 += CommandVariantEntry.computeBytesConsumed(buf, pos4);
         }

         if (pos4 - offset > maxEnd) {
            maxEnd = pos4 - offset;
         }
      }

      if ((nullBits[0] & 32) != 0) {
         int fieldOffset5 = buf.getIntLE(offset + 22);
         if (fieldOffset5 < 0 || fieldOffset5 > buf.writerIndex() - offset - 42) {
            throw ProtocolException.invalidOffset("Subcommands", fieldOffset5, maxEnd);
         }

         int pos5 = offset + 42 + fieldOffset5;
         int arrLen = VarInt.peek(buf, pos5);
         pos5 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            pos5 += computeBytesConsumed(buf, pos5);
         }

         if (pos5 - offset > maxEnd) {
            maxEnd = pos5 - offset;
         }
      }

      if ((nullBits[0] & 64) != 0) {
         int fieldOffset6 = buf.getIntLE(offset + 26);
         if (fieldOffset6 < 0 || fieldOffset6 > buf.writerIndex() - offset - 42) {
            throw ProtocolException.invalidOffset("SubcommandHints", fieldOffset6, maxEnd);
         }

         int pos6 = offset + 42 + fieldOffset6;
         int arrLen = VarInt.peek(buf, pos6);
         pos6 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            int sl = VarInt.peek(buf, pos6);
            pos6 += VarInt.size(sl) + sl;
         }

         if (pos6 - offset > maxEnd) {
            maxEnd = pos6 - offset;
         }
      }

      if ((nullBits[0] & 128) != 0) {
         int fieldOffset7 = buf.getIntLE(offset + 30);
         if (fieldOffset7 < 0 || fieldOffset7 > buf.writerIndex() - offset - 42) {
            throw ProtocolException.invalidOffset("RequiredArgs", fieldOffset7, maxEnd);
         }

         int pos7 = offset + 42 + fieldOffset7;
         int arrLen = VarInt.peek(buf, pos7);
         pos7 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            pos7 += CommandArgInfo.computeBytesConsumed(buf, pos7);
         }

         if (pos7 - offset > maxEnd) {
            maxEnd = pos7 - offset;
         }
      }

      if ((nullBits[1] & 1) != 0) {
         int fieldOffset8 = buf.getIntLE(offset + 34);
         if (fieldOffset8 < 0 || fieldOffset8 > buf.writerIndex() - offset - 42) {
            throw ProtocolException.invalidOffset("OptionalArgs", fieldOffset8, maxEnd);
         }

         int pos8 = offset + 42 + fieldOffset8;
         int arrLen = VarInt.peek(buf, pos8);
         pos8 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            pos8 += CommandOptionalArgEntry.computeBytesConsumed(buf, pos8);
         }

         if (pos8 - offset > maxEnd) {
            maxEnd = pos8 - offset;
         }
      }

      if ((nullBits[1] & 2) != 0) {
         int fieldOffset9 = buf.getIntLE(offset + 38);
         if (fieldOffset9 < 0 || fieldOffset9 > buf.writerIndex() - offset - 42) {
            throw ProtocolException.invalidOffset("SuggestionOverrides", fieldOffset9, maxEnd);
         }

         int pos9 = offset + 42 + fieldOffset9;
         int arrLen = VarInt.peek(buf, pos9);
         pos9 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            pos9 += CommandSuggestionOverride.computeBytesConsumed(buf, pos9);
         }

         if (pos9 - offset > maxEnd) {
            maxEnd = pos9 - offset;
         }
      }

      return maxEnd;
   }

   public void serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      byte[] nullBits = new byte[2];
      if (this.name != null) {
         nullBits[0] = (byte)(nullBits[0] | 1);
      }

      if (this.aliases != null) {
         nullBits[0] = (byte)(nullBits[0] | 2);
      }

      if (this.usageText != null) {
         nullBits[0] = (byte)(nullBits[0] | 4);
      }

      if (this.description != null) {
         nullBits[0] = (byte)(nullBits[0] | 8);
      }

      if (this.variants != null) {
         nullBits[0] = (byte)(nullBits[0] | 16);
      }

      if (this.subcommands != null) {
         nullBits[0] = (byte)(nullBits[0] | 32);
      }

      if (this.subcommandHints != null) {
         nullBits[0] = (byte)(nullBits[0] | 64);
      }

      if (this.requiredArgs != null) {
         nullBits[0] = (byte)(nullBits[0] | 128);
      }

      if (this.optionalArgs != null) {
         nullBits[1] = (byte)(nullBits[1] | 1);
      }

      if (this.suggestionOverrides != null) {
         nullBits[1] = (byte)(nullBits[1] | 2);
      }

      buf.writeBytes(nullBits);
      int nameOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int aliasesOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int usageTextOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int descriptionOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int variantsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int subcommandsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int subcommandHintsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int requiredArgsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int optionalArgsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int suggestionOverridesOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.name != null) {
         buf.setIntLE(nameOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.name, 4096000);
      } else {
         buf.setIntLE(nameOffsetSlot, -1);
      }

      if (this.aliases != null) {
         buf.setIntLE(aliasesOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.aliases.length > 4096000) {
            throw ProtocolException.arrayTooLong("Aliases", this.aliases.length, 4096000);
         }

         VarInt.write(buf, this.aliases.length);

         for (String item : this.aliases) {
            PacketIO.writeVarString(buf, item, 4096000);
         }
      } else {
         buf.setIntLE(aliasesOffsetSlot, -1);
      }

      if (this.usageText != null) {
         buf.setIntLE(usageTextOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.usageText, 4096000);
      } else {
         buf.setIntLE(usageTextOffsetSlot, -1);
      }

      if (this.description != null) {
         buf.setIntLE(descriptionOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.description, 4096000);
      } else {
         buf.setIntLE(descriptionOffsetSlot, -1);
      }

      if (this.variants != null) {
         buf.setIntLE(variantsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.variants.length > 4096000) {
            throw ProtocolException.arrayTooLong("Variants", this.variants.length, 4096000);
         }

         VarInt.write(buf, this.variants.length);

         for (CommandVariantEntry item : this.variants) {
            item.serialize(buf);
         }
      } else {
         buf.setIntLE(variantsOffsetSlot, -1);
      }

      if (this.subcommands != null) {
         buf.setIntLE(subcommandsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.subcommands.length > 4096000) {
            throw ProtocolException.arrayTooLong("Subcommands", this.subcommands.length, 4096000);
         }

         VarInt.write(buf, this.subcommands.length);

         for (CommandTreeEntry item : this.subcommands) {
            item.serialize(buf);
         }
      } else {
         buf.setIntLE(subcommandsOffsetSlot, -1);
      }

      if (this.subcommandHints != null) {
         buf.setIntLE(subcommandHintsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.subcommandHints.length > 4096000) {
            throw ProtocolException.arrayTooLong("SubcommandHints", this.subcommandHints.length, 4096000);
         }

         VarInt.write(buf, this.subcommandHints.length);

         for (String item : this.subcommandHints) {
            PacketIO.writeVarString(buf, item, 4096000);
         }
      } else {
         buf.setIntLE(subcommandHintsOffsetSlot, -1);
      }

      if (this.requiredArgs != null) {
         buf.setIntLE(requiredArgsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.requiredArgs.length > 4096000) {
            throw ProtocolException.arrayTooLong("RequiredArgs", this.requiredArgs.length, 4096000);
         }

         VarInt.write(buf, this.requiredArgs.length);

         for (CommandArgInfo item : this.requiredArgs) {
            item.serialize(buf);
         }
      } else {
         buf.setIntLE(requiredArgsOffsetSlot, -1);
      }

      if (this.optionalArgs != null) {
         buf.setIntLE(optionalArgsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.optionalArgs.length > 4096000) {
            throw ProtocolException.arrayTooLong("OptionalArgs", this.optionalArgs.length, 4096000);
         }

         VarInt.write(buf, this.optionalArgs.length);

         for (CommandOptionalArgEntry item : this.optionalArgs) {
            item.serialize(buf);
         }
      } else {
         buf.setIntLE(optionalArgsOffsetSlot, -1);
      }

      if (this.suggestionOverrides != null) {
         buf.setIntLE(suggestionOverridesOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.suggestionOverrides.length > 4096000) {
            throw ProtocolException.arrayTooLong("SuggestionOverrides", this.suggestionOverrides.length, 4096000);
         }

         VarInt.write(buf, this.suggestionOverrides.length);

         for (CommandSuggestionOverride item : this.suggestionOverrides) {
            item.serialize(buf);
         }
      } else {
         buf.setIntLE(suggestionOverridesOffsetSlot, -1);
      }
   }

   public int computeSize() {
      int size = 42;
      if (this.name != null) {
         size += PacketIO.stringSize(this.name);
      }

      if (this.aliases != null) {
         int aliasesSize = 0;

         for (String elem : this.aliases) {
            aliasesSize += PacketIO.stringSize(elem);
         }

         size += VarInt.size(this.aliases.length) + aliasesSize;
      }

      if (this.usageText != null) {
         size += PacketIO.stringSize(this.usageText);
      }

      if (this.description != null) {
         size += PacketIO.stringSize(this.description);
      }

      if (this.variants != null) {
         int variantsSize = 0;

         for (CommandVariantEntry elem : this.variants) {
            variantsSize += elem.computeSize();
         }

         size += VarInt.size(this.variants.length) + variantsSize;
      }

      if (this.subcommands != null) {
         int subcommandsSize = 0;

         for (CommandTreeEntry elem : this.subcommands) {
            subcommandsSize += elem.computeSize();
         }

         size += VarInt.size(this.subcommands.length) + subcommandsSize;
      }

      if (this.subcommandHints != null) {
         int subcommandHintsSize = 0;

         for (String elem : this.subcommandHints) {
            subcommandHintsSize += PacketIO.stringSize(elem);
         }

         size += VarInt.size(this.subcommandHints.length) + subcommandHintsSize;
      }

      if (this.requiredArgs != null) {
         int requiredArgsSize = 0;

         for (CommandArgInfo elem : this.requiredArgs) {
            requiredArgsSize += elem.computeSize();
         }

         size += VarInt.size(this.requiredArgs.length) + requiredArgsSize;
      }

      if (this.optionalArgs != null) {
         int optionalArgsSize = 0;

         for (CommandOptionalArgEntry elem : this.optionalArgs) {
            optionalArgsSize += elem.computeSize();
         }

         size += VarInt.size(this.optionalArgs.length) + optionalArgsSize;
      }

      if (this.suggestionOverrides != null) {
         int suggestionOverridesSize = 0;

         for (CommandSuggestionOverride elem : this.suggestionOverrides) {
            suggestionOverridesSize += elem.computeSize();
         }

         size += VarInt.size(this.suggestionOverrides.length) + suggestionOverridesSize;
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 42) {
         return ValidationResult.error("Buffer too small: expected at least 42 bytes");
      }

      byte[] nullBits = PacketIO.readBytes(buffer, offset, 2);
      if ((nullBits[0] & 1) != 0) {
         int nameOffset = buffer.getIntLE(offset + 2);
         if (nameOffset < 0 || nameOffset > buffer.writerIndex() - offset - 42) {
            return ValidationResult.error("Invalid offset for Name");
         }

         int pos = offset + 42 + nameOffset;
         int nameLen = VarInt.peek(buffer, pos);
         if (nameLen < 0) {
            return ValidationResult.error("Invalid string length for Name");
         }

         if (nameLen > 4096000) {
            return ValidationResult.error("Name exceeds max length 4096000");
         }

         pos += VarInt.size(nameLen);
         pos += nameLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Name");
         }
      }

      if ((nullBits[0] & 2) != 0) {
         int aliasesOffset = buffer.getIntLE(offset + 6);
         if (aliasesOffset < 0 || aliasesOffset > buffer.writerIndex() - offset - 42) {
            return ValidationResult.error("Invalid offset for Aliases");
         }

         int pos = offset + 42 + aliasesOffset;
         int aliasesCount = VarInt.peek(buffer, pos);
         if (aliasesCount < 0) {
            return ValidationResult.error("Invalid array count for Aliases");
         }

         if (aliasesCount > 4096000) {
            return ValidationResult.error("Aliases exceeds max length 4096000");
         }

         pos += VarInt.size(aliasesCount);

         for (int i = 0; i < aliasesCount; i++) {
            int strLen = VarInt.peek(buffer, pos);
            if (strLen < 0) {
               return ValidationResult.error("Invalid string length in Aliases");
            }

            pos += VarInt.size(strLen);
            pos += strLen;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading string in Aliases");
            }
         }
      }

      if ((nullBits[0] & 4) != 0) {
         int usageTextOffset = buffer.getIntLE(offset + 10);
         if (usageTextOffset < 0 || usageTextOffset > buffer.writerIndex() - offset - 42) {
            return ValidationResult.error("Invalid offset for UsageText");
         }

         int pos = offset + 42 + usageTextOffset;
         int usageTextLen = VarInt.peek(buffer, pos);
         if (usageTextLen < 0) {
            return ValidationResult.error("Invalid string length for UsageText");
         }

         if (usageTextLen > 4096000) {
            return ValidationResult.error("UsageText exceeds max length 4096000");
         }

         pos += VarInt.size(usageTextLen);
         pos += usageTextLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading UsageText");
         }
      }

      if ((nullBits[0] & 8) != 0) {
         int descriptionOffset = buffer.getIntLE(offset + 14);
         if (descriptionOffset < 0 || descriptionOffset > buffer.writerIndex() - offset - 42) {
            return ValidationResult.error("Invalid offset for Description");
         }

         int pos = offset + 42 + descriptionOffset;
         int descriptionLen = VarInt.peek(buffer, pos);
         if (descriptionLen < 0) {
            return ValidationResult.error("Invalid string length for Description");
         }

         if (descriptionLen > 4096000) {
            return ValidationResult.error("Description exceeds max length 4096000");
         }

         pos += VarInt.size(descriptionLen);
         pos += descriptionLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Description");
         }
      }

      if ((nullBits[0] & 16) != 0) {
         int variantsOffset = buffer.getIntLE(offset + 18);
         if (variantsOffset < 0 || variantsOffset > buffer.writerIndex() - offset - 42) {
            return ValidationResult.error("Invalid offset for Variants");
         }

         int pos = offset + 42 + variantsOffset;
         int variantsCount = VarInt.peek(buffer, pos);
         if (variantsCount < 0) {
            return ValidationResult.error("Invalid array count for Variants");
         }

         if (variantsCount > 4096000) {
            return ValidationResult.error("Variants exceeds max length 4096000");
         }

         pos += VarInt.size(variantsCount);

         for (int i = 0; i < variantsCount; i++) {
            ValidationResult structResult = CommandVariantEntry.validateStructure(buffer, pos);
            if (!structResult.isValid()) {
               return ValidationResult.error("Invalid CommandVariantEntry in Variants[" + i + "]: " + structResult.error());
            }

            pos += CommandVariantEntry.computeBytesConsumed(buffer, pos);
         }
      }

      if ((nullBits[0] & 32) != 0) {
         int subcommandsOffset = buffer.getIntLE(offset + 22);
         if (subcommandsOffset < 0 || subcommandsOffset > buffer.writerIndex() - offset - 42) {
            return ValidationResult.error("Invalid offset for Subcommands");
         }

         int pos = offset + 42 + subcommandsOffset;
         int subcommandsCount = VarInt.peek(buffer, pos);
         if (subcommandsCount < 0) {
            return ValidationResult.error("Invalid array count for Subcommands");
         }

         if (subcommandsCount > 4096000) {
            return ValidationResult.error("Subcommands exceeds max length 4096000");
         }

         pos += VarInt.size(subcommandsCount);

         for (int i = 0; i < subcommandsCount; i++) {
            ValidationResult structResult = validateStructure(buffer, pos);
            if (!structResult.isValid()) {
               return ValidationResult.error("Invalid CommandTreeEntry in Subcommands[" + i + "]: " + structResult.error());
            }

            pos += computeBytesConsumed(buffer, pos);
         }
      }

      if ((nullBits[0] & 64) != 0) {
         int subcommandHintsOffset = buffer.getIntLE(offset + 26);
         if (subcommandHintsOffset < 0 || subcommandHintsOffset > buffer.writerIndex() - offset - 42) {
            return ValidationResult.error("Invalid offset for SubcommandHints");
         }

         int pos = offset + 42 + subcommandHintsOffset;
         int subcommandHintsCount = VarInt.peek(buffer, pos);
         if (subcommandHintsCount < 0) {
            return ValidationResult.error("Invalid array count for SubcommandHints");
         }

         if (subcommandHintsCount > 4096000) {
            return ValidationResult.error("SubcommandHints exceeds max length 4096000");
         }

         pos += VarInt.size(subcommandHintsCount);

         for (int i = 0; i < subcommandHintsCount; i++) {
            int strLen = VarInt.peek(buffer, pos);
            if (strLen < 0) {
               return ValidationResult.error("Invalid string length in SubcommandHints");
            }

            pos += VarInt.size(strLen);
            pos += strLen;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading string in SubcommandHints");
            }
         }
      }

      if ((nullBits[0] & 128) != 0) {
         int requiredArgsOffset = buffer.getIntLE(offset + 30);
         if (requiredArgsOffset < 0 || requiredArgsOffset > buffer.writerIndex() - offset - 42) {
            return ValidationResult.error("Invalid offset for RequiredArgs");
         }

         int pos = offset + 42 + requiredArgsOffset;
         int requiredArgsCount = VarInt.peek(buffer, pos);
         if (requiredArgsCount < 0) {
            return ValidationResult.error("Invalid array count for RequiredArgs");
         }

         if (requiredArgsCount > 4096000) {
            return ValidationResult.error("RequiredArgs exceeds max length 4096000");
         }

         pos += VarInt.size(requiredArgsCount);

         for (int i = 0; i < requiredArgsCount; i++) {
            ValidationResult structResult = CommandArgInfo.validateStructure(buffer, pos);
            if (!structResult.isValid()) {
               return ValidationResult.error("Invalid CommandArgInfo in RequiredArgs[" + i + "]: " + structResult.error());
            }

            pos += CommandArgInfo.computeBytesConsumed(buffer, pos);
         }
      }

      if ((nullBits[1] & 1) != 0) {
         int optionalArgsOffset = buffer.getIntLE(offset + 34);
         if (optionalArgsOffset < 0 || optionalArgsOffset > buffer.writerIndex() - offset - 42) {
            return ValidationResult.error("Invalid offset for OptionalArgs");
         }

         int pos = offset + 42 + optionalArgsOffset;
         int optionalArgsCount = VarInt.peek(buffer, pos);
         if (optionalArgsCount < 0) {
            return ValidationResult.error("Invalid array count for OptionalArgs");
         }

         if (optionalArgsCount > 4096000) {
            return ValidationResult.error("OptionalArgs exceeds max length 4096000");
         }

         pos += VarInt.size(optionalArgsCount);

         for (int i = 0; i < optionalArgsCount; i++) {
            ValidationResult structResult = CommandOptionalArgEntry.validateStructure(buffer, pos);
            if (!structResult.isValid()) {
               return ValidationResult.error("Invalid CommandOptionalArgEntry in OptionalArgs[" + i + "]: " + structResult.error());
            }

            pos += CommandOptionalArgEntry.computeBytesConsumed(buffer, pos);
         }
      }

      if ((nullBits[1] & 2) != 0) {
         int suggestionOverridesOffset = buffer.getIntLE(offset + 38);
         if (suggestionOverridesOffset < 0 || suggestionOverridesOffset > buffer.writerIndex() - offset - 42) {
            return ValidationResult.error("Invalid offset for SuggestionOverrides");
         }

         int pos = offset + 42 + suggestionOverridesOffset;
         int suggestionOverridesCount = VarInt.peek(buffer, pos);
         if (suggestionOverridesCount < 0) {
            return ValidationResult.error("Invalid array count for SuggestionOverrides");
         }

         if (suggestionOverridesCount > 4096000) {
            return ValidationResult.error("SuggestionOverrides exceeds max length 4096000");
         }

         pos += VarInt.size(suggestionOverridesCount);

         for (int i = 0; i < suggestionOverridesCount; i++) {
            ValidationResult structResult = CommandSuggestionOverride.validateStructure(buffer, pos);
            if (!structResult.isValid()) {
               return ValidationResult.error("Invalid CommandSuggestionOverride in SuggestionOverrides[" + i + "]: " + structResult.error());
            }

            pos += CommandSuggestionOverride.computeBytesConsumed(buffer, pos);
         }
      }

      return ValidationResult.OK;
   }

   public CommandTreeEntry clone() {
      CommandTreeEntry copy = new CommandTreeEntry();
      copy.name = this.name;
      copy.aliases = this.aliases != null ? Arrays.copyOf(this.aliases, this.aliases.length) : null;
      copy.usageText = this.usageText;
      copy.description = this.description;
      copy.variants = this.variants != null ? Arrays.stream(this.variants).map(e -> e.clone()).toArray(CommandVariantEntry[]::new) : null;
      copy.subcommands = this.subcommands != null ? Arrays.stream(this.subcommands).map(e -> e.clone()).toArray(CommandTreeEntry[]::new) : null;
      copy.subcommandHints = this.subcommandHints != null ? Arrays.copyOf(this.subcommandHints, this.subcommandHints.length) : null;
      copy.requiredArgs = this.requiredArgs != null ? Arrays.stream(this.requiredArgs).map(e -> e.clone()).toArray(CommandArgInfo[]::new) : null;
      copy.optionalArgs = this.optionalArgs != null ? Arrays.stream(this.optionalArgs).map(e -> e.clone()).toArray(CommandOptionalArgEntry[]::new) : null;
      copy.suggestionOverrides = this.suggestionOverrides != null
         ? Arrays.stream(this.suggestionOverrides).map(e -> e.clone()).toArray(CommandSuggestionOverride[]::new)
         : null;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof CommandTreeEntry other)
            ? false
            : Objects.equals(this.name, other.name)
               && Arrays.equals(this.aliases, other.aliases)
               && Objects.equals(this.usageText, other.usageText)
               && Objects.equals(this.description, other.description)
               && Arrays.equals(this.variants, other.variants)
               && Arrays.equals(this.subcommands, other.subcommands)
               && Arrays.equals(this.subcommandHints, other.subcommandHints)
               && Arrays.equals(this.requiredArgs, other.requiredArgs)
               && Arrays.equals(this.optionalArgs, other.optionalArgs)
               && Arrays.equals(this.suggestionOverrides, other.suggestionOverrides);
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      result = 31 * result + Objects.hashCode(this.name);
      result = 31 * result + Arrays.hashCode(this.aliases);
      result = 31 * result + Objects.hashCode(this.usageText);
      result = 31 * result + Objects.hashCode(this.description);
      result = 31 * result + Arrays.hashCode(this.variants);
      result = 31 * result + Arrays.hashCode(this.subcommands);
      result = 31 * result + Arrays.hashCode(this.subcommandHints);
      result = 31 * result + Arrays.hashCode(this.requiredArgs);
      result = 31 * result + Arrays.hashCode(this.optionalArgs);
      return 31 * result + Arrays.hashCode(this.suggestionOverrides);
   }
}
