/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.role;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.entity.nameplate.Nameplate;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.modules.entity.component.ActiveAnimationComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.flock.FlockMembership;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.role.RoleDebugFlags;
import com.hypixel.hytale.server.npc.role.support.MarkedEntitySupport;
import com.hypixel.hytale.server.npc.util.InventoryHelper;
import com.hypixel.hytale.server.spawning.util.LightRangePredicate;
import java.time.temporal.ChronoField;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RoleDebugDisplay {
    protected boolean debugDisplayState;
    protected boolean debugDisplayTime;
    protected boolean debugDisplayFlock;
    protected boolean debugDisplayAnim;
    protected boolean debugDisplayLockedTarget;
    protected boolean debugDisplayLightLevel;
    protected boolean debugDisplayFreeSlots;
    protected boolean debugDisplayCustom;
    protected boolean debugDisplayPathFinder;
    protected boolean debugDisplayHP;
    protected boolean debugDisplayStamina;
    protected boolean debugDisplaySpeed;
    protected boolean debugDisplayInternalId;
    protected boolean debugDisplayName;
    @Nonnull
    protected StringBuilder debugDisplay = new StringBuilder(20);

    private RoleDebugDisplay() {
    }

    public void display(@Nonnull Role role, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        EntityStatMap entityStatsComponent;
        FlockMembership flockMembershipComponent;
        NPCEntity npcComponent = archetypeChunk.getComponent(index, NPCEntity.getComponentType());
        assert (npcComponent != null);
        if (this.debugDisplayInternalId) {
            this.debugDisplay.append("ID-").append(archetypeChunk.getReferenceTo(index).getIndex()).append(" ");
        }
        if (this.debugDisplayName) {
            this.debugDisplay.append(" Role(").append(role.getRoleName()).append(")");
        }
        if (this.debugDisplayState) {
            role.getStateSupport().appendStateName(this.debugDisplay);
        }
        if (this.debugDisplayFlock && (flockMembershipComponent = archetypeChunk.getComponent(index, FlockMembership.getComponentType())) != null) {
            if (flockMembershipComponent.getMembershipType().isActingAsLeader()) {
                this.debugDisplay.append(" LDR");
            } else {
                this.debugDisplay.append(" FLK");
            }
        }
        WorldTimeResource worldTimeResource = commandBuffer.getResource(WorldTimeResource.getResourceType());
        if (this.debugDisplayTime) {
            double dayProgress = (double)(24 * worldTimeResource.getGameDateTime().get(ChronoField.SECOND_OF_DAY)) / (double)WorldTimeResource.SECONDS_PER_DAY;
            this.debugDisplay.append(' ').append((double)((int)(100.0 * dayProgress)) / 100.0);
        }
        if (this.debugDisplayAnim) {
            ActiveAnimationComponent activeAnimationComponent = archetypeChunk.getComponent(index, ActiveAnimationComponent.getComponentType());
            assert (activeAnimationComponent != null);
            String[] activeAnimations = activeAnimationComponent.getActiveAnimations();
            MovementStates movementStates = archetypeChunk.getComponent(index, MovementStatesComponent.getComponentType()).getMovementStates();
            this.debugDisplay.append(" M:");
            this.debugDisplay.append(movementStates.idle ? (char)'I' : '-');
            this.debugDisplay.append(movementStates.horizontalIdle ? (char)'H' : '-');
            this.debugDisplay.append(movementStates.running ? (char)'R' : '-');
            this.debugDisplay.append(movementStates.climbing ? (char)'C' : '-');
            this.debugDisplay.append(movementStates.jumping ? (char)'J' : '-');
            this.debugDisplay.append(movementStates.falling ? (char)'F' : '-');
            this.debugDisplay.append(movementStates.crouching ? (char)'c' : '-');
            this.debugDisplay.append(movementStates.flying ? (char)'f' : '-');
            this.debugDisplay.append(movementStates.swimming ? (char)'s' : '-');
            this.debugDisplay.append(movementStates.swimJumping ? (char)'S' : '-');
            this.debugDisplay.append(movementStates.onGround ? (char)'o' : '-');
            this.debugDisplay.append(movementStates.inFluid ? (char)'w' : '-');
            String animationId = activeAnimations[AnimationSlot.Status.ordinal()];
            this.debugDisplay.append(" S:").append(animationId != null ? animationId : "-");
            animationId = activeAnimations[AnimationSlot.Action.ordinal()];
            this.debugDisplay.append(" A:").append(animationId != null ? animationId : "-");
            animationId = activeAnimations[AnimationSlot.Face.ordinal()];
            this.debugDisplay.append(" F:").append(animationId != null ? animationId : "-");
        }
        if (this.debugDisplayLockedTarget) {
            MarkedEntitySupport markedEntitySupport = role.getMarkedEntitySupport();
            int targetSlotCount = markedEntitySupport.getMarkedEntitySlotCount();
            for (int i = 0; i < targetSlotCount; ++i) {
                String slotName = markedEntitySupport.getSlotName(i);
                Ref<EntityStore> targetRef = markedEntitySupport.getMarkedEntityRef(i);
                if (targetRef == null) {
                    this.debugDisplay.append(" T(").append(slotName).append("):-");
                    continue;
                }
                PlayerRef targetPlayerRefComponent = commandBuffer.getComponent(targetRef, PlayerRef.getComponentType());
                NPCEntity targetNpcComponent = commandBuffer.getComponent(targetRef, NPCEntity.getComponentType());
                if (targetPlayerRefComponent != null) {
                    this.debugDisplay.append(" TP(").append(slotName).append("):").append(targetPlayerRefComponent.getUsername());
                    continue;
                }
                if (targetNpcComponent != null) {
                    String roleName = targetNpcComponent.getRoleName();
                    if (roleName == null || roleName.isEmpty()) {
                        roleName = "???";
                    }
                    this.debugDisplay.append(" T(").append(slotName).append("):").append(roleName);
                    continue;
                }
                this.debugDisplay.append(" T(").append(slotName).append("):?");
            }
        }
        if (this.debugDisplayLightLevel) {
            TransformComponent transformComponent = archetypeChunk.getComponent(index, TransformComponent.getComponentType());
            assert (transformComponent != null);
            Ref<ChunkStore> chunkRef = transformComponent.getChunkRef();
            if (chunkRef != null && chunkRef.isValid()) {
                World world = commandBuffer.getExternalData().getWorld();
                Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
                BlockChunk blockChunkComponent = chunkStore.getComponent(chunkRef, BlockChunk.getComponentType());
                assert (blockChunkComponent != null);
                Vector3d position = transformComponent.getPosition();
                int x = MathUtil.floor(position.getX());
                int y = MathUtil.floor(position.getY());
                int z = MathUtil.floor(position.getZ());
                double sunlightFactor = worldTimeResource.getSunlightFactor();
                this.debugDisplay.append(" LL:").append(LightRangePredicate.lightToPrecentage(LightRangePredicate.calculateLightValue(blockChunkComponent, x, y, z, sunlightFactor))).append('/').append(LightRangePredicate.lightToPrecentage(blockChunkComponent.getSkyLight(x, y, z))).append('/').append(LightRangePredicate.lightToPrecentage((byte)((double)blockChunkComponent.getSkyLight(x, y, z) * sunlightFactor))).append('/').append(LightRangePredicate.lightToPrecentage(blockChunkComponent.getRedBlockLight(x, y, z))).append('/').append(LightRangePredicate.lightToPrecentage(blockChunkComponent.getGreenBlockLight(x, y, z))).append('/').append(LightRangePredicate.lightToPrecentage(blockChunkComponent.getBlueBlockLight(x, y, z)));
            }
        }
        String displayPathfinderString = role.getDebugSupport().pollDisplayPathfinderString();
        if (this.debugDisplayPathFinder && displayPathfinderString != null && !displayPathfinderString.isEmpty()) {
            this.debugDisplay.append(!this.debugDisplay.isEmpty() ? " PF:" : "PF:").append(displayPathfinderString);
        }
        String customString = role.getDebugSupport().pollDisplayCustomString();
        if (this.debugDisplayCustom && customString != null && !customString.isEmpty()) {
            if (!this.debugDisplay.isEmpty()) {
                this.debugDisplay.append(' ');
            }
            this.debugDisplay.append(customString);
        }
        if (this.debugDisplayFreeSlots) {
            Inventory inventory = npcComponent.getInventory();
            int hotbarFreeSlots = InventoryHelper.countFreeSlots(inventory.getHotbar());
            int inventoryFreeSlots = InventoryHelper.countFreeSlots(inventory.getStorage());
            this.debugDisplay.append(" FS:").append(hotbarFreeSlots).append('/').append(inventoryFreeSlots);
        }
        if (this.debugDisplayHP) {
            entityStatsComponent = archetypeChunk.getComponent(index, EntityStatMap.getComponentType());
            assert (entityStatsComponent != null);
            EntityStatValue healthValue = entityStatsComponent.get(DefaultEntityStatTypes.getHealth());
            if (healthValue == null) {
                this.debugDisplay.append(" HP: N/A");
            } else {
                this.debugDisplay.append(" HP:").append(healthValue.get()).append('/').append(healthValue.getMax());
            }
        }
        if (this.debugDisplayStamina) {
            entityStatsComponent = archetypeChunk.getComponent(index, EntityStatMap.getComponentType());
            assert (entityStatsComponent != null);
            EntityStatValue staminaValue = entityStatsComponent.get(DefaultEntityStatTypes.getStamina());
            if (staminaValue == null) {
                this.debugDisplay.append(" Stamina: N/A");
            } else {
                this.debugDisplay.append(" Stamina:").append(staminaValue.get()).append('/').append(staminaValue.getMax());
            }
        }
        if (this.debugDisplaySpeed) {
            Velocity velocityComponent = archetypeChunk.getComponent(index, Velocity.getComponentType());
            assert (velocityComponent != null);
            this.debugDisplay.append(" SPD:").append(MathUtil.round(velocityComponent.getSpeed(), 1));
        }
        if (!this.debugDisplay.isEmpty()) {
            Nameplate nameplateComponent = archetypeChunk.getComponent(index, Nameplate.getComponentType());
            if (nameplateComponent != null) {
                nameplateComponent.setText(this.debugDisplay.toString());
            } else {
                Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
                commandBuffer.addComponent(ref, Nameplate.getComponentType(), new Nameplate(this.debugDisplay.toString()));
            }
            this.debugDisplay.setLength(0);
        }
    }

    @Nullable
    public static RoleDebugDisplay create(@Nonnull EnumSet<RoleDebugFlags> debugFlags) {
        boolean debugDisplayState = debugFlags.contains(RoleDebugFlags.DisplayState);
        boolean debugDisplayTime = debugFlags.contains(RoleDebugFlags.DisplayTime);
        boolean debugDisplayFlock = debugFlags.contains(RoleDebugFlags.DisplayFlock);
        boolean debugDisplayAnim = debugFlags.contains(RoleDebugFlags.DisplayAnim);
        boolean debugDisplayLockedTarget = debugFlags.contains(RoleDebugFlags.DisplayTarget);
        boolean debugDisplayLightLevel = debugFlags.contains(RoleDebugFlags.DisplayLightLevel);
        boolean debugDisplayCustom = debugFlags.contains(RoleDebugFlags.DisplayCustom);
        boolean debugDisplayFreeSlots = debugFlags.contains(RoleDebugFlags.DisplayFreeSlots);
        boolean debugDisplayPathFinder = debugFlags.contains(RoleDebugFlags.Pathfinder);
        boolean debugDisplayHP = debugFlags.contains(RoleDebugFlags.DisplayHP);
        boolean debugDisplayStamina = debugFlags.contains(RoleDebugFlags.DisplayStamina);
        boolean debugDisplaySpeed = debugFlags.contains(RoleDebugFlags.DisplaySpeed);
        boolean debugDisplayName = debugFlags.contains(RoleDebugFlags.DisplayName);
        boolean debugDisplayInternalId = debugFlags.contains(RoleDebugFlags.DisplayInternalId);
        if (!(debugDisplayInternalId || debugDisplayState || debugDisplayFlock || debugDisplayTime || debugDisplayAnim || debugDisplayLockedTarget || debugDisplayLightLevel || debugDisplayCustom || debugDisplayFreeSlots || debugDisplayPathFinder || debugDisplayHP || debugDisplaySpeed || debugDisplayName || debugDisplayStamina)) {
            return null;
        }
        RoleDebugDisplay debugDisplay = new RoleDebugDisplay();
        debugDisplay.debugDisplayState = debugDisplayState;
        debugDisplay.debugDisplayTime = debugDisplayTime;
        debugDisplay.debugDisplayFlock = debugDisplayFlock;
        debugDisplay.debugDisplayAnim = debugDisplayAnim;
        debugDisplay.debugDisplayLockedTarget = debugDisplayLockedTarget;
        debugDisplay.debugDisplayLightLevel = debugDisplayLightLevel;
        debugDisplay.debugDisplayCustom = debugDisplayCustom;
        debugDisplay.debugDisplayFreeSlots = debugDisplayFreeSlots;
        debugDisplay.debugDisplayPathFinder = debugDisplayPathFinder;
        debugDisplay.debugDisplayHP = debugDisplayHP;
        debugDisplay.debugDisplayStamina = debugDisplayStamina;
        debugDisplay.debugDisplaySpeed = debugDisplaySpeed;
        debugDisplay.debugDisplayInternalId = debugDisplayInternalId;
        debugDisplay.debugDisplayName = debugDisplayName;
        return debugDisplay;
    }
}

