/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Resource;
import com.hypixel.hytale.component.ResourceType;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record ResourceRegistration<ECS_TYPE, T extends Resource<ECS_TYPE>>(@Nonnull Class<? super T> typeClass, @Nullable String id, @Nullable BuilderCodec<T> codec, @Nonnull Supplier<T> supplier, @Nonnull ResourceType<ECS_TYPE, T> resourceType) {
}

