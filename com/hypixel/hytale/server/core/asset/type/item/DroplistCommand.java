/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.item;

import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemDropList;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.item.ItemModule;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

public class DroplistCommand
extends CommandBase {
    @Nonnull
    private final RequiredArg<String> itemDroplistArg = this.withRequiredArg("droplist", "server.commands.droplist.set.droplist.desc", ArgTypes.STRING);
    private final OptionalArg<Integer> countArg = (OptionalArg)this.withOptionalArg("count", "server.commands.droplist.set.count.desc", ArgTypes.INTEGER).addValidator(Validators.greaterThan(0));

    public DroplistCommand() {
        super("droplist", "server.commands.droplist.desc");
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        String droplistId = (String)this.itemDroplistArg.get(context);
        ItemDropList itemDropList = ItemDropList.getAssetMap().getAsset(droplistId);
        if (itemDropList == null) {
            context.sendMessage(Message.translation("server.commands.droplist.notFound").param("droplistId", droplistId));
            return;
        }
        int count = this.countArg.provided(context) ? (Integer)this.countArg.get(context) : 1;
        LinkedHashMap<String, Integer> accumulatedDrops = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < count; ++i) {
            List<ItemStack> randomItemsToDrop = ItemModule.get().getRandomItemDrops(droplistId);
            for (ItemStack itemStack : randomItemsToDrop) {
                accumulatedDrops.merge(itemStack.getItemId(), itemStack.getQuantity(), Integer::sum);
            }
        }
        if (accumulatedDrops.isEmpty()) {
            context.sendMessage(Message.translation("server.commands.droplist.empty").param("droplistId", droplistId));
            return;
        }
        context.sendMessage(Message.translation("server.commands.droplist.result").param("droplistId", droplistId));
        for (Map.Entry entry : accumulatedDrops.entrySet()) {
            Message message = Message.translation("server.commands.droplist.result.item").param("itemName", (String)entry.getKey()).param("itemQuantity", (Integer)entry.getValue());
            context.sendMessage(message);
        }
    }
}

