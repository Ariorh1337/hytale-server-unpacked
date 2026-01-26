/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.pojo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

final class CreatorExecutable<T> {
    private final Class<T> clazz;
    private final Constructor<T> constructor;
    private final Method method;
    private final List<BsonProperty> properties = new ArrayList<BsonProperty>();
    private final Integer idPropertyIndex;
    private final List<Class<?>> parameterTypes = new ArrayList();
    private final List<Type> parameterGenericTypes = new ArrayList<Type>();

    CreatorExecutable(Class<T> clazz, Constructor<T> constructor) {
        this(clazz, constructor, null);
    }

    CreatorExecutable(Class<T> clazz, Method method) {
        this(clazz, null, method);
    }

    private CreatorExecutable(Class<T> clazz, Constructor<T> constructor, Method method) {
        this.clazz = clazz;
        this.constructor = constructor;
        this.method = method;
        Integer idPropertyIndex = null;
        if (constructor != null || method != null) {
            Class<?>[] paramTypes = constructor != null ? constructor.getParameterTypes() : method.getParameterTypes();
            Type[] genericParamTypes = constructor != null ? constructor.getGenericParameterTypes() : method.getGenericParameterTypes();
            this.parameterTypes.addAll(Arrays.asList(paramTypes));
            this.parameterGenericTypes.addAll(Arrays.asList(genericParamTypes));
            Annotation[][] parameterAnnotations = constructor != null ? constructor.getParameterAnnotations() : method.getParameterAnnotations();
            block0: for (int i = 0; i < parameterAnnotations.length; ++i) {
                Annotation[] parameterAnnotation;
                for (Annotation annotation : parameterAnnotation = parameterAnnotations[i]) {
                    if (annotation.annotationType().equals(BsonProperty.class)) {
                        this.properties.add((BsonProperty)annotation);
                        continue block0;
                    }
                    if (!annotation.annotationType().equals(BsonId.class)) continue;
                    this.properties.add(null);
                    idPropertyIndex = i;
                    continue block0;
                }
            }
        }
        this.idPropertyIndex = idPropertyIndex;
    }

    Class<T> getType() {
        return this.clazz;
    }

    List<BsonProperty> getProperties() {
        return this.properties;
    }

    Integer getIdPropertyIndex() {
        return this.idPropertyIndex;
    }

    List<Class<?>> getParameterTypes() {
        return this.parameterTypes;
    }

    List<Type> getParameterGenericTypes() {
        return this.parameterGenericTypes;
    }

    T getInstance() {
        this.checkHasAnExecutable();
        try {
            if (this.constructor != null) {
                return this.constructor.newInstance(new Object[0]);
            }
            return (T)this.method.invoke(this.clazz, new Object[0]);
        }
        catch (Exception e) {
            throw new CodecConfigurationException(e.getMessage(), e);
        }
    }

    T getInstance(Object[] params) {
        this.checkHasAnExecutable();
        try {
            if (this.constructor != null) {
                return this.constructor.newInstance(params);
            }
            return (T)this.method.invoke(this.clazz, params);
        }
        catch (Exception e) {
            throw new CodecConfigurationException(e.getMessage(), e);
        }
    }

    CodecConfigurationException getError(Class<?> clazz, String msg) {
        return CreatorExecutable.getError(clazz, this.constructor != null, msg);
    }

    private void checkHasAnExecutable() {
        if (this.constructor == null && this.method == null) {
            throw new CodecConfigurationException(String.format("Cannot find a public constructor for '%s'.", this.clazz.getSimpleName()));
        }
    }

    private static CodecConfigurationException getError(Class<?> clazz, boolean isConstructor, String msg) {
        return new CodecConfigurationException(String.format("Invalid @BsonCreator %s in %s. %s", isConstructor ? "constructor" : "method", clazz.getSimpleName(), msg));
    }
}

