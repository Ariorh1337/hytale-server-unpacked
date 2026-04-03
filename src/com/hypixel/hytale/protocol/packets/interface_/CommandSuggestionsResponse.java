package com.hypixel.hytale.protocol.packets.interface_;

import com.hypixel.hytale.protocol.NetworkChannel;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.ToClientPacket;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CommandSuggestionsResponse implements Packet, ToClientPacket {
   public static final int PACKET_ID = 237;
   public static final boolean IS_COMPRESSED = false;
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 17;
   public static final int VARIABLE_FIELD_COUNT = 8;
   public static final int VARIABLE_BLOCK_START = 49;
   public static final int MAX_SIZE = 1677721600;
   public int startPosition;
   public int length;
   @Nullable
   public String[] suggestions;
   @Nullable
   public boolean[] continuations;
   @Nullable
   public String usageText;
   public int currentArgStart;
   public int currentArgLength;
   @Nullable
   public String[] variantPatterns;
   @Nullable
   public String[] subcommands;
   @Nullable
   public String[] subcommandHints;
   @Nullable
   public String[] optionalArgs;
   @Nullable
   public String[] optionalArgHints;

   @Override
   public int getId() {
      return 237;
   }

   @Override
   public NetworkChannel getChannel() {
      return NetworkChannel.Default;
   }

   public CommandSuggestionsResponse() {
   }

   public CommandSuggestionsResponse(
      int startPosition,
      int length,
      @Nullable String[] suggestions,
      @Nullable boolean[] continuations,
      @Nullable String usageText,
      int currentArgStart,
      int currentArgLength,
      @Nullable String[] variantPatterns,
      @Nullable String[] subcommands,
      @Nullable String[] subcommandHints,
      @Nullable String[] optionalArgs,
      @Nullable String[] optionalArgHints
   ) {
      this.startPosition = startPosition;
      this.length = length;
      this.suggestions = suggestions;
      this.continuations = continuations;
      this.usageText = usageText;
      this.currentArgStart = currentArgStart;
      this.currentArgLength = currentArgLength;
      this.variantPatterns = variantPatterns;
      this.subcommands = subcommands;
      this.subcommandHints = subcommandHints;
      this.optionalArgs = optionalArgs;
      this.optionalArgHints = optionalArgHints;
   }

   public CommandSuggestionsResponse(@Nonnull CommandSuggestionsResponse other) {
      this.startPosition = other.startPosition;
      this.length = other.length;
      this.suggestions = other.suggestions;
      this.continuations = other.continuations;
      this.usageText = other.usageText;
      this.currentArgStart = other.currentArgStart;
      this.currentArgLength = other.currentArgLength;
      this.variantPatterns = other.variantPatterns;
      this.subcommands = other.subcommands;
      this.subcommandHints = other.subcommandHints;
      this.optionalArgs = other.optionalArgs;
      this.optionalArgHints = other.optionalArgHints;
   }

   @Nonnull
   public static CommandSuggestionsResponse deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 49) {
         throw ProtocolException.bufferTooSmall("CommandSuggestionsResponse", 49, buf.readableBytes() - offset);
      }

      CommandSuggestionsResponse obj = new CommandSuggestionsResponse();
      byte nullBits = buf.getByte(offset);
      obj.startPosition = buf.getIntLE(offset + 1);
      obj.length = buf.getIntLE(offset + 5);
      obj.currentArgStart = buf.getIntLE(offset + 9);
      obj.currentArgLength = buf.getIntLE(offset + 13);
      if ((nullBits & 1) != 0) {
         int varPosBase0 = buf.getIntLE(offset + 17);
         if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 49) {
            throw ProtocolException.invalidOffset("Suggestions", varPosBase0, buf.readableBytes());
         }

         int varPos0 = offset + 49 + varPosBase0;
         int suggestionsCount = VarInt.peek(buf, varPos0);
         if (suggestionsCount < 0) {
            throw ProtocolException.invalidVarInt("Suggestions");
         }

         int varIntLen = VarInt.size(suggestionsCount);
         if (suggestionsCount > 4096000) {
            throw ProtocolException.arrayTooLong("Suggestions", suggestionsCount, 4096000);
         }

         if (varPos0 + varIntLen + suggestionsCount * 1L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Suggestions", varPos0 + varIntLen + suggestionsCount * 1, buf.readableBytes());
         }

         obj.suggestions = new String[suggestionsCount];
         int elemPos = varPos0 + varIntLen;

         for (int i = 0; i < suggestionsCount; i++) {
            int strLen = VarInt.peek(buf, elemPos);
            if (strLen < 0) {
               throw ProtocolException.invalidVarInt("suggestions[" + i + "]");
            }

            int strVarLen = VarInt.size(strLen);
            if (strLen > 4096000) {
               throw ProtocolException.stringTooLong("suggestions[" + i + "]", strLen, 4096000);
            }

            if (elemPos + strVarLen + strLen > buf.readableBytes()) {
               throw ProtocolException.bufferTooSmall("suggestions[" + i + "]", elemPos + strVarLen + strLen, buf.readableBytes());
            }

            obj.suggestions[i] = PacketIO.readVarString(buf, elemPos);
            elemPos += strVarLen + strLen;
         }
      }

      if ((nullBits & 2) != 0) {
         int varPosBase1 = buf.getIntLE(offset + 21);
         if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 49) {
            throw ProtocolException.invalidOffset("Continuations", varPosBase1, buf.readableBytes());
         }

         int varPos1 = offset + 49 + varPosBase1;
         int continuationsCount = VarInt.peek(buf, varPos1);
         if (continuationsCount < 0) {
            throw ProtocolException.invalidVarInt("Continuations");
         }

         int varIntLen = VarInt.size(continuationsCount);
         if (continuationsCount > 4096000) {
            throw ProtocolException.arrayTooLong("Continuations", continuationsCount, 4096000);
         }

         if (varPos1 + varIntLen + continuationsCount * 1L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Continuations", varPos1 + varIntLen + continuationsCount * 1, buf.readableBytes());
         }

         obj.continuations = new boolean[continuationsCount];

         for (int i = 0; i < continuationsCount; i++) {
            obj.continuations[i] = buf.getByte(varPos1 + varIntLen + i * 1) != 0;
         }
      }

      if ((nullBits & 4) != 0) {
         int varPosBase2 = buf.getIntLE(offset + 25);
         if (varPosBase2 < 0 || varPosBase2 > buf.writerIndex() - offset - 49) {
            throw ProtocolException.invalidOffset("UsageText", varPosBase2, buf.readableBytes());
         }

         int varPos2 = offset + 49 + varPosBase2;
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

      if ((nullBits & 8) != 0) {
         int varPosBase3 = buf.getIntLE(offset + 29);
         if (varPosBase3 < 0 || varPosBase3 > buf.writerIndex() - offset - 49) {
            throw ProtocolException.invalidOffset("VariantPatterns", varPosBase3, buf.readableBytes());
         }

         int varPos3 = offset + 49 + varPosBase3;
         int variantPatternsCount = VarInt.peek(buf, varPos3);
         if (variantPatternsCount < 0) {
            throw ProtocolException.invalidVarInt("VariantPatterns");
         }

         int varIntLen = VarInt.size(variantPatternsCount);
         if (variantPatternsCount > 4096000) {
            throw ProtocolException.arrayTooLong("VariantPatterns", variantPatternsCount, 4096000);
         }

         if (varPos3 + varIntLen + variantPatternsCount * 1L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("VariantPatterns", varPos3 + varIntLen + variantPatternsCount * 1, buf.readableBytes());
         }

         obj.variantPatterns = new String[variantPatternsCount];
         int elemPos = varPos3 + varIntLen;

         for (int i = 0; i < variantPatternsCount; i++) {
            int strLen = VarInt.peek(buf, elemPos);
            if (strLen < 0) {
               throw ProtocolException.invalidVarInt("variantPatterns[" + i + "]");
            }

            int strVarLen = VarInt.size(strLen);
            if (strLen > 4096000) {
               throw ProtocolException.stringTooLong("variantPatterns[" + i + "]", strLen, 4096000);
            }

            if (elemPos + strVarLen + strLen > buf.readableBytes()) {
               throw ProtocolException.bufferTooSmall("variantPatterns[" + i + "]", elemPos + strVarLen + strLen, buf.readableBytes());
            }

            obj.variantPatterns[i] = PacketIO.readVarString(buf, elemPos);
            elemPos += strVarLen + strLen;
         }
      }

      if ((nullBits & 16) != 0) {
         int varPosBase4 = buf.getIntLE(offset + 33);
         if (varPosBase4 < 0 || varPosBase4 > buf.writerIndex() - offset - 49) {
            throw ProtocolException.invalidOffset("Subcommands", varPosBase4, buf.readableBytes());
         }

         int varPos4 = offset + 49 + varPosBase4;
         int subcommandsCount = VarInt.peek(buf, varPos4);
         if (subcommandsCount < 0) {
            throw ProtocolException.invalidVarInt("Subcommands");
         }

         int varIntLen = VarInt.size(subcommandsCount);
         if (subcommandsCount > 4096000) {
            throw ProtocolException.arrayTooLong("Subcommands", subcommandsCount, 4096000);
         }

         if (varPos4 + varIntLen + subcommandsCount * 1L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Subcommands", varPos4 + varIntLen + subcommandsCount * 1, buf.readableBytes());
         }

         obj.subcommands = new String[subcommandsCount];
         int elemPos = varPos4 + varIntLen;

         for (int i = 0; i < subcommandsCount; i++) {
            int strLen = VarInt.peek(buf, elemPos);
            if (strLen < 0) {
               throw ProtocolException.invalidVarInt("subcommands[" + i + "]");
            }

            int strVarLen = VarInt.size(strLen);
            if (strLen > 4096000) {
               throw ProtocolException.stringTooLong("subcommands[" + i + "]", strLen, 4096000);
            }

            if (elemPos + strVarLen + strLen > buf.readableBytes()) {
               throw ProtocolException.bufferTooSmall("subcommands[" + i + "]", elemPos + strVarLen + strLen, buf.readableBytes());
            }

            obj.subcommands[i] = PacketIO.readVarString(buf, elemPos);
            elemPos += strVarLen + strLen;
         }
      }

      if ((nullBits & 32) != 0) {
         int varPosBase5 = buf.getIntLE(offset + 37);
         if (varPosBase5 < 0 || varPosBase5 > buf.writerIndex() - offset - 49) {
            throw ProtocolException.invalidOffset("SubcommandHints", varPosBase5, buf.readableBytes());
         }

         int varPos5 = offset + 49 + varPosBase5;
         int subcommandHintsCount = VarInt.peek(buf, varPos5);
         if (subcommandHintsCount < 0) {
            throw ProtocolException.invalidVarInt("SubcommandHints");
         }

         int varIntLen = VarInt.size(subcommandHintsCount);
         if (subcommandHintsCount > 4096000) {
            throw ProtocolException.arrayTooLong("SubcommandHints", subcommandHintsCount, 4096000);
         }

         if (varPos5 + varIntLen + subcommandHintsCount * 1L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("SubcommandHints", varPos5 + varIntLen + subcommandHintsCount * 1, buf.readableBytes());
         }

         obj.subcommandHints = new String[subcommandHintsCount];
         int elemPos = varPos5 + varIntLen;

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

      if ((nullBits & 64) != 0) {
         int varPosBase6 = buf.getIntLE(offset + 41);
         if (varPosBase6 < 0 || varPosBase6 > buf.writerIndex() - offset - 49) {
            throw ProtocolException.invalidOffset("OptionalArgs", varPosBase6, buf.readableBytes());
         }

         int varPos6 = offset + 49 + varPosBase6;
         int optionalArgsCount = VarInt.peek(buf, varPos6);
         if (optionalArgsCount < 0) {
            throw ProtocolException.invalidVarInt("OptionalArgs");
         }

         int varIntLen = VarInt.size(optionalArgsCount);
         if (optionalArgsCount > 4096000) {
            throw ProtocolException.arrayTooLong("OptionalArgs", optionalArgsCount, 4096000);
         }

         if (varPos6 + varIntLen + optionalArgsCount * 1L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("OptionalArgs", varPos6 + varIntLen + optionalArgsCount * 1, buf.readableBytes());
         }

         obj.optionalArgs = new String[optionalArgsCount];
         int elemPos = varPos6 + varIntLen;

         for (int i = 0; i < optionalArgsCount; i++) {
            int strLen = VarInt.peek(buf, elemPos);
            if (strLen < 0) {
               throw ProtocolException.invalidVarInt("optionalArgs[" + i + "]");
            }

            int strVarLen = VarInt.size(strLen);
            if (strLen > 4096000) {
               throw ProtocolException.stringTooLong("optionalArgs[" + i + "]", strLen, 4096000);
            }

            if (elemPos + strVarLen + strLen > buf.readableBytes()) {
               throw ProtocolException.bufferTooSmall("optionalArgs[" + i + "]", elemPos + strVarLen + strLen, buf.readableBytes());
            }

            obj.optionalArgs[i] = PacketIO.readVarString(buf, elemPos);
            elemPos += strVarLen + strLen;
         }
      }

      if ((nullBits & 128) != 0) {
         int varPosBase7 = buf.getIntLE(offset + 45);
         if (varPosBase7 < 0 || varPosBase7 > buf.writerIndex() - offset - 49) {
            throw ProtocolException.invalidOffset("OptionalArgHints", varPosBase7, buf.readableBytes());
         }

         int varPos7 = offset + 49 + varPosBase7;
         int optionalArgHintsCount = VarInt.peek(buf, varPos7);
         if (optionalArgHintsCount < 0) {
            throw ProtocolException.invalidVarInt("OptionalArgHints");
         }

         int varIntLen = VarInt.size(optionalArgHintsCount);
         if (optionalArgHintsCount > 4096000) {
            throw ProtocolException.arrayTooLong("OptionalArgHints", optionalArgHintsCount, 4096000);
         }

         if (varPos7 + varIntLen + optionalArgHintsCount * 1L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("OptionalArgHints", varPos7 + varIntLen + optionalArgHintsCount * 1, buf.readableBytes());
         }

         obj.optionalArgHints = new String[optionalArgHintsCount];
         int elemPos = varPos7 + varIntLen;

         for (int i = 0; i < optionalArgHintsCount; i++) {
            int strLen = VarInt.peek(buf, elemPos);
            if (strLen < 0) {
               throw ProtocolException.invalidVarInt("optionalArgHints[" + i + "]");
            }

            int strVarLen = VarInt.size(strLen);
            if (strLen > 4096000) {
               throw ProtocolException.stringTooLong("optionalArgHints[" + i + "]", strLen, 4096000);
            }

            if (elemPos + strVarLen + strLen > buf.readableBytes()) {
               throw ProtocolException.bufferTooSmall("optionalArgHints[" + i + "]", elemPos + strVarLen + strLen, buf.readableBytes());
            }

            obj.optionalArgHints[i] = PacketIO.readVarString(buf, elemPos);
            elemPos += strVarLen + strLen;
         }
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 49;
      if ((nullBits & 1) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 17);
         if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 49) {
            throw ProtocolException.invalidOffset("Suggestions", fieldOffset0, maxEnd);
         }

         int pos0 = offset + 49 + fieldOffset0;
         int arrLen = VarInt.peek(buf, pos0);
         pos0 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            int sl = VarInt.peek(buf, pos0);
            pos0 += VarInt.size(sl) + sl;
         }

         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits & 2) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 21);
         if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 49) {
            throw ProtocolException.invalidOffset("Continuations", fieldOffset1, maxEnd);
         }

         int pos1 = offset + 49 + fieldOffset1;
         int arrLen = VarInt.peek(buf, pos1);
         pos1 += VarInt.size(arrLen) + arrLen * 1;
         if (pos1 - offset > maxEnd) {
            maxEnd = pos1 - offset;
         }
      }

      if ((nullBits & 4) != 0) {
         int fieldOffset2 = buf.getIntLE(offset + 25);
         if (fieldOffset2 < 0 || fieldOffset2 > buf.writerIndex() - offset - 49) {
            throw ProtocolException.invalidOffset("UsageText", fieldOffset2, maxEnd);
         }

         int pos2 = offset + 49 + fieldOffset2;
         int sl = VarInt.peek(buf, pos2);
         pos2 += VarInt.size(sl) + sl;
         if (pos2 - offset > maxEnd) {
            maxEnd = pos2 - offset;
         }
      }

      if ((nullBits & 8) != 0) {
         int fieldOffset3 = buf.getIntLE(offset + 29);
         if (fieldOffset3 < 0 || fieldOffset3 > buf.writerIndex() - offset - 49) {
            throw ProtocolException.invalidOffset("VariantPatterns", fieldOffset3, maxEnd);
         }

         int pos3 = offset + 49 + fieldOffset3;
         int arrLen = VarInt.peek(buf, pos3);
         pos3 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            int sl = VarInt.peek(buf, pos3);
            pos3 += VarInt.size(sl) + sl;
         }

         if (pos3 - offset > maxEnd) {
            maxEnd = pos3 - offset;
         }
      }

      if ((nullBits & 16) != 0) {
         int fieldOffset4 = buf.getIntLE(offset + 33);
         if (fieldOffset4 < 0 || fieldOffset4 > buf.writerIndex() - offset - 49) {
            throw ProtocolException.invalidOffset("Subcommands", fieldOffset4, maxEnd);
         }

         int pos4 = offset + 49 + fieldOffset4;
         int arrLen = VarInt.peek(buf, pos4);
         pos4 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            int sl = VarInt.peek(buf, pos4);
            pos4 += VarInt.size(sl) + sl;
         }

         if (pos4 - offset > maxEnd) {
            maxEnd = pos4 - offset;
         }
      }

      if ((nullBits & 32) != 0) {
         int fieldOffset5 = buf.getIntLE(offset + 37);
         if (fieldOffset5 < 0 || fieldOffset5 > buf.writerIndex() - offset - 49) {
            throw ProtocolException.invalidOffset("SubcommandHints", fieldOffset5, maxEnd);
         }

         int pos5 = offset + 49 + fieldOffset5;
         int arrLen = VarInt.peek(buf, pos5);
         pos5 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            int sl = VarInt.peek(buf, pos5);
            pos5 += VarInt.size(sl) + sl;
         }

         if (pos5 - offset > maxEnd) {
            maxEnd = pos5 - offset;
         }
      }

      if ((nullBits & 64) != 0) {
         int fieldOffset6 = buf.getIntLE(offset + 41);
         if (fieldOffset6 < 0 || fieldOffset6 > buf.writerIndex() - offset - 49) {
            throw ProtocolException.invalidOffset("OptionalArgs", fieldOffset6, maxEnd);
         }

         int pos6 = offset + 49 + fieldOffset6;
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

      if ((nullBits & 128) != 0) {
         int fieldOffset7 = buf.getIntLE(offset + 45);
         if (fieldOffset7 < 0 || fieldOffset7 > buf.writerIndex() - offset - 49) {
            throw ProtocolException.invalidOffset("OptionalArgHints", fieldOffset7, maxEnd);
         }

         int pos7 = offset + 49 + fieldOffset7;
         int arrLen = VarInt.peek(buf, pos7);
         pos7 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            int sl = VarInt.peek(buf, pos7);
            pos7 += VarInt.size(sl) + sl;
         }

         if (pos7 - offset > maxEnd) {
            maxEnd = pos7 - offset;
         }
      }

      return maxEnd;
   }

   @Override
   public void serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      byte nullBits = 0;
      if (this.suggestions != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.continuations != null) {
         nullBits = (byte)(nullBits | 2);
      }

      if (this.usageText != null) {
         nullBits = (byte)(nullBits | 4);
      }

      if (this.variantPatterns != null) {
         nullBits = (byte)(nullBits | 8);
      }

      if (this.subcommands != null) {
         nullBits = (byte)(nullBits | 16);
      }

      if (this.subcommandHints != null) {
         nullBits = (byte)(nullBits | 32);
      }

      if (this.optionalArgs != null) {
         nullBits = (byte)(nullBits | 64);
      }

      if (this.optionalArgHints != null) {
         nullBits = (byte)(nullBits | 128);
      }

      buf.writeByte(nullBits);
      buf.writeIntLE(this.startPosition);
      buf.writeIntLE(this.length);
      buf.writeIntLE(this.currentArgStart);
      buf.writeIntLE(this.currentArgLength);
      int suggestionsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int continuationsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int usageTextOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int variantPatternsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int subcommandsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int subcommandHintsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int optionalArgsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int optionalArgHintsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.suggestions != null) {
         buf.setIntLE(suggestionsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.suggestions.length > 4096000) {
            throw ProtocolException.arrayTooLong("Suggestions", this.suggestions.length, 4096000);
         }

         VarInt.write(buf, this.suggestions.length);

         for (String item : this.suggestions) {
            PacketIO.writeVarString(buf, item, 4096000);
         }
      } else {
         buf.setIntLE(suggestionsOffsetSlot, -1);
      }

      if (this.continuations != null) {
         buf.setIntLE(continuationsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.continuations.length > 4096000) {
            throw ProtocolException.arrayTooLong("Continuations", this.continuations.length, 4096000);
         }

         VarInt.write(buf, this.continuations.length);

         for (boolean item : this.continuations) {
            buf.writeByte(item ? 1 : 0);
         }
      } else {
         buf.setIntLE(continuationsOffsetSlot, -1);
      }

      if (this.usageText != null) {
         buf.setIntLE(usageTextOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.usageText, 4096000);
      } else {
         buf.setIntLE(usageTextOffsetSlot, -1);
      }

      if (this.variantPatterns != null) {
         buf.setIntLE(variantPatternsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.variantPatterns.length > 4096000) {
            throw ProtocolException.arrayTooLong("VariantPatterns", this.variantPatterns.length, 4096000);
         }

         VarInt.write(buf, this.variantPatterns.length);

         for (String item : this.variantPatterns) {
            PacketIO.writeVarString(buf, item, 4096000);
         }
      } else {
         buf.setIntLE(variantPatternsOffsetSlot, -1);
      }

      if (this.subcommands != null) {
         buf.setIntLE(subcommandsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.subcommands.length > 4096000) {
            throw ProtocolException.arrayTooLong("Subcommands", this.subcommands.length, 4096000);
         }

         VarInt.write(buf, this.subcommands.length);

         for (String item : this.subcommands) {
            PacketIO.writeVarString(buf, item, 4096000);
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

      if (this.optionalArgs != null) {
         buf.setIntLE(optionalArgsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.optionalArgs.length > 4096000) {
            throw ProtocolException.arrayTooLong("OptionalArgs", this.optionalArgs.length, 4096000);
         }

         VarInt.write(buf, this.optionalArgs.length);

         for (String item : this.optionalArgs) {
            PacketIO.writeVarString(buf, item, 4096000);
         }
      } else {
         buf.setIntLE(optionalArgsOffsetSlot, -1);
      }

      if (this.optionalArgHints != null) {
         buf.setIntLE(optionalArgHintsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.optionalArgHints.length > 4096000) {
            throw ProtocolException.arrayTooLong("OptionalArgHints", this.optionalArgHints.length, 4096000);
         }

         VarInt.write(buf, this.optionalArgHints.length);

         for (String item : this.optionalArgHints) {
            PacketIO.writeVarString(buf, item, 4096000);
         }
      } else {
         buf.setIntLE(optionalArgHintsOffsetSlot, -1);
      }
   }

   @Override
   public int computeSize() {
      int size = 49;
      if (this.suggestions != null) {
         int suggestionsSize = 0;

         for (String elem : this.suggestions) {
            suggestionsSize += PacketIO.stringSize(elem);
         }

         size += VarInt.size(this.suggestions.length) + suggestionsSize;
      }

      if (this.continuations != null) {
         size += VarInt.size(this.continuations.length) + this.continuations.length * 1;
      }

      if (this.usageText != null) {
         size += PacketIO.stringSize(this.usageText);
      }

      if (this.variantPatterns != null) {
         int variantPatternsSize = 0;

         for (String elem : this.variantPatterns) {
            variantPatternsSize += PacketIO.stringSize(elem);
         }

         size += VarInt.size(this.variantPatterns.length) + variantPatternsSize;
      }

      if (this.subcommands != null) {
         int subcommandsSize = 0;

         for (String elem : this.subcommands) {
            subcommandsSize += PacketIO.stringSize(elem);
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

      if (this.optionalArgs != null) {
         int optionalArgsSize = 0;

         for (String elem : this.optionalArgs) {
            optionalArgsSize += PacketIO.stringSize(elem);
         }

         size += VarInt.size(this.optionalArgs.length) + optionalArgsSize;
      }

      if (this.optionalArgHints != null) {
         int optionalArgHintsSize = 0;

         for (String elem : this.optionalArgHints) {
            optionalArgHintsSize += PacketIO.stringSize(elem);
         }

         size += VarInt.size(this.optionalArgHints.length) + optionalArgHintsSize;
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 49) {
         return ValidationResult.error("Buffer too small: expected at least 49 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      if ((nullBits & 1) != 0) {
         int suggestionsOffset = buffer.getIntLE(offset + 17);
         if (suggestionsOffset < 0 || suggestionsOffset > buffer.writerIndex() - offset - 49) {
            return ValidationResult.error("Invalid offset for Suggestions");
         }

         int pos = offset + 49 + suggestionsOffset;
         int suggestionsCount = VarInt.peek(buffer, pos);
         if (suggestionsCount < 0) {
            return ValidationResult.error("Invalid array count for Suggestions");
         }

         if (suggestionsCount > 4096000) {
            return ValidationResult.error("Suggestions exceeds max length 4096000");
         }

         pos += VarInt.size(suggestionsCount);

         for (int i = 0; i < suggestionsCount; i++) {
            int strLen = VarInt.peek(buffer, pos);
            if (strLen < 0) {
               return ValidationResult.error("Invalid string length in Suggestions");
            }

            pos += VarInt.size(strLen);
            pos += strLen;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading string in Suggestions");
            }
         }
      }

      if ((nullBits & 2) != 0) {
         int continuationsOffset = buffer.getIntLE(offset + 21);
         if (continuationsOffset < 0 || continuationsOffset > buffer.writerIndex() - offset - 49) {
            return ValidationResult.error("Invalid offset for Continuations");
         }

         int pos = offset + 49 + continuationsOffset;
         int continuationsCount = VarInt.peek(buffer, pos);
         if (continuationsCount < 0) {
            return ValidationResult.error("Invalid array count for Continuations");
         }

         if (continuationsCount > 4096000) {
            return ValidationResult.error("Continuations exceeds max length 4096000");
         }

         pos += VarInt.size(continuationsCount);
         pos += continuationsCount * 1;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Continuations");
         }
      }

      if ((nullBits & 4) != 0) {
         int usageTextOffset = buffer.getIntLE(offset + 25);
         if (usageTextOffset < 0 || usageTextOffset > buffer.writerIndex() - offset - 49) {
            return ValidationResult.error("Invalid offset for UsageText");
         }

         int pos = offset + 49 + usageTextOffset;
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

      if ((nullBits & 8) != 0) {
         int variantPatternsOffset = buffer.getIntLE(offset + 29);
         if (variantPatternsOffset < 0 || variantPatternsOffset > buffer.writerIndex() - offset - 49) {
            return ValidationResult.error("Invalid offset for VariantPatterns");
         }

         int pos = offset + 49 + variantPatternsOffset;
         int variantPatternsCount = VarInt.peek(buffer, pos);
         if (variantPatternsCount < 0) {
            return ValidationResult.error("Invalid array count for VariantPatterns");
         }

         if (variantPatternsCount > 4096000) {
            return ValidationResult.error("VariantPatterns exceeds max length 4096000");
         }

         pos += VarInt.size(variantPatternsCount);

         for (int i = 0; i < variantPatternsCount; i++) {
            int strLen = VarInt.peek(buffer, pos);
            if (strLen < 0) {
               return ValidationResult.error("Invalid string length in VariantPatterns");
            }

            pos += VarInt.size(strLen);
            pos += strLen;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading string in VariantPatterns");
            }
         }
      }

      if ((nullBits & 16) != 0) {
         int subcommandsOffset = buffer.getIntLE(offset + 33);
         if (subcommandsOffset < 0 || subcommandsOffset > buffer.writerIndex() - offset - 49) {
            return ValidationResult.error("Invalid offset for Subcommands");
         }

         int pos = offset + 49 + subcommandsOffset;
         int subcommandsCount = VarInt.peek(buffer, pos);
         if (subcommandsCount < 0) {
            return ValidationResult.error("Invalid array count for Subcommands");
         }

         if (subcommandsCount > 4096000) {
            return ValidationResult.error("Subcommands exceeds max length 4096000");
         }

         pos += VarInt.size(subcommandsCount);

         for (int i = 0; i < subcommandsCount; i++) {
            int strLen = VarInt.peek(buffer, pos);
            if (strLen < 0) {
               return ValidationResult.error("Invalid string length in Subcommands");
            }

            pos += VarInt.size(strLen);
            pos += strLen;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading string in Subcommands");
            }
         }
      }

      if ((nullBits & 32) != 0) {
         int subcommandHintsOffset = buffer.getIntLE(offset + 37);
         if (subcommandHintsOffset < 0 || subcommandHintsOffset > buffer.writerIndex() - offset - 49) {
            return ValidationResult.error("Invalid offset for SubcommandHints");
         }

         int pos = offset + 49 + subcommandHintsOffset;
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

      if ((nullBits & 64) != 0) {
         int optionalArgsOffset = buffer.getIntLE(offset + 41);
         if (optionalArgsOffset < 0 || optionalArgsOffset > buffer.writerIndex() - offset - 49) {
            return ValidationResult.error("Invalid offset for OptionalArgs");
         }

         int pos = offset + 49 + optionalArgsOffset;
         int optionalArgsCount = VarInt.peek(buffer, pos);
         if (optionalArgsCount < 0) {
            return ValidationResult.error("Invalid array count for OptionalArgs");
         }

         if (optionalArgsCount > 4096000) {
            return ValidationResult.error("OptionalArgs exceeds max length 4096000");
         }

         pos += VarInt.size(optionalArgsCount);

         for (int i = 0; i < optionalArgsCount; i++) {
            int strLen = VarInt.peek(buffer, pos);
            if (strLen < 0) {
               return ValidationResult.error("Invalid string length in OptionalArgs");
            }

            pos += VarInt.size(strLen);
            pos += strLen;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading string in OptionalArgs");
            }
         }
      }

      if ((nullBits & 128) != 0) {
         int optionalArgHintsOffset = buffer.getIntLE(offset + 45);
         if (optionalArgHintsOffset < 0 || optionalArgHintsOffset > buffer.writerIndex() - offset - 49) {
            return ValidationResult.error("Invalid offset for OptionalArgHints");
         }

         int pos = offset + 49 + optionalArgHintsOffset;
         int optionalArgHintsCount = VarInt.peek(buffer, pos);
         if (optionalArgHintsCount < 0) {
            return ValidationResult.error("Invalid array count for OptionalArgHints");
         }

         if (optionalArgHintsCount > 4096000) {
            return ValidationResult.error("OptionalArgHints exceeds max length 4096000");
         }

         pos += VarInt.size(optionalArgHintsCount);

         for (int i = 0; i < optionalArgHintsCount; i++) {
            int strLen = VarInt.peek(buffer, pos);
            if (strLen < 0) {
               return ValidationResult.error("Invalid string length in OptionalArgHints");
            }

            pos += VarInt.size(strLen);
            pos += strLen;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading string in OptionalArgHints");
            }
         }
      }

      return ValidationResult.OK;
   }

   public CommandSuggestionsResponse clone() {
      CommandSuggestionsResponse copy = new CommandSuggestionsResponse();
      copy.startPosition = this.startPosition;
      copy.length = this.length;
      copy.suggestions = this.suggestions != null ? Arrays.copyOf(this.suggestions, this.suggestions.length) : null;
      copy.continuations = this.continuations != null ? Arrays.copyOf(this.continuations, this.continuations.length) : null;
      copy.usageText = this.usageText;
      copy.currentArgStart = this.currentArgStart;
      copy.currentArgLength = this.currentArgLength;
      copy.variantPatterns = this.variantPatterns != null ? Arrays.copyOf(this.variantPatterns, this.variantPatterns.length) : null;
      copy.subcommands = this.subcommands != null ? Arrays.copyOf(this.subcommands, this.subcommands.length) : null;
      copy.subcommandHints = this.subcommandHints != null ? Arrays.copyOf(this.subcommandHints, this.subcommandHints.length) : null;
      copy.optionalArgs = this.optionalArgs != null ? Arrays.copyOf(this.optionalArgs, this.optionalArgs.length) : null;
      copy.optionalArgHints = this.optionalArgHints != null ? Arrays.copyOf(this.optionalArgHints, this.optionalArgHints.length) : null;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof CommandSuggestionsResponse other)
            ? false
            : this.startPosition == other.startPosition
               && this.length == other.length
               && Arrays.equals(this.suggestions, other.suggestions)
               && Arrays.equals(this.continuations, other.continuations)
               && Objects.equals(this.usageText, other.usageText)
               && this.currentArgStart == other.currentArgStart
               && this.currentArgLength == other.currentArgLength
               && Arrays.equals(this.variantPatterns, other.variantPatterns)
               && Arrays.equals(this.subcommands, other.subcommands)
               && Arrays.equals(this.subcommandHints, other.subcommandHints)
               && Arrays.equals(this.optionalArgs, other.optionalArgs)
               && Arrays.equals(this.optionalArgHints, other.optionalArgHints);
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      result = 31 * result + Integer.hashCode(this.startPosition);
      result = 31 * result + Integer.hashCode(this.length);
      result = 31 * result + Arrays.hashCode(this.suggestions);
      result = 31 * result + Arrays.hashCode(this.continuations);
      result = 31 * result + Objects.hashCode(this.usageText);
      result = 31 * result + Integer.hashCode(this.currentArgStart);
      result = 31 * result + Integer.hashCode(this.currentArgLength);
      result = 31 * result + Arrays.hashCode(this.variantPatterns);
      result = 31 * result + Arrays.hashCode(this.subcommands);
      result = 31 * result + Arrays.hashCode(this.subcommandHints);
      result = 31 * result + Arrays.hashCode(this.optionalArgs);
      return 31 * result + Arrays.hashCode(this.optionalArgHints);
   }
}
