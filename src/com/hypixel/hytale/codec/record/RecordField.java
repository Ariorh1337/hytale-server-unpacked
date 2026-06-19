package com.hypixel.hytale.codec.record;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.PrimitiveCodec;
import com.hypixel.hytale.codec.exception.CodecException;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.schema.metadata.Metadata;
import com.hypixel.hytale.codec.util.RawJsonReader;
import com.hypixel.hytale.codec.validation.LateValidator;
import com.hypixel.hytale.codec.validation.LazyLateValidator;
import com.hypixel.hytale.codec.validation.ValidatableCodec;
import com.hypixel.hytale.codec.validation.ValidationResults;
import com.hypixel.hytale.codec.validation.Validator;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.codec.validation.validator.DeprecatedValidator;
import com.hypixel.hytale.codec.validation.validator.NonNullValidator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonDocument;
import org.bson.BsonValue;

public final class RecordField<Type, FieldType> {
   @Nonnull
   private final KeyedCodec<FieldType> codec;
   @Nonnull
   private final Function<Type, FieldType> accessor;
   private final int index;
   @Nullable
   private final List<Validator<? super FieldType>> validators;
   @Nullable
   private final List<Metadata> metadata;
   @Nullable
   private final String documentation;
   @Nullable
   private final NonNullValidator<? super FieldType> nonNullValidator;
   private final boolean isPrimitive;

   RecordField(@Nonnull KeyedCodec<FieldType> codec, @Nonnull Function<Type, FieldType> accessor, int index, @Nonnull RecordField.Options<FieldType> options) {
      this.codec = Objects.requireNonNull(codec, "codec parameter can't be null");
      this.accessor = Objects.requireNonNull(accessor, "accessor parameter can't be null");
      this.index = index;
      this.validators = options.validators;
      this.metadata = options.metadata;
      this.documentation = options.documentation;
      NonNullValidator<? super FieldType> found = null;
      if (this.validators != null) {
         for (Validator<? super FieldType> validator : this.validators) {
            if (validator instanceof NonNullValidator) {
               found = (NonNullValidator<? super FieldType>)validator;
               break;
            }
         }
      }

      this.nonNullValidator = found;
      this.isPrimitive = codec.getChildCodec() instanceof PrimitiveCodec;
   }

   @Nonnull
   public KeyedCodec<FieldType> getCodec() {
      return this.codec;
   }

   @Nullable
   public String getDocumentation() {
      return this.documentation;
   }

   public boolean hasNonNullValidator() {
      return this.nonNullValidator != null;
   }

   public boolean isPrimitive() {
      return this.isPrimitive;
   }

   public void encode(@Nonnull BsonDocument document, Type t, @Nonnull ExtraInfo extraInfo) {
      FieldType value = this.accessor.apply(t);
      this.codec.put(document, value, extraInfo);
   }

   @Nullable
   FieldType getValue(@Nonnull Type t) {
      return this.accessor.apply(t);
   }

   public void decode(@Nonnull BsonValue bsonValue, @Nonnull Object[] args, @Nonnull ExtraInfo extraInfo) {
      extraInfo.pushKey(this.codec.getKey());

      try {
         FieldType value;
         if (Codec.isNullBsonValue(bsonValue)) {
            value = null;
         } else {
            try {
               value = this.codec.getChildCodec().decode(bsonValue, extraInfo);
            } catch (Exception e) {
               throw new CodecException("Failed to decode", bsonValue, extraInfo, e);
            }
         }

         this.setValue(args, value, extraInfo);
      } finally {
         extraInfo.popKey();
      }
   }

   public void decodeJson(@Nonnull RawJsonReader reader, @Nonnull Object[] args, @Nonnull ExtraInfo extraInfo) throws IOException {
      int read = reader.peek();
      if (read == -1) {
         throw new IOException("Unexpected EOF!");
      }

      switch (read) {
         case 78:
         case 110:
            reader.readNullValue();
            this.setValue(args, null, extraInfo);
            return;
         default:
            FieldType value = this.codec.getChildCodec().decodeJson(reader, extraInfo);
            this.setValue(args, value, extraInfo);
      }
   }

   private void setValue(@Nonnull Object[] args, @Nullable FieldType value, @Nonnull ExtraInfo extraInfo) {
      if (this.validators != null) {
         ValidationResults results = extraInfo.getValidationResults();

         for (int i = 0; i < this.validators.size(); i++) {
            Validator<? super FieldType> validator = this.validators.get(i);
            if (validator != this.nonNullValidator) {
               validator.accept(value, results);
            }
         }

         results._processValidationResults();
      }

      if (this.isPrimitive && value == null) {
         ValidationResults results = extraInfo.getValidationResults();
         if (this.nonNullValidator != null) {
            this.nonNullValidator.accept(null, results);
         } else {
            Validators.nonNull().accept(null, results);
         }

         results._processValidationResults();
      } else {
         args[this.index] = value;
      }
   }

   void validateRequiredArg(@Nonnull Object[] args, @Nonnull ValidationResults results) {
      if (this.nonNullValidator != null) {
         if (args[this.index] == null) {
            this.nonNullValidator.accept(null, results);
            results._processValidationResults();
         }
      }
   }

   public void validate(Type t, @Nonnull ExtraInfo extraInfo) {
      this.validateValue(this.accessor.apply(t), extraInfo, null);
   }

   void validateDefaults(@Nullable Type t, @Nullable Object rawDefault, @Nonnull ExtraInfo extraInfo, @Nonnull Set<Codec<?>> tested) {
      FieldType value = (FieldType)(t != null ? this.accessor.apply(t) : rawDefault);
      if (value != null && !this.codec.isRequired() && this.nonNullValidator == null) {
         this.validateValue(value, extraInfo, v -> v instanceof DeprecatedValidator);
      }

      ValidatableCodec.validateDefaults(this.codec.getChildCodec(), extraInfo, tested);
   }

   private void validateValue(@Nullable FieldType value, @Nonnull ExtraInfo extraInfo, @Nullable Predicate<Validator<? super FieldType>> filter) {
      if (value != null && this.codec.getChildCodec() instanceof ValidatableCodec<?> validatable) {
         ((ValidatableCodec<FieldType>)validatable).validate(value, extraInfo);
      }

      if (this.validators != null) {
         ValidationResults results = extraInfo.getValidationResults();

         for (int i = 0; i < this.validators.size(); i++) {
            Validator<? super FieldType> validator = this.validators.get(i);
            if ((filter == null || !filter.test(validator)) && !(validator instanceof LateValidator)) {
               validator.accept(value, results);
            }
         }

         results._processValidationResults();
      }
   }

   public void updateSchema(@Nonnull SchemaContext context, @Nonnull Schema target) {
      if (this.validators != null) {
         for (int i = 0; i < this.validators.size(); i++) {
            this.validators.get(i).updateSchema(context, target);
         }
      }

      if (this.metadata != null) {
         for (int i = 0; i < this.metadata.size(); i++) {
            this.metadata.get(i).modify(target);
         }
      }
   }

   @Nonnull
   @Override
   public String toString() {
      return "RecordField{codec=" + this.codec + ", index=" + this.index + "}";
   }

   public static final class Options<FieldType> {
      @Nullable
      private List<Validator<? super FieldType>> validators;
      @Nullable
      private List<Metadata> metadata;
      @Nullable
      private String documentation;

      @Nonnull
      public RecordField.Options<FieldType> addValidator(@Nonnull Validator<? super FieldType> validator) {
         if (this.validators == null) {
            this.validators = new ObjectArrayList<>();
         }

         this.validators.add(validator);
         return this;
      }

      @Nonnull
      public RecordField.Options<FieldType> addValidatorLate(@Nonnull Supplier<LateValidator<? super FieldType>> validatorSupplier) {
         if (this.validators == null) {
            this.validators = new ObjectArrayList<>();
         }

         this.validators.add(new LazyLateValidator<>(validatorSupplier));
         return this;
      }

      @Nonnull
      public RecordField.Options<FieldType> documentation(@Nonnull String documentation) {
         this.documentation = documentation;
         return this;
      }

      @Nonnull
      public RecordField.Options<FieldType> metadata(@Nonnull Metadata metadata) {
         if (this.metadata == null) {
            this.metadata = new ObjectArrayList<>();
         }

         this.metadata.add(metadata);
         return this;
      }
   }
}
