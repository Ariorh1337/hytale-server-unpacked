/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.Android;
import com.google.protobuf.CheckReturnValue;
import com.google.protobuf.MapFieldSchema;
import com.google.protobuf.MapFieldSchemaLite;

@CheckReturnValue
final class MapFieldSchemas {
    private static final MapFieldSchema FULL_SCHEMA = MapFieldSchemas.loadSchemaForFullRuntime();
    private static final MapFieldSchema LITE_SCHEMA = new MapFieldSchemaLite();

    static MapFieldSchema full() {
        return FULL_SCHEMA;
    }

    static MapFieldSchema lite() {
        return LITE_SCHEMA;
    }

    private static MapFieldSchema loadSchemaForFullRuntime() {
        if (Android.assumeLiteRuntime) {
            return null;
        }
        try {
            Class<?> clazz = Class.forName("com.google.protobuf.MapFieldSchemaFull");
            return (MapFieldSchema)clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (Exception e) {
            return null;
        }
    }

    private MapFieldSchemas() {
    }
}

