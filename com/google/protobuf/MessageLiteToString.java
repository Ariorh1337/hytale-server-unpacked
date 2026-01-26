/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.MessageLite;
import com.google.protobuf.TextFormatEscaper;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

final class MessageLiteToString {
    private static final String LIST_SUFFIX = "List";
    private static final String BUILDER_LIST_SUFFIX = "OrBuilderList";
    private static final String MAP_SUFFIX = "Map";
    private static final String BYTES_SUFFIX = "Bytes";
    private static final char[] INDENT_BUFFER = new char[80];

    private MessageLiteToString() {
    }

    static String toString(MessageLite messageLite, String commentString) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("# ").append(commentString);
        MessageLiteToString.reflectivePrintWithIndent(messageLite, buffer, 0);
        return buffer.toString();
    }

    private static void reflectivePrintWithIndent(MessageLite messageLite, StringBuilder buffer, int indent) {
        HashSet<String> setters = new HashSet<String>();
        HashMap<String, Method> hazzers = new HashMap<String, Method>();
        TreeMap<String, Method> getters = new TreeMap<String, Method>();
        for (Method method : messageLite.getClass().getDeclaredMethods()) {
            if (Modifier.isStatic(method.getModifiers()) || method.getName().length() < 3) continue;
            if (method.getName().startsWith("set")) {
                setters.add(method.getName());
                continue;
            }
            if (!Modifier.isPublic(method.getModifiers()) || method.getParameterTypes().length != 0) continue;
            if (method.getName().startsWith("has")) {
                hazzers.put(method.getName(), method);
                continue;
            }
            if (!method.getName().startsWith("get")) continue;
            getters.put(method.getName(), method);
        }
        for (Map.Entry entry : getters.entrySet()) {
            Method mapMethod;
            Method listMethod;
            String suffix = ((String)entry.getKey()).substring(3);
            if (suffix.endsWith(LIST_SUFFIX) && !suffix.endsWith(BUILDER_LIST_SUFFIX) && !suffix.equals(LIST_SUFFIX) && (listMethod = (Method)entry.getValue()) != null && listMethod.getReturnType().equals(List.class)) {
                MessageLiteToString.printField(buffer, indent, suffix.substring(0, suffix.length() - LIST_SUFFIX.length()), GeneratedMessageLite.invokeOrDie(listMethod, messageLite, new Object[0]));
                continue;
            }
            if (suffix.endsWith(MAP_SUFFIX) && !suffix.equals(MAP_SUFFIX) && (mapMethod = (Method)entry.getValue()) != null && mapMethod.getReturnType().equals(Map.class) && !mapMethod.isAnnotationPresent(Deprecated.class) && Modifier.isPublic(mapMethod.getModifiers())) {
                MessageLiteToString.printField(buffer, indent, suffix.substring(0, suffix.length() - MAP_SUFFIX.length()), GeneratedMessageLite.invokeOrDie(mapMethod, messageLite, new Object[0]));
                continue;
            }
            if (!setters.contains("set" + suffix) || suffix.endsWith(BYTES_SUFFIX) && getters.containsKey("get" + suffix.substring(0, suffix.length() - BYTES_SUFFIX.length()))) continue;
            Method getMethod = (Method)entry.getValue();
            Method hasMethod = (Method)hazzers.get("has" + suffix);
            if (getMethod == null) continue;
            Object value = GeneratedMessageLite.invokeOrDie(getMethod, messageLite, new Object[0]);
            boolean bl = hasMethod == null ? !MessageLiteToString.isDefaultValue(value) : (Boolean)GeneratedMessageLite.invokeOrDie(hasMethod, messageLite, new Object[0]);
            boolean hasValue = bl;
            if (!hasValue) continue;
            MessageLiteToString.printField(buffer, indent, suffix, value);
        }
        if (messageLite instanceof GeneratedMessageLite.ExtendableMessage) {
            Iterator<Map.Entry<GeneratedMessageLite.ExtensionDescriptor, Object>> iter = ((GeneratedMessageLite.ExtendableMessage)messageLite).extensions.iterator();
            while (iter.hasNext()) {
                Map.Entry<GeneratedMessageLite.ExtensionDescriptor, Object> entry = iter.next();
                MessageLiteToString.printField(buffer, indent, "[" + entry.getKey().getNumber() + "]", entry.getValue());
            }
        }
        if (((GeneratedMessageLite)messageLite).unknownFields != null) {
            ((GeneratedMessageLite)messageLite).unknownFields.printWithIndent(buffer, indent);
        }
    }

    private static boolean isDefaultValue(Object o) {
        if (o instanceof Boolean) {
            return (Boolean)o == false;
        }
        if (o instanceof Integer) {
            return (Integer)o == 0;
        }
        if (o instanceof Float) {
            return Float.floatToRawIntBits(((Float)o).floatValue()) == 0;
        }
        if (o instanceof Double) {
            return Double.doubleToRawLongBits((Double)o) == 0L;
        }
        if (o instanceof String) {
            return o.equals("");
        }
        if (o instanceof ByteString) {
            return o.equals(ByteString.EMPTY);
        }
        if (o instanceof MessageLite) {
            return o == ((MessageLite)o).getDefaultInstanceForType();
        }
        if (o instanceof Enum) {
            return ((Enum)o).ordinal() == 0;
        }
        return false;
    }

    static void printField(StringBuilder buffer, int indent, String name, Object object) {
        if (object instanceof List) {
            List list = (List)object;
            for (Object entry : list) {
                MessageLiteToString.printField(buffer, indent, name, entry);
            }
            return;
        }
        if (object instanceof Map) {
            Map map = (Map)object;
            for (Map.Entry entry : map.entrySet()) {
                MessageLiteToString.printField(buffer, indent, name, entry);
            }
            return;
        }
        buffer.append('\n');
        MessageLiteToString.indent(indent, buffer);
        buffer.append(MessageLiteToString.pascalCaseToSnakeCase(name));
        if (object instanceof String) {
            buffer.append(": \"").append(TextFormatEscaper.escapeText((String)object)).append('\"');
        } else if (object instanceof ByteString) {
            buffer.append(": \"").append(TextFormatEscaper.escapeBytes((ByteString)object)).append('\"');
        } else if (object instanceof GeneratedMessageLite) {
            buffer.append(" {");
            MessageLiteToString.reflectivePrintWithIndent((GeneratedMessageLite)object, buffer, indent + 2);
            buffer.append("\n");
            MessageLiteToString.indent(indent, buffer);
            buffer.append("}");
        } else if (object instanceof Map.Entry) {
            buffer.append(" {");
            Map.Entry entry = (Map.Entry)object;
            MessageLiteToString.printField(buffer, indent + 2, "key", entry.getKey());
            MessageLiteToString.printField(buffer, indent + 2, "value", entry.getValue());
            buffer.append("\n");
            MessageLiteToString.indent(indent, buffer);
            buffer.append("}");
        } else {
            buffer.append(": ").append(object);
        }
    }

    private static void indent(int indent, StringBuilder buffer) {
        while (indent > 0) {
            int partialIndent = indent;
            if (partialIndent > INDENT_BUFFER.length) {
                partialIndent = INDENT_BUFFER.length;
            }
            buffer.append(INDENT_BUFFER, 0, partialIndent);
            indent -= partialIndent;
        }
    }

    private static String pascalCaseToSnakeCase(String pascalCase) {
        if (pascalCase.isEmpty()) {
            return pascalCase;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(Character.toLowerCase(pascalCase.charAt(0)));
        for (int i = 1; i < pascalCase.length(); ++i) {
            char ch = pascalCase.charAt(i);
            if (Character.isUpperCase(ch)) {
                builder.append("_");
            }
            builder.append(Character.toLowerCase(ch));
        }
        return builder.toString();
    }

    static {
        Arrays.fill(INDENT_BUFFER, ' ');
    }
}

