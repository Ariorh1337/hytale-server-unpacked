package com.hypixel.hytale.builtin.triggervolumes.effect.builtin;

import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerContext;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.PrefabListAsset;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.prefab.selection.buffer.PrefabBufferUtil;
import com.hypixel.hytale.server.core.prefab.selection.buffer.impl.PrefabBuffer;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.PrefabUtil;
import java.nio.file.Path;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3i;

public class PastePrefabEffect extends TriggerEffect {
   @Nonnull
   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
   @Nonnull
   public static final BuilderCodec<PastePrefabEffect> CODEC = BuilderCodec.builder(PastePrefabEffect.class, PastePrefabEffect::new, BASE_CODEC)
      .append(new KeyedCodec<>("PrefabList", Codec.STRING, false), (e, v) -> e.prefabListId = v, e -> e.prefabListId)
      .add()
      .append(new KeyedCodec<>("Prefab", Codec.STRING, false), (e, v) -> e.prefabRelPath = v, e -> e.prefabRelPath)
      .add()
      .append(new KeyedCodec<>("Position", Vector3dUtil.CODEC, false), (e, v) -> e.position = v, e -> e.position)
      .add()
      .append(new KeyedCodec<>("AtVolumeOrigin", Codec.BOOLEAN, false), (e, v) -> e.atVolumeOrigin = v, e -> e.atVolumeOrigin)
      .add()
      .build();
   @Nullable
   private String prefabListId;
   @Nullable
   private String prefabRelPath;
   @Nullable
   private Vector3d position;
   private boolean atVolumeOrigin = true;
   private final transient Set<UUID> firedEntities = ConcurrentHashMap.newKeySet();

   @Override
   public void execute(@Nonnull TriggerContext context) {
      boolean hasList = this.prefabListId != null && !this.prefabListId.isBlank();
      boolean hasDirect = this.prefabRelPath != null && !this.prefabRelPath.isBlank();
      if (hasList || hasDirect) {
         Store<EntityStore> store = context.getStore();
         UUIDComponent uuidComponent = store.getComponent(context.getEntityRef(), UUIDComponent.getComponentType());
         if (uuidComponent == null || this.firedEntities.add(uuidComponent.getUuid())) {
            Path prefabPathFile = null;
            if (hasDirect) {
               prefabPathFile = resolveDirectPrefabPath(this.prefabRelPath.trim());
               if (prefabPathFile == null) {
                  LOGGER.at(Level.WARNING).log("PastePrefabEffect: Prefab '%s' not found", this.prefabRelPath);
                  return;
               }
            } else {
               PrefabListAsset prefabListAsset = PrefabListAsset.getAssetMap().getAsset(this.prefabListId);
               if (prefabListAsset == null) {
                  LOGGER.at(Level.WARNING).log("PastePrefabEffect: PrefabList '%s' not found", this.prefabListId);
                  return;
               }

               prefabPathFile = prefabListAsset.getRandomPrefab();
               if (prefabPathFile == null) {
                  return;
               }
            }

            PrefabBuffer prefabBuffer = PrefabBufferUtil.loadBuffer(prefabPathFile);
            if (prefabBuffer != null) {
               World world = store.getExternalData().getWorld();
               if (world != null) {
                  Vector3d origin = context.getVolume().getPosition();
                  Vector3d pastePos;
                  if (this.atVolumeOrigin) {
                     pastePos = new Vector3d(origin);
                     if (this.position != null) {
                        pastePos.add(this.position);
                     }
                  } else {
                     pastePos = this.position != null ? new Vector3d(this.position) : new Vector3d(origin);
                  }

                  Vector3i blockPos = new Vector3i((int)Math.floor(pastePos.x()), (int)Math.floor(pastePos.y()), (int)Math.floor(pastePos.z()));
                  PrefabUtil.paste(prefabBuffer.newAccess(), world, blockPos, Rotation.None, true, new Random(), store);
               }
            }
         }
      }
   }

   @Nullable
   private static Path resolveDirectPrefabPath(@Nonnull String rel) {
      String key = rel.replace('\\', '/').trim();
      PrefabStore store = PrefabStore.get();
      Path p = store.findAssetPrefabPath(key);
      if (p != null) {
         return p;
      }

      if (!key.endsWith(".prefab.json")) {
         p = store.findAssetPrefabPath(key + ".prefab.json");
      }

      return p;
   }

   @Override
   public void onEntityExit(@Nonnull UUID entityUuid) {
      this.firedEntities.remove(entityUuid);
   }
}
