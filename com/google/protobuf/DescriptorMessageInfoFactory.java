/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.FieldInfo;
import com.google.protobuf.FieldType;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Internal;
import com.google.protobuf.Message;
import com.google.protobuf.MessageInfo;
import com.google.protobuf.MessageInfoFactory;
import com.google.protobuf.OneofInfo;
import com.google.protobuf.ProtoSyntax;
import com.google.protobuf.SchemaUtil;
import com.google.protobuf.StructuralMessageInfo;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

final class DescriptorMessageInfoFactory
implements MessageInfoFactory {
    private static final String GET_DEFAULT_INSTANCE_METHOD_NAME = "getDefaultInstance";
    private static final DescriptorMessageInfoFactory instance = new DescriptorMessageInfoFactory();
    private static final Set<String> specialFieldNames = new HashSet<String>(Arrays.asList("Class", "DefaultInstanceForType", "ParserForType", "SerializedSize", "AllFields", "DescriptorForType", "InitializationErrorString", "UnknownFields", "CachedSize"));
    private static IsInitializedCheckAnalyzer isInitializedCheckAnalyzer = new IsInitializedCheckAnalyzer();

    private DescriptorMessageInfoFactory() {
    }

    public static DescriptorMessageInfoFactory getInstance() {
        return instance;
    }

    @Override
    public boolean isSupported(Class<?> messageType) {
        return GeneratedMessage.class.isAssignableFrom(messageType);
    }

    @Override
    public MessageInfo messageInfoFor(Class<?> messageType) {
        if (!GeneratedMessage.class.isAssignableFrom(messageType)) {
            throw new IllegalArgumentException("Unsupported message type: " + messageType.getName());
        }
        return DescriptorMessageInfoFactory.convert(messageType, DescriptorMessageInfoFactory.descriptorForType(messageType));
    }

    private static Message getDefaultInstance(Class<?> messageType) {
        try {
            Method method = messageType.getDeclaredMethod(GET_DEFAULT_INSTANCE_METHOD_NAME, new Class[0]);
            return (Message)method.invoke(null, new Object[0]);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Unable to get default instance for message class " + messageType.getName(), e);
        }
    }

    private static Descriptors.Descriptor descriptorForType(Class<?> messageType) {
        return DescriptorMessageInfoFactory.getDefaultInstance(messageType).getDescriptorForType();
    }

    private static ProtoSyntax convertSyntax(DescriptorProtos.Edition edition) {
        switch (edition) {
            case EDITION_PROTO2: {
                return ProtoSyntax.PROTO2;
            }
            case EDITION_PROTO3: {
                return ProtoSyntax.PROTO3;
            }
        }
        return ProtoSyntax.EDITIONS;
    }

    private static MessageInfo convert(Class<?> messageType, Descriptors.Descriptor messageDescriptor) {
        List<Descriptors.FieldDescriptor> fieldDescriptors = messageDescriptor.getFields();
        StructuralMessageInfo.Builder builder = StructuralMessageInfo.newBuilder(fieldDescriptors.size());
        builder.withDefaultInstance(DescriptorMessageInfoFactory.getDefaultInstance(messageType));
        builder.withSyntax(DescriptorMessageInfoFactory.convertSyntax(messageDescriptor.getFile().getEdition()));
        builder.withMessageSetWireFormat(messageDescriptor.getOptions().getMessageSetWireFormat());
        OneofState oneofState = new OneofState();
        int bitFieldIndex = 0;
        int presenceMask = 1;
        Field bitField = null;
        for (int i = 0; i < fieldDescriptors.size(); ++i) {
            final Descriptors.FieldDescriptor fd = fieldDescriptors.get(i);
            boolean enforceUtf8 = fd.needsUtf8Check();
            Internal.EnumVerifier enumVerifier = null;
            if (fd.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM && fd.legacyEnumFieldTreatedAsClosed()) {
                enumVerifier = new Internal.EnumVerifier(){

                    @Override
                    public boolean isInRange(int number) {
                        return fd.getEnumType().findValueByNumber(number) != null;
                    }
                };
            }
            if (fd.getRealContainingOneof() != null) {
                builder.withField(DescriptorMessageInfoFactory.buildOneofMember(messageType, fd, oneofState, enforceUtf8, enumVerifier));
                continue;
            }
            Field field = DescriptorMessageInfoFactory.field(messageType, fd);
            int number = fd.getNumber();
            FieldType type = DescriptorMessageInfoFactory.getFieldType(fd);
            if (!fd.hasPresence()) {
                FieldInfo fieldImplicitPresence;
                if (fd.isMapField()) {
                    final Descriptors.FieldDescriptor valueField = fd.getMessageType().findFieldByNumber(2);
                    if (valueField.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM && valueField.legacyEnumFieldTreatedAsClosed()) {
                        enumVerifier = new Internal.EnumVerifier(){

                            @Override
                            public boolean isInRange(int number) {
                                return valueField.getEnumType().findValueByNumber(number) != null;
                            }
                        };
                    }
                    fieldImplicitPresence = FieldInfo.forMapField(field, number, SchemaUtil.getMapDefaultEntry(messageType, fd.getName()), enumVerifier);
                } else {
                    fieldImplicitPresence = fd.isRepeated() && fd.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE ? FieldInfo.forRepeatedMessageField(field, number, type, DescriptorMessageInfoFactory.getTypeForRepeatedMessageField(messageType, fd)) : (fd.isPacked() ? (enumVerifier != null ? FieldInfo.forPackedFieldWithEnumVerifier(field, number, type, enumVerifier, DescriptorMessageInfoFactory.cachedSizeField(messageType, fd)) : FieldInfo.forPackedField(field, number, type, DescriptorMessageInfoFactory.cachedSizeField(messageType, fd))) : (enumVerifier != null ? FieldInfo.forFieldWithEnumVerifier(field, number, type, enumVerifier) : FieldInfo.forField(field, number, type, enforceUtf8)));
                }
                builder.withField(fieldImplicitPresence);
                continue;
            }
            if (bitField == null) {
                bitField = DescriptorMessageInfoFactory.bitField(messageType, bitFieldIndex);
            }
            FieldInfo fieldExplicitPresence = fd.isRequired() ? FieldInfo.forLegacyRequiredField(field, number, type, bitField, presenceMask, enforceUtf8, enumVerifier) : FieldInfo.forExplicitPresenceField(field, number, type, bitField, presenceMask, enforceUtf8, enumVerifier);
            builder.withField(fieldExplicitPresence);
            if ((presenceMask <<= 1) != 0) continue;
            bitField = null;
            presenceMask = 1;
            ++bitFieldIndex;
        }
        ArrayList<Integer> fieldsToCheckIsInitialized = new ArrayList<Integer>();
        for (int i = 0; i < fieldDescriptors.size(); ++i) {
            Descriptors.FieldDescriptor fd = fieldDescriptors.get(i);
            if (!fd.isRequired() && (fd.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE || !DescriptorMessageInfoFactory.needsIsInitializedCheck(fd.getMessageType()))) continue;
            fieldsToCheckIsInitialized.add(fd.getNumber());
        }
        int[] numbers = new int[fieldsToCheckIsInitialized.size()];
        for (int i = 0; i < fieldsToCheckIsInitialized.size(); ++i) {
            numbers[i] = (Integer)fieldsToCheckIsInitialized.get(i);
        }
        if (numbers.length > 0) {
            builder.withCheckInitialized(numbers);
        }
        return builder.build();
    }

    private static boolean needsIsInitializedCheck(Descriptors.Descriptor descriptor) {
        return isInitializedCheckAnalyzer.needsIsInitializedCheck(descriptor);
    }

    private static FieldInfo buildOneofMember(Class<?> messageType, Descriptors.FieldDescriptor fd, OneofState oneofState, boolean enforceUtf8, Internal.EnumVerifier enumVerifier) {
        OneofInfo oneof = oneofState.getOneof(messageType, fd.getContainingOneof());
        FieldType type = DescriptorMessageInfoFactory.getFieldType(fd);
        Class<?> oneofStoredType = DescriptorMessageInfoFactory.getOneofStoredType(messageType, fd, type);
        return FieldInfo.forOneofMemberField(fd.getNumber(), type, oneof, oneofStoredType, enforceUtf8, enumVerifier);
    }

    private static Class<?> getOneofStoredType(Class<?> messageType, Descriptors.FieldDescriptor fd, FieldType type) {
        switch (type.getJavaType()) {
            case BOOLEAN: {
                return Boolean.class;
            }
            case BYTE_STRING: {
                return ByteString.class;
            }
            case DOUBLE: {
                return Double.class;
            }
            case FLOAT: {
                return Float.class;
            }
            case ENUM: 
            case INT: {
                return Integer.class;
            }
            case LONG: {
                return Long.class;
            }
            case STRING: {
                return String.class;
            }
            case MESSAGE: {
                return DescriptorMessageInfoFactory.getOneofStoredTypeForMessage(messageType, fd);
            }
        }
        throw new IllegalArgumentException("Invalid type for oneof: " + (Object)((Object)type));
    }

    private static FieldType getFieldType(Descriptors.FieldDescriptor fd) {
        switch (fd.getType()) {
            case BOOL: {
                if (!fd.isRepeated()) {
                    return FieldType.BOOL;
                }
                return fd.isPacked() ? FieldType.BOOL_LIST_PACKED : FieldType.BOOL_LIST;
            }
            case BYTES: {
                return fd.isRepeated() ? FieldType.BYTES_LIST : FieldType.BYTES;
            }
            case DOUBLE: {
                if (!fd.isRepeated()) {
                    return FieldType.DOUBLE;
                }
                return fd.isPacked() ? FieldType.DOUBLE_LIST_PACKED : FieldType.DOUBLE_LIST;
            }
            case ENUM: {
                if (!fd.isRepeated()) {
                    return FieldType.ENUM;
                }
                return fd.isPacked() ? FieldType.ENUM_LIST_PACKED : FieldType.ENUM_LIST;
            }
            case FIXED32: {
                if (!fd.isRepeated()) {
                    return FieldType.FIXED32;
                }
                return fd.isPacked() ? FieldType.FIXED32_LIST_PACKED : FieldType.FIXED32_LIST;
            }
            case FIXED64: {
                if (!fd.isRepeated()) {
                    return FieldType.FIXED64;
                }
                return fd.isPacked() ? FieldType.FIXED64_LIST_PACKED : FieldType.FIXED64_LIST;
            }
            case FLOAT: {
                if (!fd.isRepeated()) {
                    return FieldType.FLOAT;
                }
                return fd.isPacked() ? FieldType.FLOAT_LIST_PACKED : FieldType.FLOAT_LIST;
            }
            case GROUP: {
                return fd.isRepeated() ? FieldType.GROUP_LIST : FieldType.GROUP;
            }
            case INT32: {
                if (!fd.isRepeated()) {
                    return FieldType.INT32;
                }
                return fd.isPacked() ? FieldType.INT32_LIST_PACKED : FieldType.INT32_LIST;
            }
            case INT64: {
                if (!fd.isRepeated()) {
                    return FieldType.INT64;
                }
                return fd.isPacked() ? FieldType.INT64_LIST_PACKED : FieldType.INT64_LIST;
            }
            case MESSAGE: {
                if (fd.isMapField()) {
                    return FieldType.MAP;
                }
                return fd.isRepeated() ? FieldType.MESSAGE_LIST : FieldType.MESSAGE;
            }
            case SFIXED32: {
                if (!fd.isRepeated()) {
                    return FieldType.SFIXED32;
                }
                return fd.isPacked() ? FieldType.SFIXED32_LIST_PACKED : FieldType.SFIXED32_LIST;
            }
            case SFIXED64: {
                if (!fd.isRepeated()) {
                    return FieldType.SFIXED64;
                }
                return fd.isPacked() ? FieldType.SFIXED64_LIST_PACKED : FieldType.SFIXED64_LIST;
            }
            case SINT32: {
                if (!fd.isRepeated()) {
                    return FieldType.SINT32;
                }
                return fd.isPacked() ? FieldType.SINT32_LIST_PACKED : FieldType.SINT32_LIST;
            }
            case SINT64: {
                if (!fd.isRepeated()) {
                    return FieldType.SINT64;
                }
                return fd.isPacked() ? FieldType.SINT64_LIST_PACKED : FieldType.SINT64_LIST;
            }
            case STRING: {
                return fd.isRepeated() ? FieldType.STRING_LIST : FieldType.STRING;
            }
            case UINT32: {
                if (!fd.isRepeated()) {
                    return FieldType.UINT32;
                }
                return fd.isPacked() ? FieldType.UINT32_LIST_PACKED : FieldType.UINT32_LIST;
            }
            case UINT64: {
                if (!fd.isRepeated()) {
                    return FieldType.UINT64;
                }
                return fd.isPacked() ? FieldType.UINT64_LIST_PACKED : FieldType.UINT64_LIST;
            }
        }
        throw new IllegalArgumentException("Unsupported field type: " + (Object)((Object)fd.getType()));
    }

    private static Field bitField(Class<?> messageType, int index) {
        return DescriptorMessageInfoFactory.field(messageType, "bitField" + index + "_");
    }

    private static Field field(Class<?> messageType, Descriptors.FieldDescriptor fd) {
        return DescriptorMessageInfoFactory.field(messageType, DescriptorMessageInfoFactory.getFieldName(fd));
    }

    private static Field cachedSizeField(Class<?> messageType, Descriptors.FieldDescriptor fd) {
        return DescriptorMessageInfoFactory.field(messageType, DescriptorMessageInfoFactory.getCachedSizeFieldName(fd));
    }

    private static Field field(Class<?> messageType, String fieldName) {
        try {
            return messageType.getDeclaredField(fieldName);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Unable to find field " + fieldName + " in message class " + messageType.getName(), e);
        }
    }

    static String getFieldName(Descriptors.FieldDescriptor fd) {
        String name = fd.getType() == Descriptors.FieldDescriptor.Type.GROUP ? fd.getMessageType().getName() : fd.getName();
        String upperCamelCaseName = DescriptorMessageInfoFactory.snakeCaseToUpperCamelCase(name);
        String suffix = specialFieldNames.contains(upperCamelCaseName) ? "__" : "_";
        return DescriptorMessageInfoFactory.snakeCaseToLowerCamelCase(name) + suffix;
    }

    private static String getCachedSizeFieldName(Descriptors.FieldDescriptor fd) {
        return DescriptorMessageInfoFactory.snakeCaseToLowerCamelCase(fd.getName()) + "MemoizedSerializedSize";
    }

    private static String snakeCaseToLowerCamelCase(String snakeCase) {
        return DescriptorMessageInfoFactory.snakeCaseToCamelCase(snakeCase, false);
    }

    private static String snakeCaseToUpperCamelCase(String snakeCase) {
        return DescriptorMessageInfoFactory.snakeCaseToCamelCase(snakeCase, true);
    }

    private static String snakeCaseToCamelCase(String snakeCase, boolean capFirst) {
        StringBuilder sb = new StringBuilder(snakeCase.length() + 1);
        boolean capNext = capFirst;
        for (int ctr = 0; ctr < snakeCase.length(); ++ctr) {
            char next = snakeCase.charAt(ctr);
            if (next == '_') {
                capNext = true;
                continue;
            }
            if (Character.isDigit(next)) {
                sb.append(next);
                capNext = true;
                continue;
            }
            if (capNext) {
                sb.append(Character.toUpperCase(next));
                capNext = false;
                continue;
            }
            if (ctr == 0) {
                sb.append(Character.toLowerCase(next));
                continue;
            }
            sb.append(next);
        }
        return sb.toString();
    }

    private static Class<?> getOneofStoredTypeForMessage(Class<?> messageType, Descriptors.FieldDescriptor fd) {
        try {
            String name = fd.getType() == Descriptors.FieldDescriptor.Type.GROUP ? fd.getMessageType().getName() : fd.getName();
            Method getter = messageType.getDeclaredMethod(DescriptorMessageInfoFactory.getterForField(name), new Class[0]);
            return getter.getReturnType();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<?> getTypeForRepeatedMessageField(Class<?> messageType, Descriptors.FieldDescriptor fd) {
        try {
            String name = fd.getType() == Descriptors.FieldDescriptor.Type.GROUP ? fd.getMessageType().getName() : fd.getName();
            Method getter = messageType.getDeclaredMethod(DescriptorMessageInfoFactory.getterForField(name), Integer.TYPE);
            return getter.getReturnType();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getterForField(String snakeCase) {
        String camelCase = DescriptorMessageInfoFactory.snakeCaseToLowerCamelCase(snakeCase);
        StringBuilder builder = new StringBuilder("get");
        builder.append(Character.toUpperCase(camelCase.charAt(0)));
        builder.append(camelCase.substring(1, camelCase.length()));
        return builder.toString();
    }

    private static final class OneofState {
        private OneofInfo[] oneofs = new OneofInfo[2];

        private OneofState() {
        }

        OneofInfo getOneof(Class<?> messageType, Descriptors.OneofDescriptor desc) {
            OneofInfo info;
            int index = desc.getIndex();
            if (index >= this.oneofs.length) {
                this.oneofs = Arrays.copyOf(this.oneofs, index * 2);
            }
            if ((info = this.oneofs[index]) == null) {
                this.oneofs[index] = info = OneofState.newInfo(messageType, desc);
            }
            return info;
        }

        private static OneofInfo newInfo(Class<?> messageType, Descriptors.OneofDescriptor desc) {
            String camelCase = DescriptorMessageInfoFactory.snakeCaseToLowerCamelCase(desc.getName());
            String valueFieldName = camelCase + "_";
            String caseFieldName = camelCase + "Case_";
            return new OneofInfo(desc.getIndex(), DescriptorMessageInfoFactory.field(messageType, caseFieldName), DescriptorMessageInfoFactory.field(messageType, valueFieldName));
        }
    }

    static class IsInitializedCheckAnalyzer {
        private final Map<Descriptors.Descriptor, Boolean> resultCache = new ConcurrentHashMap<Descriptors.Descriptor, Boolean>();
        private int index = 0;
        private final Stack<Node> stack = new Stack();
        private final Map<Descriptors.Descriptor, Node> nodeCache = new HashMap<Descriptors.Descriptor, Node>();

        IsInitializedCheckAnalyzer() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean needsIsInitializedCheck(Descriptors.Descriptor descriptor) {
            Boolean cachedValue = this.resultCache.get(descriptor);
            if (cachedValue != null) {
                return cachedValue;
            }
            IsInitializedCheckAnalyzer isInitializedCheckAnalyzer = this;
            synchronized (isInitializedCheckAnalyzer) {
                cachedValue = this.resultCache.get(descriptor);
                if (cachedValue != null) {
                    return cachedValue;
                }
                return this.dfs((Descriptors.Descriptor)descriptor).component.needsIsInitializedCheck;
            }
        }

        private Node dfs(Descriptors.Descriptor descriptor) {
            Node result = new Node(descriptor, this.index++);
            this.stack.push(result);
            this.nodeCache.put(descriptor, result);
            for (Descriptors.FieldDescriptor field : descriptor.getFields()) {
                if (field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) continue;
                Node child = this.nodeCache.get(field.getMessageType());
                if (child == null) {
                    child = this.dfs(field.getMessageType());
                    result.lowLink = Math.min(result.lowLink, child.lowLink);
                    continue;
                }
                if (child.component != null) continue;
                result.lowLink = Math.min(result.lowLink, child.lowLink);
            }
            if (result.index == result.lowLink) {
                Node node;
                StronglyConnectedComponent component = new StronglyConnectedComponent();
                do {
                    node = this.stack.pop();
                    node.component = component;
                    component.messages.add(node.descriptor);
                } while (node != result);
                this.analyze(component);
            }
            return result;
        }

        private void analyze(StronglyConnectedComponent component) {
            boolean needsIsInitializedCheck = false;
            block0: for (Descriptors.Descriptor descriptor : component.messages) {
                if (descriptor.isExtendable()) {
                    needsIsInitializedCheck = true;
                    break;
                }
                for (Descriptors.FieldDescriptor field : descriptor.getFields()) {
                    if (field.isRequired()) {
                        needsIsInitializedCheck = true;
                        break block0;
                    }
                    if (field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) continue;
                    Node node = this.nodeCache.get(field.getMessageType());
                    if (node.component == component || !node.component.needsIsInitializedCheck) continue;
                    needsIsInitializedCheck = true;
                    break block0;
                }
            }
            component.needsIsInitializedCheck = needsIsInitializedCheck;
            for (Descriptors.Descriptor descriptor : component.messages) {
                this.resultCache.put(descriptor, component.needsIsInitializedCheck);
            }
        }

        private static class Node {
            final Descriptors.Descriptor descriptor;
            final int index;
            int lowLink;
            StronglyConnectedComponent component;

            Node(Descriptors.Descriptor descriptor, int index) {
                this.descriptor = descriptor;
                this.index = index;
                this.lowLink = index;
                this.component = null;
            }
        }

        private static class StronglyConnectedComponent {
            final List<Descriptors.Descriptor> messages = new ArrayList<Descriptors.Descriptor>();
            boolean needsIsInitializedCheck = false;

            private StronglyConnectedComponent() {
            }
        }
    }
}

