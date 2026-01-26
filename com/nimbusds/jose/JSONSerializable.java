/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose;

import java.util.Map;

public interface JSONSerializable {
    public Map<String, Object> toGeneralJSONObject();

    public Map<String, Object> toFlattenedJSONObject();
}

