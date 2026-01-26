/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.commands;

import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.common.util.RandomUtil;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.function.consumer.TriConsumer;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.PlayerSkin;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.command.system.exceptions.GeneralCommandException;
import com.hypixel.hytale.server.core.cosmetics.CosmeticsModule;
import com.hypixel.hytale.server.core.entity.Frozen;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.player.ApplyRandomSkinPersistedComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSkinComponent;
import com.hypixel.hytale.server.core.modules.physics.util.PhysicsMath;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.flock.FlockPlugin;
import com.hypixel.hytale.server.flock.config.FlockAsset;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderInfo;
import com.hypixel.hytale.server.npc.commands.NPCCommand;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.pages.EntitySpawnPage;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.role.RoleDebugFlags;
import com.hypixel.hytale.server.spawning.ISpawnableWithModel;
import com.hypixel.hytale.server.spawning.SpawnTestResult;
import com.hypixel.hytale.server.spawning.SpawningContext;
import java.util.AbstractCollection;
import java.util.EnumSet;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NPCSpawnCommand
extends AbstractPlayerCommand {
    private static final double PLAYER_FOOT_POINT_EPSILON = 0.01;
    @Nonnull
    private final RequiredArg<BuilderInfo> roleArg = this.withRequiredArg("role", "server.commands.npc.spawn.role.desc", NPCCommand.NPC_ROLE);
    @Nonnull
    private final OptionalArg<Integer> countArg = (OptionalArg)this.withOptionalArg("count", "server.commands.npc.spawn.count.desc", ArgTypes.INTEGER).addValidator(Validators.greaterThan(0));
    @Nonnull
    private final OptionalArg<Double> radiusArg = (OptionalArg)this.withOptionalArg("radius", "server.commands.npc.spawn.radius.desc", ArgTypes.DOUBLE).addValidator(Validators.greaterThan(0.0));
    @Nonnull
    private final OptionalArg<String> flagsArg = this.withOptionalArg("flags", "server.commands.npc.spawn.flags.desc", ArgTypes.STRING);
    @Nonnull
    private final OptionalArg<Double> speedArg = (OptionalArg)this.withOptionalArg("speed", "server.commands.npc.spawn.speed.desc", ArgTypes.DOUBLE).addValidator(Validators.greaterThan(0.0));
    @Nonnull
    private final FlagArg nonRandomArg = this.withFlagArg("nonrandom", "server.commands.npc.spawn.random.desc");
    @Nonnull
    private final OptionalArg<String> positionSetArg = this.withOptionalArg("position", "server.commands.npc.spawn.position.desc", ArgTypes.STRING);
    @Nonnull
    private final OptionalArg<String> posOffsetArg = this.withOptionalArg("posOffset", "server.commands.npc.spawn.posOffset.desc", ArgTypes.STRING);
    @Nonnull
    private final OptionalArg<String> headRotationArg = this.withOptionalArg("headRotation", "server.commands.npc.spawn.headRotation.desc", ArgTypes.STRING);
    @Nonnull
    private final OptionalArg<String> bodyRotationArg = this.withOptionalArg("bodyRotation", "server.commands.npc.spawn.bodyRotation.desc", ArgTypes.STRING);
    @Nonnull
    private final FlagArg randomRotationArg = this.withFlagArg("randomRotation", "server.commands.npc.spawn.randomRotation.desc");
    @Nonnull
    private final FlagArg facingRotationArg = this.withFlagArg("facingRotation", "server.commands.npc.spawn.facingRotation.desc");
    @Nonnull
    private final OptionalArg<String> flockArg = this.withOptionalArg("flock", "server.commands.npc.spawn.flock.desc", ArgTypes.STRING);
    @Nonnull
    private final FlagArg testArg = this.withFlagArg("test", "server.commands.npc.spawn.test.desc");
    @Nonnull
    private final FlagArg spawnOnGroundArg = this.withFlagArg("spawnOnGround", "server.commands.npc.spawn.spawnOnGround.desc");
    @Nonnull
    private final FlagArg frozenArg = this.withFlagArg("frozen", "server.commands.npc.spawn.frozen.desc");
    @Nonnull
    private final FlagArg randomModelArg = this.withFlagArg("randomModel", "server.commands.npc.spawn.randomModel.desc");
    @Nonnull
    private final OptionalArg<Float> scaleArg = this.withOptionalArg("scale", "server.commands.npc.spawn.scale.desc", ArgTypes.FLOAT);
    @Nonnull
    private final FlagArg bypassScaleLimitsArg = this.withFlagArg("bypassScaleLimits", "server.commands.npc.spawn.bypassScaleLimits.desc");

    public NPCSpawnCommand() {
        super("spawn", "server.commands.npc.spawn.desc");
        this.addUsageVariant(new SpawnPageCommand());
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        NPCPlugin npcPlugin = NPCPlugin.get();
        BuilderInfo roleInfo = (BuilderInfo)this.roleArg.get(context);
        int roleIndex = roleInfo.getIndex();
        HeadRotation headRotationComponent = store.getComponent(ref, HeadRotation.getComponentType());
        assert (headRotationComponent != null);
        Vector3f playerHeadRotation = headRotationComponent.getRotation();
        TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        assert (transformComponent != null);
        Vector3d playerPosition = transformComponent.getPosition();
        BoundingBox boundingBoxComponent = store.getComponent(ref, BoundingBox.getComponentType());
        assert (boundingBoxComponent != null);
        Box playerBoundingBox = boundingBoxComponent.getBoundingBox();
        int count = this.countArg.provided(context) ? (Integer)this.countArg.get(context) : 1;
        double radius = this.radiusArg.provided(context) ? (Double)this.radiusArg.get(context) : 8.0;
        String flagsString = this.flagsArg.provided(context) ? (String)this.flagsArg.get(context) : null;
        EnumSet<RoleDebugFlags> flags = flagsString != null ? RoleDebugFlags.getFlags(flagsString.split(",")) : RoleDebugFlags.getPreset("none");
        Vector3d velocity = new Vector3d(Vector3d.ZERO);
        if (this.speedArg.provided(context)) {
            PhysicsMath.vectorFromAngles(playerHeadRotation.getYaw(), playerHeadRotation.getPitch(), velocity);
            velocity.setLength((Double)this.speedArg.get(context));
        }
        Random random = (Boolean)this.nonRandomArg.get(context) != false ? new Random(0L) : ThreadLocalRandom.current();
        Vector3d posOffset = this.posOffsetArg.provided(context) ? this.parseVector3d(context, (String)this.posOffsetArg.get(context)) : null;
        Vector3f headRotation = this.headRotationArg.provided(context) ? this.parseVector3f(context, (String)this.headRotationArg.get(context)) : null;
        boolean randomRotation = false;
        Vector3f rotation = playerHeadRotation;
        if (this.bodyRotationArg.provided(context)) {
            rotation = this.parseVector3f(context, (String)this.bodyRotationArg.get(context));
        } else if (((Boolean)this.randomRotationArg.get(context)).booleanValue()) {
            randomRotation = true;
        } else if (((Boolean)this.facingRotationArg.get(context)).booleanValue()) {
            rotation.setY(rotation.getY() - (float)Math.PI);
        }
        String flockSizeString = this.flockArg.provided(context) ? (String)this.flockArg.get(context) : "1";
        Integer flockSize = this.parseFlockSize(context, flockSizeString);
        if (flockSize == null) {
            return;
        }
        Boolean frozen = (Boolean)this.frozenArg.get(context);
        npcPlugin.forceValidation(roleIndex);
        if (!npcPlugin.testAndValidateRole(roleInfo)) {
            throw new GeneralCommandException(Message.translation("server.commands.npc.spawn.validation_failed"));
        }
        try {
            for (int i = 0; i < count; ++i) {
                NPCEntity npc;
                Ref<EntityStore> npcRef;
                Model model;
                Builder<Role> roleBuilder = npcPlugin.tryGetCachedValidRole(roleIndex);
                if (roleBuilder == null) {
                    throw new IllegalArgumentException("Can't find a matching role builder");
                }
                if (!(roleBuilder instanceof ISpawnableWithModel)) {
                    throw new IllegalArgumentException("Role builder must support ISpawnableWithModel interface");
                }
                ISpawnableWithModel spawnable = (ISpawnableWithModel)((Object)roleBuilder);
                if (!roleBuilder.isSpawnable()) {
                    throw new IllegalArgumentException("Abstract role templates cannot be spawned directly - a variant needs to be created!");
                }
                SpawningContext spawningContext = new SpawningContext();
                if (!spawningContext.setSpawnable(spawnable)) {
                    throw new GeneralCommandException(Message.translation("server.commands.npc.spawn.cantSetRolebuilder"));
                }
                TriConsumer<NPCEntity, Ref<EntityStore>, Store<EntityStore>> skinApplyingFunction = null;
                if (((Boolean)this.randomModelArg.get(context)).booleanValue()) {
                    PlayerSkin playerSkin = CosmeticsModule.get().generateRandomSkin(RandomUtil.getSecureRandom());
                    model = CosmeticsModule.get().createModel(playerSkin);
                    skinApplyingFunction = (npcEntity, entityStoreRef, entityStore) -> {
                        entityStore.putComponent(entityStoreRef, PlayerSkinComponent.getComponentType(), new PlayerSkinComponent(playerSkin));
                        entityStore.putComponent(entityStoreRef, ApplyRandomSkinPersistedComponent.getComponentType(), ApplyRandomSkinPersistedComponent.INSTANCE);
                    };
                } else {
                    model = spawningContext.getModel();
                }
                if (randomRotation) {
                    rotation = new Vector3f(0.0f, (float)(2.0 * random.nextDouble() * Math.PI), 0.0f);
                }
                if (this.scaleArg.provided(context)) {
                    ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset(model.getModelAssetId());
                    assert (modelAsset != null);
                    Float scale = (Float)this.scaleArg.get(context);
                    if (!((Boolean)this.bypassScaleLimitsArg.get(context)).booleanValue()) {
                        scale = Float.valueOf(MathUtil.clamp(scale.floatValue(), modelAsset.getMinScale(), modelAsset.getMaxScale()));
                    }
                    model = Model.createScaledModel(modelAsset, scale.floatValue());
                }
                if (count == 1 && ((Boolean)this.testArg.get(context)).booleanValue()) {
                    if (!spawningContext.set(world, playerPosition.x, playerPosition.y, playerPosition.z)) {
                        throw new GeneralCommandException(Message.translation("server.commands.npc.spawn.cantSpawnNotEnoughSpace"));
                    }
                    if (spawnable.canSpawn(spawningContext) != SpawnTestResult.TEST_OK) {
                        throw new GeneralCommandException(Message.translation("server.commands.npc.spawn.cantSpawnNotSuitable"));
                    }
                    Vector3d spawnPosition = spawningContext.newPosition();
                    if (posOffset != null) {
                        spawnPosition.add(posOffset);
                    }
                    npcPair = npcPlugin.spawnEntity(store, roleIndex, spawnPosition, rotation, model, skinApplyingFunction);
                    npcRef = npcPair.first();
                    npc = npcPair.second();
                    if (flockSize > 1) {
                        FlockPlugin.trySpawnFlock(npcRef, npc, store, roleIndex, spawnPosition, rotation, flockSize, skinApplyingFunction);
                    }
                } else {
                    Vector3d position;
                    if (this.positionSetArg.provided(context)) {
                        position = this.parseVector3d(context, (String)this.positionSetArg.get(context));
                        if (position == null) {
                            return;
                        }
                        position.y -= model.getBoundingBox().min.y;
                    } else {
                        position = new Vector3d(playerPosition);
                        position.y = Math.floor(position.y + playerBoundingBox.min.y + 0.01) - model.getBoundingBox().min.y;
                    }
                    if (posOffset != null) {
                        position.add(posOffset);
                    }
                    npcPair = npcPlugin.spawnEntity(store, roleIndex, position, rotation, model, skinApplyingFunction);
                    npcRef = npcPair.first();
                    npc = npcPair.second();
                    if (flockSize > 1) {
                        FlockPlugin.trySpawnFlock(npcRef, npc, store, roleIndex, position, rotation, flockSize, skinApplyingFunction);
                    }
                }
                TransformComponent npcTransformComponent = store.getComponent(npcRef, TransformComponent.getComponentType());
                assert (npcTransformComponent != null);
                HeadRotation npcHeadRotationComponent = store.getComponent(npcRef, HeadRotation.getComponentType());
                assert (npcHeadRotationComponent != null);
                UUIDComponent npcUuidComponent = store.getComponent(npcRef, UUIDComponent.getComponentType());
                assert (npcUuidComponent != null);
                if (headRotation != null) {
                    npcHeadRotationComponent.getRotation().assign(headRotation);
                    store.ensureComponent(npcRef, Frozen.getComponentType());
                }
                Vector3d npcPosition = npcTransformComponent.getPosition();
                double x = npcPosition.getX();
                double y = npcPosition.getY();
                double z = npcPosition.getZ();
                if (count > 1) {
                    x += random.nextDouble() * 2.0 * radius - radius;
                    z += random.nextDouble() * 2.0 * radius - radius;
                    y += (Boolean)this.spawnOnGroundArg.get(context) != false ? 0.1 : random.nextDouble() * 2.0 + 5.0;
                } else {
                    y += 0.1;
                }
                npcPosition.assign(x, y, z);
                npc.saveLeashInformation(npcPosition, npcTransformComponent.getRotation());
                if (!velocity.equals(Vector3d.ZERO)) {
                    npc.getRole().forceVelocity(velocity, null, false);
                }
                if (frozen.booleanValue()) {
                    store.ensureComponent(npcRef, Frozen.getComponentType());
                }
                Object debugFlags = npc.getRoleDebugFlags().clone();
                ((AbstractCollection)debugFlags).addAll(flags);
                if (!((AbstractCollection)debugFlags).isEmpty()) {
                    Holder<EntityStore> holder = store.removeEntity(npcRef, RemoveReason.UNLOAD);
                    npc.setRoleDebugFlags((EnumSet<RoleDebugFlags>)debugFlags);
                    store.addEntity(holder, AddReason.LOAD);
                }
                NPCPlugin.get().getLogger().at(Level.INFO).log("%s created with id %s at position %s", npc.getRoleName(), npcUuidComponent.getUuid(), Vector3d.formatShortString(npcPosition));
            }
        }
        catch (IllegalArgumentException | IllegalStateException | NullPointerException e) {
            NPCPlugin.get().getLogger().at(Level.WARNING).log("Spawn failed: " + e.getMessage());
            throw new GeneralCommandException(Message.translation("server.commands.npc.spawn.failed").param("reason", e.getMessage()));
        }
    }

    @Nullable
    private Vector3d parseVector3d(@Nonnull CommandContext context, @Nonnull String str) {
        String[] parts = str.split(",");
        if (parts.length != 3) {
            context.sendMessage(Message.raw("Invalid Vector3d format: must be three comma-separated doubles"));
            return null;
        }
        try {
            return new Vector3d(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
        }
        catch (NumberFormatException e) {
            context.sendMessage(Message.raw("Invalid Vector3d format: " + e.getMessage()));
            return null;
        }
    }

    @Nullable
    private Vector3f parseVector3f(@Nonnull CommandContext context, @Nonnull String str) {
        String[] parts = str.split(",");
        if (parts.length != 3) {
            context.sendMessage(Message.raw("Invalid Vector3f format: must be three comma-separated floats"));
            return null;
        }
        try {
            return new Vector3f(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]));
        }
        catch (NumberFormatException e) {
            context.sendMessage(Message.raw("Invalid Vector3f format: " + e.getMessage()));
            return null;
        }
    }

    @Nullable
    private Integer parseFlockSize(@Nonnull CommandContext context, @Nonnull String str) {
        try {
            Integer size = Integer.valueOf(str);
            if (size <= 0) {
                context.sendMessage(Message.raw("Flock size must be greater than 0!"));
                return null;
            }
            return size;
        }
        catch (NumberFormatException e) {
            FlockAsset flockDefinition = (FlockAsset)FlockAsset.getAssetMap().getAsset(str);
            if (flockDefinition == null) {
                context.sendMessage(Message.raw("No such flock asset: " + str));
                return null;
            }
            return flockDefinition.pickFlockSize();
        }
    }

    public static class SpawnPageCommand
    extends AbstractPlayerCommand {
        public SpawnPageCommand() {
            super("server.commands.npc.spawn.page.desc");
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            assert (playerComponent != null);
            PlayerRef playerRefComponent = store.getComponent(ref, PlayerRef.getComponentType());
            assert (playerRefComponent != null);
            playerComponent.getPageManager().openCustomPage(ref, store, new EntitySpawnPage(playerRefComponent));
        }
    }
}

