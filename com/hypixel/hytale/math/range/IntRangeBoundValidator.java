/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.range;

import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.ArraySchema;
import com.hypixel.hytale.codec.schema.config.IntegerSchema;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.validation.ValidationResults;
import com.hypixel.hytale.codec.validation.Validator;
import com.hypixel.hytale.math.range.IntRange;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class IntRangeBoundValidator
implements Validator<IntRange> {
    private final Integer min;
    private final Integer max;
    private final boolean inclusive;
    private final boolean lowerBound;

    @Nonnull
    public static IntRangeBoundValidator lowerBound(Integer min, Integer max, boolean inclusive) {
        return new IntRangeBoundValidator(min, max, inclusive, true);
    }

    @Nonnull
    public static IntRangeBoundValidator upperBound(Integer min, Integer max, boolean inclusive) {
        return new IntRangeBoundValidator(min, max, inclusive, false);
    }

    private IntRangeBoundValidator(Integer min, Integer max, boolean inclusive, boolean lowerBound) {
        this.min = min;
        this.max = max;
        this.inclusive = inclusive;
        this.lowerBound = lowerBound;
    }

    @Override
    public void accept(@Nullable IntRange intRange, @Nonnull ValidationResults results) {
        if (intRange == null) {
            return;
        }
        if (this.lowerBound) {
            this.validateBound(intRange.getInclusiveMin(), "Min bound", results);
        } else {
            this.validateBound(intRange.getInclusiveMax(), "Max bound", results);
        }
    }

    private void validateBound(int value, String boundName, @Nonnull ValidationResults results) {
        if (this.min != null) {
            if (this.inclusive) {
                if (value < this.min) {
                    results.fail(boundName + " must be greater than or equal to " + this.min);
                }
            } else if (value <= this.min) {
                results.fail(boundName + " must be greater than " + this.min);
            }
        }
        if (this.max != null) {
            if (this.inclusive) {
                if (value > this.max) {
                    results.fail(boundName + " must be less than or equal to " + this.max);
                }
            } else if (value >= this.max) {
                results.fail(boundName + " must be less than " + this.max);
            }
        }
    }

    @Override
    public void updateSchema(SchemaContext context, Schema target) {
        if (!(target instanceof ArraySchema)) {
            throw new IllegalArgumentException();
        }
        ArraySchema arraySchema = (ArraySchema)target;
        Schema[] items = (Schema[])arraySchema.getItems();
        if (items == null) {
            throw new IllegalArgumentException();
        }
        if (this.lowerBound) {
            if (!(items[0] instanceof IntegerSchema)) {
                throw new IllegalArgumentException();
            }
            this.updateSchemaBound((IntegerSchema)items[0]);
        } else {
            if (!(items[1] instanceof IntegerSchema)) {
                throw new IllegalArgumentException();
            }
            this.updateSchemaBound((IntegerSchema)items[1]);
        }
    }

    private void updateSchemaBound(@Nonnull IntegerSchema integerSchema) {
        if (this.min != null) {
            if (this.inclusive) {
                integerSchema.setMinimum(this.min);
            } else {
                integerSchema.setExclusiveMinimum(this.min);
            }
        }
        if (this.max != null) {
            if (this.inclusive) {
                integerSchema.setMaximum(this.max);
            } else {
                integerSchema.setExclusiveMaximum(this.max);
            }
        }
    }
}

