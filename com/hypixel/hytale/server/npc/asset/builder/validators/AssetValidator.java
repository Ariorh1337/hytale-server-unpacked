/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder.validators;

import com.hypixel.hytale.codec.schema.config.StringSchema;
import java.util.EnumSet;
import javax.annotation.Nonnull;

public abstract class AssetValidator {
    public static final EnumSet<Config> CanBeEmpty = EnumSet.of(Config.NULLABLE, Config.CAN_BE_EMPTY);
    public static final EnumSet<Config> ListCanBeEmpty = EnumSet.of(Config.LIST_NULLABLE, Config.LIST_CAN_BE_EMPTY);
    private final EnumSet<Config> config;

    public AssetValidator(EnumSet<Config> config) {
        this.config = config;
    }

    public AssetValidator() {
        this(EnumSet.noneOf(Config.class));
    }

    public boolean isNullable() {
        return this.config.contains((Object)Config.NULLABLE);
    }

    public boolean canBeEmpty() {
        return this.config.contains((Object)Config.CAN_BE_EMPTY);
    }

    public boolean isListNullable() {
        return this.config.contains((Object)Config.LIST_NULLABLE);
    }

    public boolean canListBeEmpty() {
        return this.config.contains((Object)Config.LIST_CAN_BE_EMPTY);
    }

    public boolean isMatcher() {
        return this.config.contains((Object)Config.MATCHER);
    }

    public abstract String getDomain();

    public abstract boolean test(String var1);

    public abstract String errorMessage(String var1, String var2);

    public abstract String getAssetName();

    public void updateSchema(@Nonnull StringSchema schema) {
        if (!this.isMatcher()) {
            schema.setHytaleAssetRef(this.getAssetName());
        }
    }

    public static enum Config {
        NULLABLE,
        CAN_BE_EMPTY,
        LIST_NULLABLE,
        LIST_CAN_BE_EMPTY,
        MATCHER;

    }
}

