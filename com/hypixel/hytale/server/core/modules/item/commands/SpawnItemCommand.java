/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.item.commands;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandUtil;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.ItemUtils;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.permissions.HytalePermissions;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nonnull;

public class SpawnItemCommand
extends AbstractPlayerCommand {
    @Nonnull
    private static final Message MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD = Message.translation("server.commands.errors.playerNotInWorld");
    @Nonnull
    private final RequiredArg<Item> itemArg = this.withRequiredArg("item", "server.commands.spawnitem.item.desc", ArgTypes.ITEM_ASSET);
    @Nonnull
    private final DefaultArg<Integer> quantityArg = this.withDefaultArg("qty", "server.commands.spawnitem.quantity.desc", ArgTypes.INTEGER, Integer.valueOf(1), "1");
    @Nonnull
    private final OptionalArg<Integer> countArg = (OptionalArg)this.withOptionalArg("count", "server.commands.spawnitem.count.desc", ArgTypes.INTEGER).addAliases("n");
    @Nonnull
    private final DefaultArg<Float> forceArg = (DefaultArg)this.withDefaultArg("force", "server.commands.spawnitem.force.desc", ArgTypes.FLOAT, Float.valueOf(1.0f), "1.0").addAliases("x");

    public SpawnItemCommand() {
        super("spawnitem", "server.commands.spawnitem.desc");
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        CommandUtil.requirePermission(context.sender(), HytalePermissions.fromCommand("spawnitem"));
        Item item = (Item)this.itemArg.get(context);
        String itemId = item.getId();
        int quantity = (Integer)this.quantityArg.get(context);
        float force = ((Float)this.forceArg.get(context)).floatValue();
        TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        ModelComponent modelComponent = store.getComponent(ref, ModelComponent.getComponentType());
        if (transformComponent == null || modelComponent == null) {
            context.sendMessage(MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD);
            return;
        }
        Vector3d playerPosition = transformComponent.getPosition();
        Model playerModel = modelComponent.getModel();
        float throwSpeed = 6.0f * force;
        if (this.countArg.provided(context)) {
            int count = (Integer)this.countArg.get(context);
            Vector3d throwPosition = playerPosition.clone();
            throwPosition.add(0.0, playerModel.getEyeHeight(ref, store), 0.0);
            ThreadLocalRandom random = ThreadLocalRandom.current();
            for (int i = 0; i < count; ++i) {
                Holder<EntityStore> itemEntityHolder = ItemComponent.generateItemDrop(store, new ItemStack(itemId, quantity), throwPosition, Vector3f.ZERO, (float)random.nextGaussian() * throwSpeed, 0.5f, (float)random.nextGaussian() * throwSpeed);
                ItemComponent itemEntityComponent = itemEntityHolder.getComponent(ItemComponent.getComponentType());
                if (itemEntityComponent != null) {
                    itemEntityComponent.setPickupDelay(1.5f);
                }
                store.addEntity(itemEntityHolder, AddReason.SPAWN);
            }
            int totalQuantity = count * quantity;
            context.sendMessage(Message.translation("server.commands.spawnitem.spawnedMultiple").param("count", count).param("quantity", quantity).param("total", totalQuantity).param("item", itemId));
        } else {
            ItemUtils.throwItem(ref, new ItemStack(itemId, quantity), throwSpeed, store);
            context.sendMessage(Message.translation("server.commands.spawnitem.spawned").param("quantity", quantity).param("item", itemId));
        }
    }
}

