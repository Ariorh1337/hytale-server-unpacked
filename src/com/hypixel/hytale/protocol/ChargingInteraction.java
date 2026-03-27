package com.hypixel.hytale.protocol;

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

public class ChargingInteraction extends Interaction {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 47;
   public static final int VARIABLE_FIELD_COUNT = 7;
   public static final int VARIABLE_BLOCK_START = 75;
   public static final int MAX_SIZE = 1677721600;
   public int failed = Integer.MIN_VALUE;
   public boolean allowIndefiniteHold;
   public boolean displayProgress;
   public boolean cancelOnOtherClick;
   public boolean failOnDamage;
   public float mouseSensitivityAdjustmentTarget;
   public float mouseSensitivityAdjustmentDuration;
   @Nullable
   public Map<Float, Integer> chargedNext;
   @Nullable
   public Map<InteractionType, Integer> forks;
   @Nullable
   public ChargingDelay chargingDelay;

   public ChargingInteraction() {
   }

   public ChargingInteraction(
      @Nonnull WaitForDataFrom waitForDataFrom,
      @Nullable InteractionEffects effects,
      float horizontalSpeedMultiplier,
      float runTime,
      boolean cancelOnItemChange,
      @Nullable Map<GameMode, InteractionSettings> settings,
      @Nullable InteractionRules rules,
      @Nullable int[] tags,
      @Nullable InteractionCameraSettings camera,
      int failed,
      boolean allowIndefiniteHold,
      boolean displayProgress,
      boolean cancelOnOtherClick,
      boolean failOnDamage,
      float mouseSensitivityAdjustmentTarget,
      float mouseSensitivityAdjustmentDuration,
      @Nullable Map<Float, Integer> chargedNext,
      @Nullable Map<InteractionType, Integer> forks,
      @Nullable ChargingDelay chargingDelay
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
      this.failed = failed;
      this.allowIndefiniteHold = allowIndefiniteHold;
      this.displayProgress = displayProgress;
      this.cancelOnOtherClick = cancelOnOtherClick;
      this.failOnDamage = failOnDamage;
      this.mouseSensitivityAdjustmentTarget = mouseSensitivityAdjustmentTarget;
      this.mouseSensitivityAdjustmentDuration = mouseSensitivityAdjustmentDuration;
      this.chargedNext = chargedNext;
      this.forks = forks;
      this.chargingDelay = chargingDelay;
   }

   public ChargingInteraction(@Nonnull ChargingInteraction other) {
      this.waitForDataFrom = other.waitForDataFrom;
      this.effects = other.effects;
      this.horizontalSpeedMultiplier = other.horizontalSpeedMultiplier;
      this.runTime = other.runTime;
      this.cancelOnItemChange = other.cancelOnItemChange;
      this.settings = other.settings;
      this.rules = other.rules;
      this.tags = other.tags;
      this.camera = other.camera;
      this.failed = other.failed;
      this.allowIndefiniteHold = other.allowIndefiniteHold;
      this.displayProgress = other.displayProgress;
      this.cancelOnOtherClick = other.cancelOnOtherClick;
      this.failOnDamage = other.failOnDamage;
      this.mouseSensitivityAdjustmentTarget = other.mouseSensitivityAdjustmentTarget;
      this.mouseSensitivityAdjustmentDuration = other.mouseSensitivityAdjustmentDuration;
      this.chargedNext = other.chargedNext;
      this.forks = other.forks;
      this.chargingDelay = other.chargingDelay;
   }

   @Nonnull
   public static ChargingInteraction deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 75) {
         throw ProtocolException.bufferTooSmall("ChargingInteraction", 75, buf.readableBytes() - offset);
      }

      ChargingInteraction obj = new ChargingInteraction();
      byte nullBits = buf.getByte(offset);
      obj.waitForDataFrom = WaitForDataFrom.fromValue(buf.getByte(offset + 1));
      obj.horizontalSpeedMultiplier = buf.getFloatLE(offset + 2);
      obj.runTime = buf.getFloatLE(offset + 6);
      obj.cancelOnItemChange = buf.getByte(offset + 10) != 0;
      obj.failed = buf.getIntLE(offset + 11);
      obj.allowIndefiniteHold = buf.getByte(offset + 15) != 0;
      obj.displayProgress = buf.getByte(offset + 16) != 0;
      obj.cancelOnOtherClick = buf.getByte(offset + 17) != 0;
      obj.failOnDamage = buf.getByte(offset + 18) != 0;
      obj.mouseSensitivityAdjustmentTarget = buf.getFloatLE(offset + 19);
      obj.mouseSensitivityAdjustmentDuration = buf.getFloatLE(offset + 23);
      if ((nullBits & 1) != 0) {
         obj.chargingDelay = ChargingDelay.deserialize(buf, offset + 27);
      }

      if ((nullBits & 2) != 0) {
         int varPosBase0 = buf.getIntLE(offset + 47);
         if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 75) {
            throw ProtocolException.invalidOffset("Effects", varPosBase0, buf.readableBytes());
         }

         int varPos0 = offset + 75 + varPosBase0;
         obj.effects = InteractionEffects.deserialize(buf, varPos0);
      }

      if ((nullBits & 4) != 0) {
         int varPosBase1 = buf.getIntLE(offset + 51);
         if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 75) {
            throw ProtocolException.invalidOffset("Settings", varPosBase1, buf.readableBytes());
         }

         int varPos1 = offset + 75 + varPosBase1;
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

      if ((nullBits & 8) != 0) {
         int varPosBase2 = buf.getIntLE(offset + 55);
         if (varPosBase2 < 0 || varPosBase2 > buf.writerIndex() - offset - 75) {
            throw ProtocolException.invalidOffset("Rules", varPosBase2, buf.readableBytes());
         }

         int varPos2 = offset + 75 + varPosBase2;
         obj.rules = InteractionRules.deserialize(buf, varPos2);
      }

      if ((nullBits & 16) != 0) {
         int varPosBase3 = buf.getIntLE(offset + 59);
         if (varPosBase3 < 0 || varPosBase3 > buf.writerIndex() - offset - 75) {
            throw ProtocolException.invalidOffset("Tags", varPosBase3, buf.readableBytes());
         }

         int varPos3 = offset + 75 + varPosBase3;
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

      if ((nullBits & 32) != 0) {
         int varPosBase4 = buf.getIntLE(offset + 63);
         if (varPosBase4 < 0 || varPosBase4 > buf.writerIndex() - offset - 75) {
            throw ProtocolException.invalidOffset("Camera", varPosBase4, buf.readableBytes());
         }

         int varPos4 = offset + 75 + varPosBase4;
         obj.camera = InteractionCameraSettings.deserialize(buf, varPos4);
      }

      if ((nullBits & 64) != 0) {
         int varPosBase5 = buf.getIntLE(offset + 67);
         if (varPosBase5 < 0 || varPosBase5 > buf.writerIndex() - offset - 75) {
            throw ProtocolException.invalidOffset("ChargedNext", varPosBase5, buf.readableBytes());
         }

         int varPos5 = offset + 75 + varPosBase5;
         int chargedNextCount = VarInt.peek(buf, varPos5);
         if (chargedNextCount < 0) {
            throw ProtocolException.invalidVarInt("ChargedNext");
         }

         int varIntLen = VarInt.size(chargedNextCount);
         if (chargedNextCount > 4096000) {
            throw ProtocolException.dictionaryTooLarge("ChargedNext", chargedNextCount, 4096000);
         }

         obj.chargedNext = new HashMap<>(chargedNextCount);
         int dictPos = varPos5 + varIntLen;

         for (int i = 0; i < chargedNextCount; i++) {
            float key = buf.getFloatLE(dictPos);
            dictPos += 4;
            int val = buf.getIntLE(dictPos);
            dictPos += 4;
            if (obj.chargedNext.put(key, val) != null) {
               throw ProtocolException.duplicateKey("chargedNext", key);
            }
         }
      }

      if ((nullBits & 128) != 0) {
         int varPosBase6 = buf.getIntLE(offset + 71);
         if (varPosBase6 < 0 || varPosBase6 > buf.writerIndex() - offset - 75) {
            throw ProtocolException.invalidOffset("Forks", varPosBase6, buf.readableBytes());
         }

         int varPos6 = offset + 75 + varPosBase6;
         int forksCount = VarInt.peek(buf, varPos6);
         if (forksCount < 0) {
            throw ProtocolException.invalidVarInt("Forks");
         }

         int varIntLen = VarInt.size(forksCount);
         if (forksCount > 4096000) {
            throw ProtocolException.dictionaryTooLarge("Forks", forksCount, 4096000);
         }

         obj.forks = new HashMap<>(forksCount);
         int dictPos = varPos6 + varIntLen;

         for (int i = 0; i < forksCount; i++) {
            InteractionType key = InteractionType.fromValue(buf.getByte(dictPos));
            int val = buf.getIntLE(++dictPos);
            dictPos += 4;
            if (obj.forks.put(key, val) != null) {
               throw ProtocolException.duplicateKey("forks", key);
            }
         }
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 75;
      if ((nullBits & 2) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 47);
         if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 75) {
            throw ProtocolException.invalidOffset("Effects", fieldOffset0, maxEnd);
         }

         int pos0 = offset + 75 + fieldOffset0;
         pos0 += InteractionEffects.computeBytesConsumed(buf, pos0);
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits & 4) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 51);
         if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 75) {
            throw ProtocolException.invalidOffset("Settings", fieldOffset1, maxEnd);
         }

         int pos1 = offset + 75 + fieldOffset1;
         int dictLen = VarInt.peek(buf, pos1);
         pos1 += VarInt.size(dictLen);

         for (int i = 0; i < dictLen; i++) {
            pos1 = ++pos1 + InteractionSettings.computeBytesConsumed(buf, pos1);
         }

         if (pos1 - offset > maxEnd) {
            maxEnd = pos1 - offset;
         }
      }

      if ((nullBits & 8) != 0) {
         int fieldOffset2 = buf.getIntLE(offset + 55);
         if (fieldOffset2 < 0 || fieldOffset2 > buf.writerIndex() - offset - 75) {
            throw ProtocolException.invalidOffset("Rules", fieldOffset2, maxEnd);
         }

         int pos2 = offset + 75 + fieldOffset2;
         pos2 += InteractionRules.computeBytesConsumed(buf, pos2);
         if (pos2 - offset > maxEnd) {
            maxEnd = pos2 - offset;
         }
      }

      if ((nullBits & 16) != 0) {
         int fieldOffset3 = buf.getIntLE(offset + 59);
         if (fieldOffset3 < 0 || fieldOffset3 > buf.writerIndex() - offset - 75) {
            throw ProtocolException.invalidOffset("Tags", fieldOffset3, maxEnd);
         }

         int pos3 = offset + 75 + fieldOffset3;
         int arrLen = VarInt.peek(buf, pos3);
         pos3 += VarInt.size(arrLen) + arrLen * 4;
         if (pos3 - offset > maxEnd) {
            maxEnd = pos3 - offset;
         }
      }

      if ((nullBits & 32) != 0) {
         int fieldOffset4 = buf.getIntLE(offset + 63);
         if (fieldOffset4 < 0 || fieldOffset4 > buf.writerIndex() - offset - 75) {
            throw ProtocolException.invalidOffset("Camera", fieldOffset4, maxEnd);
         }

         int pos4 = offset + 75 + fieldOffset4;
         pos4 += InteractionCameraSettings.computeBytesConsumed(buf, pos4);
         if (pos4 - offset > maxEnd) {
            maxEnd = pos4 - offset;
         }
      }

      if ((nullBits & 64) != 0) {
         int fieldOffset5 = buf.getIntLE(offset + 67);
         if (fieldOffset5 < 0 || fieldOffset5 > buf.writerIndex() - offset - 75) {
            throw ProtocolException.invalidOffset("ChargedNext", fieldOffset5, maxEnd);
         }

         int pos5 = offset + 75 + fieldOffset5;
         int dictLen = VarInt.peek(buf, pos5);
         pos5 += VarInt.size(dictLen);

         for (int i = 0; i < dictLen; i++) {
            pos5 += 4;
            pos5 += 4;
         }

         if (pos5 - offset > maxEnd) {
            maxEnd = pos5 - offset;
         }
      }

      if ((nullBits & 128) != 0) {
         int fieldOffset6 = buf.getIntLE(offset + 71);
         if (fieldOffset6 < 0 || fieldOffset6 > buf.writerIndex() - offset - 75) {
            throw ProtocolException.invalidOffset("Forks", fieldOffset6, maxEnd);
         }

         int pos6 = offset + 75 + fieldOffset6;
         int dictLen = VarInt.peek(buf, pos6);
         pos6 += VarInt.size(dictLen);

         for (int i = 0; i < dictLen; i++) {
            pos6 = ++pos6 + 4;
         }

         if (pos6 - offset > maxEnd) {
            maxEnd = pos6 - offset;
         }
      }

      return maxEnd;
   }

   @Override
   public int serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      byte nullBits = 0;
      if (this.chargingDelay != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.effects != null) {
         nullBits = (byte)(nullBits | 2);
      }

      if (this.settings != null) {
         nullBits = (byte)(nullBits | 4);
      }

      if (this.rules != null) {
         nullBits = (byte)(nullBits | 8);
      }

      if (this.tags != null) {
         nullBits = (byte)(nullBits | 16);
      }

      if (this.camera != null) {
         nullBits = (byte)(nullBits | 32);
      }

      if (this.chargedNext != null) {
         nullBits = (byte)(nullBits | 64);
      }

      if (this.forks != null) {
         nullBits = (byte)(nullBits | 128);
      }

      buf.writeByte(nullBits);
      buf.writeByte(this.waitForDataFrom.getValue());
      buf.writeFloatLE(this.horizontalSpeedMultiplier);
      buf.writeFloatLE(this.runTime);
      buf.writeByte(this.cancelOnItemChange ? 1 : 0);
      buf.writeIntLE(this.failed);
      buf.writeByte(this.allowIndefiniteHold ? 1 : 0);
      buf.writeByte(this.displayProgress ? 1 : 0);
      buf.writeByte(this.cancelOnOtherClick ? 1 : 0);
      buf.writeByte(this.failOnDamage ? 1 : 0);
      buf.writeFloatLE(this.mouseSensitivityAdjustmentTarget);
      buf.writeFloatLE(this.mouseSensitivityAdjustmentDuration);
      if (this.chargingDelay != null) {
         this.chargingDelay.serialize(buf);
      } else {
         buf.writeZero(20);
      }

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
      int chargedNextOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int forksOffsetSlot = buf.writerIndex();
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

      if (this.chargedNext != null) {
         buf.setIntLE(chargedNextOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.chargedNext.size() > 4096000) {
            throw ProtocolException.dictionaryTooLarge("ChargedNext", this.chargedNext.size(), 4096000);
         }

         VarInt.write(buf, this.chargedNext.size());

         for (Entry<Float, Integer> e : this.chargedNext.entrySet()) {
            buf.writeFloatLE(e.getKey());
            buf.writeIntLE(e.getValue());
         }
      } else {
         buf.setIntLE(chargedNextOffsetSlot, -1);
      }

      if (this.forks != null) {
         buf.setIntLE(forksOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.forks.size() > 4096000) {
            throw ProtocolException.dictionaryTooLarge("Forks", this.forks.size(), 4096000);
         }

         VarInt.write(buf, this.forks.size());

         for (Entry<InteractionType, Integer> e : this.forks.entrySet()) {
            buf.writeByte(e.getKey().getValue());
            buf.writeIntLE(e.getValue());
         }
      } else {
         buf.setIntLE(forksOffsetSlot, -1);
      }

      return buf.writerIndex() - startPos;
   }

   @Override
   public int computeSize() {
      int size = 75;
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

      if (this.chargedNext != null) {
         size += VarInt.size(this.chargedNext.size()) + this.chargedNext.size() * 8;
      }

      if (this.forks != null) {
         size += VarInt.size(this.forks.size()) + this.forks.size() * 5;
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 75) {
         return ValidationResult.error("Buffer too small: expected at least 75 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      int v = buffer.getByte(offset + 1) & 255;
      if (v >= 3) {
         return ValidationResult.error("Invalid WaitForDataFrom value for WaitForDataFrom");
      }

      if ((nullBits & 2) != 0) {
         v = buffer.getIntLE(offset + 47);
         if (v < 0 || v > buffer.writerIndex() - offset - 75) {
            return ValidationResult.error("Invalid offset for Effects");
         }

         int pos = offset + 75 + v;
         ValidationResult effectsResult = InteractionEffects.validateStructure(buffer, pos);
         if (!effectsResult.isValid()) {
            return ValidationResult.error("Invalid Effects: " + effectsResult.error());
         }

         pos += InteractionEffects.computeBytesConsumed(buffer, pos);
      }

      if ((nullBits & 4) != 0) {
         v = buffer.getIntLE(offset + 51);
         if (v < 0 || v > buffer.writerIndex() - offset - 75) {
            return ValidationResult.error("Invalid offset for Settings");
         }

         int pos = offset + 75 + v;
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

      if ((nullBits & 8) != 0) {
         v = buffer.getIntLE(offset + 55);
         if (v < 0 || v > buffer.writerIndex() - offset - 75) {
            return ValidationResult.error("Invalid offset for Rules");
         }

         int pos = offset + 75 + v;
         ValidationResult rulesResult = InteractionRules.validateStructure(buffer, pos);
         if (!rulesResult.isValid()) {
            return ValidationResult.error("Invalid Rules: " + rulesResult.error());
         }

         pos += InteractionRules.computeBytesConsumed(buffer, pos);
      }

      if ((nullBits & 16) != 0) {
         v = buffer.getIntLE(offset + 59);
         if (v < 0 || v > buffer.writerIndex() - offset - 75) {
            return ValidationResult.error("Invalid offset for Tags");
         }

         int pos = offset + 75 + v;
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

      if ((nullBits & 32) != 0) {
         v = buffer.getIntLE(offset + 63);
         if (v < 0 || v > buffer.writerIndex() - offset - 75) {
            return ValidationResult.error("Invalid offset for Camera");
         }

         int pos = offset + 75 + v;
         ValidationResult cameraResult = InteractionCameraSettings.validateStructure(buffer, pos);
         if (!cameraResult.isValid()) {
            return ValidationResult.error("Invalid Camera: " + cameraResult.error());
         }

         pos += InteractionCameraSettings.computeBytesConsumed(buffer, pos);
      }

      if ((nullBits & 64) != 0) {
         v = buffer.getIntLE(offset + 67);
         if (v < 0 || v > buffer.writerIndex() - offset - 75) {
            return ValidationResult.error("Invalid offset for ChargedNext");
         }

         int pos = offset + 75 + v;
         int chargedNextCount = VarInt.peek(buffer, pos);
         if (chargedNextCount < 0) {
            return ValidationResult.error("Invalid dictionary count for ChargedNext");
         }

         if (chargedNextCount > 4096000) {
            return ValidationResult.error("ChargedNext exceeds max length 4096000");
         }

         pos += VarInt.size(chargedNextCount);

         for (int i = 0; i < chargedNextCount; i++) {
            pos += 4;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading key");
            }

            pos += 4;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading value");
            }
         }
      }

      if ((nullBits & 128) != 0) {
         v = buffer.getIntLE(offset + 71);
         if (v < 0 || v > buffer.writerIndex() - offset - 75) {
            return ValidationResult.error("Invalid offset for Forks");
         }

         int pos = offset + 75 + v;
         int forksCount = VarInt.peek(buffer, pos);
         if (forksCount < 0) {
            return ValidationResult.error("Invalid dictionary count for Forks");
         }

         if (forksCount > 4096000) {
            return ValidationResult.error("Forks exceeds max length 4096000");
         }

         pos += VarInt.size(forksCount);

         for (int i = 0; i < forksCount; i++) {
            int vx = buffer.getByte(pos) & 255;
            if (vx >= 25) {
               return ValidationResult.error("Invalid InteractionType value for key");
            }

            pos = ++pos + 4;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading value");
            }
         }
      }

      return ValidationResult.OK;
   }

   public ChargingInteraction clone() {
      ChargingInteraction copy = new ChargingInteraction();
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
      copy.failed = this.failed;
      copy.allowIndefiniteHold = this.allowIndefiniteHold;
      copy.displayProgress = this.displayProgress;
      copy.cancelOnOtherClick = this.cancelOnOtherClick;
      copy.failOnDamage = this.failOnDamage;
      copy.mouseSensitivityAdjustmentTarget = this.mouseSensitivityAdjustmentTarget;
      copy.mouseSensitivityAdjustmentDuration = this.mouseSensitivityAdjustmentDuration;
      copy.chargedNext = this.chargedNext != null ? new HashMap<>(this.chargedNext) : null;
      copy.forks = this.forks != null ? new HashMap<>(this.forks) : null;
      copy.chargingDelay = this.chargingDelay != null ? this.chargingDelay.clone() : null;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof ChargingInteraction other)
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
               && this.failed == other.failed
               && this.allowIndefiniteHold == other.allowIndefiniteHold
               && this.displayProgress == other.displayProgress
               && this.cancelOnOtherClick == other.cancelOnOtherClick
               && this.failOnDamage == other.failOnDamage
               && this.mouseSensitivityAdjustmentTarget == other.mouseSensitivityAdjustmentTarget
               && this.mouseSensitivityAdjustmentDuration == other.mouseSensitivityAdjustmentDuration
               && Objects.equals(this.chargedNext, other.chargedNext)
               && Objects.equals(this.forks, other.forks)
               && Objects.equals(this.chargingDelay, other.chargingDelay);
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
      result = 31 * result + Integer.hashCode(this.failed);
      result = 31 * result + Boolean.hashCode(this.allowIndefiniteHold);
      result = 31 * result + Boolean.hashCode(this.displayProgress);
      result = 31 * result + Boolean.hashCode(this.cancelOnOtherClick);
      result = 31 * result + Boolean.hashCode(this.failOnDamage);
      result = 31 * result + Float.hashCode(this.mouseSensitivityAdjustmentTarget);
      result = 31 * result + Float.hashCode(this.mouseSensitivityAdjustmentDuration);
      result = 31 * result + Objects.hashCode(this.chargedNext);
      result = 31 * result + Objects.hashCode(this.forks);
      return 31 * result + Objects.hashCode(this.chargingDelay);
   }
}
