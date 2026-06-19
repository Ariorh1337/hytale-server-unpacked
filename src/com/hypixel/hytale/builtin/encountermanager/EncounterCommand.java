package com.hypixel.hytale.builtin.encountermanager;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.ParseResult;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.SingleArgumentType;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.command.system.exceptions.GeneralCommandException;
import com.hypixel.hytale.server.core.command.system.suggestion.SuggestionResult;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.nameplate.Nameplate;
import com.hypixel.hytale.server.core.modules.entity.component.HiddenFromAdventurePlayers;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.asset.builder.BuilderInfo;
import java.awt.Color;
import javax.annotation.Nonnull;

public class EncounterCommand extends AbstractCommandCollection {
   @Nonnull
   public static final SingleArgumentType<BuilderInfo> ENCOUNTER_ASSET = new SingleArgumentType<BuilderInfo>(
      "server.commands.encounter.argtype.name", "server.commands.encounter.argtype.usage"
   ) {
      public BuilderInfo parse(@Nonnull String input, @Nonnull ParseResult parseResult) {
         NPCPlugin npcPlugin = NPCPlugin.get();
         int index = npcPlugin.getIndex(input);
         if (index != Integer.MIN_VALUE) {
            BuilderInfo builderInfo = npcPlugin.getBuilderManager().tryGetBuilderInfo(index);
            if (builderInfo != null && builderInfo.getBuilder().category() == EncounterManager.class) {
               return builderInfo;
            }
         }

         parseResult.fail(Message.translation("server.commands.notfound").param("type", "Encounter").param("id", input).color(Color.RED));
         return null;
      }

      @Override
      public void suggest(@Nonnull CommandSender sender, @Nonnull String textAlreadyEntered, int numParametersTyped, @Nonnull SuggestionResult result) {
         String lower = textAlreadyEntered.toLowerCase();

         for (BuilderInfo info : NPCPlugin.get().getBuilderManager().getAllBuilders().values()) {
            if (info.getBuilder().category() == EncounterManager.class) {
               String name = info.getKeyName();
               if (name.toLowerCase().startsWith(lower)) {
                  result.suggest(name);
               }
            }
         }
      }
   };

   public EncounterCommand() {
      super("encounter", "server.commands.encounter.desc");
      this.setPermissionGroups("hytale:WorldEditor");
      this.addSubCommand(new EncounterCommand.Add());
   }

   private static class Add extends AbstractPlayerCommand {
      @Nonnull
      private final RequiredArg<BuilderInfo> encounterArg = this.withRequiredArg(
         "encounter", "server.commands.encounter.add.arg.encounter.desc", EncounterCommand.ENCOUNTER_ASSET
      );

      public Add() {
         super("add", "server.commands.encounter.add.desc");
      }

      @Override
      protected void execute(
         @Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world
      ) {
         TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
         if (transformComponent == null) {
            throw new GeneralCommandException(Message.translation("server.commands.errors.playerOnly"));
         }

         BuilderInfo info = this.encounterArg.get(context);
         String encounterId = info.getKeyName();
         EncounterManager encounter = new EncounterManager(encounterId, info.getIndex());
         Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
         holder.addComponent(EncounterManager.getComponentType(), encounter);
         holder.addComponent(TransformComponent.getComponentType(), transformComponent.clone());
         holder.addComponent(Nameplate.getComponentType(), new Nameplate(encounterId));
         holder.ensureComponent(UUIDComponent.getComponentType());
         holder.ensureComponent(HiddenFromAdventurePlayers.getComponentType());
         Model model = EncounterManagerPlugin.get().getMarkerModel();
         holder.addComponent(ModelComponent.getComponentType(), new ModelComponent(model));
         holder.addComponent(PersistentModel.getComponentType(), new PersistentModel(model.toReference()));
         Ref<EntityStore> spawnedRef = store.addEntity(holder, AddReason.SPAWN);
         if (spawnedRef != null && spawnedRef.isValid()) {
            context.sendMessage(Message.translation("server.commands.encounter.add.added").param("encounterId", encounterId));
         } else {
            context.sendMessage(Message.translation("server.commands.encounter.add.failed").param("encounterId", encounterId));
         }
      }
   }
}
