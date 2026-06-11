package com.hypixel.hytale.server.npc.storage;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.util.Alarm;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.annotation.Nonnull;

public class AlarmStore extends ParameterStore<Alarm> implements Component<EntityStore> {
   public static final BuilderCodec<AlarmStore> CODEC = BuilderCodec.builder(AlarmStore.class, AlarmStore::new)
      .append(new KeyedCodec<>("Parameters", new MapCodec<>(Alarm.CODEC, HashMap::new, false)), (store, o) -> store.parameters = o, store -> store.parameters)
      .add()
      .build();

   @Nonnull
   public static ComponentType<EntityStore, AlarmStore> getComponentType() {
      return NPCPlugin.get().getAlarmStoreComponentType();
   }

   @Nonnull
   public static AlarmStore get(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> accessor) {
      AlarmStore store = accessor.getComponent(ref, getComponentType());
      assert store != null : "Missing AlarmStore on entity " + ref;
      return store;
   }

   @Nonnull
   protected Alarm createParameter() {
      return new Alarm();
   }

   @Override
   public Component<EntityStore> clone() {
      AlarmStore copy = new AlarmStore();
      copy.parameters = new HashMap<>(this.parameters.size());

      for (Entry<String, Alarm> entry : this.parameters.entrySet()) {
         copy.parameters.put(entry.getKey(), entry.getValue().cloneAlarm());
      }

      return copy;
   }
}
