/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

public interface FieldNameValidator {
    public boolean validate(String var1);

    public FieldNameValidator getValidatorForField(String var1);

    default public void start() {
    }

    default public void end() {
    }
}

