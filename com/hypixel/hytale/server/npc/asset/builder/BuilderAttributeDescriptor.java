/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder;

import com.hypixel.hytale.server.npc.asset.builder.BuilderBase;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.validators.Validator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderAttributeDescriptor {
    private final String name;
    private final String type;
    private RequirementType required;
    private boolean computable;
    private final BuilderDescriptorState state;
    private final String shortDescription;
    private final String longDescription;
    @Nullable
    private String defaultValue;
    @Nullable
    private String domain;
    private int minSize;
    private int maxSize;
    @Nullable
    private Validator validator;
    @Nullable
    private Map<String, String> flagDescriptions;

    public BuilderAttributeDescriptor(String name, String type, BuilderDescriptorState state, String shortDescription, String longDescription) {
        this.name = name;
        this.type = type;
        this.state = state;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.required = RequirementType.OPTIONAL;
        this.computable = false;
        this.defaultValue = null;
        this.domain = null;
        this.validator = null;
        this.flagDescriptions = null;
        this.minSize = -1;
        this.maxSize = -1;
    }

    @Nonnull
    public BuilderAttributeDescriptor required() {
        this.required = RequirementType.REQUIRED;
        this.defaultValue = null;
        return this;
    }

    @Nonnull
    public BuilderAttributeDescriptor requiredIfNotOverridden() {
        this.required = RequirementType.REQUIRED_IF_NOT_OVERRIDDEN;
        this.defaultValue = null;
        return this;
    }

    @Nonnull
    public BuilderAttributeDescriptor optional(String defaultValue) {
        this.required = RequirementType.OPTIONAL;
        this.defaultValue = defaultValue;
        return this;
    }

    @Nonnull
    public BuilderAttributeDescriptor optional(double[] defaultValue) {
        this.required = RequirementType.OPTIONAL;
        this.defaultValue = Arrays.toString(defaultValue);
        return this;
    }

    @Nonnull
    public BuilderAttributeDescriptor optional(int[] defaultValue) {
        this.required = RequirementType.OPTIONAL;
        this.defaultValue = Arrays.toString(defaultValue);
        return this;
    }

    @Nonnull
    public BuilderAttributeDescriptor optional(String[] defaultValue) {
        this.required = RequirementType.OPTIONAL;
        this.defaultValue = Arrays.toString(defaultValue);
        return this;
    }

    @Nonnull
    public BuilderAttributeDescriptor optional(boolean[] defaultValue) {
        this.required = RequirementType.OPTIONAL;
        this.defaultValue = Arrays.toString(defaultValue);
        return this;
    }

    @Nonnull
    public BuilderAttributeDescriptor computable() {
        this.computable = true;
        return this;
    }

    @Nonnull
    public <E extends Enum<E>> BuilderAttributeDescriptor setBasicEnum(@Nonnull Class<E> clazz) {
        Enum[] enumConstants = (Enum[])clazz.getEnumConstants();
        this.domain = BuilderBase.getDomain((Enum[])enumConstants);
        HashMap<String, String> result = new HashMap<String, String>();
        for (Enum E : enumConstants) {
            result.put(E.toString(), E.toString());
        }
        this.flagDescriptions = result;
        return this;
    }

    @Nonnull
    public <E extends Enum<E>> BuilderAttributeDescriptor setEnum(@Nonnull Class<E> clazz) {
        Enum[] enumConstants = (Enum[])clazz.getEnumConstants();
        this.domain = BuilderBase.getDomain((Enum[])enumConstants);
        HashMap<String, String> result = new HashMap<String, String>();
        for (Enum E : enumConstants) {
            result.put(E.toString(), (String)((Supplier)((Object)E)).get());
        }
        this.flagDescriptions = result;
        return this;
    }

    @Nonnull
    public BuilderAttributeDescriptor domain(String domain) {
        this.domain = domain;
        return this;
    }

    @Nonnull
    public BuilderAttributeDescriptor validator(Validator validator) {
        this.validator = validator;
        return this;
    }

    @Nonnull
    public BuilderAttributeDescriptor length(int size) {
        return this.length(size, size);
    }

    @Nonnull
    public BuilderAttributeDescriptor length(int minSize, int maxSize) {
        this.minSize = minSize;
        this.maxSize = maxSize;
        return this;
    }

    @Nonnull
    public String toString() {
        return "BuilderAttributeDescriptor{name='" + this.name + "', type='" + this.type + "', required=" + String.valueOf((Object)this.required) + ", computable=" + this.computable + ", state=" + String.valueOf((Object)this.state) + ", shortDescription='" + this.shortDescription + "', longDescription='" + this.longDescription + "', defaultValue='" + this.defaultValue + "', domain='" + this.domain + "', minSize=" + this.minSize + ", maxSize=" + this.maxSize + ", validator=" + String.valueOf(this.validator) + ", flagDescriptions=" + String.valueOf(this.flagDescriptions) + "}";
    }

    private static enum RequirementType {
        REQUIRED,
        OPTIONAL,
        REQUIRED_IF_NOT_OVERRIDDEN;

    }
}

