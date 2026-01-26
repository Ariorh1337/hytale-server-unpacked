/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.validation.validator;

import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.ArraySchema;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.validation.ValidationResults;
import com.hypixel.hytale.codec.validation.Validator;
import javax.annotation.Nonnull;

public class IntArrayValidator
implements Validator<int[]> {
    private Validator<Integer> validator;

    public IntArrayValidator(Validator<Integer> validator) {
        this.validator = validator;
    }

    @Override
    public void accept(@Nonnull int[] is, ValidationResults results) {
        for (int i : is) {
            this.validator.accept((Integer)i, results);
        }
    }

    @Override
    public void updateSchema(SchemaContext context, Schema target) {
        if (!(target instanceof ArraySchema)) {
            throw new IllegalArgumentException();
        }
        Schema item = (Schema)((ArraySchema)target).getItems();
        this.validator.updateSchema(context, item);
    }
}

