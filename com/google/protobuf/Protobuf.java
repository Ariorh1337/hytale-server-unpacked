/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.CanIgnoreReturnValue;
import com.google.protobuf.CheckReturnValue;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.Internal;
import com.google.protobuf.ManifestSchemaFactory;
import com.google.protobuf.MessageSchema;
import com.google.protobuf.Reader;
import com.google.protobuf.Schema;
import com.google.protobuf.SchemaFactory;
import com.google.protobuf.Writer;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@CheckReturnValue
final class Protobuf {
    private static final Protobuf INSTANCE = new Protobuf();
    private final SchemaFactory schemaFactory;
    private final ConcurrentMap<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap();

    public static Protobuf getInstance() {
        return INSTANCE;
    }

    public <T> void writeTo(T message, Writer writer) throws IOException {
        this.schemaFor(message).writeTo(message, writer);
    }

    public <T> void mergeFrom(T message, Reader reader) throws IOException {
        this.mergeFrom(message, reader, ExtensionRegistryLite.getEmptyRegistry());
    }

    public <T> void mergeFrom(T message, Reader reader, ExtensionRegistryLite extensionRegistry) throws IOException {
        this.schemaFor(message).mergeFrom(message, reader, extensionRegistry);
    }

    public <T> void makeImmutable(T message) {
        this.schemaFor(message).makeImmutable(message);
    }

    <T> boolean isInitialized(T message) {
        return this.schemaFor(message).isInitialized(message);
    }

    public <T> Schema<T> schemaFor(Class<T> messageType) {
        Schema<?> previous;
        Internal.checkNotNull(messageType, "messageType");
        Schema<Object> schema = (Schema<T>)this.schemaCache.get(messageType);
        if (schema == null && (previous = this.registerSchema(messageType, schema = this.schemaFactory.createSchema(messageType))) != null) {
            schema = previous;
        }
        return schema;
    }

    public <T> Schema<T> schemaFor(T message) {
        return this.schemaFor((T)message.getClass());
    }

    public Schema<?> registerSchema(Class<?> messageType, Schema<?> schema) {
        Internal.checkNotNull(messageType, "messageType");
        Internal.checkNotNull(schema, "schema");
        return this.schemaCache.putIfAbsent(messageType, schema);
    }

    @CanIgnoreReturnValue
    public Schema<?> registerSchemaOverride(Class<?> messageType, Schema<?> schema) {
        Internal.checkNotNull(messageType, "messageType");
        Internal.checkNotNull(schema, "schema");
        return this.schemaCache.put(messageType, schema);
    }

    private Protobuf() {
        this.schemaFactory = new ManifestSchemaFactory();
    }

    int getTotalSchemaSize() {
        int result = 0;
        for (Schema schema : this.schemaCache.values()) {
            if (!(schema instanceof MessageSchema)) continue;
            result += ((MessageSchema)schema).getSchemaSize();
        }
        return result;
    }
}

