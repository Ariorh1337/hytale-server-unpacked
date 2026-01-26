/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal;

import io.netty.util.internal.PlatformDependent0;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public final class ReflectionUtil {
    private ReflectionUtil() {
    }

    public static Throwable trySetAccessible(AccessibleObject object, boolean checkAccessible) {
        if (checkAccessible && !PlatformDependent0.isExplicitTryReflectionSetAccessible()) {
            return new UnsupportedOperationException("Reflective setAccessible(true) disabled");
        }
        try {
            object.setAccessible(true);
            return null;
        }
        catch (SecurityException e) {
            return e;
        }
        catch (RuntimeException e) {
            return ReflectionUtil.handleInaccessibleObjectException(e);
        }
    }

    private static RuntimeException handleInaccessibleObjectException(RuntimeException e) {
        if ("java.lang.reflect.InaccessibleObjectException".equals(e.getClass().getName())) {
            return e;
        }
        throw e;
    }

    private static Class<?> fail(Class<?> type, String typeParamName) {
        throw new IllegalStateException("cannot determine the type of the type parameter '" + typeParamName + "': " + type);
    }

    public static Class<?> resolveTypeParameter(Object object, Class<?> parametrizedSuperclass, String typeParamName) {
        Class<?> thisClass;
        Class<?> currentClass = thisClass = object.getClass();
        while (true) {
            if (currentClass.getSuperclass() == parametrizedSuperclass) {
                int typeParamIndex = -1;
                TypeVariable<Class<?>>[] typeParams = currentClass.getSuperclass().getTypeParameters();
                for (int i = 0; i < typeParams.length; ++i) {
                    if (!typeParamName.equals(typeParams[i].getName())) continue;
                    typeParamIndex = i;
                    break;
                }
                if (typeParamIndex < 0) {
                    throw new IllegalStateException("unknown type parameter '" + typeParamName + "': " + parametrizedSuperclass);
                }
                Type genericSuperType = currentClass.getGenericSuperclass();
                if (!(genericSuperType instanceof ParameterizedType)) {
                    return Object.class;
                }
                Type[] actualTypeParams = ((ParameterizedType)genericSuperType).getActualTypeArguments();
                Type actualTypeParam = actualTypeParams[typeParamIndex];
                if (actualTypeParam instanceof ParameterizedType) {
                    actualTypeParam = ((ParameterizedType)actualTypeParam).getRawType();
                }
                if (actualTypeParam instanceof Class) {
                    return (Class)actualTypeParam;
                }
                if (actualTypeParam instanceof GenericArrayType) {
                    Type componentType = ((GenericArrayType)actualTypeParam).getGenericComponentType();
                    if (componentType instanceof ParameterizedType) {
                        componentType = ((ParameterizedType)componentType).getRawType();
                    }
                    if (componentType instanceof Class) {
                        return Array.newInstance((Class)componentType, 0).getClass();
                    }
                }
                if (actualTypeParam instanceof TypeVariable) {
                    TypeVariable v = (TypeVariable)actualTypeParam;
                    if (!(v.getGenericDeclaration() instanceof Class)) {
                        return Object.class;
                    }
                    currentClass = thisClass;
                    parametrizedSuperclass = (Class)v.getGenericDeclaration();
                    typeParamName = v.getName();
                    if (parametrizedSuperclass.isAssignableFrom(thisClass)) continue;
                    return Object.class;
                }
                return ReflectionUtil.fail(thisClass, typeParamName);
            }
            if ((currentClass = currentClass.getSuperclass()) == null) break;
        }
        return ReflectionUtil.fail(thisClass, typeParamName);
    }
}

