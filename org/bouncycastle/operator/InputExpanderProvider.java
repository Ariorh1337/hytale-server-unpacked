/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.InputExpander;

public interface InputExpanderProvider {
    public InputExpander get(AlgorithmIdentifier var1);
}

