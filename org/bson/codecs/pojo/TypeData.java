/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.pojo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.assertions.Assertions;
import org.bson.codecs.pojo.PropertyReflectionUtils;
import org.bson.codecs.pojo.TypeWithTypeParameters;

final class TypeData<T>
implements TypeWithTypeParameters<T> {
    private final Class<T> type;
    private final List<TypeData<?>> typeParameters;
    private static final Map<Class<?>, Class<?>> PRIMITIVE_CLASS_MAP;

    public static <T> Builder<T> builder(Class<T> type) {
        return new Builder(Assertions.notNull("type", type));
    }

    public static TypeData<?> newInstance(Method method) {
        if (PropertyReflectionUtils.isGetter(method)) {
            return TypeData.newInstance(method.getGenericReturnType(), method.getReturnType());
        }
        return TypeData.newInstance(method.getGenericParameterTypes()[0], method.getParameterTypes()[0]);
    }

    public static TypeData<?> newInstance(Field field) {
        return TypeData.newInstance(field.getGenericType(), field.getType());
    }

    public static <T> TypeData<T> newInstance(Type genericType, Class<T> clazz) {
        Builder<T> builder = TypeData.builder(clazz);
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType)genericType;
            for (Type argType : pType.getActualTypeArguments()) {
                TypeData.getNestedTypeData(builder, argType);
            }
        }
        return builder.build();
    }

    private static <T> void getNestedTypeData(Builder<T> builder, Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType)type;
            Builder<T> paramBuilder = TypeData.builder((Class)pType.getRawType());
            for (Type argType : pType.getActualTypeArguments()) {
                TypeData.getNestedTypeData(paramBuilder, argType);
            }
            builder.addTypeParameter(paramBuilder.build());
        } else if (type instanceof WildcardType) {
            builder.addTypeParameter(TypeData.builder((Class)((WildcardType)type).getUpperBounds()[0]).build());
        } else if (type instanceof TypeVariable) {
            builder.addTypeParameter(TypeData.builder(Object.class).build());
        } else if (type instanceof Class) {
            builder.addTypeParameter(TypeData.builder((Class)type).build());
        }
    }

    @Override
    public Class<T> getType() {
        return this.type;
    }

    @Override
    public List<TypeData<?>> getTypeParameters() {
        return this.typeParameters;
    }

    public String toString() {
        String typeParams = this.typeParameters.isEmpty() ? "" : ", typeParameters=[" + TypeData.nestedTypeParameters(this.typeParameters) + "]";
        return "TypeData{type=" + this.type.getSimpleName() + typeParams + "}";
    }

    private static String nestedTypeParameters(List<TypeData<?>> typeParameters) {
        StringBuilder builder = new StringBuilder();
        int count = 0;
        int last = typeParameters.size();
        for (TypeData<?> typeParameter : typeParameters) {
            ++count;
            builder.append(typeParameter.getType().getSimpleName());
            if (!typeParameter.getTypeParameters().isEmpty()) {
                builder.append(String.format("<%s>", TypeData.nestedTypeParameters(typeParameter.getTypeParameters())));
            }
            if (count >= last) continue;
            builder.append(", ");
        }
        return builder.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TypeData)) {
            return false;
        }
        TypeData that = (TypeData)o;
        if (!this.getType().equals(that.getType())) {
            return false;
        }
        return this.getTypeParameters().equals(that.getTypeParameters());
    }

    public int hashCode() {
        int result = this.getType().hashCode();
        result = 31 * result + this.getTypeParameters().hashCode();
        return result;
    }

    private TypeData(Class<T> type, List<TypeData<?>> typeParameters) {
        this.type = this.boxType(type);
        this.typeParameters = typeParameters;
    }

    boolean isAssignableFrom(Class<?> cls) {
        return this.type.isAssignableFrom(this.boxType(cls));
    }

    private <S> Class<S> boxType(Class<S> clazz) {
        if (clazz.isPrimitive()) {
            return PRIMITIVE_CLASS_MAP.get(clazz);
        }
        return clazz;
    }

    static {
        HashMap map = new HashMap();
        map.put(Boolean.TYPE, Boolean.class);
        map.put(Byte.TYPE, Byte.class);
        map.put(Character.TYPE, Character.class);
        map.put(Double.TYPE, Double.class);
        map.put(Float.TYPE, Float.class);
        map.put(Integer.TYPE, Integer.class);
        map.put(Long.TYPE, Long.class);
        map.put(Short.TYPE, Short.class);
        PRIMITIVE_CLASS_MAP = map;
    }

    public static final class Builder<T> {
        private final Class<T> type;
        private final List<TypeData<?>> typeParameters = new ArrayList();

        private Builder(Class<T> type) {
            this.type = type;
        }

        public <S> Builder<T> addTypeParameter(TypeData<S> typeParameter) {
            this.typeParameters.add(Assertions.notNull("typeParameter", typeParameter));
            return this;
        }

        public Builder<T> addTypeParameters(List<TypeData<?>> typeParameters) {
            Assertions.notNull("typeParameters", typeParameters);
            for (TypeData<?> typeParameter : typeParameters) {
                this.addTypeParameter(typeParameter);
            }
            return this;
        }

        public TypeData<T> build() {
            return new TypeData(this.type, Collections.unmodifiableList(this.typeParameters));
        }
    }
}

