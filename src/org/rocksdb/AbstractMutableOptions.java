package org.rocksdb;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Map.Entry;

public class AbstractMutableOptions {
   protected static final String KEY_VALUE_PAIR_SEPARATOR = ";";
   protected static final char KEY_VALUE_SEPARATOR = '=';
   static final String INT_ARRAY_INT_SEPARATOR = ":";
   private static final String HAS_NOT_BEEN_SET = " has not been set";
   protected final String[] keys;
   private final String[] values;

   protected AbstractMutableOptions(String[] var1, String[] var2) {
      this.keys = var1;
      this.values = var2;
   }

   String[] getKeys() {
      return this.keys;
   }

   String[] getValues() {
      return this.values;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();

      for (int var2 = 0; var2 < this.keys.length; var2++) {
         var1.append(this.keys[var2]).append('=').append(this.values[var2]);
         if (var2 + 1 < this.keys.length) {
            var1.append(";");
         }
      }

      return var1.toString();
   }

   public abstract static class AbstractMutableOptionsBuilder<T extends AbstractMutableOptions, U extends AbstractMutableOptions.AbstractMutableOptionsBuilder<T, U, K>, K extends MutableOptionKey> {
      private final Map<K, MutableOptionValue<?>> options = new LinkedHashMap<>();
      private final List<OptionString.Entry> unknown = new ArrayList<>();

      protected abstract U self();

      protected abstract Map<String, K> allKeys();

      protected abstract T build(String[] var1, String[] var2);

      public T build() {
         String[] var1 = new String[this.options.size()];
         String[] var2 = new String[this.options.size()];
         int var3 = 0;

         for (Entry var5 : this.options.entrySet()) {
            var1[var3] = ((MutableOptionKey)var5.getKey()).name();
            var2[var3] = ((MutableOptionValue)var5.getValue()).asString();
            var3++;
         }

         return this.build(var1, var2);
      }

      protected U setDouble(K var1, double var2) {
         if (var1.getValueType() != MutableOptionKey.ValueType.DOUBLE) {
            throw new IllegalArgumentException(var1 + " does not accept a double value");
         }

         this.options.put((K)var1, MutableOptionValue.fromDouble(var2));
         return this.self();
      }

      protected double getDouble(K var1) throws NoSuchElementException, NumberFormatException {
         MutableOptionValue var2 = this.options.get(var1);
         if (var2 == null) {
            throw new NoSuchElementException(var1.name() + " has not been set");
         } else {
            return var2.asDouble();
         }
      }

      protected U setLong(K var1, long var2) {
         if (var1.getValueType() != MutableOptionKey.ValueType.LONG) {
            throw new IllegalArgumentException(var1 + " does not accept a long value");
         }

         this.options.put((K)var1, MutableOptionValue.fromLong(var2));
         return this.self();
      }

      protected long getLong(K var1) throws NoSuchElementException, NumberFormatException {
         MutableOptionValue var2 = this.options.get(var1);
         if (var2 == null) {
            throw new NoSuchElementException(var1.name() + " has not been set");
         } else {
            return var2.asLong();
         }
      }

      protected U setInt(K var1, int var2) {
         if (var1.getValueType() != MutableOptionKey.ValueType.INT) {
            throw new IllegalArgumentException(var1 + " does not accept an integer value");
         }

         this.options.put((K)var1, MutableOptionValue.fromInt(var2));
         return this.self();
      }

      protected int getInt(K var1) throws NoSuchElementException, NumberFormatException {
         MutableOptionValue var2 = this.options.get(var1);
         if (var2 == null) {
            throw new NoSuchElementException(var1.name() + " has not been set");
         } else {
            return var2.asInt();
         }
      }

      protected U setBoolean(K var1, boolean var2) {
         if (var1.getValueType() != MutableOptionKey.ValueType.BOOLEAN) {
            throw new IllegalArgumentException(var1 + " does not accept a boolean value");
         }

         this.options.put((K)var1, MutableOptionValue.fromBoolean(var2));
         return this.self();
      }

      protected boolean getBoolean(K var1) throws NoSuchElementException, NumberFormatException {
         MutableOptionValue var2 = this.options.get(var1);
         if (var2 == null) {
            throw new NoSuchElementException(var1.name() + " has not been set");
         } else {
            return var2.asBoolean();
         }
      }

      protected U setIntArray(K var1, int[] var2) {
         if (var1.getValueType() != MutableOptionKey.ValueType.INT_ARRAY) {
            throw new IllegalArgumentException(var1 + " does not accept an int array value");
         }

         this.options.put((K)var1, MutableOptionValue.fromIntArray(var2));
         return this.self();
      }

      protected int[] getIntArray(K var1) throws NoSuchElementException, NumberFormatException {
         MutableOptionValue var2 = this.options.get(var1);
         if (var2 == null) {
            throw new NoSuchElementException(var1.name() + " has not been set");
         } else {
            return var2.asIntArray();
         }
      }

      protected U setString(K var1, String var2) {
         if (var1.getValueType() != MutableOptionKey.ValueType.STRING) {
            throw new IllegalArgumentException(var1 + " does not accept a string value");
         }

         this.options.put((K)var1, MutableOptionValue.fromString(var2));
         return this.self();
      }

      protected String getString(K var1) {
         MutableOptionValue var2 = this.options.get(var1);
         if (var2 == null) {
            throw new NoSuchElementException(var1.name() + " has not been set");
         } else {
            return var2.asString();
         }
      }

      protected <N extends Enum<N>> U setEnum(K var1, N var2) {
         if (var1.getValueType() != MutableOptionKey.ValueType.ENUM) {
            throw new IllegalArgumentException(var1 + " does not accept a Enum value");
         }

         this.options.put((K)var1, MutableOptionValue.fromEnum((N)var2));
         return this.self();
      }

      protected <N extends Enum<N>> N getEnum(K var1) throws NoSuchElementException, NumberFormatException {
         MutableOptionValue var2 = this.options.get(var1);
         if (var2 == null) {
            throw new NoSuchElementException(var1.name() + " has not been set");
         } else if (!(var2 instanceof MutableOptionValue.MutableOptionEnumValue)) {
            throw new NoSuchElementException(var1.name() + " is not of Enum type");
         } else {
            return (N)((MutableOptionValue.MutableOptionEnumValue)var2).asObject();
         }
      }

      private long parseAsLong(String var1) {
         try {
            return Long.parseLong(var1);
         } catch (NumberFormatException var5) {
            double var3 = Double.parseDouble(var1);
            if (var3 != Math.round(var3)) {
               throw new IllegalArgumentException("Unable to parse or round " + var1 + " to long", var5);
            } else {
               return Math.round(var3);
            }
         }
      }

      private int parseAsInt(String var1) {
         try {
            return Integer.parseInt(var1);
         } catch (NumberFormatException var5) {
            double var3 = Double.parseDouble(var1);
            if (var3 != Math.round(var3)) {
               throw new IllegalArgumentException("Unable to parse or round " + var1 + " to int", var5);
            } else {
               return (int)Math.round(var3);
            }
         }
      }

      protected U fromParsed(List<OptionString.Entry> var1, boolean var2) {
         Objects.requireNonNull(var1);

         for (OptionString.Entry var4 : var1) {
            try {
               if (var4.key.isEmpty()) {
                  throw new IllegalArgumentException("options string is invalid: " + var4);
               }

               this.fromOptionString(var4, var2);
            } catch (NumberFormatException var6) {
               throw new IllegalArgumentException("" + var4.key + "=" + var4.value + " - not a valid value for its type", var6);
            }
         }

         return this.self();
      }

      private U fromOptionString(OptionString.Entry var1, boolean var2) throws IllegalArgumentException {
         Objects.requireNonNull(var1.key);
         Objects.requireNonNull(var1.value);
         MutableOptionKey var3 = this.allKeys().get(var1.key);
         if (var3 == null && var2) {
            this.unknown.add(var1);
            return this.self();
         }

         if (var3 == null) {
            throw new IllegalArgumentException("Key: " + null + " is not a known option key");
         }

         if (!var1.value.isList()) {
            throw new IllegalArgumentException("Option: " + var3 + " is not a simple value or list, don't know how to parse it");
         }

         if (var3.getValueType() != MutableOptionKey.ValueType.INT_ARRAY
            && var3.getValueType() != MutableOptionKey.ValueType.STRING
            && var1.value.list.size() != 1) {
            throw new IllegalArgumentException("Simple value does not have exactly 1 item: " + var1.value.list);
         }

         List var4 = var1.value.list;
         String var5 = (String)var4.get(0);
         switch (var3.getValueType()) {
            case DOUBLE:
               return this.setDouble((K)var3, Double.parseDouble(var5));
            case LONG:
               return this.setLong((K)var3, this.parseAsLong(var5));
            case INT:
               return this.setInt((K)var3, this.parseAsInt(var5));
            case BOOLEAN:
               return this.setBoolean((K)var3, Boolean.parseBoolean(var5));
            case INT_ARRAY:
               int[] var6 = new int[var4.size()];

               for (int var9 = 0; var9 < var4.size(); var9++) {
                  var6[var9] = Integer.parseInt((String)var4.get(var9));
               }

               return this.setIntArray((K)var3, var6);
            case ENUM:
               String var7 = var3.name();
               if ("prepopulate_blob_cache".equals(var7)) {
                  PrepopulateBlobCache var10 = PrepopulateBlobCache.getFromInternal(var5);
                  return this.setEnum((K)var3, var10);
               } else {
                  if (!"compression".equals(var7) && !"blob_compression_type".equals(var7)) {
                     throw new IllegalArgumentException("Unknown enum type: " + var3.name());
                  }

                  CompressionType var8 = CompressionType.getFromInternal(var5);
                  return this.setEnum((K)var3, var8);
               }
            case STRING:
               return this.setString((K)var3, var1.value.toString());
            default:
               throw new IllegalStateException(var3 + " has unknown value type: " + var3.getValueType());
         }
      }

      public List<OptionString.Entry> getUnknown() {
         return new ArrayList<>(this.unknown);
      }
   }
}
