package com.hypixel.hytale.protocol;

import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolState;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemBase {
   public static final int NULLABLE_BIT_FIELD_SIZE = 5;
   public static final int FIXED_BLOCK_SIZE = 148;
   public static final int VARIABLE_FIELD_COUNT = 28;
   public static final int VARIABLE_BLOCK_START = 260;
   public static final int MAX_SIZE = 1677721600;
   @Nullable
   public String id;
   @Nullable
   public String model;
   public float scale;
   @Nullable
   public String texture;
   @Nullable
   public String animation;
   @Nullable
   public String playerAnimationsId;
   public boolean usePlayerAnimations;
   public int maxStack;
   public int reticleIndex;
   @Nullable
   public String icon;
   @Nullable
   public AssetIconProperties iconProperties;
   @Nullable
   public ItemTranslationProperties translationProperties;
   public int itemLevel;
   public int qualityIndex;
   @Nullable
   public ItemResourceType[] resourceTypes;
   public boolean consumable;
   public boolean variant;
   public int blockId;
   @Nullable
   public ItemTool tool;
   @Nullable
   public ItemWeapon weapon;
   @Nullable
   public ItemArmor armor;
   @Nullable
   public ItemGlider gliderConfig;
   @Nullable
   public ItemUtility utility;
   @Nullable
   public BlockSelectorToolData blockSelectorTool;
   @Nullable
   public BuilderToolState builderToolData;
   @Nullable
   public ItemEntityConfig itemEntity;
   @Nullable
   public String set;
   @Nullable
   public String[] categories;
   @Nullable
   public String subCategory;
   @Nullable
   public ModelParticle[] particles;
   @Nullable
   public ModelParticle[] firstPersonParticles;
   @Nullable
   public ModelTrail[] trails;
   @Nullable
   public ColorLight light;
   public double durability;
   public int soundEventIndex;
   public int itemSoundSetIndex;
   @Nullable
   public Map<InteractionType, Integer> interactions;
   @Nullable
   public Map<String, Integer> interactionVars;
   @Nullable
   public InteractionConfiguration interactionConfig;
   @Nullable
   public String droppedItemAnimation;
   @Nullable
   public int[] tagIndexes;
   @Nullable
   public Map<Integer, ItemAppearanceCondition[]> itemAppearanceConditions;
   @Nullable
   public int[] displayEntityStatsHUD;
   @Nullable
   public ItemPullbackConfiguration pullbackConfig;
   public boolean clipsGeometry;
   public boolean renderDeployablePreview;
   @Nullable
   public ItemHudUI[] hudUI;

   public ItemBase() {
   }

   public ItemBase(
      @Nullable String id,
      @Nullable String model,
      float scale,
      @Nullable String texture,
      @Nullable String animation,
      @Nullable String playerAnimationsId,
      boolean usePlayerAnimations,
      int maxStack,
      int reticleIndex,
      @Nullable String icon,
      @Nullable AssetIconProperties iconProperties,
      @Nullable ItemTranslationProperties translationProperties,
      int itemLevel,
      int qualityIndex,
      @Nullable ItemResourceType[] resourceTypes,
      boolean consumable,
      boolean variant,
      int blockId,
      @Nullable ItemTool tool,
      @Nullable ItemWeapon weapon,
      @Nullable ItemArmor armor,
      @Nullable ItemGlider gliderConfig,
      @Nullable ItemUtility utility,
      @Nullable BlockSelectorToolData blockSelectorTool,
      @Nullable BuilderToolState builderToolData,
      @Nullable ItemEntityConfig itemEntity,
      @Nullable String set,
      @Nullable String[] categories,
      @Nullable String subCategory,
      @Nullable ModelParticle[] particles,
      @Nullable ModelParticle[] firstPersonParticles,
      @Nullable ModelTrail[] trails,
      @Nullable ColorLight light,
      double durability,
      int soundEventIndex,
      int itemSoundSetIndex,
      @Nullable Map<InteractionType, Integer> interactions,
      @Nullable Map<String, Integer> interactionVars,
      @Nullable InteractionConfiguration interactionConfig,
      @Nullable String droppedItemAnimation,
      @Nullable int[] tagIndexes,
      @Nullable Map<Integer, ItemAppearanceCondition[]> itemAppearanceConditions,
      @Nullable int[] displayEntityStatsHUD,
      @Nullable ItemPullbackConfiguration pullbackConfig,
      boolean clipsGeometry,
      boolean renderDeployablePreview,
      @Nullable ItemHudUI[] hudUI
   ) {
      this.id = id;
      this.model = model;
      this.scale = scale;
      this.texture = texture;
      this.animation = animation;
      this.playerAnimationsId = playerAnimationsId;
      this.usePlayerAnimations = usePlayerAnimations;
      this.maxStack = maxStack;
      this.reticleIndex = reticleIndex;
      this.icon = icon;
      this.iconProperties = iconProperties;
      this.translationProperties = translationProperties;
      this.itemLevel = itemLevel;
      this.qualityIndex = qualityIndex;
      this.resourceTypes = resourceTypes;
      this.consumable = consumable;
      this.variant = variant;
      this.blockId = blockId;
      this.tool = tool;
      this.weapon = weapon;
      this.armor = armor;
      this.gliderConfig = gliderConfig;
      this.utility = utility;
      this.blockSelectorTool = blockSelectorTool;
      this.builderToolData = builderToolData;
      this.itemEntity = itemEntity;
      this.set = set;
      this.categories = categories;
      this.subCategory = subCategory;
      this.particles = particles;
      this.firstPersonParticles = firstPersonParticles;
      this.trails = trails;
      this.light = light;
      this.durability = durability;
      this.soundEventIndex = soundEventIndex;
      this.itemSoundSetIndex = itemSoundSetIndex;
      this.interactions = interactions;
      this.interactionVars = interactionVars;
      this.interactionConfig = interactionConfig;
      this.droppedItemAnimation = droppedItemAnimation;
      this.tagIndexes = tagIndexes;
      this.itemAppearanceConditions = itemAppearanceConditions;
      this.displayEntityStatsHUD = displayEntityStatsHUD;
      this.pullbackConfig = pullbackConfig;
      this.clipsGeometry = clipsGeometry;
      this.renderDeployablePreview = renderDeployablePreview;
      this.hudUI = hudUI;
   }

   public ItemBase(@Nonnull ItemBase other) {
      this.id = other.id;
      this.model = other.model;
      this.scale = other.scale;
      this.texture = other.texture;
      this.animation = other.animation;
      this.playerAnimationsId = other.playerAnimationsId;
      this.usePlayerAnimations = other.usePlayerAnimations;
      this.maxStack = other.maxStack;
      this.reticleIndex = other.reticleIndex;
      this.icon = other.icon;
      this.iconProperties = other.iconProperties;
      this.translationProperties = other.translationProperties;
      this.itemLevel = other.itemLevel;
      this.qualityIndex = other.qualityIndex;
      this.resourceTypes = other.resourceTypes;
      this.consumable = other.consumable;
      this.variant = other.variant;
      this.blockId = other.blockId;
      this.tool = other.tool;
      this.weapon = other.weapon;
      this.armor = other.armor;
      this.gliderConfig = other.gliderConfig;
      this.utility = other.utility;
      this.blockSelectorTool = other.blockSelectorTool;
      this.builderToolData = other.builderToolData;
      this.itemEntity = other.itemEntity;
      this.set = other.set;
      this.categories = other.categories;
      this.subCategory = other.subCategory;
      this.particles = other.particles;
      this.firstPersonParticles = other.firstPersonParticles;
      this.trails = other.trails;
      this.light = other.light;
      this.durability = other.durability;
      this.soundEventIndex = other.soundEventIndex;
      this.itemSoundSetIndex = other.itemSoundSetIndex;
      this.interactions = other.interactions;
      this.interactionVars = other.interactionVars;
      this.interactionConfig = other.interactionConfig;
      this.droppedItemAnimation = other.droppedItemAnimation;
      this.tagIndexes = other.tagIndexes;
      this.itemAppearanceConditions = other.itemAppearanceConditions;
      this.displayEntityStatsHUD = other.displayEntityStatsHUD;
      this.pullbackConfig = other.pullbackConfig;
      this.clipsGeometry = other.clipsGeometry;
      this.renderDeployablePreview = other.renderDeployablePreview;
      this.hudUI = other.hudUI;
   }

   @Nonnull
   public static ItemBase deserialize(@Nonnull ByteBuf buf, int offset) {
      if (buf.readableBytes() - offset < 260) {
         throw ProtocolException.bufferTooSmall("ItemBase", 260, buf.readableBytes() - offset);
      }

      ItemBase obj = new ItemBase();
      byte[] nullBits = PacketIO.readBytes(buf, offset, 5);
      obj.scale = buf.getFloatLE(offset + 5);
      obj.usePlayerAnimations = buf.getByte(offset + 9) != 0;
      obj.maxStack = buf.getIntLE(offset + 10);
      obj.reticleIndex = buf.getIntLE(offset + 14);
      if ((nullBits[0] & 1) != 0) {
         obj.iconProperties = AssetIconProperties.deserialize(buf, offset + 18);
      }

      obj.itemLevel = buf.getIntLE(offset + 43);
      obj.qualityIndex = buf.getIntLE(offset + 47);
      obj.consumable = buf.getByte(offset + 51) != 0;
      obj.variant = buf.getByte(offset + 52) != 0;
      obj.blockId = buf.getIntLE(offset + 53);
      if ((nullBits[0] & 2) != 0) {
         obj.gliderConfig = ItemGlider.deserialize(buf, offset + 57);
      }

      if ((nullBits[0] & 4) != 0) {
         obj.blockSelectorTool = BlockSelectorToolData.deserialize(buf, offset + 73);
      }

      if ((nullBits[0] & 8) != 0) {
         obj.light = ColorLight.deserialize(buf, offset + 77);
      }

      obj.durability = buf.getDoubleLE(offset + 81);
      obj.soundEventIndex = buf.getIntLE(offset + 89);
      obj.itemSoundSetIndex = buf.getIntLE(offset + 93);
      if ((nullBits[0] & 16) != 0) {
         obj.pullbackConfig = ItemPullbackConfiguration.deserialize(buf, offset + 97);
      }

      obj.clipsGeometry = buf.getByte(offset + 146) != 0;
      obj.renderDeployablePreview = buf.getByte(offset + 147) != 0;
      if ((nullBits[0] & 32) != 0) {
         int varPosBase0 = buf.getIntLE(offset + 148);
         if (varPosBase0 < 0 || varPosBase0 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("Id", varPosBase0, buf.readableBytes());
         }

         int varPos0 = offset + 260 + varPosBase0;
         int idLen = VarInt.peek(buf, varPos0);
         if (idLen < 0) {
            throw ProtocolException.invalidVarInt("Id");
         }

         int idVarIntLen = VarInt.size(idLen);
         if (idLen > 4096000) {
            throw ProtocolException.stringTooLong("Id", idLen, 4096000);
         }

         if (varPos0 + idVarIntLen + idLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Id", varPos0 + idVarIntLen + idLen, buf.readableBytes());
         }

         obj.id = PacketIO.readVarString(buf, varPos0, PacketIO.UTF8);
      }

      if ((nullBits[0] & 64) != 0) {
         int varPosBase1 = buf.getIntLE(offset + 152);
         if (varPosBase1 < 0 || varPosBase1 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("Model", varPosBase1, buf.readableBytes());
         }

         int varPos1 = offset + 260 + varPosBase1;
         int modelLen = VarInt.peek(buf, varPos1);
         if (modelLen < 0) {
            throw ProtocolException.invalidVarInt("Model");
         }

         int modelVarIntLen = VarInt.size(modelLen);
         if (modelLen > 4096000) {
            throw ProtocolException.stringTooLong("Model", modelLen, 4096000);
         }

         if (varPos1 + modelVarIntLen + modelLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Model", varPos1 + modelVarIntLen + modelLen, buf.readableBytes());
         }

         obj.model = PacketIO.readVarString(buf, varPos1, PacketIO.UTF8);
      }

      if ((nullBits[0] & 128) != 0) {
         int varPosBase2 = buf.getIntLE(offset + 156);
         if (varPosBase2 < 0 || varPosBase2 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("Texture", varPosBase2, buf.readableBytes());
         }

         int varPos2 = offset + 260 + varPosBase2;
         int textureLen = VarInt.peek(buf, varPos2);
         if (textureLen < 0) {
            throw ProtocolException.invalidVarInt("Texture");
         }

         int textureVarIntLen = VarInt.size(textureLen);
         if (textureLen > 4096000) {
            throw ProtocolException.stringTooLong("Texture", textureLen, 4096000);
         }

         if (varPos2 + textureVarIntLen + textureLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Texture", varPos2 + textureVarIntLen + textureLen, buf.readableBytes());
         }

         obj.texture = PacketIO.readVarString(buf, varPos2, PacketIO.UTF8);
      }

      if ((nullBits[1] & 1) != 0) {
         int varPosBase3 = buf.getIntLE(offset + 160);
         if (varPosBase3 < 0 || varPosBase3 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("Animation", varPosBase3, buf.readableBytes());
         }

         int varPos3 = offset + 260 + varPosBase3;
         int animationLen = VarInt.peek(buf, varPos3);
         if (animationLen < 0) {
            throw ProtocolException.invalidVarInt("Animation");
         }

         int animationVarIntLen = VarInt.size(animationLen);
         if (animationLen > 4096000) {
            throw ProtocolException.stringTooLong("Animation", animationLen, 4096000);
         }

         if (varPos3 + animationVarIntLen + animationLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Animation", varPos3 + animationVarIntLen + animationLen, buf.readableBytes());
         }

         obj.animation = PacketIO.readVarString(buf, varPos3, PacketIO.UTF8);
      }

      if ((nullBits[1] & 2) != 0) {
         int varPosBase4 = buf.getIntLE(offset + 164);
         if (varPosBase4 < 0 || varPosBase4 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("PlayerAnimationsId", varPosBase4, buf.readableBytes());
         }

         int varPos4 = offset + 260 + varPosBase4;
         int playerAnimationsIdLen = VarInt.peek(buf, varPos4);
         if (playerAnimationsIdLen < 0) {
            throw ProtocolException.invalidVarInt("PlayerAnimationsId");
         }

         int playerAnimationsIdVarIntLen = VarInt.size(playerAnimationsIdLen);
         if (playerAnimationsIdLen > 4096000) {
            throw ProtocolException.stringTooLong("PlayerAnimationsId", playerAnimationsIdLen, 4096000);
         }

         if (varPos4 + playerAnimationsIdVarIntLen + playerAnimationsIdLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("PlayerAnimationsId", varPos4 + playerAnimationsIdVarIntLen + playerAnimationsIdLen, buf.readableBytes());
         }

         obj.playerAnimationsId = PacketIO.readVarString(buf, varPos4, PacketIO.UTF8);
      }

      if ((nullBits[1] & 4) != 0) {
         int varPosBase5 = buf.getIntLE(offset + 168);
         if (varPosBase5 < 0 || varPosBase5 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("Icon", varPosBase5, buf.readableBytes());
         }

         int varPos5 = offset + 260 + varPosBase5;
         int iconLen = VarInt.peek(buf, varPos5);
         if (iconLen < 0) {
            throw ProtocolException.invalidVarInt("Icon");
         }

         int iconVarIntLen = VarInt.size(iconLen);
         if (iconLen > 4096000) {
            throw ProtocolException.stringTooLong("Icon", iconLen, 4096000);
         }

         if (varPos5 + iconVarIntLen + iconLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Icon", varPos5 + iconVarIntLen + iconLen, buf.readableBytes());
         }

         obj.icon = PacketIO.readVarString(buf, varPos5, PacketIO.UTF8);
      }

      if ((nullBits[1] & 8) != 0) {
         int varPosBase6 = buf.getIntLE(offset + 172);
         if (varPosBase6 < 0 || varPosBase6 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("TranslationProperties", varPosBase6, buf.readableBytes());
         }

         int varPos6 = offset + 260 + varPosBase6;
         obj.translationProperties = ItemTranslationProperties.deserialize(buf, varPos6);
      }

      if ((nullBits[1] & 16) != 0) {
         int varPosBase7 = buf.getIntLE(offset + 176);
         if (varPosBase7 < 0 || varPosBase7 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("ResourceTypes", varPosBase7, buf.readableBytes());
         }

         int varPos7 = offset + 260 + varPosBase7;
         int resourceTypesCount = VarInt.peek(buf, varPos7);
         if (resourceTypesCount < 0) {
            throw ProtocolException.invalidVarInt("ResourceTypes");
         }

         int varIntLen = VarInt.size(resourceTypesCount);
         if (resourceTypesCount > 4096000) {
            throw ProtocolException.arrayTooLong("ResourceTypes", resourceTypesCount, 4096000);
         }

         if (varPos7 + varIntLen + resourceTypesCount * 5L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("ResourceTypes", varPos7 + varIntLen + resourceTypesCount * 5, buf.readableBytes());
         }

         obj.resourceTypes = new ItemResourceType[resourceTypesCount];
         int elemPos = varPos7 + varIntLen;

         for (int i = 0; i < resourceTypesCount; i++) {
            obj.resourceTypes[i] = ItemResourceType.deserialize(buf, elemPos);
            elemPos += ItemResourceType.computeBytesConsumed(buf, elemPos);
         }
      }

      if ((nullBits[1] & 32) != 0) {
         int varPosBase8 = buf.getIntLE(offset + 180);
         if (varPosBase8 < 0 || varPosBase8 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("Tool", varPosBase8, buf.readableBytes());
         }

         int varPos8 = offset + 260 + varPosBase8;
         obj.tool = ItemTool.deserialize(buf, varPos8);
      }

      if ((nullBits[1] & 64) != 0) {
         int varPosBase9 = buf.getIntLE(offset + 184);
         if (varPosBase9 < 0 || varPosBase9 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("Weapon", varPosBase9, buf.readableBytes());
         }

         int varPos9 = offset + 260 + varPosBase9;
         obj.weapon = ItemWeapon.deserialize(buf, varPos9);
      }

      if ((nullBits[1] & 128) != 0) {
         int varPosBase10 = buf.getIntLE(offset + 188);
         if (varPosBase10 < 0 || varPosBase10 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("Armor", varPosBase10, buf.readableBytes());
         }

         int varPos10 = offset + 260 + varPosBase10;
         obj.armor = ItemArmor.deserialize(buf, varPos10);
      }

      if ((nullBits[2] & 1) != 0) {
         int varPosBase11 = buf.getIntLE(offset + 192);
         if (varPosBase11 < 0 || varPosBase11 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("Utility", varPosBase11, buf.readableBytes());
         }

         int varPos11 = offset + 260 + varPosBase11;
         obj.utility = ItemUtility.deserialize(buf, varPos11);
      }

      if ((nullBits[2] & 2) != 0) {
         int varPosBase12 = buf.getIntLE(offset + 196);
         if (varPosBase12 < 0 || varPosBase12 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("BuilderToolData", varPosBase12, buf.readableBytes());
         }

         int varPos12 = offset + 260 + varPosBase12;
         obj.builderToolData = BuilderToolState.deserialize(buf, varPos12);
      }

      if ((nullBits[2] & 4) != 0) {
         int varPosBase13 = buf.getIntLE(offset + 200);
         if (varPosBase13 < 0 || varPosBase13 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("ItemEntity", varPosBase13, buf.readableBytes());
         }

         int varPos13 = offset + 260 + varPosBase13;
         obj.itemEntity = ItemEntityConfig.deserialize(buf, varPos13);
      }

      if ((nullBits[2] & 8) != 0) {
         int varPosBase14 = buf.getIntLE(offset + 204);
         if (varPosBase14 < 0 || varPosBase14 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("Set", varPosBase14, buf.readableBytes());
         }

         int varPos14 = offset + 260 + varPosBase14;
         int setLen = VarInt.peek(buf, varPos14);
         if (setLen < 0) {
            throw ProtocolException.invalidVarInt("Set");
         }

         int setVarIntLen = VarInt.size(setLen);
         if (setLen > 4096000) {
            throw ProtocolException.stringTooLong("Set", setLen, 4096000);
         }

         if (varPos14 + setVarIntLen + setLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Set", varPos14 + setVarIntLen + setLen, buf.readableBytes());
         }

         obj.set = PacketIO.readVarString(buf, varPos14, PacketIO.UTF8);
      }

      if ((nullBits[2] & 16) != 0) {
         int varPosBase15 = buf.getIntLE(offset + 208);
         if (varPosBase15 < 0 || varPosBase15 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("Categories", varPosBase15, buf.readableBytes());
         }

         int varPos15 = offset + 260 + varPosBase15;
         int categoriesCount = VarInt.peek(buf, varPos15);
         if (categoriesCount < 0) {
            throw ProtocolException.invalidVarInt("Categories");
         }

         int varIntLen = VarInt.size(categoriesCount);
         if (categoriesCount > 4096000) {
            throw ProtocolException.arrayTooLong("Categories", categoriesCount, 4096000);
         }

         if (varPos15 + varIntLen + categoriesCount * 1L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Categories", varPos15 + varIntLen + categoriesCount * 1, buf.readableBytes());
         }

         obj.categories = new String[categoriesCount];
         int elemPos = varPos15 + varIntLen;

         for (int i = 0; i < categoriesCount; i++) {
            int strLen = VarInt.peek(buf, elemPos);
            if (strLen < 0) {
               throw ProtocolException.invalidVarInt("categories[" + i + "]");
            }

            int strVarLen = VarInt.size(strLen);
            if (strLen > 4096000) {
               throw ProtocolException.stringTooLong("categories[" + i + "]", strLen, 4096000);
            }

            if (elemPos + strVarLen + strLen > buf.readableBytes()) {
               throw ProtocolException.bufferTooSmall("categories[" + i + "]", elemPos + strVarLen + strLen, buf.readableBytes());
            }

            obj.categories[i] = PacketIO.readVarString(buf, elemPos);
            elemPos += strVarLen + strLen;
         }
      }

      if ((nullBits[2] & 32) != 0) {
         int varPosBase16 = buf.getIntLE(offset + 212);
         if (varPosBase16 < 0 || varPosBase16 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("SubCategory", varPosBase16, buf.readableBytes());
         }

         int varPos16 = offset + 260 + varPosBase16;
         int subCategoryLen = VarInt.peek(buf, varPos16);
         if (subCategoryLen < 0) {
            throw ProtocolException.invalidVarInt("SubCategory");
         }

         int subCategoryVarIntLen = VarInt.size(subCategoryLen);
         if (subCategoryLen > 4096000) {
            throw ProtocolException.stringTooLong("SubCategory", subCategoryLen, 4096000);
         }

         if (varPos16 + subCategoryVarIntLen + subCategoryLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("SubCategory", varPos16 + subCategoryVarIntLen + subCategoryLen, buf.readableBytes());
         }

         obj.subCategory = PacketIO.readVarString(buf, varPos16, PacketIO.UTF8);
      }

      if ((nullBits[2] & 64) != 0) {
         int varPosBase17 = buf.getIntLE(offset + 216);
         if (varPosBase17 < 0 || varPosBase17 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("Particles", varPosBase17, buf.readableBytes());
         }

         int varPos17 = offset + 260 + varPosBase17;
         int particlesCount = VarInt.peek(buf, varPos17);
         if (particlesCount < 0) {
            throw ProtocolException.invalidVarInt("Particles");
         }

         int varIntLen = VarInt.size(particlesCount);
         if (particlesCount > 4096000) {
            throw ProtocolException.arrayTooLong("Particles", particlesCount, 4096000);
         }

         if (varPos17 + varIntLen + particlesCount * 34L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Particles", varPos17 + varIntLen + particlesCount * 34, buf.readableBytes());
         }

         obj.particles = new ModelParticle[particlesCount];
         int elemPos = varPos17 + varIntLen;

         for (int i = 0; i < particlesCount; i++) {
            obj.particles[i] = ModelParticle.deserialize(buf, elemPos);
            elemPos += ModelParticle.computeBytesConsumed(buf, elemPos);
         }
      }

      if ((nullBits[2] & 128) != 0) {
         int varPosBase18 = buf.getIntLE(offset + 220);
         if (varPosBase18 < 0 || varPosBase18 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("FirstPersonParticles", varPosBase18, buf.readableBytes());
         }

         int varPos18 = offset + 260 + varPosBase18;
         int firstPersonParticlesCount = VarInt.peek(buf, varPos18);
         if (firstPersonParticlesCount < 0) {
            throw ProtocolException.invalidVarInt("FirstPersonParticles");
         }

         int varIntLen = VarInt.size(firstPersonParticlesCount);
         if (firstPersonParticlesCount > 4096000) {
            throw ProtocolException.arrayTooLong("FirstPersonParticles", firstPersonParticlesCount, 4096000);
         }

         if (varPos18 + varIntLen + firstPersonParticlesCount * 34L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("FirstPersonParticles", varPos18 + varIntLen + firstPersonParticlesCount * 34, buf.readableBytes());
         }

         obj.firstPersonParticles = new ModelParticle[firstPersonParticlesCount];
         int elemPos = varPos18 + varIntLen;

         for (int i = 0; i < firstPersonParticlesCount; i++) {
            obj.firstPersonParticles[i] = ModelParticle.deserialize(buf, elemPos);
            elemPos += ModelParticle.computeBytesConsumed(buf, elemPos);
         }
      }

      if ((nullBits[3] & 1) != 0) {
         int varPosBase19 = buf.getIntLE(offset + 224);
         if (varPosBase19 < 0 || varPosBase19 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("Trails", varPosBase19, buf.readableBytes());
         }

         int varPos19 = offset + 260 + varPosBase19;
         int trailsCount = VarInt.peek(buf, varPos19);
         if (trailsCount < 0) {
            throw ProtocolException.invalidVarInt("Trails");
         }

         int varIntLen = VarInt.size(trailsCount);
         if (trailsCount > 4096000) {
            throw ProtocolException.arrayTooLong("Trails", trailsCount, 4096000);
         }

         if (varPos19 + varIntLen + trailsCount * 27L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("Trails", varPos19 + varIntLen + trailsCount * 27, buf.readableBytes());
         }

         obj.trails = new ModelTrail[trailsCount];
         int elemPos = varPos19 + varIntLen;

         for (int i = 0; i < trailsCount; i++) {
            obj.trails[i] = ModelTrail.deserialize(buf, elemPos);
            elemPos += ModelTrail.computeBytesConsumed(buf, elemPos);
         }
      }

      if ((nullBits[3] & 2) != 0) {
         int varPosBase20 = buf.getIntLE(offset + 228);
         if (varPosBase20 < 0 || varPosBase20 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("Interactions", varPosBase20, buf.readableBytes());
         }

         int varPos20 = offset + 260 + varPosBase20;
         int interactionsCount = VarInt.peek(buf, varPos20);
         if (interactionsCount < 0) {
            throw ProtocolException.invalidVarInt("Interactions");
         }

         int varIntLen = VarInt.size(interactionsCount);
         if (interactionsCount > 4096000) {
            throw ProtocolException.dictionaryTooLarge("Interactions", interactionsCount, 4096000);
         }

         obj.interactions = new HashMap<>(interactionsCount);
         int dictPos = varPos20 + varIntLen;

         for (int i = 0; i < interactionsCount; i++) {
            InteractionType key = InteractionType.fromValue(buf.getByte(dictPos));
            int val = buf.getIntLE(++dictPos);
            dictPos += 4;
            if (obj.interactions.put(key, val) != null) {
               throw ProtocolException.duplicateKey("interactions", key);
            }
         }
      }

      if ((nullBits[3] & 4) != 0) {
         int varPosBase21 = buf.getIntLE(offset + 232);
         if (varPosBase21 < 0 || varPosBase21 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("InteractionVars", varPosBase21, buf.readableBytes());
         }

         int varPos21 = offset + 260 + varPosBase21;
         int interactionVarsCount = VarInt.peek(buf, varPos21);
         if (interactionVarsCount < 0) {
            throw ProtocolException.invalidVarInt("InteractionVars");
         }

         int varIntLen = VarInt.size(interactionVarsCount);
         if (interactionVarsCount > 4096000) {
            throw ProtocolException.dictionaryTooLarge("InteractionVars", interactionVarsCount, 4096000);
         }

         obj.interactionVars = new HashMap<>(interactionVarsCount);
         int dictPos = varPos21 + varIntLen;

         for (int i = 0; i < interactionVarsCount; i++) {
            int keyLen = VarInt.peek(buf, dictPos);
            if (keyLen < 0) {
               throw ProtocolException.invalidVarInt("key");
            }

            int keyVarLen = VarInt.size(keyLen);
            if (keyLen > 4096000) {
               throw ProtocolException.stringTooLong("key", keyLen, 4096000);
            }

            if (dictPos + keyVarLen + keyLen > buf.readableBytes()) {
               throw ProtocolException.bufferTooSmall("key", dictPos + keyVarLen + keyLen, buf.readableBytes());
            }

            String key = PacketIO.readVarString(buf, dictPos);
            dictPos += keyVarLen + keyLen;
            int val = buf.getIntLE(dictPos);
            dictPos += 4;
            if (obj.interactionVars.put(key, val) != null) {
               throw ProtocolException.duplicateKey("interactionVars", key);
            }
         }
      }

      if ((nullBits[3] & 8) != 0) {
         int varPosBase22 = buf.getIntLE(offset + 236);
         if (varPosBase22 < 0 || varPosBase22 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("InteractionConfig", varPosBase22, buf.readableBytes());
         }

         int varPos22 = offset + 260 + varPosBase22;
         obj.interactionConfig = InteractionConfiguration.deserialize(buf, varPos22);
      }

      if ((nullBits[3] & 16) != 0) {
         int varPosBase23 = buf.getIntLE(offset + 240);
         if (varPosBase23 < 0 || varPosBase23 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("DroppedItemAnimation", varPosBase23, buf.readableBytes());
         }

         int varPos23 = offset + 260 + varPosBase23;
         int droppedItemAnimationLen = VarInt.peek(buf, varPos23);
         if (droppedItemAnimationLen < 0) {
            throw ProtocolException.invalidVarInt("DroppedItemAnimation");
         }

         int droppedItemAnimationVarIntLen = VarInt.size(droppedItemAnimationLen);
         if (droppedItemAnimationLen > 4096000) {
            throw ProtocolException.stringTooLong("DroppedItemAnimation", droppedItemAnimationLen, 4096000);
         }

         if (varPos23 + droppedItemAnimationVarIntLen + droppedItemAnimationLen > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall(
               "DroppedItemAnimation", varPos23 + droppedItemAnimationVarIntLen + droppedItemAnimationLen, buf.readableBytes()
            );
         }

         obj.droppedItemAnimation = PacketIO.readVarString(buf, varPos23, PacketIO.UTF8);
      }

      if ((nullBits[3] & 32) != 0) {
         int varPosBase24 = buf.getIntLE(offset + 244);
         if (varPosBase24 < 0 || varPosBase24 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("TagIndexes", varPosBase24, buf.readableBytes());
         }

         int varPos24 = offset + 260 + varPosBase24;
         int tagIndexesCount = VarInt.peek(buf, varPos24);
         if (tagIndexesCount < 0) {
            throw ProtocolException.invalidVarInt("TagIndexes");
         }

         int varIntLen = VarInt.size(tagIndexesCount);
         if (tagIndexesCount > 4096000) {
            throw ProtocolException.arrayTooLong("TagIndexes", tagIndexesCount, 4096000);
         }

         if (varPos24 + varIntLen + tagIndexesCount * 4L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("TagIndexes", varPos24 + varIntLen + tagIndexesCount * 4, buf.readableBytes());
         }

         obj.tagIndexes = new int[tagIndexesCount];

         for (int i = 0; i < tagIndexesCount; i++) {
            obj.tagIndexes[i] = buf.getIntLE(varPos24 + varIntLen + i * 4);
         }
      }

      if ((nullBits[3] & 64) != 0) {
         int varPosBase25 = buf.getIntLE(offset + 248);
         if (varPosBase25 < 0 || varPosBase25 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("ItemAppearanceConditions", varPosBase25, buf.readableBytes());
         }

         int varPos25 = offset + 260 + varPosBase25;
         int itemAppearanceConditionsCount = VarInt.peek(buf, varPos25);
         if (itemAppearanceConditionsCount < 0) {
            throw ProtocolException.invalidVarInt("ItemAppearanceConditions");
         }

         int varIntLen = VarInt.size(itemAppearanceConditionsCount);
         if (itemAppearanceConditionsCount > 4096000) {
            throw ProtocolException.dictionaryTooLarge("ItemAppearanceConditions", itemAppearanceConditionsCount, 4096000);
         }

         obj.itemAppearanceConditions = new HashMap<>(itemAppearanceConditionsCount);
         int dictPos = varPos25 + varIntLen;

         for (int i = 0; i < itemAppearanceConditionsCount; i++) {
            int key = buf.getIntLE(dictPos);
            dictPos += 4;
            int valLen = VarInt.peek(buf, dictPos);
            if (valLen < 0) {
               throw ProtocolException.invalidVarInt("val");
            }

            int valVarLen = VarInt.size(valLen);
            if (valLen > 64) {
               throw ProtocolException.arrayTooLong("val", valLen, 64);
            }

            if (dictPos + valVarLen + valLen * 18L > buf.readableBytes()) {
               throw ProtocolException.bufferTooSmall("val", dictPos + valVarLen + valLen * 18, buf.readableBytes());
            }

            dictPos += valVarLen;
            ItemAppearanceCondition[] val = new ItemAppearanceCondition[valLen];

            for (int valIdx = 0; valIdx < valLen; valIdx++) {
               val[valIdx] = ItemAppearanceCondition.deserialize(buf, dictPos);
               dictPos += ItemAppearanceCondition.computeBytesConsumed(buf, dictPos);
            }

            if (obj.itemAppearanceConditions.put(key, val) != null) {
               throw ProtocolException.duplicateKey("itemAppearanceConditions", key);
            }
         }
      }

      if ((nullBits[3] & 128) != 0) {
         int varPosBase26 = buf.getIntLE(offset + 252);
         if (varPosBase26 < 0 || varPosBase26 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("DisplayEntityStatsHUD", varPosBase26, buf.readableBytes());
         }

         int varPos26 = offset + 260 + varPosBase26;
         int displayEntityStatsHUDCount = VarInt.peek(buf, varPos26);
         if (displayEntityStatsHUDCount < 0) {
            throw ProtocolException.invalidVarInt("DisplayEntityStatsHUD");
         }

         int varIntLen = VarInt.size(displayEntityStatsHUDCount);
         if (displayEntityStatsHUDCount > 4096000) {
            throw ProtocolException.arrayTooLong("DisplayEntityStatsHUD", displayEntityStatsHUDCount, 4096000);
         }

         if (varPos26 + varIntLen + displayEntityStatsHUDCount * 4L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("DisplayEntityStatsHUD", varPos26 + varIntLen + displayEntityStatsHUDCount * 4, buf.readableBytes());
         }

         obj.displayEntityStatsHUD = new int[displayEntityStatsHUDCount];

         for (int i = 0; i < displayEntityStatsHUDCount; i++) {
            obj.displayEntityStatsHUD[i] = buf.getIntLE(varPos26 + varIntLen + i * 4);
         }
      }

      if ((nullBits[4] & 1) != 0) {
         int varPosBase27 = buf.getIntLE(offset + 256);
         if (varPosBase27 < 0 || varPosBase27 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("HudUI", varPosBase27, buf.readableBytes());
         }

         int varPos27 = offset + 260 + varPosBase27;
         int hudUICount = VarInt.peek(buf, varPos27);
         if (hudUICount < 0) {
            throw ProtocolException.invalidVarInt("HudUI");
         }

         int varIntLen = VarInt.size(hudUICount);
         if (hudUICount > 4096000) {
            throw ProtocolException.arrayTooLong("HudUI", hudUICount, 4096000);
         }

         if (varPos27 + varIntLen + hudUICount * 2L > buf.readableBytes()) {
            throw ProtocolException.bufferTooSmall("HudUI", varPos27 + varIntLen + hudUICount * 2, buf.readableBytes());
         }

         obj.hudUI = new ItemHudUI[hudUICount];
         int elemPos = varPos27 + varIntLen;

         for (int i = 0; i < hudUICount; i++) {
            obj.hudUI[i] = ItemHudUI.deserialize(buf, elemPos);
            elemPos += ItemHudUI.computeBytesConsumed(buf, elemPos);
         }
      }

      return obj;
   }

   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
      byte[] nullBits = PacketIO.readBytes(buf, offset, 5);
      int maxEnd = 260;
      if ((nullBits[0] & 32) != 0) {
         int fieldOffset0 = buf.getIntLE(offset + 148);
         if (fieldOffset0 < 0 || fieldOffset0 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("Id", fieldOffset0, maxEnd);
         }

         int pos0 = offset + 260 + fieldOffset0;
         int sl = VarInt.peek(buf, pos0);
         pos0 += VarInt.size(sl) + sl;
         if (pos0 - offset > maxEnd) {
            maxEnd = pos0 - offset;
         }
      }

      if ((nullBits[0] & 64) != 0) {
         int fieldOffset1 = buf.getIntLE(offset + 152);
         if (fieldOffset1 < 0 || fieldOffset1 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("Model", fieldOffset1, maxEnd);
         }

         int pos1 = offset + 260 + fieldOffset1;
         int sl = VarInt.peek(buf, pos1);
         pos1 += VarInt.size(sl) + sl;
         if (pos1 - offset > maxEnd) {
            maxEnd = pos1 - offset;
         }
      }

      if ((nullBits[0] & 128) != 0) {
         int fieldOffset2 = buf.getIntLE(offset + 156);
         if (fieldOffset2 < 0 || fieldOffset2 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("Texture", fieldOffset2, maxEnd);
         }

         int pos2 = offset + 260 + fieldOffset2;
         int sl = VarInt.peek(buf, pos2);
         pos2 += VarInt.size(sl) + sl;
         if (pos2 - offset > maxEnd) {
            maxEnd = pos2 - offset;
         }
      }

      if ((nullBits[1] & 1) != 0) {
         int fieldOffset3 = buf.getIntLE(offset + 160);
         if (fieldOffset3 < 0 || fieldOffset3 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("Animation", fieldOffset3, maxEnd);
         }

         int pos3 = offset + 260 + fieldOffset3;
         int sl = VarInt.peek(buf, pos3);
         pos3 += VarInt.size(sl) + sl;
         if (pos3 - offset > maxEnd) {
            maxEnd = pos3 - offset;
         }
      }

      if ((nullBits[1] & 2) != 0) {
         int fieldOffset4 = buf.getIntLE(offset + 164);
         if (fieldOffset4 < 0 || fieldOffset4 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("PlayerAnimationsId", fieldOffset4, maxEnd);
         }

         int pos4 = offset + 260 + fieldOffset4;
         int sl = VarInt.peek(buf, pos4);
         pos4 += VarInt.size(sl) + sl;
         if (pos4 - offset > maxEnd) {
            maxEnd = pos4 - offset;
         }
      }

      if ((nullBits[1] & 4) != 0) {
         int fieldOffset5 = buf.getIntLE(offset + 168);
         if (fieldOffset5 < 0 || fieldOffset5 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("Icon", fieldOffset5, maxEnd);
         }

         int pos5 = offset + 260 + fieldOffset5;
         int sl = VarInt.peek(buf, pos5);
         pos5 += VarInt.size(sl) + sl;
         if (pos5 - offset > maxEnd) {
            maxEnd = pos5 - offset;
         }
      }

      if ((nullBits[1] & 8) != 0) {
         int fieldOffset6 = buf.getIntLE(offset + 172);
         if (fieldOffset6 < 0 || fieldOffset6 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("TranslationProperties", fieldOffset6, maxEnd);
         }

         int pos6 = offset + 260 + fieldOffset6;
         pos6 += ItemTranslationProperties.computeBytesConsumed(buf, pos6);
         if (pos6 - offset > maxEnd) {
            maxEnd = pos6 - offset;
         }
      }

      if ((nullBits[1] & 16) != 0) {
         int fieldOffset7 = buf.getIntLE(offset + 176);
         if (fieldOffset7 < 0 || fieldOffset7 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("ResourceTypes", fieldOffset7, maxEnd);
         }

         int pos7 = offset + 260 + fieldOffset7;
         int arrLen = VarInt.peek(buf, pos7);
         pos7 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            pos7 += ItemResourceType.computeBytesConsumed(buf, pos7);
         }

         if (pos7 - offset > maxEnd) {
            maxEnd = pos7 - offset;
         }
      }

      if ((nullBits[1] & 32) != 0) {
         int fieldOffset8 = buf.getIntLE(offset + 180);
         if (fieldOffset8 < 0 || fieldOffset8 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("Tool", fieldOffset8, maxEnd);
         }

         int pos8 = offset + 260 + fieldOffset8;
         pos8 += ItemTool.computeBytesConsumed(buf, pos8);
         if (pos8 - offset > maxEnd) {
            maxEnd = pos8 - offset;
         }
      }

      if ((nullBits[1] & 64) != 0) {
         int fieldOffset9 = buf.getIntLE(offset + 184);
         if (fieldOffset9 < 0 || fieldOffset9 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("Weapon", fieldOffset9, maxEnd);
         }

         int pos9 = offset + 260 + fieldOffset9;
         pos9 += ItemWeapon.computeBytesConsumed(buf, pos9);
         if (pos9 - offset > maxEnd) {
            maxEnd = pos9 - offset;
         }
      }

      if ((nullBits[1] & 128) != 0) {
         int fieldOffset10 = buf.getIntLE(offset + 188);
         if (fieldOffset10 < 0 || fieldOffset10 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("Armor", fieldOffset10, maxEnd);
         }

         int pos10 = offset + 260 + fieldOffset10;
         pos10 += ItemArmor.computeBytesConsumed(buf, pos10);
         if (pos10 - offset > maxEnd) {
            maxEnd = pos10 - offset;
         }
      }

      if ((nullBits[2] & 1) != 0) {
         int fieldOffset11 = buf.getIntLE(offset + 192);
         if (fieldOffset11 < 0 || fieldOffset11 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("Utility", fieldOffset11, maxEnd);
         }

         int pos11 = offset + 260 + fieldOffset11;
         pos11 += ItemUtility.computeBytesConsumed(buf, pos11);
         if (pos11 - offset > maxEnd) {
            maxEnd = pos11 - offset;
         }
      }

      if ((nullBits[2] & 2) != 0) {
         int fieldOffset12 = buf.getIntLE(offset + 196);
         if (fieldOffset12 < 0 || fieldOffset12 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("BuilderToolData", fieldOffset12, maxEnd);
         }

         int pos12 = offset + 260 + fieldOffset12;
         pos12 += BuilderToolState.computeBytesConsumed(buf, pos12);
         if (pos12 - offset > maxEnd) {
            maxEnd = pos12 - offset;
         }
      }

      if ((nullBits[2] & 4) != 0) {
         int fieldOffset13 = buf.getIntLE(offset + 200);
         if (fieldOffset13 < 0 || fieldOffset13 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("ItemEntity", fieldOffset13, maxEnd);
         }

         int pos13 = offset + 260 + fieldOffset13;
         pos13 += ItemEntityConfig.computeBytesConsumed(buf, pos13);
         if (pos13 - offset > maxEnd) {
            maxEnd = pos13 - offset;
         }
      }

      if ((nullBits[2] & 8) != 0) {
         int fieldOffset14 = buf.getIntLE(offset + 204);
         if (fieldOffset14 < 0 || fieldOffset14 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("Set", fieldOffset14, maxEnd);
         }

         int pos14 = offset + 260 + fieldOffset14;
         int sl = VarInt.peek(buf, pos14);
         pos14 += VarInt.size(sl) + sl;
         if (pos14 - offset > maxEnd) {
            maxEnd = pos14 - offset;
         }
      }

      if ((nullBits[2] & 16) != 0) {
         int fieldOffset15 = buf.getIntLE(offset + 208);
         if (fieldOffset15 < 0 || fieldOffset15 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("Categories", fieldOffset15, maxEnd);
         }

         int pos15 = offset + 260 + fieldOffset15;
         int arrLen = VarInt.peek(buf, pos15);
         pos15 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            int sl = VarInt.peek(buf, pos15);
            pos15 += VarInt.size(sl) + sl;
         }

         if (pos15 - offset > maxEnd) {
            maxEnd = pos15 - offset;
         }
      }

      if ((nullBits[2] & 32) != 0) {
         int fieldOffset16 = buf.getIntLE(offset + 212);
         if (fieldOffset16 < 0 || fieldOffset16 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("SubCategory", fieldOffset16, maxEnd);
         }

         int pos16 = offset + 260 + fieldOffset16;
         int sl = VarInt.peek(buf, pos16);
         pos16 += VarInt.size(sl) + sl;
         if (pos16 - offset > maxEnd) {
            maxEnd = pos16 - offset;
         }
      }

      if ((nullBits[2] & 64) != 0) {
         int fieldOffset17 = buf.getIntLE(offset + 216);
         if (fieldOffset17 < 0 || fieldOffset17 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("Particles", fieldOffset17, maxEnd);
         }

         int pos17 = offset + 260 + fieldOffset17;
         int arrLen = VarInt.peek(buf, pos17);
         pos17 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            pos17 += ModelParticle.computeBytesConsumed(buf, pos17);
         }

         if (pos17 - offset > maxEnd) {
            maxEnd = pos17 - offset;
         }
      }

      if ((nullBits[2] & 128) != 0) {
         int fieldOffset18 = buf.getIntLE(offset + 220);
         if (fieldOffset18 < 0 || fieldOffset18 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("FirstPersonParticles", fieldOffset18, maxEnd);
         }

         int pos18 = offset + 260 + fieldOffset18;
         int arrLen = VarInt.peek(buf, pos18);
         pos18 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            pos18 += ModelParticle.computeBytesConsumed(buf, pos18);
         }

         if (pos18 - offset > maxEnd) {
            maxEnd = pos18 - offset;
         }
      }

      if ((nullBits[3] & 1) != 0) {
         int fieldOffset19 = buf.getIntLE(offset + 224);
         if (fieldOffset19 < 0 || fieldOffset19 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("Trails", fieldOffset19, maxEnd);
         }

         int pos19 = offset + 260 + fieldOffset19;
         int arrLen = VarInt.peek(buf, pos19);
         pos19 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            pos19 += ModelTrail.computeBytesConsumed(buf, pos19);
         }

         if (pos19 - offset > maxEnd) {
            maxEnd = pos19 - offset;
         }
      }

      if ((nullBits[3] & 2) != 0) {
         int fieldOffset20 = buf.getIntLE(offset + 228);
         if (fieldOffset20 < 0 || fieldOffset20 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("Interactions", fieldOffset20, maxEnd);
         }

         int pos20 = offset + 260 + fieldOffset20;
         int dictLen = VarInt.peek(buf, pos20);
         pos20 += VarInt.size(dictLen);

         for (int i = 0; i < dictLen; i++) {
            pos20 = ++pos20 + 4;
         }

         if (pos20 - offset > maxEnd) {
            maxEnd = pos20 - offset;
         }
      }

      if ((nullBits[3] & 4) != 0) {
         int fieldOffset21 = buf.getIntLE(offset + 232);
         if (fieldOffset21 < 0 || fieldOffset21 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("InteractionVars", fieldOffset21, maxEnd);
         }

         int pos21 = offset + 260 + fieldOffset21;
         int dictLen = VarInt.peek(buf, pos21);
         pos21 += VarInt.size(dictLen);

         for (int i = 0; i < dictLen; i++) {
            int sl = VarInt.peek(buf, pos21);
            pos21 += VarInt.size(sl) + sl;
            pos21 += 4;
         }

         if (pos21 - offset > maxEnd) {
            maxEnd = pos21 - offset;
         }
      }

      if ((nullBits[3] & 8) != 0) {
         int fieldOffset22 = buf.getIntLE(offset + 236);
         if (fieldOffset22 < 0 || fieldOffset22 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("InteractionConfig", fieldOffset22, maxEnd);
         }

         int pos22 = offset + 260 + fieldOffset22;
         pos22 += InteractionConfiguration.computeBytesConsumed(buf, pos22);
         if (pos22 - offset > maxEnd) {
            maxEnd = pos22 - offset;
         }
      }

      if ((nullBits[3] & 16) != 0) {
         int fieldOffset23 = buf.getIntLE(offset + 240);
         if (fieldOffset23 < 0 || fieldOffset23 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("DroppedItemAnimation", fieldOffset23, maxEnd);
         }

         int pos23 = offset + 260 + fieldOffset23;
         int sl = VarInt.peek(buf, pos23);
         pos23 += VarInt.size(sl) + sl;
         if (pos23 - offset > maxEnd) {
            maxEnd = pos23 - offset;
         }
      }

      if ((nullBits[3] & 32) != 0) {
         int fieldOffset24 = buf.getIntLE(offset + 244);
         if (fieldOffset24 < 0 || fieldOffset24 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("TagIndexes", fieldOffset24, maxEnd);
         }

         int pos24 = offset + 260 + fieldOffset24;
         int arrLen = VarInt.peek(buf, pos24);
         pos24 += VarInt.size(arrLen) + arrLen * 4;
         if (pos24 - offset > maxEnd) {
            maxEnd = pos24 - offset;
         }
      }

      if ((nullBits[3] & 64) != 0) {
         int fieldOffset25 = buf.getIntLE(offset + 248);
         if (fieldOffset25 < 0 || fieldOffset25 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("ItemAppearanceConditions", fieldOffset25, maxEnd);
         }

         int pos25 = offset + 260 + fieldOffset25;
         int dictLen = VarInt.peek(buf, pos25);
         pos25 += VarInt.size(dictLen);

         for (int i = 0; i < dictLen; i++) {
            pos25 += 4;
            int al = VarInt.peek(buf, pos25);
            pos25 += VarInt.size(al);

            for (int j = 0; j < al; j++) {
               pos25 += ItemAppearanceCondition.computeBytesConsumed(buf, pos25);
            }
         }

         if (pos25 - offset > maxEnd) {
            maxEnd = pos25 - offset;
         }
      }

      if ((nullBits[3] & 128) != 0) {
         int fieldOffset26 = buf.getIntLE(offset + 252);
         if (fieldOffset26 < 0 || fieldOffset26 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("DisplayEntityStatsHUD", fieldOffset26, maxEnd);
         }

         int pos26 = offset + 260 + fieldOffset26;
         int arrLen = VarInt.peek(buf, pos26);
         pos26 += VarInt.size(arrLen) + arrLen * 4;
         if (pos26 - offset > maxEnd) {
            maxEnd = pos26 - offset;
         }
      }

      if ((nullBits[4] & 1) != 0) {
         int fieldOffset27 = buf.getIntLE(offset + 256);
         if (fieldOffset27 < 0 || fieldOffset27 > buf.writerIndex() - offset - 260) {
            throw ProtocolException.invalidOffset("HudUI", fieldOffset27, maxEnd);
         }

         int pos27 = offset + 260 + fieldOffset27;
         int arrLen = VarInt.peek(buf, pos27);
         pos27 += VarInt.size(arrLen);

         for (int i = 0; i < arrLen; i++) {
            pos27 += ItemHudUI.computeBytesConsumed(buf, pos27);
         }

         if (pos27 - offset > maxEnd) {
            maxEnd = pos27 - offset;
         }
      }

      return maxEnd;
   }

   public void serialize(@Nonnull ByteBuf buf) {
      int startPos = buf.writerIndex();
      byte[] nullBits = new byte[5];
      if (this.iconProperties != null) {
         nullBits[0] = (byte)(nullBits[0] | 1);
      }

      if (this.gliderConfig != null) {
         nullBits[0] = (byte)(nullBits[0] | 2);
      }

      if (this.blockSelectorTool != null) {
         nullBits[0] = (byte)(nullBits[0] | 4);
      }

      if (this.light != null) {
         nullBits[0] = (byte)(nullBits[0] | 8);
      }

      if (this.pullbackConfig != null) {
         nullBits[0] = (byte)(nullBits[0] | 16);
      }

      if (this.id != null) {
         nullBits[0] = (byte)(nullBits[0] | 32);
      }

      if (this.model != null) {
         nullBits[0] = (byte)(nullBits[0] | 64);
      }

      if (this.texture != null) {
         nullBits[0] = (byte)(nullBits[0] | 128);
      }

      if (this.animation != null) {
         nullBits[1] = (byte)(nullBits[1] | 1);
      }

      if (this.playerAnimationsId != null) {
         nullBits[1] = (byte)(nullBits[1] | 2);
      }

      if (this.icon != null) {
         nullBits[1] = (byte)(nullBits[1] | 4);
      }

      if (this.translationProperties != null) {
         nullBits[1] = (byte)(nullBits[1] | 8);
      }

      if (this.resourceTypes != null) {
         nullBits[1] = (byte)(nullBits[1] | 16);
      }

      if (this.tool != null) {
         nullBits[1] = (byte)(nullBits[1] | 32);
      }

      if (this.weapon != null) {
         nullBits[1] = (byte)(nullBits[1] | 64);
      }

      if (this.armor != null) {
         nullBits[1] = (byte)(nullBits[1] | 128);
      }

      if (this.utility != null) {
         nullBits[2] = (byte)(nullBits[2] | 1);
      }

      if (this.builderToolData != null) {
         nullBits[2] = (byte)(nullBits[2] | 2);
      }

      if (this.itemEntity != null) {
         nullBits[2] = (byte)(nullBits[2] | 4);
      }

      if (this.set != null) {
         nullBits[2] = (byte)(nullBits[2] | 8);
      }

      if (this.categories != null) {
         nullBits[2] = (byte)(nullBits[2] | 16);
      }

      if (this.subCategory != null) {
         nullBits[2] = (byte)(nullBits[2] | 32);
      }

      if (this.particles != null) {
         nullBits[2] = (byte)(nullBits[2] | 64);
      }

      if (this.firstPersonParticles != null) {
         nullBits[2] = (byte)(nullBits[2] | 128);
      }

      if (this.trails != null) {
         nullBits[3] = (byte)(nullBits[3] | 1);
      }

      if (this.interactions != null) {
         nullBits[3] = (byte)(nullBits[3] | 2);
      }

      if (this.interactionVars != null) {
         nullBits[3] = (byte)(nullBits[3] | 4);
      }

      if (this.interactionConfig != null) {
         nullBits[3] = (byte)(nullBits[3] | 8);
      }

      if (this.droppedItemAnimation != null) {
         nullBits[3] = (byte)(nullBits[3] | 16);
      }

      if (this.tagIndexes != null) {
         nullBits[3] = (byte)(nullBits[3] | 32);
      }

      if (this.itemAppearanceConditions != null) {
         nullBits[3] = (byte)(nullBits[3] | 64);
      }

      if (this.displayEntityStatsHUD != null) {
         nullBits[3] = (byte)(nullBits[3] | 128);
      }

      if (this.hudUI != null) {
         nullBits[4] = (byte)(nullBits[4] | 1);
      }

      buf.writeBytes(nullBits);
      buf.writeFloatLE(this.scale);
      buf.writeByte(this.usePlayerAnimations ? 1 : 0);
      buf.writeIntLE(this.maxStack);
      buf.writeIntLE(this.reticleIndex);
      if (this.iconProperties != null) {
         this.iconProperties.serialize(buf);
      } else {
         buf.writeZero(25);
      }

      buf.writeIntLE(this.itemLevel);
      buf.writeIntLE(this.qualityIndex);
      buf.writeByte(this.consumable ? 1 : 0);
      buf.writeByte(this.variant ? 1 : 0);
      buf.writeIntLE(this.blockId);
      if (this.gliderConfig != null) {
         this.gliderConfig.serialize(buf);
      } else {
         buf.writeZero(16);
      }

      if (this.blockSelectorTool != null) {
         this.blockSelectorTool.serialize(buf);
      } else {
         buf.writeZero(4);
      }

      if (this.light != null) {
         this.light.serialize(buf);
      } else {
         buf.writeZero(4);
      }

      buf.writeDoubleLE(this.durability);
      buf.writeIntLE(this.soundEventIndex);
      buf.writeIntLE(this.itemSoundSetIndex);
      if (this.pullbackConfig != null) {
         this.pullbackConfig.serialize(buf);
      } else {
         buf.writeZero(49);
      }

      buf.writeByte(this.clipsGeometry ? 1 : 0);
      buf.writeByte(this.renderDeployablePreview ? 1 : 0);
      int idOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int modelOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int textureOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int animationOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int playerAnimationsIdOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int iconOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int translationPropertiesOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int resourceTypesOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int toolOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int weaponOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int armorOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int utilityOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int builderToolDataOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int itemEntityOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int setOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int categoriesOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int subCategoryOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int particlesOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int firstPersonParticlesOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int trailsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int interactionsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int interactionVarsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int interactionConfigOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int droppedItemAnimationOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int tagIndexesOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int itemAppearanceConditionsOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int displayEntityStatsHUDOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int hudUIOffsetSlot = buf.writerIndex();
      buf.writeIntLE(0);
      int varBlockStart = buf.writerIndex();
      if (this.id != null) {
         buf.setIntLE(idOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.id, 4096000);
      } else {
         buf.setIntLE(idOffsetSlot, -1);
      }

      if (this.model != null) {
         buf.setIntLE(modelOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.model, 4096000);
      } else {
         buf.setIntLE(modelOffsetSlot, -1);
      }

      if (this.texture != null) {
         buf.setIntLE(textureOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.texture, 4096000);
      } else {
         buf.setIntLE(textureOffsetSlot, -1);
      }

      if (this.animation != null) {
         buf.setIntLE(animationOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.animation, 4096000);
      } else {
         buf.setIntLE(animationOffsetSlot, -1);
      }

      if (this.playerAnimationsId != null) {
         buf.setIntLE(playerAnimationsIdOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.playerAnimationsId, 4096000);
      } else {
         buf.setIntLE(playerAnimationsIdOffsetSlot, -1);
      }

      if (this.icon != null) {
         buf.setIntLE(iconOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.icon, 4096000);
      } else {
         buf.setIntLE(iconOffsetSlot, -1);
      }

      if (this.translationProperties != null) {
         buf.setIntLE(translationPropertiesOffsetSlot, buf.writerIndex() - varBlockStart);
         this.translationProperties.serialize(buf);
      } else {
         buf.setIntLE(translationPropertiesOffsetSlot, -1);
      }

      if (this.resourceTypes != null) {
         buf.setIntLE(resourceTypesOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.resourceTypes.length > 4096000) {
            throw ProtocolException.arrayTooLong("ResourceTypes", this.resourceTypes.length, 4096000);
         }

         VarInt.write(buf, this.resourceTypes.length);

         for (ItemResourceType item : this.resourceTypes) {
            item.serialize(buf);
         }
      } else {
         buf.setIntLE(resourceTypesOffsetSlot, -1);
      }

      if (this.tool != null) {
         buf.setIntLE(toolOffsetSlot, buf.writerIndex() - varBlockStart);
         this.tool.serialize(buf);
      } else {
         buf.setIntLE(toolOffsetSlot, -1);
      }

      if (this.weapon != null) {
         buf.setIntLE(weaponOffsetSlot, buf.writerIndex() - varBlockStart);
         this.weapon.serialize(buf);
      } else {
         buf.setIntLE(weaponOffsetSlot, -1);
      }

      if (this.armor != null) {
         buf.setIntLE(armorOffsetSlot, buf.writerIndex() - varBlockStart);
         this.armor.serialize(buf);
      } else {
         buf.setIntLE(armorOffsetSlot, -1);
      }

      if (this.utility != null) {
         buf.setIntLE(utilityOffsetSlot, buf.writerIndex() - varBlockStart);
         this.utility.serialize(buf);
      } else {
         buf.setIntLE(utilityOffsetSlot, -1);
      }

      if (this.builderToolData != null) {
         buf.setIntLE(builderToolDataOffsetSlot, buf.writerIndex() - varBlockStart);
         this.builderToolData.serialize(buf);
      } else {
         buf.setIntLE(builderToolDataOffsetSlot, -1);
      }

      if (this.itemEntity != null) {
         buf.setIntLE(itemEntityOffsetSlot, buf.writerIndex() - varBlockStart);
         this.itemEntity.serialize(buf);
      } else {
         buf.setIntLE(itemEntityOffsetSlot, -1);
      }

      if (this.set != null) {
         buf.setIntLE(setOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.set, 4096000);
      } else {
         buf.setIntLE(setOffsetSlot, -1);
      }

      if (this.categories != null) {
         buf.setIntLE(categoriesOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.categories.length > 4096000) {
            throw ProtocolException.arrayTooLong("Categories", this.categories.length, 4096000);
         }

         VarInt.write(buf, this.categories.length);

         for (String item : this.categories) {
            PacketIO.writeVarString(buf, item, 4096000);
         }
      } else {
         buf.setIntLE(categoriesOffsetSlot, -1);
      }

      if (this.subCategory != null) {
         buf.setIntLE(subCategoryOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.subCategory, 4096000);
      } else {
         buf.setIntLE(subCategoryOffsetSlot, -1);
      }

      if (this.particles != null) {
         buf.setIntLE(particlesOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.particles.length > 4096000) {
            throw ProtocolException.arrayTooLong("Particles", this.particles.length, 4096000);
         }

         VarInt.write(buf, this.particles.length);

         for (ModelParticle item : this.particles) {
            item.serialize(buf);
         }
      } else {
         buf.setIntLE(particlesOffsetSlot, -1);
      }

      if (this.firstPersonParticles != null) {
         buf.setIntLE(firstPersonParticlesOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.firstPersonParticles.length > 4096000) {
            throw ProtocolException.arrayTooLong("FirstPersonParticles", this.firstPersonParticles.length, 4096000);
         }

         VarInt.write(buf, this.firstPersonParticles.length);

         for (ModelParticle item : this.firstPersonParticles) {
            item.serialize(buf);
         }
      } else {
         buf.setIntLE(firstPersonParticlesOffsetSlot, -1);
      }

      if (this.trails != null) {
         buf.setIntLE(trailsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.trails.length > 4096000) {
            throw ProtocolException.arrayTooLong("Trails", this.trails.length, 4096000);
         }

         VarInt.write(buf, this.trails.length);

         for (ModelTrail item : this.trails) {
            item.serialize(buf);
         }
      } else {
         buf.setIntLE(trailsOffsetSlot, -1);
      }

      if (this.interactions != null) {
         buf.setIntLE(interactionsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.interactions.size() > 4096000) {
            throw ProtocolException.dictionaryTooLarge("Interactions", this.interactions.size(), 4096000);
         }

         VarInt.write(buf, this.interactions.size());

         for (Entry<InteractionType, Integer> e : this.interactions.entrySet()) {
            buf.writeByte(e.getKey().getValue());
            buf.writeIntLE(e.getValue());
         }
      } else {
         buf.setIntLE(interactionsOffsetSlot, -1);
      }

      if (this.interactionVars != null) {
         buf.setIntLE(interactionVarsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.interactionVars.size() > 4096000) {
            throw ProtocolException.dictionaryTooLarge("InteractionVars", this.interactionVars.size(), 4096000);
         }

         VarInt.write(buf, this.interactionVars.size());

         for (Entry<String, Integer> e : this.interactionVars.entrySet()) {
            PacketIO.writeVarString(buf, e.getKey(), 4096000);
            buf.writeIntLE(e.getValue());
         }
      } else {
         buf.setIntLE(interactionVarsOffsetSlot, -1);
      }

      if (this.interactionConfig != null) {
         buf.setIntLE(interactionConfigOffsetSlot, buf.writerIndex() - varBlockStart);
         this.interactionConfig.serialize(buf);
      } else {
         buf.setIntLE(interactionConfigOffsetSlot, -1);
      }

      if (this.droppedItemAnimation != null) {
         buf.setIntLE(droppedItemAnimationOffsetSlot, buf.writerIndex() - varBlockStart);
         PacketIO.writeVarString(buf, this.droppedItemAnimation, 4096000);
      } else {
         buf.setIntLE(droppedItemAnimationOffsetSlot, -1);
      }

      if (this.tagIndexes != null) {
         buf.setIntLE(tagIndexesOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.tagIndexes.length > 4096000) {
            throw ProtocolException.arrayTooLong("TagIndexes", this.tagIndexes.length, 4096000);
         }

         VarInt.write(buf, this.tagIndexes.length);

         for (int item : this.tagIndexes) {
            buf.writeIntLE(item);
         }
      } else {
         buf.setIntLE(tagIndexesOffsetSlot, -1);
      }

      if (this.itemAppearanceConditions != null) {
         buf.setIntLE(itemAppearanceConditionsOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.itemAppearanceConditions.size() > 4096000) {
            throw ProtocolException.dictionaryTooLarge("ItemAppearanceConditions", this.itemAppearanceConditions.size(), 4096000);
         }

         VarInt.write(buf, this.itemAppearanceConditions.size());

         for (Entry<Integer, ItemAppearanceCondition[]> e : this.itemAppearanceConditions.entrySet()) {
            buf.writeIntLE(e.getKey());
            VarInt.write(buf, e.getValue().length);

            for (ItemAppearanceCondition arrItem : e.getValue()) {
               arrItem.serialize(buf);
            }
         }
      } else {
         buf.setIntLE(itemAppearanceConditionsOffsetSlot, -1);
      }

      if (this.displayEntityStatsHUD != null) {
         buf.setIntLE(displayEntityStatsHUDOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.displayEntityStatsHUD.length > 4096000) {
            throw ProtocolException.arrayTooLong("DisplayEntityStatsHUD", this.displayEntityStatsHUD.length, 4096000);
         }

         VarInt.write(buf, this.displayEntityStatsHUD.length);

         for (int item : this.displayEntityStatsHUD) {
            buf.writeIntLE(item);
         }
      } else {
         buf.setIntLE(displayEntityStatsHUDOffsetSlot, -1);
      }

      if (this.hudUI != null) {
         buf.setIntLE(hudUIOffsetSlot, buf.writerIndex() - varBlockStart);
         if (this.hudUI.length > 4096000) {
            throw ProtocolException.arrayTooLong("HudUI", this.hudUI.length, 4096000);
         }

         VarInt.write(buf, this.hudUI.length);

         for (ItemHudUI item : this.hudUI) {
            item.serialize(buf);
         }
      } else {
         buf.setIntLE(hudUIOffsetSlot, -1);
      }
   }

   public int computeSize() {
      int size = 260;
      if (this.id != null) {
         size += PacketIO.stringSize(this.id);
      }

      if (this.model != null) {
         size += PacketIO.stringSize(this.model);
      }

      if (this.texture != null) {
         size += PacketIO.stringSize(this.texture);
      }

      if (this.animation != null) {
         size += PacketIO.stringSize(this.animation);
      }

      if (this.playerAnimationsId != null) {
         size += PacketIO.stringSize(this.playerAnimationsId);
      }

      if (this.icon != null) {
         size += PacketIO.stringSize(this.icon);
      }

      if (this.translationProperties != null) {
         size += this.translationProperties.computeSize();
      }

      if (this.resourceTypes != null) {
         int resourceTypesSize = 0;

         for (ItemResourceType elem : this.resourceTypes) {
            resourceTypesSize += elem.computeSize();
         }

         size += VarInt.size(this.resourceTypes.length) + resourceTypesSize;
      }

      if (this.tool != null) {
         size += this.tool.computeSize();
      }

      if (this.weapon != null) {
         size += this.weapon.computeSize();
      }

      if (this.armor != null) {
         size += this.armor.computeSize();
      }

      if (this.utility != null) {
         size += this.utility.computeSize();
      }

      if (this.builderToolData != null) {
         size += this.builderToolData.computeSize();
      }

      if (this.itemEntity != null) {
         size += this.itemEntity.computeSize();
      }

      if (this.set != null) {
         size += PacketIO.stringSize(this.set);
      }

      if (this.categories != null) {
         int categoriesSize = 0;

         for (String elem : this.categories) {
            categoriesSize += PacketIO.stringSize(elem);
         }

         size += VarInt.size(this.categories.length) + categoriesSize;
      }

      if (this.subCategory != null) {
         size += PacketIO.stringSize(this.subCategory);
      }

      if (this.particles != null) {
         int particlesSize = 0;

         for (ModelParticle elem : this.particles) {
            particlesSize += elem.computeSize();
         }

         size += VarInt.size(this.particles.length) + particlesSize;
      }

      if (this.firstPersonParticles != null) {
         int firstPersonParticlesSize = 0;

         for (ModelParticle elem : this.firstPersonParticles) {
            firstPersonParticlesSize += elem.computeSize();
         }

         size += VarInt.size(this.firstPersonParticles.length) + firstPersonParticlesSize;
      }

      if (this.trails != null) {
         int trailsSize = 0;

         for (ModelTrail elem : this.trails) {
            trailsSize += elem.computeSize();
         }

         size += VarInt.size(this.trails.length) + trailsSize;
      }

      if (this.interactions != null) {
         size += VarInt.size(this.interactions.size()) + this.interactions.size() * 5;
      }

      if (this.interactionVars != null) {
         int interactionVarsSize = 0;

         for (Entry<String, Integer> kvp : this.interactionVars.entrySet()) {
            interactionVarsSize += PacketIO.stringSize(kvp.getKey()) + 4;
         }

         size += VarInt.size(this.interactionVars.size()) + interactionVarsSize;
      }

      if (this.interactionConfig != null) {
         size += this.interactionConfig.computeSize();
      }

      if (this.droppedItemAnimation != null) {
         size += PacketIO.stringSize(this.droppedItemAnimation);
      }

      if (this.tagIndexes != null) {
         size += VarInt.size(this.tagIndexes.length) + this.tagIndexes.length * 4;
      }

      if (this.itemAppearanceConditions != null) {
         int itemAppearanceConditionsSize = 0;

         for (Entry<Integer, ItemAppearanceCondition[]> kvp : this.itemAppearanceConditions.entrySet()) {
            itemAppearanceConditionsSize += 4 + VarInt.size(kvp.getValue().length) + Arrays.stream(kvp.getValue()).mapToInt(inner -> inner.computeSize()).sum();
         }

         size += VarInt.size(this.itemAppearanceConditions.size()) + itemAppearanceConditionsSize;
      }

      if (this.displayEntityStatsHUD != null) {
         size += VarInt.size(this.displayEntityStatsHUD.length) + this.displayEntityStatsHUD.length * 4;
      }

      if (this.hudUI != null) {
         int hudUISize = 0;

         for (ItemHudUI elem : this.hudUI) {
            hudUISize += elem.computeSize();
         }

         size += VarInt.size(this.hudUI.length) + hudUISize;
      }

      return size;
   }

   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
      if (buffer.readableBytes() - offset < 260) {
         return ValidationResult.error("Buffer too small: expected at least 260 bytes");
      }

      byte[] nullBits = PacketIO.readBytes(buffer, offset, 5);
      if ((nullBits[0] & 32) != 0) {
         int idOffset = buffer.getIntLE(offset + 148);
         if (idOffset < 0 || idOffset > buffer.writerIndex() - offset - 260) {
            return ValidationResult.error("Invalid offset for Id");
         }

         int pos = offset + 260 + idOffset;
         int idLen = VarInt.peek(buffer, pos);
         if (idLen < 0) {
            return ValidationResult.error("Invalid string length for Id");
         }

         if (idLen > 4096000) {
            return ValidationResult.error("Id exceeds max length 4096000");
         }

         pos += VarInt.size(idLen);
         pos += idLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Id");
         }
      }

      if ((nullBits[0] & 64) != 0) {
         int modelOffset = buffer.getIntLE(offset + 152);
         if (modelOffset < 0 || modelOffset > buffer.writerIndex() - offset - 260) {
            return ValidationResult.error("Invalid offset for Model");
         }

         int pos = offset + 260 + modelOffset;
         int modelLen = VarInt.peek(buffer, pos);
         if (modelLen < 0) {
            return ValidationResult.error("Invalid string length for Model");
         }

         if (modelLen > 4096000) {
            return ValidationResult.error("Model exceeds max length 4096000");
         }

         pos += VarInt.size(modelLen);
         pos += modelLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Model");
         }
      }

      if ((nullBits[0] & 128) != 0) {
         int textureOffset = buffer.getIntLE(offset + 156);
         if (textureOffset < 0 || textureOffset > buffer.writerIndex() - offset - 260) {
            return ValidationResult.error("Invalid offset for Texture");
         }

         int pos = offset + 260 + textureOffset;
         int textureLen = VarInt.peek(buffer, pos);
         if (textureLen < 0) {
            return ValidationResult.error("Invalid string length for Texture");
         }

         if (textureLen > 4096000) {
            return ValidationResult.error("Texture exceeds max length 4096000");
         }

         pos += VarInt.size(textureLen);
         pos += textureLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Texture");
         }
      }

      if ((nullBits[1] & 1) != 0) {
         int animationOffset = buffer.getIntLE(offset + 160);
         if (animationOffset < 0 || animationOffset > buffer.writerIndex() - offset - 260) {
            return ValidationResult.error("Invalid offset for Animation");
         }

         int pos = offset + 260 + animationOffset;
         int animationLen = VarInt.peek(buffer, pos);
         if (animationLen < 0) {
            return ValidationResult.error("Invalid string length for Animation");
         }

         if (animationLen > 4096000) {
            return ValidationResult.error("Animation exceeds max length 4096000");
         }

         pos += VarInt.size(animationLen);
         pos += animationLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Animation");
         }
      }

      if ((nullBits[1] & 2) != 0) {
         int playerAnimationsIdOffset = buffer.getIntLE(offset + 164);
         if (playerAnimationsIdOffset < 0 || playerAnimationsIdOffset > buffer.writerIndex() - offset - 260) {
            return ValidationResult.error("Invalid offset for PlayerAnimationsId");
         }

         int pos = offset + 260 + playerAnimationsIdOffset;
         int playerAnimationsIdLen = VarInt.peek(buffer, pos);
         if (playerAnimationsIdLen < 0) {
            return ValidationResult.error("Invalid string length for PlayerAnimationsId");
         }

         if (playerAnimationsIdLen > 4096000) {
            return ValidationResult.error("PlayerAnimationsId exceeds max length 4096000");
         }

         pos += VarInt.size(playerAnimationsIdLen);
         pos += playerAnimationsIdLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading PlayerAnimationsId");
         }
      }

      if ((nullBits[1] & 4) != 0) {
         int iconOffset = buffer.getIntLE(offset + 168);
         if (iconOffset < 0 || iconOffset > buffer.writerIndex() - offset - 260) {
            return ValidationResult.error("Invalid offset for Icon");
         }

         int pos = offset + 260 + iconOffset;
         int iconLen = VarInt.peek(buffer, pos);
         if (iconLen < 0) {
            return ValidationResult.error("Invalid string length for Icon");
         }

         if (iconLen > 4096000) {
            return ValidationResult.error("Icon exceeds max length 4096000");
         }

         pos += VarInt.size(iconLen);
         pos += iconLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Icon");
         }
      }

      if ((nullBits[1] & 8) != 0) {
         int translationPropertiesOffset = buffer.getIntLE(offset + 172);
         if (translationPropertiesOffset < 0 || translationPropertiesOffset > buffer.writerIndex() - offset - 260) {
            return ValidationResult.error("Invalid offset for TranslationProperties");
         }

         int pos = offset + 260 + translationPropertiesOffset;
         ValidationResult translationPropertiesResult = ItemTranslationProperties.validateStructure(buffer, pos);
         if (!translationPropertiesResult.isValid()) {
            return ValidationResult.error("Invalid TranslationProperties: " + translationPropertiesResult.error());
         }

         pos += ItemTranslationProperties.computeBytesConsumed(buffer, pos);
      }

      if ((nullBits[1] & 16) != 0) {
         int resourceTypesOffset = buffer.getIntLE(offset + 176);
         if (resourceTypesOffset < 0 || resourceTypesOffset > buffer.writerIndex() - offset - 260) {
            return ValidationResult.error("Invalid offset for ResourceTypes");
         }

         int pos = offset + 260 + resourceTypesOffset;
         int resourceTypesCount = VarInt.peek(buffer, pos);
         if (resourceTypesCount < 0) {
            return ValidationResult.error("Invalid array count for ResourceTypes");
         }

         if (resourceTypesCount > 4096000) {
            return ValidationResult.error("ResourceTypes exceeds max length 4096000");
         }

         pos += VarInt.size(resourceTypesCount);

         for (int i = 0; i < resourceTypesCount; i++) {
            ValidationResult structResult = ItemResourceType.validateStructure(buffer, pos);
            if (!structResult.isValid()) {
               return ValidationResult.error("Invalid ItemResourceType in ResourceTypes[" + i + "]: " + structResult.error());
            }

            pos += ItemResourceType.computeBytesConsumed(buffer, pos);
         }
      }

      if ((nullBits[1] & 32) != 0) {
         int toolOffset = buffer.getIntLE(offset + 180);
         if (toolOffset < 0 || toolOffset > buffer.writerIndex() - offset - 260) {
            return ValidationResult.error("Invalid offset for Tool");
         }

         int pos = offset + 260 + toolOffset;
         ValidationResult toolResult = ItemTool.validateStructure(buffer, pos);
         if (!toolResult.isValid()) {
            return ValidationResult.error("Invalid Tool: " + toolResult.error());
         }

         pos += ItemTool.computeBytesConsumed(buffer, pos);
      }

      if ((nullBits[1] & 64) != 0) {
         int weaponOffset = buffer.getIntLE(offset + 184);
         if (weaponOffset < 0 || weaponOffset > buffer.writerIndex() - offset - 260) {
            return ValidationResult.error("Invalid offset for Weapon");
         }

         int pos = offset + 260 + weaponOffset;
         ValidationResult weaponResult = ItemWeapon.validateStructure(buffer, pos);
         if (!weaponResult.isValid()) {
            return ValidationResult.error("Invalid Weapon: " + weaponResult.error());
         }

         pos += ItemWeapon.computeBytesConsumed(buffer, pos);
      }

      if ((nullBits[1] & 128) != 0) {
         int armorOffset = buffer.getIntLE(offset + 188);
         if (armorOffset < 0 || armorOffset > buffer.writerIndex() - offset - 260) {
            return ValidationResult.error("Invalid offset for Armor");
         }

         int pos = offset + 260 + armorOffset;
         ValidationResult armorResult = ItemArmor.validateStructure(buffer, pos);
         if (!armorResult.isValid()) {
            return ValidationResult.error("Invalid Armor: " + armorResult.error());
         }

         pos += ItemArmor.computeBytesConsumed(buffer, pos);
      }

      if ((nullBits[2] & 1) != 0) {
         int utilityOffset = buffer.getIntLE(offset + 192);
         if (utilityOffset < 0 || utilityOffset > buffer.writerIndex() - offset - 260) {
            return ValidationResult.error("Invalid offset for Utility");
         }

         int pos = offset + 260 + utilityOffset;
         ValidationResult utilityResult = ItemUtility.validateStructure(buffer, pos);
         if (!utilityResult.isValid()) {
            return ValidationResult.error("Invalid Utility: " + utilityResult.error());
         }

         pos += ItemUtility.computeBytesConsumed(buffer, pos);
      }

      if ((nullBits[2] & 2) != 0) {
         int builderToolDataOffset = buffer.getIntLE(offset + 196);
         if (builderToolDataOffset < 0 || builderToolDataOffset > buffer.writerIndex() - offset - 260) {
            return ValidationResult.error("Invalid offset for BuilderToolData");
         }

         int pos = offset + 260 + builderToolDataOffset;
         ValidationResult builderToolDataResult = BuilderToolState.validateStructure(buffer, pos);
         if (!builderToolDataResult.isValid()) {
            return ValidationResult.error("Invalid BuilderToolData: " + builderToolDataResult.error());
         }

         pos += BuilderToolState.computeBytesConsumed(buffer, pos);
      }

      if ((nullBits[2] & 4) != 0) {
         int itemEntityOffset = buffer.getIntLE(offset + 200);
         if (itemEntityOffset < 0 || itemEntityOffset > buffer.writerIndex() - offset - 260) {
            return ValidationResult.error("Invalid offset for ItemEntity");
         }

         int pos = offset + 260 + itemEntityOffset;
         ValidationResult itemEntityResult = ItemEntityConfig.validateStructure(buffer, pos);
         if (!itemEntityResult.isValid()) {
            return ValidationResult.error("Invalid ItemEntity: " + itemEntityResult.error());
         }

         pos += ItemEntityConfig.computeBytesConsumed(buffer, pos);
      }

      if ((nullBits[2] & 8) != 0) {
         int setOffset = buffer.getIntLE(offset + 204);
         if (setOffset < 0 || setOffset > buffer.writerIndex() - offset - 260) {
            return ValidationResult.error("Invalid offset for Set");
         }

         int pos = offset + 260 + setOffset;
         int setLen = VarInt.peek(buffer, pos);
         if (setLen < 0) {
            return ValidationResult.error("Invalid string length for Set");
         }

         if (setLen > 4096000) {
            return ValidationResult.error("Set exceeds max length 4096000");
         }

         pos += VarInt.size(setLen);
         pos += setLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading Set");
         }
      }

      if ((nullBits[2] & 16) != 0) {
         int categoriesOffset = buffer.getIntLE(offset + 208);
         if (categoriesOffset < 0 || categoriesOffset > buffer.writerIndex() - offset - 260) {
            return ValidationResult.error("Invalid offset for Categories");
         }

         int pos = offset + 260 + categoriesOffset;
         int categoriesCount = VarInt.peek(buffer, pos);
         if (categoriesCount < 0) {
            return ValidationResult.error("Invalid array count for Categories");
         }

         if (categoriesCount > 4096000) {
            return ValidationResult.error("Categories exceeds max length 4096000");
         }

         pos += VarInt.size(categoriesCount);

         for (int i = 0; i < categoriesCount; i++) {
            int strLen = VarInt.peek(buffer, pos);
            if (strLen < 0) {
               return ValidationResult.error("Invalid string length in Categories");
            }

            pos += VarInt.size(strLen);
            pos += strLen;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading string in Categories");
            }
         }
      }

      if ((nullBits[2] & 32) != 0) {
         int subCategoryOffset = buffer.getIntLE(offset + 212);
         if (subCategoryOffset < 0 || subCategoryOffset > buffer.writerIndex() - offset - 260) {
            return ValidationResult.error("Invalid offset for SubCategory");
         }

         int pos = offset + 260 + subCategoryOffset;
         int subCategoryLen = VarInt.peek(buffer, pos);
         if (subCategoryLen < 0) {
            return ValidationResult.error("Invalid string length for SubCategory");
         }

         if (subCategoryLen > 4096000) {
            return ValidationResult.error("SubCategory exceeds max length 4096000");
         }

         pos += VarInt.size(subCategoryLen);
         pos += subCategoryLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading SubCategory");
         }
      }

      if ((nullBits[2] & 64) != 0) {
         int particlesOffset = buffer.getIntLE(offset + 216);
         if (particlesOffset < 0 || particlesOffset > buffer.writerIndex() - offset - 260) {
            return ValidationResult.error("Invalid offset for Particles");
         }

         int pos = offset + 260 + particlesOffset;
         int particlesCount = VarInt.peek(buffer, pos);
         if (particlesCount < 0) {
            return ValidationResult.error("Invalid array count for Particles");
         }

         if (particlesCount > 4096000) {
            return ValidationResult.error("Particles exceeds max length 4096000");
         }

         pos += VarInt.size(particlesCount);

         for (int i = 0; i < particlesCount; i++) {
            ValidationResult structResult = ModelParticle.validateStructure(buffer, pos);
            if (!structResult.isValid()) {
               return ValidationResult.error("Invalid ModelParticle in Particles[" + i + "]: " + structResult.error());
            }

            pos += ModelParticle.computeBytesConsumed(buffer, pos);
         }
      }

      if ((nullBits[2] & 128) != 0) {
         int firstPersonParticlesOffset = buffer.getIntLE(offset + 220);
         if (firstPersonParticlesOffset < 0 || firstPersonParticlesOffset > buffer.writerIndex() - offset - 260) {
            return ValidationResult.error("Invalid offset for FirstPersonParticles");
         }

         int pos = offset + 260 + firstPersonParticlesOffset;
         int firstPersonParticlesCount = VarInt.peek(buffer, pos);
         if (firstPersonParticlesCount < 0) {
            return ValidationResult.error("Invalid array count for FirstPersonParticles");
         }

         if (firstPersonParticlesCount > 4096000) {
            return ValidationResult.error("FirstPersonParticles exceeds max length 4096000");
         }

         pos += VarInt.size(firstPersonParticlesCount);

         for (int i = 0; i < firstPersonParticlesCount; i++) {
            ValidationResult structResult = ModelParticle.validateStructure(buffer, pos);
            if (!structResult.isValid()) {
               return ValidationResult.error("Invalid ModelParticle in FirstPersonParticles[" + i + "]: " + structResult.error());
            }

            pos += ModelParticle.computeBytesConsumed(buffer, pos);
         }
      }

      if ((nullBits[3] & 1) != 0) {
         int trailsOffset = buffer.getIntLE(offset + 224);
         if (trailsOffset < 0 || trailsOffset > buffer.writerIndex() - offset - 260) {
            return ValidationResult.error("Invalid offset for Trails");
         }

         int pos = offset + 260 + trailsOffset;
         int trailsCount = VarInt.peek(buffer, pos);
         if (trailsCount < 0) {
            return ValidationResult.error("Invalid array count for Trails");
         }

         if (trailsCount > 4096000) {
            return ValidationResult.error("Trails exceeds max length 4096000");
         }

         pos += VarInt.size(trailsCount);

         for (int i = 0; i < trailsCount; i++) {
            ValidationResult structResult = ModelTrail.validateStructure(buffer, pos);
            if (!structResult.isValid()) {
               return ValidationResult.error("Invalid ModelTrail in Trails[" + i + "]: " + structResult.error());
            }

            pos += ModelTrail.computeBytesConsumed(buffer, pos);
         }
      }

      if ((nullBits[3] & 2) != 0) {
         int interactionsOffset = buffer.getIntLE(offset + 228);
         if (interactionsOffset < 0 || interactionsOffset > buffer.writerIndex() - offset - 260) {
            return ValidationResult.error("Invalid offset for Interactions");
         }

         int pos = offset + 260 + interactionsOffset;
         int interactionsCount = VarInt.peek(buffer, pos);
         if (interactionsCount < 0) {
            return ValidationResult.error("Invalid dictionary count for Interactions");
         }

         if (interactionsCount > 4096000) {
            return ValidationResult.error("Interactions exceeds max length 4096000");
         }

         pos += VarInt.size(interactionsCount);

         for (int i = 0; i < interactionsCount; i++) {
            int v = buffer.getByte(pos) & 255;
            if (v >= 25) {
               return ValidationResult.error("Invalid InteractionType value for key");
            }

            pos = ++pos + 4;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading value");
            }
         }
      }

      if ((nullBits[3] & 4) != 0) {
         int interactionVarsOffset = buffer.getIntLE(offset + 232);
         if (interactionVarsOffset < 0 || interactionVarsOffset > buffer.writerIndex() - offset - 260) {
            return ValidationResult.error("Invalid offset for InteractionVars");
         }

         int pos = offset + 260 + interactionVarsOffset;
         int interactionVarsCount = VarInt.peek(buffer, pos);
         if (interactionVarsCount < 0) {
            return ValidationResult.error("Invalid dictionary count for InteractionVars");
         }

         if (interactionVarsCount > 4096000) {
            return ValidationResult.error("InteractionVars exceeds max length 4096000");
         }

         pos += VarInt.size(interactionVarsCount);

         for (int i = 0; i < interactionVarsCount; i++) {
            int keyLen = VarInt.peek(buffer, pos);
            if (keyLen < 0) {
               return ValidationResult.error("Invalid string length for key");
            }

            if (keyLen > 4096000) {
               return ValidationResult.error("key exceeds max length 4096000");
            }

            pos += VarInt.size(keyLen);
            pos += keyLen;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading key");
            }

            pos += 4;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading value");
            }
         }
      }

      if ((nullBits[3] & 8) != 0) {
         int interactionConfigOffset = buffer.getIntLE(offset + 236);
         if (interactionConfigOffset < 0 || interactionConfigOffset > buffer.writerIndex() - offset - 260) {
            return ValidationResult.error("Invalid offset for InteractionConfig");
         }

         int pos = offset + 260 + interactionConfigOffset;
         ValidationResult interactionConfigResult = InteractionConfiguration.validateStructure(buffer, pos);
         if (!interactionConfigResult.isValid()) {
            return ValidationResult.error("Invalid InteractionConfig: " + interactionConfigResult.error());
         }

         pos += InteractionConfiguration.computeBytesConsumed(buffer, pos);
      }

      if ((nullBits[3] & 16) != 0) {
         int droppedItemAnimationOffset = buffer.getIntLE(offset + 240);
         if (droppedItemAnimationOffset < 0 || droppedItemAnimationOffset > buffer.writerIndex() - offset - 260) {
            return ValidationResult.error("Invalid offset for DroppedItemAnimation");
         }

         int pos = offset + 260 + droppedItemAnimationOffset;
         int droppedItemAnimationLen = VarInt.peek(buffer, pos);
         if (droppedItemAnimationLen < 0) {
            return ValidationResult.error("Invalid string length for DroppedItemAnimation");
         }

         if (droppedItemAnimationLen > 4096000) {
            return ValidationResult.error("DroppedItemAnimation exceeds max length 4096000");
         }

         pos += VarInt.size(droppedItemAnimationLen);
         pos += droppedItemAnimationLen;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading DroppedItemAnimation");
         }
      }

      if ((nullBits[3] & 32) != 0) {
         int tagIndexesOffset = buffer.getIntLE(offset + 244);
         if (tagIndexesOffset < 0 || tagIndexesOffset > buffer.writerIndex() - offset - 260) {
            return ValidationResult.error("Invalid offset for TagIndexes");
         }

         int pos = offset + 260 + tagIndexesOffset;
         int tagIndexesCount = VarInt.peek(buffer, pos);
         if (tagIndexesCount < 0) {
            return ValidationResult.error("Invalid array count for TagIndexes");
         }

         if (tagIndexesCount > 4096000) {
            return ValidationResult.error("TagIndexes exceeds max length 4096000");
         }

         pos += VarInt.size(tagIndexesCount);
         pos += tagIndexesCount * 4;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading TagIndexes");
         }
      }

      if ((nullBits[3] & 64) != 0) {
         int itemAppearanceConditionsOffset = buffer.getIntLE(offset + 248);
         if (itemAppearanceConditionsOffset < 0 || itemAppearanceConditionsOffset > buffer.writerIndex() - offset - 260) {
            return ValidationResult.error("Invalid offset for ItemAppearanceConditions");
         }

         int pos = offset + 260 + itemAppearanceConditionsOffset;
         int itemAppearanceConditionsCount = VarInt.peek(buffer, pos);
         if (itemAppearanceConditionsCount < 0) {
            return ValidationResult.error("Invalid dictionary count for ItemAppearanceConditions");
         }

         if (itemAppearanceConditionsCount > 4096000) {
            return ValidationResult.error("ItemAppearanceConditions exceeds max length 4096000");
         }

         pos += VarInt.size(itemAppearanceConditionsCount);

         for (int i = 0; i < itemAppearanceConditionsCount; i++) {
            pos += 4;
            if (pos > buffer.writerIndex()) {
               return ValidationResult.error("Buffer overflow reading key");
            }

            int valueArrCount = VarInt.peek(buffer, pos);
            if (valueArrCount < 0) {
               return ValidationResult.error("Invalid array count for value");
            }

            pos += VarInt.size(valueArrCount);

            for (int valueArrIdx = 0; valueArrIdx < valueArrCount; valueArrIdx++) {
               pos += ItemAppearanceCondition.computeBytesConsumed(buffer, pos);
            }
         }
      }

      if ((nullBits[3] & 128) != 0) {
         int displayEntityStatsHUDOffset = buffer.getIntLE(offset + 252);
         if (displayEntityStatsHUDOffset < 0 || displayEntityStatsHUDOffset > buffer.writerIndex() - offset - 260) {
            return ValidationResult.error("Invalid offset for DisplayEntityStatsHUD");
         }

         int pos = offset + 260 + displayEntityStatsHUDOffset;
         int displayEntityStatsHUDCount = VarInt.peek(buffer, pos);
         if (displayEntityStatsHUDCount < 0) {
            return ValidationResult.error("Invalid array count for DisplayEntityStatsHUD");
         }

         if (displayEntityStatsHUDCount > 4096000) {
            return ValidationResult.error("DisplayEntityStatsHUD exceeds max length 4096000");
         }

         pos += VarInt.size(displayEntityStatsHUDCount);
         pos += displayEntityStatsHUDCount * 4;
         if (pos > buffer.writerIndex()) {
            return ValidationResult.error("Buffer overflow reading DisplayEntityStatsHUD");
         }
      }

      if ((nullBits[4] & 1) != 0) {
         int hudUIOffset = buffer.getIntLE(offset + 256);
         if (hudUIOffset < 0 || hudUIOffset > buffer.writerIndex() - offset - 260) {
            return ValidationResult.error("Invalid offset for HudUI");
         }

         int pos = offset + 260 + hudUIOffset;
         int hudUICount = VarInt.peek(buffer, pos);
         if (hudUICount < 0) {
            return ValidationResult.error("Invalid array count for HudUI");
         }

         if (hudUICount > 4096000) {
            return ValidationResult.error("HudUI exceeds max length 4096000");
         }

         pos += VarInt.size(hudUICount);

         for (int i = 0; i < hudUICount; i++) {
            ValidationResult structResult = ItemHudUI.validateStructure(buffer, pos);
            if (!structResult.isValid()) {
               return ValidationResult.error("Invalid ItemHudUI in HudUI[" + i + "]: " + structResult.error());
            }

            pos += ItemHudUI.computeBytesConsumed(buffer, pos);
         }
      }

      return ValidationResult.OK;
   }

   public ItemBase clone() {
      ItemBase copy = new ItemBase();
      copy.id = this.id;
      copy.model = this.model;
      copy.scale = this.scale;
      copy.texture = this.texture;
      copy.animation = this.animation;
      copy.playerAnimationsId = this.playerAnimationsId;
      copy.usePlayerAnimations = this.usePlayerAnimations;
      copy.maxStack = this.maxStack;
      copy.reticleIndex = this.reticleIndex;
      copy.icon = this.icon;
      copy.iconProperties = this.iconProperties != null ? this.iconProperties.clone() : null;
      copy.translationProperties = this.translationProperties != null ? this.translationProperties.clone() : null;
      copy.itemLevel = this.itemLevel;
      copy.qualityIndex = this.qualityIndex;
      copy.resourceTypes = this.resourceTypes != null ? Arrays.stream(this.resourceTypes).map(ex -> ex.clone()).toArray(ItemResourceType[]::new) : null;
      copy.consumable = this.consumable;
      copy.variant = this.variant;
      copy.blockId = this.blockId;
      copy.tool = this.tool != null ? this.tool.clone() : null;
      copy.weapon = this.weapon != null ? this.weapon.clone() : null;
      copy.armor = this.armor != null ? this.armor.clone() : null;
      copy.gliderConfig = this.gliderConfig != null ? this.gliderConfig.clone() : null;
      copy.utility = this.utility != null ? this.utility.clone() : null;
      copy.blockSelectorTool = this.blockSelectorTool != null ? this.blockSelectorTool.clone() : null;
      copy.builderToolData = this.builderToolData != null ? this.builderToolData.clone() : null;
      copy.itemEntity = this.itemEntity != null ? this.itemEntity.clone() : null;
      copy.set = this.set;
      copy.categories = this.categories != null ? Arrays.copyOf(this.categories, this.categories.length) : null;
      copy.subCategory = this.subCategory;
      copy.particles = this.particles != null ? Arrays.stream(this.particles).map(ex -> ex.clone()).toArray(ModelParticle[]::new) : null;
      copy.firstPersonParticles = this.firstPersonParticles != null
         ? Arrays.stream(this.firstPersonParticles).map(ex -> ex.clone()).toArray(ModelParticle[]::new)
         : null;
      copy.trails = this.trails != null ? Arrays.stream(this.trails).map(ex -> ex.clone()).toArray(ModelTrail[]::new) : null;
      copy.light = this.light != null ? this.light.clone() : null;
      copy.durability = this.durability;
      copy.soundEventIndex = this.soundEventIndex;
      copy.itemSoundSetIndex = this.itemSoundSetIndex;
      copy.interactions = this.interactions != null ? new HashMap<>(this.interactions) : null;
      copy.interactionVars = this.interactionVars != null ? new HashMap<>(this.interactionVars) : null;
      copy.interactionConfig = this.interactionConfig != null ? this.interactionConfig.clone() : null;
      copy.droppedItemAnimation = this.droppedItemAnimation;
      copy.tagIndexes = this.tagIndexes != null ? Arrays.copyOf(this.tagIndexes, this.tagIndexes.length) : null;
      if (this.itemAppearanceConditions != null) {
         Map<Integer, ItemAppearanceCondition[]> m = new HashMap<>();

         for (Entry<Integer, ItemAppearanceCondition[]> e : this.itemAppearanceConditions.entrySet()) {
            m.put(e.getKey(), Arrays.stream(e.getValue()).map(x -> x.clone()).toArray(ItemAppearanceCondition[]::new));
         }

         copy.itemAppearanceConditions = m;
      }

      copy.displayEntityStatsHUD = this.displayEntityStatsHUD != null ? Arrays.copyOf(this.displayEntityStatsHUD, this.displayEntityStatsHUD.length) : null;
      copy.pullbackConfig = this.pullbackConfig != null ? this.pullbackConfig.clone() : null;
      copy.clipsGeometry = this.clipsGeometry;
      copy.renderDeployablePreview = this.renderDeployablePreview;
      copy.hudUI = this.hudUI != null ? Arrays.stream(this.hudUI).map(ex -> ex.clone()).toArray(ItemHudUI[]::new) : null;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof ItemBase other)
            ? false
            : Objects.equals(this.id, other.id)
               && Objects.equals(this.model, other.model)
               && this.scale == other.scale
               && Objects.equals(this.texture, other.texture)
               && Objects.equals(this.animation, other.animation)
               && Objects.equals(this.playerAnimationsId, other.playerAnimationsId)
               && this.usePlayerAnimations == other.usePlayerAnimations
               && this.maxStack == other.maxStack
               && this.reticleIndex == other.reticleIndex
               && Objects.equals(this.icon, other.icon)
               && Objects.equals(this.iconProperties, other.iconProperties)
               && Objects.equals(this.translationProperties, other.translationProperties)
               && this.itemLevel == other.itemLevel
               && this.qualityIndex == other.qualityIndex
               && Arrays.equals(this.resourceTypes, other.resourceTypes)
               && this.consumable == other.consumable
               && this.variant == other.variant
               && this.blockId == other.blockId
               && Objects.equals(this.tool, other.tool)
               && Objects.equals(this.weapon, other.weapon)
               && Objects.equals(this.armor, other.armor)
               && Objects.equals(this.gliderConfig, other.gliderConfig)
               && Objects.equals(this.utility, other.utility)
               && Objects.equals(this.blockSelectorTool, other.blockSelectorTool)
               && Objects.equals(this.builderToolData, other.builderToolData)
               && Objects.equals(this.itemEntity, other.itemEntity)
               && Objects.equals(this.set, other.set)
               && Arrays.equals(this.categories, other.categories)
               && Objects.equals(this.subCategory, other.subCategory)
               && Arrays.equals(this.particles, other.particles)
               && Arrays.equals(this.firstPersonParticles, other.firstPersonParticles)
               && Arrays.equals(this.trails, other.trails)
               && Objects.equals(this.light, other.light)
               && this.durability == other.durability
               && this.soundEventIndex == other.soundEventIndex
               && this.itemSoundSetIndex == other.itemSoundSetIndex
               && Objects.equals(this.interactions, other.interactions)
               && Objects.equals(this.interactionVars, other.interactionVars)
               && Objects.equals(this.interactionConfig, other.interactionConfig)
               && Objects.equals(this.droppedItemAnimation, other.droppedItemAnimation)
               && Arrays.equals(this.tagIndexes, other.tagIndexes)
               && Objects.equals(this.itemAppearanceConditions, other.itemAppearanceConditions)
               && Arrays.equals(this.displayEntityStatsHUD, other.displayEntityStatsHUD)
               && Objects.equals(this.pullbackConfig, other.pullbackConfig)
               && this.clipsGeometry == other.clipsGeometry
               && this.renderDeployablePreview == other.renderDeployablePreview
               && Arrays.equals(this.hudUI, other.hudUI);
      }
   }

   @Override
   public int hashCode() {
      int result = 1;
      result = 31 * result + Objects.hashCode(this.id);
      result = 31 * result + Objects.hashCode(this.model);
      result = 31 * result + Float.hashCode(this.scale);
      result = 31 * result + Objects.hashCode(this.texture);
      result = 31 * result + Objects.hashCode(this.animation);
      result = 31 * result + Objects.hashCode(this.playerAnimationsId);
      result = 31 * result + Boolean.hashCode(this.usePlayerAnimations);
      result = 31 * result + Integer.hashCode(this.maxStack);
      result = 31 * result + Integer.hashCode(this.reticleIndex);
      result = 31 * result + Objects.hashCode(this.icon);
      result = 31 * result + Objects.hashCode(this.iconProperties);
      result = 31 * result + Objects.hashCode(this.translationProperties);
      result = 31 * result + Integer.hashCode(this.itemLevel);
      result = 31 * result + Integer.hashCode(this.qualityIndex);
      result = 31 * result + Arrays.hashCode(this.resourceTypes);
      result = 31 * result + Boolean.hashCode(this.consumable);
      result = 31 * result + Boolean.hashCode(this.variant);
      result = 31 * result + Integer.hashCode(this.blockId);
      result = 31 * result + Objects.hashCode(this.tool);
      result = 31 * result + Objects.hashCode(this.weapon);
      result = 31 * result + Objects.hashCode(this.armor);
      result = 31 * result + Objects.hashCode(this.gliderConfig);
      result = 31 * result + Objects.hashCode(this.utility);
      result = 31 * result + Objects.hashCode(this.blockSelectorTool);
      result = 31 * result + Objects.hashCode(this.builderToolData);
      result = 31 * result + Objects.hashCode(this.itemEntity);
      result = 31 * result + Objects.hashCode(this.set);
      result = 31 * result + Arrays.hashCode(this.categories);
      result = 31 * result + Objects.hashCode(this.subCategory);
      result = 31 * result + Arrays.hashCode(this.particles);
      result = 31 * result + Arrays.hashCode(this.firstPersonParticles);
      result = 31 * result + Arrays.hashCode(this.trails);
      result = 31 * result + Objects.hashCode(this.light);
      result = 31 * result + Double.hashCode(this.durability);
      result = 31 * result + Integer.hashCode(this.soundEventIndex);
      result = 31 * result + Integer.hashCode(this.itemSoundSetIndex);
      result = 31 * result + Objects.hashCode(this.interactions);
      result = 31 * result + Objects.hashCode(this.interactionVars);
      result = 31 * result + Objects.hashCode(this.interactionConfig);
      result = 31 * result + Objects.hashCode(this.droppedItemAnimation);
      result = 31 * result + Arrays.hashCode(this.tagIndexes);
      result = 31 * result + Objects.hashCode(this.itemAppearanceConditions);
      result = 31 * result + Arrays.hashCode(this.displayEntityStatsHUD);
      result = 31 * result + Objects.hashCode(this.pullbackConfig);
      result = 31 * result + Boolean.hashCode(this.clipsGeometry);
      result = 31 * result + Boolean.hashCode(this.renderDeployablePreview);
      return 31 * result + Arrays.hashCode(this.hudUI);
   }
}
