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

public class ReplaceInteraction extends Interaction {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 15;
   public static final int VARIABLE_FIELD_COUNT = 6;
   public static final int VARIABLE_BLOCK_START = 39;
   public static final int MAX_SIZE = 1677721600;
   public int defaultValue;
   @Nullable
   public String variable;

   public ReplaceInteraction() {
   }

   public ReplaceInteraction(
      @Nonnull WaitForDataFrom waitForDataFrom,
      @Nullable InteractionEffects effects,
      float horizontalSpeedMultiplier,
      float runTime,
      boolean cancelOnItemChange,
      @Nullable Map<GameMode, InteractionSettings> settings,
      @Nullable InteractionRules rules,
      @Nullable int[] tags,
      @Nullable InteractionCameraSettings camera,
      int defaultValue,
      @Nullable String variable
   ) {
      this.waitForDataFrom = waitForDataFrom;
      this.effects = effects;
      this.horizontalSpeedMultiplier = horizontalSpeedMultiplier;
      this.runTime = runTime;
      this.cancelOnItemChange = cancelOnItemChange;
      this.settings = settings;
      this.rules = rules;
      this.tags = tags;
      this.camera = camera;
      this.defaultValue = defaultValue;
      this.variable = variable;
   }

   public ReplaceInteraction(@Nonnull ReplaceInteraction other) {
      this.waitForDataFrom = other.waitForDataFrom;
      this.effects = other.effects;
      this.horizontalSpeedMultiplier = other.horizontalSpeedMultiplier;
      this.runTime = other.runTime;
      this.cancelOnItemChange = other.cancelOnItemChange;
      this.settings = other.settings;
      this.rules = other.rules;
      this.tags = other.tags;
      this.camera = other.camera;
      this.defaultValue = other.defaultValue;
      this.variable = other.variable;
   }

   @Nonnull
   public static ReplaceInteraction deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 39) {
         throw ProtocolException.bufferTooSmall("ReplaceInteraction", 39, buf.readableBytes() - offset);
      }

      ReplaceInteraction obj = new ReplaceInteraction();
      byte nullBits = buf.getByte(offset);
      obj.waitForDataFrom = WaitForDataFrom.fromValue(buf.getByte(offset + 1));
      obj.horizontalSpeedMultiplier = buf.getFloatLE(offset + 2);
      obj.runTime = buf.getFloatLE(offset + 6);
      obj.cancelOnItemChange = buf.getByte(offset + 10) != 0;
      obj.defaultValue = buf.getIntLE(offset + 11);
      if ((nullBits & 1) != 0) {
         int varPosBase0 = buf.getIntLE(offset + 15);
         if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 39) {
            throw ProtocolException.invalidOffset("Effects", varPosBase0, buf.readableBytes());
         }

         int varPos0 = offset + 39 + varPosBase0;
         obj.effects = InteractionEffects.deserialize(buf, varPos0);
      }

      if ((nullBits & 2) != 0) {
         int varPosBase1 = buf.getIntLE(offset + 19);
         if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 39) {
            throw ProtocolException.invalidOffset("Settings", varPosBase1, buf.readableBytes());
         }

         int varPos1 = offset + 39 + varPosBase1;
         int settingsCount = VarInt.peek(buf, varPos1);
         if (settingsCount < 0) {
            throw ProtocolException.invalidVarInt("Settings");
         }

         int varIntLen = VarInt.size(settingsCount);
         if (settingsCount > 4096000) {
            throw ProtocolException.dictionaryTooLarge("Settings", settingsCount, 4096000);
         }

         obj.settings = new HashMap<>(settingsCount);
         int dictPos = varPos1 + varIntLen;

         for (int i = 0; i < settingsCount; i++) {
            GameMode key = GameMode.fromValue(buf.getByte(dictPos));
            InteractionSettings val = InteractionSettings.deserialize(buf, ++dictPos);
            dictPos += InteractionSettings.computeBytesConsumed(buf, dictPos);
            if (obj.settings.put(key, val) != null) {
               throw ProtocolException.duplicateKey("settings", key);
            }
         }
      }

      if ((nullBits & 4) != 0) {
         int varPosBase2 = buf.getIntLE(offset + 23);
         if (varPosBase2 < 0 || varPosBase2 > buf.writerIndex() - offset - 39) {
            throw ProtocolException.invalidOffset("Rules", varPosBase2, buf.readableBytes());
         }

         int varPos2 = offset + 39 + varPosBase2;
         obj.rules = InteractionRules.deserialize(buf, varPos2);
      }

      if ((nullBits & 8) != 0) {
         int varPosBase3 = buf.getIntLE(offset + 27);
         if (varPosBase3 < 0 || varPosBase3 > buf.writerIndex() - offset - 39) {
            throw ProtocolException.invalidOffset("Tags", varPosBase3, buf.readableBytes());
         }

         int varPos3 = offset + 39 + varPosBase3;
         int tagsCount = VarInt.peek(buf, varPos3);
         if (tagsCount < 0) {
            throw ProtocolException.invalidVarInt("Tags");
         }

         int varIntLen = VarInt.size(tagsCount);
         if (tagsCount > 4096000) {
            throw ProtocolException.arrayTooLong("Tags", tagsCount, 4096000);
         }

         if (varPos3 + varIntLen + tagsCount * 4L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Tags", varPos3 + varIntLen + tagsCount * 4, buf.readableBytes());
         }

         obj.tags = new int[tagsCount];

         for (int i = 0; i < tagsCount; i++) {
            obj.tags[i] = buf.getIntLE(varPos3 + varIntLen + i * 4);
         }
      }

      if ((nullBits & 16) != 0) {
         int varPosBase4 = buf.getIntLE(offset + 31);
         if (varPosBase4 < 0 || varPosBase4 > buf.writerIndex() - offset - 39) {
            throw ProtocolException.invalidOffset("Camera", varPosBase4, buf.readableBytes());
         }

         int varPos4 = offset + 39 + varPosBase4;
         obj.camera = InteractionCameraSettings.deserialize(buf, varPos4);
      }

      if ((nullBits & 32) != 0) {
         int varPosBase5 = buf.getIntLE(offset + 35);
         if (varPosBase5 < 0 || varPosBase5 > buf.writerIndex() - offset - 39) {
            throw ProtocolException.invalidOffset("Variable", varPosBase5, buf.readableBytes());
         }

         int varPos5 = offset + 39 + varPosBase5;
         int variableLen = VarInt.peek(buf, varPos5);
         if (variableLen < 0) {
            throw ProtocolException.invalidVarInt("Variable");
         }

         int variableVarIntLen = VarInt.size(variableLen);
         if (variableLen > 4096000) {
            throw ProtocolException.stringTooLong("Variable", variableLen, 4096000);
         }

         if (varPos5 + variableVarIntLen + variableLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Variable", varPos5 + variableVarIntLen + variableLen, buf.readableBytes());
         }

         obj.variable = PacketIO.readVarString(buf, varPos5, PacketIO.UTF8);
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 39;
      if ((nullBits & 1) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 15);
         if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 39) {
            throw ProtocolException.invalidOffset("Effects", fieldOffset0, maxEnd);
         }

         int pos0 = offset + 39 + fieldOffset0;
         pos0 += InteractionEffects.computeBytesConsumed(buf, pos0);
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits & 2) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 19);
         if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 39) {
            throw ProtocolException.invalidOffset("Settings", fieldOffset1, maxEnd);
         }

         int pos1 = offset + 39 + fieldOffset1;
         int dictLen = VarInt.peek(buf, pos1);
         pos1 += VarInt.size(dictLen);

         for (int i = 0; i < dictLen; i++) {
            pos1 = ++pos1 + InteractionSettings.computeBytesConsumed(buf, pos1);
         }

         if (pos1 - offset > maxEnd) {
            maxEnd = pos1 - offset;
         }
      }

      if ((nullBits & 4) != 0) {
         int fieldOffset2 = buf.getIntLE(offset + 23);
         if (fieldOffset2 < 0 || fieldOffset2 > buf.writerIndex() - offset - 39) {
            throw ProtocolException.invalidOffset("Rules", fieldOffset2, maxEnd);
         }

         int pos2 = offset + 39 + fieldOffset2;
         pos2 += InteractionRules.computeBytesConsumed(buf, pos2);
         if (pos2 - offset > maxEnd) {
            maxEnd = pos2 - offset;
         }
      }

      if ((nullBits & 8) != 0) {
         int fieldOffset3 = buf.getIntLE(offset + 27);
         if (fieldOffset3 < 0 || fieldOffset3 > buf.writerIndex() - offset - 39) {
            throw ProtocolException.invalidOffset("Tags", fieldOffset3, maxEnd);
         }

         int pos3 = offset + 39 + fieldOffset3;
         int arrLen = VarInt.peek(buf, pos3);
         pos3 += VarInt.size(arrLen) + arrLen * 4;
         if (pos3 - offset > maxEnd) {
            maxEnd = pos3 - offset;
         }
      }

      if ((nullBits & 16) != 0) {
         int fieldOffset4 = buf.getIntLE(offset + 31);
         if (fieldOffset4 < 0 || fieldOffset4 > buf.writerIndex() - offset - 39) {
            throw ProtocolException.invalidOffset("Camera", fieldOffset4, maxEnd);
         }

         int pos4 = offset + 39 + fieldOffset4;
         pos4 += InteractionCameraSettings.computeBytesConsumed(buf, pos4);
         if (pos4 - offset > maxEnd) {
            maxEnd = pos4 - offset;
         }
      }

      if ((nullBits & 32) != 0) {
         int fieldOffset5 = buf.getIntLE(offset + 35);
         if (fieldOffset5 < 0 || fieldOffset5 > buf.writerIndex() - offset - 39) {
            throw ProtocolException.invalidOffset("Variable", fieldOffset5, maxEnd);
         }

         int pos5 = offset + 39 + fieldOffset5;
         int sl = VarInt.peek(buf, pos5);
         pos5 += VarInt.size(sl) + sl;
         if (pos5 - offset > maxEnd) {
            maxEnd = pos5 - offset;
         }
      }

      return maxEnd;
   }

   @Override
   public int serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      byte nullBits = 0;
      if (this.effects != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.settings != null) {
         nullBits = (byte)(nullBits | 2);
      }

      if (this.rules != null) {
         nullBits = (byte)(nullBits | 4);
      }

      if (this.tags != null) {
         nullBits = (byte)(nullBits | 8);
      }

      if (this.camera != null) {
         nullBits = (byte)(nullBits | 16);
      }

      if (this.variable != null) {
         nullBits = (byte)(nullBits | 32);
      }

      buf.writeByte(nullBits);
      buf.writeByte(this.waitForDataFrom.getValue());
      buf.writeFloatLE(this.horizontalSpeedMultiplier);
      buf.writeFloatLE(this.runTime);
      buf.writeByte(this.cancelOnItemChange ? 1 : 0);
      buf.writeIntLE(this.defaultValue);
      int effectsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int settingsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int rulesOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int tagsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int cameraOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int variableOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.effects != null) {
         buf.setIntLE(effectsOffsetSlot, buf.writerIndex() - varBlockStart);
         this.effects.serialize(buf);
      } else {
         buf.setIntLE(effectsOffsetSlot, -1);
      }

      if (this.settings != null) {
         buf.setIntLE(settingsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.settings.size() > 4096000) {
            throw ProtocolException.dictionaryTooLarge("Settings", this.settings.size(), 4096000);
         }

         VarInt.write(buf, this.settings.size());

         for (Entry<GameMode, InteractionSettings> e : this.settings.entrySet()) {
            buf.writeByte(e.getKey().getValue());
            e.getValue().serialize(buf);
         }
      } else {
         buf.setIntLE(settingsOffsetSlot, -1);
      }

      if (this.rules != null) {
         buf.setIntLE(rulesOffsetSlot, buf.writerIndex() - varBlockStart);
         this.rules.serialize(buf);
      } else {
         buf.setIntLE(rulesOffsetSlot, -1);
      }

      if (this.tags != null) {
         buf.setIntLE(tagsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.tags.length > 4096000) {
            throw ProtocolException.arrayTooLong("Tags", this.tags.length, 4096000);
         }

         VarInt.write(buf, this.tags.length);

         for (int item : this.tags) {
            buf.writeIntLE(item);
         }
      } else {
         buf.setIntLE(tagsOffsetSlot, -1);
      }

      if (this.camera != null) {
         buf.setIntLE(cameraOffsetSlot, buf.writerIndex() - varBlockStart);
         this.camera.serialize(buf);
      } else {
         buf.setIntLE(cameraOffsetSlot, -1);
      }

      if (this.variable != null) {
         buf.setIntLE(variableOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.variable, 4096000);
      } else {
         buf.setIntLE(variableOffsetSlot, -1);
      }

      return buf.writerIndex() - startPos;
   }

   @Override
   public int computeSize() {
      int size = 39;
      if (this.effects != null) {
         size += this.effects.computeSize();
      }

      if (this.settings != null) {
         size += VarInt.size(this.settings.size()) + this.settings.size() * 2;
      }

      if (this.rules != null) {
         size += this.rules.computeSize();
      }

      if (this.tags != null) {
         size += VarInt.size(this.tags.length) + this.tags.length * 4;
      }

      if (this.camera != null) {
         size += this.camera.computeSize();
      }

      if (this.variable != null) {
         size += PacketIO.stringSize(this.variable);
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 39) {
         return ValidationResult.error("Buffer too small: expected at least 39 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      int v = buffer.getByte(offset + 1) & 255;
      if (v >= 3) {
         return ValidationResult.error("Invalid WaitForDataFrom value for WaitForDataFrom");
      }

      if ((nullBits & 1) != 0) {
         v = buffer.getIntLE(offset + 15);
         if (v < 0 || v > buffer.writerIndex() - offset - 39) {
            return ValidationResult.error("Invalid offset for Effects");
         }

         int pos = offset + 39 + v;
         ValidationResult effectsResult = InteractionEffects.validateStructure(buffer, pos);
         if (!effectsResult.isValid()) {
            return ValidationResult.error("Invalid Effects: " + effectsResult.error());
         }

         pos += InteractionEffects.computeBytesConsumed(buffer, pos);
      }

      if ((nullBits & 2) != 0) {
         v = buffer.getIntLE(offset + 19);
         if (v < 0 || v > buffer.writerIndex() - offset - 39) {
            return ValidationResult.error("Invalid offset for Settings");
         }

         int pos = offset + 39 + v;
         int settingsCount = VarInt.peek(buffer, pos);
         if (settingsCount < 0) {
            return ValidationResult.error("Invalid dictionary count for Settings");
         }

         if (settingsCount > 4096000) {
            return ValidationResult.error("Settings exceeds max length 4096000");
         }

         pos += VarInt.size(settingsCount);

         for (int i = 0; i < settingsCount; i++) {
            int vx = buffer.getByte(pos) & 255;
            if (vx >= 2) {
               return ValidationResult.error("Invalid GameMode value for key");
            }

            pos++;
            pos++;
         }
      }

      if ((nullBits & 4) != 0) {
         v = buffer.getIntLE(offset + 23);
         if (v < 0 || v > buffer.writerIndex() - offset - 39) {
            return ValidationResult.error("Invalid offset for Rules");
         }

         int pos = offset + 39 + v;
         ValidationResult rulesResult = InteractionRules.validateStructure(buffer, pos);
         if (!rulesResult.isValid()) {
            return ValidationResult.error("Invalid Rules: " + rulesResult.error());
         }

         pos += InteractionRules.computeBytesConsumed(buffer, pos);
      }

      if ((nullBits & 8) != 0) {
         v = buffer.getIntLE(offset + 27);
         if (v < 0 || v > buffer.writerIndex() - offset - 39) {
            return ValidationResult.error("Invalid offset for Tags");
         }

         int pos = offset + 39 + v;
         int tagsCount = VarInt.peek(buffer, pos);
         if (tagsCount < 0) {
            return ValidationResult.error("Invalid array count for Tags");
         }

         if (tagsCount > 4096000) {
            return ValidationResult.error("Tags exceeds max length 4096000");
         }

         pos += VarInt.size(tagsCount);
         pos += tagsCount * 4;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Tags");
         }
      }

      if ((nullBits & 16) != 0) {
         v = buffer.getIntLE(offset + 31);
         if (v < 0 || v > buffer.writerIndex() - offset - 39) {
            return ValidationResult.error("Invalid offset for Camera");
         }

         int pos = offset + 39 + v;
         ValidationResult cameraResult = InteractionCameraSettings.validateStructure(buffer, pos);
         if (!cameraResult.isValid()) {
            return ValidationResult.error("Invalid Camera: " + cameraResult.error());
         }

         pos += InteractionCameraSettings.computeBytesConsumed(buffer, pos);
      }

      if ((nullBits & 32) != 0) {
         v = buffer.getIntLE(offset + 35);
         if (v < 0 || v > buffer.writerIndex() - offset - 39) {
            return ValidationResult.error("Invalid offset for Variable");
         }

         int pos = offset + 39 + v;
         int variableLen = VarInt.peek(buffer, pos);
         if (variableLen < 0) {
            return ValidationResult.error("Invalid string length for Variable");
         }

         if (variableLen > 4096000) {
            return ValidationResult.error("Variable exceeds max length 4096000");
         }

         pos += VarInt.size(variableLen);
         pos += variableLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Variable");
         }
      }

      return ValidationResult.OK;
   }

   public ReplaceInteraction clone() {
      ReplaceInteraction copy = new ReplaceInteraction();
      copy.waitForDataFrom = this.waitForDataFrom;
      copy.effects = this.effects != null ? this.effects.clone() : null;
      copy.horizontalSpeedMultiplier = this.horizontalSpeedMultiplier;
      copy.runTime = this.runTime;
      copy.cancelOnItemChange = this.cancelOnItemChange;
      if (this.settings != null) {
         Map<GameMode, InteractionSettings> m = new HashMap<>();

         for (Entry<GameMode, InteractionSettings> e : this.settings.entrySet()) {
            m.put(e.getKey(), e.getValue().clone());
         }

         copy.settings = m;
      }

      copy.rules = this.rules != null ? this.rules.clone() : null;
      copy.tags = this.tags != null ? Arrays.copyOf(this.tags, this.tags.length) : null;
      copy.camera = this.camera != null ? this.camera.clone() : null;
      copy.defaultValue = this.defaultValue;
      copy.variable = this.variable;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof ReplaceInteraction other)
            ? false
            : Objects.equals(this.waitForDataFrom, other.waitForDataFrom)
               && Objects.equals(this.effects, other.effects)
               && this.horizontalSpeedMultiplier == other.horizontalSpeedMultiplier
               && this.runTime == other.runTime
               && this.cancelOnItemChange == other.cancelOnItemChange
               && Objects.equals(this.settings, other.settings)
               && Objects.equals(this.rules, other.rules)
               && Arrays.equals(this.tags, other.tags)
               && Objects.equals(this.camera, other.camera)
               && this.defaultValue == other.defaultValue
               && Objects.equals(this.variable, other.variable);
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      result = 31 * result + Objects.hashCode(this.waitForDataFrom);
      result = 31 * result + Objects.hashCode(this.effects);
      result = 31 * result + Float.hashCode(this.horizontalSpeedMultiplier);
      result = 31 * result + Float.hashCode(this.runTime);
      result = 31 * result + Boolean.hashCode(this.cancelOnItemChange);
      result = 31 * result + Objects.hashCode(this.settings);
      result = 31 * result + Objects.hashCode(this.rules);
      result = 31 * result + Arrays.hashCode(this.tags);
      result = 31 * result + Objects.hashCode(this.camera);
      result = 31 * result + Integer.hashCode(this.defaultValue);
      return 31 * result + Objects.hashCode(this.variable);
   }
}
