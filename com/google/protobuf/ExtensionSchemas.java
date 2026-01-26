/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.Android;
import com.google.protobuf.CheckReturnValue;
import com.google.protobuf.ExtensionSchema;
import com.google.protobuf.ExtensionSchemaLite;

@CheckReturnValue
final class ExtensionSchemas {
    private static final ExtensionSchema<?> LITE_SCHEMA = new ExtensionSchemaLite();
    private static final ExtensionSchema<?> FULL_SCHEMA = ExtensionSchemas.loadSchemaForFullRuntime();

    private static ExtensionSchema<?> loadSchemaForFullRuntime() {
        if (Android.assumeLiteRuntime) {
            return null;
        }
        try {
            Class<?> clazz = Class.forName("com.google.protobuf.ExtensionSchemaFull");
            return (ExtensionSchema)clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (Exception e) {
            return null;
        }
    }

    static ExtensionSchema<?> lite() {
        return LITE_SCHEMA;
    }

    static ExtensionSchema<?> full() {
        if (FULL_SCHEMA == null) {
            throw new IllegalStateException("Protobuf runtime is not correctly loaded.");
        }
        return FULL_SCHEMA;
    }

    private ExtensionSchemas() {
    }
}

