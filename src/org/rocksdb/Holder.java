package org.rocksdb;

public class Holder<T> {
   private T value;

   public Holder() {
   }

   public Holder(T var1) {
      this.value = (T)var1;
   }

   public T getValue() {
      return this.value;
   }

   public void setValue(T var1) {
      this.value = (T)var1;
   }
}
