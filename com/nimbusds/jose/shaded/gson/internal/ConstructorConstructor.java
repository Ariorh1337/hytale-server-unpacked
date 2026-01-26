/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.shaded.gson.internal;

import com.nimbusds.jose.shaded.gson.InstanceCreator;
import com.nimbusds.jose.shaded.gson.JsonIOException;
import com.nimbusds.jose.shaded.gson.ReflectionAccessFilter;
import com.nimbusds.jose.shaded.gson.internal.GsonTypes;
import com.nimbusds.jose.shaded.gson.internal.LinkedTreeMap;
import com.nimbusds.jose.shaded.gson.internal.ObjectConstructor;
import com.nimbusds.jose.shaded.gson.internal.ReflectionAccessFilterHelper;
import com.nimbusds.jose.shaded.gson.internal.TroubleshootingGuide;
import com.nimbusds.jose.shaded.gson.internal.UnsafeAllocator;
import com.nimbusds.jose.shaded.gson.internal.reflect.ReflectionHelper;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public final class ConstructorConstructor {
    private final Map<Type, InstanceCreator<?>> instanceCreators;
    private final boolean useJdkUnsafe;
    private final List<ReflectionAccessFilter> reflectionFilters;

    public ConstructorConstructor(Map<Type, InstanceCreator<?>> instanceCreators, boolean useJdkUnsafe, List<ReflectionAccessFilter> reflectionFilters) {
        this.instanceCreators = instanceCreators;
        this.useJdkUnsafe = useJdkUnsafe;
        this.reflectionFilters = reflectionFilters;
    }

    static String checkInstantiable(Class<?> c) {
        int modifiers = c.getModifiers();
        if (Modifier.isInterface(modifiers)) {
            return "Interfaces can't be instantiated! Register an InstanceCreator or a TypeAdapter for this type. Interface name: " + c.getName();
        }
        if (Modifier.isAbstract(modifiers)) {
            return "Abstract classes can't be instantiated! Adjust the R8 configuration or register an InstanceCreator or a TypeAdapter for this type. Class name: " + c.getName() + "\nSee " + TroubleshootingGuide.createUrl("r8-abstract-class");
        }
        return null;
    }

    public <T> ObjectConstructor<T> get(TypeToken<T> typeToken) {
        return this.get(typeToken, true);
    }

    public <T> ObjectConstructor<T> get(TypeToken<T> typeToken, boolean allowUnsafe) {
        Type type = typeToken.getType();
        Class<T> rawType = typeToken.getRawType();
        InstanceCreator<?> typeCreator = this.instanceCreators.get(type);
        if (typeCreator != null) {
            return () -> typeCreator.createInstance(type);
        }
        InstanceCreator<?> rawTypeCreator = this.instanceCreators.get(rawType);
        if (rawTypeCreator != null) {
            return () -> rawTypeCreator.createInstance(type);
        }
        ObjectConstructor<T> specialConstructor = ConstructorConstructor.newSpecialCollectionConstructor(type, rawType);
        if (specialConstructor != null) {
            return specialConstructor;
        }
        ReflectionAccessFilter.FilterResult filterResult = ReflectionAccessFilterHelper.getFilterResult(this.reflectionFilters, rawType);
        ObjectConstructor<T> defaultConstructor = ConstructorConstructor.newDefaultConstructor(rawType, filterResult);
        if (defaultConstructor != null) {
            return defaultConstructor;
        }
        ObjectConstructor<T> defaultImplementation = ConstructorConstructor.newDefaultImplementationConstructor(type, rawType);
        if (defaultImplementation != null) {
            return defaultImplementation;
        }
        String exceptionMessage = ConstructorConstructor.checkInstantiable(rawType);
        if (exceptionMessage != null) {
            return () -> {
                throw new JsonIOException(exceptionMessage);
            };
        }
        if (!allowUnsafe) {
            String message = "Unable to create instance of " + rawType + "; Register an InstanceCreator or a TypeAdapter for this type.";
            return () -> {
                throw new JsonIOException(message);
            };
        }
        if (filterResult != ReflectionAccessFilter.FilterResult.ALLOW) {
            String message = "Unable to create instance of " + rawType + "; ReflectionAccessFilter does not permit using reflection or Unsafe. Register an InstanceCreator or a TypeAdapter for this type or adjust the access filter to allow using reflection.";
            return () -> {
                throw new JsonIOException(message);
            };
        }
        return this.newUnsafeAllocator(rawType);
    }

    private static <T> ObjectConstructor<T> newSpecialCollectionConstructor(Type type, Class<? super T> rawType) {
        if (EnumSet.class.isAssignableFrom(rawType)) {
            return () -> {
                if (type instanceof ParameterizedType) {
                    Type elementType = ((ParameterizedType)type).getActualTypeArguments()[0];
                    if (elementType instanceof Class) {
                        EnumSet set = EnumSet.noneOf((Class)elementType);
                        return set;
                    }
                    throw new JsonIOException("Invalid EnumSet type: " + type.toString());
                }
                throw new JsonIOException("Invalid EnumSet type: " + type.toString());
            };
        }
        if (rawType == EnumMap.class) {
            return () -> {
                if (type instanceof ParameterizedType) {
                    Type elementType = ((ParameterizedType)type).getActualTypeArguments()[0];
                    if (elementType instanceof Class) {
                        EnumMap map = new EnumMap((Class)elementType);
                        return map;
                    }
                    throw new JsonIOException("Invalid EnumMap type: " + type.toString());
                }
                throw new JsonIOException("Invalid EnumMap type: " + type.toString());
            };
        }
        return null;
    }

    private static <T> ObjectConstructor<T> newDefaultConstructor(Class<? super T> rawType, ReflectionAccessFilter.FilterResult filterResult) {
        String exceptionMessage;
        boolean canAccess;
        Constructor constructor;
        if (Modifier.isAbstract(rawType.getModifiers())) {
            return null;
        }
        try {
            constructor = rawType.getDeclaredConstructor(new Class[0]);
        }
        catch (NoSuchMethodException e) {
            return null;
        }
        boolean bl = canAccess = filterResult == ReflectionAccessFilter.FilterResult.ALLOW || ReflectionAccessFilterHelper.canAccess(constructor, null) && (filterResult != ReflectionAccessFilter.FilterResult.BLOCK_ALL || Modifier.isPublic(constructor.getModifiers()));
        if (!canAccess) {
            String message = "Unable to invoke no-args constructor of " + rawType + "; constructor is not accessible and ReflectionAccessFilter does not permit making it accessible. Register an InstanceCreator or a TypeAdapter for this type, change the visibility of the constructor or adjust the access filter.";
            return () -> {
                throw new JsonIOException(message);
            };
        }
        if (filterResult == ReflectionAccessFilter.FilterResult.ALLOW && (exceptionMessage = ReflectionHelper.tryMakeAccessible(constructor)) != null) {
            return () -> {
                throw new JsonIOException(exceptionMessage);
            };
        }
        return () -> {
            try {
                Object newInstance = constructor.newInstance(new Object[0]);
                return newInstance;
            }
            catch (InstantiationException e) {
                throw new RuntimeException("Failed to invoke constructor '" + ReflectionHelper.constructorToString(constructor) + "' with no args", e);
            }
            catch (InvocationTargetException e) {
                throw new RuntimeException("Failed to invoke constructor '" + ReflectionHelper.constructorToString(constructor) + "' with no args", e.getCause());
            }
            catch (IllegalAccessException e) {
                throw ReflectionHelper.createExceptionForUnexpectedIllegalAccess(e);
            }
        };
    }

    private static <T> ObjectConstructor<T> newDefaultImplementationConstructor(Type type, Class<? super T> rawType) {
        if (Collection.class.isAssignableFrom(rawType)) {
            ObjectConstructor<? extends Collection<? extends Object>> constructor = ConstructorConstructor.newCollectionConstructor(rawType);
            return constructor;
        }
        if (Map.class.isAssignableFrom(rawType)) {
            ObjectConstructor<? extends Map<? extends Object, Object>> constructor = ConstructorConstructor.newMapConstructor(type, rawType);
            return constructor;
        }
        return null;
    }

    private static ObjectConstructor<? extends Collection<? extends Object>> newCollectionConstructor(Class<?> rawType) {
        if (rawType.isAssignableFrom(ArrayList.class)) {
            return () -> new ArrayList();
        }
        if (rawType.isAssignableFrom(LinkedHashSet.class)) {
            return () -> new LinkedHashSet();
        }
        if (rawType.isAssignableFrom(TreeSet.class)) {
            return () -> new TreeSet();
        }
        if (rawType.isAssignableFrom(ArrayDeque.class)) {
            return () -> new ArrayDeque();
        }
        return null;
    }

    private static boolean hasStringKeyType(Type mapType) {
        if (!(mapType instanceof ParameterizedType)) {
            return true;
        }
        Type[] typeArguments = ((ParameterizedType)mapType).getActualTypeArguments();
        if (typeArguments.length == 0) {
            return false;
        }
        return GsonTypes.getRawType(typeArguments[0]) == String.class;
    }

    private static ObjectConstructor<? extends Map<? extends Object, Object>> newMapConstructor(Type type, Class<?> rawType) {
        if (rawType.isAssignableFrom(LinkedTreeMap.class) && ConstructorConstructor.hasStringKeyType(type)) {
            return () -> new LinkedTreeMap();
        }
        if (rawType.isAssignableFrom(LinkedHashMap.class)) {
            return () -> new LinkedHashMap();
        }
        if (rawType.isAssignableFrom(TreeMap.class)) {
            return () -> new TreeMap();
        }
        if (rawType.isAssignableFrom(ConcurrentHashMap.class)) {
            return () -> new ConcurrentHashMap();
        }
        if (rawType.isAssignableFrom(ConcurrentSkipListMap.class)) {
            return () -> new ConcurrentSkipListMap();
        }
        return null;
    }

    private <T> ObjectConstructor<T> newUnsafeAllocator(Class<? super T> rawType) {
        if (this.useJdkUnsafe) {
            return () -> {
                try {
                    Object newInstance = UnsafeAllocator.INSTANCE.newInstance(rawType);
                    return newInstance;
                }
                catch (Exception e) {
                    throw new RuntimeException("Unable to create instance of " + rawType + ". Registering an InstanceCreator or a TypeAdapter for this type, or adding a no-args constructor may fix this problem.", e);
                }
            };
        }
        String exceptionMessage = "Unable to create instance of " + rawType + "; usage of JDK Unsafe is disabled. Registering an InstanceCreator or a TypeAdapter for this type, adding a no-args constructor, or enabling usage of JDK Unsafe may fix this problem.";
        if (rawType.getDeclaredConstructors().length == 0) {
            exceptionMessage = exceptionMessage + " Or adjust your R8 configuration to keep the no-args constructor of the class.";
        }
        String exceptionMessageF = exceptionMessage;
        return () -> {
            throw new JsonIOException(exceptionMessageF);
        };
    }

    public String toString() {
        return this.instanceCreators.toString();
    }
}

