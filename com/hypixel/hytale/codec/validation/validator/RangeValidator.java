/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.validation.validator;

import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.IntegerSchema;
import com.hypixel.hytale.codec.schema.config.NumberSchema;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.validation.ValidationResults;
import com.hypixel.hytale.codec.validation.Validator;
import com.hypixel.hytale.logger.HytaleLogger;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RangeValidator<T extends Comparable<T>>
implements Validator<T> {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private final T min;
    private final T max;
    private final boolean inclusive;

    public RangeValidator(T min, T max, boolean inclusive) {
        this.min = min;
        this.max = max;
        this.inclusive = inclusive;
    }

    @Override
    public void accept(@Nullable T t, @Nonnull ValidationResults results) {
        int compare;
        if (t == null) {
            return;
        }
        if (this.min != null) {
            compare = t.compareTo(this.min);
            if (this.inclusive) {
                if (compare < 0) {
                    results.fail("Must be greater than or equal to " + String.valueOf(this.min));
                }
            } else if (compare < 0 || compare == 0) {
                results.fail("Must be greater than " + String.valueOf(this.min));
            }
        }
        if (this.max != null) {
            compare = t.compareTo(this.max);
            if (this.inclusive) {
                if (compare > 0) {
                    results.fail("Must be less than or equal to " + String.valueOf(this.max));
                }
            } else if (compare > 0 || compare == 0) {
                results.fail("Must be less than " + String.valueOf(this.max));
            }
        }
    }

    @Override
    public void updateSchema(SchemaContext context, Schema target) {
        if (this.min != null && !(this.min instanceof Number)) {
            LOGGER.at(Level.WARNING).log("Can't handle: min is not a Number: %s, %s", this.min, this.min.getClass());
            return;
        }
        if (this.max != null && !(this.max instanceof Number)) {
            LOGGER.at(Level.WARNING).log("Can't handle: max is not a Number: %s, %s", this.max, this.max.getClass());
            return;
        }
        if (!(target instanceof NumberSchema) && !(target instanceof IntegerSchema)) {
            boolean failed = true;
            if (target.getAnyOf() != null) {
                for (Schema schema : target.getAnyOf()) {
                    if (!(schema instanceof NumberSchema) && !(schema instanceof IntegerSchema)) continue;
                    this.updateSchema(schema);
                    failed = false;
                }
            }
            if (failed) {
                LOGGER.at(Level.WARNING).log("Can't handle: %s as a range: %s", (Object)target.getHytale().getType(), (Object)target);
            }
            return;
        }
        this.updateSchema(target);
    }

    private void updateSchema(Schema target) {
        if (target instanceof IntegerSchema) {
            Number v;
            IntegerSchema i = (IntegerSchema)target;
            if (this.min != null) {
                v = (Number)this.min;
                if (this.inclusive) {
                    i.setMinimum(v.intValue());
                } else {
                    i.setExclusiveMinimum(v.intValue());
                }
            }
            if (this.max != null) {
                v = (Number)this.max;
                if (this.inclusive) {
                    i.setMaximum(v.intValue());
                } else {
                    i.setExclusiveMaximum(v.intValue());
                }
            }
        } else {
            Number v;
            NumberSchema i = (NumberSchema)target;
            if (this.min != null) {
                v = (Number)this.min;
                if (this.inclusive) {
                    i.setMinimum(v.doubleValue());
                } else {
                    i.setExclusiveMinimum(v.doubleValue());
                }
            }
            if (this.max != null) {
                v = (Number)this.max;
                if (this.inclusive) {
                    i.setMaximum(v.doubleValue());
                } else {
                    i.setExclusiveMaximum(v.doubleValue());
                }
            }
        }
    }
}

