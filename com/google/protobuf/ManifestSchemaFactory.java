/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.Android;
import com.google.protobuf.CheckReturnValue;
import com.google.protobuf.ExtensionSchemas;
import com.google.protobuf.GeneratedMessageInfoFactory;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.Internal;
import com.google.protobuf.ListFieldSchemas;
import com.google.protobuf.MapFieldSchemas;
import com.google.protobuf.MessageInfo;
import com.google.protobuf.MessageInfoFactory;
import com.google.protobuf.MessageSchema;
import com.google.protobuf.MessageSetSchema;
import com.google.protobuf.NewInstanceSchemas;
import com.google.protobuf.Schema;
import com.google.protobuf.SchemaFactory;
import com.google.protobuf.SchemaUtil;

@CheckReturnValue
final class ManifestSchemaFactory
implements SchemaFactory {
    private final MessageInfoFactory messageInfoFactory;
    private static final MessageInfoFactory EMPTY_FACTORY = new MessageInfoFactory(){

        @Override
        public boolean isSupported(Class<?> clazz) {
            return false;
        }

        @Override
        public MessageInfo messageInfoFor(Class<?> clazz) {
            throw new IllegalStateException("This should never be called.");
        }
    };

    public ManifestSchemaFactory() {
        this(ManifestSchemaFactory.getDefaultMessageInfoFactory());
    }

    private ManifestSchemaFactory(MessageInfoFactory messageInfoFactory) {
        this.messageInfoFactory = Internal.checkNotNull(messageInfoFactory, "messageInfoFactory");
    }

    @Override
    public <T> Schema<T> createSchema(Class<T> messageType) {
        SchemaUtil.requireGeneratedMessage(messageType);
        MessageInfo messageInfo = this.messageInfoFactory.messageInfoFor(messageType);
        if (messageInfo.isMessageSetWireFormat()) {
            return ManifestSchemaFactory.useLiteRuntime(messageType) ? MessageSetSchema.newSchema(SchemaUtil.unknownFieldSetLiteSchema(), ExtensionSchemas.lite(), messageInfo.getDefaultInstance()) : MessageSetSchema.newSchema(SchemaUtil.unknownFieldSetFullSchema(), ExtensionSchemas.full(), messageInfo.getDefaultInstance());
        }
        return ManifestSchemaFactory.newSchema(messageType, messageInfo);
    }

    private static <T> Schema<T> newSchema(Class<T> messageType, MessageInfo messageInfo) {
        return ManifestSchemaFactory.useLiteRuntime(messageType) ? MessageSchema.newSchema(messageType, messageInfo, NewInstanceSchemas.lite(), ListFieldSchemas.lite(), SchemaUtil.unknownFieldSetLiteSchema(), ManifestSchemaFactory.allowExtensions(messageInfo) ? ExtensionSchemas.lite() : null, MapFieldSchemas.lite()) : MessageSchema.newSchema(messageType, messageInfo, NewInstanceSchemas.full(), ListFieldSchemas.full(), SchemaUtil.unknownFieldSetFullSchema(), ManifestSchemaFactory.allowExtensions(messageInfo) ? ExtensionSchemas.full() : null, MapFieldSchemas.full());
    }

    private static boolean allowExtensions(MessageInfo messageInfo) {
        switch (messageInfo.getSyntax()) {
            case PROTO3: {
                return false;
            }
        }
        return true;
    }

    private static MessageInfoFactory getDefaultMessageInfoFactory() {
        return new CompositeMessageInfoFactory(GeneratedMessageInfoFactory.getInstance(), ManifestSchemaFactory.getDescriptorMessageInfoFactory());
    }

    private static MessageInfoFactory getDescriptorMessageInfoFactory() {
        if (Android.assumeLiteRuntime) {
            return EMPTY_FACTORY;
        }
        try {
            Class<?> clazz = Class.forName("com.google.protobuf.DescriptorMessageInfoFactory");
            return (MessageInfoFactory)clazz.getDeclaredMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
        }
        catch (Exception e) {
            return EMPTY_FACTORY;
        }
    }

    private static boolean useLiteRuntime(Class<?> messageType) {
        return Android.assumeLiteRuntime || GeneratedMessageLite.class.isAssignableFrom(messageType);
    }

    private static class CompositeMessageInfoFactory
    implements MessageInfoFactory {
        private MessageInfoFactory[] factories;

        CompositeMessageInfoFactory(MessageInfoFactory ... factories) {
            this.factories = factories;
        }

        @Override
        public boolean isSupported(Class<?> clazz) {
            for (MessageInfoFactory factory : this.factories) {
                if (!factory.isSupported(clazz)) continue;
                return true;
            }
            return false;
        }

        @Override
        public MessageInfo messageInfoFor(Class<?> clazz) {
            for (MessageInfoFactory factory : this.factories) {
                if (!factory.isSupported(clazz)) continue;
                return factory.messageInfoFor(clazz);
            }
            throw new UnsupportedOperationException("No factory is available for message type: " + clazz.getName());
        }
    }
}

