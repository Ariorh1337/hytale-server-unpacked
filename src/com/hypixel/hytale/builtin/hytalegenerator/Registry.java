package com.hypixel.hytale.builtin.hytalegenerator;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import javax.annotation.Nonnull;

public class Registry<T> {
   private final Object2IntMap<T> objectToId = new Object2IntOpenHashMap<>();
   private final Int2ObjectMap<T> idToObject = new Int2ObjectOpenHashMap<>();

   public Registry() {
      this.objectToId.defaultReturnValue(-1);
   }

   public int getIdOrRegister(T object) {
      int id = this.objectToId.getInt(object);
      if (id != -1) {
         return id;
      }

      id = this.objectToId.size();
      this.idToObject.put(id, object);
      this.objectToId.put(object, id);
      return id;
   }

   public T getObject(int id) {
      return this.idToObject.get(id);
   }

   public int size() {
      return this.objectToId.size();
   }

   @Nonnull
   public List<T> getAllValues() {
      return new ArrayList<>(this.idToObject.values());
   }

   public void forEach(@Nonnull BiConsumer<Integer, T> consumer) {
      this.idToObject.forEach(consumer::accept);
   }
}
