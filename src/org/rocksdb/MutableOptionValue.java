package org.rocksdb;

public abstract class MutableOptionValue<T> {
   abstract double asDouble() throws NumberFormatException;

   abstract long asLong() throws NumberFormatException;

   abstract int asInt() throws NumberFormatException;

   abstract boolean asBoolean() throws IllegalStateException;

   abstract int[] asIntArray() throws IllegalStateException;

   abstract String asString();

   abstract T asObject();

   static MutableOptionValue<String> fromString(String var0) {
      return new MutableOptionValue.MutableOptionStringValue(var0);
   }

   static MutableOptionValue<Double> fromDouble(double var0) {
      return new MutableOptionValue.MutableOptionDoubleValue(var0);
   }

   static MutableOptionValue<Long> fromLong(long var0) {
      return new MutableOptionValue.MutableOptionLongValue(var0);
   }

   static MutableOptionValue<Integer> fromInt(int var0) {
      return new MutableOptionValue.MutableOptionIntValue(var0);
   }

   static MutableOptionValue<Boolean> fromBoolean(boolean var0) {
      return new MutableOptionValue.MutableOptionBooleanValue(var0);
   }

   static MutableOptionValue<int[]> fromIntArray(int[] var0) {
      return new MutableOptionValue.MutableOptionIntArrayValue(var0);
   }

   static <N extends Enum<N>> MutableOptionValue<N> fromEnum(N var0) {
      return new MutableOptionValue.MutableOptionEnumValue<>((N)var0);
   }

   static class MutableOptionBooleanValue extends MutableOptionValue<Boolean> {
      private final boolean value;

      MutableOptionBooleanValue(boolean var1) {
         this.value = var1;
      }

      @Override
      double asDouble() {
         throw new NumberFormatException("boolean is not applicable as double");
      }

      @Override
      long asLong() throws NumberFormatException {
         throw new NumberFormatException("boolean is not applicable as Long");
      }

      @Override
      int asInt() throws NumberFormatException {
         throw new NumberFormatException("boolean is not applicable as int");
      }

      @Override
      boolean asBoolean() {
         return this.value;
      }

      @Override
      int[] asIntArray() throws IllegalStateException {
         throw new IllegalStateException("boolean is not applicable as int[]");
      }

      @Override
      String asString() {
         return String.valueOf(this.value);
      }

      Boolean asObject() {
         return this.value;
      }
   }

   static class MutableOptionDoubleValue extends MutableOptionValue<Double> {
      private final double value;

      MutableOptionDoubleValue(double var1) {
         this.value = var1;
      }

      @Override
      double asDouble() {
         return this.value;
      }

      @Override
      long asLong() throws NumberFormatException {
         return Double.valueOf(this.value).longValue();
      }

      @Override
      int asInt() throws NumberFormatException {
         if (!(this.value > 2.147483647E9) && !(this.value < -2.1474836E9F)) {
            return Double.valueOf(this.value).intValue();
         } else {
            throw new NumberFormatException("double value lies outside the bounds of int");
         }
      }

      @Override
      boolean asBoolean() throws IllegalStateException {
         throw new IllegalStateException("double is not applicable as boolean");
      }

      @Override
      int[] asIntArray() throws IllegalStateException {
         if (!(this.value > 2.147483647E9) && !(this.value < -2.1474836E9F)) {
            return new int[]{Double.valueOf(this.value).intValue()};
         } else {
            throw new NumberFormatException("double value lies outside the bounds of int");
         }
      }

      @Override
      String asString() {
         return String.valueOf(this.value);
      }

      Double asObject() {
         return this.value;
      }
   }

   static class MutableOptionEnumValue<T extends Enum<T>> extends MutableOptionValue.MutableOptionValueObject<T> {
      MutableOptionEnumValue(T var1) {
         super((T)var1);
      }

      @Override
      double asDouble() throws NumberFormatException {
         throw new NumberFormatException("Enum is not applicable as double");
      }

      @Override
      long asLong() throws NumberFormatException {
         throw new NumberFormatException("Enum is not applicable as long");
      }

      @Override
      int asInt() throws NumberFormatException {
         throw new NumberFormatException("Enum is not applicable as int");
      }

      @Override
      boolean asBoolean() throws IllegalStateException {
         throw new NumberFormatException("Enum is not applicable as boolean");
      }

      @Override
      int[] asIntArray() throws IllegalStateException {
         throw new NumberFormatException("Enum is not applicable as int[]");
      }

      @Override
      String asString() {
         return ((Enum)this.value).name();
      }
   }

   static class MutableOptionIntArrayValue extends MutableOptionValue.MutableOptionValueObject<int[]> {
      MutableOptionIntArrayValue(int[] var1) {
         super(var1);
      }

      @Override
      double asDouble() {
         throw new NumberFormatException("int[] is not applicable as double");
      }

      @Override
      long asLong() throws NumberFormatException {
         throw new NumberFormatException("int[] is not applicable as Long");
      }

      @Override
      int asInt() throws NumberFormatException {
         throw new NumberFormatException("int[] is not applicable as int");
      }

      @Override
      boolean asBoolean() {
         throw new NumberFormatException("int[] is not applicable as boolean");
      }

      @Override
      int[] asIntArray() throws IllegalStateException {
         return this.value;
      }

      @Override
      String asString() {
         StringBuilder var1 = new StringBuilder();

         for (int var2 = 0; var2 < ((int[])this.value).length; var2++) {
            var1.append(this.value[var2]);
            if (var2 + 1 < this.value.length) {
               var1.append(":");
            }
         }

         return var1.toString();
      }
   }

   static class MutableOptionIntValue extends MutableOptionValue<Integer> {
      private final int value;

      MutableOptionIntValue(int var1) {
         this.value = var1;
      }

      @Override
      double asDouble() {
         return Integer.valueOf(this.value).doubleValue();
      }

      @Override
      long asLong() throws NumberFormatException {
         return this.value;
      }

      @Override
      int asInt() throws NumberFormatException {
         return this.value;
      }

      @Override
      boolean asBoolean() throws IllegalStateException {
         throw new IllegalStateException("int is not applicable as boolean");
      }

      @Override
      int[] asIntArray() throws IllegalStateException {
         return new int[]{this.value};
      }

      @Override
      String asString() {
         return String.valueOf(this.value);
      }

      Integer asObject() {
         return this.value;
      }
   }

   static class MutableOptionLongValue extends MutableOptionValue<Long> {
      private final long value;

      MutableOptionLongValue(long var1) {
         this.value = var1;
      }

      @Override
      double asDouble() {
         return Long.valueOf(this.value).doubleValue();
      }

      @Override
      long asLong() throws NumberFormatException {
         return this.value;
      }

      @Override
      int asInt() throws NumberFormatException {
         if (this.value <= 2147483647L && this.value >= -2147483648L) {
            return Long.valueOf(this.value).intValue();
         } else {
            throw new NumberFormatException("long value lies outside the bounds of int");
         }
      }

      @Override
      boolean asBoolean() throws IllegalStateException {
         throw new IllegalStateException("long is not applicable as boolean");
      }

      @Override
      int[] asIntArray() throws IllegalStateException {
         if (this.value <= 2147483647L && this.value >= -2147483648L) {
            return new int[]{Long.valueOf(this.value).intValue()};
         } else {
            throw new NumberFormatException("long value lies outside the bounds of int");
         }
      }

      @Override
      String asString() {
         return String.valueOf(this.value);
      }

      Long asObject() {
         return this.value;
      }
   }

   static class MutableOptionStringValue extends MutableOptionValue.MutableOptionValueObject<String> {
      MutableOptionStringValue(String var1) {
         super(var1);
      }

      @Override
      double asDouble() throws NumberFormatException {
         return Double.parseDouble(this.value);
      }

      @Override
      long asLong() throws NumberFormatException {
         return Long.parseLong(this.value);
      }

      @Override
      int asInt() throws NumberFormatException {
         return Integer.parseInt(this.value);
      }

      @Override
      boolean asBoolean() throws IllegalStateException {
         return Boolean.parseBoolean(this.value);
      }

      @Override
      int[] asIntArray() throws IllegalStateException {
         throw new IllegalStateException("String is not applicable as int[]");
      }

      @Override
      String asString() {
         return this.value;
      }
   }

   private abstract static class MutableOptionValueObject<T> extends MutableOptionValue<T> {
      protected final T value;

      protected MutableOptionValueObject(T var1) {
         this.value = (T)var1;
      }

      @Override
      T asObject() {
         return this.value;
      }
   }
}
