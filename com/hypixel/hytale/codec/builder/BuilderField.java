/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.builder;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.InheritCodec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.PrimitiveCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.schema.metadata.Metadata;
import com.hypixel.hytale.codec.util.RawJsonReader;
import com.hypixel.hytale.codec.validation.LateValidator;
import com.hypixel.hytale.codec.validation.LegacyValidator;
import com.hypixel.hytale.codec.validation.ValidatableCodec;
import com.hypixel.hytale.codec.validation.ValidationResults;
import com.hypixel.hytale.codec.validation.Validator;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.codec.validation.validator.DeprecatedValidator;
import com.hypixel.hytale.codec.validation.validator.NonNullValidator;
import com.hypixel.hytale.function.consumer.TriConsumer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonDocument;

public class BuilderField<Type, FieldType> {
    @Nonnull
    protected final KeyedCodec<FieldType> codec;
    @Nonnull
    protected final TriConsumer<Type, FieldType, ExtraInfo> setter;
    @Nonnull
    protected final BiFunction<Type, ExtraInfo, FieldType> getter;
    protected final TriConsumer<Type, Type, ExtraInfo> inherit;
    @Nullable
    protected final List<Validator<? super FieldType>> validators;
    @Nullable
    protected final List<Metadata> metadata;
    protected final int minVersion;
    protected final int maxVersion;
    @Nullable
    protected final String documentation;
    @Nullable
    protected final NonNullValidator<? super FieldType> nonNullValidator;
    protected final boolean isPrimitive;

    protected BuilderField(@Nonnull FieldBuilder<Type, FieldType, ?> builder) {
        this.codec = builder.codec;
        this.setter = builder.setter;
        this.getter = builder.getter;
        this.inherit = builder.inherit;
        this.validators = builder.validators;
        this.metadata = builder.metadata;
        this.minVersion = builder.minVersion;
        this.maxVersion = builder.maxVersion;
        this.documentation = builder.documentation;
        if (builder.validators == null) {
            this.nonNullValidator = null;
        } else {
            NonNullValidator found = null;
            for (Validator validator : builder.validators) {
                if (!(validator instanceof NonNullValidator)) continue;
                found = (NonNullValidator)validator;
                break;
            }
            this.nonNullValidator = found;
        }
        this.isPrimitive = this.codec.getChildCodec() instanceof PrimitiveCodec;
    }

    protected BuilderField(@Nonnull KeyedCodec<FieldType> codec, TriConsumer<Type, FieldType, ExtraInfo> setter, BiFunction<Type, ExtraInfo, FieldType> getter, TriConsumer<Type, Type, ExtraInfo> inherit) {
        this.codec = Objects.requireNonNull(codec, "codec parameter can't be null");
        this.setter = Objects.requireNonNull(setter, "setter parameter can't be null");
        this.getter = Objects.requireNonNull(getter, "getter parameter can't be null");
        this.inherit = inherit;
        this.validators = null;
        this.metadata = null;
        this.minVersion = Integer.MIN_VALUE;
        this.maxVersion = Integer.MAX_VALUE;
        this.documentation = null;
        this.nonNullValidator = null;
        this.isPrimitive = codec.getChildCodec() instanceof PrimitiveCodec;
    }

    @Nonnull
    public KeyedCodec<FieldType> getCodec() {
        return this.codec;
    }

    public int getMinVersion() {
        return this.minVersion;
    }

    public int getMaxVersion() {
        return this.maxVersion;
    }

    public int getHighestSupportedVersion() {
        return this.maxVersion != Integer.MAX_VALUE ? this.maxVersion : this.minVersion;
    }

    public boolean supportsVersion(int version) {
        return version == Integer.MIN_VALUE || version >= this.minVersion && version <= this.maxVersion;
    }

    @Nullable
    public List<Validator<? super FieldType>> getValidators() {
        return this.validators;
    }

    public boolean hasNonNullValidator() {
        return this.nonNullValidator != null;
    }

    @Nullable
    public String getDocumentation() {
        return this.documentation;
    }

    public void decode(BsonDocument document, Type t, @Nonnull ExtraInfo extraInfo) {
        FieldType value = this.codec.getOrNull(document, extraInfo);
        this.setValue(t, value, extraInfo);
    }

    public void decodeAndInherit(BsonDocument document, Type t, @Nullable Type parent, @Nonnull ExtraInfo extraInfo) {
        Object parentValue = parent != null ? (Object)this.getter.apply(parent, extraInfo) : null;
        FieldType value = this.codec.getAndInherit(document, parentValue, extraInfo).orElse(null);
        this.setValue(t, value, extraInfo);
    }

    public void encode(@Nonnull BsonDocument document, Type t, @Nonnull ExtraInfo extraInfo) {
        FieldType value = this.getter.apply(t, extraInfo);
        if (value != null) {
            this.codec.put(document, value, extraInfo);
        }
    }

    public void inherit(Type t, Type parent, ExtraInfo extraInfo) {
        if (this.inherit != null) {
            this.inherit.accept(t, parent, extraInfo);
        }
    }

    public void decodeJson(@Nonnull RawJsonReader reader, Type t, @Nonnull ExtraInfo extraInfo) throws IOException {
        int read = reader.peek();
        if (read == -1) {
            throw new IOException("Unexpected EOF!");
        }
        switch (read) {
            case 78: 
            case 110: {
                reader.readNullValue();
                this.setValue(t, null, extraInfo);
                return;
            }
        }
        FieldType value = this.codec.getChildCodec().decodeJson(reader, extraInfo);
        this.setValue(t, value, extraInfo);
    }

    public void decodeAndInheritJson(@Nonnull RawJsonReader reader, Type t, @Nullable Type parent, @Nonnull ExtraInfo extraInfo) throws IOException {
        int read = reader.peek();
        if (read == -1) {
            throw new IOException("Unexpected EOF!");
        }
        switch (read) {
            case 78: 
            case 110: {
                reader.readNullValue();
                this.setValue(t, null, extraInfo);
                return;
            }
        }
        Codec<FieldType> child = this.codec.getChildCodec();
        if (child instanceof InheritCodec) {
            Object parentValue = parent != null ? (Object)this.getter.apply(parent, extraInfo) : null;
            FieldType value = ((InheritCodec)child).decodeAndInheritJson(reader, parentValue, extraInfo);
            this.setValue(t, value, extraInfo);
        } else {
            FieldType value = child.decodeJson(reader, extraInfo);
            this.setValue(t, value, extraInfo);
        }
    }

    public void setValue(Type t, @Nullable FieldType value, @Nonnull ExtraInfo extraInfo) {
        ValidationResults results;
        if (this.validators != null) {
            results = extraInfo.getValidationResults();
            for (int i = 0; i < this.validators.size(); ++i) {
                this.validators.get(i).accept(value, results);
            }
            results._processValidationResults();
        }
        if (this.isPrimitive && value == null) {
            results = extraInfo.getValidationResults();
            Validators.nonNull().accept((Object)null, results);
            results._processValidationResults();
            return;
        }
        this.setter.accept(t, value, extraInfo);
    }

    public void validate(Type t, @Nonnull ExtraInfo extraInfo) {
        FieldType value = this.getter.apply(t, extraInfo);
        this.validateValue(value, extraInfo, null);
    }

    public void validateDefaults(Type t, @Nonnull ExtraInfo extraInfo, Set<Codec<?>> tested) {
        FieldType defaultValue = this.getter.apply(t, extraInfo);
        if (defaultValue != null && !this.codec.isRequired() && this.nonNullValidator == null) {
            this.validateValue(defaultValue, extraInfo, v -> v instanceof DeprecatedValidator);
        }
        Codec<FieldType> childCodec = this.codec.getChildCodec();
        ValidatableCodec.validateDefaults(childCodec, extraInfo, tested);
    }

    private void validateValue(FieldType value, @Nonnull ExtraInfo extraInfo, @Nullable Predicate<Validator<? super FieldType>> filter) {
        if (this.codec instanceof ValidatableCodec) {
            ((ValidatableCodec)((Object)this.codec)).validate(value, extraInfo);
        }
        if (this.validators != null) {
            ValidationResults results = extraInfo.getValidationResults();
            for (int i = 0; i < this.validators.size(); ++i) {
                Validator<FieldType> validator = this.validators.get(i);
                if (filter != null && filter.test(validator) || validator instanceof LateValidator) continue;
                validator.accept(value, results);
            }
            results._processValidationResults();
        }
    }

    public void nullValidate(Type t, @Nonnull ValidationResults results, ExtraInfo extraInfo) {
        if (this.nonNullValidator == null) {
            return;
        }
        FieldType apply = this.getter.apply(t, extraInfo);
        if (apply != null) {
            return;
        }
        this.nonNullValidator.accept((FieldType)null, results);
        results._processValidationResults();
    }

    public void updateSchema(SchemaContext context, @Nonnull Schema target) {
        int i;
        if (this.validators != null) {
            for (i = 0; i < this.validators.size(); ++i) {
                Validator<FieldType> validator = this.validators.get(i);
                validator.updateSchema(context, target);
            }
        }
        if (this.metadata != null) {
            for (i = 0; i < this.metadata.size(); ++i) {
                Metadata meta = this.metadata.get(i);
                meta.modify(target);
            }
        }
        if (this.inherit != null) {
            target.getHytale().setInheritsProperty(true);
        }
    }

    @Nonnull
    public String toString() {
        return "BuilderField{codec=" + String.valueOf(this.codec) + ", setter=" + String.valueOf(this.setter) + ", getter=" + String.valueOf(this.getter) + "}";
    }

    public static class FieldBuilder<T, FieldType, Builder extends BuilderCodec.BuilderBase<T, Builder>> {
        @Nonnull
        protected final Builder parentBuilder;
        @Nonnull
        protected final KeyedCodec<FieldType> codec;
        @Nonnull
        protected final TriConsumer<T, FieldType, ExtraInfo> setter;
        @Nonnull
        protected final BiFunction<T, ExtraInfo, FieldType> getter;
        protected final TriConsumer<T, T, ExtraInfo> inherit;
        protected List<Validator<? super FieldType>> validators;
        protected List<Metadata> metadata;
        protected int minVersion = Integer.MIN_VALUE;
        protected int maxVersion = Integer.MAX_VALUE;
        protected String documentation;

        public FieldBuilder(Builder parentBuilder, KeyedCodec<FieldType> codec, TriConsumer<T, FieldType, ExtraInfo> setter, BiFunction<T, ExtraInfo, FieldType> getter, TriConsumer<T, T, ExtraInfo> inherit) {
            this.parentBuilder = (BuilderCodec.BuilderBase)Objects.requireNonNull(parentBuilder, "parentBuilder parameter can't be null");
            this.codec = Objects.requireNonNull(codec, "codec parameter can't be null");
            this.setter = Objects.requireNonNull(setter, "setter parameter can't be null");
            this.getter = Objects.requireNonNull(getter, "getter parameter can't be null");
            this.inherit = inherit;
        }

        @Nonnull
        public FieldBuilder<T, FieldType, Builder> addValidator(Validator<? super FieldType> validator) {
            if (this.validators == null) {
                this.validators = new ObjectArrayList<Validator<? super FieldType>>();
            }
            this.validators.add(validator);
            return this;
        }

        @Nonnull
        @Deprecated(forRemoval=true)
        public FieldBuilder<T, FieldType, Builder> addValidator(LegacyValidator<? super FieldType> validator) {
            if (this.validators == null) {
                this.validators = new ObjectArrayList<Validator<? super FieldType>>();
            }
            this.validators.add(validator);
            return this;
        }

        @Nonnull
        public FieldBuilder<T, FieldType, Builder> addValidatorLate(final @Nonnull Supplier<LateValidator<? super FieldType>> validatorSupplier) {
            if (this.validators == null) {
                this.validators = new ObjectArrayList<Validator<? super FieldType>>();
            }
            this.validators.add(new LateValidator<FieldType>(this){
                private LateValidator<? super FieldType> validator;

                @Override
                public void accept(FieldType fieldType, ValidationResults results) {
                    if (this.validator == null) {
                        this.validator = (LateValidator)validatorSupplier.get();
                    }
                    this.validator.accept(fieldType, results);
                }

                @Override
                public void acceptLate(FieldType fieldType, ValidationResults results, ExtraInfo extraInfo) {
                    if (this.validator == null) {
                        this.validator = (LateValidator)validatorSupplier.get();
                    }
                    this.validator.acceptLate(fieldType, results, extraInfo);
                }

                @Override
                public void updateSchema(SchemaContext context, Schema target) {
                    if (this.validator == null) {
                        this.validator = (LateValidator)validatorSupplier.get();
                    }
                    this.validator.updateSchema(context, target);
                }
            });
            return this;
        }

        @Nonnull
        public FieldBuilder<T, FieldType, Builder> setVersionRange(int minVersion, int maxVersion) {
            this.minVersion = minVersion;
            this.maxVersion = maxVersion;
            return this;
        }

        @Nonnull
        public FieldBuilder<T, FieldType, Builder> documentation(String doc) {
            this.documentation = doc;
            return this;
        }

        @Nonnull
        public FieldBuilder<T, FieldType, Builder> metadata(Metadata metadata) {
            if (this.metadata == null) {
                this.metadata = new ObjectArrayList<Metadata>();
            }
            this.metadata.add(metadata);
            return this;
        }

        @Nonnull
        public Builder add() {
            ((BuilderCodec.BuilderBase)this.parentBuilder).addField(new BuilderField(this));
            return this.parentBuilder;
        }
    }
}

