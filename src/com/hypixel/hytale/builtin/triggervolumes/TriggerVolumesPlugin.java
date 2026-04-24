package com.hypixel.hytale.builtin.triggervolumes;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
import com.hypixel.hytale.builtin.triggervolumes.asset.TriggerEffectAsset;
import com.hypixel.hytale.builtin.triggervolumes.command.TriggerVolumeCommand;
import com.hypixel.hytale.builtin.triggervolumes.component.TriggerVolume;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.builtin.ConditionalEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.builtin.ControlDoorsEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.builtin.DestroyVolumeEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.builtin.EntityEffectEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.builtin.PastePrefabEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.builtin.PlaySoundEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.builtin.PlayVfxEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.builtin.RunCommandEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.builtin.SendMessageEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.builtin.SetVelocityEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.builtin.SetWeatherEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.builtin.ShowEventTitleEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.builtin.TeleportEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.builtin.TriggerNpcMarkersEffect;
import com.hypixel.hytale.builtin.triggervolumes.interaction.DestroyTaggedVolumesInteraction;
import com.hypixel.hytale.builtin.triggervolumes.manager.GroupEntry;
import com.hypixel.hytale.builtin.triggervolumes.manager.TriggerVolumeManager;
import com.hypixel.hytale.builtin.triggervolumes.manager.VolumeEntry;
import com.hypixel.hytale.builtin.triggervolumes.prefab.TriggerVolumePasteHandler;
import com.hypixel.hytale.builtin.triggervolumes.prefab.TriggerVolumePrefabContributor;
import com.hypixel.hytale.builtin.triggervolumes.prefab.TriggerVolumePrefabPasteRemapSystem;
import com.hypixel.hytale.builtin.triggervolumes.system.TriggerVolumeTickingSystem;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ComponentRegistry;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.player.UpdateTriggerVolumeDisplay;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.PrefabListAsset;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.asset.type.particle.config.ParticleSystem;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.asset.type.weather.config.Weather;
import com.hypixel.hytale.server.core.io.ServerManager;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.events.RemoveWorldEvent;
import com.hypixel.hytale.server.core.universe.world.events.StartWorldEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.spawning.assets.spawnmarker.config.SpawnMarker;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TriggerVolumesPlugin extends JavaPlugin {
   @Nonnull
   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
   private static TriggerVolumesPlugin instance;
   private ResourceType<EntityStore, TriggerVolumeManager> managerResourceType;
   private ComponentType<EntityStore, TriggerVolume> triggerVolumeComponentType;
   private final Map<String, AssetSourceProvider> assetSources = new LinkedHashMap<>();
   private final Map<TriggerVolumesPlugin.AssetFieldKey, String> assetFieldMappings = new HashMap<>();

   @Nonnull
   public static TriggerVolumesPlugin get() {
      return instance;
   }

   public TriggerVolumesPlugin(@Nonnull JavaPluginInit init) {
      super(init);
   }

   public <T extends TriggerEffect> void registerEffectType(@Nonnull String id, @Nonnull Class<T> clazz, @Nonnull BuilderCodec<T> codec) {
      Objects.requireNonNull(id, "id");
      Objects.requireNonNull(clazz, "clazz");
      Objects.requireNonNull(codec, "codec");
      if (TriggerEffect.CODEC.getCodecFor(id) != null) {
         throw new IllegalArgumentException("Trigger effect type '" + id + "' is already registered");
      }

      TriggerEffect.CODEC.register(id, clazz, codec);
      LOGGER.at(Level.INFO).log("Registered trigger effect type '%s' (%s)", id, clazz.getSimpleName());
   }

   public void registerAssetSource(@Nonnull String sourceId, @Nonnull AssetSourceProvider provider) {
      Objects.requireNonNull(sourceId, "sourceId");
      Objects.requireNonNull(provider, "provider");
      this.assetSources.put(sourceId, provider);
   }

   public void registerAssetField(@Nonnull String effectTypeId, @Nonnull String fieldKey, @Nonnull String sourceId) {
      Objects.requireNonNull(effectTypeId, "effectTypeId");
      Objects.requireNonNull(fieldKey, "fieldKey");
      Objects.requireNonNull(sourceId, "sourceId");
      this.assetFieldMappings.put(new TriggerVolumesPlugin.AssetFieldKey(effectTypeId, fieldKey), sourceId);
   }

   @Nullable
   public String getAssetSourceForField(@Nonnull String effectTypeId, @Nonnull String fieldKey) {
      return this.assetFieldMappings.get(new TriggerVolumesPlugin.AssetFieldKey(effectTypeId, fieldKey));
   }

   @Nonnull
   public Collection<String> getAssetIds(@Nonnull String sourceId) {
      AssetSourceProvider provider = this.assetSources.get(sourceId);
      return provider != null ? provider.getAssetIds() : List.of();
   }

   @Nonnull
   public ResourceType<EntityStore, TriggerVolumeManager> getManagerResourceType() {
      return this.managerResourceType;
   }

   @Nonnull
   public ComponentType<EntityStore, TriggerVolume> getTriggerVolumeComponentType() {
      return this.triggerVolumeComponentType;
   }

   @Override
   protected void setup() {
      instance = this;
      this.registerEffectTypes();
      AssetRegistry.register(
         ((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)HytaleAssetStore.builder(
                        TriggerEffectAsset.class, new DefaultAssetMap()
                     )
                     .setPath("TriggerVolumes/Effects"))
                  .setCodec(TriggerEffectAsset.CODEC))
               .setKeyFunction(TriggerEffectAsset::getId))
            .build()
      );
      ComponentRegistry<EntityStore> entityStoreRegistry = EntityStore.REGISTRY;
      this.managerResourceType = entityStoreRegistry.registerResource(TriggerVolumeManager.class, "TriggerVolumeData", TriggerVolumeManager.CODEC);
      this.triggerVolumeComponentType = entityStoreRegistry.registerComponent(TriggerVolume.class, "TriggerVolume", TriggerVolume.CODEC);
      EntityModule entityModule = EntityModule.get();
      entityStoreRegistry.registerSystem(
         new TriggerVolumeTickingSystem(this.managerResourceType, entityModule.getPlayerSpatialResourceType(), entityModule.getEntitySpatialResourceType())
      );
      entityStoreRegistry.registerSystem(new TriggerVolumePasteHandler(this.managerResourceType, this.triggerVolumeComponentType));
      entityStoreRegistry.registerSystem(new TriggerVolumePrefabPasteRemapSystem(this.managerResourceType));
      this.getCodecRegistry(Interaction.CODEC).register("DestroyTaggedVolumes", DestroyTaggedVolumesInteraction.class, DestroyTaggedVolumesInteraction.CODEC);
      this.getCommandRegistry().registerCommand(new TriggerVolumeCommand());
      ServerManager.get().registerSubPacketHandlers(TriggerVolumeToolPacketHandler::new);
      this.getEventRegistry().registerGlobal(StartWorldEvent.class, event -> this.initManagerForWorld(event.getWorld()));
      this.getEventRegistry().registerGlobal(RemoveWorldEvent.class, this::onWorldRemoved);
   }

   @Override
   protected void start() {
      BuilderToolsPlugin builderTools = BuilderToolsPlugin.get();
      builderTools.registerPrefabSaveContributor(new TriggerVolumePrefabContributor());
      builderTools.registerClipboardContributor(new TriggerVolumePrefabContributor());
      builderTools.setSelectionBoundsUpdatedCallback((playerRef, store) -> {
         World world = store.getExternalData().getWorld();
         if (world != null) {
            TriggerVolumeManager mgr = world.getEntityStore().getStore().getResource(this.managerResourceType);
            if (mgr != null) {
               mgr.addViewer(playerRef.getUuid(), TriggerVolumeManager.ViewSource.SELECTION_TOOL);
               mgr.sendVolumeDisplay(playerRef);
            }
         }
      });
      builderTools.setBuilderToolModeDeactivatedCallback((playerRef, store) -> {
         World world = store.getExternalData().getWorld();
         if (world != null) {
            TriggerVolumeManager mgr = world.getEntityStore().getStore().getResource(this.managerResourceType);
            if (mgr != null) {
               mgr.removeViewer(playerRef.getUuid(), TriggerVolumeManager.ViewSource.SELECTION_TOOL);
               if (!mgr.isViewing(playerRef.getUuid())) {
                  playerRef.getPacketHandler().write(new UpdateTriggerVolumeDisplay());
               } else {
                  mgr.sendVolumeDisplay(playerRef);
               }
            }
         }
      });
      builderTools.setSelectionClearedCallback((playerRef, store) -> {
         World world = store.getExternalData().getWorld();
         if (world != null) {
            TriggerVolumeManager mgr = world.getEntityStore().getStore().getResource(this.managerResourceType);
            if (mgr != null) {
               mgr.removeViewer(playerRef.getUuid(), TriggerVolumeManager.ViewSource.SELECTION_TOOL);
               if (!mgr.isViewing(playerRef.getUuid())) {
                  playerRef.getPacketHandler().write(new UpdateTriggerVolumeDisplay());
               } else {
                  mgr.sendVolumeDisplay(playerRef);
               }
            }
         }
      });
   }

   @Override
   protected void shutdown() {
   }

   private void onWorldRemoved(@Nonnull RemoveWorldEvent event) {
      Store<EntityStore> store = event.getWorld().getEntityStore().getStore();
      TriggerVolumeManager manager = store.getResource(this.managerResourceType);
      if (manager != null) {
         manager.clearViewers();
      }
   }

   private void registerEffectTypes() {
      TriggerEffect.CODEC.register("Teleport", TeleportEffect.class, TeleportEffect.CODEC);
      TriggerEffect.CODEC.register("SendMessage", SendMessageEffect.class, SendMessageEffect.CODEC);
      TriggerEffect.CODEC.register("RunCommand", RunCommandEffect.class, RunCommandEffect.CODEC);
      TriggerEffect.CODEC.register("PlaySound", PlaySoundEffect.class, PlaySoundEffect.CODEC);
      TriggerEffect.CODEC.register("SetVelocity", SetVelocityEffect.class, SetVelocityEffect.CODEC);
      TriggerEffect.CODEC.register("Conditional", ConditionalEffect.class, ConditionalEffect.CODEC);
      TriggerEffect.CODEC.register("EntityEffect", EntityEffectEffect.class, EntityEffectEffect.CODEC);
      TriggerEffect.CODEC.register("TriggerNpcMarkers", TriggerNpcMarkersEffect.class, TriggerNpcMarkersEffect.CODEC);
      TriggerEffect.CODEC.register("PlayVfx", PlayVfxEffect.class, PlayVfxEffect.CODEC);
      TriggerEffect.CODEC.register("SetWeather", SetWeatherEffect.class, SetWeatherEffect.CODEC);
      TriggerEffect.CODEC.register("ShowEventTitle", ShowEventTitleEffect.class, ShowEventTitleEffect.CODEC);
      TriggerEffect.CODEC.register("PastePrefab", PastePrefabEffect.class, PastePrefabEffect.CODEC);
      TriggerEffect.CODEC.register("ControlDoors", ControlDoorsEffect.class, ControlDoorsEffect.CODEC);
      TriggerEffect.CODEC.register("DestroyVolume", DestroyVolumeEffect.class, DestroyVolumeEffect.CODEC);
      this.registerAssetSource("EntityEffect", () -> EntityEffect.getAssetMap().getAssetMap().keySet());
      this.registerAssetSource("SoundEvent", () -> SoundEvent.getAssetMap().getAssetMap().keySet());
      this.registerAssetSource("EffectAsset", () -> {
         AssetStore<String, TriggerEffectAsset, DefaultAssetMap<String, TriggerEffectAsset>> store = AssetRegistry.getAssetStore(TriggerEffectAsset.class);
         return store != null ? ((DefaultAssetMap)store.getAssetMap()).getAssetMap().keySet() : Set.of();
      });
      this.registerAssetSource("Weather", () -> Weather.getAssetMap().getAssetMap().keySet());
      this.registerAssetSource("PrefabList", () -> PrefabListAsset.getAssetMap().getAssetMap().keySet());
      this.registerAssetSource("Prefab", TriggerVolumesPlugin::collectPrefabRelPaths);
      this.registerAssetSource("ParticleSystem", () -> ParticleSystem.getAssetMap().getAssetMap().keySet());
      this.registerAssetSource("ManualSpawnMarker", TriggerVolumesPlugin::collectManualSpawnMarkerIds);
      this.registerAssetField("EntityEffect", "Effect", "EntityEffect");
      this.registerAssetField("PlaySound", "SoundEvent", "SoundEvent");
      this.registerAssetField("Conditional", "EffectAssetRef", "EffectAsset");
      this.registerAssetField("SetWeather", "Weather", "Weather");
      this.registerAssetField("PastePrefab", "PrefabList", "PrefabList");
      this.registerAssetField("PastePrefab", "Prefab", "Prefab");
      this.registerAssetField("PlayVfx", "ParticleSystem", "ParticleSystem");
      this.registerAssetField("TriggerNpcMarkers", "MarkerType", "ManualSpawnMarker");
   }

   @Nonnull
   private static Collection<String> collectPrefabRelPaths() {
      TreeSet<String> ids = new TreeSet<>();
      PrefabStore store = PrefabStore.get();

      for (PrefabStore.AssetPackPrefabPath entry : store.getAllAssetPrefabPaths()) {
         Path root = entry.prefabsPath();

         try (Stream<Path> stream = Files.walk(root)) {
            stream.filter(PrefabStore.PREFAB_FILTER).forEach(p -> {
               Path rel = root.relativize(p);
               String s = rel.toString().replace('\\', '/');
               if (s.endsWith(".prefab.json")) {
                  s = s.substring(0, s.length() - ".prefab.json".length());
               }

               ids.add(s);
            });
         } catch (Exception e) {
            LOGGER.at(Level.WARNING).withCause(e).log("Failed to enumerate prefabs under %s", root);
         }
      }

      return ids;
   }

   @Nonnull
   private static Collection<String> collectManualSpawnMarkerIds() {
      ArrayList<String> ids = new ArrayList<>();

      for (Entry<String, SpawnMarker> e : SpawnMarker.getAssetMap().getAssetMap().entrySet()) {
         if (e.getValue().isManualTrigger()) {
            ids.add(e.getKey());
         }
      }

      Collections.sort(ids);
      return ids;
   }

   private void initManagerForWorld(@Nonnull World world) {
      String worldName = world.getName().toLowerCase(Locale.ROOT);
      Store<EntityStore> store = world.getEntityStore().getStore();
      TriggerVolumeManager manager = store.getResource(this.managerResourceType);
      if (manager != null) {
         manager.setWorld(world);
         AssetStore<String, TriggerEffectAsset, DefaultAssetMap<String, TriggerEffectAsset>> effectAssetStore = AssetRegistry.getAssetStore(
            TriggerEffectAsset.class
         );

         for (VolumeEntry vol : manager.getVolumesMap().values()) {
            vol.setWorldName(worldName);
            if (vol.getEffectAssetRef() != null && effectAssetStore != null) {
               TriggerEffectAsset effectAsset = (TriggerEffectAsset)((DefaultAssetMap)effectAssetStore.getAssetMap()).getAsset(vol.getEffectAssetRef());
               if (effectAsset == null) {
                  LOGGER.at(Level.WARNING).log("Volume '%s' references missing effect asset '%s'", vol.getId(), vol.getEffectAssetRef());
               } else {
                  vol.getEffects().clear();
                  vol.getEffects().addAll(Arrays.asList(effectAsset.getEffects()));
               }
            }
         }

         for (GroupEntry group : manager.getGroupsMap().values()) {
            group.setWorldName(worldName);
         }

         LOGGER.at(Level.INFO)
            .log("Loaded %d trigger volumes and %d groups for world '%s'", manager.getVolumesMap().size(), manager.getGroupsMap().size(), worldName);
      }
   }

   private record AssetFieldKey(@Nonnull String typeId, @Nonnull String fieldKey) {
   }
}
