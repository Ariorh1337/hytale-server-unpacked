/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.blackboard.view.event.block;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.event.events.ecs.DamageBlockEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerInteractEvent;
import com.hypixel.hytale.server.core.modules.blockset.BlockSetModule;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSettings;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.blackboard.Blackboard;
import com.hypixel.hytale.server.npc.blackboard.view.event.EventNotification;
import com.hypixel.hytale.server.npc.blackboard.view.event.EventTypeRegistration;
import com.hypixel.hytale.server.npc.blackboard.view.event.EventView;
import com.hypixel.hytale.server.npc.blackboard.view.event.block.BlockEventType;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import it.unimi.dsi.fastutil.ints.IntSet;
import javax.annotation.Nonnull;

public class BlockEventView
extends EventView<BlockEventView, BlockEventType, EventNotification> {
    public BlockEventView(@Nonnull World world) {
        super(BlockEventType.class, (Enum[])BlockEventType.VALUES, new EventNotification(), world);
        this.eventRegistry.register(PlayerInteractEvent.class, world.getName(), this::onPlayerInteraction);
        for (BlockEventType eventType : BlockEventType.VALUES) {
            this.entityMapsByEventType.put(eventType, new EventTypeRegistration<BlockEventType, EventNotification>(eventType, (set, blockId) -> BlockSetModule.getInstance().blockInSet(set, blockId), NPCEntity::notifyBlockChange));
        }
    }

    @Override
    public BlockEventView getUpdatedView(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        World entityWorld = componentAccessor.getExternalData().getWorld();
        if (!entityWorld.equals(this.world)) {
            Blackboard blackboardResource = componentAccessor.getResource(Blackboard.getResourceType());
            return blackboardResource.getView(BlockEventView.class, ref, componentAccessor);
        }
        return this;
    }

    @Override
    public void initialiseEntity(@Nonnull Ref<EntityStore> ref, @Nonnull NPCEntity npcComponent) {
        for (int i = 0; i < BlockEventType.VALUES.length; ++i) {
            BlockEventType type = BlockEventType.VALUES[i];
            IntSet eventSets = npcComponent.getBlackboardBlockChangeSet(type);
            if (eventSets == null) continue;
            ((EventTypeRegistration)this.entityMapsByEventType.get(type)).initialiseEntity(npcComponent.getReference(), eventSets);
        }
    }

    @Override
    protected void onEvent(int senderTypeId, double x, double y, double z, Ref<EntityStore> initiator, Ref<EntityStore> skip, @Nonnull ComponentAccessor<EntityStore> componentAccessor, BlockEventType type) {
        super.onEvent(senderTypeId, x + 0.5, y + 0.5, z + 0.5, initiator, skip, componentAccessor, type);
    }

    public void onEntityDamageBlock(@Nonnull Ref<EntityStore> ref, @Nonnull DamageBlockEvent event) {
        if (event.isCancelled()) {
            return;
        }
        this.processDamagedBlock(ref, event.getBlockType().getId(), event.getTargetBlock(), BlockEventType.DAMAGE);
    }

    public void onEntityBreakBlock(@Nonnull Ref<EntityStore> ref, @Nonnull BreakBlockEvent event) {
        if (event.isCancelled()) {
            return;
        }
        this.processDamagedBlock(ref, event.getBlockType().getId(), event.getTargetBlock(), BlockEventType.DESTRUCTION);
    }

    private void processDamagedBlock(@Nonnull Ref<EntityStore> initiatorRef, String block, @Nonnull Vector3i position, @Nonnull BlockEventType type) {
        PlayerSettings playerSettingsComponent;
        Store<EntityStore> store = initiatorRef.getStore();
        Player playerComponent = store.getComponent(initiatorRef, Player.getComponentType());
        if (!(playerComponent == null || playerComponent.getGameMode() != GameMode.Creative || (playerSettingsComponent = store.getComponent(initiatorRef, PlayerSettings.getComponentType())) != null && playerSettingsComponent.creativeSettings().allowNPCDetection())) {
            return;
        }
        int blockId = BlockType.getAssetMap().getIndex(block);
        if (blockId == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Unknown key! " + block);
        }
        this.onEvent(blockId, (double)position.x, (double)position.y, (double)position.z, initiatorRef, (Ref<EntityStore>)null, (ComponentAccessor<EntityStore>)store, type);
    }

    private void onPlayerInteraction(@Nonnull PlayerInteractEvent event) {
        PlayerSettings playerSettingsComponent;
        if (event.isCancelled()) {
            return;
        }
        Player playerComponent = event.getPlayer();
        Ref<EntityStore> ref = event.getPlayerRef();
        Store<EntityStore> store = ref.getStore();
        if (!(playerComponent.getGameMode() != GameMode.Creative || (playerSettingsComponent = store.getComponent(ref, PlayerSettings.getComponentType())) != null && playerSettingsComponent.creativeSettings().allowNPCDetection())) {
            return;
        }
        Vector3i blockPosition = event.getTargetBlock();
        if (blockPosition == null || event.getActionType() != InteractionType.Use) {
            return;
        }
        World world = store.getExternalData().getWorld();
        int blockId = world.getBlock(blockPosition.x, blockPosition.y, blockPosition.z);
        Vector3i targetBlock = event.getTargetBlock();
        this.onEvent(blockId, (double)targetBlock.x, (double)targetBlock.y, (double)targetBlock.z, ref, (Ref<EntityStore>)null, (ComponentAccessor<EntityStore>)store, BlockEventType.INTERACTION);
    }
}

