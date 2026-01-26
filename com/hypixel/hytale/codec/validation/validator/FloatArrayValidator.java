/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.validation.validator;

import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.ArraySchema;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.validation.ValidationResults;
import com.hypixel.hytale.codec.validation.Validator;
import javax.annotation.Nullable;

public class FloatArrayValidator
implements Validator<float[]> {
    private final Validator<Float> validator;

    public FloatArrayValidator(Validator<Float> validator) {
        this.validator = validator;
    }

    @Override
    public void accept(@Nullable float[] floats, ValidationResults results) {
        if (floats == null) {
            return;
        }
        for (float t : floats) {
            this.validator.accept(Float.valueOf(t), results);
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

