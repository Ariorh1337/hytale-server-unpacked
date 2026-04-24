package com.hypixel.hytale.builtin.triggervolumes.command;

import com.hypixel.hytale.builtin.triggervolumes.EntityTargetType;
import com.hypixel.hytale.builtin.triggervolumes.TriggerVolumesPlugin;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEventType;
import com.hypixel.hytale.builtin.triggervolumes.effect.builtin.ConditionalEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.builtin.DestroyVolumeEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.builtin.PlaySoundEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.builtin.PlayVfxEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.builtin.SendMessageEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.builtin.SetVelocityEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.builtin.ShowEventTitleEffect;
import com.hypixel.hytale.builtin.triggervolumes.manager.CooldownMode;
import com.hypixel.hytale.builtin.triggervolumes.manager.TriggerVolumeManager;
import com.hypixel.hytale.builtin.triggervolumes.manager.VolumeEntry;
import com.hypixel.hytale.builtin.triggervolumes.shape.BoxShape;
import com.hypixel.hytale.builtin.triggervolumes.shape.CylinderShape;
import com.hypixel.hytale.builtin.triggervolumes.shape.SphereShape;
import com.hypixel.hytale.builtin.triggervolumes.shape.TriggerVolumeShape;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractWorldCommand;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;

public class TriggerVolumeTestCommand extends AbstractWorldCommand {
   private static final String TEST_PREFIX = "tvtest_";
   private static final double SPACING = 8.0;
   private final FlagArg cleanupFlag = this.withFlagArg("cleanup", "server.commands.triggervolume.test.cleanup.desc");

   public TriggerVolumeTestCommand() {
      super("test", "server.commands.triggervolume.test.desc");
   }

   @Override
   protected void execute(@Nonnull CommandContext context, @Nonnull World world, @Nonnull Store<EntityStore> store) {
      TriggerVolumesPlugin plugin = TriggerVolumesPlugin.get();
      TriggerVolumeManager manager = store.getResource(plugin.getManagerResourceType());
      if (manager != null) {
         String worldName = world.getName().toLowerCase(Locale.ROOT);
         if (!this.cleanupFlag.get(context)) {
            Ref<EntityStore> playerRef = context.senderAsPlayerRef();
            if (playerRef != null && playerRef.isValid()) {
               TransformComponent transform = store.getComponent(playerRef, TransformComponent.getComponentType());
               if (transform != null) {
                  Vector3d position = transform.getPosition();
                  float yaw = transform.getRotation().yaw();
                  double forwardX = -Math.sin(yaw);
                  double forwardZ = -Math.cos(yaw);
                  EnumSet<EntityTargetType> targets = EnumSet.of(EntityTargetType.PLAYER);
                  BoxShape box = new BoxShape(new Vector3d(-2.0, 0.0, -2.0), new Vector3d(2.0, 3.0, 2.0));
                  int created = 0;
                  int slot = 0;
                  created += this.registerVolume(
                     manager,
                     worldName,
                     "tvtest_enter_message",
                     slotPosition(position, forwardX, forwardZ, ++slot),
                     box,
                     List.of(SendMessageEffect.create(TriggerEventType.ENTER, "server.commands.triggervolume.test.msg.enter")),
                     targets
                  );
                  created += this.registerVolume(
                     manager,
                     worldName,
                     "tvtest_exit_message",
                     slotPosition(position, forwardX, forwardZ, ++slot),
                     box,
                     List.of(SendMessageEffect.create(TriggerEventType.EXIT, "server.commands.triggervolume.test.msg.exit")),
                     targets
                  );
                  created += this.registerVolume(
                     manager,
                     worldName,
                     "tvtest_launch_pad",
                     slotPosition(position, forwardX, forwardZ, ++slot),
                     new SphereShape(new Vector3d(0.0, 1.5, 0.0), 3.0),
                     List.of(SetVelocityEffect.create(TriggerEventType.ENTER, new Vector3d(0.0, 20.0, 0.0), false)),
                     targets
                  );
                  created += this.registerVolume(
                     manager,
                     worldName,
                     "tvtest_play_sound",
                     slotPosition(position, forwardX, forwardZ, ++slot),
                     new CylinderShape(new Vector3d(0.0, 0.0, 0.0), 3.0, 4.0),
                     List.of(PlaySoundEffect.create(TriggerEventType.ENTER, "SFX_Player_Pickup_Item")),
                     targets
                  );
                  SendMessageEffect tickMsg = SendMessageEffect.create(TriggerEventType.TICK, "server.commands.triggervolume.test.msg.tick");
                  tickMsg.setInterval(2.0F);
                  created += this.registerVolume(
                     manager,
                     worldName,
                     "tvtest_tick_message_2s",
                     slotPosition(position, forwardX, forwardZ, ++slot),
                     new SphereShape(new Vector3d(0.0, 1.5, 0.0), 3.0),
                     List.of(tickMsg),
                     targets
                  );
                  SendMessageEffect innerMsg = SendMessageEffect.create(TriggerEventType.ENTER, "server.commands.triggervolume.test.msg.conditional");
                  ConditionalEffect conditional = ConditionalEffect.create(TriggerEventType.ENTER, null, 3.0F, innerMsg);
                  created += this.registerVolume(
                     manager,
                     worldName,
                     "tvtest_conditional_3s_cooldown",
                     slotPosition(position, forwardX, forwardZ, ++slot),
                     box,
                     List.of(conditional),
                     targets
                  );
                  VolumeEntry delayedVol = this.registerVolumeEntry(
                     manager,
                     worldName,
                     "tvtest_delayed_2s_message",
                     slotPosition(position, forwardX, forwardZ, ++slot),
                     box,
                     List.of(SendMessageEffect.create(TriggerEventType.ENTER, "server.commands.triggervolume.test.msg.delayed")),
                     targets
                  );
                  if (delayedVol != null) {
                     delayedVol.setActivationDelay(2.0F);
                     created++;
                  }

                  SendMessageEffect seq1 = SendMessageEffect.create(TriggerEventType.ENTER, "server.commands.triggervolume.test.msg.seq1");
                  SendMessageEffect seq2 = SendMessageEffect.create(TriggerEventType.ENTER, "server.commands.triggervolume.test.msg.seq2");
                  seq2.setDelay(1.0F);
                  SendMessageEffect seq3 = SendMessageEffect.create(TriggerEventType.ENTER, "server.commands.triggervolume.test.msg.seq3");
                  seq3.setDelay(2.0F);
                  created += this.registerVolume(
                     manager,
                     worldName,
                     "tvtest_sequenced_messages",
                     slotPosition(position, forwardX, forwardZ, ++slot),
                     box,
                     List.of(seq1, seq2, seq3),
                     targets
                  );
                  created += this.registerVolume(
                     manager,
                     worldName,
                     "tvtest_event_title",
                     slotPosition(position, forwardX, forwardZ, ++slot),
                     box,
                     List.of(
                        ShowEventTitleEffect.create(
                           TriggerEventType.ENTER,
                           "server.commands.triggervolume.test.msg.titlePrimary",
                           "server.commands.triggervolume.test.msg.titleSecondary",
                           true
                        )
                     ),
                     targets
                  );
                  created += this.registerVolume(
                     manager,
                     worldName,
                     "tvtest_one_shot_destroy",
                     slotPosition(position, forwardX, forwardZ, ++slot),
                     box,
                     List.of(
                        SendMessageEffect.create(TriggerEventType.ENTER, "server.commands.triggervolume.test.msg.oneShot"),
                        DestroyVolumeEffect.create(TriggerEventType.ENTER)
                     ),
                     targets
                  );
                  created += this.registerVolume(
                     manager,
                     worldName,
                     "tvtest_vfx_at_origin",
                     slotPosition(position, forwardX, forwardZ, ++slot),
                     box,
                     List.of(PlayVfxEffect.create(TriggerEventType.ENTER, "Totem_Heal_Simple_Test", false)),
                     targets
                  );
                  created += this.registerVolume(
                     manager,
                     worldName,
                     "tvtest_vfx_at_entity",
                     slotPosition(position, forwardX, forwardZ, ++slot),
                     box,
                     List.of(PlayVfxEffect.create(TriggerEventType.ENTER, "Totem_Heal_Simple_Test", true)),
                     targets
                  );
                  VolumeEntry perPlayerCd = this.registerVolumeEntry(
                     manager,
                     worldName,
                     "tvtest_cooldown_per_player_5s",
                     slotPosition(position, forwardX, forwardZ, ++slot),
                     box,
                     List.of(SendMessageEffect.create(TriggerEventType.ENTER, "server.commands.triggervolume.test.msg.cooldownPerPlayer")),
                     targets
                  );
                  if (perPlayerCd != null) {
                     perPlayerCd.setCooldown(5.0F);
                     perPlayerCd.setCooldownMode(CooldownMode.PER_ENTITY);
                     created++;
                  }

                  VolumeEntry totalCd = this.registerVolumeEntry(
                     manager,
                     worldName,
                     "tvtest_cooldown_total_10s",
                     slotPosition(position, forwardX, forwardZ, ++slot),
                     box,
                     List.of(SendMessageEffect.create(TriggerEventType.ENTER, "server.commands.triggervolume.test.msg.cooldownTotal")),
                     targets
                  );
                  if (totalCd != null) {
                     totalCd.setCooldown(10.0F);
                     totalCd.setCooldownMode(CooldownMode.TOTAL);
                     created++;
                  }

                  context.sendMessage(Message.translation("server.commands.triggervolume.test.success").param("count", created));
               }
            }
         } else {
            ArrayList<String> toRemove = new ArrayList<>();

            for (Entry<String, VolumeEntry> entry : manager.getVolumesMap().entrySet()) {
               if (entry.getKey().startsWith("tvtest_")) {
                  toRemove.add(entry.getKey());
               }
            }

            for (String id : toRemove) {
               manager.unregister(id);
               manager.notifyViewersRemove(id);
            }

            context.sendMessage(Message.translation("server.commands.triggervolume.test.cleaned").param("count", toRemove.size()));
         }
      }
   }

   @Nonnull
   private static Vector3d slotPosition(@Nonnull Vector3d origin, double forwardX, double forwardZ, int slot) {
      return new Vector3d(origin.x() + forwardX * 8.0 * slot, origin.y(), origin.z() + forwardZ * 8.0 * slot);
   }

   private int registerVolume(
      @Nonnull TriggerVolumeManager manager,
      @Nonnull String worldName,
      @Nonnull String id,
      @Nonnull Vector3d position,
      @Nonnull TriggerVolumeShape shape,
      @Nonnull List<TriggerEffect> effects,
      @Nonnull Set<EntityTargetType> targets
   ) {
      this.registerVolumeEntry(manager, worldName, id, position, shape, effects, targets);
      return 1;
   }

   @Nullable
   private VolumeEntry registerVolumeEntry(
      @Nonnull TriggerVolumeManager manager,
      @Nonnull String worldName,
      @Nonnull String id,
      @Nonnull Vector3d position,
      @Nonnull TriggerVolumeShape shape,
      @Nonnull List<TriggerEffect> effects,
      @Nonnull Set<EntityTargetType> targets
   ) {
      if (manager.hasVolume(id)) {
         manager.unregister(id);
      }

      VolumeEntry entry = new VolumeEntry(id, worldName, position, shape, new ArrayList<>(effects), targets, true);
      manager.register(id, entry);
      return entry;
   }
}
