/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.validators;

import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.validation.LateValidator;
import com.hypixel.hytale.codec.validation.ValidationResults;
import com.hypixel.hytale.server.npc.NPCPlugin;
import javax.annotation.Nonnull;

public class NPCRoleValidator
implements LateValidator<String> {
    public static final NPCRoleValidator INSTANCE = new NPCRoleValidator();

    @Override
    public void accept(String s, ValidationResults results) {
    }

    @Override
    public void updateSchema(SchemaContext context, @Nonnull Schema target) {
        target.setHytaleAssetRef("NPCRole");
    }

    @Override
    public void acceptLate(String s, @Nonnull ValidationResults results, ExtraInfo extraInfo) {
        try {
            NPCPlugin.get().validateSpawnableRole(s);
        }
        catch (IllegalArgumentException e) {
            results.fail(e.getMessage());
        }
    }
}

