package com.hypixel.hytale.builtin.encountermanager;

import com.hypixel.hytale.builtin.encountermanager.npc.builders.BuilderActionChangeTargetRole;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.schema.SchemaGenerator;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.asset.builder.BuilderFactory;
import com.hypixel.hytale.server.npc.asset.builder.BuilderInfo;
import com.hypixel.hytale.server.npc.asset.builder.BuilderManager;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class EncounterManagerPlugin extends JavaPlugin {
   public static final String CATEGORY = "EncounterManager";
   public static final String ASSET_PATH = "Server/EncounterManager";
   public static final String MARKER_MODEL = "Encounter_Marker";
   private static EncounterManagerPlugin instance;
   private ComponentType<EntityStore, EncounterManager> encounterComponentType;
   private Model markerModel;

   public static EncounterManagerPlugin get() {
      return instance;
   }

   public EncounterManagerPlugin(@Nonnull JavaPluginInit init) {
      super(init);
   }

   @Nonnull
   public ComponentType<EntityStore, EncounterManager> getEncounterComponentType() {
      return this.encounterComponentType;
   }

   @Override
   protected void setup() {
      instance = this;
      ComponentRegistryProxy<EntityStore> entityStoreRegistry = this.getEntityStoreRegistry();
      BuilderManager builderManager = NPCPlugin.get().getBuilderManager();
      builderManager.addCategory("EncounterManager", EncounterManager.class);
      BuilderFactory<EncounterManager> factory = new BuilderFactory<>(EncounterManager.class, "Type");
      factory.add("Generic", BuilderEncounterManager::new);
      builderManager.registerFactory(factory);
      builderManager.registerAssetPath("Server/EncounterManager", "EncounterManager");
      NPCPlugin.get().registerCoreComponentType("ChangeTargetRole", BuilderActionChangeTargetRole::new);
      SchemaGenerator.registerAssetSchema("EncounterManager.json", ctx -> {
         Schema schema = builderManager.generateSchema(ctx, EncounterManager.class);
         schema.setId("EncounterManager.json");
         schema.setTitle("EncounterManager");
         Schema.HytaleMetadata hytale = schema.getHytale();
         hytale.setPath("EncounterManager");
         hytale.setExtension(".json");
         return schema;
      }, List.of("EncounterManager/*.json", "EncounterManager/**/*.json"), null);
      this.encounterComponentType = entityStoreRegistry.registerComponent(EncounterManager.class, "EncounterManager", EncounterManager.CODEC);
      entityStoreRegistry.registerSystem(new EncounterManagerSystems.BuilderSystem(this.encounterComponentType));
      entityStoreRegistry.registerSystem(new EncounterManagerSystems.ActivateSystem(this.encounterComponentType));
      entityStoreRegistry.registerSystem(new EncounterManagerSystems.TickSystem(this.encounterComponentType));
      entityStoreRegistry.registerSystem(new EncounterManagerSystems.EnsureNetworkSendable(this.encounterComponentType));
      entityStoreRegistry.registerSystem(new EncounterManagerSystems.LifecycleSystem(this.encounterComponentType));
      entityStoreRegistry.registerSystem(new EncounterManagerSystems.TeleportSystem(this.encounterComponentType));
      builderManager.addBuilderReloadListener(this::onBuilderReloaded);
      NPCPlugin.get().registerBeaconReceiverProvider((origin, range, store, out) -> store.forEachChunk(EncounterManager.getComponentType(), (chunk, var5) -> {
         for (int i = 0; i < chunk.size(); i++) {
            TransformComponent transform = chunk.getComponent(i, TransformComponent.getComponentType());
            if (transform != null && transform.getPosition().distanceSquared(origin) <= range * range) {
               out.add(chunk.getReferenceTo(i));
            }
         }
      }));
      this.getCommandRegistry().registerCommand(new EncounterCommand());
   }

   private void onBuilderReloaded(@Nonnull BuilderInfo info) {
      if (info.getBuilder().category() == EncounterManager.class) {
         int index = info.getIndex();

         for (Entry<String, World> entry : Universe.get().getWorlds().entrySet()) {
            World world = entry.getValue();
            world.execute(() -> this.reloadEncounters(world.getEntityStore().getStore(), index));
         }
      }
   }

   private void reloadEncounters(@Nonnull Store<EntityStore> store, int index) {
      ObjectArrayList<Ref<EntityStore>> refs = new ObjectArrayList<>();
      store.forEachChunk(this.encounterComponentType, (chunk, commandBuffer) -> {
         for (int i = 0; i < chunk.size(); i++) {
            EncounterManager encounterx = chunk.getComponent(i, this.encounterComponentType);
            if (encounterx != null && encounterx.getEncounterIndex() == index) {
               refs.add(chunk.getReferenceTo(i));
            }
         }
      });

      for (Ref<EntityStore> ref : refs) {
         if (ref.isValid()) {
            try {
               Holder<EntityStore> holder = store.removeEntity(ref, RemoveReason.UNLOAD);
               EncounterManager encounter = holder.getComponent(this.encounterComponentType);
               if (encounter != null) {
                  encounter.resetRuntime();
               }

               holder.tryRemoveComponent(NetworkId.getComponentType());
               store.addEntity(holder, AddReason.LOAD);
            } catch (RuntimeException e) {
               this.getLogger().at(Level.SEVERE).withCause(e).log("Failed to hot-reload encounter entity %s", ref);
            }
         }
      }
   }

   @Nonnull
   public Model getMarkerModel() {
      if (this.markerModel == null) {
         ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset("Encounter_Marker");
         Objects.requireNonNull(modelAsset, "Missing model asset 'Encounter_Marker' for the encounter marker");
         this.markerModel = Model.createUnitScaleModel(modelAsset);
      }

      return this.markerModel;
   }
}
