/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.shaded.gson;

import com.nimbusds.jose.shaded.gson.JsonDeserializationContext;
import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonParseException;
import java.lang.reflect.Type;

public interface JsonDeserializer<T> {
    public T deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException;
}

