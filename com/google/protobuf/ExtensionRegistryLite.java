/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.Android;
import com.google.protobuf.ExtensionLite;
import com.google.protobuf.ExtensionRegistryFactory;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.MessageLite;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ExtensionRegistryLite {
    private static volatile boolean eagerlyParseMessageSets = false;
    static final String EXTENSION_CLASS_NAME = "com.google.protobuf.Extension";
    private static volatile ExtensionRegistryLite emptyRegistry;
    static final ExtensionRegistryLite EMPTY_REGISTRY_LITE;
    private final Map<ObjectIntPair, GeneratedMessageLite.GeneratedExtension<?, ?>> extensionsByNumber;

    public static boolean isEagerlyParseMessageSets() {
        return eagerlyParseMessageSets;
    }

    public static void setEagerlyParseMessageSets(boolean isEagerlyParse) {
        eagerlyParseMessageSets = isEagerlyParse;
    }

    public static ExtensionRegistryLite newInstance() {
        return Android.assumeLiteRuntime ? new ExtensionRegistryLite() : ExtensionRegistryFactory.create();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static ExtensionRegistryLite getEmptyRegistry() {
        if (Android.assumeLiteRuntime) {
            return EMPTY_REGISTRY_LITE;
        }
        ExtensionRegistryLite result = emptyRegistry;
        if (result != null) return result;
        Class<ExtensionRegistryLite> clazz = ExtensionRegistryLite.class;
        synchronized (ExtensionRegistryLite.class) {
            result = emptyRegistry;
            if (result != null) return result;
            emptyRegistry = result = ExtensionRegistryFactory.createEmpty();
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return result;
        }
    }

    public ExtensionRegistryLite getUnmodifiable() {
        return new ExtensionRegistryLite(this);
    }

    public <ContainingType extends MessageLite> GeneratedMessageLite.GeneratedExtension<ContainingType, ?> findLiteExtensionByNumber(ContainingType containingTypeDefaultInstance, int fieldNumber) {
        return this.extensionsByNumber.get(new ObjectIntPair(containingTypeDefaultInstance, fieldNumber));
    }

    public final void add(GeneratedMessageLite.GeneratedExtension<?, ?> extension) {
        this.extensionsByNumber.put(new ObjectIntPair(extension.getContainingTypeDefaultInstance(), extension.getNumber()), extension);
    }

    public final void add(ExtensionLite<?, ?> extension) {
        if (extension instanceof GeneratedMessageLite.GeneratedExtension) {
            this.add((GeneratedMessageLite.GeneratedExtension)extension);
        }
        if (!Android.assumeLiteRuntime && ExtensionRegistryFactory.isFullRegistry(this)) {
            try {
                this.getClass().getMethod("add", ExtensionClassHolder.INSTANCE).invoke((Object)this, extension);
            }
            catch (Exception e) {
                throw new IllegalArgumentException(String.format("Could not invoke ExtensionRegistry#add for %s", extension), e);
            }
        }
    }

    ExtensionRegistryLite() {
        this.extensionsByNumber = new HashMap();
    }

    ExtensionRegistryLite(ExtensionRegistryLite other) {
        this.extensionsByNumber = other == EMPTY_REGISTRY_LITE ? Collections.emptyMap() : Collections.unmodifiableMap(other.extensionsByNumber);
    }

    ExtensionRegistryLite(boolean empty) {
        this.extensionsByNumber = Collections.emptyMap();
    }

    static {
        EMPTY_REGISTRY_LITE = new ExtensionRegistryLite(true);
    }

    private static final class ObjectIntPair {
        private final Object object;
        private final int number;

        ObjectIntPair(Object object, int number) {
            this.object = object;
            this.number = number;
        }

        public int hashCode() {
            return System.identityHashCode(this.object) * 65535 + this.number;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof ObjectIntPair)) {
                return false;
            }
            ObjectIntPair other = (ObjectIntPair)obj;
            return this.object == other.object && this.number == other.number;
        }
    }

    private static class ExtensionClassHolder {
        static final Class<?> INSTANCE = ExtensionClassHolder.resolveExtensionClass();

        private ExtensionClassHolder() {
        }

        static Class<?> resolveExtensionClass() {
            try {
                return Class.forName(ExtensionRegistryLite.EXTENSION_CLASS_NAME);
            }
            catch (ClassNotFoundException e) {
                return null;
            }
        }
    }
}

