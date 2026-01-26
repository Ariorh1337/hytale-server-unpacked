/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.field;

import org.bouncycastle.math.field.ExtensionField;
import org.bouncycastle.math.field.Polynomial;

public interface PolynomialExtensionField
extends ExtensionField {
    public Polynomial getMinimalPolynomial();
}

