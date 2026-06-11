package com.hypixel.hytale.server.npc.storage;

import java.util.HashMap;
import java.util.Map;

public abstract class ParameterStore<Type extends PersistentParameter<?>> {
   protected Map<String, Type> parameters = new HashMap<>();

   protected ParameterStore() {
   }

   public Type get(String name) {
      Type parameter = this.parameters.get(name);
      if (parameter == null) {
         parameter = this.createParameter();
         this.parameters.put(name, parameter);
      }

      return parameter;
   }

   protected abstract Type createParameter();
}
