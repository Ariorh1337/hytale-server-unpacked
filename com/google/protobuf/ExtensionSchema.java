/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.CheckReturnValue;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.FieldSet;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Reader;
import com.google.protobuf.UnknownFieldSchema;
import com.google.protobuf.Writer;
import java.io.IOException;
import java.util.Map;

@CheckReturnValue
abstract class ExtensionSchema<T extends FieldSet.FieldDescriptorLite<T>> {
    ExtensionSchema() {
    }

    abstract boolean hasExtensions(MessageLite var1);

    abstract FieldSet<T> getExtensions(Object var1);

    abstract void setExtensions(Object var1, FieldSet<T> var2);

    abstract FieldSet<T> getMutableExtensions(Object var1);

    abstract void makeImmutable(Object var1);

    abstract <UT, UB> UB parseExtension(Object var1, Reader var2, Object var3, ExtensionRegistryLite var4, FieldSet<T> var5, UB var6, UnknownFieldSchema<UT, UB> var7) throws IOException;

    abstract int extensionNumber(Map.Entry<?, ?> var1);

    abstract void serializeExtension(Writer var1, Map.Entry<?, ?> var2) throws IOException;

    abstract Object findExtensionByNumber(ExtensionRegistryLite var1, MessageLite var2, int var3);

    abstract void parseLengthPrefixedMessageSetItem(Reader var1, Object var2, ExtensionRegistryLite var3, FieldSet<T> var4) throws IOException;

    abstract void parseMessageSetItem(ByteString var1, Object var2, ExtensionRegistryLite var3, FieldSet<T> var4) throws IOException;
}

