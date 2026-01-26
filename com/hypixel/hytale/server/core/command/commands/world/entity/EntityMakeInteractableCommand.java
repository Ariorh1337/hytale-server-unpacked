/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.commands.world.entity;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractTargetEntityCommand;
import com.hypixel.hytale.server.core.modules.entity.component.Interactable;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectList;
import javax.annotation.Nonnull;

public class EntityMakeInteractableCommand
extends AbstractTargetEntityCommand {
    @Nonnull
    private final FlagArg disableFlag = this.withFlagArg("disable", "server.commands.entity.interactable.disable.desc");

    public EntityMakeInteractableCommand() {
        super("interactable", "server.commands.entity.interactable.desc");
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull ObjectList<Ref<EntityStore>> entities, @Nonnull World world, @Nonnull Store<EntityStore> store) {
        boolean disable = this.disableFlag.provided(context);
        for (Ref ref : entities) {
            if (disable) {
                store.tryRemoveComponent(ref, Interactable.getComponentType());
                continue;
            }
            store.ensureComponent(ref, Interactable.getComponentType());
        }
        context.sendMessage(Message.translation("server.commands.entity.interactable.success." + (disable ? "unset" : "set")).param("amount", entities.size()));
    }
}

