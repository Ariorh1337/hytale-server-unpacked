/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.serialization;

@Deprecated
public interface ClassResolver {
    public Class<?> resolve(String var1) throws ClassNotFoundException;
}

