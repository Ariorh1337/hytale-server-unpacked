package com.hypixel.hytale.server.npc.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.support.PositionCache;
import com.hypixel.hytale.server.npc.role.support.RoleStats;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class NPCSensorStatsCommand extends AbstractPlayerCommand {
   public NPCSensorStatsCommand() {
      super("sensorstats", "server.commands.npc.sensorstats.desc");
   }

   @Override
   protected void execute(
      @Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world
   ) {
      NPCPlugin npcPlugin = NPCPlugin.get();
      List<String> roles = npcPlugin.getRoleTemplateNames(true);
      if (roles.isEmpty()) {
         context.sendMessage(Message.translation("server.commands.npc.sensorstats.noroles"));
      } else {
         roles.sort(String::compareToIgnoreCase);
         TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
         assert transformComponent != null;
         Vector3d pos = new Vector3d(transformComponent.getPosition());
         StringBuilder out = new StringBuilder();

         for (int i = 0; i < roles.size(); i++) {
            String roleName = roles.get(i);
            int idx = NPCPlugin.get().getIndex(roleName);
            if (idx >= 0) {
               Pair<Ref<EntityStore>, NPCEntity> pair = npcPlugin.spawnEntity(
                  store, idx, pos, null, null, (npcEntity, var1x, var2x) -> npcEntity.setCollectSensorStats(true), null
               );
               if (pair == null) {
                  context.sendMessage(Message.translation("server.commands.npc.spawn.templateNotFound").param("template", roleName));
               } else {
                  Ref<EntityStore> npcRef = pair.first();
                  NPCEntity npc = pair.second();

                  try {
                     RoleStats roleStats = PositionCache.get(npcRef, store).getRoleStats();
                     if (roleStats != null) {
                        if (!isRangesEmpty(roleStats, true)) {
                           out.append('\n').append("PLY ");
                           formatRanges(out, roleStats, "S=", true, RoleStats.RangeType.SORTED, 25);
                           formatRanges(out, roleStats, "U=", true, RoleStats.RangeType.UNSORTED, 9);
                           formatRanges(out, roleStats, "A=", true, RoleStats.RangeType.AVOIDANCE, 9);
                           formatBuckets(out, roleStats, "B=", true, 20);
                           out.append(roleName);
                        }

                        if (!isRangesEmpty(roleStats, false)) {
                           out.append('\n').append("ENT ");
                           formatRanges(out, roleStats, "S=", false, RoleStats.RangeType.SORTED, 25);
                           formatRanges(out, roleStats, "U=", false, RoleStats.RangeType.UNSORTED, 9);
                           formatRanges(out, roleStats, "A=", false, RoleStats.RangeType.AVOIDANCE, 9);
                           formatBuckets(out, roleStats, "B=", false, 20);
                           out.append(roleName);
                        }
                     }
                  } catch (Throwable t) {
                     npcPlugin.getLogger().at(Level.WARNING).log("Error reading sensor stats for role " + roleName + ": " + t.getMessage());
                  } finally {
                     npc.remove();
                  }
               }
            }
         }

         npcPlugin.getLogger().at(Level.INFO).log(out.toString());
      }
   }

   private static boolean isRangesEmpty(@Nonnull RoleStats roleStats, boolean isPlayer) {
      return roleStats.getRanges(isPlayer, RoleStats.RangeType.SORTED) == null
         && roleStats.getRanges(isPlayer, RoleStats.RangeType.UNSORTED) == null
         && roleStats.getRanges(isPlayer, RoleStats.RangeType.AVOIDANCE) == null;
   }

   private static void formatBuckets(@Nonnull StringBuilder builder, @Nonnull RoleStats roleStats, @Nonnull String label, boolean isPlayer, int width) {
      builder.append(label);
      int length = builder.length();
      IntArrayList buckets = roleStats.getBuckets(isPlayer);

      for (int i = 0; i < buckets.size(); i++) {
         builder.append(buckets.getInt(i)).append(" ");
      }

      length = width + length - builder.length();
      if (length > 0) {
         builder.append(" ".repeat(length));
      }
   }

   private static void formatRanges(
      @Nonnull StringBuilder builder, @Nonnull RoleStats roleStats, @Nonnull String label, boolean isPlayer, @Nonnull RoleStats.RangeType rangeType, int width
   ) {
      builder.append(label);
      int length = builder.length();
      int[] ranges = roleStats.getRangesSorted(isPlayer, rangeType);
      if (ranges != null && ranges.length != 0) {
         for (int range : ranges) {
            builder.append(range).append(" ");
         }
      } else {
         builder.append("- ");
      }

      length = width + length - builder.length();
      if (length > 0) {
         builder.append(" ".repeat(length));
      }
   }
}
