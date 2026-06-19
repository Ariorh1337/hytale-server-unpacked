package com.hypixel.hytale.codec.record;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.StringTreeMap;
import com.hypixel.hytale.codec.exception.CodecException;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.NullSchema;
import com.hypixel.hytale.codec.schema.config.ObjectSchema;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.util.RawJsonReader;
import com.hypixel.hytale.codec.validation.ValidatableCodec;
import com.hypixel.hytale.codec.validation.ValidationResults;
import com.hypixel.hytale.codec.validation.Validator;
import com.hypixel.hytale.function.function.Function4;
import com.hypixel.hytale.function.function.Function5;
import com.hypixel.hytale.function.function.Function6;
import com.hypixel.hytale.function.function.Function7;
import com.hypixel.hytale.function.function.Function8;
import com.hypixel.hytale.function.function.TriFunction;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.lang.reflect.RecordComponent;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonDocument;
import org.bson.BsonValue;

public final class RecordCodec<T> implements Codec<T>, ValidatableCodec<T> {
   @Nonnull
   private final Class<T> type;
   @Nonnull
   private final Function<Object[], T> constructor;
   @Nonnull
   private final RecordField<T, ?>[] fields;
   @Nonnull
   private final Object[] defaults;
   @Nonnull
   private final Map<String, RecordField<T, ?>> byKey;
   @Nonnull
   private final StringTreeMap<RecordField<T, ?>> jsonLookup;
   private final boolean hasNonNullValidator;

   RecordCodec(@Nonnull Class<T> type, @Nonnull List<RecordField<T, ?>> fieldList, @Nonnull Function<Object[], T> constructor) {
      this.type = type;
      this.constructor = constructor;
      this.fields = fieldList.toArray(new RecordField[0]);
      this.defaults = computeDefaults(type, this.fields);
      Map<String, RecordField<T, ?>> byKey = new Object2ObjectOpenHashMap<>(this.fields.length);
      StringTreeMap<RecordField<T, ?>> jsonLookup = new StringTreeMap<>();
      boolean hasNonNullValidator = false;

      for (RecordField<T, ?> field : this.fields) {
         String key = field.getCodec().getKey();
         if (byKey.put(key, field) != null) {
            throw new IllegalArgumentException("Duplicate key '" + key + "' in RecordCodec for " + type.getName());
         }

         jsonLookup.put(key, field);
         hasNonNullValidator |= field.hasNonNullValidator();
      }

      this.byKey = byKey;
      this.jsonLookup = jsonLookup;
      this.hasNonNullValidator = hasNonNullValidator;
   }

   @Nonnull
   public Class<T> getType() {
      return this.type;
   }

   @Override
   public T decode(@Nonnull BsonValue bsonValue, @Nonnull ExtraInfo extraInfo) {
      BsonDocument document = bsonValue.asDocument();
      Object[] args = (Object[])this.defaults.clone();

      for (Entry<String, BsonValue> entry : document.entrySet()) {
         RecordField<T, ?> field = this.byKey.get(entry.getKey());
         if (field != null) {
            field.decode(entry.getValue(), args, extraInfo);
         } else {
            extraInfo.addUnknownKey(entry.getKey());
         }
      }

      this.validateRequired(args, extraInfo);
      return this.construct(args);
   }

   @Nonnull
   public BsonDocument encode(T t, @Nonnull ExtraInfo extraInfo) {
      BsonDocument document = new BsonDocument();

      for (RecordField<T, ?> field : this.fields) {
         field.encode(document, t, extraInfo);
      }

      return document;
   }

   @Override
   public T decodeJson(@Nonnull RawJsonReader reader, @Nonnull ExtraInfo extraInfo) throws IOException {
      Object[] args = (Object[])this.defaults.clone();
      reader.expect('{');
      reader.consumeWhiteSpace();
      if (!reader.tryConsume('}')) {
         while (true) {
            this.readEntry(reader, args, extraInfo);
            reader.consumeWhiteSpace();
            if (reader.tryConsumeOrExpect('}', ',')) {
               break;
            }

            reader.consumeWhiteSpace();
         }
      }

      this.validateRequired(args, extraInfo);
      return this.construct(args);
   }

   private void readEntry(@Nonnull RawJsonReader reader, @Nonnull Object[] args, @Nonnull ExtraInfo extraInfo) throws IOException {
      reader.mark();
      StringTreeMap<RecordField<T, ?>> entry = this.jsonLookup.findEntry(reader);
      RecordField<T, ?> field;
      if (entry != null && (field = entry.getValue()) != null) {
         reader.unmark();
         reader.consumeWhiteSpace();
         reader.expect(':');
         reader.consumeWhiteSpace();
         extraInfo.pushKey(field.getCodec().getKey(), reader);

         try {
            field.decodeJson(reader, args, extraInfo);
         } catch (Exception e) {
            throw new CodecException("Failed to decode", reader, extraInfo, e);
         } finally {
            extraInfo.popKey();
         }
      } else {
         reader.reset();
         this.readUnknownField(reader, extraInfo);
      }
   }

   private void readUnknownField(@Nonnull RawJsonReader reader, @Nonnull ExtraInfo extraInfo) throws IOException {
      extraInfo.readUnknownKey(reader);
      reader.consumeWhiteSpace();
      reader.expect(':');
      reader.consumeWhiteSpace();
      reader.skipValue();
   }

   private T construct(@Nonnull Object[] args) {
      try {
         return this.constructor.apply(args);
      } catch (Exception e) {
         throw new CodecException("Failed to construct " + this.type.getSimpleName(), e);
      }
   }

   @Nullable
   private T tryConstructDefaultInstance() {
      try {
         return this.constructor.apply((Object[])this.defaults.clone());
      } catch (Exception e) {
         return null;
      }
   }

   private void validateRequired(@Nonnull Object[] args, @Nonnull ExtraInfo extraInfo) {
      if (this.hasNonNullValidator) {
         ValidationResults results = extraInfo.getValidationResults();

         for (RecordField<T, ?> field : this.fields) {
            if (field.hasNonNullValidator()) {
               extraInfo.pushKey(field.getCodec().getKey());

               try {
                  field.validateRequiredArg(args, results);
               } finally {
                  extraInfo.popKey();
               }
            }
         }
      }
   }

   @Override
   public void validate(T t, @Nonnull ExtraInfo extraInfo) {
      for (RecordField<T, ?> field : this.fields) {
         extraInfo.pushKey(field.getCodec().getKey());

         try {
            field.validate(t, extraInfo);
         } finally {
            extraInfo.popKey();
         }
      }
   }

   @Override
   public void validateDefaults(@Nonnull ExtraInfo extraInfo, @Nonnull Set<Codec<?>> tested) {
      if (tested.add(this)) {
         T t = this.tryConstructDefaultInstance();

         for (int i = 0; i < this.fields.length; i++) {
            RecordField<T, ?> field = this.fields[i];
            extraInfo.pushKey(field.getCodec().getKey());

            try {
               field.validateDefaults(t, this.defaults[i], extraInfo, tested);
            } finally {
               extraInfo.popKey();
            }
         }
      }
   }

   @Nonnull
   @Override
   public Schema toSchema(@Nonnull SchemaContext context) {
      ObjectSchema schema = new ObjectSchema();
      schema.setAdditionalProperties(false);
      schema.setTitle(this.type.getSimpleName());
      T defaultInstance = this.tryConstructDefaultInstance();
      Map<String, Schema> properties = new Object2ObjectLinkedOpenHashMap<>();

      for (RecordField<T, ?> field : this.fields) {
         Schema fieldSchema = refFieldSchema(context, field, defaultInstance);
         field.updateSchema(context, fieldSchema);
         Schema finalSchema = fieldSchema;
         String typeId = Schema.CODEC.getIdFor((Class<? extends Schema>)fieldSchema.getClass());
         if (!typeId.isEmpty()) {
            if (!field.hasNonNullValidator() && !field.isPrimitive()) {
               fieldSchema.setTypes(new String[]{typeId, "null"});
            }
         } else if (!field.hasNonNullValidator()) {
            finalSchema = Schema.anyOf(fieldSchema, NullSchema.INSTANCE);
         }

         finalSchema.setMarkdownDescription(field.getDocumentation());
         properties.put(field.getCodec().getKey(), finalSchema);
      }

      schema.setProperties(properties);
      return schema;
   }

   @Nonnull
   private static <R, F> Schema refFieldSchema(@Nonnull SchemaContext context, @Nonnull RecordField<R, F> field, @Nullable R defaultInstance) {
      F def = defaultInstance != null ? field.getValue(defaultInstance) : null;
      return context.refDefinition(field.getCodec().getChildCodec(), def);
   }

   @Nonnull
   @Override
   public String toString() {
      return "RecordCodec{type=" + this.type.getSimpleName() + ", fields=" + this.byKey.keySet() + "}";
   }

   @Nonnull
   private static Object[] computeDefaults(@Nonnull Class<?> type, @Nonnull RecordField<?, ?>[] fields) {
      Object[] defaults = new Object[fields.length];
      if (!type.isRecord()) {
         for (RecordField<?, ?> field : fields) {
            if (field.isPrimitive()) {
               throw new IllegalArgumentException(
                  "RecordCodec for non-record type "
                     + type.getName()
                     + " has a primitive-backed field '"
                     + field.getCodec().getKey()
                     + "'; use a record, or a boxed component type, so absent fields can default"
               );
            }
         }

         return defaults;
      } else {
         RecordComponent[] components = type.getRecordComponents();
         if (components.length != fields.length) {
            throw new IllegalArgumentException(
               "RecordCodec for " + type.getName() + " has " + fields.length + " fields but the record declares " + components.length + " components"
            );
         }

         for (int i = 0; i < components.length; i++) {
            defaults[i] = primitiveZero(components[i].getType());
         }

         return defaults;
      }
   }

   private static Object primitiveZero(@Nonnull Class<?> componentType) {
      if (!componentType.isPrimitive()) {
         return null;
      } else if (componentType == int.class) {
         return 0;
      } else if (componentType == long.class) {
         return 0L;
      } else if (componentType == double.class) {
         return 0.0;
      } else if (componentType == float.class) {
         return 0.0F;
      } else if (componentType == boolean.class) {
         return Boolean.FALSE;
      } else if (componentType == byte.class) {
         return (byte)0;
      } else if (componentType == short.class) {
         return (short)0;
      } else {
         return componentType == char.class ? '\u0000' : null;
      }
   }

   @Nonnull
   public static <T> RecordCodec.Builder0<T> builder(@Nonnull Class<T> type) {
      return new RecordCodec.Builder0<>(type, new ObjectArrayList<>());
   }

   @Nonnull
   public static <T> RecordCodec.RawBuilder<T> rawBuilder(@Nonnull Class<T> type) {
      return new RecordCodec.RawBuilder<>(type, new ObjectArrayList<>());
   }

   public abstract static sealed class AbstractBuilder<T>
      permits RecordCodec.Builder0,
      RecordCodec.Builder1,
      RecordCodec.Builder2,
      RecordCodec.Builder3,
      RecordCodec.Builder4,
      RecordCodec.Builder5,
      RecordCodec.Builder6,
      RecordCodec.Builder7,
      RecordCodec.Builder8,
      RecordCodec.RawBuilder {
      @Nonnull
      protected final Class<T> type;
      @Nonnull
      protected final List<RecordField<T, ?>> fields;

      protected AbstractBuilder(@Nonnull Class<T> type, @Nonnull List<RecordField<T, ?>> fields) {
         this.type = type;
         this.fields = fields;
      }

      protected final <F> void addField(@Nonnull KeyedCodec<F> codec, @Nonnull Function<T, F> accessor) {
         this.fields.add(new RecordField<>(codec, accessor, this.fields.size(), new RecordField.Options<>()));
      }

      protected final <F> void addField(@Nonnull KeyedCodec<F> codec, @Nonnull Function<T, F> accessor, @Nonnull Validator<? super F> validator) {
         RecordField.Options<F> options = new RecordField.Options<>();
         options.addValidator(validator);
         this.fields.add(new RecordField<>(codec, accessor, this.fields.size(), options));
      }

      protected final <F> void addField(@Nonnull KeyedCodec<F> codec, @Nonnull Function<T, F> accessor, @Nonnull Consumer<RecordField.Options<F>> options) {
         RecordField.Options<F> fieldOptions = new RecordField.Options<>();
         options.accept(fieldOptions);
         this.fields.add(new RecordField<>(codec, accessor, this.fields.size(), fieldOptions));
      }

      @Nonnull
      public RecordCodec<T> buildRaw(@Nonnull Function<Object[], T> constructor) {
         return new RecordCodec<>(this.type, this.fields, constructor);
      }
   }

   public static final class Builder0<T> extends RecordCodec.AbstractBuilder<T> {
      Builder0(Class<T> type, List<RecordField<T, ?>> fields) {
         super(type, fields);
      }

      @Nonnull
      public <A> RecordCodec.Builder1<T, A> append(@Nonnull KeyedCodec<A> codec, @Nonnull Function<T, A> accessor) {
         this.addField(codec, accessor);
         return new RecordCodec.Builder1<>(this.type, this.fields);
      }

      @Nonnull
      public <A> RecordCodec.Builder1<T, A> append(@Nonnull KeyedCodec<A> codec, @Nonnull Function<T, A> accessor, @Nonnull Validator<? super A> validator) {
         this.addField(codec, accessor, validator);
         return new RecordCodec.Builder1<>(this.type, this.fields);
      }

      @Nonnull
      public <A> RecordCodec.Builder1<T, A> append(
         @Nonnull KeyedCodec<A> codec, @Nonnull Function<T, A> accessor, @Nonnull Consumer<RecordField.Options<A>> options
      ) {
         this.addField(codec, accessor, options);
         return new RecordCodec.Builder1<>(this.type, this.fields);
      }
   }

   public static final class Builder1<T, A> extends RecordCodec.AbstractBuilder<T> {
      Builder1(Class<T> type, List<RecordField<T, ?>> fields) {
         super(type, fields);
      }

      @Nonnull
      public <B> RecordCodec.Builder2<T, A, B> append(@Nonnull KeyedCodec<B> codec, @Nonnull Function<T, B> accessor) {
         this.addField(codec, accessor);
         return new RecordCodec.Builder2<>(this.type, this.fields);
      }

      @Nonnull
      public <B> RecordCodec.Builder2<T, A, B> append(@Nonnull KeyedCodec<B> codec, @Nonnull Function<T, B> accessor, @Nonnull Validator<? super B> validator) {
         this.addField(codec, accessor, validator);
         return new RecordCodec.Builder2<>(this.type, this.fields);
      }

      @Nonnull
      public <B> RecordCodec.Builder2<T, A, B> append(
         @Nonnull KeyedCodec<B> codec, @Nonnull Function<T, B> accessor, @Nonnull Consumer<RecordField.Options<B>> options
      ) {
         this.addField(codec, accessor, options);
         return new RecordCodec.Builder2<>(this.type, this.fields);
      }

      @Nonnull
      public RecordCodec<T> build(@Nonnull Function<A, T> constructor) {
         return new RecordCodec<>(this.type, this.fields, args -> constructor.apply((A)args[0]));
      }
   }

   public static final class Builder2<T, A, B> extends RecordCodec.AbstractBuilder<T> {
      Builder2(Class<T> type, List<RecordField<T, ?>> fields) {
         super(type, fields);
      }

      @Nonnull
      public <C> RecordCodec.Builder3<T, A, B, C> append(@Nonnull KeyedCodec<C> codec, @Nonnull Function<T, C> accessor) {
         this.addField(codec, accessor);
         return new RecordCodec.Builder3<>(this.type, this.fields);
      }

      @Nonnull
      public <C> RecordCodec.Builder3<T, A, B, C> append(
         @Nonnull KeyedCodec<C> codec, @Nonnull Function<T, C> accessor, @Nonnull Validator<? super C> validator
      ) {
         this.addField(codec, accessor, validator);
         return new RecordCodec.Builder3<>(this.type, this.fields);
      }

      @Nonnull
      public <C> RecordCodec.Builder3<T, A, B, C> append(
         @Nonnull KeyedCodec<C> codec, @Nonnull Function<T, C> accessor, @Nonnull Consumer<RecordField.Options<C>> options
      ) {
         this.addField(codec, accessor, options);
         return new RecordCodec.Builder3<>(this.type, this.fields);
      }

      @Nonnull
      public RecordCodec<T> build(@Nonnull BiFunction<A, B, T> constructor) {
         return new RecordCodec<>(this.type, this.fields, args -> constructor.apply((A)args[0], (B)args[1]));
      }
   }

   public static final class Builder3<T, A, B, C> extends RecordCodec.AbstractBuilder<T> {
      Builder3(Class<T> type, List<RecordField<T, ?>> fields) {
         super(type, fields);
      }

      @Nonnull
      public <D> RecordCodec.Builder4<T, A, B, C, D> append(@Nonnull KeyedCodec<D> codec, @Nonnull Function<T, D> accessor) {
         this.addField(codec, accessor);
         return new RecordCodec.Builder4<>(this.type, this.fields);
      }

      @Nonnull
      public <D> RecordCodec.Builder4<T, A, B, C, D> append(
         @Nonnull KeyedCodec<D> codec, @Nonnull Function<T, D> accessor, @Nonnull Validator<? super D> validator
      ) {
         this.addField(codec, accessor, validator);
         return new RecordCodec.Builder4<>(this.type, this.fields);
      }

      @Nonnull
      public <D> RecordCodec.Builder4<T, A, B, C, D> append(
         @Nonnull KeyedCodec<D> codec, @Nonnull Function<T, D> accessor, @Nonnull Consumer<RecordField.Options<D>> options
      ) {
         this.addField(codec, accessor, options);
         return new RecordCodec.Builder4<>(this.type, this.fields);
      }

      @Nonnull
      public RecordCodec<T> build(@Nonnull TriFunction<A, B, C, T> constructor) {
         return new RecordCodec<>(this.type, this.fields, args -> constructor.apply((A)args[0], (B)args[1], (C)args[2]));
      }
   }

   public static final class Builder4<T, A, B, C, D> extends RecordCodec.AbstractBuilder<T> {
      Builder4(Class<T> type, List<RecordField<T, ?>> fields) {
         super(type, fields);
      }

      @Nonnull
      public <E> RecordCodec.Builder5<T, A, B, C, D, E> append(@Nonnull KeyedCodec<E> codec, @Nonnull Function<T, E> accessor) {
         this.addField(codec, accessor);
         return new RecordCodec.Builder5<>(this.type, this.fields);
      }

      @Nonnull
      public <E> RecordCodec.Builder5<T, A, B, C, D, E> append(
         @Nonnull KeyedCodec<E> codec, @Nonnull Function<T, E> accessor, @Nonnull Validator<? super E> validator
      ) {
         this.addField(codec, accessor, validator);
         return new RecordCodec.Builder5<>(this.type, this.fields);
      }

      @Nonnull
      public <E> RecordCodec.Builder5<T, A, B, C, D, E> append(
         @Nonnull KeyedCodec<E> codec, @Nonnull Function<T, E> accessor, @Nonnull Consumer<RecordField.Options<E>> options
      ) {
         this.addField(codec, accessor, options);
         return new RecordCodec.Builder5<>(this.type, this.fields);
      }

      @Nonnull
      public RecordCodec<T> build(@Nonnull Function4<A, B, C, D, T> constructor) {
         return new RecordCodec<>(this.type, this.fields, args -> constructor.apply((A)args[0], (B)args[1], (C)args[2], (D)args[3]));
      }
   }

   public static final class Builder5<T, A, B, C, D, E> extends RecordCodec.AbstractBuilder<T> {
      Builder5(Class<T> type, List<RecordField<T, ?>> fields) {
         super(type, fields);
      }

      @Nonnull
      public <F> RecordCodec.Builder6<T, A, B, C, D, E, F> append(@Nonnull KeyedCodec<F> codec, @Nonnull Function<T, F> accessor) {
         this.addField(codec, accessor);
         return new RecordCodec.Builder6<>(this.type, this.fields);
      }

      @Nonnull
      public <F> RecordCodec.Builder6<T, A, B, C, D, E, F> append(
         @Nonnull KeyedCodec<F> codec, @Nonnull Function<T, F> accessor, @Nonnull Validator<? super F> validator
      ) {
         this.addField(codec, accessor, validator);
         return new RecordCodec.Builder6<>(this.type, this.fields);
      }

      @Nonnull
      public <F> RecordCodec.Builder6<T, A, B, C, D, E, F> append(
         @Nonnull KeyedCodec<F> codec, @Nonnull Function<T, F> accessor, @Nonnull Consumer<RecordField.Options<F>> options
      ) {
         this.addField(codec, accessor, options);
         return new RecordCodec.Builder6<>(this.type, this.fields);
      }

      @Nonnull
      public RecordCodec<T> build(@Nonnull Function5<A, B, C, D, E, T> constructor) {
         return new RecordCodec<>(this.type, this.fields, args -> constructor.apply((A)args[0], (B)args[1], (C)args[2], (D)args[3], (E)args[4]));
      }
   }

   public static final class Builder6<T, A, B, C, D, E, F> extends RecordCodec.AbstractBuilder<T> {
      Builder6(Class<T> type, List<RecordField<T, ?>> fields) {
         super(type, fields);
      }

      @Nonnull
      public <G> RecordCodec.Builder7<T, A, B, C, D, E, F, G> append(@Nonnull KeyedCodec<G> codec, @Nonnull Function<T, G> accessor) {
         this.addField(codec, accessor);
         return new RecordCodec.Builder7<>(this.type, this.fields);
      }

      @Nonnull
      public <G> RecordCodec.Builder7<T, A, B, C, D, E, F, G> append(
         @Nonnull KeyedCodec<G> codec, @Nonnull Function<T, G> accessor, @Nonnull Validator<? super G> validator
      ) {
         this.addField(codec, accessor, validator);
         return new RecordCodec.Builder7<>(this.type, this.fields);
      }

      @Nonnull
      public <G> RecordCodec.Builder7<T, A, B, C, D, E, F, G> append(
         @Nonnull KeyedCodec<G> codec, @Nonnull Function<T, G> accessor, @Nonnull Consumer<RecordField.Options<G>> options
      ) {
         this.addField(codec, accessor, options);
         return new RecordCodec.Builder7<>(this.type, this.fields);
      }

      @Nonnull
      public RecordCodec<T> build(@Nonnull Function6<A, B, C, D, E, F, T> constructor) {
         return new RecordCodec<>(this.type, this.fields, args -> constructor.apply((A)args[0], (B)args[1], (C)args[2], (D)args[3], (E)args[4], (F)args[5]));
      }
   }

   public static final class Builder7<T, A, B, C, D, E, F, G> extends RecordCodec.AbstractBuilder<T> {
      Builder7(Class<T> type, List<RecordField<T, ?>> fields) {
         super(type, fields);
      }

      @Nonnull
      public <H> RecordCodec.Builder8<T, A, B, C, D, E, F, G, H> append(@Nonnull KeyedCodec<H> codec, @Nonnull Function<T, H> accessor) {
         this.addField(codec, accessor);
         return new RecordCodec.Builder8<>(this.type, this.fields);
      }

      @Nonnull
      public <H> RecordCodec.Builder8<T, A, B, C, D, E, F, G, H> append(
         @Nonnull KeyedCodec<H> codec, @Nonnull Function<T, H> accessor, @Nonnull Validator<? super H> validator
      ) {
         this.addField(codec, accessor, validator);
         return new RecordCodec.Builder8<>(this.type, this.fields);
      }

      @Nonnull
      public <H> RecordCodec.Builder8<T, A, B, C, D, E, F, G, H> append(
         @Nonnull KeyedCodec<H> codec, @Nonnull Function<T, H> accessor, @Nonnull Consumer<RecordField.Options<H>> options
      ) {
         this.addField(codec, accessor, options);
         return new RecordCodec.Builder8<>(this.type, this.fields);
      }

      @Nonnull
      public RecordCodec<T> build(@Nonnull Function7<A, B, C, D, E, F, G, T> constructor) {
         return new RecordCodec<>(
            this.type, this.fields, args -> constructor.apply((A)args[0], (B)args[1], (C)args[2], (D)args[3], (E)args[4], (F)args[5], (G)args[6])
         );
      }
   }

   public static final class Builder8<T, A, B, C, D, E, F, G, H> extends RecordCodec.AbstractBuilder<T> {
      Builder8(Class<T> type, List<RecordField<T, ?>> fields) {
         super(type, fields);
      }

      @Nonnull
      public <I> RecordCodec.RawBuilder<T> append(@Nonnull KeyedCodec<I> codec, @Nonnull Function<T, I> accessor) {
         this.addField(codec, accessor);
         return new RecordCodec.RawBuilder<>(this.type, this.fields);
      }

      @Nonnull
      public <I> RecordCodec.RawBuilder<T> append(@Nonnull KeyedCodec<I> codec, @Nonnull Function<T, I> accessor, @Nonnull Validator<? super I> validator) {
         this.addField(codec, accessor, validator);
         return new RecordCodec.RawBuilder<>(this.type, this.fields);
      }

      @Nonnull
      public <I> RecordCodec.RawBuilder<T> append(
         @Nonnull KeyedCodec<I> codec, @Nonnull Function<T, I> accessor, @Nonnull Consumer<RecordField.Options<I>> options
      ) {
         this.addField(codec, accessor, options);
         return new RecordCodec.RawBuilder<>(this.type, this.fields);
      }

      @Nonnull
      public RecordCodec<T> build(@Nonnull Function8<A, B, C, D, E, F, G, H, T> constructor) {
         return new RecordCodec<>(
            this.type, this.fields, args -> constructor.apply((A)args[0], (B)args[1], (C)args[2], (D)args[3], (E)args[4], (F)args[5], (G)args[6], (H)args[7])
         );
      }
   }

   public static final class RawBuilder<T> extends RecordCodec.AbstractBuilder<T> {
      RawBuilder(Class<T> type, List<RecordField<T, ?>> fields) {
         super(type, fields);
      }

      @Nonnull
      public <F> RecordCodec.RawBuilder<T> append(@Nonnull KeyedCodec<F> codec, @Nonnull Function<T, F> accessor) {
         this.addField(codec, accessor);
         return this;
      }

      @Nonnull
      public <F> RecordCodec.RawBuilder<T> append(@Nonnull KeyedCodec<F> codec, @Nonnull Function<T, F> accessor, @Nonnull Validator<? super F> validator) {
         this.addField(codec, accessor, validator);
         return this;
      }

      @Nonnull
      public <F> RecordCodec.RawBuilder<T> append(
         @Nonnull KeyedCodec<F> codec, @Nonnull Function<T, F> accessor, @Nonnull Consumer<RecordField.Options<F>> options
      ) {
         this.addField(codec, accessor, options);
         return this;
      }
   }
}
