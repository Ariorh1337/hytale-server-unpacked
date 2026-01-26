/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.ArrayDecoders;
import com.google.protobuf.ByteString;
import com.google.protobuf.CanIgnoreReturnValue;
import com.google.protobuf.CheckReturnValue;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.ExtensionSchema;
import com.google.protobuf.FieldInfo;
import com.google.protobuf.FieldSet;
import com.google.protobuf.FieldType;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.ListFieldSchema;
import com.google.protobuf.MapEntryLite;
import com.google.protobuf.MapFieldSchema;
import com.google.protobuf.MessageInfo;
import com.google.protobuf.MessageLite;
import com.google.protobuf.NewInstanceSchema;
import com.google.protobuf.OneofInfo;
import com.google.protobuf.ProtoSyntax;
import com.google.protobuf.Protobuf;
import com.google.protobuf.RawMessageInfo;
import com.google.protobuf.Reader;
import com.google.protobuf.Schema;
import com.google.protobuf.SchemaUtil;
import com.google.protobuf.StructuralMessageInfo;
import com.google.protobuf.UnknownFieldSchema;
import com.google.protobuf.UnknownFieldSetLite;
import com.google.protobuf.UnsafeUtil;
import com.google.protobuf.Utf8;
import com.google.protobuf.WireFormat;
import com.google.protobuf.Writer;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import sun.misc.Unsafe;

@CheckReturnValue
final class MessageSchema<T>
implements Schema<T> {
    private static final int INTS_PER_FIELD = 3;
    private static final int OFFSET_BITS = 20;
    private static final int OFFSET_MASK = 1048575;
    private static final int FIELD_TYPE_MASK = 0xFF00000;
    private static final int REQUIRED_MASK = 0x10000000;
    private static final int ENFORCE_UTF8_MASK = 0x20000000;
    private static final int LEGACY_ENUM_IS_CLOSED_MASK = Integer.MIN_VALUE;
    private static final int NO_PRESENCE_SENTINEL = 1048575;
    private static final int[] EMPTY_INT_ARRAY = new int[0];
    private static final int REQUIRED_BIT = 256;
    private static final int UTF8_CHECK_BIT = 512;
    private static final int CHECK_INITIALIZED_BIT = 1024;
    private static final int LEGACY_ENUM_IS_CLOSED_BIT = 2048;
    private static final int HAS_HAS_BIT = 4096;
    static final int ONEOF_TYPE_OFFSET = 51;
    private static final Unsafe UNSAFE = UnsafeUtil.getUnsafe();
    private final int[] buffer;
    private final Object[] objects;
    private final int minFieldNumber;
    private final int maxFieldNumber;
    private final MessageLite defaultInstance;
    private final boolean hasExtensions;
    private final boolean lite;
    private final boolean useCachedSizeField;
    private final int[] intArray;
    private final int checkInitializedCount;
    private final int repeatedFieldOffsetStart;
    private final NewInstanceSchema newInstanceSchema;
    private final ListFieldSchema listFieldSchema;
    private final UnknownFieldSchema<?, ?> unknownFieldSchema;
    private final ExtensionSchema<?> extensionSchema;
    private final MapFieldSchema mapFieldSchema;

    private MessageSchema(int[] buffer, Object[] objects, int minFieldNumber, int maxFieldNumber, MessageLite defaultInstance, boolean useCachedSizeField, int[] intArray, int checkInitialized, int mapFieldPositions, NewInstanceSchema newInstanceSchema, ListFieldSchema listFieldSchema, UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MapFieldSchema mapFieldSchema) {
        this.buffer = buffer;
        this.objects = objects;
        this.minFieldNumber = minFieldNumber;
        this.maxFieldNumber = maxFieldNumber;
        this.lite = defaultInstance instanceof GeneratedMessageLite;
        this.hasExtensions = extensionSchema != null && extensionSchema.hasExtensions(defaultInstance);
        this.useCachedSizeField = useCachedSizeField;
        this.intArray = intArray;
        this.checkInitializedCount = checkInitialized;
        this.repeatedFieldOffsetStart = mapFieldPositions;
        this.newInstanceSchema = newInstanceSchema;
        this.listFieldSchema = listFieldSchema;
        this.unknownFieldSchema = unknownFieldSchema;
        this.extensionSchema = extensionSchema;
        this.defaultInstance = defaultInstance;
        this.mapFieldSchema = mapFieldSchema;
    }

    static <T> MessageSchema<T> newSchema(Class<T> messageClass, MessageInfo messageInfo, NewInstanceSchema newInstanceSchema, ListFieldSchema listFieldSchema, UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MapFieldSchema mapFieldSchema) {
        if (messageInfo instanceof RawMessageInfo) {
            return MessageSchema.newSchemaForRawMessageInfo((RawMessageInfo)messageInfo, newInstanceSchema, listFieldSchema, unknownFieldSchema, extensionSchema, mapFieldSchema);
        }
        return MessageSchema.newSchemaForMessageInfo((StructuralMessageInfo)messageInfo, newInstanceSchema, listFieldSchema, unknownFieldSchema, extensionSchema, mapFieldSchema);
    }

    static <T> MessageSchema<T> newSchemaForRawMessageInfo(RawMessageInfo messageInfo, NewInstanceSchema newInstanceSchema, ListFieldSchema listFieldSchema, UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MapFieldSchema mapFieldSchema) {
        int objectsPosition;
        int[] intArray;
        int checkInitialized;
        int repeatedFieldCount;
        int mapFieldCount;
        int numEntries;
        int maxFieldNumber;
        int minFieldNumber;
        int hasBitsCount;
        int oneofCount;
        int fieldCount;
        int next;
        String info = messageInfo.getStringInfo();
        int length = info.length();
        int i = 0;
        if ((next = info.charAt(i++)) >= 55296) {
            int result = next & 0x1FFF;
            int shift = 13;
            while ((next = info.charAt(i++)) >= 55296) {
                result |= (next & 0x1FFF) << shift;
                shift += 13;
            }
            next = result | next << shift;
        }
        int unusedFlags = next;
        if ((next = info.charAt(i++)) >= 55296) {
            int result = next & 0x1FFF;
            int shift = 13;
            while (true) {
                char c = info.charAt(i++);
                next = c;
                if (c < '\ud800') break;
                result |= (next & 0x1FFF) << shift;
                shift += 13;
            }
            next = result | next << shift;
        }
        if ((fieldCount = next) == 0) {
            oneofCount = 0;
            hasBitsCount = 0;
            minFieldNumber = 0;
            maxFieldNumber = 0;
            numEntries = 0;
            mapFieldCount = 0;
            repeatedFieldCount = 0;
            checkInitialized = 0;
            intArray = EMPTY_INT_ARRAY;
            objectsPosition = 0;
        } else {
            int shift;
            int result;
            if ((next = info.charAt(i++)) >= 55296) {
                result = next & 0x1FFF;
                shift = 13;
                while (true) {
                    char c = info.charAt(i++);
                    next = c;
                    if (c < '\ud800') break;
                    result |= (next & 0x1FFF) << shift;
                    shift += 13;
                }
                next = result | next << shift;
            }
            oneofCount = next;
            if ((next = (int)info.charAt(i++)) >= 55296) {
                result = next & 0x1FFF;
                shift = 13;
                while (true) {
                    char c = info.charAt(i++);
                    next = c;
                    if (c < '\ud800') break;
                    result |= (next & 0x1FFF) << shift;
                    shift += 13;
                }
                next = result | next << shift;
            }
            hasBitsCount = next;
            if ((next = (int)info.charAt(i++)) >= 55296) {
                result = next & 0x1FFF;
                shift = 13;
                while (true) {
                    char c = info.charAt(i++);
                    next = c;
                    if (c < '\ud800') break;
                    result |= (next & 0x1FFF) << shift;
                    shift += 13;
                }
                next = result | next << shift;
            }
            minFieldNumber = next;
            if ((next = (int)info.charAt(i++)) >= 55296) {
                result = next & 0x1FFF;
                shift = 13;
                while (true) {
                    char c = info.charAt(i++);
                    next = c;
                    if (c < '\ud800') break;
                    result |= (next & 0x1FFF) << shift;
                    shift += 13;
                }
                next = result | next << shift;
            }
            maxFieldNumber = next;
            if ((next = (int)info.charAt(i++)) >= 55296) {
                result = next & 0x1FFF;
                shift = 13;
                while (true) {
                    char c = info.charAt(i++);
                    next = c;
                    if (c < '\ud800') break;
                    result |= (next & 0x1FFF) << shift;
                    shift += 13;
                }
                next = result | next << shift;
            }
            numEntries = next;
            if ((next = (int)info.charAt(i++)) >= 55296) {
                result = next & 0x1FFF;
                shift = 13;
                while (true) {
                    char c = info.charAt(i++);
                    next = c;
                    if (c < '\ud800') break;
                    result |= (next & 0x1FFF) << shift;
                    shift += 13;
                }
                next = result | next << shift;
            }
            mapFieldCount = next;
            if ((next = (int)info.charAt(i++)) >= 55296) {
                result = next & 0x1FFF;
                shift = 13;
                while (true) {
                    char c = info.charAt(i++);
                    next = c;
                    if (c < '\ud800') break;
                    result |= (next & 0x1FFF) << shift;
                    shift += 13;
                }
                next = result | next << shift;
            }
            repeatedFieldCount = next;
            if ((next = (int)info.charAt(i++)) >= 55296) {
                result = next & 0x1FFF;
                shift = 13;
                while (true) {
                    char c = info.charAt(i++);
                    next = c;
                    if (c < '\ud800') break;
                    result |= (next & 0x1FFF) << shift;
                    shift += 13;
                }
                next = result | next << shift;
            }
            checkInitialized = next;
            intArray = new int[checkInitialized + mapFieldCount + repeatedFieldCount];
            objectsPosition = oneofCount * 2 + hasBitsCount;
        }
        Unsafe unsafe = UNSAFE;
        Object[] messageInfoObjects = messageInfo.getObjects();
        int checkInitializedPosition = 0;
        Class<?> messageClass = messageInfo.getDefaultInstance().getClass();
        int[] buffer = new int[numEntries * 3];
        Object[] objects = new Object[numEntries * 2];
        int mapFieldIndex = checkInitialized;
        int repeatedFieldIndex = checkInitialized + mapFieldCount;
        int bufferIndex = 0;
        while (i < length) {
            int presenceMaskShift;
            int presenceFieldOffset;
            int fieldOffset;
            int shift;
            int result;
            if ((next = (int)info.charAt(i++)) >= 55296) {
                result = next & 0x1FFF;
                shift = 13;
                while (true) {
                    char c = info.charAt(i++);
                    next = c;
                    if (c < '\ud800') break;
                    result |= (next & 0x1FFF) << shift;
                    shift += 13;
                }
                next = result | next << shift;
            }
            int fieldNumber = next;
            if ((next = (int)info.charAt(i++)) >= 55296) {
                result = next & 0x1FFF;
                shift = 13;
                while (true) {
                    char c = info.charAt(i++);
                    next = c;
                    if (c < '\ud800') break;
                    result |= (next & 0x1FFF) << shift;
                    shift += 13;
                }
                next = result | next << shift;
            }
            int fieldTypeWithExtraBits = next;
            int fieldType = fieldTypeWithExtraBits & 0xFF;
            if ((fieldTypeWithExtraBits & 0x400) != 0) {
                intArray[checkInitializedPosition++] = bufferIndex;
            }
            if (fieldType >= 51) {
                Field oneofCaseField;
                Field oneofField;
                if ((next = (int)info.charAt(i++)) >= 55296) {
                    int result2 = next & 0x1FFF;
                    int shift2 = 13;
                    while (true) {
                        char c = info.charAt(i++);
                        next = c;
                        if (c < '\ud800') break;
                        result2 |= (next & 0x1FFF) << shift2;
                        shift2 += 13;
                    }
                    next = result2 | next << shift2;
                }
                int oneofIndex = next;
                int oneofFieldType = fieldType - 51;
                if (oneofFieldType == 9 || oneofFieldType == 17) {
                    objects[bufferIndex / 3 * 2 + 1] = messageInfoObjects[objectsPosition++];
                } else if (oneofFieldType == 12 && (messageInfo.getSyntax().equals((Object)ProtoSyntax.PROTO2) || (fieldTypeWithExtraBits & 0x800) != 0)) {
                    objects[bufferIndex / 3 * 2 + 1] = messageInfoObjects[objectsPosition++];
                }
                int index = oneofIndex * 2;
                Object o = messageInfoObjects[index];
                if (o instanceof Field) {
                    oneofField = (Field)o;
                } else {
                    oneofField = MessageSchema.reflectField(messageClass, (String)o);
                    messageInfoObjects[index] = oneofField;
                }
                fieldOffset = (int)unsafe.objectFieldOffset(oneofField);
                o = messageInfoObjects[++index];
                if (o instanceof Field) {
                    oneofCaseField = (Field)o;
                } else {
                    oneofCaseField = MessageSchema.reflectField(messageClass, (String)o);
                    messageInfoObjects[index] = oneofCaseField;
                }
                presenceFieldOffset = (int)unsafe.objectFieldOffset(oneofCaseField);
                presenceMaskShift = 0;
            } else {
                boolean hasHasBit;
                Field field = MessageSchema.reflectField(messageClass, (String)messageInfoObjects[objectsPosition++]);
                if (fieldType == 9 || fieldType == 17) {
                    objects[bufferIndex / 3 * 2 + 1] = field.getType();
                } else if (fieldType == 27 || fieldType == 49) {
                    objects[bufferIndex / 3 * 2 + 1] = messageInfoObjects[objectsPosition++];
                } else if (fieldType == 12 || fieldType == 30 || fieldType == 44) {
                    if (messageInfo.getSyntax() == ProtoSyntax.PROTO2 || (fieldTypeWithExtraBits & 0x800) != 0) {
                        objects[bufferIndex / 3 * 2 + 1] = messageInfoObjects[objectsPosition++];
                    }
                } else if (fieldType == 50) {
                    intArray[mapFieldIndex++] = bufferIndex;
                    objects[bufferIndex / 3 * 2] = messageInfoObjects[objectsPosition++];
                    if ((fieldTypeWithExtraBits & 0x800) != 0) {
                        objects[bufferIndex / 3 * 2 + 1] = messageInfoObjects[objectsPosition++];
                    }
                }
                fieldOffset = (int)unsafe.objectFieldOffset(field);
                boolean bl = hasHasBit = (fieldTypeWithExtraBits & 0x1000) != 0;
                if (hasHasBit && fieldType <= 17) {
                    Field hasBitsField;
                    int hasBitsIndex;
                    int index;
                    Object o;
                    if ((next = (int)info.charAt(i++)) >= 55296) {
                        int result3 = next & 0x1FFF;
                        int shift3 = 13;
                        while (true) {
                            char c = info.charAt(i++);
                            next = c;
                            if (c < '\ud800') break;
                            result3 |= (next & 0x1FFF) << shift3;
                            shift3 += 13;
                        }
                        next = result3 | next << shift3;
                    }
                    if ((o = messageInfoObjects[index = oneofCount * 2 + (hasBitsIndex = next) / 32]) instanceof Field) {
                        hasBitsField = (Field)o;
                    } else {
                        hasBitsField = MessageSchema.reflectField(messageClass, (String)o);
                        messageInfoObjects[index] = hasBitsField;
                    }
                    presenceFieldOffset = (int)unsafe.objectFieldOffset(hasBitsField);
                    presenceMaskShift = hasBitsIndex % 32;
                } else {
                    presenceFieldOffset = 1048575;
                    presenceMaskShift = 0;
                }
                if (fieldType >= 18 && fieldType <= 49) {
                    intArray[repeatedFieldIndex++] = fieldOffset;
                }
            }
            buffer[bufferIndex++] = fieldNumber;
            buffer[bufferIndex++] = ((fieldTypeWithExtraBits & 0x200) != 0 ? 0x20000000 : 0) | ((fieldTypeWithExtraBits & 0x100) != 0 ? 0x10000000 : 0) | ((fieldTypeWithExtraBits & 0x800) != 0 ? Integer.MIN_VALUE : 0) | fieldType << 20 | fieldOffset;
            buffer[bufferIndex++] = presenceMaskShift << 20 | presenceFieldOffset;
        }
        return new MessageSchema<T>(buffer, objects, minFieldNumber, maxFieldNumber, messageInfo.getDefaultInstance(), false, intArray, checkInitialized, checkInitialized + mapFieldCount, newInstanceSchema, listFieldSchema, unknownFieldSchema, extensionSchema, mapFieldSchema);
    }

    private static Field reflectField(Class<?> messageClass, String fieldName) {
        try {
            return messageClass.getDeclaredField(fieldName);
        }
        catch (NoSuchFieldException e) {
            Object[] fields;
            for (Field field : fields = messageClass.getDeclaredFields()) {
                if (!fieldName.equals(field.getName())) continue;
                return field;
            }
            throw new RuntimeException("Field " + fieldName + " for " + messageClass.getName() + " not found. Known fields are " + Arrays.toString(fields), e);
        }
    }

    static <T> MessageSchema<T> newSchemaForMessageInfo(StructuralMessageInfo messageInfo, NewInstanceSchema newInstanceSchema, ListFieldSchema listFieldSchema, UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MapFieldSchema mapFieldSchema) {
        int[] combined;
        int combinedLength;
        int maxFieldNumber;
        int minFieldNumber;
        FieldInfo[] fis = messageInfo.getFields();
        if (fis.length == 0) {
            minFieldNumber = 0;
            maxFieldNumber = 0;
        } else {
            minFieldNumber = fis[0].getFieldNumber();
            maxFieldNumber = fis[fis.length - 1].getFieldNumber();
        }
        int numEntries = fis.length;
        int[] buffer = new int[numEntries * 3];
        Object[] objects = new Object[numEntries * 2];
        int mapFieldCount = 0;
        int repeatedFieldCount = 0;
        for (FieldInfo fi : fis) {
            if (fi.getType() == FieldType.MAP) {
                ++mapFieldCount;
                continue;
            }
            if (fi.getType().id() < 18 || fi.getType().id() > 49) continue;
            ++repeatedFieldCount;
        }
        int[] mapFieldPositions = mapFieldCount > 0 ? new int[mapFieldCount] : null;
        int[] repeatedFieldOffsets = repeatedFieldCount > 0 ? new int[repeatedFieldCount] : null;
        mapFieldCount = 0;
        repeatedFieldCount = 0;
        int[] checkInitialized = messageInfo.getCheckInitialized();
        if (checkInitialized == null) {
            checkInitialized = EMPTY_INT_ARRAY;
        }
        int checkInitializedIndex = 0;
        int fieldIndex = 0;
        int bufferIndex = 0;
        while (fieldIndex < fis.length) {
            FieldInfo fi = fis[fieldIndex];
            int fieldNumber = fi.getFieldNumber();
            MessageSchema.storeFieldData(fi, buffer, bufferIndex, objects);
            if (checkInitializedIndex < checkInitialized.length && checkInitialized[checkInitializedIndex] == fieldNumber) {
                checkInitialized[checkInitializedIndex++] = bufferIndex;
            }
            if (fi.getType() == FieldType.MAP) {
                mapFieldPositions[mapFieldCount++] = bufferIndex;
            } else if (fi.getType().id() >= 18 && fi.getType().id() <= 49) {
                repeatedFieldOffsets[repeatedFieldCount++] = (int)UnsafeUtil.objectFieldOffset(fi.getField());
            }
            ++fieldIndex;
            bufferIndex += 3;
        }
        if (mapFieldPositions == null) {
            mapFieldPositions = EMPTY_INT_ARRAY;
        }
        if (repeatedFieldOffsets == null) {
            repeatedFieldOffsets = EMPTY_INT_ARRAY;
        }
        if ((combinedLength = checkInitialized.length + mapFieldPositions.length + repeatedFieldOffsets.length) > 0) {
            combined = new int[combinedLength];
            System.arraycopy(checkInitialized, 0, combined, 0, checkInitialized.length);
            System.arraycopy(mapFieldPositions, 0, combined, checkInitialized.length, mapFieldPositions.length);
            System.arraycopy(repeatedFieldOffsets, 0, combined, checkInitialized.length + mapFieldPositions.length, repeatedFieldOffsets.length);
        } else {
            combined = EMPTY_INT_ARRAY;
        }
        return new MessageSchema<T>(buffer, objects, minFieldNumber, maxFieldNumber, messageInfo.getDefaultInstance(), true, combined, checkInitialized.length, checkInitialized.length + mapFieldPositions.length, newInstanceSchema, listFieldSchema, unknownFieldSchema, extensionSchema, mapFieldSchema);
    }

    private static void storeFieldData(FieldInfo fi, int[] buffer, int bufferIndex, Object[] objects) {
        int presenceMaskShift;
        int presenceFieldOffset;
        int fieldOffset;
        int typeId;
        OneofInfo oneof = fi.getOneof();
        if (oneof != null) {
            typeId = fi.getType().id() + 51;
            fieldOffset = (int)UnsafeUtil.objectFieldOffset(oneof.getValueField());
            presenceFieldOffset = (int)UnsafeUtil.objectFieldOffset(oneof.getCaseField());
            presenceMaskShift = 0;
        } else {
            FieldType type = fi.getType();
            fieldOffset = (int)UnsafeUtil.objectFieldOffset(fi.getField());
            typeId = type.id();
            if (!type.isList() && !type.isMap()) {
                Field presenceField = fi.getPresenceField();
                presenceFieldOffset = presenceField == null ? 1048575 : (int)UnsafeUtil.objectFieldOffset(presenceField);
                presenceMaskShift = Integer.numberOfTrailingZeros(fi.getPresenceMask());
            } else if (fi.getCachedSizeField() == null) {
                presenceFieldOffset = 0;
                presenceMaskShift = 0;
            } else {
                presenceFieldOffset = (int)UnsafeUtil.objectFieldOffset(fi.getCachedSizeField());
                presenceMaskShift = 0;
            }
        }
        buffer[bufferIndex] = fi.getFieldNumber();
        buffer[bufferIndex + 1] = (fi.isEnforceUtf8() ? 0x20000000 : 0) | (fi.isRequired() ? 0x10000000 : 0) | typeId << 20 | fieldOffset;
        buffer[bufferIndex + 2] = presenceMaskShift << 20 | presenceFieldOffset;
        Class<?> messageFieldClass = fi.getMessageFieldClass();
        if (fi.getMapDefaultEntry() != null) {
            objects[bufferIndex / 3 * 2] = fi.getMapDefaultEntry();
            if (messageFieldClass != null) {
                objects[bufferIndex / 3 * 2 + 1] = messageFieldClass;
            } else if (fi.getEnumVerifier() != null) {
                objects[bufferIndex / 3 * 2 + 1] = fi.getEnumVerifier();
            }
        } else if (messageFieldClass != null) {
            objects[bufferIndex / 3 * 2 + 1] = messageFieldClass;
        } else if (fi.getEnumVerifier() != null) {
            objects[bufferIndex / 3 * 2 + 1] = fi.getEnumVerifier();
        }
    }

    @Override
    public T newInstance() {
        return (T)this.newInstanceSchema.newInstance(this.defaultInstance);
    }

    @Override
    public boolean equals(T message, T other) {
        Object otherUnknown;
        int bufferLength = this.buffer.length;
        for (int pos = 0; pos < bufferLength; pos += 3) {
            if (this.equals(message, other, pos)) continue;
            return false;
        }
        Object messageUnknown = this.unknownFieldSchema.getFromMessage(message);
        if (!messageUnknown.equals(otherUnknown = this.unknownFieldSchema.getFromMessage(other))) {
            return false;
        }
        if (this.hasExtensions) {
            FieldSet<?> messageExtensions = this.extensionSchema.getExtensions(message);
            FieldSet<?> otherExtensions = this.extensionSchema.getExtensions(other);
            return messageExtensions.equals(otherExtensions);
        }
        return true;
    }

    private boolean equals(T message, T other, int pos) {
        int typeAndOffset = this.typeAndOffsetAt(pos);
        long offset = MessageSchema.offset(typeAndOffset);
        switch (MessageSchema.type(typeAndOffset)) {
            case 0: {
                return this.arePresentForEquals(message, other, pos) && Double.doubleToLongBits(UnsafeUtil.getDouble(message, offset)) == Double.doubleToLongBits(UnsafeUtil.getDouble(other, offset));
            }
            case 1: {
                return this.arePresentForEquals(message, other, pos) && Float.floatToIntBits(UnsafeUtil.getFloat(message, offset)) == Float.floatToIntBits(UnsafeUtil.getFloat(other, offset));
            }
            case 2: {
                return this.arePresentForEquals(message, other, pos) && UnsafeUtil.getLong(message, offset) == UnsafeUtil.getLong(other, offset);
            }
            case 3: {
                return this.arePresentForEquals(message, other, pos) && UnsafeUtil.getLong(message, offset) == UnsafeUtil.getLong(other, offset);
            }
            case 4: {
                return this.arePresentForEquals(message, other, pos) && UnsafeUtil.getInt(message, offset) == UnsafeUtil.getInt(other, offset);
            }
            case 5: {
                return this.arePresentForEquals(message, other, pos) && UnsafeUtil.getLong(message, offset) == UnsafeUtil.getLong(other, offset);
            }
            case 6: {
                return this.arePresentForEquals(message, other, pos) && UnsafeUtil.getInt(message, offset) == UnsafeUtil.getInt(other, offset);
            }
            case 7: {
                return this.arePresentForEquals(message, other, pos) && UnsafeUtil.getBoolean(message, offset) == UnsafeUtil.getBoolean(other, offset);
            }
            case 8: {
                return this.arePresentForEquals(message, other, pos) && SchemaUtil.safeEquals(UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset));
            }
            case 9: {
                return this.arePresentForEquals(message, other, pos) && SchemaUtil.safeEquals(UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset));
            }
            case 10: {
                return this.arePresentForEquals(message, other, pos) && SchemaUtil.safeEquals(UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset));
            }
            case 11: {
                return this.arePresentForEquals(message, other, pos) && UnsafeUtil.getInt(message, offset) == UnsafeUtil.getInt(other, offset);
            }
            case 12: {
                return this.arePresentForEquals(message, other, pos) && UnsafeUtil.getInt(message, offset) == UnsafeUtil.getInt(other, offset);
            }
            case 13: {
                return this.arePresentForEquals(message, other, pos) && UnsafeUtil.getInt(message, offset) == UnsafeUtil.getInt(other, offset);
            }
            case 14: {
                return this.arePresentForEquals(message, other, pos) && UnsafeUtil.getLong(message, offset) == UnsafeUtil.getLong(other, offset);
            }
            case 15: {
                return this.arePresentForEquals(message, other, pos) && UnsafeUtil.getInt(message, offset) == UnsafeUtil.getInt(other, offset);
            }
            case 16: {
                return this.arePresentForEquals(message, other, pos) && UnsafeUtil.getLong(message, offset) == UnsafeUtil.getLong(other, offset);
            }
            case 17: {
                return this.arePresentForEquals(message, other, pos) && SchemaUtil.safeEquals(UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset));
            }
            case 18: 
            case 19: 
            case 20: 
            case 21: 
            case 22: 
            case 23: 
            case 24: 
            case 25: 
            case 26: 
            case 27: 
            case 28: 
            case 29: 
            case 30: 
            case 31: 
            case 32: 
            case 33: 
            case 34: 
            case 35: 
            case 36: 
            case 37: 
            case 38: 
            case 39: 
            case 40: 
            case 41: 
            case 42: 
            case 43: 
            case 44: 
            case 45: 
            case 46: 
            case 47: 
            case 48: 
            case 49: {
                return SchemaUtil.safeEquals(UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset));
            }
            case 50: {
                return SchemaUtil.safeEquals(UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset));
            }
            case 51: 
            case 52: 
            case 53: 
            case 54: 
            case 55: 
            case 56: 
            case 57: 
            case 58: 
            case 59: 
            case 60: 
            case 61: 
            case 62: 
            case 63: 
            case 64: 
            case 65: 
            case 66: 
            case 67: 
            case 68: {
                return this.isOneofCaseEqual(message, other, pos) && SchemaUtil.safeEquals(UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset));
            }
        }
        return true;
    }

    @Override
    public int hashCode(T message) {
        int hashCode = 0;
        int bufferLength = this.buffer.length;
        block40: for (int pos = 0; pos < bufferLength; pos += 3) {
            int typeAndOffset = this.typeAndOffsetAt(pos);
            int entryNumber = this.numberAt(pos);
            long offset = MessageSchema.offset(typeAndOffset);
            switch (MessageSchema.type(typeAndOffset)) {
                case 0: {
                    hashCode = hashCode * 53 + Internal.hashLong(Double.doubleToLongBits(UnsafeUtil.getDouble(message, offset)));
                    continue block40;
                }
                case 1: {
                    hashCode = hashCode * 53 + Float.floatToIntBits(UnsafeUtil.getFloat(message, offset));
                    continue block40;
                }
                case 2: {
                    hashCode = hashCode * 53 + Internal.hashLong(UnsafeUtil.getLong(message, offset));
                    continue block40;
                }
                case 3: {
                    hashCode = hashCode * 53 + Internal.hashLong(UnsafeUtil.getLong(message, offset));
                    continue block40;
                }
                case 4: {
                    hashCode = hashCode * 53 + UnsafeUtil.getInt(message, offset);
                    continue block40;
                }
                case 5: {
                    hashCode = hashCode * 53 + Internal.hashLong(UnsafeUtil.getLong(message, offset));
                    continue block40;
                }
                case 6: {
                    hashCode = hashCode * 53 + UnsafeUtil.getInt(message, offset);
                    continue block40;
                }
                case 7: {
                    hashCode = hashCode * 53 + Internal.hashBoolean(UnsafeUtil.getBoolean(message, offset));
                    continue block40;
                }
                case 8: {
                    hashCode = hashCode * 53 + ((String)UnsafeUtil.getObject(message, offset)).hashCode();
                    continue block40;
                }
                case 9: {
                    int protoHash = 37;
                    Object submessage = UnsafeUtil.getObject(message, offset);
                    if (submessage != null) {
                        protoHash = submessage.hashCode();
                    }
                    hashCode = 53 * hashCode + protoHash;
                    continue block40;
                }
                case 10: {
                    hashCode = hashCode * 53 + UnsafeUtil.getObject(message, offset).hashCode();
                    continue block40;
                }
                case 11: {
                    hashCode = hashCode * 53 + UnsafeUtil.getInt(message, offset);
                    continue block40;
                }
                case 12: {
                    hashCode = hashCode * 53 + UnsafeUtil.getInt(message, offset);
                    continue block40;
                }
                case 13: {
                    hashCode = hashCode * 53 + UnsafeUtil.getInt(message, offset);
                    continue block40;
                }
                case 14: {
                    hashCode = hashCode * 53 + Internal.hashLong(UnsafeUtil.getLong(message, offset));
                    continue block40;
                }
                case 15: {
                    hashCode = hashCode * 53 + UnsafeUtil.getInt(message, offset);
                    continue block40;
                }
                case 16: {
                    hashCode = hashCode * 53 + Internal.hashLong(UnsafeUtil.getLong(message, offset));
                    continue block40;
                }
                case 17: {
                    int protoHash = 37;
                    Object submessage = UnsafeUtil.getObject(message, offset);
                    if (submessage != null) {
                        protoHash = submessage.hashCode();
                    }
                    hashCode = 53 * hashCode + protoHash;
                    continue block40;
                }
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 22: 
                case 23: 
                case 24: 
                case 25: 
                case 26: 
                case 27: 
                case 28: 
                case 29: 
                case 30: 
                case 31: 
                case 32: 
                case 33: 
                case 34: 
                case 35: 
                case 36: 
                case 37: 
                case 38: 
                case 39: 
                case 40: 
                case 41: 
                case 42: 
                case 43: 
                case 44: 
                case 45: 
                case 46: 
                case 47: 
                case 48: 
                case 49: {
                    hashCode = hashCode * 53 + UnsafeUtil.getObject(message, offset).hashCode();
                    continue block40;
                }
                case 50: {
                    hashCode = hashCode * 53 + UnsafeUtil.getObject(message, offset).hashCode();
                    continue block40;
                }
                case 51: {
                    if (!this.isOneofPresent(message, entryNumber, pos)) continue block40;
                    hashCode = hashCode * 53 + Internal.hashLong(Double.doubleToLongBits(MessageSchema.oneofDoubleAt(message, offset)));
                    continue block40;
                }
                case 52: {
                    if (!this.isOneofPresent(message, entryNumber, pos)) continue block40;
                    hashCode = hashCode * 53 + Float.floatToIntBits(MessageSchema.oneofFloatAt(message, offset));
                    continue block40;
                }
                case 53: {
                    if (!this.isOneofPresent(message, entryNumber, pos)) continue block40;
                    hashCode = hashCode * 53 + Internal.hashLong(MessageSchema.oneofLongAt(message, offset));
                    continue block40;
                }
                case 54: {
                    if (!this.isOneofPresent(message, entryNumber, pos)) continue block40;
                    hashCode = hashCode * 53 + Internal.hashLong(MessageSchema.oneofLongAt(message, offset));
                    continue block40;
                }
                case 55: {
                    if (!this.isOneofPresent(message, entryNumber, pos)) continue block40;
                    hashCode = hashCode * 53 + MessageSchema.oneofIntAt(message, offset);
                    continue block40;
                }
                case 56: {
                    if (!this.isOneofPresent(message, entryNumber, pos)) continue block40;
                    hashCode = hashCode * 53 + Internal.hashLong(MessageSchema.oneofLongAt(message, offset));
                    continue block40;
                }
                case 57: {
                    if (!this.isOneofPresent(message, entryNumber, pos)) continue block40;
                    hashCode = hashCode * 53 + MessageSchema.oneofIntAt(message, offset);
                    continue block40;
                }
                case 58: {
                    if (!this.isOneofPresent(message, entryNumber, pos)) continue block40;
                    hashCode = hashCode * 53 + Internal.hashBoolean(MessageSchema.oneofBooleanAt(message, offset));
                    continue block40;
                }
                case 59: {
                    if (!this.isOneofPresent(message, entryNumber, pos)) continue block40;
                    hashCode = hashCode * 53 + ((String)UnsafeUtil.getObject(message, offset)).hashCode();
                    continue block40;
                }
                case 60: {
                    if (!this.isOneofPresent(message, entryNumber, pos)) continue block40;
                    Object submessage = UnsafeUtil.getObject(message, offset);
                    hashCode = 53 * hashCode + submessage.hashCode();
                    continue block40;
                }
                case 61: {
                    if (!this.isOneofPresent(message, entryNumber, pos)) continue block40;
                    hashCode = hashCode * 53 + UnsafeUtil.getObject(message, offset).hashCode();
                    continue block40;
                }
                case 62: {
                    if (!this.isOneofPresent(message, entryNumber, pos)) continue block40;
                    hashCode = hashCode * 53 + MessageSchema.oneofIntAt(message, offset);
                    continue block40;
                }
                case 63: {
                    if (!this.isOneofPresent(message, entryNumber, pos)) continue block40;
                    hashCode = hashCode * 53 + MessageSchema.oneofIntAt(message, offset);
                    continue block40;
                }
                case 64: {
                    if (!this.isOneofPresent(message, entryNumber, pos)) continue block40;
                    hashCode = hashCode * 53 + MessageSchema.oneofIntAt(message, offset);
                    continue block40;
                }
                case 65: {
                    if (!this.isOneofPresent(message, entryNumber, pos)) continue block40;
                    hashCode = hashCode * 53 + Internal.hashLong(MessageSchema.oneofLongAt(message, offset));
                    continue block40;
                }
                case 66: {
                    if (!this.isOneofPresent(message, entryNumber, pos)) continue block40;
                    hashCode = hashCode * 53 + MessageSchema.oneofIntAt(message, offset);
                    continue block40;
                }
                case 67: {
                    if (!this.isOneofPresent(message, entryNumber, pos)) continue block40;
                    hashCode = hashCode * 53 + Internal.hashLong(MessageSchema.oneofLongAt(message, offset));
                    continue block40;
                }
                case 68: {
                    if (!this.isOneofPresent(message, entryNumber, pos)) continue block40;
                    Object submessage = UnsafeUtil.getObject(message, offset);
                    hashCode = 53 * hashCode + submessage.hashCode();
                    continue block40;
                }
            }
        }
        hashCode = hashCode * 53 + this.unknownFieldSchema.getFromMessage(message).hashCode();
        if (this.hasExtensions) {
            hashCode = hashCode * 53 + this.extensionSchema.getExtensions(message).hashCode();
        }
        return hashCode;
    }

    @Override
    public void mergeFrom(T message, T other) {
        MessageSchema.checkMutable(message);
        if (other == null) {
            throw new NullPointerException();
        }
        for (int i = 0; i < this.buffer.length; i += 3) {
            this.mergeSingleField(message, other, i);
        }
        SchemaUtil.mergeUnknownFields(this.unknownFieldSchema, message, other);
        if (this.hasExtensions) {
            SchemaUtil.mergeExtensions(this.extensionSchema, message, other);
        }
    }

    private void mergeSingleField(T message, T other, int pos) {
        int typeAndOffset = this.typeAndOffsetAt(pos);
        long offset = MessageSchema.offset(typeAndOffset);
        int number = this.numberAt(pos);
        switch (MessageSchema.type(typeAndOffset)) {
            case 0: {
                if (!this.isFieldPresent(other, pos)) break;
                UnsafeUtil.putDouble(message, offset, UnsafeUtil.getDouble(other, offset));
                this.setFieldPresent(message, pos);
                break;
            }
            case 1: {
                if (!this.isFieldPresent(other, pos)) break;
                UnsafeUtil.putFloat(message, offset, UnsafeUtil.getFloat(other, offset));
                this.setFieldPresent(message, pos);
                break;
            }
            case 2: {
                if (!this.isFieldPresent(other, pos)) break;
                UnsafeUtil.putLong(message, offset, UnsafeUtil.getLong(other, offset));
                this.setFieldPresent(message, pos);
                break;
            }
            case 3: {
                if (!this.isFieldPresent(other, pos)) break;
                UnsafeUtil.putLong(message, offset, UnsafeUtil.getLong(other, offset));
                this.setFieldPresent(message, pos);
                break;
            }
            case 4: {
                if (!this.isFieldPresent(other, pos)) break;
                UnsafeUtil.putInt(message, offset, UnsafeUtil.getInt(other, offset));
                this.setFieldPresent(message, pos);
                break;
            }
            case 5: {
                if (!this.isFieldPresent(other, pos)) break;
                UnsafeUtil.putLong(message, offset, UnsafeUtil.getLong(other, offset));
                this.setFieldPresent(message, pos);
                break;
            }
            case 6: {
                if (!this.isFieldPresent(other, pos)) break;
                UnsafeUtil.putInt(message, offset, UnsafeUtil.getInt(other, offset));
                this.setFieldPresent(message, pos);
                break;
            }
            case 7: {
                if (!this.isFieldPresent(other, pos)) break;
                UnsafeUtil.putBoolean(message, offset, UnsafeUtil.getBoolean(other, offset));
                this.setFieldPresent(message, pos);
                break;
            }
            case 8: {
                if (!this.isFieldPresent(other, pos)) break;
                UnsafeUtil.putObject(message, offset, UnsafeUtil.getObject(other, offset));
                this.setFieldPresent(message, pos);
                break;
            }
            case 9: {
                this.mergeMessage(message, other, pos);
                break;
            }
            case 10: {
                if (!this.isFieldPresent(other, pos)) break;
                UnsafeUtil.putObject(message, offset, UnsafeUtil.getObject(other, offset));
                this.setFieldPresent(message, pos);
                break;
            }
            case 11: {
                if (!this.isFieldPresent(other, pos)) break;
                UnsafeUtil.putInt(message, offset, UnsafeUtil.getInt(other, offset));
                this.setFieldPresent(message, pos);
                break;
            }
            case 12: {
                if (!this.isFieldPresent(other, pos)) break;
                UnsafeUtil.putInt(message, offset, UnsafeUtil.getInt(other, offset));
                this.setFieldPresent(message, pos);
                break;
            }
            case 13: {
                if (!this.isFieldPresent(other, pos)) break;
                UnsafeUtil.putInt(message, offset, UnsafeUtil.getInt(other, offset));
                this.setFieldPresent(message, pos);
                break;
            }
            case 14: {
                if (!this.isFieldPresent(other, pos)) break;
                UnsafeUtil.putLong(message, offset, UnsafeUtil.getLong(other, offset));
                this.setFieldPresent(message, pos);
                break;
            }
            case 15: {
                if (!this.isFieldPresent(other, pos)) break;
                UnsafeUtil.putInt(message, offset, UnsafeUtil.getInt(other, offset));
                this.setFieldPresent(message, pos);
                break;
            }
            case 16: {
                if (!this.isFieldPresent(other, pos)) break;
                UnsafeUtil.putLong(message, offset, UnsafeUtil.getLong(other, offset));
                this.setFieldPresent(message, pos);
                break;
            }
            case 17: {
                this.mergeMessage(message, other, pos);
                break;
            }
            case 18: 
            case 19: 
            case 20: 
            case 21: 
            case 22: 
            case 23: 
            case 24: 
            case 25: 
            case 26: 
            case 27: 
            case 28: 
            case 29: 
            case 30: 
            case 31: 
            case 32: 
            case 33: 
            case 34: 
            case 35: 
            case 36: 
            case 37: 
            case 38: 
            case 39: 
            case 40: 
            case 41: 
            case 42: 
            case 43: 
            case 44: 
            case 45: 
            case 46: 
            case 47: 
            case 48: 
            case 49: {
                this.listFieldSchema.mergeListsAt(message, other, offset);
                break;
            }
            case 50: {
                SchemaUtil.mergeMap(this.mapFieldSchema, message, other, offset);
                break;
            }
            case 51: 
            case 52: 
            case 53: 
            case 54: 
            case 55: 
            case 56: 
            case 57: 
            case 58: 
            case 59: {
                if (!this.isOneofPresent(other, number, pos)) break;
                UnsafeUtil.putObject(message, offset, UnsafeUtil.getObject(other, offset));
                this.setOneofPresent(message, number, pos);
                break;
            }
            case 60: {
                this.mergeOneofMessage(message, other, pos);
                break;
            }
            case 61: 
            case 62: 
            case 63: 
            case 64: 
            case 65: 
            case 66: 
            case 67: {
                if (!this.isOneofPresent(other, number, pos)) break;
                UnsafeUtil.putObject(message, offset, UnsafeUtil.getObject(other, offset));
                this.setOneofPresent(message, number, pos);
                break;
            }
            case 68: {
                this.mergeOneofMessage(message, other, pos);
                break;
            }
        }
    }

    private void mergeMessage(T targetParent, T sourceParent, int pos) {
        if (!this.isFieldPresent(sourceParent, pos)) {
            return;
        }
        int typeAndOffset = this.typeAndOffsetAt(pos);
        long offset = MessageSchema.offset(typeAndOffset);
        Object source = UNSAFE.getObject(sourceParent, offset);
        if (source == null) {
            throw new IllegalStateException("Source subfield " + this.numberAt(pos) + " is present but null: " + sourceParent);
        }
        Schema fieldSchema = this.getMessageFieldSchema(pos);
        if (!this.isFieldPresent(targetParent, pos)) {
            if (!MessageSchema.isMutable(source)) {
                UNSAFE.putObject(targetParent, offset, source);
            } else {
                Object copyOfSource = fieldSchema.newInstance();
                fieldSchema.mergeFrom(copyOfSource, source);
                UNSAFE.putObject(targetParent, offset, copyOfSource);
            }
            this.setFieldPresent(targetParent, pos);
            return;
        }
        Object target = UNSAFE.getObject(targetParent, offset);
        if (!MessageSchema.isMutable(target)) {
            Object newInstance = fieldSchema.newInstance();
            fieldSchema.mergeFrom(newInstance, target);
            UNSAFE.putObject(targetParent, offset, newInstance);
            target = newInstance;
        }
        fieldSchema.mergeFrom(target, source);
    }

    private void mergeOneofMessage(T targetParent, T sourceParent, int pos) {
        int number = this.numberAt(pos);
        if (!this.isOneofPresent(sourceParent, number, pos)) {
            return;
        }
        long offset = MessageSchema.offset(this.typeAndOffsetAt(pos));
        Object source = UNSAFE.getObject(sourceParent, offset);
        if (source == null) {
            throw new IllegalStateException("Source subfield " + this.numberAt(pos) + " is present but null: " + sourceParent);
        }
        Schema fieldSchema = this.getMessageFieldSchema(pos);
        if (!this.isOneofPresent(targetParent, number, pos)) {
            if (!MessageSchema.isMutable(source)) {
                UNSAFE.putObject(targetParent, offset, source);
            } else {
                Object copyOfSource = fieldSchema.newInstance();
                fieldSchema.mergeFrom(copyOfSource, source);
                UNSAFE.putObject(targetParent, offset, copyOfSource);
            }
            this.setOneofPresent(targetParent, number, pos);
            return;
        }
        Object target = UNSAFE.getObject(targetParent, offset);
        if (!MessageSchema.isMutable(target)) {
            Object newInstance = fieldSchema.newInstance();
            fieldSchema.mergeFrom(newInstance, target);
            UNSAFE.putObject(targetParent, offset, newInstance);
            target = newInstance;
        }
        fieldSchema.mergeFrom(target, source);
    }

    @Override
    public int getSerializedSize(T message) {
        int size = 0;
        Unsafe unsafe = UNSAFE;
        int currentPresenceFieldOffset = 1048575;
        int currentPresenceField = 0;
        block71: for (int i = 0; i < this.buffer.length; i += 3) {
            int typeAndOffset = this.typeAndOffsetAt(i);
            int fieldType = MessageSchema.type(typeAndOffset);
            int number = this.numberAt(i);
            int presenceMask = 0;
            int presenceMaskAndOffset = this.buffer[i + 2];
            int presenceOrCachedSizeFieldOffset = presenceMaskAndOffset & 0xFFFFF;
            if (fieldType <= 17) {
                if (presenceOrCachedSizeFieldOffset != currentPresenceFieldOffset) {
                    currentPresenceFieldOffset = presenceOrCachedSizeFieldOffset;
                    currentPresenceField = currentPresenceFieldOffset == 1048575 ? 0 : unsafe.getInt(message, currentPresenceFieldOffset);
                }
                presenceMask = 1 << (presenceMaskAndOffset >>> 20);
            }
            long offset = MessageSchema.offset(typeAndOffset);
            int cachedSizeOffset = fieldType >= FieldType.DOUBLE_LIST_PACKED.id() && fieldType <= FieldType.SINT64_LIST_PACKED.id() ? presenceOrCachedSizeFieldOffset : 0;
            switch (fieldType) {
                case 0: {
                    if (!this.isFieldPresent(message, i, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    size += CodedOutputStream.computeDoubleSize(number, 0.0);
                    continue block71;
                }
                case 1: {
                    if (!this.isFieldPresent(message, i, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    size += CodedOutputStream.computeFloatSize(number, 0.0f);
                    continue block71;
                }
                case 2: {
                    if (!this.isFieldPresent(message, i, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    size += CodedOutputStream.computeInt64Size(number, unsafe.getLong(message, offset));
                    continue block71;
                }
                case 3: {
                    if (!this.isFieldPresent(message, i, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    size += CodedOutputStream.computeUInt64Size(number, unsafe.getLong(message, offset));
                    continue block71;
                }
                case 4: {
                    if (!this.isFieldPresent(message, i, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    size += CodedOutputStream.computeInt32Size(number, unsafe.getInt(message, offset));
                    continue block71;
                }
                case 5: {
                    if (!this.isFieldPresent(message, i, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    size += CodedOutputStream.computeFixed64Size(number, 0L);
                    continue block71;
                }
                case 6: {
                    if (!this.isFieldPresent(message, i, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    size += CodedOutputStream.computeFixed32Size(number, 0);
                    continue block71;
                }
                case 7: {
                    if (!this.isFieldPresent(message, i, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    size += CodedOutputStream.computeBoolSize(number, true);
                    continue block71;
                }
                case 8: {
                    if (!this.isFieldPresent(message, i, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    Object value = unsafe.getObject(message, offset);
                    if (value instanceof ByteString) {
                        size += CodedOutputStream.computeBytesSize(number, (ByteString)value);
                        continue block71;
                    }
                    size += CodedOutputStream.computeStringSize(number, (String)value);
                    continue block71;
                }
                case 9: {
                    if (!this.isFieldPresent(message, i, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    Object value = unsafe.getObject(message, offset);
                    size += SchemaUtil.computeSizeMessage(number, value, this.getMessageFieldSchema(i));
                    continue block71;
                }
                case 10: {
                    if (!this.isFieldPresent(message, i, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    ByteString value = (ByteString)unsafe.getObject(message, offset);
                    size += CodedOutputStream.computeBytesSize(number, value);
                    continue block71;
                }
                case 11: {
                    if (!this.isFieldPresent(message, i, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    size += CodedOutputStream.computeUInt32Size(number, unsafe.getInt(message, offset));
                    continue block71;
                }
                case 12: {
                    if (!this.isFieldPresent(message, i, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    size += CodedOutputStream.computeEnumSize(number, unsafe.getInt(message, offset));
                    continue block71;
                }
                case 13: {
                    if (!this.isFieldPresent(message, i, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    size += CodedOutputStream.computeSFixed32Size(number, 0);
                    continue block71;
                }
                case 14: {
                    if (!this.isFieldPresent(message, i, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    size += CodedOutputStream.computeSFixed64Size(number, 0L);
                    continue block71;
                }
                case 15: {
                    if (!this.isFieldPresent(message, i, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    size += CodedOutputStream.computeSInt32Size(number, unsafe.getInt(message, offset));
                    continue block71;
                }
                case 16: {
                    if (!this.isFieldPresent(message, i, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    size += CodedOutputStream.computeSInt64Size(number, unsafe.getLong(message, offset));
                    continue block71;
                }
                case 17: {
                    if (!this.isFieldPresent(message, i, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    size += SchemaUtil.computeGroupSize(number, (MessageLite)unsafe.getObject(message, offset), this.getMessageFieldSchema(i));
                    continue block71;
                }
                case 18: {
                    size += SchemaUtil.computeSizeFixed64List(number, (List)unsafe.getObject(message, offset), false);
                    continue block71;
                }
                case 19: {
                    size += SchemaUtil.computeSizeFixed32List(number, (List)unsafe.getObject(message, offset), false);
                    continue block71;
                }
                case 20: {
                    size += SchemaUtil.computeSizeInt64List(number, (List)unsafe.getObject(message, offset), false);
                    continue block71;
                }
                case 21: {
                    size += SchemaUtil.computeSizeUInt64List(number, (List)unsafe.getObject(message, offset), false);
                    continue block71;
                }
                case 22: {
                    size += SchemaUtil.computeSizeInt32List(number, (List)unsafe.getObject(message, offset), false);
                    continue block71;
                }
                case 23: {
                    size += SchemaUtil.computeSizeFixed64List(number, (List)unsafe.getObject(message, offset), false);
                    continue block71;
                }
                case 24: {
                    size += SchemaUtil.computeSizeFixed32List(number, (List)unsafe.getObject(message, offset), false);
                    continue block71;
                }
                case 25: {
                    size += SchemaUtil.computeSizeBoolList(number, (List)unsafe.getObject(message, offset), false);
                    continue block71;
                }
                case 26: {
                    size += SchemaUtil.computeSizeStringList(number, (List)unsafe.getObject(message, offset));
                    continue block71;
                }
                case 27: {
                    size += SchemaUtil.computeSizeMessageList(number, (List)unsafe.getObject(message, offset), this.getMessageFieldSchema(i));
                    continue block71;
                }
                case 28: {
                    size += SchemaUtil.computeSizeByteStringList(number, (List)unsafe.getObject(message, offset));
                    continue block71;
                }
                case 29: {
                    size += SchemaUtil.computeSizeUInt32List(number, (List)unsafe.getObject(message, offset), false);
                    continue block71;
                }
                case 30: {
                    size += SchemaUtil.computeSizeEnumList(number, (List)unsafe.getObject(message, offset), false);
                    continue block71;
                }
                case 31: {
                    size += SchemaUtil.computeSizeFixed32List(number, (List)unsafe.getObject(message, offset), false);
                    continue block71;
                }
                case 32: {
                    size += SchemaUtil.computeSizeFixed64List(number, (List)unsafe.getObject(message, offset), false);
                    continue block71;
                }
                case 33: {
                    size += SchemaUtil.computeSizeSInt32List(number, (List)unsafe.getObject(message, offset), false);
                    continue block71;
                }
                case 34: {
                    size += SchemaUtil.computeSizeSInt64List(number, (List)unsafe.getObject(message, offset), false);
                    continue block71;
                }
                case 35: {
                    int fieldSize = SchemaUtil.computeSizeFixed64ListNoTag((List)unsafe.getObject(message, offset));
                    if (fieldSize <= 0) continue block71;
                    if (this.useCachedSizeField) {
                        unsafe.putInt(message, cachedSizeOffset, fieldSize);
                    }
                    size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
                    continue block71;
                }
                case 36: {
                    int fieldSize = SchemaUtil.computeSizeFixed32ListNoTag((List)unsafe.getObject(message, offset));
                    if (fieldSize <= 0) continue block71;
                    if (this.useCachedSizeField) {
                        unsafe.putInt(message, cachedSizeOffset, fieldSize);
                    }
                    size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
                    continue block71;
                }
                case 37: {
                    int fieldSize = SchemaUtil.computeSizeInt64ListNoTag((List)unsafe.getObject(message, offset));
                    if (fieldSize <= 0) continue block71;
                    if (this.useCachedSizeField) {
                        unsafe.putInt(message, cachedSizeOffset, fieldSize);
                    }
                    size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
                    continue block71;
                }
                case 38: {
                    int fieldSize = SchemaUtil.computeSizeUInt64ListNoTag((List)unsafe.getObject(message, offset));
                    if (fieldSize <= 0) continue block71;
                    if (this.useCachedSizeField) {
                        unsafe.putInt(message, cachedSizeOffset, fieldSize);
                    }
                    size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
                    continue block71;
                }
                case 39: {
                    int fieldSize = SchemaUtil.computeSizeInt32ListNoTag((List)unsafe.getObject(message, offset));
                    if (fieldSize <= 0) continue block71;
                    if (this.useCachedSizeField) {
                        unsafe.putInt(message, cachedSizeOffset, fieldSize);
                    }
                    size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
                    continue block71;
                }
                case 40: {
                    int fieldSize = SchemaUtil.computeSizeFixed64ListNoTag((List)unsafe.getObject(message, offset));
                    if (fieldSize <= 0) continue block71;
                    if (this.useCachedSizeField) {
                        unsafe.putInt(message, cachedSizeOffset, fieldSize);
                    }
                    size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
                    continue block71;
                }
                case 41: {
                    int fieldSize = SchemaUtil.computeSizeFixed32ListNoTag((List)unsafe.getObject(message, offset));
                    if (fieldSize <= 0) continue block71;
                    if (this.useCachedSizeField) {
                        unsafe.putInt(message, cachedSizeOffset, fieldSize);
                    }
                    size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
                    continue block71;
                }
                case 42: {
                    int fieldSize = SchemaUtil.computeSizeBoolListNoTag((List)unsafe.getObject(message, offset));
                    if (fieldSize <= 0) continue block71;
                    if (this.useCachedSizeField) {
                        unsafe.putInt(message, cachedSizeOffset, fieldSize);
                    }
                    size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
                    continue block71;
                }
                case 43: {
                    int fieldSize = SchemaUtil.computeSizeUInt32ListNoTag((List)unsafe.getObject(message, offset));
                    if (fieldSize <= 0) continue block71;
                    if (this.useCachedSizeField) {
                        unsafe.putInt(message, cachedSizeOffset, fieldSize);
                    }
                    size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
                    continue block71;
                }
                case 44: {
                    int fieldSize = SchemaUtil.computeSizeEnumListNoTag((List)unsafe.getObject(message, offset));
                    if (fieldSize <= 0) continue block71;
                    if (this.useCachedSizeField) {
                        unsafe.putInt(message, cachedSizeOffset, fieldSize);
                    }
                    size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
                    continue block71;
                }
                case 45: {
                    int fieldSize = SchemaUtil.computeSizeFixed32ListNoTag((List)unsafe.getObject(message, offset));
                    if (fieldSize <= 0) continue block71;
                    if (this.useCachedSizeField) {
                        unsafe.putInt(message, cachedSizeOffset, fieldSize);
                    }
                    size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
                    continue block71;
                }
                case 46: {
                    int fieldSize = SchemaUtil.computeSizeFixed64ListNoTag((List)unsafe.getObject(message, offset));
                    if (fieldSize <= 0) continue block71;
                    if (this.useCachedSizeField) {
                        unsafe.putInt(message, cachedSizeOffset, fieldSize);
                    }
                    size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
                    continue block71;
                }
                case 47: {
                    int fieldSize = SchemaUtil.computeSizeSInt32ListNoTag((List)unsafe.getObject(message, offset));
                    if (fieldSize <= 0) continue block71;
                    if (this.useCachedSizeField) {
                        unsafe.putInt(message, cachedSizeOffset, fieldSize);
                    }
                    size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
                    continue block71;
                }
                case 48: {
                    int fieldSize = SchemaUtil.computeSizeSInt64ListNoTag((List)unsafe.getObject(message, offset));
                    if (fieldSize <= 0) continue block71;
                    if (this.useCachedSizeField) {
                        unsafe.putInt(message, cachedSizeOffset, fieldSize);
                    }
                    size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
                    continue block71;
                }
                case 49: {
                    size += SchemaUtil.computeSizeGroupList(number, (List)unsafe.getObject(message, offset), this.getMessageFieldSchema(i));
                    continue block71;
                }
                case 50: {
                    size += this.mapFieldSchema.getSerializedSize(number, unsafe.getObject(message, offset), this.getMapFieldDefaultEntry(i));
                    continue block71;
                }
                case 51: {
                    if (!this.isOneofPresent(message, number, i)) continue block71;
                    size += CodedOutputStream.computeDoubleSize(number, 0.0);
                    continue block71;
                }
                case 52: {
                    if (!this.isOneofPresent(message, number, i)) continue block71;
                    size += CodedOutputStream.computeFloatSize(number, 0.0f);
                    continue block71;
                }
                case 53: {
                    if (!this.isOneofPresent(message, number, i)) continue block71;
                    size += CodedOutputStream.computeInt64Size(number, MessageSchema.oneofLongAt(message, offset));
                    continue block71;
                }
                case 54: {
                    if (!this.isOneofPresent(message, number, i)) continue block71;
                    size += CodedOutputStream.computeUInt64Size(number, MessageSchema.oneofLongAt(message, offset));
                    continue block71;
                }
                case 55: {
                    if (!this.isOneofPresent(message, number, i)) continue block71;
                    size += CodedOutputStream.computeInt32Size(number, MessageSchema.oneofIntAt(message, offset));
                    continue block71;
                }
                case 56: {
                    if (!this.isOneofPresent(message, number, i)) continue block71;
                    size += CodedOutputStream.computeFixed64Size(number, 0L);
                    continue block71;
                }
                case 57: {
                    if (!this.isOneofPresent(message, number, i)) continue block71;
                    size += CodedOutputStream.computeFixed32Size(number, 0);
                    continue block71;
                }
                case 58: {
                    if (!this.isOneofPresent(message, number, i)) continue block71;
                    size += CodedOutputStream.computeBoolSize(number, true);
                    continue block71;
                }
                case 59: {
                    if (!this.isOneofPresent(message, number, i)) continue block71;
                    Object value = unsafe.getObject(message, offset);
                    if (value instanceof ByteString) {
                        size += CodedOutputStream.computeBytesSize(number, (ByteString)value);
                        continue block71;
                    }
                    size += CodedOutputStream.computeStringSize(number, (String)value);
                    continue block71;
                }
                case 60: {
                    if (!this.isOneofPresent(message, number, i)) continue block71;
                    Object value = unsafe.getObject(message, offset);
                    size += SchemaUtil.computeSizeMessage(number, value, this.getMessageFieldSchema(i));
                    continue block71;
                }
                case 61: {
                    if (!this.isOneofPresent(message, number, i)) continue block71;
                    size += CodedOutputStream.computeBytesSize(number, (ByteString)unsafe.getObject(message, offset));
                    continue block71;
                }
                case 62: {
                    if (!this.isOneofPresent(message, number, i)) continue block71;
                    size += CodedOutputStream.computeUInt32Size(number, MessageSchema.oneofIntAt(message, offset));
                    continue block71;
                }
                case 63: {
                    if (!this.isOneofPresent(message, number, i)) continue block71;
                    size += CodedOutputStream.computeEnumSize(number, MessageSchema.oneofIntAt(message, offset));
                    continue block71;
                }
                case 64: {
                    if (!this.isOneofPresent(message, number, i)) continue block71;
                    size += CodedOutputStream.computeSFixed32Size(number, 0);
                    continue block71;
                }
                case 65: {
                    if (!this.isOneofPresent(message, number, i)) continue block71;
                    size += CodedOutputStream.computeSFixed64Size(number, 0L);
                    continue block71;
                }
                case 66: {
                    if (!this.isOneofPresent(message, number, i)) continue block71;
                    size += CodedOutputStream.computeSInt32Size(number, MessageSchema.oneofIntAt(message, offset));
                    continue block71;
                }
                case 67: {
                    if (!this.isOneofPresent(message, number, i)) continue block71;
                    size += CodedOutputStream.computeSInt64Size(number, MessageSchema.oneofLongAt(message, offset));
                    continue block71;
                }
                case 68: {
                    if (!this.isOneofPresent(message, number, i)) continue block71;
                    size += SchemaUtil.computeGroupSize(number, (MessageLite)unsafe.getObject(message, offset), this.getMessageFieldSchema(i));
                    continue block71;
                }
            }
        }
        size += this.getUnknownFieldsSerializedSize(this.unknownFieldSchema, message);
        if (this.hasExtensions) {
            size += this.extensionSchema.getExtensions(message).getSerializedSize();
        }
        return size;
    }

    private <UT, UB> int getUnknownFieldsSerializedSize(UnknownFieldSchema<UT, UB> schema, T message) {
        UT unknowns = schema.getFromMessage(message);
        return schema.getSerializedSize(unknowns);
    }

    @Override
    public void writeTo(T message, Writer writer) throws IOException {
        if (writer.fieldOrder() == Writer.FieldOrder.DESCENDING) {
            this.writeFieldsInDescendingOrder(message, writer);
        } else {
            this.writeFieldsInAscendingOrder(message, writer);
        }
    }

    private void writeFieldsInAscendingOrder(T message, Writer writer) throws IOException {
        FieldSet<?> extensions;
        Iterator<Map.Entry<?, Object>> extensionIterator = null;
        Map.Entry<?, Object> nextExtension = null;
        if (this.hasExtensions && !(extensions = this.extensionSchema.getExtensions(message)).isEmpty()) {
            extensionIterator = extensions.iterator();
            nextExtension = extensionIterator.next();
        }
        int currentPresenceFieldOffset = 1048575;
        int currentPresenceField = 0;
        int bufferLength = this.buffer.length;
        Unsafe unsafe = UNSAFE;
        block71: for (int pos = 0; pos < bufferLength; pos += 3) {
            int typeAndOffset = this.typeAndOffsetAt(pos);
            int number = this.numberAt(pos);
            int fieldType = MessageSchema.type(typeAndOffset);
            int presenceMask = 0;
            if (fieldType <= 17) {
                int presenceMaskAndOffset = this.buffer[pos + 2];
                int presenceFieldOffset = presenceMaskAndOffset & 0xFFFFF;
                if (presenceFieldOffset != currentPresenceFieldOffset) {
                    currentPresenceFieldOffset = presenceFieldOffset;
                    currentPresenceField = currentPresenceFieldOffset == 1048575 ? 0 : unsafe.getInt(message, presenceFieldOffset);
                }
                presenceMask = 1 << (presenceMaskAndOffset >>> 20);
            }
            while (nextExtension != null && this.extensionSchema.extensionNumber(nextExtension) <= number) {
                this.extensionSchema.serializeExtension(writer, nextExtension);
                nextExtension = extensionIterator.hasNext() ? extensionIterator.next() : null;
            }
            long offset = MessageSchema.offset(typeAndOffset);
            switch (fieldType) {
                case 0: {
                    if (!this.isFieldPresent(message, pos, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    writer.writeDouble(number, MessageSchema.doubleAt(message, offset));
                    continue block71;
                }
                case 1: {
                    if (!this.isFieldPresent(message, pos, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    writer.writeFloat(number, MessageSchema.floatAt(message, offset));
                    continue block71;
                }
                case 2: {
                    if (!this.isFieldPresent(message, pos, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    writer.writeInt64(number, unsafe.getLong(message, offset));
                    continue block71;
                }
                case 3: {
                    if (!this.isFieldPresent(message, pos, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    writer.writeUInt64(number, unsafe.getLong(message, offset));
                    continue block71;
                }
                case 4: {
                    if (!this.isFieldPresent(message, pos, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    writer.writeInt32(number, unsafe.getInt(message, offset));
                    continue block71;
                }
                case 5: {
                    if (!this.isFieldPresent(message, pos, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    writer.writeFixed64(number, unsafe.getLong(message, offset));
                    continue block71;
                }
                case 6: {
                    if (!this.isFieldPresent(message, pos, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    writer.writeFixed32(number, unsafe.getInt(message, offset));
                    continue block71;
                }
                case 7: {
                    if (!this.isFieldPresent(message, pos, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    writer.writeBool(number, MessageSchema.booleanAt(message, offset));
                    continue block71;
                }
                case 8: {
                    if (!this.isFieldPresent(message, pos, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    this.writeString(number, unsafe.getObject(message, offset), writer);
                    continue block71;
                }
                case 9: {
                    if (!this.isFieldPresent(message, pos, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    Object value = unsafe.getObject(message, offset);
                    writer.writeMessage(number, value, this.getMessageFieldSchema(pos));
                    continue block71;
                }
                case 10: {
                    if (!this.isFieldPresent(message, pos, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    writer.writeBytes(number, (ByteString)unsafe.getObject(message, offset));
                    continue block71;
                }
                case 11: {
                    if (!this.isFieldPresent(message, pos, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    writer.writeUInt32(number, unsafe.getInt(message, offset));
                    continue block71;
                }
                case 12: {
                    if (!this.isFieldPresent(message, pos, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    writer.writeEnum(number, unsafe.getInt(message, offset));
                    continue block71;
                }
                case 13: {
                    if (!this.isFieldPresent(message, pos, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    writer.writeSFixed32(number, unsafe.getInt(message, offset));
                    continue block71;
                }
                case 14: {
                    if (!this.isFieldPresent(message, pos, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    writer.writeSFixed64(number, unsafe.getLong(message, offset));
                    continue block71;
                }
                case 15: {
                    if (!this.isFieldPresent(message, pos, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    writer.writeSInt32(number, unsafe.getInt(message, offset));
                    continue block71;
                }
                case 16: {
                    if (!this.isFieldPresent(message, pos, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    writer.writeSInt64(number, unsafe.getLong(message, offset));
                    continue block71;
                }
                case 17: {
                    if (!this.isFieldPresent(message, pos, currentPresenceFieldOffset, currentPresenceField, presenceMask)) continue block71;
                    writer.writeGroup(number, unsafe.getObject(message, offset), this.getMessageFieldSchema(pos));
                    continue block71;
                }
                case 18: {
                    SchemaUtil.writeDoubleList(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer, false);
                    continue block71;
                }
                case 19: {
                    SchemaUtil.writeFloatList(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer, false);
                    continue block71;
                }
                case 20: {
                    SchemaUtil.writeInt64List(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer, false);
                    continue block71;
                }
                case 21: {
                    SchemaUtil.writeUInt64List(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer, false);
                    continue block71;
                }
                case 22: {
                    SchemaUtil.writeInt32List(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer, false);
                    continue block71;
                }
                case 23: {
                    SchemaUtil.writeFixed64List(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer, false);
                    continue block71;
                }
                case 24: {
                    SchemaUtil.writeFixed32List(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer, false);
                    continue block71;
                }
                case 25: {
                    SchemaUtil.writeBoolList(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer, false);
                    continue block71;
                }
                case 26: {
                    SchemaUtil.writeStringList(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer);
                    continue block71;
                }
                case 27: {
                    SchemaUtil.writeMessageList(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer, this.getMessageFieldSchema(pos));
                    continue block71;
                }
                case 28: {
                    SchemaUtil.writeBytesList(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer);
                    continue block71;
                }
                case 29: {
                    SchemaUtil.writeUInt32List(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer, false);
                    continue block71;
                }
                case 30: {
                    SchemaUtil.writeEnumList(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer, false);
                    continue block71;
                }
                case 31: {
                    SchemaUtil.writeSFixed32List(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer, false);
                    continue block71;
                }
                case 32: {
                    SchemaUtil.writeSFixed64List(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer, false);
                    continue block71;
                }
                case 33: {
                    SchemaUtil.writeSInt32List(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer, false);
                    continue block71;
                }
                case 34: {
                    SchemaUtil.writeSInt64List(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer, false);
                    continue block71;
                }
                case 35: {
                    SchemaUtil.writeDoubleList(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer, true);
                    continue block71;
                }
                case 36: {
                    SchemaUtil.writeFloatList(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer, true);
                    continue block71;
                }
                case 37: {
                    SchemaUtil.writeInt64List(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer, true);
                    continue block71;
                }
                case 38: {
                    SchemaUtil.writeUInt64List(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer, true);
                    continue block71;
                }
                case 39: {
                    SchemaUtil.writeInt32List(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer, true);
                    continue block71;
                }
                case 40: {
                    SchemaUtil.writeFixed64List(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer, true);
                    continue block71;
                }
                case 41: {
                    SchemaUtil.writeFixed32List(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer, true);
                    continue block71;
                }
                case 42: {
                    SchemaUtil.writeBoolList(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer, true);
                    continue block71;
                }
                case 43: {
                    SchemaUtil.writeUInt32List(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer, true);
                    continue block71;
                }
                case 44: {
                    SchemaUtil.writeEnumList(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer, true);
                    continue block71;
                }
                case 45: {
                    SchemaUtil.writeSFixed32List(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer, true);
                    continue block71;
                }
                case 46: {
                    SchemaUtil.writeSFixed64List(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer, true);
                    continue block71;
                }
                case 47: {
                    SchemaUtil.writeSInt32List(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer, true);
                    continue block71;
                }
                case 48: {
                    SchemaUtil.writeSInt64List(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer, true);
                    continue block71;
                }
                case 49: {
                    SchemaUtil.writeGroupList(this.numberAt(pos), (List)unsafe.getObject(message, offset), writer, this.getMessageFieldSchema(pos));
                    continue block71;
                }
                case 50: {
                    this.writeMapHelper(writer, number, unsafe.getObject(message, offset), pos);
                    continue block71;
                }
                case 51: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeDouble(number, MessageSchema.oneofDoubleAt(message, offset));
                    continue block71;
                }
                case 52: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeFloat(number, MessageSchema.oneofFloatAt(message, offset));
                    continue block71;
                }
                case 53: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeInt64(number, MessageSchema.oneofLongAt(message, offset));
                    continue block71;
                }
                case 54: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeUInt64(number, MessageSchema.oneofLongAt(message, offset));
                    continue block71;
                }
                case 55: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeInt32(number, MessageSchema.oneofIntAt(message, offset));
                    continue block71;
                }
                case 56: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeFixed64(number, MessageSchema.oneofLongAt(message, offset));
                    continue block71;
                }
                case 57: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeFixed32(number, MessageSchema.oneofIntAt(message, offset));
                    continue block71;
                }
                case 58: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeBool(number, MessageSchema.oneofBooleanAt(message, offset));
                    continue block71;
                }
                case 59: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    this.writeString(number, unsafe.getObject(message, offset), writer);
                    continue block71;
                }
                case 60: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    Object value = unsafe.getObject(message, offset);
                    writer.writeMessage(number, value, this.getMessageFieldSchema(pos));
                    continue block71;
                }
                case 61: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeBytes(number, (ByteString)unsafe.getObject(message, offset));
                    continue block71;
                }
                case 62: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeUInt32(number, MessageSchema.oneofIntAt(message, offset));
                    continue block71;
                }
                case 63: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeEnum(number, MessageSchema.oneofIntAt(message, offset));
                    continue block71;
                }
                case 64: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeSFixed32(number, MessageSchema.oneofIntAt(message, offset));
                    continue block71;
                }
                case 65: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeSFixed64(number, MessageSchema.oneofLongAt(message, offset));
                    continue block71;
                }
                case 66: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeSInt32(number, MessageSchema.oneofIntAt(message, offset));
                    continue block71;
                }
                case 67: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeSInt64(number, MessageSchema.oneofLongAt(message, offset));
                    continue block71;
                }
                case 68: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeGroup(number, unsafe.getObject(message, offset), this.getMessageFieldSchema(pos));
                    continue block71;
                }
            }
        }
        while (nextExtension != null) {
            this.extensionSchema.serializeExtension(writer, nextExtension);
            nextExtension = extensionIterator.hasNext() ? extensionIterator.next() : null;
        }
        this.writeUnknownInMessageTo(this.unknownFieldSchema, message, writer);
    }

    private void writeFieldsInDescendingOrder(T message, Writer writer) throws IOException {
        FieldSet<?> extensions;
        this.writeUnknownInMessageTo(this.unknownFieldSchema, message, writer);
        Iterator<Map.Entry<?, Object>> extensionIterator = null;
        Map.Entry<?, Object> nextExtension = null;
        if (this.hasExtensions && !(extensions = this.extensionSchema.getExtensions(message)).isEmpty()) {
            extensionIterator = extensions.descendingIterator();
            nextExtension = extensionIterator.next();
        }
        block71: for (int pos = this.buffer.length - 3; pos >= 0; pos -= 3) {
            int typeAndOffset = this.typeAndOffsetAt(pos);
            int number = this.numberAt(pos);
            while (nextExtension != null && this.extensionSchema.extensionNumber(nextExtension) > number) {
                this.extensionSchema.serializeExtension(writer, nextExtension);
                nextExtension = extensionIterator.hasNext() ? extensionIterator.next() : null;
            }
            switch (MessageSchema.type(typeAndOffset)) {
                case 0: {
                    if (!this.isFieldPresent(message, pos)) continue block71;
                    writer.writeDouble(number, MessageSchema.doubleAt(message, MessageSchema.offset(typeAndOffset)));
                    continue block71;
                }
                case 1: {
                    if (!this.isFieldPresent(message, pos)) continue block71;
                    writer.writeFloat(number, MessageSchema.floatAt(message, MessageSchema.offset(typeAndOffset)));
                    continue block71;
                }
                case 2: {
                    if (!this.isFieldPresent(message, pos)) continue block71;
                    writer.writeInt64(number, MessageSchema.longAt(message, MessageSchema.offset(typeAndOffset)));
                    continue block71;
                }
                case 3: {
                    if (!this.isFieldPresent(message, pos)) continue block71;
                    writer.writeUInt64(number, MessageSchema.longAt(message, MessageSchema.offset(typeAndOffset)));
                    continue block71;
                }
                case 4: {
                    if (!this.isFieldPresent(message, pos)) continue block71;
                    writer.writeInt32(number, MessageSchema.intAt(message, MessageSchema.offset(typeAndOffset)));
                    continue block71;
                }
                case 5: {
                    if (!this.isFieldPresent(message, pos)) continue block71;
                    writer.writeFixed64(number, MessageSchema.longAt(message, MessageSchema.offset(typeAndOffset)));
                    continue block71;
                }
                case 6: {
                    if (!this.isFieldPresent(message, pos)) continue block71;
                    writer.writeFixed32(number, MessageSchema.intAt(message, MessageSchema.offset(typeAndOffset)));
                    continue block71;
                }
                case 7: {
                    if (!this.isFieldPresent(message, pos)) continue block71;
                    writer.writeBool(number, MessageSchema.booleanAt(message, MessageSchema.offset(typeAndOffset)));
                    continue block71;
                }
                case 8: {
                    if (!this.isFieldPresent(message, pos)) continue block71;
                    this.writeString(number, UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer);
                    continue block71;
                }
                case 9: {
                    if (!this.isFieldPresent(message, pos)) continue block71;
                    Object value = UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset));
                    writer.writeMessage(number, value, this.getMessageFieldSchema(pos));
                    continue block71;
                }
                case 10: {
                    if (!this.isFieldPresent(message, pos)) continue block71;
                    writer.writeBytes(number, (ByteString)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)));
                    continue block71;
                }
                case 11: {
                    if (!this.isFieldPresent(message, pos)) continue block71;
                    writer.writeUInt32(number, MessageSchema.intAt(message, MessageSchema.offset(typeAndOffset)));
                    continue block71;
                }
                case 12: {
                    if (!this.isFieldPresent(message, pos)) continue block71;
                    writer.writeEnum(number, MessageSchema.intAt(message, MessageSchema.offset(typeAndOffset)));
                    continue block71;
                }
                case 13: {
                    if (!this.isFieldPresent(message, pos)) continue block71;
                    writer.writeSFixed32(number, MessageSchema.intAt(message, MessageSchema.offset(typeAndOffset)));
                    continue block71;
                }
                case 14: {
                    if (!this.isFieldPresent(message, pos)) continue block71;
                    writer.writeSFixed64(number, MessageSchema.longAt(message, MessageSchema.offset(typeAndOffset)));
                    continue block71;
                }
                case 15: {
                    if (!this.isFieldPresent(message, pos)) continue block71;
                    writer.writeSInt32(number, MessageSchema.intAt(message, MessageSchema.offset(typeAndOffset)));
                    continue block71;
                }
                case 16: {
                    if (!this.isFieldPresent(message, pos)) continue block71;
                    writer.writeSInt64(number, MessageSchema.longAt(message, MessageSchema.offset(typeAndOffset)));
                    continue block71;
                }
                case 17: {
                    if (!this.isFieldPresent(message, pos)) continue block71;
                    writer.writeGroup(number, UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), this.getMessageFieldSchema(pos));
                    continue block71;
                }
                case 18: {
                    SchemaUtil.writeDoubleList(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer, false);
                    continue block71;
                }
                case 19: {
                    SchemaUtil.writeFloatList(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer, false);
                    continue block71;
                }
                case 20: {
                    SchemaUtil.writeInt64List(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer, false);
                    continue block71;
                }
                case 21: {
                    SchemaUtil.writeUInt64List(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer, false);
                    continue block71;
                }
                case 22: {
                    SchemaUtil.writeInt32List(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer, false);
                    continue block71;
                }
                case 23: {
                    SchemaUtil.writeFixed64List(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer, false);
                    continue block71;
                }
                case 24: {
                    SchemaUtil.writeFixed32List(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer, false);
                    continue block71;
                }
                case 25: {
                    SchemaUtil.writeBoolList(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer, false);
                    continue block71;
                }
                case 26: {
                    SchemaUtil.writeStringList(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer);
                    continue block71;
                }
                case 27: {
                    SchemaUtil.writeMessageList(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer, this.getMessageFieldSchema(pos));
                    continue block71;
                }
                case 28: {
                    SchemaUtil.writeBytesList(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer);
                    continue block71;
                }
                case 29: {
                    SchemaUtil.writeUInt32List(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer, false);
                    continue block71;
                }
                case 30: {
                    SchemaUtil.writeEnumList(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer, false);
                    continue block71;
                }
                case 31: {
                    SchemaUtil.writeSFixed32List(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer, false);
                    continue block71;
                }
                case 32: {
                    SchemaUtil.writeSFixed64List(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer, false);
                    continue block71;
                }
                case 33: {
                    SchemaUtil.writeSInt32List(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer, false);
                    continue block71;
                }
                case 34: {
                    SchemaUtil.writeSInt64List(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer, false);
                    continue block71;
                }
                case 35: {
                    SchemaUtil.writeDoubleList(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer, true);
                    continue block71;
                }
                case 36: {
                    SchemaUtil.writeFloatList(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer, true);
                    continue block71;
                }
                case 37: {
                    SchemaUtil.writeInt64List(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer, true);
                    continue block71;
                }
                case 38: {
                    SchemaUtil.writeUInt64List(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer, true);
                    continue block71;
                }
                case 39: {
                    SchemaUtil.writeInt32List(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer, true);
                    continue block71;
                }
                case 40: {
                    SchemaUtil.writeFixed64List(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer, true);
                    continue block71;
                }
                case 41: {
                    SchemaUtil.writeFixed32List(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer, true);
                    continue block71;
                }
                case 42: {
                    SchemaUtil.writeBoolList(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer, true);
                    continue block71;
                }
                case 43: {
                    SchemaUtil.writeUInt32List(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer, true);
                    continue block71;
                }
                case 44: {
                    SchemaUtil.writeEnumList(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer, true);
                    continue block71;
                }
                case 45: {
                    SchemaUtil.writeSFixed32List(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer, true);
                    continue block71;
                }
                case 46: {
                    SchemaUtil.writeSFixed64List(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer, true);
                    continue block71;
                }
                case 47: {
                    SchemaUtil.writeSInt32List(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer, true);
                    continue block71;
                }
                case 48: {
                    SchemaUtil.writeSInt64List(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer, true);
                    continue block71;
                }
                case 49: {
                    SchemaUtil.writeGroupList(number, (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer, this.getMessageFieldSchema(pos));
                    continue block71;
                }
                case 50: {
                    this.writeMapHelper(writer, number, UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), pos);
                    continue block71;
                }
                case 51: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeDouble(number, MessageSchema.oneofDoubleAt(message, MessageSchema.offset(typeAndOffset)));
                    continue block71;
                }
                case 52: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeFloat(number, MessageSchema.oneofFloatAt(message, MessageSchema.offset(typeAndOffset)));
                    continue block71;
                }
                case 53: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeInt64(number, MessageSchema.oneofLongAt(message, MessageSchema.offset(typeAndOffset)));
                    continue block71;
                }
                case 54: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeUInt64(number, MessageSchema.oneofLongAt(message, MessageSchema.offset(typeAndOffset)));
                    continue block71;
                }
                case 55: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeInt32(number, MessageSchema.oneofIntAt(message, MessageSchema.offset(typeAndOffset)));
                    continue block71;
                }
                case 56: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeFixed64(number, MessageSchema.oneofLongAt(message, MessageSchema.offset(typeAndOffset)));
                    continue block71;
                }
                case 57: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeFixed32(number, MessageSchema.oneofIntAt(message, MessageSchema.offset(typeAndOffset)));
                    continue block71;
                }
                case 58: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeBool(number, MessageSchema.oneofBooleanAt(message, MessageSchema.offset(typeAndOffset)));
                    continue block71;
                }
                case 59: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    this.writeString(number, UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), writer);
                    continue block71;
                }
                case 60: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    Object value = UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset));
                    writer.writeMessage(number, value, this.getMessageFieldSchema(pos));
                    continue block71;
                }
                case 61: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeBytes(number, (ByteString)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)));
                    continue block71;
                }
                case 62: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeUInt32(number, MessageSchema.oneofIntAt(message, MessageSchema.offset(typeAndOffset)));
                    continue block71;
                }
                case 63: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeEnum(number, MessageSchema.oneofIntAt(message, MessageSchema.offset(typeAndOffset)));
                    continue block71;
                }
                case 64: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeSFixed32(number, MessageSchema.oneofIntAt(message, MessageSchema.offset(typeAndOffset)));
                    continue block71;
                }
                case 65: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeSFixed64(number, MessageSchema.oneofLongAt(message, MessageSchema.offset(typeAndOffset)));
                    continue block71;
                }
                case 66: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeSInt32(number, MessageSchema.oneofIntAt(message, MessageSchema.offset(typeAndOffset)));
                    continue block71;
                }
                case 67: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeSInt64(number, MessageSchema.oneofLongAt(message, MessageSchema.offset(typeAndOffset)));
                    continue block71;
                }
                case 68: {
                    if (!this.isOneofPresent(message, number, pos)) continue block71;
                    writer.writeGroup(number, UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)), this.getMessageFieldSchema(pos));
                    continue block71;
                }
            }
        }
        while (nextExtension != null) {
            this.extensionSchema.serializeExtension(writer, nextExtension);
            nextExtension = extensionIterator.hasNext() ? extensionIterator.next() : null;
        }
    }

    private <K, V> void writeMapHelper(Writer writer, int number, Object mapField, int pos) throws IOException {
        if (mapField != null) {
            writer.writeMap(number, this.mapFieldSchema.forMapMetadata(this.getMapFieldDefaultEntry(pos)), this.mapFieldSchema.forMapData(mapField));
        }
    }

    private <UT, UB> void writeUnknownInMessageTo(UnknownFieldSchema<UT, UB> schema, T message, Writer writer) throws IOException {
        schema.writeTo(schema.getFromMessage(message), writer);
    }

    @Override
    public void mergeFrom(T message, Reader reader, ExtensionRegistryLite extensionRegistry) throws IOException {
        if (extensionRegistry == null) {
            throw new NullPointerException();
        }
        MessageSchema.checkMutable(message);
        this.mergeFromHelper(this.unknownFieldSchema, this.extensionSchema, message, reader, extensionRegistry);
    }

    private <UT, UB, ET extends FieldSet.FieldDescriptorLite<ET>> void mergeFromHelper(UnknownFieldSchema<UT, UB> unknownFieldSchema, ExtensionSchema<ET> extensionSchema, T message, Reader reader, ExtensionRegistryLite extensionRegistry) throws IOException {
        Object unknownFields = null;
        FieldSet<ET> extensions = null;
        try {
            while (true) {
                int number;
                int pos;
                if ((pos = this.positionForFieldNumber(number = reader.getFieldNumber())) < 0) {
                    Object extension;
                    if (number == Integer.MAX_VALUE) {
                        return;
                    }
                    Object object = extension = !this.hasExtensions ? null : extensionSchema.findExtensionByNumber(extensionRegistry, this.defaultInstance, number);
                    if (extension != null) {
                        if (extensions == null) {
                            extensions = extensionSchema.getMutableExtensions(message);
                        }
                        unknownFields = extensionSchema.parseExtension(message, reader, extension, extensionRegistry, extensions, unknownFields, unknownFieldSchema);
                        continue;
                    }
                    if (unknownFieldSchema.shouldDiscardUnknownFields(reader)) {
                        if (reader.skipField()) {
                            continue;
                        }
                    } else {
                        if (unknownFields == null) {
                            unknownFields = unknownFieldSchema.getBuilderFromMessage(message);
                        }
                        if (unknownFieldSchema.mergeOneFieldFrom(unknownFields, reader, 0)) continue;
                    }
                    return;
                }
                int typeAndOffset = this.typeAndOffsetAt(pos);
                try {
                    switch (MessageSchema.type(typeAndOffset)) {
                        case 0: {
                            UnsafeUtil.putDouble(message, MessageSchema.offset(typeAndOffset), reader.readDouble());
                            this.setFieldPresent(message, pos);
                            break;
                        }
                        case 1: {
                            UnsafeUtil.putFloat(message, MessageSchema.offset(typeAndOffset), reader.readFloat());
                            this.setFieldPresent(message, pos);
                            break;
                        }
                        case 2: {
                            UnsafeUtil.putLong(message, MessageSchema.offset(typeAndOffset), reader.readInt64());
                            this.setFieldPresent(message, pos);
                            break;
                        }
                        case 3: {
                            UnsafeUtil.putLong(message, MessageSchema.offset(typeAndOffset), reader.readUInt64());
                            this.setFieldPresent(message, pos);
                            break;
                        }
                        case 4: {
                            UnsafeUtil.putInt(message, MessageSchema.offset(typeAndOffset), reader.readInt32());
                            this.setFieldPresent(message, pos);
                            break;
                        }
                        case 5: {
                            UnsafeUtil.putLong(message, MessageSchema.offset(typeAndOffset), reader.readFixed64());
                            this.setFieldPresent(message, pos);
                            break;
                        }
                        case 6: {
                            UnsafeUtil.putInt(message, MessageSchema.offset(typeAndOffset), reader.readFixed32());
                            this.setFieldPresent(message, pos);
                            break;
                        }
                        case 7: {
                            UnsafeUtil.putBoolean(message, MessageSchema.offset(typeAndOffset), reader.readBool());
                            this.setFieldPresent(message, pos);
                            break;
                        }
                        case 8: {
                            this.readString(message, typeAndOffset, reader);
                            this.setFieldPresent(message, pos);
                            break;
                        }
                        case 9: {
                            MessageLite current = (MessageLite)this.mutableMessageFieldForMerge(message, pos);
                            reader.mergeMessageField(current, this.getMessageFieldSchema(pos), extensionRegistry);
                            this.storeMessageField(message, pos, current);
                            break;
                        }
                        case 10: {
                            UnsafeUtil.putObject(message, MessageSchema.offset(typeAndOffset), (Object)reader.readBytes());
                            this.setFieldPresent(message, pos);
                            break;
                        }
                        case 11: {
                            UnsafeUtil.putInt(message, MessageSchema.offset(typeAndOffset), reader.readUInt32());
                            this.setFieldPresent(message, pos);
                            break;
                        }
                        case 12: {
                            int enumValue = reader.readEnum();
                            Internal.EnumVerifier enumVerifier = this.getEnumFieldVerifier(pos);
                            if (enumVerifier == null || enumVerifier.isInRange(enumValue)) {
                                UnsafeUtil.putInt(message, MessageSchema.offset(typeAndOffset), enumValue);
                                this.setFieldPresent(message, pos);
                                break;
                            }
                            unknownFields = SchemaUtil.storeUnknownEnum(message, number, enumValue, unknownFields, unknownFieldSchema);
                            break;
                        }
                        case 13: {
                            UnsafeUtil.putInt(message, MessageSchema.offset(typeAndOffset), reader.readSFixed32());
                            this.setFieldPresent(message, pos);
                            break;
                        }
                        case 14: {
                            UnsafeUtil.putLong(message, MessageSchema.offset(typeAndOffset), reader.readSFixed64());
                            this.setFieldPresent(message, pos);
                            break;
                        }
                        case 15: {
                            UnsafeUtil.putInt(message, MessageSchema.offset(typeAndOffset), reader.readSInt32());
                            this.setFieldPresent(message, pos);
                            break;
                        }
                        case 16: {
                            UnsafeUtil.putLong(message, MessageSchema.offset(typeAndOffset), reader.readSInt64());
                            this.setFieldPresent(message, pos);
                            break;
                        }
                        case 17: {
                            MessageLite current = (MessageLite)this.mutableMessageFieldForMerge(message, pos);
                            reader.mergeGroupField(current, this.getMessageFieldSchema(pos), extensionRegistry);
                            this.storeMessageField(message, pos, current);
                            break;
                        }
                        case 18: {
                            reader.readDoubleList(this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset)));
                            break;
                        }
                        case 19: {
                            reader.readFloatList(this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset)));
                            break;
                        }
                        case 20: {
                            reader.readInt64List(this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset)));
                            break;
                        }
                        case 21: {
                            reader.readUInt64List(this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset)));
                            break;
                        }
                        case 22: {
                            reader.readInt32List(this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset)));
                            break;
                        }
                        case 23: {
                            reader.readFixed64List(this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset)));
                            break;
                        }
                        case 24: {
                            reader.readFixed32List(this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset)));
                            break;
                        }
                        case 25: {
                            reader.readBoolList(this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset)));
                            break;
                        }
                        case 26: {
                            this.readStringList(message, typeAndOffset, reader);
                            break;
                        }
                        case 27: {
                            this.readMessageList(message, typeAndOffset, reader, this.getMessageFieldSchema(pos), extensionRegistry);
                            break;
                        }
                        case 28: {
                            reader.readBytesList(this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset)));
                            break;
                        }
                        case 29: {
                            reader.readUInt32List(this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset)));
                            break;
                        }
                        case 30: {
                            List<Integer> enumList = this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset));
                            reader.readEnumList(enumList);
                            unknownFields = SchemaUtil.filterUnknownEnumList(message, number, enumList, this.getEnumFieldVerifier(pos), unknownFields, unknownFieldSchema);
                            break;
                        }
                        case 31: {
                            reader.readSFixed32List(this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset)));
                            break;
                        }
                        case 32: {
                            reader.readSFixed64List(this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset)));
                            break;
                        }
                        case 33: {
                            reader.readSInt32List(this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset)));
                            break;
                        }
                        case 34: {
                            reader.readSInt64List(this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset)));
                            break;
                        }
                        case 35: {
                            reader.readDoubleList(this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset)));
                            break;
                        }
                        case 36: {
                            reader.readFloatList(this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset)));
                            break;
                        }
                        case 37: {
                            reader.readInt64List(this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset)));
                            break;
                        }
                        case 38: {
                            reader.readUInt64List(this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset)));
                            break;
                        }
                        case 39: {
                            reader.readInt32List(this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset)));
                            break;
                        }
                        case 40: {
                            reader.readFixed64List(this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset)));
                            break;
                        }
                        case 41: {
                            reader.readFixed32List(this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset)));
                            break;
                        }
                        case 42: {
                            reader.readBoolList(this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset)));
                            break;
                        }
                        case 43: {
                            reader.readUInt32List(this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset)));
                            break;
                        }
                        case 44: {
                            List<Integer> enumList = this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset));
                            reader.readEnumList(enumList);
                            unknownFields = SchemaUtil.filterUnknownEnumList(message, number, enumList, this.getEnumFieldVerifier(pos), unknownFields, unknownFieldSchema);
                            break;
                        }
                        case 45: {
                            reader.readSFixed32List(this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset)));
                            break;
                        }
                        case 46: {
                            reader.readSFixed64List(this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset)));
                            break;
                        }
                        case 47: {
                            reader.readSInt32List(this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset)));
                            break;
                        }
                        case 48: {
                            reader.readSInt64List(this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset)));
                            break;
                        }
                        case 49: {
                            this.readGroupList(message, MessageSchema.offset(typeAndOffset), reader, this.getMessageFieldSchema(pos), extensionRegistry);
                            break;
                        }
                        case 50: {
                            this.mergeMap(message, pos, this.getMapFieldDefaultEntry(pos), extensionRegistry, reader);
                            break;
                        }
                        case 51: {
                            UnsafeUtil.putObject(message, MessageSchema.offset(typeAndOffset), (Object)reader.readDouble());
                            this.setOneofPresent(message, number, pos);
                            break;
                        }
                        case 52: {
                            UnsafeUtil.putObject(message, MessageSchema.offset(typeAndOffset), (Object)Float.valueOf(reader.readFloat()));
                            this.setOneofPresent(message, number, pos);
                            break;
                        }
                        case 53: {
                            UnsafeUtil.putObject(message, MessageSchema.offset(typeAndOffset), (Object)reader.readInt64());
                            this.setOneofPresent(message, number, pos);
                            break;
                        }
                        case 54: {
                            UnsafeUtil.putObject(message, MessageSchema.offset(typeAndOffset), (Object)reader.readUInt64());
                            this.setOneofPresent(message, number, pos);
                            break;
                        }
                        case 55: {
                            UnsafeUtil.putObject(message, MessageSchema.offset(typeAndOffset), (Object)reader.readInt32());
                            this.setOneofPresent(message, number, pos);
                            break;
                        }
                        case 56: {
                            UnsafeUtil.putObject(message, MessageSchema.offset(typeAndOffset), (Object)reader.readFixed64());
                            this.setOneofPresent(message, number, pos);
                            break;
                        }
                        case 57: {
                            UnsafeUtil.putObject(message, MessageSchema.offset(typeAndOffset), (Object)reader.readFixed32());
                            this.setOneofPresent(message, number, pos);
                            break;
                        }
                        case 58: {
                            UnsafeUtil.putObject(message, MessageSchema.offset(typeAndOffset), (Object)reader.readBool());
                            this.setOneofPresent(message, number, pos);
                            break;
                        }
                        case 59: {
                            this.readString(message, typeAndOffset, reader);
                            this.setOneofPresent(message, number, pos);
                            break;
                        }
                        case 60: {
                            MessageLite current = (MessageLite)this.mutableOneofMessageFieldForMerge(message, number, pos);
                            reader.mergeMessageField(current, this.getMessageFieldSchema(pos), extensionRegistry);
                            this.storeOneofMessageField(message, number, pos, current);
                            break;
                        }
                        case 61: {
                            UnsafeUtil.putObject(message, MessageSchema.offset(typeAndOffset), (Object)reader.readBytes());
                            this.setOneofPresent(message, number, pos);
                            break;
                        }
                        case 62: {
                            UnsafeUtil.putObject(message, MessageSchema.offset(typeAndOffset), (Object)reader.readUInt32());
                            this.setOneofPresent(message, number, pos);
                            break;
                        }
                        case 63: {
                            int enumValue = reader.readEnum();
                            Internal.EnumVerifier enumVerifier = this.getEnumFieldVerifier(pos);
                            if (enumVerifier == null || enumVerifier.isInRange(enumValue)) {
                                UnsafeUtil.putObject(message, MessageSchema.offset(typeAndOffset), (Object)enumValue);
                                this.setOneofPresent(message, number, pos);
                                break;
                            }
                            unknownFields = SchemaUtil.storeUnknownEnum(message, number, enumValue, unknownFields, unknownFieldSchema);
                            break;
                        }
                        case 64: {
                            UnsafeUtil.putObject(message, MessageSchema.offset(typeAndOffset), (Object)reader.readSFixed32());
                            this.setOneofPresent(message, number, pos);
                            break;
                        }
                        case 65: {
                            UnsafeUtil.putObject(message, MessageSchema.offset(typeAndOffset), (Object)reader.readSFixed64());
                            this.setOneofPresent(message, number, pos);
                            break;
                        }
                        case 66: {
                            UnsafeUtil.putObject(message, MessageSchema.offset(typeAndOffset), (Object)reader.readSInt32());
                            this.setOneofPresent(message, number, pos);
                            break;
                        }
                        case 67: {
                            UnsafeUtil.putObject(message, MessageSchema.offset(typeAndOffset), (Object)reader.readSInt64());
                            this.setOneofPresent(message, number, pos);
                            break;
                        }
                        case 68: {
                            MessageLite current = (MessageLite)this.mutableOneofMessageFieldForMerge(message, number, pos);
                            reader.mergeGroupField(current, this.getMessageFieldSchema(pos), extensionRegistry);
                            this.storeOneofMessageField(message, number, pos, current);
                            break;
                        }
                        default: {
                            if (unknownFields == null) {
                                unknownFields = unknownFieldSchema.getBuilderFromMessage(message);
                            }
                            if (unknownFieldSchema.mergeOneFieldFrom(unknownFields, reader, 0)) break;
                            return;
                        }
                    }
                }
                catch (InvalidProtocolBufferException.InvalidWireTypeException e) {
                    if (unknownFieldSchema.shouldDiscardUnknownFields(reader)) {
                        if (reader.skipField()) continue;
                        return;
                    }
                    if (unknownFields == null) {
                        unknownFields = unknownFieldSchema.getBuilderFromMessage(message);
                    }
                    if (unknownFieldSchema.mergeOneFieldFrom(unknownFields, reader, 0)) continue;
                    return;
                }
            }
        }
        finally {
            for (int i = this.checkInitializedCount; i < this.repeatedFieldOffsetStart; ++i) {
                unknownFields = this.filterMapUnknownEnumValues(message, this.intArray[i], unknownFields, unknownFieldSchema, message);
            }
            if (unknownFields != null) {
                unknownFieldSchema.setBuilderToMessage(message, unknownFields);
            }
        }
    }

    static UnknownFieldSetLite getMutableUnknownFields(Object message) {
        UnknownFieldSetLite unknownFields = ((GeneratedMessageLite)message).unknownFields;
        if (unknownFields == UnknownFieldSetLite.getDefaultInstance()) {
            ((GeneratedMessageLite)message).unknownFields = unknownFields = UnknownFieldSetLite.newInstance();
        }
        return unknownFields;
    }

    private int decodeMapEntryValue(byte[] data, int position, int limit, WireFormat.FieldType fieldType, Class<?> messageType, ArrayDecoders.Registers registers) throws IOException {
        switch (fieldType) {
            case BOOL: {
                position = ArrayDecoders.decodeVarint64(data, position, registers);
                registers.object1 = registers.long1 != 0L;
                break;
            }
            case BYTES: {
                position = ArrayDecoders.decodeBytes(data, position, registers);
                break;
            }
            case DOUBLE: {
                registers.object1 = ArrayDecoders.decodeDouble(data, position);
                position += 8;
                break;
            }
            case FIXED32: 
            case SFIXED32: {
                registers.object1 = ArrayDecoders.decodeFixed32(data, position);
                position += 4;
                break;
            }
            case FIXED64: 
            case SFIXED64: {
                registers.object1 = ArrayDecoders.decodeFixed64(data, position);
                position += 8;
                break;
            }
            case FLOAT: {
                registers.object1 = Float.valueOf(ArrayDecoders.decodeFloat(data, position));
                position += 4;
                break;
            }
            case ENUM: 
            case INT32: 
            case UINT32: {
                position = ArrayDecoders.decodeVarint32(data, position, registers);
                registers.object1 = registers.int1;
                break;
            }
            case INT64: 
            case UINT64: {
                position = ArrayDecoders.decodeVarint64(data, position, registers);
                registers.object1 = registers.long1;
                break;
            }
            case MESSAGE: {
                position = ArrayDecoders.decodeMessageField(Protobuf.getInstance().schemaFor(messageType), data, position, limit, registers);
                break;
            }
            case SINT32: {
                position = ArrayDecoders.decodeVarint32(data, position, registers);
                registers.object1 = CodedInputStream.decodeZigZag32(registers.int1);
                break;
            }
            case SINT64: {
                position = ArrayDecoders.decodeVarint64(data, position, registers);
                registers.object1 = CodedInputStream.decodeZigZag64(registers.long1);
                break;
            }
            case STRING: {
                position = ArrayDecoders.decodeStringRequireUtf8(data, position, registers);
                break;
            }
            default: {
                throw new RuntimeException("unsupported field type.");
            }
        }
        return position;
    }

    private <K, V> int decodeMapEntry(byte[] data, int position, int limit, MapEntryLite.Metadata<K, V> metadata, Map<K, V> target, ArrayDecoders.Registers registers) throws IOException {
        position = ArrayDecoders.decodeVarint32(data, position, registers);
        int length = registers.int1;
        if (length < 0 || length > limit - position) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        int end = position + length;
        Object key = metadata.defaultKey;
        Object value = metadata.defaultValue;
        block4: while (position < end) {
            int tag;
            if ((tag = data[position++]) < 0) {
                position = ArrayDecoders.decodeVarint32(tag, data, position, registers);
                tag = registers.int1;
            }
            int fieldNumber = tag >>> 3;
            int wireType = tag & 7;
            switch (fieldNumber) {
                case 1: {
                    if (wireType != metadata.keyType.getWireType()) break;
                    position = this.decodeMapEntryValue(data, position, limit, metadata.keyType, null, registers);
                    key = registers.object1;
                    continue block4;
                }
                case 2: {
                    if (wireType != metadata.valueType.getWireType()) break;
                    position = this.decodeMapEntryValue(data, position, limit, metadata.valueType, metadata.defaultValue.getClass(), registers);
                    value = registers.object1;
                    continue block4;
                }
            }
            position = ArrayDecoders.skipField(tag, data, position, limit, registers);
        }
        if (position != end) {
            throw InvalidProtocolBufferException.parseFailure();
        }
        target.put(key, value);
        return end;
    }

    private int parseRepeatedField(T message, byte[] data, int position, int limit, int tag, int number, int wireType, int bufferPosition, long typeAndOffset, int fieldType, long fieldOffset, ArrayDecoders.Registers registers) throws IOException {
        Internal.ProtobufList<Object> list = (Internal.ProtobufList<Object>)UNSAFE.getObject(message, fieldOffset);
        if (!list.isModifiable()) {
            int size = list.size();
            list = list.mutableCopyWithCapacity(size * 2);
            UNSAFE.putObject(message, fieldOffset, list);
        }
        switch (fieldType) {
            case 18: 
            case 35: {
                if (wireType == 2) {
                    position = ArrayDecoders.decodePackedDoubleList(data, position, list, registers);
                    break;
                }
                if (wireType != 1) break;
                position = ArrayDecoders.decodeDoubleList(tag, data, position, limit, list, registers);
                break;
            }
            case 19: 
            case 36: {
                if (wireType == 2) {
                    position = ArrayDecoders.decodePackedFloatList(data, position, list, registers);
                    break;
                }
                if (wireType != 5) break;
                position = ArrayDecoders.decodeFloatList(tag, data, position, limit, list, registers);
                break;
            }
            case 20: 
            case 21: 
            case 37: 
            case 38: {
                if (wireType == 2) {
                    position = ArrayDecoders.decodePackedVarint64List(data, position, list, registers);
                    break;
                }
                if (wireType != 0) break;
                position = ArrayDecoders.decodeVarint64List(tag, data, position, limit, list, registers);
                break;
            }
            case 22: 
            case 29: 
            case 39: 
            case 43: {
                if (wireType == 2) {
                    position = ArrayDecoders.decodePackedVarint32List(data, position, list, registers);
                    break;
                }
                if (wireType != 0) break;
                position = ArrayDecoders.decodeVarint32List(tag, data, position, limit, list, registers);
                break;
            }
            case 23: 
            case 32: 
            case 40: 
            case 46: {
                if (wireType == 2) {
                    position = ArrayDecoders.decodePackedFixed64List(data, position, list, registers);
                    break;
                }
                if (wireType != 1) break;
                position = ArrayDecoders.decodeFixed64List(tag, data, position, limit, list, registers);
                break;
            }
            case 24: 
            case 31: 
            case 41: 
            case 45: {
                if (wireType == 2) {
                    position = ArrayDecoders.decodePackedFixed32List(data, position, list, registers);
                    break;
                }
                if (wireType != 5) break;
                position = ArrayDecoders.decodeFixed32List(tag, data, position, limit, list, registers);
                break;
            }
            case 25: 
            case 42: {
                if (wireType == 2) {
                    position = ArrayDecoders.decodePackedBoolList(data, position, list, registers);
                    break;
                }
                if (wireType != 0) break;
                position = ArrayDecoders.decodeBoolList(tag, data, position, limit, list, registers);
                break;
            }
            case 26: {
                if (wireType != 2) break;
                if ((typeAndOffset & 0x20000000L) == 0L) {
                    position = ArrayDecoders.decodeStringList(tag, data, position, limit, list, registers);
                    break;
                }
                position = ArrayDecoders.decodeStringListRequireUtf8(tag, data, position, limit, list, registers);
                break;
            }
            case 27: {
                if (wireType != 2) break;
                position = ArrayDecoders.decodeMessageList(this.getMessageFieldSchema(bufferPosition), tag, data, position, limit, list, registers);
                break;
            }
            case 28: {
                if (wireType != 2) break;
                position = ArrayDecoders.decodeBytesList(tag, data, position, limit, list, registers);
                break;
            }
            case 30: 
            case 44: {
                if (wireType == 2) {
                    position = ArrayDecoders.decodePackedVarint32List(data, position, list, registers);
                } else {
                    if (wireType != 0) break;
                    position = ArrayDecoders.decodeVarint32List(tag, data, position, limit, list, registers);
                }
                SchemaUtil.filterUnknownEnumList(message, number, list, this.getEnumFieldVerifier(bufferPosition), null, this.unknownFieldSchema);
                break;
            }
            case 33: 
            case 47: {
                if (wireType == 2) {
                    position = ArrayDecoders.decodePackedSInt32List(data, position, list, registers);
                    break;
                }
                if (wireType != 0) break;
                position = ArrayDecoders.decodeSInt32List(tag, data, position, limit, list, registers);
                break;
            }
            case 34: 
            case 48: {
                if (wireType == 2) {
                    position = ArrayDecoders.decodePackedSInt64List(data, position, list, registers);
                    break;
                }
                if (wireType != 0) break;
                position = ArrayDecoders.decodeSInt64List(tag, data, position, limit, list, registers);
                break;
            }
            case 49: {
                if (wireType != 3) break;
                position = ArrayDecoders.decodeGroupList(this.getMessageFieldSchema(bufferPosition), tag, data, position, limit, list, registers);
                break;
            }
        }
        return position;
    }

    private <K, V> int parseMapField(T message, byte[] data, int position, int limit, int bufferPosition, long fieldOffset, ArrayDecoders.Registers registers) throws IOException {
        Unsafe unsafe = UNSAFE;
        Object mapDefaultEntry = this.getMapFieldDefaultEntry(bufferPosition);
        Object mapField = unsafe.getObject(message, fieldOffset);
        if (this.mapFieldSchema.isImmutable(mapField)) {
            Object oldMapField = mapField;
            mapField = this.mapFieldSchema.newMapField(mapDefaultEntry);
            this.mapFieldSchema.mergeFrom(mapField, oldMapField);
            unsafe.putObject(message, fieldOffset, mapField);
        }
        return this.decodeMapEntry(data, position, limit, this.mapFieldSchema.forMapMetadata(mapDefaultEntry), this.mapFieldSchema.forMutableMapData(mapField), registers);
    }

    private int parseOneofField(T message, byte[] data, int position, int limit, int tag, int number, int wireType, int typeAndOffset, int fieldType, long fieldOffset, int bufferPosition, ArrayDecoders.Registers registers) throws IOException {
        Unsafe unsafe = UNSAFE;
        long oneofCaseOffset = this.buffer[bufferPosition + 2] & 0xFFFFF;
        switch (fieldType) {
            case 51: {
                if (wireType != 1) break;
                unsafe.putObject(message, fieldOffset, ArrayDecoders.decodeDouble(data, position));
                position += 8;
                unsafe.putInt(message, oneofCaseOffset, number);
                break;
            }
            case 52: {
                if (wireType != 5) break;
                unsafe.putObject(message, fieldOffset, Float.valueOf(ArrayDecoders.decodeFloat(data, position)));
                position += 4;
                unsafe.putInt(message, oneofCaseOffset, number);
                break;
            }
            case 53: 
            case 54: {
                if (wireType != 0) break;
                position = ArrayDecoders.decodeVarint64(data, position, registers);
                unsafe.putObject(message, fieldOffset, registers.long1);
                unsafe.putInt(message, oneofCaseOffset, number);
                break;
            }
            case 55: 
            case 62: {
                if (wireType != 0) break;
                position = ArrayDecoders.decodeVarint32(data, position, registers);
                unsafe.putObject(message, fieldOffset, registers.int1);
                unsafe.putInt(message, oneofCaseOffset, number);
                break;
            }
            case 56: 
            case 65: {
                if (wireType != 1) break;
                unsafe.putObject(message, fieldOffset, ArrayDecoders.decodeFixed64(data, position));
                position += 8;
                unsafe.putInt(message, oneofCaseOffset, number);
                break;
            }
            case 57: 
            case 64: {
                if (wireType != 5) break;
                unsafe.putObject(message, fieldOffset, ArrayDecoders.decodeFixed32(data, position));
                position += 4;
                unsafe.putInt(message, oneofCaseOffset, number);
                break;
            }
            case 58: {
                if (wireType != 0) break;
                position = ArrayDecoders.decodeVarint64(data, position, registers);
                unsafe.putObject(message, fieldOffset, registers.long1 != 0L);
                unsafe.putInt(message, oneofCaseOffset, number);
                break;
            }
            case 59: {
                if (wireType != 2) break;
                position = ArrayDecoders.decodeVarint32(data, position, registers);
                int length = registers.int1;
                if (length == 0) {
                    unsafe.putObject(message, fieldOffset, "");
                } else {
                    if ((typeAndOffset & 0x20000000) != 0 && !Utf8.isValidUtf8(data, position, position + length)) {
                        throw InvalidProtocolBufferException.invalidUtf8();
                    }
                    String value = new String(data, position, length, Internal.UTF_8);
                    unsafe.putObject(message, fieldOffset, value);
                    position += length;
                }
                unsafe.putInt(message, oneofCaseOffset, number);
                break;
            }
            case 60: {
                if (wireType != 2) break;
                Object current = this.mutableOneofMessageFieldForMerge(message, number, bufferPosition);
                position = ArrayDecoders.mergeMessageField(current, this.getMessageFieldSchema(bufferPosition), data, position, limit, registers);
                this.storeOneofMessageField(message, number, bufferPosition, current);
                break;
            }
            case 61: {
                if (wireType != 2) break;
                position = ArrayDecoders.decodeBytes(data, position, registers);
                unsafe.putObject(message, fieldOffset, registers.object1);
                unsafe.putInt(message, oneofCaseOffset, number);
                break;
            }
            case 63: {
                if (wireType != 0) break;
                position = ArrayDecoders.decodeVarint32(data, position, registers);
                int enumValue = registers.int1;
                Internal.EnumVerifier enumVerifier = this.getEnumFieldVerifier(bufferPosition);
                if (enumVerifier == null || enumVerifier.isInRange(enumValue)) {
                    unsafe.putObject(message, fieldOffset, enumValue);
                    unsafe.putInt(message, oneofCaseOffset, number);
                    break;
                }
                MessageSchema.getMutableUnknownFields(message).storeField(tag, enumValue);
                break;
            }
            case 66: {
                if (wireType != 0) break;
                position = ArrayDecoders.decodeVarint32(data, position, registers);
                unsafe.putObject(message, fieldOffset, CodedInputStream.decodeZigZag32(registers.int1));
                unsafe.putInt(message, oneofCaseOffset, number);
                break;
            }
            case 67: {
                if (wireType != 0) break;
                position = ArrayDecoders.decodeVarint64(data, position, registers);
                unsafe.putObject(message, fieldOffset, CodedInputStream.decodeZigZag64(registers.long1));
                unsafe.putInt(message, oneofCaseOffset, number);
                break;
            }
            case 68: {
                if (wireType != 3) break;
                Object current = this.mutableOneofMessageFieldForMerge(message, number, bufferPosition);
                int endTag = tag & 0xFFFFFFF8 | 4;
                position = ArrayDecoders.mergeGroupField(current, this.getMessageFieldSchema(bufferPosition), data, position, limit, endTag, registers);
                this.storeOneofMessageField(message, number, bufferPosition, current);
                break;
            }
        }
        return position;
    }

    private Schema getMessageFieldSchema(int pos) {
        int index = pos / 3 * 2;
        Schema<Class> schema = (Schema<Class>)this.objects[index];
        if (schema != null) {
            return schema;
        }
        this.objects[index] = schema = Protobuf.getInstance().schemaFor((Class)this.objects[index + 1]);
        return schema;
    }

    private Object getMapFieldDefaultEntry(int pos) {
        return this.objects[pos / 3 * 2];
    }

    private Internal.EnumVerifier getEnumFieldVerifier(int pos) {
        return (Internal.EnumVerifier)this.objects[pos / 3 * 2 + 1];
    }

    @CanIgnoreReturnValue
    int parseMessage(T message, byte[] data, int position, int limit, int endDelimited, ArrayDecoders.Registers registers) throws IOException {
        MessageSchema.checkMutable(message);
        Unsafe unsafe = UNSAFE;
        int currentPresenceFieldOffset = 1048575;
        int currentPresenceField = 0;
        int tag = 0;
        int oldNumber = -1;
        int pos = 0;
        block16: while (position < limit) {
            if ((tag = data[position++]) < 0) {
                position = ArrayDecoders.decodeVarint32(tag, data, position, registers);
                tag = registers.int1;
            }
            int number = tag >>> 3;
            int wireType = tag & 7;
            pos = number > oldNumber ? this.positionForFieldNumber(number, pos / 3) : this.positionForFieldNumber(number);
            oldNumber = number;
            if (pos == -1) {
                pos = 0;
            } else {
                int oldPosition;
                int typeAndOffset = this.buffer[pos + 1];
                int fieldType = MessageSchema.type(typeAndOffset);
                long fieldOffset = MessageSchema.offset(typeAndOffset);
                if (fieldType <= 17) {
                    int presenceMaskAndOffset = this.buffer[pos + 2];
                    int presenceMask = 1 << (presenceMaskAndOffset >>> 20);
                    int presenceFieldOffset = presenceMaskAndOffset & 0xFFFFF;
                    if (presenceFieldOffset != currentPresenceFieldOffset) {
                        if (currentPresenceFieldOffset != 1048575) {
                            unsafe.putInt(message, currentPresenceFieldOffset, currentPresenceField);
                        }
                        currentPresenceFieldOffset = presenceFieldOffset;
                        currentPresenceField = presenceFieldOffset == 1048575 ? 0 : unsafe.getInt(message, presenceFieldOffset);
                    }
                    switch (fieldType) {
                        case 0: {
                            if (wireType != 1) break;
                            UnsafeUtil.putDouble(message, fieldOffset, ArrayDecoders.decodeDouble(data, position));
                            position += 8;
                            currentPresenceField |= presenceMask;
                            continue block16;
                        }
                        case 1: {
                            if (wireType != 5) break;
                            UnsafeUtil.putFloat(message, fieldOffset, ArrayDecoders.decodeFloat(data, position));
                            position += 4;
                            currentPresenceField |= presenceMask;
                            continue block16;
                        }
                        case 2: 
                        case 3: {
                            if (wireType != 0) break;
                            position = ArrayDecoders.decodeVarint64(data, position, registers);
                            unsafe.putLong(message, fieldOffset, registers.long1);
                            currentPresenceField |= presenceMask;
                            continue block16;
                        }
                        case 4: 
                        case 11: {
                            if (wireType != 0) break;
                            position = ArrayDecoders.decodeVarint32(data, position, registers);
                            unsafe.putInt(message, fieldOffset, registers.int1);
                            currentPresenceField |= presenceMask;
                            continue block16;
                        }
                        case 5: 
                        case 14: {
                            if (wireType != 1) break;
                            unsafe.putLong(message, fieldOffset, ArrayDecoders.decodeFixed64(data, position));
                            position += 8;
                            currentPresenceField |= presenceMask;
                            continue block16;
                        }
                        case 6: 
                        case 13: {
                            if (wireType != 5) break;
                            unsafe.putInt(message, fieldOffset, ArrayDecoders.decodeFixed32(data, position));
                            position += 4;
                            currentPresenceField |= presenceMask;
                            continue block16;
                        }
                        case 7: {
                            if (wireType != 0) break;
                            position = ArrayDecoders.decodeVarint64(data, position, registers);
                            UnsafeUtil.putBoolean(message, fieldOffset, registers.long1 != 0L);
                            currentPresenceField |= presenceMask;
                            continue block16;
                        }
                        case 8: {
                            if (wireType != 2) break;
                            position = MessageSchema.isEnforceUtf8(typeAndOffset) ? ArrayDecoders.decodeStringRequireUtf8(data, position, registers) : ArrayDecoders.decodeString(data, position, registers);
                            unsafe.putObject(message, fieldOffset, registers.object1);
                            currentPresenceField |= presenceMask;
                            continue block16;
                        }
                        case 9: {
                            if (wireType != 2) break;
                            Object current = this.mutableMessageFieldForMerge(message, pos);
                            position = ArrayDecoders.mergeMessageField(current, this.getMessageFieldSchema(pos), data, position, limit, registers);
                            this.storeMessageField(message, pos, current);
                            currentPresenceField |= presenceMask;
                            continue block16;
                        }
                        case 10: {
                            if (wireType != 2) break;
                            position = ArrayDecoders.decodeBytes(data, position, registers);
                            unsafe.putObject(message, fieldOffset, registers.object1);
                            currentPresenceField |= presenceMask;
                            continue block16;
                        }
                        case 12: {
                            if (wireType != 0) break;
                            position = ArrayDecoders.decodeVarint32(data, position, registers);
                            int enumValue = registers.int1;
                            Internal.EnumVerifier enumVerifier = this.getEnumFieldVerifier(pos);
                            if (!MessageSchema.isLegacyEnumIsClosed(typeAndOffset) || enumVerifier == null || enumVerifier.isInRange(enumValue)) {
                                unsafe.putInt(message, fieldOffset, enumValue);
                                currentPresenceField |= presenceMask;
                                continue block16;
                            }
                            MessageSchema.getMutableUnknownFields(message).storeField(tag, enumValue);
                            continue block16;
                        }
                        case 15: {
                            if (wireType != 0) break;
                            position = ArrayDecoders.decodeVarint32(data, position, registers);
                            unsafe.putInt(message, fieldOffset, CodedInputStream.decodeZigZag32(registers.int1));
                            currentPresenceField |= presenceMask;
                            continue block16;
                        }
                        case 16: {
                            if (wireType != 0) break;
                            position = ArrayDecoders.decodeVarint64(data, position, registers);
                            unsafe.putLong(message, fieldOffset, CodedInputStream.decodeZigZag64(registers.long1));
                            currentPresenceField |= presenceMask;
                            continue block16;
                        }
                        case 17: {
                            if (wireType != 3) break;
                            Object current = this.mutableMessageFieldForMerge(message, pos);
                            int endTag = number << 3 | 4;
                            position = ArrayDecoders.mergeGroupField(current, this.getMessageFieldSchema(pos), data, position, limit, endTag, registers);
                            this.storeMessageField(message, pos, current);
                            currentPresenceField |= presenceMask;
                            continue block16;
                        }
                    }
                } else if (fieldType == 27) {
                    if (wireType == 2) {
                        Internal.ProtobufList list = (Internal.ProtobufList)unsafe.getObject(message, fieldOffset);
                        if (!list.isModifiable()) {
                            int size = list.size();
                            list = list.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
                            unsafe.putObject(message, fieldOffset, list);
                        }
                        position = ArrayDecoders.decodeMessageList(this.getMessageFieldSchema(pos), tag, data, position, limit, list, registers);
                        continue;
                    }
                } else if (fieldType <= 49) {
                    oldPosition = position;
                    if ((position = this.parseRepeatedField(message, data, position, limit, tag, number, wireType, pos, typeAndOffset, fieldType, fieldOffset, registers)) != oldPosition) {
                        continue;
                    }
                } else if (fieldType == 50) {
                    if (wireType == 2) {
                        oldPosition = position;
                        if ((position = this.parseMapField(message, data, position, limit, pos, fieldOffset, registers)) != oldPosition) {
                            continue;
                        }
                    }
                } else {
                    oldPosition = position;
                    if ((position = this.parseOneofField(message, data, position, limit, tag, number, wireType, typeAndOffset, fieldType, fieldOffset, pos, registers)) != oldPosition) continue;
                }
            }
            if (tag == endDelimited && endDelimited != 0) break;
            if (this.hasExtensions && registers.extensionRegistry != ExtensionRegistryLite.getEmptyRegistry()) {
                position = ArrayDecoders.decodeExtensionOrUnknownField(tag, data, position, limit, message, this.defaultInstance, this.unknownFieldSchema, registers);
                continue;
            }
            position = ArrayDecoders.decodeUnknownField(tag, data, position, limit, MessageSchema.getMutableUnknownFields(message), registers);
        }
        if (currentPresenceFieldOffset != 1048575) {
            unsafe.putInt(message, currentPresenceFieldOffset, currentPresenceField);
        }
        UnknownFieldSetLite unknownFields = null;
        for (int i = this.checkInitializedCount; i < this.repeatedFieldOffsetStart; ++i) {
            unknownFields = this.filterMapUnknownEnumValues(message, this.intArray[i], unknownFields, this.unknownFieldSchema, message);
        }
        if (unknownFields != null) {
            this.unknownFieldSchema.setBuilderToMessage(message, unknownFields);
        }
        if (endDelimited == 0 ? position != limit : position > limit || tag != endDelimited) {
            throw InvalidProtocolBufferException.parseFailure();
        }
        return position;
    }

    private Object mutableMessageFieldForMerge(T message, int pos) {
        Schema fieldSchema = this.getMessageFieldSchema(pos);
        long offset = MessageSchema.offset(this.typeAndOffsetAt(pos));
        if (!this.isFieldPresent(message, pos)) {
            return fieldSchema.newInstance();
        }
        Object current = UNSAFE.getObject(message, offset);
        if (MessageSchema.isMutable(current)) {
            return current;
        }
        Object newMessage = fieldSchema.newInstance();
        if (current != null) {
            fieldSchema.mergeFrom(newMessage, current);
        }
        return newMessage;
    }

    private void storeMessageField(T message, int pos, Object field) {
        UNSAFE.putObject(message, MessageSchema.offset(this.typeAndOffsetAt(pos)), field);
        this.setFieldPresent(message, pos);
    }

    private Object mutableOneofMessageFieldForMerge(T message, int fieldNumber, int pos) {
        Schema fieldSchema = this.getMessageFieldSchema(pos);
        if (!this.isOneofPresent(message, fieldNumber, pos)) {
            return fieldSchema.newInstance();
        }
        Object current = UNSAFE.getObject(message, MessageSchema.offset(this.typeAndOffsetAt(pos)));
        if (MessageSchema.isMutable(current)) {
            return current;
        }
        Object newMessage = fieldSchema.newInstance();
        if (current != null) {
            fieldSchema.mergeFrom(newMessage, current);
        }
        return newMessage;
    }

    private void storeOneofMessageField(T message, int fieldNumber, int pos, Object field) {
        UNSAFE.putObject(message, MessageSchema.offset(this.typeAndOffsetAt(pos)), field);
        this.setOneofPresent(message, fieldNumber, pos);
    }

    @Override
    public void mergeFrom(T message, byte[] data, int position, int limit, ArrayDecoders.Registers registers) throws IOException {
        this.parseMessage(message, data, position, limit, 0, registers);
    }

    @Override
    public void makeImmutable(T message) {
        if (!MessageSchema.isMutable(message)) {
            return;
        }
        if (message instanceof GeneratedMessageLite) {
            GeneratedMessageLite generatedMessage = (GeneratedMessageLite)message;
            generatedMessage.clearMemoizedSerializedSize();
            generatedMessage.clearMemoizedHashCode();
            generatedMessage.markImmutable();
        }
        int bufferLength = this.buffer.length;
        block6: for (int pos = 0; pos < bufferLength; pos += 3) {
            int typeAndOffset = this.typeAndOffsetAt(pos);
            long offset = MessageSchema.offset(typeAndOffset);
            switch (MessageSchema.type(typeAndOffset)) {
                case 9: 
                case 17: {
                    if (!this.isFieldPresent(message, pos)) continue block6;
                    this.getMessageFieldSchema(pos).makeImmutable(UNSAFE.getObject(message, offset));
                    continue block6;
                }
                case 60: 
                case 68: {
                    if (!this.isOneofPresent(message, this.numberAt(pos), pos)) continue block6;
                    this.getMessageFieldSchema(pos).makeImmutable(UNSAFE.getObject(message, offset));
                    continue block6;
                }
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 22: 
                case 23: 
                case 24: 
                case 25: 
                case 26: 
                case 27: 
                case 28: 
                case 29: 
                case 30: 
                case 31: 
                case 32: 
                case 33: 
                case 34: 
                case 35: 
                case 36: 
                case 37: 
                case 38: 
                case 39: 
                case 40: 
                case 41: 
                case 42: 
                case 43: 
                case 44: 
                case 45: 
                case 46: 
                case 47: 
                case 48: 
                case 49: {
                    this.listFieldSchema.makeImmutableListAt(message, offset);
                    continue block6;
                }
                case 50: {
                    Object mapField = UNSAFE.getObject(message, offset);
                    if (mapField == null) continue block6;
                    UNSAFE.putObject(message, offset, this.mapFieldSchema.toImmutable(mapField));
                }
            }
        }
        this.unknownFieldSchema.makeImmutable(message);
        if (this.hasExtensions) {
            this.extensionSchema.makeImmutable(message);
        }
    }

    private final <K, V> void mergeMap(Object message, int pos, Object mapDefaultEntry, ExtensionRegistryLite extensionRegistry, Reader reader) throws IOException {
        long offset = MessageSchema.offset(this.typeAndOffsetAt(pos));
        Object mapField = UnsafeUtil.getObject(message, offset);
        if (mapField == null) {
            mapField = this.mapFieldSchema.newMapField(mapDefaultEntry);
            UnsafeUtil.putObject(message, offset, mapField);
        } else if (this.mapFieldSchema.isImmutable(mapField)) {
            Object oldMapField = mapField;
            mapField = this.mapFieldSchema.newMapField(mapDefaultEntry);
            this.mapFieldSchema.mergeFrom(mapField, oldMapField);
            UnsafeUtil.putObject(message, offset, mapField);
        }
        reader.readMap(this.mapFieldSchema.forMutableMapData(mapField), this.mapFieldSchema.forMapMetadata(mapDefaultEntry), extensionRegistry);
    }

    private <UT, UB> UB filterMapUnknownEnumValues(Object message, int pos, UB unknownFields, UnknownFieldSchema<UT, UB> unknownFieldSchema, Object containerMessage) {
        int fieldNumber = this.numberAt(pos);
        long offset = MessageSchema.offset(this.typeAndOffsetAt(pos));
        Object mapField = UnsafeUtil.getObject(message, offset);
        if (mapField == null) {
            return unknownFields;
        }
        Internal.EnumVerifier enumVerifier = this.getEnumFieldVerifier(pos);
        if (enumVerifier == null) {
            return unknownFields;
        }
        Map<?, ?> mapData = this.mapFieldSchema.forMutableMapData(mapField);
        unknownFields = this.filterUnknownEnumMap(pos, fieldNumber, mapData, enumVerifier, unknownFields, unknownFieldSchema, containerMessage);
        return unknownFields;
    }

    private <K, V, UT, UB> UB filterUnknownEnumMap(int pos, int number, Map<K, V> mapData, Internal.EnumVerifier enumVerifier, UB unknownFields, UnknownFieldSchema<UT, UB> unknownFieldSchema, Object containerMessage) {
        MapEntryLite.Metadata<?, ?> metadata = this.mapFieldSchema.forMapMetadata(this.getMapFieldDefaultEntry(pos));
        Iterator<Map.Entry<K, V>> it = mapData.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<K, V> entry = it.next();
            if (enumVerifier.isInRange((Integer)entry.getValue())) continue;
            if (unknownFields == null) {
                unknownFields = unknownFieldSchema.getBuilderFromMessage(containerMessage);
            }
            int entrySize = MapEntryLite.computeSerializedSize(metadata, entry.getKey(), entry.getValue());
            ByteString.CodedBuilder codedBuilder = ByteString.newCodedBuilder(entrySize);
            CodedOutputStream codedOutput = codedBuilder.getCodedOutput();
            try {
                MapEntryLite.writeTo(codedOutput, metadata, entry.getKey(), entry.getValue());
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            unknownFieldSchema.addLengthDelimited(unknownFields, number, codedBuilder.build());
            it.remove();
        }
        return unknownFields;
    }

    @Override
    public final boolean isInitialized(T message) {
        int currentPresenceFieldOffset = 1048575;
        int currentPresenceField = 0;
        block6: for (int i = 0; i < this.checkInitializedCount; ++i) {
            int pos = this.intArray[i];
            int number = this.numberAt(pos);
            int typeAndOffset = this.typeAndOffsetAt(pos);
            int presenceMaskAndOffset = this.buffer[pos + 2];
            int presenceFieldOffset = presenceMaskAndOffset & 0xFFFFF;
            int presenceMask = 1 << (presenceMaskAndOffset >>> 20);
            if (presenceFieldOffset != currentPresenceFieldOffset && (currentPresenceFieldOffset = presenceFieldOffset) != 1048575) {
                currentPresenceField = UNSAFE.getInt(message, presenceFieldOffset);
            }
            if (MessageSchema.isRequired(typeAndOffset) && !this.isFieldPresent(message, pos, currentPresenceFieldOffset, currentPresenceField, presenceMask)) {
                return false;
            }
            switch (MessageSchema.type(typeAndOffset)) {
                case 9: 
                case 17: {
                    if (!this.isFieldPresent(message, pos, currentPresenceFieldOffset, currentPresenceField, presenceMask) || MessageSchema.isInitialized(message, typeAndOffset, this.getMessageFieldSchema(pos))) continue block6;
                    return false;
                }
                case 27: 
                case 49: {
                    if (this.isListInitialized(message, typeAndOffset, pos)) continue block6;
                    return false;
                }
                case 60: 
                case 68: {
                    if (!this.isOneofPresent(message, number, pos) || MessageSchema.isInitialized(message, typeAndOffset, this.getMessageFieldSchema(pos))) continue block6;
                    return false;
                }
                case 50: {
                    if (this.isMapInitialized(message, typeAndOffset, pos)) continue block6;
                    return false;
                }
            }
        }
        return !this.hasExtensions || this.extensionSchema.getExtensions(message).isInitialized();
    }

    private static boolean isInitialized(Object message, int typeAndOffset, Schema schema) {
        Object nested = UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset));
        return schema.isInitialized(nested);
    }

    private <N> boolean isListInitialized(Object message, int typeAndOffset, int pos) {
        List list = (List)UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset));
        if (list.isEmpty()) {
            return true;
        }
        Schema schema = this.getMessageFieldSchema(pos);
        for (int i = 0; i < list.size(); ++i) {
            Object nested = list.get(i);
            if (schema.isInitialized(nested)) continue;
            return false;
        }
        return true;
    }

    private boolean isMapInitialized(T message, int typeAndOffset, int pos) {
        Map<?, ?> map = this.mapFieldSchema.forMapData(UnsafeUtil.getObject(message, MessageSchema.offset(typeAndOffset)));
        if (map.isEmpty()) {
            return true;
        }
        Object mapDefaultEntry = this.getMapFieldDefaultEntry(pos);
        MapEntryLite.Metadata<?, ?> metadata = this.mapFieldSchema.forMapMetadata(mapDefaultEntry);
        if (metadata.valueType.getJavaType() != WireFormat.JavaType.MESSAGE) {
            return true;
        }
        Schema<Class<?>> schema = null;
        for (Object nested : map.values()) {
            if (schema == null) {
                schema = Protobuf.getInstance().schemaFor(nested.getClass());
            }
            if (schema.isInitialized((Class<?>)nested)) continue;
            return false;
        }
        return true;
    }

    private void writeString(int fieldNumber, Object value, Writer writer) throws IOException {
        if (value instanceof String) {
            writer.writeString(fieldNumber, (String)value);
        } else {
            writer.writeBytes(fieldNumber, (ByteString)value);
        }
    }

    private void readString(Object message, int typeAndOffset, Reader reader) throws IOException {
        if (MessageSchema.isEnforceUtf8(typeAndOffset)) {
            UnsafeUtil.putObject(message, MessageSchema.offset(typeAndOffset), (Object)reader.readStringRequireUtf8());
        } else if (this.lite) {
            UnsafeUtil.putObject(message, MessageSchema.offset(typeAndOffset), (Object)reader.readString());
        } else {
            UnsafeUtil.putObject(message, MessageSchema.offset(typeAndOffset), (Object)reader.readBytes());
        }
    }

    private void readStringList(Object message, int typeAndOffset, Reader reader) throws IOException {
        if (MessageSchema.isEnforceUtf8(typeAndOffset)) {
            reader.readStringListRequireUtf8(this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset)));
        } else {
            reader.readStringList(this.listFieldSchema.mutableListAt(message, MessageSchema.offset(typeAndOffset)));
        }
    }

    private <E> void readMessageList(Object message, int typeAndOffset, Reader reader, Schema<E> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
        long offset = MessageSchema.offset(typeAndOffset);
        reader.readMessageList(this.listFieldSchema.mutableListAt(message, offset), schema, extensionRegistry);
    }

    private <E> void readGroupList(Object message, long offset, Reader reader, Schema<E> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
        reader.readGroupList(this.listFieldSchema.mutableListAt(message, offset), schema, extensionRegistry);
    }

    private int numberAt(int pos) {
        return this.buffer[pos];
    }

    private int typeAndOffsetAt(int pos) {
        return this.buffer[pos + 1];
    }

    private int presenceMaskAndOffsetAt(int pos) {
        return this.buffer[pos + 2];
    }

    private static int type(int value) {
        return (value & 0xFF00000) >>> 20;
    }

    private static boolean isRequired(int value) {
        return (value & 0x10000000) != 0;
    }

    private static boolean isEnforceUtf8(int value) {
        return (value & 0x20000000) != 0;
    }

    private static boolean isLegacyEnumIsClosed(int value) {
        return (value & Integer.MIN_VALUE) != 0;
    }

    private static long offset(int value) {
        return value & 0xFFFFF;
    }

    private static boolean isMutable(Object message) {
        if (message == null) {
            return false;
        }
        if (message instanceof GeneratedMessageLite) {
            return ((GeneratedMessageLite)message).isMutable();
        }
        return true;
    }

    private static void checkMutable(Object message) {
        if (!MessageSchema.isMutable(message)) {
            throw new IllegalArgumentException("Mutating immutable message: " + message);
        }
    }

    private static <T> double doubleAt(T message, long offset) {
        return UnsafeUtil.getDouble(message, offset);
    }

    private static <T> float floatAt(T message, long offset) {
        return UnsafeUtil.getFloat(message, offset);
    }

    private static <T> int intAt(T message, long offset) {
        return UnsafeUtil.getInt(message, offset);
    }

    private static <T> long longAt(T message, long offset) {
        return UnsafeUtil.getLong(message, offset);
    }

    private static <T> boolean booleanAt(T message, long offset) {
        return UnsafeUtil.getBoolean(message, offset);
    }

    private static <T> double oneofDoubleAt(T message, long offset) {
        return (Double)UnsafeUtil.getObject(message, offset);
    }

    private static <T> float oneofFloatAt(T message, long offset) {
        return ((Float)UnsafeUtil.getObject(message, offset)).floatValue();
    }

    private static <T> int oneofIntAt(T message, long offset) {
        return (Integer)UnsafeUtil.getObject(message, offset);
    }

    private static <T> long oneofLongAt(T message, long offset) {
        return (Long)UnsafeUtil.getObject(message, offset);
    }

    private static <T> boolean oneofBooleanAt(T message, long offset) {
        return (Boolean)UnsafeUtil.getObject(message, offset);
    }

    private boolean arePresentForEquals(T message, T other, int pos) {
        return this.isFieldPresent(message, pos) == this.isFieldPresent(other, pos);
    }

    private boolean isFieldPresent(T message, int pos, int presenceFieldOffset, int presenceField, int presenceMask) {
        if (presenceFieldOffset == 1048575) {
            return this.isFieldPresent(message, pos);
        }
        return (presenceField & presenceMask) != 0;
    }

    private boolean isFieldPresent(T message, int pos) {
        int presenceMaskAndOffset = this.presenceMaskAndOffsetAt(pos);
        long presenceFieldOffset = presenceMaskAndOffset & 0xFFFFF;
        if (presenceFieldOffset == 1048575L) {
            int typeAndOffset = this.typeAndOffsetAt(pos);
            long offset = MessageSchema.offset(typeAndOffset);
            switch (MessageSchema.type(typeAndOffset)) {
                case 0: {
                    return Double.doubleToRawLongBits(UnsafeUtil.getDouble(message, offset)) != 0L;
                }
                case 1: {
                    return Float.floatToRawIntBits(UnsafeUtil.getFloat(message, offset)) != 0;
                }
                case 2: {
                    return UnsafeUtil.getLong(message, offset) != 0L;
                }
                case 3: {
                    return UnsafeUtil.getLong(message, offset) != 0L;
                }
                case 4: {
                    return UnsafeUtil.getInt(message, offset) != 0;
                }
                case 5: {
                    return UnsafeUtil.getLong(message, offset) != 0L;
                }
                case 6: {
                    return UnsafeUtil.getInt(message, offset) != 0;
                }
                case 7: {
                    return UnsafeUtil.getBoolean(message, offset);
                }
                case 8: {
                    Object value = UnsafeUtil.getObject(message, offset);
                    if (value instanceof String) {
                        return !((String)value).isEmpty();
                    }
                    if (value instanceof ByteString) {
                        return !ByteString.EMPTY.equals(value);
                    }
                    throw new IllegalArgumentException();
                }
                case 9: {
                    return UnsafeUtil.getObject(message, offset) != null;
                }
                case 10: {
                    return !ByteString.EMPTY.equals(UnsafeUtil.getObject(message, offset));
                }
                case 11: {
                    return UnsafeUtil.getInt(message, offset) != 0;
                }
                case 12: {
                    return UnsafeUtil.getInt(message, offset) != 0;
                }
                case 13: {
                    return UnsafeUtil.getInt(message, offset) != 0;
                }
                case 14: {
                    return UnsafeUtil.getLong(message, offset) != 0L;
                }
                case 15: {
                    return UnsafeUtil.getInt(message, offset) != 0;
                }
                case 16: {
                    return UnsafeUtil.getLong(message, offset) != 0L;
                }
                case 17: {
                    return UnsafeUtil.getObject(message, offset) != null;
                }
            }
            throw new IllegalArgumentException();
        }
        int presenceMask = 1 << (presenceMaskAndOffset >>> 20);
        return (UnsafeUtil.getInt(message, (long)(presenceMaskAndOffset & 0xFFFFF)) & presenceMask) != 0;
    }

    private void setFieldPresent(T message, int pos) {
        int presenceMaskAndOffset = this.presenceMaskAndOffsetAt(pos);
        long presenceFieldOffset = presenceMaskAndOffset & 0xFFFFF;
        if (presenceFieldOffset == 1048575L) {
            return;
        }
        int presenceMask = 1 << (presenceMaskAndOffset >>> 20);
        UnsafeUtil.putInt(message, presenceFieldOffset, UnsafeUtil.getInt(message, presenceFieldOffset) | presenceMask);
    }

    private boolean isOneofPresent(T message, int fieldNumber, int pos) {
        int presenceMaskAndOffset = this.presenceMaskAndOffsetAt(pos);
        return UnsafeUtil.getInt(message, (long)(presenceMaskAndOffset & 0xFFFFF)) == fieldNumber;
    }

    private boolean isOneofCaseEqual(T message, T other, int pos) {
        int presenceMaskAndOffset = this.presenceMaskAndOffsetAt(pos);
        return UnsafeUtil.getInt(message, (long)(presenceMaskAndOffset & 0xFFFFF)) == UnsafeUtil.getInt(other, (long)(presenceMaskAndOffset & 0xFFFFF));
    }

    private void setOneofPresent(T message, int fieldNumber, int pos) {
        int presenceMaskAndOffset = this.presenceMaskAndOffsetAt(pos);
        UnsafeUtil.putInt(message, (long)(presenceMaskAndOffset & 0xFFFFF), fieldNumber);
    }

    private int positionForFieldNumber(int number) {
        if (number >= this.minFieldNumber && number <= this.maxFieldNumber) {
            return this.slowPositionForFieldNumber(number, 0);
        }
        return -1;
    }

    private int positionForFieldNumber(int number, int min) {
        if (number >= this.minFieldNumber && number <= this.maxFieldNumber) {
            return this.slowPositionForFieldNumber(number, min);
        }
        return -1;
    }

    private int slowPositionForFieldNumber(int number, int min) {
        int max = this.buffer.length / 3 - 1;
        while (min <= max) {
            int mid = max + min >>> 1;
            int pos = mid * 3;
            int midFieldNumber = this.numberAt(pos);
            if (number == midFieldNumber) {
                return pos;
            }
            if (number < midFieldNumber) {
                max = mid - 1;
                continue;
            }
            min = mid + 1;
        }
        return -1;
    }

    int getSchemaSize() {
        return this.buffer.length * 3;
    }
}

