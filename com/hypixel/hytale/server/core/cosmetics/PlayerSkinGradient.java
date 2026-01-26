/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.cosmetics;

import com.hypixel.hytale.server.core.cosmetics.PlayerSkinTintColor;
import javax.annotation.Nonnull;
import org.bson.BsonDocument;

public class PlayerSkinGradient
extends PlayerSkinTintColor {
    private String texture;

    protected PlayerSkinGradient(@Nonnull BsonDocument doc) {
        super(doc);
        if (doc.containsKey("Texture")) {
            this.texture = doc.getString("Texture").getValue();
        }
    }

    public String getTexture() {
        return this.texture;
    }

    @Override
    @Nonnull
    public String toString() {
        return "PlayerSkinGradient{texture='" + this.texture + "', id='" + this.id + "', baseColor='" + String.valueOf(this.baseColor) + "'}";
    }
}

