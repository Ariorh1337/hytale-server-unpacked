/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.CanIgnoreReturnValue;
import com.google.protobuf.CheckReturnValue;
import com.google.protobuf.MapEntryLite;
import java.util.Map;

@CheckReturnValue
interface MapFieldSchema {
    public Map<?, ?> forMutableMapData(Object var1);

    public Map<?, ?> forMapData(Object var1);

    public boolean isImmutable(Object var1);

    public Object toImmutable(Object var1);

    public Object newMapField(Object var1);

    public MapEntryLite.Metadata<?, ?> forMapMetadata(Object var1);

    @CanIgnoreReturnValue
    public Object mergeFrom(Object var1, Object var2);

    public int getSerializedSize(int var1, Object var2, Object var3);
}

