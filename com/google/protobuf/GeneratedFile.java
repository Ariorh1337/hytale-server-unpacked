/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.GeneratedMessage;

public abstract class GeneratedFile {
    protected GeneratedFile() {
    }

    protected static void addOptionalExtension(ExtensionRegistry registry, String className, String fieldName) {
        try {
            GeneratedMessage.GeneratedExtension ext = (GeneratedMessage.GeneratedExtension)Class.forName(className).getField(fieldName).get(null);
            registry.add(ext);
        }
        catch (ClassNotFoundException classNotFoundException) {
        }
        catch (NoSuchFieldException noSuchFieldException) {
        }
        catch (IllegalAccessException illegalAccessException) {
            // empty catch block
        }
    }
}

