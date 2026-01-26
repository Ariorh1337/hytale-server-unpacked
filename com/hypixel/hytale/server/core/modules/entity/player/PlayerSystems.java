/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.entity.player;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.component.system.QuerySystem;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.component.system.tick.RunWhenPausedSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.ComponentUpdate;
import com.hypixel.hytale.protocol.ComponentUpdateType;
import com.hypixel.hytale.protocol.EntityUpdate;
import com.hypixel.hytale.protocol.Equipment;
import com.hypixel.hytale.protocol.ItemArmorSlot;
import com.hypixel.hytale.protocol.ModelTransform;
import com.hypixel.hytale.protocol.Nameplate;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolsSetSoundSet;
import com.hypixel.hytale.protocol.packets.entities.EntityUpdates;
import com.hypixel.hytale.protocol.packets.inventory.SetActiveSlot;
import com.hypixel.hytale.protocol.packets.player.SetBlockPlacementOverride;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
import com.hypixel.hytale.server.core.asset.type.gameplay.PlayerConfig;
import com.hypixel.hytale.server.core.asset.type.gameplay.SpawnConfig;
import com.hypixel.hytale.server.core.asset.type.particle.config.WorldParticle;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerWorldData;
import com.hypixel.hytale.server.core.entity.entities.player.data.UniqueItemUsagesComponent;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.entity.entities.player.pages.RespawnPage;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.DisplayNameComponent;
import com.hypixel.hytale.server.core.modules.entity.component.EntityScaleComponent;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.Intangible;
import com.hypixel.hytale.server.core.modules.entity.component.Interactable;
import com.hypixel.hytale.server.core.modules.entity.component.Invulnerable;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.RespondToHit;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.event.KillFeedEvent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerInput;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSettings;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSkinComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.modules.entity.tracker.EntityTrackerSystems;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.projectile.component.PredictedProjectile;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.universe.world.PlayerUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.PositionUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.lang.runtime.SwitchBootstraps;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class PlayerSystems {
    @Nonnull
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public static class KillFeedDecedentEventSystem
    extends EntityEventSystem<EntityStore, KillFeedEvent.DecedentMessage> {
        @Nonnull
        private final ComponentType<EntityStore, PlayerRef> playerRefComponentType = PlayerRef.getComponentType();

        public KillFeedDecedentEventSystem() {
            super(KillFeedEvent.DecedentMessage.class);
        }

        @Override
        public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull KillFeedEvent.DecedentMessage event) {
            Message displayName;
            DisplayNameComponent displayNameComponent = archetypeChunk.getComponent(index, DisplayNameComponent.getComponentType());
            if (displayNameComponent != null) {
                displayName = displayNameComponent.getDisplayName();
            } else {
                PlayerRef playerRefComponent = archetypeChunk.getComponent(index, this.playerRefComponentType);
                assert (playerRefComponent != null);
                displayName = Message.raw(playerRefComponent.getUsername());
            }
            event.setMessage(displayName);
        }

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return this.playerRefComponentType;
        }
    }

    public static class KillFeedKillerEventSystem
    extends EntityEventSystem<EntityStore, KillFeedEvent.KillerMessage> {
        @Nonnull
        private final ComponentType<EntityStore, PlayerRef> playerRefComponentType = PlayerRef.getComponentType();

        public KillFeedKillerEventSystem() {
            super(KillFeedEvent.KillerMessage.class);
        }

        @Override
        public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull KillFeedEvent.KillerMessage event) {
            Message displayName;
            DisplayNameComponent displayNameComponent = archetypeChunk.getComponent(index, DisplayNameComponent.getComponentType());
            if (displayNameComponent != null) {
                displayName = displayNameComponent.getDisplayName();
            } else {
                PlayerRef playerRefComponent = archetypeChunk.getComponent(index, this.playerRefComponentType);
                assert (playerRefComponent != null);
                displayName = Message.raw(playerRefComponent.getUsername());
            }
            event.setMessage(displayName);
        }

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return this.playerRefComponentType;
        }
    }

    public static class NameplateRefChangeSystem
    extends RefChangeSystem<EntityStore, DisplayNameComponent> {
        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return Player.getComponentType();
        }

        @Override
        @Nonnull
        public ComponentType<EntityStore, DisplayNameComponent> componentType() {
            return DisplayNameComponent.getComponentType();
        }

        @Override
        public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull DisplayNameComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            com.hypixel.hytale.server.core.entity.nameplate.Nameplate nameplateComponent = commandBuffer.ensureAndGetComponent(ref, com.hypixel.hytale.server.core.entity.nameplate.Nameplate.getComponentType());
            nameplateComponent.setText(component.getDisplayName() != null ? component.getDisplayName().getAnsiMessage() : "");
        }

        @Override
        public void onComponentSet(@Nonnull Ref<EntityStore> ref, DisplayNameComponent oldComponent, @Nonnull DisplayNameComponent newComponent, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            com.hypixel.hytale.server.core.entity.nameplate.Nameplate nameplateComponent = commandBuffer.ensureAndGetComponent(ref, com.hypixel.hytale.server.core.entity.nameplate.Nameplate.getComponentType());
            nameplateComponent.setText(newComponent.getDisplayName() != null ? newComponent.getDisplayName().getAnsiMessage() : "");
        }

        @Override
        public void onComponentRemoved(@Nonnull Ref<EntityStore> ref, @Nonnull DisplayNameComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            com.hypixel.hytale.server.core.entity.nameplate.Nameplate nameplateComponent = commandBuffer.ensureAndGetComponent(ref, com.hypixel.hytale.server.core.entity.nameplate.Nameplate.getComponentType());
            nameplateComponent.setText("");
        }
    }

    public static class NameplateRefSystem
    extends RefSystem<EntityStore> {
        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return Archetype.of(Player.getComponentType(), DisplayNameComponent.getComponentType());
        }

        @Override
        public void onEntityAdded(@Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            DisplayNameComponent displayNameComponent = commandBuffer.getComponent(ref, DisplayNameComponent.getComponentType());
            assert (displayNameComponent != null);
            if (commandBuffer.getComponent(ref, com.hypixel.hytale.server.core.entity.nameplate.Nameplate.getComponentType()) != null) {
                return;
            }
            String displayName = displayNameComponent.getDisplayName() != null ? displayNameComponent.getDisplayName().getAnsiMessage() : "";
            com.hypixel.hytale.server.core.entity.nameplate.Nameplate nameplateComponent = new com.hypixel.hytale.server.core.entity.nameplate.Nameplate(displayName);
            commandBuffer.putComponent(ref, com.hypixel.hytale.server.core.entity.nameplate.Nameplate.getComponentType(), nameplateComponent);
        }

        @Override
        public void onEntityRemove(@Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        }
    }

    public static class PlayerRemovedSystem
    extends HolderSystem<EntityStore> {
        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(PlayerRef.getComponentType(), Player.getComponentType(), TransformComponent.getComponentType(), HeadRotation.getComponentType(), DisplayNameComponent.getComponentType());
        }

        @Override
        public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
        }

        @Override
        public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
            World world = store.getExternalData().getWorld();
            PlayerRef playerRefComponent = holder.getComponent(PlayerRef.getComponentType());
            assert (playerRefComponent != null);
            Player playerComponent = holder.getComponent(Player.getComponentType());
            assert (playerComponent != null);
            TransformComponent transformComponent = holder.getComponent(TransformComponent.getComponentType());
            assert (transformComponent != null);
            HeadRotation headRotationComponent = holder.getComponent(HeadRotation.getComponentType());
            assert (headRotationComponent != null);
            DisplayNameComponent displayNameComponent = holder.getComponent(DisplayNameComponent.getComponentType());
            assert (displayNameComponent != null);
            Message displayName = displayNameComponent.getDisplayName();
            LOGGER.at(Level.INFO).log("Removing player '%s%s' from world '%s' (%s)", playerRefComponent.getUsername(), displayName != null ? " (" + displayName.getAnsiMessage() + ")" : "", world.getName(), playerRefComponent.getUuid());
            playerComponent.getPlayerConfigData().getPerWorldData(world.getName()).setLastPosition(new Transform(transformComponent.getPosition().clone(), headRotationComponent.getRotation().clone()));
            playerRefComponent.getPacketHandler().setQueuePackets(false);
            playerRefComponent.getPacketHandler().tryFlush();
            WorldConfig worldConfig = world.getWorldConfig();
            PlayerUtil.broadcastMessageToPlayers(playerRefComponent.getUuid(), Message.translation("server.general.playerLeftWorld").param("username", playerRefComponent.getUsername()).param("world", worldConfig.getDisplayName() != null ? worldConfig.getDisplayName() : WorldConfig.formatDisplayName(world.getName())), store);
        }
    }

    public static class EnsureUniqueItemUsagesSystem
    extends HolderSystem<EntityStore> {
        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(PlayerRef.getComponentType(), Query.not(UniqueItemUsagesComponent.getComponentType()));
        }

        @Override
        public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
            holder.ensureComponent(UniqueItemUsagesComponent.getComponentType());
        }

        @Override
        public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
        }
    }

    public static class EnsureEffectControllerSystem
    extends HolderSystem<EntityStore> {
        @Override
        public Query<EntityStore> getQuery() {
            return PlayerRef.getComponentType();
        }

        @Override
        public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
            holder.ensureComponent(EffectControllerComponent.getComponentType());
        }

        @Override
        public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
        }
    }

    public static class EnsurePlayerInput
    extends HolderSystem<EntityStore> {
        @Override
        public Query<EntityStore> getQuery() {
            return PlayerRef.getComponentType();
        }

        @Override
        public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
            holder.ensureComponent(PlayerInput.getComponentType());
        }

        @Override
        public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
            holder.removeComponent(PlayerInput.getComponentType());
        }
    }

    public static class BlockPausedMovementSystem
    implements RunWhenPausedSystem<EntityStore>,
    QuerySystem<EntityStore> {
        @Nonnull
        private final Query<EntityStore> query = Query.and(Player.getComponentType(), PlayerInput.getComponentType(), TransformComponent.getComponentType(), HeadRotation.getComponentType());

        @Override
        public void tick(float dt, int systemIndex, @Nonnull Store<EntityStore> store) {
            store.forEachChunk(systemIndex, BlockPausedMovementSystem::onTick);
        }

        private static void onTick(@Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            for (int index = 0; index < archetypeChunk.size(); ++index) {
                PlayerInput playerInputComponent = archetypeChunk.getComponent(index, PlayerInput.getComponentType());
                assert (playerInputComponent != null);
                boolean shouldTeleport = false;
                TransformComponent transformComponent = archetypeChunk.getComponent(index, TransformComponent.getComponentType());
                assert (transformComponent != null);
                HeadRotation headRotationComponent = archetypeChunk.getComponent(index, HeadRotation.getComponentType());
                assert (headRotationComponent != null);
                List<PlayerInput.InputUpdate> movementUpdateQueue = playerInputComponent.getMovementUpdateQueue();
                for (PlayerInput.InputUpdate entry : movementUpdateQueue) {
                    PlayerInput.InputUpdate inputUpdate;
                    Objects.requireNonNull(entry);
                    int n = 0;
                    switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{PlayerInput.AbsoluteMovement.class, PlayerInput.RelativeMovement.class, PlayerInput.SetHead.class}, (Object)inputUpdate, n)) {
                        case 0: {
                            PlayerInput.AbsoluteMovement abs = (PlayerInput.AbsoluteMovement)inputUpdate;
                            shouldTeleport = transformComponent.getPosition().distanceSquaredTo(abs.getX(), abs.getY(), abs.getZ()) > (double)0.01f;
                            break;
                        }
                        case 1: {
                            PlayerInput.RelativeMovement rel = (PlayerInput.RelativeMovement)inputUpdate;
                            Vector3d position = transformComponent.getPosition();
                            shouldTeleport = transformComponent.getPosition().distanceSquaredTo(position.x + rel.getX(), position.y + rel.getY(), position.z + rel.getZ()) > (double)0.01f;
                            break;
                        }
                        case 2: {
                            PlayerInput.SetHead head = (PlayerInput.SetHead)inputUpdate;
                            shouldTeleport = headRotationComponent.getRotation().distanceSquaredTo(head.direction().pitch, head.direction().yaw, head.direction().roll) > 0.01f;
                            break;
                        }
                    }
                }
                movementUpdateQueue.clear();
                if (!shouldTeleport) continue;
                Teleport teleport = Teleport.createExact(transformComponent.getPosition(), transformComponent.getRotation(), headRotationComponent.getRotation()).withoutVelocityReset();
                Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
                commandBuffer.addComponent(ref, Teleport.getComponentType(), teleport);
            }
        }

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return this.query;
        }
    }

    public static class UpdatePlayerRef
    extends EntityTickingSystem<EntityStore> {
        @Nonnull
        private final Query<EntityStore> query = Query.and(PlayerRef.getComponentType(), TransformComponent.getComponentType(), HeadRotation.getComponentType());

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return this.query;
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            World world = commandBuffer.getExternalData().getWorld();
            TransformComponent transformComponent = archetypeChunk.getComponent(index, TransformComponent.getComponentType());
            assert (transformComponent != null);
            Transform transform = transformComponent.getTransform();
            HeadRotation headRotationComponent = archetypeChunk.getComponent(index, HeadRotation.getComponentType());
            assert (headRotationComponent != null);
            Vector3f headRotation = headRotationComponent.getRotation();
            PlayerRef playerRefComponent = archetypeChunk.getComponent(index, PlayerRef.getComponentType());
            assert (playerRefComponent != null);
            playerRefComponent.updatePosition(world, transform, headRotation);
        }
    }

    public static class ProcessPlayerInput
    extends EntityTickingSystem<EntityStore> {
        @Nonnull
        private final Query<EntityStore> query = Query.and(Player.getComponentType(), PlayerInput.getComponentType(), TransformComponent.getComponentType());

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return this.query;
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            PlayerInput playerInputComponent = archetypeChunk.getComponent(index, PlayerInput.getComponentType());
            assert (playerInputComponent != null);
            List<PlayerInput.InputUpdate> movementUpdateQueue = playerInputComponent.getMovementUpdateQueue();
            for (PlayerInput.InputUpdate entry : movementUpdateQueue) {
                entry.apply(commandBuffer, archetypeChunk, index);
            }
            movementUpdateQueue.clear();
        }
    }

    public static class PlayerAddedSystem
    extends RefSystem<EntityStore> {
        @Nonnull
        private static final Message MESSAGE_SERVER_GENERAL_KILLED_BY_UNKNOWN = Message.translation("server.general.killedByUnknown");
        @Nonnull
        private final Set<Dependency<EntityStore>> dependencies = Set.of(new SystemDependency(Order.AFTER, PlayerSpawnedSystem.class));
        @Nonnull
        private final Query<EntityStore> query;

        public PlayerAddedSystem(@Nonnull ComponentType<EntityStore, MovementManager> movementManagerComponentType) {
            this.query = Query.and(Player.getComponentType(), PlayerRef.getComponentType(), movementManagerComponentType);
        }

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return this.query;
        }

        @Override
        @Nonnull
        public Set<Dependency<EntityStore>> getDependencies() {
            return this.dependencies;
        }

        @Override
        public void onEntityAdded(@Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            World world = commandBuffer.getExternalData().getWorld();
            Player playerComponent = commandBuffer.getComponent(ref, Player.getComponentType());
            assert (playerComponent != null);
            PlayerRef playerRefComponent = commandBuffer.getComponent(ref, PlayerRef.getComponentType());
            assert (playerRefComponent != null);
            MovementManager movementManagerComponent = commandBuffer.getComponent(ref, MovementManager.getComponentType());
            assert (movementManagerComponent != null);
            if (commandBuffer.getComponent(ref, DisplayNameComponent.getComponentType()) == null) {
                Message displayName = Message.raw(playerRefComponent.getUsername());
                commandBuffer.putComponent(ref, DisplayNameComponent.getComponentType(), new DisplayNameComponent(displayName));
            }
            playerComponent.setLastSpawnTimeNanos(System.nanoTime());
            PacketHandler playerConnection = playerRefComponent.getPacketHandler();
            Objects.requireNonNull(world, "world");
            Objects.requireNonNull(playerComponent.getPlayerConfigData(), "data");
            PlayerWorldData perWorldData = playerComponent.getPlayerConfigData().getPerWorldData(world.getName());
            Player.initGameMode(ref, commandBuffer);
            playerConnection.writeNoCache(new BuilderToolsSetSoundSet(world.getGameplayConfig().getCreativePlaySoundSetIndex()));
            playerComponent.sendInventory();
            Inventory playerInventory = playerComponent.getInventory();
            playerConnection.writeNoCache(new SetActiveSlot(-1, playerInventory.getActiveHotbarSlot()));
            playerConnection.writeNoCache(new SetActiveSlot(-5, playerInventory.getActiveUtilitySlot()));
            playerConnection.writeNoCache(new SetActiveSlot(-8, playerInventory.getActiveToolsSlot()));
            if (playerInventory.containsBrokenItem()) {
                playerComponent.sendMessage(Message.translation("server.general.repair.itemBrokenOnRespawn").color("#ff5555"));
            }
            playerConnection.writeNoCache(new SetBlockPlacementOverride(playerComponent.isOverrideBlockPlacementRestrictions()));
            DeathComponent deathComponent = commandBuffer.getComponent(ref, DeathComponent.getComponentType());
            if (deathComponent != null) {
                Message pendingDeathMessage = deathComponent.getDeathMessage();
                if (pendingDeathMessage == null) {
                    ((HytaleLogger.Api)Entity.LOGGER.at(Level.SEVERE).withCause(new Throwable())).log("Player wasn't alive but didn't have a pending death message?");
                    pendingDeathMessage = MESSAGE_SERVER_GENERAL_KILLED_BY_UNKNOWN;
                }
                RespawnPage respawnPage = new RespawnPage(playerRefComponent, pendingDeathMessage, deathComponent.displayDataOnDeathScreen(), deathComponent.getDeathItemLoss());
                playerComponent.getPageManager().openCustomPage(ref, store, respawnPage);
            }
            TransformComponent transform = commandBuffer.getComponent(ref, TransformComponent.getComponentType());
            GameplayConfig gameplayConfig = world.getGameplayConfig();
            SpawnConfig spawnConfig = gameplayConfig.getSpawnConfig();
            if (transform != null) {
                Vector3d position = transform.getPosition();
                SpatialResource<Ref<EntityStore>, EntityStore> playerSpatialResource = commandBuffer.getResource(EntityModule.get().getPlayerSpatialResourceType());
                ObjectList<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
                playerSpatialResource.getSpatialStructure().collect(position, 75.0, results);
                results.add(ref);
                if (playerComponent.isFirstSpawn()) {
                    WorldParticle[] firstSpawnParticles = spawnConfig.getFirstSpawnParticles();
                    if (firstSpawnParticles == null) {
                        firstSpawnParticles = spawnConfig.getSpawnParticles();
                    }
                    if (firstSpawnParticles != null) {
                        ParticleUtil.spawnParticleEffects(firstSpawnParticles, position, null, results, commandBuffer);
                    }
                } else {
                    WorldParticle[] spawnParticles = spawnConfig.getSpawnParticles();
                    if (spawnParticles != null) {
                        ParticleUtil.spawnParticleEffects(spawnParticles, position, null, results, commandBuffer);
                    }
                }
            }
            playerConnection.tryFlush();
            perWorldData.setFirstSpawn(false);
        }

        @Override
        public void onEntityRemove(@Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            Player playerComponent = commandBuffer.getComponent(ref, Player.getComponentType());
            assert (playerComponent != null);
            playerComponent.getWindowManager().closeAllWindows(ref, commandBuffer);
        }
    }

    public static class PlayerSpawnedSystem
    extends RefSystem<EntityStore> {
        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return Player.getComponentType();
        }

        @Override
        public void onEntityAdded(@Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            PlayerSpawnedSystem.sendPlayerSelf(ref, store);
        }

        @Override
        public void onEntityRemove(@Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        }

        @Deprecated
        public static void sendPlayerSelf(@Nonnull Ref<EntityStore> viewerRef, @Nonnull Store<EntityStore> store) {
            EntityStatMap statMapComponent;
            EffectControllerComponent effectControllerComponent;
            PredictedProjectile predictionComponent;
            com.hypixel.hytale.server.core.entity.nameplate.Nameplate nameplateComponent;
            ComponentUpdate update;
            EntityTrackerSystems.EntityViewer entityViewerComponent = store.getComponent(viewerRef, EntityTrackerSystems.EntityViewer.getComponentType());
            if (entityViewerComponent == null) {
                throw new IllegalArgumentException("Viewer is missing EntityViewer component");
            }
            NetworkId networkIdComponent = store.getComponent(viewerRef, NetworkId.getComponentType());
            if (networkIdComponent == null) {
                throw new IllegalArgumentException("Viewer is missing NetworkId component");
            }
            Player playerComponent = store.getComponent(viewerRef, Player.getComponentType());
            if (playerComponent == null) {
                throw new IllegalArgumentException("Viewer is missing Player component");
            }
            EntityUpdate entityUpdate = new EntityUpdate();
            entityUpdate.networkId = networkIdComponent.getId();
            ObjectArrayList<ComponentUpdate> list = new ObjectArrayList<ComponentUpdate>();
            Archetype<EntityStore> viewerArchetype = store.getArchetype(viewerRef);
            if (viewerArchetype.contains(Interactable.getComponentType())) {
                update = new ComponentUpdate();
                update.type = ComponentUpdateType.Interactable;
                list.add(update);
            }
            if (viewerArchetype.contains(Intangible.getComponentType())) {
                update = new ComponentUpdate();
                update.type = ComponentUpdateType.Intangible;
                list.add(update);
            }
            if (viewerArchetype.contains(Invulnerable.getComponentType())) {
                update = new ComponentUpdate();
                update.type = ComponentUpdateType.Invulnerable;
                list.add(update);
            }
            if (viewerArchetype.contains(RespondToHit.getComponentType())) {
                update = new ComponentUpdate();
                update.type = ComponentUpdateType.RespondToHit;
                list.add(update);
            }
            if ((nameplateComponent = store.getComponent(viewerRef, com.hypixel.hytale.server.core.entity.nameplate.Nameplate.getComponentType())) != null) {
                ComponentUpdate update2 = new ComponentUpdate();
                update2.type = ComponentUpdateType.Nameplate;
                update2.nameplate = new Nameplate();
                update2.nameplate.text = nameplateComponent.getText();
                list.add(update2);
            }
            if ((predictionComponent = store.getComponent(viewerRef, PredictedProjectile.getComponentType())) != null) {
                ComponentUpdate update3 = new ComponentUpdate();
                update3.type = ComponentUpdateType.Prediction;
                update3.predictionId = predictionComponent.getUuid();
                list.add(update3);
            }
            ModelComponent modelComponent = store.getComponent(viewerRef, ModelComponent.getComponentType());
            ComponentUpdate update4 = new ComponentUpdate();
            update4.type = ComponentUpdateType.Model;
            update4.model = modelComponent != null ? modelComponent.getModel().toPacket() : null;
            EntityScaleComponent entityScaleComponent = store.getComponent(viewerRef, EntityScaleComponent.getComponentType());
            if (entityScaleComponent != null) {
                update4.entityScale = entityScaleComponent.getScale();
            }
            list.add(update4);
            update4 = new ComponentUpdate();
            update4.type = ComponentUpdateType.PlayerSkin;
            PlayerSkinComponent playerSkinComponent = store.getComponent(viewerRef, PlayerSkinComponent.getComponentType());
            update4.skin = playerSkinComponent != null ? playerSkinComponent.getPlayerSkin() : null;
            list.add(update4);
            Inventory inventory = playerComponent.getInventory();
            ComponentUpdate update5 = new ComponentUpdate();
            update5.type = ComponentUpdateType.Equipment;
            update5.equipment = new Equipment();
            ItemContainer armor = inventory.getArmor();
            update5.equipment.armorIds = new String[armor.getCapacity()];
            Arrays.fill(update5.equipment.armorIds, "");
            armor.forEachWithMeta((slot, itemStack, armorIds) -> {
                armorIds[slot] = itemStack.getItemId();
            }, update5.equipment.armorIds);
            PlayerSettings playerSettingsComponent = store.getComponent(viewerRef, PlayerSettings.getComponentType());
            if (playerSettingsComponent != null) {
                PlayerConfig.ArmorVisibilityOption armorVisibilityOption = store.getExternalData().getWorld().getGameplayConfig().getPlayerConfig().getArmorVisibilityOption();
                if (armorVisibilityOption.canHideHelmet() && playerSettingsComponent.hideHelmet()) {
                    update5.equipment.armorIds[ItemArmorSlot.Head.ordinal()] = "";
                }
                if (armorVisibilityOption.canHideCuirass() && playerSettingsComponent.hideCuirass()) {
                    update5.equipment.armorIds[ItemArmorSlot.Chest.ordinal()] = "";
                }
                if (armorVisibilityOption.canHideGauntlets() && playerSettingsComponent.hideGauntlets()) {
                    update5.equipment.armorIds[ItemArmorSlot.Hands.ordinal()] = "";
                }
                if (armorVisibilityOption.canHidePants() && playerSettingsComponent.hidePants()) {
                    update5.equipment.armorIds[ItemArmorSlot.Legs.ordinal()] = "";
                }
            }
            ItemStack itemInHand = inventory.getItemInHand();
            update5.equipment.rightHandItemId = itemInHand != null ? itemInHand.getItemId() : "Empty";
            ItemStack utilityItem = inventory.getUtilityItem();
            update5.equipment.leftHandItemId = utilityItem != null ? utilityItem.getItemId() : "Empty";
            list.add(update5);
            TransformComponent transformComponent = store.getComponent(viewerRef, TransformComponent.getComponentType());
            HeadRotation headRotationComponent = store.getComponent(viewerRef, HeadRotation.getComponentType());
            if (transformComponent != null && headRotationComponent != null) {
                ComponentUpdate update6 = new ComponentUpdate();
                update6.type = ComponentUpdateType.Transform;
                update6.transform = new ModelTransform();
                update6.transform.position = PositionUtil.toPositionPacket(transformComponent.getPosition());
                update6.transform.bodyOrientation = PositionUtil.toDirectionPacket(transformComponent.getRotation());
                update6.transform.lookOrientation = PositionUtil.toDirectionPacket(headRotationComponent.getRotation());
                list.add(update6);
            }
            if ((effectControllerComponent = store.getComponent(viewerRef, EffectControllerComponent.getComponentType())) != null) {
                ComponentUpdate update7 = new ComponentUpdate();
                update7.type = ComponentUpdateType.EntityEffects;
                update7.entityEffectUpdates = effectControllerComponent.createInitUpdates();
                list.add(update7);
            }
            if ((statMapComponent = store.getComponent(viewerRef, EntityStatMap.getComponentType())) != null) {
                ComponentUpdate update8 = new ComponentUpdate();
                update8.type = ComponentUpdateType.EntityStats;
                update8.entityStatUpdates = statMapComponent.createInitUpdate(true);
                list.add(update8);
            }
            entityUpdate.updates = (ComponentUpdate[])list.toArray(ComponentUpdate[]::new);
            entityViewerComponent.packetReceiver.writeNoCache(new EntityUpdates(null, new EntityUpdate[]{entityUpdate}));
        }
    }
}

