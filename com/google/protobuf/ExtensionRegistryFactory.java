/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.Android;
import com.google.protobuf.ExtensionRegistryLite;

final class ExtensionRegistryFactory {
    static final String FULL_REGISTRY_CLASS_NAME = "com.google.protobuf.ExtensionRegistry";
    static final Class<?> EXTENSION_REGISTRY_CLASS = ExtensionRegistryFactory.reflectExtensionRegistry();

    ExtensionRegistryFactory() {
    }

    static Class<?> reflectExtensionRegistry() {
        try {
            return Class.forName(FULL_REGISTRY_CLASS_NAME);
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static ExtensionRegistryLite create() {
        ExtensionRegistryLite result = ExtensionRegistryFactory.invokeSubclassFactory("newInstance");
        return result != null ? result : new ExtensionRegistryLite();
    }

    public static ExtensionRegistryLite createEmpty() {
        ExtensionRegistryLite result = ExtensionRegistryFactory.invokeSubclassFactory("getEmptyRegistry");
        return result != null ? result : ExtensionRegistryLite.EMPTY_REGISTRY_LITE;
    }

    static boolean isFullRegistry(ExtensionRegistryLite registry) {
        return !Android.assumeLiteRuntime && EXTENSION_REGISTRY_CLASS != null && EXTENSION_REGISTRY_CLASS.isAssignableFrom(registry.getClass());
    }

    private static final ExtensionRegistryLite invokeSubclassFactory(String methodName) {
        if (EXTENSION_REGISTRY_CLASS == null) {
            return null;
        }
        try {
            return (ExtensionRegistryLite)EXTENSION_REGISTRY_CLASS.getDeclaredMethod(methodName, new Class[0]).invoke(null, new Object[0]);
        }
        catch (Exception e) {
            return null;
        }
    }
}

