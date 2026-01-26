/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.Android;
import com.google.protobuf.CheckReturnValue;
import com.google.protobuf.ListFieldSchema;
import com.google.protobuf.ListFieldSchemaLite;

@CheckReturnValue
final class ListFieldSchemas {
    private static final ListFieldSchema FULL_SCHEMA = ListFieldSchemas.loadSchemaForFullRuntime();
    private static final ListFieldSchema LITE_SCHEMA = new ListFieldSchemaLite();

    static ListFieldSchema full() {
        return FULL_SCHEMA;
    }

    static ListFieldSchema lite() {
        return LITE_SCHEMA;
    }

    private static ListFieldSchema loadSchemaForFullRuntime() {
        if (Android.assumeLiteRuntime) {
            return null;
        }
        try {
            Class<?> clazz = Class.forName("com.google.protobuf.ListFieldSchemaFull");
            return (ListFieldSchema)clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (Exception e) {
            return null;
        }
    }

    private ListFieldSchemas() {
    }
}

