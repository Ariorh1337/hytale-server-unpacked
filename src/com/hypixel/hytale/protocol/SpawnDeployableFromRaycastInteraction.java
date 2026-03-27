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

public class SpawnDeployableFromRaycastInteraction extends SimpleInteraction {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 23;
   public static final int VARIABLE_FIELD_COUNT = 7;
   public static final int VARIABLE_BLOCK_START = 51;
   public static final int MAX_SIZE = 1677721600;
   @Nullable
   public DeployableConfig deployableConfig;
   public float maxDistance;
   @Nullable
   public Map<Integer, Float> costs;

   public SpawnDeployableFromRaycastInteraction() {
   }

   public SpawnDeployableFromRaycastInteraction(
      @Nonnull WaitForDataFrom waitForDataFrom,
      @Nullable InteractionEffects effects,
      float horizontalSpeedMultiplier,
      float runTime,
      boolean cancelOnItemChange,
      @Nullable Map<GameMode, InteractionSettings> settings,
      @Nullable InteractionRules rules,
      @Nullable int[] tags,
      @Nullable InteractionCameraSettings camera,
      int next,
      int failed,
      @Nullable DeployableConfig deployableConfig,
      float maxDistance,
      @Nullable Map<Integer, Float> costs
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
      this.next = next;
      this.failed = failed;
      this.deployableConfig = deployableConfig;
      this.maxDistance = maxDistance;
      this.costs = costs;
   }

   public SpawnDeployableFromRaycastInteraction(@Nonnull SpawnDeployableFromRaycastInteraction other) {
      this.waitForDataFrom = other.waitForDataFrom;
      this.effects = other.effects;
      this.horizontalSpeedMultiplier = other.horizontalSpeedMultiplier;
      this.runTime = other.runTime;
      this.cancelOnItemChange = other.cancelOnItemChange;
      this.settings = other.settings;
      this.rules = other.rules;
      this.tags = other.tags;
      this.camera = other.camera;
      this.next = other.next;
      this.failed = other.failed;
      this.deployableConfig = other.deployableConfig;
      this.maxDistance = other.maxDistance;
      this.costs = other.costs;
   }

   @Nonnull
   public static SpawnDeployableFromRaycastInteraction deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 51) {
         throw ProtocolException.bufferTooSmall("SpawnDeployableFromRaycastInteraction", 51, buf.readableBytes() - offset);
      }

      SpawnDeployableFromRaycastInteraction obj = new SpawnDeployableFromRaycastInteraction();
      byte nullBits = buf.getByte(offset);
      obj.waitForDataFrom = WaitForDataFrom.fromValue(buf.getByte(offset + 1));
      obj.horizontalSpeedMultiplier = buf.getFloatLE(offset + 2);
      obj.runTime = buf.getFloatLE(offset + 6);
      obj.cancelOnItemChange = buf.getByte(offset + 10) != 0;
      obj.next = buf.getIntLE(offset + 11);
      obj.failed = buf.getIntLE(offset + 15);
      obj.maxDistance = buf.getFloatLE(offset + 19);
      if ((nullBits & 1) != 0) {
         int varPosBase0 = buf.getIntLE(offset + 23);
         if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 51) {
            throw ProtocolException.invalidOffset("Effects", varPosBase0, buf.readableBytes());
         }

         int varPos0 = offset + 51 + varPosBase0;
         obj.effects = InteractionEffects.deserialize(buf, varPos0);
      }

      if ((nullBits & 2) != 0) {
         int varPosBase1 = buf.getIntLE(offset + 27);
         if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 51) {
            throw ProtocolException.invalidOffset("Settings", varPosBase1, buf.readableBytes());
         }

         int varPos1 = offset + 51 + varPosBase1;
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
         int varPosBase2 = buf.getIntLE(offset + 31);
         if (varPosBase2 < 0 || varPosBase2 > buf.writerIndex() - offset - 51) {
            throw ProtocolException.invalidOffset("Rules", varPosBase2, buf.readableBytes());
         }

         int varPos2 = offset + 51 + varPosBase2;
         obj.rules = InteractionRules.deserialize(buf, varPos2);
      }

      if ((nullBits & 8) != 0) {
         int varPosBase3 = buf.getIntLE(offset + 35);
         if (varPosBase3 < 0 || varPosBase3 > buf.writerIndex() - offset - 51) {
            throw ProtocolException.invalidOffset("Tags", varPosBase3, buf.readableBytes());
         }

         int varPos3 = offset + 51 + varPosBase3;
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
         int varPosBase4 = buf.getIntLE(offset + 39);
         if (varPosBase4 < 0 || varPosBase4 > buf.writerIndex() - offset - 51) {
            throw ProtocolException.invalidOffset("Camera", varPosBase4, buf.readableBytes());
         }

         int varPos4 = offset + 51 + varPosBase4;
         obj.camera = InteractionCameraSettings.deserialize(buf, varPos4);
      }

      if ((nullBits & 32) != 0) {
         int varPosBase5 = buf.getIntLE(offset + 43);
         if (varPosBase5 < 0 || varPosBase5 > buf.writerIndex() - offset - 51) {
            throw ProtocolException.invalidOffset("DeployableConfig", varPosBase5, buf.readableBytes());
         }

         int varPos5 = offset + 51 + varPosBase5;
         obj.deployableConfig = DeployableConfig.deserialize(buf, varPos5);
      }

      if ((nullBits & 64) != 0) {
         int varPosBase6 = buf.getIntLE(offset + 47);
         if (varPosBase6 < 0 || varPosBase6 > buf.writerIndex() - offset - 51) {
            throw ProtocolException.invalidOffset("Costs", varPosBase6, buf.readableBytes());
         }

         int varPos6 = offset + 51 + varPosBase6;
         int costsCount = VarInt.peek(buf, varPos6);
         if (costsCount < 0) {
            throw ProtocolException.invalidVarInt("Costs");
         }

         int varIntLen = VarInt.size(costsCount);
         if (costsCount > 4096000) {
            throw ProtocolException.dictionaryTooLarge("Costs", costsCount, 4096000);
         }

         obj.costs = new HashMap<>(costsCount);
         int dictPos = varPos6 + varIntLen;

         for (int i = 0; i < costsCount; i++) {
            int key = buf.getIntLE(dictPos);
            dictPos += 4;
            float val = buf.getFloatLE(dictPos);
            dictPos += 4;
            if (obj.costs.put(key, val) != null) {
               throw ProtocolException.duplicateKey("costs", key);
            }
         }
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 51;
      if ((nullBits & 1) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 23);
         if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 51) {
            throw ProtocolException.invalidOffset("Effects", fieldOffset0, maxEnd);
         }

         int pos0 = offset + 51 + fieldOffset0;
         pos0 += InteractionEffects.computeBytesConsumed(buf, pos0);
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits & 2) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 27);
         if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 51) {
            throw ProtocolException.invalidOffset("Settings", fieldOffset1, maxEnd);
         }

         int pos1 = offset + 51 + fieldOffset1;
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
         int fieldOffset2 = buf.getIntLE(offset + 31);
         if (fieldOffset2 < 0 || fieldOffset2 > buf.writerIndex() - offset - 51) {
            throw ProtocolException.invalidOffset("Rules", fieldOffset2, maxEnd);
         }

         int pos2 = offset + 51 + fieldOffset2;
         pos2 += InteractionRules.computeBytesConsumed(buf, pos2);
         if (pos2 - offset > maxEnd) {
            maxEnd = pos2 - offset;
         }
      }

      if ((nullBits & 8) != 0) {
         int fieldOffset3 = buf.getIntLE(offset + 35);
         if (fieldOffset3 < 0 || fieldOffset3 > buf.writerIndex() - offset - 51) {
            throw ProtocolException.invalidOffset("Tags", fieldOffset3, maxEnd);
         }

         int pos3 = offset + 51 + fieldOffset3;
         int arrLen = VarInt.peek(buf, pos3);
         pos3 += VarInt.size(arrLen) + arrLen * 4;
         if (pos3 - offset > maxEnd) {
            maxEnd = pos3 - offset;
         }
      }

      if ((nullBits & 16) != 0) {
         int fieldOffset4 = buf.getIntLE(offset + 39);
         if (fieldOffset4 < 0 || fieldOffset4 > buf.writerIndex() - offset - 51) {
            throw ProtocolException.invalidOffset("Camera", fieldOffset4, maxEnd);
         }

         int pos4 = offset + 51 + fieldOffset4;
         pos4 += InteractionCameraSettings.computeBytesConsumed(buf, pos4);
         if (pos4 - offset > maxEnd) {
            maxEnd = pos4 - offset;
         }
      }

      if ((nullBits & 32) != 0) {
         int fieldOffset5 = buf.getIntLE(offset + 43);
         if (fieldOffset5 < 0 || fieldOffset5 > buf.writerIndex() - offset - 51) {
            throw ProtocolException.invalidOffset("DeployableConfig", fieldOffset5, maxEnd);
         }

         int pos5 = offset + 51 + fieldOffset5;
         pos5 += DeployableConfig.computeBytesConsumed(buf, pos5);
         if (pos5 - offset > maxEnd) {
            maxEnd = pos5 - offset;
         }
      }

      if ((nullBits & 64) != 0) {
         int fieldOffset6 = buf.getIntLE(offset + 47);
         if (fieldOffset6 < 0 || fieldOffset6 > buf.writerIndex() - offset - 51) {
            throw ProtocolException.invalidOffset("Costs", fieldOffset6, maxEnd);
         }

         int pos6 = offset + 51 + fieldOffset6;
         int dictLen = VarInt.peek(buf, pos6);
         pos6 += VarInt.size(dictLen);

         for (int i = 0; i < dictLen; i++) {
            pos6 += 4;
            pos6 += 4;
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

      if (this.deployableConfig != null) {
         nullBits = (byte)(nullBits | 32);
      }

      if (this.costs != null) {
         nullBits = (byte)(nullBits | 64);
      }

      buf.writeByte(nullBits);
      buf.writeByte(this.waitForDataFrom.getValue());
      buf.writeFloatLE(this.horizontalSpeedMultiplier);
      buf.writeFloatLE(this.runTime);
      buf.writeByte(this.cancelOnItemChange ? 1 : 0);
      buf.writeIntLE(this.next);
      buf.writeIntLE(this.failed);
      buf.writeFloatLE(this.maxDistance);
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
      int deployableConfigOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int costsOffsetSlot = buf.writerIndex();
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

      if (this.deployableConfig != null) {
         buf.setIntLE(deployableConfigOffsetSlot, buf.writerIndex() - varBlockStart);
         this.deployableConfig.serialize(buf);
      } else {
         buf.setIntLE(deployableConfigOffsetSlot, -1);
      }

      if (this.costs != null) {
         buf.setIntLE(costsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.costs.size() > 4096000) {
            throw ProtocolException.dictionaryTooLarge("Costs", this.costs.size(), 4096000);
         }

         VarInt.write(buf, this.costs.size());

         for (Entry<Integer, Float> e : this.costs.entrySet()) {
            buf.writeIntLE(e.getKey());
            buf.writeFloatLE(e.getValue());
         }
      } else {
         buf.setIntLE(costsOffsetSlot, -1);
      }

      return buf.writerIndex() - startPos;
   }

   @Override
   public int computeSize() {
      int size = 51;
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

      if (this.deployableConfig != null) {
         size += this.deployableConfig.computeSize();
      }

      if (this.costs != null) {
         size += VarInt.size(this.costs.size()) + this.costs.size() * 8;
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 51) {
         return ValidationResult.error("Buffer too small: expected at least 51 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      int v = buffer.getByte(offset + 1) & 255;
      if (v >= 3) {
         return ValidationResult.error("Invalid WaitForDataFrom value for WaitForDataFrom");
      }

      if ((nullBits & 1) != 0) {
         v = buffer.getIntLE(offset + 23);
         if (v < 0 || v > buffer.writerIndex() - offset - 51) {
            return ValidationResult.error("Invalid offset for Effects");
         }

         int pos = offset + 51 + v;
         ValidationResult effectsResult = InteractionEffects.validateStructure(buffer, pos);
         if (!effectsResult.isValid()) {
            return ValidationResult.error("Invalid Effects: " + effectsResult.error());
         }

         pos += InteractionEffects.computeBytesConsumed(buffer, pos);
      }

      if ((nullBits & 2) != 0) {
         v = buffer.getIntLE(offset + 27);
         if (v < 0 || v > buffer.writerIndex() - offset - 51) {
            return ValidationResult.error("Invalid offset for Settings");
         }

         int pos = offset + 51 + v;
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
         v = buffer.getIntLE(offset + 31);
         if (v < 0 || v > buffer.writerIndex() - offset - 51) {
            return ValidationResult.error("Invalid offset for Rules");
         }

         int pos = offset + 51 + v;
         ValidationResult rulesResult = InteractionRules.validateStructure(buffer, pos);
         if (!rulesResult.isValid()) {
            return ValidationResult.error("Invalid Rules: " + rulesResult.error());
         }

         pos += InteractionRules.computeBytesConsumed(buffer, pos);
      }

      if ((nullBits & 8) != 0) {
         v = buffer.getIntLE(offset + 35);
         if (v < 0 || v > buffer.writerIndex() - offset - 51) {
            return ValidationResult.error("Invalid offset for Tags");
         }

         int pos = offset + 51 + v;
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
         v = buffer.getIntLE(offset + 39);
         if (v < 0 || v > buffer.writerIndex() - offset - 51) {
            return ValidationResult.error("Invalid offset for Camera");
         }

         int pos = offset + 51 + v;
         ValidationResult cameraResult = InteractionCameraSettings.validateStructure(buffer, pos);
         if (!cameraResult.isValid()) {
            return ValidationResult.error("Invalid Camera: " + cameraResult.error());
         }

         pos += InteractionCameraSettings.computeBytesConsumed(buffer, pos);
      }

      if ((nullBits & 32) != 0) {
         v = buffer.getIntLE(offset + 43);
         if (v < 0 || v > buffer.writerIndex() - offset - 51) {
            return ValidationResult.error("Invalid offset for DeployableConfig");
         }

         int pos = offset + 51 + v;
         ValidationResult deployableConfigResult = DeployableConfig.validateStructure(buffer, pos);
         if (!deployableConfigResult.isValid()) {
            return ValidationResult.error("Invalid DeployableConfig: " + deployableConfigResult.error());
         }

         pos += DeployableConfig.computeBytesConsumed(buffer, pos);
      }

      if ((nullBits & 64) != 0) {
         v = buffer.getIntLE(offset + 47);
         if (v < 0 || v > buffer.writerIndex() - offset - 51) {
            return ValidationResult.error("Invalid offset for Costs");
         }

         int pos = offset + 51 + v;
         int costsCount = VarInt.peek(buffer, pos);
         if (costsCount < 0) {
            return ValidationResult.error("Invalid dictionary count for Costs");
         }

         if (costsCount > 4096000) {
            return ValidationResult.error("Costs exceeds max length 4096000");
         }

         pos += VarInt.size(costsCount);

         for (int i = 0; i < costsCount; i++) {
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

      return ValidationResult.OK;
   }

   public SpawnDeployableFromRaycastInteraction clone() {
      SpawnDeployableFromRaycastInteraction copy = new SpawnDeployableFromRaycastInteraction();
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
      copy.next = this.next;
      copy.failed = this.failed;
      copy.deployableConfig = this.deployableConfig != null ? this.deployableConfig.clone() : null;
      copy.maxDistance = this.maxDistance;
      copy.costs = this.costs != null ? new HashMap<>(this.costs) : null;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof SpawnDeployableFromRaycastInteraction other)
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
               && this.next == other.next
               && this.failed == other.failed
               && Objects.equals(this.deployableConfig, other.deployableConfig)
               && this.maxDistance == other.maxDistance
               && Objects.equals(this.costs, other.costs);
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
      result = 31 * result + Integer.hashCode(this.next);
      result = 31 * result + Integer.hashCode(this.failed);
      result = 31 * result + Objects.hashCode(this.deployableConfig);
      result = 31 * result + Float.hashCode(this.maxDistance);
      return 31 * result + Objects.hashCode(this.costs);
   }
}
