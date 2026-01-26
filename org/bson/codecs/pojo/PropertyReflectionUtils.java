/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.pojo;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class PropertyReflectionUtils {
    private static final String IS_PREFIX = "is";
    private static final String GET_PREFIX = "get";
    private static final String SET_PREFIX = "set";

    private PropertyReflectionUtils() {
    }

    static boolean isGetter(Method method) {
        if (method.getParameterTypes().length > 0) {
            return false;
        }
        if (method.getName().startsWith(GET_PREFIX) && method.getName().length() > GET_PREFIX.length()) {
            return Character.isUpperCase(method.getName().charAt(GET_PREFIX.length()));
        }
        if (method.getName().startsWith(IS_PREFIX) && method.getName().length() > IS_PREFIX.length()) {
            return Character.isUpperCase(method.getName().charAt(IS_PREFIX.length()));
        }
        return false;
    }

    static boolean isSetter(Method method) {
        if (method.getName().startsWith(SET_PREFIX) && method.getName().length() > SET_PREFIX.length() && method.getParameterTypes().length == 1) {
            return Character.isUpperCase(method.getName().charAt(SET_PREFIX.length()));
        }
        return false;
    }

    static String toPropertyName(Method method) {
        String name;
        String propertyName = name.substring((name = method.getName()).startsWith(IS_PREFIX) ? 2 : 3, name.length());
        char[] chars = propertyName.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    static PropertyMethods getPropertyMethods(Class<?> clazz) {
        ArrayList<Method> setters = new ArrayList<Method>();
        ArrayList<Method> getters = new ArrayList<Method>();
        for (Class<?> clazz2 : clazz.getInterfaces()) {
            for (Method method : clazz2.getDeclaredMethods()) {
                if (!method.isDefault()) continue;
                PropertyReflectionUtils.verifyAddMethodToList(method, getters, setters);
            }
        }
        for (GenericDeclaration genericDeclaration : clazz.getDeclaredMethods()) {
            PropertyReflectionUtils.verifyAddMethodToList((Method)genericDeclaration, getters, setters);
        }
        return new PropertyMethods(getters, setters);
    }

    private static void verifyAddMethodToList(Method method, List<Method> getters, List<Method> setters) {
        if (Modifier.isPublic(method.getModifiers()) && !method.isBridge()) {
            if (PropertyReflectionUtils.isGetter(method)) {
                getters.add(method);
            } else if (PropertyReflectionUtils.isSetter(method)) {
                setters.add(method);
            }
        }
    }

    static class PropertyMethods {
        private final Collection<Method> getterMethods;
        private final Collection<Method> setterMethods;

        PropertyMethods(Collection<Method> getterMethods, Collection<Method> setterMethods) {
            this.getterMethods = getterMethods;
            this.setterMethods = setterMethods;
        }

        Collection<Method> getGetterMethods() {
            return this.getterMethods;
        }

        Collection<Method> getSetterMethods() {
            return this.setterMethods;
        }
    }
}

