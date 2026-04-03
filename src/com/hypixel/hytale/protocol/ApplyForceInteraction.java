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

public class ApplyForceInteraction extends SimpleInteraction {
   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
   public static final int FIXED_BLOCK_SIZE = 80;
   public static final int VARIABLE_FIELD_COUNT = 6;
   public static final int VARIABLE_BLOCK_START = 104;
   public static final int MAX_SIZE = 1677721600;
   @Nullable
   public VelocityConfig velocityConfig;
   @Nonnull
   public ChangeVelocityType changeVelocityType = ChangeVelocityType.Add;
   @Nullable
   public AppliedForce[] forces;
   public float duration;
   @Nullable
   public FloatRange verticalClamp;
   public boolean waitForGround;
   public boolean waitForCollision;
   public float groundCheckDelay;
   public float collisionCheckDelay;
   public int groundNext;
   public int collisionNext;
   public float raycastDistance;
   public float raycastHeightOffset;
   @Nonnull
   public RaycastMode raycastMode = RaycastMode.FollowMotion;

   public ApplyForceInteraction() {
   }

   public ApplyForceInteraction(
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
      @Nullable VelocityConfig velocityConfig,
      @Nonnull ChangeVelocityType changeVelocityType,
      @Nullable AppliedForce[] forces,
      float duration,
      @Nullable FloatRange verticalClamp,
      boolean waitForGround,
      boolean waitForCollision,
      float groundCheckDelay,
      float collisionCheckDelay,
      int groundNext,
      int collisionNext,
      float raycastDistance,
      float raycastHeightOffset,
      @Nonnull RaycastMode raycastMode
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
      this.velocityConfig = velocityConfig;
      this.changeVelocityType = changeVelocityType;
      this.forces = forces;
      this.duration = duration;
      this.verticalClamp = verticalClamp;
      this.waitForGround = waitForGround;
      this.waitForCollision = waitForCollision;
      this.groundCheckDelay = groundCheckDelay;
      this.collisionCheckDelay = collisionCheckDelay;
      this.groundNext = groundNext;
      this.collisionNext = collisionNext;
      this.raycastDistance = raycastDistance;
      this.raycastHeightOffset = raycastHeightOffset;
      this.raycastMode = raycastMode;
   }

   public ApplyForceInteraction(@Nonnull ApplyForceInteraction other) {
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
      this.velocityConfig = other.velocityConfig;
      this.changeVelocityType = other.changeVelocityType;
      this.forces = other.forces;
      this.duration = other.duration;
      this.verticalClamp = other.verticalClamp;
      this.waitForGround = other.waitForGround;
      this.waitForCollision = other.waitForCollision;
      this.groundCheckDelay = other.groundCheckDelay;
      this.collisionCheckDelay = other.collisionCheckDelay;
      this.groundNext = other.groundNext;
      this.collisionNext = other.collisionNext;
      this.raycastDistance = other.raycastDistance;
      this.raycastHeightOffset = other.raycastHeightOffset;
      this.raycastMode = other.raycastMode;
   }

   @Nonnull
   public static ApplyForceInteraction deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 104) {
         throw ProtocolException.bufferTooSmall("ApplyForceInteraction", 104, buf.readableBytes() - offset);
      }

      ApplyForceInteraction obj = new ApplyForceInteraction();
      byte nullBits = buf.getByte(offset);
      obj.waitForDataFrom = WaitForDataFrom.fromValue(buf.getByte(offset + 1));
      obj.horizontalSpeedMultiplier = buf.getFloatLE(offset + 2);
      obj.runTime = buf.getFloatLE(offset + 6);
      obj.cancelOnItemChange = buf.getByte(offset + 10) != 0;
      obj.next = buf.getIntLE(offset + 11);
      obj.failed = buf.getIntLE(offset + 15);
      if ((nullBits & 1) != 0) {
         obj.velocityConfig = VelocityConfig.deserialize(buf, offset + 19);
      }

      obj.changeVelocityType = ChangeVelocityType.fromValue(buf.getByte(offset + 40));
      obj.duration = buf.getFloatLE(offset + 41);
      if ((nullBits & 2) != 0) {
         obj.verticalClamp = FloatRange.deserialize(buf, offset + 45);
      }

      obj.waitForGround = buf.getByte(offset + 53) != 0;
      obj.waitForCollision = buf.getByte(offset + 54) != 0;
      obj.groundCheckDelay = buf.getFloatLE(offset + 55);
      obj.collisionCheckDelay = buf.getFloatLE(offset + 59);
      obj.groundNext = buf.getIntLE(offset + 63);
      obj.collisionNext = buf.getIntLE(offset + 67);
      obj.raycastDistance = buf.getFloatLE(offset + 71);
      obj.raycastHeightOffset = buf.getFloatLE(offset + 75);
      obj.raycastMode = RaycastMode.fromValue(buf.getByte(offset + 79));
      if ((nullBits & 4) != 0) {
         int varPosBase0 = buf.getIntLE(offset + 80);
         if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 104) {
            throw ProtocolException.invalidOffset("Effects", varPosBase0, buf.readableBytes());
         }

         int varPos0 = offset + 104 + varPosBase0;
         obj.effects = InteractionEffects.deserialize(buf, varPos0);
      }

      if ((nullBits & 8) != 0) {
         int varPosBase1 = buf.getIntLE(offset + 84);
         if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 104) {
            throw ProtocolException.invalidOffset("Settings", varPosBase1, buf.readableBytes());
         }

         int varPos1 = offset + 104 + varPosBase1;
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

      if ((nullBits & 16) != 0) {
         int varPosBase2 = buf.getIntLE(offset + 88);
         if (varPosBase2 < 0 || varPosBase2 > buf.writerIndex() - offset - 104) {
            throw ProtocolException.invalidOffset("Rules", varPosBase2, buf.readableBytes());
         }

         int varPos2 = offset + 104 + varPosBase2;
         obj.rules = InteractionRules.deserialize(buf, varPos2);
      }

      if ((nullBits & 32) != 0) {
         int varPosBase3 = buf.getIntLE(offset + 92);
         if (varPosBase3 < 0 || varPosBase3 > buf.writerIndex() - offset - 104) {
            throw ProtocolException.invalidOffset("Tags", varPosBase3, buf.readableBytes());
         }

         int varPos3 = offset + 104 + varPosBase3;
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

      if ((nullBits & 64) != 0) {
         int varPosBase4 = buf.getIntLE(offset + 96);
         if (varPosBase4 < 0 || varPosBase4 > buf.writerIndex() - offset - 104) {
            throw ProtocolException.invalidOffset("Camera", varPosBase4, buf.readableBytes());
         }

         int varPos4 = offset + 104 + varPosBase4;
         obj.camera = InteractionCameraSettings.deserialize(buf, varPos4);
      }

      if ((nullBits & 128) != 0) {
         int varPosBase5 = buf.getIntLE(offset + 100);
         if (varPosBase5 < 0 || varPosBase5 > buf.writerIndex() - offset - 104) {
            throw ProtocolException.invalidOffset("Forces", varPosBase5, buf.readableBytes());
         }

         int varPos5 = offset + 104 + varPosBase5;
         int forcesCount = VarInt.peek(buf, varPos5);
         if (forcesCount < 0) {
            throw ProtocolException.invalidVarInt("Forces");
         }

         int varIntLen = VarInt.size(forcesCount);
         if (forcesCount > 4096000) {
            throw ProtocolException.arrayTooLong("Forces", forcesCount, 4096000);
         }

         if (varPos5 + varIntLen + forcesCount * 17L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Forces", varPos5 + varIntLen + forcesCount * 17, buf.readableBytes());
         }

         obj.forces = new AppliedForce[forcesCount];
         int elemPos = varPos5 + varIntLen;

         for (int i = 0; i < forcesCount; i++) {
            obj.forces[i] = AppliedForce.deserialize(buf, elemPos);
            elemPos += AppliedForce.computeBytesConsumed(buf, elemPos);
         }
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte nullBits = buf.getByte(offset);
      int maxEnd = 104;
      if ((nullBits & 4) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 80);
         if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 104) {
            throw ProtocolException.invalidOffset("Effects", fieldOffset0, maxEnd);
         }

         int pos0 = offset + 104 + fieldOffset0;
         pos0 += InteractionEffects.computeBytesConsumed(buf, pos0);
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits & 8) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 84);
         if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 104) {
            throw ProtocolException.invalidOffset("Settings", fieldOffset1, maxEnd);
         }

         int pos1 = offset + 104 + fieldOffset1;
         int dictLen = VarInt.peek(buf, pos1);
         pos1 += VarInt.size(dictLen);

         for (int i = 0; i < dictLen; i++) {
            pos1 = ++pos1 + InteractionSettings.computeBytesConsumed(buf, pos1);
         }

         if (pos1 - offset > maxEnd) {
            maxEnd = pos1 - offset;
         }
      }

      if ((nullBits & 16) != 0) {
         int fieldOffset2 = buf.getIntLE(offset + 88);
         if (fieldOffset2 < 0 || fieldOffset2 > buf.writerIndex() - offset - 104) {
            throw ProtocolException.invalidOffset("Rules", fieldOffset2, maxEnd);
         }

         int pos2 = offset + 104 + fieldOffset2;
         pos2 += InteractionRules.computeBytesConsumed(buf, pos2);
         if (pos2 - offset > maxEnd) {
            maxEnd = pos2 - offset;
         }
      }

      if ((nullBits & 32) != 0) {
         int fieldOffset3 = buf.getIntLE(offset + 92);
         if (fieldOffset3 < 0 || fieldOffset3 > buf.writerIndex() - offset - 104) {
            throw ProtocolException.invalidOffset("Tags", fieldOffset3, maxEnd);
         }

         int pos3 = offset + 104 + fieldOffset3;
         int arrLen = VarInt.peek(buf, pos3);
         pos3 += VarInt.size(arrLen) + arrLen * 4;
         if (pos3 - offset > maxEnd) {
            maxEnd = pos3 - offset;
         }
      }

      if ((nullBits & 64) != 0) {
         int fieldOffset4 = buf.getIntLE(offset + 96);
         if (fieldOffset4 < 0 || fieldOffset4 > buf.writerIndex() - offset - 104) {
            throw ProtocolException.invalidOffset("Camera", fieldOffset4, maxEnd);
         }

         int pos4 = offset + 104 + fieldOffset4;
         pos4 += InteractionCameraSettings.computeBytesConsumed(buf, pos4);
         if (pos4 - offset > maxEnd) {
            maxEnd = pos4 - offset;
         }
      }

      if ((nullBits & 128) != 0) {
         int fieldOffset5 = buf.getIntLE(offset + 100);
         if (fieldOffset5 < 0 || fieldOffset5 > buf.writerIndex() - offset - 104) {
            throw ProtocolException.invalidOffset("Forces", fieldOffset5, maxEnd);
         }

         int pos5 = offset + 104 + fieldOffset5;
         int arrLen = VarInt.peek(buf, pos5);
         pos5 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            pos5 += AppliedForce.computeBytesConsumed(buf, pos5);
         }

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
      if (this.velocityConfig != null) {
         nullBits = (byte)(nullBits | 1);
      }

      if (this.verticalClamp != null) {
         nullBits = (byte)(nullBits | 2);
      }

      if (this.effects != null) {
         nullBits = (byte)(nullBits | 4);
      }

      if (this.settings != null) {
         nullBits = (byte)(nullBits | 8);
      }

      if (this.rules != null) {
         nullBits = (byte)(nullBits | 16);
      }

      if (this.tags != null) {
         nullBits = (byte)(nullBits | 32);
      }

      if (this.camera != null) {
         nullBits = (byte)(nullBits | 64);
      }

      if (this.forces != null) {
         nullBits = (byte)(nullBits | 128);
      }

      buf.writeByte(nullBits);
      buf.writeByte(this.waitForDataFrom.getValue());
      buf.writeFloatLE(this.horizontalSpeedMultiplier);
      buf.writeFloatLE(this.runTime);
      buf.writeByte(this.cancelOnItemChange ? 1 : 0);
      buf.writeIntLE(this.next);
      buf.writeIntLE(this.failed);
      if (this.velocityConfig != null) {
         this.velocityConfig.serialize(buf);
      } else {
         buf.writeZero(21);
      }

      buf.writeByte(this.changeVelocityType.getValue());
      buf.writeFloatLE(this.duration);
      if (this.verticalClamp != null) {
         this.verticalClamp.serialize(buf);
      } else {
         buf.writeZero(8);
      }

      buf.writeByte(this.waitForGround ? 1 : 0);
      buf.writeByte(this.waitForCollision ? 1 : 0);
      buf.writeFloatLE(this.groundCheckDelay);
      buf.writeFloatLE(this.collisionCheckDelay);
      buf.writeIntLE(this.groundNext);
      buf.writeIntLE(this.collisionNext);
      buf.writeFloatLE(this.raycastDistance);
      buf.writeFloatLE(this.raycastHeightOffset);
      buf.writeByte(this.raycastMode.getValue());
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
      int forcesOffsetSlot = buf.writerIndex();
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

      if (this.forces != null) {
         buf.setIntLE(forcesOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.forces.length > 4096000) {
            throw ProtocolException.arrayTooLong("Forces", this.forces.length, 4096000);
         }

         VarInt.write(buf, this.forces.length);

         for (AppliedForce item : this.forces) {
            item.serialize(buf);
         }
      } else {
         buf.setIntLE(forcesOffsetSlot, -1);
      }

      return buf.writerIndex() - startPos;
   }

   @Override
   public int computeSize() {
      int size = 104;
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

      if (this.forces != null) {
         size += VarInt.size(this.forces.length) + this.forces.length * 17;
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 104) {
         return ValidationResult.error("Buffer too small: expected at least 104 bytes");
      }

      byte nullBits = buffer.getByte(offset);
      int v = buffer.getByte(offset + 1) & 255;
      if (v >= 3) {
         return ValidationResult.error("Invalid WaitForDataFrom value for WaitForDataFrom");
      }

      v = buffer.getByte(offset + 40) & 255;
      if (v >= 2) {
         return ValidationResult.error("Invalid ChangeVelocityType value for ChangeVelocityType");
      }

      v = buffer.getByte(offset + 79) & 255;
      if (v >= 2) {
         return ValidationResult.error("Invalid RaycastMode value for RaycastMode");
      }

      if ((nullBits & 4) != 0) {
         v = buffer.getIntLE(offset + 80);
         if (v < 0 || v > buffer.writerIndex() - offset - 104) {
            return ValidationResult.error("Invalid offset for Effects");
         }

         int pos = offset + 104 + v;
         ValidationResult effectsResult = InteractionEffects.validateStructure(buffer, pos);
         if (!effectsResult.isValid()) {
            return ValidationResult.error("Invalid Effects: " + effectsResult.error());
         }

         pos += InteractionEffects.computeBytesConsumed(buffer, pos);
      }

      if ((nullBits & 8) != 0) {
         v = buffer.getIntLE(offset + 84);
         if (v < 0 || v > buffer.writerIndex() - offset - 104) {
            return ValidationResult.error("Invalid offset for Settings");
         }

         int pos = offset + 104 + v;
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

      if ((nullBits & 16) != 0) {
         v = buffer.getIntLE(offset + 88);
         if (v < 0 || v > buffer.writerIndex() - offset - 104) {
            return ValidationResult.error("Invalid offset for Rules");
         }

         int pos = offset + 104 + v;
         ValidationResult rulesResult = InteractionRules.validateStructure(buffer, pos);
         if (!rulesResult.isValid()) {
            return ValidationResult.error("Invalid Rules: " + rulesResult.error());
         }

         pos += InteractionRules.computeBytesConsumed(buffer, pos);
      }

      if ((nullBits & 32) != 0) {
         v = buffer.getIntLE(offset + 92);
         if (v < 0 || v > buffer.writerIndex() - offset - 104) {
            return ValidationResult.error("Invalid offset for Tags");
         }

         int pos = offset + 104 + v;
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

      if ((nullBits & 64) != 0) {
         v = buffer.getIntLE(offset + 96);
         if (v < 0 || v > buffer.writerIndex() - offset - 104) {
            return ValidationResult.error("Invalid offset for Camera");
         }

         int pos = offset + 104 + v;
         ValidationResult cameraResult = InteractionCameraSettings.validateStructure(buffer, pos);
         if (!cameraResult.isValid()) {
            return ValidationResult.error("Invalid Camera: " + cameraResult.error());
         }

         pos += InteractionCameraSettings.computeBytesConsumed(buffer, pos);
      }

      if ((nullBits & 128) != 0) {
         v = buffer.getIntLE(offset + 100);
         if (v < 0 || v > buffer.writerIndex() - offset - 104) {
            return ValidationResult.error("Invalid offset for Forces");
         }

         int pos = offset + 104 + v;
         int forcesCount = VarInt.peek(buffer, pos);
         if (forcesCount < 0) {
            return ValidationResult.error("Invalid array count for Forces");
         }

         if (forcesCount > 4096000) {
            return ValidationResult.error("Forces exceeds max length 4096000");
         }

         pos += VarInt.size(forcesCount);
         pos += forcesCount * 17;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Forces");
         }
      }

      return ValidationResult.OK;
   }

   public ApplyForceInteraction clone() {
      ApplyForceInteraction copy = new ApplyForceInteraction();
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
      copy.velocityConfig = this.velocityConfig != null ? this.velocityConfig.clone() : null;
      copy.changeVelocityType = this.changeVelocityType;
      copy.forces = this.forces != null ? Arrays.stream(this.forces).map(ex -> ex.clone()).toArray(AppliedForce[]::new) : null;
      copy.duration = this.duration;
      copy.verticalClamp = this.verticalClamp != null ? this.verticalClamp.clone() : null;
      copy.waitForGround = this.waitForGround;
      copy.waitForCollision = this.waitForCollision;
      copy.groundCheckDelay = this.groundCheckDelay;
      copy.collisionCheckDelay = this.collisionCheckDelay;
      copy.groundNext = this.groundNext;
      copy.collisionNext = this.collisionNext;
      copy.raycastDistance = this.raycastDistance;
      copy.raycastHeightOffset = this.raycastHeightOffset;
      copy.raycastMode = this.raycastMode;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof ApplyForceInteraction other)
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
               && Objects.equals(this.velocityConfig, other.velocityConfig)
               && Objects.equals(this.changeVelocityType, other.changeVelocityType)
               && Arrays.equals(this.forces, other.forces)
               && this.duration == other.duration
               && Objects.equals(this.verticalClamp, other.verticalClamp)
               && this.waitForGround == other.waitForGround
               && this.waitForCollision == other.waitForCollision
               && this.groundCheckDelay == other.groundCheckDelay
               && this.collisionCheckDelay == other.collisionCheckDelay
               && this.groundNext == other.groundNext
               && this.collisionNext == other.collisionNext
               && this.raycastDistance == other.raycastDistance
               && this.raycastHeightOffset == other.raycastHeightOffset
               && Objects.equals(this.raycastMode, other.raycastMode);
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
      result = 31 * result + Objects.hashCode(this.velocityConfig);
      result = 31 * result + Objects.hashCode(this.changeVelocityType);
      result = 31 * result + Arrays.hashCode(this.forces);
      result = 31 * result + Float.hashCode(this.duration);
      result = 31 * result + Objects.hashCode(this.verticalClamp);
      result = 31 * result + Boolean.hashCode(this.waitForGround);
      result = 31 * result + Boolean.hashCode(this.waitForCollision);
      result = 31 * result + Float.hashCode(this.groundCheckDelay);
      result = 31 * result + Float.hashCode(this.collisionCheckDelay);
      result = 31 * result + Integer.hashCode(this.groundNext);
      result = 31 * result + Integer.hashCode(this.collisionNext);
      result = 31 * result + Float.hashCode(this.raycastDistance);
      result = 31 * result + Float.hashCode(this.raycastHeightOffset);
      return 31 * result + Objects.hashCode(this.raycastMode);
   }
}
